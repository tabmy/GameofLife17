package Model;


/**
 * This class provides general functionality for the {@code nextGeneration()} methods of its subclasses.
 * */
public abstract class Rule {

    /**
     * Determines the next generation of a pattern based on the rules that are implemented.
     *
     * @param gameBoard
     *      {@code byte} array that contains the cells in the current generation.
     * @param neighBoard
     *      Array with the neighbouring cells of each cell in {@code gameBoard}.
     */
    public abstract void nextGeneration(byte[][] gameBoard, byte[][] neighBoard);

    /**
     * Determines whether a specific cell under a rule should live or die.
     *
     * @param cell
     *      The cell whose fate is to be decided.
     * @return
     *      True if cell lives.
     *      False otherwise.
     */
    public abstract boolean nextGenCell(Integer cell);

}
