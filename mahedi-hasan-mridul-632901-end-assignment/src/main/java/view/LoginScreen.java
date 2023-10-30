package view;
import controller.OrderController;
import controller.ProductController;
import exception.AccountLockedException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import controller.LoginController;
import database.Data;
import model.Person;

import java.io.FileNotFoundException;

public class LoginScreen extends Application {

    private Stage stage;
    private Scene scene;
    private Label usernameLabel;
    private TextField usernameField;
    private Label passwordLabel;
    private Label errorLabel;
    private PasswordField passwordField;
    private Button loginButton;
    private GridPane layout;
    private LoginController loginController;
    private ProductController  productController;
    private OrderController orderController;
    private int loginAttempts = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public LoginScreen() throws FileNotFoundException {
        // Initialize the Data and LoginController
        Data data = new Data();
        loginController = new LoginController(data);
        productController = new ProductController(data);
        orderController = new OrderController(data);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        createUI();
        configureLayout();
        setupEventHandlers();

        stage.setTitle("Login Screen");
        stage.setScene(scene);

        // Show the login window
        stage.show();
    }

    private void createUI() {
        // Create the username label and field
        usernameLabel = new Label("Username:");
        usernameLabel.getStyleClass().add("login-label");
        usernameField = new TextField();
        usernameField.getStyleClass().add("login-input");

        // Create the password label and field
        passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("login-label");
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("login-input");

        // Create the login button
        loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");

        // Create the error label for displaying error messages
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        // Add the controls to a GridPane layout
        layout = new GridPane();
        layout.add(usernameLabel, 0, 0);
        layout.add(usernameField, 1, 0);
        layout.add(passwordLabel, 0, 1);
        layout.add(passwordField, 1, 1);
        layout.add(loginButton, 1, 2);
        layout.add(errorLabel, 1, 3);
        // Create the scene
        scene = new Scene(layout);

        // Load the CSS file
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    private void configureLayout() {
        // Set constraints to make the GridPane larger and remove gaps
        layout.setPrefSize(600, 400);
        layout.setHgap(10);
        layout.setVgap(10);

        // Center the GridPane in the scene
        layout.setAlignment(Pos.CENTER);
    }

    private void setupEventHandlers() {
        // Add an event listener to the login button
        loginButton.setOnAction(event -> {
            try {
                handleLogin();
            } catch (AccountLockedException e) {
                displayAccountLockDialog();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void handleLogin() throws AccountLockedException, Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            throw new AccountLockedException("Account locked due to too many incorrect login attempts.");
        }

        // Use the LoginController to validate the login
        Person loggedInPerson = loginController.login(username, password);

        if (loggedInPerson != null) {
            // If login is successful, show the main window with the logged-in Person
            showMainWindow(loggedInPerson);
        } else {
            // Increment the login attempts and display an error message
            loginAttempts++;
            errorLabel.setText("Invalid username/password combination. Attempts left: " + (MAX_LOGIN_ATTEMPTS - loginAttempts));
        }
    }

    public void showMainWindow(Person loggedInPerson) throws Exception {
        // Create the main window
        MainWindow mainWindow = new MainWindow(loggedInPerson, productController, orderController);
        mainWindow.start(stage);
    }

    private void displayAccountLockDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Account Locked");
        alert.setHeaderText("Account Locked");
        alert.setContentText("Your account has been locked due to too many incorrect login attempts.");
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();

        // Close the application after displaying the alert
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
