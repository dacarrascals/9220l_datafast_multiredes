package com.datafast.transactions.venta;

import android.content.Context;
import android.media.ToneGenerator;
import android.util.Log;

import com.datafast.pinpad.cmd.PC.Control;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
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
import com.newpos.libpay.utils.ISOUtil;

import java.sql.SQLOutput;

import cn.desert.newpos.payui.UIUtils;

import static com.android.newpos.pay.StartAppDATAFAST.lastPan;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;

public class Venta extends FinanceTrans implements TransPresenter {

    waitRspReverse callbackRsp = null;
    private String aCmd;

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
        processPPFail.setTransName(TransEName);
        Logger.information("Venta.java -> Se crea constructor de Venta");
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

        try {
            Logger.information("Venta.java -> start()");

            final int reverso = validateReverseCash();
            if (reverso != 1995) {
                return;
            }
            Logger.information("Venta.java -> verificar batch");
            if (!checkBatchAndSettle(true, true)) {
                return;
            }
            Logger.information("Venta.java ->  setea montos");
            if (setAmountPP()) {
                if (CardProcess(INMODE_IC | INMODE_MAG | INMODE_NFC | INMODE_HAND)) {
                    if (!prepareOnline()) {
                        if (retVal==Tcode.T_gen_2_ac_fail) {
                            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
                        }else {
                            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
                            transUI.showError(timeout, retVal, processPPFail);
                            return;
                        }
                    }

                    if (aCmd.equals(PP)) {
                        if ((inputMode == ENTRY_MODE_MAG && !isPinExist) && retVal == Tcode.T_user_cancel_input) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            callbackRsp = null;
                        } else {
                            callbackRsp = new waitRspReverse() {
                                @Override
                                public void getWaitRspReverse(int status) {
                                    reversePinpad(status);
                                }
                            };
                        }
                    }
                } else {
                    UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i("Venta", String.valueOf(retVal));
                    Logger.information("Venta.java -> retval   "+ String.valueOf(retVal) );
                    if (retVal==Tcode.T_user_cancel_input)
                         transUI.showError(timeout, retVal, processPPFail);
                    return;
                    //transUI.showError(timeout, retVal,processPPFail);
                }
            }

            if (aCmd.equals(PP) && retVal != Tcode.T_no_answer && retVal != Tcode.T_socket_err) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (callbackRsp != null) {
                    callbackRsp.getWaitRspReverse(retVal);
                }
            } else if (!aCmd.equals(LT) && !aCmd.equals(CT)) {
                transUI.showfinish();
            }
            if (Control.failEchoTest) {
                Control.failEchoTest = false;
                Control.echoTest = true;
            }
            Logger.debug("SaleTrans>>finish");
        } catch (Exception e){
            Logger.information("Venta.java -> Se ingresa al catch  " + e.toString());
            retVal = Tcode.T_err_trm;
            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
            transUI.showError(timeout, retVal, processPPFail);
            return;
        }
    }

    /**
     * 准备联机
     */
    private boolean prepareOnline() {

        Logger.information("Venta.java -> Se ingresa al prepareOnline()");

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
                            lastPan=Pan;
                            transUI.trannSuccess(timeout, Tcode.Status.read_card_ok);
                           /* UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);*/
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

                    if ((retVal = CommonFunctionalities.setTipoCuenta(timeout, ProcCode, transUI, ISOUtil.stringToBoolean(rango.getTIPO_DE_CUENTA()))) != 0) {
                        return false;
                    }

                    ProcCode = CommonFunctionalities.getProCode();

                    field58();
                    if (retVal == 0) {
                        transUI.handling(timeout, Tcode.Status.connecting_center);
                        setDatas(inputMode);
                        if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC) {
                            retVal = OnlineTrans(emv);
                        } else {
                            retVal = OnlineTrans(null);
                        }
                        if (retVal == 0) {
                            Logger.information("Venta.java -> Ok prepareOnline()");
                            //Solo se usa en la venta (Gasolinera)
                            msgAprob(Tcode.Status.sale_succ,true);
                            CommonFunctionalities.obtenerBin(Pan);
                            clearPan();
                            return true;
                        } else {
                            Logger.information("Venta.java -> Fallido prepareOnline()");
                            if (retVal != Tcode.T_no_answer && retVal != Tcode.T_socket_err) {
                                processPPFail.cmdCancel(Server.cmd, retVal);
                                transUI.handlingError(timeout, retVal);
                            } else {
                                transUI.showError(timeout, retVal,processPPFail);
                            }
                            //transUI.showError(timeout, retVal,processPPFail);
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

    public void  reversePinpad(int status){
        retVal = status;
        if (Reverse() != 0) {
            if (retVal != Tcode.T_not_reverse) {
                UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
                transUI.showError(timeout, retVal);
            } else {
                transUI.showfinish();
            }
        } else {
            transUI.trannSuccess(timeout, Tcode.Status.rev_receive_ok);
        }

    }
}
