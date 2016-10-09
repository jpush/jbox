package com.jiguang.jbox.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.ChannelDataSource;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
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
    public void requestDeveloper(final String devKey, final DeveloperDataSource.LoadDevCallback callback) {
        final Resources resources = AppApplication.getAppContext().getResources();
        String url = String.format(resources.getString(R.string.url_get_developers), devKey);

        try {
            // TODO: 用作校验，目前暂时不用。
            String devKeyBase64 = Base64.encodeToString(devKey.getBytes("UTF-8"), Base64.NO_WRAP);

            final Request request = new Request.Builder()
                    .url(url)
                    .build();

            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onDataNotAvailable();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String body = response.body().string();

                        LogUtil.LOGI(TAG, body);

                        try {
                            JSONObject json = new JSONObject(body);
                            Developer dev = new Developer(json.getString("dev_key"),
                                    json.getString("dev_name"), json.getString("platform"));
                            dev.setDesc(json.getString("desc"));

                            // 从服务器下载头像。
                            if (!TextUtils.isEmpty(json.getString("avatar"))) {
                                String url = "http://" + json.getString("avatar");
                                String fileName = "avatar_" + devKey + ".png";

                                ImageLoader imgLoader = ImageLoader.getInstance();
                                Bitmap avatarBitmap = imgLoader.loadImageSync(url);

                                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        "/jbox/avatar/";
                                File dir = new File(dirPath);
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                File avatarFile = new File(dir, fileName);
                                FileOutputStream out = new FileOutputStream(avatarFile);
                                avatarBitmap.compress(Bitmap.CompressFormat.PNG, 85,out);

                                out.flush();
                                out.close();

                                dev.setAvatarPath(avatarFile.getAbsolutePath());
                            }

                            callback.onDevLoaded(dev);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onDataNotAvailable();
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
     * @param devKey
     * @return Channel 名称列表。
     */
    public void requestChannels(String devKey,
                                final ChannelDataSource.LoadChannelsNameCallback callback) {
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

                        JSONObject channelsJson = new JSONObject(body);
                        List<String> channels = new ArrayList<>();

                        JSONArray jsonArr = channelsJson.getJSONArray("channels");
                        for (int i = 0; i < jsonArr.length(); i++) {
                            String channelName = jsonArr.getString(i);
                            channels.add(channelName);
                        }

                        callback.onChannelsNameLoaded(channels);
                    } else {
                        LogUtil.LOGE(TAG, "Unexpected code " + response);
                        callback.onDataNotAvailable();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
