package com.datafast.pinpad.cmd.PP;

import com.datafast.pinpad.cmd.process.ProccessData;

public class PP_Response {

    private String typeMsg;
    private String rspCodeMsg;
    private String idCodNetAcq;
    private String rspCode;
    private String msgRsp;
    private String secuencialTrans;
    private String numberBatch;
    private String hourTrans;
    private String dateTrans;
    private String numberAuth;
    private String TID;
    private String MID;
    private String interestFinancingValue;
    private String msgPrintAwards;
    private String codBankAcq;
    private String nameBankAcq;
    private String nameGroupCard;
    private String modeReadCard;
    private String nameCardHolder;
    private String fixedAmount;
    private String appEMV;
    private String AIDEMV;
    private String criptEMV;
    private String validatePIN;
    private String ARQC;
    private String TVR;
    private String TSI;
    private String numberCardMask;
    private String expDateCard;
    private String numberCardEncrypt;
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

    public String getIdCodNetAcq() {
        return idCodNetAcq;
    }

    public void setIdCodNetAcq(String idCodNetAcq) {
        this.idCodNetAcq = idCodNetAcq;
    }

    public String getRspCode() {
        return rspCode;
    }

    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    public String getMsgRsp() {
        return msgRsp;
    }

    public void setMsgRsp(String msgRsp) {
        this.msgRsp = msgRsp;
    }

    public String getSecuencialTrans() {
        return secuencialTrans;
    }

    public void setSecuencialTrans(String secuencialTrans) {
        this.secuencialTrans = secuencialTrans;
    }

    public String getNumberBatch() {
        return numberBatch;
    }

    public void setNumberBatch(String numberBatch) {
        this.numberBatch = numberBatch;
    }

    public String getHourTrans() {
        return hourTrans;
    }

    public void setHourTrans(String hourTrans) {
        this.hourTrans = hourTrans;
    }

    public String getDateTrans() {
        return dateTrans;
    }

    public void setDateTrans(String dateTrans) {
        this.dateTrans = dateTrans;
    }

    public String getNumberAuth() {
        return numberAuth;
    }

    public void setNumberAuth(String numberAuth) {
        this.numberAuth = numberAuth;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public String getInterestFinancingValue() {
        return interestFinancingValue;
    }

    public void setInterestFinancingValue(String interestFinancingValue) {
        this.interestFinancingValue = interestFinancingValue;
    }

    public String getMsgPrintAwards() {
        return msgPrintAwards;
    }

    public void setMsgPrintAwards(String msgPrintAwards) {
        this.msgPrintAwards = msgPrintAwards;
    }

    public String getCodBankAcq() {
        return codBankAcq;
    }

    public void setCodBankAcq(String codBankAcq) {
        this.codBankAcq = codBankAcq;
    }

    public String getNameBankAcq() {
        return nameBankAcq;
    }

    public void setNameBankAcq(String nameBankAcq) {
        this.nameBankAcq = nameBankAcq;
    }

    public String getNameGroupCard() {
        return nameGroupCard;
    }

    public void setNameGroupCard(String nameGroupCard) {
        this.nameGroupCard = nameGroupCard;
    }

    public String getModeReadCard() {
        return modeReadCard;
    }

    public void setModeReadCard(String modeReadCard) {
        this.modeReadCard = modeReadCard;
    }

    public String getNameCardHolder() {
        return nameCardHolder;
    }

    public void setNameCardHolder(String nameCardHolder) {
        this.nameCardHolder = nameCardHolder;
    }

    public String getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(String fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public String getAppEMV() {
        return appEMV;
    }

    public void setAppEMV(String appEMV) {
        this.appEMV = appEMV;
    }

    public String getAIDEMV() {
        return AIDEMV;
    }

    public void setAIDEMV(String AIDEMV) {
        this.AIDEMV = AIDEMV;
    }

    public String getCriptEMV() {
        return criptEMV;
    }

    public void setCriptEMV(String criptEMV) {
        this.criptEMV = criptEMV;
    }

    public String getValidatePIN() {
        return validatePIN;
    }

    public void setValidatePIN(String validatePIN) {
        this.validatePIN = validatePIN;
    }

    public String getARQC() {
        return ARQC;
    }

    public void setARQC(String ARQC) {
        this.ARQC = ARQC;
    }

    public String getTVR() {
        return TVR;
    }

    public void setTVR(String TVR) {
        this.TVR = TVR;
    }

    public String getTSI() {
        return TSI;
    }

    public void setTSI(String TSI) {
        this.TSI = TSI;
    }

    public String getNumberCardMask() {
        return numberCardMask;
    }

    public void setNumberCardMask(String numberCardMask) {
        this.numberCardMask = numberCardMask;
    }

    public String getExpDateCard() {
        return expDateCard;
    }

    public void setExpDateCard(String expDateCard) {
        this.expDateCard = expDateCard;
    }

    public String getNumberCardEncrypt() {
        return numberCardEncrypt;
    }

    public void setNumberCardEncrypt(String numberCardEncrypt) {
        this.numberCardEncrypt = numberCardEncrypt;
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
        proccessData.setData(idCodNetAcq);
        proccessData.setData(rspCode);
        proccessData.setData(msgRsp);
        proccessData.setData(secuencialTrans);
        proccessData.setData(numberBatch);
        proccessData.setData(hourTrans);
        proccessData.setData(dateTrans);
        proccessData.setData(numberAuth);
        proccessData.setData(TID);
        proccessData.setData(MID);
        proccessData.setData(interestFinancingValue);
        proccessData.setData(msgPrintAwards);
        proccessData.setData(codBankAcq);
        proccessData.setData(nameBankAcq);
        proccessData.setData(nameGroupCard);
        proccessData.setData(modeReadCard);
        proccessData.setData(nameCardHolder);
        proccessData.setData(fixedAmount);
        proccessData.setData(appEMV);
        proccessData.setData(AIDEMV);
        proccessData.setData(criptEMV);
        proccessData.setData(validatePIN);
        proccessData.setData(ARQC);
        proccessData.setData(TVR);
        proccessData.setData(TSI);
        proccessData.setData(numberCardMask);
        proccessData.setData(expDateCard);
        proccessData.setData(numberCardEncrypt);
        proccessData.setData(filler);

        proccessData.setData(hash);

        return proccessData.getByteData(false);
    }
}
