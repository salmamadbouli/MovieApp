package com.example.salma.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by abdel on 11/5/2016.
 */

public class MovieAdapter extends BaseAdapter  {

    private Context mContext;
    private ArrayList<Movie> movieList;

    public MovieAdapter(Context mContext ,ArrayList<Movie> List)
    {  this.mContext = mContext;
        this.movieList = List;
    }
    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int position) {return position;}

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_movies ,parent,false);
        ImageView movieimage = (ImageView)convertView.findViewById(R.id.list_item_movie_imageview);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+ movieList.get(position).getPosterPath()).into(movieimage);
        return convertView;
    }

    public void resetList (ArrayList<Movie> newList) {
        this.movieList = newList;
        this.notifyDataSetChanged();
    }

}