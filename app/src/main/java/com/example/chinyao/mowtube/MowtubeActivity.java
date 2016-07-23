package com.example.chinyao.mowtube;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MowtubeActivity extends AppCompatActivity {

    // TODO: Use Shared Preferences
    static Boolean autoplay_on_wifi_only = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mowtube_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        // up right menu button
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        // up left drawer button
        // ab.setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        MowtubeViewPagerAdapter mowtubeViewPagerAdapter = new MowtubeViewPagerAdapter(getSupportFragmentManager());
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(1), getString(R.string.home));
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(2), getString(R.string.upcoming));
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(3), getString(R.string.trending));
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(4), getString(R.string.favorite));
        viewPager.setAdapter(mowtubeViewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (autoplay_on_wifi_only) {
            menu.findItem(R.id.autoplay_on_wifi_only).setChecked(true);
        }
        else {
            menu.findItem(R.id.autoplay_always).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.autoplay_on_wifi_only:
                autoplay_on_wifi_only = true;
                return true;
            case R.id.autoplay_always:
                autoplay_on_wifi_only = false;
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
