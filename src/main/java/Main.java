import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Main {

    //methodes is er conflict, zo ja conflict pas de het pad aan om conflict te vermijden
    public static void main(String[] args) {


        Inlezer jsonInlezer = new Inlezer();

        JSONObject data = Inlezer.inlezenJSON();


        JSONArray slots = (JSONArray) data.get("slots");
        JSONArray assignments = (JSONArray) data.get("assignments");
        JSONArray containers = (JSONArray) data.get("containers");

        System.out.println(slots.get(1));

        Yard yard = new Yard();
        ArrayList<Container> containersArray =  new ArrayList<>();

        int lengte  = Integer.MIN_VALUE;
        int breedte = Integer.MIN_VALUE;

        for (int i = 0; i < slots.size(); i++) {
            JSONObject slot = new JSONObject();
            slot.putAll((Map) slots.get(i));
            int x = ((Long) slot.get("x")).intValue();
            int y =  ((Long) slot.get("y")).intValue();
            if(x > lengte){
                lengte = x;
            }
            if(y >breedte){
                breedte = y;
            }
        }

        int hoogte = 2;
        yard.createYard(slots, lengte +1, breedte+1, hoogte);

        for(int i = 0; i < containers.size(); i++){
            JSONObject container = new JSONObject();
            container.putAll((Map) containers.get(i));
            Container nieuw = new Container(container.get("id"), container.get("length"));
            containersArray.add(nieuw);
        }

        for(int i = 0; i < assignments.size(); i++){
            JSONObject assignment = new JSONObject();
            assignment.putAll((Map) assignments.get(i));
            yard.addContainer(assignment.get("slot_id"),containersArray.get(((Long) assignment.get("container_id")).intValue()-1));
        }

        System.out.println(yard);

//        Scanner sc = new Scanner(System.in);
//
//        int aantalKranen = sc.nextInt();
//
//        if (aantalKranen > 3) {
//            aantalKranen = 3;
//        }
//
//        ContainerStack containerStack = new ContainerStack();
//        containerStack.display(2, 3);

    }


}

