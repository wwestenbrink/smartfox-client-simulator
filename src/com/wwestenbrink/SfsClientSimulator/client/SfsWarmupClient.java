package com.wwestenbrink.SfsClientSimulator.client;

import sfs2x.client.core.BaseEvent;

public class SfsWarmupClient extends SfsBaseClient {
    @Override
    protected void onConnected(BaseEvent event) {
        super.onConnected(event);

        login(zone);
    }

    @Override
    protected void onLogin(BaseEvent event) {
        super.onLogin(event);
        disconnect();
    }

    @Override
    public String getName() {
        return "Warmup";
    }
}
