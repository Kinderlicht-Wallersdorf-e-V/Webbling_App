package com.example.webbling_test;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class testAsyncTask extends AsyncTask<URL, String, String> {
    public Context con = null;
    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        System.out.println("Out " + o);
        Toast.makeText(con, o, Toast.LENGTH_LONG);
    }


    @Override
    protected String doInBackground(URL... urls) {
        String output = "";
        try{
            URL url = new URL("https://kinderlichtwdorf.webling.eu/api/1/membergroup?format=full&apikey=eaab12f49595f7d8ca8a938cf0d082ec");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            try{
                InputStream in = new BufferedInputStream(con.getInputStream());
                char l = ' ';

                while(( l = (char) in.read()) != -1){
                    output += l;
                }
            } catch (Exception e){
                System.out.println(e);
            } finally{
                con.disconnect();
            }
        } catch(Exception e){
            System.out.println("Error" + e);
        }

        return output;
    }
}
