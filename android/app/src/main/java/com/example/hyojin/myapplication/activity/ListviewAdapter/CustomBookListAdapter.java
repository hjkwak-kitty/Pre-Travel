package com.example.hyojin.myapplication.activity.ListviewAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.activity.data.Book;
import com.example.hyojin.myapplication.app.AppController;

import java.util.ArrayList;

/**
 * Created by Hyojin on 2016-05-21.
 */public class CustomBookListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Book> movieItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomBookListAdapter(Activity activity, ArrayList Items) {
        this.activity = activity;
        this.movieItems = Items;
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_contents_low, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);

        TextView textTitle = (TextView) convertView.findViewById(R.id.title);
        TextView textMaker = (TextView) convertView.findViewById(R.id.rating);
        TextView textTime = (TextView) convertView.findViewById(R.id.genre);

        // getting movie data for the row
        Book m = movieItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        textTitle.setText(movieItems.get(position).getTitle());

        // rating
        textMaker.setText(movieItems.get(position).getDirector());

        // genre
        textTime.setText(movieItems.get(position).getGenre());


        return convertView;
    }

}
