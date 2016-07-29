package com.example.chinyao.mow.mowdigest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chinyao.mow.R;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestFragment extends Fragment {
    private int mode = 1;

    public static MowdigestFragment newInstance(int mode) {
        MowdigestFragment theFragment = new MowdigestFragment();

        theFragment.mode = mode;

        return theFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        // Note that this can have more than RecyclerView
        SwipeRefreshLayout theRootContainer =
                (SwipeRefreshLayout) inflater.inflate(R.layout.mowtube_stream_fragment, container, false);
                */
        NestedScrollView theRootContainer =
                (NestedScrollView) inflater.inflate(R.layout.mowdigest_fragment, container, false);

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
}
