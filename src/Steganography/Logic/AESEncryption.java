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

public class AESEncryption {

    private static SecretKeySpec secretKey;

    private final static SecureRandom rnd = new SecureRandom();
    private final static IvParameterSpec iv = new IvParameterSpec(rnd.generateSeed(16));

    private static void setKey(String myKey){
        MessageDigest sha;
        byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
        try{
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }catch (GeneralSecurityException e){
            e.printStackTrace();
            AlertBox.error("Error while setting key", e.getMessage());
        }
    }

    public static byte[] encrypt(byte[] input, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return cipher.doFinal(input);
        }catch (GeneralSecurityException e){
            e.printStackTrace();
            AlertBox.error("Error while encrypting", e.getMessage());
        }
        return null;
    }

    public static void encrypt(File input, File output, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            process(cipher, input, output);
        }catch(IOException | GeneralSecurityException e) {
            e.printStackTrace();
            AlertBox.error("Error while decrypting", e.getMessage());
        }
    }

    public static byte[] decrypt(byte[] input, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return cipher.doFinal(input);
        }catch (GeneralSecurityException e){
            e.printStackTrace();
            AlertBox.error("Error while decrypting", e.getMessage());
        }
        return null;
    }

    public static void decrypt(File input, File output, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            process(cipher, input, output);
        }catch(IOException | GeneralSecurityException e) {
            e.printStackTrace();
            output.delete();
            AlertBox.error("Error while decrypting", e.getMessage());
        }
    }

    private static void process(Cipher ci, File input, File output) throws IOException, GeneralSecurityException{
        try (FileInputStream fis = new FileInputStream(input);
             FileOutputStream fos = new FileOutputStream(output)) {
                byte[] ibuf = new byte[1024];
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
