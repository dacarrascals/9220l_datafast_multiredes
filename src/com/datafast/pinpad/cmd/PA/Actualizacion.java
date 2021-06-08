package com.datafast.pinpad.cmd.PA;

import android.content.Context;
import android.content.Intent;

import com.datafast.server.callback.waitResponse;
import com.datafast.server.server_tcp.Server;
import com.datafast.tools_bacth.ToolsBatch;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREAUTO;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_TRAMA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.INICIO_DIA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.OK;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PA;
import static com.newpos.libpay.presenter.TransUIImpl.getStatusInfo;
import static com.newpos.libpay.trans.Trans.idLote;

public class Actualizacion{

    PA_Request pa_request;
    PA_Response pa_response;
    Context ctx;
    public boolean intentOK = false;
    public int tramaValida;
    public String msgfail = "ERROR EN PROCESO ACTUALIZACION";
    public static boolean echoTest;
    public static boolean goEchoTest;

    public Actualizacion(Context context){
        this.ctx = context;
        pa_request = new PA_Request();
        pa_response = new PA_Response();
    }

    public PA_Response getPa_response(){
        return pa_response;
    }
    public boolean procesoActualizacion(byte[] data){
        //this.listenerResponse = listener;
        idAcquirer = idLote;

        if (!Server.correctLength){
            pa_request.UnPackHash(data);
            processFail(ERROR_PROCESO, "ERROR EN TRAMA");
            tramaValida = -1;
            echoTest = false;
            pa_request.setCountValid(0);
            return false;
        }

        pa_request.UnPackData(data);

        if (pa_request.getCountValid() > 0){
            processFail(ERROR_PROCESO, "ERROR EN TRAMA");
            tramaValida = -1;
            echoTest = false;
            pa_request.setCountValid(0);
            return false;
        }
        tramaValida=0;
        if (!PAYUtils.isNullWithTrim(pa_request.getMID()) && !PAYUtils.isNullWithTrim(pa_request.getTID())){
            TMConfig.getInstance().setMerchID(pa_request.getMID()).setTermID(pa_request.getTID()).save();
        }
        if (!ToolsBatch.statusTrans(idAcquirer) && !ToolsBatch.statusTrans(idAcquirer + FILE_NAME_PREAUTO)) {
            try{
                Intent intentPack = ctx.getPackageManager().getLaunchIntentForPackage("com.downloadmanager");
                intentPack.putExtra("ipConnection", pa_request.getIpPrimary());
                ctx.startActivity(intentPack);
                processOk();
                intentOK = true;
                echoTest = true;
            } catch(Exception e) {
                intentOK = false;
                echoTest = false;
                msgfail = getStatusInfo(String.valueOf(61));
                processFail(ERROR_TRAMA, msgfail);
            }
        } else {
            intentOK = false;
            echoTest = false;
            msgfail = "REALICE PROCESO DE CONTROL";
            processFail(INICIO_DIA, "PROCESO CONTROL");
        }
        return true;
    }

    private void processOk(){
        pa_response.setRspCodeMsg(OK);
        pa_response.setFiller("");
        pa_response.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(62)) + "", 20, ' '));
        pa_response.setTypeMsg(PA);
        pa_response.setHash(pa_request.getHash());
    }
    private void processFail(String msg, String codRet){
        pa_response.setRspCodeMsg(msg);
        pa_response.setFiller("");
        pa_response.setMsgRsp(ISOUtil.padright(codRet + "", 20, ' '));
        pa_response.setTypeMsg(PA);
        pa_response.setHash(pa_request.getHash());
    }

}
