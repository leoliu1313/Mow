package com.example.chinyao.mow.mowdigest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.model.MowdigestImage;
import com.example.chinyao.mow.mowdigest.model.MowdigestNews;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestRecyclerAdapter
        extends RecyclerView.Adapter<MowdigestRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<MowdigestNews> newsDigest;
    private RecyclerView recyclerView;

    public MowdigestRecyclerAdapter(Context theContext, List<MowdigestNews> theNewsDigest, RecyclerView theRecyclerView) {
        // context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        // mBackground = mTypedValue.resourceId;
        context = theContext;
        newsDigest = theNewsDigest;
        recyclerView = theRecyclerView;
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

    @Override
    public MowdigestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder output = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View theView = inflater.inflate(R.layout.mowdigest_card_view, parent, false);
        output = new ViewHolder(theView);
        return output;
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
            MowdigestNews theNews = newsDigest.get(position);
            // TODO: default image
            String image = "";
            boolean found = false;
            for (MowdigestImage theImage : theNews.getMedia().get(0).getMediaMetadata()) {
                // also change Glide placeholder() and error() in MowdigestSwipeAdapter.java
                // square320
                // mediumThreeByTwo440
                if (theImage.getFormat().equals("mediumThreeByTwo440")) {
                    image = theImage.getUrl();
                    break;
                }
                // in case the above is not there
                if (!found && theImage.getFormat().equals("sfSpan")) {
                    image = theImage.getUrl();
                    found = true;
                    // keep searching
                }
                if (!found) {
                    image = theImage.getUrl();
                    // keep searching
                }
            }
            Glide.with(context)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.mediumthreebytwo440)
                    .error(R.drawable.mediumthreebytwo440)
                    .into(holder.card_image);
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
        return newsDigest.size();
    }
}
