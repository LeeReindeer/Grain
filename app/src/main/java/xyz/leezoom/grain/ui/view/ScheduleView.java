
/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/23/17 10:58 PM
 */

package xyz.leezoom.grain.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

/**
 * @Author lee
 * @Time 9/23/17.
 */
// TODO: 10/8/17
public class ScheduleView extends ScrollView {

    private final static int DAY_TEXT_HEIGHT = 50;
    private Paint paint;
    private Path path;

    private EditText editView;

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
    }

    public EditText setInputEdit(EditText e) {
        editView = e;
        return editView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(0);
        //hide view if keyBoard is shown.
        InputMethodManager input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!input.isActive(editView)) {
            int singleWidth = getWidth() / 8;
            int singleHeight = (getHeight() - DAY_TEXT_HEIGHT) / 12;
            //draw vertical line
            for (int i = 1; i <= 7; i++) {
                path.moveTo(singleWidth * i, 0);
                path.lineTo(singleWidth * i, getHeight());
            }
            canvas.save();

            //draw horizontal line
            path.moveTo(0, DAY_TEXT_HEIGHT);
            path.lineTo(getWidth(), DAY_TEXT_HEIGHT);
            for (int i = 1; i <= 11; i++) {
                path.moveTo(0, DAY_TEXT_HEIGHT + singleHeight * i);
                path.lineTo(getWidth(), DAY_TEXT_HEIGHT + singleHeight * i);

            }
            canvas.save();

            //draw text
            paint.setTextSize(40);
            for (int i = 1; i <= 12; i++) {
                canvas.drawText(String.valueOf(i), DAY_TEXT_HEIGHT, singleHeight * i, paint);
            }

            canvas.drawPath(path, paint);
        }
    }
}
