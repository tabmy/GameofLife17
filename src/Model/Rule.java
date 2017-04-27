package Model;


/**
 * Class {@code Rule} represents an abstraction of the various rules that can be implemented in Game Of Life. It
 * provides general functionality for the {@code nextGeneration()} methods of its subclasses, but does not specify the
 * actual rules that are going to be implemented.
 * */
public abstract class Rule {

    public abstract void nextGeneration(byte[][] gameBoard, byte[][] neighBoard);

    public abstract Integer cellToBorn(Integer cell);

    public abstract Integer cellSurvive(Integer cell);

    public abstract boolean nextGenCell(Integer cell);

}
