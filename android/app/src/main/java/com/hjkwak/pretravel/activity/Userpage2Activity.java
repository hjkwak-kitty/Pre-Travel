package com.hjkwak.pretravel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.ListviewAdapter.CustomContentListAdapter;
import com.hjkwak.pretravel.activity.Posting.SeePostActivity;
import com.hjkwak.pretravel.activity.data.MyList;

import java.util.ArrayList;

public class Userpage2Activity extends Activity {

    ListView listviewUser;
    ArrayList<MyList> userList;
    String text_uid;
    CustomContentListAdapter adapter;
    String writer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage2);
        listviewUser = (ListView)findViewById(R.id.listviewUser);
        text_uid = getIntent().getStringExtra("text_uid");
        userList = (ArrayList<MyList>)getIntent().getSerializableExtra("Data");
        writer = getIntent().getStringExtra("writer");
        adapter = new CustomContentListAdapter(Userpage2Activity.this, userList);
        listviewUser.setAdapter(adapter);

        listviewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(Userpage2Activity.this, SeePostActivity.class);
                intent.putExtra("data", userList);
                intent.putExtra("position", position);
                intent.putExtra("city", userList.get(position).getCity());
                intent.putExtra("country", userList.get(position).getCountry());
                intent.putExtra("user_name",writer);
                startActivity(intent);
            }
        });

    }

}
