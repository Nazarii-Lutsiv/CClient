package client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CClient extends javax.swing.JFrame {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 4321;

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss:  ");
    private ConnectionHendler connectionHendler;
    private ProtocolManager protocolManager;
    private Thread showResponseThread;

    public CClient() {
        super("CClient");
        initComponents();

        connectionHendler = new ConnectionHendler(DEFAULT_HOST, DEFAULT_PORT);

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // connect to server
                connectionHendler.setHost(jTextField1.getText());
                connectionHendler.setPort(Integer.parseInt(jTextField3.getText()));

                if (connectionHendler.getSocket() == null) {
                    connectionHendler.listenSocket();
                    if (connectionHendler.getSocket().isConnected()) {
                        connectionHendler.readMessage();
                        connectionHendler.sendRequestInfo();
                        showResponse();
                        jTextArea1.append(simpleDateFormat.format(new Date()) + "Connected to server!\n");
                    } else {
                        jTextArea1.append(simpleDateFormat.format(new Date()) + "Can`t connect to server!\n");
                        connectionHendler.close(true);
                        return;
                    }
                }
            }
        });

        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connectionHendler.getSocket() != null) {
                    connectionHendler.close(true);
                    showResponseThread.interrupt();
                    if (connectionHendler.getSocket() == null) {
                        jTextArea1.append(simpleDateFormat.format(new Date()) + "Disconnected!\n");
                    }
                }
            }
        });

        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // send message
                if (connectionHendler.getSocket() == null) {
                    jTextArea1.append(simpleDateFormat.format(new Date()) + "No connection with server" + "\n");
                    return;
                }
                if (connectionHendler.getSocket().isConnected()) {
                    connectionHendler.sendMessage(jTextField2.getText());
                    jTextArea1.append(simpleDateFormat.format(new Date()) + jTextField2.getText() + "\n");
                }
            }
        });
    }

    private void  showResponse() {
        showResponseThread = new Thread(() -> {
            while (connectionHendler.getSocket().isConnected()) {
                if (connectionHendler.getResponseText() != null && !connectionHendler.getResponseText().equals("")) {
                    jTextArea1.append(simpleDateFormat.format(new Date()) + connectionHendler.getResponseText() + "\n");
                    connectionHendler.setResponseText(null);
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
        jTextField3 = new javax.swing.JTextField();

        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextField1.setText("localhost");

        jLabel1.setText("Address");

        jTextField3.setText("4321");

        jLabel3.setText("Port");

        jButton1.setText("Connect");

        jButton2.setText("Disconnect");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTextField2.setText("#login: l-kow p-rie;");

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
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel3)
                                                .addGap(28, 28, 28)
                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                        .addComponent(jLabel3)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            java.util.logging.Logger.getLogger(CClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;

    //ConsoleBuffer Java lib
    // End of variables declaration//GEN-END:variables
}
