package com.example.chinyao.mow.mowtweebook;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.EndlessRecyclerViewScrollListener;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowtweebookFragment extends Fragment {
    // ButterKnife
    // http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
    @BindView(R.id.mowtube_swipe_refresh_layout)
    SwipeRefreshLayout theSwipeRefreshLayout;
    @BindView(R.id.mowtube_recycler_view)
    RecyclerView theRecyclerView;

    private int mode = 1;
    private List<MowtweebookTweet> tweets = null;
    private ViewPager viewPager = null;

    // private MowdigestRecyclerAdapter newsDigestAdapter = null;
    private Handler handler = null;
    private Runnable runnable = null; // remember to new Handler(), onDestroy(), removeCallbacksAndMessages()
    private boolean lock = false;
    private String query = null;
    private int page = 1;
    private boolean first_time = true;

    public MenuItem searchItem = null;
    public String begin_date = null;
    public String end_date = null;
    public int sort_spinner_mode = 0;
    public boolean[] sections = null;
    public String fq = null;

    public static final int NewsContentMode = 2;
    // 1: debug
    // 2: nytimes api

    public static MowtweebookFragment newInstance(int mode,
                                                  List<MowtweebookTweet> tweets,
                                                  ViewPager viewPager) {
        MowtweebookFragment theFragment = new MowtweebookFragment();

        theFragment.mode = mode;
        theFragment.tweets = tweets; // avoid java.lang.NullPointerException at getItemCount()
        theFragment.viewPager = viewPager;

        theFragment.handler = new Handler();

        return theFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Note that this can have more than RecyclerView
        View view = inflater.inflate(R.layout.mowtube_stream_fragment, container, false);

        // orientation issue
        // http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments
        // http://stackoverflow.com/questions/32478968/android-viewpager-orientation-change
        // https://medium.com/@roideuniverse/android-viewpager-fragmentpageradapter-and-orientation-changes-256c23bee035#.ufb2ywv33
        // http://stackoverflow.com/questions/28982512/handling-orientation-change-with-viewpager-fragmentpageradapter
        // https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
        // http://guides.codepath.com/android/Handling-Configuration-Changes
        // http://guides.codepath.com/android/Understanding-App-Resources
        // http://android-er.blogspot.com/2013/05/how-setretaininstancetrue-affect.html
        setRetainInstance(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ButterKnife
        // http://stackoverflow.com/questions/27002200/butterknife-fragment-rotation-giving-nullpointer
        ButterKnife.bind(this, view);

        setupRecyclerView(theRecyclerView);

        setupSwipeRefreshLayout();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        if (mode == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            /*
            recyclerView.setAdapter(
                    new MowdigestFakeAdapter(
                            getActivity(),
                            new ArrayList<>(Arrays.asList("")),
                            tweets,
                            this
                    )
            );
            */
        }
        else if (mode == 2){
            // recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            if (NewsContentMode == 1) {
                /*
                recyclerView.setAdapter(
                        new MowdigestRecyclerAdapter(
                                getActivity(),
                                MowdigestPopularNews.debug(),
                                recyclerView
                        )
                );
                */
            }
            else if (NewsContentMode == 2) {
                // Endless-Scrolling-with-AdapterViews-and-RecyclerView
                // http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView#troubleshooting
                // https://gist.github.com/nesquena/d09dc68ff07e845cc622
                recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        customLoadMoreDataFromApi();
                    }
                });
                /*
                newsDigestAdapter = new MowdigestRecyclerAdapter(
                        getActivity(),
                        tweets,
                        recyclerView
                );
                recyclerView.setAdapter(newsDigestAdapter);
                // rotation orientation
                if (first_time) {
                    // avoid reloading
                    first_time = false;
                    doArticleSearch();
                }
                */
            }
        }
    }

    private void customLoadMoreDataFromApi() {
        /*
        if (!lock) {
            lock = true;
            page++;
            final Call<MowdigestSearchResult> call =
                    MowdigestActivity.TheAPIInterface.articleSearch(
                            query,
                            fq,
                            sort_spinner_mode == 0 ? "newest" : "oldest",
                            begin_date,
                            end_date,
                            page,
                            MowdigestActivity.API_KEY);
            call.enqueue(new Callback<MowdigestSearchResult>() {
                @Override
                public void onResponse(Call<MowdigestSearchResult> call, Response<MowdigestSearchResult> response) {
                    Log.d("MowdigestFragment", "onResponse");
                    Log.d("MowdigestFragment",
                            "statusCode " + response.code());
                    MowdigestSearchResult theSearch = response.body();
                    if (theSearch != null
                            && theSearch.getResponse() != null
                            && theSearch.getResponse().getDocs() != null) {
                        Log.d("MowdigestFragment",
                                "theSearch.getResponse().getDocs().size() " + theSearch.getResponse().getDocs().size());
                        for (MowdigestSearchNews theNews : theSearch.getResponse().getDocs()) {
                            tweets.add(MowdigestPopularNews.fromSearchNews(theNews));
                        }
                        notifyNewsDigest();
                    }
                    lock = false;
                }

                @Override
                public void onFailure(Call<MowdigestSearchResult> call, Throwable t) {
                    Log.d("MowdigestFragment", "onFailure");
                    lock = false;
                }
            });
        }
        */
    }

    void notifyNewsDigest() {
        /*
        if (newsDigestAdapter != null) {
            newsDigestAdapter.notifyDataSetChanged();
        }
        */
    }

    // pull-to-refresh
    // http://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide
    // theSwipeRefreshLayout.setRefreshing(false)
    private void setupSwipeRefreshLayout() {
        // Setup refresh listener which triggers new data loading
        if (mode == 1) {
            theSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    tweets.clear(); // avoid crash here if mode == 2
                    refreshAsync();
                }
            });
        }
        else if (mode == 2) {
            theSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // tweets.clear(); // avoid crash here if mode == 2
                    refreshAsync();
                }
            });
        }
        // Configure the refreshing colors
        theSwipeRefreshLayout.setColorSchemeResources(
                R.color.mowtubeColorAccent,
                R.color.mowtubeColorAccentLightLight
        );
    }

    public void refreshAsync() {
        if (mode == 1) {
            // this is real
            setupRecyclerView(theRecyclerView);
        }
        else if (mode == 2) {
            if (true) {
                // this is real
                doArticleSearch();
            }
            else {
                // this is fake
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                    runnable = null;
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (theSwipeRefreshLayout != null) {
                            theSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // avoid memory leak
        // https://techblog.badoo.com/blog/2014/08/28/android-handler-memory-leaks/
        // http://stackoverflow.com/questions/8430805/clicking-the-back-button-twice-to-exit-an-activity
        if (handler != null) {
            // remove all the callbacks
            handler.removeCallbacksAndMessages(null);
        }
    }

    /*
    @Override
    public void onAsyncFinished(int preOffset) {
        if (preOffset == 0) {
            if (theSwipeRefreshLayout != null) {
                theSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
    */

    public void doArticleSearch() {
        doArticleSearch(query);
    }

    public void doArticleSearch(final String theQuery) {
        if (!lock) {
            lock = true;
            // perform query here
            // viewPager.setCurrentItem(1);
            viewPager.setCurrentItem(1, true);
            if (theSwipeRefreshLayout != null) {
                theSwipeRefreshLayout.setRefreshing(true);
            }
            tweets.clear();
            /*
            final Call<MowdigestSearchResult> call =
                    MowdigestActivity.TheAPIInterface.articleSearch(
                            theQuery,
                            fq,
                            sort_spinner_mode == 0 ? "newest" : "oldest",
                            begin_date,
                            end_date,
                            1,
                            MowdigestActivity.API_KEY);
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
                            tweets.add(MowdigestPopularNews.fromSearchNews(theNews));
                        }
                        notifyNewsDigest();
                        query = theQuery;
                        page = 1;
                    }
                    if (theSwipeRefreshLayout != null) {
                        theSwipeRefreshLayout.setRefreshing(false);
                    }
                    lock = false;
                }

                @Override
                public void onFailure(Call<MowdigestSearchResult> call, Throwable t) {
                    Log.d("MowdigestSwipeAdapter", "onFailure");
                    if (theSwipeRefreshLayout != null) {
                        theSwipeRefreshLayout.setRefreshing(false);
                    }
                    lock = false;
                }
            });
            */
        }
    }
}
