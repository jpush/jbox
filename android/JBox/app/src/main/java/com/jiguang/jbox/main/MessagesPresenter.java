package com.jiguang.jbox.main;

import android.support.annotation.NonNull;

import com.jiguang.jbox.data.Message;
import com.jiguang.jbox.data.source.MessageDataSource;
import com.jiguang.jbox.data.source.MessageRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI, receives the data and updates the UI as required.
 */
public class MessagesPresenter implements MessagesContract.Presenter {

    private final MessageRepository mRepository;

    private final MessagesContract.View mMessagesView;

    private boolean mFirstLoaded = true;

    public MessagesPresenter(@NonNull MessageRepository repository,
                             @NonNull MessagesContract.View view) {
        mRepository = checkNotNull(repository);
        mMessagesView = checkNotNull(view);

        mMessagesView.setPresenter(this);
    }

    @Override
    public void loadMessages(String devKey, String channelName, boolean forceUpdate) {
        loadMessages(devKey, channelName, forceUpdate || mFirstLoaded, false);
        mFirstLoaded = false;
    }

    private void loadMessages(String devKey, String channelName, boolean forceUpdate,
                              boolean showLoadingUI) {
        mRepository.getMessages(devKey, channelName, new MessageDataSource.LoadMessagesCallback() {
            @Override
            public void onMessagesLoaded(List<Message> messages) {
                if (!mMessagesView.isActive()) {
                    return;
                }
                if (!messages.isEmpty()) {
                    mMessagesView.showMessages(messages);
                }
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void saveMessage(@NonNull Message msg) {
        checkNotNull(msg);
        mRepository.saveMessage(msg);
    }

    @Override
    public void start() {

    }

}
