package com.example.hyojin.myapplication.activity.Fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button

import com.example.hyojin.myapplication.R
import com.example.hyojin.myapplication.activity.LoginActivity
import com.example.hyojin.myapplication.helper.SQLiteHandler
import com.example.hyojin.myapplication.helper.SessionManager

import com.facebook.FacebookSdk.getApplicationContext
import kotlinx.android.synthetic.main.activity

class MainActivity : FragmentActivity(), OnClickListener, MypageFragment.OnHeadlineSelectedListener, SearchFragment.OnHeadlineSelectedListener {
    internal val TAG = "MainActivity"
    internal var mCurrentFragmentIndex: Int = 0
    //    public static final int FRAGMENT_SETTING=4;
    private var session: SessionManager? = null
    private var db: SQLiteHandler? = null
    internal val country: String? =null
    internal val city: String? =null
    internal var state = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnHome = findViewById(R.id.btnHome) as Button
        btnHome.setOnClickListener(this)
        val btnMypage = findViewById(R.id.btnMypage) as Button
        btnMypage.setOnClickListener(this)
        val btnSearch = findViewById(R.id.btnSearch) as Button
        btnSearch.setOnClickListener(this)
        val btnInfo = findViewById(R.id.btnInfo) as Button
        btnInfo.setOnClickListener(this)


        // Session manager
        session = SessionManager(getApplicationContext())

        // SQLite database handler
        db = SQLiteHandler(getApplicationContext())

        if (!session!!.isLoggedIn) {
            logoutUser()
        }

        mCurrentFragmentIndex = FRAGMENT_HOME
        fragmentReplace(mCurrentFragmentIndex)
    }

    override fun fragmentReplace(reqNewFragmentIndex: Int) {

        var newFragment: Fragment? = null
        Log.d(TAG, "fragmentReplace " + reqNewFragmentIndex)
        newFragment = getFragment(reqNewFragmentIndex)

        // replace fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.ll_fragment, newFragment)

        // Commit the transaction
        transaction.commit()

    }

    private fun getFragment(idx: Int): Fragment {
        var newFragment: Fragment? = null

        when (idx) {
            FRAGMENT_HOME -> newFragment = HomeFragment()
            FRAGMENT_MYPAGE -> newFragment = MypageFragment()
            FRAGMENT_SEARCH -> newFragment = SearchFragment()
            FRAGMENT_INFO -> newFragment = InfoFragment()
            else -> Log.d(TAG, "Unhandle case")
        }
        return newFragment
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnHome -> {
                mCurrentFragmentIndex = FRAGMENT_HOME
                fragmentReplace(mCurrentFragmentIndex)
            }
            R.id.btnMypage -> {
                mCurrentFragmentIndex = FRAGMENT_MYPAGE
                fragmentReplace(mCurrentFragmentIndex)
            }
            R.id.btnSearch -> {
                mCurrentFragmentIndex = FRAGMENT_SEARCH
                fragmentReplace(mCurrentFragmentIndex)
            }
            R.id.btnInfo -> {
                mCurrentFragmentIndex = FRAGMENT_INFO
                fragmentReplace(mCurrentFragmentIndex)
            }
        }

    }

    private fun logoutUser() {
        session!!.setLogin(false)

        db!!.deleteUsers()

        // Launching the login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        //finish();
    }

    override fun setState(state: Int, country: String, city: String) {
        this.country = country
        this.city = city
        this.state = state
    }

    override fun getCountry(): String {
        return country
    }

    override fun getCity(): String {
        return city
    }

    override fun getState(): Int {
        return state
    }

    companion object {
        val FRAGMENT_HOME = 0
        val FRAGMENT_MYPAGE = 1
        val FRAGMENT_SEARCH = 2
        val FRAGMENT_INFO = 3
    }


}
