package com.example.chinyao.mowtube;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chinyao on 7/23/2016.
 */
public class MowtubeRecyclerViewAdapter
        extends RecyclerView.Adapter<MowtubeRecyclerViewAdapter.ViewHolder> {

    // private final TypedValue mTypedValue = new TypedValue();
    // private int mBackground;
    private List<MowtubeMovie> mValues;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mowtube_list_item, parent, false);
        // view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // public String mBoundString;

        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextViewTitle;
        public final TextView mTextView2;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.movieImage);
            mTextViewTitle = (TextView) view.findViewById(android.R.id.title);
            mTextView2 = (TextView) view.findViewById(android.R.id.text2);
        }
    }

    public MowtubeRecyclerViewAdapter(Context context, List<MowtubeMovie> items) {
        // context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        // mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MowtubeMovie theMowtubeMovie = mValues.get(position);
        if (MowtubeListFragment.ContentMode == 1) {
            // holder.mBoundString = mValues.get(position).title;
            holder.mTextViewTitle.setText(theMowtubeMovie.title);
            Glide.with(holder.mImageView.getContext())
                    .load(MowActivity.getRandomDrawable())
                    .fitCenter()
                    .into(holder.mImageView);
        }
        else if (MowtubeListFragment.ContentMode == 2) {
            holder.mTextViewTitle.setText(theMowtubeMovie.title);
            holder.mTextView2.setText(theMowtubeMovie.release_date);
            if (MowtubeListFragment.ImageLoadingMode == 1) {
                // Glide supports gif only in load()
                // speed up gif by diskCacheStrategy(DiskCacheStrategy.SOURCE)
                Glide.with(holder.mImageView.getContext())
                        .load("http://image.tmdb.org/t/p/w500" + theMowtubeMovie.backdrop_path)
                        .centerCrop()
                        .placeholder(R.drawable.blobb)
                        .error(R.drawable.tmdb)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.mImageView);

            }
            else if (MowtubeListFragment.ImageLoadingMode == 2) {
                if (theMowtubeMovie.backdrop_path.equals("null")) {
                    Glide.with(holder.mImageView.getContext())
                            .load(R.drawable.blobb)
                            .centerCrop()
                            .placeholder(R.drawable.blobb)
                            .error(R.drawable.tmdb)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.mImageView);
                }
                else {
                    // Picasso does not support gif
                    Picasso.with(holder.mImageView.getContext())
                            .load("http://image.tmdb.org/t/p/w500" + theMowtubeMovie.backdrop_path)
                            .fit().centerCrop()
                            .placeholder(R.drawable.blobb)
                            .error(R.drawable.tmdb)
                            .into(holder.mImageView);
                }
            }
            // https://github.com/rtheunissen/md-preloader
            // https://thomas.vanhoutte.be/miniblog/android-lollipop-animated-loading-gif/
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MowActivity.class);
                // intent.putExtra(MowActivity.EXTRA_NAME, holder.mBoundString);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}