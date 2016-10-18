package com.jiguang.jbox.main;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;

import java.util.ArrayList;
import java.util.List;

import static com.jiguang.jbox.R.id.viewPager;


/**
 * 侧滑界面。
 */
public class NavigationDrawerFragment extends Fragment
        implements DeveloperListFragment.OnListFragmentInteractionListener {
    private final String TAG = "NavigationDrawerFragment";

    private ViewPager mViewPager;

    private ImageView mIvCircleFirst;
    private ImageView mIvCircleSecond;

    public DeveloperListFragment mDevListFragment;
    public ChannelListFragment mChannelListFragment;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer_main, container, false);

        mIvCircleFirst = (ImageView) v.findViewById(R.id.iv_circle_first);
        mIvCircleFirst.setSelected(true);
        mIvCircleSecond = (ImageView) v.findViewById(R.id.iv_circle_second);

        List<android.support.v4.app.Fragment> fragmentList = new ArrayList<>();

        mDevListFragment = new DeveloperListFragment();
        mDevListFragment.setListener(this);
        fragmentList.add(mDevListFragment);

        mChannelListFragment = new ChannelListFragment();
        fragmentList.add(mChannelListFragment);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        mViewPager = (ViewPager) v.findViewById(viewPager);
        mViewPager.setAdapter(new MyViewPagerAdapter(fm, fragmentList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mIvCircleFirst.setSelected(true);
                    mIvCircleSecond.setSelected(false);
                } else {
                    mIvCircleFirst.setSelected(false);
                    mIvCircleSecond.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDevListItemClick(String devKey) {
        mChannelListFragment.updateData(devKey);
        mViewPager.setCurrentItem(1);
    }

    public void updateData() {
        mDevListFragment.updateData();
        mChannelListFragment.updateData(AppApplication.currentDevKey);
    }

    static class MyViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        MyViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
