import java.awt.*;
import javax.swing.*;

public class Gui extends JFrame {

    public Gui(String title, Yard yard, int hoogte) {

        final int lengte = yard.getLengte();
        final int breedte = yard.getBreedte();

        // Color of board
        Color green = Color.GREEN;
        Color orange = Color.ORANGE;
        JButton slot;

        for (int i=0; i<lengte; i++) {
            for (int j=0; j<breedte; j++) {
                if (yard.matrix[i][j][hoogte].getContainer_id() < 0) {
                    slot = new JButton();
                    slot.setBackground(green);
                    add(slot);
                }
                else {
                    slot = new JButton();
                    slot.setBackground(orange);
                    slot.setText(Integer.toString(yard.getMatrix()[i][j][hoogte].getContainer_id()));
                    add(slot);
                }
            }
        }

        this.setTitle(title); // Setting the title of board
        this.setLayout(new GridLayout(yard.getLengte(), yard.getBreedte())); // GridLayout will arrange elements in Grid Manager
        this.setSize(1000, 1000); // Size of the board
        this.setVisible(true);
    }
}