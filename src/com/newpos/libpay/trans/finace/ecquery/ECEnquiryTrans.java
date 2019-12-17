package com.newpos.libpay.trans.finace.ecquery;

import android.content.Context;

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
import com.newpos.libpay.utils.PAYUtils;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_ICC_CTL;

/**
 * 电子现金余额查询类
 * @author zhouqiang
 */

public class ECEnquiryTrans extends FinanceTrans implements TransPresenter{

	public ECEnquiryTrans(Context ctx, String transEname , TransInputPara p) {
		super(ctx, transEname);
		para = p ;
		transUI = para.getTransUI() ;
		isReversal = false;
		isSaveLog = false;
		isDebit = false;
		isProcPreTrans = false;
		isProcSuffix = false;
	}

	@Override
	public ISO8583 getISO8583() {
		return iso8583;
	}

	@Override
	public void start() {
		CardInfo cardInfo = transUI.getCardUse(GERCARD_MSG_ICC_CTL, timeout , INMODE_IC|INMODE_NFC, transEname);
		afterGetCardUse(cardInfo);
		Logger.debug("EC_EnquiryTrans>>finish");
		return;
	}

	private void afterGetCardUse(CardInfo info){
		setTraceNoInc(false);
		if(info.isResultFalg()){
			int type = info.getCardType() ;
			switch (type){
				case CardManager.TYPE_MAG :inputMode = ENTRY_MODE_MAG ;break;
				case CardManager.TYPE_ICC :inputMode = ENTRY_MODE_ICC ;break;
				case CardManager.TYPE_NFC :inputMode = ENTRY_MODE_NFC ;break;
			}
			para.setInputMode(inputMode);
			if(inputMode == ENTRY_MODE_NFC){
				isICC();
			}
			if(inputMode == ENTRY_MODE_ICC){
				isICC();
			}
		}else {
			transUI.showError(timeout , info.getErrno());
		}
	}

	private void isNFC(){
		transUI.handling(timeout, Tcode.Status.handling);
		qpboc = new QpbocTransaction(para);
		retVal = qpboc.start();
		if (retVal == 0) {
			String ec_amount = qpboc.getEC_AMOUNT();
			if (ec_amount == null) {
				transUI.showError(timeout, Tcode.T_read_ec_amount_err);
			} else {
				retVal = offlineTrans(ec_amount);
				if (0 == retVal) {
					transUI.trannSuccess(timeout, Tcode.Status.ecenquiry_succ,
							PAYUtils.getStrAmount(Long.parseLong(ec_amount)));
				} else {
					transUI.showError(timeout, retVal);
				}
			}
		} else {
			transUI.showError(timeout, retVal);
		}
	}

	private void isICC(){
		transUI.handling(timeout, Tcode.Status.handling);
		emv = new EmvTransaction(para,  Type.EC_ENQUIRY);
		retVal = emv.start();
		if (retVal == 0 || retVal == 1) {
			String ec_amount = emv.getECAmount();
			if(ec_amount == null){
				transUI.showError(timeout , Tcode.T_read_ec_amount_err);
			}else {
				Pan = emv.getCardNo() ;
				retVal = offlineTrans(ec_amount);
				if(0 == retVal){
					transUI.trannSuccess(timeout , Tcode.Status.ecenquiry_succ ,
							PAYUtils.getStrAmount(Long.parseLong(ec_amount)));
				}else {
					transUI.showError(timeout , retVal);
				}
			}
		} else {
			transUI.showError(timeout , retVal);
		}
	}
}
