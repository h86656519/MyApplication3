package com.example.app_tts_geo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends GPSActivity {
    TextView loc_view, goal_view, km_view;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        loc_view = (TextView) findViewById(R.id.loc_view);
        goal_view = (TextView) findViewById(R.id.goal_view);
        km_view = (TextView) findViewById(R.id.km_view);
        openGPS(listener);
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            loc_view.setText(String.format("%f, %f",
                    location.getLatitude(),
                    location.getLongitude()
            ));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void onClick(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results.size() > 0) {
                goal_view.setText(results.get(0));
                // 請實作
                //km_view.setText("9 公里");
            } else {
                Toast.makeText(context, "我聽不懂, 請再說一次 !", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e) {

        }
    }


}
