public class Cell {
    private int x;
    private int y;
    private boolean alive;

    public Cell(int x, int y, boolean alive) {
        this.x = x;
        this.y = y;
        this.alive = alive;
    }

    public Cell update(int neighbours) {
        Cell clone = clone();
        if (!clone.alive && neighbours == 3)
            clone.setAlive(true);
        else if (clone.alive && (neighbours < 2 || neighbours > 3))
            clone.setAlive(false);
        return clone;
    }

    public Cell clone() {
        return new Cell(x, y, alive);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void flipAlive() {
        alive = !alive;
    }
}