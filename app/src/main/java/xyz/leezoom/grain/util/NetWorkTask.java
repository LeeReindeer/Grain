package xyz.leezoom.grain.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIp;
import xyz.leezoom.grain.module.User;

/**
 * @Author lee
 * @Time 9/5/17.
 *
 */

public class NetWorkTask extends AsyncTask<Void, Void, Boolean> {

    private User user;
    private NetWorkTask mTask;
    private OnNetWorkListener listener;
    private SharedPreferences query;
    private Context mContext;
    private QueryType queryType;

    /**
     *
     * @param user
     * @param context
     * @param queryType
     */
    public NetWorkTask(User user, Context context, QueryType queryType) {
        this.user = user;
        this.mContext = context;
        this.queryType = queryType;
        if (context instanceof OnNetWorkListener) {
            listener = ((OnNetWorkListener) context);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            //Thread.sleep(500);
            PackMessage packMessage=new PackMessage(user.getAccount(),user.getCertCard(),user.getExtend(),user.getHostInfo(),user.getOthers(),user.getPassword(),user.getSchoolId(),user.getPhoneNumber(),queryType.name(),user.getToken(),user.getName(),user.getVersion());
            String packMsg= MyBase64.stringToBASE64(packMessage.PackQuestMessage());
            String receiveMsg="";
            TcpCommon tcpCommon=new TcpCommon();
            Socket socket=new Socket(ServerIp.mainIp,Integer.valueOf(ServerIp.mainServerPort));
            socket.setSoTimeout(5000);
            InputStream in=socket.getInputStream();
            OutputStream out=socket.getOutputStream();
            tcpCommon.SendString(out,packMsg);
            receiveMsg=tcpCommon.ReceiveString(in);
            in.close();
            out.close();
            socket.close();
            query = mContext.getSharedPreferences("query",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = query.edit();
            editor.putString(queryType.name(),MyBase64.stringToBASE64(receiveMsg));
            // commit
            editor.apply();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
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

    public interface OnNetWorkListener{
        void onSuccess();
        void onFailed();
    }
}
