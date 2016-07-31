package com.example.chinyao.mow.mowdigest;

import android.content.Context;
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
import com.example.chinyao.mow.mowdigest.model.MowdigestImage;
import com.example.chinyao.mow.mowdigest.model.MowdigestPopularNews;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestRecyclerAdapter
        extends RecyclerView.Adapter<MowdigestRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<MowdigestPopularNews> newsDigest;

    public MowdigestRecyclerAdapter(Context theContext, List<MowdigestPopularNews> theNewsDigest, RecyclerView theRecyclerView) {
        // context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        // mBackground = mTypedValue.resourceId;
        context = theContext;
        newsDigest = theNewsDigest;
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
        if (position < newsDigest.size()) {
            boolean do_full_span = true;
            if (position - 1 >= 0) {
                if (newsDigest.get(position - 1).getMowdigestFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 2 >= 0) {
                if (newsDigest.get(position - 2).getMowdigestFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 3 >= 0) {
                if (newsDigest.get(position - 3).getMowdigestFullSpan()) {
                    do_full_span = false;
                }
            }
            if (position - 4 >= 0) {
                if (newsDigest.get(position - 4).getMowdigestFullSpan()) {
                    do_full_span = false;
                }
            }
            if (do_full_span) {
                if (newsDigest.get(position).getMedia().get(0).getMediaMetadata().size() > 0) {
                    newsDigest.get(position).setMowdigestFullSpan(true);
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public MowdigestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(MowdigestRecyclerAdapter.ViewHolder holder, final int position) {
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
            MowdigestPopularNews theNews = newsDigest.get(position);
            String image = null;
            boolean found_sfSpan = false;
            boolean found = false;
            for (MowdigestImage theImage : theNews.getMedia().get(0).getMediaMetadata()) {
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
            holder.card_title.setText(theNews.getTitle());
            holder.card_section.setText(theNews.getSection());
            holder.card_published_date.setText(theNews.getPublished_date());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Clicked Position = " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (newsDigest != null) {
            return newsDigest.size();
        }
        else {
            return 0;
        }
    }
}
