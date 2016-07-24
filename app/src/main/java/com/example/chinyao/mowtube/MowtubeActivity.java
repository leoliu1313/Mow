package com.example.chinyao.mowtube;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class MowtubeActivity extends AppCompatActivity {

    // TODO: Use Shared Preferences
    static Boolean autoplay_on_wifi_only = false;

    public static final String TMDB_API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";

    // https://console.developers.google.com/
    public static final String YOUTUBE_API_KEY = "AIzaSyDclFRxzBdoqRGHVftdG1WFqBX2C2mVe04";

    private FragmentManager theFragmentManager;
    private YouTubePlayerSupportFragment mYouTubeContainer;
    private YouTubePlayer mYouTubePlayer;
    private DraggableView theDraggableView;
    private AppBarLayout theAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mowtube_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // up right menu button
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            // up left drawer button
            // ab.setDisplayHomeAsUpEnabled(true);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        theAppBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        theFragmentManager = getSupportFragmentManager();
        theDraggableView = (DraggableView)findViewById(R.id.draggable_view);
        theDraggableView.setVisibility(View.INVISIBLE);
        //theDraggableView.setVisibility(View.GONE);


        // theDraggableView.bringToFront();
        // ViewGroup viewGroup = ((ViewGroup) viewPager.getParent());
        // int index = viewGroup.indexOfChild(viewPager);
        // for(int i = 0; i<index; i++) {
        //     viewGroup.bringChildToFront(viewGroup.getChildAt(i));
        // }

        // AppBarLayout
        // theAppBarLayout.setVisibility(View.INVISIBLE);

        theDraggableView.setDraggableListener(new DraggableListener() {
            @Override public void onMaximized() {
                if (theAppBarLayout.getVisibility() != View.INVISIBLE) {
                    theAppBarLayout.setVisibility(View.INVISIBLE);
                    mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                }
            }

            @Override public void onMinimized() {
                if (theAppBarLayout.getVisibility() != View.VISIBLE) {
                    theAppBarLayout.setVisibility(View.VISIBLE);
                    mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                }
            }

            @Override public void onClosedToLeft() {
                mYouTubePlayer.pause();
            }

            @Override public void onClosedToRight() {
                mYouTubePlayer.pause();
            }
        });

        initializeYoutubeFragment("fis-9Zqu2Ro");
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

    public void initializeYoutubeFragment(final String input) {
        mYouTubeContainer = YouTubePlayerSupportFragment.newInstance();
        mYouTubeContainer.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                if (!wasRestored) {
                    mYouTubePlayer = youTubePlayer;
                    mYouTubePlayer.cueVideo(input);
                    mYouTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {
                        }

                        @Override
                        public void onLoaded(String s) {
                        }

                        @Override
                        public void onAdStarted() {
                        }

                        @Override
                        public void onVideoStarted() {
                        }

                        @Override
                        public void onVideoEnded() {
                        }

                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {
                            Log.d("MowtubeActivity", "onError " + errorReason.toString());
                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("MowtubeActivity", "onInitializationFailure " + youTubeInitializationResult.toString());
            }
        });

        theFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_youtube_player, mYouTubeContainer)
                .commit();
    }

    public void loadYoutube(String input) {
        theDraggableView.maximize();
        if (autoplay_on_wifi_only && !isWifiConnected()) {
            mYouTubePlayer.cueVideo(input);
        }
        else {
            mYouTubePlayer.loadVideo(input);
        }
        // mYouTubePlayer.play();
        theDraggableView.setVisibility(View.VISIBLE);
    }

    // TODO: implement this function
    private boolean isWifiConnected() {
        return false;
    }
}
