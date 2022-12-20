import java.awt.*;
import javax.swing.*;

public class Gui extends JFrame {

    public Gui(String title, Yard yard, int hoogte) {

        final int lengte = yard.getLengte();
        final int breedte = yard.getBreedte();
        final int squareCount = lengte*breedte;

        // Color of board
        Color green = Color.GREEN;
        Color orange = Color.ORANGE;
        JButton chessButton;

        for (int i=0; i<lengte; i++) {
            for (int j=0; j<breedte; j++) {
                if (yard.matrix[i][j][hoogte].getContainer_id() < 0) {
                    chessButton = new JButton();
                    chessButton.setBackground(green);
                    add(chessButton);
                }
                else {
                    chessButton = new JButton();
                    chessButton.setBackground(orange);
                    add(chessButton);
                }
            }
        }

/*        for (int i = 1; i <= squareCount; i++) {
            if (i % 2 == 0) { // Adding color based on the odd and even initially.
                chessButton = new JButton();
                chessButton.setBackground(green);
                add(chessButton);
            } else {
                chessButton = new JButton();
                chessButton.setBackground(orange);
                add(chessButton);
            }
        }*/

        this.setTitle(title); // Setting the title of board
        this.setLayout(new GridLayout(yard.getLengte(), yard.getBreedte())); // GridLayout will arrange elements in Grid Manager
        this.setSize(1000, 1000); // Size of the board
        this.setVisible(true);
    }
}