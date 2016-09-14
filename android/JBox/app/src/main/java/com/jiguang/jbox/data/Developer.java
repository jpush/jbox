package com.jiguang.jbox.data;

import android.support.annotation.NonNull;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class Developer {

    private final String mDevKey;      // 用户验证通过后生成的 dev_key，作为唯一标识。

    private final String mDevName;     // 在第三方平台下的 username。

    private final String mPlatform;    // 第三方登录平台。

    private String mDesc;               // 用户描述。

    private String mAvatarPath;         // 头像路径。

    public Developer(@NonNull String devKey, @NonNull String devName, @NonNull String platform) {
        mDevKey = checkNotNull(devKey);
        mDevName = checkNotNull(devName);
        mPlatform = checkNotNull(platform);
    }

    public String getDevName() {
        return mDevName;
    }

    public String getDevKey() {
        return mDevKey;
    }

    public String getPlatform() {
        return mPlatform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Developer developer = (Developer) o;
        return Objects.equal(mDevName, developer.mDevName) &&
                Objects.equal(mDevKey, developer.mDevKey) &&
                Objects.equal(mPlatform, developer.mPlatform);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mDevName, mDevKey, mPlatform);
    }

    @Override
    public String toString() {
        return "Developer: " + mDevName;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public String getAvatarPath() {
        return mAvatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        mAvatarPath = avatarPath;
    }
}
