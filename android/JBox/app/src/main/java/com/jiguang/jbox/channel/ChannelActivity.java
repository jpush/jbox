package com.jiguang.jbox.channel;

import android.app.Activity;
import android.os.Bundle;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jiguang.jbox.R;

public class ChannelActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.smlv_channel);
    }

}
