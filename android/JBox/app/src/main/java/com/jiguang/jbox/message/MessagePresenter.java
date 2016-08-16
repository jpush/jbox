package com.jiguang.jbox.message;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelsRepository;

public class MessagePresenter implements MessageContract.Presenter {

    private final ChannelsRepository mChannelsRepository;

    private final MessageContract.View mView;

    public MessagePresenter(@NonNull ChannelsRepository repository,
                            @NonNull MessageContract.View view) {
        mChannelsRepository = repository;
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void loadChannels(boolean forceUpdate) {

    }

    @Override
    public void openChannelDetail(@NonNull Channel channel) {

    }

    @Override
    public void start() {

    }
}
