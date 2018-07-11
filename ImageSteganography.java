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
import java.util.Scanner;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageSteganography extends BaseSteganography{
    protected BufferedImage image;
    // Constructors
    public ImageSteganography(File input, boolean isEncrypted) throws IOException{
        this.isEncrypted = isEncrypted;
        this.image = ImageIO.read(input);
        this.capacity = this.image.getHeight()*this.image.getWidth();
    }
    public ImageSteganography(File input) throws IOException{
        this.isEncrypted = false;
        this.image = ImageIO.read(input);
        this.capacity = this.image.getHeight()*this.image.getWidth();
    }
    public BufferedImage getImage(){
        return this.image;
    }
    public void writeHeader(byte[] header){
        int i=0, j=0;
        for(byte b : header){
            this.image.setRGB(j,i,hidePixel(this.image.getRGB(j,i),b));
            increment(i,j);
        }
    }
    public byte[] getHeader(){
        int i=0, j=0;
        byte b;
        List<Byte> header = new ArrayList<Byte>();
        do{
            b = revealPixel(this.image.getRGB(j,i));
            header.add(b);
        }while(b != (byte) '!');
        return Helpers.toByteArray(header);
    }
    public void encode(String str) throws IOException{
        try{
            this.writeHeader(this.setHeader(str));
            int i=(this.header.length)%(this.image.getWidth());
            int j=(this.header.length)/(this.image.getWidth());
            byte[] message = str.getBytes(Charset.forName("UTF-8"));
            for (byte b : message){
                this.image.setRGB(j,i,hidePixel(this.image.getRGB(j,i),b));
                increment(i,j);
            }
            File img = new File("encoded.png");
            ImageIO.write(this.image, "png", img);
            System.out.println("Message hidden inside encoded.png");
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void encode(File doc) throws IOException{
        try{
            this.writeHeader(this.setHeader(doc));
            int i=(this.header.length)%(this.image.getWidth());
            int j=(this.header.length)/(this.image.getWidth());
            String line;
            this.writeHeader(this.setHeader(doc));
            InputStreamReader stream = new InputStreamReader(new FileInputStream(doc));
            BufferedReader reader = new BufferedReader(stream);//reads the user file
            while((line = reader.readLine()) != null){
                byte[] message = line.getBytes(Charset.forName("UTF-8"));
                for (byte b : message){
                    this.image.setRGB(j,i,hidePixel(this.image.getRGB(j,i),b));
                    increment(i,j);
                }
                this.image.setRGB(j,i,hidePixel(this.image.getRGB(j,i),Byte.parseByte("00001010",2))); // Adds new line caracter
            }
            reader.close();
            stream.close();
            File eimg = new File("encoded.png");
            ImageIO.write(this.image, "png", eimg);
            System.out.println("File hidden inside encoded.png");
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void decode() throws IOException {
        try{
            byte[] header = this.getHeader();
            Map<String, String> attributes = this.getAttributes(header);
            File file = new File("secret."+attributes.get("extension"));
            int length = Integer.parseInt(attributes.get("length"));
            FileOutputStream fos = new FileOutputStream(file);
            int i=(header.length)%(this.image.getWidth());
            int j=(header.length)/(this.image.getWidth());
            int pos = 0;
            do{
                byte b = revealPixel(this.image.getRGB(j,i));
                fos.write(b);
                pos++;
            }while(pos<length);
            fos.close();
            if(attributes.get("encryption").equals('E')){
                // Temporary
                System.out.print("Enter Password :\t");
                Scanner scan = new Scanner(System.in);
                String password = scan.nextLine();
                file = AESEncryption.decrypt(file, password);
            }
            System.out.println("Secret file saved to "+ file.getName());
        }catch(Exception e) {System.out.println("Decoding Error : "+ e.getMessage());}
    }
    //hides the bits of pixelB in pixelA
    private int hidePixel(int pixelA, byte data){
        int pixel = pixelA;
        String pixelB = String.format("%8s",Integer.toBinaryString(data)).replace(' ','0');
        pixelB = pixelB.substring(pixelB.length()-8, pixelB.length());
        System.out.println("pixelB: "+pixelB);
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
    private byte revealPixel(int pixel){
        byte b;
        //opposite of hidePixel;
        String str="";
        int x = (pixel & 0x00070000)>>>16;
        str += String.format("%3s",Integer.toBinaryString(x)).replace(' ','0');
        x = (pixel & 0x00000700)>>>8;
        str += String.format("%3s",Integer.toBinaryString(x)).replace(' ','0');
        x = (pixel & 0x00000003);
        str += String.format("%2s",Integer.toBinaryString(x)).replace(' ','0');
        b = Byte.parseByte(str, 2);
        return b;
    }
    protected void increment(int i,int j){
        System.out.println("I: "+i+"  J: "+j);
        j++;
        if(j==this.image.getWidth()-1){
            j=0;
            i++;
        }
    }
}