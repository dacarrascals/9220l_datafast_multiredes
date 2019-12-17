package com.datafast.pinpad.cmd.rules;


import com.datafast.pinpad.cmd.Tools.encryption;

public class RulesPinPad {

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

    public void processCardNumber(String track2, String pan) {

        String firstDigCard = pan.substring(0,1);

        switch(firstDigCard) {
            case TWO:
            case TRHEE:
            case FOUR:
            case FIVE:
            case SIX:
                this.cardNumber = encryption.hashSha256(pan);
                break;

            default:
                this.cardNumber = track2;
                break;
        }



    }
}
