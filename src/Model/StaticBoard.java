package Model;

/**
 * This class defines a default static board for the game. It implements the specifications provided byt the Board
 * superclass by giving values and functionality to the variables and methods specified. It also implements a set of
 * rules that is going to be used during the game.
 *
 * @see Model.Board
 * */
public class StaticBoard extends Board {

    /**
     * The board with the cells of the current generation.
     * */
    private byte[][] gameBoard;

    /**
     * The board with the neighbouring cells of a given cell in the current generation. Used to determine what the next
     * generation will be according to the specified rules set.
     * */
    private byte[][] neighBoard;

    /** //todo Edit
     * Conway's rules for the game.
     *
     * @see Model.ConwayRule
     * */
    private Rule rule;

    /**
     * Width of the game board.
     * */
    private final int WIDTH;

    /**
     * Height of the game board.
     * */
    private final int HEIGHT;

    private int genCount = 0;


    /**
     * Constructor of {@code StaticBoard}. The width and height of the board are specified by the method's
     * parameters, which become the dimensions of a new {@code gameBoard}. Conway's rules are implemented.
     *
     * @param w
     *          Width of the new {@code gameBoard}
     * @param h
     *          Height of the new {@code gameBoard}
     * */
    public StaticBoard(int h, int w){
        HEIGHT = h;
        WIDTH = w;

        gameBoard = new byte[w][h];
        rule = new ConwayRule();
    }

    /**
     * Gets the width of the {@code StaticBoard}.
     *
     * @return
     *          Width of the {@code StaticBoard}
     * */
    @Override
    public int getWIDTH() {
        return WIDTH;
    }

    /**
     * Gets the height of the {@code StaticBoard}.
     *
     * @return
     *          Height of the {@code StaticBoard}
     * */
    @Override
    public int getHEIGHT() {
        return HEIGHT;
    }

    /**
     * Counts the neighbours of each cell, then calls the {@code nextGeneration()} method from the {@code ConwayRule}
     * class. The result is a new generation specified by Conway's rules.
     *
     * @see #countNeighbours()
     * */
    @Override
    public void nextGeneration() {
        countNeighbours();
        rule.nextGeneration(gameBoard, neighBoard);

        genCount++;
    }

    /**
     * Return the current state of the specified cell.
     *
     * @param x
     *          x-position of the specified cell
     * @param y
     *          y-position of the specified cell
     * @return
     *          Integer value determined by the state of the specified cell
     * */
    @Override
    public boolean getCellState(int x, int y) {
        return gameBoard[x][y] == 1;
    }

    public int getGenCount() {
        return genCount;
    }

    public void setGenCount(int genCount) {
        this.genCount = genCount;
    }

    /**
     * Sets the state of the specified cell to the byte value specified by the parameter.
     *
     * @param x
     *          x-position of the cell to be set
     * @param y
     *          y-position of the cell to be set
     * @param b
     *          Value that gets assigned to the cell (0 for dead, 1 for alive)
     * */
    @Override
    public void setCellState(int x, int y, boolean b) {
        gameBoard[x][y] = (byte) (b ? 1 : 0);
    }

    /**
     * Counts the neighbours of a specified cell in the {@code gameBoard} and fills in the neighbouring cells into
     * {@code neighBoard}. The method iterates through the boards (which are the same size), then checks whether a
     * given cell is alive or dead. If it is alive, its neighbours are counted and {@code neighBoard} gets the
     * neighbouring cells.
     * */
    @Override
    public void countNeighbours() {
        // declare neighBoard with the same dimensions as gameBoard
        neighBoard = new byte[WIDTH][HEIGHT];

        // iterate through the board dimensions
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                // specify when to fill neighBoard, depending on the amount of neighbouring cells in gameBoard
                if (gameBoard[i][j] == 1) {
                    if (i > 0) {
                        neighBoard[i - 1][j]++;
                    }
                    if (j > 0) {
                        neighBoard[i][j - 1]++;
                    }
                    if (i > 0 && j > 0) {
                        neighBoard[i - 1][j - 1]++;
                    }
                    if (i < WIDTH - 1) {
                        neighBoard[i + 1][j]++;
                    }
                    if (j < HEIGHT - 1) {
                        neighBoard[i][j + 1]++;
                    }
                    if (i < WIDTH - 1 && j < HEIGHT - 1) {
                        neighBoard[i + 1][j + 1]++;
                    }
                    if (i > 0 && j < HEIGHT - 1) {
                        neighBoard[i - 1][j + 1]++;
                    }
                    if (i < WIDTH - 1 && j > 0) {
                        neighBoard[i + 1][j - 1]++;
                    }
                }
            }
        }
    }

    /**
     * Returns a string representation of the {@code gameBoard}, so that it can be printed to the screen. The board
     * is iterated through, and the cells are appended to a {@code StringBuilder}.
     *
     * @return
     *          String representation of the board
     * */
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        // iterate through board and append to StringBuilder
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH ; j++) {
                stringBuilder.append(gameBoard[j][i]);
            }
        }
        return stringBuilder.toString();
    }


    /**
     * Clears the {@code gameBoard} by assigning a new blank array with the same dimensions to it.
     * */
    public void clear(){

        for (int i = 0; i < WIDTH ; i ++) {
            for (int j = 0; j < HEIGHT ; j++) {
                gameBoard[i][j] = 0;
            }
        }
        gameBoard = new byte[WIDTH][HEIGHT];
        genCount = 0;
    }


    // ---- ---- For testing purposes: ---- ---- //

    //Todo Remove test-constructor
    public StaticBoard(byte[][] board){
        this.gameBoard = board;
        rule = new ConwayRule();
        WIDTH = board.length;
        HEIGHT = board[0].length;
    }

    public StaticBoard(){
        rule = new ConwayRule();
        HEIGHT = WIDTH = 4;
    }

    public String toStringBoard(){
        StringBuilder stringBuilder = new StringBuilder();

        // iterate through board and append to StringBuilder
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH ; j++) {
                stringBuilder.append(gameBoard[j][i] == 1 ? 1 : " ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void setBoard(byte[][] board){
        this.gameBoard = board;
    }

    public byte[][] getGameBoard(){
        return gameBoard;
    }
    public byte[][] getNeighBoard(){
        return neighBoard;
    }

    public String neighBoardString(){
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                stringBuilder.append(neighBoard[i][j]);
            }
        }
        return stringBuilder.toString();
    }

}
