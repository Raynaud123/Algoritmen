import java.util.ArrayList;
import java.util.Scanner;

public class Main{

//methodes is er conflict, zo ja conflict pas de het pad aan om conflict te vermijden
    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);

        int aantalKranen = sc.nextInt();

        if (aantalKranen > 3) {
            aantalKranen = 3;
        }

        ContainerStack containerStack = new ContainerStack();
        containerStack.display(2, 3);

    }


}

