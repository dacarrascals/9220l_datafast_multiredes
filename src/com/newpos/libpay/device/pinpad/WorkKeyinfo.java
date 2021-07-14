package com.newpos.libpay.device.pinpad;

/**
 * Created by zhouqiang on 2017/3/15.
 * @author zhouqiang
 * 工作密钥信息详情
 */

public class WorkKeyinfo {
    private int masterKeyIndex ;
    private int workKeyIndex ;
    private int keySystem ;
    private int keyType ;
    private byte[] hexKeyData ;
    private int mode ;

    public int getMasterKeyIndex() {
        return masterKeyIndex;
    }

    public void setMasterKeyIndex(int masterKeyIndex) {
        this.masterKeyIndex = masterKeyIndex;
    }

    public int getWorkKeyIndex() {
        return workKeyIndex;
    }

    public void setWorkKeyIndex(int workKeyIndex) {
        this.workKeyIndex = workKeyIndex;
    }

    public int getKeySystem() {
        return keySystem;
    }

    public void setKeySystem(int keySystem) {
        this.keySystem = keySystem;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public byte[] getPrivacyKeyData() {
        return hexKeyData;
    }

    public void setPrivacyKeyData(byte[] privacyKeyData) {
        this.hexKeyData = privacyKeyData;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
