package Steganography;

import Steganography.Exceptions.CannotDecodeException;
import Steganography.Exceptions.CannotEncodeException;
import Steganography.Exceptions.UnsupportedImageTypeException;
import Steganography.Logic.AESEncryption;
import Steganography.Logic.BaseSteganography;
import Steganography.Logic.GifSteganography;
import Steganography.Logic.HiddenData;
import Steganography.Logic.ImageInImageSteganography;
import Steganography.Logic.ImageSteganography;
import Steganography.Logic.Utils;
import Steganography.Logic.ZLibCompression;
import Steganography.Modals.AboutPage;
import Steganography.Modals.AlertBox;
import Steganography.Modals.PasswordPrompt;
import Steganography.Types.DataFormat;
import Steganography.Types.PasswordType;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Controller{

    // JavaFX Components
    @FXML
    private Menu editMenu;
    @FXML
    private MenuItem newSecretDocument, newSecretImage, cutMenu, copyMenu, pasteMenu, undoMenu, redoMenu, selectAllMenu, deselectMenu, deleteMenu;
    @FXML
    private RadioMenuItem darkTheme, lightTheme;
    @FXML
    private ImageView secretImageView, coverImageView, steganographicImageView;
    @FXML
    private TextArea secretMessage;
    @FXML
    private Button encodeDocument, encodeImage, decodeImage;
    @FXML
    private Tab secretImageTab, secretMessageTab, secretDocumentTab;
    @FXML
    private VBox root, coverImagePane, secretImagePane, steganographicImagePane;
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
    // Clipboard
    private Clipboard systemClipboard = Clipboard.getSystemClipboard();

    /**
     * Sets the cover image from the <code>JavaFX FileChooser</code> and adds it to the {@link #coverImageView}
     * then enables the disabled secret data controls.
     */
    public void setCoverImage() {
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
            editMenu.setDisable(false);
            newSecretDocument.setDisable(false);
            newSecretImage.setDisable(false);
            secretMessageTab.setDisable(false);
            secretDocumentTab.setDisable(false);
            secretImageTab.setDisable(Utils.getFileExtension(coverImage).toLowerCase().equals("gif"));
            messagePixelsPerByteWrapper.setVisible(!Utils.getFileExtension(coverImage).toLowerCase().equals("gif"));
            documentPixelsPerByteWrapper.setVisible(!Utils.getFileExtension(coverImage).toLowerCase().equals("gif"));
        } else {
            AlertBox.error("Error while setting cover image", "Try again...");
        }
    }

    /**
     * Sets the steganographic image from the <code>JavaFX FileChooser</code> and adds it to the {@link #steganographicImageView}.
     */
    public void setSteganographicImage() {
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

    /**
     * Sets the secret document from the <code>JavaFX FileChooser</code> then adds its content
     * line by line in the {@link #secretDocumentContent} using {@link #getDocumentContent(ListView, File)}
     */
    public void setSecretDocument() {
        FileChooser fc = new FileChooser();
        fc.setTitle("New Secret Document...");
        secretDocument = fc.showOpenDialog(null);
        if (secretDocument != null) {
            encodeDocument.setDisable(false);
            try {
                getDocumentContent(secretDocumentContent, secretDocument);
            } catch (IOException e) {
                e.printStackTrace();
                AlertBox.error("Error while setting secret document", e.getMessage());
            }
        } else {
            AlertBox.error("Error while setting secret document", "Try again...");
        }
    }

    /**
     * Sets the secret image from the <code>JavaFX FileChooser</code> and adds it to the {@link #secretImageView}.
     */
    public void setSecretImage() {
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

    /**
     * Encodes a message in an image after compressing then encrypting it (if enabled),
     * then calls either {@link ImageSteganography} or {@link GifSteganography} based
     * on the cover image extension.
     */
    public void encodeMessageInImage() {
        String message = secretMessage.getText();
        byte[] secret = message.getBytes(StandardCharsets.UTF_8);
        if(compressMessage.isSelected())
            secret = ZLibCompression.compress(secret);
        if (encryptMessage.isSelected())
            secret = AESEncryption.encrypt(secret, password);
        String imageExtension = Utils.getFileExtension(coverImage).toLowerCase();
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
            } catch (IOException | CannotEncodeException | UnsupportedImageTypeException e) {
                e.printStackTrace();
                AlertBox.error("Error while encoding", e.getMessage());
            }
        }
    }

    /**
     * Encodes a document in an image after compressing then encrypting it (if enabled),
     * then calls either {@link ImageSteganography} or {@link GifSteganography} based
     * on the cover image extension.
     */
    public void encodeDocumentInImage() {
        String secretFileExtension = Utils.getFileExtension(secretDocument);
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
            String imageExtension = Utils.getFileExtension(coverImage).toLowerCase();
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
        } catch (IOException | CannotEncodeException | UnsupportedImageTypeException e) {
            e.printStackTrace();
            AlertBox.error("Error while encoding", e.getMessage());
        }
    }

    /**
     * Encodes an image in another image using {@link ImageInImageSteganography}.
     */
    public void encodeImageInImage() {
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
            } catch (IOException | CannotEncodeException | UnsupportedImageTypeException e) {
                e.printStackTrace();
                AlertBox.error("Error while encoding", e.getMessage());
            }
        }
    }

    /**
     * Handles decoding the image by decoding the data using the appropriate class
     * based on the extension ({@link ImageSteganography} or {@link GifSteganography}),
     * then constructs an {@link HiddenData} object from the image header,
     * then performs decoding and decompression (if enabled) to return the secret data.
     */
    public void decodeImage() {
        String imageExtension = Utils.getFileExtension(steganographicImage);
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

            if (hiddenData.format == DataFormat.MESSAGE) {
                tempFile = File.createTempFile("message", ".txt");
                img.decode(tempFile);
                byte[] secret = Files.readAllBytes(tempFile.toPath());
                String message;
                if (hiddenData.isEncrypted) {
                    password = PasswordPrompt.display(PasswordType.DECRYPT);
                    secret = AESEncryption.decrypt(secret, password);
                }
                if (hiddenData.isCompressed)
                    secret = ZLibCompression.decompress(secret);
                message = new String(secret, StandardCharsets.UTF_8);
                if (message.length() > 0)
                    AlertBox.information("Decoding successful!", "Here is the secret message:", message);
                tempFile.deleteOnExit();
            }

            else if(hiddenData.format == DataFormat.DOCUMENT) {
                file = fc.showSaveDialog(null);
                if (hiddenData.isCompressed || hiddenData.isEncrypted) {
                    tempFile = File.createTempFile("temp", "." + hiddenData.extension);
                    tempFile.deleteOnExit();
                    img.decode(tempFile);
                }
                if (hiddenData.isEncrypted) {
                    password = PasswordPrompt.display(PasswordType.DECRYPT);
                }
                if (hiddenData.isEncrypted && hiddenData.isCompressed) {
                    File auxFile = File.createTempFile("aux", "." + hiddenData.extension);
                    auxFile.deleteOnExit();
                    AESEncryption.decrypt(tempFile, auxFile, password);
                    ZLibCompression.decompress(auxFile, file);
                } else {
                    if (hiddenData.isCompressed)
                        ZLibCompression.decompress(tempFile, file);
                    else if (hiddenData.isEncrypted)
                        AESEncryption.decrypt(tempFile, file, password);
                    else
                        img.decode(file);
                }
                if (file != null && file.length() > 0)
                    AlertBox.information("Decoding Successful!", "Document decoded in " + file.getName(), file);
            }

            else if(hiddenData.format == DataFormat.IMAGE){
                ImageInImageSteganography imgInImg = new ImageInImageSteganography(steganographicImage);
                file = fc.showSaveDialog(null);
                imgInImg.decode(file);
                AlertBox.information("Decoding Successful!", "Image decoded in " + file.getName(), file);
            }
        } catch (IOException | CannotDecodeException | UnsupportedImageTypeException e) {
            e.printStackTrace();
            AlertBox.error("Error while decoding", e.getMessage());
        }
    }


    /**
     * Gets the encryption mode from the password prompt.
     */
    public void getEncryptionPassword() {
        if (encryptMessage.isSelected() || encryptDocument.isSelected()) {
            if ((password = PasswordPrompt.display(PasswordType.ENCRYPT)) == null) {
                encryptMessage.setSelected(false);
                encryptDocument.setSelected(false);
            }
        } else {
            password = null;
        }
    }

    /** Returns the value of the encryption mode radio buttons.
     *
     * @param group radio button group.
     * @return      encryption mode (1 or 2).
     */
    private byte getToggleGroupValue(ToggleGroup group){
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        return (byte) Character.getNumericValue(selectedRadioButton.getText().charAt(0));
    }

    /**
     * Reads a document line by line and sets its contents into a <code>ListView</code>.
     *
     * @param documentView <code>JavaFX ListView</code> that will hold the document line by line
     * @param document     document to display in the ListView
     * @throws IOException if an error occurs while reading the document
     */
    private static void getDocumentContent(ListView<String> documentView, File document) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(document));
        BufferedReader reader = new BufferedReader(streamReader);//reads the user file
        String line;
        documentView.getItems().clear();
        while ((line = reader.readLine()) != null)
            documentView.getItems().add(line);
    }

    /** Undoes the last change to the {@link #secretMessage} <code>TextArea</code>. */
    public void undo() { secretMessage.undo(); }
    /** Redoes the last change to the {@link #secretMessage} <code>TextArea</code>. */
    public void redo() { secretMessage.redo(); }
    /** Cuts the content of the {@link #secretMessage} <code>TextArea</code> to the system clipboard. */
    public void cut() { secretMessage.cut(); }
    /** Copies the content of the {@link #secretMessage} <code>TextArea</code> to the system clipboard. */
    public void copy(){ secretMessage.copy();}
    /** Pastes the content of the system clipboard to the {@link #secretMessage} <code>TextArea</code>. */
    public void paste(){ secretMessage.paste(); }
    /** Deletes the selected text of the {@link #secretMessage} <code>TextArea</code>. */
    public void delete(){ secretMessage.replaceSelection(""); }
    /** Selects all the content of the {@link #secretMessage} <code>TextArea</code>. */
    public void selectAll(){ secretMessage.selectAll(); }
    /** Deselects the current {@link #secretMessage} <code>TextArea</code> selection. */
    public void deselect() { secretMessage.deselect(); }

    /** Handles the state of the menu items in the edit menu. */
    public void showingEditMenu() {
        if( systemClipboard == null ) {systemClipboard = Clipboard.getSystemClipboard();}

        if(systemClipboard.hasString()) { pasteMenu.setDisable(false); }
        else {pasteMenu.setDisable(true);}

        if(!secretMessage.getSelectedText().equals("")) {cutMenu.setDisable(false); copyMenu.setDisable(false); deselectMenu.setDisable(true); deleteMenu.setDisable(false);}
        else { cutMenu.setDisable(true); copyMenu.setDisable(true); deselectMenu.setDisable(true); deleteMenu.setDisable(true);}

        if (secretMessage.getSelectedText().equals(secretMessage.getText())) { selectAllMenu.setDisable(true); }
        else { selectAllMenu.setDisable(false); }

        if(secretMessage.isRedoable()) { redoMenu.setDisable(false); }
        else { redoMenu.setDisable(true); }

        if(secretMessage.isUndoable()) { undoMenu.setDisable(false); }
        else { undoMenu.setDisable(true); }
    }

    /** Handles switching between themes for the main scene. */
    public void setTheme(){
        if(darkTheme.isSelected())
            root.getScene().getStylesheets().add(getClass().getResource("Resources/modena_dark.css").toExternalForm());
        if(lightTheme.isSelected())
            root.getScene().getStylesheets().remove(getClass().getResource("Resources/modena_dark.css").toExternalForm());
    }

    /** Displays the About Page. */
    public void showAboutPage() {
        AboutPage.display();
    }

    /** Quits the app. */
    public void quitApp() {
        System.exit(0);
    }

}
