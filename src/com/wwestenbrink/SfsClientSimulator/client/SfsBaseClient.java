package com.wwestenbrink.SfsClientSimulator.client;

import com.smartfoxserver.v2.exceptions.SFSException;
import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.LoginRequest;
import com.wwestenbrink.SfsClientSimulator.log.Logger;
import com.wwestenbrink.SfsClientSimulator.model.ClientModel;

import javax.swing.*;
import java.util.HashMap;

public abstract class SfsBaseClient implements Runnable, IEventListener {
    protected static String serverHost = "local-smartfox.crowdpark-cloud.com";
    protected static String zone = "BasicExamples";

    private int id;
    private String status;
    private ClientModel model;
    private SmartFox sfsClient;
    private HashMap<String, Long> timings = new HashMap();
    private Logger logger;

    public void setModel(ClientModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        init();
        connect();
    }

    private void init() {
        updateStatus("init", "Initializing");

        // Instantiate SmartFox client
        sfsClient = new SmartFox();

        // Add event listeners
        sfsClient.addEventListener(SFSEvent.CONNECTION, this);
        sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
        sfsClient.addEventListener(SFSEvent.LOGIN, this);
        sfsClient.addEventListener(SFSEvent.LOGIN_ERROR, this);
        sfsClient.addEventListener(SFSEvent.ROOM_JOIN, this);
        sfsClient.addEventListener(SFSEvent.HANDSHAKE, this);
    }

    protected void connect() {
        updateStatus("connecting", "Connecting to smartfox server "+serverHost);
        sfsClient.connect(serverHost, 9933);
    }

    protected void disconnect() {
        sfsClient.disconnect();
    }

    protected void login(String zone)
    {
        sfsClient.send(new LoginRequest("","", zone));
    }

    @Override
    public void dispatch(BaseEvent event) throws SFSException {
        switch (event.getType()) {
            case SFSEvent.CONNECTION: onConnection(event); return;
            case SFSEvent.CONNECTION_LOST: onConnectionLost(event); return;
            case SFSEvent.LOGIN: onLogin(event); return;
            case SFSEvent.LOGIN_ERROR: onLoginError(event); return;
            case SFSEvent.ROOM_JOIN: onRoomJoin(event); return;
            case SFSEvent.HANDSHAKE: onHandshake(event); return;
       }
       onRecieve(event);
    }

    protected void onConnection(BaseEvent event) {
        if (event.getArguments().get("success").equals(true)) {
            onConnected(event);
        } else {
            updateStatus("disconnected", "Failed to connect");
        }
    }

    protected void onConnected(BaseEvent event) {
        updateStatus("connected", "Connected to smartfox", "connecting");
    }

    protected void onConnectionLost(BaseEvent event) {
        updateStatus("disconnected", "Disconnected: " + event.getArguments().toString());
    }

    protected void onLogin(BaseEvent event) {
        updateStatus("loggedin", "Logged in to zone " + sfsClient.getCurrentZone(), "connected");
    }

    protected void onLoginError(BaseEvent event) {
        updateStatus("loginerror", "Login failed");
    }

    protected void onRoomJoin(BaseEvent event) {
        updateStatus("joinedroom", "Joined room " + sfsClient.getLastJoinedRoom().getName());
    }

    protected void onHandshake(BaseEvent event) {
        log("Handshake");
    }

    protected void onRecieve(BaseEvent event) {
        log("SfsEvent " + event.getType() + ", arguments:"+event.getArguments());
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return "Client "+(id+1);
    }

    public String getStatus() {
        return status;
    }

    private void updateStatus(String status, String logMsg, String appendLatencySinceState) {
        this.status = status;
        setTiming(status);

        if (appendLatencySinceState != null) {
            try {
                logMsg = logMsg + " ("+ getLatency(appendLatencySinceState, status)+"ms)";
            } catch (Exception e) {}
        }

        log(logMsg);
    }

    private void updateStatus(String status, String logMsg) {
        updateStatus(status, logMsg, null);
    }

    private void setStatus(String status) {
        updateStatus(status, status);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private void log(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (model != null)
                    model.fireTableRowsUpdated(id, id);

                logger.log(getName() + " " + msg + "\n");
            }
        });
    }

    private void setTiming(String name) {
        timings.put(name, System.currentTimeMillis());
    }

    public long getLatency(String from ,String till) throws Exception {
        if (!timings.containsKey(from)) {
            throw new Exception("not "+from+" yet");
        }

        if (!timings.containsKey(till)) {
            throw new Exception("not "+till+" yet");
        }

        return timings.get(till) - timings.get(from);
    }
}