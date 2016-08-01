package com.example.chinyao.mow.mowdigest;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.model.MowdigestPopularNews;
import com.example.chinyao.mow.mowdigest.model.MowdigestSearchNews;
import com.example.chinyao.mow.mowdigest.model.MowdigestSearchResult;
import com.example.chinyao.mow.mowdigest.swipe.MowdigestFakeAdapter;
import com.example.chinyao.mow.mowdigest.swipe.MowdigestSwipeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestFragment extends Fragment implements MowdigestSwipeAdapter.OnAsyncFinishedListener {
    // ButterKnife
    // http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
    @BindView(R.id.mowtube_swipe_refresh_layout)
    SwipeRefreshLayout theSwipeRefreshLayout;
    @BindView(R.id.mowtube_recycler_view)
    RecyclerView theRecyclerView;

    public MenuItem searchItem;
    public String query = null;
    public int page = 1;

    private int mode = 1;
    private List<MowdigestPopularNews> newsDigest = null;
    private MowdigestRecyclerAdapter newsDigestAdapter = null;
    private Handler handler = null;
    private Runnable runnable = null; // remember to new Handler(), onDestroy(), removeCallbacksAndMessages()
    private boolean lock = false;

    public static final int NewsContentMode = 2;
    // 1: debug
    // 2: nytimes api

    public static MowdigestFragment newInstance(int mode, List<MowdigestPopularNews> newsDigest) {
        MowdigestFragment theFragment = new MowdigestFragment();

        theFragment.mode = mode;
        theFragment.newsDigest = newsDigest; // avoid java.lang.NullPointerException at getItemCount()
        theFragment.handler = new Handler();

        return theFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Note that this can have more than RecyclerView
        SwipeRefreshLayout theRootContainer =
                (SwipeRefreshLayout) inflater.inflate(R.layout.mowtube_stream_fragment, container, false);

        // ButterKnife
        ButterKnife.bind(this, theRootContainer);

        setupRecyclerView(theRecyclerView);

        setupSwipeRefreshLayout();

        // orientation issue
        // http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments
        // http://stackoverflow.com/questions/32478968/android-viewpager-orientation-change
        // https://medium.com/@roideuniverse/android-viewpager-fragmentpageradapter-and-orientation-changes-256c23bee035#.ufb2ywv33
        // http://stackoverflow.com/questions/28982512/handling-orientation-change-with-viewpager-fragmentpageradapter
        // https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
        // http://guides.codepath.com/android/Handling-Configuration-Changes
        // http://guides.codepath.com/android/Understanding-App-Resources
        setRetainInstance(true);

        return theRootContainer;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        if (mode == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(
                    new MowdigestFakeAdapter(
                            getActivity(),
                            new ArrayList<>(Arrays.asList("")),
                            newsDigest,
                            this
                    )
            );
        }
        else if (mode == 2){
            // TODO
            // recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            if (NewsContentMode == 1) {
                recyclerView.setAdapter(
                        new MowdigestRecyclerAdapter(
                                getActivity(),
                                MowdigestPopularNews.debug(),
                                recyclerView
                        )
                );
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
                        if (MowdigestActivity.need_clear) {
                            customLoadMoreDataFromApi();
                        }
                    }
                });
                newsDigestAdapter = new MowdigestRecyclerAdapter(
                        getActivity(),
                        newsDigest,
                        recyclerView
                );
                recyclerView.setAdapter(newsDigestAdapter);
            }
        }
    }

    private void customLoadMoreDataFromApi() {
        if (!lock) {
            lock = true;
            page++;
            final Call<MowdigestSearchResult> call =
                    MowdigestActivity.TheAPIInterface.articleSearch(
                            query,
                            null,
                            "newest",
                            null,
                            null,
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
                            newsDigest.add(MowdigestPopularNews.fromSearchNews(theNews));
                        }
                        notifyNewsDigest();
                        lock = false;
                    }
                }

                @Override
                public void onFailure(Call<MowdigestSearchResult> call, Throwable t) {
                    lock = false;
                    Log.d("MowdigestFragment", "onFailure");
                }
            });
        }
    }

    void notifyNewsDigest() {
        newsDigestAdapter.notifyDataSetChanged();
    }

    // pull-to-refresh
    // http://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide
    private void setupSwipeRefreshLayout() {
        // Setup refresh listener which triggers new data loading
        if (mode == 1) {
            theSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    newsDigest.clear(); // avoid crash here if mode == 2
                    refreshAsync();
                }
            });
        }
        else if (mode == 2) {
            theSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // newsDigest.clear(); // avoid crash here if mode == 2
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
            // this is fake
            if (runnable != null) {
                handler.removeCallbacks(runnable);
                runnable = null;
            }
            runnable = new Runnable() {
                @Override
                public void run() {
                    theSwipeRefreshLayout.setRefreshing(false);
                }
            };
            handler.postDelayed(runnable, 1000);
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

    @Override
    public void onAsyncFinished(int preOffset) {
        if (preOffset == 0) {
            theSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
