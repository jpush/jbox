package com.jiguang.jbox.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jiguang.jbox.R;

public class Topbar extends FrameLayout {

    private TextView mTvBack;

    private TextView mTvTitle;

    public Topbar(Context context) {
        super(context);
    }

    public Topbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.topbar, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Toolbar, 0, 0);

        String title = a.getString(R.styleable.Topbar_title);
        boolean isShowBack = a.getBoolean(R.styleable.Topbar_showBack, false);

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
    }

    public Topbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(String title) {
        if (mTvTitle != null) {
            mTvTitle.setText(title);
        }
    }

}
