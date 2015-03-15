package com.zousan.homesecurity;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by zousan on 2015/03/15.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mQueue;
    private static Context mContext;

    private VolleySingleton(Context context){
        mContext = context;
        mQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if(mInstance==null){
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mQueue==null){
            mQueue = Volley.newRequestQueue(mContext.getApplicationContext());

        }
        return mQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
