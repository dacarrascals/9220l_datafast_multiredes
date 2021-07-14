package com.datafast.pinpad.cmd.LT;

import com.datafast.pinpad.cmd.process.ProccessData;
import com.newpos.libpay.utils.ISOUtil;

import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.INICIO_DIA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.OK;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.TO;
import static com.newpos.libpay.trans.Tcode.T_success;
import static com.newpos.libpay.trans.Tcode.T_user_cancel_input;
import static com.newpos.libpay.trans.Tcode.T_wait_timeout;

public class LT_Response {

    private String typeMsg;
    private String rspCodeMsg;
    private String idCodNetCte;
    private String idCodNetDef;
    private String cardNumber;
    private String cardExpDate;
    private String cardNumEncryp;
    private String msgRsp;
    private String filler;

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

    public String getIdCodNetCte() {
        return idCodNetCte;
    }

    public void setIdCodNetCte(String idCodNetCte) {
        this.idCodNetCte = idCodNetCte;
    }

    public String getIdCodNetDef() {
        return idCodNetDef;
    }

    public void setIdCodNetDef(String idCodNetDef) {
        this.idCodNetDef = idCodNetDef;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpDate() {
        return cardExpDate;
    }

    public void setCardExpDate(String cardExpDate) {
        this.cardExpDate = cardExpDate;
    }

    public String getCardNumEncryp() {
        return cardNumEncryp;
    }

    public void setCardNumEncryp(String cardNumEncryp) {
        this.cardNumEncryp = cardNumEncryp;
    }

    public String getMsgRsp() {
        return msgRsp;
    }

    public void setMsgRsp(String msgRsp) {
        this.msgRsp = msgRsp;
    }

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
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
        proccessData.setData(idCodNetCte);
        proccessData.setData(idCodNetDef);
        proccessData.setData(cardNumber);
        proccessData.setData(cardExpDate);
        proccessData.setData(cardNumEncryp);
        proccessData.setData(msgRsp);
        proccessData.setData(filler);
        proccessData.setData(hash);

        return proccessData.getByteData(false);
    }
}
