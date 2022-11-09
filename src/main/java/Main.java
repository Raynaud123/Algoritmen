package src.main.java;

import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Scanner;

public class Main{

//methodes is er conflict, zo ja conflict pas de het pad aan om conflict te vermijden
    public static void main(String[] args) {

        JSONParser jsonParser = new JSONParser();



        Scanner sc = new Scanner(System.in);

        int aantalKranen = sc.nextInt();

        if (aantalKranen > 3) {
            aantalKranen = 3;
        }
}


}

