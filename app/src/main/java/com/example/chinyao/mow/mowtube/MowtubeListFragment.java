package com.example.chinyao.mow.mowtube;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowtube.model.MowtubeMovie;
import com.example.chinyao.mow.mowtube.recycler.MowtubeDividerItemDecoration;
import com.example.chinyao.mow.mowtube.recycler.MowtubeRecyclerViewAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chinyao on 7/23/2016.
 */
public class MowtubeListFragment extends Fragment {

    public static final int ContentMode = 1;
    // 1: debug mode
    // 2: android-async-http

    public static final int ImageLoadingMode = 2;
    // 1: Glide
    // 2: Picasso

    // for landscape view
    public static final int MaxPosterTitleLength = 35;
    public static final int MaxPosterSubLength = 120;

    private Handler handler = null;
    private Runnable runnable = null;
    private int mode = 1;
    private SwipeRefreshLayout theSwipeRefreshLayout = null;

    public static MowtubeListFragment newInstance(int mode) {
        MowtubeListFragment theFragment = new MowtubeListFragment();
        theFragment.mode = mode;
        theFragment.handler = new Handler();

        return theFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Note that this can have more than RecyclerView
        SwipeRefreshLayout theRootContainer =
                (SwipeRefreshLayout) inflater.inflate(R.layout.mowtube_stream_fragment, container, false);

        if (handler == null) {
            handler = new Handler();
        }

        RecyclerView theRecyclerView =
                (RecyclerView) theRootContainer.findViewById(R.id.mowtube_recycler_view);
        setupRecyclerView(theRecyclerView);

        // Lookup the swipe container view
        theSwipeRefreshLayout = theRootContainer;
        // Setup refresh listener which triggers new data loading
        theSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync();
            }
        });
        // Configure the refreshing colors
        theSwipeRefreshLayout.setColorSchemeResources(
                R.color.mowtubeColorAccent,
                R.color.mowtubeColorAccentLightLight
        );

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

    private void setupRecyclerView(final RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        // Show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(true);
        progressDialog.setMessage(getString(R.string.loading_msg));

        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        };
        handler.postDelayed(runnable, 1000);

        if (ContentMode == 1) {
            // debug mode
            recyclerView.setAdapter(
                    new MowtubeRecyclerViewAdapter(
                            getActivity(),
                            MowtubeMovie.generateDebugArrayList(),
                            null
                    )
            );
            if (runnable != null) {
                handler.removeCallbacks(runnable);
                runnable = null;
            }
            progressDialog.dismiss();
        }
        else if (ContentMode == 2) {
            AsyncHttpClient client = new AsyncHttpClient();
            // Turn off Debug Log
            client.setLoggingEnabled(false);
            String url = "";
            if (mode == 1) {
                url = "https://api.themoviedb.org/3/movie/now_playing";
            }
            else if (mode == 2) {
                url = "https://api.themoviedb.org/3/movie/upcoming";
            }
            else if (mode == 3) {
                url = "https://api.themoviedb.org/3/movie/popular";
            }
            else if (mode == 4) {
                // TODO: actually this is favorite
                url = "https://api.themoviedb.org/3/movie/now_playing";
            }
            RequestParams params = new RequestParams();
            params.put("api_key", MowtubeActivity.TMDB_API_KEY);
            Log.d("MowtubeListFragment", url);
            client.get(url, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            ArrayList<MowtubeMovie> movies = null;
                            try {
                                JSONArray moviesJson = response.getJSONArray("results");
                                // Here we now have the json array of movies!
                                // Log.d("MowtubeListFragment", moviesJson.toString());
                                movies = MowtubeMovie.fromJson(moviesJson);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (movies == null) {
                                movies = MowtubeMovie.generateDebugArrayList();
                            }

                            if (mode == 4) {
                                ArrayList<MowtubeMovie> tmp = new ArrayList<>();
                                for (MowtubeMovie movie : movies) {
                                    if (movie.id == 188927
                                            || movie.id == 332567
                                            || movie.id == 374205) {
                                        tmp.add(movie);
                                    }
                                }
                                movies = tmp;
                            }

                            // called when response HTTP status is "200 OK"
                            recyclerView.setAdapter(
                                    new MowtubeRecyclerViewAdapter(
                                            getActivity(),
                                            movies,
                                            recyclerView
                                    )
                            );

                            // grey divider between each other
                            recyclerView.addItemDecoration(
                                    new MowtubeDividerItemDecoration(
                                            getContext(),
                                            MowtubeDividerItemDecoration.VERTICAL_LIST
                                    )
                            );

                            if (runnable != null) {
                                handler.removeCallbacks(runnable);
                                runnable = null;
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Toast.makeText(getContext(), "Failed to download data: " + res, Toast.LENGTH_LONG).show();
                            if (runnable != null) {
                                handler.removeCallbacks(runnable);
                                runnable = null;
                            }
                            progressDialog.dismiss();
                        }
                    }
            );
        }
    }

    public void fetchTimelineAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP

        // Since movie data is most likely the same,
        // simply do a fake refresh.
        // TODO: implement real refresh for fetchTimelineAsync()

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
        handler.postDelayed(runnable, 5000);
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
}