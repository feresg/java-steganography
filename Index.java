/* Project: Steganography
   Description: Hide a text or an image inside an image.
   Authors: Ilyes Hamrouni, Feres Gaaloul.
   Usage: Temporarily, use terminal, "java Index".
   Help and Guide: see comments.
*/

/**
 * Notes:
 *  - When encoding, the first line of the image will contain the following
 *    - First 3 pixels will contain the size of the file
 *    - The next pixels will contain the extension of the file (used for getting back non plain text documents???)
 * `  - We still encode the two least significant bits
 *  - The secret message will be encoded from the pixel (1,0) , (1,1), (1,2), ..., (2,0)...
 * - Issues:
 *   - Only works on RGBA png files. issues with decoding jpg, bmp and gif (but no runtime error when encoding)
 *   (Converting RGB png files to rgba files (with alpha = 255) could help...)
 */



import java.io.File;  //to manipulate files
import java.io.IOException;
import java.awt.image.BufferedImage; // to extract rgb
import javax.imageio.ImageIO; //to read the image
import java.util.Scanner ; //



public class Index
{

  public static void main(String[] args) throws IOException{
    System.out.println("*** Steganography ***");
    Image image = new Image(); // this is the image that holds the message.
    image.choose();
  }
}
