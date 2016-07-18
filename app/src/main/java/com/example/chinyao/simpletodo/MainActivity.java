package com.example.chinyao.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainActivity extends AppCompatActivity
        implements EditItemFragment.EditItemListener,
        CalendarDatePickerDialogFragment.OnDateSetListener,
        AppBarLayout.OnOffsetChangedListener,
        ItemPinnedMessageDialogFragment.EventListener {

    private final int DefaultItemMode = 2;
    // 1: debug first time
    // 2: real first time

    private final int DefaultDebugItemCount = 20;
    // for debug first time

    private final int AddItemMode = 4;
    // 1: insert to the end via adapter plus scroll
    // 2: insert to the end via data source plus no scroll
    // 3: insert to the end via data source plus scroll
    // 4: insert to the beginning via data source plus scroll

    private final int BackEndMode = 2;
    // 1: file system
    // 2: Cupboard for SQLiteDatabase

    private final int CustomAdapter = 3;
    // 1: ArrayAdapter
    // 2: custom adapter for custom item view
    // 3: android-advancedrecyclerview

    private final int UIMode = 3;
    // 1: Activity via Intent
    // 2: Fragment
    // 3: material-dialogs

    private final int CoordinatorLayout = 2;
    // styles.xml
    // 1: not CoordinatorLayout
    // 2:     CoordinatorLayout for CollapsingToolbarLayout

    // we need to use findViewById to get the following from layout
    private ListView itemsListView;
    private EditText newItemEditText;
    private ImageView imageView;

    // data
    private ArrayList<TodoModel> itemsArrayList;

    // connect from ArrayList to ListView
    private ArrayAdapter<TodoModel> itemsArrayAdapter;
    private ItemAdapter itemsCustomAdapter;

    // connect to MaterialDialog
    private MaterialSimpleListAdapter materialDialogAdapter;

    private SQLiteDatabase db;

    private SimpleDateFormat sdf;

    // Activity via Intent
    private final int REQUEST_CODE = 5566;

    // DialogFragment Tag for CalendarDatePickerDialogFragment.OnDateSetListener
    private static final String TAG_CODE = "5566";

    // getRandomDrawable
    private static final Random RANDOM = new Random();
    private static int currentDrawable = -1;

    // AppBarLayout.OnOffsetChangedListener
    private int onOffsetChangedState = 0;
    private Handler handler = null;
    private Runnable runnable = null;

    // android-advancedrecyclerview
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CoordinatorLayout == 2) {
            // http://guides.codepath.com/android/Design-Support-Library
            // http://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout
            // https://inthecheesefactory.com/blog/android-design-support-library-codelab/en
            // http://stackoverflow.com/questions/31738831/how-to-change-collapsingtoolbarlayout-typeface-and-size
            // http://stackoverflow.com/questions/19691530/valid-values-for-androidfontfamily-and-what-they-map-to
            // https://www.syntaxismyui.com/category/android/
            setContentView(R.layout.activity_detail);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(getString(R.string.app_name));

            Typeface tf;
            // tf = Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/FrutigerLTStd-Light.otf");
            tf = collapsingToolbar.getExpandedTitleTypeface();
            tf = Typeface.create(tf, Typeface.ITALIC); // BOLD_ITALIC
            collapsingToolbar.setExpandedTitleTypeface(tf);
            tf = collapsingToolbar.getCollapsedTitleTypeface();
            tf = Typeface.create(tf, Typeface.ITALIC);
            collapsingToolbar.setCollapsedTitleTypeface(tf);

            imageView = (ImageView) findViewById(R.id.backdrop);
            glideRandomDrawable();
        }
        else if (CoordinatorLayout == 1) {
            setContentView(R.layout.activity_main);
        }

        // need to use findViewById to get object from layout
        itemsListView = (ListView) findViewById(R.id.lvItems);
        newItemEditText = (EditText) findViewById(R.id.etNewItem);

        if (CoordinatorLayout == 2) {
            if (CustomAdapter == 1 || CustomAdapter == 2) {
                // scroll ListView in CoordinatorLayout
                ViewCompat.setNestedScrollingEnabled(itemsListView, true);
            }
        }

        sdf = new SimpleDateFormat("MM/dd/yyyy");

        // create ArrayList
        if (BackEndMode == 2) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            db = dbHelper.getWritableDatabase();
        }
        readItems();

        //  connect ArrayList and ListView
        if (CustomAdapter == 1) {
            itemsArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, itemsArrayList);
            itemsListView.setAdapter(itemsArrayAdapter);
        } else if (CustomAdapter == 2) {
            itemsCustomAdapter = new ItemAdapter(this, itemsArrayList);
            itemsListView.setAdapter(itemsCustomAdapter);
        }

        setListener();


        // update this with DatabaseHelper.java and app/build.gradle
        if (CoordinatorLayout == 1) {
            Toast.makeText(this,
                           "Version 1607181348",
                           Toast.LENGTH_SHORT)
                 .show();
        }
        else if (CoordinatorLayout == 2) {
            Snackbar.make(findViewById(R.id.fab),
                          "Version 1607181348",
                          Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    // Activity via Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            String content = data.getStringExtra("content");

            if (position != -1) {
                itemsArrayList.get(position).content = content; // need to notify
                notifyItemsAdapter();

                writeItems();
            }
        }
    }

    // onClick callback
    public void onAddItem(View view) {
        String itemText = newItemEditText.getText().toString();
        addItem(itemText);
        // hide keyboard
        // View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        newItemEditText.setText("");
    }

    private void addItem(String itemText) {
        if (!itemText.isEmpty()) {
            TodoModel newItem = new TodoModel(itemText,
                    sdf.format(new Date()),
                    "Low Priority"
            );
            if (CustomAdapter != 3) {
                switch (AddItemMode) {
                    case 1:
                        // 1: insert to the end via adapter plus scroll
                        if (CustomAdapter == 1) {
                            itemsArrayAdapter.add(newItem); // cannot add to the beginning
                        } else if (CustomAdapter == 2) {
                            itemsCustomAdapter.add(newItem); // cannot add to the beginning
                        }
                        itemsListView.smoothScrollToPosition(itemsListView.getCount() - 1);
                        break;
                    case 2:
                        // 2: insert to the end via data source plus no scroll
                        itemsArrayList.add(newItem);
                        break;
                    case 3:
                        // 3: insert to the end via data source plus scroll
                        itemsArrayList.add(newItem);
                        notifyItemsAdapter();
                        itemsListView.smoothScrollToPosition(itemsListView.getCount() - 1); // need to notify
                        break;
                    case 4:
                        // 4: insert to the beginning via data source plus scroll
                        itemsArrayList.add(0, newItem); // need to notify
                        notifyItemsAdapter();
                        itemsListView.smoothScrollToPosition(0); // need to notify
                        break;
                    default:
                        break;
                }
            }
            else if (CustomAdapter == 3) {
                itemsArrayList.add(0, newItem); // need to notify
                getDataProvider().addItem(newItem);
            }

            writeItems();
        }
    }

    // EditItemFragment.EditItemListener
    // This method is invoked in the activity when the listener is triggered
    // Access the data result passed to the activity here
    @Override
    public void onFinishEditItemListener(int position, TodoModel theTodoModel) {
        itemsArrayList.get(position).refresh(theTodoModel); // need to notify
        notifyItemsAdapter();

        writeItems();
    }

    // CalendarDatePickerDialogFragment.OnDateSetListener
    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog,
                          int year,
                          int monthOfYear,
                          int dayOfMonth) {
        if (dialog.getTag() == TAG_CODE) {
            int position = dialog.getArguments().getInt("position", -1);
            if (position != -1) {
                // TODO: try other libraries instead of android-betterpickers
                // there is a month bug over here
                itemsArrayList.get(position).date =
                        String.format("%02d", monthOfYear + 1) + "/" +
                                String.format("%02d", dayOfMonth) + "/" +
                                String.format("%04d", year); // need to notify
                notifyItemsAdapter();

                writeItems();

                notifyMaterialDialogAdapter(position);
            }
        }
    }

    private void setListener() {
        if (CustomAdapter != 3) {
            itemsListView.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapter,
                                                       View item,
                                                       int position,
                                                       long id) {
                            return removeItem(position);
                        }
                    }
            );
            itemsListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter,
                                                View item,
                                                int position,
                                                long id) {
                            editItem(item, position);
                        }
                    }
            );
        }

        if (CoordinatorLayout == 2) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialDialog theDialog =
                            new MaterialDialog.Builder(MainActivity.this)
                                    .title(R.string.add_button)
                                    .inputType(InputType.TYPE_CLASS_TEXT)
                                    .positiveText(getString(R.string.save_button))
                                    .inputRangeRes(1, 200, R.color.colorAccentLight)
                                    .input(0, 0, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(@NonNull MaterialDialog dialog,
                                                            CharSequence input) {
                                            addItem(input.toString());
                                        }
                                    }).build();
                    theDialog.getInputEditText().setSingleLine(false);
                    theDialog.show();
                }
            });

            final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
            appbar.addOnOffsetChangedListener(this);
        }

        if (CustomAdapter == 3) {
            getSupportFragmentManager().beginTransaction()
                    .add(new ExampleDataProviderFragment().addItems(itemsArrayList), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SwipeableExampleFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }
    }

    void editItem(View item, int position) {
        if (UIMode == 1) {
            Intent data = new Intent(MainActivity.this, EditItemActivity.class);
            data.putExtra("position", position);
            if (CustomAdapter == 1) {
                data.putExtra("content", ((TextView) item).getText().toString());
            } else if (CustomAdapter == 2) {
                data.putExtra("content",
                        ((TextView) ((RelativeLayout) item).getChildAt(0)).getText().toString());
            }
            startActivityForResult(data, REQUEST_CODE);
        } else if (UIMode == 2) {
            // EditItemFragment.EditItemListener
            FragmentManager fm = getSupportFragmentManager();
            EditItemFragment theFragment =
                    EditItemFragment.newInstance(position, itemsArrayList.get(position));
            theFragment.show(fm, "fragment_edit_name");
        } else if (UIMode == 3) {
            showMaterialDialog(position);
        }
    }

    boolean removeItem(int position) {
        if (UIMode == 1 || UIMode == 2) {
            itemsArrayList.remove(position); // need to notify
            notifyItemsAdapter();

            writeItems();
        } else if (UIMode == 3) {
            // TODO: find a better way instead of final int
            final int position_tag = position;
            TodoModel theTodoModel = itemsArrayList.get(position);
            new MaterialDialog.Builder(MainActivity.this)
                    .title(getString(R.string.remove_item_title))
                    .content(theTodoModel.content)
                    .positiveText(getString(R.string.remove_item_pos))
                    .negativeText(getString(R.string.remove_item_neg))
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            // which.name(); // enum > String
                            if (which.equals(DialogAction.POSITIVE)) {
                                itemsArrayList.remove(position_tag); // need to notify
                                notifyItemsAdapter();

                                if (CustomAdapter == 3) {
                                    getDataProvider().removeItem(position_tag);
                                    notifyItemsAdapter(position_tag);
                                }

                                writeItems();
                            }
                        }
                    })
                    .show();
        }
        return true;
    }

    // AppBarLayout.OnOffsetChangedListener
    // http://stackoverflow.com/questions/32213783/detecting-when-appbarlayout-collapsingtoolbarlayout-is-completely-expanded
    // http://stackoverflow.com/questions/31872653/how-can-i-determine-that-collapsingtoolbar-is-collapsed
    // http://stackoverflow.com/questions/3072173/how-to-call-a-method-after-a-delay-in-android
    // http://stackoverflow.com/questions/18671067/how-to-stop-handler-runnable
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        /*
        Log.d("MainActivity", "start");
        Log.d("MainActivity", Integer.toString(appBarLayout.getTotalScrollRange()));
        Log.d("MainActivity", Integer.toString(offset));
        */
        if (handler == null) {
            handler = new Handler();
        }
        if (onOffsetChangedState == 0 && (appBarLayout.getTotalScrollRange() + offset) == 0) {
            onOffsetChangedState = 1;
            if (runnable != null) {
                handler.removeCallbacks(runnable);
                runnable = null;
            }
            runnable = new Runnable() {
                @Override
                public void run() {
                    glideRandomDrawable();
                }
            };
            //Do something after 1 second
            handler.postDelayed(runnable, 1000);
        }
        else if (onOffsetChangedState == 1 && (appBarLayout.getTotalScrollRange() + offset) > 30) {
            if (runnable != null) {
                handler.removeCallbacks(runnable);
                runnable = null;
            }
            onOffsetChangedState = 0;
        }
    }

    private void showMaterialDialog(int position){
        // TODO: find a better way instead of final int
        final int position_tag = position;
        materialDialogAdapter = new MaterialSimpleListAdapter(this);
        materialDialogAdapter.addAll(createDataArrayList(itemsArrayList.get(position)));

        new MaterialDialog.Builder(this)
                .title(getString(R.string.edit_item_title))
                .adapter(materialDialogAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        MaterialSimpleListItem item = materialDialogAdapter.getItem(which);
                        String itemText = item.getContent().toString();
                        if (which == 0) {
                            MaterialDialog theDialog =
                                    new MaterialDialog.Builder(MainActivity.this)
                                            .inputType(InputType.TYPE_CLASS_TEXT)
                                            .positiveText(getString(R.string.save_button))
                                            .inputRangeRes(1, 200, R.color.colorAccentLight)
                                            .input(null, itemText, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(@NonNull MaterialDialog dialog,
                                                                    CharSequence input) {
                                                    // Toast.makeText(MainActivity.this,
                                                    //         input.toString(),
                                                    //         Toast.LENGTH_SHORT).show();

                                                    itemsArrayList.get(position_tag).content =
                                                            input.toString(); // need to notify
                                                    notifyItemsAdapter();

                                                    if (CustomAdapter == 3) {
                                                        getDataProvider().editItem(position_tag, input.toString());
                                                        notifyItemsAdapter(position_tag);
                                                    }

                                                    writeItems();

                                                    notifyMaterialDialogAdapter(position_tag);

                                                    // showMaterialDialog(position_tag);
                                                }
                                            }).build();
                            theDialog.getInputEditText().setSingleLine(false);
                            theDialog.show();
                        }
                        else if (which == 1) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(sdf.parse(itemsArrayList.get(position_tag).date,
                                    new ParsePosition(0)));
                            // CalendarDatePickerDialogFragment.OnDateSetListener
                            CalendarDatePickerDialogFragment cdp =
                                    new CalendarDatePickerDialogFragment()
                                            .setThemeCustom(R.style.DateTheme)
                                            .setOnDateSetListener(MainActivity.this)
                                            .setPreselectedDate(cal.get(Calendar.YEAR),
                                                    cal.get(Calendar.MONTH),
                                                    cal.get(Calendar.DATE));
                            Bundle args = new Bundle();
                            args.putInt("position", position_tag);
                            cdp.setArguments(args);
                            cdp.show(getSupportFragmentManager(), TAG_CODE);
                        }
                        else if (which == 2) {
                            int preselectedIndex = -1;
                            if (itemsArrayList.get(position_tag).priority.equals("Low Priority")) {
                                preselectedIndex = 2;
                            }
                            else if (itemsArrayList.get(position_tag).priority.equals("Mid Priority")) {
                                preselectedIndex = 1;
                            }
                            else if (itemsArrayList.get(position_tag).priority.equals("High Priority")) {
                                preselectedIndex = 0;
                            }
                            new MaterialDialog.Builder(MainActivity.this)
                                    .items(R.array.priority)
                                    .itemsCallbackSingleChoice(preselectedIndex,
                                            new MaterialDialog.ListCallbackSingleChoice() {
                                                @Override
                                                public boolean onSelection(MaterialDialog dialog,
                                                                           View view,
                                                                           int which,
                                                                           CharSequence text) {
                                                    if (which == 2) {
                                                        itemsArrayList.get(position_tag).priority = "Low Priority";
                                                    }
                                                    else if (which == 1) {
                                                        itemsArrayList.get(position_tag).priority = "Mid Priority";
                                                    }
                                                    else if (which == 0) {
                                                        itemsArrayList.get(position_tag).priority = "High Priority";
                                                    }
                                                    notifyItemsAdapter();

                                                    writeItems();

                                                    notifyMaterialDialogAdapter(position_tag);

                                                    return true; // allow selection
                                                }
                                            })
                                    .positiveText(getString(R.string.save_button))
                                    .show();
                        }
                        // dialog.dismiss();
                    }
                })
                .build().show();
    }

    private ArrayList<MaterialSimpleListItem> createDataArrayList(TodoModel theTodoModel) {
        ArrayList<MaterialSimpleListItem> dataArrayList = new ArrayList<>();
        dataArrayList.add(new MaterialSimpleListItem.Builder(MainActivity.this)
                .content(theTodoModel.content)
                .icon(R.drawable.ic_bookmark_border_black_48dp)
                .backgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryLight))
                .build());
        dataArrayList.add(new MaterialSimpleListItem.Builder(this)
                .content(theTodoModel.date)
                .icon(R.drawable.ic_date_range_black_48dp)
                .backgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorDateLight))
                .build());
        dataArrayList.add(new MaterialSimpleListItem.Builder(this)
                .content(theTodoModel.priority)
                .icon(R.drawable.ic_priority_high_black_48dp)
                .backgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight))
                .build());
        return dataArrayList;
    }

    private void notifyMaterialDialogAdapter(int position_tag) {
        if (materialDialogAdapter != null) {
            materialDialogAdapter.clear();
            materialDialogAdapter.addAll(createDataArrayList(itemsArrayList.get(position_tag)));
            materialDialogAdapter.notifyDataSetChanged();
        }
    }

    private void defaultItems() {
        itemsArrayList = new ArrayList<>();
        if (DefaultItemMode == 1) {
            for (int index = DefaultDebugItemCount; index >= 1; index--) {
                itemsArrayList.add(new TodoModel("Item " + Integer.toString(index),
                        sdf.format(new Date()),
                        "Low Priority"
                ));
            }
        }
        else if (DefaultItemMode == 2) {
            itemsArrayList.add(new TodoModel("My Birthday",
                    "08/08/2016",
                    "Low Priority"
            ));
            itemsArrayList.add(new TodoModel("Google Keep\n" +
                    "UI is awesome\n" +
                    "Try to implement it",
                    "07/31/2016",
                    "High Priority"
            ));
            itemsArrayList.add(new TodoModel("Independence Day",
                    "07/04/2016",
                    "Low Priority"
            ));
            itemsArrayList.add(new TodoModel("Game of Thrones",
                    "06/26/2016",
                    "Low Priority"
            ));
            itemsArrayList.add(new TodoModel("CodePath Pre-work\n" +
                    "Extend it : >",
                    "06/22/2016",
                    "High Priority"
            ));
        }
    }

    private void readItems() {
        if (BackEndMode == 1) {
            File filesDir = getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                ArrayList<String> inputs =
                        new ArrayList<String>(FileUtils.readLines(todoFile));
                for (int index = 0; index < inputs.size(); index++) {
                    itemsArrayList.add(new TodoModel(inputs.get(index),
                            sdf.format(new Date()),
                            "Low Priority"
                    ));
                }
            } catch (IOException e) {
                e.printStackTrace();
                defaultItems();
                writeItems();
            }
        }
        else if (BackEndMode == 2) {
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
                itemsArrayList = new ArrayList<>();
                while (it.hasNext()) {
                    TodoModel theTodoModel = it.next();
                    // do something with bunny
                    itemsArrayList.add(0, theTodoModel);
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
        if (BackEndMode == 1) {
            File filesDir = getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                FileUtils.writeLines(todoFile, itemsArrayList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (BackEndMode == 2) {
            // TODO: only update the necessary items instead of all
            cupboard().withDatabase(db).delete(TodoModel.class, null);
            for (int index = 0; index < itemsArrayList.size(); index++) {
                cupboard().withDatabase(db).put(itemsArrayList.get(index));
            }
        }
    }

    private void notifyItemsAdapter() {
        if (CustomAdapter == 1) {
            itemsArrayAdapter.notifyDataSetChanged();
        }
        else if (CustomAdapter == 2) {
            itemsCustomAdapter.notifyDataSetChanged();
        }
    }

    private void notifyItemsAdapter(int position) {
        if (CustomAdapter == 3) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
            ((SwipeableExampleFragment) fragment).notifyItemChanged(position);
        }
    }

    private void glideRandomDrawable() {
        Glide.with(MainActivity.this).load(getRandomDrawable()).centerCrop().into(imageView);
    }

    private static int getRandomDrawable() {
        final int totalDrawable = 3;
        int nextDrawable = RANDOM.nextInt(totalDrawable);
        while (currentDrawable == nextDrawable) {
            nextDrawable = RANDOM.nextInt(totalDrawable);
        }
        currentDrawable = nextDrawable;
        switch (currentDrawable) {
            default:
            case 0:
                return R.drawable.coffee_21;
            case 1:
                return R.drawable.coffee_22;
            case 2:
                return R.drawable.coffee_23;
        }
    }

    // android-advancedrecyclerview
    // ItemPinnedMessageDialogFragment.EventListener
    public void onNotifyItemPinnedDialogDismissed(int position, boolean ok) {
        getDataProvider().getItem(position).setPinned(ok);
        notifyItemsAdapter(position);
    }

    public ExampleDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ExampleDataProviderFragment) fragment).getDataProvider();
    }

    // android-advancedrecyclerview
    public void onItemRemoved(int position) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }

    // android-advancedrecyclerview
    private void onItemUndoActionClicked() {
        int position = getDataProvider().undoLastRemoval();
        if (position >= 0) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
            ((SwipeableExampleFragment) fragment).notifyItemInserted(position);
        }
    }

    // android-advancedrecyclerview
    public void onItemPinned(int position) {
        final DialogFragment dialog = ItemPinnedMessageDialogFragment.newInstance(position);

        getSupportFragmentManager()
                .beginTransaction()
                .add(dialog, FRAGMENT_TAG_ITEM_PINNED_DIALOG)
                .commit();
    }

    // android-advancedrecyclerview
    public void onItemClicked(int position) {
        AbstractDataProvider.Data data = getDataProvider().getItem(position);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            notifyItemsAdapter(position);
        }
        else {
            editItem(null, position);
        }
    }

    // android-advancedrecyclerview
    public boolean onItemLongClicked(int position) {
        AbstractDataProvider.Data data = getDataProvider().getItem(position);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            notifyItemsAdapter(position);
            return false;
        }
        else {
            return removeItem(position);
        }
    }
}
