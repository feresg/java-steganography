package Steganography.Logic;

import Steganography.Modals.AlertBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    /**
     * Hashes an image to return a password value
     * by adding up the diagonal pixels.
     *
     * @param image image used as key
     * @return      the key hashed from the image.
     */
    public static String hashImage(File image){
        try{
            BufferedImage img = ImageIO.read(image);
            long pwd = 0;
            for (int i=0; i < img.getWidth() && i < img.getHeight(); i++)
                pwd += img.getRGB(i,i);
            return String.valueOf(pwd);
        }catch (IOException e) {
            AlertBox.error("Error while handling the key image", e.getMessage());
            return null;
        }
    }

}
