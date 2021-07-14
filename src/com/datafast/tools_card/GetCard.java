package com.datafast.tools_card;

import android.os.AsyncTask;

import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardListener;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.trans.Tcode;

import java.util.concurrent.CountDownLatch;

public class GetCard extends AsyncTask<Boolean, Void, CardInfo> {

    private int mode;
    private int timeOut;
    private boolean ret;
    public static CardInfo cInfo;
    public static CardManager cardManager = null;
    private GetCardInfo callback;

    public GetCard(CardInfo cInfo, int mode, int timeOut) {
        this.cInfo = cInfo;
        this.mode = mode;
        this.timeOut = timeOut;
    }

    public GetCardInfo callback(final GetCardInfo callback){
        this.callback = callback;
        return callback;
    }

    @Override
    protected CardInfo doInBackground(Boolean... booleans) {

        while (true){

            if (isCancelled()){
                cardManager = null;
                break;
            }

            cInfo = new CardInfo();
            final CountDownLatch wait = new CountDownLatch(1);
            cardManager = CardManager.getInstance(mode);

            cardManager.getCard(timeOut, new CardListener() {
                @Override
                public void callback(CardInfo cardInfo) {
                    /*cInfo.setResultFalg(cardInfo.isResultFalg());
                    cInfo.setNfcType(cardInfo.getNfcType());
                    cInfo.setCardType(cardInfo.getCardType());
                    cInfo.setTrackNo(cardInfo.getTrackNo());
                    cInfo.setCardAtr(cardInfo.getCardAtr());
                    cInfo.setErrno(cardInfo.getErrno());
                    cardManager = null;
                    wait.countDown();

                    if (cInfo.isResultFalg())
                        ret = true;
                    else
                        ret = false;*/

                    if (isCancelled()) {
                        cardManager = null;
                        ret = false;
                    }else {
                        cInfo.setResultFalg(cardInfo.isResultFalg());
                        cInfo.setNfcType(cardInfo.getNfcType());
                        cInfo.setCardType(cardInfo.getCardType());
                        cInfo.setTrackNo(cardInfo.getTrackNo());
                        cInfo.setCardAtr(cardInfo.getCardAtr());
                        cInfo.setErrno(cardInfo.getErrno());
                        cardManager = null;
                        wait.countDown();

                        if (cInfo.isResultFalg())
                            ret = true;
                        else
                            ret = false;
                    }
                }
            });

            try {
                wait.await();
            } catch (InterruptedException e) {
                Logger.error("Exception" + e.toString());
                Thread.currentThread().interrupt();
            }

            if (ret)
                if (!(cInfo.getErrno() == Tcode.T_search_card_err))
                    break;

        }
        return cInfo;
    }

    @Override
    protected void onPostExecute(CardInfo cardInfo) {
        super.onPostExecute(cardInfo);

        callback.getCardInf(cardInfo);
    }
}
