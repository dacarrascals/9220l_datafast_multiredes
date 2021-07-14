package com.newpos.libpay.device.scanner;

/**
 * Created by zhouqiang on 2017/7/7.
 * @author zhouqiang
 * 扫码信息详情
 */

public class QRCInfo {
    private boolean resultFalg ;

    /** 成功返回 */
    private String qrc ;

    /** 错误返回 */
    private int errno ;

    public QRCInfo(){}

    public boolean isResultFalg() {
        return resultFalg;
    }

    public void setResultFalg(boolean resultFalg) {
        this.resultFalg = resultFalg;
    }

    public String getQrc() {
        return qrc;
    }

    public void setQrc(String qrc) {
        this.qrc = qrc;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }
}
