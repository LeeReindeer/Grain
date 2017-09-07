package xyz.leezoom.grain.ui;


import android.content.Context;
import android.content.SharedPreferences;
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
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.PackMessage;
import xyz.leezoom.grain.util.TcpUtil;
/**
 * A simple {@link Fragment} subclass.
 */
public class MarkFragment extends Fragment {

    private List<Mark> markList = new ArrayList<>();
    private MarkAdapter adapter;
    private NetWorkTask markTask;

    private SharedPreferences info;
    private SharedPreferences query;

    private String [] markSplitArray;
    private final static QueryType queryType = QueryType.ZFQueryXueshengChengji;
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
        User user = new User();
        info = getActivity().getSharedPreferences("info",Context.MODE_PRIVATE);
        query = getActivity().getSharedPreferences("query",Context.MODE_PRIVATE);
        String name = MyBase64.BASE64ToString(info.getString("nnn","none"));
        String account = MyBase64.BASE64ToString(info.getString("aaa","none"));
        String idCard = MyBase64.BASE64ToString(info.getString("ppp","none"));
        String pass = idCard.substring(9, 18);
        user.setName(name);
        user.setAccount(account);
        user.setSchoolId(account);
        user.setCertCard(idCard);
        user.setExtend(account);
        user.setPassword(pass);
        user.setToken(MyBase64.BASE64ToString(query.getString("ttt","none")));
        if (fromNet) {
            markTask = new NetWorkTask(user, queryType);
            markTask.execute((Void) null);
        }else{
            getMarkDataFromLocal(null,false);
            adapter.notifyDataSetChanged();
        }
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
    private boolean getMarkDataFromLocal(String netMarks,boolean isFromNet){
        markList.clear();
        query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
        String marks = MyBase64.BASE64ToString(query.getString(queryType.name(),"none"));
        boolean isHaveLocalData = !marks.equals("none");
        if (!isHaveLocalData || marks.equals("false")) {
            isHaveLocalData = false;
            return false;
        }
        String allMarks [] = !isFromNet ? marks.split("\n"):netMarks.split("\n");
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
            markList.add(mark);
        }
        return isHaveLocalData;
    }

    /**
     * get mark from server
     */
    public class NetWorkTask extends AsyncTask<Void, Void, Boolean> {

        private User user;
        private SharedPreferences query;
        private QueryType queryType;

        /**
         * @param user
         * @param queryType
         */
        public NetWorkTask(User user, QueryType queryType) {
            this.user = user;
            this.queryType = queryType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            PackMessage packMessage=new PackMessage(user.getAccount(),user.getCertCard(),user.getExtend(),user.getHostInfo(),user.getOthers(),user.getPassword(),user.getSchoolId(),user.getPhoneNumber(),queryType.name(),user.getToken(),user.getName(),user.getVersion());
            TcpUtil tcpUtil= new  TcpUtil(ServerIp.mainServerPort,packMessage);
            String receiveMsg = tcpUtil.receiveString();
            query = getActivity().getSharedPreferences("query",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = query.edit();
            editor.putString(queryType.name(),MyBase64.stringToBASE64(receiveMsg));
            // commit
            editor.apply();
            return true;
        }

        @Override
        protected void onPostExecute( Boolean success) {
            markTask = null;

            if (success) {
                //
                markList.clear();
                query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
                String marks = MyBase64.BASE64ToString(query.getString(queryType.name(),"none"));
                if (marks == null || marks.equals("false")) {
                    //todo show failed page or send broadcast to show dialog
                    Toast.makeText(getContext(), "Failed.YOu can try to enter new token.",Toast.LENGTH_SHORT).show();
                    return;
                }
                getMarkDataFromLocal(marks,true);
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            } else {
                //show failed fragment
            }
        }

        @Override
        protected void onCancelled() {
            //show failed fragment
        }
    }

}
