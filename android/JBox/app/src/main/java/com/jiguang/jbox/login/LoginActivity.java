package com.jiguang.jbox.login;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.jiguang.jbox.R;
import com.jiguang.jbox.data.Developer;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends Activity implements QRCodeView.Delegate {
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    private ZXingView mQRCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mQRCodeView = (ZXingView) findViewById(R.id.zxingView);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestQRCodePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestQRCodePermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和闪光灯的权限",
                    REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        // 保存 dev 信息。
        try {
            JSONObject json = new JSONObject(result);
            String devName = json.getString("dev_name");
            String devKey = json.getString("dev_key");
            String platform = json.getString("platform");
            Developer dev = new Developer(devName, devKey, platform);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, R.string.error_open_camera, Toast.LENGTH_SHORT).show();
    }

}
