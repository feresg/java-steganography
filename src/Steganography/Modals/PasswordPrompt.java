package Steganography.Modals;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PasswordPrompt {

    private static String errorLabelText;
    private static String password;

    public static String display(String mode) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Enter Password");
        window.setMinWidth(400);
        window.setMinHeight(150);
        window.setOnCloseRequest(e -> password = null);

        // Password
        Label passLabel = new Label("Password:");
        GridPane.setConstraints(passLabel, 0, 0);
        PasswordField passText = new PasswordField();
        passText.setPrefWidth(230);
        GridPane.setConstraints(passText, 1, 0);

        // Confirm Password
        Label confirmPassLabel = new Label("Confirm Password:");
        GridPane.setConstraints(confirmPassLabel, 0, 1);
        PasswordField confirmPassText = new PasswordField();
        confirmPassText.setPrefWidth(230);
        GridPane.setConstraints(confirmPassText, 1, 1);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        GridPane.setConstraints(errorLabel, 1, 2);

        // Buttons:
        Button okButton = new Button("OK");
        okButton.setDefaultButton(true);
        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);

        // Button ActionEvents
        okButton.setOnAction(e -> {
            if(mode.equals("encrypt")) {
                if (validatePassword(passText.getText(), confirmPassText.getText())) {
                    password = passText.getText();
                    window.close();
                }
                else {
                    password = null;
                    errorLabel.setText(errorLabelText);
                    passText.clear();
                    confirmPassText.clear();
                }
            }
            else if(mode.equals("decrypt")) {
                password = passText.getText();
                window.close();
            }
        });

        cancelButton.setOnAction(e -> {
            password = null;
            window.close();
        });

        // GridPane with 10px padding around edge
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.getChildren().addAll(passLabel, passText, errorLabel);
        if(mode.equals("encrypt")) {
            grid.getChildren().addAll(confirmPassLabel, confirmPassText);
        }

        // HBox
        HBox base = new HBox(5);
        base.setPadding(new Insets(10));
        base.setAlignment(Pos.BASELINE_RIGHT);
        base.getChildren().addAll(okButton, cancelButton);

        // BorderPane
        BorderPane bPane = new BorderPane();
        bPane.setCenter(grid);
        bPane.setBottom(base);

        Scene scene = new Scene(bPane);
        window.setScene(scene);
        window.showAndWait();
        return password;
    }


    private static boolean validatePassword(String pass,String confirmPass) {
        boolean isValid = true;

        if(pass.equals("") || confirmPass.equals("")) {
            errorLabelText = "Both fields are required."; isValid = false;
        }
        else if(!pass.equals(confirmPass)){
            errorLabelText = "Passwords don't match!"; isValid = false;
        }
        return isValid;
    }
}
