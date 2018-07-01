import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
// JPEG Compression (?)
// import javax.imageio.IIOImage;
// import javax.imageio.ImageIO;
// import javax.imageio.ImageWriter;
// import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
// import javax.imageio.stream.ImageOutputStream;
// import java.util.Locale;
// import javax.imageio.ImageWriteParam;

/*
    Class Image:Handles the image and coding .
    Methods:
              set_path(): takes the image path .
              code(): codes the text in the image, this method might be refactored in the futures and divised into two Methods
              the method is still in process
              decode() : decodes the hidden message in image, can be refactored and divided in 3 methods(decode message length, decode file extension, decode secret message)
*/

public class Image {
  //Note : we need to set the image and text paths public so we can access them later for graphical use, like moving the image or saving it.

  String path; //image path
  String ext; // image extension
  int u,v=0;

  //constructor
  public Image(){

  }

    //image path
    public void set_image_path(){
      Scanner scan = new Scanner(System.in);
      System.out.println("Enter the path of the image.\n");
      path = scan.next();
      ext = Helpers.getFileExtension(path);
    }

public void encode() throws IOException{
  try{
    BufferedImage new_img = ImageIO.read(new File(path));
    // First step : encoding file extension
    // & Second step : encoding file size
    BufferedReader infoReader = new BufferedReader(new FileReader("./info")); //reads the text.
    String line;
    int width = new_img.getWidth();
    int i=0,j=0;
    System.out.println("WIDTH:"+new_img.getWidth());
    System.out.println("HEIGHT:"+new_img.getHeight());
    while((line = infoReader.readLine()) != null){
      System.out.println(i+","+j+"\t"+i+","+(width-1-j));
      int[] leftTable = get_pixel(new_img,i,j);
      int[] rightTable = get_pixel(new_img,i, width-1-j);
      //System.out.println(get_pixel(new_img,0,i));
      String b3 = line.substring(0,2);
      String b2 = line.substring(2,4);
      String b1 = line.substring(4,6);
      String b0 = line.substring(6,8);

      // Left pixels
      String alpha1 = (Integer.toBinaryString(leftTable[3]));
      String red1 = String.format("%8s", Integer.toBinaryString(leftTable[2])).replace(" ", "0").substring(0,6)+b3;
      String green1 = String.format("%8s", Integer.toBinaryString(leftTable[1])).replace(" ", "0").substring(0,6)+b2;
      String blue1 = (Integer.toBinaryString(leftTable[0]));
      set_pixel(new_img,i,j,Integer.parseInt(alpha1,2),Integer.parseInt(red1,2),Integer.parseInt(green1,2),Integer.parseInt(blue1,2));
      // Right pixels
      String alpha2 = (Integer.toBinaryString(rightTable[3]));
      String red2 = (Integer.toBinaryString(rightTable[2]));
      String green2 = String.format("%8s", Integer.toBinaryString(rightTable[1])).replace(" ", "0").substring(0,6)+b1;
      String blue2 = String.format("%8s", Integer.toBinaryString(rightTable[0])).replace(" ", "0").substring(0,6)+b0;
      set_pixel(new_img,i,width-1-j,Integer.parseInt(alpha2,2),Integer.parseInt(red2,2),Integer.parseInt(green2,2),Integer.parseInt(blue2,2));

      j++;
   }
   infoReader.close();
   i++;
   j=0;
    // Final step : encoding message from index (1,0) until end of message
    BufferedReader reader = new BufferedReader(new FileReader("./text")); //reads the text.

    //set_pixel(new_img,u,v,Integer.parseInt(count,2),Integer.parseInt(count,2),Integer.parseInt(count,2),Integer.parseInt(count,2));
    while( (line = reader.readLine()) != null){
      System.out.println(i+","+j+"\t"+i+","+(width-1-j));
      int[] leftTable = get_pixel(new_img,i,j);
      int[] rightTable = get_pixel(new_img,i, width-1-j);
      //System.out.println(get_pixel(new_img,0,i));
      String b3 = line.substring(0,2);
      String b2 = line.substring(2,4);
      String b1 = line.substring(4,6);
      String b0 = line.substring(6,8);

      // Left pixels
      String alpha1 = (Integer.toBinaryString(leftTable[3]));
      String red1 = String.format("%8s", Integer.toBinaryString(leftTable[2])).replace(" ", "0").substring(0,6)+b3;
      String green1 = String.format("%8s", Integer.toBinaryString(leftTable[1])).replace(" ", "0").substring(0,6)+b2;
      String blue1 = (Integer.toBinaryString(leftTable[0]));
      set_pixel(new_img,i,j,Integer.parseInt(alpha1,2),Integer.parseInt(red1,2),Integer.parseInt(green1,2),Integer.parseInt(blue1,2));
      // Right pixels
      String alpha2 = (Integer.toBinaryString(rightTable[3]));
      String red2 = (Integer.toBinaryString(rightTable[2]));
      String green2 = String.format("%8s", Integer.toBinaryString(rightTable[1])).replace(" ", "0").substring(0,6).substring(0,6)+b1;
      String blue2 = String.format("%8s", Integer.toBinaryString(rightTable[0])).replace(" ", "0").substring(0,6)+b0;
      set_pixel(new_img,i,width-1-j,Integer.parseInt(alpha2,2),Integer.parseInt(red2,2),Integer.parseInt(green2,2),Integer.parseInt(blue2,2));

      j++;
      if(j>=width-j-1){
        j=0;
        i++;
      }

      //System.out.println(get_pixel(new_img,u,v));
    }
    reader.close();
    File secret_img = new File("encoded."+ext);
    if (ext.equals("jpg")){
      System.out.println("INSIDE JPG IF");
      // float quality = 1f;
      // ImageWriter imgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
      // ImageOutputStream ioStream = ImageIO.createImageOutputStream(secret_img);
      // imgWriter.setOutput( ioStream );

      // JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(Locale.getDefault());
      // jpegParams.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
      // jpegParams.setCompressionQuality( quality );
      // imgWriter.write( null, new IIOImage( new_img, null, null ), jpegParams );
      // ioStream.flush();
      // ioStream.close();
      // imgWriter.dispose();
      ImageIO.write(new_img, "png", secret_img);

    }else{
      ImageIO.write(new_img, ext, secret_img);
    }
    System.out.println("Message hidden inside encoded."+ext);
  } catch(IOException e){
    System.out.println("Image Not Found!");
  }
}
public void decode() throws IOException{
  try{
    BufferedImage secret_img = ImageIO.read(new File(path));
    // First step : decoding file extension in pixels (0,3) to (0,7) ==> 5 caracters
    String file_ext = "";
    String ext_bin="";
    int i=0, j=0;
    int width = secret_img.getWidth();
    while(j<5){
      ext_bin="";
      int [] leftExt = get_pixel(secret_img, i, j);
      int [] rightExt = get_pixel(secret_img, i, width-1-j);
      ext_bin += String.format("%8s", Integer.toBinaryString(leftExt[2])).replace(" ", "0").substring(6,8);
      ext_bin += String.format("%8s", Integer.toBinaryString(leftExt[1])).replace(" ", "0").substring(6,8);
      ext_bin += String.format("%8s", Integer.toBinaryString(rightExt[1])).replace(" ", "0").substring(6,8);
      ext_bin += String.format("%8s", Integer.toBinaryString(rightExt[0])).replace(" ", "0").substring(6,8);
      //System.out.println(ext_bin);
      if (ext_bin.equals("00000000")){
        break;
      }
      file_ext +=(char)Integer.parseInt(ext_bin,2);
      j++;
    }
    String trimmed_file_ext = file_ext.substring(0,j);
    // Second step : decoding file size in pixels (0,0) to (0,2) ==> ~ 16 Mb
    j=5;
    int msg_len = 0;
    String len_bin = "";
    while(j<8){
      int [] leftSize = get_pixel(secret_img, i, j);
      int [] rightSize = get_pixel(secret_img, i, width-1-j);
      len_bin += String.format("%8s", Integer.toBinaryString(leftSize[2])).replace(" ", "0").substring(6,8);
      len_bin += String.format("%8s", Integer.toBinaryString(leftSize[1])).replace(" ", "0").substring(6,8);
      len_bin += String.format("%8s", Integer.toBinaryString(rightSize[1])).replace(" ", "0").substring(6,8);
      len_bin += String.format("%8s", Integer.toBinaryString(rightSize[0])).replace(" ", "0").substring(6,8);
      file_ext += (char) Integer.parseInt(ext_bin,2);
      j++;
    }
    //System.out.println(len_bin);
    msg_len = Integer.parseInt(len_bin,2);
    System.out.println("Hidden Message Length: "+msg_len);
    System.out.println("Hidden File Extension: "+trimmed_file_ext);
    // Final step : decoding message from index (1,0) until end of message
    String secret_msg = "";
    String outputFileName = Helpers.setOutputFilename(trimmed_file_ext);
    File msg = new File(outputFileName);
    BufferedWriter writer = new BufferedWriter(new FileWriter(msg)); //buffer to write to file
    i++;
    j=0;
    int k=msg_len;
    do{
      String char_bin = "";
      int [] leftData = get_pixel(secret_img, i, j);
      int [] rightData = get_pixel(secret_img, i, width-1-j);
      char_bin += String.format("%8s", Integer.toBinaryString(leftData[2])).replace(" ", "0").substring(6,8);
      char_bin += String.format("%8s", Integer.toBinaryString(leftData[1])).replace(" ", "0").substring(6,8);
      char_bin += String.format("%8s", Integer.toBinaryString(rightData[1])).replace(" ", "0").substring(6,8);
      char_bin += String.format("%8s", Integer.toBinaryString(rightData[0])).replace(" ", "0").substring(6,8);
      //System.out.println(char_bin);
      //secret_msg+= (char) Integer.parseInt(char_bin,2);
      writer.append((char) Integer.parseInt(char_bin,2));
      //System.out.println(secret_msg);
      k--;
      //System.out.println(msg_len - k);
      j++;
      if (j >= secret_img.getWidth() - 1 -j){
        i++;
        j=0;
      }
    }while(k>0);
    writer.close();
    //System.out.println("----------\n"+secret_msg+"----------");
    System.out.println("Message saved to file "+outputFileName);

  }catch(IOException e){
    System.out.println("Image Not Found!");
  }
}
public int[] get_pixel(BufferedImage img,int x, int y){
  int i = img.getRGB(y,x);
  //System.out.println(Integer.toBinaryString(i));
  int alpha = ((i & 0xFF000000)>>>24);
  int red = ((i & 0x00FF0000)>>>16);
  int green = ((i & 0x0000FF00)>>>8);
  int blue =  (i & 0x000000FF);
  int[] table_pixel = new int[4];
  table_pixel[3] = alpha;
  table_pixel[2] = red;
  table_pixel[1] = green;
  table_pixel[0] = blue;
  return table_pixel;
}
public void set_pixel(BufferedImage img,int x,int y, int b3,int b2, int b1, int b0){
   int B3 = ((b3 & 0x000000FF)<<24);
   int B2 = ((b2 & 0x000000FF)<<16);
   int B1 = ((b1 & 0x000000FF)<<8);
   int B0 = (b0 & 0x000000FF);
  //String i = b3+b2+b1+b0;
  //long j = Integer.parseInt(i,2);
  int i = (B3 + B2 + B1 + B0);
  img.setRGB(y,x,i);
}

public void choose() throws IOException{
  Message message = new Message();
  int m ;
  do{
    System.out.println("\n  1) Encode Image");
    System.out.println("\n  2) Decode Image");
    System.out.println("\n  0) Quit");
    Scanner scan = new Scanner(System.in);
    m = scan.nextInt();
    if (m == 1) {
      message.choose();
      set_image_path();
      encode();
    }else if (m == 2){
      set_image_path();
      decode();
    }else if (m==0){
      System.out.println("Goodbye!");
    }else{
      System.out.println("Invalid Choice");
    }
  } while(m!=0);
}
}
