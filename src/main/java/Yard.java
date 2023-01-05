import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

//TODO: Container effectief verplaatsen
//TODO: GUI aanpassen container beweegt mee
//TODO: Crossover bug fixen
//TODO: Als geen plaats op verdieping lager -> gaten creeëren

public class Yard {

    //We can possibly use stack as height
    Coördinaat[][][] matrix;
    int hoogte;
    int lengte;
    int breedte;
    ArrayList<Beweging> solution;
    //Mapping between id en x-cöordinaat
    HashMap<Integer,Integer> mapping_id_xcoor;
    HashMap<Integer,Integer> mapping_id_ycoor;
    ArrayList<Container> containersArray;
    ArrayList<Kraan> cranes;
    ArrayList<Container> notOnTargetId;
    ArrayList<Container> containersThatNeedToBeMoved;
    ArrayList<Beweging> temp;
    Boolean changed;

    public void createYard(JSONArray slots, int lengte, int breedte, int hoogte){

        //TODO: Stacking constraints

        mapping_id_xcoor = new HashMap<>();
        mapping_id_ycoor = new HashMap<>();
        notOnTargetId = new ArrayList<>();
        containersThatNeedToBeMoved = new ArrayList<>();
        temp = new ArrayList<>();
        this.hoogte = hoogte;
        this.lengte = lengte;
        this.breedte = breedte;
        cranes = new ArrayList<>();
        solution = new ArrayList<>();
        changed = false;

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
        this.containersArray = containersArray;
        int level = maxHeight-1;
        int loops = 0;

        while(level != targetHeight-1 && loops < 50) {

            moveContainersToLowerLevel(level, true);
            if (findContainersOnLevel(level, containersArray).size() == 0) {
                level--;
            }
            else {
                tryToCreateGaps();
            }
            loops++;
        }

        addTimestampsToSolution();
    }

    private void tryToCreateGaps() {
        // TODO implement
    }

    private void moveContainersToLowerLevel(int level, boolean isHighestLevel) {
        ArrayList<Container> containersOnLevel;
        containersOnLevel = findContainersOnLevel(level, containersArray);
        int counter = 0;

        for (Container c : containersOnLevel) {

            if (c.getHoogte() == level && c.getTarget_hoogte() == Integer.MIN_VALUE) {
                // Probeer elk verdiep tot plaats gevonden
                int targetId = -1;
                int targetHeight = -1;
                for (int i = 0; i<level; i++) {
                    targetId = findEmptyPlace(i, c, containersArray);
                    targetHeight = i;
                    if (targetId != -1) break;
                }

                if (targetId == -1) {
                    if (level != 0) {
                        if (isHighestLevel) {
                            changed = false;
                            moveContainersToLowerLevel(level - 1, false);
                            if (changed) moveContainersToLowerLevel(level, true);
                            else System.out.println("No solution found");
                        }
                        else if (counter == containersOnLevel.size()){
                            moveContainersToLowerLevel(level - 1, false);
                        }
                    }
                    else {
                        System.out.println("Zit op het verdiep 0, dus kan niet lager");
                        break;
                    }
                }
                else {
                    // Hier al id toewijzen zodat geen tweede beweging naar dat slot kan
                    c.setTarget_id(targetId);
                    Coördinaat nuBezet = getFromTargetId(targetId, targetHeight);
                    assert nuBezet != null;
                    int bezetteX = nuBezet.getX();
                    int bezetteY = nuBezet.getY();
                    for (int i=bezetteX; i<bezetteX+c.getLength(); i++) {
                        matrix[i][bezetteY][targetHeight].setContainer_id(c.getId());
                    }
                    c.setTarget_hoogte(targetHeight);
                    makeMovement(c);
                    changed = true;
                    System.out.println("Container " + c.getId());
                    System.out.println("DE TARGET X, Y, Z: " + bezetteX + ", " + bezetteY + ", " + targetHeight);
                }
                counter++;
            }
        }
    }

    private Coördinaat getFromTargetId(int targetId, int hoogte) {
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[i].length; j++) {
                if (matrix[i][j][hoogte].getId() == targetId) {
                    return matrix[i][j][hoogte];
                }
            }
        }
        return null;
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
                        if (hoogte != 0) {
                            int containerBelow = matrix[mapping_id_xcoor.get(idOfPossibleEmptySlot+i)][mapping_id_ycoor.get(idOfPossibleEmptySlot)][hoogte-1].getContainer_id();

                            // Constraint 2
                            if (containerBelow == Integer.MIN_VALUE) {
                                possible = false;
                                break;
                            }
                            // Constraint 3
                            else if (c.getLength()<containersArray.get(containerBelow).getLength()) {
                                possible = false;
                                idEmpty = -1;
                            }
                            else idEmpty = idOfPossibleEmptySlot;
                        }
                        else idEmpty = idOfPossibleEmptySlot;
                    }
                }

                // Hoekje op hoekje rechts
                if (hoogte != 0 && x != matrix.length-c.getLength()-1 && matrix[x+c.getLength()][y][hoogte-1].getContainer_id() == matrix[x+c.getLength()-1][y][hoogte-1].getContainer_id()) {
                    possible = false;
                    idEmpty = -1;
                }

                // Hoekje op hoekje links
                if (hoogte != 0 && x != 0 && matrix[x][y][hoogte-1].getContainer_id() == matrix[x-1][y][hoogte-1].getContainer_id()) {
                    possible = false;
                    idEmpty = -1;
                }

                if (possible) {
                    return idEmpty;
                }
            }
        }
        return -1;
    }

    private ArrayList<Container> findContainersOnLevel(int level, ArrayList<Container> containersArray) {
        ArrayList<Container> containersOnLevel = new ArrayList<>();
        for (Container c : containersArray) {
            if (c.getHoogte() == level && c.getTarget_hoogte() == Integer.MIN_VALUE) containersOnLevel.add(c);
        }
        return containersOnLevel;
    }


    //Method for when targetAssignments are given
    public void calculateMovementsTargetAssignments(JSONArray targetAssignments, ArrayList<Container> containersArray) {
        findContainersNotOnTargetId(targetAssignments, containersArray);
        this.containersArray = containersArray;
        for (Container c : containersArray) {
            if(notOnTargetId.contains(c)){
                checkTargetId(c, containersArray);
            }
        }
        boolean opnieuw = true;

        do{
            for (Container c : notOnTargetId){
                if (!containersThatNeedToBeMoved.contains(c)){
                    boolean test = checkIfContainerFreeToMove(c, containersArray);
                    if (!test){
                        opnieuw = test;
                        System.out.println(c);
                    }
                }
                //All containers that need to be moved added to containersthatneedtobemovedArray
            }
            System.out.println("opnieuw");
        }while (!opnieuw);

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

        for (Kraan k : cranes){
            if (k.getBewegingLijst().isEmpty()){
                solution.add(moveKraanFirst(k, 0));
                totaleBewegingen++;
            }
        }

        while (solution.size() != totaleBewegingen) {
            for (Kraan c: cranes){
                if (c.getBewegingLijst().size() != 0){
                    totaleBewegingen = setStartingTimes(c,totaleBewegingen);
                }
            }
        }
    }

    private Beweging moveKraanFirst(Kraan kraan, int i) {
        int startX = kraan.getX();
        float startY = kraan.getY();
        int eindX = kraan.getXmax()-1;
        int duur = Math.abs(startX-eindX)/kraan.getXspeed();

        Beweging nieuwe = new Beweging(Integer.MIN_VALUE,0,duur,matrix[startX][(int) Math.floor(startY)][0], matrix[eindX][(int) Math.floor(startY)][0],kraan.getId(),true);
        kraan.setX(eindX);
        kraan.setY((int) Math.floor(startY));
        kraan.getAddedMovements().add(nieuwe);
        return nieuwe;
    }


    private int calculateHoogte(int eindX, float eindY,Coördinaat start, int id) {
        int hoogte = 0;
        Container c = null;
        for (Container cont: containersArray){
            if (cont.getId() == id){
                c = cont;
            }
        }
        for (int i = 0; i < this.hoogte; i++){
            int hit = 0;
            for (int j = 0; j < c.getLength(); j++){
                if (matrix[eindX+j][(int) Math.floor(eindY)][i].getContainer_id() == Integer.MIN_VALUE){
                    hit++;
                }
            }
            if (hit == c.getLength()){
                hoogte = i;
            }
        }
        return  hoogte;
    }


    private int setStartingTimes(Kraan c,int totaal) {
        if (solution.isEmpty() ){
            Beweging b = c.getBewegingLijst().get(0);
            if(b.getPriorityMoves().isEmpty()){
                int startX = b.getStart().getX();
                float startY = b.getStart().getY();
                int eindX = b.getEind().getX();
                float eindY = b.getEind().getY();
                int duurX = Math.abs(eindX - startX)/c.getXspeed();
                float duurY = Math.abs(eindY- startY)/c.getYspeed();
                float duur = Math.max(duurX,duurY);
                b.setStartTijdstip(0);
                b.setEindTijdstip((int) Math.ceil(duur));
                solution.add(b);
                c.getAddedMovements().add(b);
                c.getBewegingLijst().remove(0);
            }
        }else {
            Beweging volgende = c.getBewegingLijst().get(0);
            if (solution.containsAll(volgende.priorityMoves)){
                int startX = volgende.getStart().getX();
                float startY = volgende.getStart().getY();
                int eindX = volgende.getEind().getX();
                float eindY = volgende.getEind().getY();
                int duurX = Math.abs(eindX - startX)/c.getXspeed();
                float duurY = Math.abs(eindY- startY)/c.getYspeed();
                float duur = Math.max(duurX,duurY);
                int hits = 0;
                for (Beweging b : solution){
                    if(b.getKraan_id() == volgende.getKraan_id()){
                        hits++;
                        volgende.setStartTijdstip(b.getEindTijdstip()+1);
                        volgende.setEindTijdstip(b.getEindTijdstip()+1+(int) Math.ceil(duur));
                    }
                }
                //Hits to know if first movement of crane X
                if (hits == 0){
                    volgende.setStartTijdstip(0);
                    volgende.setEindTijdstip((int) Math.ceil(duur));
                }
                Beweging temp = null;
                boolean tempAdded = false;
                if(volgende.priorityMoves.size() != 0 && volgende.isTussenBeweging()){
                    int minimumTijd = Integer.MIN_VALUE;
                    for (Beweging priority : volgende.priorityMoves){
                        if(priority.getEindTijdstip() > minimumTijd){
                            minimumTijd = priority.getEindTijdstip();
                        }
                    }
                    if(minimumTijd > volgende.getEindTijdstip()){
                        volgende.setStartTijdstip(minimumTijd+1);
                        volgende.setEindTijdstip(minimumTijd+1+(int) Math.ceil(duur));
                    }
                }


                for (Beweging b :solution){
                    if(b.getKraan_id() != volgende.getKraan_id()){

                       if(inSameTimeInterval(b,volgende) && isCollision(b,volgende)){
                                volgende.setStartTijdstip(b.getEindTijdstip()+1);
                                volgende.setEindTijdstip(b.getEindTijdstip()+1+(int) Math.ceil(duur));
                        }
                        else if(isCrossOver(b,volgende) && inSameTimeInterval(b,volgende)){
                            temp = moveCrossOverKraan(b,volgende);
                            tempAdded = true;
                        }
                        else if(isLatestmove(b) && isCollision(b,volgende)){
                           temp = moveKraan(b.getKraan_id(), volgende.getStartTijdstip(), volgende.getStart(), volgende.getEind());
                           tempAdded = true;
                           System.out.println("collision" + b.getId());
                           System.out.println("nieuwe collision" + volgende.getId() + "kraan: " + volgende.getKraan_id());
                       }
//                        else if(isLatestmove(b) && isCrossOver(b,volgende)){
//                           temp = moveKraanCrossOverLatest(b,volgende);
//                           tempAdded = true;
//                           System.out.println("crossover " +  b.getId());
//                           System.out.println("nieuwe crossover: " + volgende.getId() + "kraan: " + volgende.getKraan_id());
//                       }

                    }
                }
                    if (tempAdded){
                        solution.add(temp);
                        totaal++;

                    }
                    c.getAddedMovements().add(volgende);
                    int kraanBeweging = c.getKraanBewegingAantal()+1;
                    volgende.setKraanBeweging(kraanBeweging);
                    c.setKraanBewegingAantal(kraanBeweging);
                    solution.add(volgende);
                    c.getBewegingLijst().remove(0);
                return totaal;
            }
            }
        return totaal;
    }

    private Beweging moveKraanCrossOverLatest(Beweging b, Beweging volgende) {
        //TODO nog te implementeren indien tijd
        return null;
    }

    private Beweging moveCrossOverKraan(Beweging b, Beweging volgende) {
        Beweging nieuw = null;
        if(volgende.isTussenBeweging()){
             for (Kraan k : cranes){
                 if (k.getId() == volgende.getKraan_id()){
                     int size = k.getAddedMovements().size();
                     Beweging lastMovement = k.getAddedMovements().get(size-1);
                     int startX = volgende.getStart().getX();
                     float startY = volgende.getStart().getY();
                     float eindY;
                     int eindX;
                     int duur;
                     if(volgende.getEind().getX() < b.getEind().getX()){
                          eindX  = b.getEind().getX() + 2;
                          if (eindX>= k.getXmax()-1){
                              eindX = k.getXmax()-1;
                          }
                          eindY = volgende.getEind().getY();
                          int duurX = Math.abs(startX-eindX)/k.getXspeed();
                          float duurY = Math.abs(startY-eindY)/k.getYspeed();
                          duur = Math.max(duurX,(int) Math.ceil(duurY));
                         int kraanBeweging = k.getKraanBewegingAantal()+1;
                          nieuw = new Beweging(Integer.MIN_VALUE,lastMovement.getEindTijdstip()+1,lastMovement.getEindTijdstip()+1+duur, volgende.getStart(),matrix[eindX][(int) Math.floor(eindY)][hoogte-1],k.getId(),true);
                          volgende.getStart().setX(eindX);
                          volgende.getStart().setY((int) Math.floor(eindY));
                          if(b.getEindTijdstip()>nieuw.getEindTijdstip()){
                              volgende.setStartTijdstip(b.getEindTijdstip());
                          }else {
                              volgende.setStartTijdstip(nieuw.getEindTijdstip()+1);
                          }
                          startX = volgende.getStart().getX();
                          startY = volgende.getStart().getY();
                         eindX  = volgende.getEind().getX();
                         eindY = volgende.getEind().getY();
                         duurX = Math.abs(startX-eindX)/k.getXspeed();
                         duurY = Math.abs(startY-eindY)/k.getYspeed();
                         duur = Math.max(duurX,(int) Math.ceil(duurY));
                         volgende.setEindTijdstip(volgende.getStartTijdstip()+duur);
                          nieuw.setKraanBeweging(kraanBeweging);
                         k.setX(eindX);
                         k.setY((int) Math.floor(eindY));
                         k.getAddedMovements().add(nieuw);
                         k.setKraanBewegingAantal(kraanBeweging);
                         return nieuw;
                     }else{
                         eindX  = b.getEind().getX() - 2;
                         if (eindX < k.getXmin()){
                             eindX = k.getXmin();
                         }
                         eindY = volgende.getEind().getY();
                         int duurX = Math.abs(startX-eindX)/k.getXspeed();
                         float duurY = Math.abs(startY-eindY)/k.getYspeed();
                         duur = Math.max(duurX,(int) Math.ceil(duurY));
                         int kraanBeweging = k.getKraanBewegingAantal()+1;
                         nieuw = new Beweging(Integer.MIN_VALUE,lastMovement.getEindTijdstip()+1,lastMovement.getEindTijdstip()+1+duur, volgende.getStart(),matrix[eindX][(int) Math.floor(eindY)][hoogte-1],k.getId(),true);
                         volgende.getStart().setX(eindX);
                         volgende.getStart().setY((int) Math.floor(eindY));
                         if(b.getEindTijdstip()>nieuw.getEindTijdstip()){
                             volgende.setStartTijdstip(b.getEindTijdstip());
                         }else {
                             volgende.setStartTijdstip(nieuw.getEindTijdstip()+1);
                         }
                         startX = volgende.getStart().getX();
                         startY = volgende.getStart().getY();
                         eindX  = volgende.getEind().getX();
                         eindY = volgende.getEind().getY();
                         duurX = Math.abs(startX-eindX)/k.getXspeed();
                         duurY = Math.abs(startY-eindY)/k.getYspeed();
                         duur = Math.max(duurX,(int) Math.ceil(duurY));
                         volgende.setEindTijdstip(volgende.getStartTijdstip()+duur);
                         nieuw.setKraanBeweging(kraanBeweging);
                         k.setX(eindX);
                         k.setY((int) Math.floor(eindY));
                         k.getAddedMovements().add(nieuw);
                         k.setKraanBewegingAantal(kraanBeweging);
                         return nieuw;
                     }

                 }
             }
        }else {
            for (Kraan k : cranes){
                if (k.getId() == volgende.getKraan_id()){
                    int size = k.getAddedMovements().size();
                    Beweging lastMovement = k.getAddedMovements().get(size-1);
                    int startX = volgende.getStart().getX();
                    float startY = volgende.getStart().getY();
                    float eindY;
                    int eindX;
                    int duur;
                    if(volgende.getEind().getX() < b.getEind().getX()){
                        eindX  = b.getEind().getX() + 2;
                        if (eindX>= k.getXmax()){
                            eindX = k.getXmax()-1;
                        }
                        eindY = volgende.getEind().getY();
                        int duurX = Math.abs(startX-eindX)/k.getXspeed();
                        float duurY = Math.abs(startY-eindY)/k.getYspeed();
                        duur = Math.max(duurX,(int) Math.ceil(duurY));
                        int kraanBeweging = k.getKraanBewegingAantal()+1;
                        nieuw = new Beweging(volgende.getId(),lastMovement.getEindTijdstip()+1,lastMovement.getEindTijdstip()+1+duur, volgende.getStart(),matrix[eindX][(int) Math.floor(eindY)][hoogte-1],k.getId(),false);
                        volgende.getStart().setX(eindX);
                        volgende.getStart().setY((int) Math.floor(eindY));
                        if(b.getEindTijdstip()>nieuw.getEindTijdstip()){
                            volgende.setStartTijdstip(b.getEindTijdstip());
                        }else {
                            volgende.setStartTijdstip(nieuw.getEindTijdstip()+1);
                        }
                        startX = volgende.getStart().getX();
                        startY = volgende.getStart().getY();
                        eindX  = volgende.getEind().getX();
                        eindY = volgende.getEind().getY();
                        duurX = Math.abs(startX-eindX)/k.getXspeed();
                        duurY = Math.abs(startY-eindY)/k.getYspeed();
                        duur = Math.max(duurX,(int) Math.ceil(duurY));
                        volgende.setEindTijdstip(volgende.getStartTijdstip()+duur);
                        nieuw.setKraanBeweging(kraanBeweging);
                        k.setX(eindX);
                        k.setY((int) Math.floor(eindY));
                        k.getAddedMovements().add(nieuw);
                        k.setKraanBewegingAantal(kraanBeweging);
                        return nieuw;
                    }else{
                        eindX  = b.getEind().getX() - 2;
                        if (eindX < k.getXmin()){
                            eindX = k.getXmin();
                        }
                        eindY = volgende.getEind().getY();
                        int duurX = Math.abs(startX-eindX)/k.getXspeed();
                        float duurY = Math.abs(startY-eindY)/k.getYspeed();
                        duur = Math.max(duurX,(int) Math.ceil(duurY));
                        int kraanBeweging = k.getKraanBewegingAantal()+1;
                        nieuw = new Beweging(volgende.getId(),lastMovement.getEindTijdstip()+1,lastMovement.getEindTijdstip()+1+duur, volgende.getStart(),matrix[eindX][(int) Math.floor(eindY)][hoogte-1],k.getId(),false);
                        volgende.getStart().setX(eindX);
                        volgende.getStart().setY((int) Math.floor(eindY));
                        if(b.getEindTijdstip()>nieuw.getEindTijdstip()){
                            volgende.setStartTijdstip(b.getEindTijdstip());
                        }else {
                            volgende.setStartTijdstip(nieuw.getEindTijdstip()+1);
                        }
                        startX = volgende.getStart().getX();
                        startY = volgende.getStart().getY();
                        eindX  = volgende.getEind().getX();
                        eindY = volgende.getEind().getY();
                        duurX = Math.abs(startX-eindX)/k.getXspeed();
                        duurY = Math.abs(startY-eindY)/k.getYspeed();
                        duur = Math.max(duurX,(int) Math.ceil(duurY));
                        volgende.setEindTijdstip(volgende.getStartTijdstip()+duur);
                        nieuw.setKraanBeweging(kraanBeweging);
                        k.setX(eindX);
                        k.setY((int) Math.floor(eindY));
                        k.getAddedMovements().add(nieuw);
                        k.setKraanBewegingAantal(kraanBeweging);
                        return nieuw;
                    }

                }
            }
        }

        return nieuw;
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
                        eindX=k.getXmin();
                    }else if(eindX < k.getXmin()) {
                        eindX = k.getXmin();
                    }
                }else {
                    eindX = startX + 2;
                    if (eindX>=lengte){
                        //TODO check when fully implemented
                        eindX=lengte-1;
                    }
                }
                duur = 2/k.getXspeed();
                int kraanBeweging = k.getKraanBewegingAantal()+1;
                Beweging nieuwe = new Beweging(Integer.MIN_VALUE,startTijdstip,startTijdstip+duur,matrix[startX][(int) Math.floor(startY)][0], matrix[eindX][(int) Math.floor(startY)][0],kraan_id,true);
                nieuwe.setKraanBeweging(kraanBeweging);
                k.setX(eindX);
                k.setY((int) Math.floor(startY));
                k.getAddedMovements().add(nieuwe);
                k.setKraanBewegingAantal(kraanBeweging);
                return nieuwe;
            }
        }
        return null;
    }

    private boolean isLatestmove(Beweging b) {
        for (Kraan k : cranes){
            if(b.getKraan_id() == k.getId()){
                int index = k.getAddedMovements().size();
                if(k.getAddedMovements().get(index-1).getEindTijdstip() == b.getEindTijdstip() && k.getBewegingLijst().isEmpty()){
                    return  true;
                }
            }
        }
        return false;
    }

    private boolean isCrossOver(Beweging b, Beweging volgende) {
        boolean crossover = false;
        if(b.getStart().getX() < volgende.getStart().getX()){
            if(b.getEind().getX() >= volgende.getEind().getX()){
                crossover = true;
            }
        }else if(b.getStart().getX() > volgende.getStart().getX()){
            if(b.getEind().getX() <= volgende.getEind().getX()){
                crossover = true;
            }
        }


        return crossover;
    }


    private boolean isCollision(Beweging b, Beweging volgende) {

        return volgende.getEind().getX() == b.getEind().getX();
    }

    private boolean inSameTimeInterval(Beweging b, Beweging volgende) {
        return b != volgende && ((b.getStartTijdstip() <= volgende.getEindTijdstip() && b.getEindTijdstip() >= volgende.getEindTijdstip()) || (b.getStartTijdstip() <= volgende.getStartTijdstip() && b.getEindTijdstip() >= volgende.getStartTijdstip()));
    }


    private void makeMovement(Container c) {
        int startX = mapping_id_xcoor.get(c.getSlot_id());
        int startY = mapping_id_ycoor.get(c.getSlot_id());
        int eindX = mapping_id_xcoor.get(c.getTarget_id());
        int eindY= mapping_id_ycoor.get(c.getTarget_id());
        ArrayList<Kraan> availableCranes = new ArrayList<>();
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
            Kraan kleinst = null;
            int grootte = Integer.MAX_VALUE;


            for (Kraan kraan : availableCranes){
//                if (kraan.getBewegingLijst().size() < grootte){
//                    grootte = kraan.getBewegingLijst().size();
//                    kleinst = kraan;
//                }
                if(Math.abs(eindX - kraan.getX()) < grootte){
                    grootte = Math.abs(eindX - kraan.getX());
                    kleinst = kraan;
                }
            }
            assert kleinst != null;
            addMovement(c,kleinst);
        }
        else if(availableCranes.size()==0){

            System.out.println("No cranes");
            System.out.println(c);
            //TODO implement noCranesTakeFullMovement, wss extra containers toevoegen aan containersthatneedtobemoved
            Kraan startKraan = null;
            Kraan eindKraan = null;
            for (Kraan k : cranes){
                   if(k.getXmin() <= mapping_id_xcoor.get(c.getSlot_id()) && k.getXmax() >= mapping_id_xcoor.get(c.getSlot_id())){
                       startKraan = k;
                   }
                   if (k.getXmin() <= mapping_id_xcoor.get(c.getTarget_id()) && k.getXmax() >= mapping_id_xcoor.get(c.getTarget_id())){
                       eindKraan = k;
                   }
            }

            addMovementsFreePlaceBetweenCranes(startKraan,eindKraan,c);


        }
        else{
            addMovement(c,availableCranes.get(0));       
        }
    }

    private void addMovementsFreePlaceBetweenCranes(Kraan startKraan, Kraan eindKraan, Container c) {
        int startX= mapping_id_xcoor.get(c.getSlot_id());
        int startY= mapping_id_ycoor.get(c.getSlot_id());
        int eindX= mapping_id_xcoor.get(c.getTarget_id());
        int eindY= mapping_id_ycoor.get(c.getTarget_id());
        int startXSimilar;
        int endXsimilar;

        if (startX > eindX){
            startXSimilar = startKraan.getXmin();
            endXsimilar =   eindKraan.getXmax();
        } else {
            startXSimilar = eindKraan.getXmin();
            endXsimilar = startKraan.getXmax();
        }
        boolean test = false;
        for (int h = hoogte-1; h >= 0 && !test;h--){
            for(int i = startXSimilar; i <= endXsimilar && !test; i++){
                for (int j = 0; j < breedte && !test; j++){
                    if(feasible(i,j,h,c)){

                        if (startKraan.getX() != startX || startKraan.getY() != startY){
                            Beweging tussen = new Beweging(c.getId(),0,0,matrix[startKraan.getX()][(int) Math.floor(startKraan.getY())][startKraan.getZ()],matrix[startX][startY][c.getHoogte()],startKraan.getId(),true);
                            temp.add(tussen);
                            startKraan.getBewegingLijst().add(tussen);
                            startKraan.setX(startX);
                            startKraan.setY(startY);
                        }

                        Beweging effec = new Beweging(c.getId(),0,0,matrix[startX][startY][c.getHoogte()],matrix[i][j][h],startKraan.getId(),false);
                        temp.add(effec);
                        startKraan.getBewegingLijst().add(effec);
                        startKraan.setX(i);
                        startKraan.setY(j);


                        if (eindKraan.getX() != i || eindKraan.getY() != j){
                            Beweging tussen = new Beweging(c.getId(),0,0,matrix[eindKraan.getX()][(int) Math.floor(eindKraan.getY())][eindKraan.getZ()],matrix[i][j][h],eindKraan.getId(),true);
                            temp.add(tussen);
                            tussen.priorityMoves.add(effec);
                            eindKraan.getBewegingLijst().add(tussen);
                            eindKraan.setX(i);
                            eindKraan.setY(j);
                        }

                        System.out.println("start" + startKraan.getId());
                        System.out.println("eind" + eindKraan.getId());

                        Beweging eind = new Beweging(c.getId(),0,0,matrix[i][j][h],matrix[eindX][eindY][0],eindKraan.getId(),false);
                        temp.add(eind);
                        eind.priorityMoves.add(effec);
                        eindKraan.getBewegingLijst().add(eind);
                        eindKraan.setX(eindX);
                        eindKraan.setY(eindY);
                        test  =true;
                        break;
                    }
                }
            }
        }



    }

    private boolean feasible(int i, int j, int h, Container c) {
        int count = 0;
        int idOfPossibleEmptySlot = matrix[i][j][h].getId();
        for (int k = 0; k < c.getLength(); k++){
                if (matrix[i+k][j][h].getContainer_id()==Integer.MIN_VALUE){
                    if (h != 0){
                        int containerBelow = matrix[mapping_id_xcoor.get(idOfPossibleEmptySlot+k)][mapping_id_ycoor.get(idOfPossibleEmptySlot)][h-1].getContainer_id();
                        // Constraint 2
                        if (h-1 != 0 && (containerBelow == Integer.MIN_VALUE)) {
                            break;
                        }
                        // Constraint 3
                        //TODO: klopt constraint?
                        if (h-1 != 0 && c.getLength()< containersArray.get(containerBelow).getLength()) {
                            break;
                        }else {
                            count++;
                        }
                    }else {
                        count++;
                    }
                }
            }
        return count == c.getLength();
    }


    private void addMovement(Container c, Kraan kraan) {
        int startX= mapping_id_xcoor.get(c.getSlot_id());
        int startY= mapping_id_ycoor.get(c.getSlot_id());
        int eindX= mapping_id_xcoor.get(c.getTarget_id());
        int eindY= mapping_id_ycoor.get(c.getTarget_id());

        if (kraan.getX() != startX || kraan.getY() != startY){
            Beweging tussen = new Beweging(c.getId(),0,0,matrix[kraan.getX()][(int) Math.floor(kraan.getY())][kraan.getZ()],matrix[startX][startY][c.getHoogte()],kraan.getId(),true);
            temp.add(tussen);
            kraan.getBewegingLijst().add(tussen);
            kraan.setX(startX);
            kraan.setY(startY);
            kraan.setZ(c.getHoogte());
        }

        Beweging effec = new Beweging(c.getId(),0,0,matrix[startX][startY][c.getHoogte()],matrix[eindX][eindY][c.getTarget_hoogte()],kraan.getId(),false);
        temp.add(effec);
        kraan.getBewegingLijst().add(effec);
        kraan.setX(eindX);
        kraan.setY(eindY);
        kraan.setZ(c.getTarget_hoogte());
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
                c.setTarget_hoogte(i);
                return true;
            }
        }
        int indexCont = Integer.MIN_VALUE;
        int indexC = Integer.MIN_VALUE;
        for (Container cont : notOnTargetId){
            if(cont.getSlot_id() == c.getTarget_id()){
                  indexCont = notOnTargetId.indexOf(cont);
                  indexC = notOnTargetId.indexOf(c);
                  c.getPriorityMoves().add(cont);
                  c.setTarget_hoogte(cont.getHoogte());
            }
        }

        if(indexCont < indexC){
            return true;
        }else {
            notOnTargetId.remove(indexC);
            notOnTargetId.add(indexCont,c);
            return true;
        }
        //TODO: Implement, if not on target id  in arraylist containerstobemoved, done I think?
        //return false;
    }

    private boolean checkIfContainerFreeToMove(Container c, ArrayList<Container> containersArray ) {

        int x = mapping_id_xcoor.get(c.getSlot_id());
        int y = mapping_id_ycoor.get(c.getSlot_id());
        int z = c.getHoogte();
        //If container on top no need to check below
        if (z != hoogte-1){
            for (int i = 0; i < c.getLength();i++){
                if(Integer.MIN_VALUE != matrix[x + i][y][z+1].getContainer_id()){
                        for (Container container: containersArray){
                            if (container.getId() == matrix[x + i][y][z+1].getContainer_id()){
                                checkIfContainerFreeToMove(container, containersArray);
                                break;
                            }
                        }
                }
                if(!containersThatNeedToBeMoved.contains(c)){
                    containersThatNeedToBeMoved.add(c);
                    return true;
                }else {
                    return true;
                }
            }
        }else {
            if(containersThatNeedToBeMoved.containsAll(c.getPriorityMoves())){
                containersThatNeedToBeMoved.add(c);
                return true;
            }
            else {
                return false;
            }
        }
        return false;
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

    public void setMatrix(Coördinaat[][][] matrix) {
        this.matrix = matrix;
    }

    public ArrayList<Kraan> getCranes() {
        return cranes;
    }

    public void setCranes(ArrayList<Kraan> cranes) {
        this.cranes = cranes;
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
