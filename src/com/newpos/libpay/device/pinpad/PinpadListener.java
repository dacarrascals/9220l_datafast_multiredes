package com.newpos.libpay.device.pinpad;

/**
 * Created by zhouqiang on 2017/3/17.
 * @author zhouqiang
 * 密码键盘输入密码监听
 */

public interface PinpadListener {
    void callback(PinInfo info);
}
