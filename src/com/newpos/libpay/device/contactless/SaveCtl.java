package com.newpos.libpay.device.contactless;

import com.newpos.libpay.utils.ISOUtil;

public class SaveCtl {
    protected String ExpDateCTL;
    protected String PancCTL;
    protected String PanSeqNoCTL;
    protected String Track1CTL;
    protected String Track2CTL;
    protected String PINCTL;
    protected byte[] ICCDataCTL2;
    protected String holderNameCTL;
    protected String ARQCCTL;
    protected String TVRCTL;
    protected String TSICTL;
    protected String LableCTL="";

    public String getCIDCTL() {
        return CIDCTL;
    }

    public void setCIDCTL(String CIDCTL) {
        this.CIDCTL = CIDCTL;
    }

    protected String CIDCTL;

    public String getPanSeqNoCTL() {
        return PanSeqNoCTL;
    }

    public void setPanSeqNoCTL(String panSeqNoCTL) {
        PanSeqNoCTL = panSeqNoCTL;
    }

    public String getHolderNameCTL() {
        return holderNameCTL;
    }

    public void setHolderNameCTL(String holderNameCTL) {
        this.holderNameCTL = holderNameCTL;
    }

    public String getARQCCTL() {
        return ARQCCTL;
    }

    public void setARQCCTL(String ARQCCTL) {
        this.ARQCCTL = ARQCCTL;
    }

    public String getTVRCTL() {
        return TVRCTL;
    }

    public void setTVRCTL(String TVRCTL) {
        this.TVRCTL = TVRCTL;
    }

    public String getTSICTL() {
        return TSICTL;
    }

    public void setTSICTL(String TSICTL) {
        this.TSICTL = TSICTL;
    }

    public String getLableCTL() {
        return LableCTL;
    }

    public void setLableCTL(String lableCTL) {
        LableCTL = lableCTL;
    }

    public String getAIDCTL() {
        return AIDCTL;
    }

    public void setAIDCTL(String AIDCTL) {
        this.AIDCTL = AIDCTL;
    }

    protected String AIDCTL;

    public String getExpDateCTL() {
        return ExpDateCTL;
    }

    public void setExpDateCTL(String expDateCTL) {
        ExpDateCTL = expDateCTL;
    }

    public String getPancCTL() {
        return PancCTL;
    }

    public void setPancCTL(String pancCTL) {
        PancCTL = pancCTL;
    }

    public String getTrack1CTL() {
        return Track1CTL;
    }

    public void setTrack1CTL(String track1CTL) {
        Track1CTL = track1CTL;
    }

    public String getTrack2CTL() {
        return Track2CTL;
    }

    public void setTrack2CTL(String track2CTL) {
        Track2CTL = track2CTL;
    }

    public String getPINCTL() {
        return PINCTL;
    }

    public void setPINCTL(String PINCTL) {
        this.PINCTL = PINCTL;
    }

    public byte[] getICCDataCTL2() {
        return ICCDataCTL2;
    }

    public void setICCDataCTL2(byte[] ICCDataCTL) {
        this.ICCDataCTL2 = ICCDataCTL;
    }

    public byte[] changeIccdata(long amount){
        String monto =  ISOUtil.padleft(amount + "", 12, '0');
        String data=ISOUtil.byte2hex(ICCDataCTL2);
        String[] parts = data.split("9F0206");
        String part1 = parts[0];
        String part2 = parts[1];
        String sSubCadena = part2.substring(12,part2.length());
        return  ISOUtil.hex2byte(part1+"9F0206"+monto+sSubCadena);
    }
}
