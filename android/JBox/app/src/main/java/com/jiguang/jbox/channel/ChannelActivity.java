package com.jiguang.jbox.channel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.util.HttpUtil;
import com.jiguang.jbox.view.TopBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 订阅 channel 界面。
 */
public class ChannelActivity extends Activity {
    private final String TAG = "ChannelActivity";

    public static final String EXTRA_DEV_KEY = "dev_key";

    private static final int MSG_DEV_UPDATE = 0;

    private SubChannelListAdapter mListAdapter;

    private List<Channel> mChannels = new ArrayList<>();

    private List<Channel> mLocalChannels;

    private Set<String> mTags = new HashSet<>();  // 订阅 Channel 的 tag

    private Handler mHandler;

    // head view.
    private TextView mTvDevName;
    private TextView mTvDevDesc;
    private CircleImageView mIvAvatar;

    private String mDevKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        mDevKey = getIntent().getStringExtra(EXTRA_DEV_KEY);

        TopBar topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setLeftClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBack();
            }
        });

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MSG_DEV_UPDATE) {       // 获取用户信息后，更新界面。
                    Bundle data = msg.getData();

                    mTvDevName.setText(data.getString("devName"));

                    if (!TextUtils.isEmpty(data.getString("avatarUrl"))) {
                        Glide.with(AppApplication.getAppContext())
                                .load(data.getString("avatarUrl"))
                                .placeholder(R.drawable.default_avatar)
                                .dontAnimate()
                                .into(mIvAvatar);
                    }

                    if (!TextUtils.isEmpty(data.getString("desc"))) {
                        mTvDevDesc.setText(data.getString("desc"));
                    }
                }
                return false;
            }
        });

        ListView listView = (ListView) findViewById(R.id.lv_channel);

        // Init head view.
        View headView = getLayoutInflater().inflate(R.layout.view_subscribe_channel, listView, false);
        mIvAvatar = (CircleImageView) headView.findViewById(R.id.iv_dev_icon);
        mTvDevName = (TextView) headView.findViewById(R.id.tv_name);
        mTvDevDesc = (TextView) headView.findViewById(R.id.tv_desc);
        listView.addHeaderView(headView);

        mListAdapter = new SubChannelListAdapter(mChannels, new OnChannelCheckedListener() {
            @Override
            public void onChannelChecked(int position, boolean isChecked) {
                mChannels.get(position).isSubscribe = isChecked;
            }
        });
        listView.setAdapter(mListAdapter);

        // 初始化开发者信息。
        HttpUtil.getInstance().requestDeveloper(mDevKey, new DeveloperDataSource.LoadDevCallback() {
            @Override
            public void onDevLoaded(Developer dev) {
                android.os.Message msg = new android.os.Message();
                msg.what = MSG_DEV_UPDATE;
                Bundle bundle = new Bundle();
                bundle.putString("devName", dev.name);
                bundle.putString("desc", dev.desc);
                bundle.putString("avatarUrl", dev.avatarUrl);
                msg.setData(bundle);
                msg.setTarget(mHandler);
                msg.sendToTarget();

                dev.save();
            }

            @Override
            public void onDataNotAvailable() {
                Toast.makeText(getApplicationContext(), R.string.channel_error_dev,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // 从本地数据库中进行查询。
        mLocalChannels = new Select().from(Channel.class).where("DevKey = ?", mDevKey).execute();
        if (mLocalChannels != null) {
            mListAdapter.replaceData(mLocalChannels);
        }

        if (!HttpUtil.isNetworkAvailable() && mLocalChannels != null) {
            mListAdapter.replaceData(mLocalChannels);
        } else {
            // 服务器端的 Channel 列表，要和本地数据库中的做对比。
            HttpUtil.getInstance().requestChannels(mDevKey,
                    new ChannelDataSource.LoadChannelsCallback() {
                        @Override
                        public void onChannelsLoaded(List<Channel> channels) {
                            if (channels == null) {
                                return;
                            }

                            if (mLocalChannels == null) {
                                mListAdapter.replaceData(mChannels);
                                return;
                            }

                            mChannels = channels;

                            // 将本地数据和服务器数据做对比，同步订阅状态。
                            for (Channel channel : mChannels) {
                                for (Channel localChannel : mLocalChannels) {
                                    if (localChannel.name.equals(channel.name)) {
                                        channel.isSubscribe = localChannel.isSubscribe;
                                    }
                                }
                            }
                            mListAdapter.replaceData(mChannels);
                        }

                        @Override
                        public void onDataNotAvailable() {
                            Toast.makeText(getApplicationContext(), R.string.channel_error_http,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onBack();   // 当点击回退键,保存数据,并打上 tag。
        super.onBackPressed();
    }

    /**
     * 将数据保存到数据库中，并打上 JPush TAG。
     */
    private void onBack() {
        if (!mChannels.equals(mLocalChannels)) {
            new Delete().from(Channel.class).where("DevKey = ?", mDevKey).execute();

            boolean firstSubscribe = true;

            AppApplication.currentChannelName = "";

            for (Channel c : mChannels) {
                if (c.isSubscribe) {
                    if (firstSubscribe) {
                        AppApplication.currentChannelName = c.name;
                        firstSubscribe = false;
                    }
                    mTags.add(c.devKey + "_" + c.name);
                }
                c.save();
            }

            JPushInterface.setTags(this, mTags, new TagAliasCallback() {
                @Override
                public void gotResult(int result, String desc, Set<String> set) {
                    if (result == 0) {
                        Toast.makeText(getApplicationContext(), R.string.channel_success,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.channel_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            AppApplication.shouldUpdateData = true;
        }
        finish();
    }

}
