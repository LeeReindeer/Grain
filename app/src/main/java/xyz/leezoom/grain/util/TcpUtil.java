package xyz.leezoom.grain.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import xyz.leezoom.grain.module.ServerIp;

/**
 * @Author lee
 * @Time 9/7/17.
 */

public class TcpUtil {

    private String port;
    private PackMessage packMessage;

    public TcpUtil(String port, PackMessage packMessage) {
        this.port = port;
        this.packMessage = packMessage;
    }

    public String receiveString(){

        String receiveMsg="";
        try {
            //PackMessage packMessage=new PackMessage(user.getAccount(),user.getCertCard(),user.getExtend(),user.getHostInfo(),user.getOthers(),user.getPassword(),user.getSchoolId(),user.getPhoneNumber(),queryType.name(),user.getToken(),user.getName(),user.getVersion());
            String packMsg= MyBase64.stringToBASE64(packMessage.PackQuestMessage());
            TcpCommon tcpCommon=new TcpCommon();
            Socket socket=new Socket(ServerIp.mainIp,Integer.valueOf(this.port));
            socket.setSoTimeout(5000);
            InputStream in=socket.getInputStream();
            OutputStream out=socket.getOutputStream();
            tcpCommon.SendString(out,packMsg);
            receiveMsg=tcpCommon.ReceiveString(in);
            in.close();
            out.close();
            socket.close();
            //query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            //SharedPreferences.Editor editor = query.edit();
            //editor.putString(queryType.name(),MyBase64.stringToBASE64(receiveMsg));
            // commit
            //editor.apply();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveMsg;
    }
}
