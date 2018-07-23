package Steganography.Modals;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class ImageViewer {

    public static void display(File image) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        Image img = new Image(image.toURI().toString());

        ImageView imgView = new ImageView(img);
        imgView.setPreserveRatio(true);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(imgView);

        imgView.fitWidthProperty().bind(layout.widthProperty());
        imgView.fitHeightProperty().bind(layout.heightProperty());

        Scene scene = new Scene(new Group());
        scene.setRoot(layout);
        window.setScene(scene);
        window.setTitle("Image");
        window.setMaxHeight(600);
        window.setMaxHeight(900);
        window.showAndWait();
    }
}
