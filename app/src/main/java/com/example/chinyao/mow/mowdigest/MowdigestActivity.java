package com.example.chinyao.mow.mowdigest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.model.MowdigestPopularNews;
import com.example.chinyao.mow.mowdigest.model.MowdigestSearchNews;
import com.example.chinyao.mow.mowdigest.model.MowdigestSearchResult;
import com.example.chinyao.mow.mowtube.MowtubeListFragment;
import com.example.chinyao.mow.mowtube.MowtubeViewPagerAdapter;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MowdigestActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
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

    private List<MowdigestPopularNews> newsDigest;
    private MowdigestFragment newsTrainingFragment;
    private MowdigestFragment newsDigestFragment;
    private int preselectedIndex = 0;
    private EditText passwordInput;
    private View positiveAction;
    Spinner spinner;
    Button theButton;
    CheckBox checkbox;

    public static OkHttpClient TheOkHttpClient = null;
    public static MowdigestAPIInterface TheAPIInterface = null;

    // TODO: avoid this?
    public static boolean need_clear = false;

    public static final String API_KEY = "fb2092b45dc44c299ecf5098b9b1209d";
    public static final String BASE_URL = "http://api.nytimes.com";
    public static final String MOST_POPULAR = "/svc/mostpopular/v2/mostviewed"; /* /all-sections/1.json */
    public static final String ARTICALE_SEARCH = "/svc/search/v2/articlesearch.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mowdigest_activity);

        // ButterKnife
        ButterKnife.bind(this);

        setupNetwork();

        setSupportActionBar(toolbar);

        setupViewPager();

        Toast.makeText(this,
                getResources().getString(R.string.app_version),
                Toast.LENGTH_SHORT)
                .show();
    }

    private void setupNetwork() {
        // chrome://inspect/#devices
        Stetho.initializeWithDefaults(this);
        // add a Facebook StethoInterceptor to the OkHttpClient's list of network interceptors
        TheOkHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        // set up a breakpoint below for early network calls

        Retrofit retrofit = new Retrofit.Builder()
                .client(MowdigestActivity.TheOkHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheAPIInterface = retrofit.create(MowdigestAPIInterface.class);
    }

    private void setupViewPager() {
        newsDigest = new ArrayList<>();

        // 3 tabs so set it to 2
        viewPager.setOffscreenPageLimit(2);

        // TODO
        // people on stackoverflow said this is bad implementation
        // use getView() instead?
        MowtubeViewPagerAdapter theAdapter = new MowtubeViewPagerAdapter(getSupportFragmentManager());

        newsTrainingFragment = MowdigestFragment.newInstance(1, newsDigest);
        theAdapter.addFragment(newsTrainingFragment, getString(R.string.training));

        newsDigestFragment = MowdigestFragment.newInstance(2, newsDigest);
        theAdapter.addFragment(newsDigestFragment, getString(R.string.digest));

        theAdapter.addFragment(MowtubeListFragment.newInstance(3), getString(R.string.explore));

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
                    newsDigestFragment.notifyNewsDigest();
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
                    newsDigestFragment.notifyNewsDigest();
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
        newsTrainingFragment.searchItem = searchItem;

        final MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);

        // Now we need to hook up a listener for when a search is performed:
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                // perform query here
                need_clear = true;
                // viewPager.setCurrentItem(1);
                viewPager.setCurrentItem(1, true);
                newsDigestFragment.theSwipeRefreshLayout.setRefreshing(true);
                newsDigest.clear();
                final Call<MowdigestSearchResult> call =
                        MowdigestActivity.TheAPIInterface.articleSearch(
                                query,
                                null,
                                "newest",
                                null,
                                null,
                                1,
                                API_KEY);
                call.enqueue(new Callback<MowdigestSearchResult>() {
                    @Override
                    public void onResponse(Call<MowdigestSearchResult> call, Response<MowdigestSearchResult> response) {
                        Log.d("MowdigestActivity", "onResponse");
                        Log.d("MowdigestActivity",
                                "statusCode " + response.code());
                        MowdigestSearchResult theSearch = response.body();
                        if (theSearch != null
                                && theSearch.getResponse() != null
                                && theSearch.getResponse().getDocs() != null) {
                            Log.d("MowdigestActivity",
                                    "theSearch.getResponse().getDocs().size() " + theSearch.getResponse().getDocs().size());
                            for (MowdigestSearchNews theNews : theSearch.getResponse().getDocs()) {
                                newsDigest.add(MowdigestPopularNews.fromSearchNews(theNews));
                            }
                            newsDigestFragment.notifyNewsDigest();
                            newsDigestFragment.theSwipeRefreshLayout.setRefreshing(false);
                            need_clear = true;
                            newsDigestFragment.query = query;
                            newsDigestFragment.page = 1;
                        }
                    }

                    @Override
                    public void onFailure(Call<MowdigestSearchResult> call, Throwable t) {
                        Log.d("MowdigestSwipeAdapter", "onFailure");
                    }
                });

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
                showMaterialDialog();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.app_version),
                        Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    void showMaterialDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Filter")
                .customView(R.layout.mowdigest_filter, true)
                .positiveText(getString(R.string.save_button))
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String value = spinner.getSelectedItem().toString();
                        Toast.makeText(getApplicationContext(),
                                "Password: " + passwordInput.getText().toString() +
                                "spinner: " + value,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }).build();
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        View view = dialog.getCustomView();

        if (view != null) {
            theButton = (Button) view.findViewById(R.id.button);
            spinner = (Spinner) view.findViewById(R.id.mySpinner);
            passwordInput = (EditText) dialog.getCustomView().findViewById(R.id.password);
            checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.showPassword);
        }

        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd =
                        com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        MowdigestActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        //noinspection ConstantConditions
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // Toggling the show password CheckBox will mask or unmask the password input EditText
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
            }
        });
        dialog.show();
        positiveAction.setEnabled(false); // disabled by default

        /*
        new MaterialDialog.Builder(MowdigestActivity.this)
                .items(R.array.sort)
                .itemsCallbackSingleChoice(preselectedIndex,
                        new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog,
                                                       View view,
                                                       int which,
                                                       CharSequence text) {
                                if (which == 0) {
                                    Toast.makeText(getApplicationContext(),
                                            "000",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                                else if (which == 1) {
                                    Toast.makeText(getApplicationContext(),
                                            "111",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                                return true; // allow selection
                            }
                        })
                .positiveText(getString(R.string.save_button))
                .show();
                */
    }

    @Override
    public void onDateSet(DatePickerDialog view,
                          int year, int monthOfYear, int dayOfMonth,
                          int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        Toast.makeText(getApplicationContext(),
                        " year: " + year
                        + " month: " + (monthOfYear + 1)
                        + " day\n: " + dayOfMonth
                        + " year: " + yearEnd
                        + " month: " + (monthOfYearEnd + 1)
                        + " day: " + dayOfMonthEnd
                ,
                Toast.LENGTH_SHORT)
                .show();
    }
}
