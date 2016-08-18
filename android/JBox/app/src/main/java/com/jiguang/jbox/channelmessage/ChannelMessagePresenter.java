package com.jiguang.jbox.channelmessage;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.source.ChannelsDataSource;
import com.jiguang.jbox.data.source.ChannelsRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChannelMessagePresenter implements ChannelMessageContract.Presenter {

    private final ChannelsRepository mChannelsRepository;

    private final ChannelMessageContract.View mView;

    public ChannelMessagePresenter(@NonNull ChannelsRepository channelsRepository,
                                  @NonNull ChannelMessageContract.View view) {
        mChannelsRepository = channelsRepository;
        mView = view;
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    /**
     * 加载所有已订阅的 Channel。
     */
    @Override
    public void loadChannels(boolean forceUpdate) {
        if (forceUpdate) {
            mChannelsRepository.refreshChannels();
        }
        mChannelsRepository.getChannels(new ChannelsDataSource.LoadChannelsCallback() {
            @Override
            public void onChannelsLoaded(List<Channel> channels) {

            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void openChannelMessages(@NonNull Channel channel) {
        checkNotNull(channel);
        mView.showMessagesUi(channel.getName());
    }

    @Override
    public void start() {
        loadChannels(false);
    }

}
