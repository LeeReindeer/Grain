/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/27/17 3:52 PM
 */

package xyz.leezoom.grain.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.User;

/**
 * @Author lee
 * @Time 9/5/17.
 *
 */

public class NetWorkTask extends AsyncTask<String, Void, Boolean> {

    private User user;
    private NetWorkTask mTask;
    private NetWorkListener listener;
    private Context mContext;
    private String port;
    private SharedPreferences query;
    private QueryType queryType;

    /**
     *
     * @param user
     * @param queryType
     * @param listener
     * */
    public NetWorkTask(User user, QueryType queryType, String port, NetWorkListener listener, Context context) {
        this.user = user;
        this.listener = listener;
        this.queryType = queryType;
        this.mContext = context;
        this.port = port;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        PackMessage packMessage = null;

        String extend = user.getExtend();
        if (queryType == QueryType.CardUserPayment) {
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
            extend = user.getExtend()+","+ft.format(date)+",0";
        }else if (queryType ==QueryType.ZFQueryCengkeXYMC) {
            extend = "";
        } else if (queryType == QueryType.ZFQueryCengkeToday) {
            extend = params[0]; //faculty.Example: 数理与信息学院,50,0
        } else if (queryType == QueryType.ZFAllClassnames) {
            extend = params[0]; //year.Example: 2015
        }

        packMessage=new PackMessage(queryType.name(), user.getName(), user.getSchoolId(), user.getAccount(), user.getPassword(),
                user.getPhoneNumber(), user.getCertCard(), user.getToken(), extend, user.getHostInfo(), user.getVersion(), user.getOthers());
        String receiveMsg="";
        TcpUtil tcpUtil = new TcpUtil(port, packMessage);
        receiveMsg = tcpUtil.receiveString();
        query = mContext.getSharedPreferences("query",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = query.edit();
        if (queryType == QueryType.ZFQueryCengkeToday) {
            String key = extend.replaceAll("[\\D]", "");
            editor.putString(key, MyBase64.stringToBASE64(receiveMsg));
        } else {
            editor.putString(queryType.name(), MyBase64.stringToBASE64(receiveMsg));
        }
        // commit
        editor.apply();
        return !(receiveMsg == null || receiveMsg.equals("false") || receiveMsg.isEmpty());
    }

    @Override
    protected void onPostExecute( Boolean success) {
        mTask = null;

        if (success) {
            listener.onSuccess();
        } else {
            //show failed fragment
            listener.onFailed();
        }
    }

    @Override
    protected void onCancelled() {
       listener.onFailed();
    }

    public interface NetWorkListener{
        void onSuccess();
        void onFailed();
    }
}
