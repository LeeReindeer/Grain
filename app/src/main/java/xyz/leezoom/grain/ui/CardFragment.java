package xyz.leezoom.grain.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Card;
import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIp;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.PackMessage;
import xyz.leezoom.grain.util.TcpUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment {

    // FIXME: 9/6/17 get card info failed

    @BindView(R.id.cd_recent_money_list) ListView moneyListView;
    @BindView(R.id.cd_pic) CircleImageView mUserPic;
    @BindView(R.id.cd_name) TextView mName;
    @BindView(R.id.cd_status) Button mStatus;
    @BindView(R.id.cd_money) Button mBalance;
    @BindView(R.id.cd_progress) ProgressBar mProgressView;

    private List<Card> moneyList = new ArrayList<>();
    private List<Card> adapterData = new ArrayList();
    private MoneyListAdapter adapter;
    private SharedPreferences query;
    private SharedPreferences info;
    private NetWorkTask cTask;
    private User user;
    private Card baseCardInfo;
    private final static QueryType BASEINFO = QueryType.CardUserBaseInfo;
    private final static QueryType PAYMENT = QueryType.CardUserPayment;
    private final static QueryType PICTURE = QueryType.CardUserPicture;

    public CardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        ButterKnife.bind(this,view);
        showProgress(true);
        adapter = new MoneyListAdapter(getContext(),adapterData);
        moneyListView.setAdapter(adapter);
        mUserPic.setImageResource(R.color.pink_700);
        info = getActivity().getSharedPreferences("info",Context.MODE_PRIVATE);
        query = getActivity().getSharedPreferences("query",Context.MODE_PRIVATE);
        String name = MyBase64.BASE64ToString(info.getString("nnn","none"));
        String account = MyBase64.BASE64ToString(info.getString("aaa","none"));
        String idCard = MyBase64.BASE64ToString(info.getString("ppp","none"));
        String pass = idCard.substring(9, 18);
        user = new User();
        user.setName(name);
        user.setAccount(account);
        user.setSchoolId(account);
        user.setCertCard(idCard);
        user.setExtend(account);
        user.setPassword(pass);
        user.setToken(MyBase64.BASE64ToString(query.getString("ttt","none")));
        initList();
        //gte base info
        cTask = new NetWorkTask(user, BASEINFO);
        cTask.execute((Void)null);
        //get user info
        //cTask = new NetWorkTask(user, PICTURE);
        //cTask.execute((Void)null);
        adapter.notifyDataSetChanged();
        return  view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        NetWorkTask payTask = new NetWorkTask(user,PAYMENT);
        payTask.execute((Void)null);
    }

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

            PackMessage packMessage;
            if (queryType == PAYMENT){
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
                System.out.println(ft.format(date));
                packMessage=new PackMessage(user.getAccount(),user.getCertCard(),user.getExtend()+","+ft.format(date)+",0",user.getHostInfo(),user.getOthers(),user.getPassword(),user.getSchoolId(),user.getPhoneNumber(),queryType.name(),user.getToken(),user.getName(),user.getVersion());
            }else {
                packMessage=new PackMessage(user.getAccount(),user.getCertCard(),user.getExtend(),user.getHostInfo(),user.getOthers(),user.getPassword(),user.getSchoolId(),user.getPhoneNumber(),queryType.name(),user.getToken(),user.getName(),user.getVersion());
            }
            TcpUtil tcpUtil= new  TcpUtil(ServerIp.cardServerPort,packMessage);
            String receiveMsg = tcpUtil.receiveString();
            query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = query.edit();
            editor.putString(queryType.name(), MyBase64.stringToBASE64(receiveMsg));
            // commit
            editor.apply();
            return true;
        }

        @Override
        protected void onPostExecute( Boolean success) {
            cTask = null;

            if (success) {
                query = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
                final String cardData = MyBase64.BASE64ToString(query.getString(queryType.name(),"none"));
                if (cardData == null || cardData.equals("false")) {
                    //todo show failed page or send broadcast to show dialog
                    Toast.makeText(getContext(), "Failed.YOu can try to enter new token.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (queryType == BASEINFO){
                    baseCardInfo = new Card();
                    String [] baseInfo = cardData.split(PackMessage.SplitFields);
                    baseCardInfo.setStatus(baseInfo[4]);
                    baseCardInfo.setBalance(baseInfo[12]);
                    baseCardInfo.setName(baseInfo[0]);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mName.setText(baseCardInfo.getName());
                            mStatus.setText(baseCardInfo.getStatus());
                            mBalance.setText(baseCardInfo.getBalance());
                        }
                    });
                }else if (queryType == PAYMENT){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String [] allPayment = cardData.split("\n");
                            for (String e : allPayment) {
                                String [] singlePay = e.split(PackMessage.SplitFields);
                                Card card1 = new Card();
                                card1.setTime(singlePay[1].substring(0,11));
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

                }else if (queryType == PICTURE){

                }
                showProgress(false);
                //add data to list
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
