package com.example.chinyao.mow.mowdigest.detail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookParcelWrap;

import org.parceler.Parcels;


public class YahooParallaxActivity extends AppCompatActivity {

    private ViewPager mPager;
    private SlidePagerAdapter mPagerAdapter;
    private MowtweebookParcelWrap theWrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mowdigest_detail_parallax);

        setupToolbar();
        handleStatusBar();

        // Bundle

        Bundle bundle = getIntent().getBundleExtra(BundleKey.TYPE_YAHOO);
        String first_image = null;
        String first_title = null;
        String first_section = null;
        String first_abstract = null;
        if (bundle != null) {
            first_image = bundle.getString("first_image");
            first_section = bundle.getString("first_section");
            first_title = bundle.getString("first_title");
            first_abstract = bundle.getString("first_abstract");
        }

        // Parcels
        theWrap = (MowtweebookParcelWrap) Parcels.unwrap(getIntent().getParcelableExtra("tweets"));

        // SlidePagerAdapter
        int size = 3;
        if (theWrap != null && theWrap.tweets != null) {
            size = theWrap.tweets.size();
        }
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), size);

        // Bundle
        if (bundle != null) {
            mPagerAdapter.first_image = first_image;
            mPagerAdapter.first_section = first_section;
            mPagerAdapter.first_title = first_title;
            mPagerAdapter.first_abstract = first_abstract;
        }

        // Parcels
        if (theWrap != null && theWrap.tweets != null) {
            mPagerAdapter.tweets = theWrap.tweets;
        }

        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(getIntent().getIntExtra("default", 0));

        int[] resId = {R.id.cover_img};
        mPager.setPageTransformer(true, new ParallaxTransformer(0.6f, 0, resId, true));
    }

    private void setupToolbar() {
        // http://guides.codepath.com/android/Extended-ActionBar-Guide
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    /**Hide the status bar on pre-19 android
     * Set the status bar to transparent after version 19 */
    private void handleStatusBar() {
        if (Build.VERSION.SDK_INT < 19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            Window w = getWindow();
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // http://guides.codepath.com/android/Sharing-Content-with-Intents

        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_yahoo_parallax, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_settings);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String tmp = mPagerAdapter.first_abstract;
                if (theWrap != null && theWrap.tweets != null) {
                    if (mPager.getCurrentItem() < theWrap.tweets.size()) {
                        tmp = theWrap.tweets.get(mPager.getCurrentItem()).getText();
                    }
                }
                shareIntent.putExtra(Intent.EXTRA_TEXT, tmp);
                startActivity(Intent.createChooser(shareIntent, "")); // title
                return false;
            }
        });

        // Fetch reference to the share action provider
        // miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Return true to display menu
        return true;
    }

    // back button in the top bar doesn't work
    // http://guides.codepath.com/android/Extended-ActionBar-Guide
    // http://codetheory.in/difference-between-setdisplayhomeasupenabled-sethomebuttonenabled-and-setdisplayshowhomeenabled/
    // http://stackoverflow.com/questions/34020745/home-back-button-in-navigation-bar-doesnt-work-in-my-android-app
    // http://stackoverflow.com/questions/22182888/actionbar-up-button-destroys-parent-activity-back-does-not
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
