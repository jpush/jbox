package com.jiguang.jbox.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.jiguang.jbox.R;
import com.jiguang.jbox.channel.ChannelActivity;
import com.jiguang.jbox.util.LogUtil;
import com.jiguang.jbox.view.TopBar;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;


/**
 * 二维码扫描界面。
 */
public class ScanActivity extends Activity implements QRCodeView.Delegate {

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 0;
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 1;

    private QRCodeView mScanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        TopBar topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setRightClick(new View.OnClickListener() {   // 打开相册,并选择图片扫描二维码。
            @Override
            public void onClick(View view) {
                startActivityForResult(BGAPhotoPickerActivity.newIntent(
                        getApplicationContext(), null, 1, null, false),
                        REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
            }
        });

        mScanView = (QRCodeView) findViewById(R.id.scanview);
        mScanView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 请求拍照权限。
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_PERMISSION_CAMERA);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScanView.startCamera();
        mScanView.startSpot();
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
        LogUtil.LOGI("ScanActivity", "devKey: " + result);
        if (TextUtils.isEmpty(result)) {
            return;
        }

        if (result.contains("_")) { // 扫描的是 channel，马上订阅。

        } else {
            // 扫描二维码返回 devKey，再请求 developer 信息。
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ChannelActivity.EXTRA_DEV_KEY, result);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "打开相机出错", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mScanView.showScanRect();

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            // TODO: 有内存泄漏风险。
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {
                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(ScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    } else {
                        onScanQRCodeSuccess(result);
                    }
                }
            }.execute();
        }
    }
}
