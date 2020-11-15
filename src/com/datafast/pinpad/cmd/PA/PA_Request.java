package com.datafast.pinpad.cmd.PA;

import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;

public class PA_Request {

    private String hash;
    private String idCodNetAcq;
    private String nameApp;
    private String MID;
    private String TID;
    private String ipPrimary;
    private String typeDownload;
    private String requestPA;
    private int countValid;

    public String getIdCodNetAcq() {
        return idCodNetAcq;
    }

    public void setIdCodNetAcq(String idCodNetAcq) {
        this.idCodNetAcq = idCodNetAcq;
    }

    public String getNameApp() {
        return nameApp;
    }

    public void setNameApp(String nameApp) {
        this.nameApp = nameApp;
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

    public String getIpPrimary() {
        return ipPrimary;
    }

    public void setIpPrimary(String ipPrimary) {
        this.ipPrimary = ipPrimary;
    }

    public String getTypeDownload() {
        return typeDownload;
    }

    public void setTypeDownload(String typeDownload) {
        this.typeDownload = typeDownload;
    }

    public String getRequestPA() {
        return requestPA;
    }

    public void setRequestPA(String requestPA) {
        this.requestPA = requestPA;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getCountValid() {
        return countValid;
    }

    public void setCountValid(int countValid) {
        this.countValid = countValid;
    }

    public void UnPackData(byte[] aData){

        byte[] tmp = null;
        int offset = 0;

        try
        {

            //codRedACQ
            tmp = new byte[1];
            System.arraycopy(aData, offset, tmp, 0, 1);
            offset += 1;
            this.idCodNetAcq = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (idCodNetAcq.length() != 1) {
                countValid ++;
            }

            //nameApp
            tmp = new byte[20];
            System.arraycopy(aData, offset, tmp, 0, 20);
            offset += 20;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 20) {
                countValid ++;
            }
            this.nameApp = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

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
            //ipPolaris
            tmp = new byte[21];
            System.arraycopy(aData, offset, tmp, 0, 21);
            offset += 21;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).contains(":")) {
                this.ipPrimary = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            } else {
                countValid ++;
            }

            //typeDownload
            tmp = new byte[1];
            System.arraycopy(aData, offset, tmp, 0, 1);
            offset += 1;
            this.typeDownload = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (typeDownload.length() != 1) {
                countValid ++;
            }

            //hash
            tmp = new byte[32];
            System.arraycopy(aData, offset, tmp, 0, 32);
            offset += 32;
            this.hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            setCorrectHash(aData);

        }
        catch(Exception e)
        {
            e.getMessage();
            setCorrectHash(aData);
        }

        return;
    }

    private void setCorrectHash(byte[] aData){
        String correctHash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aData)).trim();
        correctHash = correctHash.substring(correctHash.length() - 32);
        if (hash == null || !correctHash.equals(hash)){
            if (!typeDownload.isEmpty() && typeDownload.equals("F")){
                correctHash = correctHash.substring(1, correctHash.length());
            }
            hash = correctHash;
            countValid ++;
        }
    }
}
