package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainWindow extends Application {

    private Stage stage;
    private BorderPane mainLayout;
    private VBox sideMenu;
    private StackPane contentArea;

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
        Label label = new Label(content);
        contentArea.getChildren().add(label);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
