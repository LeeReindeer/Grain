/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/9/17 5:43 PM
 */

package xyz.leezoom.grain.util;

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

public class NetWorkTask extends AsyncTask<Void, Void, Boolean> {

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
    protected Boolean doInBackground(Void... params) {
        PackMessage packMessage = null;
        if (queryType == QueryType.CardUserPayment){
            Date date = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
            //System.out.println(ft.format(date));
            packMessage=new PackMessage(queryType.name(), user.getName(), user.getSchoolId(), user.getAccount(), user.getPassword(),
                    user.getPhoneNumber(), user.getCertCard(), user.getToken(), user.getExtend()+","+ft.format(date)+",0",user.getHostInfo(),user.getVersion(),user.getOthers());
        }else {
            packMessage=new PackMessage(queryType.name(), user.getName(), user.getSchoolId(), user.getAccount(), user.getPassword(),
                    user.getPhoneNumber(), user.getCertCard(), user.getToken(), user.getExtend(),user.getHostInfo(),user.getVersion(),user.getOthers());
        }

        String receiveMsg="";
        TcpUtil tcpUtil = new TcpUtil(port, packMessage);
        receiveMsg = tcpUtil.receiveString();
        query = mContext.getSharedPreferences("query",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = query.edit();
        editor.putString(queryType.name(),MyBase64.stringToBASE64(receiveMsg));
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
