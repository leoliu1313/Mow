package com.example.chinyao.mow.mowtweebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.detail.YahooParallaxActivity;
import com.example.chinyao.mow.mowtweebook.activity.MowtweebookActivity;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookParcelWrap;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookUser;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowtweebookRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private MowtweebookRestClient client;
    private List<MowtweebookTweet> tweets;
    public MowtweebookActivity theActivity;

    public MowtweebookRecyclerAdapter(
            Context context,
            MowtweebookRestClient client,
            List<MowtweebookTweet> tweets
            ) {
        // context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        // mBackground = mTypedValue.resourceId;
        this.context = context;
        this.client = client;
        this.tweets = tweets;
    }

    // Heterogenous-Layouts-inside-RecyclerView
    @Override
    public int getItemViewType(int position) {
        if (position < tweets.size()) {
            MowtweebookTweet theTweet = tweets.get(position);
            if (theTweet.isMowtweebookProfile()) {
                return 2;
            }

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View theView = null;
        // Heterogenous-Layouts-inside-RecyclerView
        if (viewType == 0) {
            theView = inflater.inflate(R.layout.mowtweebook_card_view, parent, false);
        }
        else if (viewType == 1) {
            // theView = inflater.inflate(R.layout.mowdigest_card_view_full_span, parent, false);
            theView = inflater.inflate(R.layout.mowtweebook_card_view, parent, false);
        }
        else if (viewType == 2) {
            theView = inflater.inflate(R.layout.mowtweebook_profile_view, parent, false);
            return new ViewHolder2(theView);
        }
        // Log.d("onCreateViewHolder", "viewType = " + viewType);
        return new ViewHolder1(theView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StaggeredGridLayoutManager.LayoutParams layoutParams;
        // Heterogenous-Layouts-inside-RecyclerView
        switch (holder.getItemViewType()) {
            case 2:
                ViewHolder2 holder2 = (ViewHolder2) holder;
                layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder2.view.getLayoutParams();
                layoutParams.setFullSpan(true);
                setupHolder2(holder2);
                break;
            case 0:
            case 1:
            default:
                final ViewHolder1 holder1 = (ViewHolder1) holder;
                layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder1.view.getLayoutParams();
                layoutParams.setFullSpan(false);
                setupHolder1(holder1, position);
                break;
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

    public void setupHolder2(ViewHolder2 holder2) {
        if (tweets.size() == 0) {
            // this is not expected
            holder2.wrapper.setVisibility(View.GONE);
            return;
        }

        holder2.wrapper.setVisibility(View.VISIBLE); // in case View.GONE
        MowtweebookUser theUser = tweets.get(1).getUser();

        Glide.with(context)
                .load(theUser.getProfile_banner_url())
                .centerCrop()
                .placeholder(R.drawable.mediumthreebytwo440)
                .error(R.drawable.mediumthreebytwo440)
                .into(holder2.banner);

        Glide.with(context)
                .load(theUser.getProfile_image_url())
                .bitmapTransform(new RoundedCornersTransformation(
                        context, 5, 0,
                        RoundedCornersTransformation.CornerType.ALL))
                .placeholder(R.drawable.profile_image)
                .error(R.drawable.profile_image)
                .into(holder2.profile);

        holder2.name.setText(theUser.getName());
        holder2.screen.setText(theUser.getScreen_name());
        holder2.description.setText(theUser.getDescription());
        holder2.following_count.setText(convertCount(theUser.getFriends_count()));
        holder2.follower_count.setText(convertCount(theUser.getFollowers_count()));
        holder2.tweet_count.setText(convertCount(theUser.getStatuses_count()));
    }

    private String convertCount(String input) {
        if (input.length() > 6) {
            return input.substring(0, input.length() - 6) + "M";
        }
        if (input.length() > 3) {
            return input.substring(0, input.length() - 3) + "K";
        }
        return input;
    }

    public void setupHolder1(final ViewHolder1 holder1, final int position) {
        MowtweebookTweet theTweet = tweets.get(position);
        String image = theTweet.getMowtweebookImageUrl();
        if (image == null) {
            // holder.card_image.setImageResource(android.R.color.transparent);
            holder1.card_image.setImageResource(0);
            holder1.card_image.setVisibility(View.GONE);
        }
        else {
            holder1.card_image.setVisibility(View.INVISIBLE);
            Glide.with(context)
                    .load(image)
                    // TODO: override doesn't work correctly
                    .override(
                            theTweet.getEntities().getMedia().get(0).getSizes().getLarge().getW(),
                            theTweet.getEntities().getMedia().get(0).getSizes().getLarge().getH()
                    )
                    .centerCrop()
                    .placeholder(R.drawable.square320)
                    .error(R.drawable.square320)
                    .into(holder1.card_image);
            holder1.card_image.setVisibility(View.VISIBLE);
        }
        Glide.with(context)
                .load(theTweet.getUser().getProfile_image_url())
                .bitmapTransform(new RoundedCornersTransformation(
                        context, 5, 0,
                        RoundedCornersTransformation.CornerType.ALL))
                .placeholder(R.drawable.profile_image)
                .error(R.drawable.profile_image)
                .into(holder1.card_profile_image);
        holder1.card_image.setVisibility(View.VISIBLE);
        holder1.card_name.setText(theTweet.getUser().getName());
        holder1.card_id.setText(theTweet.getUser().getScreen_name());
        holder1.card_published_date.setText(theTweet.getCreated_at());
        holder1.card_body.setText(theTweet.getText());
        holder1.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "Clicked Position = " + position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, YahooParallaxActivity.class);

                // Parcels
                // TODO: memory issue
                List<MowtweebookTweet> theTweets = new ArrayList<>();
                theTweets.add(tweets.get(position));
                int index;
                int count = 0;
                for (index = position - 1; index >= 0; index--) {
                    if (tweets.get(index).getMowtweebookImageUrl() != null) {
                        theTweets.add(tweets.get(index));
                        count++;
                        if (theTweets.size() > 10) {
                            break;
                        }
                    }
                }
                Collections.reverse(theTweets);
                for (index = position + 1; index < tweets.size(); index++) {
                    if (tweets.get(index).getMowtweebookImageUrl() != null) {
                        theTweets.add(tweets.get(index));
                        if (theTweets.size() > 20) {
                            break;
                        }
                    }
                }
                MowtweebookParcelWrap theWrap =
                        new MowtweebookParcelWrap(theTweets);
                intent.putExtra("tweets", Parcels.wrap(theWrap));
                intent.putExtra("default", count);

                context.startActivity(intent);
            }
        });

        holder1.ic_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ic_reply", "onClick");
                MaterialDialog theDialog =
                        new MaterialDialog.Builder(context)
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .positiveText(context.getResources().getString(R.string.save_button))
                                .inputRangeRes(1, 100, R.color.mowColorAccentLight)
                                .input(
                                        "", // hint
                                        tweets.get(position).getUser().getScreen_name() + " ", // prefill
                                        new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog,
                                                                CharSequence input) {
                                                client.postUpdate(
                                                        input.toString(),
                                                        tweets.get(position).getId_str(),
                                                        new JsonHttpResponseHandler() {
                                                            @Override
                                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                                Log.d("postUpdate", response.toString());
                                                                tweets.add(0, MowtweebookTweet.parseJSON(3, response.toString()));
                                                                notifyDataSetChanged();
                                                                // TODO: also show post on the other tab
                                                                // TODO: scroll to the end?
                                                            }

                                                            @Override
                                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                                Log.d("postUpdate", errorResponse.toString());
                                                            }
                                                        });
                                            }
                                        }).build();
                theDialog.getInputEditText().setSingleLine(false);
                theDialog.show();
            }
        });

        if (tweets.get(position).getRetweeted().equals("true")) {
            holder1.ic_retweet.setImageResource(R.drawable.ic_retweeted);
            // TODO: untweet
        }
        else {
            holder1.ic_retweet.setImageResource(R.drawable.ic_retweet);
            holder1.ic_retweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ic_retweet", "onClick");
                    client.postRetweet(tweets.get(position).getId_str(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("ic_retweet", response.toString());
                            holder1.ic_retweet.setImageResource(R.drawable.ic_retweeted);
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
            holder1.ic_like.setImageResource(R.drawable.ic_liked);
            // TODO: unlike
        }
        else {
            holder1.ic_like.setImageResource(R.drawable.ic_like);
            holder1.ic_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ic_like", "onClick");
                    client.postLike(tweets.get(position).getId_str(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("ic_like", response.toString());
                            holder1.ic_like.setImageResource(R.drawable.ic_liked);
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

        holder1.card_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("card_profile_image", "onClick");
                theActivity.viewPager.setCurrentItem(1);
                theActivity.theUserTimelineFragment.clearAndrefreshAsync(tweets.get(position).getUser().getId_str());
            }
        });
    }

    public class ViewHolder1
            extends RecyclerView.ViewHolder {
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
        @BindView(R.id.ic_reply)
        ImageView ic_reply;
        @BindView(R.id.ic_retweet)
        ImageView ic_retweet;
        @BindView(R.id.ic_like)
        ImageView ic_like;

        public final View view;

        public ViewHolder1(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolder2
            extends RecyclerView.ViewHolder {
        @BindView(R.id.wrapper)
        RelativeLayout wrapper;
        @BindView(R.id.banner)
        ImageView banner;
        @BindView(R.id.profile)
        ImageView profile;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.screen)
        TextView screen;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.following_count)
        TextView following_count;
        @BindView(R.id.follower_count)
        TextView follower_count;
        @BindView(R.id.tweet_count)
        TextView tweet_count;

        public final View view;

        public ViewHolder2(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
