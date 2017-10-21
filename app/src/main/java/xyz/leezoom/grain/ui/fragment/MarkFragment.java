/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/12/17 2:59 PM
 */

package xyz.leezoom.grain.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Mark;
import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIp;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.ui.MarkAdapter;
import xyz.leezoom.grain.util.FragmentUtil;
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.PackMessage;
/**
 * A simple {@link Fragment} subclass.
 */
public class MarkFragment extends Fragment {

    private String sortStatus = SORTBYDEFAULT;
    private final static String SORTBYNAME = "SORTBYNAME";
    private final static String SORTBYSCORE = "SORTBYSCORE";
    private final static String SORTBYDEFAULT = "SORTBYDEFAULT";
    private List<Mark> markList = new ArrayList<>();
    private List<Mark> defaultMarkList = new ArrayList<>();
    private MarkAdapter adapter;
    private SharedPreferences info;
    private SharedPreferences query;
    private String [] markSplitArray;
    private final static QueryType queryType = QueryType.ZFQueryXueshengChengji;
    private xyz.leezoom.grain.util.NetWorkTask.NetWorkListener listener = new xyz.leezoom.grain.util.NetWorkTask.NetWorkListener() {
        @Override
        public void onSuccess() {
            markList.clear();
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            String marks = MyBase64.BASE64ToString(query.getString(queryType.name(),"none"));
            getMarkDataFromLocal(marks,true);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed() {
            FragmentUtil.showFailedPage(getActivity(), true, MarkFragment.this);
        }
    };
    //get user info from MainActivity
    private User user;

    private FloatingActionButton fab;
    private FloatingActionsMenu fabMenu;
    @BindView(R.id.mk_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.mk_refresh_view) SwipeRefreshLayout refreshLayout;

    public MarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mark, container, false);
        ButterKnife.bind(this,view);

        fab = getActivity().findViewById(R.id.fab_b);
        fabMenu = getActivity().findViewById(R.id.multiple_actions);
        fab.setVisibility(View.VISIBLE);
        fab.setIcon(R.drawable.ic_sort_white_48dp);
        fab.setTitle("Sort by Default");
        //sort mark
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markList.clear();
                refreshLayout.setRefreshing(true);
                if (sortStatus.equals(SORTBYDEFAULT)){
                    fab.setTitle("Sort by Name");
                    sortStatus = SORTBYNAME;
                    getMarkDataFromLocal(null, false);
                }else if (sortStatus.equals(SORTBYNAME)){
                    fab.setTitle("Sort by score");
                    sortStatus = SORTBYSCORE;
                    getMarkDataFromLocal(null, false);
                }else {
                    fab.setTitle("Sort by Default");
                    sortStatus = SORTBYDEFAULT;
                    markList.addAll(defaultMarkList);
                    refreshLayout.setRefreshing(false);
                }
                fabMenu.collapse();
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new MarkAdapter(getActivity(), markList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //pull down to refresh
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMarks();
            }
        });
        initList(false);
        return view;
    }

    private void refreshMarks(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initList(true);
                        refreshLayout.setRefreshing(false);
                    }
                });

            }
        }).start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FragmentUtil.showFailedPage(getActivity(), false, this);
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
    }

    private void initList(boolean fromNet){
        markList.clear();
        user = new User();
        info = getActivity().getSharedPreferences("info",Context.MODE_PRIVATE);
        query = getActivity().getSharedPreferences("query",Context.MODE_PRIVATE);
        String name = MyBase64.BASE64ToString(info.getString("nnn","none"));
        String account = MyBase64.BASE64ToString(info.getString("aaa","none"));
        String pass = MyBase64.BASE64ToString(info.getString("ppp","none"));
        String hostInfo = MyBase64.BASE64ToString(info.getString("hhh","none"));
        String idCard = MyBase64.BASE64ToString(info.getString("ccc","none"));
        user.setName(name);
        user.setAccount(account);
        user.setSchoolId(account);
        user.setCertCard(idCard);
        user.setExtend(account);
        user.setPassword(pass);
        user.setToken(MyBase64.BASE64ToString(query.getString("ttt","none")));
        user.setHostInfo(hostInfo);
        //markTask = new NetWorkTask(user, queryType);
        //markTask.execute((Void) null);
        refreshLayout.setRefreshing(true);
        xyz.leezoom.grain.util.NetWorkTask mTask = new xyz.leezoom.grain.util.NetWorkTask(user, queryType, ServerIp.mainServerPort, listener, getContext());
        mTask.execute((Void) null);
        /*Mark mark = new Mark();
        mark.setName("大学英语1");
        mark.setTeacherName(" ming");
        mark.setScore("100");
        mark.setCredit("5.0");
        mark.setGp("1.0");
        markList.add(0,mark);*/
    }

    /**
     *
     * @param netMarks marks from internet
     * @return
     */
    private void getMarkDataFromLocal(String netMarks, boolean isFromNet){
        markList.clear();
        //sort
        String sortStatus = this.sortStatus;
        String [] allMarks;
        if (isFromNet){
            allMarks = netMarks.split("\n");
        }else {
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            String marks = MyBase64.BASE64ToString(query.getString(queryType.name(),"none"));
            allMarks = marks.split("\n");
        }

        List<Mark> processedMark = new ArrayList<>();
        for (String e: allMarks){
            Log.d("mark",e);
            markSplitArray = e.split(PackMessage.SplitFields);
            Mark mark = new Mark();
            mark.setSchoolId(markSplitArray[0]);
            mark.setYear(markSplitArray[2]);
            mark.setSemester(markSplitArray[3]);
            mark.setTeacherName(markSplitArray[4]);
            mark.setName(markSplitArray[5]);
            mark.setScore(markSplitArray[6]);
            mark.setCredit(markSplitArray[7]);
            mark.setGp(markSplitArray[8]);
            boolean isProcessed = false;
            //ensure to get Not repeat scores
            for (int i = 0; i < processedMark.size(); i++) {
                if (processedMark.get(i).getName().equals(mark.getName()) &&
                        processedMark.get(i).getScore().equals(mark.getScore()) &&
                        processedMark.get(i).getSemester().equals(mark.getSemester())){
                    isProcessed = true;
                    break;
                }
            }
            if (!isProcessed) {
                markList.add(mark);
                processedMark.add(mark);
            }
        }
        //sort list
        if (sortStatus.equals(SORTBYDEFAULT)){
            defaultMarkList.clear();
            defaultMarkList.addAll(markList);
        }else if (sortStatus.equals(SORTBYNAME)){
            Collections.sort(markList, new SortByName());
        }else {
            Collections.sort(markList, new SortBySore());
        }
        refreshLayout.setRefreshing(false);
        processedMark.clear();
    }

    class  SortBySore implements Comparator{

        @Override
        public int compare(Object o1, Object o2) {
            Mark m1 = (Mark) o1;
            Mark m2 = (Mark) o2;
            return Integer.valueOf(m1.getScore()) - Integer.valueOf(m2.getScore());
        }
    }
    class SortByName implements Comparator{

        @Override
        public int compare(Object o1, Object o2) {
            Mark m1 = (Mark) o1;
            Mark m2 = (Mark) o2;
            return m1.getName().compareTo(m2.getName());
        }
    }
}
