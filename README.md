# StegIt

> The word “steganography” can be defined as “covered writing” or the technique of hiding messages inside other messages. It is derived from a Greek word “στεγαυω”. This idea of data hiding is not a novelty, it has been used for centuries all across the world under different regimes - but to date it is still unknown to most people - is a tool for hiding information so that it does not even appear to exist.

![Screenshot](https://github.com/feresg/java-steganography/raw/master/Screenshot.png)

### An image steganography application to hide text, documents or images in images.
    StegIt uses the Least Significant Bit (LSB) algorithm to hide the secret data in the least significant bits of each RGB components of an image in a way that will be invisible to the naked eye
    
##### Authors: [Feres Gaaloul](https://github.com/feresg), [Ilyes Hamrouni](https://github.com/ilyes-hamrouni)
    ENSI Summer Project.

#### Features:
- Use 24 bit bitmap  `PNG`, `BMP`  and `JPG`  images and also animated `GIF` images as cover image.
- Hide text, documents and 24 bit bitmap images as secret data.
- Compress data before encoding to reduce effect on stego image using  the `Java ZLib` compression library.
- Encrypt the secret message or document using `128 bit AES CBC` encryption (Advanced Encrytion Standard) for an added layer of security.
- Provide either a password for encryption, or an image that will be automatically hashed to create an encryption key.
- Choose the number of pixels to use to hide 1bit of data/1 pixel (1 or 2).
- Interaction with system applications and clipboard.

#### Environment :
    This application is built using `Java` and `JavaFX` for GUI development.

#### Requirements:
    `Java JDK` is required to build the project, `Java JRE` is required to run the project.

#### Notes:
  - Tested working document formats :
      - WORKING .txt, .log, (any plain text file format (.java, .py .cpp...)) .xml .rtf (includes rich text formatting) .doc (with basic rich text formatting).
      - NOT working : .pdf .docx.
      
#### Issues:
  - Cover `JPG` images are saved as `PNG` to avoid loss of hidden data after JPEG compression.
  - Due to the limitation of the `GIF` colormap, hiding large data can heavily alter images.
  
  #### Project Structure:
  - __src__ : source files.
  - __testFiles__ : images and documents used to test the app.
  - __documentation__ : `JavaDoc` documentation of the project.
  - __StegIt.jar__ : Binary application.
  - __Screenshot.png__ : main screenshot.
  - __Package Steganography.png__ : `UML` class diagram.
  - __Algorithme.pdf__ : project, GUI and algorithms description (in french).


