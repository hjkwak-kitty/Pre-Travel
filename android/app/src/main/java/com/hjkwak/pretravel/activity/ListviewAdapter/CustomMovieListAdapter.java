package com.hjkwak.pretravel.activity.ListviewAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.data.Movie;
import com.hjkwak.pretravel.app.AppController;

import java.util.ArrayList;

/**
 * Created by Hyojin on 2016-05-21.
 */public class CustomMovieListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Movie> movieItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomMovieListAdapter(Activity activity, ArrayList Items) {
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

//        TextView textTitle = (TextView)convertView.findViewById(R.id.textTitle);
  //      TextView textMaker = (TextView)convertView.findViewById(R.id.textMaker);
    //    TextView textTime = (TextView)convertView.findViewById(R.id.textTime);

        TextView textTitle = (TextView) convertView.findViewById(R.id.title);
        TextView textMaker = (TextView) convertView.findViewById(R.id.rating);
        TextView textTime = (TextView) convertView.findViewById(R.id.genre);
      //  TextView year = (TextView) convertView.findViewById(R.id.releaseYear);



        // getting movie data for the row
        Movie m = movieItems.get(position);

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
