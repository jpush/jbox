package com.jiguang.jbox.data;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import static com.google.common.base.Preconditions.checkNotNull;

@Table(name = "Developer")
public class Developer extends Model {

    @Column(name = "Key", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String key;      // 用户验证通过后生成的 dev_key，作为唯一标识。

    @Column(name = "Name")
    public String name;     // 在第三方平台下的 username。

    @Column(name = "Platform")
    public String platform;    // 第三方登录平台。

    @Column(name = "Desc")
    public String desc;               // 用户描述。

    @Column(name = "AvatarPath")
    public String avatarPath;         // 头像路径。

    public Developer() {
        super();
    }

    public Developer(@NonNull String devKey, @NonNull String devName, @NonNull String platform,
                     String desc, String path) {
        super();
        this.key = checkNotNull(devKey);
        this.name = checkNotNull(devName);
        this.platform = checkNotNull(platform);
        this.desc = desc;
        this.avatarPath = path;
    }
}
