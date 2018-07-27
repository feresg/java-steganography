package Steganography;

import Steganography.Logic.*;

import Steganography.Modals.*;

import Steganography.Types.PasswordType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Controller {

    @FXML
    private MenuItem newSecretDocument, newSecretImage;
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
    private CheckBox encryptMessage, encryptDocument, compressDocument, compressMessage;
    @FXML
    private ToggleGroup messagePixelsPerByte, documentPixelsPerByte, pixelsPerPixel;
    @FXML
    private HBox messagePixelsPerByteWrapper, documentPixelsPerByteWrapper;
    // Files;
    private File coverImage, secretImage, secretDocument, steganographicImage, tempFile;
    // Password
    private String password;

    public void setCoverImage(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("New Cover Image...");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png", "*.bmp", "*.jpg", "*.jpeg", "*.gif"));
        coverImage = fc.showOpenDialog(null);
        if (coverImage != null) {
            coverImagePane.setMinSize(0, 0);
            coverImageView.setImage(new Image("file:" + coverImage.getPath()));
            coverImageView.fitWidthProperty().bind(coverImagePane.widthProperty());
            coverImageView.fitHeightProperty().bind(coverImagePane.heightProperty());
            coverImagePane.setMaxSize(900, 900);
            newSecretDocument.setDisable(false);
            newSecretImage.setDisable(false);
            secretMessageTab.setDisable(false);
            secretDocumentTab.setDisable(false);
            secretImageTab.setDisable(Helpers.getFileExtension(coverImage).toLowerCase().equals("gif"));
            messagePixelsPerByteWrapper.setVisible(!Helpers.getFileExtension(coverImage).toLowerCase().equals("gif"));
            documentPixelsPerByteWrapper.setVisible(!Helpers.getFileExtension(coverImage).toLowerCase().equals("gif"));
        } else {
            AlertBox.error("Error while setting cover image", "Try again...");
        }
    }

    public void setSteganographicImage(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("New Steganographic Image...");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png", "*.bmp", "*.jpg", "*.jpeg", "*.gif"));
        steganographicImage = fc.showOpenDialog(null);
        if (steganographicImage != null) {
            steganographicImagePane.setMinSize(0, 0);
            steganographicImageView.setImage(new Image("file:" + steganographicImage.getPath()));
            steganographicImageView.fitWidthProperty().bind(steganographicImagePane.widthProperty());
            steganographicImageView.fitHeightProperty().bind(steganographicImagePane.heightProperty());
            steganographicImagePane.setMaxSize(1440, 900);
            decodeImage.setDisable(false);
        } else {
            AlertBox.error("Error while setting steganographic image", "Try again...");
        }
    }

    public void setSecretDocument(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("New Secret Document...");
        secretDocument = fc.showOpenDialog(null);
        if (secretDocument != null) {
            encodeDocument.setDisable(false);
            try {
                DocumentViewer.showSecretDocument(secretDocumentContent, secretDocument);
            } catch (IOException e) {
                e.printStackTrace();
                AlertBox.error("Error while setting secret document", e.getMessage());
            }
        } else {
            AlertBox.error("Error while setting secret document", "Try again...");
        }
    }

    public void setSecretImage(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("New Secret Image...");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png", "*.bmp", "*.jpg", "*.jpeg"));
        secretImage = fc.showOpenDialog(null);
        if (secretImage != null) {
            secretImagePane.setMinSize(0, 0);
            secretImageView.setImage(new Image("file:" + secretImage.getPath()));
            secretImageView.fitWidthProperty().bind(secretImagePane.widthProperty());
            secretImageView.fitHeightProperty().bind(secretImagePane.heightProperty());
            secretImagePane.setMaxSize(900, 900);
            encodeImage.setDisable(false);

        } else {
            AlertBox.error("Error while setting secret image", "Try again...");
        }
    }

    public void encodeMessageInImage(ActionEvent event) {
        String message = secretMessage.getText();
        byte[] secret = message.getBytes(StandardCharsets.UTF_8);
        if(compressMessage.isSelected())
            secret = ZLibCompression.compress(secret);
        if (encryptMessage.isSelected())
            secret = AESEncryption.encrypt(secret, password);
        String imageExtension = Helpers.getFileExtension(coverImage).toLowerCase();
        imageExtension = (imageExtension.matches("jpg|jpeg")) ? "png" : imageExtension;
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Steganographic Image...");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        imageExtension.toUpperCase(),
                        "*." + imageExtension));

        steganographicImage = fc.showSaveDialog(null);
        if (steganographicImage != null) {
            BaseSteganography img;
            try {
                if (imageExtension.toLowerCase().equals("gif"))
                    img = new GifSteganography(coverImage, encryptMessage.isSelected(), compressMessage.isSelected());
                else
                    img = new ImageSteganography(coverImage, encryptMessage.isSelected(), compressMessage.isSelected(), getToggleGroupValue(messagePixelsPerByte));
                img.encode(secret, steganographicImage);
                AlertBox.information("Encoding Successful!", "Message encoded successfully in " + steganographicImage.getName() + ".", steganographicImage);
            } catch (IOException e) {
                e.printStackTrace();
                AlertBox.error("Error while encoding", e.getMessage());
            }
        }
    }

    public void encodeDocumentInImage(ActionEvent event) {
        String secretFileExtension = Helpers.getFileExtension(secretDocument);
        try {
            if(compressDocument.isSelected() || encryptDocument.isSelected()) {tempFile = File.createTempFile("temp", "." + secretFileExtension); tempFile.deleteOnExit();}
            if(compressDocument.isSelected() && encryptDocument.isSelected()) {
                File auxFile = File.createTempFile("aux", "."+secretFileExtension); auxFile.deleteOnExit();
                ZLibCompression.compress(secretDocument, auxFile);
                AESEncryption.encrypt(auxFile, tempFile, password);
            }else{
                if(compressDocument.isSelected())
                    ZLibCompression.compress(secretDocument, tempFile);
                else if(encryptDocument.isSelected())
                    AESEncryption.encrypt(secretDocument, tempFile, password);
            }
            if(compressDocument.isSelected() || encryptDocument.isSelected()) { secretDocument = tempFile; }
            String imageExtension = Helpers.getFileExtension(coverImage).toLowerCase();
            imageExtension = (imageExtension.matches("jpg|jpeg")) ? "png" : imageExtension;
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(
                            imageExtension.toUpperCase(),
                            "*." + imageExtension));
            steganographicImage = fc.showSaveDialog(null);
            if (steganographicImage != null) {
                BaseSteganography img;
                if (imageExtension.toLowerCase().equals("gif"))
                    img = new GifSteganography(coverImage, encryptDocument.isSelected(), compressDocument.isSelected());
                else
                    img = new ImageSteganography(coverImage, encryptDocument.isSelected(), compressDocument.isSelected(), getToggleGroupValue(documentPixelsPerByte));
                img.encode(secretDocument, steganographicImage);
                AlertBox.information("Encoding Successful!", "Document encoded successfully in " + steganographicImage.getName() + ".", steganographicImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertBox.error("Error while encoding", e.getMessage());
        }
    }

    public void encodeImageInImage(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "PNG Image",
                        "*.png"));
        steganographicImage = fc.showSaveDialog(null);
        if (steganographicImage != null) {
            try {
                ImageInImageSteganography img = new ImageInImageSteganography(coverImage, getToggleGroupValue(pixelsPerPixel));
                img.encode(secretImage, steganographicImage);
                AlertBox.information("Encoding Successful!", "Image " + secretImage.getName() + " encoded successfully in " + steganographicImage.getName() + ".", steganographicImage);
            } catch (IOException e) {
                e.printStackTrace();
                AlertBox.error("Error while encoding", e.getMessage());
            }
        }
    }

    public void decodeImage(ActionEvent event) {
        String imageExtension = Helpers.getFileExtension(steganographicImage);
        HiddenData hiddenData;
        FileChooser fc = new FileChooser();
        File file;
        try {
            BaseSteganography img = (imageExtension.toLowerCase().equals("gif")) ? new GifSteganography(steganographicImage) : new ImageSteganography(steganographicImage);
            hiddenData = new HiddenData(img.getHeader());
            fc.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(
                            hiddenData.extension.toUpperCase(),
                            "*." + hiddenData.extension));
            switch (hiddenData.format) {
                case MESSAGE:
                    tempFile = File.createTempFile("message", ".txt");
                    img.decode(tempFile);
                    byte[] secret = Files.readAllBytes(tempFile.toPath());
                    String message;
                    if (hiddenData.isEncrypted) {
                        password = PasswordPrompt.display(PasswordType.DECRYPT);
                        secret = AESEncryption.decrypt(secret, password);
                    }
                    if(hiddenData.isCompressed)
                        secret = ZLibCompression.decompress(secret);
                    message = new String(secret, StandardCharsets.UTF_8);
                    if (message.length() > 0)
                        AlertBox.information("Decoding successful!", "Here is the secret message:", message);
                    tempFile.deleteOnExit();
                    break;
                case DOCUMENT:
                    file = fc.showSaveDialog(null);
                    if(hiddenData.isCompressed || hiddenData.isEncrypted) {
                        tempFile = File.createTempFile("temp", "."+hiddenData.extension);tempFile.deleteOnExit();
                        img.decode(tempFile);
                    }
                    if(hiddenData.isEncrypted) { password = PasswordPrompt.display(PasswordType.DECRYPT); }
                    if (hiddenData.isEncrypted && hiddenData.isCompressed) {
                        File auxFile = File.createTempFile("aux", "."+hiddenData.extension);
                        auxFile.deleteOnExit();
                        AESEncryption.decrypt(tempFile, auxFile, password);
                        ZLibCompression.decompress(auxFile, file);
                    }else{
                        if(hiddenData.isCompressed)
                            ZLibCompression.decompress(tempFile, file);
                        else if(hiddenData.isEncrypted)
                            AESEncryption.decrypt(tempFile, file, password);
                        else
                            img.decode(file);
                    }
                    if (file != null && file.length() > 0)
                        AlertBox.information("Decoding Successful!", "Document decoded in " + file.getName(), file);
                    break;
                case IMAGE:
                    ImageInImageSteganography imgInImg = new ImageInImageSteganography(steganographicImage);
                    file = fc.showSaveDialog(null);
                    imgInImg.decode(file);
                    AlertBox.information("Decoding Successful!", "Image decoded in " + file.getName(), file);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertBox.error("Error while decoding", e.getMessage());
        }
    }

    public void getEncryptionPassword(ActionEvent event) {
        if (encryptMessage.isSelected() || encryptDocument.isSelected()) {
            if ((password = PasswordPrompt.display(PasswordType.ENCRYPT)) == null) {
                encryptMessage.setSelected(false);
                encryptDocument.setSelected(false);
            }
        } else {
            password = null;
        }
    }

    public void showAboutPage(ActionEvent event) {
        AboutPage.display(event);
    }

    public void quitApp(ActionEvent event) {
        System.exit(0);
    }

    private byte getToggleGroupValue(ToggleGroup group){
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        return (byte) Integer.parseInt(selectedRadioButton.getText());
    }

}
