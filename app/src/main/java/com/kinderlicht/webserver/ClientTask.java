package com.kinderlicht.webserver;

import android.os.AsyncTask;

import com.kinderlicht.authentication.ServerUser;

public class ClientTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        return ((ServerUser) (Client.loginManager.getUser("Server"))).sendMessage(10, strings[0]);
    }

    @Override
    protected void onPostExecute(String string){
        System.out.println(string);
    }
}
