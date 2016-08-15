package com.jiguang.jbox.channel;

import com.jiguang.jbox.BasePresenter;

public interface ChannelsContract {

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadChannels(boolean foreceUpdate);
    }

}
