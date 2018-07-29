package Steganography.Types;

/**
 * Format of the data that is hidden inside the steganographic image.
 */
public enum DataFormat {
    /** Embedded message byte array in image. */
    MESSAGE,
    /** Embedded file document in image. */
    DOCUMENT,
    /** Embedded image in image.
     * @see Steganography.Logic.ImageInImageSteganography */
    IMAGE}
