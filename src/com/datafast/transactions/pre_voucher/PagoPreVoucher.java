package com.datafast.transactions.pre_voucher;

import android.content.Context;
import android.media.ToneGenerator;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.android.newpos.libemv.PBOCTag9c;
import com.android.newpos.libemv.PBOCTransProperty;
import com.android.newpos.libemv.PBOCode;
import com.android.newpos.pay.StartAppDATAFAST;
import com.datafast.tools_bacth.ToolsBatch;
import com.datafast.transactions.common.CommonFunctionalities;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.contactless.EmvL2Process;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.process.EmvTransaction;
import com.newpos.libpay.process.QpbocTransaction;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;

import cn.desert.newpos.payui.UIUtils;

import static cn.desert.newpos.payui.master.MasterControl.callbackFallback;
import static cn.desert.newpos.payui.master.MasterControl.incardTable;
import static com.android.newpos.pay.StartAppDATAFAST.listPrompts;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREVOUCHER;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_CTL;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_ICC;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_SWIPE;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.TITULO_ANULACION;
import static com.datafast.menus.menus.idAcquirer;
import static com.newpos.libpay.trans.Tcode.T_void_card_not_same;

public class PagoPreVoucher extends FinanceTrans implements TransPresenter {

    private TransLogData data;
    private InputInfo info;

    public PagoPreVoucher(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        init(transEname, p);
    }

    private void init(String transEname, TransInputPara p) {
        para = p;
        transUI = para.getTransUI();
        isReversal = true;
        isProcPreTrans = true;
        isSaveLog = true;
        isDebit = true;
        TransEName = transEname;
        currency_name = CommonFunctionalities.tipoMoneda()[0];
        typeCoin = CommonFunctionalities.tipoMoneda()[1];
        host_id = idAcquirer;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {

        if (!CommonFunctionalities.checkCierre(context)) {
            transUI.showError(timeout, Tcode.T_err_batch_full);
            return;
        }

        if (!haveTrans())
            return;

        if (!requestTracer())
            return;

        searchPreVoucher();


        if (retVal == 0) {
            UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
        } else {
            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
        }

        StartAppDATAFAST.getCard = null;
        Logger.debug("PagoPreVoucher>>finish");
    }

    /**
     * Proceso de busqueda del PreVoucher
     */
    private void searchPreVoucher() {
        TransLog log = TransLog.getInstance(idLote + FILE_NAME_PREVOUCHER);
        data = log.searchTransLogByTraceNo(info.getResult());

        if (data != null && !data.getIsVoided()) {
            processPay();
        } else {
            processErrPay();
        }
    }

    /**
     * Proceso de Pago
     */
    private void processPay() {
        retVal = transUI.showTransInfo(timeout, data);

        if (0 == retVal) {

            isFallBack = data.isFallback();

            if(!setFieldsPago())
                return;

            if (!data.isFallback()) {
                CardInfo cardInfo = null;
                if (data.getEntryMode().equals(MODE_MAG + CapPinPOS())) {
                    cardInfo = transUI.getCardUse(GERCARD_MSG_SWIPE, timeout, INMODE_MAG, transEname);
                } else if (data.getEntryMode().equals(MODE_ICC + CapPinPOS())) {
                    cardInfo = transUI.getCardUse(GERCARD_MSG_ICC, timeout, INMODE_IC, transEname);
                } else if (data.getEntryMode().equals(MODE_CTL + CapPinPOS())) {
                    cardInfo = transUI.getCardUse(GERCARD_MSG_CTL, timeout, INMODE_NFC, transEname);
                } else if (data.getEntryMode().equals(MODE_HANDLE + CapPinPOS())) {
                    isHandle();
                    return;
                } else {
                    retVal = Tcode.T_unknow_err;
                    transUI.showError(timeout, retVal);
                    return;
                }

                if (cardInfo != null)
                    afterGetCardUse(cardInfo);

            } else {

                Pan = data.getPanNormal();
                ExpDate = data.getExpDate();

                if (!incardTable(Pan, TransEName)) {
                    retVal = Tcode.T_unsupport_card;
                    transUI.showError(timeout, Tcode.T_unsupport_card);
                    return;
                }

                if (data.getPanNormal().equals(Pan)) {
                    CommonFunctionalities.checkExpDate(data.getPanNormal(), ISOUtil.stringToBoolean(rango.getFECHA_EXP()));
                    prepareOnline();
                } else {
                    retVal = T_void_card_not_same;
                    transUI.showError(timeout, T_void_card_not_same);
                }
            }
        } else {
            retVal = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
    }

    /**
     * Procesa e indica el error cuando no es permitido el pago de PreVoucher
     */
    private void processErrPay() {
        if (data != null) {
            if (data.getIsVoided()) {
                retVal = Tcode.T_trans_is_voided;
                transUI.showError(timeout, Tcode.T_err_prevoucher_already_paid);
            }
        } else {
            retVal = Tcode.T_not_find_trans;
            transUI.showError(timeout, Tcode.T_not_find_trans);
        }
    }

    /**
     * Verifica si el comercio tiene PreVoucher en su lote
     */
    private boolean haveTrans() {
        if (!ToolsBatch.statusTrans(idLote + FILE_NAME_PREVOUCHER)) {
            retVal = Tcode.T_err_no_trans;
            transUI.showError(timeout, Tcode.T_err_no_trans);
            return false;
        }

        return true;
    }

    /**
     * Solicita el numero de referencia del PreVoucher que se quiere pagar
     */
    private boolean requestTracer() {

        info = transUI.getOutsideInput(timeout, InputManager.Mode.VOUCHER, TITULO_ANULACION);

        if (info.isResultFlag())
            return true;
        else {
            retVal = Tcode.T_user_cancel_input;;
            transUI.showError(timeout, retVal);
            return false;
        }
    }

    private boolean setFieldsPago() {

        if (data.getProcCode() != null) {
            ProcCode = ProcCode.substring(0, 2) + data.getProcCode().substring(2);
        }
        if (data.getTraceNo() != null)//DE11
            TraceNo = data.getTraceNo();

        if (data.getLocalTime() != null)
            LocalTime = data.getLocalTime();//DE12

        if (data.getLocalDate() != null)
            LocalDate = data.getLocalDate();//DE13

        if (data.getEntryMode() != null)
            EntryMode = data.getEntryMode();//DE22

        if (data.getTypeCoin() != null)
            typeCoin = data.getTypeCoin();// tipo de moneda de la transaccion

        AmountBase0 = data.getAmmount0();
        AmountXX = data.getAmmountXX();
        IvaAmount = data.getAmmountIVA();

        if (data.isTip()){
            if (data.getTransEName().equals(Type.PREVOUCHER)){
                if(!setPropinaPagoPrevoucher())
                    return false;
            }
        }else {
            TipAmount = data.getTipAmout();
        }
        ServiceAmount = data.getAmmountService();
        Amount = data.getAmount();//DE4

        if (EntryMode == MODE_ICC)
            setICCData();
        else if (EntryMode == MODE_CTL) {
            setICCDataCTL();
        }

        if (data.getNii() != null)
            Nii = data.getNii();//DE24

        if (data.getSvrCode() != null)
            SvrCode = data.getSvrCode();//DE25

        if (!isFallBack) {
            if (data.getTrack2() != null)
                Track2 = data.getTrack2();//DE35
        }

        if (data.getRRN() != null)
            RRN = data.getRRN();//DE37

        if (data.getTermID() != null)
            TermID = data.getTermID();//DE41

        if (data.getMerchID() != null)
            MerchID = data.getMerchID();//DE42

        if (data.getCurrencyCode() != null)
            CurrencyCode = data.getCurrencyCode();//DE49

        if (data.getPIN() != null)
            PIN = data.getPIN();//DE52

        if (data.getTraceNo() != null)
            Field62 = data.getTraceNo();//DE62

        if (data.getField63() != null)
            Field63 = data.getField63();//DE63

        if (data.getPanNormal() != null)
            Pan = data.getPanNormal();

        if (data.getEName() != null)
            TypeTransVoid = data.getEName();

        if (data.getExpDate() != null)
            ExpDate = data.getExpDate();

        return true;
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
                PBOCTrans();
            }
        } else {
            transUI.showError(timeout, info.getErrno());
        }
    }

    private void isICC() {
        String creditCard = "SI";

        transUI.handling(timeout, Tcode.Status.handling);
        emv = new EmvTransaction(para, Type.PAGO_PRE_VOUCHER);
        emv.setTraceNo(TraceNo);
        retVal = emv.start();

        if (1 == retVal || retVal == 0) {
            //Credito
            if (PAYUtils.isNullWithTrim(emv.getPinBlock())) {
                isPinExist = true;
            }//Cancelo usuario
            else if (emv.getPinBlock().equals("CANCEL")) {
                isPinExist = false;
            }//debito
            else {
                creditCard = "NO";
                isPinExist = true;
            }
            if (isPinExist) {
                if (creditCard.equals("NO"))
                    PIN = emv.getPinBlock();

                setICCData();
                if (data.getPanNormal().equals(Pan)) {
                    prepareOnline();
                } else
                    transUI.showError(timeout, T_void_card_not_same);
            } else {
                retVal = Tcode.T_user_cancel_pin_err;
                transUI.showError(timeout, Tcode.T_user_cancel_pin_err);
            }

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
        Pan = cardNo;
        Track1 = data1;
        Track2 = data2;
        Track3 = data3;

        if (data.getPanNormal().equals(Pan))
            prepareOnline();
        else
            transUI.showError(timeout, T_void_card_not_same);

    }

    private void NotNeedCard(TransLogData data) {

        byte[] temp = new byte[128];
        // 卡号
        int len = PAYUtils.get_tlv_data_kernal(0x5A, temp);
        Pan = ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len));
        // 有效期
        len = PAYUtils.get_tlv_data_kernal(0x5F24, temp);
        if (len == 3) {
            ExpDate = ISOUtil.byte2hex(temp, 0, len - 1);
        }
        // 2磁道
        len = PAYUtils.get_tlv_data_kernal(0x57, temp);
        Track2 = ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len));
        // 1磁道
        len = PAYUtils.get_tlv_data_kernal(0x9F1F, temp);
        Track1 = new String(temp, 0, len);
        // 卡序号
        len = PAYUtils.get_tlv_data_kernal(0x5F34, temp);
        PanSeqNo = ISOUtil.padleft(ISOUtil.byte2int(temp, 0, len) + "", 3, '0');
        //55域数据
        temp = new byte[512];
        len = PAYUtils.pack_tags(PAYUtils.wOnlineTags, temp);
        if (len > 0) {
            ICCData = new byte[len];
            System.arraycopy(temp, 0, ICCData, 0, len);
        } else {
            ICCData = null;
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
        Logger.error("PAN =" + Pan);

        if (!incardTable(Pan, TransEName)) {
            retVal = Tcode.T_unsupport_card;
            transUI.showError(timeout, retVal);
            return;
        }

        if (data.getPanNormal().equals(Pan)) {
            handlePBOCode(PBOCode.PBOC_REQUEST_ONLINE);
        } else {
            transUI.showError(timeout, T_void_card_not_same);
            return;
        }
    }

    /**
     * handle PBOC transaction
     *
     * @param code Code
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

    private void isHandle() {
        if ((retVal = CommonFunctionalities.setPanManual(timeout, TransEName, transUI)) != 0) {
            return;
        }

        Pan = CommonFunctionalities.getPan();

        if (!incardTable(Pan, TransEName)) {
            retVal = Tcode.T_unsupport_card;
            transUI.showError(timeout, Tcode.T_unsupport_card);
            return;
        }

        if (data.getPanNormal().equals(Pan))
            prepareOnline();
        else {
            retVal = T_void_card_not_same;
            transUI.showError(timeout, T_void_card_not_same);
        }
    }

    private boolean setPropinaPagoPrevoucher(){
        long pulTemp = 0;
        boolean ret = false;

        while (true) {
            InputInfo inputInfo = transUI.getOutsideInput(timeout, InputManager.Mode.AMOUNT, "INGRESE PROPINA");
            if (inputInfo.isResultFlag()) {
                TipAmount = Long.parseLong(inputInfo.getResult());

                long propinaPercent = Long.parseLong(tconf.getPORCENTAJE_MAXIMO_PROPINA());

                if (propinaPercent <= 0) {
                    TipAmount = 0;
                    retVal = Tcode.T_user_cancel_operation;
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                    ret = false;
                }

                pulTemp = data.getAmmountXX() + data.getAmmount0();

                pulTemp = calcularPorcentaje(pulTemp, propinaPercent);

                if (TipAmount > pulTemp) {
                    inputInfo = transUI.showMessageInfo(data.getTransEName(), "PROPINA MAYOR A LA" + "\n" + "PERMITIDA", "CANCELAR", "REINTENTAR", timeout);
                    if (!inputInfo.isResultFlag()) {
                        retVal = Tcode.T_user_cancel_operation;
                        transUI.showError(timeout, Tcode.T_user_cancel_operation);
                        ret = false;
                    }
                } else {
                    ret = true;
                    break;
                }
            } else {
                retVal = Tcode.T_user_cancel_operation;
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
                ret = false;
            }
        }
        return ret;
    }

    private long calcularPorcentaje(long monto, long porcentaje) {
        double result = (double) porcentaje / 100;
        double iva = monto * result;
        double ivaFinal = Math.round(iva);
        return (long) ivaFinal;
    }

    private void prepareOnline() {

        transLog = TransLog.getInstance(idAcquirer);

        if ((retVal = CommonFunctionalities.setTipoCuenta(timeout, ProcCode, transUI, ISOUtil.stringToBoolean(rango.getTIPO_DE_CUENTA()))) != 0) {
            return;
        }

        ProcCode = CommonFunctionalities.getProCode();

        if ((retVal = CommonFunctionalities.setPrompt(timeout, TransEName, listPrompts, transUI)) != 0) {
            retVal = Tcode.T_user_cancel_input;
            return;
        }

        Field58 = CommonFunctionalities.getFld58Prompts();

        /*if ((retVal = CommonFunctionalities.confirmAmount(timeout, TransEName, transUI, getConfirmarMontos())) != 0) {
            retVal = Tcode.T_user_cancel_input;
            return;
        }*/

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
                data.setVoided(true);
                int index = TransLog.getInstance(idLote + FILE_NAME_PREVOUCHER).getCurrentIndex(data);
                TransLog.getInstance(idLote + FILE_NAME_PREVOUCHER).deleteTransLog(index);
                TransLog.getInstance(idLote + FILE_NAME_PREVOUCHER).saveLog(data, idLote + FILE_NAME_PREVOUCHER);
                if (typeCoin != null) {
                    switch (typeCoin) {
                        case DOLAR:
                            String authCode = iso8583.getfield(38);
                            transUI.trannSuccess(timeout, Tcode.Status.pago_prevoucher_exitoso,"APROBADA #" + authCode);
                            break;
                    }
                } else {
                    transUI.trannSuccess(timeout, Tcode.Status.sale_succ, "");
                }
            }else {
                transUI.showError(timeout, retVal);
            }

            if (callbackFallback != null) {
                callbackFallback.getResponseTransFallback(retVal, null);
            }
        } else {
            transUI.showError(timeout, retVal);
        }
    }

}
