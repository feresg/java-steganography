package Steganography.Logic;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.Charset;

public abstract class BaseSteganography{
    protected boolean isEncrypted = false;
    protected int capacity;
    protected byte[] header;
    // Constructor
    public BaseSteganography(){};
    // Abstract encoding and decoding functions
    public abstract void encode(String str, File output) throws IOException;
    public abstract void encode(File doc, File output) throws IOException;
    public abstract void decode(File file) throws IOException;
    // Capacity and Header getters
    public long getCapacity(){
        return this.capacity;
    }
    public abstract byte[] getHeader();
    // header setter method (string and file)
    protected byte[] setHeader(String message){
        List<Byte> header = new ArrayList<Byte>();
        header.add((byte)'M');
        header.add((this.isEncrypted) ? (byte)'E' : (byte)'U');
        String messageLength = String.format("%16s", Integer.toBinaryString(message.length())).replace(' ', '0');
        for(int i=0; i<messageLength.length();i+=8){
            header.add((byte)Integer.parseInt(messageLength.substring(i,i+8),2));
        }
        header.add(((byte) '!'));
        this.header = Helpers.toByteArray(header);
        return Helpers.toByteArray(header);
    }
    protected byte[] setHeader(File file) throws IOException{
        System.out.println("calling setheader");
        List<Byte> header = new ArrayList<Byte>();
        String extension = Helpers.getFileExtension(file).toLowerCase();
        header.add((byte)'D');
        header.add((this.isEncrypted) ? (byte)'E' : (byte)'U');
        String fileLength = String.format("%24s",Long.toBinaryString(file.length())).replace(' ', '0');
        for(int i=0; i<fileLength.length();i+=8){
            header.add((byte)Integer.parseInt(fileLength.substring(i,i+8),2));
        }
        byte[] fileExtension = extension.getBytes(Charset.forName("UTF-8"));
        for(byte b : fileExtension)
            header.add(b);
        header.add(((byte) '!'));
        this.header = Helpers.toByteArray(header);
        return Helpers.toByteArray(header);
    }
    public Map<String, String> getAttributes(byte[] header){
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("mode",  Character.toString((char) header[0]));
        if(attributes.get("mode").equals("I")){
            String height="", width="";
            for(int j=1; j<3; j++){
                String w = String.format("%8s",Integer.toBinaryString(header[j])).replace(' ','0');
                width += w.substring(w.length()-8, w.length());
                String h = String.format("%8s",Integer.toBinaryString(header[j+2])).replace(' ','0');
                height += h.substring(h.length()-8, h.length());
            }
            attributes.put("width", String.valueOf(Integer.parseInt(width,2)));
            attributes.put("height", String.valueOf(Integer.parseInt(height,2)));
            attributes.put("extension", "png");
            return attributes;
        }
        attributes.put("encryption",Character.toString((char) header[1]));
        int length = 0;
        String extension = "";
        String lengthBin = "";
        switch(attributes.get("mode")){
            case "M" :
                for(int j=2; j<4; j++){
                    String currentByte = String.format("%8s",Integer.toBinaryString(header[j])).replace(' ','0');
                    lengthBin += currentByte.substring(currentByte.length()-8,currentByte.length());
                }
                length = Integer.parseInt(lengthBin, 2);
                extension = "txt";
                break;
            case "D" :
                for(int j=2; j<5; j++){
                    String currentByte = String.format("%8s",Integer.toBinaryString(header[j])).replace(' ','0');
                    lengthBin += currentByte.substring(currentByte.length()-8,currentByte.length());
                }
                length = Integer.parseInt(lengthBin, 2);
                for(int j=5; j<header.length-1;j++)
                    extension += (char) header[j];
                break;
            default :
                break;
        }
        attributes.put("length", String.valueOf(length));
        attributes.put("extension", extension);
        return attributes;
    }

}
