package com.newpos.libpay.device.pinpad;

/**
 * Created by zhouqiang on 2017/11/17.
 */

public class OfflineRSA {
    /**
     * 模长
     */
    private int modLen ;

    /**
     * 模
     */
    private byte[] mod ;

    /**
     * 指数长
     */
    private int expLen ;

    /**
     * 指数
     */
    private byte[] exp ;

    /**
     * 随机数
     */
    private byte[] iccRandom ;

    public int getModLen() {
        return modLen;
    }

    public void setModLen(int modLen) {
        this.modLen = modLen;
    }

    public byte[] getMod() {
        return mod;
    }

    public void setMod(byte[] mod) {
        this.mod = mod;
    }

    public int getExpLen() {
        return expLen;
    }

    public void setExpLen(int expLen) {
        this.expLen = expLen;
    }

    public byte[] getExp() {
        return exp;
    }

    public void setExp(byte[] exp) {
        this.exp = exp;
    }

    public byte[] getIccRandom() {
        return iccRandom;
    }

    public void setIccRandom(byte[] iccRandom) {
        this.iccRandom = iccRandom;
    }
}
