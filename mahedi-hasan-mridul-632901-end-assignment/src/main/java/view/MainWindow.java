package view;

import controller.ProductController;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Person;
import model.Product;
import model.ProductOrder;
import model.Role;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainWindow extends Application {

    private Stage stage;
    private BorderPane mainLayout;
    private VBox sideMenu;
    private StackPane contentArea;
    private Person loggedInPerson;
    private ProductController productController;
    private Label errorMessageLabel;
    TableView<Product> productInventoryTable = new TableView<>();
    public MainWindow(Person loggedInPerson, ProductController productController) {
        this.loggedInPerson = loggedInPerson;
        this.productController = productController;
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
        } else if ("Product Inventory Content".equals(content) && loggedInPerson.getRole() == Role.Manager) {
            displayProductInventory();
        }
        else {
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

    private void displayProductInventory() {
        VBox productInventoryContent = new VBox(10);

        // Create the product inventory table
        TableView<Product> productInventoryTable = createProductInventoryTable();

        // Create buttons for adding, editing, and deleting products
        HBox buttonContainer = createButtonContainer();

        // Create the error message label
        errorMessageLabel = new Label("Please select a product to delete.");
        errorMessageLabel.getStyleClass().add("error-label");
        errorMessageLabel.setVisible(false);
        // Add components to the content
        productInventoryContent.getChildren().addAll(productInventoryTable, buttonContainer,errorMessageLabel);
        productInventoryContent.setAlignment(Pos.CENTER);

        contentArea.getChildren().add(productInventoryContent);
    }

    private TableView<Product> createProductInventoryTable() {
        TableColumn<Product, Integer> stockLevelCol = new TableColumn<>("Stock");
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        TableColumn<Product, String> descriptionCol = new TableColumn<>("Description");

        List<Product> products = productController.loadProducts();
        productInventoryTable.getItems().addAll(products);

        stockLevelCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockQuantity()).asObject());
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        priceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        productInventoryTable.getColumns().addAll(nameCol, categoryCol, priceCol, stockLevelCol);

        return productInventoryTable;
    }

    private HBox createButtonContainer() {
        Button addProductButton = new Button("Add Product");
        addProductButton.setOnAction(event -> handleAddProductButtonClick());

        Button editProductButton = new Button("Edit Product");
        editProductButton.setOnAction(event -> handleEditProductButtonClick());

        Button deleteProductButton = new Button("Delete Product");
        deleteProductButton.setOnAction(event -> handleDeleteProductButtonClick());

        HBox buttonContainer = new HBox(10); // Horizontal container
        buttonContainer.getChildren().addAll(addProductButton, editProductButton, deleteProductButton);

        return buttonContainer;
    }
    private void handleAddProductButtonClick() {
        // Implement the logic for adding a product
    }

    private void handleEditProductButtonClick() {
        // Implement the logic for editing a product
    }

    private void handleDeleteProductButtonClick() {
        // Obtain the selected product from the TableView
        Product selectedProduct = productInventoryTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // A product is selected, proceed with deletion.
            int selectedProductId = selectedProduct.getId();
            productController.deleteProduct(selectedProductId);

            // Remove the selected product from the TableView.
            productInventoryTable.getItems().remove(selectedProduct);
            // Hide the error message label (if it was visible)
            errorMessageLabel.setVisible(false);
        } else {
            // Show the error message since no product is selected
            errorMessageLabel.setVisible(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}