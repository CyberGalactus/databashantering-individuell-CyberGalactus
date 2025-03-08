package se.systementor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.Date;

public class CashRegisterForm {
    private JPanel panel1;
    private JPanel panelRight;
    private JPanel panelLeft;
    private JTextArea receiptArea;
    private JPanel buttonsPanel;
    private JTextField textField1;
    private JTextField textField2;
    private JButton addButton;
    private JButton payButton;
    private Database database = new Database();
    private Product lastClickedProduct = null;
    private double summa = 0.0;
    private int quantity;


    public CashRegisterForm() {
        for (Product product : database.activeProducts()) {
            JButton button = new JButton(product.getName());
            buttonsPanel.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    lastClickedProduct = product;
                    textField1.setText(product.getName());
                }
            });
        }
        payButton.addActionListener(new ActionListener() { //betalning
            @Override
            public void actionPerformed(ActionEvent e) {

                if (summa == 0.0) {
                    JOptionPane.showMessageDialog(null, "Inga varor i kvittot!", "Fel", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                receiptArea.append("\n");
                receiptArea.append("----------------------------------------------------\n");
              //  receiptArea.append("Total                                        -------\n");
                receiptArea.append("Total                                        "+ summa +"kr\n");
                receiptArea.append("----------------------------------------------------\n");
                receiptArea.append("                  TACK FÖR DITT KÖP\n");
                receiptArea.append("----------------------------------------------------\n");


                lastClickedProduct = null;
                summa = 0.0;
            }
        });
        addButton.addActionListener(new ActionListener() { // ADD
            @Override
            public void actionPerformed(ActionEvent e) {

                if (lastClickedProduct == null) {
                    JOptionPane.showMessageDialog(null, "Välj en produkt först!", "Fel", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    quantity = Integer.parseInt(textField2.getText());
                } catch (NumberFormatException ex) {
                    throw new RuntimeException(ex);
                }

                if(summa == 0)  {

                    receiptArea.append("                EDUS SUPER MEGA SHOP\n");
                    receiptArea.append("----------------------------------------------------\n");
                    receiptArea.append("\n");
                    int OrderDetailsID = database.createOrder(summa);
                    Date d = new Date();
                    receiptArea.append("Kvittonummer: " + OrderDetailsID + " Datum: " + d.toString() + "\n");
                    receiptArea.append("\n");
                    receiptArea.append("----------------------------------------------------\n");
                }

                receiptArea.append(lastClickedProduct.getName() + "                       "
                        + quantity + "*" + "      " + lastClickedProduct.getPrice() + "kr" + " = " +
                        lastClickedProduct.getPrice() * quantity + "kr\n");
                receiptArea.append("                                " + "Moms (" + lastClickedProduct.getVat() + "%): " + lastClickedProduct.getPrice() * lastClickedProduct.getVat() / 100 * quantity + "kr\n" );
                summa = summa + (quantity * lastClickedProduct.getPrice());

            }
        });
    }

    public void run() {
        JFrame frame = new JFrame("Cash Register");
        frame.setContentPane(new CashRegisterForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize( 1000, 800 ) ;


        frame.setVisible(true);



    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
