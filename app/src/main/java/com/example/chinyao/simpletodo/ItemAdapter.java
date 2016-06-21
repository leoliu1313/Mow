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
public class ItemAdapter extends ArrayAdapter<TodoModel> {
    // View lookup cache
    // reduce findViewById() calls
    private static class ViewHolder {
        TextView content;
        TextView priority;
        TextView date;
    }

    public ItemAdapter(Context context, ArrayList<TodoModel> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoModel item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.content = (TextView) convertView.findViewById(R.id.itemContent);
            viewHolder.priority = (TextView) convertView.findViewById(R.id.itemPriority);
            viewHolder.date = (TextView) convertView.findViewById(R.id.itemDate);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.content.setText(item.content);
        viewHolder.priority.setText(item.priority);
        viewHolder.date.setText(item.date);
        // Return the completed view to render on screen
        return convertView;
    }
}
