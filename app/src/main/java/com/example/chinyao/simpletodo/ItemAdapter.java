package com.example.chinyao.simpletodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chinyao on 6/20/2016.
 */
public class ItemAdapter extends ArrayAdapter<String> {
    public ItemAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        // Lookup view for data population
        TextView itemContent = (TextView) convertView.findViewById(R.id.itemContent);
        // Populate the data into the template view using the data object
        itemContent.setText(item);
        // Return the completed view to render on screen
        return convertView;
    }
}
