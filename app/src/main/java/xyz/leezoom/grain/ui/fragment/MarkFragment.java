package xyz.leezoom.grain.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Mark;
import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIp;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.ui.MarkAdapter;
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.NetWorkTask;
import xyz.leezoom.grain.util.PackMessage;
import xyz.leezoom.grain.util.TcpUtil;
/**
 * A simple {@link Fragment} subclass.
 */
public class MarkFragment extends Fragment {

    private List<Mark> markList = new ArrayList<>();
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
            getMarkDataFromLocal(marks);
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailed() {
            //todo show failed page or send broadcast to show dialog
        }
    };
    //get user info from MainActivity
    private User user;
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
        adapter = new MarkAdapter(getActivity(), markList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //switch down to refresh
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
    public void onDestroyView() {
        super.onDestroyView();
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
    private void getMarkDataFromLocal(String netMarks){
        markList.clear();
        query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
        String marks = MyBase64.BASE64ToString(query.getString(queryType.name(),"none"));
        String [] allMarks = netMarks.split("\n");
        List<String> processed = new ArrayList<>();
        for (String e: allMarks){
            Log.d("mark",e);
            markSplitArray = e.split(PackMessage.SplitFields);
            //get Not repeat scores
            if (!processed.contains(markSplitArray[5])) {
                processed.add(markSplitArray[5]);
                Mark mark = new Mark();
                mark.setSchoolId(markSplitArray[0]);
                mark.setYear(markSplitArray[2]);
                mark.setSemester(markSplitArray[3]);
                mark.setTeacherName(markSplitArray[4]);
                mark.setName(markSplitArray[5]);
                mark.setScore(markSplitArray[6]);
                mark.setCredit(markSplitArray[7]);
                mark.setGp(markSplitArray[8]);
                markList.add(mark);
            }
        }
        processed.clear();
    }
}
