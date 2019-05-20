package com.example.app_chart_sqlite;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    Context context;
    SQLiteDatabase db;
    SimpleCursorAdapter adapter;
    ListView listView;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 啟用 Javascrip
        webSettings.setBuiltInZoomControls(true); // 啟用 Zoom
        listView = (ListView) findViewById(R.id.listView);

        MyDBHelper helper = new MyDBHelper(context, "Chart.db", null, 1);
        db = helper.getWritableDatabase();
        adapter = new SimpleCursorAdapter(
                context,
                R.layout.row,
                read(),
                new String[] {"item", "num"},
                new int[] {R.id.item_view, R.id.num_view},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        listView.setAdapter(adapter);

        loadChart(webView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
            }
        });

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    // 新增資料
    public void create() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.edit, null);
        final EditText item_edit = (EditText) layout.findViewById(R.id.item_edit);
        final EditText num_edit = (EditText) layout.findViewById(R.id.num_edit);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("新增");
        alert.setView(layout);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "INSERT INTO pizza('item', 'num') VALUES(?, ?)";
                String item = item_edit.getText().toString();
                int num = Integer.parseInt(num_edit.getText().toString());
                Object[] args = {item, num};
                db.execSQL(sql, args);
                adapter.changeCursor(read()); // 重讀 (相當於 adapter.notifyDataSetChanged();)
                loadChart(webView);
            }
        });
        alert.setNegativeButton("Cancel", null);
        alert.show();
    }

    // 查詢資料
    public Cursor read() {
        return db.rawQuery("SELECT _id, item, num, tdate FROM pizza", null);
    }

    // 查詢資料2
    public Cursor readGroup() {
        return db.rawQuery("SELECT item, SUM(num) FROM pizza GROUP BY item", null);
    }

    public void loadChart(WebView webView) {
        String asset_path = "file:///android_asset/";
        String html = getHtml("chart.html");
        int num1 = 0; // mushroom
        int num2 = 0; // beef
        int num3 = 0; // olives
        int num4 = 0; // onion
        Cursor cursor = readGroup();
        int count = cursor.getCount();
        cursor.moveToFirst();
        for(int i=0;i<count;i++) {
            String item = cursor.getString(0);
            int num = cursor.getInt(1);
            switch (item) {
                case "mushroom":
                    num1 = num;
                    break;
                case "beef":
                    num2 = num;
                    break;
                case "olives":
                    num3 = num;
                    break;
                case "onion":
                    num4 = num;
                    break;
            }
            cursor.moveToNext();
        }

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

}
