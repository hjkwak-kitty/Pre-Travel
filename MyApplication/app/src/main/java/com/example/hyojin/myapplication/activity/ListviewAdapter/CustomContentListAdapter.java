package com.example.hyojin.myapplication.activity.ListviewAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.activity.data.MyList;
import com.example.hyojin.myapplication.app.AppController;

import java.util.ArrayList;

/**
 * Created by Hyojin on 2016-05-21.
 */public class CustomContentListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<MyList> contents;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomContentListAdapter(Activity activity, ArrayList contents) {
        this.activity = activity;
        this.contents = contents;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int location) {
        return contents.get(location);
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
        ImageView tagBook=(ImageView)convertView.findViewById(R.id.imgTagBook);



        // getting movie data for the row
       MyList m = contents.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        textTitle.setText(contents.get(position).getTitle());

        // rating
        textMaker.setText(contents.get(position).getGenre());

        textTitle.setTextColor(Color.GRAY);
        textMaker.setTextColor(Color.GRAY);
        textTime.setTextColor(Color.GRAY);
        if(contents.get(position).getDirector().equals("movie")) {
            tagBook.setBackground(convertView.getResources().getDrawable(R.drawable.tag_movie));
            }
        else if(contents.get(position).getDirector().equals("book")) {
            tagBook.setBackground(convertView.getResources().getDrawable(R.drawable.tag_book));
            }
        else {
            tagBook.setBackground(convertView.getResources().getDrawable(R.drawable.tag_delete));
            }


        // genre
        textTime.setText(contents.get(position).getCon_data3());

        return convertView;
    }


}
