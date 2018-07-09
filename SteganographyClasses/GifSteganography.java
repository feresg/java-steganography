import javax.imageio.metadata.IIOMetadata;

public class GifSteganography extends BaseSteganography{
    private boolean isEncrypted = false;
    private long capacity;
    private BufferedImage[] frames;
    private IIOMetadata[] metadatas;

    // Constructors
    public GifSteganography(File input, boolean isEncrypted){
        this.isEncrypted = isEncrypted;
        this.frames = getFrames(input);
        this.metadatas = getMetadatas(input);
    }
    // Getters
    public boolean getEncryptionStatus(){
        return this.isEncrypted;
    }
    public long getCapacity(){
        return this.capacity;
    }
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
    public String setHeader(String message){
        String header = "M!"+message.length()+"!";
        header += (this.isEncrypted) ? "1" : "0";
        header += "!!";
        return header;
    }
    public String setHeader(File file){
        String header = "F+";
        header += "F!"+Helpers.getFileExtension(file)+"!"+file.length()+"!";
        header += (this.isEncrypted) ? "1" : "0";
        header += "!!";
        return header;
    }
    public String getHeader(){

    }
}