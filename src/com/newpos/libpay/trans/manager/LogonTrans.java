package com.newpos.libpay.trans.manager;

import android.content.Context;

import com.newpos.libpay.Logger;
import com.newpos.libpay.device.pinpad.PinpadKeytem;
import com.newpos.libpay.device.pinpad.PinpadKeytype;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.device.pinpad.WorkKeyinfo;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.emv.EMVHandler;
import com.pos.device.ped.Ped;

/**
 * 签到实体类
 * @author zhouqiang
 */

public class LogonTrans extends Trans implements TransPresenter{

	public LogonTrans(Context ctx , String transEN , TransInputPara p) {
		super(ctx, transEN);
		para = p ;
		setTraceNoInc(false);
		TransEName = transEN ;
		transUI = para.getTransUI();
	}

	@Override
	public ISO8583 getISO8583() {
		return iso8583;
	}

	@Override
	public void start() {
		timeout = 60 * 1000 ;
		if(!cfg.isOnline()){
			transUI.handling(timeout , Tcode.Status.terminal_logon);
			retVal = SignInOffline() ;
			if( retVal  == 0){
				transUI.trannSuccess(timeout , Tcode.Status.logon_succ);
			}else {
				transUI.showError(timeout , Tcode.T_unknow_err);
			}
			return;
		}
		retVal = sign() ;
		if(retVal == 0){
			if(EMVHandler.getInstance().getAidInfoNum()==0){
				DparaTrans dTrans = new DparaTrans(context , Type.DOWNPARA , null);
				transUI.handling(timeout , Tcode.Status.downing_aid);
				retVal = dTrans.DownloadAid();
				if(retVal == 0){
					transUI.handling(timeout , Tcode.Status.downing_capk);
					retVal = dTrans.DownloadCapk();
					if(retVal == 0){
						DparaTrans.loadAIDCAPK2EMVKernel();
						transUI.trannSuccess(timeout , Tcode.Status.logon_down_succ);
					}else {
						transUI.showError(timeout , retVal);
					}
				}else {
					transUI.showError(timeout , retVal);
				}
			}else {
				transUI.trannSuccess(timeout , Tcode.Status.logon_succ);
			}
		}else {
			transUI.showError(timeout , Tcode.T_unknow_err);
		}

		Logger.debug("LogonTrans>>finish");
		return;
	}

	private int sign(){
		transUI.handling(timeout , Tcode.Status.terminal_logon);
		retVal = SignIn();
		if(retVal!=0){
			return retVal ;
		}
		return 0 ;
	}

	private void setFields() {
		if (MsgID != null) {
			iso8583.setField(0, MsgID);
		}
		if (TraceNo != null) {
			iso8583.setField(11, TraceNo);
		}
		if (LocalTime != null) {
			iso8583.setField(12, LocalTime);
		}
		if (LocalDate != null) {
			iso8583.setField(13, LocalDate);
		}
		iso8583.setField(41, TermID);
		if (MerchID != null){
			iso8583.setField(42, MerchID);
		}
		if (Field60 != null){
			iso8583.setField(60, Field60);
		}
		if (Field62 != null){
			iso8583.setField(62, Field62);
		}
		if (Field63 != null){
			iso8583.setField(63, Field63);
		}
	}

	/**
	 * 签到
	 * @throws
	 **/
	public int SignIn() {
		TransEName = Type.LOGON ;
		setFixedDatas();
		iso8583.set62AttrDataType(2);
		iso8583.setField(11, cfg.getTraceNo());
		iso8583.setField(62,"53657175656E6365204E6F3132333230393832303030373031");
		String f60_3 ;
		if (cfg.isSingleKey()) {
			f60_3 = "001";
		} else if (cfg.isTrackEncrypt()) {
			f60_3 = "004";
		} else {
			f60_3 = "003";
		}
		Field60 = Field60.substring(0, 8) + f60_3;
		iso8583.setField(63, ISOUtil.padleft(cfg.getOprNo()+"",2,'0') + " ");
		setFields();
		retVal = OnLineTrans(transUI);
		Logger.debug("LogonTrans>>SignIn>>OnLineTrans finish");
		if (retVal != 0) {
			return retVal ;
		}
		String rspCode = iso8583.getfield(39);
		netWork.close();
		if (rspCode != null && rspCode.equals("00")) {
			String str60 = iso8583.getfield(60);
			cfg.setBatchNo(Integer.parseInt(str60.substring(2 , 8))).save();
			Logger.debug("current batchNo = " + cfg.getBatchNo());
			String strField62 = iso8583.getfield(62);
			if (strField62 == null) {
				return Tcode.T_receive_err;
			}
			byte[] field62 = ISOUtil.str2bcd(strField62, false);
			int setKeyRet = setKey(field62);
			if (setKeyRet != 0) {
				return setKeyRet ;
			} else {
				return 0;
			}
		} else {
			if (rspCode == null) {
				return Tcode.T_receive_err;
			} else {
				return Integer.valueOf(rspCode);
			}
		}
	}

	/**
	 * 脱机模拟签到
	 * @return
	 */
	public int SignInOffline() {
		byte[] keys = ISOUtil.str2bcd("1CF08008FD62A1E217153829C3A6E51C2A7B0CB84A187EE99C9D002BE1010250792913C4325EA56471657F39F8B3D6562CC515E0403BEB676CCCB22E" , false);
		return setKey(keys);
	}

	private int setKey(byte[] keyData) {
		WorkKeyinfo workKeyinfo = new WorkKeyinfo() ;
		workKeyinfo.setMasterKeyIndex(cfg.getMasterKeyIndex());
		workKeyinfo.setWorkKeyIndex(cfg.getMasterKeyIndex());
		workKeyinfo.setMode(Ped.KEY_VERIFY_KVC);
		workKeyinfo.setKeySystem(PinpadKeytem.MS_DES);

		byte[] temp ; // 临时存储数组
		int keyLen;//密钥长度
//		if(keyData.length!=60)
//			return -1 ;//银联下发三个密钥
//		if(keyData.length!=40)
//			return -2 ;//中信银行下发两个密钥
		keyLen = 20 ;

		//注入PINK
		temp = new byte[keyLen];
		System.arraycopy(keyData, 0, temp, 0, keyLen);
		long start = System.currentTimeMillis();
		workKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_PINK);
		workKeyinfo.setPrivacyKeyData(temp);
		retVal = PinpadManager.loadWKey(workKeyinfo);
		Logger.debug("LogonTrans>>setKey>>PINK="+retVal);
		long end = System.currentTimeMillis();
		Logger.debug("LogonTrans>>setKey>>TIME="+(end - start));
		if (retVal != 0) {
			return retVal;
		}

		//注入MACK
		System.arraycopy(keyData, keyLen, temp, 0, keyLen);
		start = System.currentTimeMillis();
		workKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_MACK);
		if(cfg.getStandard() == 1){
			System.arraycopy(temp , 0 , temp , 8 , 8 );
		}
		workKeyinfo.setPrivacyKeyData(temp);
		retVal = PinpadManager.loadWKey(workKeyinfo);
		Logger.debug("LogonTrans>>setKey>>MACK="+retVal);
		end = System.currentTimeMillis();
		Logger.debug("LogonTrans>>setKey>>TIME="+(end - start));
		if (retVal != 0) {
			return retVal;
		}

		//注入EACK
		if(cfg.isTrackEncrypt()){
			System.arraycopy(keyData, keyLen*2, temp, 0, keyLen);
			start = System.currentTimeMillis();
			//71657F39F8B3D6562CC515E0403BEB676CCCB22E
			//国密不支持，使用MAC区域去保存，注意索引
			workKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_EAK);
			workKeyinfo.setPrivacyKeyData(temp);
			retVal = PinpadManager.loadWKey(workKeyinfo);
			Logger.debug("LogonTrans>>setKey>>EACK="+retVal);
			end = System.currentTimeMillis();
			Logger.debug("LogonTrans>>setKey>>TIME="+(end - start));
			if (retVal != 0) {
				return retVal;
			}
		}

		return 0;
	}
}
