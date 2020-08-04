package com.newpos.libpay.presenter;

import android.app.Activity;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.datafast.inicializacion.prompts.Prompt;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.server.server_tcp.Server;
import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.PaySdkException;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardListener;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.pinpad.OfflineRSA;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.device.pinpad.PinpadListener;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.device.scanner.QRCInfo;
import com.newpos.libpay.device.scanner.QRCListener;
import com.newpos.libpay.device.scanner.ScannerManager;
import com.newpos.libpay.device.user.OnUserResultListener;
import com.newpos.libpay.global.TMConstants;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import static com.datafast.tools_card.GetCard.cInfo;
import static com.datafast.tools_card.GetCard.cardManager;
import static com.newpos.libpay.trans.finace.FinanceTrans.INMODE_HAND;

/**
 * Created by zhouqiang on 2017/4/25.
 *
 * @author zhouqiang
 * 交易UI接口实现类
 * MVP架构中的P层 ，处理复杂的逻辑及数据
 */

public class TransUIImpl implements TransUI {

    private TransView transView;
    private Activity mActivity;

    public TransUIImpl(Activity activity, TransView tv) {
        this.transView = tv;
        this.mActivity = activity;
    }

    private CountDownLatch mLatch;
    private int mRet = 0;
    private InputManager.Style payStyle;

    private final OnUserResultListener listener = new OnUserResultListener() {
        @Override
        public void confirm(InputManager.Style style) {
            mRet = 0;
            payStyle = style;
            mLatch.countDown();
        }

        @Override
        public void cancel() {
            mRet = 1;
            mLatch.countDown();
        }

        @Override
        public void confirm(int applistselect) {
            mRet = applistselect;
            mLatch.countDown();
        }
    };

    @Override
    public PinInfo getPinpadOfflinePin(int timeout, String amount, String cardNo) {
        Logger.debug("Masterctl>>getPinpadOfflinePin");
        this.mLatch = new CountDownLatch(1);
        final PinInfo pinInfo = new PinInfo();
        PinpadManager pinpadManager = PinpadManager.getInstance();
        pinpadManager.getOfflinePin(timeout,amount,cardNo, new PinpadListener() {
            @Override
            public void callback(PinInfo info) {
            }
        });
        transView.showMsgInfo(timeout, getStatusInfo(String.valueOf(Tcode.Status.handling)),false);
        return null ;
    }

    @Override
    public void showOfflinePinResult(int count) {
        transView.showOfflinePIN(count);
    }
    @Override
    public InputInfo getOutsideInput(int timeout, InputManager.Mode type, String title) {
        transView.showInputView(timeout, type, listener, title);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(type));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public CardInfo getCardUse(String msg, int timeout, int mode, String title) {
        transView.showCardView(msg, timeout, mode, title, listener);
        this.mLatch = new CountDownLatch(1);
        //final CardInfo cInfo = new CardInfo() ;
        //CardManager cardManager = CardManager.getInstance(mode);
        cInfo = new CardInfo();
        cardManager = CardManager.getInstance(mode);
        cardManager.getCard(timeout, new CardListener() {
            @Override
            public void callback(CardInfo cardInfo) {
                cInfo.setResultFalg(cardInfo.isResultFalg());
                cInfo.setNfcType(cardInfo.getNfcType());
                cInfo.setCardType(cardInfo.getCardType());
                cInfo.setTrackNo(cardInfo.getTrackNo());
                cInfo.setCardAtr(cardInfo.getCardAtr());
                cInfo.setErrno(cardInfo.getErrno());
                mLatch.countDown();
            }
        });
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }

        if (cInfo.getErrno()==0){//Validacion para control de ingreso de tarjeta manualmente
            if ((mode & INMODE_HAND) != 0) {
                if (transView.getInput(InputManager.Mode.REFERENCE).equals("MANUAL")) {
                    cInfo.setResultFalg(true);
                    cInfo.setCardType(CardManager.TYPE_HAND);
                }
            }
        }

        return cInfo;
    }

    @Override
    public CardInfo getCardUseAmount(String msg, int timeout, int mode, String title,String label, String amount) {
        transView.showCardViewAmount(msg, timeout, mode, title,label,amount, listener);
        this.mLatch = new CountDownLatch(1);
        //final CardInfo cInfo = new CardInfo() ;
        //CardManager cardManager = CardManager.getInstance(mode);
        cInfo = new CardInfo();
        cardManager = CardManager.getInstance(mode);
        cardManager.getCard(timeout, new CardListener() {
            @Override
            public void callback(CardInfo cardInfo) {
                cInfo.setResultFalg(cardInfo.isResultFalg());
                cInfo.setNfcType(cardInfo.getNfcType());
                cInfo.setCardType(cardInfo.getCardType());
                cInfo.setTrackNo(cardInfo.getTrackNo());
                cInfo.setCardAtr(cardInfo.getCardAtr());
                cInfo.setErrno(cardInfo.getErrno());
                mLatch.countDown();
            }
        });
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }

        if (cInfo.getErrno()==0){//Validacion para control de ingreso de tarjeta manualmente
            if ((mode & INMODE_HAND) != 0) {
                if (transView.getInput(InputManager.Mode.REFERENCE).equals("MANUAL")) {
                    cInfo.setResultFalg(true);
                    cInfo.setCardType(CardManager.TYPE_HAND);
                }
            }
        }

        return cInfo;
    }

    @Override
    public CardInfo getCardUsePagosElect(String msg, int timeout, int mode, String title, String label, String amount, int minLen, int maxLen) {
        transView.showCardPagosElect(msg, timeout, mode, title,label,amount, minLen, maxLen, listener);
        this.mLatch = new CountDownLatch(1);
        //final CardInfo cInfo = new CardInfo() ;
        //CardManager cardManager = CardManager.getInstance(mode);
        cInfo = new CardInfo();
        cardManager = CardManager.getInstance(mode);
        cardManager.getCard(timeout, new CardListener() {
            @Override
            public void callback(CardInfo cardInfo) {
                cInfo.setResultFalg(cardInfo.isResultFalg());
                cInfo.setNfcType(cardInfo.getNfcType());
                cInfo.setCardType(cardInfo.getCardType());
                cInfo.setTrackNo(cardInfo.getTrackNo());
                cInfo.setCardAtr(cardInfo.getCardAtr());
                cInfo.setErrno(cardInfo.getErrno());
                mLatch.countDown();
            }
        });
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }

        if (cInfo.getErrno()==0){//Validacion para control de ingreso de tarjeta manualmente
            if ((mode & INMODE_HAND) != 0) {
                String input = transView.getInput(InputManager.Mode.REFERENCE);
                if (input.contains("MANUAL")) {
                    cInfo.setResultFalg(true);
                    cInfo.setCardType(CardManager.TYPE_HAND);
                    if (input.contains("|")) {
                        cInfo.setToken(input.substring(input.indexOf("|") + 1));
                    }
                }
            }
        }

        return cInfo;
    }

    @Override
    public CardInfo getCardFallback(String msg, int timeout, int mode, String title) {
        transView.showCardView(msg, timeout, mode, title, listener);
        this.mLatch = new CountDownLatch(1);
        //final CardInfo cInfo = new CardInfo() ;
        //CardManager cardManager = CardManager.getInstance(mode);
        cInfo = new CardInfo();
        cardManager = CardManager.getInstance(mode);
        cardManager.getCard(timeout, new CardListener() {
            @Override
            public void callback(CardInfo cardInfo) {
                cInfo.setResultFalg(cardInfo.isResultFalg());
                cInfo.setNfcType(cardInfo.getNfcType());
                cInfo.setCardType(cardInfo.getCardType());
                cInfo.setTrackNo(cardInfo.getTrackNo());
                cInfo.setCardAtr(cardInfo.getCardAtr());
                cInfo.setErrno(cardInfo.getErrno());
                mLatch.countDown();
            }
        });
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }

        return cInfo;
    }

    @Override
    public QRCInfo getQRCInfo(final int timeout, final InputManager.Style mode) {
        transView.showQRCView(timeout, mode);
        this.mLatch = new CountDownLatch(1);
        final QRCInfo qinfo = new QRCInfo();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ScannerManager manager = ScannerManager.getInstance(mActivity, mode);
                manager.getQRCode(timeout, new QRCListener() {
                    @Override
                    public void callback(QRCInfo info) {
                        qinfo.setResultFalg(info.isResultFalg());
                        qinfo.setErrno(info.getErrno());
                        qinfo.setQrc(info.getQrc());
                        mLatch.countDown();
                    }
                });
            }
        });

        try {
            mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return qinfo;
    }

    @Override
    public PinInfo getPinpadOnlinePin(int timeout, String amount, String cardNo) {
        this.mLatch = new CountDownLatch(1);
        final PinInfo pinInfo = new PinInfo();
        PinpadManager pinpadManager = PinpadManager.getInstance();
        pinpadManager.getPin(timeout, amount, cardNo, new PinpadListener() {
            @Override
            public void callback(PinInfo info) {
                pinInfo.setResultFlag(info.isResultFlag());
                pinInfo.setErrno(info.getErrno());
                pinInfo.setNoPin(info.isNoPin());
                pinInfo.setPinblock(info.getPinblock());
                mLatch.countDown();
            }
        });
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        transView.showMsgInfo(timeout, getStatusInfo(String.valueOf(Tcode.Status.handling)),false);
        return pinInfo;
    }

    @Override
    public int showCardConfirm(int timeout, String cn) {
        this.mLatch = new CountDownLatch(1);
        transView.showCardNo(timeout, cn, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return mRet;
    }

    @Override
    public InputInfo showMessageInfo(String title, String msg, String btnCancel, String btnConfirm, int timeout) {
        this.mLatch = new CountDownLatch(1);
        transView.showMessageInfo(title, msg, btnCancel, btnConfirm, timeout, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
        }
        return info;
    }

    @Override
    public InputInfo showMessageImpresion(String title, String msg, String btnCancel, String btnConfirm, int timeout) {
        this.mLatch = new CountDownLatch(1);
        transView.showMessageImpresion(title, msg, btnCancel, btnConfirm, timeout, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
        }
        return info;
    }

    @Override
    public int showCardApplist(int timeout, String[] list) {
        this.mLatch = new CountDownLatch(1);
        transView.showCardAppListView(timeout, list, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return mRet;
    }

    @Override
    public int showMultiLangs(int timeout, String[] langs) {
        this.mLatch = new CountDownLatch(1);
        transView.showMultiLangView(timeout, langs, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return mRet;
    }

    @Override
    public void handling(int timeout, int status) {
        transView.showMsgInfo(timeout, getStatusInfo(String.valueOf(status)),false);
    }

    public void handlingError(int timeout, int status) {
        transView.showMsgInfo(timeout, getErrInfo(String.valueOf(status)),true);
    }

    @Override
    public void handling(int timeout, int status, String title) {
        transView.showMsgInfo(timeout, getStatusInfo(String.valueOf(status)), title,false);
    }

    @Override
    public void handlingInfo(int timeout, int status, String msg) {
        transView.showMsgInfo(timeout, getStatusInfo(String.valueOf(status)) + msg,true);
    }

    @Override
    public int showTransInfo(int timeout, TransLogData logData) {
        this.mLatch = new CountDownLatch(1);
        transView.showTransInfoView(timeout, logData, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return mRet;
    }

    @Override
    public void trannSuccess(int timeout, int code, String... args) {
        String info = getStatusInfo(String.valueOf(code));
        if (args.length != 0) {
            info += "\n" + args[0];
        }
        transView.showSuccess(timeout, info);
    }

    @Override
    public void showError(int timeout, int errcode) {
        transView.showError(timeout, getErrInfo(String.valueOf(errcode)));
    }

    @Override
    public void showfinish() {
        transView.showfinishview();
    }

    @Override
    public void showError(int timeout, int errcode, ProcessPPFail processPPFail) {
        processPPFail.cmdCancel(Server.cmd,errcode);
        transView.showError(timeout, getErrInfo(String.valueOf(errcode)));
    }

    @Override
    public InputInfo showTypeCoin(int timeout, final String title) {
        transView.showTypeCoinView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showInputUser(int timeout, String title, String label, int min, int max) {
        transView.showInputUser(timeout, title, label, min, max, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public void toasTrans(int errcode, boolean sound, boolean isErr) {
        if (isErr)
            transView.toasTransView(getErrInfo(String.valueOf(errcode)), sound);
        else
            transView.toasTransView(getStatusInfo(String.valueOf(errcode)), sound);
    }

    @Override
    public void toasTransReverse(int errcode, boolean sound, boolean isErr) {
        if (isErr)
            transView.toasTransViewReverse(getErrInfo(String.valueOf(errcode)), sound);
        else
            transView.toasTransViewReverse(getStatusInfo(String.valueOf(errcode)), sound);
    }

    public void toasTrans(String errcode, boolean sound, boolean isErr) {
        if (isErr)
            transView.toasTransView(errcode, sound);
        else
            transView.toasTransView(errcode, sound);
    }

    @Override
    public InputInfo showConfirmAmount(int timeout, String title, String label, String amnt, boolean isHTML) {
        transView.showConfirmAmountView(timeout, title, label, amnt, isHTML, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public void showMessage(String message, boolean transaccion) {
        transView.showMsgInfo(60 * 1000, message, transaccion);
    }


    /**
     * =============================================
     */

    public static String getStatusInfo(String status) {
        try {
            String[] infos = Locale.getDefault().getLanguage().equals("zh") ?
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.STATUS, status) :
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.STATUS_EN, status);
            if (infos != null) {
                return infos[0];
            }
        } catch (PaySdkException pse) {
            Logger.error("Exception" + pse.toString());
            Thread.currentThread().interrupt();
        }
        if (Locale.getDefault().getLanguage().equals("zh")) {
            return "未知信息";
        } else {
            return "Error Desconocido";
        }
    }

    public static String getErrInfo(String status) {
        try {
            String[] errs = Locale.getDefault().getLanguage().equals("zh") ?
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.ERRNO, status) :
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.ERRNO_EN, status);
            if (errs != null) {
                return errs[0];
            }
        } catch (PaySdkException pse) {
            Logger.error("Exception" + pse.toString());
            Thread.currentThread().interrupt();
        }
        if (Locale.getDefault().getLanguage().equals("zh")) {
            return "未知错误";
        } else {
            return "Codigo de Error Desconocido";
        }
    }

    @Override
    public void showCardImg(String img) {
        this.mLatch = new CountDownLatch(1);
        transView.showCardViewImg(img, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
    }

    @Override
    public InputInfo showSignature(int timeout, String title, String transType) {
        transView.showSignatureView(timeout, listener, title, transType);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showList(int timeout, String title, String transType, final ArrayList<String> listMenu, int id) {
        transView.showListView(timeout, listener, title, transType, listMenu, id);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showInputPrompt(int timeout, String transType, String nameAcq, Prompt cls) {
        transView.showInputPromptView(timeout, transType, nameAcq, cls, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
            info.setNextStyle(payStyle);
        }
        return info;
    }
}
