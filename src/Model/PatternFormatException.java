package Model;

/**
 * Thrown when there is an exception regarding the pattern files and pattern formats occuring.
 * @author Abelsen, Tommy
 * @author Petrovic, Branislav
 */

public class PatternFormatException extends Exception {

    /**
     * Constructs a <code>PatternFormatException</code> with the specified detail message.
     * @param message
     */
    public PatternFormatException(String message){
        super(message);
    }

   /* public PatternFormatException(){
        super();
    }
    */
}
