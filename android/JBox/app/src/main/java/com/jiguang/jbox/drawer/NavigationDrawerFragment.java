package com.jiguang.jbox.drawer;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.drawer.adapter.DrawerViewPagerAdapter;
import com.jiguang.jbox.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.jiguang.jbox.R.id.viewPager;


/**
 * 侧滑界面。
 */
public class NavigationDrawerFragment extends Fragment
        implements DeveloperListFragment.OnListFragmentInteractionListener {

    private ViewPager mViewPager;

    private ImageView mIvCircleFirst;
    private ImageView mIvCircleSecond;

    public DeveloperListFragment devListFragment;
    public ChannelListFragment channelListFragment;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer_main, container, false);

        mIvCircleFirst = (ImageView) v.findViewById(R.id.iv_circle_first);
        mIvCircleSecond = (ImageView) v.findViewById(R.id.iv_circle_second);
        mIvCircleFirst.setSelected(true);

        List<Fragment> fragmentList = new ArrayList<>(2);

        devListFragment = new DeveloperListFragment();
        devListFragment.setListener(this);
        fragmentList.add(devListFragment);

        channelListFragment = new ChannelListFragment();
        fragmentList.add(channelListFragment);

        final OnDrawerPageChangeListener drawerPageChangeListener = (MainActivity) getActivity();

        FragmentManager fm = getActivity().getSupportFragmentManager();

        mViewPager = (ViewPager) v.findViewById(viewPager);
        mViewPager.setAdapter(new DrawerViewPagerAdapter(fm, fragmentList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                drawerPageChangeListener.onPageSelected(position);

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
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDevListItemClick(String devKey) {
        channelListFragment.updateData(devKey);
        mViewPager.setCurrentItem(1);
    }

    public void updateData() {
        devListFragment.updateData();
        channelListFragment.updateData(AppApplication.currentDevKey);
    }

    /**
     * 监听侧边栏的页面选择
     */
    public interface OnDrawerPageChangeListener {

        /**
         * @param position  Position index of the new selected page.
         */
        void onPageSelected(int position);
    }
}
