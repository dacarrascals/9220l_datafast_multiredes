package com.datafast.pinpad.cmd.PC;

import com.datafast.pinpad.cmd.process.ProccessData;

public class PC_Response {

    private String typeMsg;
    private String rspCodeMsg;
    private String filler;
    private String msgRsp;

    private String hash;

    public String getTypeMsg() {
        return typeMsg;
    }

    public void setTypeMsg(String typeMsg) {
        this.typeMsg = typeMsg;
    }

    public String getRspCodeMsg() {
        return rspCodeMsg;
    }

    public void setRspCodeMsg(String rspCodeMsg) {
        this.rspCodeMsg = rspCodeMsg;
    }

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    public String getMsgRsp() {
        return msgRsp;
    }

    public void setMsgRsp(String msgRsp) {
        this.msgRsp = msgRsp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public byte[] packData(){

        ProccessData proccessData = new ProccessData();

        proccessData.setData(typeMsg);
        proccessData.setData(rspCodeMsg);
        proccessData.setData(filler);
        proccessData.setData(msgRsp);
        proccessData.setData(hash);

        return proccessData.getByteData(false);
    }
}
