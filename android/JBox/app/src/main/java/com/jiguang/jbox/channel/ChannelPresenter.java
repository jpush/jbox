package com.jiguang.jbox.channel;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelsRepository;

public class ChannelPresenter implements ChannelsContract.Presenter {

    private final ChannelsRepository mChannelsRepository;

    private final ChannelsContract.View mChannelsView;

    private boolean mFirstLoad = true;

    public ChannelPresenter(@NonNull ChannelsRepository repository,
                            @NonNull ChannelsContract.View channelView) {
        mChannelsRepository = repository;
        mChannelsView = channelView;

        mChannelsView.setPresenter(this);
    }


    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadChannels(boolean forceUpdate) {

    }

    @Override
    public void subscribe() {

    }

    @Override
    public void openChannelMessages(@NonNull Channel channel) {

    }

    @Override
    public void start() {

    }
}
