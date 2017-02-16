package Model;

/**
 * Created by Tommy on 07.02.2017.
 */
public class StaticBoard extends Board {

    private byte [][] gameBoard;
    private Rule rule;
    private final int WIDTH;
    private final int HEIGHT;


    private byte[][] testBoard = {
            {1,0,0,1},
            {0,1,1,0},
            {0,1,1,0},
            {1,0,0,1}
    };


    public StaticBoard() {
        WIDTH = HEIGHT = 4;

        gameBoard = testBoard;

        rule = new ConwayRule();
    }

    @Override
    public int getWIDTH(){
        return WIDTH;
    }

    @Override
    public int getHEIGHT(){
        return HEIGHT;
    }

    @Override
    public void nextGeneration(){
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                gameBoard[i][j] = (byte)Math.abs(gameBoard[i][j] -1);
            }
        }
    }

    @Override
    public int getCellState(int x, int y){
        return gameBoard[x][y];
    }

    @Override
    public void setCellState(int x, int y, byte b) {
        gameBoard[x][y] = b;
    }


    @Override
    public void countNeighbours(){
        byte [][] neighBoard = new byte[WIDTH][HEIGHT];

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT ; j++) {
                if(gameBoard[i][j] == 1){
                    if (i > 0){neighBoard[i-1][j] += 1;}
                    if (j > 0){neighBoard[i][j-1] += 1;}
                    if (i > 0 && j > 0){neighBoard[i-1][j-1] += 1;}
                    if (i < WIDTH - 1){neighBoard[i+1][j] += 1;}
                    if (j < HEIGHT - 1){neighBoard[i][j+1] += 1;}
                    if (i < WIDTH - 1 && j < HEIGHT - 1){neighBoard[i+1][j+1] += 1;}
                    if (i > 0 && j < HEIGHT - 1){neighBoard[i-1][j+1] += 1;}
                    if (i < WIDTH - 1 && j > 0){neighBoard[i+1][j-1] += 1;}
                }
            }
        }
    }
}
