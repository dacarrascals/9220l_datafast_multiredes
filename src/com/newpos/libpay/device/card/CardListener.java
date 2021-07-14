package com.newpos.libpay.device.card;

/**
 * Created by zhouqiang on 2017/3/14.
 * @author zhouqiang
 * 寻卡全局监听
 */

public interface CardListener {
    public void callback(CardInfo cardInfo);
}
