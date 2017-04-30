import Model.DynamicBoard;
import org.junit.Test;
// import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertEquals;


public class dynamicBoardTest {

    @Test
    public void constructorTest() {
        DynamicBoard board = new DynamicBoard(3, 3);
        assertEquals("000000000", board.toString());
    }

    @Test
    public void settCellStateTest(){
        DynamicBoard board = new DynamicBoard();

        board.setCellState(5,5, true);
        assertEquals(true, board.getCellState(5,5));
        board.setCellState(50, 55, true);
        assertEquals(true, board.getCellState(50,55));
        assertEquals(false, board.getCellState(10,10));
        assertEquals(false, board.getCellState(50,54));

    }


}
