package com.example.chinyao.simpletodo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // need to use findViewById to get object from layout
    ListView lvItems;
    EditText etNewItem;

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter; // connect ArrayList and ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // need to use findViewById to get object from layout
        lvItems = (ListView) findViewById(R.id.lvItems);
        etNewItem = (EditText) findViewById(R.id.etNewItem);

        // create default ArrayList
        items = new ArrayList<>();
        for (int index = 1; index <= 20; index++) {
            items.add("Item " + Integer.toString(index));
        }

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
                        return true;
                    }
                }
        );
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

            etNewItem.setText("");
        }
    }
}
