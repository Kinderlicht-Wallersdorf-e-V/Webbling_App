package com.kinderlicht.webserver;

import android.os.AsyncTask;


public class ClientTask extends AsyncTask<ServerMessage, Void, String> {

    @Override
    protected String doInBackground(ServerMessage... message) {
        return ClientWrapper.sendMessage(message[0].getCode(), message[0].getMessage());
    }
}
