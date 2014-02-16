package com.wwestenbrink.SfsClientSimulator.model;

import com.wwestenbrink.SfsClientSimulator.client.SfsBaseClient;

import javax.swing.table.AbstractTableModel;
import java.util.concurrent.ConcurrentHashMap;

public class ClientModel extends AbstractTableModel {
    private static final int COL_NAME = 0;
    private static final int COL_CONNECT=1;
    private static final int COL_LOGIN=2;
    private static final int COL_STATUS=3;
    private static final String[] colLabels = {"Client", "Connect", "Login", "Status"};

    private ConcurrentHashMap<Integer, SfsBaseClient> clients;
    private int lastId = 0;

    public ClientModel(int initialSize) {
        this.clients = new ConcurrentHashMap<Integer, SfsBaseClient>(initialSize);
    }

    public void addClient(SfsBaseClient client) {
        client.setModel(this);
        client.setId(lastId);
        clients.put(lastId, client);
        fireTableRowsInserted(lastId, lastId);
        lastId++;
    }

    public SfsBaseClient getClient(int id) {
        return clients.get(id);
    }

    @Override
    public int getRowCount() {
        return this.clients.size();
    }

    @Override
    public int getColumnCount() {
        return colLabels.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return colLabels[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SfsBaseClient client = (SfsBaseClient) clients.get(rowIndex);

        try {
            switch (columnIndex) {
                case COL_NAME: return client.getName();
                case COL_CONNECT: return client.getLatency("connecting", "connected") + "ms";
                case COL_LOGIN: return client.getLatency("connected", "loggedin") + "ms";
                case COL_STATUS: return client.getStatus();
            }
        } catch (Exception e) {
            return e.getMessage();
        }

        return "";
    }
}
