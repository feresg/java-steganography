package Steganography.Exceptions;

/**
 * The {@code UnsupportedImageTypeException} exception is thrown when the cover image is unsupported by the encoding process.
 * e.g. : grayscale images, 16bit images...
 */
public class UnsupportedImageTypeException extends Exception{

    /**
     * Constructs a new <code>CannotDecodeException</code> with the specified error message.
     *
     * @param message the error message which can be retrieved with the <code>getMessage</code> method
     */
    public UnsupportedImageTypeException(String message) {super(message);}

    /**
     * Constructs a new <code>CannotDecodeException</code> with the specified error message and cause.
     *
     * @param message the error message which can be retrieved with the <code>getMessage</code> method
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public UnsupportedImageTypeException(String message, Throwable cause) {super(message, cause);}
}
