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
