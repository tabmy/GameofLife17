package Model;

/**
 * Created by Tommy on 07.02.2017.
 */
public class StaticBoard extends Board {

    private byte [][] gameBoard;
    private final int WIDTH;
    private final int HEIGHT;

    private byte[][] testBoard = {{1,0,0,1},{0,1,1,0},{0,1,1,0}, {1,0,0,1}};


    public StaticBoard() {
        WIDTH = HEIGHT = 4;

        gameBoard = testBoard;
    }

    /*public StaticBoard(){
        WIDTH = 200;
        HEIGHT = 200;

        gameBoard = new byte[WIDTH][HEIGHT];
    }*/

    @Override
    public int getWIDTH(){
        return WIDTH;
    }

    @Override
    public int getHEIGHT(){
        return HEIGHT;
    }

    @Override
    public int getCellState(int x, int y){
        return gameBoard[x][y];
    }

    @Override
    public void setCellState(int x, int y, byte b) {
        gameBoard[x][y] = b;
    }

}
