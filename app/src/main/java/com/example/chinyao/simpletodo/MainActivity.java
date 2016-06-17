package com.example.chinyao.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // need to use findViewById to get object from layout
    ListView lvItems;
    EditText etNewItem;

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter; // connect ArrayList and ListView

    private final int REQUEST_CODE = 5566;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // need to use findViewById to get object from layout
        lvItems = (ListView) findViewById(R.id.lvItems);
        etNewItem = (EditText) findViewById(R.id.etNewItem);

        // create default ArrayList
        readItems();

        //  connect ArrayList and ListView
        itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);

        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item,
                                                   int position,
                                                   long id) {
                        items.remove(position); // need to notify
                        itemsAdapter.notifyDataSetChanged();

                        writeItems();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                                   View item,
                                                   int position,
                                                   long id) {
                        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                        i.putExtra("position", position);
                        i.putExtra("content", ((TextView) item).getText().toString());
                        startActivityForResult(i, REQUEST_CODE);
                    }
                }
        );

        Toast.makeText(this, "Version 2016.06.17.16.24", Toast.LENGTH_SHORT).show();
    }

    public void onAddItem(View view) {
        String itemText = etNewItem.getText().toString();
        if (!itemText.isEmpty()) {
            // 1: insert to the beginning via adapter plus scroll
            // itemsAdapter.add(itemText); // cannot add to the beginning
            // lvItems.smoothScrollToPosition(lvItems.getCount() - 1);

            // 2: insert to the end via data source plus no scroll
            // items.add(itemText);

            // 3: insert to the end via data source plus scroll
            // items.add(itemText);
            // itemsAdapter.notifyDataSetChanged();
            // lvItems.smoothScrollToPosition(lvItems.getCount() - 1); // need to notify

            // 4: insert to the beginning via data source plus scroll
            items.add(0, itemText); // need to notify
            itemsAdapter.notifyDataSetChanged();
            lvItems.smoothScrollToPosition(0); // need to notify

            // hide keyboard
            // View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            etNewItem.setText("");
            writeItems();
        }
    }

    private void defaultItems() {
        items = new ArrayList<>();
        for (int index = 1; index <= 20; index++) {
            items.add("Item " + Integer.toString(index));
        }
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            e.printStackTrace();
            defaultItems();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
