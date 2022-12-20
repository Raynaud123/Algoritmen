import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Main {

    //methodes is er conflict, zo ja conflict pas de het pad aan om conflict te vermijden
    public static void main(String[] args) {


        Inlezer jsonInlezer = new Inlezer();

        //Path ingeven

        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/Voorbeeld1/terminal22_1_100_1_10.json");
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
            JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/Voorbeeld1/terminal22_1_100_1_10target.json");
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
        

//        System.out.println(slots.get(1));
//        System.out.println(maxHeight);
//        System.out.println(width);
//        System.out.println(length);

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
            yard.addContainer(assignment.get("slot_id"), containersArray.get(((Long) assignment.get("container_id")).intValue() - 1));
        }
        assert cranes != null;
        yard.addCranes(cranes);
       //System.out.println(yard.toString());

        if(targetHeight==Integer.MIN_VALUE){
            yard.calculateMovementsTargetAssignments(targetassignments,containersArray);
        }else {
            yard.calculateMovementsTargetHeight(targetHeight,containersArray);
        }


        System.out.println(yard.toString());




        Gui gui = new Gui("Container gui", yard, 0);

//        ContainerStack containerStack = new ContainerStack();
//        containerStack.display(yard);
//        yard.voerBewegingUit(4)

    }


}

