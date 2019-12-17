package com.datafast.pinpad.cmd.CT;

import com.datafast.pinpad.cmd.process.ProccessData;

public class CT_Response {

    private String typeMsg;
    private String rspCodeMsg;
    private String cardNumber;
    private String binCard;
    private String cardExpDate;
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBinCard() {
        return binCard;
    }

    public void setBinCard(String binCard) {
        this.binCard = binCard;
    }

    public String getCardExpDate() {
        return cardExpDate;
    }

    public void setCardExpDate(String cardExpDate) {
        this.cardExpDate = cardExpDate;
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
        proccessData.setData(cardNumber);
        proccessData.setData(binCard);
        proccessData.setData(cardExpDate);
        proccessData.setData(msgRsp);
        proccessData.setData(filler);
        proccessData.setData(hash);

        return proccessData.getByteData(false);
    }
}
