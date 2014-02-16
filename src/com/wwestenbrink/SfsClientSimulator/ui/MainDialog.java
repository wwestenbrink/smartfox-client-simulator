package com.wwestenbrink.SfsClientSimulator.ui;

import com.wwestenbrink.SfsClientSimulator.client.SfsTestClient;
import com.wwestenbrink.SfsClientSimulator.client.SfsWarmupClient;
import com.wwestenbrink.SfsClientSimulator.client.SfsBaseClient;
import com.wwestenbrink.SfsClientSimulator.model.ClientModel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainDialog extends JDialog implements TableModelListener {
    private static final String appTitle = "SfsBaseClient";
    private static final int startClients = 50;

    private int clientsConnected = 0;
    private long avgConnectLatency = 0;
    private long avgLoginLatency = 0;

    private ClientModel clientModel;

    private JPanel contentPane;
    private LogArea logTextArea;
    private JButton quitButton;
    private JButton spawnClientsButton;
    private JTable clientTable;
    private JLabel statusLabel;
    private int clientsLoggedin;

    public MainDialog() {
        super(new MainFrame(appTitle));

        setTitle(appTitle);
        setContentPane(contentPane);

        spawnClientsButton.setText("Start " + startClients + " clients");
        updateStatusLabel();

        // initialize client model
        clientModel = new ClientModel(startClients);
        clientModel.addTableModelListener(this);

        // initialize client table
        clientTable.setModel(clientModel);
        clientTable.getColumnModel().getColumn(0).setMaxWidth(100);
        clientTable.getColumnModel().getColumn(1).setMaxWidth(100);
        clientTable.getColumnModel().getColumn(2).setMaxWidth(100);

        // make sure the caret is always updated to scroll automatically
        DefaultCaret caret = (DefaultCaret) logTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // initialize dialog
        setModal(true);
        getRootPane().setDefaultButton(spawnClientsButton);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onQuit();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQuit();
            }
        });

        spawnClientsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSpawnClients();
            }
        });

        SfsBaseClient client = new SfsWarmupClient();
        client.setLogger(logTextArea);
        Thread thread = new Thread(client);
        thread.start();
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onSpawnClients() {
        logTextArea.log("Starting " + startClients + " clients \n");

        for (int i = 0; i < startClients; i++) {
            SfsBaseClient client = new SfsTestClient();
            client.setLogger(logTextArea);
            clientModel.addClient(client);

            Thread thread = new Thread(client);
            thread.start();
        }
    }

    /** recalculate some properties when clientModel changes */
    public void tableChanged(TableModelEvent e) {
        long connectionLatencySum = 0;
        int nConnected = 0;
        long loginLatencySum = 0;
        int nLoggedin = 0;

        for (int i = 0, rows = clientModel.getRowCount(); i < rows; i++) {
            try {
                SfsBaseClient client = clientModel.getClient(i);
                connectionLatencySum = connectionLatencySum + client.getLatency("connecting", "connected");
                nConnected++;

                loginLatencySum = loginLatencySum + client.getLatency("connected", "loggedin");
                nLoggedin++;
            } catch (Exception ex) {
                // ignore messages about clients not having reached a certain state
            }
        }

        if (nConnected > 0) {
            avgConnectLatency = connectionLatencySum / nConnected;
            clientsConnected = nConnected;

            if (nLoggedin > 0) {
                avgLoginLatency = loginLatencySum / nLoggedin;
                clientsLoggedin = nLoggedin;
            }

            updateStatusLabel();
        }
    }

    private void updateStatusLabel() {
        String status;

        status = clientsConnected + " connected" +
                " (avg: " + avgConnectLatency + "ms) " +
                clientsLoggedin + " loggedIn" +
                " (avg: " + avgLoginLatency + "ms)";

        statusLabel.setText(status);
    }

    private void onQuit() {
        System.out.println("quiting");
        dispose();
    }
}