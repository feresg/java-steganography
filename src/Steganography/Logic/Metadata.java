package Steganography.Logic;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

public class Metadata{
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
}