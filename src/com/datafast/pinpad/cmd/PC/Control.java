package com.datafast.pinpad.cmd.PC;

import android.content.Context;

import com.datafast.tools_bacth.ToolsBatch;
import com.datafast.transactions.common.CommonFunctionalities;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogReverse;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_TRAMA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.OK;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PC;
import static com.datafast.transactions.common.CommonFunctionalities.saveDateSettle;
import static com.newpos.libpay.presenter.TransUIImpl.getStatusInfo;
import static com.newpos.libpay.trans.Trans.idLote;

public class Control {

    PC_Request pc_request;
    PC_Response pc_response;
    Context context;
    public static boolean echoTest;

    public Control(Context context){
        this.context = context;
        pc_request = new PC_Request();
        pc_response = new PC_Response();
    }

    public PC_Response getPc_response(){
        return pc_response;
    }

    public int actualizacionControl(byte[] data){
        int ret = 2;
        pc_request.UnPackData(data);

        if (pc_request.getCountValid() > 0){
            processInvalid();
            echoTest = false;
            return 0;
        }

        if (!PAYUtils.isNullWithTrim(pc_request.getBatchNumber()) &&
            !PAYUtils.isNullWithTrim(pc_request.getTracerNumber()) &&
            !PAYUtils.isNullWithTrim(pc_request.getMID()) &&
            !PAYUtils.isNullWithTrim(pc_request.getTID()) &&
            !PAYUtils.isNullWithTrim(pc_request.getCID())){

            TMConfig.getInstance().setBatchNopc(Integer.parseInt(pc_request.getBatchNumber()))
                                  .setTraceNo(Integer.parseInt(pc_request.getTracerNumber()))
                                  .setMerchID(pc_request.getMID())
                                  .setTermID(pc_request.getTID())
                                  .setCID(pc_request.getCID()).save();

            deleteBatch();

            saveDateSettle(context);

            processOk();
            ret = 1;
            echoTest = true;

        }else{
            processFail();
            echoTest = false;
        }
        return ret;
    }

    private void processOk(){
        pc_response.setRspCodeMsg(OK);
        pc_response.setFiller("");
        pc_response.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(56)) + "", 20, ' '));
        pc_response.setTypeMsg(PC);
        pc_response.setHash(pc_request.getHash());
    }

    private void processInvalid(){
        pc_response.setRspCodeMsg(ERROR_PROCESO);
        pc_response.setFiller("");
        pc_response.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(57)) + "", 20, ' '));
        pc_response.setTypeMsg(PC);
        pc_response.setHash(pc_request.getHash());
    }

    private void processFail(){
        pc_response.setRspCodeMsg(ERROR_PROCESO);
        pc_response.setFiller("");
        pc_response.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(57)) + "", 20, ' '));
        pc_response.setTypeMsg(PC);
        pc_response.setHash(pc_request.getHash());
    }

    private void deleteBatch(){
        if (ToolsBatch.statusTrans(idAcquirer)) {

            idAcquirer = idLote;
            if (ToolsBatch.statusTrans(idAcquirer)) {
                int val = Integer.parseInt(TMConfig.getInstance().getBatchNo());
                TMConfig.getInstance().setBatchNopc(val).save();
                TransLog.getInstance(idAcquirer).clearAll(idAcquirer);
                CommonFunctionalities.limpiarPanTarjGasolinera("");
                TransLog.clearReveral(true);
            }

            if (TransLogReverse.getInstance().getSize() > 0){
                TransLogReverse.getInstance().clearAll();
            }
        }else {
            /*ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);*/
        }

    }
}
