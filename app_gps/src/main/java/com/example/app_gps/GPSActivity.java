package com.example.app_gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class GPSActivity extends AppCompatActivity {

    final int REQUEST_FINE_LOCATION_PERMISSION = 99;
    LocationListener listener;

    // 使用 GPS
    public void openGPS(LocationListener listener) {
        this.listener = listener;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 確認權限
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission(); // 請求權限授權
            return;
        }
        // 進行 GPS 監聽程序
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, listener);
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        } catch(Exception e) {

        }
    }

    // GPS 授權 (Android 6.0 以上需使用者手動授權)
    private void requestLocationPermission() {
        // 如果裝置版本是6.0（包含）以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 取得授權狀態，參數是請求授權的名稱
            int hasPermission = checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION);

            // 如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // 請求授權
                //     第一個參數是請求授權的名稱
                //     第二個參數是請求代碼
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            }
        }
    }

    // 使用者GPS手動授權設定回應
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION_PERMISSION) {
            // 是否同意授權使用 GPS ?
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //取得權限，進行存取
                Toast.makeText(getApplicationContext(), "取得權限，進行存取", Toast.LENGTH_SHORT).show();
                // 使用 GPS
                openGPS(listener);
            } else {
                //使用者拒絕權限，停止存取功能
                Toast.makeText(getApplicationContext(), "使用者拒絕權限，停止存取功能", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }
}
