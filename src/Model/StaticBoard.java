package Model;

/**
 * Created by Tommy on 07.02.2017.
 */
public class StaticBoard extends Board {

    private byte [][] gameBoard;
    private final int WIDTH;
    private final int HEIGHT;

    public StaticBoard(){
        WIDTH = 200;
        HEIGHT = 200;

        gameBoard = new byte[WIDTH][HEIGHT];
    }

    public int getWIDTH(){
        return WIDTH;
    }

    public int getHEIGHT(){
        return HEIGHT;
    }

    @Override
    public int getCellState(int x, int y){
        return gameBoard[x][y];
    }

}
