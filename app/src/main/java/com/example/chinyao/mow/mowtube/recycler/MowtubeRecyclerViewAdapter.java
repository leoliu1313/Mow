package com.example.chinyao.mow.mowtube.recycler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mow.MowActivity;
import com.example.chinyao.mow.mowtube.MowtubeActivity;
import com.example.chinyao.mow.mowtube.MowtubeListFragment;
import com.example.chinyao.mow.mowtube.model.MowtubeMovie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by chinyao on 7/23/2016.
 */
public class MowtubeRecyclerViewAdapter
        extends RecyclerView.Adapter<MowtubeRecyclerViewAdapter.ViewHolder> {

    // private final TypedValue mTypedValue = new TypedValue();
    // private int mBackground;
    private List<MowtubeMovie> mMovies;
    private RecyclerView mRecyclerView;

    public MowtubeRecyclerViewAdapter(Context context, List<MowtubeMovie> items, RecyclerView rv) {
        // context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        // mBackground = mTypedValue.resourceId;
        mMovies = items;
        mRecyclerView = rv;
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    // Heterogenous-Layouts-inside-RecyclerView
    @Override
    public int getItemViewType(int position) {
        if (position < mMovies.size()) {
            if (mMovies.get(position).vote_average > 5) {
                return 0;
            } else {
                return 1;
            }
        }
        else {
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder output = null;
        View theView;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Heterogenous-Layouts-inside-RecyclerView
        if (viewType == 1) {
            theView = inflater.inflate(R.layout.mowtube_list_item_poster, parent, false);
            // TODO: use different ViewHolder
            output = new ViewHolder(theView);
        }
        else {
            theView = inflater.inflate(R.layout.mowtube_list_item, parent, false);
            // theView.setBackgroundResource(mBackground);
            output = new ViewHolder(theView);
        }
        return output;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MowtubeMovie theMowtubeMovie = mMovies.get(position);
        if (MowtubeListFragment.ContentMode == 1) {
            // debug mode
            // holder.mBoundString = mMovies.get(position).title;
            holder.mTextViewTitle.setText(theMowtubeMovie.title);
            Glide.with(holder.mImageView.getContext())
                    .load(theMowtubeMovie.backdrop_path)
                    .fitCenter()
                    .into(holder.mImageView);
        }
        else if (MowtubeListFragment.ContentMode == 2) {
            String input;

            input = theMowtubeMovie.title;
            // Heterogenous-Layouts-inside-RecyclerView
            if (holder.getItemViewType() == 1) {
                if (input.length() > MowtubeListFragment.MaxPosterTitleLength) {
                    input = input.substring(0, MowtubeListFragment.MaxPosterTitleLength) + "...";
                }
            }
            holder.mTextViewTitle.setText(input);

            input = theMowtubeMovie.release_date;
            // Heterogenous-Layouts-inside-RecyclerView
            if (holder.getItemViewType() == 1) {
                input += " ";
                for (int index = 0; index < theMowtubeMovie.genres.size(); index++) {
                    if (index != 0) {
                        input += ", ";
                    }
                    input += theMowtubeMovie.genres.get(index);
                }
                input += ". " + theMowtubeMovie.overview;
                if (input.length() > MowtubeListFragment.MaxPosterSubLength) {
                    input = input.substring(0, MowtubeListFragment.MaxPosterSubLength) + "...";
                }
            }
            holder.mTextView2.setText(input);

            // https://api.themoviedb.org/3/configuration?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed
            // w154
            // w185
            // w300
            // w500
            String imageLink = theMowtubeMovie.backdrop_path;
            int placeholderLink = R.drawable.blobb;
            int errorLink = R.drawable.tmdb;
            // Heterogenous-Layouts-inside-RecyclerView
            if (holder.getItemViewType() == 1) {
                imageLink = theMowtubeMovie.poster_path;
                placeholderLink = R.drawable.blobb_poster;
                errorLink = R.drawable.tmdb_poster;
            }
            if (MowtubeListFragment.ImageLoadingMode == 1) {
                Glide.with(holder.mImageView.getContext())
                        .load("http://image.tmdb.org/t/p/w500" + imageLink)
                        .fitCenter().centerCrop()
                        .placeholder(placeholderLink)
                        .error(errorLink)
                        .into(holder.mImageView);
            }
            else if (MowtubeListFragment.ImageLoadingMode == 2) {
                if (imageLink.equals("null")) {
                    // Glide supports gif only in load()
                    // speed up gif by diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    Glide.with(holder.mImageView.getContext())
                            .load(placeholderLink)
                            .fitCenter().centerCrop()
                            .placeholder(placeholderLink)
                            .error(errorLink)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.mImageView);
                }
                else {
                    // Picasso does not support gif
                    Picasso.with(holder.mImageView.getContext())
                            .load("http://image.tmdb.org/t/p/w500" + imageLink)
                            .transform(new RoundedCornersTransformation(10, 10))
                            .fit().centerCrop()
                            .placeholder(placeholderLink)
                            .error(errorLink)
                            .into(holder.mImageView);
                }
            }
            // https://github.com/rtheunissen/md-preloader
            // https://thomas.vanhoutte.be/miniblog/android-lollipop-animated-loading-gif/
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerView == null) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MowActivity.class);
                    // intent.putExtra(MowActivity.EXTRA_NAME, holder.mBoundString);
                    context.startActivity(intent);
                }
                else {
                    int position = mRecyclerView.getChildAdapterPosition(v);
                    final int id = mMovies.get(position).id;
                    final MowtubeActivity activity = (MowtubeActivity) v.getContext();

                    AsyncHttpClient client = new AsyncHttpClient();
                    // Turn off Debug Log
                    client.setLoggingEnabled(false);
                    String url = "https://api.themoviedb.org/3/movie/" + id + "/trailers";
                    RequestParams params = new RequestParams();
                    params.put("api_key", MowtubeActivity.TMDB_API_KEY);
                    Log.d("RecyclerViewAdapter", url);
                    client.get(url, params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        JSONArray moviesJson = response.getJSONArray("youtube");
                                        for (int i=0; i < moviesJson.length(); i++) {
                                            response = moviesJson.getJSONObject(i);
                                            if (response.getString("type").equals("Trailer")) {
                                                activity.loadBottom(response.getString("source"), id);
                                                return;
                                            }
                                        }
                                        if (moviesJson.length() > 0) {
                                            // https://api.themoviedb.org/3/movie/316727/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed
                                            // Some movies only have Featurette instead of Trailer
                                            response = moviesJson.getJSONObject(0);
                                            activity.loadBottom(response.getString("source"), id);
                                            return;
                                        }
                                        activity.loadBottom(MowtubeActivity.YOUTUBE_DEFAULT_LINK, id);
                                        Toast.makeText(activity,
                                                "No Trailer...",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                }
                            }
                    );
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final View mView;
        // public String mBoundString;

        @BindView(R.id.r_movie_image) ImageView mImageView;
        @BindView(R.id.r_title) TextView mTextViewTitle;
        @BindView(R.id.r_sub_title) TextView mTextView2;
        @BindView(R.id.r_menu_image_button) ImageButton mImageButton;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
            mImageButton.setOnClickListener(this);
        }

        // TODO: PopupMenu
        @Override
        public void onClick(View v) {
            if (v.getId() == mImageButton.getId()){
                /*
                Toast.makeText(
                        v.getContext(),
                        "ITEM PRESSED = " + String.valueOf(getAdapterPosition()),
                        Toast.LENGTH_SHORT
                ).show();
                */
                Toast.makeText(
                        v.getContext(),
                        "Add " + (String)mTextViewTitle.getText() + " to Favorite",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}