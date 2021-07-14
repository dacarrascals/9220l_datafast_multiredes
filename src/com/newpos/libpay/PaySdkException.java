package com.newpos.libpay;

/**
 * Created by zhouqiang on 2017/4/25.
 * @author zhouqiang
 * 异常处理
 */

public class PaySdkException extends Exception {

    public static final String NOT_INIT = "please init pay sdk first!" ;
    public static final String PARA_NULL = "init pay sdk paras is null!" ;

    public PaySdkException(String msg){
        Logger.error(msg);
    }
}
