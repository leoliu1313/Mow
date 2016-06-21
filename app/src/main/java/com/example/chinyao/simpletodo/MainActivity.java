package com.example.chinyao.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainActivity extends AppCompatActivity {

    private final int DefaultItemCount = 3;

    private final int AddItem_Mode = 4;
    // 1: insert to the end via adapter plus scroll
    // 2: insert to the end via data source plus no scroll
    // 3: insert to the end via data source plus scroll
    // 4: insert to the beginning via data source plus scroll

    private final int BackEnd_Mode = 2;
    // 1: file system
    // 2: Cupboard

    private final int CustomAdapter = 2;
    // 1: ArrayAdapter
    // 2: custom adapter

    // need to use findViewById to get object from layout
    ListView lvItems;
    EditText etNewItem;

    // data
    ArrayList<String> items;

    // connect ArrayList and ListView
    ArrayAdapter<String> itemsAdapter;
    ItemAdapter itemsCustomAdapter;

    private final int REQUEST_CODE = 5566; // Intent

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // need to use findViewById to get object from layout
        lvItems = (ListView) findViewById(R.id.lvItems);
        etNewItem = (EditText) findViewById(R.id.etNewItem);

        // create ArrayList
        if (BackEnd_Mode == 2) {
            PracticeDatabaseHelper dbHelper = new PracticeDatabaseHelper(this);
            db = dbHelper.getWritableDatabase();
        }
        readItems();

        //  connect ArrayList and ListView
        if (CustomAdapter == 1) {
            itemsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, items);
            lvItems.setAdapter(itemsAdapter);
        }
        else if (CustomAdapter == 2) {
            itemsCustomAdapter = new ItemAdapter(this, items);
            lvItems.setAdapter(itemsCustomAdapter);
        }

        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item,
                                                   int position,
                                                   long id) {
                        items.remove(position); // need to notify
                        notifyAdapter();

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
                        Intent data = new Intent(MainActivity.this, EditItemActivity.class);
                        data.putExtra("position", position);
                        if (CustomAdapter == 1) {
                            data.putExtra("content", ((TextView) item).getText().toString());
                        }
                        else if (CustomAdapter == 2) {
                            data.putExtra("content",
                                    ((TextView) ((RelativeLayout) item).getChildAt(0)).getText().toString());
                        }
                        startActivityForResult(data, REQUEST_CODE);
                    }
                }
        );

        Toast.makeText(this, "Version 1606201700", Toast.LENGTH_SHORT).show();
        // update this with PracticeDatabaseHelper.java and app/build.gradle
    }

    public void onAddItem(View view) {
        String itemText = etNewItem.getText().toString();
        if (!itemText.isEmpty()) {
            switch (AddItem_Mode) {
                case 1:
                    // 1: insert to the end via adapter plus scroll
                    if (CustomAdapter == 1) {
                        itemsAdapter.add(itemText); // cannot add to the beginning
                    }
                    else if (CustomAdapter == 2) {
                        itemsCustomAdapter.add(itemText); // cannot add to the beginning
                    }
                    lvItems.smoothScrollToPosition(lvItems.getCount() - 1);
                    break;
                case 2:
                    // 2: insert to the end via data source plus no scroll
                    items.add(itemText);
                    break;
                case 3:
                    // 3: insert to the end via data source plus scroll
                    items.add(itemText);
                    notifyAdapter();
                    lvItems.smoothScrollToPosition(lvItems.getCount() - 1); // need to notify
                    break;
                case 4:
                    // 4: insert to the beginning via data source plus scroll
                    items.add(0, itemText); // need to notify
                    notifyAdapter();
                    lvItems.smoothScrollToPosition(0); // need to notify
                    break;
                default:
                    break;
            }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            String content = data.getStringExtra("content");

            if (position != -1) {
                items.set(position, content); // need to notify
                notifyAdapter();

                writeItems();
            }
        }
    }

    private void notifyAdapter() {
        if (CustomAdapter == 1) {
            itemsAdapter.notifyDataSetChanged();
        }
        else if (CustomAdapter == 2) {
            itemsCustomAdapter.notifyDataSetChanged();
        }
    }

    private void defaultItems() {
        items = new ArrayList<>();
        for (int index = DefaultItemCount; index >= 1; index--) {
            items.add("Item " + Integer.toString(index));
        }
    }

    private void readItems() {
        if (BackEnd_Mode == 1) {
            File filesDir = getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                items = new ArrayList<String>(FileUtils.readLines(todoFile));
            } catch (IOException e) {
                e.printStackTrace();
                defaultItems();
                writeItems();
            }
        }
        else if (BackEnd_Mode == 2) {
            // TODO: use Shared Preferences instead of SQL
            // http://guides.codepath.com/android/Persisting-Data-to-the-Device
            // https://developer.android.com/reference/android/content/SharedPreferences.html
            // get the first book in the result
            SettingModel theSettingModel = cupboard().withDatabase(db).query(SettingModel.class).get();
            if (theSettingModel == null) {
                defaultItems();
                writeItems();
                cupboard().withDatabase(db).put(new SettingModel(false));
            }
            else if (theSettingModel.firstTime == true) {
                defaultItems();
                writeItems();
                theSettingModel.firstTime = false;
                cupboard().withDatabase(db).put(theSettingModel);
            }

            // Get the cursor for this query
            Cursor cursorTodoModel = cupboard().withDatabase(db).query(TodoModel.class).getCursor();
            try {
                // Iterate Bunnys
                QueryResultIterable<TodoModel> itr =
                        cupboard().withCursor(cursorTodoModel).iterate(TodoModel.class);
                Iterator<TodoModel> it = itr.iterator();
                items = new ArrayList<>();
                while (it.hasNext()) {
                    TodoModel theTodoModel = it.next();
                    // do something with bunny
                    items.add(theTodoModel.content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // close the cursor
                cursorTodoModel.close();
            }
        }
    }

    private void writeItems() {
        if (BackEnd_Mode == 1) {
            File filesDir = getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                FileUtils.writeLines(todoFile, items);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (BackEnd_Mode == 2) {
            // TODO: only update the necessary items instead of all
            cupboard().withDatabase(db).delete(TodoModel.class, null);
            for (int index = 0; index < items.size(); index++) {
                cupboard().withDatabase(db).put(new TodoModel(items.get(index)));
            }
        }
    }
}
