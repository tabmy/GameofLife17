package Model;

import java.util.ArrayList;

public class DynamicBoard extends Board {

    private ArrayList<ArrayList<Integer>> gameBoard = new ArrayList<>();
    private Rule rule;
    private int height;
    private int width;
    private final int MAXSIZE = 3000;

    public DynamicBoard() {
        for (int i = 0; i < 100; i++) {
            gameBoard.add(i, new ArrayList<>());
            for (int j = 0; j < 100; j++) {
                gameBoard.get(i).add(0);
            }
        }

        rule = new ConwayRule();
        height = gameBoard.size();
        width = gameBoard.get(0).size();
    }

    @Override
    public boolean getCellState(int x, int y) {
        return !(x >= MAXSIZE || y >= MAXSIZE) && !(x >= width || y >= height) && gameBoard.get(x).get(y) == 1;
    }

    @Override
    public void setCellState(int x, int y, boolean b) {
        if (x >= MAXSIZE || y >= MAXSIZE) return;
        else if ((x > (width - 2) && b) || (y > (height - 2) && b)) {
            expand(x, y);
        }
        gameBoard.get(x).set(y, b ? new Integer(1) : new Integer(0));
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
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                setCellState(i, j, rule.nextGenCell(gameBoard.get(i).get(j)));
            }
        }

        for (ArrayList<Integer> arr : gameBoard) {
            if (arr.get(0) == 1) {
                expandNegative(0, -1);
                break;
            }
        }

        for (int j = 0; j < gameBoard.get(0).size(); j++) {
            if (gameBoard.get(0).get(j) == 1) {
                expandNegative(-1, 0);
                break;
            }
        }
    }

    @Override
    public void countNeighbours() {

        boolean noNeighbours = true;

        // iterate through the board dimensions
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // increasing the count of each neighbour around a living cell.
                // the numbers indicated by each cell after this method is run through the nextGeneration.
                if (gameBoard.get(i).get(j) % 10 == 1) {
                    noNeighbours = false;
                    if (i > 0) {
                        gameBoard.get(i - 1).set(j, gameBoard.get(i - 1).get(j) + 10);
                    }
                    if (j > 0) {
                        gameBoard.get(i).set(j - 1, gameBoard.get(i).get(j - 1) + 10);
                    }
                    if (i > 0 && j > 0) {
                        gameBoard.get(i - 1).set(j - 1, gameBoard.get(i - 1).get(j - 1) + 10);
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
        if (noNeighbours) clear();
    }

    @Override
    public void clear() {
        gameBoard = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            gameBoard.add(i, new ArrayList<>());
            for (int j = 0; j < 100; j++) {
                gameBoard.get(i).add(0);
            }
        }
        height = gameBoard.size();
        width = gameBoard.get(0).size();
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                stringBuilder.append(gameBoard.get(i).get(j));
            }
        }

        return stringBuilder.toString();
    }

    public void expand(int x, int y) {
        if (x + 2 > width) {
            int expandWidth = (x + 2) < MAXSIZE ? (x + 2) : MAXSIZE;
            int i = gameBoard.size();
            for (; i < expandWidth; i++) {
                gameBoard.add(new ArrayList<>());
                for (int j = gameBoard.get(i).size(); j <= height; j++) {
                    gameBoard.get(i).add(0);
                }
            }
            width = expandWidth;
        }
        if (y + 2 > height) {
            int expandHeight = (y + 2) < MAXSIZE ? (y + 2) : MAXSIZE;
            for (int i = 0; i < width; i++) {
                int j = gameBoard.get(i).size();
                for (; j <= expandHeight; j++) {
                    gameBoard.get(i).add(0);
                }
            }
            height = expandHeight;
        }
    }

    private void expandNegative(int x, int y) {
        if (width - x > MAXSIZE || height - y > MAXSIZE) return;
        if (x < 0) {
            int expandWidth = -x;
            for (int i = 0; i < expandWidth; i++) {
                gameBoard.add(0, new ArrayList<>());
                for (int j = 0; j < height; j++) {
                    gameBoard.get(0).add(0);
                }
            }
            width += expandWidth;
        }
        if (y < 0) {
            int expandHeight = -y;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < expandHeight; j++) {
                    gameBoard.get(i).add(0, 0);
                }
            }
            height += expandHeight;
        }
    }

    public int getMAXSIZE() {
        return MAXSIZE;
    }

    // --- For testing purposes --- //
    public DynamicBoard(int x, int y) {

        gameBoard = new ArrayList<>(x);

        for (int i = 0; i < x; i++) {
            gameBoard.add(new ArrayList<>(y));
            for (int j = 0; j < y; j++) {
                gameBoard.get(i).add(j, 0);
            }
        }
        width = gameBoard.size();
        height = gameBoard.get(0).size();
    }

    public void setCellNoExpand(int x, int y, boolean b) {
        if (x < 0 || x > width || y < 0 || y > height) return;
        gameBoard.get(x).set(y, b ? 1 : 0);
    }

    public String toStringBoard() {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                stringBuilder.append(gameBoard.get(i).get(j));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
