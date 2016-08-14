package com.example.chinyao.mow.mowdigest.detail;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookParcelWrap;

import org.parceler.Parcels;


public class YahooParallaxActivity extends AppCompatActivity {

    private ViewPager mPager;
    private SlidePagerAdapter mPagerAdapter;

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
            first_title = bundle.getString("first_title");
            first_section = bundle.getString("first_section");
            first_abstract = bundle.getString("first_abstract");
        }

        // Parcels
        MowtweebookParcelWrap theWrap =
                (MowtweebookParcelWrap) Parcels.unwrap(getIntent().getParcelableExtra("tweets"));

        // SlidePagerAdapter
        int size = 3;
        if (theWrap != null && theWrap.tweets != null) {
            size = theWrap.tweets.size();
        }
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), size);

        // Bundle
        mPagerAdapter.first_image = first_image;
        mPagerAdapter.first_title = first_title;
        mPagerAdapter.first_section = first_section;
        mPagerAdapter.first_abstract = first_abstract;

        // Parcels
        mPagerAdapter.tweets = theWrap.tweets;

        mPager.setAdapter(mPagerAdapter);
        int[] resId = {R.id.cover_img};
        mPager.setPageTransformer(true, new ParallaxTransformer(0.6f, 0, resId, true));
    }
    
    /**Hide the status bar on pre-19 android
     * Set the status bar to transparent after version 19 */
    private void handleStatusBar() {
        if (Build.VERSION.SDK_INT < 19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            Window w = getWindow();
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
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
        getMenuInflater().inflate(R.menu.menu_yahoo_parallax, menu);
        return true;
    }



}
