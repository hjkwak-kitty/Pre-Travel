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
import com.example.hyojin.myapplication.activity.data.Movie;
import com.example.hyojin.myapplication.app.AppController;
import com.example.hyojin.myapplication.helper.SQLiteHandler;

import java.util.ArrayList;

public class AddMovieActivity extends Activity {

    int position;
    String country, city;
    ArrayList<Movie> data;
    private SQLiteHandler db;
    final private int EDIT=1;
    String uuid;
    SelectMovieActivity selectMovieA = (SelectMovieActivity)SelectMovieActivity.SelectMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //글번호 확인
        SharedPreferences sp = getSharedPreferences("now_post", MODE_PRIVATE);
        uuid = sp.getString("uuid",null);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        position = getIntent().getIntExtra("position", position);
        country = getIntent().getStringExtra("country");
        city = getIntent().getStringExtra("city");
        data = (ArrayList<Movie>)getIntent().getSerializableExtra("data");


        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.thumbnail);
        TextView textTitle = (TextView) findViewById(R.id.title);
        TextView textMaker = (TextView) findViewById(R.id.rating);
        TextView textTime = (TextView) findViewById(R.id.genre);
        Button btnCheck = (Button) findViewById(R.id.check);
        final EditText inputRecomend = (EditText) findViewById(R.id.inputComment);

        Movie m = data.get(position);

        // thumbnail image
        final String photo = m.getThumbnailUrl();
        thumbNail.setImageUrl(photo, imageLoader);

        // title
        final String title=data.get(position).getTitle();
        textTitle.setText(title);

        // rating
        final String data1 = data.get(position).getDirector();
        textMaker.setText(data1);

        // genre
        final String data2 =data.get(position).getGenre();
        textTime.setText(data2);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String comment = inputRecomend.getText().toString();
                if (!comment.isEmpty()) {
                    db.addContents(uuid, country, city, title, "movie", data1,
                            data2,comment, photo,0 ,"임시저장");
                    Intent intent = new Intent(AddMovieActivity.this, PostListActivity.class);
                    intent.putExtra("EDIT", EDIT);
                    startActivity(intent);
                    selectMovieA.finish();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "추천이유를 입력해 주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
