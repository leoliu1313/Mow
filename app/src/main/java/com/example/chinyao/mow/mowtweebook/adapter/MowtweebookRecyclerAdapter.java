package com.example.chinyao.mow.mowtweebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.detail.BundleKey;
import com.example.chinyao.mow.mowdigest.detail.YahooParallaxActivity;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        @BindView(R.id.card_profile_image)
        ImageView card_profile_image;
        @BindView(R.id.card_name)
        TextView card_name;
        @BindView(R.id.card_id)
        TextView card_id;
        @BindView(R.id.card_published_date)
        TextView card_published_date;
        @BindView(R.id.card_image)
        ImageView card_image;
        @BindView(R.id.card_body)
        TextView card_body;

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
                if (tweets.get(position - 1).isMowtweebookFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 2 >= 0) {
                if (tweets.get(position - 2).isMowtweebookFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 3 >= 0) {
                if (tweets.get(position - 3).isMowtweebookFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 4 >= 0) {
                if (tweets.get(position - 4).isMowtweebookFullSpan()) {
                    do_full_span = false;
                }
            }
            // TODO: don't set up tweet here
            MowtweebookTweet theTweet = tweets.get(position);
            if (!theTweet.isMowtweebookProcessed()) {
                theTweet.setMowtweebookProcessed(true);

                // retweet
                if (theTweet.getRetweeted_status() != null) {
                    theTweet.getRetweeted_status().setOriginal_user(theTweet.getUser());
                    tweets.set(position, theTweet.getRetweeted_status());
                    theTweet = tweets.get(position);
                }

                String tmp;

                // image
                if (theTweet.getEntities() != null
                        && theTweet.getEntities().getMedia() != null
                        && theTweet.getEntities().getMedia().size() > 0) {
                    tmp = theTweet.getEntities().getMedia().get(0).getMedia_url();
                    tmp = tmp.replaceAll("normal", "bigger");
                    theTweet.setMowtweebookImageUrl(tmp);
                }

                // tweet content
                tmp = theTweet.getText();
                if (tmp != null) {
                    tmp = tmp.replaceAll("https://t.co/.*", "");
                }
                if (theTweet.getEntities() != null
                        && theTweet.getEntities().getUrls() != null
                        && theTweet.getEntities().getUrls().size() > 0
                        && theTweet.getEntities().getUrls().get(0).getExpanded_url() != null) {
                    tmp = tmp + " " + theTweet.getEntities().getUrls().get(0).getExpanded_url();
                }
                theTweet.setText(tmp);

                // @id
                tmp = theTweet.getUser().getScreen_name();
                if (tmp != null) {
                    tmp = "@" + tmp;
                    theTweet.getUser().setScreen_name(tmp);
                }

                // timestamp
                tmp = theTweet.getCreated_at();
                if (tmp != null) {
                    tmp = getRelativeTimeAgo(tmp);
                    tmp = tmp.replaceAll(" seconds ago", "s");
                    tmp = tmp.replaceAll(" minutes ago", "m");
                    tmp = tmp.replaceAll(" hours ago", "h");
                    tmp = tmp.replaceAll(" dats ago", "d");
                    theTweet.setCreated_at(tmp);
                }
            }

            if (do_full_span) {
                if (theTweet.getMowtweebookImageUrl() != null) {
                    theTweet.setMowtweebookFullSpan(true);
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public MowtweebookRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View theView = null;
        // Heterogenous-Layouts-inside-RecyclerView
        /*
        if (viewType == 0) {
            theView = inflater.inflate(R.layout.mowdigest_card_view, parent, false);
        }
        else if (viewType == 1) {
            theView = inflater.inflate(R.layout.mowdigest_card_view_full_span, parent, false);
        }
        */
        theView = inflater.inflate(R.layout.mowtweebook_card_view, parent, false);
        return new ViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(MowtweebookRecyclerAdapter.ViewHolder holder, final int position) {
        MowtweebookTweet theTweet = tweets.get(position);
        String image = theTweet.getMowtweebookImageUrl();
        StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) holder.view.getLayoutParams();
        // Heterogenous-Layouts-inside-RecyclerView
        if (holder.getItemViewType() == 1) {
            layoutParams.setFullSpan(true);
        }
        else if (holder.getItemViewType() == 0) {
            layoutParams.setFullSpan(false);
        }
        if (image == null) {
            holder.card_image.setImageResource(android.R.color.transparent);
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
        Glide.with(context)
                .load(theTweet.getUser().getProfile_image_url())
                .placeholder(R.drawable.profile_image)
                .error(R.drawable.profile_image)
                .into(holder.card_profile_image);
        holder.card_image.setVisibility(View.VISIBLE);
        holder.card_name.setText(theTweet.getUser().getName());
        holder.card_id.setText(theTweet.getUser().getScreen_name());
        holder.card_published_date.setText(theTweet.getCreated_at());
        holder.card_body.setText(theTweet.getText());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked Position = " + position, Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putFloat(BundleKey.PARALLAX_SPEED, 0.6f);
                bundle.putString("first_image",
                        tweets.get(position).getMowtweebookImageUrl());
                bundle.putString("first_title",
                        tweets.get(position).getText());
                bundle.putString("first_section",
                        tweets.get(position).getUser().getScreen_name());
                bundle.putString("first_abstract",
                        tweets.get(position).getText());
                Intent intent = new Intent(context, YahooParallaxActivity.class);
                intent.putExtra(BundleKey.TYPE_YAHOO, bundle);
                context.startActivity(intent);
            }
        });
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

    // relative timestamp
    // https://gist.github.com/nesquena/f786232f5ef72f6e10a7
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate;
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            relativeDate = rawJsonDate;
        }

        return relativeDate;
    }
}
