package com.example.chinyao.mow.mowdigest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chinyao.mow.R;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestFragment extends Fragment {
    // ButterKnife
    // http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
    @BindView(R.id.m1_swipe)
    SwipeFlingAdapterView theSwipe;

    private int mode = 1;
    private ArrayList<Swipe> al;
    private MowdigestSwipeAdapter myAppAdapter;

    static String link1 = "https://s-media-cache-ak0.pinimg.com/236x/e7/7b/29/e77b294d3dc6245ab4b517142e1f63b0.jpg";
    static String link2 = "https://s-media-cache-ak0.pinimg.com/236x/e7/7b/29/e77b294d3dc6245ab4b517142e1f63b0.jpg";
    static String link3 = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTqEJBhd92spKnkYretdXnn5Twbnoii1NgdjXLBuddq8bF1bfEA";

    public static MowdigestFragment newInstance(int mode) {
        MowdigestFragment theFragment = new MowdigestFragment();

        theFragment.mode = mode;

        return theFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NestedScrollView theRootContainer =
                (NestedScrollView) inflater.inflate(R.layout.mowdigest_fragment, container, false);

        // ButterKnife
        ButterKnife.bind(this, theRootContainer);
        theSwipe = (SwipeFlingAdapterView) theRootContainer.findViewById(R.id.m1_swipe);

        setupSwipe();

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

    private void setupSwipe() {
        al = new ArrayList<>();
        al.add(new Swipe(link1, "link1"));
        al.add(new Swipe(link2, "link2"));
        al.add(new Swipe(link1, "link1"));
        al.add(new Swipe(link2, "link2"));
        al.add(new Swipe(link1, "link1"));
        al.add(new Swipe(link2, "link2"));

        myAppAdapter = new MowdigestSwipeAdapter(al, getContext());


        theSwipe.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // nothing
                Log.d("MowdigestFragment", "removeFirstObjectInAdapter");
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                al.remove(0);
                myAppAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                al.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                al.add(new Swipe(link3, "More"));
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = theSwipe.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        theSwipe.setAdapter(myAppAdapter);
    }
}
