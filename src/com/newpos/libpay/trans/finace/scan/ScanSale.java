package com.newpos.libpay.trans.finace.scan;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.device.scanner.QRCInfo;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.printer.Printer;

/**
 * Created by zhouqiang on 2017/11/14.
 */

public class ScanSale extends FinanceTrans implements TransPresenter{

    public ScanSale(Context ctx, String transEname  , TransInputPara para) {
        super(ctx, transEname);
        this.para = para ;
        this.transUI = para.getTransUI() ;
        isSaveLog = true;
        isProcPreTrans = true;
        isProcSuffix = true;
    }

    @Override
    public void start() {
        InputInfo inputInfo = transUI.getOutsideInput(timeout , InputManager.Mode.AMOUNT, "");
        if(inputInfo.isResultFlag()){
            Logger.debug(inputInfo.getResult());
            Amount = Long.parseLong(inputInfo.getResult());
            inputMode = ENTRY_MODE_QRC ;
            QRCInfo qrcInfo = transUI.getQRCInfo(timeout , InputManager.Style.ALIPAY);
            if(qrcInfo.isResultFalg()){
                String paycode = qrcInfo.getQrc() ;
                if(isSaveLog){
                    TransLogData data = setScanData(paycode);
                    transLog.saveLog(data);
                }
                cfg.incTraceNo();
                if(para.isNeedPrint()){
                    transUI.handling(timeout , Tcode.Status.printing_recept);
                    PrintManager printManager = PrintManager.getmInstance(context , transUI);
                    do{
                        retVal = printManager.print(transLog.getLastTransLog(), false, false);
                    }while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
                    if (retVal == Printer.PRINTER_OK) {
                        retVal = 0 ;
                    } else {
                        retVal = Tcode.T_printer_exception ;
                    }
                }else {
                    retVal = 0 ;
                }
                if(retVal == 0){
                    transUI.trannSuccess(timeout , Tcode.Status.scan_pay_success ,
                            PAYUtils.getStrAmount(Amount));
                }else {
                    transUI.showError(timeout , retVal);
                }
            }else {
                transUI.showError(timeout , qrcInfo.getErrno());
            }
        }else {
            transUI.showError(timeout , inputInfo.getErrno());
        }

        Logger.debug("ScanSale>>finish");
        return;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }
}
