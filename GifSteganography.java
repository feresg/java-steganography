import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadata;

public class GifSteganography extends BaseSteganography{
    private BufferedImage[] frames;
    private IIOMetadata[] metadatas;
    private int delayMS;

    // Constructors
    public GifSteganography(File input, boolean isEncrypted) throws IOException{
        this.isEncrypted = isEncrypted;
        this.frames = getFrames(input);
        this.metadatas = getMetadatas(input);
        this.delayMS = getDelayMS(input);
    }
    // Getters
    public static BufferedImage[] getFrames(File gif) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream input = ImageIO.createImageInputStream(gif);
        reader.setInput(input);
        int count = reader.getNumImages(true);
        BufferedImage[] frames = new BufferedImage[count];
        for (int index = 0; index < count; index++) {
            frames[index] = reader.read(index);
        }
        return frames;
    }
    public static IIOMetadata[] getMetadatas(File gif) throws IOException{
        ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
        ImageInputStream input = ImageIO.createImageInputStream(gif);
        reader.setInput(input);
        int count = reader.getNumImages(true);
        IIOMetadata[] metadatas = new IIOMetadata[count];
        for(int index = 0; index < count; index++){
            metadatas[index] =  reader.getImageMetadata(index);
        }
        return metadatas;
    }
    public static int getDelayMS(File gif) throws IOException{
        ImageReader gif_img = ImageIO.getImageReadersBySuffix("gif").next();
        ImageInputStream input = ImageIO.createImageInputStream(gif);
        gif_img.setInput(input);
        IIOMetadata imageMetaData =  gif_img.getImageMetadata(0);
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode)imageMetaData.getAsTree(metaFormatName);
        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        int delayTimeMS = Integer.parseInt(graphicsControlExtensionNode.getAttribute("delayTime"));
        return delayTimeMS;
    }
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)== 0) {
            return((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return(node);
    }
    public void writeHeader(byte[] header){
        int i=0, j=0, k=0;
        int[] pixel = new int[4];
        for(byte b : header){
            for(int l=0; l<8; l++){
                WritableRaster raster = this.frames[k].getRaster();
                raster.getPixel(j,i,pixel);
                pixel[0] = hidePixel(pixel[0], String.format("%8s",Integer.toBinaryString(b)).replace(' ', '0').charAt(l));
                raster.setPixel(j,i,pixel);
                increment(i,j,k);
            }
        }
    }
    public byte[] getHeader(){
        int i=0, j=0, k=0;
        byte b;
        int[] pixel = new int[4];
        List<Byte> header = new ArrayList<Byte>();
        do{
            String bit = "";
            for(int l=0; l<8; l++){
                Raster raster = this.frames[k].getRaster();
                bit += String.format("%8s", Integer.toBinaryString(pixel[0])).replace(" ", "0").charAt(7);
            }
            b = Byte.parseByte(bit,2);
            header.add(b);
        }while(b != (byte) '!');
        return Helpers.toByteArray(header);
    }
    public void encode(String str){
        try{
            this.writeHeader(this.setHeader(str));
            int i=(this.header.length)%(this.frames[0].getWidth());
            int j=(this.header.length)/(this.frames[0].getWidth());
            int k=0;
            int[] pixel = new int[4];
            byte[] message = str.getBytes(Charset.forName("UTF-8"));
            for(byte b : message){
                for(int l=0; l<8; l++){
                    WritableRaster raster = this.frames[k].getRaster();
                    raster.getPixel(j,i,pixel);
                    pixel[0] = hidePixel(pixel[0], String.format("%8s",Integer.toBinaryString(b)).replace(' ', '0').charAt(l));
                    raster.setPixel(j,i,pixel);
                    increment(i,j,k);
                }
            }
            File eimg = new File("encoded.gif");
            ImageOutputStream output = new FileImageOutputStream(eimg);
            ColorModel cm = this.frames[0].getColorModel();
            ImageTypeSpecifier imageType = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
            GifSequenceWriter writer = new GifSequenceWriter(output, imageType, this.delayMS, true);
            for(int x=0; x<this.frames.length; x++) {
              writer.writeToSequence(this.frames[x], this.metadatas[x]);
            }
            writer.close();
            output.close();
            System.out.println("Message hidden inside encoded.gif");
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void encode(File doc){
        try{
            this.writeHeader(this.setHeader(doc));
            int[] pixel = new int[4];
            int i=(this.header.length*8)%(this.frames[0].getWidth());
            int j=(this.header.length*8)/(this.frames[0].getWidth());
            int k=0;
            String line;
            InputStreamReader stream = new InputStreamReader(new FileInputStream(doc));
            BufferedReader reader = new BufferedReader(stream);//reads the user file
            while((line = reader.readLine()) != null){
                byte[] message = line.getBytes(Charset.forName("UTF-8"));
                for(byte b : message){
                    for(int l=0; l<8; l++){
                        WritableRaster raster = this.frames[k].getRaster();
                        raster.getPixel(j,i,pixel);
                        pixel[0] = hidePixel(pixel[0], String.format("%8s",Integer.toBinaryString(b)).replace(' ', '0').charAt(l));
                        raster.setPixel(j,i,pixel);
                        increment(i,j,k);
                    }
                }
            }
            File eimg = new File("encoded.gif");
            ImageOutputStream output = new FileImageOutputStream(eimg);
            ColorModel cm = this.frames[0].getColorModel();
            ImageTypeSpecifier imageType = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
            GifSequenceWriter writer = new GifSequenceWriter(output, imageType, this.delayMS, true);
            for(int x=0; x<this.frames.length; x++) {
              writer.writeToSequence(this.frames[x], this.metadatas[x]);
            }
            writer.close();
            output.close();
            System.out.println("File hidden inside encoded.gif");
        }catch(Exception e) {System.out.println("Encoding Error : "+ e.getMessage());}
    }
    public void decode() throws IOException{
        try{
            byte[] header = this.getHeader();
            Map<String, String> attributes = getAttributes(header);
            int i=(header.length*8)%(this.frames[0].getWidth());
            int j=(header.length*8)/(this.frames[0].getWidth());
            int k=0;
            int length = Integer.parseInt(attributes.get("length"));
            int pos = 0;
            int pixel[] = new int[4];
            byte b;
            File file = new File("secret."+attributes.get("extension"));
            FileOutputStream fos = new FileOutputStream(file);
            do{
                String bit = "";
                for(int l=0; l<8; l++){
                    Raster raster = this.frames[k].getRaster();
                    raster.getPixel(j+l,i,pixel);
                    bit += String.format("%8s", Integer.toBinaryString(pixel[0])).replace(" ", "0").charAt(7);
                }
                b = Byte.parseByte(bit,2);
                fos.write(b);
            }while(pos<length);
            fos.close();
            if(attributes.get("encryption").equals('E')){
                // Temporary
                System.out.print("Enter Password :\t");
                Scanner scan = new Scanner(System.in);
                String password = scan.nextLine();
                file = AESEncryption.decrypt(file, password);
            }
            System.out.println("Secret file saved to "+ file.getName());
        }catch(Exception e) {System.out.println("Decryption Error : "+ e.getMessage());}
    }
    public int hidePixel(int pixel, char c){
        String before = String.format("%8s", Integer.toBinaryString(pixel)).replace(' ','0');
        String after = before.substring(0,7)+c;
        return Integer.parseInt(after,2);
    }
    public void increment(int i, int j, int k){
        j++;
        if(j == this.frames[k].getWidth()){
            j=0;
            i++;
        }
        if(i == this.frames[k].getHeight()){
            j=0;
            i=0;
            k++;
        }
    }
}