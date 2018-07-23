package Steganography;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

import Steganography.Logic.*;
import Steganography.Modals.*;


public class Controller {
    // Used Items
    @FXML
    private MenuItem newSecretDocument, newSecretImage, newSteganographicImage, aboutPage, quitApp;
    @FXML
    private ImageView secretImageView, coverImageView, steganographicImageView;
    @FXML
    private TextArea secretMessage;
    @FXML
    private Button encodeDocument, encodeImage, decodeImage;
    @FXML
    private Tab secretImageTab, secretMessageTab, secretDocumentTab;
    @FXML
    private VBox coverImagePane, secretImagePane, steganographicImagePane;
    @FXML
    private ListView<String> secretDocumentContent;
    @FXML
    private CheckBox encryptMessage, encryptDocument;
    // Files;
    private File coverImage, secretImage, secretDocument, steganographicImage, tempFile;
    // Password
    private String password;
    public void setCoverImage(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("New Cover Image...");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png","*.bmp","*.jpg","*.jpeg","*.gif"));
        coverImage = fc.showOpenDialog(null);
        if(coverImage != null){
            coverImagePane.setMinSize(0, 0);
            coverImageView.setImage(new Image("file:"+coverImage.getPath()));
            coverImageView.fitWidthProperty().bind(coverImagePane.widthProperty());
            coverImageView.fitHeightProperty().bind(coverImagePane.heightProperty());
            coverImagePane.setMaxSize(900,900);
            newSecretDocument.setDisable(false);
            newSecretImage.setDisable(false);
            secretMessageTab.setDisable(false);
            secretDocumentTab.setDisable(false);
            secretImageTab.setDisable(Helpers.getFileExtension(coverImage).toLowerCase().equals("gif"));

        } else{
            System.out.println("Error Adding Cover Image...");
        }
    }
    public void setSteganographicImage(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("New Steganographic Image...");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png","*.bmp","*.jpg","*.jpeg","*.gif"));
        steganographicImage = fc.showOpenDialog(null);
        if(steganographicImage != null){
            steganographicImagePane.setMinSize(0, 0);
            steganographicImageView.setImage(new Image("file:"+steganographicImage.getPath()));
            steganographicImageView.fitWidthProperty().bind(steganographicImagePane.widthProperty());
            steganographicImageView.fitHeightProperty().bind(steganographicImagePane.heightProperty());
            steganographicImagePane.setMaxSize(1440,900);
            decodeImage.setDisable(false);
        }else{
            System.out.println("Error Adding Steganographic Image...");
        }

    }
    public void setSecretDocument(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("New Secret Document...");
        secretDocument = fc.showOpenDialog(null);
        if(secretDocument != null){
            encodeDocument.setDisable(false);
            try{
                DocumentViewer.showSecretDocument(secretDocumentContent, secretDocument);
            }catch(IOException e) {e.printStackTrace();}
        }else{
            System.out.println("Error Adding Secret Document...");
        }
    }
    public void setSecretImage(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("New Secret Image...");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png","*.bmp","*.jpg","*.jpeg"));
        secretImage = fc.showOpenDialog(null);
        if(secretImage != null){
            secretImagePane.setMinSize(0, 0);
            secretImageView.setImage(new Image("file:"+secretImage.getPath()));
            secretImageView.fitWidthProperty().bind(secretImagePane.widthProperty());
            secretImageView.fitHeightProperty().bind(secretImagePane.heightProperty());
            secretImagePane.setMaxSize(900,900);
            encodeImage.setDisable(false);
        } else{
            System.out.println("Error Adding Secret Image...");
        }
    }
    public void encodeMessageInImage(ActionEvent event){
        try{
            String message = secretMessage.getText();
            if(encryptMessage.isSelected()){
                message = AESEncryption.encrypt(message, password);
            }
            String imageExtension = Helpers.getFileExtension(coverImage).toLowerCase();
            imageExtension = (imageExtension.matches("jpg|jpeg")) ? "png" : imageExtension;
            FileChooser fc = new FileChooser();
            fc.setTitle("Save Steganographic Image...");
            fc.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(
                            imageExtension.toUpperCase(),
                            "*."+imageExtension));

            steganographicImage = fc.showSaveDialog(null);
            if(steganographicImage != null){
                BaseSteganography img;
                if(imageExtension.toLowerCase().equals("gif"))
                    img = new GifSteganography(coverImage, encryptMessage.isSelected());
                else
                    img = new ImageSteganography(coverImage, encryptMessage.isSelected());
                img.encode(message, steganographicImage);
                AlertBox.information("Encoding Successful!", "Message encoded successfully in "+steganographicImage.getName()+".", steganographicImage);
            }
        }catch (Exception e) {e.printStackTrace();}
    }
    public void encodeDocumentInImage(ActionEvent event){
        try{
            String secretFileExtension = Helpers.getFileExtension(secretDocument);
            if(encryptDocument.isSelected()){
                tempFile = File.createTempFile("encrypted", "."+secretFileExtension);
                AESEncryption.encrypt(secretDocument, tempFile, password);
                secretDocument = tempFile;
                tempFile.deleteOnExit();
            }
            String imageExtension = Helpers.getFileExtension(coverImage).toLowerCase();
            imageExtension = (imageExtension.matches("jpg|jpeg")) ? "png" : imageExtension;
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(
                            imageExtension.toUpperCase(),
                            "*."+imageExtension));
            steganographicImage = fc.showSaveDialog(null);
            if(steganographicImage != null){
                BaseSteganography img;
                if(imageExtension.toLowerCase().equals("gif"))
                    img = new GifSteganography(coverImage, encryptDocument.isSelected());
                else
                    img = new ImageSteganography(coverImage, encryptDocument.isSelected());
                img.encode(secretDocument, steganographicImage);
                AlertBox.information("Encoding Successful!", "Document encoded successfully in "+steganographicImage.getName()+".", steganographicImage);
            }
        }catch (Exception e) {e.printStackTrace();}
    }
    public void encodeImageInImage(ActionEvent event){
        try{
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(
                            "PNG Image",
                            "*.png"));
            steganographicImage = fc.showSaveDialog(null);
            if(steganographicImage != null){
                ImageInImageSteganography img = new ImageInImageSteganography(coverImage);
                img.encode(secretImage, steganographicImage);
                AlertBox.information("Encoding Successful!", "Image "+secretImage.getName()+" encoded successfully in "+steganographicImage.getName()+".", steganographicImage);
            }
        }catch (Exception e) {e.printStackTrace();}
    }
    public void decodeImage(ActionEvent event) throws IOException{
        String imageExtension = Helpers.getFileExtension(steganographicImage);
        String fileExtension = "";
        Map<String, String> attributes;
        FileChooser fc = new FileChooser();
        File file;
        BaseSteganography img = (imageExtension.toLowerCase().equals("gif")) ? new GifSteganography(steganographicImage) : new ImageSteganography(steganographicImage);
        attributes = img.getAttributes(img.getHeader());
        fileExtension = attributes.get("extension");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        fileExtension.toUpperCase(),
                        "*."+fileExtension));
        switch (attributes.get("mode")){
            case "M":
                tempFile = File.createTempFile("message",".txt");
                img.decode(tempFile);
                String message = new Scanner(tempFile).useDelimiter("\\Z").next();
                if(attributes.get("encryption").equals("E")){
                    password = PasswordPrompt.display("decrypt");
                    message = AESEncryption.decrypt(message, password);
                }
                if(message != null && message.length() > 0)
                    AlertBox.information("Decoding successful!", "Here is the secret message:", message);
                tempFile.deleteOnExit();
                break;
            case "D":
                file = fc.showSaveDialog(null);
                img.decode(file);
                if(attributes.get("encryption").equals("E")){
                    password = PasswordPrompt.display("decrypt");
                    AESEncryption.decrypt(file, file, password);
                }
                if(file != null && file.length() > 0)
                    AlertBox.information("Decoding Successful!", "Document decoded in "+file.getName(), file);
                break;
            case "I":
                ImageInImageSteganography imgInImg = new ImageInImageSteganography(steganographicImage);
                file = fc.showSaveDialog(null);
                imgInImg.decode(file);
                AlertBox.information("Decoding Successful!", "Image decoded in "+file.getName(), file);
                break;
        }
    }
    public void getEncryptionPassword(ActionEvent event){
        if(encryptMessage.isSelected() || encryptDocument.isSelected()){
            if((password = PasswordPrompt.display("encrypt")) == null) {
                encryptMessage.setSelected(false); encryptDocument.setSelected(false);
            }
            System.out.println(password);
        } else {password = null;}
    }
    public void showAboutPage(ActionEvent event){
        AboutPage.display(event);
    }
    public void quitApp(ActionEvent event){
        System.exit(0);
    }
}
