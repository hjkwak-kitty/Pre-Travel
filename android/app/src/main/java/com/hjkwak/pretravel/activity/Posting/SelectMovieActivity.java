package com.hjkwak.pretravel.activity.Posting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.LoginActivity;
import com.hjkwak.pretravel.activity.RegisterActivity;
import com.hjkwak.pretravel.activity.data.Movie;
import com.hjkwak.pretravel.app.AppConfig;
import com.hjkwak.pretravel.app.AppController;
import com.hjkwak.pretravel.activity.ListviewAdapter.CustomMovieListAdapter;
import com.hjkwak.pretravel.helper.SQLiteHandler;
import com.hjkwak.pretravel.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SelectMovieActivity extends Activity implements OnItemClickListener{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    EditText inputSearch;
    TextView textTitle;
    Button btnChkMovie;
    ListView listviewSearchMovie;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private ArrayList<Movie> movieList= new ArrayList<Movie>();
    CustomMovieListAdapter movieListAdapter;
    int EDIT_ACTIVITY=1;
    String city;
    String country;
    public static Activity SelectMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contents);

        inputSearch = (EditText)findViewById(R.id.inputSearch);
        btnChkMovie=(Button)findViewById(R.id.btnChkMovie);
        listviewSearchMovie =(ListView)findViewById(R.id.listviewSearchMovie);
        textTitle =(TextView)findViewById(R.id.textTitle);
        SelectMovie=SelectMovieActivity.this;

        movieListAdapter = new CustomMovieListAdapter(SelectMovieActivity.this, movieList);
        listviewSearchMovie.setOnItemClickListener(this);
        listviewSearchMovie.setAdapter(movieListAdapter);

        //제목 설정
        SharedPreferences sp = getSharedPreferences("now_post", MODE_PRIVATE);
        String myPlace =sp.getString("myPlace", null);
        textTitle.setText(myPlace);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        city=getIntent().getStringExtra("city");
        country=getIntent().getStringExtra("country");

        inputSearch.setHint("추천할 영화를 입력해주세요.");


        btnChkMovie.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                movieList.clear();
                String movie = inputSearch.getText().toString().trim();
                if (!movie.isEmpty()) {
                    checkMovie(movie);
                } else {
                    Toast.makeText(getApplicationContext(), "검색 값이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 영화 정보 확인
     * */
    private void checkMovie(final String keyword) {
        // Tag used to cancel the request
        String tag_string_req = "req_checkingMovie";

        pDialog.setMessage("Checking ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCHMOVIE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Check Movie Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        int totalCount = jObj.getInt("totalCount");
                        for(int i=0; i<totalCount ; i++) {
                                String title = jObj.getJSONArray("genre").getString(i);
                                String actor = jObj.getJSONArray("actor").getString(i);
                                String moreinfo = jObj.getJSONArray("moreinfo").getString(i);
                                String m_title = jObj.getJSONArray("title").getString(i);
                                String date = jObj.getJSONArray("date").getString(i);
                                String age = jObj.getJSONArray("age").getString(i);
                                String time = jObj.getJSONArray("time").getString(i);
                                String director = jObj.getJSONArray("director").getString(i);
                                String poster = jObj.getJSONArray("poster").getString(i);
                                String eng_title = jObj.getJSONArray("eng_title").getString(i);
                                movieList.add(new Movie(title,actor,moreinfo,m_title,date,age,time,director,poster,eng_title));
                        }
                            movieListAdapter.notifyDataSetChanged();

                        if(movieList==null)
                            Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Checking Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("keyword", keyword);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //finish();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this,AddMovieActivity.class);
        intent.putExtra("data", movieList);
        intent.putExtra("position", position);
        startActivityForResult(intent, EDIT_ACTIVITY);
    }
}
