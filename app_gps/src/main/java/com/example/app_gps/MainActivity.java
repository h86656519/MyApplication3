package com.example.app_gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends GPSActivity {
    Context context;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        textView = (TextView) findViewById(R.id.textView);

        openGPS(listener);
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 25.0448241,121.5084509 (台北車站)
            float[] result = new float[1];
            Location.distanceBetween(
                    location.getLatitude(), location.getLongitude(),
                    25.0448241, 121.5084509,
                    result
            );

            textView.setText(String.format("緯度:%f\n經度:%f\n確度:%f\n標高:%f\n時間:%s\n速度:%f\n方位:%f\n距離:%f m",
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAccuracy(),
                    location.getAltitude(),
                    new Date(location.getTime()),
                    location.getSpeed(),
                    location.getBearing(),
                    result[0]
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

}
