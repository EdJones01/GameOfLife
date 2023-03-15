import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.*;

public class GameMenuBar extends JMenuBar {
    public GameMenuBar(ActionListener actionListener) {
        JMenu fileMenu = new JMenu("File");

        fileMenu.add(createMenuItem("New", actionListener));

        JMenu simulationMenu = new JMenu("Simulation");
        simulationMenu.add(createMenuItem("Toggle Simulation", "toggle", actionListener));
        JMenuItem fpsLabel = createMenuItem("FPS = 10", null);
        simulationMenu.add(fpsLabel);

        JSlider speedSlider = new JSlider(1, 60, 10);
        speedSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fpsLabel.setText("FPS = " + speedSlider.getValue());
                actionListener.actionPerformed(new ActionEvent(speedSlider, 0, "fps_" + speedSlider.getValue()));
            }
        });
        simulationMenu.add(speedSlider);

        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.add(createMenuItem("Change cell color", "color", actionListener));

        add(fileMenu);
        add(simulationMenu);
        add(settingsMenu);
    }

    private JMenuItem createMenuItem(String name, ActionListener actionListener) {
        return createMenuItem(name, name.toLowerCase(), actionListener);
    }

    private JMenuItem createMenuItem(String name, String cmd, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(actionListener);
        menuItem.setActionCommand(cmd);
        return menuItem;
    }
}