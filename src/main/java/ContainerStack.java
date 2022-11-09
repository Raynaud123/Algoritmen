import java.awt.*;
import javax.swing.*;

public class ContainerStack extends JFrame {

    JFrame frame = new JFrame("Container Stack bovenaanzicht");

    public void display(int x, int y) {
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //indicates terminate operation on close of window
        GridLayout gl = new GridLayout(x, y);//create grid layout frame
        JPanel panel = new JPanel();
        panel.setLayout(gl);

        frame.setSize(600, 600);

        for (int i=0; i<x; i++) {
            for (int j=0; j<y; j++) {
                JTextArea text = new JTextArea("Slot (" + i + "," + j + ")");
                text.setEditable(false);
                panel.add(text);
            }
        }

        frame.add(panel);
    }
}