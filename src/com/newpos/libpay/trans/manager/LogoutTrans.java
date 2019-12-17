package com.newpos.libpay.trans.manager;

import android.content.Context;

import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.utils.ISOUtil;

/**
 * 签出实体类
 * @author zhouqiang
 */

@Deprecated
public class LogoutTrans extends Trans implements TransPresenter{

	public LogoutTrans(Context ctx , String transEN , TransInputPara p) {
		super(ctx, transEN);
		para = p ;
		setTraceNoInc(false);
		TransEName = transEN ;
		if(para != null){
			transUI = para.getTransUI();
		}
	}

	@Override
	public ISO8583 getISO8583() {
		return iso8583;
	}

	@Override
	public void start() {
/**
		transUI.handling(timeout , Tcode.Status.terminal_logonout);
		retVal = Logout();
		if(retVal!=0){
			transUI.showError(timeout , retVal);
		}else {
			transUI.trannSuccess(timeout , Tcode.Status.logonout_succ);
		}

		Logger.debug("LogoutTrans>>finish");
		return;
*/
	}

	/**
	 * 签退
	 * @throws
	 **/
	public int Logout() {
		TransEName = Type.LOGOUT ;
		setFixedDatas();
		iso8583.clearData();
		iso8583.setField(0, MsgID);
		iso8583.setField(11, cfg.getTraceNo());
		iso8583.setField(41, cfg.getTermID());
		iso8583.setField(42, cfg.getMerchID());
		Logger.debug("Filed60 = "+Field60);
		iso8583.setField(60, Field60);
		iso8583.setField(63, ISOUtil.padleft(cfg.getOprNo()+"",2,'0') + " ");
		retVal = OnLineTrans();
		Logger.debug("LogonTrans>>Logout>>OnLineTrans finish");
		if (retVal != 0) {
			return retVal ;
		}
		String rspCode = iso8583.getfield(39);
		netWork.close();
		if (rspCode != null && rspCode.equals("00")) {
			Logger.debug("LogoutTrans>>Logout>>签退成功");
			return 0 ;
		} else {
			if (rspCode == null) {
				return Tcode.T_receive_err;
			} else {
				return Integer.valueOf(rspCode);
			}
		}
	}
}
