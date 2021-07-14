package com.newpos.libpay.device.card;

import android.os.ConditionVariable;

import com.newpos.bypay.EmvL2;
import com.newpos.bypay.EmvL2App;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.contactless.LedManager;
import com.newpos.libpay.trans.Tcode;
import com.pos.device.SDKException;
import com.pos.device.icc.ContactCard;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.IccReaderCallback;
import com.pos.device.icc.OperatorMode;
import com.pos.device.icc.SlotType;
import com.pos.device.icc.VCC;
import com.pos.device.magcard.MagCardCallback;
import com.pos.device.magcard.MagCardReader;
import com.pos.device.magcard.MagneticCard;
import com.pos.device.magcard.TrackInfo;
import com.pos.device.picc.EmvContactlessCard;
import com.pos.device.picc.PiccReader;
import com.pos.device.picc.PiccReaderCallback;

/**
 * Created by zhouqiang on 2017/3/14.
 * @author zhouqiang
 * 寻卡管理者
 */

public class CardManager {

    public static final int TYPE_MAG = 1 ;
    public static final int TYPE_ICC = 2 ;
    public static final int TYPE_NFC = 3 ;
    public static final int TYPE_HAND= 4 ;

    public static final int INMODE_MAG = 0x02;
    public static final int INMODE_IC = 0x08;
    public static final int INMODE_NFC = 0x10;

    private static CardManager instance ;

    private static int mode ;

    //ACQUIRER_ROW acquirer_row = ACQUIRER_ROW.getSingletonInstance();
    //Context context;

    private CardManager(){}

    public static CardManager getInstance(int m){
        mode = m ;
        if(null == instance){
            instance = new CardManager();
        }
        return instance ;
    }

    private MagCardReader magCardReader ;
    private IccReader iccReader ;
    private PiccReader piccReader ;
    public EmvContactlessCard mEmvContactlessCard;

    public int GetMode(){
        return mode;
    }

    public void SetMode(int pmode){
        mode=pmode;
    }

    public void PiccReset() {
        try {
            piccReader.reset();
        } catch (SDKException e) {
            Logger.error("Exception" + e.toString());
        }
    }

    private void init(){
        if( (mode & INMODE_MAG ) != 0 ){
            magCardReader = MagCardReader.getInstance();
        }
        if( (mode & INMODE_IC) != 0 ){
            iccReader = IccReader.getInstance(SlotType.USER_CARD);
        }
        if( (mode & INMODE_NFC) != 0 ){
            piccReader = PiccReader.getInstance();
        }
        isEnd = false ;
    }

    private void stopMAG(){
        try {
            if(magCardReader!=null){
                magCardReader.stopSearchCard();
            }
        } catch (SDKException e) {
            Logger.error("Exception" + e.toString());
        }
    }

    private void stopICC(){
        if(iccReader!=null){
            try {
                iccReader.stopSearchCard();
            } catch (SDKException e) {
                Logger.error("Exception" + e.toString());
            }
        }
    }

    public void stopPICC(){
        if(piccReader!=null){
            LedManager.getInstance().turnOffAll();
            piccReader.stopSearchCard();
            try {
                piccReader.release();
            } catch (SDKException e) {
                Logger.error("Exception" + e.toString());
            }
        }
    }

    public void releaseAll(){
        isEnd = true ;
        try {
            if(magCardReader!=null){
                magCardReader.stopSearchCard();
                Logger.debug("mag stop");
            }
            if(iccReader!=null){
                iccReader.stopSearchCard();
                iccReader.release();
                Logger.debug("icc stop");
            }
            if(piccReader!=null){
                piccReader.stopSearchCard();
                piccReader.release();
                Logger.debug("picc stop");
            }
        } catch (SDKException e) {
            Logger.error("Exception" + e.toString());
        }
    }

    private CardListener listener ;

    private boolean isEnd = false ;

    public void getCard(final int timeout , CardListener l){
        Logger.debug("CardManager>>getCard>>timeout="+timeout);
        init();
        //acquirer_row.selectACQUIRER_ROW("01", context);
        final CardInfo info = new CardInfo() ;
        if(null == l){
            info.setResultFalg(false);
            info.setErrno(Tcode.T_invoke_para_err);
            listener.callback(info);
        }else {
            this.listener = l ;
            new Thread(){
                @Override
                public void run(){
                    try{
                        if( (mode & INMODE_MAG) != 0 ){
                            Logger.debug("CardManager>>getCard>>MAG");
                            magCardReader.startSearchCard(timeout, new MagCardCallback() {
                                @Override
                                public void onSearchResult(int i, MagneticCard magneticCard) {
                                    if(!isEnd){
                                        Logger.debug("CardManager>>getCard>>MAG>>i="+i);
                                        isEnd = true ;
                                        stopICC();
                                        stopPICC();
                                        if( 0 == i ){
                                            listener.callback(handleMAG(magneticCard));
                                        }
                                        else {
                                            info.setResultFalg(false);
                                            if (2==i)
                                                info.setErrno(Tcode.T_wait_timeout);
                                            else
                                                info.setErrno(Tcode.T_search_card_err);
                                            listener.callback(info);
                                        }
                                    }
                                }
                            });
                        }if( (mode & INMODE_IC) != 0 ){
                            Logger.debug("CardManager>>getCard>>ICC");
                            iccReader.startSearchCard(timeout, new IccReaderCallback() {
                                @Override
                                public void onSearchResult(int i) {
                                    if(!isEnd){
                                        Logger.debug("CardManager>>getCard>>ICC>>i="+i);
                                        isEnd = true ;
                                        stopMAG();
                                        stopPICC();
                                        if( 0 == i ){
                                            try {
                                                listener.callback(handleICC());
                                            } catch (SDKException e) {
                                                info.setResultFalg(false);
                                                info.setErrno(Tcode.T_sdk_err);
                                                listener.callback(info);
                                            }
                                        }else {
                                            info.setResultFalg(false);
                                            if (2==i)
                                                info.setErrno(Tcode.T_wait_timeout);
                                            else
                                                info.setErrno(Tcode.T_search_card_err);
                                            listener.callback(info);
                                        }
                                    }
                                }
                            });
                        }if( (mode & INMODE_NFC) != 0 ){
                            Logger.debug("CardManager>>getCard>>NFC");
                            piccReader.startSearchCard(timeout, new PiccReaderCallback() {
                                @Override
                                public void onSearchResult(int i, int i1) {
                                    try {
                                        Thread.sleep(400);
                                    } catch (InterruptedException e) {
                                        Logger.error("Exception" + e.toString());
                                        Thread.currentThread().interrupt();
                                    }
                                    if(!isEnd){
                                        Logger.debug("CardManager>>getCard>>NFC>>i="+i);
                                        isEnd = true ;
                                        stopICC();
                                        stopMAG();
                                        if( 0 == i ){
                                            listener.callback(handlePICC(i1));
                                        }else {
                                            info.setResultFalg(false);
                                            if (2==i)
                                                info.setErrno(Tcode.T_wait_timeout);
                                            else
                                                info.setErrno(Tcode.T_search_card_err);
                                            listener.callback(info);
                                        }
                                    }
                                }
                            });
                        }
                    }catch (SDKException sdk){
                        Logger.debug("SDKException="+sdk.getMessage().toString());
                        releaseAll();
                        info.setResultFalg(false);
                        info.setErrno(Tcode.T_sdk_err);
                        listener.callback(info);
                    }
                }
            }.start();
        }
    }

    private CardInfo handleMAG(MagneticCard card){
        CardInfo info = new CardInfo() ;
        info.setResultFalg(true);
        info.setCardType(CardManager.TYPE_MAG);
        TrackInfo ti_1 = card.getTrackInfos(MagneticCard.TRACK_1);
        TrackInfo ti_2 = card.getTrackInfos(MagneticCard.TRACK_2);
        TrackInfo ti_3 = card.getTrackInfos(MagneticCard.TRACK_3);
        info.setTrackNo(new String[]{ti_1.getData() , ti_2.getData() , ti_3.getData()});
        return info ;
    }

    private CardInfo handleICC() throws SDKException {
        CardInfo info = new CardInfo();
        info.setCardType(CardManager.TYPE_ICC);
        if (iccReader.isCardPresent()) {
            ContactCard contactCard = iccReader.connectCard(VCC.VOLT_5 , OperatorMode.EMV_MODE);
            byte[] atr = contactCard.getATR() ;
            if (atr.length != 0) {
                info.setResultFalg(true);
                info.setCardAtr(atr);
            } else {
                info.setResultFalg(false);
                info.setErrno(Tcode.T_ic_power_err);
            }
        } else {
            info.setResultFalg(false);
            info.setErrno(Tcode.T_ic_not_exist_err);
        }
        return info;
    }

    private CardInfo handlePICC(int nfcType){
        CardInfo info = new CardInfo();
        info.setResultFalg(true);
        info.setCardType(CardManager.TYPE_NFC);
        info.setNfcType(nfcType);
        return info ;
    }

    private ConditionVariable mVariable;
    private int timeout= 60*1000;
    public int DetectCards(EmvL2App param){
        mVariable = new ConditionVariable();
        final int[] ret = {0};
        releaseAll();
        if (param.DetectMagStripe) {
            //Not support
        }
        if (param.DetectContact) {
            //Not Support
        }
        if (param.DetectContactLess) {
            piccReader = PiccReader.getInstance();
            Logger.debug("use contactless card");
            piccReader.startSearchCard(timeout, new PiccReaderCallback() {
                @Override
                public void onSearchResult(int i, int i1) {
                    Logger.debug("get contactless card i = "+i+" i1 = "+i1);
                    if (i == 0) {
                        ret[0] = EmvL2.L2_CS_PRESENT;
                        try {
                            mEmvContactlessCard = EmvContactlessCard.connect();
                        } catch (SDKException e) {
                            Logger.error("Exception" + e.toString());
                        }
                        mode = TYPE_NFC;
                    }else {
                        // ret[0] = EmvL2.L2_CS_TIMEOUT;
                        Logger.debug("get picc error error");

                    }
                    stopICC();
                    stopMAG();
                    mVariable.open();
                }
            });
        }

        mVariable.block();
        return ret[0];
    }
}
