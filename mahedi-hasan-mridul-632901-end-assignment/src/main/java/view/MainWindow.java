package view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Person;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainWindow extends Application {

    private Stage stage;
    private BorderPane mainLayout;
    private VBox sideMenu;
    private StackPane contentArea;
    private Person loggedInPerson; // Add a field to store the logged-in Person

    // Constructor that accepts the logged-in Person
    public MainWindow(Person loggedInPerson) {
        this.loggedInPerson = loggedInPerson;
    }
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Main Window");

        mainLayout = new BorderPane();

        // Create the side menu (VBox)
        createSideMenu();

        // Create the content area (StackPane)
        contentArea = new StackPane();

        // Add the side menu to the left and content area to the center
        mainLayout.setLeft(sideMenu);
        mainLayout.setCenter(contentArea);

        // Display the "Dashboard" content by default
        setContent("Dashboard Content");

        // Create the scene
        Scene scene = new Scene(mainLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); // Load CSS
        stage.setScene(scene);

        // Show the main window
        stage.show();
    }

    private void createSideMenu() {
        sideMenu = new VBox(10);
        sideMenu.getStyleClass().add("side-menu");

        // Create clickable items
        createMenuItem("Dashboard", "Dashboard Content");
        createMenuItem("Create Order", "Create Order Content");
        createMenuItem("Product Inventory", "Product Inventory Content");
        createMenuItem("Order History", "Order History Content");
    }

    private void createMenuItem(String text, String content) {
        Button menuItem = new Button(text);
        menuItem.setOnAction(event -> setContent(content));
        sideMenu.getChildren().add(menuItem);
    }

    private void setContent(String content) {
        contentArea.getChildren().clear();

        if ("Dashboard Content".equals(content)) {
            // Create a VBox to display user information and date/time
            VBox dashboardContent = new VBox(10);
            dashboardContent.getStyleClass().add("dashboard-content");
            // Get the current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(new Date());

            // Create labels to display user info and date/time
            Label nameLabel = new Label("Welcome: " + loggedInPerson.getFirstName()+ " "+ loggedInPerson.getLastName() + "!");
            Label roleLabel = new Label("Your Role is: " + loggedInPerson.getRole());
            Label dateTimeLabel = new Label("It is now: " + dateTime);

            VBox labelsContainer = new VBox(10);
            labelsContainer.setAlignment(Pos.CENTER);
            labelsContainer.getChildren().addAll(nameLabel, roleLabel, dateTimeLabel);

            dashboardContent.getChildren().add(labelsContainer);

            StackPane.setAlignment(dashboardContent, Pos.CENTER); // Center the content
            contentArea.getChildren().add(dashboardContent);
        } else {
            // Display regular content for other menu items
            Label label = new Label(content);
            contentArea.getChildren().add(label);
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
