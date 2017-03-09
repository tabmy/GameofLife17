import Model.StaticBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Tommy on 23.02.2017.
 */
public class staticBoardTest {

    /*
            {1, 0, 0, 1},
            {0, 1, 1, 0},
            {0, 1, 1, 0},
            {1, 0, 0, 1}
     */

    @Test
    public void toStringTest() {
        StaticBoard board = new StaticBoard();
        String string = board.toString();

        org.junit.jupiter.api.Assertions.assertEquals(string, "1001011001101001");
        board.setBoard(new byte[][]{
                {1, 1, 0, 0},
                {0, 0, 1, 1},
                {1, 0, 1, 0},
                {0, 1, 0, 1}
        });
        string = board.toString();
        Assertions.assertEquals(string, "1100001110100101");
    }

    @Test
    public void countNeighbourTest() {
        StaticBoard board = new StaticBoard();
        board.countNeighbours();
        String string = board.neighBoardString();
        Assertions.assertEquals(string, "1331344334431331");

        board.setBoard(new byte[][]{
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1}
        });

        board.countNeighbours();
        string = board.neighBoardString();
        Assertions.assertEquals(string, "3553588558853553");

        board.setBoard(new byte[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        board.countNeighbours();
        string = board.neighBoardString();
        Assertions.assertEquals(string, "0000000000000000");

    }
}
