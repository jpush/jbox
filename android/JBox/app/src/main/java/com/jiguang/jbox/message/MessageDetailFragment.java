package com.jiguang.jbox.message;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiguang.jbox.R;

public class MessageDetailFragment extends Fragment {
    public MessageDetailFragment() {
        // Required empty public constructor
    }

    public static MessageDetailFragment newInstance() {
        MessageDetailFragment fragment = new MessageDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message_detail, container, false);

        return v;
    }

}
