package com.example.hyojin.myapplication.activity.Posting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.activity.ListviewAdapter.CustomContentListAdapter;
import com.example.hyojin.myapplication.activity.LoginActivity;
import com.example.hyojin.myapplication.activity.RegisterActivity;
import com.example.hyojin.myapplication.activity.data.MyList;
import com.example.hyojin.myapplication.app.AppConfig;
import com.example.hyojin.myapplication.app.AppController;
import com.example.hyojin.myapplication.helper.SQLiteHandler;
import com.example.hyojin.myapplication.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostListActivity extends Activity{
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputWhere;
    private Button btnChkWhere;
    private Button btnUpload;
    private ListView listviewContent;
    int EDIT_ACTIVITY=1;
    private ImageButton btnAdd;
    private TextView textWhere;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private String city;
    private String country;
    private LinearLayout background;

    int EDIT=0;
    String uuid;
    String myplace;
    ArrayList<MyList> mine = new ArrayList<>();
    public static Activity PostList;
    CustomContentListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        inputWhere =(EditText)findViewById(R.id.inputWhere);
        btnChkWhere =(Button)findViewById(R.id.btnChkWhere);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        textWhere = (TextView)findViewById(R.id.textWhere);
        btnAdd=(ImageButton)findViewById(R.id.btnAdd);
        listviewContent =(ListView)findViewById(R.id.listviewContents);
        background = (LinearLayout)findViewById(R.id.background);

        PostList = PostListActivity.this;


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //로그인 세션 확인
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        //수정중인 포스팅 일 때, 리스트 세팅 및 뷰 세팅
        EDIT=getIntent().getIntExtra("EDIT",EDIT);

        if(EDIT>0){
            if(EDIT==1){
                SharedPreferences sp = getSharedPreferences("now_post", MODE_PRIVATE);
                uuid=sp.getString("uuid",null);
                country=sp.getString("country", null);
                city=sp.getString("city",null);
                myplace=country+" "+city;
                setView(myplace);

                mine = db.getContentsDetails(uuid);
            }
            else{
                uuid = getIntent().getStringExtra("text_uid");
                mine = db.getContentsDetails(uuid);
                country=getIntent().getStringExtra("country");
                city=getIntent().getStringExtra("city");
                myplace=country+" "+city;
                setView(myplace);
                setInfo();
            }

            adapter = new CustomContentListAdapter(PostListActivity.this, mine);
            listviewContent.setAdapter(adapter);

            listviewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    CharSequence[] what = {"수정", "삭제"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(PostListActivity.this);
                    builder.setTitle("무엇을하시겠습니까?");

                    builder.setItems(what, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent toEdit = new Intent(PostListActivity.this, EditContentsActivity.class);
                                    toEdit.putExtra("data", mine);
                                    toEdit.putExtra("position", position);
                                    toEdit.putExtra("city", city);
                                    toEdit.putExtra("country", country);
                                    toEdit.putExtra("EDIT", EDIT);
                                    startActivity(toEdit);
                                    break;
                                case 1:
                                    checkYesNo(position);
                                    break;
                            }
                            //shareAlert.dismiss();
                        }
                    });
                    final AlertDialog shareAlert = builder.create();
                    shareAlert.show();
                }
            });
        } else if(EDIT==0){
            //글번호생성
            uuid = UUID.randomUUID().toString().replace("-", "");
        }

        btnChkWhere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String where = inputWhere.getText().toString().trim();
                if (!where.isEmpty()) {
                    checkWhere(where);
                }else {
                    Toast.makeText(getApplicationContext(), "장소를 입력해 주세요", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addList(country, city);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                deleteContents(uuid);
                uploadContents();
            }
        });

    }

    //서버 DB 컨텐츠 삭제
    private void deleteContents(final String text_uid){
        // Tag used to cancel the request
        String tag_string_req = "req_deleteContents";

        pDialog.setMessage("deleting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_DELETECONTENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "delete contents: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in server data
                        Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_LONG).show();

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

                // Fetching user details from sqlite
                HashMap<String, String> params = new HashMap<>();
                params.put("text_uid", text_uid);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    /*삭제하시겠습니까 확인창*/
    private void checkYesNo(final int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostListActivity.this);

        // 제목셋팅
        alertDialogBuilder.setTitle("리스트 삭제");

        // AlertDialog 셋팅
        alertDialogBuilder.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 리스트삭제
                db.deleteMyContent(mine.get(position).getText_uid(),mine.get(position).getTitle());
                mine.remove(position);
                adapter.notifyDataSetChanged();

            }
        })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        // 다이얼로그를 취소한다
                        dialog.cancel();
                    }
                });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();

    }
    /*컨텐츠 서버 디비로 업로드*/
    private void uploadContents(){
        // Tag used to cancel the request
        String tag_string_req = "req_uploadContents";

        pDialog.setMessage("Checking ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_POSTUPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "upload contents: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in server data
                        Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();
                        finish();

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
                String[] index = {"user_name","text_uid","country","city",
                        "con_title","con_data1", "con_data2","con_data3", "con_data4","con_photo","recommend","date"};

                // Fetching user details from sqlite
                HashMap<String, String> user = db.getUserDetails();
                String name = user.get("name");

                //params.put(index[0]+"_"+i,name);
                params.put("size", String.valueOf(mine.size()));
                //params.put("test","test");
                //params.put("user_name_1","이름테스트");
                for(int i=0;i<mine.size();i++){
                    params.put(index[0]+"_"+i,name);
                    params.put(index[1]+"_"+i,mine.get(i).getText_uid());
                    params.put(index[2]+"_"+i,country);
                    params.put(index[3]+"_"+i,city);
                    params.put(index[4]+"_"+i,mine.get(i).getTitle());
                    params.put(index[5] + "_"+i,mine.get(i).getDirector());
                    params.put(index[6]+"_"+i,mine.get(i).getGenre());
                    params.put(index[7]+ "_"+i,mine.get(i).getCon_data3());
                    params.put(index[8]+"_"+i,mine.get(i).getCon_data4());
                    params.put(index[9]+"_"+i,mine.get(i).getThumbnailUrl());
                    params.put(index[10]+"_"+i,String.valueOf(mine.get(i).getRecommend()));
                    params.put(index[11]+"_"+i,mine.get(i).getdate());
                }
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    /**
     * 장소 정보 확인
     * */
    private void checkWhere(final String where) {
        // Tag used to cancel the request
        String tag_string_req = "req_checkingWhere";

        pDialog.setMessage("Checking ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SETWHEREITIS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Check Where Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        city = jObj.getString("city");
                        country = jObj.getString("country");
                        if(city.equals("")||city.equals("null"))
                            myplace= country;
                        else
                            myplace = country+ " "+city;

                        confirmWhere(myplace);


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
                params.put("where", where);
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
        finish();
    }

    private void addList(final String country, final String city) {
        CharSequence[] what={"책","영화"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PostListActivity.this);
        builder.setTitle("추가 할 목록");

        builder.setItems(what, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(PostListActivity.this, SelectBookActivity.class);
                        intent.putExtra("city", city);
                        intent.putExtra("country", country);
                        intent.putExtra("EDIT",EDIT);
                        startActivity(intent);
                        finish();
                        break;
                    case 1:
                        Intent intent1 = new Intent(PostListActivity.this, SelectMovieActivity.class);
                        intent1.putExtra("city", city);
                        intent1.putExtra("country", country);
                        intent1.putExtra("EDIT",EDIT);
                        startActivity(intent1);
                        finish();
                        break;
                }
                //shareAlert.dismiss();
            }
        });
        final AlertDialog shareAlert = builder.create();
        shareAlert.show();
    }
    private void confirmWhere(final String myplace) {
        CharSequence[] where={myplace, "추가하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PostListActivity.this);
        builder.setTitle("장소 확인");

        builder.setItems(where, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        setInfo();
                        setView(myplace);
                        break;
                    case 1:
                        View dialog2 = View.inflate(getApplicationContext(), R.layout.pop_add, null);
                        final AlertDialog ad = new AlertDialog.Builder(PostListActivity.this).setView(dialog2).create();
                        final EditText country = (EditText) dialog2.findViewById(R.id.inputcountry);
                        final EditText city = (EditText) dialog2.findViewById(R.id.inputcity);
                        dialog2.findViewById(R.id.completeButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addWhere(country.getText().toString(), city.getText().toString());
                                ad.dismiss();
                            }
                        });
                        ad.show();
                        break;
                }
                //shareAlert.dismiss();
            }
        });
        final AlertDialog shareAlert = builder.create();
        shareAlert.show();
    }

    public void addWhere(final String country, final String city){
        // Tag used to cancel the request
        String tag_string_req = "req_addCountryCity";

        pDialog.setMessage("Checking ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ADDWHERE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Where Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        Toast.makeText(getApplicationContext(), "추가성공", Toast.LENGTH_LONG).show();

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
                params.put("city", city);
                params.put("country", country);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void setView(String myplace){
        //장소 확인 후 뷰셋팅
        btnChkWhere.setVisibility(View.GONE);
        inputWhere.setVisibility(View.GONE);
        textWhere.setText(myplace + "에 가기 전 봐야할 책/영화 추천");
        btnAdd.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.VISIBLE);
        background.setBackgroundColor(getResources().getColor(R.color.wring_background));
    }


    private void setInfo(){
        //지금 하고 있는 포스팅 정보 생성
        SharedPreferences sp = getSharedPreferences("now_post", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("uuid", uuid);
        edit.putString("country", country);
        edit.putString("city", city);
        edit.putString("myPlace", myplace);
        edit.putString("state", "edit");
        edit.commit();
    }


}
