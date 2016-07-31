package com.example.chinyao.mow.mowdigest;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.model.MowdigestNews;
import com.example.chinyao.mow.mowtube.MowtubeListFragment;
import com.example.chinyao.mow.mowtube.MowtubeViewPagerAdapter;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MowdigestActivity extends AppCompatActivity {
    // ButterKnife
    // http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
    @BindView(R.id.m_app_bar_layout)
    AppBarLayout theAppBarLayout;
    @BindView(R.id.m_toolbar)
    Toolbar toolbar;
    @BindView(R.id.m_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.m_search_view)
    MaterialSearchView searchView;
    @BindView(R.id.m_view_pager)
    ViewPager viewPager;

    private MowdigestFragment newsDigestFragment;

    public static OkHttpClient TheOkHttpClient = null;
    public static MowdigestAPIInterface TheAPIInterface = null;

    public static final String BASE_URL = "http://api.nytimes.com";
    public static final String MOST_POPULAR = "/svc/mostpopular/v2/mostviewed"; /* /all-sections/1.json */
    public static final String MOST_POPULAR_API_KEY = "fb2092b45dc44c299ecf5098b9b1209d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mowdigest_activity);

        // ButterKnife
        ButterKnife.bind(this);

        setupNetwork();

        setSupportActionBar(toolbar);

        setupSearchView();

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
        List<MowdigestNews> newsDigest = new ArrayList<>();

        // 3 tabs so set it to 2
        viewPager.setOffscreenPageLimit(2);

        // TODO
        // people on stackoverflow said this is bad implementation
        // use getView() instead?
        MowtubeViewPagerAdapter mowtubeViewPagerAdapter = new MowtubeViewPagerAdapter(getSupportFragmentManager());
        mowtubeViewPagerAdapter.addFragment(MowdigestFragment.newInstance(1, newsDigest), getString(R.string.training));
        newsDigestFragment = MowdigestFragment.newInstance(2, newsDigest);
        mowtubeViewPagerAdapter.addFragment(newsDigestFragment, getString(R.string.digest));
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(3), getString(R.string.explore));
        viewPager.setAdapter(mowtubeViewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        // use         |     tab1    |     tab2    |
        // instead of  |  tab1  |  tab2  |         |
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO
                if (arg0 == 1) {
                    newsDigestFragment.loadNewsDigest();
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
                    newsDigestFragment.loadNewsDigest();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mowdigest_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                searchView.setVisibility(View.VISIBLE);
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
