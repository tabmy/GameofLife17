package Model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicBoard extends Board{

    private ArrayList<ArrayList<AtomicInteger>>  gameBoard = new ArrayList<>();
    private Rule rule;
    private int height;
    private int width;
    private final int MAXSIZE = 2000;

    public DynamicBoard(){
        for (int i = 0; i < 100; i++) {
            gameBoard.add(i, new ArrayList<>());
            for (int j = 0; j < 100 ; j++) {
                gameBoard.get(i).add(new AtomicInteger(0));
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
        return gameBoard.get(x).get(y).intValue() == 1;
    }

    @Override
    public void setCellState(int x, int y, boolean b) {

        if (x >= MAXSIZE || y >= MAXSIZE) return;
        else if (x > (width - 10) || y > (height - 10)){
            expand(x, y);
        }

       gameBoard.get(x).set(y, b ? new AtomicInteger(1 ): new
               AtomicInteger(0));
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

    }

    @Override
    public void countNeighbours() {

    }

    @Override
    public void clear() {

    }

    @Override
    public String toString(){

        return null;
    }

    private void expand(int x, int y){
        if (x + 10 > width){
            int expandWidth = (x + 100) < MAXSIZE ? (x + 100) : MAXSIZE;
            int i = gameBoard.size();
            for (; i < expandWidth ; i++) {
                gameBoard.add(new ArrayList<>());
                for (int j = gameBoard.get(i).size(); j <= height; j++) {
                    gameBoard.get(i).add(new AtomicInteger(0));
                }
            }
            width = expandWidth;
        }
        if (y + 10 > height){
            int expandHeight = (y + 100) < MAXSIZE ? (y + 100) : MAXSIZE;
            for (int i = 0; i < width ; i++) {
                int j = gameBoard.get(i).size();
              /* while (gameBoard.get(i).size() < expandHeight) {// */for (; j <= expandHeight; j++) {
                    gameBoard.get(i).add(new AtomicInteger(0));
                }
            }
            height = expandHeight;
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
}
