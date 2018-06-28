import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner ;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;

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
    // First step : encoding file size in pixels (0,0) to (0,2) ==> ~ 16 Mb
    //& Second step : encoding file extension in pixels (0,3) to (0,8) ==> .+5 caracters
    BufferedReader infoReader = new BufferedReader(new FileReader("./info")); //reads the text.
    String line;
    int i=0;
    while((line = infoReader.readLine()) != null){
      System.out.println("0,"+i);
      int[] table = get_pixel(new_img,0,i);
      //System.out.println(get_pixel(new_img,0,i));
      String b3 = line.substring(0,2);
      String b2 = line.substring(2,4);
      String b1 = line.substring(4,6);
      String b0 = line.substring(6,8);
      
      String alpha = (Integer.toBinaryString(table[3])).substring(0,6)+b3;
      String red = (Integer.toBinaryString(table[2])).substring(0,6)+b2;
      String green = (Integer.toBinaryString(table[1])).substring(0,6)+b1;
      String blue = (Integer.toBinaryString(table[0])).substring(0,6)+b0;
      set_pixel(new_img,0,i,Integer.parseInt(alpha,2),Integer.parseInt(red,2),Integer.parseInt(green,2),Integer.parseInt(blue,2));

      i++;
   }
   infoReader.close();
    // Final step : encoding message from index (1,0) until end of message
    BufferedReader reader = new BufferedReader(new FileReader("./text")); //reads the text.
    int u=1, v=0;
    System.out.println("WIDTH:"+new_img.getWidth());
    System.out.println("HEIGHT:"+new_img.getHeight());

    //set_pixel(new_img,u,v,Integer.parseInt(count,2),Integer.parseInt(count,2),Integer.parseInt(count,2),Integer.parseInt(count,2));
    while( (line = reader.readLine()) != null){
      System.out.println(u+","+v);
       int[] table = get_pixel(new_img,u,v);
       //System.out.println(get_pixel(new_img,u,v));
       String b3 = line.substring(0,2);
       String b2 = line.substring(2,4);
       String b1 = line.substring(4,6);
       String b0 = line.substring(6,8);
       String alpha = String.format("%8s", Integer.toBinaryString(table[3])).replace(' ', '0').substring(0,6)+b3;
       String red = String.format("%8s", Integer.toBinaryString(table[2])).replace(' ', '0').substring(0,6)+b2;
       String green = String.format("%8s", Integer.toBinaryString(table[1])).replace(' ', '0').substring(0,6)+b1;
       String blue = String.format("%8s", Integer.toBinaryString(table[0])).replace(' ', '0').substring(0,6)+b0;

      set_pixel(new_img,u,v,Integer.parseInt(alpha,2),Integer.parseInt(red,2),Integer.parseInt(green,2),Integer.parseInt(blue,2));
      v++;
      if (v > new_img.getWidth()-1 ){
        u++;
        v=0;
      }
      //System.out.println(get_pixel(new_img,u,v));
    }
    reader.close();
    File secret_img = new File("encoded."+ext);
    ImageIO.write(new_img, ext, secret_img);
    System.out.println("Message hidden inside encoded."+ext);
  } catch(IOException e){
    System.out.println("Image Not Found!");
  }
}
public void decode() throws IOException{
  try{
    BufferedImage secret_img = ImageIO.read(new File(path));
    // First step : decoding file size in pixels (0,0) to (0,2) ==> ~ 16 Mb
    int msg_len = 0;
    String len_bin = "";
    int k=0;
    while(k<3){
      int [] size = get_pixel(secret_img, 0, k);
      for(int i=size.length-1; i>=0; i--){
        len_bin += Integer.toBinaryString(size[i]).substring(6,8);
      }
      k++;
    }
    System.out.println(len_bin);
    msg_len = Integer.parseInt(len_bin,2);
    System.out.println("Hidden Message Length: "+msg_len);
    // Second step : decoding file extension in pixels (0,3) to (0,7) ==> 5 caracters
    String file_ext = "";
    String ext_bin="";
    while(!(ext_bin.equals("00000000"))){
      ext_bin="";
      int [] ext = get_pixel(secret_img, 0, k);
      for(int i=ext.length-1; i>=0; i--){
        ext_bin += Integer.toBinaryString(ext[i]).substring(6,8);
      }
      if (ext_bin.equals("00000000"))
        break;
      file_ext += (char) Integer.parseInt(ext_bin,2);
      k++;
    }
    System.out.println("Hidden File Extension: "+file_ext);
    // Final step : decoding message from index (1,0) until end of message
    String secret_msg = "";
    File msg = new File("secret"+((ext !="")? "." : "")+file_ext);
    BufferedWriter writer = new BufferedWriter(new FileWriter(msg)); //buffer to write to file
    int i = 1,j=0;
    k=msg_len;
    do{
      String char_bin = "";
      int [] data = get_pixel(secret_img, i, j);
      for(int l=data.length-1; l>=0; l--){
        System.out.println(String.format("%8s", Integer.toBinaryString(data[l])).replace(' ', '0'));
        char_bin += String.format("%8s", Integer.toBinaryString(data[l])).replace(' ', '0').substring(6,8);
      }
      //System.out.println(char_bin);
      secret_msg+= (char) Integer.parseInt(char_bin,2);
      writer.append((char) Integer.parseInt(char_bin,2));
      //System.out.println(secret_msg);
      k--;
      System.out.println(msg_len - k);
      j++;
      if (j > secret_img.getWidth() - 1){
        i++;
        j=0;
      }
    }while(k>0);
    writer.close();
    System.out.println(secret_msg);

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
public void set_pixel(BufferedImage img,int x,int y ,int b3 ,int b2, int b1, int b0){
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
