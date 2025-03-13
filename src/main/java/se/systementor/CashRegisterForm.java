package se.systementor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimerTask;

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
    //mina AWS Access Key och Secret Key var synliga i koden,
    // vilket är en stor säkerhetsrisk. Dessa nycklar kan användas av obehöriga för att få åtkomst till ditt AWS-konto.
    //---------------------------------------------------
    //steg 1 i terminalen
    //export AWS_ACCESS_KEY_ID="AKIAQQABDNYHEOVTNEBE"
    //export AWS_SECRET_ACCESS_KEY="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"
    //---------------------------------------------------
    //steg 2 för att verifera att det används medans terminalen är öppen
    //printenv | grep AWS
    // om allt fungerar så der man det här i terminalen
    //AWS_ACCESS_KEY_ID=AKIAQQABDNYHEOVTNEBE
    //AWS_SECRET_ACCESS_KEY=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
    //---------------------------------------------------
    //Gör nycklarna permanenta även när man stänger terminalen --- har inte gjort
    //echo 'export AWS_ACCESS_KEY_ID="AKIAQQABDNYHEOVTNEBE"' >> ~/.zshrc
    //echo 'export AWS_SECRET_ACCESS_KEY="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"' >> ~/.zshrc
    //source ~/.zshrc
    private String AccessKey = System.getenv("AWS_ACCESS_KEY_ID");
    private String SecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");




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
        JButton buttonStat = new JButton("Dagsstatistik");
        buttonsPanel.add(buttonStat);
//        buttonStat

        //terminalens miljövariabler laddades inte in automatiskt. när jag skrev
        // det här
        System.out.println("AWS_ACCESS_KEY_ID: " + AccessKey);
        System.out.println("AWS_SECRET_ACCESS_KEY: " + SecretKey);
        // jag stände intelij helt och skrev i min öppna teminal
        // open -a "IntelliJ IDEA"
        //---------------------------------------------------
        // Nu ger System.getenv("AWS_ACCESS_KEY_ID") rätt värde!


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

                Timer timer = new Timer(3000,e1 -> {
                    receiptArea.setText("");

                    lastClickedProduct = null;
                    summa = 0.0;
                });
                timer.setRepeats(false);
                timer.start();


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
