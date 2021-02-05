package com.datafast.pinpad.cmd.PP;

import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;

public class PP_Request {

    private int countValid;

    private String typeMsg;
    private String typeTrans;
    private String idCodNetAcq;
    private String idCodDef;
    private String limitDef;
    private String monthsGrace;
    private String filler1;
    private String amountTotal;
    private String amountIVA;
    private String amountNotIVA;
    private String IVA;
    private String service;
    private String tips;
    private String fixedAmount;
    private String sequential;
    private String dateTrans;
    private String hourTrans;
    private String authNumber;
    private String MID;
    private String TID;
    private String CID;
    private String OTT;
    private String providerOTT;
    private String invoiceNumber;
    private String pushSalesman;
    private String filler2;
    private String hash;

    public int getCountValid() {
        return countValid;
    }

    public void setCountValid(int countValid) {
        this.countValid = countValid;
    }

    public String getTypeMsg() {
        return typeMsg;
    }

    public void setTypeMsg(String typeMsg) {
        this.typeMsg = typeMsg;
    }

    public String getTypeTrans() {
        return typeTrans;
    }

    public void setTypeTrans(String typeTrans) {
        this.typeTrans = typeTrans;
    }

    public String getIdCodNetAcq() {
        return idCodNetAcq;
    }

    public void setIdCodNetAcq(String idCodNetAcq) {
        this.idCodNetAcq = idCodNetAcq;
    }

    public String getIdCodDef() {
        return idCodDef;
    }

    public void setIdCodDef(String idCodDef) {
        this.idCodDef = idCodDef;
    }

    public String getLimitDef() {
        return limitDef;
    }

    public void setLimitDef(String limitDef) {
        this.limitDef = limitDef;
    }

    public String getMonthsGrace() {
        return monthsGrace;
    }

    public void setMonthsGrace(String monthsGrace) {
        this.monthsGrace = monthsGrace;
    }

    public String getFiller1() {
        return filler1;
    }

    public void setFiller1(String filler1) {
        this.filler1 = filler1;
    }

    public String getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(String amountTotal) {
        this.amountTotal = amountTotal;
    }

    public String getAmountIVA() {
        return amountIVA;
    }

    public void setAmountIVA(String amountIVA) {
        this.amountIVA = amountIVA;
    }

    public String getAmountNotIVA() {
        return amountNotIVA;
    }

    public void setAmountNotIVA(String amountNotIVA) {
        this.amountNotIVA = amountNotIVA;
    }

    public String getIVA() {
        return IVA;
    }

    public void setIVA(String IVA) {
        this.IVA = IVA;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(String fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public String getSequential() {
        return sequential;
    }

    public void setSequential(String sequential) {
        this.sequential = sequential;
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

    public String getAuthNumber() {
        return authNumber;
    }

    public void setAuthNumber(String authNumber) {
        this.authNumber = authNumber;
    }

    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getOTT() {
        return OTT;
    }

    public void setOTT(String OTT) {
        this.OTT = OTT;
    }

    public String getProviderOTT() {
        return providerOTT;
    }

    public void setProviderOTT(String providerOTT) {
        this.providerOTT = providerOTT;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPushSalesman() {
        return pushSalesman;
    }

    public void setPushSalesman(String pushSalesman) {
        this.pushSalesman = pushSalesman;
    }

    public String getFiller2() {
        return filler2;
    }

    public void setFiller2(String filler2) {
        this.filler2 = filler2;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void UnPackHash(byte[] aData){
        //idCodNetAcq
        byte[] tmp = new byte[1];
        System.arraycopy(aData, 2, tmp, 0, 1);
        this.idCodNetAcq = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

        setCorrectHash(aData);
    }

    public void UnPackData(byte[] aData){

        byte[] tmp = null;
        int offset = 0;

        try
        {
            this.countValid = 0;

            //typeTrans
            tmp = new byte[2];
            System.arraycopy(aData, offset, tmp, 0, 2);
            offset += 2;
            this.typeTrans = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //idCodNetAcq
            tmp = new byte[1];
            System.arraycopy(aData, offset, tmp, 0, 1);
            offset += 1;
            this.idCodNetAcq = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //idCodDef
            tmp = new byte[2];
            System.arraycopy(aData, offset, tmp, 0, 2);
            offset += 2;
            this.idCodDef = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //limitDef
            tmp = new byte[2];
            System.arraycopy(aData, offset, tmp, 0, 2);
            offset += 2;
            this.limitDef = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //monthsGrace
            tmp = new byte[2];
            System.arraycopy(aData, offset, tmp, 0, 2);
            offset += 2;
            this.monthsGrace = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //filler1
            tmp = new byte[1];
            System.arraycopy(aData, offset, tmp, 0, 1);
            offset += 1;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 1){
                countValid ++;
            }
            this.filler1 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //amountTotal
            tmp = new byte[12];
            System.arraycopy(aData, offset, tmp, 0, 12);
            offset += 12;
            this.amountTotal = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //amountIVA
            tmp = new byte[12];
            System.arraycopy(aData, offset, tmp, 0, 12);
            offset += 12;
            this.amountIVA = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //amountNotIVA
            tmp = new byte[12];
            System.arraycopy(aData, offset, tmp, 0, 12);
            offset += 12;
            this.amountNotIVA = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //IVA
            tmp = new byte[12];
            System.arraycopy(aData, offset, tmp, 0, 12);
            offset += 12;
            this.IVA = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //service
            tmp = new byte[12];
            System.arraycopy(aData, offset, tmp, 0, 12);
            offset += 12;
            this.service = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //tips
            tmp = new byte[12];
            System.arraycopy(aData, offset, tmp, 0, 12);
            offset += 12;
            this.tips = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //fixedAmount
            tmp = new byte[12];
            System.arraycopy(aData, offset, tmp, 0, 12);
            offset += 12;
            this.fixedAmount = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //sequential
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            this.sequential = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (sequential.length() != 6 && typeTrans.equals("03")){
                countValid ++;
            }

            //hourTrans
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            this.hourTrans = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //dateTrans
            tmp = new byte[8];
            System.arraycopy(aData, offset, tmp, 0, 8);
            offset += 8;
            this.dateTrans = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //authNumber
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            this.authNumber = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (authNumber.length() != 6 && typeTrans.equals("03")){
                countValid ++;
            }

            //MID
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.MID = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (!MID.isEmpty()){
                if (MID.length() != 10){
                    countValid ++;
                }
            }else {
                MID = TMConfig.getInstance().getMerchID();
            }

            //TID
            tmp = new byte[8];
            System.arraycopy(aData, offset, tmp, 0, 8);
            offset += 8;
            this.TID = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (!TID.isEmpty()){
                if (TID.length() != 8){
                    countValid ++;
                }
            }else {
                TID = TMConfig.getInstance().getTermID();
            }

            //CID
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            if(ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 15){
                countValid ++;
            }
            this.CID = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //OTT
            tmp = new byte[8];
            System.arraycopy(aData, offset, tmp, 0, 8);
            offset += 8;
            this.OTT = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (!OTT.isEmpty()){
                if (OTT.length() != 6 && typeTrans.equals("03")){
                    countValid ++;
                }
            }

            //providerOTT
            tmp = new byte[2];
            System.arraycopy(aData, offset, tmp, 0, 2);
            offset += 2;
            this.providerOTT = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (typeTrans.equals("06")){
                if (!providerOTT.isEmpty()){
                    if (providerOTT.length() != 2){
                        countValid ++;
                    }
                }else {
                    countValid ++;
                }
            }

            //invoiceNumber
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            if(ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 15){
                countValid ++;
            }
            this.invoiceNumber = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //pushSalesman
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.pushSalesman = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //filler2
            tmp = new byte[20];
            System.arraycopy(aData, offset, tmp, 0, 20);
            offset += 20;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 20){
                countValid ++;
            }
            this.filler2 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //hash
            tmp = new byte[32];
            System.arraycopy(aData, offset, tmp, 0, 32);
            offset += 32;
            this.hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            setCorrectHash(aData);
        }
        catch(Exception e)
        {
            countValid++;
            e.getMessage();
            setCorrectHash(aData);
        }

        return;
    }

    private void setCorrectHash(byte[] aData){
        String correctHash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aData)).trim();
        correctHash = correctHash.substring(correctHash.length() - 32);
        if (hash == null || !correctHash.equals(hash)){
            hash = correctHash;
            countValid ++;
        }
    }
}
