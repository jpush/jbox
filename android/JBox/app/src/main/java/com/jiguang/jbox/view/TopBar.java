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

    private TextView mTvBack;
    private TextView mTvTitle;
    private TextView mTvRight;

    public TopBar(Context context) {
        super(context);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.topbar, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopBar, 0, 0);

        String title = a.getString(R.styleable.TopBar_tbTitle);
        String rightText = a.getString(R.styleable.TopBar_right);
        boolean isShowBack = a.getBoolean(R.styleable.TopBar_showBack, false);
        boolean isShowRight = a.getBoolean(R.styleable.TopBar_showRight, false);

        mTvBack = (TextView) findViewById(R.id.tv_back);
        if (isShowBack) {
            mTvBack.setVisibility(View.VISIBLE);
            mTvBack.setOnClickListener(new OnClickListener() {
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

    public void setRightText(String rightText) {
        mTvRight.setText(rightText);
    }

    public void setRightClick(OnClickListener listener) {
        mTvRight.setOnClickListener(listener);
    }

}
