package com.jiguang.jbox.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jiguang.jbox.R;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
