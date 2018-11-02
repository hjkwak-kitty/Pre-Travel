package com.example.hyojin.myapplication.helper;

/**
 * Created by Hyojin on 2016-04-19.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hyojin.myapplication.activity.data.MyList;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // table name
    private static final String TABLE_USER = "users";
    private static final String TABLE_CONTENTS ="contents";
    private static final String TABLE_MYDATA ="myData";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    //Contents Table Columns names
    private static final String KEY_TEXT_UID ="text_uid";
    private static final String KEY_COUNTRY="country";
    private static final String KEY_CITY="city";
    private static final String KEY_CONTENT_TITLE="con_title";
    private static final String KEY_CONTENT_DATA1="con_data1";
    private static final String KEY_CONTENT_DATA2="con_data2";
    private static final String KEY_CONTENT_DATA3="con_data3";
    private static final String KEY_CONTENT_DATA4="con_data4";
    private static final String KEY_CONTENT_PHOTO="con_photo";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";
        String CREATE_CONTENTS_TABLE ="create table contents (text_uid text, country text, city text, con_title text, con_data1 text," +
                "con_data2 text, con_data3 text, con_data4 text, con_photo text,recommend INTEGER, created_at text)";
        String CREATE_MYDATA_TABLE ="Create table myData (scrapList_uid TEXT, scrapList_title TEXT, favorite TEXT, follow TEXT)";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_CONTENTS_TABLE);
        db.execSQL(CREATE_MYDATA_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CONTENTS);
        db.execSQL("drop table if EXISTS myData");

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void addContents(String text_uid, String country, String city, String con_title,
                            String con_data1, String con_data2, String con_data3, String con_data4, String con_photo,int recommend, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TEXT_UID,text_uid);
        values.put(KEY_COUNTRY,country);
        values.put(KEY_CITY,city);
        values.put(KEY_CONTENT_TITLE,con_title);
        values.put(KEY_CONTENT_DATA1,con_data1);
        values.put(KEY_CONTENT_DATA2,con_data2);
        values.put(KEY_CONTENT_DATA3,con_data3);
        values.put(KEY_CONTENT_DATA4, con_data4);
        values.put(KEY_CONTENT_PHOTO, con_photo);
        values.put("recommend", recommend);
        values.put("created_at", created_at);

        // Inserting Row
        long id = db.insert(TABLE_CONTENTS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New contents inserted into sqlite: " + id);
    }

    /**
     * Getting contents data from database
     * */
    public ArrayList getContentsDetails(String uuid) {
        ArrayList<MyList> contents = new ArrayList<>();
        //HashMap<String, String> contents = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTENTS;
        selectQuery += " where text_uid = '"+uuid+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Move to first row
        cursor.moveToFirst();
        contents.add(new MyList(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
                cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getString(10)));
        while (cursor.moveToNext()){
            contents.add(new MyList(cursor.getString(0), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),
                    cursor.getString(6),cursor.getString(7),cursor.getString(8), cursor.getInt(9),cursor.getString(10)));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching contents from Sqlite: " + contents.toString());

        return contents;
    }

    /*MY data 부분 스크랩리스트 DB수정하기*/
    public void editMyScraplist(String scrap_title, String scrap_uid) {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("scrapList_uid", scrap_uid);
        cv.put("scrapList_title", scrap_title);
        db.update(TABLE_MYDATA, cv, null, null);

        // return user
        Log.d(TAG, "Edit Contents: " + scrap_title + ", " + scrap_uid + "수정");
    }

    /**
     * 내 데이터 가져오기
     * */
    public HashMap<String, String> getMydataDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_MYDATA;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() >= 0) {
            user.put("scrapList_uid", cursor.getString(1));
            user.put("scrapList_title", cursor.getString(2));
            user.put("favorite", cursor.getString(3));
            user.put("follow", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching mydata from Sqlite: " + user.toString());

        return user;
    }





    /*MY List 부분 DB수정하기*/
    public void editMyContents(String con_data4, String text_uid, String con_title) {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("con_data4", con_data4);
        db.update(TABLE_CONTENTS, cv, "text_uid=? and con_title=? ", new String[]{text_uid, con_title});

        // return user
        Log.d(TAG, "Edit Contents: " + text_uid +", "+con_title+", "+con_data4);
    }


    /*MY List 부분 특정 컨테츠 내 아이템 DB삭제하기*/
    public void deleteMyContent(String text_uid, String con_title) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_CONTENTS, "text_uid =? and con_title=?", new String[]{text_uid, con_title});
        // return user
        Log.d(TAG, "Delete Contents: " + text_uid +", "+con_title);
    }

    /*MY List 부분 컨텐츠 DB삭제하기*/
    public void deleteConetentsUUid(String text_uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_CONTENTS, "text_uid =?", new String[]{text_uid});
        // return user
        Log.d(TAG, "Delete Contents: " + text_uid);
    }

    /*MY List 부분 DB가져오기*/
    public ArrayList getMyContentsDetails() {
        ArrayList<MyList> contents = new ArrayList<>();
        //HashMap<String, String> contents = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTENTS + " group by text_uid Order By created_at DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Move to first row

        if(cursor.getCount()!=0){
            cursor.moveToFirst();
            contents.add(new MyList(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9),cursor.getString(10)));
            while (cursor.moveToNext()){
                contents.add(new MyList(cursor.getString(0), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),
                        cursor.getString(6),cursor.getString(7),cursor.getString(8), cursor.getInt(9),cursor.getString(10)));
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching Mycontents from Sqlite: " + contents.toString());

        return contents;
    }


    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }


    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_CONTENTS, null, null);
        db.close();

        Log.d(TAG, "Deleted all contents info from sqlite");
    }



}

