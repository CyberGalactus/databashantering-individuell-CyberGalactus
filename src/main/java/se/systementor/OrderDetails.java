package se.systementor;

import java.time.LocalDateTime;

public class OrderDetails {
    private int OrderDetailsID;
    private String name;
    private LocalDateTime orderDate;
    private double totalPrice;

    public OrderDetails() {}

    public OrderDetails(String name, double totalPrice) {
        this.name = name;
        this.orderDate = LocalDateTime.now();
        this.totalPrice = totalPrice;
    }

    public int getOrderDetailsID() {
        return OrderDetailsID;
    }

    public void setOrderDetailsID(int orderDetailsID) {
        OrderDetailsID = orderDetailsID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
