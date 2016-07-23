package com.example.chinyao.mowtube;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        // public final ImageView mImageView;
        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            // mImageView = (ImageView) view.findViewById(R.id.avatar);
            mTextView = (TextView) view.findViewById(android.R.id.text1);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText();
        }
    }

    public MowtubeRecyclerViewAdapter(Context context, List<MowtubeMovie> items) {
        // context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        // mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (MowtubeListFragment.ContentMode == 1) {
            // holder.mBoundString = mValues.get(position).title;
            holder.mTextView.setText(mValues.get(position).title);
        }
        else if (MowtubeListFragment.ContentMode == 2) {
            holder.mTextView.setText(mValues.get(position).title);
        }

        /*
        Glide.with(holder.mImageView.getContext())
                .load(MowActivity.getRandomDrawable())
                .fitCenter()
                .into(holder.mImageView);
                */

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