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

public class Main{

//methodes is er conflict, zo ja conflict pas de het pad aan om conflict te vermijden
    public static void main(String[] args) {


        Inlezer jsonInlezer = new Inlezer();

        JSONObject data = Inlezer.inlezenJSON();

        Yard yard = new Yard();

        JSONArray slots  = (JSONArray) data.get("slots");

        System.out.println(slots);
        int hoogte = 3;


        for (int i = 0; i < slots.size(); i++){

        }


        Scanner sc = new Scanner(System.in);

        int aantalKranen = sc.nextInt();

        if (aantalKranen > 3) {
            aantalKranen = 3;
        }

        ContainerStack containerStack = new ContainerStack();
        containerStack.display(2, 3);

    }


}

