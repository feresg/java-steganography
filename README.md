# java-steganography

### Project: Steganography
   - Description: Hide a text or an image inside an image.
   - Authors: Ilyes Hamrouni, Feres Gaaloul.
   - Usage: Temporarily, use terminal, "java Index".
   - Help and Guide: see comments.

### Notes:
  - Each caracter will be encoded on 2 pixels :
    - (R, G) of pixel (i, j)
    - (G, B) of pixel (i, width - 1 - j)
    - We encode the two least significant bits
  - When encoding, the first line of the image will contain the following
    - First 5 pixels will contain the the file extension (used to determine wether the file is a document or an image)
    - If we encode a document, the next 3 pixels will contain the file size (in case of a document)
    - If we encode a image, we could use the next 2 pixels for the height and the next 2 for the width of the image
  - The secret message will be encoded from the pixel (1,0) onward
  - (This assumes that the encoded picture width is atleast 16-18 pixels)

### Issues:
   - Only works on png and bmp files. We can encode a jpeg only if the encoded output image is saved as a png or a bmp (not a jpeg). gif encoding and decoding not yet supported
   - Crypting the message before storing it not yet implemented
   - Encoding images not yet implemented
   - GUI not yet implemented (JAVAFx)
 