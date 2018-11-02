package com.example.hyojin.myapplication.activity.Posting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.activity.RegisterActivity;
import com.example.hyojin.myapplication.activity.data.MyList;
import com.example.hyojin.myapplication.app.AppController;
import com.example.hyojin.myapplication.helper.SQLiteHandler;

import java.util.ArrayList;

/**
 * Created by Hyojin on 2016-06-01.
 */
public class EditContentsActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    int EDIT;
    int position;
    String country, city;
    ArrayList<MyList> data;
    private SQLiteHandler db;
    String uuid;
    PostListActivity PostListA = (PostListActivity)PostListActivity.PostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        EDIT=getIntent().getIntExtra("EDIT",EDIT);

        if(EDIT==1||EDIT==2) {
            //글번호 확인
            SharedPreferences sp = getSharedPreferences("now_post", MODE_PRIVATE);
            uuid = sp.getString("uuid", null);
        }
        else{
            uuid=getIntent().getStringExtra("text_uid");
        }
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        position = getIntent().getIntExtra("position", position);
        country = getIntent().getStringExtra("country");
        city = getIntent().getStringExtra("city");
        data = (ArrayList<MyList>)getIntent().getSerializableExtra("data");

        ImageLoader imageLoader;
        imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.thumbnail);
        TextView textTitle = (TextView) findViewById(R.id.title);
        TextView textMaker = (TextView) findViewById(R.id.rating);
        TextView textTime = (TextView) findViewById(R.id.genre);
        Button btnCheck = (Button) findViewById(R.id.check);
        final EditText inputRecomend = (EditText) findViewById(R.id.inputComment);

        final MyList m = data.get(position);

        // thumbnail image
        final String photo = m.getThumbnailUrl();
        thumbNail.setImageUrl(photo, imageLoader);

        // title
        final String title=data.get(position).getTitle();
        textTitle.setText(title);

        // rating
        final String data1 = data.get(position).getDirector();


        // genre
        final String data2 =data.get(position).getGenre();
        textMaker.setText(data2);

        final String data3 = data.get(position).getCon_data3();
        textTime.setText(data3);

        inputRecomend.setText(data.get(position).getCon_data4());

        btnCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String comment = inputRecomend.getText().toString();
                if (!comment.isEmpty()) {
                    db.editMyContents(comment,uuid,title);
                    Intent intent = new Intent(EditContentsActivity.this, PostListActivity.class);
                    intent.putExtra("EDIT", 1);
                    intent.putExtra("text_uid",m.getText_uid());
                    startActivity(intent);
                    PostListA.finish();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "추천이유를 입력해 주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
