package Model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class DynamicBoard extends Board{

    private ArrayList<ArrayList<AtomicInteger>>  gameBoard = new ArrayList<>();
    private Rule rule;
    private int height;
    private int width;

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

    //Todo Implement class correctly

    @Override
    public boolean getCellState(int x, int y) {
        return gameBoard.get(x).get(y).intValue() == 1;
    }

    @Override
    public void setCellState(int x, int y, boolean b) {
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
