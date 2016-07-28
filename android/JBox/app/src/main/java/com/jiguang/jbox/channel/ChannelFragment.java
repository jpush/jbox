package com.jiguang.jbox.channel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiguang.jbox.R;

public class ChannelFragment extends Fragment {
    public ChannelFragment() {
    }

    public static ChannelFragment newInstance() {
        ChannelFragment fragment = new ChannelFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_channel, container, false);

        return v;
    }
}
