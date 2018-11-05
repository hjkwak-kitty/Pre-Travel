package com.hjkwak.pretravel.activity.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.ListviewAdapter.CustomContentListAdapter;
import com.hjkwak.pretravel.activity.Posting.SeePostActivity;
import com.hjkwak.pretravel.activity.RegisterActivity;
import com.hjkwak.pretravel.activity.data.MyList;
import com.hjkwak.pretravel.app.AppConfig;
import com.hjkwak.pretravel.app.AppController;
import com.hjkwak.pretravel.helper.SQLiteHandler;
import com.hjkwak.pretravel.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hyojin on 2016-05-06.
 */
public class SearchFragment extends Fragment{
    private static final String TAG = RegisterActivity.class.getSimpleName();
    EditText inputSearch;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Button btnSearch;
    private Button btnBook, btnMovie, btnFavorite;
    private ListView listviewSearchResult;
    CustomContentListAdapter adapter;
    String country;
    String city;
    String myname;
    ArrayList<MyList> allList, bookList, movieList;
    ArrayList<String> user_name = new ArrayList();
    int favorite_state=0, whereFavorite;
    boolean isFavorite=false;
    int state=0;// 즐겨찾기 상태
    OnHeadlineSelectedListener mCallback;
    ArrayList<String> favoriteCountry = new ArrayList<>(), favoriteCity= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);
        inputSearch=(EditText)v.findViewById(R.id.inputSearch);
        btnSearch=(Button)v.findViewById(R.id.btnSearch);
        btnBook=(Button)v.findViewById(R.id.btnBook);
        btnMovie=(Button)v.findViewById(R.id.btnMovie);
        btnFavorite=(Button)v.findViewById(R.id.Btnfavorite);
        listviewSearchResult = (ListView) v.findViewById(R.id.listviewSerachResult);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getContext());

        // SQLite database handler
        db = new SQLiteHandler(getContext());



        allList=new ArrayList<MyList>();
        bookList=new ArrayList<MyList>();
        movieList=new ArrayList<MyList>();
        adapter = new CustomContentListAdapter(getActivity(), allList);

        listviewSearchResult.setAdapter(adapter);

        state = mCallback.getState();
        if(state==1){
            country=mCallback.getCountry();
            city=mCallback.getCity();
            setData(country,city);
        }

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allList.clear();
                allList.addAll(bookList);
                adapter.notifyDataSetChanged();
            }
        });

        btnMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allList.clear();
                allList.addAll(movieList);
                adapter.notifyDataSetChanged();
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favorite_state == 0) { //추천하기
                    btnFavorite.setBackground(getResources().getDrawable(R.drawable.btn_favoriteon));
                    favoriteCountry.add(country);
                    favoriteCity.add(city);
                    pushFavorite(favoriteCountry.toString(), favoriteCity.toString(), myname);
                    Toast.makeText(getActivity(), "즐겨찾기추가", Toast.LENGTH_LONG).show();
                    favorite_state++;
                } else {
                    btnFavorite.setBackground(getResources().getDrawable(R.drawable.btn_favorite));
                    favoriteCountry.remove(whereFavorite);
                    favoriteCity.remove(whereFavorite);
                    pushFavorite(favoriteCountry.toString(), favoriteCity.toString(), myname);
                    Toast.makeText(getActivity(), "즐겨찾기삭제", Toast.LENGTH_LONG).show();
                    favorite_state--;
                    isFavorite=false;
                }

            }
        });
        //Posting click event
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String where = inputSearch.getText().toString().trim();
                if (!where.isEmpty()) {
                    checkWhere(where);
                } else {
                    Toast.makeText(getContext(), "장소를 입력해 주세요", Toast.LENGTH_LONG).show();
                }
            }
        });

        listviewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent toEdit = new Intent(getActivity(), SeePostActivity.class);
                toEdit.putExtra("data", allList);
                toEdit.putExtra("position", position);
                toEdit.putExtra("city", city);
                toEdit.putExtra("country", country);
                toEdit.putExtra("user_name",user_name.get(position));
                startActivity(toEdit);
            }
        });

        return v;
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
                        String myplace;
                        if(city.equals("")||city.equals("null"))
                            myplace= country;
                        else
                            myplace = country+" "+city;
                        confirmWhere(myplace);

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Checking Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void confirmWhere(final String myplace) {
        CharSequence[] where = {myplace, "추가하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("장소 확인");

        builder.setItems(where, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        setData(country, city);
                        break;
                    case 1:
                        View dialog2 = View.inflate(getActivity(), R.layout.pop_add, null);
                        final AlertDialog ad = new AlertDialog.Builder(getActivity()).setView(dialog2).create();
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

                        setData(country, city);
                        Toast.makeText(getActivity(), "추가성공", Toast.LENGTH_LONG).show();

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

    /*서버 데이터 검색*/
    private void searchContents(){
        // Tag used to cancel the request
        String tag_string_req = "req_getSomeContents";

        pDialog.setMessage("Checking ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "upload contents: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        allList.clear();
                        movieList.clear();
                        bookList.clear();
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
                            user_name.add(user);
                            allList.add(new MyList(text_uid,country,city,con_title,con_data1,con_data2,con_data3,con_data4,con_photo,0,null));
                            if(con_data1.equals("book")) bookList.add(new MyList(text_uid,country,city,con_title,con_data1,con_data2,con_data3,con_data4,con_photo,0,null));
                            else if(con_data1.equals("movie")) movieList.add(new MyList(text_uid,country,city,con_title,con_data1,con_data2,con_data3,con_data4,con_photo,0,null));
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "성공!"+i, Toast.LENGTH_LONG).show();

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
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                if(city!="null") params.put("city", city);
                else params.put("city","없다");
                params.put("country", country);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void setData(String country, String city){
        String myplace;
        HashMap<String,String> myinfo = db.getUserDetails();
        myname = myinfo.get("name");
        favoriteCity.clear();
        favoriteCountry.clear();
        btnFavorite.setBackgroundColor(Color.LTGRAY);
        btnFavorite.setTextColor(Color.BLACK);
        favorite_state=0;
        getFavoriteList(myname,country,city);
        if(city!="null") myplace= city;
        else myplace=country;
        inputSearch.setText(myplace);
        searchContents();
    }

    //스크랩리스트 가져오기
    private void getFavoriteList(final String myName, final String country, final String city){
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
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String favoriteContryList=jObj.getString("favorite_country").replace("[", "").replace("]","").replace(" ","").toLowerCase();
                        String[] favorite_country= favoriteContryList.split(",");
                        String favoriteCityList=jObj.getString("favorite_city").replace("[", "").replace("]","").replace(" ","").toLowerCase();
                        String[] favorite_city= favoriteCityList.split(",");
                        for(int i=0; i<favorite_city.length;i++){favoriteCountry.add(favorite_country[i]);
                            favoriteCity.add(favorite_city[i]);
                            //Toast.makeText(getActivity(), favorite_city[i]+" "+i+" "+city.replace(" ","").toLowerCase(), Toast.LENGTH_LONG).show();
                            if(favorite_city[i].equals(city.replace(" ","").toLowerCase())==true && favorite_country[i].equals(country.replace(" ","").toLowerCase())==true){
                                isFavorite=true;
                                whereFavorite=i;
                                btnFavorite.setBackground(getResources().getDrawable(R.drawable.btn_favoriteon));
                                favorite_state=1;
                            }
                            else {
                                btnFavorite.setBackground(getResources().getDrawable(R.drawable.btn_favorite));
                                isFavorite=false;
                            }
                        }
                        //Toast.makeText(getActivity(), favoriteCityList+" "+ isFavorite, Toast.LENGTH_LONG).show();
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


    //FAVORITE 목록 업데이트
    private void pushFavorite(final String favorite_country, final String favorite_city, final String name){
        // Tag used to cancel the request
        String tag_string_req = "req_pushFavoriteBtn";

        pDialog.setMessage("Update List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PUSHFAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Favorite List: " + response.toString());
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
                params.put("favorite_country", favorite_country);
                params.put("favorite_city",favorite_city);
                params.put("user_name", name);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public interface OnHeadlineSelectedListener {
        public String getCountry();
        public String getCity();
        public int getState();
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
}
