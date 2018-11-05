package com.hjkwak.pretravel.activity.ListviewAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.data.MyList;

import java.util.ArrayList;

/**
 * Created by Hyojin on 2016-06-22.
 */
public class CustomMylistAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<MyList> contents;

    public CustomMylistAdapter(Activity activity, ArrayList<MyList> Items) {
        this.activity = activity;
        this.contents = Items;
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
            convertView = inflater.inflate(R.layout.list_mypost_low, null);

        TextView txtWhere = (TextView)convertView.findViewById(R.id.txtWhere);
        TextView txtDate = (TextView)convertView.findViewById(R.id.txtDate);

        MyList m = contents.get(position);

        txtDate.setText(m.getdate());
        txtWhere.setText(m.getWhere());

        return convertView;

    }

}




