package com.datafast.pinpad.cmd.CB;

import android.content.Context;

import static com.datafast.pinpad.cmd.defines.CmdDatafast.CB;

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

    public void procesoCb(byte[] aDat) {
        cb_request.UnPackData(aDat);

        cb_response.setTypeMsg(CB);
        cb_response.setRspCodeMsg("00");
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
