package com.example.chinyao.mow.mowtweebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.chinyao.mow.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

public class MowtweebookActivity extends AppCompatActivity {
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
    @BindView(R.id.m_fab)
    FloatingActionButton fab;

    private MowtweebookFragment HomeTimelineFragment;
    private MowtweebookFragment UserTimelineFragment;
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

        setupNetwork();

        setSupportActionBar(toolbar);

        setupViewPager();

        setupFAB();

        Toast.makeText(this,
                getResources().getString(R.string.app_version),
                Toast.LENGTH_SHORT)
                .show();
    }

    private void setupNetwork() {
        // chrome://inspect/#devices
        // TODO: square/retrofit with OAuth is hard... try again?
        client = MowtweebookRestApplication.getRestClient();
        if (!client.hasNetwork()) {
            Toast.makeText(this,
                    getResources().getString(R.string.no_internet),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setupViewPager() {
        // 3 tabs so set it to 2
        viewPager.setOffscreenPageLimit(2);

        MowtweebookViewPagerAdapter theAdapter =
                new MowtweebookViewPagerAdapter(
                        getSupportFragmentManager(),
                        this, // context
                        viewPager,
                        client
                        );

        HomeTimelineFragment = (MowtweebookFragment) theAdapter.getRegisteredFragment(0);
        UserTimelineFragment = (MowtweebookFragment) theAdapter.getRegisteredFragment(1);

        viewPager.setAdapter(theAdapter);

        tabLayout.setupWithViewPager(viewPager);

        // use         |     tab1    |     tab2    |
        // instead of  |  tab1  |  tab2  |         |
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO
            }

            @Override
            public void onPageSelected(int pos) {
                // TODO
            }
        });
    }

    private void setupFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog theDialog =
                        new MaterialDialog.Builder(MowtweebookActivity.this)
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .positiveText(getString(R.string.save_button))
                                .inputRangeRes(1, 100, R.color.mowColorAccentLight)
                                .input(null, "", new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog,
                                                        CharSequence input) {
                                        // Toast.makeText(MowActivity.this,
                                        //         input.toString(),
                                        //         Toast.LENGTH_SHORT).show();

                                        HomeTimelineFragment.doTweet(input.toString());

                                        // showMaterialDialog(position_tag);
                                    }
                                }).build();
                theDialog.getInputEditText().setSingleLine(false);
                theDialog.show();
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
        HomeTimelineFragment.searchItem = searchItem;

        final MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);

        // Now we need to hook up a listener for when a search is performed:
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                UserTimelineFragment.doSearch(query);

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

        return super.onCreateOptionsMenu(menu);
    }
}
