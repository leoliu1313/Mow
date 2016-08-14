package com.example.chinyao.mow.mowdigest.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;

/**
 * Created by Jiaqi Ning on 4/5/2015.
 */
public class YahooSlidePageFragment extends Fragment {

    private ImageView mCoverImageView;
    private int mCoverImageHeight;
    private float mVerticalParalllaxSpeed = 0.3f;
    public String first_image;
    public String first_section;
    public String first_title;
    public String first_abstract;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.mowdigest_detail, container, false);
        ((NotifyingScrollView) rootView.findViewById(R.id.scroll_view))
                .setOnScrollChangedListener(mOnScrollChangedListener);
        mCoverImageView = (ImageView) rootView.findViewById(R.id.cover_img);
        TextView section = (TextView) rootView.findViewById(R.id.section);
        TextView title = (TextView) rootView.findViewById(R.id.title);
        TextView content_text = (TextView) rootView.findViewById(R.id.content_text);

        // Arguments with Bundle
        int index = getArguments().getInt(BundleKey.PAGE_INDEX, 0);

        if (first_image != null) {
            Glide.with(this)
                    .load(first_image)
                    // TODO: override
                    /*
                    .override(
                            width,
                            height
                    )
                    */
                    .centerCrop()
                    .placeholder(R.drawable.square320)
                    .error(R.drawable.square320)
                    .into(mCoverImageView);
        }
        else {
            mCoverImageView.setImageResource(R.drawable.twitter_storms);
        }
        section.setText(first_section);
        title.setText(first_title);
        content_text.setText(first_abstract);

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
