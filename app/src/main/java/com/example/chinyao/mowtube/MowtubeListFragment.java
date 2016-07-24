package com.example.chinyao.mowtube;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    public static final int ContentMode = 2;
    // 1: debug
    // 2: android-async-http

    public static final int ImageLoadingMode = 2;
    // 1: Glide
    // 2: Picasso

    private Handler handler = null;
    private Runnable runnable = null;
    private int mode;

    public static MowtubeListFragment newInstance(int mode) {
        MowtubeListFragment theFragment = new MowtubeListFragment();
        theFragment.mode = mode;
        return theFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Note that this can have more than RecyclerView
        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.mowtube_recyclerview, container, false);
        setupRecyclerView(rv);

        // orientation issue
        // http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments
        // http://stackoverflow.com/questions/32478968/android-viewpager-orientation-change
        // https://medium.com/@roideuniverse/android-viewpager-fragmentpageradapter-and-orientation-changes-256c23bee035#.ufb2ywv33
        // http://stackoverflow.com/questions/28982512/handling-orientation-change-with-viewpager-fragmentpageradapter
        // https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
        setRetainInstance(true);

        return rv;
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        // Show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(true);
        progressDialog.setMessage(getString(R.string.loading_msg));

        if (handler == null) {
            handler = new Handler();
        }
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
        //Do something after 1 second
        handler.postDelayed(runnable, 1000);

        if (ContentMode == 1) {
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
                url = "https://api.themoviedb.org/3/movie/top_rated";
            }
            RequestParams params = new RequestParams();
            params.put("api_key", MowtubeActivity.TMDB_API_KEY);
            Log.d("MowtubeListFragment", url);
            client.get(url, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            ArrayList<MowtubeMovie> businesses = null;
                            try {
                                JSONArray moviesJson = response.getJSONArray("results");
                                // Here we now have the json array of movies!
                                // Log.d("MowtubeListFragment", moviesJson.toString());
                                businesses = MowtubeMovie.fromJson(moviesJson);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (businesses == null) {
                                businesses = MowtubeMovie.generateDebugArrayList();
                            }

                            // called when response HTTP status is "200 OK"
                            recyclerView.setAdapter(
                                    new MowtubeRecyclerViewAdapter(
                                            getActivity(),
                                            businesses,
                                            recyclerView
                                    )
                            );

                            recyclerView.addItemDecoration(
                                    new DividerItemDecoration(
                                            getContext(),
                                            DividerItemDecoration.VERTICAL_LIST
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
}