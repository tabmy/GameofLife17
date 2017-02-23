package Model;

/**
 * Created by Tommy on 07.02.2017.
 */
public class ConwayRule extends Rule {

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
}
