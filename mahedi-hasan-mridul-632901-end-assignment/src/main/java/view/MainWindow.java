package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Person;
import model.ProductOrder;
import model.Role;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainWindow extends Application {

    private Stage stage;
    private BorderPane mainLayout;
    private VBox sideMenu;
    private StackPane contentArea;
    private Person loggedInPerson;

    public MainWindow(Person loggedInPerson) {
        this.loggedInPerson = loggedInPerson;
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Main Window");

        mainLayout = new BorderPane();

        createSideMenu();
        contentArea = new StackPane();

        mainLayout.setLeft(sideMenu);
        mainLayout.setCenter(contentArea);

        setContent("Dashboard Content");

        Scene scene = new Scene(mainLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);

        stage.show();
    }

    private void createSideMenu() {
        sideMenu = new VBox(10);
        sideMenu.getStyleClass().add("side-menu");

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
            displayDashboard();
        } else if ("Create Order Content".equals(content) && loggedInPerson.getRole() == Role.SALESPERSON) {
            displayCreateOrder();
        } else {
            Label label = new Label(content);
            contentArea.getChildren().add(label);
        }
    }

    private void displayDashboard() {
        VBox dashboardContent = new VBox(10);
        dashboardContent.getStyleClass().add("dashboard-content");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = dateFormat.format(new Date());

        Label nameLabel = new Label("Welcome: " + loggedInPerson.getFirstName() + " " + loggedInPerson.getLastName() + "!");
        Label roleLabel = new Label("Your Role is: " + loggedInPerson.getRole());
        Label dateTimeLabel = new Label("It is now: " + dateTime);

        VBox labelsContainer = new VBox(10);
        labelsContainer.setAlignment(Pos.CENTER);
        labelsContainer.getChildren().addAll(nameLabel, roleLabel, dateTimeLabel);

        dashboardContent.getChildren().add(labelsContainer);

        StackPane.setAlignment(dashboardContent, Pos.CENTER);
        contentArea.getChildren().add(dashboardContent);
    }

    private void displayCreateOrder() {
        // Create Order Layout
        GridPane customerInformationSection = createCustomerInformationSection();

        // Create the TableView for product orders
        TableView<ProductOrder> productOrderTable = createProductOrderTable();

        VBox customerForm = new VBox(10);
        customerForm.getChildren().addAll(customerInformationSection, productOrderTable);

        StackPane createOrderContent = new StackPane();
        createOrderContent.getStyleClass().add("create-order-content");
        createOrderContent.getChildren().add(customerForm);

        contentArea.getChildren().add(createOrderContent);
    }

    private GridPane createCustomerInformationSection() {
        GridPane customerInformationSection = new GridPane();
        customerInformationSection.setVgap(10);

        Label title = new Label("Create Order");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Customer Information");
        subtitle.getStyleClass().add("subtitle-label");

        Label customerFirstNameLabel = new Label("First Name:");
        Label customerLastNameLabel = new Label("Last Name:");
        Label customerEmailLabel = new Label("Email Address:");
        Label customerPhoneNumberLabel = new Label("Phone Number:");

        TextField customerFirstNameTextField = new TextField("First Name");
        TextField customerLastNameTextField = new TextField("Last Name");
        TextField customerEmailTextField = new TextField("E-mail address");
        TextField customerPhoneNumberTextField = new TextField("Phone Number");

        customerInformationSection.add(title, 0, 0, 2, 1);
        customerInformationSection.add(subtitle, 0, 1, 2, 1);
        customerInformationSection.add(customerFirstNameLabel, 0, 2);
        customerInformationSection.add(customerFirstNameTextField, 1, 2);
        customerInformationSection.add(customerLastNameLabel, 0, 3);
        customerInformationSection.add(customerLastNameTextField, 1, 3);
        customerInformationSection.add(customerEmailLabel, 0, 4);
        customerInformationSection.add(customerEmailTextField, 1, 4);
        customerInformationSection.add(customerPhoneNumberLabel, 0, 5);
        customerInformationSection.add(customerPhoneNumberTextField, 1, 5);

        return customerInformationSection;
    }

    private TableView<ProductOrder> createProductOrderTable() {
        TableView<ProductOrder> productOrderTable = new TableView<>();

        TableColumn<ProductOrder, String> productNameCol = new TableColumn<>("Product Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<ProductOrder, String> productCategoryCol = new TableColumn<>("Product Category");
        productCategoryCol.setCellValueFactory(new PropertyValueFactory<>("productCategory"));

        TableColumn<ProductOrder, Integer> productQuantityCol = new TableColumn<>("Product Quantity");
        productQuantityCol.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));

        productOrderTable.getColumns().addAll(productNameCol, productCategoryCol, productQuantityCol);

        return productOrderTable;
    }


    public static void main(String[] args) {
        launch(args);
    }
}