import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageOutputStream;

import gif.*;


/*
    Class Image:Handles the image and coding .
    Methods:
              set_path(): takes the image path .
              encode(): codes the text in the image, this method might be refactored in the futures and divised into two Methods
              the method is still in process
              decode() : decodes the hidden message in image, can be refactored and divided in 3 methods(decode message length, decode file extension, decode secret message)
*/

public class Image {
  //Note : we need to set the image and text paths public so we can access them later for graphical use, like moving the image or saving it.

  String path; //image path
  String ext; // image extension

  //constructor
  public Image(){

  }
  //image path
  public void set_image_path(){
    Scanner scan = new Scanner(System.in);
    System.out.print("Enter the path of the image :\t");
    path = scan.next();
    ext = Helpers.getFileExtension(path);
  }

  public void encode() throws IOException{
    try{
      BufferedImage new_img = ImageIO.read(new File(path));
      System.out.println(new_img.getColorModel());

      // First step : encoding file extension
      // & Second step : encoding file size
      // Final step : encoding message from index (1,0) until end of message
      BufferedReader reader = new BufferedReader(new FileReader("./text")); //reads the text.
      String line;
      int width = new_img.getWidth();
      int i=0,j=0;
      System.out.println("WIDTH:"+new_img.getWidth());
      System.out.println("HEIGHT:"+new_img.getHeight());
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
        String green2 = String.format("%8s", Integer.toBinaryString(rightTable[1])).replace(" ", "0").substring(0,6)+b1;
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
      if (ext.equals("jpg")){
        ext = "png";
      }
      File secret_img = new File("encoded."+ext);
      ImageIO.write(new_img, ext, secret_img);
      System.out.println("Message hidden inside encoded."+ext);
    } catch(IOException e){
      System.out.println("Image Not Found!");
      throw e;
    }
  }
  public void decode() throws IOException{
    try{
      BufferedImage secret_img = ImageIO.read(new File(path));
      String file_ext = "";
      String ext_bin="";
      int i=0, j=0;
      int width = secret_img.getWidth();
      do{
        ext_bin="";
        int [] leftExt = get_pixel(secret_img, i, j);
        int [] rightExt = get_pixel(secret_img, i, width-1-j);
        ext_bin += String.format("%8s", Integer.toBinaryString(leftExt[2])).replace(" ", "0").substring(6,8);
        ext_bin += String.format("%8s", Integer.toBinaryString(leftExt[1])).replace(" ", "0").substring(6,8);
        ext_bin += String.format("%8s", Integer.toBinaryString(rightExt[1])).replace(" ", "0").substring(6,8);
        ext_bin += String.format("%8s", Integer.toBinaryString(rightExt[0])).replace(" ", "0").substring(6,8);
        System.out.println(ext_bin);
        //System.out.println(ext_bin);
        if (!ext_bin.equals("00000000")){
          file_ext +=(char)Integer.parseInt(ext_bin,2);
        }
        j++;
      }while(!ext_bin.equals("00000000"));
      int cur_pos = j;
      int msg_len = 0;
      String len_bin = "";
      while(j<cur_pos+3){
        int [] leftSize = get_pixel(secret_img, i, j);
        int [] rightSize = get_pixel(secret_img, i, width-1-j);
        len_bin += String.format("%8s", Integer.toBinaryString(leftSize[2])).replace(" ", "0").substring(6,8);
        len_bin += String.format("%8s", Integer.toBinaryString(leftSize[1])).replace(" ", "0").substring(6,8);
        len_bin += String.format("%8s", Integer.toBinaryString(rightSize[1])).replace(" ", "0").substring(6,8);
        len_bin += String.format("%8s", Integer.toBinaryString(rightSize[0])).replace(" ", "0").substring(6,8);
        j++;
      }
      //System.out.println(len_bin);
      msg_len = Integer.parseInt(len_bin,2);
      System.out.println("Hidden Message Length: "+msg_len);
      System.out.println("Hidden File Extension: "+file_ext);
      // Final step : decoding message
      String outputFileName = Helpers.setOutputFilename(file_ext);
      File msg = new File(outputFileName);
      FileOutputStream fos = new FileOutputStream(msg);
      int k=0;
      do{
        String char_bin = "";
        int [] leftData = get_pixel(secret_img, i, j);
        int [] rightData = get_pixel(secret_img, i, width-1-j);
        char_bin += String.format("%8s", Integer.toBinaryString(leftData[2])).replace(" ", "0").substring(6,8);
        char_bin += String.format("%8s", Integer.toBinaryString(leftData[1])).replace(" ", "0").substring(6,8);
        char_bin += String.format("%8s", Integer.toBinaryString(rightData[1])).replace(" ", "0").substring(6,8);
        char_bin += String.format("%8s", Integer.toBinaryString(rightData[0])).replace(" ", "0").substring(6,8);
        //System.out.println(char_bin);
        //writer.append((char) Integer.parseInt(char_bin,2));
        //secret_msg += (char) Integer.parseInt(char_bin,2);
        //System.out.println(secret_msg);
        fos.write((byte) Integer.parseInt(char_bin, 2));
        k++;
        //System.out.println(msg_len - k);
        j++;
        if (j >= secret_img.getWidth() - 1 -j){
          i++;
          j=0;
        }
      }while(k<msg_len);
      fos.close();
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

  // public void choose() throws IOException{
  //   Message message = new Message();
  //   int m ;
  //   do{
  //     System.out.println("\n  1) Encode Image");
  //     System.out.println("\n  2) Decode Image");
  //     System.out.println("\n  0) Quit");
  //     Scanner scan = new Scanner(System.in);
  //     m = scan.nextInt();
  //     scan.close();
  //     if (m == 1) {
  //       message.choose();
  //       set_image_path();
  //       if(ext.equals("gif")){
  //         System.out.println("GIF ENCODING");
  //         encode_gif();
  //       }else{
  //         encode();
  //       }
  //       File text = new File("./text");
  //       text.delete();
  //     }else if (m == 2){
  //       set_image_path();
  //       if(ext.equals("gif")){
  //         System.out.println("DECODE GIF");
  //         decode_gif();
  //       }else{
  //         decode();
  //       }
  //     }else if (m==0){
  //       System.out.println("Goodbye!");
  //     }else{
  //       System.out.println("Invalid Choice");
  //     }
  //   } while(m!=0);
  // }
  /**
   * Steps :
   *  1/ Turn gif into array of BufferedImages
   *  2/ Apply steganography algorithm
   *  2/ Save array of BufferedImages to new gif
   */
  public void encode_gif() throws IOException{
    try{
      BufferedReader reader = new BufferedReader(new FileReader("./text")); //reads the text.
      File input_img = new File(path);
      // Metadata.readAndDisplayMetadata(path,0);
      // Metadata.readAndDisplayMetadata(path,1);

      BufferedImage[] frames = Metadata.getFrames(input_img);
      int delay = Metadata.getDelayMS(input_img);
      IIOMetadata[] metadatas = new IIOMetadata[frames.length];
      for(int x=0;x<frames.length;x++){
        metadatas[x] = Metadata.getImageMetadata(input_img, x);
      }
      String line;
      int index = 0;
      int i=0,j=0;
      int width,height;

       while((line = reader.readLine())!= null){
        System.out.println(line);
        width = frames[index].getWidth();
        height = frames[index].getHeight();
        System.out.println("Frame "+ index+"\t"+i+","+j+"..."+(j+7));
        WritableRaster raster = frames[index].getRaster();
        int array_pixels [] = new int [4];
        for (int l=0;l<8;l++){
          //System.out.println(i +","+(j+l));
          raster.getPixel(j+l,i,array_pixels);
          String current_pixel = String.format("%8s", Integer.toBinaryString(array_pixels[0])).replace(' ','0');
          //System.out.println("BEFORE : "+current_pixel);
          String new_pixel = current_pixel.substring(0,7)+line.charAt(l);
          //System.out.println("AFTER : "+new_pixel);
          array_pixels[0] = Integer.parseInt(new_pixel, 2);
          raster.setPixel(j+l,i,array_pixels);
        }
        j+=8;
        if(j>width-8){
          j=0;
          i++;
        }
        if(i > height - 1){
          i=0;
          j=0;
          index++;
        }
      }
      reader.close();
     //Turns gif into frames (encoded0.png , encoded1.png ...)
      // for (int z=0; z<frames.length;z++){
      //   System.out.println(frames[z].getColorModel());
      //   File secret_img = new File("encoded"+z+".png");
      //   ImageIO.write(frames[z], "png", secret_img);
      // }
      // Saves gif from bufferedimages array (needed tweaking of class GifSequenceWriter)
      File encoded_img = new File("encoded.gif");
      ImageOutputStream output = new FileImageOutputStream(encoded_img);

      ColorModel cm = frames[0].getColorModel();
      ImageTypeSpecifier imageType = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
      GifSequenceWriter writer = new GifSequenceWriter(output, imageType,delay, true);
      for(int k=0; k<frames.length; k++) {
        writer.writeToSequence(frames[k], metadatas[k]);
      }
      writer.close();
      output.close();
      System.out.println("Message hidden inside encoded.gif");


    }catch(IOException e){
      System.out.println(e.getMessage());
    }
  }
  
  public void decode_gif() throws IOException{
    try{
      BufferedImage[] frames = Metadata.getFrames(new File(path));
      int i=0, j=0;
      int index = 0;
      int width;
      int height;
      int array_pixels [] = new int [4];
      // Decoding the hidden file extension
      String file_ext = "";
      String ext_bin="";
      do{
        width = frames[index].getWidth();
        height = frames[index].getHeight();
        Raster raster = frames[index].getRaster();
        ext_bin="";
        for (int l=0;l<8;l++){
          raster.getPixel(j+l,i,array_pixels);
          ext_bin += String.format("%8s", Integer.toBinaryString(array_pixels[0])).replace(" ", "0").charAt(7);
        }
        //System.out.println(ext_bin);
        if (!ext_bin.equals("00000000")){
          file_ext +=(char)Integer.parseInt(ext_bin,2);
        }
        j+=8;
        if (j >= width-8){
          j=0;
          i++;
        }
        if(i > height - 1){
          i=0;
          j=0;
          index++;
        }
      }while(!ext_bin.equals("00000000"));
      // Decoding the hidden file size
      int msg_len = 0;
      String len_bin = "";
      for(int m=0;m<3;m++){
        Raster raster = frames[index].getRaster();
        for (int l=0;l<8;l++){
          raster.getPixel(j+l,i,array_pixels);
          len_bin += String.format("%8s", Integer.toBinaryString(array_pixels[0])).replace(" ", "0").charAt(7);
        }
        j+=8;
        if (j >= width-8){
          j=0;
          i++;
        }
        if(i > height - 1){
          i=0;
          j=0;
          index++;
        }
      }
      msg_len = Integer.parseInt(len_bin,2);
      System.out.println("Hidden Message Length: "+msg_len);
      System.out.println("Hidden File Extension: "+file_ext);
      // Decoding the message
      String outputFileName = Helpers.setOutputFilename(file_ext);
      File msg = new File(outputFileName);
      FileOutputStream fos = new FileOutputStream(msg);
      int k=0;
      do{
        Raster raster = frames[index].getRaster();
        String char_bin = "";
        for (int l=0;l<8;l++){
          raster.getPixel(j+l,i,array_pixels);
          char_bin += String.format("%8s", Integer.toBinaryString(array_pixels[0])).replace(" ", "0").charAt(7);
        }
        //System.out.println(char_bin);
        fos.write((byte) Integer.parseInt(char_bin, 2));
        //secret_msg += (char) Integer.parseInt(char_bin,2);
        //System.out.println(secret_msg);
        k++;
        //System.out.println(msg_len - k);
        j+=8;
        if (j >= width-8){
          j=0;
          i++;
        }
        if(i > height - 1){
          i=0;
          j=0;
          index++;
        }
      }while(k<msg_len);
      fos.close();
      System.out.println("Message saved to file "+outputFileName);
    }catch(IOException e){
      System.out.println(e.getMessage());
    }
  }




}
