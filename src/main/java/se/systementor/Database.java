package se.systementor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    String url = "jdbc:mysql://localhost:3306/edusshop";
    String user = "root";
    String password = "Enamorados17";


    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }

    public List<Product> activeProducts(){
        ArrayList<Product> products = new ArrayList<Product>();

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ProductID, name, price, vat FROM Product");

            while (rs.next()) {
                Product product = new Product();
                product.setProductID(rs.getInt("ProductID"));
                product.setName(rs.getString("Name"));
                product.setPrice(rs.getDouble("price"));
                product.setVat(rs.getDouble("vat"));
                products.add(product);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public int createOrder(double totalPrice) {
        int OrderDetailsID = -1;
        String sql = "INSERT INTO OrderDetails (total_price) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, totalPrice);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                OrderDetailsID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OrderDetailsID;
    }

    public String RecieptStatistic() {
        String xml="";
        ReceiptRows receiptRows = new ReceiptRows();
        String sql = "SELECT COUNT(*) as total," +
                "MIN(OrderDate) as FirstOrderDateTime," +
                "MAX(OrderDate) as LastOrderDateTime," +
                "SUM(total_price) as TotalSalesInclVat," +
                "SUM(total_price*0.25) as TotalVat " +
                "FROM OrderDetails ";// +
                //"WHERE DATE(OrderDate) = CURDATE()";
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                int total = rs.getInt("total");
                String FirstOrderDateTime = rs.getString("FirstOrderDateTime");
                String LastOrderDateTime = rs.getString("LastOrderDateTime");
                double TotalSalesInclVat = rs.getInt("TotalSalesInclVat");
                double TotalVat = rs.getDouble("TotalVat");

                xml = "<xml>\n" +
                        "<SaleStatistics>\n" +
                        "<FirstOrderDateTime>" + FirstOrderDateTime + "</FirstOrderDateTime>\n" +
                        "\t<LastOrderDateTime>" + LastOrderDateTime + "</LastOrderDateTime>\n" +
                        "\t<TotalSalesInclVat>" + TotalSalesInclVat + "</TotalSalesInclVat>\n" +
                        "\t<TotalVat>" + TotalVat + "</TotalVat>\n" +
                        "\t<TotalNumberOfReceipts>" + total +"</TotalNumberOfReceipts>\n" +
                        "</SaleStatistics>\n" +
                        "</xml>\n";
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return xml;
    }





}
