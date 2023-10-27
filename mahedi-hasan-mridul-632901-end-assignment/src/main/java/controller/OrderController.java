package controller;

import database.Data;
import model.Order;
import javafx.collections.ObservableList;

public class OrderController {
    private Data data;

    public OrderController(Data data) {
        this.data = data;
    }

    public void addOrder(Order order) {
        data.addOrder(order);
    }
    public ObservableList<Order> getOrders() {
        return data.getOrders();
    }
}
