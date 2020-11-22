package com.example.securitymap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter<ListItem> {

    public ListAdapter(Context context, int rsc, List<ListItem> theAdpList){
        super(context,rsc,theAdpList);


    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListItem listItem = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_cell, parent,false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.listItemName);

        textView.setText(listItem.getName());

        // dont need this anymore?
        //return super.getView(position, convertView, parent);

        return convertView;
    }
}
