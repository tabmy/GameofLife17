package Model;


/**
 * This class defines Conway's rules for Game Of Life. It is one of several rules that can be implemented in the
 * application.
 *
 * @see Model.Rule
 * */
public class ConwayRule extends Rule {

    /**
     * The {@code nextGeneration()} method of this class implements Conway's rules for the game. Every rule combines
     * two {@code byte} arrays in different ways.
     *
     * @param gameBoard
     *          The board currently in use by the game
     * @param neighBoard
     *          The board containing the amount of neighbouring cells of each cell in {@code gameBoard}
     * */
    @Override
    public void nextGeneration(byte[][] gameBoard, byte[][] neighBoard) {

        // iterates through the gameBoard
        for (int i = 0; i < gameBoard.length - 1; i++) {
            for (int j = 0; j < gameBoard[0].length - 1; j++) {

                // cell comes to life if it has 3 living neighbours
                if (neighBoard[i][j] == 3) {
                    gameBoard[i][j] = 1;
                }
                // cell stays alive if it has 2 living neighbours
                else if (gameBoard[i][j] == 1 && neighBoard[i][j] == 2) {
                    gameBoard[i][j] = 1;
                }
                // cell dies if none of the above are satisfied
                else gameBoard[i][j] = 0;
            }
        }
    }

    /**
     * Determines the fate of a particular cell according to Conway's rules.
     *
     * @param cell
     *      The cell in question. Type {@code Integer} due to concurrency implementation.
     */
    @Override
    public boolean nextGenCell(Integer cell){
        // check if cell is alive and act accordingly
        if (cell % 2 == 1){
            cell--;
            if (cell == 20 || cell == 30){
                return true;
            }
        }else if (cell == 30){
            return true;
        }

        return false;
    }

}


