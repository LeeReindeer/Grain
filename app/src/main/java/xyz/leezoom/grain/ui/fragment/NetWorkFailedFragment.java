/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/12/17 10:58 AM
 */

package xyz.leezoom.grain.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.leezoom.grain.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetWorkFailedFragment extends Fragment {


    public NetWorkFailedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net_work_failed, container, false);
        return view;
    }

}
