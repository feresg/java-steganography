package Steganography.Modals;

import Steganography.Types.PasswordType;

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


/**
 * The {@code PasswordPrompt} class is used to display a password prompt to the user when embedding data in an image or extracting it from an image
 * in a <code>JavaFX</code> application modal window.
 */
public class PasswordPrompt {

    /** Error label displayed when {@link #validatePassword(String, String)} returns <code>false</code>. */
    private static String errorLabelText;
    /** Encryption or decryption password. */
    private static String password;

    /**
     * Displays the password prompt application modal window.
     *
     * @param mode password mode (ENCRYPTION or DECRYPTION)
     * @return     the encryption/decryption password.
     * @see        PasswordType
     */
    public static String display(PasswordType mode) {
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

        // Error Label
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
            if(mode == PasswordType.ENCRYPT) {
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
            else if(mode == PasswordType.DECRYPT) {
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
        if(mode == PasswordType.ENCRYPT) {
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

    /**
     * Checks if the encryption password isn't empty
     * or if the password and password confirmation fields are equal
     *
     * @param pass        value of the password box
     * @param confirmPass value of the confirm password box
     * @return            <code>true</code> if password is valid, <code>false</code> if password is invalid.
     */
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
