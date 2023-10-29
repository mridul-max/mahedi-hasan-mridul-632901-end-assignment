package model;

import java.io.Serializable;

public class Product implements Serializable {

    private int id;
    private String name;
    private String category;
    private double price;
    private int stockQuantity;

    private String description;

    public Product(){

    }

    public Product(int id, int stockQuantity, String name, String category, double price, String description) {
        this.id = id;
        this.stockQuantity = stockQuantity;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description= description;

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
