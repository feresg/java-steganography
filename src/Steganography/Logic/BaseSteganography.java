package Steganography.Logic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseSteganography{

    boolean isEncrypted = false;
    int capacity;
    byte[] header;
    final byte newLine = 10;

    // Constructor
    BaseSteganography(){}

    // Abstract encoding and decoding and getHeader functions
    public abstract void encode(String str, File output);
    public abstract void encode(File doc, File output);
    public abstract void decode(File file);
    public abstract byte[] getHeader();

    // Capacity and Header getters
    public long getCapacity(){
        return this.capacity;
    }

    // header setter method (string and file)
    byte[] setHeader(String message){
        List<Byte> header = new ArrayList<>();
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
    byte[] setHeader(File file) throws IOException{
        List<Byte> header = new ArrayList<>();
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
        Map<String, String> attributes = new HashMap<>();
        attributes.put("mode",  Character.toString((char) header[0]));
        if(attributes.get("mode").equals("I")){
            StringBuilder height= new StringBuilder();StringBuilder width= new StringBuilder();
            for(int j=1; j<3; j++){
                String w = String.format("%8s",Integer.toBinaryString(header[j])).replace(' ','0');
                width.append(w.substring(w.length() - 8, w.length()));
                String h = String.format("%8s",Integer.toBinaryString(header[j+2])).replace(' ','0');
                height.append(h.substring(h.length() - 8, h.length()));
            }
            attributes.put("width", String.valueOf(Integer.parseInt(width.toString(),2)));
            attributes.put("height", String.valueOf(Integer.parseInt(height.toString(),2)));
            attributes.put("extension", "png");
            return attributes;
        }
        attributes.put("encryption",Character.toString((char) header[1]));
        int length = 0;
        StringBuilder extension = new StringBuilder();
        StringBuilder lengthBin = new StringBuilder();
        switch(attributes.get("mode")){
            case "M" :
                for(int j=2; j<4; j++){
                    String currentByte = String.format("%8s",Integer.toBinaryString(header[j])).replace(' ','0');
                    lengthBin.append(currentByte.substring(currentByte.length() - 8, currentByte.length()));
                }
                length = Integer.parseInt(lengthBin.toString(), 2);
                extension = new StringBuilder("txt");
                break;
            case "D" :
                for(int j=2; j<5; j++){
                    String currentByte = String.format("%8s",Integer.toBinaryString(header[j])).replace(' ','0');
                    lengthBin.append(currentByte.substring(currentByte.length() - 8, currentByte.length()));
                }
                length = Integer.parseInt(lengthBin.toString(), 2);
                for(int j=5; j<header.length-1;j++)
                    extension.append((char) header[j]);
                break;
            default :
                break;
        }
        attributes.put("length", String.valueOf(length));
        attributes.put("extension", extension.toString());
        return attributes;
    }

}
