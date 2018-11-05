package com.hjkwak.pretravel.util;

import android.util.Log;

import com.hjkwak.pretravel.BuildConfig;

public class Dlog {

    public static boolean DEBUG = BuildConfig.DEBUG;

    /**
     * Log Level Error
     **/
    public static final void e(String title, String message) {
        if (DEBUG) Log.e(title, buildLogMsg(message));
    }

    /**
     * Log Level Warning
     **/
    public static final void w(String title, String message) {
        if (DEBUG) Log.w(title, buildLogMsg(message));
    }

    /**
     * Log Level Information
     **/
    public static final void i(String title, String message) {
        if (DEBUG) Log.i(title, buildLogMsg(message));
    }

    /**
     * Log Level Debug
     **/
    public static final void d(String url, String message) {
        if (DEBUG) Log.d(url, buildLogMsg(message));
    }

    /**
     * Log Level Verbose
     **/
    public static final void v(String title, String message) {
        if (DEBUG) Log.v(title, buildLogMsg(message));
    }


    public static String buildLogMsg(String message) {

        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);

        return sb.toString();

    }


}
