package com.datafast.transactions.pagos_electronicos;

import android.content.Context;
import android.media.ToneGenerator;

import com.android.desert.keyboard.InputInfo;
import com.datafast.inicializacion.pagoselectronicos.GrupoPagosElectronicos;
import com.datafast.inicializacion.pagoselectronicos.PagosElectronicos;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.server.server_tcp.Server;
import com.datafast.transactions.callbacks.waitRspReverse;
import com.datafast.transactions.common.CommonFunctionalities;
import com.datafast.transactions.common.GetAmount;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.contactless.EmvL2Process;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import java.util.ArrayList;
import java.util.Iterator;

import cn.desert.newpos.payui.UIUtils;

import static cn.desert.newpos.payui.master.MasterControl.incardTable;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_TOKEN_PE;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;

public class TransPagosElectronicos extends FinanceTrans implements TransPresenter {

    private InputInfo inputInfo;
    private String title = "PAGOS CON CODIGO";
    public static String tipoPagoElectronico;
    private ArrayList<PagosElectronicos> listPagoElectronico;
    private int index = 0;
    waitRspReverse callbackRsp = null;
    private String aCmd;

    public TransPagosElectronicos(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);

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
        ExpDate = "0000";
        inputMode = ENTRY_MODE_HAND;
        processPPFail = new ProcessPPFail(ctx, iso8583);
        this.aCmd = Server.cmd;
    }

    public static String getTipoPagoElectronico() {
        return tipoPagoElectronico;
    }

    public static void setTipoPagoElectronico(String tipoPagoElectronico) {
        TransPagosElectronicos.tipoPagoElectronico = tipoPagoElectronico;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    @Override
    public void start() {

        if (!checkBatchAndSettle(true, true)) {
            return;
        }

        if (!setAmountPP()) {
            return;
        }

        if (procesarPagoElectronico() == 0) {
            transUI.handling(timeout, Tcode.Status.pago_electronico_exitoso);
        } else {
            //cardInf = null;
            transUI.handlingError(timeout, retVal);
            processPPFail.cmdCancel(Server.cmd, retVal);
            UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Logger.debug("PagoElecTrans>>finish");

        if (retVal != Tcode.T_wait_timeout && retVal != Tcode.T_user_cancel_input && retVal != Tcode.T_unsupport_card) {
            if (aCmd.equals(PP)) {
                callbackRsp = new waitRspReverse() {
                    @Override
                    public void getWaitRspReverse(int status) {
                        retVal = status;
                        if (Reverse() != 0) {
                            if (retVal != Tcode.T_not_reverse) {
                                transUI.showError(timeout, retVal);
                            } else {
                                transUI.showfinish();
                            }
                        } else {
                            transUI.trannSuccess(timeout, Tcode.Status.rev_receive_ok);
                        }
                    }
                };
            }
        }

        if (aCmd.equals(PP)) {
            if (callbackRsp != null) {
                callbackRsp.getWaitRspReverse(retVal);
            }
        }
    }

    private int procesarPagoElectronico() {
        String typeInput;

        llenarListPagosElectronico();

        String[] tipoTransaccion = new String[]{"PAYCLUB", "BDP WALLET"};
        String[] transaccion = new String[]{"VENTA", "DIFERIDOS"};

        if (pp_request.getTypeTrans().equals("06") && pp_request.getProviderOTT().equals("02")) {
            TypeTransElectronic = tipoTransaccion[1];
        } else {
            TypeTransElectronic = tipoTransaccion[0];
        }

        procesarSeleccion(TypeTransElectronic);

        if (index >= 0) {

            if (TypeTransElectronic.equals(listPagoElectronico.get(index).getNOMBRE_PAGO_ELECTRONICO())) {
                transUI.showCardImg(listPagoElectronico.get(index).getIMAGEN());
                if (listPagoElectronico.get(index).getNOMBRE_PAGO_ELECTRONICO().equals(Type.PAYCLUB))
                    TypeTransElectronic = Type.PAYCLUB;
                else if (listPagoElectronico.get(index).getNOMBRE_PAGO_ELECTRONICO().equals(Type.PAYBLUE))
                    TypeTransElectronic = Type.PAYBLUE;
                else {
                    retVal = Tcode.T_not_allow;
                    transUI.showError(timeout, retVal, processPPFail);
                    return retVal;
                }

                TypeTransElectronic = listPagoElectronico.get(index).getNOMBRE_PAGO_ELECTRONICO();
                setTipoPagoElectronico(TypeTransElectronic);
                title = TypeTransElectronic;
            }

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }

            transUI = para.getTransUI();
            if (pp_request.getIdCodDef().equals("00")) {
                typeInput = transaccion[0];
            } else {
                typeInput = transaccion[1];
            }


            if (typeInput.equals("VENTA")) {
                if (processWallet() != 0) {
                    transUI.showError(timeout, retVal, processPPFail);
                    return retVal;
                }
            }

            if (typeInput.equals("DIFERIDOS")) {
                para.setTransType(Type.ELECTRONIC_DEFERRED);
                TransEName = Type.ELECTRONIC_DEFERRED;

                if (!PAYUtils.isNullWithTrim(pp_request.getIdCodDef())) {
                    for (int i = 0; i < deferredType.length; i++) {
                        if (("0" + pp_request.getIdCodDef()).equals(deferredType[i][0])) {
                            TypeDeferred = deferredType[i][1];
                            break;
                        }
                    }
                }

                if (!Cuotas()) {
                    retVal = Tcode.T_user_cancel_operation;
                    transUI.showError(timeout, retVal, processPPFail);
                    return retVal;
                } else {
                    if (processWallet() != 0) {
                        transUI.showError(timeout, retVal, processPPFail);
                        return retVal;
                    }

                }
            }

            if (!incardTable(Pan, TransEName, TypeTransElectronic)) {
                retVal = Tcode.T_unsupport_card;
                transUI.showError(timeout, Tcode.T_unsupport_card, processPPFail);
                return retVal;
            }

            prepareOnline();


        } else {
            retVal = Tcode.T_not_allow;
            transUI.showError(timeout, retVal, processPPFail);
            return retVal;
        }

        return retVal;
    }

    private ArrayList<String> getListMenuType() {
        final ArrayList<String> list = new ArrayList<>();

        for (String[] str : deferredType) {
            list.add(str[1]);
        }
        return list;
    }

    private void llenarListPagosElectronico() {
        listPagoElectronico = new ArrayList<>();
        listPagoElectronico = GrupoPagosElectronicos.GetListaPagosElectronicos(tconf.getGRUPO_PAGOS_ELECTRONICOS(), context);
        if (listPagoElectronico == null) {
            listPagoElectronico = new ArrayList<>();
            listPagoElectronico.clear();
        } else if (listPagoElectronico.isEmpty())
            listPagoElectronico.clear();
    }

    private ArrayList<String> getListMenuDinamic() {
        final ArrayList<String> list = new ArrayList<>();
        Iterator<PagosElectronicos> itrPagosElect = listPagoElectronico.iterator();

        while (itrPagosElect.hasNext()) {
            PagosElectronicos pagosElectActual = itrPagosElect.next();
            list.add(pagosElectActual.getNOMBRE_PAGO_ELECTRONICO());
        }
        return list;
    }

    private int procesarSeleccion(String seleccion) {
        Iterator<PagosElectronicos> itrPagosElectronicos = listPagoElectronico.iterator();
        while (itrPagosElectronicos.hasNext()) {
            PagosElectronicos pagosElectActual = itrPagosElectronicos.next();
            if (seleccion.equals(pagosElectActual.getNOMBRE_PAGO_ELECTRONICO())) {
                break;
            }
            index++;
        }

        return index;
    }

    private ArrayList<String> getListMenu() {
        final ArrayList<String> list = new ArrayList<>();
        list.add("1. PAY CLUB");
        list.add("2. BDP WALLET");
        return list;
    }

    private ArrayList<String> getListMenuAll() {
        final ArrayList<String> list = new ArrayList<>();
        list.add("VENTA");
        list.add("DIFERIDOS");
        return list;
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

//            currency_name = amount.getmCurrencyName();
//            typeCoin = amount.getMtypeCoin();

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
            transUI.showError(timeout, Tcode.T_user_cancel_input, processPPFail);
            return false;
        }
    }

    private int getCodeOTT() {
        if (PAYUtils.isNullWithTrim(pp_request.getOTT())) {

            inputInfo = transUI.showInputUser(timeout, title, "CODIGO OTT", Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MINIMA()), Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MAXIMA()));

            if (!inputInfo.isResultFlag()) {
                retVal = Tcode.T_user_cancel_operation;
                transUI.showError(timeout, retVal, processPPFail);
                return retVal;
            } else {
                Pan = listPagoElectronico.get(index).getNUM_TARJETA();
                Pan += inputInfo.getResult();
                CodOTT = inputInfo.getResult();
                retVal = 0;
            }

        } else {

            Pan = listPagoElectronico.get(index).getNUM_TARJETA();
            Pan += pp_request.getOTT();
            CodOTT = pp_request.getOTT();
            retVal = 0;

        }

        return retVal;
    }

    private int processWallet() {
        String msj;
        if (PAYUtils.isNullWithTrim(pp_request.getOTT())) {

            if (TypeTransElectronic.equals(Type.PAYCLUB)) {
                msj = "CODIGO OTT";
            } else {
                msj = "TOKEN";
            }

            CardInfo cardInfo = transUI.getCardUsePagosElect(GERCARD_MSG_TOKEN_PE + "|" + msj, timeout,
                    INMODE_NFC | INMODE_HAND, transEname, "Monto\nTotal : ", PAYUtils.getStrAmount(Amount),
                    Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MINIMA()),
                    Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MAXIMA()));
            //CardInfo cardInfo = transUI.getCardUse(GERCARD_MSG_TOKEN_PE, timeout, INMODE_NFC | INMODE_HAND, transEname);
            if (cardInfo.isResultFalg()) {
                int type = cardInfo.getCardType();
                String tkn = cardInfo.getToken();
                switch (type) {
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
                if (inputMode == ENTRY_MODE_NFC) {
                    Amount = 1;
                    PBOCTrans();
                }
                if (inputMode == ENTRY_MODE_HAND) {
                    isDebit = false;
                    isHandle(tkn);
                }
            } else {
                retVal = cardInfo.getErrno();
                if (retVal == 0) {
                    retVal = Tcode.T_user_cancel_input;
                }
            }

        } else {
            Pan = listPagoElectronico.get(index).getNUM_TARJETA();
            Pan += pp_request.getOTT();
            if (TypeTransElectronic.equals(Type.PAYCLUB))
                CodOTT = pp_request.getOTT();
            else
                TokenElectronic = pp_request.getOTT();

            retVal = 0;
        }

        return retVal;
    }

    private void PBOCTrans() {

        int code = 0;
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
            transUI.showError(timeout, retVal, processPPFail);
            return;
        }

        emvl2.SetAmount(Amount, 0);
        emvl2.setTypeCoin(typeCoin);//JM
        code = emvl2.start();

        Logger.debug("EmvL2Process return = " + code);
        if ((code != 0)) {
            retVal = Tcode.T_err_detect_card_failed;
            if (emvl2.tkn == null) {
                retVal = Tcode.T_unsupport_card;
            }
            transUI.showError(timeout, retVal, processPPFail);
            return;
        }

        Pan = listPagoElectronico.get(index).getNUM_TARJETA();
        Pan += emvl2.tkn;

        if (TypeTransElectronic.equals(Type.PAYCLUB)) {
            CodOTT = emvl2.tkn;
        } else {
            TokenElectronic = emvl2.tkn;
        }
    }

    private void isHandle(String token) {
        /*if (TypeTransElectronic.equals(Type.PAYCLUB))
            inputInfo = transUI.showInputUser(timeout, title, "CODIGO OTT", Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MINIMA()), Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MAXIMA()));
        else
            inputInfo = transUI.showInputUser(timeout, title, "TOKEN", Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MINIMA()), Integer.parseInt(listPagoElectronico.get(index).getLONGITUD_MAXIMA()));


        if (!inputInfo.isResultFlag()) {
            retVal = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, retVal,processPPFail);
            return;
        }*/

        Pan = listPagoElectronico.get(index).getNUM_TARJETA();
        Pan += token;
        if (TypeTransElectronic.equals(Type.PAYCLUB))
            CodOTT = token;
        else
            TokenElectronic = token;

        retVal = 0;
    }

    private boolean Cuotas() {
        boolean ret = false;
        if (!PAYUtils.isNullWithTrim(pp_request.getLimitDef())) {
            numCuotasDeferred = pp_request.getLimitDef();

            if (numCuotasDeferred.equals("00") || numCuotasDeferred.equals("0")) {
                transUI.toasTrans(Tcode.T_err_invalid_len, true, true);
                processPPFail.cmdCancel(Server.cmd, retVal);
            } else {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * 准备联机
     */
    private boolean prepareOnline() {

        /*if ((retVal = CommonFunctionalities.setPrompt(timeout, TransEName, ITEM_PAGOS_ELECTRONICOS, listPrompts, transUI)) != 0) {
            retVal = Tcode.T_user_cancel_input;
            return false;
        }

        Field58 = CommonFunctionalities.getFld58Prompts();*/
        fild58();

        /*if ((retVal = CommonFunctionalities.confirmAmount(timeout, TransEName, ITEM_PAGOS_ELECTRONICOS, transUI, montos)) != 0) {
            retVal = Tcode.T_user_cancel_input;
            return false;
        }*/

        if (retVal == 0) {

            transUI.handling(timeout, Tcode.Status.connecting_center);
            setDatas(INMODE_HAND);
            if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC) {
                retVal = OnlineTrans(emv);
            } else {
                retVal = OnlineTrans(null);
            }
            Logger.debug("SaleTrans>>OnlineTrans=" + retVal);
            clearPan();
            if (retVal == 0) {
                msgAprob(Tcode.Status.pago_electronico_exitoso, true);
                return true;
            } else {
                transUI.handlingError(timeout, retVal);
                processPPFail.cmdCancel(Server.cmd, retVal);
                return false;
            }
        } else {
            transUI.showError(timeout, retVal, processPPFail);
            return false;
        }
    }
}
