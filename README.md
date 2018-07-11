# java-steganography

### Project: Steganography
   - Description: Hide a text or an image inside an image.
   - Authors: Ilyes Hamrouni, Feres Gaaloul.
   - Usage: Temporarily, use terminal, "java Index".
   - Help and Guide: see comments.

### Notes:
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

### GUI :
  ![alt text](https://raw.githubusercontent.com/feres/java-steganography/master/testFiles/screenshot.png)