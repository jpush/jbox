package com.jiguang.jbox.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jiguang.jbox.R;

public class TopBar extends FrameLayout {

    private TextView mTvTitle;
    private TextView mTvLeft;
    private TextView mTvRight;

    public TopBar(Context context) {
        super(context);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.topbar, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopBar, 0, 0);

        String title = a.getString(R.styleable.TopBar_topbar_title);
        String rightText = a.getString(R.styleable.TopBar_right);
        boolean isShowLeft = a.getBoolean(R.styleable.TopBar_show_left, false);
        boolean isShowRight = a.getBoolean(R.styleable.TopBar_show_right, false);
        int bgLeft = a.getResourceId(R.styleable.TopBar_background_left, 0);

        mTvLeft = (TextView) findViewById(R.id.tv_left);
        if (bgLeft != 0) {
            mTvLeft.setText(null);
            mTvLeft.setBackgroundResource(bgLeft);
        }

        if (isShowLeft) {
            mTvLeft.setVisibility(View.VISIBLE);
            mTvLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            });
        }

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(title);

        mTvRight = (TextView) findViewById(R.id.tv_right);
        if (isShowRight) {
            mTvRight.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(rightText)) {
            mTvRight.setText(rightText);
        }

    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setRightClick(OnClickListener listener) {
        mTvRight.setOnClickListener(listener);
    }

    public void setLeftClick(OnClickListener listener) {
        mTvLeft.setOnClickListener(listener);
    }

}
