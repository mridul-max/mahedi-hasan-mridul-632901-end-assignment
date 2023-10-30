package view;

import controller.OrderController;
import controller.ProductController;
import database.Data;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainWindow extends Application {

    private Stage stage;
    private BorderPane mainLayout;
    private VBox sideMenu;
    private StackPane contentArea;
    private Person loggedInPerson;
    private ProductController productController;
    private OrderController orderController;
    private TextField customerFirstNameTextField;
    private TextField customerLastNameTextField;
    private TextField customerEmailTextField;
    private TextField customerPhoneNumberTextField;
    private Label errorMessageLabel;
    private TextField searchField = new TextField();
    // Create the TableView for displaying ordered products
    TableView<ProductOrder> orderedProductsTable = new TableView<>();
    private TableView<Product> productInventoryTable = new TableView<>();
    private TableView<ProductOrder> productOrderTable;
    private FilteredList<Product> filteredProducts;
    // Create a TableView for displaying available products
    private TableView<Product> productTable = new TableView<>();
    private Data data;
    private ObservableList<ProductOrder> selectedProducts = FXCollections.observableArrayList();
    private ObservableList<Product> products = FXCollections.observableArrayList(); // Observable list for products

    public MainWindow(Person loggedInPerson, ProductController productController, OrderController orderController) throws FileNotFoundException {
        data = new Data();
        this.loggedInPerson = loggedInPerson;
        this.productController = productController;
        this.orderController = orderController;
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
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            // Save data when the application is closing
            try {
                data.saveDataToFileOnExit();
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately, e.g., log the error.
            }
            Platform.exit();
        });
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
        } else if("Order History Content".equals(content)){
            displayOrderHistory();
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

        customerFirstNameTextField = new TextField();
        customerLastNameTextField = new TextField();
        customerEmailTextField = new TextField();
        customerPhoneNumberTextField = new TextField();

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
        productOrderTable = new TableView<>();

        TableColumn<ProductOrder, String> productNameCol = new TableColumn<>("Name");
        productNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));

        TableColumn<ProductOrder, String> productCategoryCol = new TableColumn<>("Category");
        productCategoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<ProductOrder, Double> productPriceCol = new TableColumn<>("Price");
        productPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

        TableColumn<ProductOrder, Integer> productQuantityCol = new TableColumn<>("Quantity");
        productQuantityCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
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

    private void handleAddProduct() {
        TextField quantityField = createQuantityTextField();
        Dialog<Product> dialog = createProductSelectionDialog(quantityField);
        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(selectedProduct -> {
            int quantity = getSelectedQuantity(quantityField);
            ProductOrder productOrder = new ProductOrder(
                    selectedProduct.getName(),
                    selectedProduct.getCategory(),
                    selectedProduct.getPrice(),
                    quantity
            );
            productOrder.setQuantity(quantity);
            selectedProducts.add(productOrder);
            productOrderTable.setItems(selectedProducts);
        });
    }

    private Dialog<Product> createProductSelectionDialog(TextField quantityField) {
        GridPane grid = createDialogGrid(quantityField);

        // Initialize filteredProducts
        filteredProducts = new FilteredList<>(productController.loadProducts(), p -> true);

        // Create a VBox to hold the search field and the product list
        VBox dialogContent = new VBox();
        TextField searchField = new TextField(); // Create the search field
        dialogContent.getChildren().addAll(searchField, grid); // Add searchField and grid to the VBox

        // Attach a listener to the search field to update the product list
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProductList(newValue);
        });

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Select Product");
        dialog.setHeaderText("Select a product to add to the order");

        ButtonType addButton = new ButtonType("Add to Order", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                Product selectedProduct = getSelectedProduct(grid);
                if (selectedProduct != null) {
                    return selectedProduct;
                }
            }
            return null;
        });

        dialog.getDialogPane().setContent(dialogContent); // Set the content to dialogContent
        return dialog;
    }

    private void filterProductList(String searchText) {
        if (searchText.length() >= 3) {
            filteredProducts.setPredicate(product -> {
                String name = product.getName().toLowerCase();
                return name.contains(searchText.toLowerCase());
            });
        } else {
            // Reset the predicate to show all products when the search text is less than 3 characters
            filteredProducts.setPredicate(product -> true);
        }

        // Set the items of the productTable to the filtered products
        productTable.setItems(filteredProducts);
    }


    private GridPane createDialogGrid(TextField quantityField) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TableView<Product> productTable = createProductTableView();

        grid.add(new Label("Select a product:"), 0, 0);
        grid.add(productTable, 0, 1, 2, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        return grid;
    }


    private Product getSelectedProduct(GridPane grid) {
        TableView<Product> productTable = (TableView<Product>) grid.getChildren().get(1);
        return productTable.getSelectionModel().getSelectedItem();
    }

    private TextField createQuantityTextField() {
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        return quantityField;
    }

    private int getSelectedQuantity(TextField quantityField) {
        try {
            String input = quantityField.getText();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // Handle the case where the input is not a valid integer
            return 0; // You can return a default value or show an error message
        }
    }


    private TableView<Product> createProductTableView() {
        // Define the table columns
        TableColumn<Product, Integer> stockLevelCol = new TableColumn<>("Stock");
        stockLevelCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockQuantity()).asObject());

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());

        TableColumn<Product, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        // Set the items of the table to the available products
        productTable.setItems(productController.loadProducts()); // Use your available products list

        // Add the columns to the table
        productTable.getColumns().addAll(stockLevelCol, nameCol, categoryCol, priceCol, descriptionCol);

        return productTable;
    }



    private void handleDeleteProduct() {
        // Get the selected row from the TableView
        int selectedIndex = productOrderTable.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            // Remove the selected product from the TableView
            productOrderTable.getItems().remove(selectedIndex);
        } else {
           showErrorMessage("please select the product if you not gonna buy");
        }
    }

    private void handleCreateOrder() {
        // Collect customer information
        String customerFirstName = customerFirstNameTextField.getText();
        String customerLastName = customerLastNameTextField.getText();
        String customerEmail = customerEmailTextField.getText();
        String customerPhoneNumber = customerPhoneNumberTextField.getText();

        // Create a new Order instance
        Order order = new Order();
        order.setCustomerFirstName(customerFirstName);
        order.setCustomerLastName(customerLastName);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhoneNumber(customerPhoneNumber);
        order.setProducts(productOrderTable.getItems()); // Assuming you have products in the TableView

        // Set the creation date
        order.setCreationTime(new Date()); // Current date and time
        orderController.addOrder(order);
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

        Button importButton = new Button("Import Products");
        importButton.setOnAction(event -> handleImportButtonClick());

        HBox buttonContainer = new HBox(10); // Horizontal container
        buttonContainer.getChildren().addAll(addProductButton, editProductButton, deleteProductButton, importButton);

        return buttonContainer;
    }
    private void handleImportButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(stage); // Use 'stage' here

        if (selectedFile != null) {
            try {
                List<Product> importedProducts = readProductsFromCSV(selectedFile);
                productController.addProducts(importedProducts);
                products.addAll(importedProducts);
                productInventoryTable.setItems(products);
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately, e.g., show an error message.
            }
        }
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
    private List<Product> readProductsFromCSV(File file) throws IOException {
        List<Product> importedProducts = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            // Skip the header row
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(";");

                if (data.length == 5) { // Check if there are 5 columns in the CSV
                    String name = data[0];
                    String category = data[1];
                    double price = Double.parseDouble(data[2]);
                    String description = data[3];
                    int stockQuantity = Integer.parseInt(data[4]);
                    Product product = new Product(1, stockQuantity, name, category, price, description);
                    importedProducts.add(product);
                }
            }
        }

        return importedProducts;
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
    private void displayOrderHistory() {
        // Create the main container for the Order History section
        VBox orderHistoryContainer = new VBox(10);
        orderHistoryContainer.getStyleClass().add("order-history-content");

        // Create the title label for the Order History section
        Label orderHistoryTitle = new Label("Order History");
        orderHistoryTitle.getStyleClass().add("title-label");

        // Create the TableView for displaying order history
        TableView<Order> orderHistoryTable = createOrderHistoryTable();

        // Create the title label for the Ordered Products section
        Label orderedProductsTitle = new Label("Ordered Products");
        orderedProductsTitle.getStyleClass().add("subtitle-label");

        // Create the TableView for displaying ordered products
        TableView<ProductOrder> orderedProductsTable = createOrderedProductsTable();

        // Add components to the Order History container
        orderHistoryContainer.getChildren().addAll(orderHistoryTitle, orderHistoryTable, orderedProductsTitle, orderedProductsTable);
        orderHistoryContainer.setAlignment(Pos.CENTER);

        // Set the Order History container as the content for the right region
        contentArea.getChildren().clear();
        contentArea.getChildren().add(orderHistoryContainer);
    }

    private TableView<Order> createOrderHistoryTable() {

        TableView<Order> orderHistoryTable = new TableView<>();
        // Define the table columns
        TableColumn<Order, String> dateTimeCol = new TableColumn<>("Date/Time");
        dateTimeCol.setCellValueFactory(cellData -> {
            Date creationTime = cellData.getValue().getCreationTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateFormat.format(creationTime);
            return new SimpleStringProperty(formattedDateTime);
        });

        TableColumn<Order, String> customerNameCol = new TableColumn<>("Name");
        customerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerFirstName()));

        TableColumn<Order, Double> totalPriceCol = new TableColumn<>("Total Price");
        totalPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().calculateTotalPrice()).asObject());

        // Set the items of the table to your list of orders
        orderHistoryTable.setItems(orderController.getOrders()); // Replace with your order data source

        // Add the columns to the table
        orderHistoryTable.getColumns().addAll(dateTimeCol, customerNameCol, totalPriceCol);
        orderHistoryTable.setOnMouseClicked(event -> {
            Order selectedOrder = orderHistoryTable.getSelectionModel().getSelectedItem();
            if (selectedOrder != null) {
                // Get the products associated with the selected order
                List<ProductOrder> orderedProducts = selectedOrder.getProducts();
                // Update the items of orderedProductsTable with the products from selected order
                orderedProductsTable.setItems(FXCollections.observableArrayList(orderedProducts));
            }
        });
        return orderHistoryTable;
    }

    private TableView<ProductOrder> createOrderedProductsTable() {

        // Define the table columns for ordered products
        TableColumn<ProductOrder, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        TableColumn<ProductOrder, String> productNameCol = new TableColumn<>("Name");
        productNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));

        TableColumn<ProductOrder, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<ProductOrder, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

        // Set the items of the table to an empty list initially
        orderedProductsTable.setItems(FXCollections.observableArrayList());

        // Add the columns to the table
        orderedProductsTable.getColumns().addAll(quantityCol, productNameCol, categoryCol, priceCol);

        return orderedProductsTable;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
