package com.datafast.transactions.echotest;

import android.content.Context;
import android.media.ToneGenerator;

import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.UIUtils;

import static com.android.newpos.pay.StartAppDATAFAST.VERSION;
import static com.android.newpos.pay.StartAppDATAFAST.host_confi;
import static com.datafast.menus.menus.idAcquirer;

public class EchoTest extends FinanceTrans implements TransPresenter {

    private byte[] respData;
    private String rspCode;
    private int timeOutScreensInit = 5 * 1000;

    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     */
    public EchoTest(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        TransEName = transEname;
        para = p ;
        if(para != null) {
            transUI = para.getTransUI();
        }
        isReversal = false;
        isSaveLog = false;
        isDebit = false;
        host_id = idAcquirer;
    }

    @Override
    public void start() {

        /*if (!acquirerRow.selectACQUIRER_ROW(idAcquirer, context)){
            retVal = Tcode.T_unsupport_card;
            transUI.showError(timeout, Tcode.T_unsupport_card);
            return;
        }*/

        transUI.handling(timeout , Tcode.Status.echo_test);
        retVal = echoTest();
        if(retVal!=0){
            transUI.showError(timeOutScreensInit , retVal);
            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
        }else{
            transUI.trannSuccess(timeout , Tcode.Status.echo_test_success);
            UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
        }
        Logger.debug("InitTrans>>finish");
        return;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    private int echoTest(){
//        TransEName = Type.ECHO_TEST;
        setFixedDatas();
        setFieldInit();

        retVal = sendRcvdInit();

        if (retVal != 0) {
            return retVal ;
        }

        rspCode = iso8583.getfield(39);

        if (rspCode != null && (rspCode.equals(ISO8583.RSPCODE.RSP_00) || rspCode.equals(ISO8583.RSPCODE.RSP_89))) {
            Logger.debug("LogoutTrans>>Logout>>Init Exitosa!");
            return 0 ;

        } else {
            if (rspCode == null) {
                return Tcode.T_receive_err;
            } else {
                return Integer.valueOf(rspCode);
            }
        }
    }

    private void setFieldInit(){
        iso8583.setHasMac(false);
        iso8583.clearData();

        LocalTime = PAYUtils.getLocalTime();
        LocalDate = PAYUtils.getLocalDate();

        if (MsgID != null) {
            iso8583.setField(0, MsgID);
        }
        if (ProcCode != null) {
            iso8583.setField(3, ProcCode);
        }

        if (TraceNo != null) {
            iso8583.setField(11, TraceNo);
        }

        if (host_confi.getNII_ECHO_TEST() != null) {
            Nii = ISOUtil.padleft(host_confi.getNII_ECHO_TEST()+ "", 4, '0');
            iso8583.setField(24, Nii);
        }

        if (TermID != null) {
            iso8583.setField(41, TermID);
        }

        if (MerchID != null){
            iso8583.setField(42, MerchID);
        }


        iso8583.setField(60, VERSION);

    }

    private int sendRcvdInit() {
        int retries = Integer.parseInt(host_confi.getREINTENTOS());  //Intentos
        int startRetries = 1;
        int rta;

        do {
            transUI.handling(timeout, Tcode.Status.connecting_center, "CONECTANDO IP1 (" + startRetries + ")");
            rta = connect();
            if (rta == 0) {
                startRetries = retries;
            }
            startRetries ++;
            transUI.handling(timeout, Tcode.Status.msg_retry);
            transUI.handling(timeout, Tcode.Status.connecting_center);
        }while (retries >= startRetries);

        if (rta == -1){
            retries = Integer.parseInt(host_confi.getREINTENTOS());
            startRetries = 1;
            cfg = TMConfig.getInstance();
            cfg.setPubCommun(false);
            loadConfigIP();
            do {
                transUI.handling(timeout, Tcode.Status.connecting_center, "CONECTANDO IP2 (" + startRetries+ ")");
                rta = connect();
                if (rta == 0) {
                    startRetries = retries;
                }
                startRetries ++;
            }while (retries >= startRetries);
        }

        if (rta == -1) {
            return Tcode.T_socket_err;
        }
        transUI.handling(timeout, Tcode.Status.send_data_2_server);
        if (send() == -1) {
            return Tcode.T_send_err;
        }
        transUI.handling(timeout, Tcode.Status.send_over_2_recv);
        respData = recive();

        netWork.close();

        if (respData == null || respData.length <= 0) {
            return Tcode.T_receive_err;
        }

        int ret = iso8583.unPacketISO8583(respData);

        RspCode = iso8583.getfield(39);
        if (!"00".equals(RspCode)&& !"89".equals(RspCode)) {
            TransLog.clearReveral(false);
            //Trans reject
            ret = formatRsp(RspCode);
            //printDataReject(Pan, TraceNo, ret);
            return ret;
        }

        if (ret == 0) {
            if (isTraceNoInc) {
                cfg.incTraceNo().save();
                TraceNo = cfg.getTraceNo();
            }
        }
        return ret;
    }
}