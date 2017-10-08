/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/23/17 9:47 PM
 */

package xyz.leezoom.grain.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;

public class FunctionFragment extends Fragment implements View.OnClickListener{


    @BindView(R.id.card_mark) CardView cardView1;
    @BindView(R.id.card_class) CardView cardView2;
    @BindView(R.id.card_card) CardView cardView3;
    @BindView(R.id.card_library) CardView cardView4;

    @Override
    public void onClick(View view) {
        if (getActivity() instanceof FOnClickListener){
            ((FOnClickListener)getActivity()).onFClick(view.getId());
        }
    }


    public interface FOnClickListener{
        void onFClick(int id);
    }

    public FunctionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_function, container, false);
        ButterKnife.bind(this,view);
        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_b);
        fab.setVisibility(View.GONE);
        return view;
    }

}
