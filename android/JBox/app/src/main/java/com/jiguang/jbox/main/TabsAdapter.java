package com.jiguang.jbox.main;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {
    private List<Fragment> mTabs = new ArrayList<Fragment>();

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void initData(List<Fragment> list) {
        mTabs = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }
}
