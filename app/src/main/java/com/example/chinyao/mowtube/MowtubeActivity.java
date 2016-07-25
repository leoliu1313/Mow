package com.example.chinyao.mowtube;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MowtubeActivity extends AppCompatActivity {

    // tmdb
    // https://www.themoviedb.org/documentation/api/sessions?language=en
    // http://docs.themoviedb.apiary.io/
    // https://www.themoviedb.org/documentation/api
    public static final String TMDB_API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";

    // youtube
    // https://console.developers.google.com/
    public static final String YOUTUBE_API_KEY = "AIzaSyDclFRxzBdoqRGHVftdG1WFqBX2C2mVe04";
    public static final String YOUTUBE_DEFAULT_LINK = "664VCs3c1HU";

    // TODO: Use Shared Preferences
    private static int autoplay_mode = 2;

    // non static
    private YouTubePlayer mYouTubePlayer = null;
    private boolean youtubeError = false;
    private boolean youtubeFullscreen = false;;
    private boolean youtubePlaying = false;
    private int youtubePlayingTime = -1;
    private int orientationStatePrevious = 0;
    private int orientationState = 0;
    private String orientationStateVideo = YOUTUBE_DEFAULT_LINK;
    private int orientationStateId = 0;
    private boolean doubleBackToExitPressedOnce = false;

    // ButterKnife
    // http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
    @BindView(R.id.m_app_bar_layout) AppBarLayout theAppBarLayout;
    @BindView(R.id.m_toolbar) Toolbar toolbar;
    @BindView(R.id.m_tab_layout) TabLayout tabLayout;
    @BindView(R.id.m_view_pager) ViewPager viewPager;
    @BindView(R.id.m_draggable_view) DraggableView theDraggableView;
    @BindView(R.id.dv_bottom) ScrollView theScrollView;
    @BindView(R.id.dv_b_title) TextView theTitle;
    @BindView(R.id.dv_b_trending) TextView theTrending;
    @BindView(R.id.dv_b_release) TextView theRelease;
    @BindView(R.id.dv_b_overview) TextView theOverview;
    @BindView(R.id.dv_b_category) TextView theCategory;
    @BindView(R.id.dv_b_production) TextView theProduction;
    @BindView(R.id.dv_b_rating_bar) RatingBar theRatingBar;
    @BindView(R.id.dv_b_sub_slider_layout) SliderLayout theSliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        setContentView(R.layout.mowtube_activity);

        // ButterKnife
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // up right menu button
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            // up left drawer button
            // ab.setDisplayHomeAsUpEnabled(true);
        }

        setupViewPager();

        setupDraggableView();

        setupYoutubeFragment();

        setupSliderLayout();

        setupRatingBar();
    }

    private void setupViewPager() {
        viewPager.setOffscreenPageLimit(3);

        // TODO
        // people on stackoverflow said this is bad implementation
        // use getView() instead?
        MowtubeViewPagerAdapter mowtubeViewPagerAdapter = new MowtubeViewPagerAdapter(getSupportFragmentManager());
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(1), getString(R.string.home));
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(2), getString(R.string.upcoming));
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(3), getString(R.string.trending));
        mowtubeViewPagerAdapter.addFragment(MowtubeListFragment.newInstance(4), getString(R.string.favorite));
        viewPager.setAdapter(mowtubeViewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        // use         |     tab1    |     tab2    |
        // instead of  |  tab1  |  tab2  |         |
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (autoplay_mode == 1) {
            menu.findItem(R.id.autoplay_on_wifi_only).setChecked(true);
        }
        else if (autoplay_mode == 2) {
            menu.findItem(R.id.autoplay_on_popular).setChecked(true);
        }
        else if (autoplay_mode == 3) {
            menu.findItem(R.id.autoplay_always).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.autoplay_on_wifi_only:
                autoplay_mode = 1;
                return true;
            case R.id.autoplay_on_popular:
                autoplay_mode = 2;
                break;
            case R.id.autoplay_always:
                autoplay_mode = 3;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDraggableView() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            theDraggableView.setXTopViewScaleFactor((float)1.5);
            theDraggableView.setYTopViewScaleFactor((float)1.5);
        }

        if (orientationStatePrevious == 0) {
            theDraggableView.setVisibility(View.INVISIBLE);
        }
        else if (orientationStatePrevious == 1) {
            theDraggableView.minimize();
        }
        else if (orientationStatePrevious == 2) {
            theDraggableView.maximize();
        }


        // theDraggableView.bringToFront();
        // ViewGroup viewGroup = ((ViewGroup) viewPager.getParent());
        // int index = viewGroup.indexOfChild(viewPager);
        // for(int i = 0; i<index; i++) {
        //     viewGroup.bringChildToFront(viewGroup.getChildAt(i));
        // }

        // theAppBarLayout.setVisibility(View.INVISIBLE);

        theDraggableView.setDraggableListener(new DraggableListener() {
            @Override public void onMaximized() {
                if (theAppBarLayout.getVisibility() != View.INVISIBLE) {
                    theAppBarLayout.setVisibility(View.INVISIBLE);
                    if (isYoutubeReady()) {
                        mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    }
                    if (orientationState == 1) {
                        orientationState = 2;
                    }
                    theScrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            }

            @Override public void onMinimized() {
                if (theAppBarLayout.getVisibility() != View.VISIBLE) {
                    theAppBarLayout.setVisibility(View.VISIBLE);
                    if (isYoutubeReady()) {
                        mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                    }
                    if (orientationState == 2) {
                        orientationState = 1;
                    }
                }
            }

            @Override public void onClosedToLeft() {
                if (isYoutubeReady()) {
                    mYouTubePlayer.pause();
                    orientationState = 0;
                }
            }

            @Override public void onClosedToRight() {
                if (isYoutubeReady()) {
                    theDraggableView.maximize();
                }
            }
        });
    }

    // DraggableView and YouTubePlayerSupportFragment have problems to call setupYoutubeFragment() again
    private void setupYoutubeFragment() {
        YouTubePlayerSupportFragment mYouTubeContainer = YouTubePlayerSupportFragment.newInstance();
        mYouTubeContainer.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                if (!wasRestored) {
                    mYouTubePlayer = youTubePlayer;

                    if (orientationStatePrevious == 0) {
                        mYouTubePlayer.cueVideo(MowtubeActivity.YOUTUBE_DEFAULT_LINK);
                    }
                    else {
                        loadDetails(orientationStateVideo, orientationStateId);
                    }

                    mYouTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {}
                        @Override
                        public void onLoaded(String s) {
                            youtubeError = false;
                        }
                        @Override
                        public void onAdStarted() {}
                        @Override
                        public void onVideoStarted() {}
                        @Override
                        public void onVideoEnded() {}
                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {
                            youtubeError = true;
                            Log.d("MowtubeActivity", "onError " + errorReason.toString());
                        }
                    });

                    mYouTubePlayer.setOnFullscreenListener(new OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean _isFullScreen) {
                            if (youtubeFullscreen && !_isFullScreen) {
                                // use sensor instead of user's Android setting
                                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                                // user's Android setting
                                // if auto-rotate, then use sensor
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

                                // lock to specific orientation
                                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            }
                            youtubeFullscreen = _isFullScreen;
                        }
                    });
                }
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                youtubeError = true;
                Log.d("MowtubeActivity", "onInitializationFailure " + youTubeInitializationResult.toString());
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dv_t_youtube, mYouTubeContainer)
                .commit();
    }

    private void setupSliderLayout() {
        // theSliderLayout.setPresetTransformer("DepthPage");
        theSliderLayout.setCustomAnimation(new DescriptionAnimation());
        theSliderLayout.setDuration(3000);
    }

    private void setupRatingBar() {
        setRatingBarColor(R.color.colorAccentLightLight);
        theRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    setRatingBarColor(R.color.colorAccent);
                    Toast.makeText(MowtubeActivity.this,
                            "Vote : >",
                            // "Successfully vote " + (int)(rating * 2),
                            Toast.LENGTH_LONG)
                            .show();
                }
                else {
                    setRatingBarColor(R.color.colorAccentLightLight);
                }
            }
        });
    }

    public void loadBottom(String video, int id) {
        orientationStateVideo = video;
        orientationStateId = id;
        orientationState = 1;

        theDraggableView.minimize();
        theDraggableView.setVisibility(View.VISIBLE);

        loadDetails(video, id);
    }

    public void loadDetails(final String video, final int id) {

        AsyncHttpClient client = new AsyncHttpClient();
        // Turn off Debug Log
        client.setLoggingEnabled(false);
        String url = "https://api.themoviedb.org/3/movie/" + id;
        RequestParams params = new RequestParams();
        params.put("api_key", MowtubeActivity.TMDB_API_KEY);
        Log.d("RecyclerViewAdapter", url);
        client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            // homepage: "http://legendoftarzan.com",
                            JSONArray theJSONArray;
                            JSONObject theJSONObject;
                            StringBuilder input = new StringBuilder();
                            String tmp;

                            theTitle.setText(response.getString("title"));
                            theTrending.setText("Trending Index: " + (int)response.getDouble("popularity"));
                            theRelease.setText("Release Date: " + response.getString("release_date"));

                            tmp = response.getString("overview");
                            if (tmp.equals("null")) {
                                theOverview.setText("");
                            }
                            else {
                                theOverview.setText(tmp);
                            }

                            theJSONArray = response.getJSONArray("genres");
                            input.append("Category: ");
                            for (int i = 0; i < theJSONArray.length(); i++) {
                                if (i != 0) {
                                    input.append(", ");
                                }
                                theJSONObject = theJSONArray.getJSONObject(i);
                                input.append(theJSONObject.getString("name"));
                            }
                            theCategory.setText(input.toString());

                            input.setLength(0);
                            theJSONArray = response.getJSONArray("production_companies");
                            input.append("Production: ");
                            for (int i = 0; i < theJSONArray.length(); i++) {
                                if (i != 0) {
                                    input.append(", ");
                                }
                                theJSONObject = theJSONArray.getJSONObject(i);
                                input.append(theJSONObject.getString("name"));
                            }
                            theProduction.setText(input.toString());
                            input.setLength(0);

                            theRatingBar.setRating((float) response.getDouble("vote_average") / 2);

                            // youtube
                            if (response.getDouble("vote_average") > 5) {
                                loadYoutube(video, true);
                            }
                            else {
                                loadYoutube(video, false);
                            }

                            loadBackdrops(id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
    }

    public void loadBackdrops(int id) {
        AsyncHttpClient client = new AsyncHttpClient();
        // Turn off Debug Log
        client.setLoggingEnabled(false);
        String url = "https://api.themoviedb.org/3/movie/" + id + "/images";
        RequestParams params = new RequestParams();
        params.put("api_key", MowtubeActivity.TMDB_API_KEY);
        Log.d("RecyclerViewAdapter", url);
        client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            theSliderLayout.removeAllSliders();
                            JSONArray theJSONArray = response.getJSONArray("backdrops");
                            for (int i = 0; i < theJSONArray.length(); i++) {
                                JSONObject theJSONObject = theJSONArray.getJSONObject(i);
                                TextSliderView textSliderView = new TextSliderView(MowtubeActivity.this);
                                // initialize a SliderLayout
                                textSliderView
                                        .description((String) theTitle.getText())
                                        .image("http://image.tmdb.org/t/p/w500"
                                                + theJSONObject.getString("file_path")
                                                + "?api_key="
                                                + TMDB_API_KEY);
                                theSliderLayout.addSlider(textSliderView);
                            }
                            // TODO
                            // animate to slowly show up theSliderLayout to avoid jumping images in the beginning
                            theSliderLayout.setDuration(3000);
                            loadFinal();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
    }

    public void loadYoutube(String video, Boolean popular) {
        if (isYoutubeReady()) {
            if (youtubePlaying) {
                youtubePlaying = false;
                if (youtubePlayingTime != -1) {
                    mYouTubePlayer.loadVideo(video, youtubePlayingTime);
                }
                else {
                    mYouTubePlayer.loadVideo(video);
                }
            }
            else if (autoplay_mode == 1 && !isWifiConnected()) {
                mYouTubePlayer.cueVideo(video);
            }
            else if (autoplay_mode == 2 && !popular) {
                mYouTubePlayer.cueVideo(video);
            }
            else {
                mYouTubePlayer.loadVideo(video);
            }
            // mYouTubePlayer.play();
        } else {
            // restart app
            Toast.makeText(this,
                    "Youtube cannot be initialized.",
                    Toast.LENGTH_SHORT)
                    .show();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    public void loadFinal() {
        if (orientationStatePrevious == 1) {
            // reset orientationStatePrevious
            orientationStatePrevious = 0;
            theDraggableView.minimize();
        }
        else {
            theDraggableView.maximize();
        }
        theDraggableView.setVisibility(View.VISIBLE);
    }

    // TODO: implement this function
    private boolean isWifiConnected() {
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        savedInstanceState.putInt("orientationState", orientationState);
        savedInstanceState.putString("orientationStateVideo", orientationStateVideo);
        savedInstanceState.putInt("orientationStateId", orientationStateId);
        if (isYoutubeReady()) {
            savedInstanceState.putBoolean("youtubePlaying", mYouTubePlayer.isPlaying());
            savedInstanceState.putInt("youtubePlayingTime", mYouTubePlayer.getCurrentTimeMillis());
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        orientationState = savedInstanceState.getInt("orientationState");
        orientationStatePrevious = orientationState;
        orientationStateVideo = savedInstanceState.getString("orientationStateVideo");
        orientationStateId = savedInstanceState.getInt("orientationStateId");
        youtubePlaying = savedInstanceState.getBoolean("youtubePlaying");
        youtubePlayingTime = savedInstanceState.getInt("youtubePlayingTime");
        /*
        Toast.makeText(this,
                "onRestoreInstanceState "
                        + orientationStatePrevious + " "
                        + orientationStateVideo + " "
                        + orientationStateId + " "
                        + youtubePlaying + " "
                        + youtubePlayingTime + " "
                        ,
                Toast.LENGTH_SHORT)
                .show();
                */
    }

    @Override
    protected void onStop() {
        // avoid memory leak
        theSliderLayout.stopAutoCycle();
        super.onStop();
    }

    private void setRatingBarColor(int id) {
        LayerDrawable stars = (LayerDrawable) theRatingBar.getProgressDrawable();
        // Filled stars
        setDrawableColor(stars.getDrawable(2), ContextCompat.getColor(MowtubeActivity.this, id));
        // Half filled stars
        setDrawableColor(stars.getDrawable(1), ContextCompat.getColor(MowtubeActivity.this, id));
        // Empty stars
        setDrawableColor(stars.getDrawable(0), ContextCompat.getColor(MowtubeActivity.this, id));
    }

    private void setDrawableColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        }
        else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onBackPressed() {
        if (youtubeFullscreen) {
            mYouTubePlayer.setFullscreen(false);
        }
        else if (orientationState == 2){
            theDraggableView.minimize();
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            else {
                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
                // TODO: bad implementation due to memory leak
                // should removeCallbacks() when onDestroy()
                // or, use another implementation instead
                // http://stackoverflow.com/questions/8430805/clicking-the-back-button-twice-to-exit-an-activity
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce = false;
                            }
                        }, 2000);
            }
        }
    }

    public boolean isYoutubeReady() {
        return !youtubeError && (mYouTubePlayer != null);
    }
}
