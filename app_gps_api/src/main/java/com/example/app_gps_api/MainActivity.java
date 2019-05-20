package com.example.app_gps_api;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    EditText lat_edit, lng_edit, addr_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lat_edit = (EditText) findViewById(R.id.lat_edit);
        lng_edit = (EditText) findViewById(R.id.lng_edit);
        addr_edit = (EditText) findViewById(R.id.addr_edit);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                new Runwork().start();
                break;
            case R.id.button2:

                break;
        }
    }

    class Runwork extends Thread {
        OkHttpClient client = new OkHttpClient();
        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        String addr = "";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                addr_edit.setText(addr);
            }
        };

        @Override
        public void run() {
            try {
                String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&language=zh_tw";
                url = String.format(url, lat_edit.getText().toString(), lng_edit.getText().toString());
                String json = run(url);
                addr = new JSONObject(json).getJSONArray("results").getJSONObject(0).getString("formatted_address");
                runOnUiThread(r);
            } catch(Exception e) {

            }
        }

    }
}
