package Steganography;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Resources/layout.fxml"));
        Image icon = new Image(Controller.class.getResource("Resources/logo.png").toExternalForm(), false);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Resources/stylesheet.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("StegIt");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
