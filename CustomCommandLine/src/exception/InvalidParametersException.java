package exception;

/**
 * Represents an error while parsing the user input parameters.
 * This is equivalent to an IOException, it will be thrown in the case of
 * invalid data being passed, E.G piping to EOL.
 */
public class InvalidParametersException extends Exception {

  /**
   * @param message the message to be shown when this exception is thrown
   */
  public InvalidParametersException(String message) {
    super(message);
  }
}
