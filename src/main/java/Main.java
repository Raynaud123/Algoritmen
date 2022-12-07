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
        JSONObject data = Inlezer.inlezenJSON("inputGroot.json");


        int maxHeight = (int) ((long) data.get("maxheight"));
        int width = (int) ((long)data.get("width"));
        int length = (int) ((long)data.get("length"));
        JSONArray slots = (JSONArray) data.get("slots");
        JSONArray assignments = (JSONArray) data.get("assignments");
        JSONArray containers = (JSONArray) data.get("containers");
        JSONArray cranes =(JSONArray) data.get("cranes");

//        System.out.println(slots.get(1));
//        System.out.println(maxHeight);
//        System.out.println(width);
//        System.out.println(length);

        Yard yard = new Yard();
        ArrayList<Container> containersArray =  new ArrayList<>();


        yard.createYard(slots, length, width, maxHeight);

        // containers
        for(int i = 0; i < containers.size(); i++){
            JSONObject container = new JSONObject();
            container.putAll((Map) containers.get(i));
            Container nieuw = new Container(container.get("id"), container.get("length"));
            containersArray.add(nieuw);
        }

        // assignments
        for(int i = 0; i < assignments.size(); i++){
            JSONObject assignment = new JSONObject();
            assignment.putAll((Map) assignments.get(i));
            yard.addContainer(assignment.get("slot_id"),containersArray.get(((Long) assignment.get("container_id")).intValue()-1));
        }

        System.out.println(yard);



        ContainerStack containerStack = new ContainerStack();
        containerStack.display(yard);
//        yard.voerBewegingUit(4)

    }


}

