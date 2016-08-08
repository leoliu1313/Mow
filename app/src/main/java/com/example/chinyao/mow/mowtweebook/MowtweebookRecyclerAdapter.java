package com.example.chinyao.mow.mowtweebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.MowdigestFragment;
import com.example.chinyao.mow.mowdigest.detail.BundleKey;
import com.example.chinyao.mow.mowdigest.detail.YahooParallaxActivity;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowtweebookRecyclerAdapter
        extends RecyclerView.Adapter<MowtweebookRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<MowtweebookTweet> tweets;

    public MowtweebookRecyclerAdapter(Context context, List<MowtweebookTweet> tweets) {
        // context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        // mBackground = mTypedValue.resourceId;
        this.context = context;
        this.tweets = tweets;
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
    {
        @BindView(R.id.card_image)
        ImageView card_image;
        @BindView(R.id.card_title)
        TextView card_title;
        @BindView(R.id.card_section)
        TextView card_section;
        @BindView(R.id.card_published_date)
        TextView card_published_date;

        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    // Heterogenous-Layouts-inside-RecyclerView
    @Override
    public int getItemViewType(int position) {
        if (position < tweets.size()) {
            boolean do_full_span = true;
            if (position - 1 >= 0) {
                if (tweets.get(position - 1).isFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 2 >= 0) {
                if (tweets.get(position - 2).isFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 3 >= 0) {
                if (tweets.get(position - 3).isFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 4 >= 0) {
                if (tweets.get(position - 4).isFullSpan()) {
                    do_full_span = false;
                }
            }
            if (do_full_span) {
                /*
                if (tweets.get(position).getUser().getProfile_image_url() > 0) {
                    tweets.get(position).setMowdigestFullSpan(true);
                    return 1;
                }
                */
                tweets.get(position).setFullSpan(true);
                return 1;
            }
        }
        return 0;
    }

    @Override
    public MowtweebookRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View theView = null;
        // Heterogenous-Layouts-inside-RecyclerView
        if (viewType == 0) {
            theView = inflater.inflate(R.layout.mowdigest_card_view, parent, false);
        }
        else if (viewType == 1) {
            theView = inflater.inflate(R.layout.mowdigest_card_view_full_span, parent, false);
        }
        return new ViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(MowtweebookRecyclerAdapter.ViewHolder holder, final int position) {
        if (MowdigestFragment.NewsContentMode == 1) {
            //holder.card_title.setText(Integer.toString(position));
            if (position % 5 == 0) {
                holder.card_image.setImageResource(R.drawable.coffee_21);
            } else if (position % 5 == 1) {
                holder.card_image.setImageResource(R.drawable.coffee_22);
            } else if (position % 5 == 2) {
                holder.card_image.setImageResource(R.drawable.coffee_23);
            } else if (position % 5 == 3) {
                holder.card_image.setImageResource(R.drawable.blobb);
            } else if (position % 5 == 4) {
                holder.card_image.setImageResource(R.drawable.blobb_poster);
            }
        }
        else if (MowdigestFragment.NewsContentMode == 2) {
            MowtweebookTweet theTweet = tweets.get(position);
            String image = null;
            boolean found_sfSpan = false;
            boolean found = false;
            found = true;
            image = theTweet.getUser().getProfile_image_url();
            /*
            for (MowdigestImage theImage : theTweet.getUser().getProfile_image_url()) {
                // also change Glide placeholder() and error() in MowdigestSwipeAdapter.java
                // square: square320
                // not square: mediumThreeByTwo440 sfSpan
                if (theImage.getFormat() != null
                        && theImage.getFormat().equals("mediumThreeByTwo440")) {
                    image = theImage.getUrl();
                    break;
                    // stop searching
                    // this size is ideal
                }
                // in case the above is not there
                if (!found_sfSpan
                        && theImage.getFormat() != null
                        && theImage.getFormat().equals("sfSpan")) {
                    image = theImage.getUrl();
                    found_sfSpan = true;
                    // keep searching
                }
                if (!found) {
                    image = theImage.getUrl();
                    found = true;
                    // keep searching
                }
            }
            */
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) holder.view.getLayoutParams();
            // Heterogenous-Layouts-inside-RecyclerView
            if (holder.getItemViewType() == 1) {
                layoutParams.setFullSpan(true);
                /*
                int dimensionInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
                holder.card_image.getLayoutParams().height = dimensionInDp;
                holder.card_image.getLayoutParams().width = dimensionInDp;
                holder.card_image.requestLayout();
                */
            }
            else if (holder.getItemViewType() == 0) {
                layoutParams.setFullSpan(false);
            }
            if (image == null) {
                holder.card_image.setVisibility(View.GONE);
            }
            else {
                holder.card_image.setVisibility(View.INVISIBLE);
                Glide.with(context)
                        .load(image)
                        .centerCrop()
                        .placeholder(R.drawable.mediumthreebytwo440)
                        .error(R.drawable.mediumthreebytwo440)
                        .into(holder.card_image);
                holder.card_image.setVisibility(View.VISIBLE);
            }
            holder.card_title.setText(theTweet.getText());
            holder.card_section.setText(theTweet.getText());
            holder.card_published_date.setText(theTweet.getCreated_at());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Clicked Position = " + position, Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();
                    bundle.putFloat(BundleKey.PARALLAX_SPEED, 0.6f);
                    bundle.putString("first_image",
                            tweets.get(position).getUser().getProfile_image_url());
                    bundle.putString("first_title",
                            tweets.get(position).getText());
                    bundle.putString("first_section",
                            tweets.get(position).getText());
                    bundle.putString("first_abstract",
                            tweets.get(position).getText());
                    Intent intent = new Intent(context, YahooParallaxActivity.class);
                    intent.putExtra(BundleKey.TYPE_YAHOO, bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (tweets != null) {
            return tweets.size();
        }
        else {
            return 0;
        }
    }
}
