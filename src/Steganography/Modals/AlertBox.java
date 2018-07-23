package Steganography.Modals;

import Steganography.Logic.Helpers;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.File;
import java.util.Optional;

public class AlertBox {

    public static void error(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void information(String header, String content, File file) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType viewButton = new ButtonType("View");
        ButtonType cancelButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(viewButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == viewButton){
            String extension = Helpers.getFileExtension(file);
            if(extension.matches("png|bmp|jpg|jpeg|gif"))
                ImageViewer.display(file);
            else
                DocumentViewer.display(file);
        }
    }

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
