package com.example.app_chart_lab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    EditText num1_edit, num2_edit, num3_edit, num4_edit;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num1_edit = (EditText) findViewById(R.id.num1_edit);
        num2_edit = (EditText) findViewById(R.id.num2_edit);
        num3_edit = (EditText) findViewById(R.id.num3_edit);
        num4_edit = (EditText) findViewById(R.id.num4_edit);
        webView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 啟用 Javascrip
        webSettings.setBuiltInZoomControls(true); // 啟用 Zoom

    }

    public void loadChart(WebView webView) {
        String asset_path = "file:///android_asset/";
        String html = getHtml("chart.html");
        int num1 = Integer.parseInt(num1_edit.getText().toString());
        int num2 = Integer.parseInt(num2_edit.getText().toString());
        int num3 = Integer.parseInt(num3_edit.getText().toString());
        int num4 = Integer.parseInt(num4_edit.getText().toString());
        html = String.format(html, num1, num2, num3, num4, "ColumnChart"); // 「ColumnChart」 or 「PieChart」
        webView.loadDataWithBaseURL(asset_path, html, "text/html", "utf-8", null);
        webView.requestFocusFromTouch();
    }

    public String getHtml(String filename) {
        String html = null;
        try {
            InputStream in = getAssets().open(filename);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[in.available()];
            in.read(buffer); // 讀出
            out.write(buffer);// 寫入
            html = new String(out.toByteArray(), "UTF-8");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return html;
    }

    public void onClick(View view) {
        loadChart(webView);
    }

}
