package com.example.hyojin.myapplication.activity.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.example.hyojin.myapplication.R;
import com.example.hyojin.myapplication.activity.Posting.SeePostActivity;
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

/**
 * Created by Hyojin on 2016-05-06.
 */
public class HomeFragment extends Fragment{
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    NetworkImageView movie, book,movie2,book2;
    ImageLoader imageLoader;
    ArrayList<MyList>  best= new ArrayList<>();

    String BestURL=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

    // SqLite database handler
        db = new SQLiteHandler(getContext());
        HashMap<String,String> user = db.getUserDetails();
        final String name = user.get("name");

        // session manager
        session = new SessionManager(getContext());

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);


        imageLoader = AppController.getInstance().getImageLoaderForHome();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoaderForHome();
        movie = (NetworkImageView) v.findViewById(R.id.movie);
        book = (NetworkImageView) v.findViewById(R.id.book);
        movie2 = (NetworkImageView)v.findViewById(R.id.movie2);
        book2 = (NetworkImageView) v.findViewById(R.id.book2);

        // thumbnail image
        getHighRecommend();
        book2.setImageUrl("https://t1.search.daumcdn.net/thumb/R110x160/?fname=http%3A%2F%2Ft1.daumcdn.net%2Fbook%2FKOR9788952231833%3Fmoddttm=20160701060327",
                imageLoader);
        movie2.setImageUrl("http://t1.search.daumcdn.net/thumb/R438x0.q85/?fname=http%3A%2F%2Fcfile189.uf.daum.net%2Fimage%2F110B83234A3F1C04EA1F3C",
                imageLoader);

        //Posting click event
        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEdit = new Intent(getActivity(), SeePostActivity.class);
                toEdit.putExtra("data", best);
                toEdit.putExtra("position", 0);
                toEdit.putExtra("city", best.get(0).getCity());
                toEdit.putExtra("country", best.get(0).getCountry());
                toEdit.putExtra("user_name", name);
                startActivity(toEdit);           }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEdit = new Intent(getActivity(), SeePostActivity.class);
                toEdit.putExtra("data", best);
                toEdit.putExtra("position", 1);
                toEdit.putExtra("city", best.get(1).getCity());
                toEdit.putExtra("country", best.get(1).getCountry());
                toEdit.putExtra("user_name", name);
                startActivity(toEdit);           }
        });

        return v;
    }


    //추천수 높은 컨텐츠 가져오기
    private void getHighRecommend(){
        // Tag used to cancel the request
        String tag_string_req = "req_RecommendUserList";

        pDialog.setMessage("Getting List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GETHIGH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Getting Recommend List: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}")+1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String text_uid = jObj.getString("text_uid");
                        String country = jObj.getString("country");
                        String city = jObj.getString("city");
                        String con_title = jObj.getString("con_title");
                        String con_data1 = jObj.getString("con_data1");
                        String con_data2 = jObj.getString("con_data2");
                        String con_data3 = jObj.getString("con_data3");
                        String con_data4 = jObj.getString("con_data4");
                        String con_photo = jObj.getString("con_photo");

                        String text_uid_2 = jObj.getString("text_uid_2");
                        String country_2 = jObj.getString("country_2");
                        String city_2 = jObj.getString("city_2");
                        String con_title_2 = jObj.getString("con_title_2");
                        String con_data1_2 = jObj.getString("con_data1_2");
                        String con_data2_2 = jObj.getString("con_data2_2");
                        String con_data3_2 = jObj.getString("con_data3_2");
                        String con_data4_2 = jObj.getString("con_data4_2");
                        String con_photo_2 = jObj.getString("con_photo_2");
                        movie.setImageUrl(con_photo, imageLoader);
                        book.setImageUrl(con_photo_2, imageLoader);
                        best.add(new MyList(text_uid, country, city, con_title, con_data1, con_data2, con_data3, con_data4, con_photo, 0, null));
                        best.add(new MyList(text_uid_2,country_2,city_2,con_title_2,con_data1_2,con_data2_2,con_data3_2,con_data4_2,con_photo_2,0,null));
                        //Toast.makeText(getActivity(), best.get(0).getTitle(), Toast.LENGTH_LONG).show();

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
                Log.e(TAG, "Getting Recommend List: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
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



}
