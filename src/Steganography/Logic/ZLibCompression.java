package Steganography.Logic;

import Steganography.Modals.AlertBox;

import java.io.*;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZLibCompression {

    public static void compress(File raw, File compressed){
        try{
            InputStream is = new FileInputStream(raw);
            OutputStream os = new DeflaterOutputStream(new FileOutputStream(compressed));
            copy(is, os);

        }catch (IOException e) {
            e.printStackTrace();
            AlertBox.error("Error while compressing", e.getMessage());
        }
    }
    public static byte[] compress(byte [] raw){
        InputStream is = new ByteArrayInputStream(raw);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = new DeflaterOutputStream(baos);
        try{
            copy(is, os);
            return baos.toByteArray();

        }catch (IOException e){
            e.printStackTrace();
            AlertBox.error("Error while compressing", e.getMessage());
            return null;
        }
    }

    public static void decompress(File compressed, File raw){
        try{
            InputStream is = new InflaterInputStream(new FileInputStream(compressed));
            OutputStream os = new FileOutputStream(raw);
            copy(is, os);
        }catch (IOException e){
            e.printStackTrace();
            AlertBox.error("Error while decompressing", e.getMessage());
        }
    }
    public static byte[] decompress(byte[] compressed){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        InputStream is = new InflaterInputStream(new ByteArrayInputStream(compressed));
        try {
            copy(is, os);
            return baos.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
            AlertBox.error("Error while decompressing", e.getMessage());
            return null;
        }

    }

    private static void copy(InputStream is, OutputStream os) throws IOException{
        byte[] buffer = new byte[256];
        int length;
        while ((length = is.read(buffer)) > 0){
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
    }

}
