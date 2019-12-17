package com.newpos.libpay;

import android.util.Log;

import com.newpos.libpay.global.TMConfig;

/**
 * Created by zhouqiang on 2017/3/8.
 * @author zhouqiang
 * sdk全局日主输出
 */

public class Logger {

    public static final String TAG = "PAYSDK" ;

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    private static String newTag(StackTraceElement caller) {
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        //caller.getMethodName()
        //caller.getLineNumber()
        return TAG+"_"+callerClazzName ;
    }

    public static void debug(String msg){
        if(TMConfig.getInstance().isDebug()){
            Log.i(TAG, msg);
        }
    }

    public static void error(String msg){
        Log.e(TAG , msg);
    }
}
