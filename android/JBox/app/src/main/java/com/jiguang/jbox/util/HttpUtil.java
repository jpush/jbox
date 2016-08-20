package com.jiguang.jbox.util;

import android.content.res.Resources;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    public Developer requestDevelopers(String devKey) {
        Resources resources = AppApplication.getContext().getResources();
        String url = String.format(resources.getString(R.string.url_get_developers), devKey);

        Request request = new Request.Builder().url(url).build();
        try {
            Response response = mHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                Developer dev = new Developer(json.getString("dev_name"), json.getString("dev_key"),
                        json.getString("platform"));
                return dev;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Channel> requestChannels(String devKey) {
        Resources resources = AppApplication.getContext().getResources();
        String url = String.format(resources.getString(R.string.url_get_channels), devKey);

        Request request = new Request.Builder().url(url).build();
        try {
            Response response = mHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                JSONArray jsonArr = new JSONArray(body);
                List<Channel> channels = new ArrayList<>();
                for (int i = 0; i < jsonArr.length(); i++) {
                    String channelName = jsonArr.getString(i);
                    Channel channel = new Channel(channelName);
                    channels.add(channel);
                }
                return channels;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
