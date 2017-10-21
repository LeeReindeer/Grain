/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/23/17 9:50 PM
 */

package xyz.leezoom.grain.ui.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Card;
import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIp;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.ui.MoneyListAdapter;
import xyz.leezoom.grain.util.FragmentUtil;
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.PackMessage;


public class CardFragment extends Fragment {

    @BindView(R.id.cd_recent_money_list) ListView moneyListView;
    @BindView(R.id.cd_pic) CircleImageView mUserPic;
    @BindView(R.id.cd_name) TextView mName;
    @BindView(R.id.cd_status) Button mStatus;
    @BindView(R.id.cd_money) Button mBalance;
    @BindView(R.id.cd_progress) ProgressBar mProgressView;

    private NetWorkFailedFragment failedFragment;

    private final static QueryType BASEINFO = QueryType.CardUserBaseInfo;
    private final static QueryType PAYMENT = QueryType.CardUserPayment;
    private final static QueryType PICTURE = QueryType.CardUserPicture;
    private List<Card> moneyList = new ArrayList<>();
    private List<Card> adapterData = new ArrayList();
    private MoneyListAdapter adapter;
    private SharedPreferences query;
    private SharedPreferences info;
    //private NetWorkTask baseTask;
    private User user;
    private Card baseCardInfo;
    xyz.leezoom.grain.util.NetWorkTask baseTask;
    xyz.leezoom.grain.util.NetWorkTask payTask;
    //call on baseInfo success return
    private xyz.leezoom.grain.util.NetWorkTask.NetWorkListener bListener = new xyz.leezoom.grain.util.NetWorkTask.NetWorkListener() {
        @Override
        public void onSuccess() {
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            String cardData = MyBase64.BASE64ToString(query.getString(BASEINFO.name(),"none"));
            baseCardInfo = new Card();
            String [] baseInfo = cardData.split(PackMessage.SplitFields);
            baseCardInfo.setStatus(baseInfo[4]);
            baseCardInfo.setBalance(baseInfo[11] + ":" + baseInfo[13]);
            baseCardInfo.setName(baseInfo[0]);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mName.setText(baseCardInfo.getName());
                    mStatus.setText(baseCardInfo.getStatus());
                    mBalance.setText(baseCardInfo.getBalance());
                }
            });
        }
        @Override
        public void onFailed() {
            //SHOW FAILED PAGE
            showProgress(false);
            FragmentUtil.showFailedPage(getActivity(), true, CardFragment.this);
            Toast.makeText(getContext(), "Get card info failed", Toast.LENGTH_SHORT).show();
        }
    };
    //call on payment result success return
    private xyz.leezoom.grain.util.NetWorkTask.NetWorkListener pListener = new xyz.leezoom.grain.util.NetWorkTask.NetWorkListener() {
        @Override
        public void onSuccess() {
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            final String cardData = MyBase64.BASE64ToString(query.getString(PAYMENT.name(),"none"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String [] allPayment = cardData.split("\n");
                    for (String e : allPayment) {
                        String [] singlePay = e.split(PackMessage.SplitFields);
                        Card card1 = new Card();
                        card1.setTime(singlePay[1].replace(" ","\n"));
                        card1.setPlace(singlePay[12]);
                        card1.setTerminal(singlePay[9].replace("\r",""));
                        card1.setConsume(singlePay[4].contains("-")?singlePay[4]:"+"+singlePay[4]);
                        card1.setBalance(singlePay[5]);
                        moneyList.add(card1);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterData.clear();
                            adapterData.addAll(moneyList);
                            adapter.notifyDataSetChanged();
                            moneyListView.invalidateViews();
                            moneyList.clear();
                        }
                    });
                }
            }).start();
            showProgress(false);
        }

        @Override
        public void onFailed() {
        }
    };

    public CardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        ButterKnife.bind(this,view);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_b);
        fab.setVisibility(View.GONE);
        showProgress(true);
        adapter = new MoneyListAdapter(getContext(),adapterData);
        moneyListView.setAdapter(adapter);
        mUserPic.setImageResource(R.color.pink_700);
        info = getActivity().getSharedPreferences("info",Context.MODE_PRIVATE);
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
        user.setExtend(account);
        user.setPassword(pass);
        user.setToken(MyBase64.BASE64ToString(query.getString("ttt","none")));
        user.setHostInfo(hostInfo);
        //get base info
        baseTask = new xyz.leezoom.grain.util.NetWorkTask(user, BASEINFO, ServerIp.cardServerPort, bListener, getContext());
        baseTask.execute((Void)null);
        //set more info to list
        initList();
        adapter.notifyDataSetChanged();
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        showProgress(false);
        FragmentUtil.showFailedPage(getActivity(), false, this);
        failedFragment = null;
        baseTask.cancel(true);
        payTask.cancel(true);
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
    }


    //show progress bar
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUserPic.setVisibility(show ? View.GONE : View.VISIBLE);
            mUserPic.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUserPic.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mUserPic.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void initList(){
        //test
        //Card card =new Card();
        //card.setPlace("长治食堂一楼");
        //card.setTerminal("44");
        //card.setTime("2017-9-6");
        //card.setConsume("-1.0");
        //card.setBalance("217.22");
        //moneyList.add(card);
        //NetWorkTask payTask = new NetWorkTask(user,PAYMENT);
        payTask = new xyz.leezoom.grain.util.NetWorkTask(user, PAYMENT, ServerIp.cardServerPort, pListener, getContext());
        payTask.execute((Void)null);
    }
}
