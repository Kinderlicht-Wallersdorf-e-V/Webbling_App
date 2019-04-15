package com.kinderlicht.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private Button b = null;
    private TextView t = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init(){
        b = (Button) findViewById(R.id.b_001);
        t = (TextView)findViewById(R.id.tv_001);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Hello World");
                try{

                    test();

                } catch(Exception e){
                    System.out.println("Error" + e);
                }

            }
        });


    }

    private void test() throws Exception{
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://kinderlichtwdorf.webling.eu/api/1/membergroup?format=full&apikey=eaab12f49595f7d8ca8a938cf0d082ec";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response.substring(0, 500), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Hasn't worked", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }
}
