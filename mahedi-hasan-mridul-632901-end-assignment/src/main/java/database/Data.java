package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Order;
import model.Person;
import model.Product;
import model.Role;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Data implements Serializable {
    private List<Person> persons = new ArrayList<>();
    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private int lastProductId = 0;
    private int lastOrderId = 0;
    private String dataFilePath = getClass().getClassLoader().getResource("data.dat").getFile();

    public Data() throws FileNotFoundException {
        loadFromSerializedFile(dataFilePath);
        if (persons.isEmpty() && products.isEmpty()) {
            initializeSampleData();
        }
    }
    private void initializeSampleData() {
        // Add some sample users to the database
        persons.add(new Person("1", "Wim", "Wiltenburg","john@example.com", "1234567890", Role.SALESPERSON, "wim", "wim"));
        persons.add(new Person("2", "Jane", "Smith","jane@example.com", "9876543210", Role.Manager, "def", "def"));
        persons.add(new Person("3", "Alice", "Johnson","alice@example.com", "5555555555", Role.Manager, "lll", "lll"));

        // Add some sample products to the database
        products.add(new Product(1, 2, "Fender Stratocaster Electric Guitar", "Guitars", 1199.99, "available"));
        products.add(new Product(2, 4, "Marshall JCM2000 DSL100 Amp", "Electric", 1299.99, "available"));

        for (Product product : products) {
            lastProductId = Math.max(lastProductId, product.getId());
        }
    }
    public ObservableList<Product> getProducts() {
        return products;
    }

    public Person findPersonByUsername(String username) {
        for (Person person : persons) {
            if (person.getUsername().equals(username)) {
                return person;
            }
        }
        return null;
    }

    public void editProduct(Product editedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == editedProduct.getId()) {
                products.set(i, editedProduct);
                return; // Exit the loop once the product is found and updated
            }
        }
    }
    public void deleteProduct(int productId) {
        products.removeIf(product -> product.getId() == productId);
    }

    public void addProduct(Product product) {
        // Increment the last assigned product ID and set it for the new product
        int nextProductId = ++lastProductId;
        product.setId(nextProductId);

        // Add the product to the list
        products.add(product);
    }
    public ObservableList<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        int nextOrderId = ++lastOrderId;
        order.setOrderId(nextOrderId);
        orders.add(order);
    }
    private void loadFromSerializedFile(String filename) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            Data loadedData = (Data) inputStream.readObject();
            persons = loadedData.persons;
            products = loadedData.products;
            orders = loadedData.orders;
            lastProductId = loadedData.lastProductId;
            lastOrderId = loadedData.lastOrderId;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); // Handle the exception appropriately, e.g., log or display an error message.
            // Fall back to initializing sample data
            initializeSampleData();
        }
    }
    public void saveToSerializedFile(String filename) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(this);
        }
    }

    public void saveDataToFileOnExit() throws IOException {
        saveToSerializedFile(dataFilePath);
    }
    public void addProducts(List<Product> products){
        for (Product p:
             products) {
            addProduct(p);
        }
    }
}
