package com.jiguang.jbox.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.databinding.FragmentMessageBinding;
import com.jiguang.jbox.messagedetail.MessageDetailActivity;

import java.util.List;

public class MessageFragment extends Fragment implements MessageContract.View {

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMessageBinding binding = FragmentMessageBinding.;

        return binding.getRoot();
    }

    @Override
    public void showChannels(List<Channel> channels) {

    }

    @Override
    public void showChannelMessages(String channelName) {
        Intent intent = new Intent(getContext(), MessageDetailFragment.class);
        intent.putExtra(MessageDetailActivity.EXTRA_CHANNEL_NAME, channelName);
        startActivity(intent);
    }

    @Override
    public void setPresenter(MessageContract.Presenter presenter) {

    }
}
