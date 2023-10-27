package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Person;
import model.Product;
import model.Role;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<Person> persons = new ArrayList<>();
    private ObservableList<Product> products = FXCollections.observableArrayList();
    private int lastProductId = 0;
    public Data() {
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

}
