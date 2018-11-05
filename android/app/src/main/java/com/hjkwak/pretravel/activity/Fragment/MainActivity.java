package com.hjkwak.pretravel.activity.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hjkwak.pretravel.R;
import com.hjkwak.pretravel.activity.LoginActivity;
import com.hjkwak.pretravel.helper.SQLiteHandler;
import com.hjkwak.pretravel.helper.SessionManager;

public class MainActivity extends FragmentActivity implements OnClickListener, MypageFragment.OnHeadlineSelectedListener,SearchFragment.OnHeadlineSelectedListener{
    final String TAG = "MainActivity";
    int mCurrentFragmentIndex;
    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_MYPAGE = 1;
    public static final int FRAGMENT_SEARCH = 2;
    public static final int FRAGMENT_INFO =3;
//    public static final int FRAGMENT_SETTING=4;
    private SessionManager session;
    private SQLiteHandler db;
    String country, city;
    int state=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(this);
        Button btnMypage = (Button) findViewById(R.id.btnMypage);
        btnMypage.setOnClickListener(this);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        Button btnInfo = (Button) findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(this);


        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        mCurrentFragmentIndex = FRAGMENT_HOME;
        fragmentReplace(mCurrentFragmentIndex);
    }

    public void fragmentReplace(int reqNewFragmentIndex) {

        Fragment newFragment = null;
        Log.d(TAG, "fragmentReplace " + reqNewFragmentIndex);
        newFragment = getFragment(reqNewFragmentIndex);

        // replace fragment
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ll_fragment, newFragment);

        // Commit the transaction
        transaction.commit();

    }

    private Fragment getFragment(int idx) {
        Fragment newFragment = null;

        switch (idx) {
            case FRAGMENT_HOME:
                newFragment = new HomeFragment();
                break;
            case FRAGMENT_MYPAGE:
                newFragment = new MypageFragment();
                break;
            case FRAGMENT_SEARCH:
                newFragment = new SearchFragment();
                break;
            case FRAGMENT_INFO:
                newFragment = new InfoFragment();
                break;
            default:
                Log.d(TAG, "Unhandle case");
                break;
        }
        return newFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHome:
                mCurrentFragmentIndex = FRAGMENT_HOME;
                fragmentReplace(mCurrentFragmentIndex);
                break;
            case R.id.btnMypage:
                mCurrentFragmentIndex = FRAGMENT_MYPAGE;
                fragmentReplace(mCurrentFragmentIndex);
                break;
            case R.id.btnSearch:
                mCurrentFragmentIndex = FRAGMENT_SEARCH;
                fragmentReplace(mCurrentFragmentIndex);
                break;
            case R.id.btnInfo:
                mCurrentFragmentIndex = FRAGMENT_INFO;
                fragmentReplace(mCurrentFragmentIndex);
                break;
        }

    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //finish();
    }

    public void setState(int state, String country, String city){
        this.country=country;
        this.city=city;
        this.state=state;
    }

    public String getCountry(){
        return country;
    }
    public String getCity(){
        return city;
    }
    public int getState(){
        return state;
    }


}
