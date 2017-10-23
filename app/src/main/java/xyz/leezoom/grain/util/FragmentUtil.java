/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/21/17 2:26 PM
 */

package xyz.leezoom.grain.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import xyz.leezoom.grain.R;
import xyz.leezoom.grain.ui.fragment.NetWorkFailedFragment;

/**
 * @Author lee
 * @Time 10/21/17.
 */

public class FragmentUtil {

    private static final NetWorkFailedFragment failedFragment = new NetWorkFailedFragment();

    private FragmentUtil() {}

    public static void showFailedPage(FragmentActivity activity, boolean key, Fragment fThis) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (key) {
            transaction.hide(fThis);
            transaction.add(R.id.tab_content,failedFragment);
        } else {
            transaction.remove(failedFragment);
        }
        transaction.commit();
    }
}
