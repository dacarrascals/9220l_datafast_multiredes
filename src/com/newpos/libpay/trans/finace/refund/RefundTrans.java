package com.newpos.libpay.trans.finace.refund;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.process.EmvTransaction;
import com.newpos.libpay.process.QpbocTransaction;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_SWIPE_ICC_CTL;

/**
 * Created by zhouqiang on 2017/4/25.
 * 退货处理交易类
 * @author zhouqiang
 */
public class RefundTrans extends FinanceTrans implements TransPresenter {

    private TransLogData data ;

    public RefundTrans(Context ctx , String transEn , TransInputPara p){
        super(ctx , transEn);
        para = p ;
        transUI = para.getTransUI() ;
        isReversal = true;
        isSaveLog = true;
        isDebit = true;
        isProcPreTrans = true;
        isProcSuffix = true ;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {
        InputInfo info = transUI.getOutsideInput(timeout , InputManager.Mode.PASSWORD, "");
        if(info.isResultFlag()){
            String master_pass = info.getResult();
            if(master_pass.equals(cfg.getMasterPass())){
                CardInfo cardInfo = transUI.getCardUse(GERCARD_MSG_SWIPE_ICC_CTL, timeout ,INMODE_IC|INMODE_NFC|INMODE_MAG, transEname);
                afterGetCardUse(cardInfo);
            }else {
                transUI.showError(timeout , Tcode.T_master_pass_err);
            }
        }else {
            transUI.showError(timeout , info.getErrno());
        }

        Logger.debug("RefundTrans>>finish");
        return;
    }

    private void afterGetCardUse(CardInfo info){
        if(info.isResultFalg()){
            int type = info.getCardType() ;
            switch (type){
                case CardManager.TYPE_MAG :inputMode = ENTRY_MODE_MAG ;break;
                case CardManager.TYPE_ICC :inputMode = ENTRY_MODE_ICC ;break;
                case CardManager.TYPE_NFC :inputMode = ENTRY_MODE_NFC ;break;
            }
            para.setInputMode(inputMode);
            if(inputMode == ENTRY_MODE_MAG){
                isMag(info.getTrackNo());
            }
            if(inputMode == ENTRY_MODE_ICC){
                isICC();
            }
            if(inputMode == ENTRY_MODE_NFC){
                if(cfg.isForcePboc()){
                    isICC();
                }else {
                    isNFC();
                }
            }
        }else {
            transUI.showError(timeout , info.getErrno());
        }
    }

    private void isICC(){
        transUI.handling(timeout , Tcode.Status.handling);
        emv = new EmvTransaction(para,  Type.REFUND);
        retVal = emv.start() ;
        if(1 == retVal || retVal == 0){
            Pan = emv.getCardNo();
            afterCard();
        }else {
            transUI.showError(timeout , retVal);
        }
    }

    private void isNFC(){
        transUI.handling(timeout , Tcode.Status.handling);
        qpboc = new QpbocTransaction(para);
        retVal = qpboc.start() ;
        if(0 == retVal){
            String cn = qpboc.getCardNO();
            if(cn == null){
                transUI.showError(timeout , Tcode.T_qpboc_read_err);
            }else {
                Pan = cn ;
                afterCard();
            }
        }else {
            transUI.showError(timeout , retVal);
        }
    }

    /** 磁卡选项 */
    private void isMag(String[] tracks){
        String data1 = null;
        String data2 = null;
        String data3 = null;
        int msgLen = 0;
        if (tracks[0].length() > 0 && tracks[0].length() <= 80) {
            data1 = new String(tracks[0]);
        }
        if (tracks[1].length() >= 13 && tracks[1].length() <= 37) {
            data2 = new String(tracks[1]);
            if(!data2.contains("=")){
                retVal = Tcode.T_search_card_err ;
            }else {
                String judge = data2.substring(0, data2.indexOf('='));
                if(judge.length() < 13 || judge.length() > 19){
                    retVal = Tcode.T_search_card_err ;
                }else {
                    if (data2.indexOf('=') != -1) {
                        msgLen++;
                    }
                }
            }
        }
        if (tracks[2].length() >= 15 && tracks[2].length() <= 107) {
            data3 = new String(tracks[2]);
        }
        if(retVal!=0){
            transUI.showError(timeout , retVal);
        }else {
            if (msgLen == 0) {
                transUI.showError(timeout , Tcode.T_search_card_err);
            }else {
                if (cfg.isCheckICC()) {
                    int splitIndex = data2.indexOf("=");
                    if (data2.length() - splitIndex >= 5) {
                        char iccChar = data2.charAt(splitIndex + 5);
                        if (iccChar == '2' || iccChar == '6') {
                            //该卡IC卡,请刷卡
                            transUI.showError(timeout , Tcode.T_ic_not_allow_swipe);
                        }else {
                            afterMAGJudge(data2 , data3);
                        }
                    } else {
                        transUI.showError(timeout , Tcode.T_search_card_err);
                    }
                }else {
                    afterMAGJudge(data2 , data3);
                }
            }
        }
    }

    private void afterMAGJudge(String data2 , String data3){
        String cardNo = data2.substring(0, data2.indexOf('='));
        retVal = transUI.showCardConfirm(timeout , cardNo);
        if(retVal == 0){
            Pan = cardNo;
            Track2 = data2;
            Track3 = data3;
            afterCard();
        }else {
            transUI.showError(timeout , Tcode.T_user_cancel_operation);
        }
    }

    private void afterCard(){
        InputInfo info = transUI.getOutsideInput(timeout , InputManager.Mode.REFERENCE, "");
        if(info.isResultFlag()){
            String refer = info.getResult() ;
            info = transUI.getOutsideInput(timeout , InputManager.Mode.DATETIME, "");
            if(info.isResultFlag()){
                String date = info.getResult();
                info = transUI.getOutsideInput(timeout , InputManager.Mode.AMOUNT, "");
                if(info.isResultFlag()){
                    String amount = info.getResult();
                    data = TransLog.getInstance().searchTransLogByREFERDATE(refer , date);
                    if(data!=null){
                        if(data.getAmount() == Long.parseLong(amount)){
                            if(data.getIsVoided()){
                                transUI.showError(timeout , Tcode.T_trans_is_voided);
                            }else {
                                if(data.getAmount() <= 500){
                                    AuthCode = data.getAuthCode();
                                    RRN = data.getRRN();
                                    Amount = data.getAmount();
                                    Field61 = data.getBatchNo()+data.getTraceNo()+data.getLocalDate();
                                    Field63 = "000" ;
                                    isPinExist = false;
                                    prepareOnline();
                                }else {
                                    transUI.showError(timeout , Tcode.T_refund_amount_beyond);
                                }
                            }
                        }else {
                            transUI.showError(timeout , Tcode.T_amount_not_same);
                        }
                    }else{
                        transUI.showError(timeout , Tcode.T_not_find_trans);
                    }
                }else {
                    transUI.showError(timeout , info.getErrno());
                }
            }else {
                transUI.showError(timeout , info.getErrno());
            }
        }else {
            transUI.showError(timeout , info.getErrno());
        }
    }

    private void prepareOnline(){
        transUI.handling(timeout , Tcode.Status.connecting_center);
        setDatas(inputMode);
        if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC){
            retVal = OnlineTrans(emv);
        }else{
            retVal = OnlineTrans(null);
        }
        Logger.debug("VoidTrans>>OnlineTrans="+retVal);
        clearPan();
        if(retVal == 0){
            data.setVoided(true);
            int index = TransLog.getInstance().getCurrentIndex(data);
            TransLog.getInstance().updateTransLog(index,data);
            transUI.trannSuccess(timeout , Tcode.Status.refund_success ,
                    PAYUtils.getStrAmount(Amount));
        }else {
            transUI.showError(timeout , retVal);
        }
    }
}
