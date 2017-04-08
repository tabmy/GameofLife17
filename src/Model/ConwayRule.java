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
}


/*          Older version
 public byte[][] nextGeneration(byte[][] gameBoard, byte[][] neighBoard) {
        byte[][] nextGen = new byte[gameBoard.length][gameBoard[0].length];
        for (int i = 0; i < gameBoard.length - 1; i++) {
            for (int j = 0; j < gameBoard[0].length - 1; j++) {

                if (gameBoard[i][j] == 1) {
                    if (neighBoard[i][j] == 2 || neighBoard[i][j] == 3){
                        nextGen[i][j] = 1;
                    }
                }
                else if (gameBoard[i][j] == 0) {
                    if (neighBoard[i][j] == 3)
                        nextGen[i][j] = 1;
                }
            }
        }

        return nextGen;
    }
 */