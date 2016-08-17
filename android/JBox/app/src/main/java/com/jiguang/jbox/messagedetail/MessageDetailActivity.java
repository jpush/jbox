package com.jiguang.jbox.messagedetail;

import android.app.Activity;
import android.os.Bundle;

import com.jiguang.jbox.R;

public class MessageDetailActivity extends Activity {
    public static final String EXTRA_CHANNEL_NAME = "CHANNEL_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
    }
}
