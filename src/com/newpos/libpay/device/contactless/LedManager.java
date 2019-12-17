package com.newpos.libpay.device.contactless;

import com.pos.device.led.Led;

public class LedManager {

    public static final int ON=1;
    public static final int OFF=0;
    private static LedManager instance;

    public static LedManager getInstance(){
        return instance==null?instance = new LedManager():instance;
    }

    private void operateLed(String led,int way ){
        switch (way){
            case ON:
                Led.setLight(led, Led.LED_ON);
                break;
            case OFF:
                Led.setLight(led, Led.LED_OFF);
                break;
            default:
                break;
        }
    }

    public void Blue(int isOn){
        operateLed(Led.LED_NFC_1,isOn);
    }

    public void Green(int isOn){
        operateLed(Led.LED_NFC_2,isOn);
    }

    public void Yellow(int isOn){
        operateLed(Led.LED_NFC_3,isOn);
    }
    public void Red(int isOn){
        operateLed(Led.LED_NFC_4,isOn);
    }

    public void turnOffAll(){
        Led.setLight(Led.LED_NFC_1, Led.LED_OFF);
        Led.setLight(Led.LED_NFC_2, Led.LED_OFF);
        Led.setLight(Led.LED_NFC_3, Led.LED_OFF);
        Led.setLight(Led.LED_NFC_4, Led.LED_OFF);
        Led.setLight(Led.LED_KEY_BOARD, Led.LED_OFF);
        Led.setLight(Led.LED_MAC_CARD, Led.LED_OFF);
        Led.setLight(Led.LED_IC_CARD, Led.LED_OFF);
        instance = null;
    }
}
