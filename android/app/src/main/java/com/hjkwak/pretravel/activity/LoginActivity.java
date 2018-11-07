package com.hjkwak.pretravel.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.Fragment.MainActivity;
import com.hjkwak.pretravel.activity.data.MyList;
import com.hjkwak.pretravel.app.AppConfig;
import com.hjkwak.pretravel.app.AppController;
import com.hjkwak.pretravel.helper.SQLiteHandler;
import com.hjkwak.pretravel.helper.SessionManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hjkwak.pretravel.network.APIClient;
import com.hjkwak.pretravel.network.ApiProvider;
import com.hjkwak.pretravel.network.MyModel;
import com.hjkwak.pretravel.network.PretravelService;
import com.hjkwak.pretravel.util.Dlog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private CallbackManager callbackManager;
    String email="test";
    String name ="test";

    private ImageView flight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 페이스북 SDK 초기화
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        setContentView(R.layout.activity_login);

       callbackManager = CallbackManager.Factory.create();
      // LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "페이스북 토큰->" + loginResult.getAccessToken().getToken());
                Log.d("TAG", "페이스북 UserID->" + loginResult.getAccessToken().getUserId());
                // Create login session
                session.setLogin(true);

                // Inserting row in users table
                db.addUser(name, email, loginResult.getAccessToken().getUserId(), "0");
                getMycontents(name);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "취소됨");
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("TAG","페이스북 로그인 결과"+response.toString());
                try{
                    //email = object.getString("email");
                    name = object.getString("name");

                    //Log.d("TAG", "페이스북 이메일->" + email);
                    Log.d("TAG", "페이스북 이름->" + name);


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();



        flight = (ImageView)findViewById(R.id.flight);
        flight.setBackgroundResource(R.drawable.flight_change);
        AnimationDrawable frameAnimation = (AnimationDrawable) flight.getBackground();
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), // 현재화면 제어권자
                R.anim.mov_flight);      // 에니메이션 설정한 파일
        flight.startAnimation(anim);
        frameAnimation.setOneShot(true);
        frameAnimation.start();




        // 스플래시 처리 핸들러
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 엑세스 토큰 확인 부분 (추가)
                Boolean isValidAccessTocken = false;
                if (isValidAccessTocken) {
                    // 엑세스 토큰이 유효 할 경우 메인 액티비티로 이동
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    // 엑세스 토큰이 유효하지 않을 경우 로그인 액티비티로 이동
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                }
                // 스플래시 액티비티 종료
                finish();
            }
        }, SPLASH_TIME_OUT);
*/

    inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to home activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "이메일/비밀번호를 입력하세요", Toast.LENGTH_LONG).show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

}


    PretravelService pretravelService;
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        pretravelService = APIClient.getClient().create(PretravelService.class);
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        /**
         GET List Resources
         **/
        Call<ResponseBody> call = pretravelService.requestLogin(email,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.body().string());
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at);
                        getMycontents(name);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Login Error: " + t.getMessage());
                Toast.makeText(getApplicationContext(),
                        t.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }


        });
//        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Login Response: " + response.toString());
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//
//                    // Check for error node in json
//                    if (!error) {
//
//                        // user successfully logged in
//                        // Create login session
//                        session.setLogin(true);
//
//                        // Now store the user in SQLite
//                        String uid = jObj.getString("uid");
//
//                        JSONObject user = jObj.getJSONObject("user");
//                        String name = user.getString("name");
//                        String email = user.getString("email");
//                        String created_at = user.getString("created_at");
//
//                        // Inserting row in users table
//                        db.addUser(name, email, uid, created_at);
//                        getMycontents(name);
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        // Error in login. Get the error message
//                        String errorMsg = jObj.getString("error_msg");
//                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", email);
//                params.put("password", password);
//
//                return params;
//            }
//
//        };
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

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
                        Toast.makeText(getApplicationContext(), "동기화 성공!"+i, Toast.LENGTH_LONG).show();
                        // Launch home activity

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
                params.put("name", name);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
