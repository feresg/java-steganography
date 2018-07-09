import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageSteganography extends BaseSteganography{
    private boolean isEncrypted = false;
    private long capacity;
    private int imageHeight;
    private int imageWidth;
    private BufferedImage image;
    // Constructors
    public ImageSteganography(File input, boolean isEncrypted){
        this.isEncrypted = isEncrypted;
        this.image = ImageIO.read(input);
        this.imageHeight = this.image.getHeight();
        this.imageWidth = this.image.getWidth();
        this.capacity = (this.imageWidth/2)*this.imageHeight;
    }
    public long getCapacity(){
        return this.capacity;
    }
    public int getImageHeight(){
        return this.imageHeight;
    }
    public int getImageWidth(){
        return this.imageWidth;
    }
    public boolean getEncryptionStatus(){
        return this.IsEncrypted;
    }
    public BufferedImage getImage(){
        return this.image;
    }
    public String setHeader(String message){
        String header = "M!"+message.length()+"!";
        header += (this.isEncrypted) ? "1" : "0";
        header += "!!";
        return header;
    }
    public String setHeader(File file){
        String header = "";
        String extension = Helpers.getFileExtension(file).toLowerCase();
        List<String> imageExtensions = Arrays.asList("png","bmp","jpg","jpeg");
        if(imageExtensions.contains(extensions)){
            header += "I!"+extension+"!";
            BufferedImage bimg = ImageIO.read(file);
            header += bimg.getWidth()+"!"+bimg.getHeight()+"!!";
        }else{
            header += "F!"+extension+"!"+file.length()+"!";
            header += (this.isEncrypted) ? "1" : "0";
            header += "!!";
        }
        return header;
    }
    public String getHeader(){

    }

}