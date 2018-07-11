import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class AESEncryption {
    private static SecretKeySpec secretKey;
    private static byte[] key;
 
    public static void setKey(String myKey){
        MessageDigest sha = null;
        try {
            key = myKey.getBytes(Charset.forName("UTF-8"));
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
 
    public static String encrypt(String strToEncrypt, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }catch (Exception e){
            System.out.println("Error while encrypting: " + e.getMessage());
        }
        return null;
    }
    public static File encrypt(File input, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            File output = new File("encrypted."+Helpers.getFileExtension(input));
            FileInputStream fis = new FileInputStream(input);
            byte[] inputBytes = new byte[(int) input.length()];
            fis.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(outputBytes);
            fis.close();
            fos.close();
            return output;
        }catch(Exception e) {
            System.out.println("Error while encrypting: " + e.getMessage());
        }
        return null;
    }
    public static String decrypt(String strToDecrypt, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }catch (Exception e){
            System.out.println("Error while decrypting: " + e.getMessage());
        }
        return null;
    }
    public static File decrypt(File input, String secret){
        try{
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            File output = new File("decrypted_"+input.getName());
            FileInputStream fis = new FileInputStream(input);
            byte[] inputBytes = new byte[(int) input.length()];
            fis.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(outputBytes);
            fis.close();
            fos.close();
            return output;
        }catch(Exception e) {
            System.out.println("Error while decrypting: " + e.getMessage());
        }
        return null;
    }
}
