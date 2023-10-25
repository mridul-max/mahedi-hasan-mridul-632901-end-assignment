package controller;

import database.Data;
import model.Product;

import java.util.List;

public class ProductController {
    private Data data;
    public ProductController(Data data) {
        this.data = data;
    }
    public List<Product> loadProducts() {
        return data.getProducts();
    }
    public void addProduct(Product product) {
        // Add the product to your data source (e.g., database or list)
        data.addProduct(product);
    }
    public void editProduct(Product product) {
        // Update the product in your data source (e.g., database or list)
        data.editProduct(product);
    }
    public void deleteProduct(int productId) {
        // Remove the product from your data source (e.g., database or list)
        data.deleteProduct(productId);
    }

}
