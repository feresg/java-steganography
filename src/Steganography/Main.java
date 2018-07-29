package Steganography;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

/**
 * Entry point of the StegIt app. The <code>JavaFX</code> runtime handles the launch of the application.
 */
public class Main extends Application {

    /**
     * Application entry point.
     * Generates the main StegIt window from the layout.fxml file and displays it.
     *
     * @param primaryStage   application main stage
     * @throws IOException   if an error occurs while loading resources.
     */
    @Override
    public void start(Stage primaryStage) throws IOException{
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

    /**
     * The main() method is ignored in correctly deployed <code>JavaFX</code> application.
     * main() serves only as fallback in case the application can not be launched
     * through deployment artifacts, e.g., in IDEs with limited <code>JavaFX</code> support.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
