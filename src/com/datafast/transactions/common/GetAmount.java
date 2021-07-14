package com.datafast.transactions.common;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.utils.ISOUtil;

import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.newpos.libpay.trans.Trans.AGREGADO;
import static com.newpos.libpay.trans.Trans.AMOUNT;
import static com.newpos.libpay.trans.Trans.BASE0AMOUNT;
import static com.newpos.libpay.trans.Trans.BASE12AMOUNT;
import static com.newpos.libpay.trans.Trans.CASHOVERAMOUNT;
import static com.newpos.libpay.trans.Trans.DESAGREGADO;
import static com.newpos.libpay.trans.Trans.IVAAMOUNT;
import static com.newpos.libpay.trans.Trans.MANUAL;
import static com.newpos.libpay.trans.Trans.PREGUNTA;
import static com.newpos.libpay.trans.Trans.SERVICEAMOUNT;
import static com.newpos.libpay.trans.Trans.TIPAMOUNT;

//import static com.datafast.menus.menus.acquirerRow;

public class GetAmount {

    private static TransUI mtransUI;
    private static int mtimeOut;
    private String mtypeCoin;
    private long mAmnt;
    private long mAmntBase0;
    private long mAmntXX;
    private long mTipAmnt;
    private long mIVAAmnt;
    private long mServiceAmnt;
    private long mCashOverAmount;
    private int mRetVal;
    private String mCurrencyName;
    //private static String expDate;
    private boolean confirmAmount;
    private String typeTrans;
    private static String titleTrans;
    private static InputInfo inputInfo = null;
    private static long[] montos;

    private static long montoFijo;
    private static String tipoMontoFijo;
    public static long[] montoTarjetaCierre;

    public static final String NO_OPERA = "0";
    public static final String PIDE_CONFIRMACION = "1";
    public static final String AUTOMATICO = "2";
    public static final String RECIBIDO = "3";

    public GetAmount(TransUI transUI, int timeOut, String Pan, String transEname) {
        this.mtransUI = transUI;
        this.mtimeOut = timeOut;
        this.titleTrans = transEname;
        this.typeTrans = transEname;
        this.setConfirmAmount();
        montoTarjetaCierre = new long[2];
    }

    private boolean isConfirmAmount() {
        return confirmAmount;
    }

    private void setConfirmAmount() {
        this.confirmAmount = true;
    }

    public String getMtypeCoin() {
        return mtypeCoin;
    }

    public void setTypeTrans(String typeTrans) {
        this.typeTrans = typeTrans;
    }

    public int getmRetVal() {
        return mRetVal;
    }

    public long getmAmnt() {
        return mAmnt;
    }

    public String getmCurrencyName() {
        return mCurrencyName;
    }

    /*public static String getExpDate() {
        return expDate;
    }*/

    public long getmAmntBase0() {
        return mAmntBase0;
    }

    public long getmAmntXX() {
        return mAmntXX;
    }

    public long getmTipAmnt() {
        return mTipAmnt;
    }

    public long getmIVAAmnt() {
        return mIVAAmnt;
    }

    public long getmServiceAmnt() {
        return mServiceAmnt;
    }

    public long getmCashOverAmount() {
        return mCashOverAmount;
    }

    public static long[] getConfirmarMontos() {
        return montos;
    }

    public static String getTipoMontoFijo() {
        return tipoMontoFijo;
    }

    public static long getmontoFijo() {
        return montoFijo;
    }

    public static long[] getMontoTarjetaCierre() {
        return montoTarjetaCierre;
    }

    /**
     * Lineas comentadas que se traen del piloto mientras se definen los métodos de la base de datos
     *
     * @return
     */
    public boolean setAmount() {

        try {
            long amountLow = Long.parseLong(tconf.getMONTO_MINIMO_TRANSACCION());
            long amountHig = Long.parseLong(tconf.getMONTO_MAXIMO_TRANSACCION());

            while (true) {

                switch (typeTrans) {
                    case Trans.Type.VOID_PREAUTO:
                    case Trans.Type.REIMPRESION:
                    case Trans.Type.PAGOS_VARIOS:
                        if (!inputAmount(AMOUNT)) {
                            mRetVal = Tcode.T_user_cancel_operation;
                            mtransUI.showError(mtimeOut, Tcode.T_user_cancel_operation);
                            return false;
                        }
                        break;
                    default:
                        if (ISOUtil.stringToBoolean(tconf.getTARIFA_CERO()) &&
                                !ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())) {
                            if (!inputAmount(BASE0AMOUNT)) {
                                mRetVal = Tcode.T_user_cancel_operation;
                                mtransUI.showError(mtimeOut, Tcode.T_user_cancel_operation);
                                return false;
                            }
                        }

                        if (!inputAmount(BASE12AMOUNT)) {
                            mRetVal = Tcode.T_user_cancel_operation;
                            mtransUI.showError(mtimeOut, Tcode.T_user_cancel_operation);
                            return false;
                        }

                        if (typeTrans.equals(Trans.Type.CASH_OVER)) {
                            if (!inputAmount(CASHOVERAMOUNT)) {
                                mRetVal = Tcode.T_user_cancel_operation;
                                mtransUI.showError(mtimeOut, Tcode.T_user_cancel_operation);
                                return false;
                            }
                        }
                        break;
                }


                if (mAmntXX + mAmntBase0 < amountLow) {
                    inputInfo = mtransUI.showMessageInfo(titleTrans, "MONTO MENOR AL" + "\n" + "PERMITIDO", "CANCELAR", "REINTENTAR", mtimeOut);
                    if (!inputInfo.isResultFlag()) {
                        return false;
                    } else
                        continue;
                } else if (mAmntXX + mAmntBase0 > amountHig) {
                    inputInfo = mtransUI.showMessageInfo(titleTrans, "MONTO MAYOR AL" + "\n" + "PERMITIDO", "CANCELAR", "REINTENTAR", mtimeOut);
                    if (!inputInfo.isResultFlag()) {
                        return false;
                    } else
                        continue;
                } else {
                    break;
                }
            }
        } catch (NumberFormatException e) {
        }

        switch (typeTrans){
            case Trans.Type.PAGOS_VARIOS:
            case Trans.Type.VOID_PREAUTO:
            case Trans.Type.REIMPRESION:
                break;
            default:
                String tipoEntrada_iva = null;
                String tipoEntrada_servicio = null;

                if (checkIVA() && checkService()) {
                    tipoEntrada_iva = checkIfModoItem(IVAAMOUNT).trim();
                    tipoEntrada_servicio = checkIfModoItem(SERVICEAMOUNT).trim();
                    if (tipoEntrada_iva.equals(DESAGREGADO) && tipoEntrada_servicio.equals(DESAGREGADO)) {
                        mAmnt = mAmntXX;
                        double base12 = Math.round(mAmntXX / 1.22);
                        mAmntXX = (long) base12;
                    }
                }
                if (checkIVA()) {
                    String rta = ingresoCalculoItem(IVAAMOUNT);
                    if (rta.equals(MANUAL)) {
                        if (!inputAmountManual(IVAAMOUNT)) {
                            mRetVal = Tcode.T_user_cancel_operation;
                            mtransUI.showError(mtimeOut, Tcode.T_user_cancel_operation);
                            return false;
                        }
                    } else if (rta.equals("false")) {
                        mRetVal = Tcode.T_amount_not_same;
                        mtransUI.showError(mtimeOut, Tcode.T_amount_not_same);
                        return false;
                    }
                }
                if (checkService()) {
                    String rta = ingresoCalculoItem(SERVICEAMOUNT);
                    if (rta.equals(MANUAL)) {
                        if (!inputAmountManual(SERVICEAMOUNT)) {
                            mRetVal = Tcode.T_user_cancel_operation;
                            mtransUI.showError(mtimeOut, Tcode.T_user_cancel_operation);
                            return false;
                        }
                    } else if (rta.equals("false")) {
                        mRetVal = Tcode.T_amount_not_same;
                        mtransUI.showError(mtimeOut, Tcode.T_amount_not_same);
                        return false;
                    }
                }
                if (checkIVA() && checkService()) {
                    if (tipoEntrada_iva.equals(DESAGREGADO) && tipoEntrada_servicio.equals(DESAGREGADO)) {
                        long total = mAmntXX + mIVAAmnt + mServiceAmnt;
                        mIVAAmnt += mAmnt - total;
                    }
                }
                if (checkTip()) {
                    switch (typeTrans){
                        case Trans.Type.PREVOUCHER:
                            break;

                        default:
                            String rta = ingresoCalculoItem(TIPAMOUNT);
                            if (rta.equals(MANUAL)) {
                                if (!inputAmountManual(TIPAMOUNT)) {
                                    mRetVal = Tcode.T_user_cancel_operation;
                                    mtransUI.showError(mtimeOut, Tcode.T_user_cancel_operation);
                                    return false;
                                }
                            } else if (rta.equals("false")) {
                                mRetVal = Tcode.T_amount_not_same;
                                mtransUI.showError(mtimeOut, Tcode.T_amount_not_same);
                                return false;
                            }
                            break;
                    }
                }
                break;
        }

        setMontos();
        mAmnt = mAmntBase0 + mAmntXX + mIVAAmnt + mTipAmnt + mServiceAmnt + mCashOverAmount;
        return true;
    }

    private boolean inputAmountManual(int ucAmtType) {

        boolean iRet;
        long pulTemp;

        while (true) {

            iRet = inputAmount(ucAmtType);

            if (!iRet) {
                return false;
            }

            if (ucAmtType == IVAAMOUNT) {

                //long IVAPercent = Long.parseLong(UtilsSQLite.getVarFile("EDC", "IVA PERCENT"));
                long IVAPercent = Long.parseLong(tconf.getPORCENTAJE_MAXIMO_IMPUESTO());
                pulTemp = mAmntXX;

                if (IVAPercent <= 0) {
                    mIVAAmnt = 0;
                    return false;
                }

                pulTemp = calcularPorcentaje(pulTemp, IVAPercent);

                if (mIVAAmnt > pulTemp) {
                    inputInfo = mtransUI.showMessageInfo(titleTrans, "IVA MAYOR AL" + "\n" + "PERMITIDO", "CANCELAR", "REINTENTAR", mtimeOut);
                    if (!inputInfo.isResultFlag()) {
                        return false;
                    }
                } else {
                    break;
                }
            } else if (ucAmtType == SERVICEAMOUNT) {

                //long servicioPercent = Long.parseLong(UtilsSQLite.getVarFile("EDC", "SERVICIO PERCENT"));
                long servicioPercent = Long.parseLong(tconf.getPORCENTAJE_MAXIMO_SERVICIO());

                if (servicioPercent <= 0) {
                    mServiceAmnt = 0;
                    return false;
                }

                pulTemp = mAmntXX + mAmntBase0;

                pulTemp = calcularPorcentaje(pulTemp, servicioPercent);

                if (mServiceAmnt > pulTemp) {
                    inputInfo = mtransUI.showMessageInfo(titleTrans, "SERVICIO MAYOR AL" + "\n" + "PERMITIDO", "CANCELAR", "REINTENTAR", mtimeOut);
                    if (!inputInfo.isResultFlag()) {
                        return false;
                    }
                } else {
                    break;
                }

            } else if (ucAmtType == TIPAMOUNT) {

                //long propinaPercent = Long.parseLong(UtilsSQLite.getVarFile("EDC", "PROPINA PERCENT"));
                long propinaPercent = Long.parseLong(tconf.getPORCENTAJE_MAXIMO_PROPINA());

                if (propinaPercent <= 0) {
                    mTipAmnt = 0;
                    return false;
                }

                pulTemp = mAmntXX + mAmntBase0;

                pulTemp = calcularPorcentaje(pulTemp, propinaPercent);

                if (mTipAmnt > pulTemp) {
                    inputInfo = mtransUI.showMessageInfo(titleTrans, "PROPINA MAYOR A LA" + "\n" + "PERMITIDA", "CANCELAR", "REINTENTAR", mtimeOut);
                    if (!inputInfo.isResultFlag()) {
                        return false;
                    }
                } else {
                    break;
                }
            }

        }//end while
        return true;
    }

    private boolean inputAmount(int ucAmtType) {

        switch (ucAmtType) {

            case AMOUNT:
                inputInfo = mtransUI.getOutsideInput(mtimeOut, InputManager.Mode.AMOUNT, "INGRESE MONTO");
                if (inputInfo.isResultFlag()) {
                    mAmntBase0 = Long.parseLong(inputInfo.getResult());
                    montoTarjetaCierre[0] = mAmntBase0;
                } else {
                    return false;
                }
                break;

            case BASE0AMOUNT:

                if (getBase0()) {

                    inputInfo = mtransUI.getOutsideInput(mtimeOut, InputManager.Mode.AMOUNT, "INGRESE MONTO 0");
                    if (inputInfo.isResultFlag()) {
                        mAmntBase0 = Long.parseLong(inputInfo.getResult());
                        montoTarjetaCierre[0] = mAmntBase0;
                    } else {
                        return false;
                    }
                }
                break;

            case BASE12AMOUNT:

                inputInfo = mtransUI.getOutsideInput(mtimeOut, InputManager.Mode.AMOUNT, "INGRESE MONTO " + tconf.getPORCENTAJE_MAXIMO_IMPUESTO());

                if (inputInfo.isResultFlag()) {

                    mAmntXX = Long.parseLong(inputInfo.getResult());
                    montoTarjetaCierre[1] = mAmntXX;
                } else {
                    return false;
                }

                break;

            case IVAAMOUNT:

                inputInfo = mtransUI.getOutsideInput(mtimeOut, InputManager.Mode.AMOUNT, "INGRESE " + tconf.getLABEL_IMPUESTO());
                if (inputInfo.isResultFlag()) {
                    mIVAAmnt = Long.parseLong(inputInfo.getResult());
                } else {
                    return false;
                }
                break;

            case TIPAMOUNT:

                inputInfo = mtransUI.getOutsideInput(mtimeOut, InputManager.Mode.AMOUNT, "INGRESE " + tconf.getLABEL_PROPINA());
                if (inputInfo.isResultFlag()) {
                    mTipAmnt = Long.parseLong(inputInfo.getResult());
                } else {
                    return false;
                }
                break;

            case SERVICEAMOUNT:

                inputInfo = mtransUI.getOutsideInput(mtimeOut, InputManager.Mode.AMOUNT, "INGRESE " + tconf.getLABEL_SERVICIO());
                if (inputInfo.isResultFlag()) {
                    mServiceAmnt = Long.parseLong(inputInfo.getResult());
                } else {
                    return false;
                }
                break;

            case CASHOVERAMOUNT:
                long amountAux2 = 0;

                do {
                    inputInfo = mtransUI.getOutsideInput(mtimeOut, InputManager.Mode.AMOUNT, "CASH OVER");
                    if (inputInfo.isResultFlag()) {
                        amountAux2 = (Long.parseLong(inputInfo.getResult()) % 1000);

                        if (amountAux2 != 0) {
                            mtransUI.toasTrans(Tcode.T_err_amount_cash_over, true, true);
                        } else {
                            mCashOverAmount = Long.parseLong(inputInfo.getResult());
                        }
                    }
                } while (amountAux2 != 0);
                break;
        }

        return true;
    }

    public static boolean getBase0() {
        String rta;
        boolean rsp = false;

        //rta = UtilsSQLite.getVarFile("EDC", "BASE CERO ENABLE");
        rta = tconf.getTARIFA_CERO();
        if (rta.equals("1"))
            rsp = true;

        return rsp;
    }

    public static boolean checkIVA() {
        String rta;
        boolean rsp = false;

        //rta = UtilsSQLite.getVarFile("EDC", "IVA ENABLE");
        rta = tconf.getHABILITAR_IMPUESTO();
        if (rta.equals("1"))
            rsp = true;

        return rsp;
    }

    public static boolean checkService() {
        String rta;
        boolean rsp = false;

        //rta = UtilsSQLite.getVarFile("EDC", "SERVICIO");
        rta = tconf.getHABILITAR_SERVICIO();
        if (rta.equals("1"))
            rsp = true;

        return rsp;
    }

    public static boolean checkTip() {
        String rta;
        boolean rsp = false;

        //rta = UtilsSQLite.getVarFile("EDC", "TIP PROCESS");
        rta = tconf.getHABILITAR_PROPINA();

        if (rta.equals("1"))
            rsp = true;

        return rsp;
    }

    public static boolean checkMontoFijo() {
        String rta;
        boolean rsp = false;

        //rta = UtilsSQLite.getVarFile("EDC", "TIP PROCESS");
        rta = tconf.getHABILITA_MONTO_FIJO();

        if (rta.equals("1"))
            rsp = true;

        return rsp;
    }

    private String checkIfModoItem(int ucTipoMonto) {

        String tipoEntrada = null;

        switch (ucTipoMonto) {
            case SERVICEAMOUNT:
                //tipoEntrada = UtilsSQLite.getVarFile("EDC", "MODO SERVICIO");
                tipoEntrada = tconf.getTIPO_SERVICIO();
                break;

            case TIPAMOUNT:
                //tipoEntrada = UtilsSQLite.getVarFile("EDC", "MODO PROPINA");
                tipoEntrada = tconf.getTIPO_PROPINA();
                break;

            case IVAAMOUNT:
                //tipoEntrada = UtilsSQLite.getVarFile("EDC", "MODO IVA");
                tipoEntrada = tconf.getTIPO_IMPUESTO();
                break;
        }

        return tipoEntrada;
    }

    private long calcularPorcentaje(long monto, long porcentaje) {
        double result = (double) porcentaje / 100;
        double iva = monto * result;
        double ivaFinal = Math.round(iva);
        return (long) ivaFinal;
    }

    private long porcentajeDes(long monto, long porcentaje) {
        double result = (double) monto * 100;
        double result2 = (double) porcentaje + 100;
        double total = result / result2;
        double ivaFinal = Math.round(total);
        return monto - (long) ivaFinal;
    }

    private boolean getIvaAmount(String tipoEntrada) {

        long pulTemp;
        boolean desagregadoEspecial = false;

        if (checkIVA() && checkService()) {
            String tipoEntrada_iva = checkIfModoItem(IVAAMOUNT).trim();
            String tipoEntrada_servicio = checkIfModoItem(SERVICEAMOUNT).trim();
            if (tipoEntrada_iva.equals(DESAGREGADO) && tipoEntrada_servicio.equals(DESAGREGADO)) {
                desagregadoEspecial = true;
            }
        }

        //long IVAPercent = Long.parseLong(UtilsSQLite.getVarFile("EDC", "IVA PERCENT"));
        long IVAPercent = Long.parseLong(tconf.getPORCENTAJE_MAXIMO_IMPUESTO());
        if (IVAPercent <= 0) {
            mIVAAmnt = 0;
            return false;
        }

        pulTemp = mAmntXX;

        if (tipoEntrada.equals(AGREGADO)) {
            mIVAAmnt = calcularPorcentaje(pulTemp, IVAPercent);
        } else if (tipoEntrada.equals(DESAGREGADO)) {

            if (desagregadoEspecial)
                mIVAAmnt = calcularPorcentaje(pulTemp, IVAPercent);
            else
                mIVAAmnt = porcentajeDes(pulTemp, IVAPercent);
        }
        if (tipoEntrada.equals(DESAGREGADO) && !desagregadoEspecial) {
            mAmntXX = mAmntXX - mIVAAmnt;
        }
        return true;
    }

    private boolean getServiceAmount(String tipoEntrada) {

        long pulTemp = 0;
        boolean desagregadoEspecial = false;

        if (checkIVA() && checkService()) {
            String tipoEntrada_iva = checkIfModoItem(IVAAMOUNT).trim();
            String tipoEntrada_servicio = checkIfModoItem(SERVICEAMOUNT).trim();
            if (tipoEntrada_iva.equals(DESAGREGADO) && tipoEntrada_servicio.equals(DESAGREGADO)) {
                desagregadoEspecial = true;
            }
        }

        if (tipoEntrada.equals(AGREGADO))
            pulTemp = mAmntXX + mAmntBase0;
        else
            pulTemp = mAmntXX;

        //long servicioPercent = Long.parseLong(UtilsSQLite.getVarFile("EDC", "SERVICIO PERCENT"));
        long servicioPercent = Long.parseLong(tconf.getPORCENTAJE_MAXIMO_SERVICIO());
        if (servicioPercent <= 0) {
            mServiceAmnt = 0;
            return false;
        }

        if (tipoEntrada.equals(AGREGADO)) {
            mServiceAmnt = calcularPorcentaje(pulTemp, servicioPercent);
        } else if (tipoEntrada.equals(DESAGREGADO)) {

            if (desagregadoEspecial)
                mServiceAmnt = calcularPorcentaje(pulTemp, servicioPercent);
            else
                mServiceAmnt = porcentajeDes(pulTemp, servicioPercent);
        }
        if (tipoEntrada.equals(DESAGREGADO) && !desagregadoEspecial) {
            mAmntXX = mAmntXX - mIVAAmnt;
        }

        return true;
    }

    private boolean getTipAmount(String tipoEntrada) {

        long pulTemp;

        //long propinaPercent = Long.parseLong(UtilsSQLite.getVarFile("EDC", "PROPINA PERCENT"));
        long propinaPercent = Long.parseLong(tconf.getPORCENTAJE_MAXIMO_PROPINA());

        if (propinaPercent <= 0) {
            mTipAmnt = 0;
            return false;
        }

        pulTemp = mAmntXX + mAmntBase0;

        if (tipoEntrada.equals(AGREGADO)) {
            mTipAmnt = calcularPorcentaje(pulTemp, propinaPercent);
        } else if (tipoEntrada.equals(DESAGREGADO)) {
            mTipAmnt = porcentajeDes(pulTemp, propinaPercent);
        }

        return true;
    }

    private String ingresoCalculoItem(int ucAmtType) {

        String tipoEntrada;

        tipoEntrada = checkIfModoItem(ucAmtType);

        switch (tipoEntrada) {

            case PREGUNTA:
            case AGREGADO:
            case DESAGREGADO:

                if (tipoEntrada.equals(PREGUNTA)) {

                    if (ucAmtType == IVAAMOUNT) {

                        inputInfo = mtransUI.showMessageInfo(titleTrans, "¿AGREGAR IVA?", "NO", "SI", mtimeOut);
                        if (inputInfo.isResultFlag()) {
                            tipoEntrada = AGREGADO;
                            if (!getIvaAmount(tipoEntrada)) {
                                return "false";
                            }
                        }

                    } else if (ucAmtType == SERVICEAMOUNT) {

                        //int ret = mtransUI.showCardConfirm(mtimeOut, "AGREGAR SERVICIO ?", titleTrans, "NO", "SI", false);
                        inputInfo = mtransUI.showMessageInfo(titleTrans, "¿AGREGAR SERVICIO?", "NO", "SI", mtimeOut);
                        if (inputInfo.isResultFlag()) {
                            tipoEntrada = AGREGADO;
                            if (!getServiceAmount(tipoEntrada)) {
                                return "false";
                            }
                        }

                    } else if (ucAmtType == TIPAMOUNT) {

                        inputInfo = mtransUI.showMessageInfo(titleTrans, "¿AGREGAR PROPINA?", "NO", "SI", mtimeOut);
                        if (inputInfo.isResultFlag()) {
                            tipoEntrada = AGREGADO;
                            if (!getTipAmount(tipoEntrada)) {
                                return "false";
                            }
                        }
                    }
                    tipoEntrada = "true";
                    return tipoEntrada;
                }

                if (ucAmtType == IVAAMOUNT) {

                    if (!getIvaAmount(tipoEntrada)) {
                        return "false";
                    }

                } else if (ucAmtType == SERVICEAMOUNT) {

                    if (!getServiceAmount(tipoEntrada)) {
                        return "false";
                    }

                } else if (ucAmtType == TIPAMOUNT) {

                    if (!getTipAmount(tipoEntrada)) {
                        return "false";
                    }
                }

                tipoEntrada = "true";

                break;

            case MANUAL:
                break;
        }
        return tipoEntrada;
    }

    /**
     * Arreglo para visualizacion de la pantalla confirmar montos
     */
    private void setMontos() {

        montos = new long[7];
        this.montos[0] = mIVAAmnt;
        this.montos[1] = mServiceAmnt;
        this.montos[2] = mTipAmnt;
        this.montos[3] = mAmntXX;
        this.montos[4] = mAmntBase0;
        this.montos[5] = mCashOverAmount;

        return;
    }

    public static String tipoMontoFijo(){
        String tipo = "";
        switch (rango.getTIPO_MONTO_FIJO()){
            case NO_OPERA:
                tipo = NO_OPERA;
                break;
            case PIDE_CONFIRMACION:
                tipo = PIDE_CONFIRMACION;
                break;
            case AUTOMATICO:
                tipo = AUTOMATICO;
                break;
            case RECIBIDO:
                tipo = RECIBIDO;
                break;
        }
        return tipo;
    }
    public static long getMontoFijo(String tipoMontoFijo){
        montoFijo = 0;

        //switch (rango.getTIPO_MONTO_FIJO()){
        switch (tipoMontoFijo){
            case NO_OPERA:
                break;
            case PIDE_CONFIRMACION:
                break;
            case AUTOMATICO:
                if (tconf.getVALOR_MONTO_FIJO()!=null) {
                    montoFijo = Long.parseLong(tconf.getVALOR_MONTO_FIJO());
                    //montos[6] = montoFijo;
                }
                break;
            case RECIBIDO:
                break;
            default:
                break;
        }

        return montoFijo;
    }

    public static void sumarMontoFijo(){

        switch (tipoMontoFijo()) {
            case PIDE_CONFIRMACION:
                inputInfo = mtransUI.showMessageInfo(titleTrans, "¿AGREGAR MONTO FIJO?", "NO", "SI", mtimeOut);
                if (inputInfo.isResultFlag()) {
                    tipoMontoFijo = AUTOMATICO;
                    montoFijo = getMontoFijo(tipoMontoFijo);
                }else {
                    tipoMontoFijo = NO_OPERA;
                    montoFijo = 0;
                }
                break;
            case NO_OPERA:
                tipoMontoFijo = NO_OPERA;
                montoFijo = 0;
                break;
            case AUTOMATICO:
                tipoMontoFijo = AUTOMATICO;
                montoFijo = getMontoFijo(tipoMontoFijo);
                break;
            case RECIBIDO:
                tipoMontoFijo = RECIBIDO;
                montoFijo = 0;
                break;
            default:
                break;
        }
        //montos[6] = montoFijo;
    }
}
