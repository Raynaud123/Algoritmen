import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class Main {

    //methodes is er conflict, zo ja conflict pas de het pad aan om conflict te vermijden
    public static void main(String[] args) {


        new Inlezer();

        //Path ingeven
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/2mh/MH2Terminal_20_10_3_2_100.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/4mh/MH2Terminal_20_10_3_2_160.json");
        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/1t/TerminalA_20_10_3_2_100.json");
//       JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/3t/TerminalA_20_10_3_2_160.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/5t/TerminalB_20_10_3_2_160.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/6t/Terminal_10_10_3_1_100.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/7t/TerminalC_10_10_3_2_80.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/8t/TerminalC_10_10_3_2_80.json");
        //       JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/9t/TerminalC_10_10_3_2_100.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/10t/TerminalC_10_10_3_2_100.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/Voorbeeld1/terminal22_1_100_1_10.json");
        int maxHeight = 0;
        int width = 0;
        int length = 0;
        int targetHeight = Integer.MIN_VALUE;
        JSONArray slots = null;
        JSONArray assignments = null;
        JSONArray cranes = null;
        JSONArray containers = null;
        JSONArray targetassignments = null;


        assert data != null;
        if(data.containsKey("maxheight")){
            maxHeight = (int) ((long) data.get("maxheight"));
        }
        if(data.containsKey("width")){
            width = (int) ((long)data.get("width"));
        }
        if(data.containsKey("length")){
            length = (int) ((long)data.get("length"));
        }
        if(data.containsKey("targetheight")){
            targetHeight= (int) ((long) data.get("targetheight"));
        }
        else{
            //        JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/Voorbeeld1/terminal22_1_100_1_10target.json");
                      JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/1t/targetTerminalA_20_10_3_2_100.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/3t/targetTerminalA_20_10_3_2_160.json");
            ///        JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/5t/targetTerminalB_20_10_3_2_160UPDATE.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/6t/targetTerminal_10_10_3_1_100.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/7t/targetTerminalC_10_10_3_2_80.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/8t/targetTerminalC_10_10_3_2_80.json");
            //           JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/9t/targetTerminalC_10_10_3_2_100.json");
//         JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/10t/targetTerminalC_10_10_3_2_100.json");
            targetassignments = (JSONArray) target.get("assignments");
        }
        if(data.containsKey("slots")){
            slots = (JSONArray) data.get("slots");
        }
        if (data.containsKey("assignments")){
            assignments = (JSONArray) data.get("assignments");
        }
        if(data.containsKey("containers")){
            containers = (JSONArray) data.get("containers");
        }
        if(data.containsKey("cranes")){
            cranes =(JSONArray) data.get("cranes");
        }



        Yard yard = new Yard();
        ArrayList<Container> containersArray =  new ArrayList<>();

        assert slots != null;
        yard.createYard(slots, length, width, maxHeight);

        // containers
        assert containers != null;
        for (Object value : containers) {
            JSONObject container = new JSONObject();
            container.putAll((Map) value);
            Container nieuw = new Container(container.get("id"), container.get("length"));
            containersArray.add(nieuw);
        }

        // assignments
        assert assignments != null;
        for (Object o : assignments) {
            JSONObject assignment = new JSONObject();
            assignment.putAll((Map) o);
            int index = Integer.MIN_VALUE;
            for(int i = 0; i < containersArray.size();i++){
                if(((Long) assignment.get("container_id")).intValue() == containersArray.get(i).getId()){
                    index = i;
                    break;
                }
            }
            yard.addContainer(assignment.get("slot_id"), containersArray.get(index));
        }

        assert cranes != null;
        yard.addCranes(cranes);
        //System.out.println(yard.toString());


        for (int i=0; i<maxHeight; i++) {
            new Gui("Hoogte " + i, yard, i);
        }


        if(targetHeight==Integer.MIN_VALUE){
            yard.calculateMovementsTargetAssignments(targetassignments,containersArray);
        }else {
            yard.calculateMovementsTargetHeight(maxHeight, targetHeight,containersArray);
        }


//        for (Kraan k : yard.cranes){
//            System.out.println(k.getId() + ": " + k.getBewegingLijst());
//            System.out.println();
//        }

        for(Beweging b: yard.solution){
            if(b.getKraan_id() == 0){
                System.out.println(b);
            }
        }

        System.out.println();

        for(Beweging b: yard.solution){
            if(b.getKraan_id() == 1){
                System.out.println(b);
            }
        }

//        for (int i=0; i<maxHeight; i++) {
//            new Gui("Hoogte " + i, yard, i);
//        }

    }
}
