package Steganography.Logic;
import Steganography.Modals.PasswordPrompt;

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
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.metadata.IIOMetadata;

public class GifSteganography extends BaseSteganography{
    private BufferedImage[] frames;
    private IIOMetadata[] metadatas;
    private int delayMS;
    private int i=0,j=0,k=0;
    byte newLine = 10;

    // Constructor
    public GifSteganography(File input, boolean isEncrypted) throws IOException{
        this.isEncrypted = isEncrypted;
        this.frames = Metadata.getFrames(input);
        this.metadatas = Metadata.getMetadatas(input);
        this.delayMS = Metadata.getDelayMS(input);
    }
    public GifSteganography(File input) throws IOException{
        this(input, false);
    }
    private void writeHeader(byte[] header){
        for(byte b : header)
            hidePixels(b);
    }
    public byte[] getHeader(){
        reset();
        int b;
        List<Byte> header = new ArrayList<Byte>();
        do{
            b = revealPixels();
            header.add((byte) b);
        }while(b != (byte) '!');
        return Helpers.toByteArray(header);
    }
    public void encode(String str, File output){
        try{
            this.writeHeader(this.setHeader(str));
            byte[] message = str.getBytes(Charset.forName("UTF-8"));
            for(byte b : message)
                hidePixels(b);
            ImageOutputStream ios = new FileImageOutputStream(output);
            ColorModel cm = this.frames[0].getColorModel();
            ImageTypeSpecifier imageType = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
            GifSequenceWriter writer = new GifSequenceWriter(ios, imageType, this.delayMS, true);
            for(int x=0; x<this.frames.length; x++)
              writer.writeToSequence(this.frames[x], this.metadatas[x]);
            writer.close();
            ios.close();
            System.out.println("Message hidden inside encoded.gif");
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void encode(File doc, File output){
        try{
            this.writeHeader(this.setHeader(doc));
            String line;
            InputStreamReader stream = new InputStreamReader(new FileInputStream(doc));
            BufferedReader reader = new BufferedReader(stream);//reads the user file
            while((line = reader.readLine()) != null){
                byte[] message = line.getBytes(Charset.forName("UTF-8"));
                for(byte b : message)
                    hidePixels(b);
                hidePixels(newLine);
            }
            ImageOutputStream ios = new FileImageOutputStream(output);
            ColorModel cm = this.frames[0].getColorModel();
            ImageTypeSpecifier imageType = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
            GifSequenceWriter writer = new GifSequenceWriter(ios, imageType, this.delayMS, true);
            for(int x=0; x<this.frames.length; x++) {
              writer.writeToSequence(this.frames[x], this.metadatas[x]);
            }
            writer.close();
            ios.close();
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void decode(File file){
        try{
            reset();
            Map<String, String> attributes = getAttributes(this.getHeader());
            int length = Integer.parseInt(attributes.get("length"));
            int pos = 0;
            int b;
            FileOutputStream fos = new FileOutputStream(file);
            do{
                b = revealPixels();
                fos.write((byte)b);
                pos++;
            }while(pos<length);
            fos.close();
            System.out.println("Secret file saved to "+ file.getName());
        }catch(Exception e) {System.out.println("Decryption Error : "+ e.getMessage());}
    }
    private int hidePixel(int pixel, char c){
        String before = String.format("%8s", Integer.toBinaryString(pixel)).replace(' ','0');
        String after = before.substring(0,7)+c;
        return Integer.parseInt(after,2);
    }
    private void hidePixels(byte b){
        int[] pixel = new int[4];
        String currentByte;
        for(int l=0; l<8; l++){
            WritableRaster raster = this.frames[k].getRaster();
            raster.getPixel(j,i,pixel);
            currentByte = String.format("%8s",Integer.toBinaryString(b)).replace(' ', '0');
            currentByte = currentByte.substring(currentByte.length()-8, currentByte.length());
            pixel[0] = hidePixel(pixel[0], currentByte.charAt(l));
            raster.setPixel(j,i,pixel);
            increment();
        }
    }
    private byte revealPixels(){
        int pixel[] = new int[4];
        int b;
        String currentByte;
        String bit = "";
        for(int l=0; l<8; l++){
            Raster raster = this.frames[k].getRaster();
            raster.getPixel(j,i,pixel);
            currentByte = String.format("%8s", Integer.toBinaryString(pixel[0])).replace(" ", "0");
            currentByte = currentByte.substring(currentByte.length()-8, currentByte.length());
            bit += currentByte.charAt(7);
            increment();
        }
        b = Integer.parseInt(bit,2);
        return (byte) b;
    }
    private void increment(){
        this.j++;
        if(this.j == this.frames[this.k].getWidth()){
            this.j=0;
            this.i++;
        }
        if(this.i == this.frames[this.k].getHeight()){
            this.j=0;
            this.i=0;
            this.k++;
        }
    }
    private void reset(){
        this.i=0;
        this.j=0;
        this.k=0;
    }
}