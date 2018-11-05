package com.hjkwak.pretravel.activity.Posting;

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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.ListviewAdapter.CustomContentListAdapter;
import com.hjkwak.pretravel.activity.RegisterActivity;
import com.hjkwak.pretravel.activity.UserpageActivity;
import com.hjkwak.pretravel.activity.data.MyList;
import com.hjkwak.pretravel.app.AppConfig;
import com.hjkwak.pretravel.app.AppController;
import com.hjkwak.pretravel.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SeePostActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    int Recommend_state=0, Scrap_state=0;
    int position;
    int type;
    String writer;
    String country, city;
    ArrayList<MyList> data, recommendList;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    public static Activity SeePost;
    boolean isInList=false , isScrap=false;
    ArrayList<String> userRecommendList, scrapTitle, scrapUid; //추천한 사람 리스트 저장되는 배열
    int whereUser; //추천한 사람 리스트 배열 위치
    CustomContentListAdapter adapter;
    String myName;
    Button btnUp;
    Button btnScrap;
    TextView txtWriter;
    int whereScrapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_post);
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        final HashMap<String, String> user = db.getUserDetails();
        myName = user.get("name");
        SeePost = (SeePostActivity)SeePostActivity.this;

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        position = getIntent().getIntExtra("position", position);
        country = getIntent().getStringExtra("country");
        city = getIntent().getStringExtra("city");
        data = (ArrayList<MyList>)getIntent().getSerializableExtra("data");

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.thumbnail);
        TextView textTitle = (TextView) findViewById(R.id.title);
        TextView textMaker = (TextView) findViewById(R.id.rating);
        TextView textTime = (TextView) findViewById(R.id.genre);
        TextView textWriter=(TextView) findViewById(R.id.txtWriter);
        TextView txtRecommend =(TextView) findViewById(R.id.txtComment);
        TextView txtNewRecommend=(TextView) findViewById(R.id.txtNewRecommend);
        txtWriter = (TextView)findViewById(R.id.txtWriter);
        ListView listviewRecommendList=(ListView) findViewById(R.id.listviewRecommend);
        btnScrap = (Button)findViewById(R.id.btnScrap);
        Button btnToList =(Button) findViewById(R.id.btnToList);
        btnUp= (Button)findViewById(R.id.btnUp);

        txtRecommend.setText(data.get(position).getCon_data4()); //추천내용 입력
        writer=getIntent().getStringExtra("user_name"); //글쓴이 입력
        textWriter.setText("글쓴이: " + writer);


        final MyList m = data.get(position);

        // thumbnail image
        final String photo = m.getThumbnailUrl();
        thumbNail.setImageUrl(photo, imageLoader);

        // title
        final String title=data.get(position).getTitle();
        textTitle.setText(title);

        // 작가/감독 , 출판사
        final String data2 =data.get(position).getGenre();
        textMaker.setText(data2);

        // 장르, 시간
        final String data3 = data.get(position).getCon_data3();
        textTime.setText(data3);


        //리스트뷰 설정
        recommendList =new ArrayList<>();
        adapter = new CustomContentListAdapter(SeePostActivity.this, recommendList);
        setData();  //리스트뷰에 데이터 삽입
        listviewRecommendList.setAdapter(adapter);
        if(city.equals("null")||city.equals(""))
            txtNewRecommend.setText(writer + "님이 추천한 리스트 for " + country);
        else
            txtNewRecommend.setText(writer + "님이 추천한 리스트 for " + country + " " + city);

        listviewRecommendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent toEdit = new Intent(SeePostActivity.this, SeePostActivity.class);
                toEdit.putExtra("data", recommendList);
                toEdit.putExtra("position", position);
                toEdit.putExtra("city", city);
                toEdit.putExtra("country", country);
                toEdit.putExtra("user_name", writer);
                startActivity(toEdit);
                SeePost.finish();
            }
        });

        btnToList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SeePost.finish();
                finish();
            }
        });

        txtWriter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent toOtheruser = new Intent(SeePostActivity.this, UserpageActivity.class);
                toOtheruser.putExtra("writer",writer);
                startActivity(toOtheruser);
                finish();
            }
        });

        //추천 상태 확인
        userRecommendList=new ArrayList<String>();
        userRecommendList.clear();
        getRecommendUserList(m.getText_uid(), m.getTitle(), userRecommendList);

            //추천버튼 클릭시
        btnUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (Recommend_state == 0) { //추천하기
                    btnUp.setBackground(getResources().getDrawable(R.drawable.btn_green));
                    userRecommendList.add(myName);
                    pushRecommend(m.getText_uid(), m.getTitle(), userRecommendList.toString(), userRecommendList.size());
                    Toast.makeText(SeePostActivity.this, "추천했습니다.", Toast.LENGTH_LONG).show();
                    type=1;
                    sendAlram(writer);
                    Recommend_state++;
                } else {
                    btnUp.setBackground(getResources().getDrawable(R.drawable.btn_round));
                    userRecommendList.remove(myName);
                    pushRecommend(m.getText_uid(), m.getTitle(), userRecommendList.toString(), userRecommendList.size());
                    Toast.makeText(SeePostActivity.this, "추천을 취소했습니다.", Toast.LENGTH_LONG).show();
                    Recommend_state--;
                    isInList=false;
                }
            }
        });

        scrapTitle = new ArrayList<>();
        scrapTitle.clear();
        scrapUid = new ArrayList<>();
        scrapUid.clear();
        getScrapList( myName, m.getText_uid(), m.getTitle());
        btnScrap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(Scrap_state==0) {
                    btnScrap.setBackground(getResources().getDrawable(R.drawable.btn_pink));
                    scrapTitle.add(m.getTitle());
                    scrapUid.add(m.getText_uid());
                    pushScrap(scrapUid.toString(), scrapTitle.toString(),myName);
                    Toast.makeText(SeePostActivity.this, "스크랩되었습니다.", Toast.LENGTH_SHORT).show();
                    Scrap_state++;
                }
                else{
                    btnScrap.setBackground(getResources().getDrawable(R.drawable.btn_round));
                    scrapTitle.remove((int)whereScrapName);
                    scrapUid.remove((int)whereScrapName);
                    whereScrapName=scrapTitle.size();
                    pushScrap(scrapUid.toString(), scrapTitle.toString(),myName);
                    Toast.makeText(SeePostActivity.this, "스크랩취소", Toast.LENGTH_SHORT).show();
                    Scrap_state--;
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


    //추천인 목록 업데이트
    private void pushRecommend(final String text_uid, final String con_title, final String recommendList, final int size){
        // Tag used to cancel the request
        String tag_string_req = "req_pushRecommendBtn";

        pDialog.setMessage("Update List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PUSHRECOMMEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Recommend List: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in server data
                        //Toast.makeText(SeePostActivity.this, userList[0]+isInList+userList[0].equals(user.get("name")), Toast.LENGTH_LONG).show();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(SeePostActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Recommend List: " + error.getMessage());
                Toast.makeText(SeePostActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("text_uid", text_uid);
                params.put("con_title",con_title);
                params.put("user",recommendList);
                params.put("Recommend_state", String.valueOf(size));
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    //추천인목록 가져오기
    private void getRecommendUserList(final String text_uid, final String con_title, final ArrayList<String> userRecommendList){
        // Tag used to cancel the request
        String tag_string_req = "req_RecommendUserList";

        pDialog.setMessage("Getting List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GETRECOMMENDUSERLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Getting Recommend List: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String recommendUserList=jObj.getString("recommendList").replace("[","").replace("]","").replace(" ", "");
                        String[] userList= recommendUserList.split(",");
                        for(int i=0; i<userList.length;i++){
                            userRecommendList.add(userList[i]);
                            if(userList[i].equals(myName)==true){
                                isInList=true;
                                btnUp.setBackground(getResources().getDrawable(R.drawable.btn_green));
                                Recommend_state=1;
                            }
                            else {
                                btnUp.setBackground(getResources().getDrawable(R.drawable.btn_round));
                                isInList = false;
                            }
                        }
                        userRecommendList.remove(0);

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(SeePostActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Getting Recommend List: " + error.getMessage());
                Toast.makeText(SeePostActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("text_uid", text_uid);
                params.put("con_title",con_title);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /*DATA SETTING*/
    private void setData(){
        // Tag used to cancel the request
        String tag_string_req = "req_setData";

        pDialog.setMessage("Setting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_RECOMMENDLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Setting Data: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in server data
                        for(int i=0; i<5 ; i++) {
                            String text_uid = jObj.getJSONArray("text_uid").getString(i);
                            String country = jObj.getJSONArray("country").getString(i);
                            String city = jObj.getJSONArray("city").getString(i);
                            String con_title = jObj.getJSONArray("con_title").getString(i);
                            String con_data1 = jObj.getJSONArray("con_data1").getString(i).trim();
                            String con_data2 = jObj.getJSONArray("con_data2").getString(i);
                            String con_data3 = jObj.getJSONArray("con_data3").getString(i);
                            String con_data4 = jObj.getJSONArray("con_data4").getString(i);
                            String con_photo = jObj.getJSONArray("con_photo").getString(i);
                            int recommend = jObj.getJSONArray("recommend").getInt(i);
                            String created_at=jObj.getJSONArray("created_at").getString(i);
                            if(!con_title.equals(data.get(position).getTitle()))
                                recommendList.add(new MyList(text_uid,country,city,con_title,con_data1,con_data2,con_data3,con_data4,con_photo,recommend,created_at));
                        }
                        //adapter.notifyDataSetChanged();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(SeePostActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Setting Data: " + error.getMessage());
                Toast.makeText(SeePostActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                if(city!="null") params.put("city", city);
                else params.put("city","없다");
                params.put("country", country);
                params.put("user_name",writer);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //스크랩리스트 가져오기
    private void getScrapList(final String myName, final String text_uid, final String con_title){
        // Tag used to cancel the request
        String tag_string_req = "req_ScrapList";

        pDialog.setMessage("Getting List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GETSCRAPLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Getting Scrap List: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String scrapUidList=jObj.getString("scrap_uid").replace("[", "").replace("]","").replace(" ","");
                        String[] scrap_uid= scrapUidList.split(",");
                        String scrapTitleList=jObj.getString("scrap_title").replace("[", "").replace("]","").trim();
                        String[] scrap_title= scrapTitleList.split(",");
                        for(int i=0; i<scrap_uid.length;i++){
                            scrapTitle.add(scrap_title[i].trim());
                            scrapUid.add(scrap_uid[i]);

                            if(scrap_title[i].trim().equals(con_title.trim())==true && scrap_uid[i].equals(text_uid.replace(" ",""))==true){
                                isScrap=true;
                                whereScrapName=i;
                                btnScrap.setBackground(getResources().getDrawable(R.drawable.btn_pink));
                                Scrap_state=1;
                            }
                            else {
                                //btnScrap.setBackground(getResources().getDrawable(R.drawable.btn_round));
                                isScrap=false;
                            }
                        }
                        //Toast.makeText(SeePostActivity.this, scrapTitle.toString()+"///"+scrapUid.toString()+"//"+isScrap, Toast.LENGTH_LONG).show();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(SeePostActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Getting Scrap List: " + error.getMessage());
                Toast.makeText(SeePostActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name", myName);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    //스크랩 목록 업데이트
    private void pushScrap(final String scrap_uid, final String scrap_title, final String name){
        // Tag used to cancel the request
        String tag_string_req = "req_pushScrapBtn";

        pDialog.setMessage("Update List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PUSHSCRAP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Scrap List: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        db.editMyScraplist(scrap_title, scrap_uid);
                        // User successfully stored in MySQL
                        // Now store the user in server data
                        //Toast.makeText(SeePostActivity.this, userList[0]+isInList+userList[0].equals(user.get("name")), Toast.LENGTH_LONG).show();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(SeePostActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Scrap List: " + error.getMessage());
                Toast.makeText(SeePostActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("scrap_uid", scrap_uid);
                params.put("scrap_title",scrap_title);
                params.put("user_name", name);
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

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SENDALRAM+"?writer="+writer+"&type="+type, new Response.Listener<String>() {
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
                        Toast.makeText(SeePostActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Scrap List: " + error.getMessage());
                Toast.makeText(SeePostActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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



