package Steganography.Logic;

import Steganography.Exceptions.CannotDecodeException;
import Steganography.Exceptions.CannotEncodeException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code ImageSteganography} class handles the steganographic process for 24-bit, RGB Bitmap images using the Least Significant Bit (LSB) method.
 * <p>Image formats supported for encoding: <code>png, bmp, jpg</code>.
 * <p>Image formats supported for decoding: <code>png, bmp</code>.
 * <p>One byte can be hidden in either 1 pixel of the image (for maximum capacity) or 2 pixels (for minimum effect on the image).
 *
 * @see BaseSteganography
 */
public class ImageSteganography extends BaseSteganography {

    /** <code>BufferedImage</code> to perform embedding/extraction on. */
    BufferedImage image;

    /**
     * Creates an <code>ImageSteganography</code> object to perform embedding or extraction of data on 24-bit, RGB Bitmap images.
     *
     * @param input         image to embed to/extract from
     * @param isEncrypted   whether the data to embed is encrypted
     * @param isCompressed  whether the data to embed is compressed
     * @param pixelsPerByte encoding mode (1 or 2 pixels/byte)
     * @throws IOException  if an error occurs while handling the image file.
     */
    public ImageSteganography(File input, boolean isEncrypted, boolean isCompressed, byte pixelsPerByte) throws IOException{
        this.isEncrypted = isEncrypted;
        this.isCompressed = isCompressed;
        this.pixelsPerByte = pixelsPerByte;
        this.image = ImageIO.read(input);
        this.capacity = this.image.getHeight()*this.image.getWidth()/this.pixelsPerByte;
    }

    /**
     * Creates an <code>ImageSteganography</code> object to perform embedding or extraction of data on 24-bit, RGB Bitmap images.
     *
     * @param input        image to embed to/extract from
     * @throws IOException if an error occurs while handling the image file.
     */
    public ImageSteganography(File input) throws IOException{ this(input, false, false, (byte) 1); }

    /**
     * Returns the {@link #image} field used to perform the embedding/extraction on.
     *
     * @return the <code>BufferedImage</code> field.
     */
    public BufferedImage getImage(){
        return this.image;
    }

    /**
     * Returns the header that contains information about the data embedded in the stego image.
     *
     * @return                       a byte array that contains information about the embedded data.
     * @throws CannotDecodeException if there is no data embedded in the stego image.
     * @see    #getHeader()
     */
    public byte[] getHeader() throws CannotDecodeException{
        int b;
        List<Byte> header = new ArrayList<>();
        if (revealByte(0,0,(byte)1) != (byte) 'M' && revealByte(0,0,(byte)1) != (byte) 'D' && revealByte(0,0,(byte)1) != (byte) 'I')
            throw new CannotDecodeException("There is nothing embedded in this image");
        do{
            b = revealByte(this.i, this.j, (byte)1);
            increment();
            header.add((byte)b);
        }while(b != (byte) '!');
        this.header = Utils.toByteArray(header);
        return Utils.toByteArray(header);
    }

    /**
     * Writes the data contained in a byte array (or {@link #header}) to the first pixels of the image.
     *
     * @param header byte array containing information about the data to embed
     * @see   #setHeader(byte[])
     * @see   #setHeader(File)
     */
    void writeHeader(byte[] header){
        for(byte b : header){
            hideByte(b, this.i, this.j, (byte)1);
            increment();
        }
    }

    /**
     * Calls {@link #setHeader(byte[])} to write the message header to the stego image,
     * then encodes the message to hide using {@link #hideByte(byte, int, int, byte)} byte by byte,
     * then saves the stego image.
     *
     * @param message                message to embed in the cover image
     * @param output                 stego image with the embedded message
     * @throws IOException           if an error occurs when creating the stego image.
     * @throws CannotEncodeException if the message is empty or larger than maximum capacity.

     */
    public void encode(byte[] message, File output) throws IOException, CannotEncodeException{
        this.writeHeader(this.setHeader(message));
        for (byte b : message) {
            hideByte(b, this.i, this.j, this.pixelsPerByte);
            increment();
        }
        ImageIO.write(this.image, "png", output);
    }

    /**
     * Calls {@link #setHeader(File)} to write the header to the file header to the stego image,
     * then encodes the file to hide using {@link #hideByte(byte, int, int, byte)} byte by byte,
     * then saves the stego image.
     *
     * @param doc                    document to embed in the cover image
     * @param output                 stego image with embedded document
     * @throws IOException           if an error occurs while reading the secret file or creating the stego image.
     * @throws CannotEncodeException if the file is empty or larger than maximum capacity.
     */
    public void encode(File doc, File output) throws IOException, CannotEncodeException{
        this.writeHeader(this.setHeader(doc));
        FileInputStream fis = new FileInputStream(doc);
        byte[] buffer = new byte[256];
        while(fis.read(buffer) > 0)
            for(byte b : buffer) {
                hideByte(b, this.i, this.j, this.pixelsPerByte);
                increment();
            }
        ImageIO.write(this.image, "png", output);
    }

    /**
     * Extracts the header from the stego image to create the {@link #secretInfo} field,
     * then decodes the hidden data in the image using {@link #revealByte(int, int, byte)},
     * then saves the secret file.
     *
     * @param file                   destination of the embedded data in the stego image
     * @throws IOException           if an error occurs while writing hidden data to the file.
     * @throws CannotDecodeException if there is no data embedded in the stego image.
     */
    public void decode(File file) throws IOException, CannotDecodeException{
        reset();
        int pos = 0;
        this.setSecretInfo(new HiddenData(this.getHeader()));
        FileOutputStream fos = new FileOutputStream(file);
        do{
            fos.write(revealByte(this.i, this.j, secretInfo.pixelsPerByte));
            increment();
            pos++;
        }while(pos<secretInfo.length);
        fos.close();
    }

    /**
     * Hides one byte in one pixels inside the least significant bits of the pixel colors.
     * <p>The <code>pixelsPerByte</code> parameter defines the encoding behaviour:</p>
     * <ul>
     *     <li><code> if pixelsPerByte == 1</code>:
     *     The 8 bits that form the byte are hidden inside the 3 LSBs of red, 2 in the 2 LSBs of green, and 3 in the 3 LSBs of blue</li>
     *     <li><code> if pixelsPerByte == 2</code>:
     *     The 4 bits to hide from the 8 bits that form the byte are hidden inside the LSB of red, 2 in the 2 LSBs of green, and  in the LSB of blue</li>
     * </ul>
     *
     * @param b             byte to hide in the pixel
     * @param i             x-index of the cover image pixel
     * @param j             y-index of the cover image pixel
     * @param pixelsPerByte encoding mode
     */
    private void embed(byte b, int i, int j, byte pixelsPerByte){
        int pixelMask = (pixelsPerByte == 1) ? 0xF8 : 0xFE, bitMask = (pixelsPerByte == 1) ? 0x07 : 0x01, shift = (pixelsPerByte == 1) ? 3 : 1;
        Color oldColor = new Color(this.image.getRGB(j,i));
        int red = oldColor.getRed(), green = oldColor.getGreen(), blue = oldColor.getBlue();
        red = red & pixelMask | b & bitMask; b = (byte) (b >> shift);
        green = green & 0xFC | b & 0x03; b = (byte) (b >> 2);
        blue = blue & pixelMask | b & bitMask; b = (byte) (b >> shift);
        Color newColor = new Color(red, green,blue);
        this.image.setRGB(j,i,newColor.getRGB());
    }

    /**
     * Hides one byte using {@link #embed(byte, int, int, byte)}.
     * <p>The <code>pixelsPerByte</code> parameter defines the encoding behaviour:</p>
     * <ul>
     *     <li><code> if pixelsPerByte == 1</code>:
     *     we hide the byte in the pixel of the cover image at the index i,j</li>
     *     <li><code> if pixelsPerByte == 1</code>:
     *     we hide the 4 most significant bits of the byte in the pixel (i, j) of the cover image
     *     and then we perform a left shift of 4 bits to the byte
     *     and we hide the 4 least significant bits of the byte in the opposite pixel of the cover image (height-1-i, width-1-j)</li>
     * </ul>
     *
     * @param b             byte to hide in the pixel(s)
     * @param i             x-index of the cover image pixel
     * @param j             y-index of the cover image pixel
     * @param pixelsPerByte encoding mode
     */
    private void hideByte(byte b, int i, int j, byte pixelsPerByte){
        embed(b, i, j, pixelsPerByte);
        if(pixelsPerByte == 2){
            b = (byte) (b >> 4);
            embed(b, this.image.getHeight()-i-1, this.image.getWidth()-j-1, pixelsPerByte);
        }
    }

    /**
     * Reveals one byte hidden inside the (i, j) pixel.
     * <p>The <code>pixelsPerByte</code> parameter defines the decoding behaviour:</p>
     * <ul>
     *     <li><code> if pixelsPerByte == 1</code>:
     *     We extract the 3 LSBs in red, 2 LSBs in green and 3 LSBs in blue to form the byte</li>
     *     <li><code> if pixelsPerByte == 2</code>:
     *     We extract the LSB in red, 2 LSBs in green and LSB in blue to form the byte</li>
     * </ul>
     *
     * @param i             x-index of the stego image pixel
     * @param j             y-index of the stego image pixel
     * @param pixelsPerByte decoding mode
     * @return              the embedded byte in stego image pixel (i, j).
     */
    private byte extract(int i, int j, byte pixelsPerByte){
        int b;
        int pixelMask = (pixelsPerByte == 1) ? 0x07 : 0x01, shift = (pixelsPerByte == 1) ? 3 : 1;
        Color color = new Color(this.image.getRGB(j,i));
        int red = color.getRed(), green = color.getGreen(), blue = color.getBlue();
        b = (blue & pixelMask) ; b = b << 2;
        b = b | (green & 0x03); b = b << shift;
        b = b | (red & pixelMask);
        return (byte) b;
    }

    /**
     * Reveals one byte using {@link #extract(int, int, byte)}.
     * <p>The <code>pixelsPerByte</code> parameter defines the decoding behaviour:</p>
     * <ul>
     *     <li><code> if pixelsPerByte == 1</code>:
     *     We extract the byte in pixel (i, j) </li>
     *     <li><code> if pixelsPerByte == 2</code>:
     *     We extract the 4 MSBs of the byte in pixel (i, j) and then the 4 LSBs of the byte in pixel (height-1-i, width-1-j)</li>
     * </ul>
     *
     * @param i             x-index of the stego image pixel
     * @param j             y-index of the stego image pixel
     * @param pixelsPerByte decoding mode
     * @return              the embedded byte in stego image.
     */
    private byte revealByte(int i, int j, byte pixelsPerByte){
        byte b = extract(i, j, pixelsPerByte);
        if(pixelsPerByte == 2){
            byte c = extract(this.image.getHeight()-i-1, this.image.getWidth()-j-1, pixelsPerByte);
            c = (byte) (c << 4); b = (byte) (c | b);
        }
        return b;
    }

    /**
     * Increments the position of the current pixel in the 24-bit, RGB Bitmap image.
     */
    protected void increment(){
        this.j++;
        if(this.j==this.image.getWidth()-1){ this.j=0;this.i++; }
    }

    /**
     * resets index back to (0, 0).
     */
    protected void reset(){ this.i = 0; this.j = 0; }

}
