package com.newpos.libpay.device.pinpad;

import com.pos.device.ped.KeyType;

/**
 * Created by zhouqiang on 2017/7/19.
 * @author zhouqiang
 * 密码键盘密钥类型
 */

public class PinpadKeytype {
    public static final int KEY_TYPE_SIEK = 1 ;
    public static final int KEY_TYPE_MASTK = 2 ;
    public static final int KEY_TYPE_PINK = 3 ;
    public static final int KEY_TYPE_MACK = 4 ;
    public static final int KEY_TYPE_FIXPINK = 5 ;
    public static final int KEY_TYPE_FIXMACK = 6 ;
    public static final int KEY_TYPE_DUKPTK = 7 ;
    public static final int KEY_TYPE_EMRKEY = 8 ;
    public static final int KEY_TYPE_KMMK = 9 ;
    public static final int KEY_TYPE_EAK = 10 ;
    public static final int KEY_TYPE_FIXEAK = 11 ;
    public static final int KEY_TYPE_TMSK = 12 ;
    public static final int KEY_TYPE_SM2PRI = 13 ;
    public static final int KEY_TYPE_RSA_PRIK = 14 ;
    public static final int KEY_TYPE_ANSK = 15 ;
    public static final int KEY_TYPE_RSA_CA_CRT = 16 ;
    public static final int KEY_TYPE_RSA_CLIENT_CRT = 17 ;

    private int value ;

    public  PinpadKeytype(int type) {
        this.value = type ;
    }

    public static KeyType getKT(int pk){
        KeyType kt = KeyType.KEY_TYPE_MASTK ;
        KeyType[] KTS = {KeyType.KEY_TYPE_SIEK, KeyType.KEY_TYPE_MASTK, KeyType.KEY_TYPE_PINK, KeyType.KEY_TYPE_MACK,
                KeyType.KEY_TYPE_FIXPINK, KeyType.KEY_TYPE_FIXMACK, KeyType.KEY_TYPE_DUKPTK, KeyType.KEY_TYPE_EMRKEY,
                KeyType.KEY_TYPE_KMMK, KeyType.KEY_TYPE_EAK, KeyType.KEY_TYPE_FIXEAK, KeyType.KEY_TYPE_TMSK,
                KeyType.KEY_TYPE_SM2PRI, KeyType.KEY_TYPE_RSA_PRIK, KeyType.KEY_TYPE_ANSK} ;
        int[] PKTS = {PinpadKeytype.KEY_TYPE_SIEK, PinpadKeytype.KEY_TYPE_MASTK, PinpadKeytype.KEY_TYPE_PINK, PinpadKeytype.KEY_TYPE_MACK,
                PinpadKeytype.KEY_TYPE_FIXPINK, PinpadKeytype.KEY_TYPE_FIXMACK, PinpadKeytype.KEY_TYPE_DUKPTK, PinpadKeytype.KEY_TYPE_EMRKEY,
                PinpadKeytype.KEY_TYPE_KMMK, PinpadKeytype.KEY_TYPE_EAK, PinpadKeytype.KEY_TYPE_FIXEAK, PinpadKeytype.KEY_TYPE_TMSK,
                PinpadKeytype.KEY_TYPE_SM2PRI, PinpadKeytype.KEY_TYPE_RSA_PRIK, PinpadKeytype.KEY_TYPE_ANSK, PinpadKeytype.KEY_TYPE_RSA_CA_CRT, PinpadKeytype.KEY_TYPE_RSA_CLIENT_CRT} ;
        for (int i = 0 ; i < KTS.length ; i ++){
            if(pk == PKTS[i]){
                kt = KTS[i] ;
                break;
            }
        }
        return kt ;
    }

    public int getType() {
        return this.value ;
    }

    public void setType(int type){
        this.value = type ;
    }
}
