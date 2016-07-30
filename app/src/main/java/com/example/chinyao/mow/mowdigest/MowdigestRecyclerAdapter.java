package com.example.chinyao.mow.mowdigest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.model.MowdigestNews;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestRecyclerAdapter
        extends RecyclerView.Adapter<MowdigestRecyclerAdapter.ViewHolder> {

    List<MowdigestNews> mNews;
    private RecyclerView mRecyclerView;

    public MowdigestRecyclerAdapter(Context context, List<MowdigestNews> items, RecyclerView rv) {
        // context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        // mBackground = mTypedValue.resourceId;
        mNews = items;
        mRecyclerView = rv;
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        @BindView(R.id.country_name)
        TextView mTextViewTitle;
        @BindView(R.id.country_photo)
        ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            int position = mRecyclerView.getChildAdapterPosition(view);
            Toast.makeText(view.getContext(), "Clicked Position = " + position, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public MowdigestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder output = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View theView = inflater.inflate(R.layout.mowdigest_card_view, parent, false);
        output = new ViewHolder(theView);
        return output;
    }

    @Override
    public void onBindViewHolder(MowdigestRecyclerAdapter.ViewHolder holder, int position) {
        holder.mTextViewTitle.setText(Integer.toString(position));
        if (position % 3 == 0) {
            holder.mImageView.setImageResource(R.drawable.background);
        }
        else if (position % 3 == 1) {
            holder.mImageView.setImageResource(R.drawable.coffee_22);
        }
        else {
            holder.mImageView.setImageResource(R.drawable.blobb_poster);
        }
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }
}
