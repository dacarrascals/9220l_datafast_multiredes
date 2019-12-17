package com.newpos.libpay.trans.finace.quickpass;

import android.content.Context;

import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.process.EmvTransaction;
import com.newpos.libpay.process.QpbocTransaction;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;

/**
 * 快速消费交易类
 * 存在电子现金优先电子电子现金
 * 当电子现金不满足要求或着交易超额则自动走联机交易
 * @author zhouqiang
 */

@Deprecated
public class QuickPassTrans extends FinanceTrans implements TransPresenter{

	public QuickPassTrans(Context ctx, String transEname , TransInputPara p) {
		super(ctx, transEname);
		para = p ;
		transUI = para.getTransUI() ;
		isReversal = false;
		isSaveLog = true;
		isDebit = true;
		isProcPreTrans = true;
//		isProcSuffix = true;
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
			para.setAmount(Amount);
			para.setOtherAmount(0);
			CardInfo cardInfo = transUI.getCardUse(timeout , INMODE_NFC|INMODE_IC, transEname);
			afterGetCardUse(cardInfo);
		}else {
			transUI.showError(timeout , info.getErrno());
		}


		Logger.debug("Quick_PassTrans>>finish");
		return;
*/
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
			if(inputMode == ENTRY_MODE_NFC){
				if(cfg.isForcePboc()){
					isICC();
				}else {
					isNFC();
				}
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
		Logger.debug("Quick_PassTrans>>QpbocTransaction=" + retVal);
		if(retVal == 0){
			byte[] temp = new byte[256];
			PAYUtils.get_tlv_data_kernal(0x57, temp);
			Pan = ISOUtil.hexString(temp).split("D")[0];
			byte[] res = new byte[32] ;
			PAYUtils.get_tlv_data_kernal(0x9F10 , res);
			Logger.debug("9f10 = "+ISOUtil.hexString(res));
			if( (res[4]&0x30) == (byte)0x20 ){
				setFixedDatas();
				isReversal = true ;
				PinInfo info = transUI.getPinpadOnlinePin(timeout , String.valueOf(Amount), Pan);
				afterQpbocGetPin(info);
			}else if( ((res[4]&0x30)  == (byte)0x10) || ((res[4]&0xC0) == (byte)0x40)){
				setICCData();
				retVal = offlineTrans("00");
				if(0 == retVal){
					transUI.trannSuccess(timeout , Tcode.Status.quickpass_succ);
				}else {
					transUI.showError(timeout , retVal);
				}
			} else {
				transUI.showError(timeout , Tcode.T_pboc_refuse);
			}
		}else {
			transUI.showError(timeout , retVal);
		}
	}

	private void isICC(){
		transUI.handling(timeout, Tcode.Status.handling);
		emv = new EmvTransaction(para,  Type.QUICKPASS);
		retVal = emv.start();
		Logger.debug("Quick_PassTrans>>EmvTransaction=" + retVal);
		if(retVal == 1){
			setFixedDatas();
			isReversal = true ;
			Pan = emv.getCardNo() ;
			PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount),Pan);
			if(info.isResultFlag()){
				if(info.isNoPin()){
					isPinExist = false ;
				}else {
					isPinExist = true;
					PIN = ISOUtil.hexString(info.getPinblock());
				}
				//设置55域数据
				setICCData();
				prepareOnline(emv.getCardNo());
			}else {
				transUI.showError(timeout , info.getErrno());
			}
		}else if(retVal == 0){
			setICCData();
			retVal = offlineTrans("00");
			if(0 == retVal){
				transUI.trannSuccess(timeout , Tcode.Status.quickpass_succ);
			}else {
				transUI.showError(timeout , retVal);
			}
		}else {
			transUI.showError(timeout , retVal);
		}
	}

	/** pboc后续处理 */
	private void afterQpbocGetPin(PinInfo info){
		if(info.isResultFlag()){
			if(info.isNoPin()){
				isPinExist = false;
			}else {
				isPinExist = true ;
				PIN = ISOUtil.hexString(info.getPinblock()) ;
			}
			IEMVHandler emvHandler = EMVHandler.getInstance();
			byte[] temp =  ISOUtil.str2bcd(Pan , false);
			if(Pan.length()%2 != 0) {
				temp[Pan.length() / 2] |= 0x0f;
			}
			emvHandler.setDataElement(new byte[]{0x5A} ,temp );
			Logger.debug("temp = "+ISOUtil.hexString(temp , 0 , temp.length));
			setICCData();
			prepareOnline(qpboc.getCardNO());
		}else {
			transUI.showError(timeout , info.getErrno());
		}
	}

	/** 准备联机 */
	private void prepareOnline(String fullcard){
		//设置完55域数据即可请求联机
		transUI.handling(timeout , Tcode.Status.connecting_center);
		setDatas(inputMode);
		//联机处理
		if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC){
			retVal = OnlineTrans(emv);
		}else{
			retVal = OnlineTrans(null);
		}
		Logger.debug("SaleTrans>>OnlineTrans="+retVal);
		clearPan();
		if(retVal == 0){
			transUI.trannSuccess(timeout , Tcode.Status.sale_succ);
		}else {
			transUI.showError(timeout , retVal);
		}
	}
}
