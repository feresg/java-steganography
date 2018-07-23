package Steganography.Modals;

import javafx.geometry.Pos;
import javafx.scene.Group;
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

public class DocumentViewer {
    public static void display(File document) {
        try{
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        ListView<String> documentView = new ListView<>();
        showSecretDocument(documentView, document);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(documentView);


        Scene scene = new Scene(layout, 600, 400);

        window.setScene(scene);
        window.setTitle("Document");
        window.showAndWait();

        } catch(IOException e) {e.printStackTrace();}
    }

    public static void showSecretDocument(ListView<String> documentView, File document) throws IOException {
        try {
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(document));
            BufferedReader reader = new BufferedReader(streamReader);//reads the user file
            String line;
            documentView.getItems().clear();
            while ((line = reader.readLine()) != null) {
                documentView.getItems().add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
