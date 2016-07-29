package com.example.chinyao.mow.mowdigest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chinyao.mow.R;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chinyao on 7/23/2016.
 */
public class MowdigestFakeRecyclerAdapter
        extends RecyclerView.Adapter<MowdigestFakeRecyclerAdapter.ViewHolder> {

    private List<String> mItems;
    private Context mContext;

    private ArrayList<Swipe> al;
    private MowdigestSwipeAdapter myAppAdapter;

    static String link1 = "https://s-media-cache-ak0.pinimg.com/236x/e7/7b/29/e77b294d3dc6245ab4b517142e1f63b0.jpg";
    static String link2 = "https://s-media-cache-ak0.pinimg.com/236x/e7/7b/29/e77b294d3dc6245ab4b517142e1f63b0.jpg";
    static String link3 = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTqEJBhd92spKnkYretdXnn5Twbnoii1NgdjXLBuddq8bF1bfEA";


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SwipeFlingAdapterView theSwipe;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            theSwipe = (SwipeFlingAdapterView) view.findViewById(R.id.m1_swipe);
        }
    }

    public MowdigestFakeRecyclerAdapter(Context context, List<String> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder output = null;
        View theView;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            theView = inflater.inflate(R.layout.mowdigest_fake_recycler_view, parent, false);
        output = new ViewHolder(theView);
        return output;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        setupSwipe(holder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void setupSwipe(final ViewHolder holder) {

        al = new ArrayList<>();
        al.add(new Swipe(link1, "link1"));
        al.add(new Swipe(link2, "link2"));
        al.add(new Swipe(link1, "link1"));
        al.add(new Swipe(link2, "link2"));
        al.add(new Swipe(link1, "link1"));
        al.add(new Swipe(link2, "link2"));

        // myAppAdapter = new MowdigestSwipeAdapter(al, getContext());
        myAppAdapter = new MowdigestSwipeAdapter(al, mContext);

        holder.theSwipe.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // nothing
                Log.d("MowdigestFragment", "removeFirstObjectInAdapter");
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                al.remove(0);
                myAppAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                al.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                al.add(new Swipe(link3, "More"));
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = holder.theSwipe.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        holder.theSwipe.setAdapter(myAppAdapter);
    }
}