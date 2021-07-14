package com.newpos.libpay.trans.finace.scan;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.printer.Printer;

/**
 * Created by zhouqiang on 2017/11/14.
 */

public class ScanRefund extends FinanceTrans implements TransPresenter{
    private TransLogData data  = null ;

    public ScanRefund(Context ctx, String transEname , TransInputPara para) {
        super(ctx, transEname);
        this.para = para ;
        this.transUI = para.getTransUI() ;
        isSaveLog = true;
        isProcPreTrans = true;
        isProcSuffix = true;
    }

    @Override
    public void start() {
        InputInfo info = transUI.getOutsideInput(timeout , InputManager.Mode.PASSWORD, "");
        if(info.isResultFlag()){
            String master_pass = info.getResult();
            if(master_pass.equals(cfg.getMasterPass())){
                info = transUI.getOutsideInput(timeout , InputManager.Mode.VOUCHER, "");
                if(info.isResultFlag()){
                    String tn = info.getResult() ;
                    info = transUI.getOutsideInput(timeout , InputManager.Mode.REFERENCE, "");
                    if(info.isResultFlag()){
                        TransLog log = TransLog.getInstance() ;
                        data = log.searchTransLogByTraceNo(tn);
                        if(data!=null && !data.getIsVoided() && data.getEName().equals(Type.SCANSALE) && data.getRRN().equals(info.getResult())){
                            retVal = transUI.showTransInfo(timeout , data);
                            if(0 == retVal){
                                Amount = data.getAmount();
                                inputMode = ENTRY_MODE_QRC ;
                                RRN = data.getRRN();
                                AuthCode = data.getAuthCode();
                                Field61 = data.getBatchNo()+data.getTraceNo();
                                if(isSaveLog){
                                    TransLogData d = setScanData(data.getPan());
                                    transLog.saveLog(d);
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
                                    data.setVoided(true);
                                    transUI.trannSuccess(timeout , Tcode.Status.scan_refund_success ,
                                            PAYUtils.getStrAmount(Amount));
                                }else {
                                    transUI.showError(timeout , retVal);
                                }
                            }else{
                                transUI.showError(timeout , Tcode.T_user_cancel_operation);
                            }
                        }else {
                            transUI.showError(timeout , Tcode.T_not_find_trans);
                        }
                    }else {
                        transUI.showError(timeout , info.getErrno());
                    }
                }else {
                    transUI.showError(timeout , info.getErrno());
                }
            }else {
                transUI.showError(timeout , Tcode.T_master_pass_err);
            }
        }else {
            transUI.showError(timeout , info.getErrno());
        }


        Logger.debug("ScanRefund>>finish");
        return;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }
}
