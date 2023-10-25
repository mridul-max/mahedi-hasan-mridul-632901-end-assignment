package model;

public class Product {

    private int id;
    private String name;
    private String category;
    private double price;
    private int stockQuantity;
    private String description;

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

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }
    public String getDescription() {
        return description;
    }

}
