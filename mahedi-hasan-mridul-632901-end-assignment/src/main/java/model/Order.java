package model;

public class Order {

    private String id;
    private Customer customer;
    private List<Product> products;
    private double totalAmount;
    private OrderStatus orderStatus;

    public Order(String id, Customer customer, List<Product> products, double totalAmount, OrderStatus orderStatus) {
        this.id = id;
        this.customer = customer;
        this.products = products;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
