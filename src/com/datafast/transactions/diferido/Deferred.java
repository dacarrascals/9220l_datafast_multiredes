package com.datafast.transactions.diferido;

import android.content.Context;
import android.media.ToneGenerator;

import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.server.server_tcp.Server;
import com.datafast.transactions.callbacks.waitRspReverse;
import com.datafast.transactions.common.CommonFunctionalities;
import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.UIUtils;

import static cn.desert.newpos.payui.master.MasterControl.callbackFallback;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.menus.menus.contFallback;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;

public class Deferred extends FinanceTrans implements TransPresenter {

    waitRspReverse callbackRsp = null;
    private String aCmd;

    public Deferred(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
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
        processPPFail = new ProcessPPFail(ctx, iso8583);
        this.aCmd = Server.cmd;
    }


    @Override
    public ISO8583 getISO8583() {
        return null;
    }


    @Override
    public void start() {

        if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_DIFERIDO())){
            transUI.showError(timeout, Tcode.T_err_deferred, processPPFail);
            return;
        }

        if (!checkBatchAndSettle(true,true)){
            return;
        }

        if (procesarDiferido() == 0) {
            //transUI.handling(timeout, Tcode.Status.diferido_exitoso);
            UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
        } else {
            transUI.handlingError(timeout, retVal);
            processPPFail.cmdCancel(Server.cmd,retVal);
            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
            try{
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Logger.debug("DiferidoTrans>>finish");

        if (retVal != 106 && retVal != 104) {
            if (aCmd.equals(PP)){
                callbackRsp = new waitRspReverse() {
                    @Override
                    public void getWaitRspReverse(int status) {
                        retVal = status;
                        if (Reverse() != 0){
                            if (retVal != Tcode.T_not_reverse){
                                transUI.showError(timeout,retVal);
                            }else {
                                transUI.showfinish();
                            }
                        }else {
                            transUI.trannSuccess(timeout,Tcode.Status.rev_receive_ok);
                        }
                    }
                };
            }
        }

        if (aCmd.equals(PP)){
            if (callbackRsp != null){
                callbackRsp.getWaitRspReverse(retVal);
            }
        }
    }

    private int procesarDiferido() {
        if (setAmountPP()){

            if (!PAYUtils.isNullWithTrim(pp_request.getIdCodDef())){

                for (int i = 0; i < deferredType.length; i++) {
                    if (("0" + pp_request.getIdCodDef()).equals(deferredType[i][0])) {
                        TypeDeferred = deferredType[i][1];
                        break;
                    }
                }
            }

            if (!Cuotas()) {
                retVal = Tcode.T_user_cancel_operation;
                transUI.showError(timeout, retVal,processPPFail);
                return retVal;
            }

            if (!CardProcess(INMODE_IC | INMODE_MAG | INMODE_NFC | INMODE_HAND)){
                if(retVal == 0){
                    retVal = Tcode.T_user_cancel_input;
                }
                contFallback = 0;
                return retVal;
            }

            if (!prepareOnline()){
                return retVal;
            }

        }
        return retVal;
    }

    private boolean Cuotas() {
        boolean ret = false;
        if (!PAYUtils.isNullWithTrim(pp_request.getLimitDef())){
            numCuotasDeferred = pp_request.getLimitDef();

            if (numCuotasDeferred.equals("00") || numCuotasDeferred.equals("0")){
                transUI.toasTrans(Tcode.T_err_invalid_len, true, true);
                processPPFail.cmdCancel(Server.cmd, retVal);
            }else{
                ret = true;
            }
        }
        return ret;
    }

    /**
     * 准备联机
     */
    private boolean prepareOnline() {

        if ((retVal = CommonFunctionalities.setTipoCuenta(timeout, ProcCode, transUI, ISOUtil.stringToBoolean(rango.getTIPO_DE_CUENTA()))) != 0) {
            return false;
        }

        ProcCode = CommonFunctionalities.getProCode();

        /*if ((retVal = CommonFunctionalities.setPrompt(timeout, TransEName, listPrompts, transUI)) != 0) {
            retVal = Tcode.T_user_cancel_input;
            return false;
        }

        Field58 = CommonFunctionalities.getFld58Prompts();*/

        fild58();

        /*if ((retVal = CommonFunctionalities.confirmAmount(timeout, TransEName, transUI, montos)) != 0) {
            retVal = Tcode.T_user_cancel_input;
            return false;
        }*/

        if (!requestPin1()) {
            return false;
        }

        if (retVal == 0) {

            transUI.handling(timeout, Tcode.Status.connecting_center);
            setDatas(inputMode);
            if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC) {
                retVal = OnlineTrans(emv);
            } else {
                retVal = OnlineTrans(null);
            }


            Logger.debug("SaleTrans>>OnlineTrans=" + retVal);
            clearPan();
            if (retVal == 0) {
                if (typeCoin != null) {
                    switch (typeCoin) {
                        case DOLAR:
                            String authCode = iso8583.getfield(38);
                            transUI.handlingInfo(timeout, Tcode.Status.diferido_exitoso,"\nAPROBADA #" + authCode);
                            return true;
                    }
                } else {
                    transUI.handlingInfo(timeout, Tcode.Status.diferido_exitoso, "");
                    return true;
                }
            }

            if (callbackFallback != null) {
                callbackFallback.getResponseTransFallback(retVal, null);
            }
        } else {
            transUI.showError(timeout, retVal,processPPFail);
        }
        return false;
    }
}
