/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/27/17 6:35 PM
 */

package xyz.leezoom.grain.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.leezoom.grain.R;

/**
 * @Author lee
 * @Time 9/29/17.
 */

public class BigCharView extends FrameLayout {


    private int mColor = Color.BLUE;
    private String mFirstCHar = "A";

    @BindView(R.id.bc_logo)
    CircleImageView bcLogo;
    @BindView(R.id.bc_first_char)
    TextView bcFirstChar;
    @BindView(R.id.bc_root_view)
    FrameLayout bcRootView;

    public BigCharView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BigCharView);
        mColor = typedArray.getColor(R.styleable.BigCharView_color, Color.BLUE);
        mFirstCHar = typedArray.getString(R.styleable.BigCharView_first_char);
        typedArray.recycle();
        initView(context);
    }

    @SuppressLint("ResourceType")
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.big_char, this, true);
        ButterKnife.bind(this, view);
        bcLogo.setImageResource(mColor);
        bcFirstChar.setText(mFirstCHar);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        bcLogo.setImageResource(mColor);
    }

    public String getFirstCHar() {
        return mFirstCHar;
    }

    public void setFirstCHar(String mFirstCHar) {
        bcFirstChar.setText(mFirstCHar);
    }
}
