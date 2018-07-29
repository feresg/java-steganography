package Steganography.Logic;

import Steganography.Modals.AlertBox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * The {@code ZLibCompression} class handles the compression/decompression of a file or byte array
 * to reduce the effect of the embedded data on the steganographic image using the Java ZLib compression/decompression library.
 * <p><code>Deflator</code> is used to compress data and <code>Inflator</code> to decompress data.
 */
public class ZLibCompression {

    /** Size of the buffer used to compress a file by chucks. */
    private static final int BUF_SIZE = 256;

    /**
     * Compresses a given file into another file using <code>Deflator</code> from the ZLib java library.
     *
     * @param raw        file to compress
     * @param compressed file to which compressed data will be written
     */
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

    /**
     * Compresses a given byte array into another file using <code>Deflator</code> from the ZLib java library.
     *
     * @param raw byte array to compress
     * @return    a byte array with the compressed data.
     */
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

    /**
     * Decompresses a given file that was compressed by {@link #compress(File, File)} using <code>Inflator</code>.
     *
     * @param compressed file to be decompressed
     * @param raw        file to which decompressed data will be written
     */
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

    /**
     * Decompresses a given byte array that was compressed by {@link #compress(byte[])} using <code>Inflator</code>.
     *
     * @param compressed byte array to be decrypted
     * @return           a byte array with the decompressed data.
     */
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

    /**
     * Copies an <code>InputStream</code> to an <code>OutputStream</code> in chucks using a buffer
     *
     * @param is           <code>InputStream</code> (source of data)
     * @param os           <code>OutputStream</code> (destination of data)
     * @throws IOException if an error occurs when reading from <code>InputStream</code> or writing to <code>OutputStream</code>.
     */
    private static void copy(InputStream is, OutputStream os) throws IOException{
        byte[] buffer = new byte[BUF_SIZE];
        int length;
        while ((length = is.read(buffer)) > 0)
            os.write(buffer, 0, length);
        is.close();
        os.close();
    }

}
