/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/7/17 8:22 PM
 */

package xyz.leezoom.grain.util;

/**
 * @Author lee
 * @Time 9/3/17.
 */
public class PackMessage {
    public static final int Messagenum = 12;
    public static final String SplitFields = "～";
    public static final String SplitGroups = "▓";
    public static final String SplitInField = ",";
    public static final String SplitRows = "\r\n";
    public String m_accnum = "";
    public String m_certcode = "";
    public String m_extend = "";
    public String m_hostinfo = "";
    public String m_messageinfo = "";
    public String m_other = "";
    public String m_passwd = "";
    public String m_percode = "";
    public String m_phonenum = "";
    public String m_questtype = "";
    public String m_token = "";
    public String m_username = "";
    public String m_version = "";

    public PackMessage(String m_questtype, String m_username, String m_percode, String m_accnum, String m_passwd,  String m_phonenum,
                       String m_certcode, String m_token, String m_extend, String m_hostinfo,  String m_version, String m_other) {
        this.m_accnum = m_accnum;
        this.m_certcode = m_certcode;
        this.m_extend = m_extend;
        this.m_hostinfo = m_hostinfo;
        this.m_other = m_other;
        this.m_passwd = m_passwd;
        this.m_percode = m_percode;
        this.m_phonenum = m_phonenum;
        this.m_questtype = m_questtype;
        this.m_token = m_token;
        this.m_username = m_username;
        this.m_version = m_version;
    }

    public String PackQuestMessage() {
        return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf("" + this.m_questtype + SplitFields)).append(this.m_username).append(SplitFields).toString())).append(this.m_percode).append(SplitFields).toString())).append(this.m_accnum).append(SplitFields).toString())).append(this.m_passwd).append(SplitFields).toString())).append(this.m_phonenum).append(SplitFields).toString())).append(this.m_certcode).append(SplitFields).toString())).append(this.m_token).append(SplitFields).toString())).append(this.m_extend).append(SplitFields).toString())).append(this.m_hostinfo).append(SplitFields).toString())).append(this.m_version).append(SplitFields).toString())).append(this.m_other).toString();
    }

    public static boolean CheckQuestMsg(String questMsg) {
        String[] tmps = questMsg.split(SplitFields);

        //for (String e : tmps) {
        //    System.out.println(e);
        //}

        //12个参数
        if (tmps == null || tmps.length != 12) {
            return false;
        }
        return true;
    }
}
