package com.example.chinyao.mow.mowdigest.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.databinding.MowtweebookDetailFragmentBinding;

/**
 * Created by Jiaqi Ning on 4/5/2015.
 */
public class YahooSlidePageFragment extends Fragment {

    private static float mVerticalParalllaxSpeed = 0.3f;

    // TODO: remvoe these
    public String first_image;
    public String first_section;
    public String first_title;
    public String first_abstract;

    private MowtweebookDetailFragmentBinding binding;
    private int mCoverImageHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ViewGroup rootView = (ViewGroup) inflater.inflate(
        //        R.layout.mowtweebook_detail_fragment, container, false);

        // Data Binding
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.mowtweebook_detail_fragment,
                container,
                false
        );

        binding.scrollView.setOnScrollChangedListener(mOnScrollChangedListener);

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
                    .into(binding.coverImg);
        }
        else {
            binding.coverImg.setImageResource(R.drawable.twitter_storms);
        }
        binding.section.setText(first_section);
        binding.title.setText(first_title);
        binding.contentText.setText(first_abstract);

        ViewTreeObserver obs = binding.coverImg.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCoverImageHeight = binding.coverImg.getHeight();
                ViewTreeObserver obs = binding.coverImg.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });

        return binding.scrollView;
    }
    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener
            = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            if (l < mCoverImageHeight){
                final float ratio =
                        (float) Math.min(Math.max(t, 0), mCoverImageHeight) / mCoverImageHeight;
                binding.coverImg.setTranslationY(ratio* mCoverImageHeight * mVerticalParalllaxSpeed);
            }
        }
    };
}
