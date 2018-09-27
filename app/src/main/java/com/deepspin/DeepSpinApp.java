package com.deepspin;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class DeepSpinApp extends Application {
    public static final String TAG = com.deepspin.DeepSpinApp.class.getSimpleName();
    private static String mCurrTakenPicPath = "";
    private static volatile com.deepspin.DeepSpinApp sInstance;
    private static RequestQueue sRequestQueue;
    private static int mCurrImageOrientation = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static com.deepspin.DeepSpinApp getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(this);
        }

        return sRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(tag);
        }
    }

    static public void setCurrImageOrientation(int orint) {
        mCurrImageOrientation = orint;
    }

    static public int getCurrImageOrientation() {
        return mCurrImageOrientation;
    }
    public static String getCurrTakenPicPath() {
        return mCurrTakenPicPath;
    }
    public static void setCurrTakenPicPath(String path) {
        mCurrTakenPicPath = path;
    }
}