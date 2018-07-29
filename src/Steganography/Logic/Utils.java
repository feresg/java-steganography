package Steganography.Logic;

import java.io.File;
import java.util.List;

/**
 * The final {@code Utils} class contains methods used by other classes in the project to reduce redundancy.
 */
public final class Utils {

    /**
     * Returns the extension of a given file.
     *
     * @param file file to extract extension from
     * @return     the extension of the given file.
     */
    public static String getFileExtension(File file) {
        String filename = file.getName();
        return filename.substring(filename.lastIndexOf(".")+1);
    }

    /**
     * Converts a list of bytes to a byte array with the appropriate size.
     *
     * @param in list of bytes to convert
     * @return   the byte array after conversion.
     */
    public static byte[] toByteArray(List<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++)
            ret[i] = in.get(i);
        return ret;
    }

}
