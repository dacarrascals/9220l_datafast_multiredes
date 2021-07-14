package com.newpos.libpay.trans.finace.forload;

import android.content.Context;

import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.finace.FinanceTrans;

/**
 * Created by zhouqiang on 2017/4/27.
 * 圈存交易类
 * @author zhouqiang
 */

@Deprecated
public class CreditForLoad extends FinanceTrans implements TransPresenter{

    public CreditForLoad(Context ctx , String en){
        super(ctx , en);
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {

    }
}
