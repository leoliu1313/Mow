package com.example.chinyao.mow.mowdigest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chinyao.mow.R;

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.r_title)
        TextView mTextViewTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public MowdigestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder output = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View theView = inflater.inflate(R.layout.mowtube_list_item_poster, parent, false);
        output = new ViewHolder(theView);
        return output;
    }

    @Override
    public void onBindViewHolder(MowdigestRecyclerAdapter.ViewHolder holder, int position) {
        holder.mTextViewTitle.setText(Integer.toString(position));
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }
}
