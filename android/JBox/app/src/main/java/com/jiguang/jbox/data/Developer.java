package com.jiguang.jbox.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Developer")
public class Developer extends Model {

    @Column(name = "Key", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String key;      // 用户验证通过后生成的 dev_key，作为唯一标识。

    @Column(name = "Name")
    public String name;     // 在第三方平台下的 username。

    @Column(name = "Platform")
    public String platform;    // 第三方登录平台。

    @Column(name = "Desc")
    public String desc;        // 用户描述。

    @Column(name="AvatarUrl")   // 头像 url。
    public String avatarUrl;

    @Column(name="IsSelected")
    public boolean isSelected = false;  // 是否被选中。

    public Developer() {
        super();
    }

    @Override
    public boolean equals(Object obj) {
        Developer objDev = (Developer) obj;

        if (!objDev.key.equals(key)) {
            return false;
        } else if (!objDev.name.equals(name)) {
            return false;
        } else if (!objDev.platform.equals(platform)) {
            return false;
        } else if (!objDev.desc.equals(desc)) {
            return false;
        } else if (!objDev.avatarUrl.equals(avatarUrl)) {
            return false;
        }

        return true;
    }
}
