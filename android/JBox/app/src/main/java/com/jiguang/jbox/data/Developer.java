package com.jiguang.jbox.data;

import com.google.common.base.Objects;

public class Developer {

    private final String mDevName;     // 在第三方平台下的 username。

    private final String mDevKey;      // 用户验证通过后生成的 dev_key。

    private final String mPlatform;    // 第三方登录平台。

    public Developer(String devName, String devKey, String platform) {
        mDevName = devName;
        mDevKey = devKey;
        mPlatform = platform;
    }

    public String getmDevName() {
        return mDevName;
    }

    public String getmDevKey() {
        return mDevKey;
    }

    public String getmPlatform() {
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
}
