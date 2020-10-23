package com.datafast.pinpad.cmd.PC;

import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;

public class PC_Request {

    private int countValid;

    private String typeMsg;
    private String batchNumber;
    private String tracerNumber;
    private String filler1;
    private String MID;
    private String TID;
    private String filler2;
    private String CID;
    private String filler3;

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

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getTracerNumber() {
        return tracerNumber;
    }

    public void setTracerNumber(String tracerNumber) {
        this.tracerNumber = tracerNumber;
    }

    public String getFiller1() {
        return filler1;
    }

    public void setFiller1(String filler1) {
        this.filler1 = filler1;
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

    public String getFiller2() {
        return filler2;
    }

    public void setFiller2(String filler2) {
        this.filler2 = filler2;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getFiller3() {
        return filler3;
    }

    public void setFiller3(String filler3) {
        this.filler3 = filler3;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void UnPackData(byte[] aData) {

        byte[] tmp = null;
        int offset = 0;
        try {

            this.countValid = 0;

            //batchNumber
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            this.batchNumber = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (batchNumber.length() != 6) {
                countValid ++;
            }

            //tracerNumber
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            this.tracerNumber = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (tracerNumber.length() != 6) {
                countValid ++;
            }

            //filler1
            tmp = new byte[12];
            System.arraycopy(aData, offset, tmp, 0, 12);
            offset += 12;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 12){
                countValid ++;
            }
            this.filler1 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

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
                if (TID.length() != 8) {
                    countValid ++;
                }
            }else {
                TID = TMConfig.getInstance().getTermID();
            }

            //filler2
            tmp = new byte[23];
            System.arraycopy(aData, offset, tmp, 0, 23);
            offset += 23;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 23){
                countValid ++;
            }
            this.filler2 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //CID
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.CID = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (CID.length() != 15) {
                countValid ++;
            }

            //filler3
            tmp = new byte[1];
            System.arraycopy(aData, offset, tmp, 0, 1);
            offset += 1;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 1){
                countValid ++;
            }
            this.filler3 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //hash
            tmp = new byte[32];
            System.arraycopy(aData, offset, tmp, 0, 32);
            offset += 32;
            this.hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (hash.length() != 32) {
                countValid += 1;
            }

        } catch (Exception e) {
            e.getMessage();
        }

        return;

    }
}
