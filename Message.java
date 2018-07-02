import java.io.File;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner ;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

/* Class Message is responsible for handling users input and processing the text.
   Methods: set_input(): takes text input from user.
            convert(): converts the text to binary and store it in a file.

*/

public class Message {
  String msg; //Holds input.
  String filepath;//Hold filepath

  public String filename= "./text";//filename we write to.
  public File text;

  //constructor.
  public Message(){
    msg = "";

  }

//convert the text input to a binary file.
  public void convert_input() throws IOException{
    text = new File(filename);
    BufferedWriter writer = new BufferedWriter(new FileWriter("text")); //buffer to write to file
    BufferedWriter infoWriter = new BufferedWriter(new FileWriter("info")); // buffer to write to info file
      // Adding to info file extension (plain text doc => no extension)
      for(int i=0; i<5; i++){
        infoWriter.append("00000000");
        infoWriter.newLine();
      }
    // Adding to text file message size (in 24 bits)
    String msg_length = String.format("%24s", Integer.toBinaryString(msg.length())).replace(' ', '0');
    for (int i=0; i<24; i+=8){
      infoWriter.append(msg_length.substring(i, i+8));
      infoWriter.newLine();
    }
    infoWriter.close();

    byte[] bin = null;
    bin = msg.getBytes("UTF-8");//converts the string to Bytes
    for (byte b : bin ) {
      String x = Integer.toBinaryString(b);
      writer.append(String.format("%08d",Integer.parseInt(x))+'\n'); //format charachter to 8bits
    }
    writer.close();
  }

//convert the document input to a binary file
  public void convert_document() throws IOException{
    try {
      InputStreamReader streamReader = new InputStreamReader(new FileInputStream(filepath));

      BufferedReader reader = new BufferedReader(streamReader);//reads the user file

      BufferedWriter textWriter = new BufferedWriter(new FileWriter("text")); //buffer to write to file

      BufferedWriter infoWriter = new BufferedWriter(new FileWriter("info")); // buffer to write to info file

      // Adding to info file extension (plain text doc => no extension)
      String file_ext = Helpers.getFileExtension(filepath);
      byte [] ext = null;
      ext = file_ext.getBytes();
      for (byte e:ext){
        String x = Integer.toBinaryString(e);
        infoWriter.append(String.format("%08d",Integer.parseInt(x)));//formats to 8bits
        infoWriter.newLine();
      }
      for(int i= ext.length; i<5; i++){
        infoWriter.append("00000000");
        infoWriter.newLine();
      }
      File doc = new File(filepath);
      // Adding to text file message size (in 24 bits)
      String file_length = String.format("%24s", Long.toBinaryString(doc.length())).replace(' ', '0');
      for (int i=0; i<24; i+=8){
        infoWriter.append(file_length.substring(i, i+8));
        infoWriter.newLine();
      }
      infoWriter.close();
      // Encoding the message
      String line;
      while((line=reader.readLine())!=null){
        //System.out.println(line);
        byte[] bin =null;
        bin = line.getBytes("UTF-8");
            for (byte b : bin ) {
              //System.out.println(Integer.toBinaryString(b));
          String x = Integer.toBinaryString(b);
          textWriter.append(String.format("%8s",x).replace(' ', '0'));//formats to 8bits
          textWriter.newLine();
        }
        textWriter.append("00001010");
        textWriter.newLine();
      }
      reader.close();
      textWriter.close();
    }catch (IOException e) {
      System.out.println("File not found!");
    }

  }
  public void set_input(){
    System.out.println("\n Enter the message you wish to hide:\n     ");
    Scanner scan = new Scanner(System.in);
    msg = scan.nextLine();
  }
  public void set_document (){
    System.out.println("\n Enter the file path: ");
      Scanner scan = new Scanner(System.in);
      filepath = scan.nextLine();
  }
  public void choose() throws IOException{
    int m ;
    System.out.println("\n  1) Enter text");
    System.out.println("\n  2) Enter a file");
    Scanner scan = new Scanner(System.in);
    m = scan.nextInt();
    if (m == 1) {
      set_input();
      convert_input();
    }else if (m == 2){
      set_document();
      convert_document();

    }else{
      System.out.println("Invalid choice!");
    }
  }

}
