import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class Yard {

    Coördinaat[][][] matrix;
    int hoogte;
    int lengte;
    int breedte;
    ArrayList<Beweging> solution;
    //Mapping between id en x-cöordinaat
    HashMap<Integer,Integer> mapping_id_xcoor;
    HashMap<Integer,Integer> mapping_id_ycoor;
    ArrayList<Kraan> cranes;
    ArrayList<Container> notOnTargetId;
    ArrayList<Container> containersThatNeedToBeMoved;
    ArrayList<List<Beweging>> potentialCollisions;
    ArrayList<Container> containersOnHighestLevel;

    public void createYard(JSONArray slots, int lengte, int breedte, int hoogte){

        //TODO: Stacking constraints

        mapping_id_xcoor = new HashMap<>();
        mapping_id_ycoor = new HashMap<>();
        notOnTargetId = new ArrayList<>();
        containersThatNeedToBeMoved = new ArrayList<>();
        containersOnHighestLevel = new ArrayList<>();
        this.hoogte = hoogte;
        this.lengte = lengte;
        this.breedte = breedte;
        cranes = new ArrayList<>();
        solution = new ArrayList<>();
        potentialCollisions = new ArrayList<>();


        matrix = new Coördinaat[lengte][breedte][hoogte];
        for (Object o : slots) {
            JSONObject slot = new JSONObject();
            slot.putAll((Map) o);
            int x = ((Long) slot.get("x")).intValue();
            int y = ((Long) slot.get("y")).intValue();
            mapping_id_xcoor.put(((Long) slot.get("id")).intValue(), x);
            mapping_id_ycoor.put(((Long) slot.get("id")).intValue(), y);
            for (int j = 0; j < hoogte; j++) {
                //If no container is assigned, container_id equals Integer_Min_Value
                matrix[x][y][j] = new Coördinaat(x, y, j, ((Long) slot.get("id")).intValue());
            }
        }

    }

    public void addContainer(Object slot_id, Container container) {
        int slots = (int)((long) slot_id);
        container.setSlot_id(slots);
        int startX  = mapping_id_xcoor.get(slots);
        int startY = mapping_id_ycoor.get(slots);
        boolean b = true;
        for (int h =0; h < hoogte && b; h++){
            b = true;
            if(matrix[startX][startY][h].getContainer_id() == Integer.MIN_VALUE){
                int count = 1;
                for (int i = 1; i < container.getLength(); i++){
                    if (matrix[startX+i][startY][h].getContainer_id() == Integer.MIN_VALUE){
                        count++;
                    }
                }
                if (count == container.getLength()){
                    b=false;
                    container.setHoogte(h);
                    for (int i = 0; i < container.getLength(); i++){
                        matrix[startX+i][startY][h].setContainer_id(container.getId());
                    }
                }
            }
        }
    }

    public void addCranes(JSONArray cranes){
        for (Object o : cranes) {
            JSONObject crane = new JSONObject();
            crane.putAll((Map) o);

            this.cranes.add(new Kraan(((Long) crane.get("x")).intValue(), Float.parseFloat(crane.get("y").toString()), ((Long) crane.get("ymin")).intValue(), ((Long) crane.get("ymax")).intValue(), ((Long) crane.get("id")).intValue(), ((Long) crane.get("xspeed")).intValue(), ((Long) crane.get("yspeed")).intValue(), ((Long) crane.get("xmax")).intValue(), ((Long) crane.get("xmin")).intValue()));
        }

    }


    //Method for when targetHeight is specified
    public void calculateMovementsTargetHeight(int maxHeight, int targetHeight, ArrayList<Container> containersArray) {
        while(maxHeight != targetHeight) {
            findContainersOnHighestLevel(containersArray, maxHeight);
            for (Container c : containersOnHighestLevel) {

                int targetId = findEmptyPlace(maxHeight-2, c, containersArray);
                if (targetId == -1) {
                    // TODO wat als geen plaats gevonden op lager verdiep
                } else {
                    c.setTarget_id(targetId);
                    c.setTarget_hoogte(maxHeight-2);
                    makeMovement(c);
                }
            }
            maxHeight--;
        }

        addTimestampsToSolution();
    }

    private int findEmptyPlace(int hoogte, Container c, ArrayList<Container> containersArray) {
        int idEmpty = -1;

        for (int x=0; x<matrix.length-c.getLength(); x++) {
            for (int y = 0; y < matrix[x].length; y++) {

                // Zoeken naar vrije plaats in verdiep eronder
                boolean possible = true;
                int idOfPossibleEmptySlot = matrix[x][y][hoogte].getId();
                for (int i = 0; i < c.getLength(); i++) {

                    // Als het bezet is
                    if (matrix[x + i][y][hoogte].getContainer_id() != Integer.MIN_VALUE) {
                        possible = false;
                        break;

                    }
                    // Plaats is vrij, controle op constraint voor laag eronder
                    else {
                        int containerBelow = matrix[mapping_id_xcoor.get(idOfPossibleEmptySlot+i)][mapping_id_ycoor.get(idOfPossibleEmptySlot)][hoogte-1].getContainer_id();

                        // Constraint 2
                        if (hoogte-1 != 0 && (containerBelow != Integer.MIN_VALUE)) {
                            possible = false;
                            break;
                        }
                        // Constraint 3
                        else if (hoogte-1 != 0 && c.getLength()<containersArray.get(containerBelow).getLength()) {
                            possible = false;
                            idEmpty = -1;
                        }
                        else idEmpty = idOfPossibleEmptySlot;
                    }
                }

                if (possible) {
                    return idEmpty;
                }
            }
        }
        return idEmpty;
    }

    private void findContainersOnHighestLevel(ArrayList<Container> containersArray, int maxHeight) {
        for (Container c : containersArray) {
            if (c.getHoogte() == maxHeight-1) containersOnHighestLevel.add(c);
        }
    }


    //Method for when targetAssignments are given
    public void calculateMovementsTargetAssignments(JSONArray targetAssignments, ArrayList<Container> containersArray) {
        findContainersNotOnTargetId(targetAssignments, containersArray);
        for (Container c : notOnTargetId) {
            //Don't Know if needed?
            checkTargetId(c, containersArray);
            //All containers that need to be moved added to containersthatneedtobemovedArray
            checkIfContainerFreeToMove(c, containersArray);
        }
        for (Container c : containersThatNeedToBeMoved) {
            makeMovement(c);
        }

        addTimestampsToSolution();
    }


    private void addTimestampsToSolution() {
        cranes.sort(new sortByLengthMovements());
        int totaleBewegingen = 0;
        for (Kraan c: cranes){
            totaleBewegingen+=c.getBewegingLijst().size();
        }

        while (solution.size() != totaleBewegingen) {
            System.out.println(solution.size());
            for (Kraan c: cranes){
                if (c.getBewegingLijst().size() != 0){
                    totaleBewegingen = setStartingTimes(c,totaleBewegingen);
                }
            }
        }
    }

    private int setStartingTimes(Kraan c,int totaal) {
        if (solution.isEmpty()){
            Beweging b = c.getBewegingLijst().get(0);
            int startX = b.getStart().getX();
            float startY = b.getStart().getY();
            int eindX = b.getEind().getX();
            float eindY = b.getEind().getY();
            int duurX = Math.abs(eindX - startX)/c.getXspeed();
            float duurY = Math.abs(eindY- startY)/c.getYspeed();
            float duur = Math.max(duurX,duurY);
            b.setStartTijdstip(0);
            b.setEindTijdstip((int) Math.ceil(duur));
            solution.add(c.getBewegingLijst().get(0));
            c.getAddedMovements().add(c.getBewegingLijst().get(0));
            c.getBewegingLijst().remove(0);
        }else {
            Beweging volgende = c.getBewegingLijst().get(0);
            int startX = volgende.getStart().getX();
            float startY = volgende.getStart().getY();
            int eindX = volgende.getEind().getX();
            float eindY = volgende.getEind().getY();
            int duurX = Math.abs(eindX - startX)/c.getXspeed();
            float duurY = Math.abs(eindY- startY)/c.getYspeed();
            float duur = Math.max(duurX,duurY);
            int hits = 0;
            for (Beweging b : solution){
                if(b.getKraan_id() == c.getBewegingLijst().get(0).getKraan_id()){
                    hits++;
                    volgende.setStartTijdstip(b.getEindTijdstip()+1);
                    volgende.setEindTijdstip(b.getEindTijdstip()+1+(int) Math.ceil(duur));
                }
            }
            if (hits == 0){
                volgende.setStartTijdstip(0);
                volgende.setEindTijdstip((int) Math.ceil(duur));
            }
            Beweging temp = null;
            boolean tempAdded = false;
            for (Beweging b :solution){
                if(b.getKraan_id() != volgende.getKraan_id()){
                    if(inSameTimeInterval(b,volgende) && isCollision(b,volgende)){
                        volgende.setStartTijdstip(b.getEindTijdstip()+1);
                        volgende.setEindTijdstip(b.getEindTijdstip()+1+(int) Math.ceil(duur));
                    }else if(isLatestmove(b)){
                        if(isCollision(b,volgende)){
                            System.out.println("collision");
                           temp = moveKraan(b.getKraan_id(), volgende.getStartTijdstip(), volgende.getStart(), volgende.getEind());
                           tempAdded = true;
                        }
                    }
                }
            }
            if (tempAdded){
                solution.add(temp);
                totaal++;
            }
            c.getAddedMovements().add(volgende);
            solution.add(volgende);
            c.getBewegingLijst().remove(0);
            return totaal;
        }

        return totaal;
    }

    private Beweging moveKraan(int kraan_id, int startTijdstip, Coördinaat startCoordinaat, Coördinaat eindCoordinaat) {
        for (Kraan k : cranes){
            if(kraan_id == k.getId()){
//                int index = k.getAddedMovements().size();
                int startX = k.getX();
                float startY = k.getY();
//                int start = startTijdstip;
                int eindX ;
                int duur;
                if(startCoordinaat.getX()-eindCoordinaat.getX() > 0){
                    eindX = startX - 2;
                    if(eindX < 0){
                        eindX=0;
                    }
                }else {
                    eindX = startX + 2;
                    if (eindX>lengte){
                        eindX=lengte;
                    }
                }
                duur = 2/k.getXspeed();
                Beweging nieuwe = new Beweging(Integer.MIN_VALUE,startTijdstip,startTijdstip+duur,matrix[startX][(int) Math.ceil(startY)][0], matrix[eindX][(int) Math.ceil(startY)][0],kraan_id,true);
                k.setX(eindX);
                k.setY((int) Math.ceil(startY));
                k.getAddedMovements().add(nieuwe);
                return nieuwe;
            }
        }
        return null;
    }

    private boolean isLatestmove(Beweging b) {
        for (Kraan k : cranes){
            if(b.getKraan_id() == k.getId()){
                int index = k.getAddedMovements().size();
                if(k.getAddedMovements().get(index-1).getEindTijdstip() == b.getEindTijdstip()){
                    return  true;
                };
            }
        }
        return false;
    }

    private boolean isCollision(Beweging b, Beweging volgende) {
        return (volgende.getStart().getX() <= b.getStart().getX() && b.getStart().getX() <= volgende.getEind().getX()) ||
                (volgende.getStart().getX() <= b.getEind().getX() && b.getEind().getX() <= volgende.getEind().getX()) ||
                (volgende.getEind().getX() <= b.getStart().getX() && b.getStart().getX() <= volgende.getStart().getX()) ||
                (volgende.getEind().getX() <= b.getEind().getX() && b.getEind().getX() <= volgende.getStart().getX());
    }

    private boolean inSameTimeInterval(Beweging b, Beweging volgende) {
        return b != volgende && ((b.getStartTijdstip() <= volgende.getEindTijdstip() && b.getEindTijdstip() >= volgende.getEindTijdstip()) || (b.getStartTijdstip() <= volgende.getStartTijdstip() && b.getEindTijdstip() >= volgende.getStartTijdstip()));
    }


    private void makeMovement(Container c) {
        int startX = mapping_id_xcoor.get(c.getSlot_id());
        int startY = mapping_id_ycoor.get(c.getSlot_id());
        int eindX = mapping_id_xcoor.get(c.getTarget_id());
        int eindY= mapping_id_ycoor.get(c.getTarget_id());
        ArrayList<Kraan> availableCranes = new ArrayList<Kraan>();
        for (Kraan k: cranes){
            if(startX<eindX){
                if(startY < eindY){
                    if(k.getXmin()<=startX && k.getXmax()>=eindX && k.getYmin() <= startY && k.getYmax()>=eindY && !availableCranes.contains(k)){
                        availableCranes.add(k);
                    }
                }else{
                    if(k.getXmin()<=startX && k.getXmax()>=eindX && k.getYmin() <= eindY && k.getYmax()>=startY && !availableCranes.contains(k)){
                        availableCranes.add(k);
                    }
                }
            }else {
                if(startY < eindY){
                    if(k.getXmin()<=eindX && k.getXmax()>=startX && k.getYmin() <= startY && k.getYmax()>=eindY && !availableCranes.contains(k)){
                        availableCranes.add(k);
                    }
                }else{
                    if(k.getXmin()<=eindX && k.getXmax()>=startX && k.getYmin() <= eindY && k.getYmax()>=startY && !availableCranes.contains(k)){
                        availableCranes.add(k);
                    }
                }
            }
        }

        if (availableCranes.size() > 1){
            //TODO choose crane from availableCranes
        }
        else if(availableCranes.size()==0){
            //TODO implement noCranesTakeFullMovement, wss extra containers toevoegen aan containersthatneedtobemoved
        }
        else{
            addMovement(c,availableCranes.get(0));       
        }
    }

    private void addMovement(Container c, Kraan kraan) {
        int startX= mapping_id_xcoor.get(c.getSlot_id());
        int startY= mapping_id_ycoor.get(c.getSlot_id());
        int eindX= mapping_id_xcoor.get(c.getTarget_id());
        int eindY= mapping_id_ycoor.get(c.getTarget_id());

            if (kraan.getX() != startX || kraan.getY() != startY){
                kraan.getBewegingLijst().add(new Beweging(c.getId(),0,0,matrix[kraan.getX()][(int) Math.floor(kraan.getY())][0],matrix[startX][startY][0],kraan.getId(),true));
                kraan.setX(startX);
                kraan.setY(startY);
            }

            kraan.getBewegingLijst().add(new Beweging(c.getId(),0,0,matrix[startX][startY][0],matrix[eindX][eindY][0],kraan.getId(),false));
            kraan.setX(eindX);
            kraan.setY(eindY);
    }



    private boolean checkTargetId(Container c, ArrayList<Container> containersArray) {
        int x = mapping_id_xcoor.get(c.getTarget_id());
        int y = mapping_id_ycoor.get(c.getTarget_id());
        for (int i = 0; i < hoogte; i++){
            int count = 0;
            for(int j = 0; j < c.getLength(); j++){
                if (matrix[x+j][y][i].getContainer_id()==Integer.MIN_VALUE){
                    count++;
                }
            }
            if (count == c.getLength()){
                return true;
            }
        }
        //TODO: Implement, if not on target id  in arraylist containerstobemoved
        System.out.println("Niet mogelijk om op target Id te plaatsen. Nog te implementeren");
        return false;
    }

    private void checkIfContainerFreeToMove(Container c, ArrayList<Container> containersArray ) {

        int x = mapping_id_xcoor.get(c.getSlot_id());
        int y = mapping_id_ycoor.get(c.getSlot_id());
        int z = c.getHoogte();
        if (z != hoogte-1){
            for (int i = 0; i < c.getLength();i++){
                if(Integer.MIN_VALUE != matrix[x + i][y][z+1].getContainer_id()){
                    //TODO:Snellere manier dan telkens containersArray doorzoeken?
                        for (Container container: containersArray){
                            if (container.getId() == matrix[x + i][y][z+1].getContainer_id()){
                                checkIfContainerFreeToMove(container, containersArray);
                                break;
                            }
                        }
                }
                if(!containersThatNeedToBeMoved.contains(c)){
                    containersThatNeedToBeMoved.add(c);
                }
            }
        }else {
            containersThatNeedToBeMoved.add(c);
        }

    }

    private void findContainersNotOnTargetId(JSONArray targetassignments, ArrayList<Container> containersArray) {
        for (Object o : targetassignments){
            JSONObject assignment = new JSONObject();
            assignment.putAll((Map) o);
            int container_id = ((Long) assignment.get("container_id")).intValue();
            int target_id = ((Long) assignment.get("slot_id")).intValue();
            for (Container c : containersArray){
                if(c.getId() == container_id){
                    if(c.getSlot_id() != target_id){
                        c.setTarget_id(target_id);
                        notOnTargetId.add(c);
                    }
                }
            }
        }

    }

    public int getLengte() {
        return lengte;
    }

    public int getBreedte() {
        return breedte;
    }

    public Coördinaat[][][] getMatrix() {
        return matrix;
    }
}

class sortByLengthMovements implements Comparator<Kraan> {

    // Method
    // Sorting in ascending order of roll number
    public int compare(Kraan a, Kraan b)
    {

        return b.getBewegingLijst().size() - a.getBewegingLijst().size();
    }
}
