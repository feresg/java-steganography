import java.io.File;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.IOException;

import java.nio.charset.Charset;

/* Class Message is responsible for handling users input and processing the text.
   Methods: set_input(): takes text input from user.
            convert(): converts the text to binary and store it in a file.

*/

public class Message {

  public String filename= "./text";//filename we write to.
  public File text;

  //constructor.
  public Message(){
  }

//convert the text input to a binary file.
  public void convert_input(String msg) throws IOException{
    text = new File(filename);
    BufferedWriter writer = new BufferedWriter(new FileWriter("text")); //buffer to write to file
      // Adding to info file extension (plain text doc => no extension)
      writer.append("00000000");
      writer.newLine();
    // Adding to text file message size (in 24 bits)
    String msg_length = String.format("%24s", Integer.toBinaryString(msg.length())).replace(' ', '0');
    for (int i=0; i<24; i+=8){
      writer.append(msg_length.substring(i, i+8));
      writer.newLine();
    }
    byte[] bin = null;
    bin = msg.getBytes(Charset.forName("UTF-8"));//converts the string to Bytes
    for (byte b : bin ) {
      String x = Integer.toBinaryString(b);
      String y = String.format("%8s",x).replace(' ','0');
      writer.append(y.substring(y.length()-8,y.length()));//formats to 8bits
      writer.newLine();
}
    writer.close();
  }

//convert the document input to a binary file
  public void convert_document(File file) throws IOException{
    try {
      InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file));

      BufferedReader reader = new BufferedReader(streamReader);//reads the user file

      BufferedWriter writer = new BufferedWriter(new FileWriter("text")); //buffer to write to file

      // Adding to info file extension (plain text doc => no extension)
      String file_ext = Helpers.getFileExtension(file);
      byte [] ext = null;
      ext = file_ext.getBytes(Charset.forName("UTF-8"));
      for (byte e:ext){
        String x = Integer.toBinaryString(e);
        writer.append(String.format("%08d",Integer.parseInt(x)));//formats to 8bits
        writer.newLine();
      }
      writer.append("00000000");
      writer.newLine();
      // Adding to text file message size (in 24 bits)
      String file_length = String.format("%24s", Long.toBinaryString(file.length())).replace(' ', '0');
      for (int i=0; i<24; i+=8){
        writer.append(file_length.substring(i, i+8));
        writer.newLine();
      }
      // Encoding the message
      String line;
      while((line=reader.readLine())!=null){
        byte[] bin =null;
        bin = line.getBytes(Charset.forName("UTF-8"));
            for (byte b : bin ) {
              //System.out.println(Integer.toBinaryString(b));
              String x = Integer.toBinaryString(b);
              String y = String.format("%8s",x).replace(' ','0');
              writer.append(y.substring(y.length()-8,y.length()));//formats to 8bits
              writer.newLine();
            }
        writer.append("00001010");
        writer.newLine();
      }
      reader.close();
      writer.close();
    }catch (IOException e) {
      System.out.println(e.getMessage());
    }

  }
  public String set_input(){
    String msg = "";
    System.out.println("\nEnter the message you wish to hide :\n");
    Scanner scan = new Scanner(System.in);
    msg = scan.nextLine();
    return msg;
  }
  public String set_document (){
    String filepath;
    System.out.println("\nEnter the file path :\n");
    Scanner scan = new Scanner(System.in);
    filepath = scan.nextLine();
    return filepath;
  }
  // public void choose() throws IOException{
  //   int m ;
  //   System.out.println("\n  1) Enter text");
  //   System.out.println("\n  2) Enter a file");
  //   Scanner scan = new Scanner(System.in);
  //   m = scan.nextInt();
  //   if (m == 1) {
  //     set_input();
  //     convert_input();
  //   }else if (m == 2){
  //     set_document();
  //     convert_document();

  //   }else{
  //     System.out.println("Invalid choice!");
  //   }
  // }

}
