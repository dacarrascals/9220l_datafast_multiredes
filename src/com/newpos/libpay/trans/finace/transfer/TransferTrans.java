package com.newpos.libpay.trans.finace.transfer;

import android.content.Context;

import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;

/**
 * Created by zhouqiang on 2017/4/25.
 * 转账处理交易类
 * @author zhouqiang
 */

@Deprecated
public class TransferTrans extends FinanceTrans implements TransPresenter {

    public TransferTrans(Context ctx , String transEn , TransInputPara p){
        super(ctx , transEn);
        para = p ;
        transUI = para.getTransUI() ;
        isReversal = true;
        isSaveLog = true;
        isDebit = true;
        isProcPreTrans = true;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {
/**
        InputInfo info = transUI.getOutsideInput(timeout , InputManager.Mode.AMOUNT);
        if(info.isResultFlag()){
            Amount = Long.parseLong(info.getResult()) ;
            //刷转出卡
            CardInfo cardInfo = transUI.getCardUse(timeout , INMODE_IC|INMODE_NFC|INMODE_MAG, transEname);
            if(cardInfo.isResultFalg()){
                int type = cardInfo.getCardType() ;
                switch (type){
                    case CardManager.TYPE_MAG :inputMode = ENTRY_MODE_MAG ;break;
                    case CardManager.TYPE_ICC :inputMode = ENTRY_MODE_ICC ;break;
                    case CardManager.TYPE_NFC :inputMode = ENTRY_MODE_NFC ;break;
                }
                para.setInputMode(inputMode);
                Logger.debug("SaleTrans>>start>>inputMode="+inputMode);
                if(inputMode == ENTRY_MODE_ICC){

                }if(inputMode == ENTRY_MODE_NFC){

                }if(inputMode == ENTRY_MODE_MAG){

                }
            }else {
                transUI.showError(timeout , cardInfo.getErrno());
            }
        }else {
            transUI.showError(timeout , info.getErrno());
        }

        Logger.debug("TransferTrans>>finish");
        return;
*/
    }
}
