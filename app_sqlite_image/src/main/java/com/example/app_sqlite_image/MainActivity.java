package com.example.app_sqlite_image;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context context;
    ListView listView;

    ImageView imageView;
    Spinner itemSpinner;
    EditText priceEdit;

    SQLiteDatabase db;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        listView = (ListView) findViewById(R.id.listView);

        // SQLite setup
        MyDBHelper dbHelper = new MyDBHelper(context, "demo.db", null, 1);
        db = dbHelper.getWritableDatabase();
        // Adapter
        // Context context, int layout, Cursor c, String[] from, int[] to, int flags
        adapter = new SimpleCursorAdapter(
                context,
                R.layout.row,
                read(),
                new String[]{"image", "item", "price"},
                new int[]{R.id.imageView, R.id.itemView, R.id.priceView},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        ) {

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);
                ImageView iv = (ImageView) view.findViewById(R.id.imageView);
                String imageBase64String = cursor.getString(cursor.getColumnIndex("image"));
                Bitmap bitmap = Utils.decodeBase64(imageBase64String);
                iv.setImageBitmap(bitmap);
                iv.setOnClickListener(null);
            }
        } ;

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                update(cursor);
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                create();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            Uri selectedimg = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                imageView.setImageBitmap(bitmap);
                imageView.setTag(bitmap);

            } catch(Exception e) {

            }
        }
    }

    // 新增
    private void create() {
        // 建立「新增對話視窗」Layout 物件
        View layoutView = LayoutInflater.from(context).inflate(R.layout.edit, null);
        imageView = (ImageView) layoutView.findViewById(R.id.imageView);
        itemSpinner = (Spinner) layoutView.findViewById(R.id.itemSpinner);
        priceEdit = (EditText) layoutView.findViewById(R.id.priceEdit);

        // Spinner 下拉選單
        String[] items = {"麥香魚", "薯餅", "可樂"};
        ArrayAdapter<String> lunchList = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                items);
        itemSpinner.setAdapter(lunchList);

        // ImageView OnClick
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                */
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                startActivityForResult(intent, 101);
            }
        });

        // 新增對話視窗
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Add");
        alert.setView(layoutView);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Insert into SQL
                String imageBase64Text = Utils.encodeTobase64((Bitmap) imageView.getTag());
                Object[] args = {
                        itemSpinner.getSelectedItem().toString(),
                        Integer.parseInt(priceEdit.getText().toString()),
                        imageBase64Text
                };
                db.execSQL("INSERT INTO Product(item, price, image) VALUES(?, ?, ?)", args);
                Toast.makeText(context, "add ok", Toast.LENGTH_SHORT).show();
                adapter.changeCursor(read());
            }
        });
        alert.setNegativeButton("Cancel", null);

        alert.show();

    }

    // 讀取
    private Cursor read() {
        Cursor cursor = db.rawQuery("SELECT _id, item, price, image, tdate FROM Product", null);
        return cursor;
    }

    // 修改
    private void update(Cursor cursor) {
        final int _id = cursor.getInt(cursor.getColumnIndex("_id"));
        String item = cursor.getString(cursor.getColumnIndex("item"));
        int price = cursor.getInt(cursor.getColumnIndex("price"));
        String imageBase64String = cursor.getString(cursor.getColumnIndex("image"));

        Toast.makeText(context, item, Toast.LENGTH_SHORT).show();

        // 建立「修改對話視窗」Layout 物件
        View layoutView = LayoutInflater.from(context).inflate(R.layout.edit, null);
        imageView = (ImageView) layoutView.findViewById(R.id.imageView);
        itemSpinner = (Spinner) layoutView.findViewById(R.id.itemSpinner);
        priceEdit = (EditText) layoutView.findViewById(R.id.priceEdit);

        // Spinner 下拉選單
        String[] items = {"麥香魚", "薯餅", "可樂"};
        ArrayAdapter<String> lunchList = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                items);
        itemSpinner.setAdapter(lunchList);

        // ImageView OnClick
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                startActivityForResult(intent, 101);
            }
        });
        // 將資料送入指定欄位
        int i=0;
        for(;i<items.length;i++) {
            if(item.equals(items[i])) {
                break;
            }
        }
        itemSpinner.setSelection(i);
        priceEdit.setText(price + "");
        imageView.setImageBitmap(Utils.decodeBase64(imageBase64String));
        // 重要 !!
        imageView.setTag(Utils.decodeBase64(imageBase64String));

        // 修改對話視窗
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Update");
        alert.setView(layoutView);
        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update SQL
                String imageBase64Text = Utils.encodeTobase64((Bitmap) imageView.getTag());
                Object[] args = {
                        itemSpinner.getSelectedItem().toString(),
                        Integer.parseInt(priceEdit.getText().toString()),
                        imageBase64Text,
                        _id
                };
                db.execSQL("Update Product SET item=?, price=?, image=? WHERE _id = ?", args);
                Toast.makeText(context, "Update ok", Toast.LENGTH_SHORT).show();
                adapter.changeCursor(read());
            }
        });
        alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Object[] args = {
                        _id
                };
                db.execSQL("Delete From Product WHERE _id = ?", args);
                Toast.makeText(context, "Delete ok", Toast.LENGTH_SHORT).show();
                adapter.changeCursor(read());
            }
        });
        alert.setNeutralButton("Cancel", null);

        alert.show();

    }

    // 刪除
    private void delete() {


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
