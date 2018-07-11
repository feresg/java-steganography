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
    public abstract void encode(String str) throws IOException;
    public abstract void encode(File doc) throws IOException;
    public abstract void decode() throws IOException;
    // header setter method (string and file)
    public byte[] setHeader(String message){
        List<Byte> header = new ArrayList<Byte>();
        header.add((byte)'M');
        header.add((this.isEncrypted) ? (byte)'E' : (byte)'U');
        String messageLength = String.format("%16s", message.length()).replace(' ', '0');
        for(int i=0; i<messageLength.length();i+=8){
            header.add(Byte.parseByte(messageLength.substring(i,i+8),2));
        }
        header.add(((byte) '!'));
        return Helpers.toByteArray(header);
    }
    public byte[] setHeader(File file) throws IOException{
        List<Byte> header = new ArrayList<Byte>();
        String extension = Helpers.getFileExtension(file).toLowerCase();
        header.add((byte)'D');
        header.add((this.isEncrypted) ? (byte)'E' : (byte)'U');
        String fileLength = String.format("%24s", file.length()).replace(' ', '0');
        for(int i=0; i<fileLength.length();i+=8)
            header.add(Byte.parseByte(fileLength.substring(i,i+8),2));
        byte[] fileExtension = extension.getBytes(Charset.forName("UTF-8"));
        for(byte b : fileExtension)
            header.add(b);
        header.add(((byte) '!'));
        return Helpers.toByteArray(header);
    }
    public Map<String, String> getAttributes(byte[] header){
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("mode", Byte.toString(header[0]));
        attributes.put("encryption", Byte.toString(header[1]));
        int length = 0;
        String extension = "";
        switch(attributes.get("mode")){
            case "M" :
                length = Integer.parseInt(String.format("%8s",Integer.toBinaryString(header[2])).replace(' ','0')+String.format("%8s",Integer.toBinaryString(header[3])).replace(' ','0'), 2);
                extension = "txt";
                break;
            case "D" :
                length = Integer.parseInt(String.format("%8s",Integer.toBinaryString(header[2])).replace(' ','0')+String.format("%8s",Integer.toBinaryString(header[3])).replace(' ','0')+String.format("%8s",Integer.toBinaryString(header[4])).replace(' ','0'), 2);
                for(int l=5; l<header.length-1;l++)
                    extension += (char) header[l];
                break;
            default :
                break;
        }
        attributes.put("length", String.valueOf(length));
        attributes.put("extension", extension);
        return attributes;
    }
    // Capacity and Encryption status and Header getters
    public boolean getEncryptionStatus(){
        return this.isEncrypted;
    }
    public long getCapacity(){
        return this.capacity;
    }
}