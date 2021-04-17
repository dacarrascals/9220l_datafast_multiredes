package com.newpos.libpay.trans.manager;

import android.content.Context;

import com.newpos.libpay.Logger;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.printer.Printer;

import java.util.List;

/**
 * Created by zhouqiang on 2017/3/31.
 * 结算交易处理类
 * @author zhouqiang
 */
@Deprecated
public class SettleTrans extends Trans implements TransPresenter{

    private int sumCount = 0 ;

    public SettleTrans(Context ctx, String transEname , TransInputPara p) {
        super(ctx, transEname);
        iso8583.setHasMac(false);
        para = p ;
        transUI = para.getTransUI() ;
        TransEName = transEname ;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {
/**
        transUI.handling(timeout , Tcode.Status.settling_start);
        settle();
*/
    }

    /**
     * 结算入口
     */
    private void settle(){
        Logger.debug("SettleTrans>>settle");
        setTraceNoInc(true);
        if(MsgID!=null) {
            iso8583.setField(0, MsgID);
        }
        iso8583.setField(11 , cfg.getTraceNo());
        iso8583.setField(41 , cfg.getTermID());
        iso8583.setField(42 , cfg.getMerchID());
        iso8583.setField(49 , cfg.getCurrencyCode());
        Logger.debug("SettleTrans>>settle>>Field60 = "+Field60);
        iso8583.setField(60 , Field60);
        iso8583.setField(63 , formatOPN(String.valueOf(cfg.getOprNo())));
        retVal = getSettleSUM48();
        if(retVal==0){
            iso8583.setField(48 , Field48);
            retVal = OnLineTrans(transUI) ;
            if(retVal!=0){
                transUI.showError(timeout , retVal);
            }else {
                String rsp = iso8583.getfield(39);
                Logger.debug("SettleTrans>>settle>>rsp="+rsp);
                if(rsp.equals("00")){
                    String f48 = iso8583.getfield(48);
                    Logger.debug("SettleTrans>>settle>>f48="+f48);
                    int flag = 1 ;
                    shell2ReversalUpsend(flag);
                }else {
                    retVal = Integer.parseInt(rsp) ;
                }
            }
        }else {
            transUI.showError(timeout , retVal);
        }
    }

    /**
     * 脚本上送，冲正上送
     * @param flag
     */
    private void shell2ReversalUpsend(int flag){
        Logger.debug("SettleTrans>>shell2ReversalUpsend");
        TransLogData data = TransLog.getScriptResult();
        retVal = 0 ;
        if (data != null) {
            transUI.handling(timeout , Tcode.Status.settle_send_shell);
            Logger.debug("SettleTrans>>shellUpsend>>结算进行脚本上送");
            setTraceNoInc(true);
            ScriptTrans script = new ScriptTrans(context, "SENDSCRIPT");
            retVal = script.sendScriptResult(data, transUI);
            if(retVal == 0) {
                TransLog.clearScriptResult();
            }
        }

        if(retVal == 0){
            TransLogData revesalData = TransLog.getReversal(false);
            if (revesalData != null) {
                setTraceNoInc(true);
                Logger.debug("FinanceTrans>>OnlineTrans>>结算进行冲正上送");
                transUI.handling(timeout , Tcode.Status.settle_send_reversal);
                RevesalTrans revesal = new RevesalTrans(context, "REVERSAL", transUI);
                for (int i = 0; i < cfg.getReversalCount() ; i++) {
                    //EL PINPAD NO REALIZA CIERRE SE COMENTA LINEA DE ENVIO DE REVERSO
                    //retVal = revesal.sendRevesal();
                    if(retVal == 0){
                        break;
                    }else {
                        if(retVal != Tcode.T_socket_err && retVal != Tcode.T_send_err){
                            continue;
                        }
                    }
                }

                if(retVal == Tcode.T_socket_err || retVal == Tcode.T_send_err){
                    //网络错误不能清除冲正，直接返回
                    transUI.showError(timeout , retVal);
                }else {
                    if(retVal != 0){
                        //冲正失败，清除冲正，结束交易
                        TransLog.clearReveral(false);
                        transUI.showError(timeout , Tcode.T_reversal_fail);
                    }else {
                        //上送明细开始
                        onlineUpsend(flag) ;
                    }
                }
            }else {
                //上送明细开始
                onlineUpsend(flag) ;
            }
        }else {
            transUI.showError(timeout , retVal);
        }
    }

    /**
     * 明细上送
     * @param flag
     */
    private void onlineUpsend(int flag){
        transUI.handling(timeout , Tcode.Status.settling_send_trans);
        Logger.debug("SettleTrans>>onlineUpsend");
        TransEName = "UPSEND" ;
        setTraceNoInc(false);
        List<TransLogData> list = TransLog.getInstance().getData() ;
        if(list!=null && list.size() > 0){
            //对账平
            if(flag == 1){
                for (int i = 0; i < list.size() ; i++) {
                    TransLogData data = list.get(i);
                    String type = data.getEName();
                    boolean need = type.equals(Type.SALE) || type.equals(Type.VOID) || type.equals(Type.QUICKPASS);
                    if (need) {
                        if (data.isICC() || data.isNFC()) {
                            //逐条上送
                            Field60 = null ;
                            setFixedDatas();
                            setFileds(data);
                            retVal = OnLineTrans(transUI);
                            if (retVal == 0) {
                                sumCount++;
                                String rsp = iso8583.getfield(39);
                                Logger.debug("SettleTrans>>onlineUpsend>>rsp = " + rsp);
                                if (!rsp.equals("00")) {
                                    retVal = Tcode.T_settle_tc_send_err;
                                    break;
                                }
                            } else {
                                Logger.debug("上送交易明细失败");
                                break;
                            }
                        }
                    }
                }
            }else {//对账不平
//                iso8583.clearData();
//                int mag_amount = 0 ;
//                int mag_count = 0 ;
//
//                mag_count ++ ;
//                mag_amount += data.getAmount() ;
//                if(mag_count / 8 == 0){
//                    //上送
//                    String a = String.valueOf(mag_amount);
//                    a = ISOUtil.padleft(a + "", 12, '0') ;
//                    if(MsgID!=null)
//                        iso8583.setField(0 , MsgID);
//                    iso8583.setField(4, a);
//                    iso8583.setField(22 , "0210");
//                    iso8583.setField(41 , cfg.getTermID());
//                    iso8583.setField(42 , cfg.getMerchID());
//                    appendField60("60");
//                    iso8583.setField(60 , Field60);
//                    Field62 = "610000"+a+cfg.getCurrencyCode();
//                    Field62 = BCD2ASC(Field62.getBytes());
//                    Logger.debug("SettleTrans>>setFileds>>Field62="+Field62);
//                    iso8583.setField(62 , Field62);
//                }
            }


            if(retVal == 0){
                settleOver();
            }else {
                transUI.showError(timeout , retVal);
            }
        }else {
            transUI.showError(timeout , Tcode.T_batch_no_trans);
        }
    }

    /**
     * 结算结束通知
     */
    private void settleOver(){
        transUI.handling(timeout , Tcode.Status.settling_over);
        Logger.debug("SettleTrans>>settleOver");
        TransEName = "UPSEND" ;
        setFixedDatas();
        setTraceNoInc(true);
        iso8583.clearData();
        if(MsgID!=null) {
            iso8583.setField(0, MsgID);
        }
        iso8583.setField(11 , cfg.getTraceNo());
        iso8583.setField(41 , cfg.getTermID());
        iso8583.setField(42 , cfg.getMerchID());
        Logger.debug("SettleTrans>>settleOver>>sumCount"+sumCount);
        iso8583.setField(48 , ISOUtil.padleft(String.valueOf(sumCount) , 4 , '0'));
        iso8583.setField(60 , "00"+cfg.getBatchNo()+"207");
        retVal = OnLineTrans(transUI);
        if(retVal!=0){
            transUI.showError(timeout , retVal);
        }else {
            String rsp = iso8583.getfield(39);
            Logger.debug("SettleTrans>>settleOver>>rsp = "+rsp);
            if(rsp.equals("00")){
                retVal = settlePrint();//打印结算单明细单
                if(retVal == 0){
                    //结算结束，清除本批次
                    TransLog.getInstance().clearAll();
                    //批次号强行加1
                    cfg.setBatchNo(Integer.parseInt(cfg.getBatchNo()) + 1 )
                        .incTraceNo()
                        .save();
                    transUI.handling(timeout , Tcode.Status.terminal_logonout);
                    LogoutTrans logoutTrans = new LogoutTrans(context , Type.LOGOUT , null);
                    retVal = logoutTrans.Logout();
                    if(retVal == 0){
                        transUI.trannSuccess(timeout , Tcode.Status.logonout_succ);
                    }else {
                        transUI.showError(timeout , retVal);
                    }
                }else {
                    //结算结束，清除本批次
                    TransLog.getInstance().clearAll();
                    transUI.showError(timeout , retVal);
                }
            }else {
                transUI.showError(timeout , Integer.parseInt(rsp));
            }
        }
    }

    /**
     * 结算打印
     * @return
     */
    private int settlePrint(){
        saveSettleLog();
        Logger.debug("SettleTrans>>settlePrint>>开始打单");
        transUI.handling(timeout , Tcode.Status.printing_recept);
        PrintManager print = PrintManager.getmInstance(context , transUI);
        do{
            retVal = print.printSettle(transLog.getLastTransLog());
        }while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
        if (retVal == Printer.PRINTER_OK) {
            retVal = 0 ;
        }else {
            retVal = Tcode.T_printer_exception ;
        }
        return retVal ;
    }

    /**
     * 存结算日志
     */
    private void saveSettleLog(){
        TransLogData LogData = new TransLogData();
        LogData.setOprNo(cfg.getOprNo());
        LogData.setEName(TransEName);
        LogData.setTraceNo(cfg.getTraceNo());
        LogData.setBatchNo(cfg.getBatchNo());
        LogData.setLocalDate(PAYUtils.getYear() + PAYUtils.getLocalDate());
        LogData.setLocalTime(PAYUtils.getLocalTime());
        LogData.setAAC(FinanceTrans.AAC_ARQC);
        transLog.saveLog(LogData);
        Logger.debug("save log logSize="+ TransLog.getInstance().getSize());
    }

    /**
     * 设置消息域内容
     * @param data
     */
    private void setFileds(TransLogData data){
        iso8583.clearData();
        iso8583.set62AttrDataType(3);
        if(MsgID!=null) {
            iso8583.setField(0, MsgID);
        }
        //iso8583.setField(2 , data.getCardFullNo());
        iso8583.setField(2 , data.getPan());
        String a = String.valueOf(data.getAmount());
        a = ISOUtil.padleft(a + "", 12, '0');
        Logger.debug("SettleTrans>>setFileds>>Amount="+a);
        iso8583.setField(4, a);
        iso8583.setField(11 , data.getTraceNo());
        Logger.debug("SettleTrans>>setFileds>>EntryMode"+data.getEntryMode());
        if(data.getEntryMode() == null){
            iso8583.setField(22 , "0510");
        }else {
            iso8583.setField(22 , data.getEntryMode());
        }
        iso8583.setField(41 , cfg.getTermID());
        iso8583.setField(42 , cfg.getMerchID());
        if(data.getICCData() != null){
            iso8583.setField(55 , ISOUtil.byte2hex(data.getICCData()));
        }
        appendField60("60");
        iso8583.setField(60 , Field60);
        Field62 = "610000"+a+cfg.getCurrencyCode();
        Logger.debug("SettleTrans>>setFileds>>Field62="+Field62);
        iso8583.setField(62 , Field62);
    }

    /**
     * 组结算交易48域
     * @return
     */
    private int getSettleSUM48(){
        List<TransLogData> list = TransLog.getInstance().getData();
        long debitAmount = 0 ;
        int debitCounts = 0 ;
        long creditAmount = 0 ;
        int creditCounts = 0 ;

        String f48_1;
        String f48_2;
        String f48_3;
        String f48_4;
        String f_waibi = "0000000000000000000000000000000";
        if(list!=null && list.size() > 0){
            Logger.debug("本批次共记录了"+list.size()+"条交易日志");
            for (int i = 0  ; i < list.size() ; i++){
                String trans = list.get(i).getEName() ;
                Logger.debug("list["+i+"] type = "+trans);
                if(trans.equals(Type.SALE)){
                    debitAmount += list.get(i).getAmount();
                    debitCounts ++;
                }
                if(trans.equals(Type.QUICKPASS)){
                    debitAmount += list.get(i).getAmount();
                    debitCounts ++;
                }
                if(trans.equals(Type.VOID)){
                    creditAmount += list.get(i).getAmount();
                    creditCounts ++ ;
                }
            }
            f48_1 = ISOUtil.padleft(String.valueOf(debitAmount) , 12 , '0');
            f48_2 = ISOUtil.padleft(String.valueOf(debitCounts) , 3 , '0');
            f48_3 = ISOUtil.padleft(String.valueOf(creditAmount) , 12 , '0');
            f48_4 = ISOUtil.padleft(String.valueOf(creditCounts) , 3 , '0');
            Field48 = f48_1+f48_2+f48_3+f48_4+"0" + f_waibi;
            Logger.debug("结算48域="+Field48);
            return 0 ;
        }else {
            return Tcode.T_batch_no_trans ;
        }
    }

    private String formatOPN(String opn){
        String two = ISOUtil.padleft(opn , 2 , '0');
        return two + " " ;
    }

    public final static char[] BToA = "0123456789abcdef".toCharArray() ;
    public static String BCD2ASC(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            int h = ((bytes[i] & 0xf0) >>> 4);
            int l = (bytes[i] & 0x0f);
            temp.append(BToA[h]).append( BToA[l]);
        }
        return temp.toString() ;
    }
}
