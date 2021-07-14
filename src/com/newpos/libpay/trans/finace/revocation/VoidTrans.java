package com.newpos.libpay.trans.finace.revocation;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.process.EmvTransaction;
import com.newpos.libpay.process.QpbocTransaction;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_SWIPE_ICC_CTL;

/**
 * Created by zhouqiang on 2016/12/6.
 * 消费撤销交易处理类
 *
 * @author zhouqiang
 */

public class VoidTrans extends FinanceTrans implements TransPresenter {

    private TransLogData data;

    public VoidTrans(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        transUI = para.getTransUI();
        isReversal = true;
        isSaveLog = true;
        isDebit = true;
        isProcPreTrans = true;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {

        InputInfo info = transUI.getOutsideInput(timeout, InputManager.Mode.VOUCHER, "");
        if (info.isResultFlag()) {
            TransLog log = TransLog.getInstance();
            data = log.searchTransLogByTraceNo(info.getResult());
            if (data != null && !data.getIsVoided() && data.getEName().equals(Type.SALE)) {
                retVal = transUI.showTransInfo(timeout, data);
                if (0 == retVal) {
                    Amount = data.getAmount();
                    RRN = data.getRRN();
                    AuthCode = data.getAuthCode();
                    Field61 = data.getBatchNo() + data.getTraceNo();
                    Pan = data.getPan();
                    ExpDate = data.getExpDate();
                    PanSeqNo = data.getPanSeqNo();
                    ICCData = data.getICCData();

                    CardInfo cardInfo = transUI.getCardUse(GERCARD_MSG_SWIPE_ICC_CTL,timeout, INMODE_IC | INMODE_NFC | INMODE_MAG, transEname);
                    afterGetCardUse(cardInfo);

                } else {
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                }
            } else {
                transUI.showError(timeout, Tcode.T_not_find_trans);
            }
        } else {
            transUI.showError(timeout, info.getErrno());
        }


        Logger.debug("VoidTrans>>finish");
        return;
    }

    private void afterGetCardUse(CardInfo info) {
        if (info.isResultFalg()) {
            int type = info.getCardType();
            switch (type) {
                case CardManager.TYPE_MAG:
                    inputMode = ENTRY_MODE_MAG;
                    break;
                case CardManager.TYPE_ICC:
                    inputMode = ENTRY_MODE_ICC;
                    break;
                case CardManager.TYPE_NFC:
                    inputMode = ENTRY_MODE_NFC;
                    break;
            }
            para.setInputMode(inputMode);
            if (inputMode == ENTRY_MODE_MAG) {
                isMag(info.getTrackNo());
            }
            if (inputMode == ENTRY_MODE_ICC) {
                isICC();
            }
            if (inputMode == ENTRY_MODE_NFC) {
                if (cfg.isForcePboc()) {
                    isICC();
                } else {
                    isNFC();
                }
            }
        } else {
            transUI.showError(timeout, info.getErrno());
        }
    }

    private void isICC() {
        transUI.handling(timeout, Tcode.Status.handling);
        emv = new EmvTransaction(para, Trans.Type.VOID);
        retVal = emv.start();
        if (1 == retVal || retVal == 0) {
            if (PAYUtils.isNullWithTrim(emv.getPinBlock())) {
                isPinExist = false;
            } else {
                isPinExist = true;
            }
            if (isPinExist) {
                PIN = emv.getPinBlock();
            }
            setICCData();
            if (data.getPanNormal().equals(Pan)){
                prepareOnline();
            }
            else
                transUI.showError(timeout, Tcode.T_void_card_not_same);

        } else {
            transUI.showError(timeout, retVal);
        }
    }

    private void isNFC() {
        transUI.handling(timeout, Tcode.Status.handling);
        qpboc = new QpbocTransaction(para);
        retVal = qpboc.start();
        if (0 == retVal) {
            String cn = qpboc.getCardNO();
            if (cn == null) {
                transUI.showError(timeout, Tcode.T_qpboc_read_err);
            } else {
                Pan = cn;
                retVal = transUI.showCardConfirm(timeout, cn);
                if (0 == retVal) {
                    PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount), cn);
                    afterQpbocGetPin(info);
                } else {
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                }
            }
        } else {
            transUI.showError(timeout, retVal);
        }
    }

    private void afterQpbocGetPin(PinInfo info) {
        if (info.isResultFlag()) {
            if (info.isNoPin()) {
                isPinExist = false;
            } else {
                isPinExist = true;
                PIN = ISOUtil.hexString(info.getPinblock());
            }
            IEMVHandler emvHandler = EMVHandler.getInstance();
            byte[] temp = ISOUtil.str2bcd(Pan, false);
            if (Pan.length() % 2 != 0) {
                temp[Pan.length() / 2] |= 0x0f;
            }
            emvHandler.setDataElement(new byte[]{0x5A}, temp);
            byte[] res = new byte[32];
            PAYUtils.get_tlv_data_kernal(0x9F10, res);
            setICCData();
            prepareOnline();
        } else {
            transUI.showError(timeout, info.getErrno());
        }
    }

    /**
     * 磁卡选项
     */
    private void isMag(String[] tracks) {
        String data1 = null;
        String data2 = null;
        String data3 = null;
        int msgLen = 0;
        if (tracks[0].length() > 0 && tracks[0].length() <= 80) {
            data1 = new String(tracks[0]);
        }
        if (tracks[1].length() >= 13 && tracks[1].length() <= 37) {
            data2 = new String(tracks[1]);
            if (!data2.contains("=")) {
                retVal = Tcode.T_search_card_err;
            } else {
                String judge = data2.substring(0, data2.indexOf('='));
                if (judge.length() < 13 || judge.length() > 19) {
                    retVal = Tcode.T_search_card_err;
                } else {
                    if (data2.indexOf('=') != -1) {
                        msgLen++;
                    }
                }
            }
        }
        if (tracks[2].length() >= 15 && tracks[2].length() <= 107) {
            data3 = new String(tracks[2]);
        }
        if (retVal != 0) {
            transUI.showError(timeout, retVal);
        } else {
            if (msgLen == 0) {
                transUI.showError(timeout, Tcode.T_search_card_err);
            } else {
                if (cfg.isCheckICC()) {
                    int splitIndex = data2.indexOf("=");
                    if (data2.length() - splitIndex >= 5) {
                        char iccChar = data2.charAt(splitIndex + 5);
                        if ((iccChar == '2' || iccChar == '6') && (!isFallBack)) {
                            transUI.showError(timeout, Tcode.T_ic_not_allow_swipe);
                        } else {
                            afterMAGJudge(data2, data3);
                        }
                    } else {
                        transUI.showError(timeout, Tcode.T_search_card_err);
                    }
                } else {
                    afterMAGJudge(data2, data3);
                }
            }
        }
    }

    private void afterMAGJudge(String data2, String data3) {
        String cardNo = data2.substring(0, data2.indexOf('='));
        retVal = transUI.showCardConfirm(timeout, cardNo);
        if (retVal == 0) {
            Pan = cardNo;
            Track2 = data2;
            Track3 = data3;
            PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount), cardNo);
            if (info.isResultFlag()) {
                if (info.isNoPin()) {
                    isPinExist = false;
                } else {
                    if (null == info.getPinblock()) {
                        isPinExist = false;
                    } else {
                        isPinExist = true;
                    }
                    PIN = ISOUtil.hexString(info.getPinblock());
                }
                prepareOnline();
            } else {
                transUI.showError(timeout, info.getErrno());
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
    }



    private void prepareOnline() {

        transUI.handling(timeout, Tcode.Status.connecting_center);
        setDatas(inputMode);

        if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC) {
            retVal = OnlineTrans(emv);
        } else {
            retVal = OnlineTrans(null);
        }
        Logger.debug("VoidTrans>>OnlineTrans=" + retVal);
        clearPan();
        if (retVal == 0) {
            data.setVoided(true);
            int index = TransLog.getInstance().getCurrentIndex(data);
            TransLog.getInstance().updateTransLog(index, data);
            transUI.trannSuccess(timeout, Tcode.Status.void_succ,
                    PAYUtils.getStrAmount(Amount));
        } else {
            transUI.showError(timeout, retVal);
        }
    }
}
