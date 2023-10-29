package model;

import java.io.Serializable;

public class ProductOrder implements Serializable {
    private String name;
    private String category;
    private double price;
    private int stock;


    private double totalPrice;

    public ProductOrder(String name, String category, double price, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Getters and setters for the fields

    public String getProductName() {
        return name;
    }

    public void setProductName(String productName) {
        this.name = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
        calculateTotalPrice();
    }

    public int getQuantity() {
        return stock;
    }

    public void setQuantity(int quantity) {
        this.stock = quantity;
        calculateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    private void calculateTotalPrice() {
        totalPrice = price * stock;
    }
}