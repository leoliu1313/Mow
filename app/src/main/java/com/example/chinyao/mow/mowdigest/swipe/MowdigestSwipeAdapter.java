package com.example.chinyao.mow.mowdigest.swipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.MowdigestActivity;
import com.example.chinyao.mow.mowdigest.model.MowdigestPopularNews;
import com.example.chinyao.mow.mowdigest.model.MowdigestPopularResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestSwipeAdapter extends BaseAdapter {

    private List<MowdigestSwipe> theSwipes;
    private Context context;
    private int offset;
    private boolean lock;
    private OnAsyncFinishedListener interfaceListenerEvent;

    private static int MaxTitleLength = 75;
    private static int MaxAbstractLength = 120;

    public MowdigestSwipeAdapter(List<MowdigestSwipe> swipes, Context context) {
        this.theSwipes = swipes;
        this.context = context;
        this.offset = 0;
        this.lock = false;
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
            // TODO: never go to here. bug?
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MowdigestSwipe theSwipe = theSwipes.get(position);
        Glide.with(context)
                .load(theSwipe.getImage())
                .centerCrop()
                /*
                // TODO: cannot have beautiful top round corners
                // import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
                .bitmapTransform(new RoundedCornersTransformation(
                        context, 30, 10,
                        RoundedCornersTransformation.CornerType.TOP))
                */
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

    public void loadMoreOld() {
        if (!lock) {
            lock = true;
            final int preOffset = offset;
            AsyncHttpClient client = new AsyncHttpClient();
            // Turn off Debug Log
            // client.setLoggingEnabled(false);
            String url = MowdigestActivity.BASE_URL + MowdigestActivity.MOST_POPULAR + "/all-sections/1.json";
            RequestParams params = new RequestParams();
            params.put("api-key", MowdigestActivity.API_KEY);
            params.put("offset", offset);
            offset += 20;
            Log.d("MowdigestSwipeAdapter", "url " + url);
            client.get(url, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("MowdigestSwipeAdapter",
                                    "statusCode " + statusCode);
                            String input = response.toString();
                            Log.d("MowdigestSwipeAdapter", input);
                            input = input.replaceAll(",\"media\":\"\"", "");
                            // input = input.replaceAll("\"media\":\"\"", "\"media\":[{\"media-metadata\":[{}]}]");
                            Log.d("MowdigestSwipeAdapter", input);
                            MowdigestPopularResult theSearch = MowdigestPopularResult.parseJSON(input);
                            Log.d("MowdigestSwipeAdapter",
                                    "theSearch.getResults().size() " + theSearch.getResults().size());
                            if (theSearch != null) {
                                Log.d("MowdigestSwipeAdapter",
                                        "theSearch.getResults().size() " + theSearch.getResults().size());
                                for (MowdigestPopularNews theNews : theSearch.getResults()) {
                                    if (theNews.getMedia() != null) {
                                        theSwipes.add(new MowdigestSwipe(theNews));
                                    }
                                }
                                notifyDataSetChanged();
                                lock = false;
                                interfaceListenerEvent.onAsyncFinished(preOffset);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            lock = false;
                            interfaceListenerEvent.onAsyncFinished(preOffset);
                        }
                    }
            );
        }
    }

    void loadMore() {
        if (!lock) {
            lock = true;
            final int preOffset = offset;
            final Call<MowdigestPopularResult> call =
                    MowdigestActivity.TheAPIInterface.getPopular(
                            "all-sections",
                            "1",
                            Integer.toString(offset),
                            MowdigestActivity.API_KEY);
            offset += 20;
            call.enqueue(new Callback<MowdigestPopularResult>() {
                @Override
                public void onResponse(Call<MowdigestPopularResult> call, Response<MowdigestPopularResult> response) {
                    int statusCode = response.code();
                    Log.d("MowdigestSwipeAdapter", "onResponse");
                    Log.d("MowdigestSwipeAdapter", "statusCode " + statusCode);
                    if (statusCode == 403) {
                        Toast.makeText(context, "Invalid authentication credentials", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MowdigestPopularResult theSearch = response.body();
                    if (theSearch != null) {
                        Log.d("MowdigestSwipeAdapter",
                                "theSearch.getResults().size() " + theSearch.getResults().size());
                        for (MowdigestPopularNews theNews : theSearch.getResults()) {
                            theSwipes.add(new MowdigestSwipe(theNews));
                        }
                        notifyDataSetChanged();
                        lock = false;
                        interfaceListenerEvent.onAsyncFinished(preOffset);
                    }
                }

                @Override
                public void onFailure(Call<MowdigestPopularResult> call, Throwable t) {
                    Log.d("MowdigestSwipeAdapter", "onFailure");
                    Log.d("MowdigestSwipeAdapter", call.request().toString());
                    Log.d("MowdigestSwipeAdapter", t.toString());
                    lock = false;
                    interfaceListenerEvent.onAsyncFinished(preOffset);
                }
            });
        }
    }

    public void setOnAsyncFinishedListener(OnAsyncFinishedListener event) {
        this.interfaceListenerEvent = event;
    }

    public interface OnAsyncFinishedListener {
        void onAsyncFinished(int preOffset);
    }
}