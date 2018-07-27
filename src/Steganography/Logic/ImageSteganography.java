package Steganography.Logic;

import Steganography.Modals.AlertBox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageSteganography extends BaseSteganography {

    protected BufferedImage image;
    private int i=0, j=0;

    // Constructors
    public ImageSteganography(File input, boolean isEncrypted, boolean isCompressed, byte pixelsPerByte) throws IOException{
        this.isEncrypted = isEncrypted;
        this.isCompressed = isCompressed;
        this.pixelsPerByte = pixelsPerByte;
        this.image = ImageIO.read(input);
        this.capacity = this.image.getHeight()*this.image.getWidth();
    }
    public ImageSteganography(File input) throws IOException{ this(input, false, false, (byte) 1); }

    public BufferedImage getImage(){
        return this.image;
    }

    void writeHeader(byte[] header){
        for(byte b : header){
            hideByte(b, this.i, this.j, (byte)1);
            increment();
        }
    }

    public byte[] getHeader(){
        reset();
        int b;
        List<Byte> header = new ArrayList<>();
        do{
            b = revealByte(this.i, this.j, (byte)1);
            increment();
            header.add((byte)b);
        }while(b != (byte) '!');
        this.header = Helpers.toByteArray(header);
        return Helpers.toByteArray(header);
    }

    public void encode(byte[] message, File output){
        try{
            this.writeHeader(this.setHeader(message));
            for (byte b : message){
                hideByte(b, this.i, this.j, this.pixelsPerByte);
                increment();
            }
            ImageIO.write(this.image, "png", output);
            System.out.println("Message hidden inside "+output.getName());
        }catch(IOException e) {e.printStackTrace(); AlertBox.error("Error while encoding", e.getMessage());}
    }
    public void encode(File doc, File output){
        try{
            this.writeHeader(this.setHeader(doc));
            FileInputStream fis = new FileInputStream(doc);
            byte[] buffer = new byte[256];
            int read = 0;
            while( ( read = fis.read( buffer ) ) > 0 ){
                for(byte b : buffer) {
                    hideByte(b, this.i, this.j, this.pixelsPerByte);
                    increment();
                }
            }
            ImageIO.write(this.image, "png", output);
            System.out.println("File hidden inside "+output.getName());
        }catch(IOException e) {
            e.printStackTrace();
            AlertBox.error("Error while encoding", e.getMessage());
        }
    }

    public void decode(File file){
        reset();
        this.setSecretInfo(new HiddenData(this.getHeader()));
        this.pixelsPerByte = secretInfo.pixelsPerByte;
        int pos = 0;
        int b;
        try{
            FileOutputStream fos = new FileOutputStream(file);
            do{
                b = revealByte(this.i, this.j, this.pixelsPerByte);
                fos.write(b);
                pos++;
                increment();
            }while(pos<secretInfo.length);
            fos.close();
            System.out.println("Secret file saved to "+ file.getName());
        }catch (IOException e) {
            e.printStackTrace();
            AlertBox.error("Error while decoding", e.getMessage());}
    }

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

    private void hideByte(byte b, int i, int j, byte pixelsPerByte){
        embed(b, i, j, pixelsPerByte);
        if(pixelsPerByte == 2){
            b = (byte) (b >> 4);
            embed(b, this.image.getHeight()-i-1, this.image.getWidth()-j-1, pixelsPerByte);
        }
    }

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

    private byte revealByte(int i, int j, byte pixelsPerByte){
        byte b = extract(i, j, pixelsPerByte);
        if(pixelsPerByte == 2){
            byte c = extract(this.image.getHeight()-i-1, this.image.getWidth()-j-1, pixelsPerByte);
            c = (byte) (c << 4); b = (byte) (c | b);
        }
        return b;
    }

    private void increment(){
        this.j++;
        if(this.j==this.image.getWidth()-1){ this.j=0;this.i++; }
    }

    private void reset() { this.i=0;this.j=0; }

}
