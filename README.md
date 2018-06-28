# java-steganography

### Project: Steganography
   Description: Hide a text or an image inside an image.
   Authors: Ilyes Hamrouni, Feres Gaaloul.
   Usage: Temporarily, use terminal, "java Index".
   Help and Guide: see comments.

### Notes:
  - When encoding, the first line of the image will contain the following
    - First 3 pixels will contain the size of the file
    - The next pixels will contain the extension of the file (used for getting back non plain text documents???)
    - We still encode the two least significant bits
  - The secret message will be encoded from the pixel (1,0) , (1,1), (1,2), ..., (2,0), (2,1), ... until end of message

### Issues:
   - Only works on RGBA png files. issues with decoding jpg, bmp and gif (but no runtime error when encoding)
   (Converting RGB png files to rgba files (with alpha = 255) could help...)
   - Issues with converting non plain text docs (.docx, .pdf ...)
 
