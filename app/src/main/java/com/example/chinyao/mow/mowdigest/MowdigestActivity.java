package com.example.chinyao.mow.mowdigest;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.model.MowdigestPopularNews;
import com.example.chinyao.mow.mowdigest.model.MowdigestSearchNews;
import com.example.chinyao.mow.mowdigest.model.MowdigestSearchResult;
import com.example.chinyao.mow.mowtube.MowtubeListFragment;
import com.example.chinyao.mow.mowtube.MowtubeViewPagerAdapter;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    @BindView(R.id.m_view_pager)
    ViewPager viewPager;

    private List<MowdigestPopularNews> newsDigest;
    private MowdigestFragment newsDigestFragment;

    public static OkHttpClient TheOkHttpClient = null;
    public static MowdigestAPIInterface TheAPIInterface = null;

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

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // up right menu button
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            // up left drawer button
            // ab.setDisplayHomeAsUpEnabled(true);
        }

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

    // http://guides.codepath.com/android/Extended-ActionBar-Guide#adding-searchview-to-actionbar
    // http://ramannanda.blogspot.com/2014/10/android-searchview-integration-with.html
    // http://www.materialdoc.com/search-filter/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mowdigest_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Now we need to hook up a listener for when a search is performed:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                final Call<MowdigestSearchResult> call =
                        MowdigestActivity.TheAPIInterface.search(
                                query,
                                null,
                                "newest",
                                null,
                                null,
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
        // searchItem.expandActionView();
        // searchView.requestFocus();

        return super.onCreateOptionsMenu(menu);
    }
}
