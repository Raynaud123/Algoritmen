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

                int targetId = findEmptyPlace(maxHeight-1, c);
                if (targetId == -1) {
                    // TODO wat als geen plaats gevonden op lager verdiep
                } else {
                    c.setTarget_id(targetId);
                    c.setTarget_hoogte(maxHeight-1);
                    makeMovement(c);
                }
            }
            maxHeight--;
        }
    }

    private int findEmptyPlace(int hoogte, Container c) {
        int idEmpty = -1;

        // TODO : for in for in for kan niet goed zijn
        for (int x=0; x<matrix.length-c.getLength(); x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                boolean possible = true;
                for (int i = 0; i < c.getLength(); i++) {
                    if (matrix[x + i][y][hoogte].getContainer_id() != Integer.MIN_VALUE) {
                        possible = false;
                        break;
                    } else idEmpty = matrix[x][y][hoogte].getId();
                }
                // TODO : if voor constraint 3

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
        Collections.sort(cranes, new sortByLengthMovements());
        for (Kraan c: cranes){
            setStartingTimes(c);
        }

        checkPotentialCollisions();

        //Check if collision
        //If collision set time later
        //Check if the rest of movements still work


        System.out.println("size collisions" + potentialCollisions.size());

        for (int i = 0; i < potentialCollisions.size(); i++){
            System.out.println(potentialCollisions.get(i));
        }


    }

    private void checkPotentialCollisions() {
        for(Beweging b: solution){
            for (Beweging bew: solution){
                if(bew.getKraan_id() != b.getKraan_id()){
                    if((bew.getStart().getX() <= b.getStart().getX() && b.getStart().getX() <= bew.getEind().getX()) ||
                            (bew.getStart().getX() <= b.getEind().getX() && b.getEind().getX() <= bew.getEind().getX()) ||
                            (bew.getEind().getX() <= b.getStart().getX() && b.getStart().getX() <= bew.getStart().getX()) ||
                            (bew.getEind().getX() <= b.getEind().getX() && b.getEind().getX() <= bew.getStart().getX())
                    ){
                        boolean added = false;
                        for (int i = 0; i < potentialCollisions.size(); i++){
                            if((potentialCollisions.get(i).contains(bew) || potentialCollisions.get(i).contains(b)) && !added){
                                added = true;
                                if(potentialCollisions.get(i).contains(b) && !potentialCollisions.get(i).contains(bew)){
                                    potentialCollisions.get(i).add(bew);
                                    break;
                                }
                                else if(potentialCollisions.get(i).contains(bew) && !potentialCollisions.get(i).contains(b)) {
                                    potentialCollisions.get(i).add(b);
                                    break;
                                }
                            }
                        }
                        if(!added){
                            ArrayList<Beweging> test = new ArrayList<>();
                            test.add(b);
                            test.add(bew);
                            potentialCollisions.add(test);
                        }
                    };
                }
            }
        }
    }


    private void setStartingTimes(Kraan c) {
        int index = 0;
        for(Beweging b : c.getBewegingLijst()){

            int startX = b.getStart().getX();
            float startY = b.getStart().getY();
            int eindX = b.getEind().getX();
            float eindY = b.getEind().getY();
            int duurX = Math.abs(eindX - startX)/c.getXspeed();
            float duurY = Math.abs(eindY- startY)/c.getYspeed();
            float duur = Math.max(duurX,duurY);

            if(index == 0){
                b.setStartTijdstip(0);
                b.setEindTijdstip((int) Math.ceil(duur));
            }else {
                for (Beweging added: solution){
                    if(added.getKraan_id() == b.getKraan_id()){
                        int startTijdstip = added.getEindTijdstip()+1;
                        b.setStartTijdstip(startTijdstip);
                        b.setEindTijdstip((int) Math.ceil(duur) + startTijdstip);
                    }
                }
            }

            solution.add(b);
            index++;
        }
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
