package Steganography.Modals;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * The {@code AlertBox} class is uses the <code>JavaFX Alert</code> modal to display either caught error messages
 * or various information about a successful steganography process.
 */
public class AlertBox {

    /**
     * Displays an error Alert box to display a caught error during the steganographic process.
     *
     * @param header  error header
     * @param content general information about the error (usually from the <code>getMessage</code> method from the <code>Java Exception</code> class)
     */
    public static void error(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a success prompt after embeding data into an image or extracting a document or an image inside an image.
     *
     * @param header       information header
     * @param content      success message
     * @param file         image or document to display
     * @throws IOException if an error occurs when opening the file.
     */
    public static void information(String header, String content, File file) throws IOException{
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType viewButton = new ButtonType("View");
        ButtonType cancelButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(viewButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == viewButton){
            Desktop.getDesktop().open(file);
        }
    }

    /**
     * Displays a success prompt after extracting a message inside an image.
     *
     * @param header  success header
     * @param content success message
     * @param message extracted message
     */
    public static void information(String header, String content, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);

        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane messageView = new GridPane();
        messageView.setMaxWidth(Double.MAX_VALUE);
        messageView.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(messageView);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }

}
