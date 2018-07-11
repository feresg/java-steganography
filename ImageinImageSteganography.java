import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

class ImageInImageSteganography extends ImageSteganography{
    ImageInImageSteganography(File input) throws IOException{
        super(input);
    }
    public byte[] setHeader(File file) throws IOException{
        List<Byte> header = new ArrayList<Byte>();
        header.add((byte)'I');
        BufferedImage bimg = ImageIO.read(file);
        String width = String.format("%16s", bimg.getWidth()).replace(' ', '0');
        String height = String.format("%16s", bimg.getHeight()).replace(' ', '0');
        for(int i=0; i<width.length();i+=8)
            header.add(Byte.parseByte(width.substring(i,i+8),2));
        for(int i=0; i<height.length();i+=8)
            header.add(Byte.parseByte(height.substring(i,i+8),2));
        header.add(((byte) '!'));
        return Helpers.toByteArray(header);
    }
    public void encode(File img) throws IOException{
        try{
            this.writeHeader(this.setHeader(img));
            int i=(this.header.length)%(this.image.getWidth())+1;
            int j=0;
            BufferedImage imageSource = this.image;
            BufferedImage imageToHide = ImageIO.read(img);
            int width = imageToHide.getWidth();
            int height = imageToHide.getHeight();
            BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            while(i<width){
                while (j<height) {
                  imageResult.setRGB(j, i, hidePixel(imageSource.getRGB(j, i), imageToHide.getRGB(i, j)));
                  j++;
                }
                i++;
            }
            File eimage = new File("encoded.png");
            ImageIO.write(imageResult,"png",eimage);
            System.out.println("Image encoded inside "+ eimage.getName());
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void decode() throws IOException{
        try{
            byte[] header = this.getHeader();
            int width = Integer.parseInt(String.format("%8s",Integer.toBinaryString(header[1])).replace(' ','0')+String.format("%8s",Integer.toBinaryString(header[1])).replace(' ','0'),2);
            int height = Integer.parseInt(String.format("%8s",Integer.toBinaryString(header[3])).replace(' ','0')+String.format("%8s",Integer.toBinaryString(header[4])).replace(' ','0'),2);
            int i=(header.length)%(this.image.getWidth())+1;
            int j=0;
            BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            while (i < width) {
                while (j < height) {
                  imageResult.setRGB(i, j, revealPixel(this.image.getRGB(i, j)));
                  j++;
                }
                i++;
            }
            File result = new File("result.png");
            ImageIO.write(imageResult,"png",result);
            System.out.println("Secret image saved to "+ result.getName());
        }catch(IOException e) {System.out.println("Decoding Error : " + e.getMessage());}
    }
    private int hidePixel(int pixelA, int pixelB) {
        return pixelA & 0xFFF8F8F8 | (pixelB & 0x00E0E0E0) >> 5;
    }
    private int revealPixel(int pixel) {
        return (pixel & 0xFF070707) << 5;
    }
}