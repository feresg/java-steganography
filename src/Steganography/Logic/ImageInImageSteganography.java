package Steganography.Logic;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageInImageSteganography extends ImageSteganography{
    public ImageInImageSteganography(File input) throws IOException{
        super(input);
    }
    protected byte[] setHeader(File file) throws IOException{
        List<Byte> header = new ArrayList<Byte>();
        header.add((byte)'I');
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
    public void encode(File img, File output) throws IOException{
        try{
            this.writeHeader(this.setHeader(img));
            BufferedImage imageToHide = ImageIO.read(img);
            int width = imageToHide.getWidth();
            int height = imageToHide.getHeight();
            int pos = ((this.header.length)/(this.image.getWidth()))+1;
            for (int k=pos; k< height+pos; k++) {
                for (int l=0 ;l<width ;l++){
                    System.out.println(k+"\t"+l);
                    this.image.setRGB(l,k,hidePixel(this.image.getRGB(l,k), imageToHide.getRGB(l,k-pos)));
                }
            }
            ImageIO.write(this.image,"png",output);
            System.out.println("Image encoded inside "+ output.getName());
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void decode(File result) throws IOException{
        try{
            this.header = this.getHeader();
            Map<String, String> attributes = this.getAttributes(this.header);
            int width = Integer.parseInt(attributes.get("width"));
            int height = Integer.parseInt(attributes.get("height"));
            int pos = ((this.header.length)/(this.image.getWidth()))+1;
            BufferedImage hiddenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int k=pos ;k<height+pos ;k++)
                for (int l=0;l<width ;l++)
                    hiddenImage.setRGB(l,k-pos,revealPixel(this.image.getRGB(l,k)));
            ImageIO.write(hiddenImage,"png",result);
            System.out.println("Secret image saved to "+ result.getName());
        }catch(IOException e) {System.out.println("Decoding Error : " + e.getMessage());}
    }
    private int hidePixel(int pixelA, int pixelB) {
        return pixelA & 0xFFF8F8F8 | (pixelB & 0x00E0E0E0) >> 5;
    }
    private int revealPixel(int pixel) { return (pixel & 0xFF070707) << 5; }
}
