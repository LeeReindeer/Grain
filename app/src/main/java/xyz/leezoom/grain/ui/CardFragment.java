package xyz.leezoom.grain.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Card;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment {

    // FIXME: 9/6/17 get card info failed

    @BindView(R.id.cd_recent_money_list) ListView moneyListView;
    @BindView(R.id.cd_pic) CircleImageView mUserPIc;
    @BindView(R.id.cd_name) TextView mName;
    @BindView(R.id.cd_status) Button mStatus;
    @BindView(R.id.cd_money) Button mBalance;

    private List<Card> moneyList = new ArrayList<>();
    private MoneyListAdapter adapter;

    public CardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        ButterKnife.bind(this,view);
        initList();
        adapter = new MoneyListAdapter(getContext(),moneyList);
        moneyListView.setAdapter(adapter);
        mUserPIc.setImageResource(R.color.pink_700);
        mName.setText("Lee");
        mStatus.setText("有效卡");
        mBalance.setText("217.22");
        return  view;
    }

    private void initList(){
        //test
        Card card =new Card();
        card.setPlace("长治食堂一楼");
        card.setTerminal("44");
        card.setTime("2017-9-6");
        card.setConsume("-1.0");
        card.setBalance("217.22");
        moneyList.add(card);
    }

}
