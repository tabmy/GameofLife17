import Model.ConwayRule;
import Model.StaticBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Tommy on 23.02.2017.
 */
public class conwayRuleTest {

    ConwayRule rule = new ConwayRule();

    @Test
    public void nextGenerationTest(){

        StaticBoard board = new StaticBoard();
        board.setBoard(new byte[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        board.countNeighbours();

        byte[][] nextGen = rule.nextGeneration(board.getGameBoard(), board.getNeighBoard());

        board.setBoard(nextGen);

        Assertions.assertEquals(board.toString(), "0000000000000000");


        board.setBoard(new byte[][]{
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}
        });
        board.countNeighbours();
        nextGen = rule.nextGeneration(board.getGameBoard(),board.getNeighBoard());
        board.setBoard(nextGen);

        Assertions.assertEquals(board.toString(), "0000011001100000");
    }
}
