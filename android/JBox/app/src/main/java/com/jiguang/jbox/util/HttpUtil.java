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

import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    private Context mContext;

    public static HttpUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new HttpUtil(context);
        }
        return INSTANCE;
    }

    private HttpUtil(Context context) {
        mHttpClient = new OkHttpClient();
        mContext = context;
    }

    // 请求开发者数据。
    public void requestDevelopers(String devKey, final DeveloperDataSource.LoadDevCallback callback) {
        final Resources resources = mContext.getResources();
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
//                                String avatar = getAvatarFromServer(json.getString("avatar"),
//                                        json.getString("dev_key"));
//                                dev.setAvatarPath(avatar);
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
     * 返回查询到的 Channel 名称。
     *
     * @param devKey
     * @return Channel 名称列表。
     */
    public List<String> requestChannels(String devKey) {
        if (TextUtils.isEmpty(devKey)) {
            return null;
        }

        Resources resources = mContext.getResources();
        String url = String.format(resources.getString(R.string.url_get_channels), devKey);

        byte[] devKeyBase64 = Base64.decode(devKey, Base64.DEFAULT);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
            if (response.isSuccessful()) {
                String body = response.body().string();
                JSONArray jsonArr = new JSONArray(body);
                List<String> channels = new ArrayList<>();
                for (int i = 0; i < jsonArr.length(); i++) {
                    String channelName = jsonArr.getString(i);
                    channels.add(channelName);
                }
                return channels;
            } else {
                LogUtil.LOGE(TAG, "Unexpected code " + response);
                return null;
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

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
