package com.jiguang.jbox.message;

import android.app.Activity;
import android.os.Bundle;

import com.jiguang.jbox.R;
import com.jiguang.jbox.view.Topbar;

/**
 * 显示 Channel 中的消息列表。
 */
public class MessageActivity extends Activity {
    public static final String EXTRA_CHANNEL_NAME = "CHANNEL_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Init topbar.
        Topbar topbar = (Topbar) findViewById(R.id.topbar);

    }

}
