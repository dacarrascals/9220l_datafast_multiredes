package com.datafast.transactions.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.InputType;

import com.android.desert.keyboard.InputInfo;
import com.android.newpos.pay.R;
import com.datafast.inicializacion.prompts.Prompt;
import com.datafast.menus.menus;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.menus.menus.NO_FALLBACK;
import static com.newpos.libpay.device.printer.PrintManager.getIdPreAuto;
import static com.newpos.libpay.trans.Trans.ENTRY_MODE_FALLBACK;
import static com.newpos.libpay.trans.Trans.idLote;
import static java.lang.Thread.sleep;

public class CommonFunctionalities {

    private static String Pan;
    private static String codOTT;
    private static String ExpDate;
    private static String Cvv2;
    private static boolean isPinExist;
    private static String PIN;
    private static String idPreAutoAmpliacion;
    private static String numReferencia;
    private static String proCode;
    public static StringBuilder Fld58Prompts;
    public static StringBuilder Fld58PromptsPrinter;
    public static StringBuilder Fld58PromptsAmountPrinter;
    public static boolean multicomercio = false;
    public static String idComercio;
    public static long sumarTotales;
    public static boolean isSumarTotales = false;

    public static String getPan() {
        return Pan;
    }

    public static String getCodOTT() {
        return codOTT;
    }

    public static String getExpDate() {
        return ExpDate;
    }

    public static String getCvv2() {
        return Cvv2;
    }

    public static boolean isIsPinExist() {
        return isPinExist;
    }

    public static String getPIN() {
        return PIN;
    }

    public static String getIdPreAutoAmpliacion() {
        return idPreAutoAmpliacion;
    }

    public static String getNumReferencia() {
        return numReferencia;
    }

    public static String getProCode() {
        return proCode;
    }

    public static String getFld58Prompts() {
        if (Fld58Prompts == null || Fld58Prompts.toString().equals("")) {
            return null;
        }
        return Fld58Prompts.toString();
    }

    public static String getFld58PromptsPrinter() {
        return Fld58PromptsPrinter.toString();
    }

    public static String getFld58PromptsAmountPrinter() {
        return Fld58PromptsAmountPrinter.toString();
    }

    public static boolean isMulticomercio() {
        return multicomercio;
    }

    public static String getIdComercio() {
        return idComercio;
    }

    public static long getSumarTotales() {
        return sumarTotales;
    }

    public static boolean isSumarTotales() {
        return isSumarTotales;
    }

    private static boolean getImg(String img) {
        boolean rta = false;
        switch (img.trim()) {
            case "0"://visa
            case "1"://Master
            case "2"://Amex
            case "3"://Diners
            case "4"://Visa Electron
            case "5"://Maestro
            case "6"://Datafast
                rta = true;
                break;

            default:
                break;
        }
        return rta;
    }

    public static void showCardImage(TransUI transUI) {

        String id_label = rango.getIMAGEN_MOSTRAR();//rango.getNOMBRE_EMISOR();

        if (getImg(id_label)) {
            transUI.showCardImg(id_label);
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static String[] tipoMoneda() {
        String moneda[] = new String[2];
        if (Trans.moneda[0].equals(tconf.getMONEDA())) {
            moneda[0] = "$";
            moneda[1] = FinanceTrans.DOLAR;
        } else if (Trans.moneda[1].equals(tconf.getMONEDA())) {
            moneda[0] = "$";
            moneda[1] = FinanceTrans.DOLAR;
        } else if (Trans.moneda[2].equals(tconf.getMONEDA())) {
            moneda[0] = "$";
            moneda[1] = FinanceTrans.EURO;
        } else {
            moneda[0] = "$";
            moneda[1] = FinanceTrans.DOLAR;
        }
        return moneda;
    }

    public static boolean checkCierre(Context context) {
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fechaActual = new Date();
        String fechaFormat = hourdateFormat.format(fechaActual);
        SharedPreferences prefs = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        String fechaCierre = prefs.getString("fechaSigCierre", null);
        if (fechaCierre != null) {
            Date dateCierre;
            Date dateActual;
            try {
                dateCierre = hourdateFormat.parse(fechaCierre);
                dateActual = hourdateFormat.parse(fechaFormat);
                int rta = dateActual.compareTo(dateCierre);
                if (rta >= 0 && (TransLog.getInstance(idLote).getSize() > 0)) {
                    return false;
                } else if (rta > 0 && (TransLog.getInstance(idLote).getSize() == 0)) {
                    saveDateSettle(context);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            saveDateSettle(context);
        }
        return true;
    }

    public static int tipoEntrada(String tipoDatEnt) {
        int ret = 0;
        switch (tipoDatEnt) {
            case Prompt.NUMERICO:
            case Prompt.MONTO:
                ret = InputType.TYPE_CLASS_NUMBER;
                break;
            case Prompt.ALFA_NUMERICO:
            case Prompt.FECHA:
            case Prompt.CLAVE:
                ret = InputType.TYPE_CLASS_TEXT;
                break;
            default:
                ret = 1;
                break;
        }
        return ret;
    }

    public static int setPanManual(int timeout, String TransEName, TransUI transUI) {

        int ret = 1;

        while (true) {

            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "DIGITE TARJETA", 0,19);

            if (inputInfo.isResultFlag()) {
                Pan = inputInfo.getResult();
                //Falta agregar funcionalidad para verificar el digito de chequeo de la tarjeta
                ret = 0;
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                /*transUI.showError(timeout, ret);*/
                break;
            }
        }

        return ret;
    }

    public static int setOTT_Token(int timeout, String TransEName, String title, String TipoPE, int min, int max,TransUI transUI) {

        int ret = 1;

        while (true) {

            InputInfo inputInfo = null;

            if (TransEName.equals(Trans.Type.ELECTRONIC) || TransEName.equals(Trans.Type.ELECTRONIC_DEFERRED)){
                if (TipoPE.equals(Trans.Type.PAYCLUB))
                    inputInfo = transUI.showInputUser(timeout, title, "CODIGO OTT", min,max);
                else if (TipoPE.equals(Trans.Type.PAYBLUE))
                    inputInfo = transUI.showInputUser(timeout, title, "INGRESE TOKEN", min,max);
            }

            if (inputInfo.isResultFlag()) {
                codOTT = inputInfo.getResult();
                ret = 0;
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                break;
            }
        }

        return ret;
    }

    public static int setFechaExp(int timeout, String TransEName, TransUI transUI, boolean mostrarPantalla) {

        int ret = 1;
        String tmp;

        if (!mostrarPantalla) {
            return 0;
        }

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "FECHA EXPIRACION MM/YY", 0,4);

            if (inputInfo.isResultFlag()) {
                tmp = inputInfo.getResult();
                ExpDate = "";
                try {
                    ExpDate += tmp.substring(2, 4);
                    ExpDate += tmp.substring(0, 2);
                    ret = 0;
                }catch (IndexOutOfBoundsException e){
                    ret = Tcode.T_err_invalid_len;
                    transUI.toasTrans(Tcode.T_err_invalid_len, true, true);
                    continue;
                }
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                /*transUI.showError(timeout, ret);*/
                break;
            }
        }

        return ret;
    }

    public static int setCVV2(int timeout, String TransEName, TransUI transUI, boolean mostrarPantalla) {

        int ret = 1;

        if (!mostrarPantalla) {
            return 0;
        }

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "CODIGO SEGURIDAD CVV2", 0,3);

            if (inputInfo.isResultFlag()) {
                if (inputInfo.getResult().length()==3) {
                    Cvv2 = inputInfo.getResult();
                    ret = 0;
                }else{
                    ret = Tcode.T_err_invalid_len;
                    transUI.toasTrans(Tcode.T_err_invalid_len, true, true);
                    continue;
                }
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                /*transUI.showError(timeout, ret);*/
                break;
            }
        }

        return ret;
    }

    public static int ctlPIN(String pan, int timeout, long amount, TransUI transUI) {
        int ret = 1;
        PinInfo info = transUI.getPinpadOnlinePin(timeout, String.valueOf(amount), pan);
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
                ret = 0;
            }
            if (!isPinExist) {
                ret = Tcode.T_user_cancel_pin_err;
                transUI.showError(timeout, ret);
                return ret;
            }
        } else {
            ret = Tcode.T_user_cancel_pin_err;
            transUI.showError(timeout, ret);
            return ret;
        }
        return ret;
    }

    public static int setPrompt(int timeout, String TransEName, ArrayList<Prompt> prompt, TransUI transUI) {

        int ret = 1;
        Fld58Prompts = new StringBuilder();
        Fld58PromptsPrinter = null;
        Fld58PromptsAmountPrinter = null;
        Fld58PromptsPrinter = new StringBuilder();
        Fld58PromptsAmountPrinter = new StringBuilder();
        sumarTotales = 0;
        isSumarTotales = false;

        //No requiere ningun prompt
        if (prompt == null || prompt.isEmpty()) {
            Fld58Prompts = null;
            return 0;
        }

        Iterator<Prompt> itrPrompts = prompt.iterator();

        while (itrPrompts.hasNext()) {

            int len = 0;
            String datoPrompt = "";
            String datoPromptPrinter = "";
            StringBuilder data = new StringBuilder();
            Prompt promptActual = itrPrompts.next();

            //Transacciones Permitidas
            switch (TransEName) {
                case Trans.Type.VENTA:
                    if (!ISOUtil.stringToBoolean(promptActual.getVENTA())) {
                        ret =  0;
                        continue;
                    }
                    if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())) {
                        if (!ISOUtil.stringToBoolean(promptActual.getVENTA_GASOLINERA())) {
                            ret =  0;
                            continue;
                        }
                    }
                    break;
                case Trans.Type.DEFERRED:
                    if (!ISOUtil.stringToBoolean(promptActual.getDIFERIDO())) {
                        ret =  0;
                        continue;
                    }
                    break;
                case Trans.Type.PAGOS_VARIOS:
                    if (!ISOUtil.stringToBoolean(promptActual.getPAGOS_VARIOS())) {
                        ret =  0;
                        continue;
                    }
                    break;
                case Trans.Type.ELECTRONIC:
                case Trans.Type.ELECTRONIC_DEFERRED:
                    break;

                default:
                    Fld58Prompts = null;
                    return 0;
            }

            InputInfo inputPrompt = transUI.showInputPrompt(timeout, TransEName, "", promptActual);

            if (inputPrompt.isResultFlag()) {

                datoPromptPrinter = inputPrompt.getResult();
                datoPrompt = rellenarPrompt(inputPrompt.getResult(), promptActual);

                len = datoPrompt.length() + 2; //2 de la longitud del codigo

                data.append(ISOUtil.padleft(len + "", 4, '0'));
                data.append(ISOUtil.convertStringToHex(promptActual.getCODIGO_PROMPTS()));
                data.append(ISOUtil.convertStringToHex(datoPrompt));
                Fld58Prompts.append(data.toString());

                if (ISOUtil.stringToBoolean(promptActual.getSUMAR_TOTALES()) &&
                        promptActual.getTIPO_DATO().equals(Prompt.MONTO)){
                    sumarTotales += Long.valueOf(datoPromptPrinter);
                    isSumarTotales = true;

                    datoPromptPrinter = "$"+PAYUtils.getStrAmount(sumarTotales);
                }

                if (promptActual.getCODIGO_PROMPTS().equals("12")) {
                    multicomercio = true;
                    idComercio = datoPromptPrinter;
                }

                setFld58PromptsPrinter(promptActual, datoPromptPrinter);

                ret = 0;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret);
                break;
            }
        }

        return ret;
    }

    public static int setPrompt(int timeout, String TransEName, String title, ArrayList<Prompt> prompt, TransUI transUI) {

        int ret = 1;
        Fld58Prompts = new StringBuilder();
        Fld58PromptsPrinter = null;
        Fld58PromptsAmountPrinter = null;
        Fld58PromptsPrinter = new StringBuilder();
        Fld58PromptsAmountPrinter = new StringBuilder();
        sumarTotales = 0;
        isSumarTotales = false;

        //No requiere ningun prompt
        if (prompt == null || prompt.isEmpty()) {
            Fld58Prompts = null;
            return 0;
        }

        Iterator<Prompt> itrPrompts = prompt.iterator();

        while (itrPrompts.hasNext()) {

            int len = 0;
            String datoPrompt = "";
            String datoPromptPrinter = "";
            StringBuilder data = new StringBuilder();
            Prompt promptActual = itrPrompts.next();

            //Transacciones Permitidas
            switch (TransEName) {
                case Trans.Type.VENTA:
                    if (!ISOUtil.stringToBoolean(promptActual.getVENTA())) {
                        ret =  0;
                        continue;
                    }
                    if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())) {
                        if (!ISOUtil.stringToBoolean(promptActual.getVENTA_GASOLINERA())) {
                            ret =  0;
                            continue;
                        }
                    }
                    break;
                case Trans.Type.DEFERRED:
                    if (!ISOUtil.stringToBoolean(promptActual.getDIFERIDO())) {
                        ret =  0;
                        continue;
                    }
                    break;
                case Trans.Type.PAGOS_VARIOS:
                    if (!ISOUtil.stringToBoolean(promptActual.getPAGOS_VARIOS())) {
                        ret =  0;
                        continue;
                    }
                    break;
                case Trans.Type.ELECTRONIC:
                case Trans.Type.ELECTRONIC_DEFERRED:
                    break;

                default:
                    Fld58Prompts = null;
                    return 0;
            }

            InputInfo inputPrompt = transUI.showInputPrompt(timeout, title, "", promptActual);

            if (inputPrompt.isResultFlag()) {

                datoPromptPrinter = inputPrompt.getResult();
                datoPrompt = rellenarPrompt(inputPrompt.getResult(), promptActual);

                len = datoPrompt.length() + 2; //2 de la longitud del codigo

                data.append(ISOUtil.padleft(len + "", 4, '0'));
                data.append(ISOUtil.convertStringToHex(promptActual.getCODIGO_PROMPTS()));
                data.append(ISOUtil.convertStringToHex(datoPrompt));
                Fld58Prompts.append(data.toString());

                if (ISOUtil.stringToBoolean(promptActual.getSUMAR_TOTALES()) &&
                        promptActual.getTIPO_DATO().equals(Prompt.MONTO)){
                    sumarTotales += Long.valueOf(datoPromptPrinter);
                    isSumarTotales = true;

                    datoPromptPrinter = "$"+PAYUtils.getStrAmount(sumarTotales);
                }

                if (promptActual.getCODIGO_PROMPTS().equals("12")) {
                    multicomercio = true;
                    idComercio = datoPromptPrinter;
                }

                setFld58PromptsPrinter(promptActual, datoPromptPrinter);

                ret = 0;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret);
                break;
            }
        }

        return ret;
    }

    private static String rellenarPrompt(String datoPrompt, Prompt promptActual){
        String prompt;
        switch (promptActual.getTIPO_DATO()) {
            case Prompt.NUMERICO:
            case Prompt.MONTO:
                prompt = ISOUtil.padleft(datoPrompt + "", Integer.parseInt(promptActual.getLONGITUD_MAXIMA()), '0');
                break;
            case Prompt.ALFA_NUMERICO:
            case Prompt.FECHA:
            case Prompt.CLAVE:
                prompt = ISOUtil.padleft(datoPrompt + "", Integer.parseInt(promptActual.getLONGITUD_MAXIMA()), ' ');
                break;
            default:
                prompt = datoPrompt;
                break;
        }
        return prompt;
    }

    private static void setFld58PromptsPrinter(Prompt prompt, String value){

        if (prompt.getTIPO_DATO().equals(Prompt.MONTO)){
            Fld58PromptsAmountPrinter.append(prompt.getNOMBRE_PROMPTS());
            Fld58PromptsAmountPrinter.append(" : ");
            Fld58PromptsAmountPrinter.append(value);
            Fld58PromptsAmountPrinter.append("|");
        }else{
            Fld58PromptsPrinter.append(prompt.getNOMBRE_PROMPTS());
            Fld58PromptsPrinter.append(" : ");
            Fld58PromptsPrinter.append(value);
            Fld58PromptsPrinter.append("|");
        }
    }

    public static int last4card(int timeout, String TransEName, String pan, TransUI transUI, boolean mostrarPantalla) {

        int ret = 1;

        if (!mostrarPantalla)
            return 0;

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "ULTIMOS 4 DIGITOS", 0,4);

            if (inputInfo.isResultFlag()) {
                String last4Pan = pan.substring((pan.length() - 4), pan.length());
                if (last4Pan.equals(inputInfo.getResult())) {
                    ret = 0;
                    break;
                } else {
                    ret = Tcode.T_err_last_4;
                    transUI.toasTrans(Tcode.T_err_last_4, true, true);
                }
            } else {
                ret = Tcode.T_user_cancel_input;
                /*transUI.showError(timeout, ret);*/
                break;
            }
        }

        return ret;
    }

    public static int setIdPreAutoAmpliacion(int timeout, String TransEName, TransUI transUI) {

        int ret = 1;
        int len = 0;
        String datoPrompt = "";
        StringBuilder tmp = new StringBuilder();

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "INGRESE NUMERO UNICO ID PREAUTORIZACION", 0,9);

            if (inputInfo.isResultFlag()) {

                if (inputInfo.getResult().length() > 5 || inputInfo.getResult().length() < 10) {
                    datoPrompt = inputInfo.getResult();
                    len = datoPrompt.length() + 2; //2 de la longitud del codigo
                    tmp.append(ISOUtil.padleft(len + "", 4, '0'));
                    tmp.append(ISOUtil.convertStringToHex("70"));//ID Preautorizacion
                    tmp.append(ISOUtil.convertStringToHex(datoPrompt));
                    idPreAutoAmpliacion = tmp.toString();
                    ret = 0;
                    break;
                } else {
                    transUI.toasTrans(Tcode.T_err_invalid_len, true, true);
                }

            } else {
                ret = Tcode.T_user_cancel_input;
                /*transUI.showError(timeout, ret);*/
                break;
            }
        }

        return ret;
    }

    public static int setNumReferencia(int timeout, String TransEName, TransUI transUI) {

        int ret = 1;

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "NO. REFERENCIA", 0,6);

            if (inputInfo.isResultFlag()) {
                numReferencia = inputInfo.getResult();
                ret = 0;
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret);
                break;
            }
        }

        return ret;
    }

    public static int confirmAmount(int timeout, String transEname, TransUI transUI, long[] montos) {

        int ret = 1;
        StringBuilder msgLabel = new StringBuilder();
        StringBuilder msgAmnt = new StringBuilder();

        long IvaAmount = montos[0];
        long ServiceAmount = montos[1];
        long TipAmount = montos[2];
        long AmountXX = montos[3];
        long AmountBase0 = montos[4];
        long AmountCashOver = montos[5];
        long montoFijo = montos[6];

        if (ISOUtil.stringToBoolean(rango.getTARJETA_CIERRE())){
            IvaAmount = 0;
            ServiceAmount = 0;
            TipAmount = 0;
            AmountBase0 = GetAmount.getMontoTarjetaCierre()[0];
            AmountXX = GetAmount.getMontoTarjetaCierre()[1];
            AmountCashOver = 0;
            montoFijo = 0;
        }

        String dataIva = "<br/>" + "<b>" + tconf.getLABEL_IMPUESTO() + "        :   $ </b>" + PAYUtils.getStrAmount(IvaAmount);
        String dataService = "<br/>" + "<b>"+tconf.getLABEL_SERVICIO()+"   :   $ </b>" + PAYUtils.getStrAmount(ServiceAmount);
        String dataTip = "<br/>" + "<b>"+tconf.getLABEL_PROPINA()+"    :   $ </b>" + PAYUtils.getStrAmount(TipAmount);
        String dataCashOver = "<br/>" + "<b>CASH OVER  :   $ </b>" + PAYUtils.getStrAmount(AmountCashOver);
        String dataMontoFijo = "<br/>" + "<b>MONTO FIJO  :   $ </b>" + PAYUtils.getStrAmount(montoFijo);

        if (!GetAmount.checkIVA())
            dataIva = "";
        if (!GetAmount.checkService())
            dataService = "";
        if (!GetAmount.checkTip())
            dataTip = "";
        if (!transEname.equals(Trans.Type.CASH_OVER))
            dataCashOver = "";
        if (tconf.getVALOR_MONTO_FIJO()!=null) {
            if (!ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO()))
                dataMontoFijo = "";
        }else
            dataMontoFijo = "";


        if (ISOUtil.stringToBoolean(rango.getTARJETA_CIERRE())) {
            msgLabel.append(" ");
            msgAmnt.append("<b>TOTAL        :   $ </b>");
            msgAmnt.append(PAYUtils.getStrAmount(AmountXX + AmountBase0 + IvaAmount + ServiceAmount + TipAmount + AmountCashOver + montoFijo));
        }else{
            msgLabel.append("<b>MONTO      :   $ </b>");
            msgLabel.append(PAYUtils.getStrAmount(AmountXX + AmountBase0) + dataIva + dataService + dataTip + dataCashOver + dataMontoFijo);
            msgLabel.append("<br/>");
            msgLabel.append("<br/>");
            msgAmnt.append("<b>TOTAL        :   $ </b>");
            msgAmnt.append(PAYUtils.getStrAmount(AmountXX + AmountBase0 + IvaAmount + ServiceAmount + TipAmount + AmountCashOver + montoFijo));
        }

        InputInfo inputInfo = transUI.showConfirmAmount(timeout, "CONFIRMAR DATOS DE " + transEname, msgLabel.toString(), msgAmnt.toString(), true);

        if (inputInfo.isResultFlag()) {
            ret = 0;
        } else {
            ret = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, ret);
        }

        return ret;
    }

    public static int confirmAmount(int timeout, String transEname, String title, TransUI transUI, long[] montos) {

        int ret = 1;
        StringBuilder msgLabel = new StringBuilder();
        StringBuilder msgAmnt = new StringBuilder();

        long IvaAmount = montos[0];
        long ServiceAmount = montos[1];
        long TipAmount = montos[2];
        long AmountXX = montos[3];
        long AmountBase0 = montos[4];
        long AmountCashOver = montos[5];
        long montoFijo = montos[6];

        if (ISOUtil.stringToBoolean(rango.getTARJETA_CIERRE())){
            IvaAmount = 0;
            ServiceAmount = 0;
            TipAmount = 0;
            AmountBase0 = GetAmount.getMontoTarjetaCierre()[0];
            AmountXX = GetAmount.getMontoTarjetaCierre()[1];
            AmountCashOver = 0;
            montoFijo = 0;
        }

        String dataIva = "<br/>" + "<b>" + tconf.getLABEL_IMPUESTO() + "        :   $ </b>" + PAYUtils.getStrAmount(IvaAmount);
        String dataService = "<br/>" + "<b>"+tconf.getLABEL_SERVICIO()+"   :   $ </b>" + PAYUtils.getStrAmount(ServiceAmount);
        String dataTip = "<br/>" + "<b>"+tconf.getLABEL_PROPINA()+"    :   $ </b>" + PAYUtils.getStrAmount(TipAmount);
        String dataCashOver = "<br/>" + "<b>CASH OVER  :   $ </b>" + PAYUtils.getStrAmount(AmountCashOver);
        String dataMontoFijo = "<br/>" + "<b>MONTO FIJO  :   $ </b>" + PAYUtils.getStrAmount(montoFijo);

        if (!GetAmount.checkIVA())
            dataIva = "";
        if (!GetAmount.checkService())
            dataService = "";
        if (!GetAmount.checkTip())
            dataTip = "";
        if (!transEname.equals(Trans.Type.CASH_OVER))
            dataCashOver = "";
        if (tconf.getVALOR_MONTO_FIJO()!=null) {
            if (!ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO()))
                dataMontoFijo = "";
        }else
            dataMontoFijo = "";


        if (ISOUtil.stringToBoolean(rango.getTARJETA_CIERRE())) {
            msgLabel.append(" ");
            msgAmnt.append("<b>TOTAL        :   $ </b>");
            msgAmnt.append(PAYUtils.getStrAmount(AmountXX + AmountBase0 + IvaAmount + ServiceAmount + TipAmount + AmountCashOver + montoFijo));
        }else{
            msgLabel.append("<b>MONTO      :   $ </b>");
            msgLabel.append(PAYUtils.getStrAmount(AmountXX + AmountBase0) + dataIva + dataService + dataTip + dataCashOver + dataMontoFijo);
            msgLabel.append("<br/>");
            msgLabel.append("<br/>");
            msgAmnt.append("<b>TOTAL        :   $ </b>");
            msgAmnt.append(PAYUtils.getStrAmount(AmountXX + AmountBase0 + IvaAmount + ServiceAmount + TipAmount + AmountCashOver + montoFijo));
        }

        InputInfo inputInfo = transUI.showConfirmAmount(timeout, "CONFIRMAR DATOS DE " + title, msgLabel.toString(), msgAmnt.toString(), true);

        if (inputInfo.isResultFlag()) {
            ret = 0;
        } else {
            ret = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, ret);
        }

        return ret;
    }

    public static String armarMensaje(String Pan, String IdPreAutAmpl, String trans) {
        StringBuilder mensaje = new StringBuilder();
        String pan = Pan.substring((Pan.length() - 4), Pan.length());

        mensaje.append("<b>TARJETA :        **</b>");
        mensaje.append(pan);
        mensaje.append("<br/>");
        mensaje.append("<b>ID :</b>            ");
        mensaje.append(getIdPreAuto(IdPreAutAmpl));
        mensaje.append("<br/>");
        mensaje.append("<br/>");
        mensaje.append("<br/>");
        mensaje.append("<br/>");
        switch (trans) {
            case Trans.Type.REIMPRESION:
                mensaje.append("<b>REIMPRIMIR?</b>");
                break;
            case Trans.Type.VOID_PREAUTO:
                mensaje.append("<b>ANULAR?</b>");
                break;
        }

        return mensaje.toString();
    }

    public static int confirmarDatos(int timeout, TransUI transUI, String Pan, String IdPreAutAmpl, String trans) {

        int ret = 1;
        InputInfo inputInfo = null;
        switch (trans) {
            case Trans.Type.REIMPRESION:
                inputInfo = transUI.showConfirmAmount(timeout, "REIMPRESION", armarMensaje(Pan, IdPreAutAmpl, trans), "", true);
                break;
            case Trans.Type.VOID_PREAUTO:
                inputInfo = transUI.showConfirmAmount(timeout, "ANULACION PREAUTORIZACION", armarMensaje(Pan, IdPreAutAmpl, trans), "", true);
                break;
            default:
                inputInfo = transUI.showConfirmAmount(timeout, "", armarMensaje(Pan, IdPreAutAmpl, trans), "", true);
                break;
        }

        if (inputInfo.isResultFlag()) {
            ret = 0;
        } else {
            ret = Tcode.T_user_cancel_input;
            /*transUI.showError(timeout, ret);*/
        }
        return ret;
    }

    public static int setTipoCuenta(int timeout, String fld3, TransUI transUI, boolean mostrarPantalla) {

        int ret = 1;

        if (!mostrarPantalla) {
            proCode = fld3;
            return 0;
        }

        InputInfo inputInfo = transUI.showTypeCoin(timeout, "TIPO DE CUENTA");
        if (inputInfo.isResultFlag()) {
            if (inputInfo.getResult().equals("1")) {
                proCode = fld3.replaceFirst("30", "10");
            } else if (inputInfo.getResult().equals("2")) {
                proCode = fld3.replace("30", "20");
            } else {
                proCode = fld3;
            }
            ret = 0;
        } else {
            ret = Tcode.T_user_cancel_input;
            /*transUI.showError(timeout, ret);*/
        }
        return ret;
    }

    public static boolean checkExpDate(String Track2, boolean checkExpDate) {
        String track2, dateCard, dateLocal;
        int yearCard, monCard, yearLocal, monLocal;

        //boolean checkExpDate = Chkoptn.stringToBoolean(Chkoptn.CheckExpDate(issuerRow));

        track2 = Track2.replace('D', '=');
        String tmp2 = track2.substring(track2.indexOf('=') + 1, track2.length());
        dateCard = tmp2.substring(0, 4);
        ExpDate = dateCard;

        if (!checkExpDate)
            return false;


        dateLocal = PAYUtils.getExpDate();
        monLocal = Integer.parseInt(dateLocal.substring(2));
        yearLocal = Integer.parseInt(dateLocal.substring(0, 2));
        yearCard = Integer.parseInt(dateCard.substring(0, 2));
        monCard = Integer.parseInt(dateCard.substring(2));

        if (yearCard > yearLocal) {
            return false;
        } else if (yearCard == yearLocal) {
            if (monCard > monLocal) {
                return false;
            } else return monCard != monLocal;
        } else {
            return true;
        }
    }

    public static void saveSettle(Context context) {
        if (TransLog.getInstance().getSize() == 1 && !getFirtsTrans(context)) {
            saveDateSettle(context);
            saveFirtsTrans(context, true);
        }
    }

    public static void saveFirtsTrans(Context context, boolean flag) {
        SharedPreferences.Editor editor = context.getSharedPreferences("firtsTrans", MODE_PRIVATE).edit();
        editor.putBoolean("firtsTrans", flag);
        editor.apply();
    }

    private static boolean getFirtsTrans(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("firtsTrans", MODE_PRIVATE);
        return prefs.getBoolean("firtsTrans", false);
    }

    public static void saveDateSettle(Context context) {
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fechaActual = new Date();
        String diasCierre = tconf.getDIAS_CIERRE();
        Date fechaCierre = sumarRestarDiasFecha(fechaActual, Integer.valueOf(diasCierre));
        String horasEchoTest = tconf.getHORAS_ECHO();
        Date fechaEchoTest = sumarHorasFecha(fechaActual, Integer.parseInt(horasEchoTest));
        SharedPreferences.Editor editor = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE).edit();
        editor.putString("fechaSigCierre", hourdateFormat.format(fechaCierre));
        editor.putString("fechaUltAct", hourdateFormat.format(fechaActual));
        editor.putString("fechaSigEchoTest", hourdateFormat.format(fechaEchoTest));
        editor.apply();
    }

    public static void saveInyeccionLlaves(Context context, boolean estado){
        SharedPreferences.Editor editor = context.getSharedPreferences("inyeccion-llaves", MODE_PRIVATE).edit();
        editor.putBoolean("stateKeys", estado);
        editor.apply();
    }

    private static Date sumarRestarDiasFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    private static Date sumarHorasFecha(Date fecha, int horas) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.HOUR_OF_DAY, horas);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    public static int fallback(int retVal) {
        int ret = retVal;
        if (ret > 1) {

            if (ret == 124) {//NO AID
                menus.contFallback = ENTRY_MODE_FALLBACK;
                ret = Tcode.T_err_fallback;
            } else {
                menus.contFallback = NO_FALLBACK;
            }
        }
        return ret;
    }

    public static boolean permitirTransGasolinera(String pan){
        if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())){

            if (ISOUtil.stringToBoolean(tconf.getNO_PERMITIR_2_TRANS_MISMO_TARJ())){

                //String bin = pan.substring(0,6);
                if (TMConfig.getInstance().getTransMaxGasolinera().equals(pan)){
                    return false;
                }
                else
                    return true;
            }else
                return true;
        }
        return true;
    }

    public static void obtenerBin(String pan){
        if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())){

            if (ISOUtil.stringToBoolean(tconf.getNO_PERMITIR_2_TRANS_MISMO_TARJ())){

                //String bin = pan.substring(0,6);
                TMConfig.getInstance().setTransMaxGasolinera(pan).save();
            }

        }
    }

    public static void limpiarPanTarjGasolinera(String pan){
        if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())){
            if (ISOUtil.stringToBoolean(tconf.getNO_PERMITIR_2_TRANS_MISMO_TARJ())){
                TMConfig.getInstance().setTransMaxGasolinera(pan).save();
            }
        }
    }

    public static boolean validateCard(int timeout, TransUI transUI){
        boolean ret;
        final int TIMEOUT_REMOVE_CARD = timeout * 1000;

        IccReader iccReader0;
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
        long start = SystemClock.uptimeMillis() ;

        while (true){
            try {
                if (iccReader0.isCardPresent()) {
                    transUI.showMessage("Retire la tarjeta",false);
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);

                    try {
                        sleep(2000);
                    }catch (InterruptedException e) {
                        Logger.error("Exception" + e.toString());
                        Thread.currentThread().interrupt();
                    }

                    /*if (SystemClock.uptimeMillis() - start > TIMEOUT_REMOVE_CARD) {
                        toneG.stopTone();
                        ret = false;
                        break;
                    }*/
                }else {
                    ret = true;
                    break;
                }
            }catch (Exception e){
                ret = true;
                break;
            }

        }
        return ret;
    }


    public static final String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStorageDirectory() +
            File.separator + "download";
    public static String[] instalarApp(Context c) {
        String[] listOfFiles = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS).list();

        String[] ret = new String[listOfFiles.length];

        String packageNameDisco = null;
        String versionNameDisco = null;
        String versionNameAppInstalada = null;

        for (int i = 0; i < listOfFiles.length; i++) {

            String apkEnDisco = listOfFiles[i];


            if (apkEnDisco.endsWith(".apk")) {

                final PackageManager pm = c.getPackageManager();
                String fullPath = DEFAULT_DOWNLOAD_PATH + "/" + apkEnDisco;
                PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);

                try {
                    packageNameDisco = info.packageName;
                    versionNameDisco = info.versionName;
                } catch (Exception e) {
                    ret[i] = apkEnDisco;
                    e.printStackTrace();
                }

                if (packageNameDisco == null){
                    packageNameDisco = "";
                }

                if (!estaInstaladaAplicacion(packageNameDisco.trim(), c)) {

                    File file = new File(DEFAULT_DOWNLOAD_PATH + "/" + apkEnDisco);
                    if (file.exists()) {
                        openFile(c, new File(DEFAULT_DOWNLOAD_PATH + "/" + apkEnDisco));//install apk
                    }

                } else {
                    PackageInfo pinfo = null;
                    try {

                        pinfo = c.getPackageManager().getPackageInfo(packageNameDisco, 0);
                        versionNameAppInstalada = pinfo.versionName;

                        if (!versionNameDisco.equals(versionNameAppInstalada)) {

                            File file = new File(DEFAULT_DOWNLOAD_PATH + "/" + apkEnDisco);

                            if (file.exists()) {
                                openFile(c, new File(DEFAULT_DOWNLOAD_PATH + "/" + apkEnDisco));//install apk
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return ret;
    }

    private static boolean estaInstaladaAplicacion(String nombrePaquete, Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(nombrePaquete, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void openFile(Context context, File file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
