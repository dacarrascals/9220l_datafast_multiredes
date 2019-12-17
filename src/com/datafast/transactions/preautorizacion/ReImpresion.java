package com.datafast.transactions.preautorizacion;

import android.content.Context;
import android.media.ToneGenerator;

import com.android.newpos.libemv.PBOCTag9c;
import com.android.newpos.libemv.PBOCTransProperty;
import com.android.newpos.libemv.PBOCode;
import com.datafast.transactions.common.CommonFunctionalities;
import com.datafast.transactions.common.GetAmount;
import com.newpos.bypay.EmvL2CVM;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.contactless.EmvL2Process;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.process.EmvTransaction;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import java.util.Objects;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;

import static cn.desert.newpos.payui.master.MasterControl.incardTable;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_FALLBACK;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_SWIPE_ICC_CTL;
import static com.datafast.menus.menus.FALLBACK;
import static com.datafast.menus.menus.contFallback;
import static com.datafast.menus.menus.idAcquirer;

public class ReImpresion extends FinanceTrans implements TransPresenter {

    public ReImpresion(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        //super(ctx, transEname, FILE_NAME_PREAUTO);

        para = p;
        transUI = para.getTransUI();
        isReversal = true;
        isProcPreTrans = true;
        isSaveLog = false;
        isDebit = true;
        TransEName = transEname;
        currency_name = CommonFunctionalities.tipoMoneda()[0];
        typeCoin = CommonFunctionalities.tipoMoneda()[1];
        host_id = idAcquirer;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    @Override
    public void start() {

        if (!CommonFunctionalities.checkCierre(context)) {
            transUI.showError(timeout, Tcode.T_err_batch_full);
            return;
        }

        if (procesarReimpresion() == 0) {
            transUI.trannSuccess(timeout, Tcode.Status.void_succ);
            UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
        } else {
            transUI.showError(timeout, retVal);
            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
        }

        Logger.debug("voidPreAutoTrans>>finish");
    }

    private int procesarReimpresion() {

        if ((retVal = CommonFunctionalities.setNumReferencia(timeout, "REIMPRESION", transUI)) != 0) {
            return retVal;
        }

        if ((retVal = CommonFunctionalities.setIdPreAutoAmpliacion(timeout, "REIMPRESION", transUI)) != 0) {
            return retVal;
        }

        IdPreAutAmpl = CommonFunctionalities.getIdPreAutoAmpliacion();
        Pan = CommonFunctionalities.getPan();
        TraceNo = ISOUtil.padleft("" + CommonFunctionalities.getNumReferencia(), 6, '0');
        AuthCode = "000000";

        if (setAmount()) {
            retVal = CardProcess();
        }
        return retVal;
    }

    private boolean setAmount() {

        GetAmount amount = new GetAmount(transUI, timeout, Pan, TransEName);
        //amount.setTypeTrans(TransEName);
        if (amount.setAmount()) {

            AmountBase0 = amount.getmAmntBase0();
            AmountXX = amount.getmAmntXX();
            IvaAmount = amount.getmIVAAmnt();
            ServiceAmount = amount.getmServiceAmnt();
            TipAmount = amount.getmTipAmnt();
            ExtAmount = ISOUtil.padleft(TipAmount + "", 12, '0');

            Amount = amount.getmAmnt();

            /*currency_name = amount.getmCurrencyName();
            typeCoin = amount.getMtypeCoin();*/

            retVal = amount.getmRetVal();

            para.setAmountBase0(AmountBase0);
            para.setAmountXX(AmountXX);
            para.setIvaAmount(IvaAmount);
            para.setServiceAmount(ServiceAmount);
            para.setTipAmount(TipAmount);
            para.setAmount(Amount);
            para.setOtherAmount(0);

            para.setCurrency_name(currency_name);
            para.setTypeCoin(typeCoin);

            return true;
        } else {
            retVal = Tcode.T_user_cancel_input;
            transUI.showError(timeout, Tcode.T_user_cancel_input);
            return false;
        }
    }

    private int CardProcess() {

        CardInfo cardInfo = transUI.getCardUse(GERCARD_MSG_SWIPE_ICC_CTL, timeout, INMODE_IC | INMODE_MAG | INMODE_NFC | INMODE_HAND, transEname);

        if (cardInfo.isResultFalg()) {
            int type = cardInfo.getCardType();
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
                case CardManager.TYPE_HAND:
                    inputMode = ENTRY_MODE_HAND;
                    break;
                default:
                    retVal = Tcode.T_not_allow;
                    return retVal;
            }
            para.setInputMode(inputMode);
            if (inputMode == ENTRY_MODE_ICC) {
                isICC();
            }
            if (inputMode == ENTRY_MODE_MAG) {
                isDebit = false;
                isMag(cardInfo.getTrackNo());
            }
            if (inputMode == ENTRY_MODE_NFC) {
                if (cfg.isForcePboc()) {
                    isICC();
                } else {
                    PBOCTrans();
                }
            }
            if (inputMode == ENTRY_MODE_HAND) {
                isDebit = false;
                isHandle();
            }
        } else {
            retVal = cardInfo.getErrno();
            if (retVal == 0) {
                retVal = Tcode.T_user_cancel_input;
            }
            else if(retVal == 107) {
                if (!CommonFunctionalities.validateCard(timeout, transUI)) {
                    retVal = Tcode.T_err_timeout;
                    return retVal;
                }
                contFallback++;
            }
        }

        if (contFallback == FALLBACK){
            isFallBack = true;

            retVal = transUI.showCardConfirm(timeout, "Pase la tarjeta");
            if(0 == retVal){
                cardInfo = transUI.getCardUse(GERCARD_MSG_FALLBACK, timeout,  INMODE_MAG, transEname);
                if (cardInfo.isResultFalg()) {
                    int type = cardInfo.getCardType();
                    switch (type) {
                        case CardManager.TYPE_MAG:
                            inputMode = ENTRY_MODE_MAG;
                            break;
                        default:
                            retVal = Tcode.T_not_allow;
                            return retVal;
                    }
                    para.setInputMode(inputMode);

                    if (inputMode == ENTRY_MODE_MAG) {
                        isDebit = false;
                        isMag(cardInfo.getTrackNo());
                    }
                } else {
                    retVal = cardInfo.getErrno();
                    if (retVal == 0) {
                        retVal = Tcode.T_user_cancel_input;
                    }
                }
            }else {
                transUI.showError(timeout , Tcode.T_user_cancel_operation);
            }
        }

        if (retVal == 107){
            CardProcess();
        }

        return retVal;
    }

    private void isHandle() {
        if ((retVal = CommonFunctionalities.setPanManual(timeout, "REIMPRESION", transUI)) != 0) {
            return;
        }

        Pan = CommonFunctionalities.getPan();

        if (!incardTable(Pan,TransEName)) {
            retVal = Tcode.T_unsupport_card;
            transUI.showError(timeout, Tcode.T_unsupport_card);
            return;
        }

        if (!ISOUtil.stringToBoolean(rango.getMANUAL())) {
            retVal = Tcode.T_err_not_allow;
            transUI.showError(timeout, retVal);
            return;
        }

        if ((retVal = CommonFunctionalities.setFechaExp(timeout, "REIMPRESION", transUI, ISOUtil.stringToBoolean(rango.getFECHA_EXP()))) != 0) {
            return;
        }

        ExpDate = CommonFunctionalities.getExpDate();

        if ((retVal = CommonFunctionalities.setCVV2(timeout, "REIMPRESION", transUI, ISOUtil.stringToBoolean(rango.getCVV2()))) != 0) {
            return;
        }

        CVV = CommonFunctionalities.getCvv2();


        prepareOnline();

        retVal = 0;
    }

    private void isMag(String[] tracks) {
        String data1 = null;
        String data2 = null;
        String data3 = null;
        int msgLen = 0;
        if (tracks[0].length() > 0 && tracks[0].length() <= 80) {
            data1 = tracks[0];
        }
        if (tracks[1].length() >= 13 && tracks[1].length() <= 37) {
            data2 = tracks[1];
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
            data3 = tracks[2];
        }
        if (retVal != 0) {
            transUI.showError(timeout, retVal);
        } else {
            if (msgLen == 0) {
                retVal = Tcode.T_search_card_err;
                transUI.showError(timeout, Tcode.T_search_card_err);
            } else {

                try {
                    if (!incardTable(data2.substring(0, data2.indexOf('=')), TransEName)) {
                        retVal = Tcode.T_unsupport_card;
                        transUI.showError(timeout, Tcode.T_unsupport_card);
                        return;
                    }
                }catch (IndexOutOfBoundsException e) {
                    retVal = Tcode.T_read_app_data_err;
                    transUI.showError(timeout, Tcode.T_read_app_data_err);
                    return;
                }

                int splitIndex = data2.indexOf("=");

                if (ISOUtil.stringToBoolean(rango.getPIN_SERVICE_CODE())) {
                    char isDebitChar = data2.charAt(splitIndex + 7);
                    if (isDebitChar == '0' || isDebitChar == '5' || isDebitChar == '6' || isDebitChar == '7') {
                        isDebit = true;
                    }
                }

                if (!ISOUtil.stringToBoolean(rango.getOMITIR_EMV())) {
                    if (data2.length() - splitIndex >= 5) {
                        char iccChar = data2.charAt(splitIndex + 5);

                        if ((iccChar == '2' || iccChar == '6') && (!isFallBack)) {
                            retVal = Tcode.T_ic_not_allow_swipe;
                            transUI.showError(timeout, Tcode.T_ic_not_allow_swipe);
                        } else {
                            afterMAGJudge(data1, data2, data3);
                        }
                    } else {
                        retVal = Tcode.T_search_card_err;
                        transUI.showError(timeout, Tcode.T_search_card_err);
                    }
                } else {
                    afterMAGJudge(data1, data2, data3);
                }
            }
        }
    }

    private void afterMAGJudge(String data1, String data2, String data3) {
        String cardNo = data2.substring(0, data2.indexOf('='));
        //retVal = transUI.showCardConfirm(timeout , cardNo);
        //if(retVal == 0){
        Pan = cardNo;
        Track1 = data1;
        Track2 = data2;
        Track3 = data3;

        if ((retVal = CommonFunctionalities.last4card(timeout, TransEName, Pan, transUI, ISOUtil.stringToBoolean(rango.getULTIMOS_4()))) != 0) {
            return;
        }

        if ((retVal = CommonFunctionalities.setCVV2(timeout, TransEName, transUI, ISOUtil.stringToBoolean(rango.getCVV2()))) != 0) {
            return;
        }

        CVV = CommonFunctionalities.getCvv2();

        prepareOnline();

    }

    public void isICC() {
        String creditCard = "SI";
        para.setAmount(Amount);
        para.setOtherAmount(0);
        transUI.handling(timeout, Tcode.Status.handling);
        emv = new EmvTransaction(para, Type.DEFERRED);
        emv.setTraceNo(TraceNo);
        retVal = emv.start();
        Pan = emv.getCardNo();

        if (retVal == 1 || retVal == 0) {

            //Credito
            if (PAYUtils.isNullWithTrim(emv.getPinBlock())) {
                isPinExist = true;
            }//Cancelo usuario
            else if (emv.getPinBlock().equals("CANCEL")) {
                isPinExist = false;
                retVal = Tcode.T_user_cancel_pin_err;
            } else if (emv.getPinBlock().equals("NULL")) {
                isPinExist = false;
                retVal = Tcode.T_err_pin_null;
            }
            //debito
            else {
                creditCard = "NO";
                isPinExist = true;
            }
            if (isPinExist) {
                if (creditCard.equals("NO"))
                    PIN = emv.getPinBlock();
                setICCData();
                retVal = 0;
                prepareOnline();
            } else {
                transUI.showError(timeout, retVal);
            }
        } else {

            transUI.showError(timeout, retVal);
        }
    }

    private void PBOCTrans() {

        int code = 0;

        PBOCTransProperty property = new PBOCTransProperty();
        property.setTag9c(PBOCTag9c.sale);
        property.setTraceNO(Integer.parseInt(TraceNo));
        property.setFirstEC(false);
        property.setForceOnline(true);
        property.setAmounts(Amount);
        property.setOtherAmounts(0);
        property.setIcCard(false);

        transUI.handling(timeout, Tcode.Status.process_trans);

        emvl2 = new EmvL2Process(this.context, para);
        emvl2.setTraceNo(TraceNo);//JM
        emvl2.setTypeTrans(TransEName);

        if ((retVal = emvl2.emvl2ParamInit()) != 0) {
            switch (retVal) {
                case 1:
                    retVal = Tcode.T_err_not_file_terminal;
                    break;
                case 2:
                    retVal = Tcode.T_err_not_file_processing;
                    break;
                case 3:
                    retVal = Tcode.T_err_not_file_entry_point;
                    break;
            }
            transUI.showError(timeout, retVal);
            return;
        }

        emvl2.SetAmount(Amount, 0);
        emvl2.setTypeCoin(typeCoin);//JM
        code = emvl2.start();

        Logger.debug("EmvL2Process return = " + code);
        if (code != 0) {
            if (code==7){
                retVal=Tcode.T_insert_card;
            }else{
                retVal=Tcode.T_err_detect_card_failed;
            }
            transUI.showError(timeout, retVal);
            return;
        }

        Pan = emvl2.GetCardNo();
        PanSeqNo = emvl2.GetPanSeqNo();
        Track2 = emvl2.GetTrack2data();
        ICCData = emvl2.GetEmvOnlineData();
        MasterControl.HOLDER_NAME = emvl2.getHolderName();
        Logger.error("PAN =" + Pan);

        if (!incardTable(Pan,TransEName)) {
            retVal = Tcode.T_unsupport_card;
            transUI.showError(timeout, Tcode.T_unsupport_card);
            return;
        }

        //Aca deben validarse los cvm

        if (emvl2.GetCVMType() == EmvL2CVM.L2_CVONLINE_PIN) {
            if (CommonFunctionalities.ctlPIN(Pan, timeout, Amount, transUI) != 0) {
                retVal = Tcode.T_user_cancel_input;
                transUI.showError(timeout, retVal);
                return;
            }
        }

        if (emvl2.GetCVMType() == EmvL2CVM.L2_CVOBTAIN_SIGNATURE) {
            MasterControl.CTL_SIGN = true;
        }


        handlePBOCode(PBOCode.PBOC_REQUEST_ONLINE);
    }

    /**
     * handle PBOC transaction
     *
     * @param code
     */
    private void handlePBOCode(int code) {
        if (code != PBOCode.PBOC_REQUEST_ONLINE) {
            transUI.showError(timeout, code);
            return;
        }
        if (inputMode != ENTRY_MODE_NFC)
            setICCDataCTL();

        prepareOnline();
    }

    private boolean requestPin() {

        if (inputMode == ENTRY_MODE_MAG) {

            if (ISOUtil.stringToBoolean(rango.getPIN())) {
                isDebit = true;
            }

            if (isDebit) {
                PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount), Pan);
                if (info.isResultFlag()) {
                    if (info.isNoPin()) {
                        isPinExist = false;
                    } else {
                        if (null == info.getPinblock()) {
                            isPinExist = false;
                        } else {
                            isPinExist = true;
                        }
                        PIN = ISOUtil.hexString(Objects.requireNonNull(info.getPinblock()));
                    }
                    if (isPinExist) {
                        return true;
                    } else {
                        retVal = Tcode.T_user_cancel_pin_err;
                        transUI.showError(timeout, info.getErrno());
                        return false;
                    }
                } else {
                    retVal = Tcode.T_user_cancel_pin_err;
                    transUI.showError(timeout, retVal);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 准备联机
     */
    private void prepareOnline() {

        if ((retVal = CommonFunctionalities.confirmarDatos(timeout, transUI, Pan, IdPreAutAmpl, TransEName)) != 0) {
            retVal = Tcode.T_user_cancel_input;
            return;
        }

        if (!requestPin()) {
            return;
        }

        if (retVal == 0) {

            transUI.handling(timeout, Tcode.Status.connecting_center);
            setDatas(inputMode);
            if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC) {
                retVal = OnlineTrans(emv);
            } else {
                retVal = OnlineTrans(null);
            }
            Logger.debug("SaleTrans>>OnlineTrans=" + retVal);
            clearPan();
            if (retVal == 0) {
                if (typeCoin != null) {
                    switch (typeCoin) {
                        case DOLAR:
                            String authCode = iso8583.getfield(38);
                            transUI.trannSuccess(timeout, Tcode.Status.reprint_exitosa,"APROBADA #" + authCode);
                            break;
                    }
                } else {
                    transUI.trannSuccess(timeout, Tcode.Status.reprint_exitosa, "");
                }
            }

        } else {
            transUI.showError(timeout, retVal);
        }
    }
}
