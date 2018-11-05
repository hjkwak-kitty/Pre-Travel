package com.hjkwak.pretravel.activity.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.LoginActivity;
import com.hjkwak.pretravel.activity.RegisterActivity;
import com.hjkwak.pretravel.activity.setting.AlramSettingActivity;
import com.hjkwak.pretravel.app.AppConfig;
import com.hjkwak.pretravel.app.AppController;
import com.hjkwak.pretravel.helper.SQLiteHandler;
import com.hjkwak.pretravel.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hyojin on 2016-05-06.
 */
public class InfoFragment extends Fragment {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    Button btnTest;
    Button btnRemoveUser;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    String user_name;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        btnTest = (Button)v.findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AlramSettingActivity.class);
                startActivity(i);
            }
        });

        // SqLite database handler
        db = new SQLiteHandler(getContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        user_name = user.get("name");


        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);


        // session manager
        session = new SessionManager(getContext());


        btnRemoveUser=(Button)v.findViewById(R.id.btnRemoveUser);
        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                // 제목셋팅
                alertDialogBuilder.setTitle("탈퇴하기");

                // AlertDialog 셋팅
                alertDialogBuilder.setMessage("진짜로 정말로 탈퇴하시겠습니까?").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 리스트삭제
                        session.setLogin(false);
                        db.deleteUsers();

                        removeUser();
                        // Launching the login activity
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
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
        });


    return v;
    }


    //탈퇴하기
    private void removeUser(){
        // Tag used to cancel the request
        String tag_string_req = "req_pushScrapBtn";

        pDialog.setMessage("Update List ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REMOVEUSER+"?user_name="+user_name, new Response.Listener<String>() {
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
