package com.jiguang.jbox.ui.channel;


public interface OnChannelCheckedListener {
    /**
     * 根据 Channel 的选择状态来判断是否订阅。
     */
    void onChannelChecked(int position, boolean isChecked);
}
