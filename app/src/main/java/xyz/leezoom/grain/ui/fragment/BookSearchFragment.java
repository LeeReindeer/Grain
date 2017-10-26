/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/11/17 3:30 PM
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
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Book;
import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIpOld;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.ui.BookAdapter;
import xyz.leezoom.grain.util.FragmentUtil;
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.NetWorkTask;
import xyz.leezoom.grain.util.PackMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookSearchFragment extends Fragment {

    private List<Book> bookList;
    private List<Book> adapterList;
    private BookAdapter adapter;
    private NetWorkTask netWorkTask;
    private String searchString;
    private User user;
    private SharedPreferences query;
    private SharedPreferences info;

    @BindView(R.id.bk_search_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.bk_search_view) EditText searchView;
    @BindView(R.id.bk_action_search) ImageView searchAction;

    private NetWorkTask.NetWorkListener listener = new NetWorkTask.NetWorkListener() {
        @Override
        public void onSuccess() {
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            String data = MyBase64.BASE64ToString(query.getString(QueryType.TsgQueryBooks.name(),"none"));
            String splitData1 [] =data.split("\n");
            for (String e : splitData1) {
                String singleData [] = e.split(PackMessage.SplitFields);
                Book book = new Book();
                book.setName(singleData[1]);
                book.setAuthor(singleData[2].replace("著","").replace("编",""));
                book.setParam1(singleData[3]);
                book.setParam2(singleData[4]);
                book.setPlace(singleData[5]);
                bookList.add(book);
            }
            adapterList.addAll(bookList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed() {
            FragmentUtil.showFailedPage(getActivity(), true, BookSearchFragment.this);
        }
    };

    @OnClick(R.id.bk_action_search) void search(){
        searchString = searchView.getText().toString();
        doSearch();
    }

    public BookSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDetach() {
        super.onDetach();
        FragmentUtil.showFailedPage(getActivity(), false, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_search, container, false);
        ButterKnife.bind(this,view);
        adapterList = new ArrayList<>();
        adapter = new BookAdapter(getContext(), adapterList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //Toast.makeText(getContext(),"book search",Toast.LENGTH_SHORT).show();
        return view;
    }

    private void doSearch(){
        bookList = new ArrayList<>();
        bookList.clear();
        info = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        query = getActivity().getSharedPreferences("query",Context.MODE_PRIVATE);
        String name = MyBase64.BASE64ToString(info.getString("nnn","none"));
        String account = MyBase64.BASE64ToString(info.getString("aaa","none"));
        String pass = MyBase64.BASE64ToString(info.getString("ppp","none"));
        String hostInfo = MyBase64.BASE64ToString(info.getString("hhh","none"));
        String idCard = MyBase64.BASE64ToString(info.getString("ccc","none"));
        user = new User();
        user.setName(name);
        user.setAccount(account);
        user.setSchoolId(account);
        user.setCertCard(idCard);
        user.setExtend(searchString+",,,0");
        user.setPassword(pass);
        user.setToken(MyBase64.BASE64ToString(query.getString("ttt","none")));
        user.setHostInfo(hostInfo);
        netWorkTask = new NetWorkTask(user, QueryType.TsgQueryBooks, ServerIpOld.libraryServerPort, listener, getContext());
        netWorkTask.execute((Void)null);
    }

}
