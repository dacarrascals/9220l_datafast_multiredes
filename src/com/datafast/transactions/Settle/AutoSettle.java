package com.datafast.transactions.Settle;

import android.content.Context;

import com.datafast.tools_bacth.ToolsBatch;
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

import java.util.List;

import static com.datafast.menus.menus.idAcquirer;

//import static com.datafast.menus.menus.acquirerRow;

public class AutoSettle extends FinanceTrans implements TransPresenter {

    private static final int ACTIVAR_CIERRE_AUTOMATICO = 4;
    private static boolean settleDone;

    public AutoSettle(Context ctx, String transEn, TransInputPara p) {
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
        for (int i = 1; i <= 1; i++) {

            idAcquirer = String.valueOf("0" + i);

            if (ToolsBatch.statusTrans(idAcquirer)) {
                /*if (!acquirerRow.selectACQUIRER_ROW(idAcquirer, context)) {
                    retVal = Tcode.T_unsupport_card;
                    transUI.showError(timeout, Tcode.T_unsupport_card);
                    return;
                }*/

                List<TransLogData> list = TransLog.getInstance(idAcquirer).getData();
                long amount_BS = 0;
                long amount_USD = 0;
                int contTrans = 0;

                for (TransLogData transLogData : list) {
                    if (!transLogData.getIsVoided()) {

                        switch (transLogData.getTypeCoin()) {
                            case LOCAL:
                                amount_BS += transLogData.getAmount() + transLogData.getTipAmout();
                                break;

                            case DOLAR:
                                amount_USD += transLogData.getAmount() + transLogData.getTipAmout();
                                break;
                        }
                        contTrans++;
                    }
                }
                packField63(amount_BS + amount_USD, contTrans);
                prepareOnline();
            }
        }

        setSettleDone(true);
        transUI.trannSuccess(timeout, Tcode.Status.logonout_succ, "");
    }

    private void packField63(long totalAmnt, int len) {
        Field63 = "";
        String auxNum = ISOUtil.padleft(String.valueOf(len) + "", 3, '0');
        Field63 += auxNum;
        auxNum = ISOUtil.padleft(String.valueOf(totalAmnt) + "", 12, '0');
        Field63 += auxNum;
        Field63 += "000000000000000000000000000000000000000000000";

        Field63 = ISOUtil.convertStringToHex(Field63);
    }

    private void prepareOnline() {

        transUI.handling(timeout, Tcode.Status.connecting_center);

        retVal = OnlineTrans(null);

        Logger.debug("Logout>>OnlineTrans=" + retVal);

        if (retVal == 0) {

            int val = Integer.parseInt(TMConfig.getInstance().getBatchNo());
            TMConfig.getInstance().setBatchNo(val).save();
            //BatchNo = ToolsBatch.incBatchNo(BatchNo);
            //acquirerRow.updateSelectACQUIRER_ROW("acquirers", idAcquirer, "sb_curr_batch_no", BatchNo, context);

            TransLogLastSettle.getInstance(true).setTransLogData(TransLog.getInstance(idAcquirer).getData());
            TransLogLastSettle.getInstance(true).saveLog();

            TransLog.getInstance(idAcquirer).clearAll(idAcquirer);
        } else {
            transUI.showError(timeout, retVal);
        }
    }

    public static int getActivarCierreAutomatico() {
        return ACTIVAR_CIERRE_AUTOMATICO;
    }

    public static boolean isSettleDone() {
        return settleDone;
    }

    public static void setSettleDone(boolean settleDone) {
        AutoSettle.settleDone = settleDone;
    }
}
