package com.jiguang.jbox.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Channel;
import com.jiguang.jbox.data.Developer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.google.common.base.Preconditions.checkNotNull;

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
                Developer dev = new Developer(json.getString("dev_key"), json.getString("dev_name"),
                        json.getString("platform"));
                dev.setDesc(json.getString("desc"));

                // 从服务器下载头像。
                if (!TextUtils.isEmpty(json.getString("avatar"))) {
                    String avatar = getAvatarFromServer(json.getString("avatar"),
                            json.getString("dev_key"));
                    dev.setAvatarPath(avatar);
                }

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

    private String getAvatarFromServer(@NonNull final String url, @NonNull String fileName) {
        checkNotNull(url);
        checkNotNull(fileName);

        try {
            URL u = new URL(url);
            Bitmap bitmap = BitmapFactory.decodeStream(u.openConnection().getInputStream());

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/JBox/avatar";

            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "avatar_" + fileName + ".png");
            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
