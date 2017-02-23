package Model;

/**
 * Created by Tommy on 07.02.2017.
 */
public class ConwayRule extends Rule {

    public byte[][] nextGeneration(byte[][] gameBoard, byte[][] neighBoard) {
        for (int i = 1; i < gameBoard.length - 1; i++) {
            for (int j = 1; j < gameBoard[0].length - 1; j++) {

                if (gameBoard[i][j] == 1) {
                    if (neighBoard[i][j] < 2)
                        gameBoard[i][j]--;

                    if (neighBoard[i][j] > 3)
                        gameBoard[i][j]--;

                    if ((neighBoard[i][j] == 2) || (neighBoard[i][j] == 3))
                        gameBoard[i][j] = gameBoard[i][j];
                }
                else if (gameBoard[i][j] == 0) {
                    if (neighBoard[i][j] == 3)
                        gameBoard[i][j]++;
                }
            }
        }

        return gameBoard;
    }
}
