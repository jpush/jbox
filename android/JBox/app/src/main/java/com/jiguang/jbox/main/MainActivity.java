package com.jiguang.jbox.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ListView mMsgListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.initData();

        mMsgListView = (ListView) findViewById(R.id.lv_msg);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}
