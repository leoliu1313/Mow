package com.example.chinyao.mow.mowtweebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowtube.MowtubeViewPagerAdapter;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import okhttp3.OkHttpClient;

public class MowtweebookActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    // ButterKnife
    // http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
    @BindView(R.id.m_app_bar_layout)
    AppBarLayout theAppBarLayout;
    @BindView(R.id.m_toolbar)
    Toolbar toolbar;
    @BindView(R.id.m_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.m_view_pager)
    ViewPager viewPager;

    private List<MowtweebookTweet> tweets;

    private MowtweebookFragment timelineFragment;
    private MowtweebookFragment emptyFragment;
    private Spinner sort_spinner;
    private TextView date_range_textview;
    private CheckBox art_checkbox;
    private CheckBox style_checkbox;
    private CheckBox sports_checkbox;

    public static OkHttpClient TheOkHttpClient = null;
    private MowtweebookRestClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mowdigest_activity);

        // ButterKnife
        ButterKnife.bind(this);

        // chrome://inspect/#devices
        setupNetwork();

        setSupportActionBar(toolbar);

        setupData();

        setupViewPager();

        Toast.makeText(this,
                getResources().getString(R.string.app_version),
                Toast.LENGTH_SHORT)
                .show();
    }

    private void setupData() {
        tweets = new ArrayList<>();
    }

    private void setupNetwork() {
        client = MowtweebookRestApplication.getRestClient();
        populateTimeline();
    }

    private void setupViewPager() {
        // 3 tabs so set it to 2
        viewPager.setOffscreenPageLimit(2);

        // TODO
        // people on stackoverflow said this is bad implementation
        // use getView() instead?
        MowtubeViewPagerAdapter theAdapter = new MowtubeViewPagerAdapter(getSupportFragmentManager());

        timelineFragment = MowtweebookFragment.newInstance(1, tweets, viewPager);
        theAdapter.addFragment(timelineFragment, getString(R.string.training));

        emptyFragment = MowtweebookFragment.newInstance(2, tweets, viewPager);
        emptyFragment.sections = new boolean[20];
        theAdapter.addFragment(emptyFragment, getString(R.string.digest));

        // theAdapter.addFragment(MowtubeListFragment.newInstance(3), getString(R.string.explore));

        viewPager.setAdapter(theAdapter);

        tabLayout.setupWithViewPager(viewPager);
        // use         |     tab1    |     tab2    |
        // instead of  |  tab1  |  tab2  |         |
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO
                if (arg0 == 1) {
                    // emptyFragment.notifyNewsDigest();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO
            }

            @Override
            public void onPageSelected(int pos) {
                // TODO
                if (pos == 1) {
                    // emptyFragment.notifyNewsDigest();
                }
            }
        });
    }

    // http://guides.codepath.com/android/Extended-ActionBar-Guide#adding-searchview-to-actionbar
    // http://ramannanda.blogspot.com/2014/10/android-searchview-integration-with.html
    // http://www.materialdoc.com/search-filter/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mowdigest_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        timelineFragment.searchItem = searchItem;

        final MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);

        // Now we need to hook up a listener for when a search is performed:
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                emptyFragment.doArticleSearch(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Expand the search view and request focus on the start up or anytime
        /*
        searchItem.expandActionView();
        searchView.requestFocus();
        */

        // http://stackoverflow.com/questions/9327826/searchviews-oncloselistener-doesnt-work
        MenuItemCompat.setOnActionExpandListener(
                searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        filterItem.setVisible(true);
                        return true; // true to expand
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        filterItem.setVisible(false);
                        return true; // true to collapse
                    }
                }
        );

        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showMaterialDialog(searchView);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private String getDateString(int begin_year, int begin_month, int begin_date) {
        if (begin_year == 0 || begin_month == 0 || begin_date == 0) {
            return null;
        }
        else {
            String year = Integer.toString(begin_year);
            String month = Integer.toString(begin_month);
            if (month.length() == 1) {
                month = "0" + month;
            }
            String date = Integer.toString(begin_date);
            if (date.length() == 1) {
                date = "0" + date;
            }
            return year + month + date;
        }
    }

    void showMaterialDialog(final SearchView searchView) {
        // init
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Filter")
                .customView(R.layout.mowdigest_filter, true)
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String input = "";
                        if (emptyFragment.sections[0]) {
                            input += " Arts";
                        }
                        if (emptyFragment.sections[4]) {
                            input += " Style";
                        }
                        if (emptyFragment.sections[12]) {
                            input += " Sports";
                        }
                        if (input.equals("")) {
                            emptyFragment.fq = null;
                        }
                        else {
                            emptyFragment.fq = "section_name.contains:(\"" + input + "\")";
                        }
                        searchView.clearFocus();
                        emptyFragment.doArticleSearch();
                        /*
                        String value = sort_spinner.getSelectedItem().toString();
                        Toast.makeText(getApplicationContext(),
                                "sort_spinner: " + value,
                                Toast.LENGTH_LONG)
                                .show();
                                */
                    }
                }).build();
        // View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        View view = dialog.getCustomView();
        if (view != null) {
            date_range_textview = (TextView) view.findViewById(R.id.datae_range_textview);
            sort_spinner = (Spinner) view.findViewById(R.id.sort_spinner);
            art_checkbox = (CheckBox) view.findViewById(R.id.art_checkbox);
            style_checkbox = (CheckBox) view.findViewById(R.id.style_checkbox);
            sports_checkbox = (CheckBox) view.findViewById(R.id.sports_checkbox);
        }

        // read current filter
        if (emptyFragment.begin_date != null && emptyFragment.end_date != null) {
            String input = emptyFragment.begin_date + " ~ " + emptyFragment.end_date;
            date_range_textview.setText(input);
        }
        if (emptyFragment.sections[0]) {
            art_checkbox.setChecked(true);
        }
        if (emptyFragment.sections[4]) {
            style_checkbox.setChecked(true);
        }
        if (emptyFragment.sections[12]) {
            sports_checkbox.setChecked(true);
        }

        // set up callback
        date_range_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd =
                        DatePickerDialog.newInstance(
                                MowtweebookActivity.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        // sort
        sort_spinner.setSelection(emptyFragment.sort_spinner_mode);
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                emptyFragment.sort_spinner_mode = sort_spinner.getSelectedItemPosition();
                /*
                String msupplier = sort_spinner.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(),
                        "msupplier: " + msupplier,
                        Toast.LENGTH_LONG)
                        .show();
                        */
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        // section
        art_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emptyFragment.sections[0] = isChecked;
                /*
                Toast.makeText(getApplicationContext(),
                        "art_checkbox isChecked: " + isChecked,
                        Toast.LENGTH_SHORT)
                        .show();
                        */
            }
        });
        style_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emptyFragment.sections[4] = isChecked;
            }
        });
        sports_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emptyFragment.sections[12] = isChecked;
            }
        });

        // show
        dialog.show();
        // positiveAction.setEnabled(false); // disabled by default
    }

    @Override
    public void onDateSet(DatePickerDialog view,
                          int year, int monthOfYear, int dayOfMonth,
                          int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        emptyFragment.begin_date = getDateString(year, monthOfYear + 1, dayOfMonth);
        emptyFragment.end_date = getDateString(yearEnd, monthOfYearEnd + 1, dayOfMonthEnd);
        String input = emptyFragment.begin_date + " ~ " + emptyFragment.end_date;
        date_range_textview.setText(input);
        /*
        Toast.makeText(getApplicationContext(),
                        " year: " + year
                        + " month: " + (monthOfYear + 1)
                        + " day\n: " + dayOfMonth
                        + " year: " + yearEnd
                        + " month: " + (monthOfYearEnd + 1)
                        + " day: " + dayOfMonthEnd
                ,
                Toast.LENGTH_LONG)
                .show();
        */
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("populateTimeline", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("populateTimeline", errorResponse.toString());
            }
        });
    }
}
