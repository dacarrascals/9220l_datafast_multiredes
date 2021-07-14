package com.newpos.libpay.device.scanner;

/**
 * Created by zhouqiang on 2017/7/7.
 * @author zhouqiang
 * 扫码接口回调
 */

public interface QRCListener {
    public void callback(QRCInfo info);
}
