package com.wwestenbrink.SfsClientSimulator.client;

import sfs2x.client.core.BaseEvent;

public class SfsTestClient extends SfsBaseClient {
    protected void onConnected(BaseEvent event) {
        super.onConnected(event);

        login(zone);
    }
}
