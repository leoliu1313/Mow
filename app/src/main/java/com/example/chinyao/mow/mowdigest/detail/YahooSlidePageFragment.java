package com.example.chinyao.mow.mowdigest.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.databinding.MowtweebookDetailFragmentBinding;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookRestClient;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookUtility;
import com.example.chinyao.mow.mowtweebook.utility.PatternEditableBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

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

    public List<MowtweebookTweet> tweets;
    public ViewPager mPager;
    public MowtweebookRestClient client;

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
        // TODO
        // mPager.getCurrentItem()

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
        createClilckableStyledSpans(binding.title);
        binding.contentText.setText(first_abstract);

        setOnClickListener();

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

    // http://guides.codepath.com/android/Working-with-the-TextView#creating-clickable-styled-spans
    // TODO: move to MowtweebookUtility
    public void createClilckableStyledSpans(TextView textView) {
        // Style clickable spans based on pattern
        new PatternEditableBuilder()
                .addPattern(Pattern.compile(
                        "\\@(\\w+)"),
                        R.color.mowtweebookColorAccent,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                // TODO
                            }
                        })
                .addPattern(Pattern.compile(
                        "\\#(\\w+)"),
                        R.color.mowtweebookColorAccent,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                // TODO
                            }
                        })
                .into(textView);
    }

    private void setOnClickListener() {
        final int position = mPager.getCurrentItem();

        binding.icReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ic_reply", "onClick");
                MowtweebookUtility.createPostUpdateDialog();
            }
        });

        if (tweets.get(position).getRetweeted().equals("true")) {
            binding.icRetweet.setImageResource(R.drawable.ic_retweeted);
            // TODO: untweet
        }
        else {
            binding.icRetweet.setImageResource(R.drawable.ic_retweet);
            binding.icRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ic_retweet", "onClick");
                    client.postRetweet(tweets.get(position).getId_str(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("ic_retweet", response.toString());
                            binding.icRetweet.setImageResource(R.drawable.ic_retweeted);
                            tweets.get(position).setRetweeted("true");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("ic_retweet", errorResponse.toString());
                        }
                    });
                }
            });
        }

        if (tweets.get(position).getFavorited().equals("true")) {
            binding.icLike.setImageResource(R.drawable.ic_liked);
            // TODO: unlike
        }
        else {
            binding.icLike.setImageResource(R.drawable.ic_like);
            binding.icLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ic_like", "onClick");
                    client.postLike(tweets.get(position).getId_str(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("ic_like", response.toString());
                            binding.icLike.setImageResource(R.drawable.ic_liked);
                            tweets.get(position).setFavorited("true");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("ic_like", errorResponse.toString());
                        }
                    });
                }
            });
        }
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
