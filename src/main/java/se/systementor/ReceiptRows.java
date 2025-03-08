package se.systementor;

public class ReceiptRows {
    private int receiptRowsID;
    private int quantity;
    private double price;
    private double vat;
    private int orderDetailsID;
    private int productID;

    public ReceiptRows() {}

    public ReceiptRows(int quantity, double price, double vat, int orderDetailsID, int productID) {
        this.quantity = quantity;
        this.price = price;
        this.vat = vat;
        this.orderDetailsID = orderDetailsID;
        this.productID = productID;
    }

    public int getReceiptRowsID() {
        return receiptRowsID;
    }

    public void setReceiptRowsID(int receiptRowsID) {
        this.receiptRowsID = receiptRowsID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public int getOrderDetailsID() {
        return orderDetailsID;
    }

    public void setOrderDetailsID(int orderDetailsID) {
        this.orderDetailsID = orderDetailsID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }
}
