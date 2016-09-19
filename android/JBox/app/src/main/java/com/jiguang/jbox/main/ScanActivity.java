package com.jiguang.jbox.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.jiguang.jbox.R;
import com.jiguang.jbox.channel.ChannelActivity;

import cn.bingoogolapple.qrcode.core.QRCodeView;


/**
 * 二维码扫描界面。
 */
public class ScanActivity extends Activity implements QRCodeView.Delegate {

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 0;

    private QRCodeView mScanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

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
            mScanView.startCamera();
        }
    }

    @Override
    protected void onResume() {
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
        if (!TextUtils.isEmpty(result)) {
            // 扫描二维码返回 devKey，再请求 developer 信息。
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ChannelActivity.EXTRA_DEV_KEY, result);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "打开相机出错", Toast.LENGTH_SHORT).show();
    }

}
