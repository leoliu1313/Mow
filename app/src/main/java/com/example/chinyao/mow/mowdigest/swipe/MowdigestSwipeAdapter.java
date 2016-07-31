package com.example.chinyao.mow.mowdigest.swipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestSwipeAdapter extends BaseAdapter {

    private List<MowdigestSwipe> theSwipes;
    private Context context;

    private static int MaxTitleLength = 75;
    private static int MaxAbstractLength = 120;

    public MowdigestSwipeAdapter(List<MowdigestSwipe> swipes, Context context) {
        this.theSwipes = swipes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return theSwipes.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // getLayoutInflater();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.mowdigest_swipe, parent, false);
            // configure view holder
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MowdigestSwipe theSwipe = theSwipes.get(position);
        Glide.with(context)
                .load(theSwipe.getImage())
                .centerCrop()
                .placeholder(R.drawable.mediumthreebytwo440)
                .error(R.drawable.mediumthreebytwo440)
                .into(viewHolder.item_swipe_image);
        String input;
        input = theSwipe.getTitle();
        if (input.length() > MaxTitleLength) {
            input = input.substring(0, MaxTitleLength) + "...";
        }
        viewHolder.item_swipe_title.setText(input);
        input = theSwipe.getAbstractString();
        if (input.length() > MaxAbstractLength) {
            input = input.substring(0, MaxAbstractLength) + "...";
        }
        viewHolder.item_swipe_abstract.setText(input);
        viewHolder.item_swipe_section.setText(theSwipe.getSection());
        viewHolder.item_swipe_published_date.setText(theSwipe.getPublished_date());

        return convertView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ButterKnife
        // http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
        @BindView(R.id.item_swipe_background)
        FrameLayout item_swipe_background;
        @BindView(R.id.item_swipe_image)
        ImageView item_swipe_image;
        @BindView(R.id.item_swipe_title)
        TextView item_swipe_title;
        @BindView(R.id.item_swipe_abstract)
        TextView item_swipe_abstract;
        @BindView(R.id.item_swipe_section)
        TextView item_swipe_section;
        @BindView(R.id.item_swipe_published_date)
        TextView item_swipe_published_date;
        @BindView(R.id.item_swipe_right_indicator)
        View item_swipe_right_indicator;
        @BindView(R.id.item_swipe_left_indicator)
        View item_swipe_left_indicator;

        public ViewHolder(View view) {
            super(view);

            // ButterKnife
            ButterKnife.bind(this, view);
        }
    }
}