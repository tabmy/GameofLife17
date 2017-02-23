package Model;

/**
 * Created by Tommy on 07.02.2017.
 */
public abstract class Board {

    private double cellSize = 0;

    private Rule rule;

    public double getCellSize(){
        return cellSize;
    }

    public void setCellSize(double x){
        cellSize = x;
    }

    public abstract int getCellState(int x, int y);

    public abstract void setCellState(int x, int y, byte b);

    public abstract int getHEIGHT();

    public abstract int getWIDTH();

    public abstract void nextGeneration();

    public abstract void countNeighbours();

    public abstract void clear();
}
