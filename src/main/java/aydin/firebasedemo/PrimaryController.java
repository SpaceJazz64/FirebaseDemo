package aydin.firebasedemo;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class PrimaryController {

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    void handleLogin(ActionEvent event) {

        String email = emailTextField.getText().trim();

        if (email.isEmpty()) {
            showAlert("Please enter email");
            return;
        }

        try {
            // Check if user exists
            UserRecord user = DemoApp.fauth.getUserByEmail(email);

            System.out.println("Login successful: " + user.getEmail());

            // Go to next screen
            DemoApp.setRoot("quad"); // your data screen

        } catch (FirebaseAuthException | IOException e) {
            showAlert("Login failed: user not found");
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}