package se.systementor;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.Validate;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JButton StatistikButton;
    private Database database = new Database();
    private Product lastClickedProduct = null;
    private double summa = 0.0;
    private int quantity;

    private String AccessKey = System.getenv("AWS_ACCESS_KEY_ID");
    private String SecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
    String bucketName = "educhitest-cuchi9182737465"; //Ange ngt unikt !!!




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
//        JButton buttonStat = new JButton("Dagsstatistik");
//        buttonsPanel.add(buttonStat);




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
        StatistikButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                S3Client s3Client;

                //Terminalens miljövariabler laddades inte in automatiskt. när jag skrev
                // det här
                System.out.println("AWS_ACCESS_KEY_ID: " + AccessKey);
                System.out.println("AWS_SECRET_ACCESS_KEY: " + SecretKey);
                // jag stände intelij helt och skrev i min öppna teminal
                // open -a "IntelliJ IDEA"
                //---------------------------------------------------
                // Nu ger System.getenv("AWS_ACCESS_KEY_ID") rätt värde!
                String xml = database.RecieptStatistic();
                System.out.println(xml);
                //jag skriver ut i terminalen för att se om det funkar och det gör det
                // XML -> S3

                s3Client = S3Client.builder().
                        credentialsProvider(new AwsCredentialsProvider() {
                            @Override
                            public AwsCredentials resolveCredentials() {
                                return AwsBasicCredentials.builder().accessKeyId(AccessKey).secretAccessKey(SecretKey).build();
                            }
                        })
                        .region(Region.EU_NORTH_1)
                        .build();

                if(!doesBucketExist(bucketName,s3Client)){
                    CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                            .bucket(bucketName)
                            .build();
                    s3Client.createBucket(bucketRequest);
                }else{
                }

                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key("statitic.xml")
                        .build();

                s3Client.putObject(objectRequest, RequestBody.fromString(xml));
            }
        });
    }

    public static boolean doesBucketExist(String bucketName, S3Client s3SyncClient) {
        try {
            Validate.notEmpty(bucketName, "The bucket name must not be null or an empty string.", "");
            s3SyncClient.getBucketAcl(r -> r.bucket(bucketName));
            return true;
        } catch (AwsServiceException ase) {
            // A redirect error or an AccessDenied exception means the bucket exists but it's not in this region
            // or we don't have permissions to it.
            if ((ase.statusCode() == HttpStatusCode.MOVED_PERMANENTLY) || "AccessDenied".equals(ase.awsErrorDetails().errorCode())) {
                return true;
            }
            if (ase.statusCode() == HttpStatusCode.NOT_FOUND) {
                return false;
            }
            throw ase;
        }
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
