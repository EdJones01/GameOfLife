import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyManagementException;
import java.util.Arrays;
import java.io.File;
import javax.swing.event.*;

public class GameMenuBar extends JMenuBar {
    ActionListener actionListener;

    public GameMenuBar(ActionListener actionListener) {
        this.actionListener = actionListener;
        JMenu fileMenu = new JMenu("File");

        fileMenu.add(createMenuItem("New"));
        fileMenu.add(createMenuItem("Open"));
        fileMenu.add(createMenuItem("Save"));

        JMenu simulationMenu = new JMenu("Simulation");
        simulationMenu.add(createMenuItem("Toggle Simulation", "toggle"));
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
        settingsMenu.add(createMenuItem("Change cell color", "color"));

        add(fileMenu);
        add(simulationMenu);
        add(createPresetMenu());
        add(settingsMenu);
    }

    private JMenu createPresetMenu() {
        JMenu presetMenu = new JMenu("Presets");

        presetMenu.add(createPresetMenuItem("Glider"));
        presetMenu.add(createPresetMenuItem("Gun"));
        presetMenu.add(createPresetMenuItem("Spaceship"));

        return presetMenu;
    }

    private JMenuItem createPresetMenuItem(String preset) {
        String name = preset.substring(0, 1).toUpperCase() + preset.substring(1);
        return createMenuItem(name, "preset_" + preset.toLowerCase() + ".gol");
    }

    private JMenuItem createMenuItem(String name) {
        return createMenuItem(name, name.toLowerCase());
    }

    private JMenuItem createMenuItem(String name, String cmd) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(actionListener);
        menuItem.setActionCommand(cmd);
        return menuItem;
    }
}