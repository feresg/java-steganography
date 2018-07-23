package Steganography.Logic;

import Steganography.Modals.AlertBox;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESEncryption {

    private static SecretKeySpec secretKey;

    private final static SecureRandom rnd = new SecureRandom();
    private final static IvParameterSpec iv = new IvParameterSpec(rnd.generateSeed(16));

    private static void setKey(String myKey){
        MessageDigest sha;
        byte[] key = myKey.getBytes(Charset.forName("UTF-8"));
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

    public static String encrypt(String strToEncrypt, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(Charset.forName("UTF-8"))));
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
            FileInputStream fis = new FileInputStream(input);
            byte[] inputBytes = new byte[(int) input.length()];
            fis.read(inputBytes);
            byte[] outputBytes = Base64.getEncoder().encode(cipher.doFinal(inputBytes));
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(outputBytes);
            fis.close();
            fos.close();
        }catch(Exception e) {
            e.printStackTrace();
            AlertBox.error("Error while encrypting", e.getMessage());
        }
    }

    public static String decrypt(String strToDecrypt, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
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
            FileInputStream fis = new FileInputStream(input);
            byte[] inputBytes = new byte[(int) input.length()];
            fis.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(Base64.getDecoder().decode(inputBytes));
            FileOutputStream fos = new FileOutputStream(output);
            for(byte b : outputBytes)
                fos.write(b);
            fis.close();
            fos.close();
        }catch(Exception e) {
            e.printStackTrace();
            output.delete();
            AlertBox.error("Error while decrypting", e.getMessage());
        }
    }

}
