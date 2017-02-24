package Model;

/**
 * Created by Toma on 07.02.2017.
 */
public class StaticBoard extends Board {

    private byte[][] gameBoard;
    private byte[][] neighBoard;
    private ConwayRule rule;
    private final int WIDTH;
    private final int HEIGHT;


    private byte[][] testBoard = {
            {1, 0, 0, 1},
            {0, 1, 1, 0},
            {0, 1, 1, 0},
            {1, 0, 0, 1}
    };

    public StaticBoard() {
        WIDTH = HEIGHT = 4;
        gameBoard = testBoard;
        rule = new ConwayRule();
    }

    public StaticBoard(int w, int h){
        WIDTH = w;
        HEIGHT = h;

        gameBoard = new byte[w][h];
        rule = new ConwayRule();
    }

    @Override
    public int getWIDTH() {
        return WIDTH;
    }

    @Override
    public int getHEIGHT() {
        return HEIGHT;
    }

    @Override
    public void nextGeneration() {
        countNeighbours();
        rule.nextGeneration(gameBoard,neighBoard);
        //gameBoard = rule.nextGeneration(gameBoard, neighBoard);
    }

    @Override
    public int getCellState(int x, int y) {
        return gameBoard[x][y];
    }

    @Override
    public void setCellState(int x, int y, byte b) {
        gameBoard[x][y] = b;
    }

    @Override
    public void countNeighbours() {
        neighBoard = new byte[WIDTH][HEIGHT];

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
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

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                stringBuilder.append(gameBoard[i][j]).toString();
            }
        }
        return stringBuilder.toString();
    }

    public void clear(){
        gameBoard = new byte[WIDTH][HEIGHT];
    }

    // For testing purposes:

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

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                stringBuilder.append(neighBoard[i][j]).toString();
            }
        }
        return stringBuilder.toString();
    }

}
