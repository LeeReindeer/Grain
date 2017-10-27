/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/27/17 5:31 PM
 */

package xyz.leezoom.grain.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import xyz.leezoom.grain.R;

public class SettingActivity extends PreferenceActivity {

    public static final String KEY_PREF_AUTO= "pre_key_auto_update";
    public static final String KEY_PREF_CHECK= "pre_key_check_update";
    public static final String KEY_PREF_FACULTY= "pre_key_faculty";
    public static final String KEY_PREF_CLASS= "pre_key_class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
