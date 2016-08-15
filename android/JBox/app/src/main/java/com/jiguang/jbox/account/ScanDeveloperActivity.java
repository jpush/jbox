package com.jiguang.jbox.account;

import android.app.Activity;
import android.os.Bundle;

import com.jiguang.jbox.R;

public class ScanDeveloperActivity extends Activity {

    public static final int REQUEST_ADD_DEVELOPER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_developer);
    }
}
