package Steganography.Logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageSteganography extends BaseSteganography {
    protected BufferedImage image;
    int i=0,j=0;
    byte newLine = 10;

    // Constructors
    public ImageSteganography(File input, boolean isEncrypted) throws IOException{
        this.isEncrypted = isEncrypted;
        this.image = ImageIO.read(input);
        this.capacity = this.image.getHeight()*this.image.getWidth();
    }
    public ImageSteganography(File input) throws IOException{
        this(input, false);
    }
    public BufferedImage getImage(){
        return this.image;
    }
    protected void writeHeader(byte[] header){
        for(byte b : header){
            this.image.setRGB(j,i,hidePixel(this.image.getRGB(j,i),b));
            increment();
        }
    }
    public byte[] getHeader(){
        reset();
        int b;
        List<Byte> header = new ArrayList<Byte>();
        do{
            b = this.revealPixel(this.image.getRGB(j,i));
            increment();
            header.add((byte)b);

        }while(b != (byte) '!');
        return Helpers.toByteArray(header);
    }
    public void encode(String str, File output) throws IOException{
        try{
            this.writeHeader(this.setHeader(str));
            byte[] message = str.getBytes(Charset.forName("UTF-8"));
            for (byte b : message){
                this.image.setRGB(j,i,hidePixel(this.image.getRGB(j,i),b));
                increment();
            }
            ImageIO.write(this.image, "png", output);
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void encode(File doc, File output) throws IOException{
        try{
            this.writeHeader(this.setHeader(doc));
            System.out.println("i: "+i+" j: "+j);
            String line;
            InputStreamReader stream = new InputStreamReader(new FileInputStream(doc));
            BufferedReader reader = new BufferedReader(stream);//reads the user file
            while((line = reader.readLine()) != null){
                byte[] message = line.getBytes(Charset.forName("UTF-8"));
                for (byte b : message){
                  this.image.setRGB(j,i,hidePixel(this.image.getRGB(j,i),b));
                  increment();
                }
                this.image.setRGB(j,i,hidePixel(this.image.getRGB(j,i),newLine)); // Adds new line caracter
                increment();
            }
            reader.close();
            stream.close();
            ImageIO.write(this.image, "png", output);
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void decode(File file) throws IOException {
        reset();
        try{
            Map<String, String> attributes = this.getAttributes(this.getHeader());
            int length = Integer.parseInt(attributes.get("length"));
            FileOutputStream fos = new FileOutputStream(file);
            int pos = 0;
            int b;
            do{
                b = revealPixel(this.image.getRGB(j,i));
                fos.write(b);
                pos++;
                increment();
            }while(pos<length);
            fos.close();
            System.out.println("Secret file saved to "+ file.getName());
        }catch(Exception e) {System.out.println("Decoding Error : "+ e.getMessage());}
    }
    //hides the bits of pixelB in pixelA
    private int hidePixel(int pixelA, byte data){
        int pixel = pixelA;
        String pixelB = String.format("%8s",Integer.toBinaryString(data)).replace(' ','0');
        pixelB = pixelB.substring(pixelB.length()-8, pixelB.length());
        //System.out.println("pixelB: "+pixelB);
        //coding the 8 bits charachter (pixelB) in one pixel as follows;
        //3 bits in red , 3 bits in green, 2 bits in blue
        //coding 3 bits in red
        pixel = pixel & 0xFFF8FFFF | ((Integer.parseInt(pixelB.substring(0,3),2) & 0x00000007) << 16);//example (1100 & 0011) |(1010 & 0111) = (0010); then shifting to the right place >>
        //coding 3 bits in green
        pixel = pixel & 0xFFFFF8FF | ((Integer.parseInt(pixelB.substring(3,6),2) & 0x00000007) <<8);
        //coding 2 bits in blue
        pixel = pixel & 0xFFFFFFFC | (Integer.parseInt(pixelB.substring(6,8),2) & 0x00000003);
        return  pixel;
    }

    //recover hidden bits in pixel
    private int revealPixel(int pixel){
        int y;
        //opposite of hidePixel;
        String str="";
        int x = (pixel & 0x00070000)>>>16;
        str += String.format("%3s",Integer.toBinaryString(x)).replace(' ','0');
        x = (pixel & 0x00000700)>>>8;
        str += String.format("%3s",Integer.toBinaryString(x)).replace(' ','0');
        x = (pixel & 0x00000003);
        str += String.format("%2s",Integer.toBinaryString(x)).replace(' ','0');
        y = Integer.parseInt(str, 2);
        return y;
    }
    private void increment(){
        this.j++;
        if(this.j==this.image.getWidth()-1){
            this.j=0;
            this.i++;
        }
    }
    private void reset(){
      this.i=0;
      this.j=0;
    }
}
