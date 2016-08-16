package com.jiguang.jbox.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiguang.jbox.R;
import com.jiguang.jbox.account.AccountFragment;
import com.jiguang.jbox.message.MessageFragment;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private TextView mTvTitle;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mTvTitle = (TextView) v.findViewById(R.id.tv_title);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(MessageFragment.newInstance());
        fragments.add(AccountFragment.newInstance());

        TabsAdapter tabsAdapter = new TabsAdapter(fm);
        tabsAdapter.initData(fragments);

        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
        mViewPager.setAdapter(tabsAdapter);
        mViewPager.addOnPageChangeListener(this);

        TextView tvMessage = (TextView) v.findViewById(R.id.tv_message);
        TextView tvAccount = (TextView) v.findViewById(R.id.tv_account);

        tvMessage.setOnClickListener(this);
        tvAccount.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_message:
                if (mViewPager.getCurrentItem() != 0) {
                    mViewPager.setCurrentItem(0);
                }
                break;
            case R.id.tv_account:
                if (mViewPager.getCurrentItem() != 1) {
                    mViewPager.setCurrentItem(1);
                }
                break;
            default:
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            mTvTitle.setText(getResources().getString(R.string.message));
        } else if (position == 1) {
            mTvTitle.setText(getResources().getString(R.string.account));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
