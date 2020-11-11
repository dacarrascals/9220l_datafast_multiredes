package com.datafast.pinpad.cmd.CB;

import android.content.Context;

import com.newpos.libpay.utils.ISOUtil;

import static com.datafast.pinpad.cmd.defines.CmdDatafast.CB;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_TRAMA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.OK;
import static com.newpos.libpay.presenter.TransUIImpl.getStatusInfo;

public class ConfiguracionBasica {

    Context context;
    CB_Request cb_request;
    CB_Response cb_response;

    public ConfiguracionBasica(Context context) {
        this.context = context;
        cb_request = new CB_Request(context);
        cb_response = new CB_Response();
    }

    public CB_Response getCb_response() {
        return cb_response;
    }

    public boolean procesoCb(byte[] aDat) {
        cb_request.UnPackData(aDat);
        
        if (cb_request.getCountValid() > 0){
            processInvalid();
            return false;
        }
        
        processOk();
        return true;
    }

    private void processInvalid() {
        cb_response.setTypeMsg(CB);
        cb_response.setRspCodeMsg(ERROR_PROCESO);
        cb_response.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(57)) + "", 20, ' ')
                + ISOUtil.spacepadRight("", 62));
        cb_response.setFiller(ISOUtil.spacepadRight("", 15));
        cb_response.setHash(cb_request.getHash());
    }

    private void processOk() {
        cb_response.setTypeMsg(CB);
        cb_response.setRspCodeMsg(OK);
        cb_response.setMsgRsp(cb_request.getLPORT()
                + cb_request.getBoxCOMM()
                + cb_request.getBoxBAUD()
                + cb_request.getSw()
                + cb_request.getRegiPPA()
                + cb_request.getDateEVOCC()
                + cb_request.getDateEVOCIP()
                + cb_request.getIp()
                + cb_request.getMask()
                + cb_request.getGateway()
                + cb_request.getDataDF()
        );
        cb_response.setFiller(cb_request.getFiller());
        cb_response.setHash(cb_request.getHash());
    }
}
