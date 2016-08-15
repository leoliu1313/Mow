package com.example.chinyao.mow.mowtweebook.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowtweebook.adapter.MowtweebookViewPagerAdapter;
import com.example.chinyao.mow.mowtweebook.fragment.MowtweebookFragment;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookPersistentTweet;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookRestApplication;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookRestClient;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookUtility;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    public ViewPager viewPager;
    @BindView(R.id.m_fab)
    FloatingActionButton fab;

    public MowtweebookFragment theHomeTimelineFragment;
    public MowtweebookFragment theUserTimelineFragment;

    private MowtweebookRestClient client;
    private MenuItem miActionProgressItem;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mowdigest_activity);

        // ButterKnife
        ButterKnife.bind(this);
        MowtweebookUtility.setupContext(this);

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
        viewPager.setOffscreenPageLimit(MowtweebookViewPagerAdapter.NUM_ITEMS - 1);

        // http://guides.codepath.com/android/Creating-and-Using-Fragments
        // http://guides.codepath.com/android/Creating-and-Using-Fragments#communicating-with-fragments
        // http://guides.codepath.com/android/ViewPager-with-FragmentPagerAdapter
        // a Fragment must have only a constructor with no arguments.
        /*
        Every fragment must have an empty constructor, so it can be instantiated
        when restoring its activity's state. It is strongly recommended that
        subclasses do not have other constructors with parameters,
        since these constructors will not be called when the fragment is re-instantiated;
        instead, arguments can be supplied by the caller with setArguments(Bundle)
        and later retrieved by the Fragment with getArguments().

        The important thing to keep in mind is that fragments should not directly communicate
        with each other and should generally only communicate with their parent activity.
        Think of the Activity as the controller managing all interaction
        with each of the fragments contained within.
         */
        MowtweebookViewPagerAdapter theAdapter =
                new MowtweebookViewPagerAdapter(getSupportFragmentManager());

        theHomeTimelineFragment = (MowtweebookFragment) theAdapter.getRegisteredFragment(0);
        theUserTimelineFragment = (MowtweebookFragment) theAdapter.getRegisteredFragment(1);

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

        searchItem = menu.findItem(R.id.action_search);

        final MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(false);

        // Now we need to hook up a listener for when a search is performed:
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewPager.setCurrentItem(0);
                theHomeTimelineFragment.mode = 4;
                theHomeTimelineFragment.clearAndrefreshAsync(query);

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

        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                PopupMenu popup = new PopupMenu(
                        MowtweebookActivity.this,
                        findViewById(R.id.action_filter)
                );
                // Inflate the menu from xml
                popup.getMenuInflater().inflate(R.menu.mowtweebook_actions, popup.getMenu());
                // Setup menu item selection
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.change_location:
                                new MaterialDialog.Builder(MowtweebookActivity.this)
                                    .title(getString(R.string.change_location_title))
                                    .content(getString(R.string.change_location_content))
                                    .positiveText(getString(R.string.change))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            // TODO
                                        }
                                    }).build().show();
                                return true;
                            case R.id.clear_data:
                                Toast.makeText(MowtweebookActivity.this,
                                        "Delete persistent tweets",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                MowtweebookPersistentTweet.deleteAll();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                // Handle dismissal with: popup.setOnDismissListener(...);
                // Show the menu
                popup.show();
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

    // http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        // ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        searchItem.setVisible(false);
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
        searchItem.setVisible(true);
    }
}
