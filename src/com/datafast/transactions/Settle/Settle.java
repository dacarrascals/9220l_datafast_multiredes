package com.datafast.transactions.Settle;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.ToneGenerator;

import com.android.desert.keyboard.InputInfo;
import com.datafast.menus.MenuAction;
import com.datafast.menus.menus;
import com.datafast.tools_bacth.ToolsBatch;
import com.datafast.transactions.common.CommonFunctionalities;
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.trans.translog.TransLogLastSettle;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.desert.newpos.payui.UIUtils;

import static android.content.Context.MODE_PRIVATE;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREAUTO;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREVOUCHER;
import static com.datafast.menus.MenuAction.callBackSeatle;
import static com.datafast.menus.MenuAction.makeInitCallback;
import static com.datafast.menus.menus.idAcquirer;

public class Settle extends FinanceTrans implements TransPresenter {

    public static boolean isSeatleOk = false;

    public Settle(Context ctx, String transEn, TransInputPara p) {
        super(ctx, transEn);
        para = p;
        transUI = para.getTransUI();
        isReversal = false;
        isSaveLog = false;
        isDebit = false;
        isProcPreTrans = false;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {

        if (!ToolsBatch.statusTrans(idAcquirer)) {
            transUI.showError(timeout, Tcode.T_err_no_trans);
            return;
        }

        List<TransLogData> list = TransLog.getInstance(menus.idAcquirer).getData();
        long amount_USD = 0;
        int contTrans = 0;
        long amountVoid_USD = 0;
        long ivaTotal = 0;
        int contTransVoid = 0;
        StringBuilder data = new StringBuilder();

        for (TransLogData transLogData : list) {
            if (!transLogData.getIsVoided()) {
                if (!transLogData.isTarjetaCierre()) {
                    amount_USD += transLogData.getAmount();
                    ivaTotal += transLogData.getAmmountIVA();
                    contTrans++;
                }
            } else {
                if (!transLogData.isTarjetaCierre()) {
                    amountVoid_USD += transLogData.getAmount();
                    contTransVoid++;
                }
            }
        }

        data.append("TRANS ACTIVAS: ");
        data.append(String.valueOf(contTrans));
        data.append("\n");
        data.append("VENTAS   :  $  ");
        data.append(PAYUtils.getStrAmount(amount_USD));
        data.append("\n");
        data.append("IVA TOTAL:  $  ");
        data.append(PAYUtils.getStrAmount(ivaTotal));
        data.append("\n");
        data.append("\n");
        data.append("¿REALIZAR CIERRE?");

        InputInfo inputInfo = transUI.showConfirmAmount(timeout, "CIERRE", data.toString(), "", false);
        if (inputInfo.isResultFlag()) {
            packField63(amount_USD, contTrans);
            prepareOnline();
        } else {
            retVal = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, retVal);
        }

        if (retVal== 0) {
            transUI.trannSuccess(timeout, Tcode.Status.logonout_succ);
            UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);

            if (makeInitCallback != null) {
                makeInitCallback.getMakeInitCallback(true);
            }

        } else {
            transUI.showError(timeout, retVal);
            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
            makeInitCallback = null;
        }
        
        MenuAction.callbackPrint = null;
    }

    private void packField63(long totalAmnt, int len) {
        Field63 = "";
        String auxNum = ISOUtil.padleft(String.valueOf(len) + "", 3, '0');
        Field63 += auxNum;
        auxNum = ISOUtil.padleft(String.valueOf(totalAmnt) + "", 12, '0');
        Field63 += auxNum;
        Field63 += "000000000000000000000000000000000000000000000000000000000000000000000000000";

        Field63 = ISOUtil.convertStringToHex(Field63);
    }

    private void prepareOnline() {

        transUI.handling(timeout, Tcode.Status.connecting_center);

        retVal = OnlineTrans(null);

        Logger.debug("Logout>>OnlineTrans=" + retVal);

        if (retVal == 0) {

            int val = Integer.parseInt(TMConfig.getInstance().getBatchNo());
            TMConfig.getInstance().setBatchNo(val).save();
            //BatchNo =  ToolsBatch.incBatchNo(BatchNo);
            //acquirerRow.updateSelectACQUIRER_ROW("acquirers", idAcquirer, "sb_curr_batch_no", BatchNo, context);

            //TransLogLastSettle.getInstance(true).setTransLogData(TransLog.getInstance(idAcquirer).getData());
            //TransLogLastSettle.getInstance(true).saveLog();

            TransLog.getInstance(idAcquirer).clearAll(idAcquirer);
            TransLog.getInstance(idAcquirer).clearAll(idAcquirer + FILE_NAME_PREAUTO);
            TransLog.getInstance(idAcquirer).clearAll(idAcquirer + FILE_NAME_PREVOUCHER);
            CommonFunctionalities.saveSettle(context);

            CommonFunctionalities.limpiarPanTarjGasolinera("");

            //isSeatleOk = true;
            guardarFechaDeUltimoCierre();
            transUI.trannSuccess(timeout, Tcode.Status.logonout_succ, "");


        } else {
            transUI.showError(timeout, retVal);
        }
    }

    private void guardarFechaDeUltimoCierre() {
        DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date fechaActual = new Date();
        SharedPreferences.Editor editor = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE).edit();
        editor.putString("fechaUltimoCierre", hourdateFormat.format(fechaActual));
        editor.apply();
    }
}
