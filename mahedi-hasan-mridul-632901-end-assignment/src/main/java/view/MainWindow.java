package view;

import controller.ProductController;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MainWindow extends Application {

    private Stage stage;
    private BorderPane mainLayout;
    private VBox sideMenu;
    private StackPane contentArea;
    private Person loggedInPerson;
    private ProductController productController;
    private Label errorMessageLabel;
    private TableView<Product> productInventoryTable = new TableView<>();
    private ObservableList<Product> products = FXCollections.observableArrayList(); // Observable list for products

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
        VBox productOrderLayout = createProductOrderTable(); // Change the return type

        VBox customerForm = new VBox(10);
        customerForm.getChildren().addAll(customerInformationSection, productOrderLayout);

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

    private VBox createProductOrderTable() {
        TableView<ProductOrder> productOrderTable = new TableView<>();
        TableColumn<ProductOrder, Integer> productQuantityCol = new TableColumn<>("Quantity");
        productQuantityCol.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));

        TableColumn<ProductOrder, String> productNameCol = new TableColumn<>("Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<ProductOrder, String> productCategoryCol = new TableColumn<>("Category");
        productCategoryCol.setCellValueFactory(new PropertyValueFactory<>("productCategory"));

        TableColumn<ProductOrder, Double> productPriceCol = new TableColumn<>("Price");
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));

        productOrderTable.getColumns().addAll(productQuantityCol, productNameCol, productCategoryCol, productPriceCol);

        Button addProductButton = new Button("Add Product");
        Button deleteProductButton = new Button("Delete Product");
        Button createOrderButton = new Button("Create Order");

        // Add action handlers for the buttons (implement these methods as needed)
        addProductButton.setOnAction(e -> handleAddProduct());
        deleteProductButton.setOnAction(e -> handleDeleteProduct());
        createOrderButton.setOnAction(e -> handleCreateOrder());

        HBox buttonLayout = new HBox(10); // Use HBox for buttons
        buttonLayout.getChildren().addAll(addProductButton, deleteProductButton, createOrderButton);

        VBox productOrderLayout = new VBox(10);
        productOrderLayout.getChildren().addAll(productOrderTable, buttonLayout);

        return productOrderLayout;
    }

    // Implement action handlers for the buttons
    private void handleAddProduct() {
        // Implement the logic for adding a product
    }

    private void handleDeleteProduct() {
        // Implement the logic for deleting a product
    }

    private void handleCreateOrder() {
        // Implement the logic for creating an order
    }

    private void displayProductInventory() {
        contentArea.getChildren().clear();

        if (productInventoryTable.getItems().isEmpty()) {
            // Load and display the product inventory table
            productInventoryTable = createProductInventoryTable();
            productInventoryTable.setItems(productController.loadProducts());
        }
        // Create buttons for adding, editing, and deleting products
        HBox buttonContainer = createButtonContainer();

        // Create the error message label
        errorMessageLabel = new Label("Please select a product to delete.");
        errorMessageLabel.getStyleClass().add("error-label");
        errorMessageLabel.setVisible(false);

        // Add components to the content
        VBox productInventoryContent = new VBox(10);
        productInventoryContent.getChildren().addAll(productInventoryTable, buttonContainer, errorMessageLabel);
        productInventoryContent.setAlignment(Pos.CENTER);


        contentArea.getChildren().add(productInventoryContent);
    }

    private TableView<Product> createProductInventoryTable() {
        TableColumn<Product, Integer> stockLevelCol = new TableColumn<>("Stock");
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        TableColumn<Product, String> descriptionCol = new TableColumn<>("Description");

        if (products.isEmpty()) {
            // Populate the products list only if it's empty
            products.addAll(productController.loadProducts());
        }

        // Set the items of the productInventoryTable to the observable products list
        productInventoryTable.setItems(products);

        stockLevelCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockQuantity()).asObject());
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        priceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        productInventoryTable.getColumns().addAll(stockLevelCol,nameCol, categoryCol, priceCol,descriptionCol);

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
        Dialog<Product> dialog = createProductDialog();
        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(newProduct -> handleNewProductAddition(newProduct));
    }

    private void handleNewProductAddition(Product newProduct) {
        productController.addProduct(newProduct);
        products.add(newProduct);
    }

    private Dialog<Product> createProductDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Please enter the product details");

        List<TextField> fields = List.of(
                createTextField("Stock"),
                createTextField("Name"),
                createTextField("Category"),
                createTextField("Price"),
                createTextField("Description")
        );

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, cancelButton);

        GridPane grid = new GridPane();
        grid.add(new Label("Stock:"), 0, 0);
        grid.add(fields.get(0), 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(fields.get(1), 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(fields.get(2), 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(fields.get(3), 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(fields.get(4), 1, 4);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    Product newProduct = createProductFromFields(fields);
                    return newProduct;
                } catch (NumberFormatException e) {
                    showErrorMessage("Invalid input. Please check your entries.");
                }
            }
            return null;
        });

        return dialog;
    }

    private Product createProductFromFields(List<TextField> fields) {
        Product newProduct = new Product();
        newProduct.setStockQuantity(Integer.parseInt(fields.get(0).getText()));
        newProduct.setName(fields.get(1).getText());
        newProduct.setCategory(fields.get(2).getText());
        newProduct.setPrice(Double.parseDouble(fields.get(3).getText()));
        newProduct.setDescription(fields.get(4).getText());
        return newProduct;
    }


    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }



    private void showErrorMessage(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
    }

    private void handleEditProductButtonClick() {
        // Obtain the selected product from the TableView
        Product selectedProduct = productInventoryTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Create a dialog for editing the selected product
            Dialog<Product> dialog = createEditProductDialog(selectedProduct);

            Optional<Product> result = dialog.showAndWait();

            result.ifPresent(editedProduct -> {
                // Update the selected product with the edited values
                selectedProduct.setStockQuantity(editedProduct.getStockQuantity());
                selectedProduct.setName(editedProduct.getName());
                selectedProduct.setCategory(editedProduct.getCategory());
                selectedProduct.setPrice(editedProduct.getPrice());
                selectedProduct.setDescription(editedProduct.getDescription());

                // Update the product in the database or your data source if necessary
                productController.editProduct(selectedProduct);

            });
        } else {
            // Show the error message since no product is selected
            errorMessageLabel.setVisible(true);
        }
    }

    private Dialog<Product> createEditProductDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit product details");

        // Create text fields with the current product details
        List<TextField> fields = createEditProductFields(product);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        GridPane grid = createProductGrid(fields);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                try {
                    Product editedProduct = createProductFromFields(fields);
                    return editedProduct;
                } catch (NumberFormatException e) {
                    showErrorMessage("Invalid input. Please check your entries.");
                }
            }
            return null;
        });

        return dialog;
    }

    private List<TextField> createEditProductFields(Product product) {
        List<TextField> fields = new ArrayList<>();

        TextField stockField = createTextField("Stock");
        stockField.setText(String.valueOf(product.getStockQuantity()));

        TextField nameField = createTextField("Name");
        nameField.setText(product.getName());

        TextField categoryField = createTextField("Category");
        categoryField.setText(product.getCategory());

        TextField priceField = createTextField("Price");
        priceField.setText(String.valueOf(product.getPrice()));

        TextField descriptionField = createTextField("Description");
        descriptionField.setText(product.getDescription());

        fields.add(stockField);
        fields.add(nameField);
        fields.add(categoryField);
        fields.add(priceField);
        fields.add(descriptionField);

        return fields;
    }

    private GridPane createProductGrid(List<TextField> fields) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        for (int i = 0; i < fields.size(); i++) {
            grid.add(new Label(fields.get(i).getPromptText() + ":"), 0, i);
            grid.add(fields.get(i), 1, i);
        }

        return grid;
    }



    private void handleDeleteProductButtonClick() {
        // Obtain the selected product from the TableView
        Product selectedProduct = productInventoryTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // A product is selected, proceed with deletion.
            int selectedProductId = selectedProduct.getId();
            productController.deleteProduct(selectedProductId);

            // Remove the selected product from the observable list
            products.remove(selectedProduct);

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
