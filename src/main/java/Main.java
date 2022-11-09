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

        System.out.println(slots.get(1));

        Yard yard = new Yard();

        int lengte  = Integer.MIN_VALUE;
        int breedte = Integer.MIN_VALUE;

        for (int i = 0; i < slots.size(); i++) {
            JSONObject slot = new JSONObject();
            slot.putAll((Map) slots.get(i));
            int x = (int) slot.get("x");
            int y = (int) slot.get("y");
            if(x > lengte){
                lengte = x;
            }
            if(y >breedte){
                breedte = y;
            }
        }

        int hoogte = 2;
        yard.createYard(slots, lengte, breedte, hoogte);



        Scanner sc = new Scanner(System.in);

        int aantalKranen = sc.nextInt();

        if (aantalKranen > 3) {
            aantalKranen = 3;
        }

        ContainerStack containerStack = new ContainerStack();
        containerStack.display(2, 3);

    }


}

