/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/11/17 3:27 PM
 */

package xyz.leezoom.grain.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Book;
import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIpOld;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.ui.activity.MainActivity;
import xyz.leezoom.grain.ui.adapter.BookAdapter;
import xyz.leezoom.grain.util.FragmentUtil;
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.NetWorkTask;
import xyz.leezoom.grain.util.PackMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class BooksFragment extends Fragment {

    private List<Book> bookList;
    private User user;
    private BookAdapter adapter;
    private NetWorkTask netWorkTask;
    private SharedPreferences query;
    private SharedPreferences info;

    @BindView(R.id.lb_books_recycler_view) RecyclerView recyclerView;

    private NetWorkTask.NetWorkListener mListener = new NetWorkTask.NetWorkListener() {
        @Override
        public void onSuccess() {
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            String data = MyBase64.BASE64ToString(query.getString(QueryType.TsgUserLibrarys.name(),"none"));
            String splitData1 [] =data.split("\n");
            for (String e : splitData1) {
                String singleData [] = e.split(PackMessage.SplitFields);
                Book book = new Book();
                try {
                    book.setName(singleData[0]);
                    book.setParam1(singleData[9]);
                    book.setParam2(singleData[10]);
                    book.setPlace(singleData[5]);
                } catch (ArrayIndexOutOfBoundsException t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
                bookList.add(book);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed() {
            FragmentUtil.showFailedPage(getActivity(), true, BooksFragment.this);
            Toast.makeText(getContext(), "Failed.Try to sign again.",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        FragmentUtil.showFailedPage(getActivity(), false, this);
    }

    public BooksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_books, container, false);
        ButterKnife.bind(this,view);
        initData();
        adapter = new BookAdapter(getContext(),bookList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void initData(){
        bookList = new ArrayList<>();
        bookList.clear();
        user = MainActivity.getUser();
        netWorkTask = new NetWorkTask(user, QueryType.TsgUserLibrarys, ServerIpOld.libraryServerPort,mListener,getContext());
        netWorkTask.execute((Void) null);
    }

}
