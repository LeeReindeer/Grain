/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/7/17 5:57 PM
 */

package xyz.leezoom.grain.ui;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Card;

/**
 * @Author lee
 * @Time 9/6/17.
 */

public class MoneyListAdapter extends ArrayAdapter<Card> implements ListAdapter {
    public MoneyListAdapter(@NonNull Context context, @NonNull List<Card> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Card card = getItem(position);
        viewHolder.mTime.setText(card.getTime());
        viewHolder.mPlace.setText(card.getPlace());
        viewHolder.mTerminal.setText(card.getTerminal());
        viewHolder.mConsume.setText(card.getConsume());
        viewHolder.mBalance.setText(card.getBalance());
        return convertView;
    }

    public  static  class ViewHolder{
        @BindView(R.id.cd_time_item)TextView mTime;
        @BindView(R.id.cd_place_item) TextView mPlace;
        @BindView(R.id.cd_terminal_item) TextView mTerminal;
        @BindView(R.id.cd_consume_item) TextView mConsume;
        @BindView(R.id.cd_balance_item) TextView mBalance;
        public  ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
