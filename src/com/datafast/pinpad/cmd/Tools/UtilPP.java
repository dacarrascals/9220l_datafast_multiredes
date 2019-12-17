package com.datafast.pinpad.cmd.Tools;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.UIUtils;

public class UtilPP {

    /**
     *
     * @param TID
     * @param MID
     * @param ctx
     * @return
     */
    public static boolean UpdateTidMidFromCash(String TID, String MID, Context ctx) {

        if(!checkTidMid(TID, MID, ctx))
            return false;

        TMConfig tmConfig = TMConfig.getInstance();

        tmConfig.setTermID(TID).
                setMerchID(MID);

        if (!tmConfig.save())
            return false;

        return true;
    }

    public static boolean checkTidMid(String TID, String MID, Context ctx){
        if (PAYUtils.isNullWithTrim(TID) || PAYUtils.isNullWithTrim(MID)) {
            //UIUtils.toast((Activity) ctx, R.drawable.ic_launcher, "MID o TID invalido", Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }
}
