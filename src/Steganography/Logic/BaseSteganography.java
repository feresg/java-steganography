package Steganography.Logic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseSteganography{

    boolean isEncrypted = false;
    boolean isCompressed = false;
    byte pixelsPerByte;
    int capacity;
    byte[] header;
    HiddenData secretInfo;

    // Constructor
    BaseSteganography(){}

    // Abstract encoding and decoding and getHeader functions
    public abstract void encode(byte[] message, File output);
    public abstract void encode(File doc, File output);
    public abstract void decode(File file);
    public abstract byte[] getHeader();

    // Capacity getter
    public long getCapacity(){
        return this.capacity;
    }

    // Secret Info getter and setter
    protected HiddenData getSecretInfo(){
        return this.secretInfo;
    }
    void setSecretInfo(HiddenData info){
        this.secretInfo = info;
    }

    // header setter method (string and file)
    byte[] setHeader(byte[] message){
        List<Byte> header = new ArrayList<>();
        header.add((byte)'M');
        header.add((this.isEncrypted) ? (byte)'E' : (byte)'U');
        header.add((this.isCompressed) ? (byte)'C' : (byte)'U');
        header.add(this.pixelsPerByte);
        String messageLength = String.format("%16s", Integer.toBinaryString(message.length)).replace(' ', '0');
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
        header.add((this.isCompressed) ? (byte)'C' : (byte)'U');
        header.add(this.pixelsPerByte);
        String fileLength = String.format("%24s",Long.toBinaryString(file.length())).replace(' ', '0');
        for(int i=0; i<fileLength.length();i+=8){
            header.add((byte)Integer.parseInt(fileLength.substring(i,i+8),2));
        }
        byte[] fileExtension = extension.getBytes(StandardCharsets.UTF_8);
        for(byte b : fileExtension)
            header.add(b);
        header.add(((byte) '!'));
        this.header = Helpers.toByteArray(header);
        return Helpers.toByteArray(header);
    }

}
