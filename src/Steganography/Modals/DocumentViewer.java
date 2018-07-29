package Steganography.Modals;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * This {@code DocumentViewer} class is used to display a document
 * after extracting it from a stego image
 * in a <code>JavaFX</code> application modal window.
 */
public class DocumentViewer {

    /**
     * Displays the document viewer application modal window.
     *
     * @param document document to display
     */
    public static void display(File document) {
        try{
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        ListView<String> secretDocumentView = new ListView<>();
        showSecretDocument(secretDocumentView, document);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(secretDocumentView);


        Scene scene = new Scene(layout, 600, 400);
        //scene.getStylesheets().add(DocumentViewer.class.getResource("Steganography/Resources/stylesheet.css").toExternalForm());
        window.setScene(scene);
        window.setTitle("Document");
        window.showAndWait();

        } catch(IOException e) {e.printStackTrace();}
    }

    /**
     * Reads a document line by line and sets its contents into a <code>ListView</code>.
     *
     * @param documentView <code>JavaFX ListView</code> that will hold the document line by line
     * @param document     document to display in the ListView
     * @throws IOException if an error occurs while reading the document
     */
    public static void showSecretDocument(ListView<String> documentView, File document) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(document));
        BufferedReader reader = new BufferedReader(streamReader);//reads the user file
        String line;
        documentView.getItems().clear();
        while ((line = reader.readLine()) != null)
            documentView.getItems().add(line);
    }

}
