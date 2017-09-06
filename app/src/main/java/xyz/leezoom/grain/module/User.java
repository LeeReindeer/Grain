package xyz.leezoom.grain.module;

/**
 * @Author lee
 * @Time 9/4/17.
 */
public class User {
    private String name="";
    private String schoolId="";
    private String account="";
    private String password="";
    private String phoneNumber="";
    private String certCard="";
    private String token="";
    private String extend=""; //查询参数
    private String hostInfo="NEM-AL10,192.168.1.100,F4:CB:52:13:05:49,,中国移动,460022147545798,898600C1111456040898";
    private String version="7.0,2.160125.8,20150608";
    private String others="1504533068218,net";

    public User() {
    }

    public User(String name, String schoolId, String password, String certCard, String extend) {
        this.name = name;
        this.schoolId = schoolId;
        this.password = password;
        this.certCard = certCard;
        this.extend = extend;
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
