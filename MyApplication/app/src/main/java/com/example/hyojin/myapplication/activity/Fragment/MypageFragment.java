package com.example.hyojin.myapplication.activity.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.activity.ListviewAdapter.CustomContentListAdapter;
import com.example.hyojin.myapplication.activity.ListviewAdapter.CustomMylistAdapter;
import com.example.hyojin.myapplication.activity.LoginActivity;
import com.example.hyojin.myapplication.activity.Posting.PostListActivity;
import com.example.hyojin.myapplication.activity.Posting.SeePostActivity;
import com.example.hyojin.myapplication.activity.RegisterActivity;
import com.example.hyojin.myapplication.activity.UserpageActivity;
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

/**
 * Created by Hyojin on 2016-05-06.
 */
public class MypageFragment extends Fragment {

    private TextView txtName;
    private Button btnLogout;
    private Button btnPosting;
    private Button btnMyPost;
    private Button btnScrap;
    private Button btnFavorite;
    private Button btnFollow;
    private ListView listviewMypost;
    ArrayList<MyList> mine, scrap;
    String name;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private SessionManager session;
    CustomMylistAdapter myListAdapter;
    int EDIT;
    int scrap_state=1;
    OnHeadlineSelectedListener mCallback;
    ArrayList<String> listWriterFollow=new ArrayList<>();


    ArrayList<String> favoriteCountry = new ArrayList<>(), favoriteCity= new ArrayList<>();
    ArrayList<String> favorite_data= new ArrayList<String>();
    ArrayAdapter adapter_favorite, adapter_follow;


    private static final String TAG = RegisterActivity.class.getSimpleName();
    ArrayList<String> scrapTitle,scrapUid;
    ArrayAdapter<String> favorite_adapter;
    CustomContentListAdapter m_adapter;
    MainActivity main = new MainActivity();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mypage, container, false);
        txtName = (TextView) v.findViewById(R.id.name);
        btnLogout = (Button) v.findViewById(R.id.btnLogout);
        btnPosting = (Button) v.findViewById(R.id.btnPosting);
        btnMyPost =(Button) v.findViewById(R.id.btnMyPost);
        btnScrap=(Button)v.findViewById(R.id.btnScrap);
        btnFavorite = (Button)v.findViewById(R.id.btnFavorite);
        btnFollow =(Button)v.findViewById(R.id.btnFollow);
        listviewMypost = (ListView)v.findViewById(R.id.listviewMypost);

        // SqLite database handler
        db = new SQLiteHandler(getContext());

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);


        // session manager
        session = new SessionManager(getContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        name = user.get("name");
        String email = user.get("email");


        //동기화
        getMycontents(name);
        scrapTitle=new ArrayList<String>();
        scrapUid = new ArrayList<String>();
        getScrapList(name);


        // Displaying the user details on the screen
        txtName.setText(name);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        //Posting click event
        btnPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PostListActivity.class);
                i.putExtra("EDIT", 0);
                startActivity(i);
            }
        });


        //Favorite 버튼 클릭 이벤트 즐찾
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFavoriteList(name);
                adapter_favorite= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,favorite_data);
                listviewMypost.setAdapter(adapter_favorite);
                listviewMypost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        mCallback.setState(1, favoriteCountry.get(position + 1), favoriteCity.get(position + 1));
                        mCallback.fragmentReplace(2);
                    }
                });

            }
        });

        //Follow 버튼 클릭 이벤트
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFollowingUserList(name, name);
                adapter_follow= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listWriterFollow);
                listviewMypost.setAdapter(adapter_follow);
                listviewMypost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Intent toOtheruser = new Intent(getActivity(), UserpageActivity.class);
                        toOtheruser.putExtra("writer",listWriterFollow.get(position));
                        startActivity(toOtheruser);
                    }
                });

            }
        });

        //스크랩버튼 클릭 이벤트
        scrap = new ArrayList<MyList>();
        scrap.clear();
        btnScrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scrap_state == 1) {
                    for (int i = 0; i < scrapUid.size(); i++) {
                        //Toast.makeText(getActivity(), scrap.size() + "!" + i, Toast.LENGTH_SHORT).show();
                        searchContent(scrapUid.get(i).trim(), scrapTitle.get(i).trim());
                    }
                    scrap_state++;
                }
                m_adapter = new CustomContentListAdapter(getActivity(), scrap);
                listviewMypost.setAdapter(m_adapter);
                listviewMypost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        CharSequence[] what = {"보기", "삭제"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("무엇을하시겠습니까?");

                        builder.setItems(what, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if (scrap.get(position).getCity() != null) {
                                            Intent toEdit = new Intent(getActivity(), SeePostActivity.class);
                                            toEdit.putExtra("data", scrap);
                                            toEdit.putExtra("position", position);
                                            toEdit.putExtra("city", scrap.get(position).getCity());
                                            toEdit.putExtra("country", scrap.get(position).getCountry());
                                            toEdit.putExtra("user_name", name);
                                            startActivity(toEdit);
                                        } else
                                            Toast.makeText(getActivity(), "삭제된 게시물 입니다.", Toast.LENGTH_LONG).show();
                                        break;
                                    case 1:
                                        checkYesNoScrap(position);
                                        break;
                                }
                                //shareAlert.dismiss();
                            }
                        });
                        final AlertDialog shareAlert = builder.create();
                        shareAlert.show();
                    }
                });

            }
        });


        btnMyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mine = db.getMyContentsDetails();
                getMycontents(name);
                myListAdapter = new CustomMylistAdapter(getActivity(), mine);
                listviewMypost.setAdapter(myListAdapter);
                listviewMypost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        CharSequence[] what = {"수정", "삭제"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("무엇을하시겠습니까?");

                        builder.setItems(what, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(getActivity(), PostListActivity.class);
                                        intent.putExtra("EDIT", 2);
                                        intent.putExtra("text_uid", mine.get(position).getText_uid());
                                        intent.putExtra("country", mine.get(position).getCountry());
                                        intent.putExtra("city", mine.get(position).getCity());
                                        startActivity(intent);
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
            }
        });

        return v;
    }

    /*삭제하시겠습니까 확인창(내글리스트)*/
    private void checkYesNo(final int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // 제목셋팅
        alertDialogBuilder.setTitle("리스트 삭제");

        // AlertDialog 셋팅
        alertDialogBuilder.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 리스트삭제
                db.deleteConetentsUUid(mine.get(position).getText_uid());
                deleteContents(mine.get(position).getText_uid());
                mine.remove(position);
                myListAdapter.notifyDataSetChanged();

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

    /*삭제하시겠습니까 확인창(스크랩리스트)*/
    private void checkYesNoScrap(final int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // 제목셋팅
        alertDialogBuilder.setTitle("리스트 삭제");

        // AlertDialog 셋팅
        alertDialogBuilder.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 리스트삭제
                scrapTitle.remove(position);
                scrapUid.remove(position);
                scrap.remove(position);
                deleteScrapList(scrapUid.toString(),scrapTitle.toString(),name);
                m_adapter.notifyDataSetChanged();

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

    //스크랩 목록 삭제
    private void deleteScrapList(final String scrap_uid, final String scrap_title, final String name){
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
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Scrap List: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_LONG).show();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Checking Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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



    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void getMycontents(final String name){
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
                        ArrayList<MyList> mylist = new ArrayList<>();
                        db.deleteContents();
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
                            db.addContents(text_uid, country, city, con_title, con_data1, con_data2, con_data3,con_data4, con_photo,recommend,created_at);
                            mylist.add(new MyList(text_uid, country, city, con_title, con_data1, con_data2, con_data3, con_data4, con_photo,recommend,created_at));
                        }
                        //Toast.makeText(getActivity(), "동기화 성공!"+i, Toast.LENGTH_LONG).show();


                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        db.deleteContents();
                        //String errorMsg = jObj.getString("error_msg");
                        //Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Checking Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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


    //스크랩리스트 가져오기
    private void getScrapList(final String myName){
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
                        String scrapUidList=jObj.getString("scrap_uid").replace("[", "").replace("]","").replace(" ", "");
                        String[] scrap_uid= scrapUidList.split(",");
                        String scrapTitleList=jObj.getString("scrap_title").replace("[", "").replace("]","").trim();
                        String[] scrap_title= scrapTitleList.split(",");
                        for(int i=0; i<scrap_uid.length;i++){
                            scrapTitle.add(scrap_title[i].trim());
                            scrapUid.add(scrap_uid[i]);
                        }
                        //Toast.makeText(getActivity(), scrapTitle.toString()+"///"+scrapUid.toString()+"//", Toast.LENGTH_LONG).show();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Getting Scrap List: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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


    /*서버 데이터 검색*/
    private void searchContent(final String text_uid, final String con_title){
        // Tag used to cancel the request
        String tag_string_req = "req_getAContent";

        pDialog.setMessage("Checking ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GETSCRAPCONTENT, new Response.Listener<String>() {
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
                            String text_uid = jObj.getString("text_uid");
                            String country = jObj.getString("country");
                            String city = jObj.getString("city");
                            String con_title = jObj.getString("con_title");
                            String con_data1 = jObj.getString("con_data1");
                            String con_data2 = jObj.getString("con_data2");
                            String con_data3 = jObj.getString("con_data3");
                            String con_data4 = jObj.getString("con_data4");
                            String con_photo = jObj.getString("con_photo");
                        String created_at= jObj.getString("created_at");
                        int recommend = jObj.getInt("recommend");
                        scrap.add(new MyList(text_uid, country, city, con_title, con_data1, con_data2, con_data3, con_data4, con_photo, recommend, created_at));
                        //Toast.makeText(getActivity(), text_uid+con_title+scrap.size(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        //String errorMsg = jObj.getString("error_msg");
                        scrap.add(new MyList(text_uid, null, null, con_title, "데이터없음", "삭제된 목록입니다.", null, null, null, 0, null));
                        //Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Checking Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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

    public interface OnHeadlineSelectedListener {
        public void fragmentReplace(int reqNewFragmentIndex);
        public void setState(int state, String country, String city);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement OnHeadlineSelectedListener");
       }
    }

    //즐겨찾기 리스트 가져오기
    private void getFavoriteList(final String myName){
        // Tag used to cancel the request
        String tag_string_req = "req_FavoriteList";

        pDialog.setMessage("Getting List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GETFAVORITELIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Getting Favorite List: " + response.toString());
                hideDialog();

                try {
                    favoriteCountry.clear();
                    favoriteCity.clear();
                    favorite_data.clear();
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String favoriteContryList=jObj.getString("favorite_country").replace("[", "").replace("]","").replace(" ","").toLowerCase();
                        String[] favorite_country= favoriteContryList.split(",");
                        String favoriteCityList=jObj.getString("favorite_city").replace("[", "").replace("]","").replace(" ","").toLowerCase();
                        String[] favorite_city= favoriteCityList.split(",");
                        for(int i=0; i<favorite_city.length;i++){
                            favoriteCountry.add(favorite_country[i]);
                            favoriteCity.add(favorite_city[i]);
                        }
                        for(int i=0;i<favoriteCity.size()-1;i++){
                            if(favoriteCity.get(i+1).equals("")||favoriteCity.get(i+1).equals("null"))
                                favorite_data.add(favoriteCountry.get(i + 1));
                            else
                                favorite_data.add(favoriteCountry.get(i + 1) + " " + favoriteCity.get(i + 1));
                        }
                        adapter_favorite.notifyDataSetChanged();
                        //Toast.makeText(getActivity(), favoriteCityList, Toast.LENGTH_LONG).show();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Getting Favorite List: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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
                        listWriterFollow.clear();
                        String followingList = jObj.getString("following").replace("[","").replace("]","").replace(" ", "");
                        String[] myFollowingList= followingList.split(",");
                        String followList=jObj.getString("follow").replace("[","").replace("]","").replace(" ", "");
                        String[] WriterFollowList= followList.split(",");
                        //Toast.makeText(UserpageActivity.this,followingList+followList, Toast.LENGTH_LONG).show();

                        //for(int i=0; i<myFollowingList.length;i++){
                        //    listMyFollowing.add(myFollowingList[i]);
                        //}
                        for(int i=0; i<WriterFollowList.length;i++){
                            listWriterFollow.add(WriterFollowList[i]);
                        }
                        listWriterFollow.remove(0);
                        adapter_follow.notifyDataSetChanged();
                        //Toast.makeText(UserpageActivity.this, listMyFollowing.toString()+"\n"+listWriterFollow.toString(), Toast.LENGTH_LONG).show();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Getting Follow List: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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


}
