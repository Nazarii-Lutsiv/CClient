package soap;


import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CClientSOAP extends javax.swing.JFrame {
    private static final String DEFAULT_URL = "http://localhost:4321/chat?wsdl";

    private String url = DEFAULT_URL;
    private Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss:  ");
    private Thread showResponseThread;

    private CommandHendlerSOAP commandHendlerSOAP;

    public CClientSOAP() {
        super("CClient");
        initComponents();
        setResizable(false);

        this.commandHendlerSOAP = new CommandHendlerSOAP(DEFAULT_URL);

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // connect to server
                commandHendlerSOAP.setUrlAdress(jTextField1.getText());

                if (commandHendlerSOAP.getServerWrapper() == null) {
                    if (commandHendlerSOAP.connectToServer()) {
                        showResponse();
                        jTextArea1.append(simpleDateFormat.format(new Date()) + "Connected to server!\n");
                    } else {
                        jTextArea1.append(simpleDateFormat.format(new Date()) + "Can't connect to server!\n");
                    }
                } else {
                    jTextArea1.append(simpleDateFormat.format(new Date()) + "Can`t connect to server, connection have already done!\n");
                    return;
                }
            }
        });

        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (commandHendlerSOAP.getServerWrapper() != null && commandHendlerSOAP.getSessionId() != null) {
                    commandHendlerSOAP.close();
                    jTextArea1.append(simpleDateFormat.format(new Date()) + "Disconnected!\n");
                } else {
                    jTextArea1.append(simpleDateFormat.format(new Date()) + "Can't exit, no loginUser!\n");
                }
            }
        });

        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // send message
                sendToServer();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                commandHendlerSOAP.close();
                e.getWindow().dispose();
            }
        });

        jTextField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendToServer();
                }
            }
        });

    }

    private void sendToServer () {
        if (commandHendlerSOAP.getServerWrapper() == null) {
            jTextArea1.append(simpleDateFormat.format(new Date()) + "No connection with server" + "\n");
            return;
        } else if (commandHendlerSOAP.getServerWrapper() != null) {
            commandHendlerSOAP.commandExecute(jTextField2.getText());
            jTextArea1.append(simpleDateFormat.format(new Date()) + jTextField2.getText() + "\n");
        }
    }

    private synchronized void showResponse() {
        showResponseThread = new Thread(() -> {
            while (commandHendlerSOAP.getServerWrapper() != null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (commandHendlerSOAP.getResponseInfo() != null && !commandHendlerSOAP.getResponseInfo().equals("")) {
                    jTextArea1.append(simpleDateFormat.format(new Date()) + commandHendlerSOAP.getResponseInfo() + "\n");
                    commandHendlerSOAP.setResponseInfo(null);
                }
            }
        });
        showResponseThread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();



        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();

        jTextArea1.setEditable(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextField1.setText("http://localhost:4321/chat?wsdl");

        jLabel1.setText("URL");

        jButton1.setText("Connect");

        jButton2.setText("Exit");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTextField2.setText("#login: l-nazar p-rie123;");

        jButton3.setText("Send");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jTextField2)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton3))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(28, 28, 28)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton1)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton2)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton3))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CClientSOAP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CClientSOAP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CClientSOAP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CClientSOAP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CClientSOAP().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;

    //ConsoleBuffer Java lib
    // End of variables declaration//GEN-END:variables
}
