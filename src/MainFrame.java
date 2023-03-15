import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{
    public MainFrame() {
        setTitle("Conway's Game of Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GamePanel panel = new GamePanel();
        setJMenuBar(new GameMenuBar(panel));
        panel.setPreferredSize(new Dimension(800, 800));
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }
}
