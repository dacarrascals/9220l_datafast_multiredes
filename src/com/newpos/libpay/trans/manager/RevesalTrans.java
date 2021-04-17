package com.newpos.libpay.trans.manager;

import android.content.Context;

import com.datafast.menus.menus;
import com.newpos.libpay.Logger;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_REVERSE;
import static com.newpos.libpay.trans.finace.FinanceTrans.getTypeCoin;

/**
 * 冲正交易实体类
 *
 * @author zhouqiang
 */
public class RevesalTrans extends Trans {

    public String rspCode;
    public RevesalTrans(Context ctx, String transEname, TransUI transUI) {
        super(ctx, transEname);
        isUseOrgVal = true; // 使用原交易的60.1 60.3
        iso8583.setHasMac(false);
        isTraceNoInc = false; // 冲正不需要自增流水号
        this.transUI = transUI;
    }

    protected void setFields(TransLogData data) {

        if (MsgID != null) {
            iso8583.setField(0, MsgID);
        }
        if (data.getPan() != null) {
            iso8583.setField(2, data.getPan());
        }
        if (data.getProcCode() != null) {
            if (data.getEName().equals(Type.ANULACION)) {
                iso8583.setField(3, "320000");
            } else
                iso8583.setField(3, data.getProcCode());
        }

        String AmoutData;
        AmoutData = ISOUtil.padleft(data.getAmount() + "", 12, '0');
        iso8583.setField(4, AmoutData);

        if (data.getTraceNo() != null) {
            iso8583.setField(11, data.getTraceNo());
        }
        if (data.getLocalTime() != null) {
            iso8583.setField(12, data.getLocalTime());
        }
        if (data.getLocalDate() != null) {
            iso8583.setField(13, data.getLocalDate());
        }
        if (data.getExpDate() != null) {
            iso8583.setField(14, data.getExpDate());
        }
        if (data.getEntryMode() != null) {
            iso8583.setField(22, data.getEntryMode());
        }
        if (data.getPanSeqNo() != null) {
            iso8583.setField(23, data.getPanSeqNo());
        }
        if (data.getNii() != null) {
            iso8583.setField(24, data.getNii());
        }
        if (data.getSvrCode() != null) {
            iso8583.setField(25, data.getSvrCode());
        }
        if (data.getTrack2() != null) {
            iso8583.setField(35, data.getTrack2());
        }
        if (data.getTermID() != null) {
            iso8583.setField(41, data.getTermID());
        }
        if (data.getMerchID() != null) {
            iso8583.setField(42, data.getMerchID());
        }
        if (data.getTypeCoin() != null) {
            String auxStr = getTypeCoin(data.getTypeCoin());
            iso8583.setField(49, auxStr);
        }
        if (data.getField54() != null) {
            iso8583.setField(54, data.getField54());
        }
        if (data.getField55() != null){
            iso8583.setField(55, data.getField55());
        }
        if (data.getField57() != null) {
            iso8583.setField(57, data.getField57());
        }
        if (data.getField58() != null) {
            iso8583.setField(58, data.getField58());
        }
        if (data.getField59() != null) {
            iso8583.setField(59, data.getField59());
        }
        if (data.getField60() != null) {
            iso8583.setField(60, data.getField60());
        }
        if (data.getField61() != null) {
            iso8583.setField(61, data.getField61());

        }
    }

    public int sendRevesal(TransLogData data) {
        setFields(data);
        retVal = OnLineTrans(transUI);
        if (retVal == 0) {
            RspCode = iso8583.getfield(39);

            TransLog log = TransLog.getInstance(menus.idAcquirer);
            TransLogData dataR = log.searchTransLogByTraceNo(data.getTraceNo());
            if (dataR != null) {
                dataR.setReversed(true);
                int index = TransLog.getInstance(menus.idAcquirer).getCurrentIndex(data);
                TransLog.getInstance(menus.idAcquirer).deleteTransLog(index);
                TransLog.getInstance(menus.idAcquirer).saveLog(dataR, menus.idAcquirer);
            }

            if (pp_request.getTypeTrans().equals("04")){ //cuando es reverso de caja si se obtiene respuesta se toma como aprobado --- #5395 Bitacora de defectos, novedad 80
                return retVal;
            }
            if (RspCode.equals("00") || RspCode.equals("12") || RspCode.equals("25")) {
                return retVal;
            } else {
                if (RspCode.equals("02")){
                    retVal = 3002;
                }else if (RspCode.equals("05")){
                    retVal = Tcode.T_trans_rejected;
                }else {
                    data.setRspCode("06");
                    rspCode = "06";
                    retVal =  Tcode.T_err_send_rev;
                }
            }
        } else if (retVal == Tcode.T_package_mac_err) {
            data.setRspCode("A0");
            rspCode = "A0";
            //TransLog.saveReversal(data, false);
        } else if (retVal == Tcode.T_receive_err) {
            data.setRspCode("08");
            rspCode = "08";
            //TransLog.saveReversal(data, false);
        } else if (retVal == Tcode.T_package_illegal) {
            data.setRspCode("08");
            rspCode = "08";
            //TransLog.saveReversal(data, false);
        } else {
            Logger.debug("Revesal result :" + retVal);
        }

        if (pp_request.getTypeTrans().equals("04")){
            TransLog.saveReversal(data, true);
        }
        return retVal;
    }
}
