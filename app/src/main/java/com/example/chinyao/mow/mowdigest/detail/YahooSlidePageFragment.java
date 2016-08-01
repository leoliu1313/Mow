package com.example.chinyao.mow.mowdigest.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.example.chinyao.mow.R;

/**
 * Created by Jiaqi Ning on 4/5/2015.
 */
public class YahooSlidePageFragment extends Fragment {

    private ImageView mCoverImageView;
    private int mCoverImageHeight;
    private float mVerticalParalllaxSpeed = 0.3f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.yahoo_page, container, false);
        ((NotifyingScrollView) rootView.findViewById(R.id.scroll_view))
                .setOnScrollChangedListener(mOnScrollChangedListener);
        mCoverImageView = (ImageView) rootView.findViewById(R.id.cover_img);

        int index = getArguments().getInt(BundleKey.PAGE_INDEX,0);
        switch (index){
            case 1:
                mCoverImageView.setImageResource(R.drawable.p2);
                break;
            case 2:
                mCoverImageView.setImageResource(R.drawable.p3);
                break;
        }

        ViewTreeObserver obs = mCoverImageView.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCoverImageHeight = mCoverImageView.getHeight();
                ViewTreeObserver obs = mCoverImageView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });

        return rootView;
    }
    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener
            = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            if (l < mCoverImageHeight){
                final float ratio =
                        (float) Math.min(Math.max(t, 0), mCoverImageHeight) / mCoverImageHeight;
                mCoverImageView.setTranslationY(ratio* mCoverImageHeight * mVerticalParalllaxSpeed);
            }
        }
    };
}
