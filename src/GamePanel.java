import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private final int initialSize = 50;
    private final int minimumSize = 5;

    private double cellWidth;
    private double cellHeight;

    private Timer updateTimer;

    private Cell[][] cells = new Cell[initialSize][initialSize];

    private Color cellColor = Color.yellow;

    public GamePanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

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
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
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

        cellWidth = (double) getWidth() / cells.length;
        cellHeight = (double) getHeight() / cells[0].length;

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                Cell c = cells[x][y];

                if (c.isAlive())
                    g2.setColor(cellColor);
                else
                    g2.setColor(Color.gray);

                g2.fill3DRect((int) (c.getX() * cellWidth), (int) (c.getY() * cellHeight),
                        (int) (cellWidth), (int) (cellHeight), c.isAlive());
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

    private void save() {
        String[] rows = new String[cells[0].length];
        for (int x = 0; x < cells.length; x++) {
            StringBuilder row = new StringBuilder();
            for (int y = 0; y < cells[0].length; y++) {
                row.append(cells[y][x].isAlive()).append(" ");
            }
            rows[x] = row.toString().trim();
        }
        String data = String.join("\n", rows);
        data += "\n" + cells.length + "," + cells[0].length;
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyy HH_mm_ss");
        try {
            Tools.saveToFile(data, Tools.chooseDirectoryWindow() + dateFormat.format(new Date()) + ".gol");
            Tools.showPopup("Save successful.");
        } catch (IOException e) {
            Tools.showPopup("Failed to save file.");
        }
    }

    private void open() {
        String[] data;
        try {
            data = Tools.readFromFile(getFilePath());
        } catch (Exception e) {
            Tools.showPopup("Failed to load file.");
            return;
        }
        String[] dimentions = data[data.length - 1].split(",");
        cells = new Cell[Integer.parseInt(dimentions[0])][Integer.parseInt(dimentions[1])];
        generateCells();

        boolean[][] outputArray = new boolean[data.length - 1][];
        for (int i = 0; i < data.length - 1; i++) {
            String[] row = data[i].split(" ");
            outputArray[i] = new boolean[row.length];
            for (int j = 0; j < row.length; j++) {
                outputArray[i][j] = Boolean.parseBoolean(row[j]);
            }
        }

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                cells[y][x].setAlive(outputArray[x][y]);
            }
        }
    }

    private String getFilePath() {
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "Game of Life Saves (.gol)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String fileName = f.getName().toLowerCase();
                    return fileName.endsWith(".gol");
                }
            }
        });
        String path = null;
        if (fc.showOpenDialog(fc) == JFileChooser.APPROVE_OPTION)
            path = (fc.getSelectedFile().getAbsolutePath());
        return path;
    }

    private void adjustCellsSize(int amount) {
        if ((cells.length == minimumSize || cells[0].length == minimumSize) && amount < 0)
            return;

        Cell[][] newCells = new Cell[cells.length + amount][cells[0].length + amount];
        System.out.println(cells.length + " " + newCells.length);

        for (int i = 0; i < newCells.length; i++) {
            for (int j = 0; j < newCells[0].length; j++) {
                newCells[i][j] = new Cell(i, j, false);
            }
        }

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                try {
                    newCells[i][j] = cells[i][j].clone();
                } catch (Exception ignored) {
                }
            }
        }

        cells = newCells;
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("update"))
            update();
        if (cmd.equals("new"))
            generateCells();
        if (cmd.equals("save"))
            save();
        if (cmd.equals("open"))
            open();
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
    public void mouseWheelMoved(MouseWheelEvent e) {
        adjustCellsSize(e.getWheelRotation() > 0 ? 1 : -1);
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