package view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoginScreen extends Application {

    private Stage stage;
    private Scene scene;
    private Label usernameLabel;
    private TextField usernameField;
    private Label passwordLabel;
    private PasswordField passwordField;
    private Button loginButton;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        // Create the username label and field
        usernameLabel = new Label("Username:");
        usernameField = new TextField();

        // Create the password label and field
        passwordLabel = new Label("Password:");
        passwordField = new PasswordField();

        // Create the login button
        loginButton = new Button("Login");

        // Add the controls to a GridPane layout
        GridPane layout = new GridPane();
        layout.add(usernameLabel, 0, 0);
        layout.add(usernameField, 1, 0);
        layout.add(passwordLabel, 0, 1);
        layout.add(passwordField, 1, 1);
        layout.add(loginButton, 1, 2);

        // Create the scene and stage
        scene = new Scene(layout, 300, 200);
        stage.setTitle("Wim's Music Dungeon - Login");
        stage.setScene(scene);

        // Add an event listener to the login button
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Validate the username and password
                String username = usernameField.getText();
                String password = passwordField.getText();

                // If the username and password are valid, open the main window
                if (username.equals("wim") && password.equals("password")) {
                    // Create the main window
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.show();

                    // Close the login window
                    stage.close();
                } else {
                    // Display an error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Invalid username/password combination");
                    alert.show();
                }
            }
        });

        // Show the login window
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}