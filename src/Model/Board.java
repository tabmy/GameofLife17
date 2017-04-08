package Model;


/**
 * Provides general variables and methods that are to be implemented in all kinds of boards to be used in Game Of Life.
 * This includes properties like rules that are going to be implemented in each game mode and the size of the cells
 * currently on the board.
 * */
public abstract class Board {

    /**
     * The size of the cells is initially set to 0.
     * */
    private double cellSize = 1;

    /**
     * The set of rules used for the game.
     * */
    private Rule rule;

    /**
     * Get the current size of the cells.
     *
     * @return
     *          Size of the cells
     * */
    public double getCellSize(){
        return cellSize;
    }

    /**
     * Set a new value for the size of the cells.
     *
     * @param x
     *          The new cell size to be set
     * */
    public void setCellSize(double x){
        cellSize = x;
    }

    /**
     * Specifies the format of subclass methods that return the current state of the specified cell.
     *
     * @param x
     *          x-position of the tested cell
     * @param y
     *          y-position of the tested cell     *
     * */
    public abstract int getCellState(int x, int y);

    /**
     * Sets the state of a cell to the value specified.
     *
     * @param x
     *          x-position of the cell
     * @param y
     *          y-position of the cell
     * @param b
     *          state that the cell will be set to (0 or 1)
     * */
    public abstract void setCellState(int x, int y, byte b);

    /**
     * Return height of subclass boards.
     * */
    public abstract int getHEIGHT();

    /**
     * Return width of subclass boards.
     * */
    public abstract int getWIDTH();

    /**
     * Method {@code nextGeneration()} specifies what pattern will be displayed at each pattern change in the specified
     * time interval. This pattern varies depending on the rules and board type, so it is made abstract here.
     * */
    public abstract void nextGeneration();

    /**
     * Counts how many neighbouring cells each cell has. In general, this method determines which cells will die and/or
     * come to life, depending on how many neighbours each cell has. This varies from rule to rule, hence the
     * abstraction.
     * */
    public abstract void countNeighbours();

    /**
     * Clears the game board from the screen in subclasses.
     * */
    public abstract void clear();
}
