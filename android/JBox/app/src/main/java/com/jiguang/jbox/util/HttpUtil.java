package com.jiguang.jbox.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;

import com.activeandroid.query.Select;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.data.source.DeveloperDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class HttpUtil {

    private static HttpUtil INSTANCE;

    private OkHttpClient mHttpClient;

    public static HttpUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpUtil();
        }
        return INSTANCE;
    }

    private HttpUtil() {
        mHttpClient = new OkHttpClient();
    }

    // 请求开发者数据。
    public void requestDeveloper(final String devKey, final DeveloperDataSource.LoadDevCallback callback) {
        final Resources resources = AppApplication.getAppContext().getResources();
        String url = String.format(resources.getString(R.string.url_get_developers), devKey);

        try {
            // TODO: 用作校验，目前暂时不用。
            String devKeyBase64 = Base64.encodeToString(devKey.getBytes("UTF-8"), Base64.NO_WRAP);

            final Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = mHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onDataNotAvailable();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String body = response.body().string();

                        try {
                            JSONObject json = new JSONObject(body);

                            Developer dev = new Developer();
                            dev.key = devKey;
                            dev.name = json.getString("dev_name");
                            dev.platform = json.getString("platform");
                            dev.desc = json.getString("desc");
                            dev.avatarUrl = "http://" + json.getString("avatar");
                            if (TextUtils.isEmpty(AppApplication.currentDevKey)) {
                                dev.isSelected = true;
                                AppApplication.currentDevKey = devKey;
                            } else {
                                dev.isSelected = false;
                            }

                            Developer localDev = new Select().from(Developer.class)
                                    .where("Key = ?", devKey)
                                    .executeSingle();

                            if (localDev != null) {
                                if (!localDev.equals(dev)) {
                                    localDev.name = dev.name;
                                    localDev.platform = dev.platform;
                                    localDev.desc = dev.desc;
                                    localDev.avatarUrl = dev.avatarUrl;
                                }
                                callback.onDevLoaded(localDev);
                            } else {
                                callback.onDevLoaded(dev);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onDataNotAvailable();
                        } finally {
                            response.close();
                        }
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回查询到的 Channel 名称列表。
     *
     * @param devKey 目标开发者的 dev key。
     */
    public void requestChannels(final String devKey, final ChannelDataSource.LoadChannelsCallback callback) {
        if (TextUtils.isEmpty(devKey)) {
            return;
        }

        Resources resources = AppApplication.getAppContext().getResources();
        String url = String.format(resources.getString(R.string.url_get_channels), devKey);

        byte[] devKeyBase64 = Base64.decode(devKey, Base64.DEFAULT);

        Request request = new Request.Builder()
                .url(url)
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onDataNotAvailable();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String body = response.body().string();

                        LogUtil.LOGI(TAG, body);

                        JSONObject channelsJson = new JSONObject(body);
                        JSONArray jsonArr = channelsJson.getJSONArray("channels");

                        List<Channel> channels = new ArrayList<>();

                        for (int i = 0; i < jsonArr.length(); i++) {
                            Channel c = new Channel();
                            c.devKey = devKey;
                            c.name = jsonArr.getString(i);
                            c.isSubscribe = true;
                            channels.add(c);
                        }

                        callback.onChannelsLoaded(channels);
                    } else {
                        LogUtil.LOGE(TAG, "Unexpected code " + response);
                        callback.onDataNotAvailable();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
            }
        });
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) AppApplication.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
