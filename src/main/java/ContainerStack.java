import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ContainerStack extends JFrame {

    JFrame frame = new JFrame("Container Stack bovenaanzicht");
    JPanel panelCenter;
    JPanel[][] panel;
    JLabel labelHeight;
    int currentHeight = 0;

    public void display(Yard yard) {
        int x_length = yard.matrix.length;
        int y_length = yard.matrix[0].length;

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //indicates terminate operation on close of window
        frame.setSize(800, 800);
        frame.setLayout(new BorderLayout(10,10));
//        frame.setResizable(false);

        // North
        JPanel panelNorth = new JPanel();
        panelNorth.setLayout(new BorderLayout());
            // button for layer up
        JButton jButtonUp = new JButton("Layer up");
        jButtonUp.addActionListener(e -> {
            if (currentHeight < yard.matrix[0][0].length-1) {
                currentHeight++;
                labelHeight.setText("Height = " + currentHeight);
                panelCenter = new JPanel();
                panelCenter.setLayout(new GridLayout(x_length, y_length));
                fillYard(yard, panelCenter, currentHeight);
                frame.add(panelCenter, BorderLayout.CENTER);
            }
        });
            // button for layer down
        JButton jButtonDown = new JButton("Layer down");
        jButtonDown.addActionListener(e -> {
            if (currentHeight > 0) {
                currentHeight--;
                labelHeight.setText("Height = " + currentHeight);
                panelCenter = new JPanel();
                panelCenter.setLayout(new GridLayout(x_length, y_length));
                fillYard(yard, panelCenter, currentHeight);
                frame.add(panelCenter, BorderLayout.CENTER);
            }
        });
        panelNorth.add(jButtonUp, BorderLayout.WEST);
        panelNorth.add(jButtonDown, BorderLayout.EAST);

        frame.add(panelNorth, BorderLayout.NORTH);

        // Center
        panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(x_length, y_length));

        fillYard(yard, panelCenter, currentHeight);
        frame.add(panelCenter, BorderLayout.CENTER);

        // South
        JPanel panelSouth = new JPanel();
        panelSouth.setLayout(new BorderLayout());

        labelHeight = new JLabel("Height = " + currentHeight);
        panelSouth.add(labelHeight, BorderLayout.CENTER);

        frame.add(panelSouth, BorderLayout.SOUTH);
    }

    public static void fillYard(Yard yard, JPanel panelCenter, int currentHeight) {
        int x_length = yard.matrix.length;
        int y_length = yard.matrix[0].length;
        JPanel[][] panel = new JPanel[x_length][y_length];

        for (int i=0; i<x_length; i++) {
            for (int j=0; j<y_length; j++) {

                panel[i][j] = new JPanel();
                panel[i][j].setLayout(new BoxLayout(panel[i][j], BoxLayout.X_AXIS));
                panel[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                if (yard.matrix[i][j][currentHeight].getContainer_id() < 0) {
                    panel[i][j].setBackground(Color.GREEN);
                    panel[i][j].add(new JLabel("Slot (" + i + "," + j + ") = free"));
                }
                else {
                    panel[i][j].setBackground(Color.ORANGE);
                    panel[i][j].add(new JLabel("Slot (" + i + "," + j + ") = " + yard.matrix[i][j][currentHeight].getContainer_id()));
                }
                panelCenter.add(panel[i][j]);
            }
        }
    }
}