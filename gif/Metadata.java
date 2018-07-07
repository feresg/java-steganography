package gif;

import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

public class Metadata {

    public static BufferedImage[] getFrames(File gif) throws IOException {
        ImageReader img = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream new_img = ImageIO.createImageInputStream(gif);
        img.setInput(new_img);
        int count = img.getNumImages(true);
        BufferedImage[] frames = new BufferedImage[count];
        for (int index = 0; index < count; index++) {
            BufferedImage frame = img.read(index);
            frames[index] = frame;
        }
        return frames;
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
    public static int[] getOffsets(File gif, int x) throws IOException{
        int [] offsets = new int[2];
        ImageReader gif_img = ImageIO.getImageReadersBySuffix("gif").next();
        ImageInputStream input = ImageIO.createImageInputStream(gif);
        gif_img.setInput(input);
        IIOMetadata imageMetaData =  gif_img.getImageMetadata(x);
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode)imageMetaData.getAsTree(metaFormatName);
        IIOMetadataNode ImageDescriptor = getNode(root, "ImageDescriptor");
        offsets[0] = Integer.parseInt(ImageDescriptor.getAttribute("imageLeftPosition"));
        offsets[1] = Integer.parseInt(ImageDescriptor.getAttribute("imageTopPosition"));
        return offsets;
    }
    public static IIOMetadata getImageMetadata(File gif, int x) throws IOException{
        ImageReader gif_img = ImageIO.getImageReadersBySuffix("gif").next();
        ImageInputStream input = ImageIO.createImageInputStream(gif);
        gif_img.setInput(input);
        IIOMetadata imageMetaData =  gif_img.getImageMetadata(x);
        return imageMetaData;
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
