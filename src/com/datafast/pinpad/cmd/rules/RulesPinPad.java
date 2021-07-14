package com.datafast.pinpad.cmd.rules;


import com.datafast.pinpad.cmd.Tools.encryption;
import com.newpos.libpay.utils.ISOUtil;

import static com.android.newpos.pay.StartAppDATAFAST.tconf;

public class RulesPinPad {

    private final String ZERO = "0";
    private final String TWO = "2";
    private final String TRHEE = "3";
    private final String FOUR = "4";
    private final String FIVE = "5";
    private final String SIX = "6";

    private String cardNumber;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void processCardNumber(String track2, String pan, String[] tokens) {

        String firstDigCard = pan.substring(0,1);

        switch(firstDigCard) {
            case ZERO:
            case TWO:
            case TRHEE:
            case FOUR:
            case FIVE:
            case SIX:
                if (tconf.getSIMBOLO_EURO().equals("0")){
                    this.cardNumber = ISOUtil.padright(encryption.hashSha1(pan), 40,' ');
                }else {
                    this.cardNumber = ISOUtil.padright(encryption.hashSha256(pan), 64,' ');
                }
                break;

            default:
                if (track2 == null) track2 = "";
                if (tconf.getSIMBOLO_EURO().equals("0")){
                    this.cardNumber = ISOUtil.padright(track2, 40,' ');
                }else {
                    this.cardNumber = ISOUtil.padright(track2, 64,' ');
                }
                break;
        }

        if (tokens != null && !track2.equals("")) {
            if (tconf.getSIMBOLO_EURO().equals("0")){
                this.cardNumber = ISOUtil.padright(encryption.hashSha1(track2), 40,' ');
            }else {
                this.cardNumber = ISOUtil.padright(encryption.hashSha256(track2), 64,' ');
            }
        }



    }

}
