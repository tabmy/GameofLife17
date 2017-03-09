package Model;

/**
 * Created by Tommy on 07.02.2017.
 */

/**
 * Class {@code Rule} represents an abstraction of the various rules that can be implemented in Game Of Life. It
 * provides general functionality for the {@code nextGeneration()} methods of its subclasses, but does not specify the
 * actual rules that are going to be implemented.
 * */
public abstract class Rule {

    public abstract void nextGeneration(byte[][] gameBoard, byte[][] neighBoard);
}
