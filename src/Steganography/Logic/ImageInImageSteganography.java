package Steganography.Logic;

import Steganography.Modals.AlertBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageInImageSteganography extends ImageSteganography{

    private byte pixelsPerPixel;

    public ImageInImageSteganography(File input, byte pixelsPerPixel) throws IOException{ super(input); this.pixelsPerPixel = pixelsPerPixel; }
    public ImageInImageSteganography(File input) throws IOException{ this(input,(byte) 1);}

    byte[] setHeader(File file) throws IOException{
        List<Byte> header = new ArrayList<>();
        header.add((byte)'I');
        header.add(pixelsPerPixel);
        BufferedImage bimg = ImageIO.read(file);
        String width = String.format("%16s",Long.toBinaryString(bimg.getWidth())).replace(' ', '0');
        String height = String.format("%16s",Long.toBinaryString(bimg.getHeight())).replace(' ', '0');
        for(int i=0; i<width.length();i+=8)
            header.add((byte)Integer.parseInt(width.substring(i,i+8),2));
        for(int i=0; i<height.length();i+=8)
            header.add((byte)Integer.parseInt(height.substring(i,i+8),2));
        header.add(((byte) '!'));
        this.header = Helpers.toByteArray(header);
        return Helpers.toByteArray(header);
    }

    public void encode(File img, File output){
        try{
            this.writeHeader(this.setHeader(img));
            BufferedImage imageToHide = ImageIO.read(img);
            int embedWidth = imageToHide.getWidth(), embedHeight = imageToHide.getHeight();
            int sourceWidth = this.image.getWidth(), sourceHeight = this.image.getHeight();
            int pos = ((this.header.length)/(sourceWidth))+1;
            for (int k=pos; k< embedHeight+pos; k++)
                for (int l=0 ;l<embedWidth ;l++){
                    this.image.setRGB(l,k,hidePixel(this.image.getRGB(l,k), imageToHide.getRGB(l,k-pos)));
                    if(this.pixelsPerPixel == 2)
                        this.image.setRGB(sourceWidth-l-1, sourceHeight-k-1, hidePixel(this.image.getRGB(sourceWidth-l-1,sourceHeight-k-1), imageToHide.getRGB(l,k-pos) << 3));
                }
            ImageIO.write(this.image,"png",output);
            System.out.println("Image encoded inside "+ output.getName());
        }catch (IOException e) {
            e.printStackTrace();
            AlertBox.error("Error while encoding", e.getMessage());
        }
    }

    public void decode(File result){
        this.setSecretInfo(new HiddenData(this.getHeader()));
        this.pixelsPerPixel = secretInfo.pixelsPerPixel;
        int sourceWidth = this.image.getWidth(), sourceHeight = this.image.getHeight();
        System.out.println("\t HEADER LENGTH : "+this.header.length);
        int pos = (this.header.length/sourceWidth)+1;
        BufferedImage hiddenImage = new BufferedImage(secretInfo.width, secretInfo.height, BufferedImage.TYPE_INT_RGB);
        for (int k=pos ;k<secretInfo.height+pos ;k++)
            for (int l=0;l<secretInfo.width ;l++) {
                int pixel = revealPixel(this.image.getRGB(l, k));
                if (this.pixelsPerPixel == 2) {
                    int pixel2 = revealPixel(this.image.getRGB(sourceWidth - l - 1, sourceHeight - k - 1));
                    pixel = pixel | (pixel2 >> 3);
                }
                hiddenImage.setRGB(l, k - pos, pixel);
            }
        try{
            ImageIO.write(hiddenImage,"png",result);
            System.out.println("Secret image saved to "+ result.getName());
        }catch (IOException e) {
            e.printStackTrace();
            AlertBox.error("Error while decoding", e.getMessage());
        }
    }

    private int hidePixel(int pixelA, int pixelB) { return pixelA & 0xFFF8F8F8 | (pixelB & 0x00E0E0E0) >> 5; }
    private int revealPixel(int pixel) { return (pixel & 0xFF070707) << 5; }

}
