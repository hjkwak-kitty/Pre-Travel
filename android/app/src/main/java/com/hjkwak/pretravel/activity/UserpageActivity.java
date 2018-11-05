package com.hjkwak.pretravel.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.ListviewAdapter.CustomMylistAdapter;
import com.hjkwak.pretravel.activity.data.MyList;
import com.hjkwak.pretravel.app.AppConfig;
import com.hjkwak.pretravel.app.AppController;
import com.hjkwak.pretravel.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserpageActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    ArrayList<MyList> mylist, myContents;
    TextView txtUsername;
    Button btnFollow;
    int follow_state=0;
    ListView userListview;
    CustomMylistAdapter userlistAdapter;
    String writer;
    boolean isInList=false;
    ArrayList<String> listWriterFollow, listMyFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
        final String user_name = user.get("name");


        btnFollow=(Button)findViewById(R.id.btnFollow);
        txtUsername = (TextView)findViewById(R.id.txtName);
        writer=getIntent().getStringExtra("writer");
        txtUsername.setText(writer);

        userListview = (ListView)findViewById(R.id.listviewUserPost);

        mylist = new ArrayList<>();
        mylist.clear();
        myContents = new ArrayList<>();
        getMycontents(writer);
        userlistAdapter = new CustomMylistAdapter(UserpageActivity.this, mylist);
        userListview.setAdapter(userlistAdapter);

        userListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                getMycontentDetail(writer, mylist.get(position).getText_uid(), position);
            }
        });


        //글쓴이가 나일때
        if(writer.equals(user_name)){
            btnFollow.setText("내페이지");
            btnFollow.setEnabled(false);
            btnFollow.setVisibility(View.GONE);
        }else{//내가 아닐때
            //follow상태 확인
            listMyFollowing = new ArrayList<>();
            listWriterFollow = new ArrayList<>();
            listMyFollowing.clear();
            listWriterFollow.clear();
            getFollowingUserList(user_name, writer);

        }

        //추천버튼 클릭시
        btnFollow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (follow_state == 0) { //추천하기
                    btnFollow.setBackground(getResources().getDrawable(R.drawable.btn_green));
                    listMyFollowing.add(user_name); //writer의 follower
                    listWriterFollow.add(writer); //내가 follow
                    pushFollow(user_name,writer,listMyFollowing.toString(),listWriterFollow.toString());
                    sendAlram(writer);
                    Toast.makeText(UserpageActivity.this, "팔로우했습니다.", Toast.LENGTH_LONG).show();
                    follow_state++;
                } else {
                    btnFollow.setBackground(getResources().getDrawable(R.drawable.btn_round));
                    listMyFollowing.remove(user_name);
                    listWriterFollow.remove(writer);
                    pushFollow(user_name, writer, listMyFollowing.toString(), listWriterFollow.toString());

                    Toast.makeText(UserpageActivity.this, "팔로우를 취소했습니다.", Toast.LENGTH_LONG).show();
                    follow_state--;
                    //isInList = false;
                }
            }
        });



    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void getMycontents(final String name){
        // Tag used to cancel the request
        String tag_string_req = "req_getMyContents";

        pDialog.setMessage("동기화중 ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_BYUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "동기화 contents: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in server data
                        int i;
                        String temp;
                        int count= jObj.getInt("size");
                        for(i=0; i<count ; i++) {
                            String user = jObj.getJSONArray("user_name").getString(i);
                            String text_uid = jObj.getJSONArray("text_uid").getString(i);
                            String country = jObj.getJSONArray("country").getString(i);
                            String city = jObj.getJSONArray("city").getString(i);
                            String con_title = jObj.getJSONArray("con_title").getString(i);
                            String con_data1 = jObj.getJSONArray("con_data1").getString(i).trim();
                            String con_data2 = jObj.getJSONArray("con_data2").getString(i);
                            String con_data3 = jObj.getJSONArray("con_data3").getString(i);
                            String con_data4 = jObj.getJSONArray("con_data4").getString(i);
                            String con_photo = jObj.getJSONArray("con_photo").getString(i);
                            String created_at = jObj.getJSONArray("created_at").getString(i);
                            int recommend = jObj.getJSONArray("recommend").getInt(i);
                            mylist.add(new MyList(text_uid, country, city, con_title, con_data1, con_data2, con_data3, con_data4, con_photo,recommend,created_at));
                        }
                       // Toast.makeText(UserpageActivity.this, "리스트!"+i, Toast.LENGTH_LONG).show();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(UserpageActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Checking Error: " + error.getMessage());
                Toast.makeText(UserpageActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void getMycontentDetail(final String name, final String search_uid, final int position){
        // Tag used to cancel the request
        String tag_string_req = "req_getMyContents";

        pDialog.setMessage("동기화중 ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SYNC, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "동기화 contents: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        myContents.clear();
                        // User successfully stored in MySQL
                        // Now store the user in server data
                        int i;
                        int count= jObj.getInt("size");
                        for(i=0; i<count ; i++) {
                            String user = jObj.getJSONArray("user_name").getString(i);
                            String text_uid = jObj.getJSONArray("text_uid").getString(i);
                            String country = jObj.getJSONArray("country").getString(i);
                            String city = jObj.getJSONArray("city").getString(i);
                            String con_title = jObj.getJSONArray("con_title").getString(i);
                            String con_data1 = jObj.getJSONArray("con_data1").getString(i).trim();
                            String con_data2 = jObj.getJSONArray("con_data2").getString(i);
                            String con_data3 = jObj.getJSONArray("con_data3").getString(i);
                            String con_data4 = jObj.getJSONArray("con_data4").getString(i);
                            String con_photo = jObj.getJSONArray("con_photo").getString(i);
                            String created_at = jObj.getJSONArray("created_at").getString(i);
                            int recommend = jObj.getJSONArray("recommend").getInt(i);
                            if(search_uid.equals(text_uid)) {
                                myContents.add(new MyList(text_uid, country, city, con_title, con_data1, con_data2, con_data3, con_data4, con_photo, recommend, created_at));
                            }
                        }
                       // Toast.makeText(UserpageActivity.this, "리스트!"+i, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UserpageActivity.this, Userpage2Activity.class);
                        intent.putExtra("text_uid", mylist.get(position).getText_uid());
                        intent.putExtra("Data",myContents);
                        intent.putExtra("writer",writer);
                        startActivity(intent);


                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(UserpageActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Checking Error: " + error.getMessage());
                Toast.makeText(UserpageActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //following 목록 가져오기
    private void getFollowingUserList(final String user, final String writer){
        // Tag used to cancel the request
        String tag_string_req = "req_FolliwingUserList";

        pDialog.setMessage("Getting List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GETFOLLOWINGLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Getting Following List: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String followingList = jObj.getString("following").replace("[","").replace("]","").replace(" ", "");
                        String[] myFollowingList= followingList.split(",");
                        String followList=jObj.getString("follow").replace("[","").replace("]","").replace(" ", "");
                        String[] WriterFollowList= followList.split(",");
                        //Toast.makeText(UserpageActivity.this,followingList+followList, Toast.LENGTH_LONG).show();

                        for(int i=0; i<myFollowingList.length;i++){
                            listMyFollowing.add(myFollowingList[i]);
                            if(myFollowingList[i].equals(user)==true){
                                isInList=true;
                                btnFollow.setBackground(getResources().getDrawable(R.drawable.btn_green));
                                follow_state=1;
                            }
                            else {
                                btnFollow.setBackground(getResources().getDrawable(R.drawable.btn_round));
                                isInList = false;
                            }
                        }
                        for(int i=0; i<WriterFollowList.length;i++){
                            listWriterFollow.add(WriterFollowList[i]);
                        }
                        //Toast.makeText(UserpageActivity.this, listMyFollowing.toString()+"\n"+listWriterFollow.toString(), Toast.LENGTH_LONG).show();
                        //userRecommendList.remove(0);
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(UserpageActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Getting Follow List: " + error.getMessage());
                Toast.makeText(UserpageActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name", user);
                params.put("writer",writer);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    //Follow 목록 업데이트
    private void pushFollow(final String user_name, final String writer, final String write_follower, final String user_follow){
        // Tag used to cancel the request
        String tag_string_req = "req_pushFollowBtn";

        pDialog.setMessage("Update List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PUSHFOLLOW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Follow List: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        //Toast.makeText(UserpageActivity.this, "Follow클릭", Toast.LENGTH_LONG).show();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(UserpageActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Follow List: " + error.getMessage());
                Toast.makeText(UserpageActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("writer", writer);
                params.put("write_follower",write_follower);
                params.put("user_follow",user_follow);
                params.put("user_name", user_name);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //알람보내기
    private void sendAlram(final String writer){
        // Tag used to cancel the request
        String tag_string_req = "req_pushScrapBtn";

        pDialog.setMessage("Update List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SENDALRAM+"?writer="+writer+"&type=2", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Scrap List: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject();
                    jObj.put("error",false);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(UserpageActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Scrap List: " + error.getMessage());
                Toast.makeText(UserpageActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("writer", writer);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



}

