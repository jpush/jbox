package com.jiguang.jbox.ui.scan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.jiguang.jbox.AppApplication;
import com.jiguang.jbox.R;
import com.jiguang.jbox.ui.channel.ChannelActivity;
import com.jiguang.jbox.data.model.Channel;
import com.jiguang.jbox.data.model.Developer;
import com.jiguang.jbox.data.source.DeveloperDataSource;
import com.jiguang.jbox.ui.main.MainActivity;
import com.jiguang.jbox.util.AppUtil;
import com.jiguang.jbox.util.HttpUtil;
import com.jiguang.jbox.util.LogUtil;
import com.jiguang.jbox.util.PermissionUtil;
import com.jiguang.jbox.view.TopBar;

import java.util.Set;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.jpush.android.api.TagAliasCallback;


/**
 * 二维码扫描界面。
 */
public class ScanActivity extends Activity implements QRCodeView.Delegate {
    
    private final String TAG = LogUtil.makeLogTag(ScanActivity.class);

    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 2;

    private QRCodeView mScanView;

    private String mDevKey;
    private String mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        requestPermission();

        TopBar topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setRightClick(new View.OnClickListener() {   // 打开相册,并选择图片扫描二维码。
            @Override
            public void onClick(View view) {
                startActivityForResult(BGAPhotoPickerActivity.newIntent(
                        getApplicationContext(), null, 1, null, false),
                        REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
            }
        });

        mScanView = (QRCodeView) findViewById(R.id.scanView);
        mScanView.setDelegate(this);
    }

    @Override
    protected void onResume() {
        if (PermissionUtil.hasPermission(this, Manifest.permission.CAMERA)) {
            mScanView.startCamera();
            mScanView.startSpot();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        mScanView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mScanView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }

        if (!PermissionUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, R.string.scan_permission, Toast.LENGTH_SHORT).show();

            PermissionUtil.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        if (result.contains("_")) { // 扫描的是 channel，马上订阅。
            String[] str = result.split("_");
            mDevKey = str[0];
            mChannel = str[1];

            if (!new Select().from(Developer.class).where("Key = ?", mDevKey).exists()) {
                HttpUtil.getInstance().requestDeveloper(mDevKey,
                        new DeveloperDataSource.LoadDevCallback() {
                            @Override
                            public void onDevLoaded(Developer dev) {
                                dev.save();

                                Bundle bundle = new Bundle();
                                bundle.putString("DevKey", dev.key);

                                Message msg = new Message();
                                msg.what = MainActivity.MSG_WHAT_UPDATE_DEV;
                                msg.setData(bundle);
                                msg.setTarget(MainActivity.handler);
                                msg.sendToTarget();
                            }

                            @Override
                            public void onDataNotAvailable() {
                                Toast.makeText(AppApplication.getAppContext(),
                                        R.string.scan_qrcode_invalid, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            saveData(mDevKey, mChannel);
            AppApplication.shouldUpdateData = true;
        } else {
            // 扫描二维码返回 devKey，再请求 developer 信息。
            Intent intent = new Intent(ScanActivity.this, ChannelActivity.class);
            intent.putExtra(ChannelActivity.EXTRA_DEV_KEY, result);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, R.string.scan_open_camer_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mScanView.showScanRect();

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(ScanActivity.this, R.string.scan_qrcode_not_found,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        onScanQRCodeSuccess(result);
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScanView.startCamera();
                    mScanView.startSpot();
                }
                return;
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!TextUtils.isEmpty(mDevKey) && !TextUtils.isEmpty(mChannel)) {
                        saveData(mDevKey, mChannel);
                    }
                }
                return;
            default:
        }
    }

    /**
     * 请求拍照权限和写外部存储权限。
     */
    private void requestPermission() {
        if (!PermissionUtil.hasPermission(this, Manifest.permission.CAMERA)) {
            PermissionUtil.requestPermission(this, Manifest.permission.CAMERA, PERMISSION_REQUEST_CAMERA);
        }

        if (!PermissionUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtil.requestPermission(this, Manifest.permission.CAMERA,
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void saveData(String devKey, String channelName) {
        Channel c = new Select().from(Channel.class)
                .where("DevKey = ? AND Name = ?", devKey, channelName)
                .executeSingle();

        if (c == null) {    // 本地不存在，进行订阅。
            c = new Channel();
            c.devKey = devKey;
            c.name = channelName;
            c.isSubscribe = true;
            c.save();

            AppUtil.setTags(new TagAliasCallback() {
                @Override
                public void gotResult(int status, String desc, Set<String> set) {
                    if (status == 0) {
                        Toast.makeText(ScanActivity.this, R.string.scan_subscribe_success,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        LogUtil.LOGI(TAG, desc);
                    }
                }
            });
        } else if (!c.isSubscribe) {
            c.isSubscribe = true;
            c.save();

            AppUtil.setTags(new TagAliasCallback() {
                @Override
                public void gotResult(int status, String desc, Set<String> set) {
                    if (status == 0) {
                        Toast.makeText(ScanActivity.this, R.string.scan_subscribe_success,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ScanActivity.this, R.string.scan_subscribe_fail,
                                Toast.LENGTH_SHORT).show();
                        LogUtil.LOGI(TAG, desc);
                    }
                }
            });
        }
    }
}
