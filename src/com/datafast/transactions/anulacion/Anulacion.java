package com.datafast.transactions.anulacion;

import android.content.Context;
import android.media.ToneGenerator;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.android.newpos.libemv.PBOCTag9c;
import com.android.newpos.libemv.PBOCTransProperty;
import com.android.newpos.libemv.PBOCode;
import com.android.newpos.pay.StartAppDATAFAST;
import com.datafast.inicializacion.pagoselectronicos.GrupoPagosElectronicos;
import com.datafast.inicializacion.pagoselectronicos.PagosElectronicos;
import com.datafast.menus.menus;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.server.activity.ServerTCP;
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
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;

import java.util.ArrayList;
import java.util.Iterator;

import static cn.desert.newpos.payui.master.MasterControl.incardTable;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREAUTO;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_CTL;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_ICC;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_SWIPE;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_PAGOS_ELECTRONICOS;
import static com.datafast.menus.menus.idAcquirer;
import static com.newpos.libpay.trans.Tcode.T_err_cod;
import static com.newpos.libpay.trans.Tcode.T_search_card_err;
import static com.newpos.libpay.trans.Tcode.T_void_card_not_same;

public class Anulacion extends FinanceTrans implements TransPresenter {

    private TransLogData data;
    private boolean mtransEnableVoid;
    private String info;
    private ArrayList<PagosElectronicos> listPagoElectronico;
    private int index;

    public Anulacion(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        transUI = para.getTransUI();
        isReversal = true;
        isSaveLog = false;
        isProcPreTrans = true;
        isDebit = true;
        isProcPreTrans = true;
        mtransEnableVoid = false;
        index = 0;
        processPPFail = new ProcessPPFail(ctx, iso8583);
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {

        if (ServerTCP.count > 0){
            transUI.showError(timeout, Tcode.T_err_trm,processPPFail);
            return;
        }

        keySecurity = pp_request.getHash();

        if (!checkBatchAndSettle(false,true)){
            return;
        }

        if (!haveTrans())
            return;

        if (!requestTracer()){
            retVal = Tcode.T_user_cancel_input;
            transUI.showError(timeout, retVal, processPPFail);
            return;
        }


        voidGeneral();

        StartAppDATAFAST.getCard = null;
        Logger.debug("VoidTrans>>finish");
    }

    /**
     * Proceso de anulacion generico
     */
    private void voidGeneral() {
        TransLog log = TransLog.getInstance(menus.idAcquirer);
        data = log.searchTransLogByTraceNo(info);

        if (data != null) {
            if (!data.getIsVoided() && transEnableVoid()) {
                if (data.getEName().equals(Type.DEFERRED) || data.getEName().equals(Type.ELECTRONIC_DEFERRED)) {
                    Field54 = data.getField54();
                    Field57 = data.getField57();
                }
                AuthCode = data.getAuthCode();
                if (AuthCode.equals(pp_request.getAuthNumber())){
                    processVoid();
                }else {
                    processErrVoid();
                }
            }
            else {
                processErrVoid();
            }
        } else {
            menus.idAcquirer = idLote + FILE_NAME_PREAUTO;
            log = TransLog.getInstance(idLote + FILE_NAME_PREAUTO);
            data = log.searchTransLogByTraceNo(info);
            if (data != null && !data.getIsVoided() && transEnableVoid() && data.getTransEName().equals(Type.AMPLIACION)) {
                AuthCode = data.getAuthCode();
                ProcCode = data.getProcCode();
                if (AuthCode.equals(pp_request.getAuthNumber())){
                    processVoid();
                }else {
                    processErrVoid();
                }
            } else {
                processErrVoid();
            }
        }
    }

    /**
     * Proceso de anulacion
     */
    private void processVoid() {

        //retVal = transUI.showTransInfo(timeout, data);

        CardInfo cardInfo = null;

        //if (0 == retVal) {

        isFallBack = data.isFallback();

        setFieldsVoid();
        if (!data.isFallback()) {
            if (data.getEntryMode().equals(MODE_MAG + CapPinPOS())) {
                cardInfo = transUI.getCardUse(GERCARD_MSG_SWIPE, timeout, INMODE_MAG, transEname);
            } else if (data.getEntryMode().equals(MODE_ICC + CapPinPOS())) {
                cardInfo = transUI.getCardUse(GERCARD_MSG_ICC, timeout, INMODE_IC, transEname);
            } else if (data.getEntryMode().equals(MODE_CTL + CapPinPOS())) {
                cardInfo = transUI.getCardUse(GERCARD_MSG_CTL, timeout, INMODE_NFC, transEname);
            } else if (data.getEntryMode().equals(MODE_HANDLE + CapPinPOS())) {
                isHandle();
                return;
            } else if (data.getEntryMode().equals("101") || data.getEntryMode().equals("102")) {
                isHandle();
                return;
            } else {
                retVal = Tcode.T_unknow_err;
                transUI.showError(timeout, retVal,processPPFail);
                return;
            }

            if (cardInfo != null)
                afterGetCardUse(cardInfo);

        } else if (data.getEntryMode().equals(MODE1_FALLBACK + CapPinPOS())) {

            cardInfo = transUI.getCardUse(GERCARD_MSG_SWIPE, timeout, INMODE_MAG, transEname);

            if (cardInfo != null) {

                afterGetCardUse(cardInfo);

            } else {
                retVal = T_search_card_err;
                transUI.showError(timeout, T_search_card_err, processPPFail);
            }
        }


        /*} else {
            retVal = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }*/
    }

    /**
     * Permite al usuario confirmar si los datos de la trans que desea
     * anular son correctos
     */
    private boolean confirmVoid(){

        //retVal = transUI.showTransInfo(timeout, data);
        retVal = transUI.showTransInfo(30*1000, data);

        if(0!=retVal){
            retVal = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, Tcode.T_user_cancel_operation,processPPFail);
            return false;
        }
        return true;
    }

    /**
     * Procesa e indica el error cuando no es permitida la anulacion
     */
    private void processErrVoid() {
        if (data != null) {
            if (data.getIsVoided()) {
                retVal = Tcode.T_trans_is_voided;
                transUI.showError(timeout, Tcode.T_trans_is_voided,processPPFail);
            } else if (!mtransEnableVoid) {
                retVal = Tcode.T_not_allow;
                transUI.showError(timeout, retVal,processPPFail);
            } else {
                retVal = Tcode.T_err_void_not_allow;
                transUI.showError(timeout, retVal,processPPFail);
            }

        } else {
            retVal = Tcode.T_not_find_trans;
            transUI.showError(timeout, Tcode.T_not_find_trans,processPPFail);
        }
    }

    /**
     * Verifica si el comercio tiene trans en su lote
     *
     * @return
     */
    private boolean haveTrans() {
        if (!ToolsBatch.statusTrans(idAcquirer) && !ToolsBatch.statusTrans(idLote + FILE_NAME_PREAUTO)) {
            retVal = Tcode.T_err_no_trans;
            transUI.showError(timeout, Tcode.T_err_no_trans,processPPFail);
            return false;
        }

        return true;
    }

    /**
     * Solicita el numero de referencia de la tran que se quiere anular
     *
     * @return
     */
    private boolean requestTracer() {

        if (PAYUtils.isNullWithTrim(pp_request.getAuthNumber())){
            return false;
        }

        if (!PAYUtils.isNullWithTrim(pp_request.getSequential())){
            info = pp_request.getSequential();
            return true;
        }
        return false;
    }

    /**
     * Valida que trans tienen permitido ser anuladas
     *
     * @return
     */
    private boolean transEnableVoid() {
        mtransEnableVoid = true;
        return mtransEnableVoid;
    }

    private void setFieldsVoid() {

        if (data.getProcCode() != null) {
            ProcCode = data.getProcCode();
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
        TipAmount = data.getTipAmout();
        ServiceAmount = data.getAmmountService();
        CashOverAmount = data.getAmmountCashOver();
        montoFijo = data.getMontoFijo();
        Amount = data.getAmount();//DE4

        if (data.getTipoMontoFijo()!=null)
            tipoMontoFijo = data.getTipoMontoFijo();

        if (EntryMode.equals(MODE_ICC + CapPinPOS()))
            setICCData();
        else if (EntryMode.equals(MODE_CTL + CapPinPOS())) {
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

        if (data.getTypeDeferred() != null) {
            TypeDeferred = data.getTypeDeferred();
        }
        if (data.getTraceNo() != null)
            Field62 = data.getTraceNo();//DE62

        if (data.getField63() != null)
            Field63 = data.getField63();//DE63

        if (data.getPanNormal() != null)
            Pan = data.getPanNormal();

        if (data.getPan() != null)
            Pan = data.getPan();

        if (data.getPanPE() != null)
            PanPE = data.getPanPE();

        if (data.getEName() != null)
            TypeTransVoid = data.getEName();

        if (data.getExpDate() != null) {
            ExpDate = data.getExpDate();
        }

        if (data.getAuthCode() != null) {
            AuthCode = data.getAuthCode();
        }

        if (data.getPagoVarioSeleccionado() != null){
            pagoVarioSeleccionado = data.getPagoVarioSeleccionado();
        }

        if (data.getIssuerName() != null){
            issuerName = data.getIssuerName();
        }

        if (data.getLabelCard() != null){
            labelName = data.getLabelCard();
        }

        if (data.getField54()!=null){
            ExtAmount = data.getField54();
        }

        if (data.getField57()!=null){
            Field57 = data.getField57();
        }

        if (data.getField58()!=null){
            Field58 = data.getField58();
        }

        if (data.getField61()!=null){
            Field61 = data.getField61();
        }

        if (data.getToken()!=null){
            TokenElectronic = data.getToken();
        }

        if (data.getOTT()!=null){
            CodOTT = data.getOTT();
        }

        if (data.isMulticomercio()){
            multicomercio = data.isMulticomercio();

            if (data.getIdComercio()!=null){
                idComercio = data.getIdComercio();
            }

            if (data.getNameMultAcq() !=null){
                nameMultAcq = data.getNameMultAcq();
            }
        }
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
            if (info.getErrno() == 0) {
                transUI.showError(timeout, Tcode.T_user_cancel_input, processPPFail);
            }
        }
    }

    private void isICC() {
        String creditCard = "SI";
        para.setAmount(Amount);
        para.setOtherAmount(0);
        transUI.handling(timeout, Tcode.Status.handling);
        emv = new EmvTransaction(para, Trans.Type.ANULACION);
        emv.setTraceNo(TraceNo);
        retVal = emv.start();
        Pan = emv.getCardNo();

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
                } else{
                    transUI.showError(timeout, T_void_card_not_same,processPPFail);
                }

            } else {
                retVal = Tcode.T_user_cancel_pin_err;
                transUI.showError(timeout, Tcode.T_user_cancel_pin_err,processPPFail);
            }

        } else {
            transUI.showError(timeout, retVal,processPPFail);
        }
    }

    private void isNFC() {
        transUI.handling(timeout, Tcode.Status.handling);
        qpboc = new QpbocTransaction(para);
        retVal = qpboc.start();
        if (0 == retVal) {
            String cn = qpboc.getCardNO();
            if (cn == null) {
                transUI.showError(timeout, Tcode.T_qpboc_read_err,processPPFail);
            } else {
                Pan = cn;
                retVal = transUI.showCardConfirm(timeout, cn);
                if (0 == retVal) {
                    PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(Amount), cn);
                    afterQpbocGetPin(info);
                } else {
                    transUI.showError(timeout, Tcode.T_user_cancel_operation,processPPFail);
                }
            }
        } else {
            transUI.showError(timeout, retVal,processPPFail);
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
            transUI.showError(timeout, info.getErrno(),processPPFail);
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
                retVal = T_search_card_err;
            } else {
                String judge = data2.substring(0, data2.indexOf('='));
                if (judge.length() < 13 || judge.length() > 19) {
                    retVal = T_search_card_err;
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
            transUI.showError(timeout, retVal,processPPFail);
        } else {
            if (msgLen == 0) {
                retVal = T_search_card_err;
                transUI.showError(timeout, T_search_card_err,processPPFail);
            } else {

                try {
                    if (!incardTable(data2.substring(0, data2.indexOf('=')), TransEName)) {
                        retVal = Tcode.T_unsupport_card;
                        transUI.showError(timeout, Tcode.T_unsupport_card,processPPFail);
                        return;
                    }
                }catch (IndexOutOfBoundsException e) {
                    retVal = Tcode.T_read_app_data_err;
                    transUI.showError(timeout, Tcode.T_read_app_data_err,processPPFail);
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
                            transUI.showError(timeout, Tcode.T_ic_not_allow_swipe,processPPFail);
                        } else {
                            afterMAGJudge(data1, data2, data3);
                        }
                    } else {
                        retVal = Tcode.T_search_card_err;
                        transUI.showError(timeout, Tcode.T_search_card_err,processPPFail);
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
        else{
            transUI.showError(timeout, T_void_card_not_same,processPPFail);
        }


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
            transUI.showError(timeout, retVal,processPPFail);
            return;
        }
        emvl2.SetAmount(Amount, 0);
        emvl2.setTypeCoin(typeCoin);//JM
        code = emvl2.start();

        Logger.debug("EmvL2Process return = " + code);
        if (code != 0) {
            retVal = Tcode.T_err_detect_card_failed;
            transUI.showError(timeout, Tcode.T_err_detect_card_failed,processPPFail);
            return;
        }

        Pan = emvl2.GetCardNo();
        PanSeqNo = emvl2.GetPanSeqNo();
        Track2 = emvl2.GetTrack2data();
        ICCData = emvl2.GetEmvOnlineData();
        Logger.error("PAN =" + Pan);

        if (!incardTable(Pan, TransEName)) {
            retVal = Tcode.T_unsupport_card;
            transUI.showError(timeout, retVal,processPPFail);
            return;
        }

        if (data.getPanNormal().equals(Pan)) {
            handlePBOCode(PBOCode.PBOC_REQUEST_ONLINE);
        } else {
            transUI.showError(timeout, T_void_card_not_same,processPPFail);
            return;
        }
    }

    /**
     * handle PBOC transaction
     *
     * @param code
     */
    private void handlePBOCode(int code) {
        if (code != PBOCode.PBOC_REQUEST_ONLINE) {
            transUI.showError(timeout, code,processPPFail);
            return;
        }
        if (inputMode != ENTRY_MODE_NFC)
            setICCDataCTL();

        prepareOnline();
    }

    private void llenarListPagosElectronico(){
        listPagoElectronico = new ArrayList<>();
        listPagoElectronico = GrupoPagosElectronicos.GetListaPagosElectronicos(tconf.getGRUPO_PAGOS_ELECTRONICOS(), context);
        if (listPagoElectronico == null){
            listPagoElectronico = new ArrayList<>();
            listPagoElectronico.clear();
        }else  if (listPagoElectronico.isEmpty())
            listPagoElectronico.clear();
    }

    private int procesarSeleccion(String seleccion){
        Iterator<PagosElectronicos> itrPagosElectronicos = listPagoElectronico.iterator();
        while (itrPagosElectronicos.hasNext()){
            PagosElectronicos pagosElectActual = itrPagosElectronicos.next();
            if (seleccion.equals(pagosElectActual.getNOMBRE_PAGO_ELECTRONICO())){
                break;
            }
            index++;
        }

        return index;
    }

    private void isHandle() {

        inputMode = ENTRY_MODE_HAND;

        switch (data.getTransEName()){
            case Type.ELECTRONIC:
            case Type.ELECTRONIC_DEFERRED:

                llenarListPagosElectronico();
                if (listPagoElectronico == null || listPagoElectronico.isEmpty()) {
                    retVal = Tcode.T_not_list_pe;
                    transUI.showError(timeout, retVal,processPPFail);
                    return;
                }

                procesarSeleccion(data.getTypeTransElectronic());

                if (index >= 0) {

                    if ((retVal = CommonFunctionalities.setOTT_Token(timeout, data.getTransEName(),ITEM_PAGOS_ELECTRONICOS,data.getTypeTransElectronic(),
                            Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MINIMA()),
                            Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MAXIMA()),transUI)) != 0) {
                        return;
                    }

                    if (data.getTypeTransElectronic().equals(Type.PAYCLUB)) {
                        if (data.getOTT() != null) {
                            Pan = listPagoElectronico.get(index).getNUM_TARJETA();
                            Pan += CommonFunctionalities.getCodOTT();

                            if (data.getOTT().equals(CommonFunctionalities.getCodOTT())) {
                                inputMode = Integer.parseInt(data.getEntryMode());
                                TypeTransElectronic = data.getTypeTransElectronic();
                                prepareOnline();
                            } else {
                                retVal = T_err_cod;
                                transUI.showError(timeout, T_err_cod,processPPFail);
                            }
                        }else {
                            retVal = T_err_cod;
                            transUI.showError(timeout, T_err_cod,processPPFail);
                        }
                    } else if (data.getTypeTransElectronic().equals(Type.PAYBLUE)) {
                        if (data.getToken() != null) {
                            Pan = listPagoElectronico.get(index).getNUM_TARJETA();
                            Pan += CommonFunctionalities.getCodOTT();

                            if (data.getToken().equals(CommonFunctionalities.getCodOTT())) {
                                inputMode = Integer.parseInt(data.getEntryMode());
                                TypeTransElectronic = data.getTypeTransElectronic();
                                prepareOnline();
                            } else {
                                retVal = T_err_cod;
                                transUI.showError(timeout, T_err_cod,processPPFail);
                            }
                        }else {
                            retVal = T_err_cod;
                            transUI.showError(timeout, T_err_cod,processPPFail);
                        }
                    }
                }else{
                    retVal = Tcode.T_not_allow;
                    transUI.showError(timeout, retVal,processPPFail);
                    return;
                }
                break;
            default:
                if ((retVal = CommonFunctionalities.setPanManual(timeout, data.getTransEName(), transUI)) != 0) {
                    return;
                }

                Pan = CommonFunctionalities.getPan();

                if (!incardTable(Pan, TransEName)) {
                    retVal = Tcode.T_unsupport_card;
                    transUI.showError(timeout, Tcode.T_unsupport_card,processPPFail);
                    return;
                }

                if (data.getPanNormal().equals(Pan)) {

                    if ((retVal = CommonFunctionalities.setFechaExp(timeout, TransEName, transUI, ISOUtil.stringToBoolean(rango.getFECHA_EXP()))) != 0) {
                        return;
                    }

                    ExpDate = CommonFunctionalities.getExpDate();

                    prepareOnline();
                }
                else {
                    retVal = T_void_card_not_same;
                    transUI.showError(timeout, T_void_card_not_same,processPPFail);
                }

                break;
        }
    }

    private void prepareOnline() {

        if (!confirmVoid()){
            return;
        }

        if (data.getTransEName().equals(Type.AMPLIACION)) {
            if ((retVal = CommonFunctionalities.setIdPreAutoAmpliacion(timeout, "ANULACION PREAUTORIZACION", transUI)) != 0) {
                return;
            }

            IdPreAutAmpl = CommonFunctionalities.getIdPreAutoAmpliacion();
        }

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
            data.setLocalDate(PAYUtils.getYear() + LocalDate);
            data.setLocalTime(LocalTime);
            data.setProcCode(ProcCode);
            int index = TransLog.getInstance(menus.idAcquirer).getCurrentIndex(data);
            TransLog.getInstance(menus.idAcquirer).deleteTransLog(index);
            TransLog.getInstance(menus.idAcquirer).saveLog(data, menus.idAcquirer);

            CommonFunctionalities.limpiarPanTarjGasolinera("");

            if (typeCoin != null) {
                switch (typeCoin) {
                    case LOCAL:
                        transUI.trannSuccess(timeout, Tcode.Status.void_succ, "-$. " + PAYUtils.getStrAmount(Amount + TipAmount));
                        break;
                    case DOLAR:
                        transUI.trannSuccess(timeout, Tcode.Status.void_succ, "APROBADA # " + data.getAuthCode());
                        break;
                }
            } else {
                transUI.trannSuccess(timeout, Tcode.Status.void_succ, "");
            }

        } else {
            transUI.showError(timeout, retVal,processPPFail);
        }

    }

}
