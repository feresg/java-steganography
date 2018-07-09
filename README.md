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
    - First pixels will contain the the file extension (used to determine wether the file is a document or an image) until "00000000"
    - If we encode a document, the next 3 pixels will contain the file size
    - If we encode a image, we could use the next 2 pixels for the height and the next 2 for the width of the image
  - The secret message will be encoded from the next pixel onward
  - (This assumes that the encoded picture width is atleast 16-18 pixels)
  - Tested working document formats :
      - WORKING .txt, .log, (any plain text file format (.java, .py .cpp...)) .xml .rtf (includes rich text formatting)
      - KIND OF working : .doc (depending on the amount of editing...)
      - NOT working : .pdf .docx

### Issues:

  - Only works on png and bmp files. We can encode a jpeg only if the encoded output image is saved as a png or a bmp (not a jpeg)
  - gif encoding optimised for smaller messages

### TODO :
  - Adding image encoding to project
  - Refactoring project
  - GUI (using JavaFX)

