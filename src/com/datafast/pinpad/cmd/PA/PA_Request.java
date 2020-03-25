package com.datafast.pinpad.cmd.PA;

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

    public void UnPackData(byte[] aData){

        byte[] tmp = null;
        int offset = 0;

        try
        {
            //red adquiriente
            tmp = new byte[1];
            System.arraycopy(aData, offset, tmp, 0,1);
            offset += 1;
            this.idCodNetAcq = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp).trim());

            //nombre del app a descargar
            tmp = new byte[20];
            System.arraycopy(aData, offset, tmp, 0,20);
            offset += 20;
            this.nameApp = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp).trim());

            //TID
            tmp = new byte[8];
            System.arraycopy(aData, offset, tmp, 0, 8);
            offset += 8;
            this.TID = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //MID
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.MID = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //ipPrimary
            tmp = new byte[21];
            System.arraycopy(aData, offset, tmp, 0, 21);
            offset += 21;
            this.ipPrimary = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //tipo de descarga
            tmp = new byte[1];
            System.arraycopy(aData, offset, tmp, 0,1);
            offset += 1;
            this.typeDownload = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp).trim());

            //hash
            tmp = new byte[32];
            System.arraycopy(aData, offset, tmp, 0, 32);
            offset += 32;
            this.hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

        }
        catch(Exception e)
        {
            e.getMessage();
        }

        return;
    }
}
