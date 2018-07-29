package Steganography.Types;

/**
 * Sets the behaviour of the PasswordPrompt based on the type of the operation.
 *
 * @see Steganography.Modals.PasswordPrompt
 */
public enum PasswordType {
    /** Use the Encryption mode (password validation and confirmation box). */
    ENCRYPT,
    /** Use the Decryption mode. */
    DECRYPT}
