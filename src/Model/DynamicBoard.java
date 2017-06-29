package Model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A dynamic board class used for dynamically altering the size of the board used in the Game of Life application.
 * This class contains all methods used in the Game of Life board representation, including dynamically resizing the
 * board, counting neighbours and changing the board values to represent the next generation.
 * @author Abelsen, Tommy
 * @author Petrovic, Branislav
 */
public class DynamicBoard extends Board {

    private ArrayList<ArrayList<AtomicInteger>> gameBoard = new ArrayList<>();
    private Rule rule;
    private int height;
    private int width;
    private int initSize = 100;
    private final int MAXSIZE = 2000;
    private ConcurrentSim simulator = new ConcurrentSim();
    private final int THREADNUM = Runtime.getRuntime().availableProcessors();

    /**
     * Default constructor using a predefined initial size.
     */
    public DynamicBoard() {
        for (int i = 0; i < initSize; i++) {
            gameBoard.add(i, new ArrayList<>());
            for (int j = 0; j < initSize; j++) {
                gameBoard.get(i).add(new AtomicInteger(0));
            }
        }

        rule = new ConwayRule();
        height = gameBoard.size();
        width = gameBoard.get(0).size();
    }

    /**
     * Returns the cell state of the cell in position (x,y) if inside the board.
     * @param x
     *          x-position of the tested cell
     * @param y
     *          y-position of the tested cell
     * @return Returns the boolean value of if the cell is within the boards borders and if the cell is alive.
     */
    @Override
    public boolean getCellState(int x, int y) {
        return !(x >= MAXSIZE || y >= MAXSIZE) && !(x >= width || y >= height) && gameBoard.get(x).get(y).intValue() == 1;
    }

    /**
     *  Sets the cell in position (x,y) to 1 or 0 depending on the boolean b.
     *  If the (x,y) position is not in the boards borders, the board will expand to the size of (x,y)
     * @param x
     *          x-position of the cell
     * @param y
     *          y-position of the cell
     * @param b
     *          state that the cell will be set to; false or true (0 or 1)
     */
    @Override
    public void setCellState(int x, int y, boolean b) {
        if (x >= MAXSIZE || y >= MAXSIZE) return;
        else if ((x > (width - 2) && b) || (y > (height - 2) && b)) {
            expand(x, y);
        }
        gameBoard.get(x).set(y, b ? new AtomicInteger(1) : new AtomicInteger(0));
    }

    /**
     * Returns the height of the board.
     * @return The length of the <code>ArrayLists</code> in gameBoard
     */
    @Override
    public int getHEIGHT() {
        return height;
    }

    /**
     * Returns the width of the board.
     * @return The length of the gameBoard <code>ArrayLists</code>.
     */
    @Override
    public int getWIDTH() {
        return width;
    }

    /**
     * Method to calculate the next generation of the current cells in the board.
     * @deprecated
     */
    @Deprecated
    @Override
    public void nextGeneration() {
        throw new UnsupportedOperationException("Please use the concurrent methods");
        /* PRE AtomicInteger
        //countNeighbours();
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
        */
    }

    /**
     * Method to count the living neighbours of the cells in the board.
     * @deprecated
     */
    @Deprecated
    @Override
    public void countNeighbours() {
        throw new UnsupportedOperationException("Please use the concurrent methods");
    /* PRE AtomicInteger
        // iterate through the board dimensions
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // increasing the count of each neighbour around a living cell.
                // the numbers indicated by each cell after this method is run through the nextGeneration.
                if (gameBoard.get(i).get(j) % 10 == 1) {
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
        } */
    }


    /**
     * Method for counting neighbours using threads.
     * @param threadNum The number in the loop-iteration of the countNeighboursConcurrent() method.
     */
    private void countNeighboursConcurrent(int threadNum) {
        int threadWidth = gameBoard.size() / THREADNUM;
        int start = threadNum * threadWidth;
        int end = (threadNum + 1) * threadWidth;

        simulator.addThreadTask(() -> countNeighConcurrentPartial(start, end));
    }

    /**
     *  Method to count the neighbours of the different parts of the gameBoard. Used to allow multiple threads
     *  working on the same board.
     * @param start The start value of the loop.
     * @param end The end value of the loop.
     */
    private void countNeighConcurrentPartial(int start, int end) {
        if (end > width) {
            end = width;
        }
        for (int i = start; i < end; i++) {
            for (int j = 0; j < height; j++) {
                if (gameBoard.get(i).get(j).intValue() % 10 == 1) {
                    if (i > 0) {
                        gameBoard.get(i - 1).set(j,
                                new AtomicInteger(gameBoard.get(i - 1).get(j).intValue() + 10));
                    }
                    if (j > 0) {
                        gameBoard.get(i).set(j - 1,
                                new AtomicInteger(gameBoard.get(i).get(j - 1).intValue() + 10));
                    }
                    if (i > 0 && j > 0) {
                        gameBoard.get(i - 1).set(j - 1,
                                new AtomicInteger(gameBoard.get(i - 1).get(j - 1).intValue() + 10));
                    }
                    if (i < width - 1) {
                        gameBoard.get(i + 1).set(j,
                                new AtomicInteger(gameBoard.get(i + 1).get(j).intValue() + 10));
                    }
                    if (j < height - 1) {
                        gameBoard.get(i).set(j + 1,
                                new AtomicInteger(gameBoard.get(i).get(j + 1).intValue() + 10));
                    }
                    if (i < width - 1 && j < height - 1) {
                        gameBoard.get(i + 1).set(j + 1,
                                new AtomicInteger(gameBoard.get(i + 1).get(j + 1).intValue() + 10));
                    }
                    if (i > 0 && j < height - 1) {
                        gameBoard.get(i - 1).set(j + 1,
                                new AtomicInteger(gameBoard.get(i - 1).get(j + 1).intValue() + 10));
                    }
                    if (i < width - 1 && j > 0) {
                        gameBoard.get(i + 1).set(j - 1,
                                new AtomicInteger(gameBoard.get(i + 1).get(j - 1).intValue() + 10));
                    }
                }
            }
        }
    }

    /**
     * Runs the current board through the countNeighbours and nextGeneration methods to represent the next generation
     * of the board according to the given rules.
     * Expands the board in negative x- and y- direction if any cells are close to the x = 0 or y = 0 borders.
     */
    public void nextGenerationConcurrent() {

        for (int i = 0; i < THREADNUM; i += 2) {
            countNeighboursConcurrent(i);
        }
        simulator.doWork();
        for (int i = 1; i < THREADNUM; i += 2) {
            countNeighboursConcurrent(i);
        }
        simulator.doWork();
        countNeighboursConcurrent(THREADNUM);
        simulator.doWork();


        for (int i = 0; i < THREADNUM; i += 2) {
            nextGenerationConcurrent(i);
        }
        simulator.doWork();
        for (int i = 1; i < THREADNUM; i += 2) {
            nextGenerationConcurrent(i);
        }
        nextGenerationConcurrent(THREADNUM);

        simulator.doWork();
        simulator.addThreadTask(() -> {
            for (ArrayList<AtomicInteger> arr : gameBoard) {
                if (arr.get(0).intValue() == 1) {
                    expandNegative(0, -1);
                    break;
                }
            }

            for (int j = 0; j < gameBoard.get(0).size(); j++) {
                if (gameBoard.get(0).get(j).intValue() == 1) {
                    expandNegative(-1, 0);
                    break;
                }
            }
        });
        simulator.doWork();
    }


    /**
     * Method for calculating the next generation of the current board using threads.
     * @param threadNum The number in the loop-iteration of the nextGenerationConcurrent() method.
     */
    private void nextGenerationConcurrent(int threadNum) {
        int threadWidth = (gameBoard.size() / THREADNUM);
        int start = threadNum * threadWidth;
        int end = ((threadNum + 1) * threadWidth) < width ? (threadNum + 1) * threadWidth : width;

        simulator.addThreadTask(() -> nextGenerationConcurrentPartial(start, end));
    }

    /**
     *  Method to calculate the next generation of the different parts of the gameBoard. Used to allow multiple threads
     *  working on the same board.
     * @param start The start value of the loop.
     * @param end The end value of the loop.
     */
    private void nextGenerationConcurrentPartial(int start, int end) {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < height; j++) {
                setCellState(i, j, rule.nextGenCell(gameBoard.get(i).get(j).intValue()));
            }
        }
    }

    /**
     * Resets the gameBoard to a new empty board of the predefined size.
     */
    @Override
    public void clear() {
        gameBoard = new ArrayList<>();
        for (int i = 0; i < initSize; i++) {
            gameBoard.add(i, new ArrayList<>());
            for (int j = 0; j < initSize; j++) {
                gameBoard.get(i).add(new AtomicInteger(0));
            }
        }
        height = gameBoard.size();
        width = gameBoard.get(0).size();
    }

    /**
     * ToString method to return the cells in the board in a String format.
     * @return String of the cells in the board.
     */
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

    /**
     * Expands the board to the given size, to a maximum of a predefined size.
     * @param x The value of the preferred expansion in x/width direction.
     * @param y The value of the preferred expansion in y/height direction.
     */
    public void expand(int x, int y) {
        if (x + 2 > width) {
            int expandWidth = (x + 2) < MAXSIZE ? (x + 2) : MAXSIZE;
            int i = gameBoard.size();
            for (; i < expandWidth; i++) {
                gameBoard.add(new ArrayList<>());
                for (int j = gameBoard.get(i).size(); j <= height; j++) {
                    gameBoard.get(i).add(new AtomicInteger(0));
                }
            }
            width = expandWidth;
        }
        if (y + 2 > height) {
            int expandHeight = (y + 2) < MAXSIZE ? (y + 2) : MAXSIZE;
            for (int i = 0; i < width; i++) {
                int j = gameBoard.get(i).size();
                for (; j <= expandHeight; j++) {
                    gameBoard.get(i).add(new AtomicInteger(0));
                }
            }
            height = expandHeight;
        }
    }

    /**
     * Expands the board to the given size, to a maximum of a predefined size.
     * @param x The value of the preferred expansion in negative x/width direction.
     * @param y The value of the preferred expansion in negative y/height direction.
     */
    private void expandNegative(int x, int y) {
        if (width - x > MAXSIZE || height - y > MAXSIZE) return;
        if (x < 0) {
            int expandWidth = -x;
            for (int i = 0; i < expandWidth; i++) {
                gameBoard.add(0, new ArrayList<>());
                for (int j = 0; j < height; j++) {
                    gameBoard.get(0).add(new AtomicInteger(0));
                }
            }
            width += expandWidth;
        }
        if (y < 0) {
            int expandHeight = -y;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < expandHeight; j++) {
                    gameBoard.get(i).add(0, new AtomicInteger(0));
                }
            }
            height += expandHeight;
        }
    }

    /**
     * Returns the predefined maximum size of the gameBoard board.
     * @return Value of the maximum number of cells in x and y direction.
     */
    public int getMAXSIZE() {
        return MAXSIZE;
    }


    //      ---     For testing purposes    ---     //
    public DynamicBoard(int x, int y) {

        gameBoard = new ArrayList<>(x);

        for (int i = 0; i < x; i++) {
            gameBoard.add(new ArrayList<>(y));
            for (int j = 0; j < y; j++) {
                gameBoard.get(i).add(j, new AtomicInteger(0));
            }
        }
        width = gameBoard.size();
        height = gameBoard.get(0).size();
    }

    public void printPerformance() {
        long start = System.currentTimeMillis();
        nextGenerationConcurrent();
        System.out.println("NextGeneration took " + (System.currentTimeMillis() - start) + "ms");
    }

    public void setCellNoExpand(int x, int y, boolean b) {
        if (x < 0 || x > width || y < 0 || y > height) return;
        gameBoard.get(x).set(y, b ? new AtomicInteger(1) : new AtomicInteger(0));
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
