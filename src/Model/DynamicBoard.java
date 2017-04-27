package Model;

import java.util.ArrayList;

public class DynamicBoard extends Board{

    private ArrayList<ArrayList<Integer>> gameBoard = new ArrayList<>();
    private Rule rule;
    private int height;
    private int width;
    private final int MAXSIZE = 2000;

    public DynamicBoard(){
        for (int i = 0; i < 100; i++) {
            gameBoard.add(i, new ArrayList<>());
            for (int j = 0; j < 100 ; j++) {
                gameBoard.get(i).add(0);
            }
        }

        rule = new ConwayRule();
        height = gameBoard.size();
        width = gameBoard.get(0).size();

    }

    @Override
    public boolean getCellState(int x, int y) {

        if (x >= MAXSIZE || y >= MAXSIZE) return false;
        if (x > width || y > height){
          return false;
        }
        return gameBoard.get(x).get(y) == 1;
    }

    @Override
    public void setCellState(int x, int y, boolean b) {

        if (x >= MAXSIZE || y >= MAXSIZE) return;
        else if ((x > (width - 10) && b) || (y > (height - 10) && b)){
            expand(x, y);
        }

       gameBoard.get(x).set(y, b ? new Integer(1 ): new
               Integer(0));
    }

    @Override
    public int getHEIGHT() {
        return height;
    }

    @Override
    public int getWIDTH() {
        return width;
    }

    @Override
    public void nextGeneration() {
        countNeighbours();
        for (int i = 0; i < width ; i++) {
            for (int j = 0; j < height ; j++) {
                setCellState(i, j, rule.nextGenCell(gameBoard.get(i).get(j)));
            }
        }

        for (int i = 0; i < gameBoard.size(); i++) {
            if (gameBoard.get(i).get(0) == 1){
                expandNegative(0 , -100);
                break;
            }
        }
        for (int j = 0; j < gameBoard.get(0).size(); j++) {
            if (gameBoard.get(0).get(j) == 1){
                expandNegative(-100, 0);
                break;
            }
        }

    }

    @Override
    public void countNeighbours() {
        // iterate through the board dimensions
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // increasing the count of each neighbour around a living cell.
                // the numbers indicated by each cell after this method is run through the nextGeneration.
                if (gameBoard.get(i).get(j) % 10 == 1) {
                    if (i > 0) {
                        gameBoard.get(i-1).set(j, gameBoard.get(i-1).get(j) + 10);
                    }
                    if (j > 0) {
                       gameBoard.get(i).set(j - 1, gameBoard.get(i).get(j - 1) + 10);
                    }
                    if (i > 0 && j > 0) {
                       gameBoard.get(i-1).set(j - 1, gameBoard.get(i - 1).get(j - 1) + 10);
                    }
                    if (i < width - 1) {
                       gameBoard.get(i + 1).set(j, gameBoard.get(i + 1).get(j) + 10);
                    }
                    if (j < height - 1) {
                        gameBoard.get(i).set(j + 1, gameBoard.get(i).get(j + 1) + 10);
                    }
                    if (i < width - 1 && j < height - 1) {
                        gameBoard.get(i + 1).set(j + 1, gameBoard.get(i + 1).get(j + 1) + 10);
                    }
                    if (i > 0 && j < height - 1) {
                       gameBoard.get(i - 1).set(j + 1, gameBoard.get(i - 1).get(j + 1) + 10);
                    }
                    if (i < width - 1 && j > 0) {
                       gameBoard.get(i + 1).set(j - 1, gameBoard.get(i + 1).get(j - 1) + 10);
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        gameBoard = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            gameBoard.add(i, new ArrayList<>());
            for (int j = 0; j < 100 ; j++) {
                gameBoard.get(i).add(0);
            }
        }
        height = gameBoard.size();
        width = gameBoard.get(0).size();
    }

    @Override
    public String toString(){
        return null;
    }

    public void expand(int x, int y){
        if (x + 10 > width){
            int expandWidth = (x + 100) < MAXSIZE ? (x + 100) : MAXSIZE;
            int i = gameBoard.size();
            for (; i < expandWidth ; i++) {
                gameBoard.add(new ArrayList<>());
                for (int j = gameBoard.get(i).size(); j <= height; j++) {
                    gameBoard.get(i).add(0);
                }
            }
            width = expandWidth;
        }
        if (y + 10 > height){
            int expandHeight = (y + 100) < MAXSIZE ? (y + 100) : MAXSIZE;
            for (int i = 0; i < width ; i++) {
                int j = gameBoard.get(i).size();
                for (; j <= expandHeight; j++) {
                    gameBoard.get(i).add(0);
                }
            }
            height = expandHeight;
        }
    }

    public void expandNegative(int x, int y){
        if (width - x > MAXSIZE || height - y > MAXSIZE) return;
        if (x < 0){
            int expandWidth = -x;
            for (int i = 0; i < expandWidth ; i++) {
                gameBoard.add(0, new ArrayList<>());
                for (int j = 0; j < height ; j++) {
                    gameBoard.get(0).add(0);
                }
            }
            width += expandWidth;
        }
        if (y < 0){
            int expandHeight = -y;
            for (int i = 0; i < width ; i++) {
                for (int j = 0; j < expandHeight ; j++) {
                    gameBoard.get(i).add(0,0);
                }
            }
            height += expandHeight;
        }
    }

    public String toStringBoard(){

        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < gameBoard.size() ; i++) {
            for (int j = 0; j < gameBoard.get(i).size() ; j++) {
                stringBuffer.append(gameBoard.get(i).get(j));
            }
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    public int getMAXSIZE(){
        return MAXSIZE;
    }
}
