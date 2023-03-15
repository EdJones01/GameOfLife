import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private final int width = 50;
    private final int height = 50;

    private int cellWidth;
    private int cellHeight;

    private Timer updateTimer;

    private Cell[][] cells = new Cell[width][height];

    private Color cellColor = Color.yellow;

    public GamePanel() {
        addMouseListener(this);
        addMouseMotionListener(this);

        generateCells();

        setupTimer(10);

        Tools.addKeyBinding(this, KeyEvent.VK_RIGHT, "update", (evt) -> update());
        Tools.addKeyBinding(this, KeyEvent.VK_SPACE, "timer", (evt) -> toggleUpdateTimer());
    }

    private void toggleUpdateTimer() {
        if (updateTimer.isRunning())
            updateTimer.stop();
        else
            updateTimer.start();
    }

    private void generateCells() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(x, y, false);
            }
        }
        repaint();
    }

    private void update() {
        Cell[][] next = new Cell[cells.length][cells[0].length];
        for (int x = 0; x < cells.length; x++)
            for (int y = 0; y < cells[0].length; y++)
                next[x][y] = cells[x][y].update(getNeighboursOfCell(cells[x][y]));
        cells = next;
        repaint();
    }

    private int getNeighboursOfCell(Cell c) {
        int count = 0;
        int[][] xyoffsets = {{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};

        for (int[] xyoffset : xyoffsets) {
            int x = c.getX() + xyoffset[0];
            int y = c.getY() + xyoffset[1];
            try {
                if (cells[x][y].isAlive())
                    count++;
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        return count;
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        cellWidth = getWidth() / width;
        cellHeight = getHeight() / height;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell c = cells[x][y];

                if (c.isAlive())
                    g2.setColor(cellColor);
                else
                    g2.setColor(Color.gray);

                g2.fill3DRect(c.getX() * cellWidth, c.getY() * cellHeight, cellWidth, cellHeight, c.isAlive());
            }
        }
    }

    private boolean mouseOutsideOfWindow(MouseEvent e) {
        return e.getX() < 0 || e.getX() > getWidth() - 1 || e.getY() < 0 || e.getY() > getHeight() - 1;
    }

    private Cell getCell(MouseEvent e) {
        int x = (int) Math.floor(e.getX() / cellWidth);
        int y = (int) Math.floor(e.getY() / cellHeight);
        return cells[x][y];
    }

    private void setupTimer(int fps) {
        updateTimer = new Timer(1000 / fps, this);
        updateTimer.setActionCommand("update");
    }

    private void toggleCell(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e))
            getCell(e).setAlive(true);

        if (SwingUtilities.isRightMouseButton(e))
            getCell(e).setAlive(false);

        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("update"))
            update();
        if (cmd.equals("new"))
            generateCells();
        if (cmd.equals("toggle"))
            toggleUpdateTimer();
        if (cmd.contains("fps_"))
            updateTimer.setDelay(1000 / Integer.parseInt(cmd.replaceAll("fps_", "")));
        if (cmd.equals("color"))
            cellColor = Tools.chooseColorDialog("Choose cell color", cellColor);
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (mouseOutsideOfWindow(e))
            return;
        toggleCell(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseOutsideOfWindow(e))
            return;
        toggleCell(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}