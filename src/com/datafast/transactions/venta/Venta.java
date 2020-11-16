package com.datafast.transactions.venta;

import android.content.Context;
import android.media.ToneGenerator;
import android.util.Log;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.server.activity.ServerTCP;
import com.datafast.server.server_tcp.Server;
import com.datafast.transactions.callbacks.waitRspReverse;
import com.datafast.transactions.common.CommonFunctionalities;
import com.datafast.transactions.common.GetAmount;
import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.trans.translog.TransLogReverse;
import com.newpos.libpay.utils.ISOUtil;

import java.util.List;

import cn.desert.newpos.payui.UIUtils;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_REVERSE;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;

public class Venta extends FinanceTrans implements TransPresenter {

    waitRspReverse callbackRsp = null;
    private String aCmd;
    boolean rev = false;

    /**
     * 金融交易类构造
     *
     * @param ctx        Context
     * @param transEname Nombre Transaccion
     * @param p          Parametros
     */
    public Venta(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        init(transEname, p);
        processPPFail = new ProcessPPFail(ctx, iso8583);
    }

    private void init(String transEname, TransInputPara p) {
        para = p;
        transUI = para.getTransUI();
        isReversal = true;
        isProcPreTrans = true;
        isSaveLog = true;
        isDebit = true;
        TransEName = transEname;
        currency_name = CommonFunctionalities.tipoMoneda()[0];
        typeCoin = CommonFunctionalities.tipoMoneda()[1];
        host_id = idAcquirer;
        this.aCmd = Server.cmd;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    @Override
    public void start() {

        TransLogData revesalData = TransLog.getReversal(true);
        if (revesalData != null){
            rev = true;
        }

        final int reverso = validateReverseCash();
        if (reverso != 1995){
            return;
        }

        if (!checkBatchAndSettle(true,true)){
            return;
        }

        if (setAmountPP()) {
            if (CardProcess(INMODE_IC | INMODE_MAG | INMODE_NFC | INMODE_HAND)){
                if(!prepareOnline()) {
                    UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
                }

                if (aCmd.equals(PP)){
                    if ((inputMode == ENTRY_MODE_MAG && !isPinExist) && retVal == Tcode.T_user_cancel_input) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        callbackRsp = null;
                    } else {
                        if (!rev && retVal == Tcode.T_no_answer){
                            transUI.showError(timeout, retVal, processPPFail);
                        }else {
                            callbackRsp = new waitRspReverse() {
                                @Override
                                public void getWaitRspReverse(int status) {
                                    retVal = status;
                                    if (Reverse() != 0) {
                                        if (retVal != Tcode.T_not_reverse) {
                                            transUI.showError(timeout, retVal);
                                        } else {
                                            transUI.showfinish();
                                        }
                                    } else {
                                        transUI.trannSuccess(timeout, Tcode.Status.rev_receive_ok);
                                    }
                                }
                            };
                        }
                    }
                }
            } else {
                UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                transUI.showError(timeout, retVal,processPPFail);
                Log.i("Venta" , String.valueOf(retVal));
            }
        }

        if (aCmd.equals(PP) && rev) {
            if (callbackRsp != null) {
                callbackRsp.getWaitRspReverse(retVal);
            }
        } else {
            transUI.showfinish();
        }
        Logger.debug("SaleTrans>>finish");
    }

    /**
     * 准备联机
     */
    private boolean prepareOnline() {

        if (retVal == 0) {
            switch (Server.cmd) {
                case LT:
                case CT:
                    retVal = OnlineTrans(null);
                    if (retVal == 0){
                        if (Server.cmd.equals(CT)){
                            transUI.trannSuccess(timeout, Tcode.Status.request_card_ok);
                            UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            return true;
                        }else {
                            transUI.trannSuccess(timeout, Tcode.Status.read_card_ok);
                            UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                            return true;
                        }
                    }else{
                        transUI.showError(timeout, retVal);
                    }
                    break;
                case PP:
                    if (!requestPin1()) {
                        return false;
                    }
                    if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())){
                        GetAmount.sumarMontoFijo();
                        montoFijo = GetAmount.getmontoFijo();
                        montos[6] = montoFijo;
                        tipoMontoFijo = GetAmount.getTipoMontoFijo();
                    }
                    fild58();
                    if (retVal == 0) {
                        transUI.handling(timeout, Tcode.Status.connecting_center);
                        setDatas(inputMode);
                        if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC) {
                            retVal = OnlineTrans(emv);
                        } else {
                            retVal = OnlineTrans(null);
                        }
                        if (retVal == 0) {
                            //Solo se usa en la venta (Gasolinera)
                            msgAprob(Tcode.Status.sale_succ,true);
                            clearPan();
                            CommonFunctionalities.obtenerBin(Pan);
                            return true;
                        } else {
                            if (rev){
                                processPPFail.cmdCancel(Server.cmd,retVal);
                                transUI.handlingError(timeout, retVal);
                                //transUI.showError(timeout, retVal,processPPFail);
                            }
                            clearPan();
                            return false;
                        }
                    } else{
                        transUI.showError(timeout, retVal,processPPFail);
                        return false;
                    }
                default:
                    break;
            }
        }else {
            transUI.showError(timeout, retVal,processPPFail);
            return false;
        }
        return false;
    }
}
