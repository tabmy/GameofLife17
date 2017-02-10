package Model;

/**
 * Created by Tommy on 07.02.2017.
 */
public abstract class Board {

    private int cellSize = 5;

    public int getCellSize(){
        return cellSize;
    }

    public void setCellSize(int x){
        cellSize = x;
    }

    public abstract int getCellState(int x, int y);

    public abstract void setCellState(int x, int y, byte b);

    public abstract int getHEIGHT();

    public abstract int getWIDTH();

    public abstract void nextGeneration();



}
