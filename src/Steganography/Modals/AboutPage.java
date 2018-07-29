package Steganography.Modals;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The {@code AboutPage} class creates a <code>JavaFX</code> application modal to display information about the StegIt app.
 */
public class AboutPage {

    /**
     * Displays the AboutPage modal window.
     */
    public static void display(){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        ImageView imgView = new ImageView(new Image(("Steganography/Resources/logo.png"), 150, 150, true, true));
        Label appName = new Label("StegIt");
        appName.setFont(new Font(20));
        VBox logo = new VBox();
        logo.setAlignment(Pos.CENTER);
        logo.setSpacing(6);
        logo.getChildren().addAll(imgView, appName);

        Label appCreators = new Label("Made by : Feres Gaaloul, Ilyes Hamrouni");
        appCreators.setFont(new Font(15));
        Label appDescription = new Label("The Ultimate Image Steganography App with Password Encryption.\n It Works on gifs too!");
        appDescription.setFont(new Font(12));
        appDescription.setAlignment(Pos.CENTER);
        Label appMoreInfo = new Label("ENSI Summer Project 2018");
        Label appVersion = new Label("Version 1.0.1");
        appVersion.setFont(new Font(10));
        Label appCopyright = new Label("Copyright © Feres Gaaloul, Ilyes Hamrouni 2018");
        appCopyright.setFont(new Font(10));

        VBox mainInfo = new VBox(6);
        mainInfo.setAlignment(Pos.CENTER);
        mainInfo.getChildren().addAll(appName, appCreators, appDescription, appMoreInfo);

        VBox secondaryInfo = new VBox(4);
        secondaryInfo.setAlignment(Pos.CENTER);
        secondaryInfo.getChildren().addAll(appVersion, appCopyright);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setTop(logo);
        layout.setCenter(mainInfo);
        layout.setBottom(secondaryInfo);

        Scene scene = new Scene(layout, 400, 400);
        window.setScene(scene);
        window.setTitle("About StegIt");
        window.showAndWait();
    }

}
