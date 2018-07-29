//  GifSequenceWriter.java
//
//  Created by Elliot Kroo on 2009-04-25.
//  Modified by Feres Gaaloul on 2018-07.
//
// This work is licensed under the Creative Commons Attribution 3.0 Unported
// License. To view a copy of this license, visit
// http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
// Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.

package Steganography.Logic;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Iterator;

/**
 * The {@code GifSequenceWriter} class is used by {@link GifSteganography} class
 * to create a GIF89a image based on an array of <code>BufferedImage</code> and an array of <code>IIOMetadata</code>.
 *
 * @author Elliot Kroo (2009-04-25).
 * @author Feres Gaaloul (2018-07).
 */
class GifSequenceWriter {

    /** Used to write a gif image. */
    private ImageWriter gifWriter;
    /** Describes how the gif will be written. */
    private ImageWriteParam imageWriteParam;

    /**
     * Creates a new GifSequenceWriter
     *
     * @author                    Elliot Kroo (elliot[at]kroo[dot]net)
     * @param outputStream        the ImageOutputStream to be written to
     * @param imageTypeSpecifier  the ImageTypeSpecifier of the image
     * @param timeBetweenFramesMS the time between frames in milliseconds
     * @param loopContinuously    whether the gif should loop repeatedly
     * @throws IIOException       if no gif ImageWriters are found.
     */
    public GifSequenceWriter(
            ImageOutputStream outputStream,
            ImageTypeSpecifier imageTypeSpecifier,
            int timeBetweenFramesMS,
            boolean loopContinuously) throws IOException {
        // my method to create a writer
        gifWriter = getWriter();
        imageWriteParam = gifWriter.getDefaultWriteParam();

        IIOMetadata imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
                imageWriteParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode)
                imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = Metadata.getNode(
                root,
                "GraphicControlExtension");

        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "transparentColorFlag",
                "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "delayTime",
                Integer.toString(timeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute(
                "transparentColorIndex",
                "0");

        IIOMetadataNode commentsNode = Metadata.getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by MAH");

        IIOMetadataNode appEntensionsNode = Metadata.getNode(
                root,
                "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");
        int loop = loopContinuously ? 0 : 1;

        child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF),
                (byte) ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        gifWriter.setOutput(outputStream);

        gifWriter.prepareWriteSequence(null);
    }

    /**
     * Returns the first available GIF ImageWriter using
     * ImageIO.getImageWritersBySuffix("gif").
     *
     * @return              a GIF ImageWriter object.
     * @throws IIOException if no GIF image writers are returned.
     */
    private static ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Exist");
        } else {
            return iter.next();
        }
    }

    /**
     * Adds a frame to the gif sequence.
     *
     * @param img                   frame to add to the sequence
     * @param originalImageMetaData metadata of the frame to add
     * @throws IOException          if an error occurs while writing a frame to the sequence.
     */
    public void writeToSequence(RenderedImage img, IIOMetadata originalImageMetaData) throws IOException {
        gifWriter.writeToSequence(
                new IIOImage(
                        img,
                        null,
                        originalImageMetaData),
                imageWriteParam);
    }

    /**
     * Close this GifSequenceWriter object. This does not close the underlying
     * stream, just finishes off the GIF.
     *
     * @throws IOException if an error occurs while closing the gif sequence.
     */
    public void close() throws IOException {
        gifWriter.endWriteSequence();
    }

}