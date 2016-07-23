package com.example.chinyao.mowtube;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private ImageView thumbnailImageView;
    private DraggablePanel draggablePanel;
    private YouTubePlayer youtubePlayer;
    private YouTubePlayerSupportFragment youtubeFragment;
    private static final String YOUTUBE_API_KEY = "AIzaSyC1rMU-mkhoyTvBIdTnYU0dss0tU9vtK48";
    private static final String VIDEO_KEY = "gsjtg7m1MMM";
    private static final String VIDEO_POSTER_THUMBNAIL =
            "http://4.bp.blogspot.com/-BT6IshdVsoA/UjfnTo_TkBI/AAAAAAAAMWk/JvDCYCoFRlQ/s1600/"
                    + "xmenDOFP.wobbly.1.jpg";
    private static final String VIDEO_POSTER_TITLE = "X-Men: Days of Future Past";
    private static final String SECOND_VIDEO_POSTER_THUMBNAIL =
            "http://media.comicbook.com/wp-content/uploads/2013/07/x-men-days-of-future-past"
                    + "-wolverine-poster.jpg";

    public static MowtubeListFragment newInstance(int mode) {
        MowtubeListFragment theFragment = new MowtubeListFragment();
        theFragment.mode = mode;
        return theFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mowtube_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.f1_recyclerview);
        setupRecyclerView(rv);

        thumbnailImageView = (ImageView) view.findViewById(R.id.f1_thumbnail);
        draggablePanel = (DraggablePanel) view.findViewById(R.id.f1_draggable_panel);

        initializeYoutubeFragment();
        initializeDraggablePanel();
        /*
        hookDraggablePanelListeners();
        */
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
                            MowtubeMovie.generateDebugArrayList()
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
            params.put("api_key", "a07e22bc18f5cb106bfe4cc1f83ad8ed");
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
                                            businesses
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

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }

    /**
     * Initialize the YouTubeSupportFrament attached as top fragment to the DraggablePanel widget and
     * reproduce the YouTube video represented with a YouTube url.
     */
    private void initializeYoutubeFragment() {
        youtubeFragment = new YouTubePlayerSupportFragment();
        // AIzaSyDclFRxzBdoqRGHVftdG1WFqBX2C2mVe04
        // YOUTUBE_API_KEY
        youtubeFragment.initialize("AIzaSyDclFRxzBdoqRGHVftdG1WFqBX2C2mVe04", new YouTubePlayer.OnInitializedListener() {

            @Override public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                          YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youtubePlayer = player;
                    // 0WWzgGyAH6Y
                    youtubePlayer.loadVideo(VIDEO_KEY);
                    youtubePlayer.setShowFullscreenButton(true);
                }
            }

            @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                          YouTubeInitializationResult error) {
            }
        });
    }

    /**
     * Initialize and configure the DraggablePanel widget with two fragments and some attributes.
     */
    private void initializeDraggablePanel() {
        // getSupportFragmentManager
        draggablePanel.setFragmentManager(getActivity().getSupportFragmentManager());
        MowtubeMoviePosterFragment moviePosterFragment = new MowtubeMoviePosterFragment();
        moviePosterFragment.setPoster(VIDEO_POSTER_THUMBNAIL);
        moviePosterFragment.setPosterTitle(VIDEO_POSTER_TITLE);
        MowtubeMoviePosterFragment moviePosterFragment2 = new MowtubeMoviePosterFragment();
        moviePosterFragment.setPoster(VIDEO_POSTER_THUMBNAIL);
        moviePosterFragment.setPosterTitle(VIDEO_POSTER_TITLE);
        draggablePanel.setTopFragment(moviePosterFragment2);
        draggablePanel.setBottomFragment(moviePosterFragment);
        draggablePanel.initializeView();
        /*
        Picasso.with(getContext())
                .load(SECOND_VIDEO_POSTER_THUMBNAIL)
                .placeholder(R.drawable.blobb)
                .into(thumbnailImageView);
                */
    }

    /**
     * Hook the DraggableListener to DraggablePanel to pause or resume the video when the
     * DragglabePanel is maximized or closed.
     */
    private void hookDraggablePanelListeners() {
        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override public void onMaximized() {
                playVideo();
            }

            @Override public void onMinimized() {
                //Empty
            }

            @Override public void onClosedToLeft() {
                pauseVideo();
            }

            @Override public void onClosedToRight() {
                pauseVideo();
            }
        });
    }

    /**
     * Pause the video reproduced in the YouTubePlayer.
     */
    private void pauseVideo() {
        if (youtubePlayer.isPlaying()) {
            youtubePlayer.pause();
        }
    }

    /**
     * Resume the video reproduced in the YouTubePlayer.
     */
    private void playVideo() {
        if (!youtubePlayer.isPlaying()) {
            youtubePlayer.play();
        }
    }
}