/* Project: Steganography
   Description: Hide a text or an image inside an image.
   Authors: Ilyes Hamrouni, Feres Gaaloul.
   Usage: Temporarily, use terminal, "java Index".
   Help and Guide: see comments.
*/

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Index
{
  private static final String MAIN_MESSAGE = "\n*** Steganography Application ***";
  private static final String MAIN_OPTIONS = "\n\t1/ Encode Image\n\t2/ Decode Image\n\t0/ Quit";
  private static final String ENCODE_OPTIONS = "\n\t1/ Encode Input\n\t2/ Encode Document\n\t3/ Encode Image";
  private static final String ENCRYPT_OPTIONS = "\nAdd Encryption? [1/0]";
  private static final String ENCRYPT_MESSAGE = "\nEnter Encryption Password :\n";
  private static final String CHOICE_ERROR = "Invalid Choice!";
  private static final String EXIT_MESSAGE = "Goodbye!";

  private static final Scanner scan = new Scanner(System.in);

  public static void main(String[] args){
    int mainOpt;
    System.out.println(MAIN_MESSAGE);
    do{
      System.out.println(MAIN_OPTIONS);
      mainOpt = scan.nextInt();
      switch(mainOpt){
        case 1:
          encoding();
          break;
        case 2:
          decoding();
          break;
        case 0:
          System.out.println(EXIT_MESSAGE);
          break;
        default:
          System.out.println(CHOICE_ERROR);
          break;
      }
    }while(mainOpt != 0);
    scan.nextLine();
  }
  private static void encoding(){
    try{
      int encodeOpt;
      do{
        System.out.println(ENCODE_OPTIONS);
        encodeOpt = scan.nextInt();
        switch(encodeOpt){
          case 1:
            encodingInput();
            break;
          case 2:
            encodingDocument();
            break;
          case 3:
            break;
          default:
            System.out.println(CHOICE_ERROR);
            break;
        }
      }while(encodeOpt < 1 && encodeOpt > 2);
      applyEncoding();
    }catch(Exception e) {System.out.println("Image Encoding Error : "+ e.getMessage());}
  }
  private static void decoding(){
    try{
      Image image = new Image();
      image.set_image_path();
      if(image.ext.equals("gif")){
        image.decode_gif();
      }else{
        image.decode();
      }
    }catch(Exception e) {System.out.println("Image Decoding Error : "+ e.getMessage());}
  }
  private static String encryption(){
    int encryptOpt;
    String password;
    System.out.println(ENCRYPT_OPTIONS);
    encryptOpt = scan.nextInt();
    scan.nextLine();
    if(encryptOpt == 1){
      System.out.println(ENCRYPT_MESSAGE);
      password = scan.nextLine();
      return password;
    }
    return null;
  }
  private static void encodingInput() throws IOException{
    Message message = new Message();
    String msg;
    String password;
    msg = message.set_input();
    password = encryption();
    if(password != null){
      msg = AESEncryption.encrypt(msg, password);
    }
    message.convert_input(msg);
  }
  private static void encodingDocument() throws IOException{
    Message message = new Message();
    String filepath = message.set_document();
    String password;
    File input = new File(filepath);
    password = encryption();
    if(password != null){
      File encFile = AESEncryption.encrypt(input, password);
      message.convert_document(encFile);
      encFile.delete();
    }else{
      message.convert_document(input);
    }
  }
  private static void applyEncoding() throws IOException{
    Image image = new Image();
    image.set_image_path();
    if(image.ext.equals("gif")){
      image.encode_gif();
    }else{
      image.encode();
    }
    File text = new File("./text");
    text.delete();
  }
}
