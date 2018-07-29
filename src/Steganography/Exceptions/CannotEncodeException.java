package Steganography.Exceptions;

/**
 * The {@code CannotEncodeException} exception is thrown when an error occurs when encoding an image,
 * mainly empty data or larger data than capacity.
 */
public class CannotEncodeException extends Exception{

    /**
     * Constructs a new <code>CannotDecodeException</code> with the specified error message.
     *
     * @param message the error message which can be retrieved with the <code>getMessage</code> method
     */
    public CannotEncodeException(String message) {super(message);}

    /**
     * Constructs a new <code>CannotDecodeException</code> with the specified error message and cause.
     *
     * @param message the error message which can be retrieved with the <code>getMessage</code> method
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public CannotEncodeException(String message, Throwable cause) {super(message, cause);}
}
