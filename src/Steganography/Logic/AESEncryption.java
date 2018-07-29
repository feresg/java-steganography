package Steganography.Logic;

import Steganography.Modals.AlertBox;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * The {@code AESEncryption} class handles the encryption/decryption operation of either a byte array or a file
 * to ensure more security for the steganographic process using password-based AES encryption.
 * <p>
 * This class uses these default encryption parameters :
 * <ul>
 *      <li>Cipher Algorithm : AES (Advanced Encryption Standard)</li>
 *      <li>Cipher Algorithm Mode : CBC (Cipher Block Chaining)</li>
 *      <li>Cipher Algorithm Padding : PKCS5Padding</li>
 *      <li>Cipher Block Size : 128 bit (16 bytes)</li>
 * </ul>
 */
public class AESEncryption {

    /** Algorithm used to hash the password given by the user. */
    private static final String HASH_ALGORITHM = "SHA-1";
    /** Algorithm used to cipher the data. */
    private static final String CIPHER_ALGORITHM = "AES";
    /** Parameters of the algorithm used to cipher the data. */
    private static final String CIPHER_SPEC = "AES/CBC/PKCS5Padding";
    /** Size of the cipher block (128 bit AES = 16 bytes). */
    private static final int CIPHER_BLOCK_SIZE = 16;
    /** Secret key used to cipher the data. */
    private static SecretKeySpec secretKey;
    /** Needed to generate the Initialisation Vector. */
    private static SecureRandom rnd = new SecureRandom();
    /** Initialisation vector needed by the Cipher Block Chaining encryption mode. */
    private static final IvParameterSpec iv = new IvParameterSpec(rnd.generateSeed(CIPHER_BLOCK_SIZE));
    /** Size of the buffer used to cipher a file by chucks. */
    private static final int BUF_SIZE = 1024;

    /**
     * Hashes the given password using SHA-1 hashing algorithm
     * then uses the first 16 characters of the hashed password
     * to generate the AESEncryption class secret encryption key.
     *
     * @param myKey password given by the user to encrypt the data
     */
    private static void setKey(String myKey){
        MessageDigest sha;
        byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
        try{
            sha = MessageDigest.getInstance(HASH_ALGORITHM);
            key = sha.digest(key);
            key = Arrays.copyOf(key, CIPHER_BLOCK_SIZE);
            secretKey = new SecretKeySpec(key, CIPHER_ALGORITHM);
        }catch (GeneralSecurityException e){
            e.printStackTrace();
            AlertBox.error("Error while setting key", e.getMessage());
        }
    }

    /**
     * Encrypts a given byte array using AES encryption.
     *
     * @param input byte array to encrypt
     * @param myKey encryption password
     * @return      a byte array with the encrypted data.
     */
    public static byte[] encrypt(byte[] input, String myKey){
        try{
            setKey(myKey);
            Cipher cipher = Cipher.getInstance(CIPHER_SPEC);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return cipher.doFinal(input);
        }catch (GeneralSecurityException e){
            e.printStackTrace();
            AlertBox.error("Error while encrypting", e.getMessage());
        }
        return null;
    }

    /**
     * Encrypts a given file into another file using AES encryption.
     *
     * @param input  file to encrypt
     * @param output file to which with encrypted data will be written
     * @param myKey  encryption password
     */
    public static void encrypt(File input, File output, String myKey){
        try{
            setKey(myKey);
            Cipher cipher = Cipher.getInstance(CIPHER_SPEC);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            process(cipher, input, output);
        }catch(IOException | GeneralSecurityException e) {
            e.printStackTrace();
            AlertBox.error("Error while decrypting", e.getMessage());
        }
    }

    /**
     * Decrypts a given byte array that was encrypted by {@link #encrypt(byte[], String)}.
     *
     * @param input encrypted byte array to be decrypted
     * @param myKey encryption password
     * @return      a byte array with the decrypted data.
     */
    public static byte[] decrypt(byte[] input, String myKey){
        try{
            setKey(myKey);
            Cipher cipher = Cipher.getInstance(CIPHER_SPEC);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return cipher.doFinal(input);
        }catch (GeneralSecurityException e){
            e.printStackTrace();
            AlertBox.error("Error while decrypting", e.getMessage());
        }
        return null;
    }

    /**
     * Decrypts a given file that was encrypted by {@link #encrypt(File, File, String)}.
     *
     * @param input  encrypted file to be decrypted
     * @param output file to which decrypted data will be written to
     * @param myKey  encryption password
     */
    public static void decrypt(File input, File output, String myKey){
        try{
            setKey(myKey);
            Cipher cipher = Cipher.getInstance(CIPHER_SPEC);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            process(cipher, input, output);
        }catch(IOException | GeneralSecurityException e) {
            e.printStackTrace();
            output.delete();
            AlertBox.error("Error while decrypting", e.getMessage());
        }
    }

    /**
     * Ciphers a file into another file in chunks using a buffer for reading the file.
     *
     * @param ci                        cryptographic cipher for encryption or decryption
     * @param input                     file before cryptographic operation
     * @param output                    file after cryptographic operation
     * @throws IOException              if an error occurs when reading or writing the file streams.
     * @throws GeneralSecurityException if an error is related to the cipher operation (ex : wrong encryption password).
     */
    private static void process(Cipher ci, File input, File output) throws IOException, GeneralSecurityException{
        try (FileInputStream fis = new FileInputStream(input);
             FileOutputStream fos = new FileOutputStream(output)) {
                byte[] ibuf = new byte[BUF_SIZE];
                int len;
                while ((len = fis.read(ibuf)) != -1) {
                    byte[] obuf = ci.update(ibuf, 0, len);
                    if ( obuf != null ) fos.write(obuf);
                }
            byte[] obuf = ci.doFinal();
            if ( obuf != null ) fos.write(obuf);
        }
    }

}
