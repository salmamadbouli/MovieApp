package com.example.salma.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ReviewsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Reviews> reviewsList;
    public ReviewsAdapter (Context context, ArrayList<Reviews> reviewsList)
    {this.context = context;
        this.reviewsList = reviewsList;
    }
    @Override
    public int getCount() {
        return reviewsList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_reviews ,parent,false);
        TextView author = (TextView)convertView.findViewById(R.id.author_name);
        author.setText(reviewsList.get(position).getAuthor());
        TextView content = (TextView)convertView.findViewById(R.id.content_review);
        content.setText(reviewsList.get(position).getContent());
        return convertView;
    }
}
