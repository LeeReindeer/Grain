package xyz.leezoom.grain.module;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.util.Random;

/**
 * @Author lee
*/


public class User {

    private String name="";
    private String schoolId="";
    private String account=""; // no use
    private String password="";
    private String phoneNumber=""; // no use
    private String certCard="";
    private String token="";
    private String extend=""; //查询参数
    private String hostInfo="";
    private String version="7.0,2.160125.8,20150608"; // no use
    private String others="1504533068218,net";; // no use


    public User(String name, String schoolId, String password, String certCard, String extend, String hostInfo, String token) {
        this.name = name;
        this.schoolId = schoolId;
        this.password = password;
        this.certCard = certCard;
        this.extend = extend;
        this.hostInfo = hostInfo;
        this.token = token;
    }

    public User() {
    }

    public void setHostInfo(String hostInfo) {
        this.hostInfo = hostInfo;
    }

    public String getHostInfo() {
        return hostInfo;
    }

    public String getVersion() {
        return version;
    }

    public String getOthers() {
        return others;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCertCard() {
        return certCard;
    }

    public void setCertCard(String certCard) {
        this.certCard = certCard;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

}
