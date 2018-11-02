package com.example.hyojin.myapplication.activity.Posting;
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
import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.activity.ListviewAdapter.CustomBookListAdapter;
import com.example.hyojin.myapplication.activity.LoginActivity;
import com.example.hyojin.myapplication.activity.RegisterActivity;
import com.example.hyojin.myapplication.activity.data.Book;
import com.example.hyojin.myapplication.app.AppConfig;
import com.example.hyojin.myapplication.app.AppController;
import com.example.hyojin.myapplication.helper.SQLiteHandler;
import com.example.hyojin.myapplication.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SelectBookActivity extends Activity implements OnItemClickListener{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    EditText inputSearch;
    Button btnChkMovie;
    TextView textTitle;
    ListView listviewSearchMovie;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private ArrayList<Book> bookList = new ArrayList<Book>();
    CustomBookListAdapter bookListAdapter;
    int EDIT_ACTIVITY=1;
    String city;
    String country;
    public static Activity SelectBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contents);


        inputSearch = (EditText)findViewById(R.id.inputSearch);
        btnChkMovie=(Button)findViewById(R.id.btnChkMovie);
        listviewSearchMovie =(ListView)findViewById(R.id.listviewSearchMovie);
        textTitle = (TextView)findViewById(R.id.textTitle);
        SelectBook = SelectBookActivity.this;


        bookListAdapter = new CustomBookListAdapter(SelectBookActivity.this, bookList);
        listviewSearchMovie.setOnItemClickListener(this);
        listviewSearchMovie.setAdapter(bookListAdapter);

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

        inputSearch.setHint("추천할 책을 입력해주세요.");


        btnChkMovie.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bookList.clear();
                String movie = inputSearch.getText().toString().trim();
                if (!movie.isEmpty()) {
                    checkMovie(movie);
                } else {
                    Toast.makeText(getApplicationContext(), "검색 값이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

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
     * 책 정보 확인
     * */
    private void checkMovie(final String keyword) {
        // Tag used to cancel the request
        String tag_string_req = "req_checkingMovie";

        pDialog.setMessage("Checking ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCHBOOK, new Response.Listener<String>() {

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
                            String title = jObj.getJSONArray("title").getString(i);
                            String author = jObj.getJSONArray("author").getString(i);
                            String publish = jObj.getJSONArray("publish").getString(i);
                            String category = jObj.getJSONArray("category").getString(i);
                            String description = jObj.getJSONArray("description").getString(i);
                            String cover = jObj.getJSONArray("cover").getString(i);

                            bookList.add(new Book(title,author,publish,category,description,cover));
                        }
                        bookListAdapter.notifyDataSetChanged();

                        if(bookList==null)
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
        Intent intent = new Intent(this,AddBookActivity.class);
        intent.putExtra("data",bookList);
        intent.putExtra("position", position);
        intent.putExtra("city", city);
        intent.putExtra("country", country);
        startActivity(intent);
    }
}
