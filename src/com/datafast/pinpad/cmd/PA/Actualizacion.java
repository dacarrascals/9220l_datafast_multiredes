package com.datafast.pinpad.cmd.PA;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.datafast.server.callback.waitResponse;
import com.datafast.tools_bacth.ToolsBatch;
import com.newpos.libpay.utils.ISOUtil;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREAUTO;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.INICIO_DIA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.OK;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PA;
import static com.newpos.libpay.presenter.TransUIImpl.getStatusInfo;

public class Actualizacion{

    PA_Request pa_request;
    PA_Response pa_response;
    waitResponse listenerResponse;
    Context ctx;
    public boolean intentOK = false;
    public String msgfail = "ERROR EN PROCESO ACTUALIZACION";

    public Actualizacion(Context context){
        this.ctx = context;
        pa_request = new PA_Request();
        pa_response = new PA_Response();
    }

    public PA_Response getPa_response(){
        return pa_response;
    }
    public void procesoActualizacion(byte[] data){
        //this.listenerResponse = listener;
        pa_request.UnPackData(data);
        if (!ToolsBatch.statusTrans(idAcquirer) && !ToolsBatch.statusTrans(idAcquirer + FILE_NAME_PREAUTO)) {
            try{
                Intent intentPack = ctx.getPackageManager().getLaunchIntentForPackage("com.downloadmanager");
                ctx.startActivity(intentPack);
                processOk();
                intentOK = true;
            }catch (Exception e){
                intentOK = false;
                processFail(ERROR_PROCESO, "ERROR EN TRAMA");
            }
        }else {
            intentOK = false;
            msgfail = "REALICE INICIO DE DIA";
            processFail(INICIO_DIA, "REALICE INICIO DE DIA");
        }
    }

    private void processOk(){
        pa_response.setRspCodeMsg(OK);
        pa_response.setFiller(ISOUtil.padright("",2,'0'));
        pa_response.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(56)) + "", 20, ' '));
        pa_response.setTypeMsg(PA);
        pa_response.setHash(pa_request.getHash());
    }
    private void processFail(String msg, String codRet){
        pa_response.setRspCodeMsg(msg);
        pa_response.setFiller(ISOUtil.padright("",2,'0'));
        pa_response.setMsgRsp(ISOUtil.padright(codRet + "", 20, ' '));
        pa_response.setTypeMsg(PA);
        pa_response.setHash(pa_request.getHash());
    }

}