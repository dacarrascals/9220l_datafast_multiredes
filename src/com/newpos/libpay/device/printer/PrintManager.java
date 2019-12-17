package com.newpos.libpay.device.printer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.android.newpos.pay.BuildConfig;
import com.android.newpos.pay.StartAppDATAFAST;
import com.datafast.inicializacion.configuracioncomercio.ChequeoIPs;
import com.datafast.inicializacion.init_emv.CAPK_ROW;
import com.datafast.inicializacion.init_emv.EMVAPP_ROW;
import com.datafast.inicializacion.trans_init.trans.dbHelper;
import com.datafast.printer.PrintParameter;
import com.datafast.tools.UtilNetwork;
import com.datafast.transactions.common.GetAmount;
/*import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;*/
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.trans.translog.TransLogLastSettle;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.config.DevConfig;
import com.pos.device.printer.PrintCanvas;
import com.pos.device.printer.PrintTask;
import com.pos.device.printer.Printer;
import com.pos.device.printer.PrinterCallback;

import org.jpos.stis.TLV_parsing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import cn.desert.newpos.payui.master.MasterControl;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static cn.desert.newpos.payui.transrecord.HistoryTrans.ALL_F_ACUM;
import static cn.desert.newpos.payui.transrecord.HistoryTrans.ALL_F_REDEN;
import static com.android.newpos.pay.StartAppDATAFAST.VERSION;
import static com.android.newpos.pay.StartAppDATAFAST.host_confi;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREAUTO;
import static com.datafast.inicializacion.trans_init.Init.NAME_DB;
import static com.datafast.menus.MenuAction.callBackSeatle;
import static com.datafast.menus.MenuAction.callbackPrint;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.transactions.common.GetAmount.AUTOMATICO;
import static com.newpos.libpay.presenter.TransUIImpl.getErrInfo;
import static com.newpos.libpay.trans.Trans.AGREGADO;
import static com.newpos.libpay.trans.Trans.DESAGREGADO;
import static com.newpos.libpay.trans.Trans.IVAAMOUNT;
import static com.newpos.libpay.trans.Trans.MANUAL;
import static com.newpos.libpay.trans.Trans.MODE_CTL;
import static com.newpos.libpay.trans.Trans.MODE_HANDLE;
import static com.newpos.libpay.trans.Trans.MODE_ICC;
import static com.newpos.libpay.trans.Trans.MODE_MAG;
import static com.newpos.libpay.trans.Trans.PREGUNTA;
import static com.newpos.libpay.trans.Trans.SERVICEAMOUNT;
import static com.newpos.libpay.trans.Trans.TIPAMOUNT;
import static com.newpos.libpay.trans.Trans.deferredType;
import static com.newpos.libpay.trans.Trans.idLote;
import static com.newpos.libpay.trans.finace.FinanceTrans.CapPinPOS;
import static com.newpos.libpay.trans.finace.FinanceTrans.DOLAR;
import static com.newpos.libpay.trans.finace.FinanceTrans.LOCAL;
import static org.jpos.stis.Util.hex2byte;


/**
 * Created by zhouqiang on 2017/3/14.
 *
 * @author zhouqiang
 * 打印管理类
 */
public class PrintManager {

    private static PrintManager mInstance;
    private static TMConfig cfg;
    int num = 0;
    boolean isPrinting = false;
    private static Context mContext;
    private static TransUI transUI;
    private TransLogData dataTrans;
    boolean isICC;
    boolean isNFC;
    boolean isScan;
    boolean isFallback;
    private final int S_SMALL = 15;
    private final int S_MEDIUM = 23;
    private final int S_BIG = 29;
    private final int MAX_CHAR_SMALL = 42;
    private final int MAX_CHAR_MEDIUM = 28;
    private final int MAX_CHAR_BIG = 22;
    private boolean BOLD_ON = true;
    private boolean BOLD_OFF = false;
    private Printer printer = null;
    private PrintTask printTask = null;
    private PackageInfo packageInfo;
    private String host_id;
    private String[] rspField57 = new String[17];
    private String[] identificadoresActivos = new String[25];

    private final int NOMBRE_COMERCIO = 0;
    private final int MID = 1;
    private final int VALOR_FINANCIACION = 2;
    private final int VALOR_TRANS = 3;
    private final int VOUCHER_RAPIDO_FAST_CLUB = 4;
    private final int PUBLICIDAD_MIN = 5;
    private final int PUBLICIDAD_MAY = 6;
    private final int PIN = 7;
    private final int VIGENCIA = 8;
    private final int VOUCHER_RAPIDO_PACIFICARD = 9;
    private final int SECURY_CODE = 10;
    private final int NUM_TEL_RECARGADOR = 11;
    private final int NUM_TRAN_PROVE = 12;
    private final int REDENCION = 13;
    private final int NOMBRE_DUENO_CUENTA = 14;
    private final int VALOR_PAGAR_CONS = 15;
    private final int COD_ERROR = 16;

    private final int ID_001 = 0;
    private final int ID_002 = 1;
    private final int ID_003 = 2;
    private final int ID_004 = 3;
    private final int ID_005 = 4;
    private final int ID_007 = 5;
    private final int ID_008 = 6;
    private final int ID_009 = 7;
    private final int ID_011 = 8;
    private final int ID_012 = 9;
    private final int ID_013 = 10;
    private final int ID_014 = 11;
    private final int ID_015 = 12;
    private final int ID_016 = 13;
    private final int ID_017 = 14;
    private final int ID_018 = 15;
    private final int ID_019 = 16;
    private final int ID_020 = 17;
    private final int ID_021 = 18;
    private final int ID_022 = 19;
    private final int ID_023 = 20;
    private final int ID_025 = 21;


    //Reportes
    private long subTotalSubTotal = 0;
    private long ivaAmountSubTotal = 0;
    private long serviceAmountSubTotal = 0;
    private long tipAmountSubTotal = 0;
    private long montoFijoSubTotal = 0;

    private long totalTempAmount = 0;
    private long totalTempIva = 0;
    private long totalTempServiceAmount = 0;
    private long totalTempTipAmount = 0;
    private long totalTempMontoFijo = 0;

    private long granTotal = 0;
    private long granTotalIva = 0;
    private long granTotalService = 0;
    private long granTotalTip = 0;
    private long granTotalMontoFijo = 0;

    private long amount;
    private long subTotal;
    private long ivaAmount;
    private long serviceAmount;
    private long tipAmount;
    private long montoFijo = 0;

    private long totalAmount = 0;
    private long totalIva = 0;
    private long totalServiceAmount = 0;
    private long totalTipAmount = 0;
    private long totalMontoFijo = 0;

    private int contTransAcq = 0;
    private int contTotalTransAcq = 0;
    private int contTransEmisor = 0;

    private String nombreActualEmisor = "";
    private String fechaTransActual = "";
    private String nombreAdquirenteActual = "";
    private String MID_InterOper = "";
    private boolean soloUnCiclo = false;
    private String[] comercioImpreso;
    private int idxImpresionComercio = 0;
    private boolean omitir = false;

    private boolean printNameIssuer = false;
    private boolean printDateTransxIssuer = false;

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    private PrintManager() {
    }

    public static PrintManager getmInstance(Context c, TransUI tui) {
        mContext = c;
        transUI = tui;
        if (null == mInstance) {
            mInstance = new PrintManager();
        }
        cfg = TMConfig.getInstance();
        //tconf = TCONF.getSingletonInstance();
        return mInstance;
    }

    /**
     * print
     *
     * @param data   dataTrans
     * @param isCopy isCopy
     * @return return
     */
    public int print(final TransLogData data, boolean isCopy, boolean duplicate) {
        int ret = -1;
        String typeTransVoid = null;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        dataTrans = data;
        isICC = data.isICC();
        isNFC = data.isNFC();
        isScan = data.isScan();
        isFallback = data.isFallback();
        int sizeTransLog = -1;

        if (dataTrans.getTypeTransVoid() != null)
            typeTransVoid = dataTrans.getTypeTransVoid();

        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.error("Exception" + e.toString());
        }

        if (typeTransVoid != null && typeTransVoid.equals(Trans.Type.AMPLIACION)) {
            sizeTransLog = TransLog.getInstance(idLote + FILE_NAME_PREAUTO).getSize();
        } else {
            sizeTransLog = TransLog.getInstance(idAcquirer).getSize();
        }

        if (sizeTransLog == 0 && !ISOUtil.stringToBoolean(rango.getTARJETA_CIERRE())) {
            ret = Tcode.T_print_no_log_err;
        } else {
            printer = Printer.getInstance();
            if (printer == null) {
                ret = Tcode.T_sdk_err;
            } else {

                if (dataTrans.isVoided()) {
                    ret = printVoidDatafast(isCopy, duplicate);
                } else if (dataTrans.getEName().equals(Trans.Type.VENTA) ||
                        dataTrans.getEName().equals(Trans.Type.DEFERRED) ||
                        dataTrans.getEName().equals(Trans.Type.PREAUTO) ||
                        dataTrans.getEName().equals(Trans.Type.AMPLIACION) ||
                        dataTrans.getEName().equals(Trans.Type.CONFIRMACION) ||
                        dataTrans.getTransEName().equals(Trans.Type.REIMPRESION) ||
                        dataTrans.getTransEName().equals(Trans.Type.VOID_PREAUTO) ||
                        dataTrans.getEName().equals(Trans.Type.ELECTRONIC) ||
                        dataTrans.getEName().equals(Trans.Type.ELECTRONIC_DEFERRED) ||
                        dataTrans.getEName().equals(Trans.Type.PREVOUCHER) ||
                        dataTrans.getEName().equals(Trans.Type.PAGO_PRE_VOUCHER) ||
                        dataTrans.getEName().equals(Trans.Type.CASH_OVER) ||
                        dataTrans.getEName().equals(Trans.Type.PAGOS_VARIOS)) {
                    ret = printSaleDatafast(false, isCopy, duplicate);
                } else if (dataTrans.getEName().equals(Trans.Type.ANULACION) && (typeTransVoid != null)) {
                    ret = printVoidDatafast(isCopy, duplicate);
                } else if (dataTrans.getEName().equals(Trans.Type.SETTLE) || dataTrans.getEName().equals(Trans.Type.AUTO_SETTLE)) {
                    ret = printReportDatafast(true, false, PrintParameter.isPrintTotals(), false);
                }
            }
        }
        return ret;
    }

    public int printSaleDatafast(boolean isRePrint, boolean isCopy, boolean duplicate) {
        Logger.debug("PrintManager>>start>>printSaleDatafast>>");

        String codeOTT = "";
        String codeToken = "";
        String nombreComercio = "";
        String MIDComercio = "";

        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        printField57(dataTrans.getField57Print());

        if (identificadoresActivos[ID_007].equals("007") || identificadoresActivos[ID_017].equals("017") || identificadoresActivos[ID_018].equals("018")) {
            nombreComercio = checkNull(rspField57[NOMBRE_COMERCIO]);
            MIDComercio = checkNull(rspField57[MID]);
        } else {
            nombreComercio = checkNull(tconf.getNOMBRE_COMERCIO());
            MIDComercio = checkNull(tconf.getCARD_ACCP_MERCH());
        }

        printHeader(nombreComercio, checkNull(tconf.getRUC()),
                checkNull(tconf.getDIRECCION_PRINCIPAL()), checkNull(tconf.getTELEFONO_COMERCIO()), paint, canvas);

        setTextPrint(setCenterText(checkNull(tconf.getCIUDAD()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        if (!isCopy) {
            setTextPrint(setCenterText(MIDComercio + " - " + checkNull(tconf.getCARD_ACCP_TERM()) + " - "
                    + VERSION + " - " + checkNull(typeEntryPoint()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (!dataTrans.getEName().equals(Trans.Type.ELECTRONIC) && !dataTrans.getEName().equals(Trans.Type.ELECTRONIC_DEFERRED)) {
            setTextPrint(setCenterText(checkNull(dataTrans.getLabelCard()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            println(paint, canvas);
        }

        switch (dataTrans.getEName()) {
            case Trans.Type.PREAUTO:
                setTextPrint(setCenterText("PREAUTORIZACION", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                setTextPrint(setCenterText("ID: " + checkNull(dataTrans.getField58()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                println(paint, canvas);
                break;
            case Trans.Type.CONFIRMACION:
            case Trans.Type.AMPLIACION:
            case Trans.Type.REIMPRESION:
            case Trans.Type.VOID_PREAUTO:
                setTextPrint(setCenterText(checkNull(dataTrans.getEName()) + " DE", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                setTextPrint(setCenterText("PREAUTORIZACION", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                setTextPrint(setCenterText("ID: " + checkNull(getIdPreAuto(dataTrans.getIdPreAutAmpl())), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                println(paint, canvas);
                break;
            case Trans.Type.ELECTRONIC:
            case Trans.Type.ELECTRONIC_DEFERRED:
                if (dataTrans.getTypeTransElectronic().equals(Trans.Type.PAYCLUB)) {
                    //setTextPrint(setCenterText("PAYCLUB MOVIL", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                    setTextPrint(setCenterText(checkNull(dataTrans.getLabelCard()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                    println(paint, canvas);
                    codeOTT = dataTrans.getOTT();
                } else if (dataTrans.getTypeTransElectronic().equals(Trans.Type.PAYBLUE)) {
                    //setTextPrint(setCenterText("BDP WALLET", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                    setTextPrint(setCenterText(checkNull(dataTrans.getLabelCard()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                    println(paint, canvas);
                    codeToken = dataTrans.getToken();
                }
                break;
        }

        if (dataTrans.getEName().equals(Trans.Type.ELECTRONIC) || dataTrans.getEName().equals(Trans.Type.ELECTRONIC_DEFERRED)) {
            switch (dataTrans.getTypeTransElectronic()) {
                case Trans.Type.PAYCLUB:
                    print_CardInfo(dataTrans.getPan(), dataTrans.getEName(), "OTT: " + codeOTT, paint, canvas, isCopy);
                    break;
                case Trans.Type.PAYBLUE:
                    print_CardInfo(dataTrans.getPan(), dataTrans.getEName(), "TOKEN: " + codeToken, paint, canvas, isCopy);
                    break;
            }
        } else {
            print_CardInfo(dataTrans.getPan(), dataTrans.getEName(), dataTrans.getExpDate(), paint, canvas, isCopy);
        }

        print_Lote_Ref(checkNull(dataTrans.getBatchNo()), checkNull(dataTrans.getTraceNo()), paint, canvas);

        if (!dataTrans.getEName().equals(Trans.Type.PREVOUCHER) &&
                !dataTrans.getEName().equals(Trans.Type.PAGO_PRE_VOUCHER)) {
            if (dataTrans.getField44() != null)
                print_Acquirer(checkNull(dataTrans.getField44()), checkNull(dataTrans.getMID_InterOper()),paint, canvas, isCopy);
            else
                print_Acquirer(checkNull(dataTrans.getIssuerName()), checkNull(dataTrans.getMID_InterOper()),paint, canvas, isCopy);
        }

        print_DateAndTime(dataTrans, paint, canvas, false);

        switch (dataTrans.getEName()) {
            case Trans.Type.PAGOS_VARIOS:
                setTextPrint(checkNull(dataTrans.getPagoVarioSeleccionadoNombre()), paint, BOLD_ON, canvas, S_SMALL);
                break;
            case Trans.Type.DEFERRED:
            case Trans.Type.ELECTRONIC_DEFERRED:
                for (int i = 0; i < deferredType.length; i++) {
                    if (dataTrans.getField57().substring(0, 3).equals(deferredType[i][0])) {
                        String typeDef = deferredType[i][1];
                        if (typeDef.length()>=16)
                            setTextPrint(setTextColumn("DIF " + checkNull(typeDef.substring(0,14)), "MESES: " + checkNull(dataTrans.getField57().substring(6, 8)), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                        else
                            setTextPrint(setTextColumn("DIF " + checkNull(typeDef), "MESES: " + checkNull(dataTrans.getField57().substring(6, 8)), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

                    }
                }
                break;
            case Trans.Type.CONFIRMACION:
                printField59(dataTrans.getField59Print(), paint, canvas);
                break;
            case Trans.Type.REIMPRESION:
            case Trans.Type.VOID_PREAUTO:
                duplicado(isRePrint, paint, canvas);
                mensajeAprobacion(paint, canvas, isCopy);
                printAmountVoidDatafast(paint, canvas);
                println(paint, canvas);
                datosTarjetaChip(isCopy, paint, canvas);
                break;
        }

        //printAID(checkNull(dataTrans.getAID()), paint, canvas);

        switch (dataTrans.getEName()) {
            case Trans.Type.REIMPRESION:
            case Trans.Type.VOID_PREAUTO:
                originalCopia(isCopy, paint, canvas);
                break;
            case Trans.Type.PREVOUCHER:
                setTextPrint(setCenterText("PREVOUCHER", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                printAmountDatafast(dataTrans.getAmmount0(), dataTrans.getAmmountXX(), dataTrans.getAmmountIVA(), dataTrans.getTipAmout(), dataTrans.getAmmountService(), dataTrans.getAmmountCashOver(), dataTrans.getMontoFijo(),paint, canvas);
                printFieldsPreVoucher(paint, canvas);
                originalCopia(isCopy, paint, canvas);
                break;
            default:
                mensajeAprobacion(paint, canvas, isCopy);
                printAmountDatafast(dataTrans.getAmmount0(), dataTrans.getAmmountXX(), dataTrans.getAmmountIVA(), dataTrans.getTipAmout(), dataTrans.getAmmountService(), dataTrans.getAmmountCashOver(), dataTrans.getMontoFijo(), paint, canvas);
                println(paint, canvas);

                //Debe ir el prompt pertinente
                printPrompts(dataTrans, paint, canvas);

                printHistory(isCopy, duplicate, paint, canvas, dataTrans.getField57Print());
                println(paint, canvas);
                break;
        }

        int ret = printData(canvas);

        if (printer != null) {
            printer = null;
        }

        return ret;
    }

    public int printVoidDatafast(boolean isCopy, boolean duplicate) {
        //Logger.debug("PrintManager>>start>>printVoidDatafast>>");

        String nombreComercio = "";
        String MIDComercio = "";
        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        if (dataTrans.isMulticomercio())
            printHeader(checkNull(dataTrans.getNameMultAcq()), checkNull(tconf.getRUC()), checkNull(tconf.getDIRECCION_PRINCIPAL()), checkNull(tconf.getTELEFONO_COMERCIO()), paint, canvas);
        else
            printHeader(checkNull(tconf.getNOMBRE_COMERCIO()), checkNull(tconf.getRUC()), checkNull(tconf.getDIRECCION_PRINCIPAL()), checkNull(tconf.getTELEFONO_COMERCIO()), paint, canvas);

        setTextPrint(setCenterText(checkNull(tconf.getCIUDAD()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        if (!isCopy) {
            if (dataTrans.isMulticomercio()) {
                setTextPrint(setCenterText(checkNull(dataTrans.getMerchID()) + " - " + checkNull(tconf.getCARD_ACCP_TERM()) + " - "
                        + VERSION + " - " + typeEntryPoint(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            }else{
                setTextPrint(setCenterText(checkNull(tconf.getCARD_ACCP_MERCH()) + " - " + checkNull(tconf.getCARD_ACCP_TERM()) + " - "
                        + VERSION + " - " + typeEntryPoint(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            }
        }


        setTextPrint(setCenterText(checkNull(dataTrans.getLabelCard()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);

        if (dataTrans.getTypeTransVoid() != null && dataTrans.getTypeTransVoid().equals(Trans.Type.AMPLIACION)) {
            setTextPrint(setCenterText(dataTrans.getTypeTransVoid() + " DE", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            setTextPrint(setCenterText("PREAUTORIZACION", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            setTextPrint(setCenterText("ID: " + getIdPreAuto(dataTrans.getIdPreAutAmpl()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            println(paint, canvas);
        }

        if (dataTrans.getEName().equals(Trans.Type.ANULACION)){
            if (dataTrans.getTypeTransVoid().equals(Trans.Type.ELECTRONIC) || dataTrans.getTypeTransVoid().equals(Trans.Type.ELECTRONIC_DEFERRED)) {
                switch (dataTrans.getTypeTransElectronic()) {
                    case Trans.Type.PAYCLUB:
                        print_CardInfo(dataTrans.getPanPE(), dataTrans.getTypeTransVoid(), "OTT: " + dataTrans.getOTT(),paint, canvas, isCopy);
                        break;
                    case Trans.Type.PAYBLUE:
                        print_CardInfo(dataTrans.getPanPE(), dataTrans.getTypeTransVoid(), "TOKEN: " + dataTrans.getToken(), paint, canvas, isCopy);
                        break;
                }
            } else {
                print_CardInfo(dataTrans.getPan(), dataTrans.getEName(), dataTrans.getExpDate(), paint, canvas, isCopy);
            }
        }else{
            if (dataTrans.getEName().equals(Trans.Type.ELECTRONIC) || dataTrans.getEName().equals(Trans.Type.ELECTRONIC_DEFERRED)) {
                switch (dataTrans.getTypeTransElectronic()) {
                    case Trans.Type.PAYCLUB:
                        print_CardInfo(dataTrans.getPan(), dataTrans.getEName(), "OTT: " + dataTrans.getOTT(),paint, canvas, isCopy);
                        break;
                    case Trans.Type.PAYBLUE:
                        print_CardInfo(dataTrans.getPan(), dataTrans.getEName(), "TOKEN: " + dataTrans.getToken(), paint, canvas, isCopy);
                        break;
                }
            } else {
                print_CardInfo(dataTrans.getPan(), dataTrans.getEName(), dataTrans.getExpDate(), paint, canvas, isCopy);
            }
        }


        //print_CardInfo(dataTrans.getPan(), dataTrans.getEName(), dataTrans.getExpDate(), paint, canvas, isCopy);
        print_Lote_Ref(dataTrans.getBatchNo(), dataTrans.getTraceNo(), paint, canvas);

        //if (!dataTrans.getEName().equals(Trans.Type.ANULACION)) {
        if (!isCopy){
            if (dataTrans.getField44() != null){
                print_Acquirer(dataTrans.getField44(), dataTrans.getMID_InterOper(), paint, canvas, false);
            }
            else{
                print_Acquirer(dataTrans.getIssuerName(), dataTrans.getMID_InterOper(), paint, canvas, false);
            }
        }

        //}

        print_DateAndTime(dataTrans, paint, canvas, false);

        println(paint, canvas);

        if (duplicate){
            setTextPrint(setCenterText("***DUPLICADO***", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }

        setTextPrint(setCenterText("ANULACION", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        //println(paint, canvas);

        mensajeAprobacion(paint, canvas, isCopy);

        printAmountVoidDatafast(paint, canvas);

        println(paint, canvas);

        if (isICC || isNFC) {
            if (!isCopy){
                printDataCARD(paint, canvas, true);
            }
        }

        println(paint, canvas);

        if (isCopy) {
            setTextPrint(setCenterText("- CLIENTE -", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setCenterText("- ORIGINAL -", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        println(paint, canvas);

        printField57(dataTrans.getField57Print());
        if (identificadoresActivos[ID_009].equals("009")) {
            if (rspField57[PUBLICIDAD_MIN].length() > 40) {
                setTextPrint(rspField57[PUBLICIDAD_MIN].substring(0, 40), paint, BOLD_ON, canvas, S_SMALL);
                if (rspField57[PUBLICIDAD_MIN].length() > 80) {
                    setTextPrint(rspField57[PUBLICIDAD_MIN].substring(40, 80), paint, BOLD_ON, canvas, S_SMALL);
                    setTextPrint(rspField57[PUBLICIDAD_MIN].substring(80), paint, BOLD_ON, canvas, S_SMALL);
                } else {
                    setTextPrint(rspField57[PUBLICIDAD_MIN].substring(40), paint, BOLD_ON, canvas, S_SMALL);
                }
            } else {
                setTextPrint(rspField57[PUBLICIDAD_MIN], paint, BOLD_ON, canvas, S_SMALL);
            }
        }

        int ret = printData(canvas);

        if (printer != null) {
            printer = null;
        }

        return ret;
    }

    public int printReportDatafast(boolean isSettle, boolean isDeleteLote, boolean printTotals, boolean usarCallback) {

        Logger.debug("PrintManager>>start>>printLogout>>");
        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        if (printTotals) {
            setTextPrint(setCenterText("RESUMEN DE VENTAS", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            printHeader(checkNull(tconf.getNOMBRE_COMERCIO()), checkNull(tconf.getRUC()), checkNull(tconf.getDIRECCION_PRINCIPAL()), tconf.getTELEFONO_COMERCIO(), paint, canvas);

            setTextPrint(setCenterText(checkNull(tconf.getCIUDAD()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            println(paint, canvas);
            println(paint, canvas);

            //setTextPrint(setCenterText("CAP ELECT DATAFAST", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            capturaElectronica(paint, canvas);
            //setTextPrint(setCenterText(checkNull(tconf.getLINEA_AUX()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            println(paint, canvas);
            setTextPrint(setTextColumn("COMERCIO: " + checkNull(tconf.getCARD_ACCP_MERCH()), "TID: " + checkNull(tconf.getCARD_ACCP_TERM()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(setTextColumn("LOTE#: " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), StartAppDATAFAST.VERSION, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            print_DateAndTime(null, paint, canvas, true);

            println(paint, canvas);
            setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);

            if (TransLog.getInstance(idAcquirer).getData().size() == 0) {
                println(paint, canvas);
                setTextPrint(setCenterText("SIN TRANSACCIONES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                setTextPrint(setCenterText("CONVENCIONALES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

                println(paint, canvas);
                setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);
            } else {
                printAllData(paint, canvas, idAcquirer);
            }

            List<TransLogData> listPreAuto = TransLog.getInstance(idAcquirer + FILE_NAME_PREAUTO).getData();
            if (listPreAuto.size() > 0) {
                printAllData(paint, canvas, idAcquirer + FILE_NAME_PREAUTO);
            }
        }

        printMessageSettle(paint, canvas);

        if (isSettle || isDeleteLote) {

            println(paint, canvas);
            println(paint, canvas);

            setTextPrint(setCenterText("RESUMEN DE VENTAS", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            printHeader(checkNull(tconf.getNOMBRE_COMERCIO()), checkNull(tconf.getRUC()), checkNull(tconf.getDIRECCION_PRINCIPAL()), checkNull(tconf.getTELEFONO_COMERCIO()), paint, canvas);

            setTextPrint(setCenterText(checkNull(tconf.getCIUDAD()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            println(paint, canvas);

            capturaElectronica(paint, canvas);

            println(paint, canvas);

            setTextPrint(setTextColumn("COMERCIO: " + checkNull(tconf.getCARD_ACCP_MERCH()), "TID: " + checkNull(tconf.getCARD_ACCP_TERM()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            setTextPrint(setTextColumn("LOTE#: " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), StartAppDATAFAST.VERSION, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            print_DateAndTime(null, paint, canvas, true);

            println(paint, canvas);

            setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);

            if (isDeleteLote) {
                setTextPrint(setCenterText("LOTE BORRADO", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            } else {
                setTextPrint(setCenterText("GB CIERRE COMPLETO", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            }
            setTextPrint(setCenterText("NUM. DE LOTE: " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }

        int ret = printData(canvas);

        if (printer != null) {
            printer = null;
        }

        if (usarCallback) {
            if (callbackPrint != null)
                callbackPrint.getRspPrintReport(0);

            if (callBackSeatle != null)
                callBackSeatle.getRspSeatleReport(0);
        }

        return ret;
    }

    public int printReportDatafast(boolean printTotals) {

        //Logger.debug("PrintManager>>start>>printLogout>>");
        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        if (printTotals) {
            setTextPrint(setCenterText("RESUMEN DE VENTAS", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            printHeader(checkNull(tconf.getNOMBRE_COMERCIO()), checkNull(tconf.getRUC()), checkNull(tconf.getDIRECCION_PRINCIPAL()), tconf.getTELEFONO_COMERCIO(), paint, canvas);

            setTextPrint(setCenterText(checkNull(tconf.getCIUDAD()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            println(paint, canvas);
            println(paint, canvas);

            //setTextPrint(setCenterText("CAP ELECT DATAFAST", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            capturaElectronica(paint, canvas);
            //setTextPrint(setCenterText(checkNull(tconf.getLINEA_AUX()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            println(paint, canvas);
            setTextPrint(setTextColumn("COMERCIO: " + checkNull(tconf.getCARD_ACCP_MERCH()), "TID: " + checkNull(tconf.getCARD_ACCP_TERM()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(setTextColumn("LOTE#: " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), StartAppDATAFAST.VERSION, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            print_DateAndTime(null, paint, canvas, true);

            println(paint, canvas);
            setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);

            if (TransLog.getInstance(idAcquirer).getData().size() == 0) {
                println(paint, canvas);
                setTextPrint(setCenterText("SIN TRANSACCIONES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                setTextPrint(setCenterText("CONVENCIONALES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

                println(paint, canvas);
                setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);
            } else {
                printAllData(paint, canvas, idAcquirer);
            }

            //List<TransLogData> listPreAuto = TransLog.getInstance(idAcquirer + FILE_NAME_PREAUTO).getData();
            if (TransLog.getInstance(idAcquirer + FILE_NAME_PREAUTO).getData().size() > 0) {

                println(paint, canvas);
                println(paint, canvas);
                println(paint, canvas);
                setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);
                setTextPrint(setCenterText("PREAUTORIZACIONES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                println(paint, canvas);

                printAllData(paint, canvas, idAcquirer + FILE_NAME_PREAUTO);
            }
        }

        int ret = printData(canvas);

        if (printer != null) {
            printer = null;
        }

        if (callbackPrint != null)
            callbackPrint.getRspPrintReport(0);

        return ret;
    }

    public int printParamInit() {

        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();
        TransLogLastSettle.getInstance(true).getData();

        paint.setTextSize(20);

        setTextPrint(setCenterText("REPORTE DE INICIALIZACION", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        setTextPrint(setCenterText(tconf.getNOMBRE_COMERCIO(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        println(paint, canvas);

        String auxText;
        setTextPrint(checkNumCharacters("ID Comercio :  " + tconf.getCARD_ACCP_MERCH(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("ID Terminal :  " + tconf.getCARD_ACCP_TERM(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Tipo de POS :  " + DevConfig.getMachine(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Version     :  " + StartAppDATAFAST.VERSION, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Hardware ID :  " + DevConfig.getSN(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("APP Name    :  DATAFAST", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);
        setTextPrint(checkNumCharacters("Consecutivo Recibo: " + TMConfig.getInstance().getTraceNo(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Lote Vigente      : " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Trans. NII        :   " + host_confi.getNII_TRANSACCIONES(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("P. Varios NII     :   " + host_confi.getNII_PAGOS_VARIOS(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("CIERRE NII        :   " + host_confi.getNII_CIERRE(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Echo Test NII     :   " + host_confi.getNII_ECHO_TEST(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);

        setTextPrint(checkNumCharacters("Tipo de Conex. : IP", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        /*setTextPrint(checkNumCharacters("Tel Aut. 1     : 026030360", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Tel Aut. 2     : 0007593042591010", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Tel Aut. 1     : 026030360", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Tel Aut. 2     : 0007593042591010", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);*/
        if (ChequeoIPs.seleccioneIP(0)!= null && ChequeoIPs.seleccioneIP(0)!=null){
            setTextPrint(checkNumCharacters("Direccion IP   : " + checkNull(ChequeoIPs.seleccioneIP(0).getIP_HOST()) + ":" + checkNull(ChequeoIPs.seleccioneIP(0).getPUERTO()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }else{
            setTextPrint(checkNumCharacters("Direccion IP   : " + checkNull(null) + ":" + checkNull(null), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
        setTextPrint(checkNumCharacters("APN            : ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);

        setTextPrint(checkNumCharacters("Ip Lan      : " + checkNull(UtilNetwork.getIPAddress(true)), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
       /* setTextPrint(checkNumCharacters("SubMask Lan : 255.255.255.0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Gateway Lan : 0.0.0.0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);*/

        println(paint, canvas);

        setTextPrint(setCenterText("PARAMETROS INICIALIZACION", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        setTextPrint(checkNumCharacters("Tipo de comunicacion : IP", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("NII                  : " + TMConfig.getInstance().getNii(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Direccion IP         : " + TMConfig.getInstance().getIp(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Puerto               : " + TMConfig.getInstance().getPort(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);

        setTextPrint(setCenterText("OPCIONES PARA EL TERMINAL", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        setTextPrint(checkNumCharacters("RECIBO", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Linea 1 : " + tconf.getLINEA_AUX(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Linea 2 : " + tconf.getNOMBRE_COMERCIO(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Linea 3 : " + tconf.getDIRECCION_PRINCIPAL(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Linea 4 : " + tconf.getTELEFONO_COMERCIO(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);

        setTextPrint(checkNumCharacters("Tipo de IVA        : " + tconf.getTIPO_IMPUESTO(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        String IVAPercent = tconf.getPORCENTAJE_MAXIMO_IMPUESTO();

        setTextPrint(checkNumCharacters("Valor IVA          : " + IVAPercent + " %", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);

        if (tconf.getHABILITAR_SERVICIO().equals("1")) {
            println(paint, canvas);
            setTextPrint(checkNumCharacters("Servicio Habilitado: SI", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("Tipo de Servicio   : " + tconf.getTIPO_SERVICIO(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            String Service = tconf.getPORCENTAJE_MAXIMO_SERVICIO();
            setTextPrint(checkNumCharacters("Porcentaje Servicio: " + Service + " %", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(checkNumCharacters("Servicio Habilitado: NO", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("Tipo de Servicio   : ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("Porcentaje Servicio: ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        println(paint, canvas);

        if (tconf.getTARIFA_CERO().equals("1")) {
            setTextPrint(checkNumCharacters("Solicita Tarifa Cero : SI", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(checkNumCharacters("Solicita Tarifa Cero : NO", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (tconf.getHABILITA_MONTO_FIJO().equals("1")) {
            setTextPrint(checkNumCharacters("Gasolinera           : SI", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(checkNumCharacters("Gasolinera           : NO", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (tconf.getHABILITAR_PROPINA().equals("1")) {
            setTextPrint(checkNumCharacters("PROPINA              : SI", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(checkNumCharacters("PROPINA              : NO", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (tconf.getTRANSACCION_PAGOS_VARIOS().equals("1")) {
            setTextPrint(checkNumCharacters("PAGOS VARIOS         : SI", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(checkNumCharacters("PAGOS VARIOS         : NO", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (tconf.getTRANSACCION_PRE_AUTO().equals("1")) {
            setTextPrint(checkNumCharacters("PREAUTORIZACION      : SI", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(checkNumCharacters("PREAUTORIZACION      : NO", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        println(paint, canvas);

        setTextPrint(checkNumCharacters("Fecha Ult. Act.      : " + checkNull(getFechaCierre("fechaUltAct")), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Dias Para Cierre     : " + checkNull(tconf.getDIAS_CIERRE()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Fecha Sig. Cierre.   : " + checkNull(getFechaCierre("fechaSigCierre")), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Fecha Ult. Cierre.   : " + checkNull(getFechaCierre("fechaUltimoCierre")), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Horas Para Echo Test : " + checkNull(tconf.getHORAS_ECHO()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("Fecha Sig. Echo Test : " + checkNull(getFechaCierre("fechaSigEchoTest")), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);
        println(paint, canvas);

        /*MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = null;

        try {
            bitMatrix = multiFormatWriter.encode("https://www.datafast.com.ec", BarcodeFormat.QR_CODE, 275, 275);
        } catch (WriterException e) {
            Logger.error("Exception" + e.toString());
        }

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);*/

        /*canvas.setX(50);
        canvas.drawBitmap(bitmap, paint);
        canvas.setX(0);*/

        println(paint, canvas);

        setTextPrint(setCenterText("FECHA Y HORA REPORTE", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        auxText = PAYUtils.getDay() + "/" + PAYUtils.getMonth() + "/" + String.valueOf(PAYUtils.getYear()) + " " + formatoHora(PAYUtils.getLocalTime());
        setTextPrint(setCenterText(auxText, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);

        int ret = printData(canvas);

        if (printer != null) {
            printer = null;
        }

        return ret;
    }

    public void selectPrintReport(String typeReport) {
        switch (typeReport) {
            case ALL_F_ACUM:
                printReportAcumulacion();
                break;
            case ALL_F_REDEN:
                printReport(false);
                break;
            default:
                printReport(true);
                break;
        }
    }

    public int printReport(boolean title) {

        Logger.debug("PrintManager>>start>>printReport>>");
        String address;
        String phone;
        String tmp;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        if (TransLog.getInstance(idAcquirer).getSize() == 0) {
            ret = Tcode.T_print_no_log_err;
        } else {

            printer = Printer.getInstance();
            if (printer == null) {
                ret = Tcode.T_sdk_err;
            } else {

                PrintCanvas canvas = new PrintCanvas();
                Paint paint = new Paint();
                ;

                //tmp = ISOUtil.hex2AsciiStr(acquirerRow.getSb_locations());
                //address = tmp.substring(0, 23);
                //phone = tmp.substring(23);

                //printHeader("", checkNull(ISOUtil.hex2AsciiStr(acquirerRow.getSb_displayMerchantName())), checkNull(address), checkNull(phone), paint, canvas);

                //setTextPrint(checkNumCharacters("TERMINAL ID  : " + checkNull(ISOUtil.hex2AsciiStr(acquirerRow.getSb_term_id())), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                //setTextPrint(checkNumCharacters("COMERCIO     : " + ISOUtil.hex2AsciiStr(acquirerRow.getSb_name()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                //setTextPrint(checkNumCharacters("COMERCIO ID  : " + checkNull(ISOUtil.hex2AsciiStr(acquirerRow.getSb_acceptor_id())), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                printDateAndTime(getFormatDateAndTime(checkNull(PAYUtils.getMonth() + " " + PAYUtils.getDay() + "," + PAYUtils.getYear()), checkNull(PAYUtils.getHMS())), S_MEDIUM, BOLD_OFF, paint, canvas);

                println(paint, canvas);
                println(paint, canvas);

                if (title)
                    setTextPrintREV(setCenterText("REPORTE DEPOSITO", S_BIG), paint, BOLD_OFF, canvas, S_BIG);

                println(paint, canvas);

                setTextPrint(setCenterText("LOTE: " + checkNull(dataTrans.getBatchNo()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

                println(paint, canvas);
                println(paint, canvas);

                printLine(paint, canvas);


                println(paint, canvas);

                setTextPrint(setCenterText("REF.      TARJETA       MONTO  ", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                println(paint, canvas);

                printAllData(paint, canvas, idAcquirer);

                printLine(paint, canvas);

                setTextPrint(setCenterText("FIN  DE  REPORTE", S_BIG), paint, BOLD_OFF, canvas, S_BIG);

                println(paint, canvas);
                println(paint, canvas);
                println(paint, canvas);

                ret = printData(canvas);

                if (printer != null) {
                    printer = null;
                }
            }
        }

        return ret;
    }

    public int printReportAcumulacion() {

        return 0;
        /*Logger.debug("PrintManager>>start>>printReport>>");
        String address;
        String phone;
        String tmp;
        this.printTask = new PrintTask();
        this.printTask.setGray(130);
        int ret = -1;
        List<TransLogData> list = TransLog.getInstance(menus.idAcquirer).getData();

        if (TransLog.getInstance(menus.idAcquirer).getSize() == 0) {
            ret = Tcode.T_print_no_log_err;
        } else {

            printer = Printer.getInstance();
            if (printer == null) {
                ret = Tcode.T_sdk_err;
            } else {

                PrintCanvas canvas = new PrintCanvas();
                Paint paint = new Paint();

                tmp = ISOUtil.hex2AsciiStr(acquirerRow.getSb_locations());
                address = tmp.substring(0, 23);
                phone = tmp.substring(23);

                printHeader("", checkNull(ISOUtil.hex2AsciiStr(acquirerRow.getSb_displayMerchantName())), checkNull(address), checkNull(phone), paint, canvas);

                setTextPrint(checkNumCharacters("TERMINAL ID  : " + checkNull(ISOUtil.hex2AsciiStr(acquirerRow.getSb_term_id())), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(checkNumCharacters("COMERCIO     : " + ISOUtil.hex2AsciiStr(acquirerRow.getSb_name()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(checkNumCharacters("COMERCIO ID  : " + checkNull(ISOUtil.hex2AsciiStr(acquirerRow.getSb_acceptor_id())), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                printDateAndTime(getFormatDateAndTime(checkNull(PAYUtils.getMonth() + " " + PAYUtils.getDay() + "," + PAYUtils.getYear()), checkNull(PAYUtils.getHMS())), S_MEDIUM, BOLD_OFF, paint, canvas);

                println(paint, canvas);
                printLine(paint, canvas);
                println(paint, canvas);

                setTextPrint(checkNumCharacters("REF.  TARJETA    FECHA    MONTO", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

                for (TransLogData transLogData : list) {

                    String typeCoin = transLogData.getTypeCoin();
                    String card = transLogData.getPan();
                    card = card.substring(card.length() - 6);
                    if (typeCoin.equals(LOCAL)) {
                        typeCoin = " LOCAL. ";
                    } else {
                        typeCoin = " DOLAR ";
                    }
                    setTextPrint(checkNumCharacters(transLogData.getTraceNo().trim() + "     " + card.trim() + "      " +
                            transLogData.getDatePrint().trim() + " " + typeCoin + ISOUtil.decimalFormat("0" + transLogData.getAmount()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }

                printLine(paint, canvas);

                setTextPrint(setCenterText("FIN  DE  REPORTE", S_BIG), paint, BOLD_OFF, canvas, S_BIG);

                println(paint, canvas);
                println(paint, canvas);
                println(paint, canvas);

                ret = printData(canvas);

                if (printer != null) {
                    printer = null;
                }
            }
        }

        return ret;*/
    }

    public int printTransreject(String value1, String value2, int rerval) {

        Logger.debug("PrintManager>>start>>printTransreject>>");
        String lote;
        String term;
        String idCom;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        printer = Printer.getInstance();
        if (printer == null) {
            ret = Tcode.T_sdk_err;
        } else {

            PrintCanvas canvas = new PrintCanvas();
            Paint paint = new Paint();

            lote = TMConfig.getInstance().getBatchNo();
            term = tconf.getCARD_ACCP_TERM();
            idCom = tconf.getCARD_ACCP_MERCH();

            printSecondHeader(checkNull(lote), checkNull(term), checkNull(idCom), paint, canvas);

            setTextPrint(setCenterText(checkNull(PAYUtils.getSecurityNum(value1, 6, 3)).trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            println(paint, canvas);

            setTextPrint(setCenterText("REF : " + checkNull(value2).trim(), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            println(paint, canvas);

            printDateAndTime(getFormatDateAndTime(checkNull(PAYUtils.getMonth() + " " + PAYUtils.getDay() + "," + PAYUtils.getYear()), checkNull(PAYUtils.getHMS())), S_MEDIUM, BOLD_OFF, paint, canvas);
            println(paint, canvas);

            String msg = getErrInfo(String.valueOf(rerval));
            setTextPrint(setCenterText(checkNull(msg).trim(), S_BIG), paint, BOLD_ON, canvas, S_MEDIUM);

            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);

            ret = printData(canvas);

            if (printer != null) {
                printer = null;
            }
        }

        return ret;
    }

    public int printEMVAppCfg() {

        Logger.debug("PrintManager>>start>>printReportEmv>>");

        EMVAPP_ROW emvappRow = EMVAPP_ROW.getSingletonInstance();
        CAPK_ROW capkRow = CAPK_ROW.getSingletonInstance();

        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        printer = Printer.getInstance();
        if (printer == null) {
            ret = Tcode.T_sdk_err;
        } else {

            PrintCanvas canvas = new PrintCanvas();
            Paint paint = new Paint();

            /*setTextPrint(setCenterText(ISOUtil.hex2AsciiStr(termCfg.getSb_dflt_name()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(setCenterText(ISOUtil.hex2AsciiStr(termCfg.getSb_name_loc().substring(0, 46)), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(setCenterText(ISOUtil.hex2AsciiStr(termCfg.getSb_name_loc().substring(46)), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);*/

            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);

            getEMVAPP_ROW(emvappRow, paint, canvas);

            println(paint, canvas);
            println(paint, canvas);

            setTextPrint("EMV KEY INFO", paint, BOLD_ON, canvas, S_MEDIUM);
            println(paint, canvas);

            setTextPrint("Public Key ID      Eff     Exp", paint, BOLD_ON, canvas, S_MEDIUM);
            getCAPK_ROW(capkRow, paint, canvas);

            ret = printData(canvas);

            if (printer != null) {
                printer = null;
            }
        }

        return ret;
    }

    public int printConfigTerminal() {

        Logger.debug("PrintManager>>start>>printConfigTerminal>>");

        String term;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        printer = Printer.getInstance();
        if (printer == null) {
            ret = Tcode.T_sdk_err;
        } else {

            PrintCanvas canvas = new PrintCanvas();
            Paint paint = new Paint();

            setTextPrint(setCenterText("CONFIGURACION TERMINAL", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            println(paint, canvas);

            term = tconf.getCARD_ACCP_TERM();
            setTextPrint(checkNumCharacters("TERMINAL: " + checkNull(term), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            printDateAndTime(getFormatDateAndTime(checkNull(PAYUtils.getMonth() + " " + PAYUtils.getDay() + "," + PAYUtils.getYear()), checkNull(PAYUtils.getHMS())), S_MEDIUM, BOLD_OFF, paint, canvas);
            printLine("=", paint, canvas);

            setTextPrint(setCenterText("LISTA DE APLICACIONES", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            printLine("=", paint, canvas);
            println(paint, canvas);

            setTextPrint(setTextColumn("APPLICATION_ID", "VERSION_NAME", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(setTextColumn(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            //Obtenido del documento EMVCo Letter of Approval - Contact Terminal Level 2 - October 27, 2017
            setTextPrint(setTextColumn("libemv.so Version 1.0.9", "AFD80709", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            println(paint, canvas);

            printLine("=", paint, canvas);

            setTextPrint(setCenterText("IP NETWORK ", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            printLine("=", paint, canvas);
            println(paint, canvas);

            // test functions
            setTextPrint(checkNumCharacters("MAC Address: " + checkNull(UtilNetwork.getMACAddress("wlan0")), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(checkNumCharacters("IP Address: " + checkNull(UtilNetwork.getIPAddress(true)), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            println(paint, canvas);

            setTextPrint(setCenterText("FIN  DE  REPORTE", S_BIG), paint, BOLD_ON, canvas, S_BIG);

            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);


            ret = printData(canvas);

            if (printer != null) {
                printer = null;
            }
        }

        return ret;
    }

    public int printLastSettle() {

        Logger.debug("PrintManager>>start>>printLogout>>");

        TransLogLastSettle.getInstance(true);
        List<TransLogData> list = TransLogLastSettle.getInstance(false).getData();

        if (list.isEmpty()) {
            return -1;
        }

        TransLogData transLogData = list.get(0);

        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        printer = Printer.getInstance();
        if (printer == null) {
            ret = Tcode.T_sdk_err;
        } else {

            PrintCanvas canvas = new PrintCanvas();
            Paint paint = new Paint();

            printHeader("", checkNull(transLogData.getNameTrade()), checkNull(transLogData.getAddressTrade()), checkNull(transLogData.getPhoneTrade()), paint, canvas);

            setTextPrint(checkNumCharacters("TERMINAL ID  : " + checkNull(transLogData.getTermID()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(checkNumCharacters("COMERCIO     : " + ISOUtil.hex2AsciiStr(transLogData.getAdquirerName()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(checkNumCharacters("COMERCIO ID  : " + checkNull(transLogData.getMerchID()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

            printDateAndTime(getFormatDateAndTime(checkNull(transLogData.getDatePrint()), checkNull(transLogData.getLocalTime())), S_MEDIUM, BOLD_OFF, paint, canvas);

            println(paint, canvas);
            println(paint, canvas);

            setTextPrintREV(setCenterText("REPORTE DEPOSITO", S_BIG), paint, BOLD_OFF, canvas, S_BIG);

            println(paint, canvas);

            setTextPrint(setCenterText("LOTE: " + checkNull(transLogData.getBatchNo()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            println(paint, canvas);
            println(paint, canvas);

            printLine(paint, canvas);

            println(paint, canvas);

            setTextPrint(setCenterText("REF.      TARJETA       MONTO  ", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            println(paint, canvas);

            printAllDataLastSettle(paint, canvas, list);

            setTextPrint("===============================", paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(setCenterText("XX CIERRE DE LOTE CONFIRMADO XX", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            setTextPrint("===============================", paint, BOLD_OFF, canvas, S_MEDIUM);
            println(paint, canvas);

            setTextPrint("BATCH " + checkNull(transLogData.getBatchNo()), paint, BOLD_OFF, canvas, S_BIG);
            setTextPrint(setCenterText("CERRADO", S_BIG), paint, BOLD_OFF, canvas, S_BIG);
            println(paint, canvas);

            setTextPrint(setCenterText("FIN  DE  REPORTE", S_BIG), paint, BOLD_OFF, canvas, S_BIG);

            println(paint, canvas);
            println(paint, canvas);
            setTextPrint(setCenterText("XXX   COPIA   XXX", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            println(paint, canvas);
            println(paint, canvas);

            ret = printData(canvas);

            if (printer != null) {
                printer = null;
            }
        }
        return ret;
    }

    /*******
     Tools print
     *******/
    private boolean getCAPK_ROW(CAPK_ROW capkRow, Paint paint, PrintCanvas canvas) {
        boolean ok = false;
        String eff;
        String exp;
        String tmp;
        dbHelper databaseAccess = new dbHelper(mContext, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : CAPK_ROW.fields) {
            sql.append(s);
            if (counter++ < CAPK_ROW.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from capks");
        sql.append(";");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;
            while (!cursor.isAfterLast()) {
                capkRow.clearCAPK_ROW();
                indexColumn = 0;
                for (String s : CAPK_ROW.fields) {
                    capkRow.setCAPK_ROW(s, cursor.getString(indexColumn++));
                }

                //Effect date
                tmp = capkRow.getEffectDate();
                eff = tmp.substring(4, 6);
                eff += "/";
                eff += tmp.substring(2, 4);

                //Exp date
                tmp = capkRow.getExpiryDate();
                exp = tmp.substring(4, 6);
                exp += "/";
                exp += tmp.substring(2, 4);

                setTextPrint(setTextColumn(capkRow.getRID() + "-" + capkRow.getKeyIdx(), eff + "  " + exp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                /*Log.d("Capks", "***********");
                Log.d("RID", capkRow.getRID());
                Log.d("ID", String.valueOf(Integer.parseInt(capkRow.getKeyIdx(), 16)));
                Log.d("Eff", capkRow.getEffectDate());
                Log.d("Exp", capkRow.getExpiryDate());
                Log.d("Capks", "***********");*/

                ok = true;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();
        return ok;
    }

    private boolean getEMVAPP_ROW(EMVAPP_ROW emvappRow, Paint paint, PrintCanvas canvas) {
        boolean ok = false;
        int aux;
        String tmp;
        long flr;
        dbHelper databaseAccess = new dbHelper(mContext, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : EMVAPP_ROW.fields) {
            sql.append(s);
            if (counter++ < EMVAPP_ROW.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from emvapps");
        sql.append(";");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;

            while (!cursor.isAfterLast()) {
                emvappRow.clearEMVAPP_ROW();
                indexColumn = 0;
                for (String s : EMVAPP_ROW.fields) {
                    emvappRow.setEMVAPP_ROW(s, cursor.getString(indexColumn++));
                }

                setTextPrint("EMV APP CFG", paint, BOLD_ON, canvas, S_MEDIUM);
                println(paint, canvas);
                TLV_parsing tlvParsing = new TLV_parsing(emvappRow.geteACFG());
                setTextPrint(setTextColumn("AID: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f06), 0, tlvParsing.getValueB(0x9f06).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                byte[] output = hex2byte(emvappRow.geteBitField());

                //AQC profile
                aux = (output[0] & 0x80);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("Def ACQ Profile: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);


                setTextPrint(setTextColumn("Type: ", (emvappRow.geteType()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("Ver: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f09), 0, tlvParsing.getValueB(0x9f09).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                if (tlvParsing.getValueB(0x9f1b) != null) {
                    flr = Long.parseLong(ISOUtil.bcd2str(tlvParsing.getValueB(0x9f1b), 0, tlvParsing.getValueB(0x9f1b).length));
                    setTextPrint(setTextColumn("FLR LIMIT: ", PAYUtils.getStrAmount(flr), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                }

                if (tlvParsing.getValueB(0xdf7f) != null) {
                    flr = Long.parseLong(ISOUtil.bcd2str(tlvParsing.getValueB(0xdf7f), 0, tlvParsing.getValueB(0xdf7f).length));
                    setTextPrint(setTextColumn("FLR LIMIT(0): ", PAYUtils.getStrAmount(flr), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                }

                setTextPrint(setTextColumn("Basic Random: ", emvappRow.geteRSBThresh(), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("Target %: ", emvappRow.geteRSTarget() + "-" + emvappRow.geteRSBMax(), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                setTextPrint(setTextColumn("Country: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f1a), 0, tlvParsing.getValueB(0x9f1a).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("Currency: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x5f2a), 0, tlvParsing.getValueB(0x5f2a).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                aux = (output[0] & 0x01);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("Allow Partial AID: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                aux = (output[0] & 0x02);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("Referral Enable: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                aux = (output[0] & 0x04);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("PIN Bypass Enable: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                aux = (output[0] & 0x08);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("Force TRM: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                setTextPrint(setTextColumn("TAC Denial: ", (emvappRow.geteTACDenial()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("TAC Online: ", (emvappRow.geteTACOnline()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("TAC Default: ", (emvappRow.geteTACDefault()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                setTextPrint(setTextColumn("Term Cap: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f33), 0, tlvParsing.getValueB(0x9f33).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("Add Cap: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f40), 0, tlvParsing.getValueB(0x9f40).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                println(paint, canvas);
                println(paint, canvas);

                ok = true;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();
        return ok;
    }

    private String getC_OPT(String data) {
        String data_final = "0";
        int aux;
        try {
            byte[] output = hex2byte(data);

            aux = (output[0] & (byte) 0x02);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[0] & (byte) 0x04);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[1] & (byte) 0x10);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[1] & (byte) 0x08);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[0] & (byte) 0x10);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[0] & (byte) 0x20);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[0] & (byte) 0x40);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[0] & (byte) 0x80);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[1] & (byte) 0x01);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[1] & (byte) 0x02);
            data_final += (aux == 0) ? "0" : "1";

            data_final += "0";
            data_final += "0";

            aux = (output[1] & (byte) 0x04);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[1] & (byte) 0x80);
            data_final += (aux == 0) ? "0" : "1";

            aux = (output[1] & (byte) 0x40);
            data_final += (aux == 0) ? "0" : "1";

        } catch (Exception ex) {
            data_final = "error";
        }

        return data_final;
    }

    private void printAllData(Paint paint, PrintCanvas canvas, String batch) {
        List<TransLogData> list = new ArrayList<>(TransLog.getInstance(batch).getData());

        if (list!=null){
            List<TransLogData> auxList = new ArrayList<>(orderList(list, true));
            List<TransLogData> listFinal = new ArrayList<>(orderList(auxList, false));
            List<TransLogData> listDetalle = new ArrayList<>(listFinal);

            Collections.sort(listDetalle, new Comparator<TransLogData>() {
                @Override
                public int compare(TransLogData transLogData, TransLogData t1) {
                    return transLogData.getTraceNo().compareTo(t1.getTraceNo());
                }
            });
            //Collections.reverse(listDetalle);

            try {

                limpiarVar();

                limpiarComerciosImpresos(listFinal);
                //List de comercios
                for (TransLogData translogAllAdquirer : listFinal) {

                    MID_InterOper = translogAllAdquirer.getMID_InterOper();

                    if (soloUnCiclo)
                        break;

                    if (translogAllAdquirer.isMulticomercio()) {
                        nombreAdquirenteActual = "";
                        nombreActualEmisor = "";
                        fechaTransActual = "";
                        totalAmount = 0;
                        totalIva = 0;
                        totalServiceAmount = 0;
                        totalTipAmount = 0;


                        for (int i = 0; i < comercioImpreso.length; i++) {
                            if (comercioImpreso[i].equals(translogAllAdquirer.getIdComercio())) {
                                omitir = true;
                                break;
                            } else
                                omitir = false;
                        }

                        if (omitir) {
                            comercioImpreso[idxImpresionComercio] = translogAllAdquirer.getIdComercio();
                            idxImpresionComercio++;
                            continue;
                        }

                        if (comercioImpreso[idxImpresionComercio].equals("-")) {

                            //Nombre Multicomercio
                            nombreComercioActual(translogAllAdquirer, paint, canvas);

                            imprimirTransDiscriminadas(translogAllAdquirer, listFinal, listDetalle, paint, canvas);

                            comercioImpreso[idxImpresionComercio] = translogAllAdquirer.getIdComercio();
                            idxImpresionComercio++;

                        }

                    } else {
                        soloUnCiclo = true;

                        imprimirTransDiscriminadas(translogAllAdquirer, listFinal, listDetalle, paint, canvas);
                    }
                }

                imprimirGranTotal(paint, canvas);

                //Se cambia a false nuevamente todas las transacciones que se imprimieron en el reporte
                for (TransLogData translog : list) {
                    translog.setAlreadyPrinted(false);
                }

            } catch (ArrayIndexOutOfBoundsException exception) {
            }
        }
    }

    private void printAllDataLastSettle(Paint paint, PrintCanvas canvas, List<TransLogData> list) {
        //List<TransLogData> list = TransLogLastSettle.getInstance(false).getData();

        long amount_BS = 0;
        long amount_USD = 0;
        int contTransBS = 0, contTransUSD = 0;
        long amountVoid_BS = 0;
        long amountVoid_USD = 0;
        int contTransVoidBS = 0, contTransVoidUSD = 0;
        int contCuotas_BS = 0, contCuotas_USD = 0;
        long amountCuotas_BS = 0, amountCuotas_USD = 0;
        int contTips_BS = 0, contTips_USD = 0;
        long amountTip_BS = 0, amountTip_USD = 0;
        long amountDesc_BS = 0;
        long amountDesc_USD = 0;
        long amountCashBack = 0;
        int contCashBack = 0;
        int contDesc_BS = 0, contDesc_USD = 0;


        for (TransLogData transLogData : list) {

            String typeAccount, typeCoin = transLogData.getTypeCoin();

            if (transLogData.isICC())
                typeAccount = "C";
            else {
                if (transLogData.isFallback())
                    typeAccount = "F";
                else
                    typeAccount = "S";
            }
            if (typeCoin.equals(LOCAL)) {
                typeCoin = "LOCAL.";
            } else {
                typeCoin = "DOLAR";
            }

            if (!transLogData.getIsVoided()) {

                switch (transLogData.getTypeCoin()) {
                    case LOCAL:
                        amount_BS += transLogData.getAmount();
                        contTransBS++;
                        if (transLogData.getNumCuotas() > 1) {
                            contCuotas_BS++;
                            amountCuotas_BS += transLogData.getAmount() + transLogData.getTipAmout();
                        }
                        if (transLogData.getTipAmout() > 0) {
                            contTips_BS++;
                            amountTip_BS += transLogData.getTipAmout();
                        }
                        break;

                    case DOLAR:
                        amount_USD += transLogData.getAmount();
                        contTransUSD++;
                        if (transLogData.getNumCuotas() > 1) {
                            contCuotas_USD++;
                            amountCuotas_USD += transLogData.getAmount() + transLogData.getTipAmout();
                        }

                        if (transLogData.getTipAmout() > 0) {
                            contTips_USD++;
                            amountTip_USD += transLogData.getTipAmout();
                        }
                        break;
                }

                setTextPrint(setTextColumn(transLogData.getTraceNo() + "  " + transLogData.getPan() + " (" + typeAccount + ") " +
                        " " + typeCoin, formatAmountLess(transLogData.getAmount()) + "", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

                if (transLogData.getNumCuotas() > 1) {

                    setTextPrint(setTextColumn("                        CUOTAS #",
                            ISOUtil.padleft(transLogData.getNumCuotas() + "", 2, '0'), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }
                if (transLogData.isTip()) {

                    setTextPrint(setTextColumn("                     PROPINA  " + typeCoin,
                            formatAmountLess(transLogData.getTipAmout()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

                    setTextPrint(setTextColumn("                       TOTAL  " + typeCoin,
                            formatAmountLess(transLogData.getTipAmout() + transLogData.getAmount()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }

            } else {

                switch (transLogData.getTypeCoin()) {
                    case LOCAL:
                        amountVoid_BS += transLogData.getAmount() + transLogData.getTipAmout() + amountCashBack;
                        contTransVoidBS++;
                        if (transLogData.getNumCuotas() > 1) {
                            contCuotas_BS++;
                            amountCuotas_BS += transLogData.getAmount() + transLogData.getTipAmout();
                        }
                        break;

                    case DOLAR:
                        amountVoid_USD += transLogData.getAmount() + transLogData.getTipAmout();
                        contTransVoidUSD++;
                        if (transLogData.getNumCuotas() > 1) {
                            contCuotas_USD++;
                            amountCuotas_USD += transLogData.getAmount() + transLogData.getTipAmout();
                        }
                        break;
                }

                setTextPrintREV(setTextColumn(transLogData.getTraceNo() + "  " + transLogData.getPan() + " (" + typeAccount + ") " +
                        " " + typeCoin, formatAmountLess(transLogData.getAmount()) + "", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

                if (transLogData.getNumCuotas() > 1) {

                    setTextPrintREV(setTextColumn("                        CUOTAS #",
                            ISOUtil.padleft(transLogData.getNumCuotas() + "", 2, '0'), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }
                if (transLogData.isTip()) {

                    setTextPrintREV(setTextColumn("                     PROPINA  " + typeCoin,
                            formatAmountLess(transLogData.getTipAmout()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

                    setTextPrintREV(setTextColumn("                       TOTAL  " + typeCoin,
                            formatAmountLess(transLogData.getTipAmout() + transLogData.getAmount()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }
            }
            println(paint, canvas);
        }

        println(paint, canvas);
        println(paint, canvas);

        printLine(paint, canvas);
        setTextPrint("TOTALES BOLIVIANOS", paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint("===============================", paint, BOLD_OFF, canvas, S_MEDIUM);
        println(paint, canvas);

        setTextPrint(setTextColumn("                 CANT.", "TOTAL", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);

        setTextPrint(setTextColumn("Cuotas                      " + ISOUtil.padleft(contCuotas_BS + "", 3, '0'), formatAmountLess(amountCuotas_BS), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setTextColumn("Total Anulaciones           " + ISOUtil.padleft(contTransVoidBS + "", 3, '0'), formatAmountLess(amountVoid_BS), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (contTips_BS > 0) {
            setTextPrint(setTextColumn("Total Propinas              " + ISOUtil.padleft(contTips_BS + "", 3, '0'), formatAmountLess(amountTip_BS), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
        setTextPrint(setTextColumn("Total Cash Back             " + ISOUtil.padleft(contCashBack + "", 3, '0'), formatAmountLess(amountCashBack), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setTextColumn("Total Descuento             " + ISOUtil.padleft(contDesc_BS + "", 3, '0'), formatAmountLess(amountDesc_BS), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        /*if (Chkoptn.stringToBoolean(Chkoptn.CashBlocked(acquirerRow))) {
            setTextPrint(setTextColumn("Total Avances               " + ISOUtil.padleft(contTransUSD + "", 3, '0'), formatAmountLess(amount_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("Total Ventas                " + ISOUtil.padleft(contTransUSD + "", 3, '0'), formatAmountLess(amount_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }*/
        setTextPrint(setTextColumn("Total Ventas                " + ISOUtil.padleft(contTransUSD + "", 3, '0'), formatAmountLess(amount_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);

        setTextPrint(setTextColumn("TOTAL GENERAL     " + ISOUtil.padleft(contTransBS + "", 3, '0'),
                formatAmountLess(amount_BS + amountTip_BS + amountCashBack), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

        println(paint, canvas);
        println(paint, canvas);
        println(paint, canvas);

        setTextPrint("TOTALES DOLARES", paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint("===============================", paint, BOLD_OFF, canvas, S_MEDIUM);
        println(paint, canvas);

        setTextPrint(setTextColumn("                 CANT.", "TOTAL", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);

        setTextPrint(setTextColumn("Cuotas                      " + ISOUtil.padleft(contCuotas_USD + "", 3, '0'), formatAmountLess(amountCuotas_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setTextColumn("Total Anulaciones           " + ISOUtil.padleft(contTransVoidUSD + "", 3, '0'), formatAmountLess(amountVoid_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (contTips_USD > 0) {
            setTextPrint(setTextColumn("Total Propinas              " + ISOUtil.padleft(contTips_USD + "", 3, '0'), formatAmountLess(amountTip_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
        setTextPrint(setTextColumn("Total Cash Back             " + "000", "0.00", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setTextColumn("Total Descuento             " + ISOUtil.padleft(contDesc_USD + "", 3, '0'), formatAmountLess(amountDesc_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        /*if (Chkoptn.stringToBoolean(Chkoptn.CashBlocked(acquirerRow))) {
            setTextPrint(setTextColumn("Total Avances               " + ISOUtil.padleft(contTransUSD + "", 3, '0'), formatAmountLess(amount_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("Total Ventas                " + ISOUtil.padleft(contTransUSD + "", 3, '0'), formatAmountLess(amount_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }*/
        setTextPrint(setTextColumn("Total Ventas                " + ISOUtil.padleft(contTransUSD + "", 3, '0'), formatAmountLess(amount_USD), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);

        setTextPrint(setTextColumn("TOTAL GENERAL     " + ISOUtil.padleft(contTransUSD + "", 3, '0'),
                formatAmountLess(amount_USD + amountTip_USD), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);


        println(paint, canvas);
        println(paint, canvas);

    }

    private void limpiarComerciosImpresos(List<TransLogData> listFinal){
        comercioImpreso = new String[listFinal.size()];
        for(int i=0; i<comercioImpreso.length; i++){
            comercioImpreso[i] = "-";
        }
    }

    private void limpiarVar(){
        contTransAcq = 0;
        contTotalTransAcq = 0;
        contTransEmisor = 0;

        subTotalSubTotal = 0;
        ivaAmountSubTotal = 0;
        serviceAmountSubTotal = 0;
        tipAmountSubTotal = 0;
        montoFijoSubTotal = 0;

        totalTempAmount = 0;
        totalTempIva = 0;
        totalTempServiceAmount = 0;
        totalTempTipAmount = 0;
        totalTempMontoFijo = 0;

        granTotal = 0;
        granTotalIva = 0;
        granTotalService = 0;
        granTotalTip = 0;
        granTotalMontoFijo = 0;

        totalAmount = 0;
        totalIva = 0;
        totalServiceAmount = 0;
        totalTipAmount = 0;
        totalMontoFijo = 0;

        montoFijo = 0;

        nombreActualEmisor = "";
        fechaTransActual = "";
        nombreAdquirenteActual = "";
        soloUnCiclo = false;
        idxImpresionComercio = 0;
        omitir = false;


    }

    private void imprimirGranTotal(Paint paint, PrintCanvas canvas){
        println(paint, canvas);
        setTextPrint("------------------------------------------", paint, BOLD_OFF, canvas, S_SMALL);
        setTextPrint(setTextColumn("GRAN TOTAL:          " + contTotalTransAcq, "$ " + PAYUtils.getStrAmount(granTotal) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (GetAmount.checkIVA())
            setTextPrint(setTextColumn(checkNull(tconf.getLABEL_IMPUESTO()) + ":", "$ " + PAYUtils.getStrAmount(granTotalIva) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (GetAmount.checkService())
            setTextPrint(setTextColumn(checkNull(tconf.getLABEL_SERVICIO()) + ":", "$ " + PAYUtils.getStrAmount(granTotalService) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (GetAmount.checkTip())
            setTextPrint(setTextColumn(checkNull(tconf.getLABEL_PROPINA()) + ":", "$ " + PAYUtils.getStrAmount(granTotalTip) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        if (tconf.getHABILITA_MONTO_FIJO() != null) {
            //if (granTotalMontoFijo != 0) {
            if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())) {
                setTextPrint(setTextColumn("TARIFA: ", "$ " + PAYUtils.getStrAmount(granTotalMontoFijo) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            }
        }
        println(paint, canvas);
        setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);
    }

    private void imprimirTransDiscriminadas(TransLogData translogAllAdquirer, List<TransLogData> listFinal, List<TransLogData> listDetalle, Paint paint, PrintCanvas canvas){

        //List Acq
        for (TransLogData translogByAdquirer : listFinal) {

            limpiarMontoSubtotales();

            //Verificar si cambio el adquirente
            if (!nombreAdquirenteActual.equals(translogByAdquirer.getField44())) {
                nombreAdquirenteActual = translogByAdquirer.getField44();

                //Nombre Adquirente
                setTextPrint(checkNull(nombreAdquirenteActual), paint, BOLD_ON, canvas, S_MEDIUM);

                //Validar si el adquirente tiene habilitada interoperabilidad
                if (MID_InterOper!=null && !MID_InterOper.equals("")){
                    setTextPrint(checkNull(translogByAdquirer.getMID_InterOper()), paint, BOLD_ON, canvas, S_SMALL);
                }else {
                    MID_InterOper="";
                }

/*====================================== Inicio Lista Emisor ==================================================================*/
                nombreActualEmisor = "";
                fechaTransActual = "";
                for (TransLogData translogIssuer : listFinal) {

                    if (translogAllAdquirer.getIdComercio()!=null) {
                        if (!translogAllAdquirer.getIdComercio().equals(translogIssuer.getIdComercio()))
                            continue;
                    }

                    //Verificar si el Adquirente seleccionado es el mismo del ciclo actual de adquirente y a demas que el emisor seleccionado sea diferente para no repetirlo
                    if (translogIssuer.getField44().equals(nombreAdquirenteActual) && !translogIssuer.getIssuerName().equals(nombreActualEmisor)) {

                        if (!nombreActualEmisor.equals("") && !fechaTransActual.equals("")) {
                            imprimirSubTotal(translogByAdquirer, paint, canvas);

                            totalTempAmount += subTotal;
                            totalTempIva += ivaAmount;
                            totalTempServiceAmount += serviceAmount;
                            totalTempTipAmount += tipAmount;
                            totalTempMontoFijo += montoFijo;
                        }
                        contTransEmisor = 0;
                        limpiarMontoSubtotales();

                        //Nombre y Fecha del emisor
                        nombreActualEmisor = translogIssuer.getIssuerName();
                        fechaTransActual = translogIssuer.getDatePrint();
                        printNameIssuer = false;
                        printDateTransxIssuer = false;
                    }
/*######################################## Inicio Lista Detalle Trans ################################################*/
                    for (TransLogData translog : listDetalle) {

                        if (translogAllAdquirer.getIdComercio()!=null) {
                            if (!translogAllAdquirer.getIdComercio().equals(translog.getIdComercio()))
                                continue;
                        }

                        //Filtrar la seleccion de la transaccion por adquirente y emisor
                        if (translog.getField44().equals(nombreAdquirenteActual) &&
                                translog.getIssuerName().equals(nombreActualEmisor) &&
                                !translog.isAlreadyPrinted())
                        {

                            //Nombre del emisor
                            if (!printNameIssuer) {
                                setTextPrint(" " + nombreActualEmisor, paint, BOLD_ON, canvas, S_MEDIUM);
                                printNameIssuer = true;
                            }

                            //Fecha por trans
                            if (!translog.getDatePrint().equals(fechaTransActual))
                                printDateTransxIssuer = false;

                            if (!printDateTransxIssuer) {
                                if (!translog.getDatePrint().equals(fechaTransActual)) {
                                    //fechaTransActual = translogIssuer.getDatePrint();
                                    setTextPrint("  " + checkNull(translog.getDatePrint()), paint, BOLD_ON, canvas, S_SMALL);
                                } else {
                                    setTextPrint("  " + checkNull(fechaTransActual), paint, BOLD_ON, canvas, S_SMALL);
                                }
                                printDateTransxIssuer = true;
                            }

                            //monto total por transaccion
                            amount = translog.getAmmount0() + translog.getAmmountXX() + translog.getAmmountIVA() +
                                    translog.getTipAmout() + translog.getAmmountService() + translog.getAmmountCashOver() +
                                    translog.getMontoFijo();

                            //Datos de la transaccion
                            setTextPrint(setTextColumn("  " + translog.getTraceNo() + "  " + translog.getPan(),
                                    anulada(translog) + " $ " + PAYUtils.getStrAmount(amount) + "  " +
                                            imprimirTipoTrans(translog), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);


                            //Solo se tiene en cuenta las trans que no estan anuladas
                            if (!translog.getIsVoided() && !translog.isTarjetaCierre()) {
                                //
                                subTotal += amount;
                                ivaAmount += translog.getAmmountIVA();
                                serviceAmount += translog.getAmmountService();
                                tipAmount += translog.getTipAmout();
                                montoFijo += translog.getMontoFijo();

                                //Ponderado para el Valor Total del Adquirente
                                subTotalSubTotal += subTotal;
                                ivaAmountSubTotal += ivaAmount;
                                serviceAmountSubTotal += serviceAmount;
                                tipAmountSubTotal += tipAmount;
                                montoFijoSubTotal += montoFijo;

                                contTotalTransAcq++;
                                contTransAcq++;
                                contTransEmisor++;
                            }

                            translog.setAlreadyPrinted(true);
                        }
                    }
/*######################################## Fin Lista Detalle Trans ################################################*/
                }
/*====================================== Fin Lista Emisor ==================================================================*/
                imprimirSubTotal(translogByAdquirer, paint, canvas);

                contTransEmisor = 0;

                totalTempAmount += subTotal;
                totalTempIva += ivaAmount;
                totalTempServiceAmount += serviceAmount;
                totalTempTipAmount += tipAmount;
                totalTempMontoFijo += montoFijo;

                limpiarMontoSubtotales();
                limpiarMontoSubTotales2();

                totalAmount += totalTempAmount;
                totalIva += totalTempIva;
                totalServiceAmount += totalTempServiceAmount;
                totalTipAmount += totalTempTipAmount;
                totalMontoFijo += totalTempMontoFijo;

                //Totales por Adquirente
                imprimirTotal(translogByAdquirer, paint, canvas);

                //Totales por Adquirente por comercio
                imprimirTotalxComercio(translogByAdquirer, paint, canvas);

                contTransAcq = 0;
                //limpiarMontoSubTotales2();

                granTotal += totalAmount;
                granTotalIva += totalIva;
                granTotalService += totalServiceAmount;
                granTotalTip += totalTipAmount;
                granTotalMontoFijo += totalMontoFijo;

                limpiarMontoTotales();
                limpiarTotales();

                printLine(paint, canvas);
            }
        }
    }

    private void imprimirSubTotal(TransLogData translogByAdquirer, Paint paint, PrintCanvas canvas){
        println(paint, canvas);
        if (contTransEmisor != 0) {
            setTextPrint(setTextColumn("", "---------------", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(setTextColumn("  SUB TOTAL:          " + contTransEmisor, "$ " + PAYUtils.getStrAmount(subTotal) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            if (GetAmount.checkIVA())
                setTextPrint(setTextColumn("  " + checkNull(tconf.getLABEL_IMPUESTO()) + ":", "$ " +
                        PAYUtils.getStrAmount(ivaAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            if (GetAmount.checkService())
                setTextPrint(setTextColumn("  " + checkNull(tconf.getLABEL_SERVICIO()) + ":", "$ " +
                        PAYUtils.getStrAmount(serviceAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            if (GetAmount.checkTip())
                setTextPrint(setTextColumn("  " + checkNull(tconf.getLABEL_PROPINA()) + ":", "$ " +
                        PAYUtils.getStrAmount(tipAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            if (translogByAdquirer.getTipoMontoFijo() != null) {
                //if (translogByAdquirer.getTipoMontoFijo().equals(AUTOMATICO)) {
                //if (translogByAdquirer.getMontoFijo() != 0) {
                setTextPrint(setTextColumn("  TARIFA: ", "$ " + PAYUtils.getStrAmount(montoFijo) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                //}
                //}
            }
        }
        println(paint, canvas);
    }

    private void imprimirTotal(TransLogData translogByAdquirer, Paint paint, PrintCanvas canvas){

        setTextPrint(setTextColumn("", "===============", S_SMALL), paint, BOLD_OFF, canvas, S_SMALL);

        setTextPrint(setTextColumn("VALOR TOTAL:        " + contTransAcq, "$ " + PAYUtils.getStrAmount(totalTempAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (GetAmount.checkIVA())
            setTextPrint(setTextColumn(checkNull(tconf.getLABEL_IMPUESTO()) + ":", "$ " + PAYUtils.getStrAmount(totalTempIva) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (GetAmount.checkService())
            setTextPrint(setTextColumn(checkNull(tconf.getLABEL_SERVICIO()) + ":", "$ " + PAYUtils.getStrAmount(totalTempServiceAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (GetAmount.checkTip())
            setTextPrint(setTextColumn(checkNull(tconf.getLABEL_PROPINA()) + ":", "$ " + PAYUtils.getStrAmount(totalTempTipAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        if (translogByAdquirer.getTipoMontoFijo() != null) {
            //if (translogByAdquirer.getTipoMontoFijo().equals(AUTOMATICO)) {
                //if (translogByAdquirer.getMontoFijo() != 0) {
            setTextPrint(setTextColumn("TARIFA: ", "$ " + PAYUtils.getStrAmount(totalTempMontoFijo) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                //}
            //}
        }
    }

    private void imprimirTotalxComercio(TransLogData translogByAdquirer, Paint paint, PrintCanvas canvas){

        if (translogByAdquirer.isMulticomercio()) {
            println(paint, canvas);
            setTextPrint(setTextColumn("", "===============", S_SMALL), paint, BOLD_OFF, canvas, S_SMALL);
            setTextPrint(setTextColumn("TOTAL COMERCIO:     " + contTransAcq, "$ " + PAYUtils.getStrAmount(totalTempAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            if (GetAmount.checkIVA())
                setTextPrint(setTextColumn(checkNull(tconf.getLABEL_IMPUESTO()) + ":", "$ " + PAYUtils.getStrAmount(totalTempIva) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            if (GetAmount.checkService())
                setTextPrint(setTextColumn(checkNull(tconf.getLABEL_SERVICIO()) + ":", "$ " + PAYUtils.getStrAmount(totalTempServiceAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            if (GetAmount.checkTip())
                setTextPrint(setTextColumn(checkNull(tconf.getLABEL_PROPINA()) + ":", "$ " + PAYUtils.getStrAmount(totalTempTipAmount) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

            if (translogByAdquirer.getTipoMontoFijo() != null) {
                //if (translogByAdquirer.getTipoMontoFijo().equals(AUTOMATICO)) {
                    //if (translogByAdquirer.getMontoFijo() != 0) {
                setTextPrint(setTextColumn("TARIFA: ", "$ " + PAYUtils.getStrAmount(totalTempMontoFijo) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                    //}
                //}
            }
        }
    }

    private void printVersionVoid(boolean isRePrint, Paint paint, PrintCanvas canvas) {
        if (isRePrint) {
            setTextPrint(setCenterText("XXX   COPIA   XXX", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            println(paint, canvas);
        }

        setTextPrint(setTextColumn(" ", "Version " + checkNull(VERSION).trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);
        println(paint, canvas);
        println(paint, canvas);
    }

    private void limpiarMontoSubtotales(){
        subTotal = 0;
        ivaAmount = 0;
        serviceAmount = 0;
        tipAmount = 0;
        montoFijo = 0;
    }

    private void limpiarMontoSubTotales2(){
        subTotalSubTotal = 0;
        ivaAmountSubTotal = 0;
        serviceAmountSubTotal = 0;
        tipAmountSubTotal = 0;
        montoFijoSubTotal = 0;
    }

    private void limpiarMontoTotales(){
        totalTempAmount = 0;
        totalTempIva = 0;
        totalTempServiceAmount = 0;
        totalTempTipAmount = 0;
        totalTempMontoFijo = 0;
    }

    private void limpiarTotales(){
        totalAmount = 0;
        totalIva = 0;
        totalServiceAmount = 0;
        totalTipAmount = 0;
        totalMontoFijo = 0;
    }

    private String checkNull(String strText) {
        if (strText == null) {
            strText = "   ";
        }
        return strText;
    }

    private void printDateAndDues(Paint paint, PrintCanvas canvas) {

        String numCoutas = "0";

        if (dataTrans.getNumCuotas() > 1) {
            numCoutas = dataTrans.getNumCuotas() + "";
        }
        if (numCoutas.equals("0")) {
            setTextPrint(setTextColumn(getFormatDateAndTime(checkNull(dataTrans.getDatePrint()), checkNull(dataTrans.getLocalTime())),
                    " ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn(getFormatDateAndTime(checkNull(dataTrans.getDatePrint()), checkNull(dataTrans.getLocalTime())),
                    "COUTAS: " + numCoutas, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
    }

    private void printSecondHeader(String lote, String term, String id_com, Paint paint, PrintCanvas canvas) {
        setTextPrint(checkNumCharacters("LOTE:" + lote.trim() + " TERM:" + term.trim() + " ID COM:" + id_com.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
        printLine(paint, canvas);
        println(paint, canvas);
    }

    private void print_AP_REF(String AP, String REF, Paint paint, PrintCanvas canvas) {
        setTextPrint(setTextColumn("AP: " + AP.trim(), "REF: " + REF.trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
    }

    private void print_expires_RRN_tipo(String expires, String RRN, String type, Paint paint, PrintCanvas canvas) {
        setTextPrint(checkNumCharacters("VENCE:" + expires.substring(2) + "/" + expires.substring(0, 2) + " RRN:" + RRN.trim() + " TIPO:" + type.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void print_Rnn(String RRN, Paint paint, PrintCanvas canvas) {
        setTextPrint(checkNumCharacters(" RRN: " + RRN.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printAID(String AID, Paint paint, PrintCanvas canvas) {
        if (isICC ) {
            setTextPrint("AID: " + checkNumCharacters(AID.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
        else if(isNFC){
            if (dataTrans.getAIDName().equals("") && AID.trim().substring(0,14).equals("A0000000031010")){
                setTextPrint("AID NAME: VISA CREDITO",paint, BOLD_ON, canvas, S_SMALL);
            }else {
                setTextPrint("AID NAME: "+dataTrans.getAIDName(),paint, BOLD_ON, canvas, S_SMALL);
            }
            setTextPrint("AID: " + checkNumCharacters(AID.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

    }

    private void printAccount(Paint paint, PrintCanvas canvas) {
        if (isICC) {
            if (dataTrans.getTypeAccount() != null)
                setTextPrint(checkNumCharacters(dataTrans.getTypeAccount().trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
    }

    private void printCard(String card, Paint paint, PrintCanvas canvas) {
        String typeAccount;
        if (isICC)
            typeAccount = "C";
        else if (isNFC) {
            typeAccount = "T";
        } else {
            if (isFallback)
                typeAccount = "F";
            else
                typeAccount = "S";
        }

        setTextPrint(setCenterText(card.trim() + " (" + typeAccount + ")", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
    }

    private void printDateAndTime(String data, int typeFont, boolean isBold, Paint paint, PrintCanvas canvas) {
        setTextPrint(checkNumCharacters(data, typeFont), paint, isBold, canvas, typeFont);
    }

    private String getFormatDateAndTime(String date, String time) {
        String newtime = PAYUtils.StringPattern(time.trim(), "HHmmss", "HH:mm");
        return "FECHA: " + date.trim() + "  HORA: " + newtime;
    }

    private void printAmount(long amount, long tip, boolean isTip, long total, String typeCoin, Paint paint, PrintCanvas canvas) {
        if (typeCoin.equals(LOCAL)) {
            typeCoin = "LOCAL.";
        } else {
            typeCoin = "DOLAR";
        }
        println(paint, canvas);

        if (isTip) {
            setTextPrint(setTextColumn("MONTO          " + typeCoin.trim(), formatAmountLess(amount), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            setTextPrint(setTextColumn("PROPINA        " + typeCoin.trim(), formatAmountLess(tip), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            printLineResult(paint, canvas);
        }

        setTextPrint(setTextColumn("TOTAL          " + typeCoin.trim(), formatAmountLess(total), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
        println(paint, canvas);
    }

    private void printAmountVoid(long total, String typeCoin, Paint paint, PrintCanvas canvas) {
        if (typeCoin.equals(LOCAL)) {
            typeCoin = "LOCAL.";
        } else {
            typeCoin = "DOLAR";
        }
        println(paint, canvas);
        println(paint, canvas);
        if (dataTrans.isTip()) {
            setTextPrint(setTextColumn("MONTO          -" + typeCoin.trim(), formatAmountLess(dataTrans.getAmount()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            setTextPrint(setTextColumn("PROPINA        -" + typeCoin.trim(), formatAmountLess(dataTrans.getTipAmout()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            printLineResult(paint, canvas);
        }
        setTextPrint(setTextColumn("TOTAL          -" + typeCoin.trim(), formatAmountLess(total), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
        println(paint, canvas);
    }

    private void printSignature(String labelCard, boolean isCopy, Paint paint, PrintCanvas canvas) {
        if (!isCopy) {
            if (TMConfig.getInstance().isBanderaMessageFirma()) {
                if (dataTrans.getEntryMode().equals(MODE_CTL + CapPinPOS())) {
                    if (MasterControl.CTL_SIGN)
                        setTextPrint("FIRMA X........................", paint, BOLD_OFF, canvas, S_MEDIUM);
                } else {
                    setTextPrint("FIRMA X........................", paint, BOLD_OFF, canvas, S_MEDIUM);
                }
            }

            if (!labelCard.equals("---"))//Con esto evitamos imprimir la cadena "---" que se agrega cuando el labelCard es null
                if (labelCard.length() > 0) {
                    setTextPrint(setCenterText(checkNumCharacters(labelCard.trim(), S_MEDIUM), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                    println(paint, canvas);
                }

            if (TMConfig.getInstance().isBanderaMessageDoc())
                setTextPrint(checkNumCharacters("DOC: ", S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

            if (TMConfig.getInstance().isBanderaMessageTel())
                setTextPrint(checkNumCharacters("TEL:", S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            println(paint, canvas);
        }
    }

    private void printLine(String character, Paint paint, PrintCanvas canvas) {
        StringBuilder dat = new StringBuilder();
        for (int i = 0; i < 45; i++) {
            dat.append(character);
        }
        setTextPrint(dat.toString(), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printLine(Paint paint, PrintCanvas canvas) {
        setTextPrint("---------------------------------------------", paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printLineResult(Paint paint, PrintCanvas canvas) {
        println(paint, canvas);
        setTextPrint("                  =======", paint, BOLD_OFF, canvas, S_BIG);
        println(paint, canvas);
    }

    private String setTextColumn(String columna1, String columna2, int size) {
        String aux = "";
        String auxText = columna2;
        auxText = setRightText(auxText, size);
        String auxText2 = columna1;

        if (auxText2.length() < auxText.length())
            aux = auxText.substring(auxText2.length());

        auxText2 += aux;

        return auxText2;
    }

    private String checkNumCharacters(String data, int size) {
        String dataPrint = "";
        int lenData = 0;

        lenData = data.length();

        switch (size) {
            case S_SMALL:
                if (lenData > MAX_CHAR_SMALL) {
                    dataPrint = data.substring(0, MAX_CHAR_SMALL);
                } else {
                    dataPrint = data;
                }
                break;

            case S_MEDIUM:
                if (lenData > MAX_CHAR_MEDIUM) {
                    dataPrint = data.substring(0, MAX_CHAR_MEDIUM);
                } else {
                    dataPrint = data;
                }
                break;

            case S_BIG:
                if (lenData > MAX_CHAR_BIG) {
                    dataPrint = data.substring(0, MAX_CHAR_BIG);
                } else {
                    dataPrint = data;
                }
                break;

        }
        return dataPrint;
    }

    private void println(Paint paint, PrintCanvas canvas) {
        setTextPrint("                                             ", paint, BOLD_ON, canvas, S_SMALL);
    }

    private void setTextPrint(String data, Paint paint, boolean bold, PrintCanvas canvas, int size) {
        Typeface typeface = (Typeface.MONOSPACE);
        data = checkNumCharacters(data, size);
        canvas.drawBitmap(drawText(data, (float) size, bold, typeface), paint);
    }

    private void setTextPrintREV(String data, Paint paint, boolean bold, PrintCanvas canvas, int size) {
        Typeface typeface = (Typeface.MONOSPACE);
        canvas.drawBitmap(drawTextREV(data, (float) size, bold, typeface), paint);
    }

    private String setCenterText(String data, int size) {
        data = padLeft(checkNumCharacters(data.trim(), size), size);
        return data;
    }

    private String setRightText(String data, int size) {
        String dataFinal = "";
        int len1 = 0;
        switch (size) {
            case S_SMALL:
                len1 = MAX_CHAR_SMALL - data.length();
                break;
            case S_MEDIUM:
                len1 = MAX_CHAR_MEDIUM - data.length();
                break;
            case S_BIG:
                len1 = MAX_CHAR_BIG - data.length();
                break;
        }

        for (int i = 0; i < len1; i++) {
            dataFinal += " ";
        }

        dataFinal += data;
        return dataFinal;
    }

    private String formatAmountLess(long valor) {

        String auxText;

        if (String.valueOf(valor).length() == 1)
            auxText = ISOUtil.decimalFormat("0" + String.valueOf(valor));
        else
            auxText = ISOUtil.decimalFormat(String.valueOf(valor));

        return auxText;
    }

    private String padLeft(String data, int size) {

        String dataFinal = "";
        int len1 = 0;

        switch (size) {
            case S_SMALL:
                len1 = MAX_CHAR_SMALL - data.length();
                break;
            case S_MEDIUM:
                len1 = MAX_CHAR_MEDIUM - data.length();
                break;
            case S_BIG:
                len1 = MAX_CHAR_BIG - data.length();
                break;
        }

        for (int i = 0; i < len1 / 2; i++) {
            dataFinal += " ";
        }
        dataFinal += data;

        return dataFinal;
    }

    private Bitmap drawText(String text, float textSize, boolean bold, Typeface typeface) {

        // Get text dimensions
        TextPaint textPaint = new TextPaint(ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(typeface);
        textPaint.setFakeBoldText(bold);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint, 400, Layout.Alignment.ALIGN_NORMAL, 40.0f, 20.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(400, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    private Bitmap drawTextREV(String text, float textSize, boolean bold, Typeface typeface) {

        // Get text dimensions
        TextPaint textPaint = new TextPaint(ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(typeface);
        textPaint.setFakeBoldText(bold);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint, 400, Layout.Alignment.ALIGN_NORMAL, 40.0f, 20.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(400, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    public int printSettle(final TransLogData data) {
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret;

        printer = Printer.getInstance();
        if (printer == null) {
            return Tcode.T_sdk_err;
        }

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        //结算单
        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.WANNING, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 1, true);
        Bitmap image = PAYUtils.getLogoByBankId(mContext, cfg.getBankid());
        canvas.drawBitmap(image, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.SETTLE_SUMMARY, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.MERCHANT_NAME + "\n" + cfg.getMerchName(), paint);
        canvas.drawText(PrintRes.CH.MERCHANT_ID + "\n" + cfg.getMerchID(), paint);
        canvas.drawText(PrintRes.CH.TERNIMAL_ID + "\n" + cfg.getTermID(), paint);
        String operNo = data.getOprNo() < 10 ? "0" + data.getOprNo() : data.getOprNo() + "";
        canvas.drawText(PrintRes.CH.OPERATOR_NO + "    " + operNo, paint);
        if (!PAYUtils.isNullWithTrim(data.getBatchNo())) {
            canvas.drawText(PrintRes.CH.BATCH_NO + data.getBatchNo(), paint);
        }
        if (!PAYUtils.isNullWithTrim(data.getLocalDate()) && !PAYUtils.isNullWithTrim(data.getLocalTime())) {
            String timeStr = PAYUtils.StringPattern(data.getLocalDate() + data.getLocalTime(), "yyyyMMddHHmmss", "yyyy/MM/dd  HH:mm:ss");
            canvas.drawText(PrintRes.CH.DATE_TIME + "\n          " + timeStr, paint);
        }
        printLine(paint, canvas);
        canvas.drawText(PrintRes.CH.SETTLE_LIST, paint);
        printLine(paint, canvas);
        canvas.drawText(PrintRes.CH.SETTLE_INNER_CARD, paint);
        List<TransLogData> list = TransLog.getInstance().getData();
        int saleAmount = 0;
        int saleSum = 0;
        int quickAmount = 0;
        int quickSum = 0;
        int voidAmount = 0;
        int voidSum = 0;
        for (int i = 0; i < list.size(); i++) {
            TransLogData tld = list.get(i);
            if (tld.getEName().equals(Trans.Type.SALE)) {
                saleAmount += tld.getAmount();
                saleSum++;
            }
            if (tld.getEName().equals(Trans.Type.QUICKPASS)) {
                if (tld.getAAC() == FinanceTrans.AAC_ARQC) {
                    saleAmount += tld.getAmount();
                    saleSum++;
                } else {
                    quickAmount += tld.getAmount();
                    quickSum++;
                }
            }
            if (tld.getEName().equals(Trans.Type.VOID)) {
                voidAmount += tld.getAmount();
                voidSum++;
            }
        }

        if (saleSum != 0) {
            canvas.drawText(formatTranstype(Trans.Type.SALE) + "           " + saleSum + "               " + PAYUtils.getStrAmount(saleAmount), paint);
        }
        if (quickSum != 0) {
            canvas.drawText("电子现金消费/SALE" + "           " + quickSum + "               " + PAYUtils.getStrAmount(quickAmount), paint);
        }
        if (voidSum != 0) {
            canvas.drawText(formatTranstype(Trans.Type.VOID) + "           " + voidSum + "               " + PAYUtils.getStrAmount(voidAmount), paint);
        }

        printLine(paint, canvas);
        canvas.drawText(PrintRes.CH.SETTLE_OUTER_CARD, paint);

        canvas.drawText("\n\n\n\n\n", paint);

        //明细单
        canvas.drawText(PrintRes.CH.WANNING, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 1, true);
        canvas.drawBitmap(image, paint);
        setFontStyle(paint, 2, false);
        printLine(paint, canvas);
        canvas.drawText(PrintRes.CH.SETTLE_DETAILS, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.SETTLE_DETAILS_LIST_CH, paint);
        setFontStyle(paint, 1, false);
        canvas.drawText(PrintRes.CH.SETTLE_DETAILS_LIST_EN, paint);
        setFontStyle(paint, 2, false);
        printLine(paint, canvas);

        //添加明细
        List<TransLogData> list1 = TransLog.getInstance().getData();
        for (int i = 0; i < list1.size(); i++) {
            TransLogData tld = list1.get(i);
            if (tld.getEName().equals(Trans.Type.SALE) || tld.getEName().equals(Trans.Type.QUICKPASS) || tld.getEName().equals(Trans.Type.VOID)) {
                canvas.drawText(tld.getTraceNo() + "     " +
                        formatDetailsType(tld) + "    " +
                        formatDetailsAuth(tld) + "    " +
                        PAYUtils.getStrAmount(tld.getAmount()) + "   " +
                        tld.getPan(), paint);
            }
        }
        ret = printData(canvas);
        return ret;
    }

    public int printDetails() {
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;
        if (TransLog.getInstance(idAcquirer).getSize() == 0) {
            ret = Tcode.T_print_no_log_err;
        } else {
            printer = Printer.getInstance();
            if (printer == null) {
                ret = Tcode.T_sdk_err;
            } else {
                PrintCanvas canvas = new PrintCanvas();
                Paint paint = new Paint();
                setFontStyle(paint, 2, true);
                canvas.drawText(PrintRes.CH.WANNING, paint);
                printLine(paint, canvas);
                setFontStyle(paint, 3, true);
                canvas.drawText("                     " + PrintRes.CH.DETAILS, paint);
                setFontStyle(paint, 2, true);
                canvas.drawText(PrintRes.CH.MERCHANT_NAME + "\n" + cfg.getMerchName(), paint);
                canvas.drawText(PrintRes.CH.MERCHANT_ID + "\n" + cfg.getMerchID(), paint);
                canvas.drawText(PrintRes.CH.TERNIMAL_ID + "\n" + cfg.getTermID(), paint);
                canvas.drawText(PrintRes.CH.BATCH_NO + "\n" + cfg.getBatchNo(), paint);
                canvas.drawText(PrintRes.CH.DATE_TIME + "\n" + PAYUtils.getSysTime(), paint);
                printLine(paint, canvas);
                int num = TransLog.getInstance(idAcquirer).getSize();
                for (int i = 0; i < num; i++) {
                    setFontStyle(paint, 1, true);
                    TransLogData data = TransLog.getInstance(idAcquirer).get(i);
                    if (data.isScan()) {
                        canvas.drawText(PrintRes.CH.SCANCODE + PAYUtils.getSecurityNum(data.getPan(), 6, 3), paint);
                    } else {
                        if (data.isICC()) {
                            canvas.drawText(PrintRes.CH.CARD_NO + PAYUtils.getSecurityNum(data.getPan(), 6, 3) + " I", paint);
                        } else if (data.isNFC()) {
                            canvas.drawText(PrintRes.CH.CARD_NO + PAYUtils.getSecurityNum(data.getPan(), 6, 3) + " C", paint);
                        } else {
                            canvas.drawText(PrintRes.CH.CARD_NO + PAYUtils.getSecurityNum(data.getPan(), 6, 3) + " S", paint);
                        }
                    }
                    canvas.drawText(PrintRes.CH.TRANS_TYPE + formatTranstype(data.getEName()), paint);
                    canvas.drawText(PrintRes.CH.AMOUNT + PrintRes.CH.RMB + PAYUtils.getStrAmount(data.getAmount()), paint);
                    canvas.drawText(PrintRes.CH.VOUCHER_NO + data.getTraceNo(), paint);
                    printLine(paint, canvas);
                }
                ret = printData(canvas);
                if (printer != null) {
                    printer = null;
                }
            }
        }
        return ret;
    }

    public int print10test(final TransLogData data, final boolean isRePrint) {
        this.printTask = new PrintTask();
        final int ret;
        boolean isICC = data.isICC();
        boolean isNFC = data.isNFC();
        boolean isScan = data.isScan();

        printer = Printer.getInstance();
        if (printer == null) {
            return Tcode.T_sdk_err;
        }

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.WANNING, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 1, true);
        Bitmap image = PAYUtils.getLogoByBankId(mContext, cfg.getBankid());
        canvas.drawBitmap(image, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.MERCHANT_COPY, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.MERCHANT_NAME + "\n" + cfg.getMerchName(), paint);
        canvas.drawText(PrintRes.CH.MERCHANT_ID + "\n" + cfg.getMerchID(), paint);
        canvas.drawText(PrintRes.CH.TERNIMAL_ID + "\n" + cfg.getTermID(), paint);
        String operNo = data.getOprNo() < 10 ? "0" + data.getOprNo() : data.getOprNo() + "";
        canvas.drawText(PrintRes.CH.OPERATOR_NO + "    " + operNo, paint);
        printLine(paint, canvas);
        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.ISSUER, paint);
        canvas.drawText(PrintRes.CH.ACQUIRER, paint);
        if (isScan) {
            canvas.drawText(PrintRes.CH.SCANCODE, paint);
        } else {
            canvas.drawText(PrintRes.CH.CARD_NO, paint);
        }
        setFontStyle(paint, 3, true);
        if (isICC) {
            canvas.drawText("     " + PAYUtils.getSecurityNum(data.getPan(), 6, 3) + " I", paint);
        } else if (isNFC) {
            canvas.drawText("     " + PAYUtils.getSecurityNum(data.getPan(), 6, 3) + " C", paint);
        } else if (isScan) {
            canvas.drawText("     " + PAYUtils.getSecurityNum(data.getPan(), 6, 3), paint);
        } else {
            canvas.drawText("     " + PAYUtils.getSecurityNum(data.getPan(), 6, 3) + " S", paint);
        }
        setFontStyle(paint, 2, false);
        canvas.drawText(PrintRes.CH.TRANS_TYPE, paint);
        setFontStyle(paint, 3, true);
        canvas.drawText(formatTranstype(data.getEName()), paint);
        setFontStyle(paint, 2, false);
        if (!PAYUtils.isNullWithTrim(data.getExpDate())) {
            canvas.drawText(PrintRes.CH.CARD_EXPDATE + "       " + data.getExpDate(), paint);
        }
        printLine(paint, canvas);
        setFontStyle(paint, 2, false);
        if (!PAYUtils.isNullWithTrim(data.getBatchNo())) {
            canvas.drawText(PrintRes.CH.BATCH_NO + data.getBatchNo(), paint);
        }
        if (!PAYUtils.isNullWithTrim(data.getTraceNo())) {
            canvas.drawText(PrintRes.CH.VOUCHER_NO + data.getTraceNo(), paint);
        }
        if (!PAYUtils.isNullWithTrim(data.getAuthCode())) {
            canvas.drawText(PrintRes.CH.AUTH_NO + data.getAuthCode(), paint);
        }
        setFontStyle(paint, 2, false);
        if (!PAYUtils.isNullWithTrim(data.getLocalDate()) && !PAYUtils.isNullWithTrim(data.getLocalTime())) {
            String timeStr = PAYUtils.StringPattern(data.getLocalDate() + data.getLocalTime(), "yyyyMMddHHmmss", "yyyy/MM/dd  HH:mm:ss");
            canvas.drawText(PrintRes.CH.DATE_TIME + "\n          " + timeStr, paint);
        }
        if (!PAYUtils.isNullWithTrim(data.getRRN())) {
            canvas.drawText(PrintRes.CH.REF_NO + data.getRRN(), paint);
        }
        canvas.drawText(PrintRes.CH.AMOUNT, paint);
        setFontStyle(paint, 3, true);
        canvas.drawText("           " + PrintRes.CH.RMB + "     " + PAYUtils.getStrAmount(data.getAmount()) + "", paint);
        printLine(paint, canvas);
        setFontStyle(paint, 1, false);
        if (!PAYUtils.isNullWithTrim(data.getRefence())) {
            canvas.drawText(PrintRes.CH.REFERENCE + "\n" + data.getRefence(), paint);
        }
        //追加ICC数据
        if (data.getICCData() != null) {
            printAppendICCData(data.getICCData(), canvas, paint);
        }
        if (isRePrint) {
            setFontStyle(paint, 3, true);
            canvas.drawText(PrintRes.CH.REPRINT, paint);
        }
        setFontStyle(paint, 3, true);
        canvas.drawText("       " + PrintRes.CH.CARDHOLDER_SIGN + "\n\n\n", paint);
        printLine(paint, canvas);
        setFontStyle(paint, 1, false);
        canvas.drawText(PrintRes.CH.AGREE_TRANS + "\n", paint);

        printTask.setPrintCanvas(canvas);
        ret = printer.getStatus();
        isPrinting = false;
        num = 0;
        Logger.debug("printer.getStatus=" + ret);
        do {
            if (!isPrinting) {
                isPrinting = true;
                printer.startPrint(printTask, new PrinterCallback() {
                    @Override
                    public void onResult(int i, PrintTask printTask) {
                        Logger.debug("PrinterCallback i = " + i);
                        isPrinting = false;
                        num++;
                    }
                });
            }
        } while (num < 10);
        return ret;
    }

    private int printData(PrintCanvas pCanvas) {
        final CountDownLatch latch = new CountDownLatch(1);
        printer = Printer.getInstance();
        int ret = printer.getStatus();
        Logger.debug("打印机状态：" + ret);
        if (Printer.PRINTER_STATUS_PAPER_LACK == ret) {
            Logger.debug("打印机缺纸，提示用户装纸");
            transUI.handling(60 * 1000, Tcode.Status.printer_lack_paper);
            long start = SystemClock.uptimeMillis();
            while (true) {
                if (SystemClock.uptimeMillis() - start > 60 * 1000) {
                    ret = Printer.PRINTER_STATUS_PAPER_LACK;
                    break;
                }
                if (printer.getStatus() == Printer.PRINTER_OK) {
                    ret = Printer.PRINTER_OK;
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        Logger.debug("printer task interrupted");
                    }
                }
            }
        }
        Logger.debug("开始打印");
        if (ret == Printer.PRINTER_OK) {
            transUI.handling(60 * 1000, Tcode.Status.printing_recept);
            printTask.setPrintCanvas(pCanvas);
            printer.startPrint(printTask, new PrinterCallback() {
                @Override
                public void onResult(int i, PrintTask printTask) {
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                Logger.error("Exception" + e.toString());
                Thread.currentThread().interrupt();
            }
        }
        return ret;
    }

    private int checkPrinterStatus() {
        long t0 = System.currentTimeMillis();
        int ret;
        while (true) {
            if (System.currentTimeMillis() - t0 > 30000) {
                ret = -1;
                break;
            }
            ret = printer.getStatus();
            Logger.debug("printer.getStatus() ret = " + ret);
            if (ret == Printer.PRINTER_OK) {
                Logger.debug("printer.getStatus()=Printer.PRINTER_OK");
                Logger.debug("打印机状态正常");
                break;
            } else if (ret == -3) {
                Logger.debug("printer.getStatus()=Printer.PRINTER_STATUS_PAPER_LACK");
                Logger.debug("提示用户装纸...");
                break;
            } else if (ret == Printer.PRINTER_STATUS_BUSY) {
                Logger.debug("printer.getStatus()=Printer.PRINTER_STATUS_BUSY");
                Logger.debug("打印机忙");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Logger.error("Exception" + e.toString());
                    Thread.currentThread().interrupt();
                }
            } else {
                break;
            }
        }
        return ret;
    }

    private String formatTranstype(String type) {
        int index = 0;
        for (int i = 0; i < PrintRes.TRANSEN.length; i++) {
            if (PrintRes.TRANSEN[i].equals(type)) {
                index = i;
            }
        }
        if (Locale.getDefault().getLanguage().equals("zh")) {
            return PrintRes.TRANSCH[index] + "(" + type + ")";
        } else {
            return type;
        }
    }

    private String formatDetailsType(TransLogData data) {
        if (data.isICC()) {
            return "I";
        } else if (data.isNFC()) {
            return "C";
        } else {
            return "S";
        }
    }

    private String formatDetailsAuth(TransLogData data) {
        if (data.getAuthCode() == null) {
            return "000000";
        } else {
            return data.getAuthCode();
        }
    }

    private void setFontStyle(Paint paint, int size, boolean isBold) {
        if (isBold) {
            paint.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            paint.setTypeface(Typeface.SERIF);
        }
        switch (size) {
            case 0:
                break;
            case 1:
                paint.setTextSize(15F);
                break;
            case 2:
                paint.setTextSize(22F);
                break;
            case 3:
                paint.setTextSize(30F);
                break;
            default:
                break;
        }
    }

    private void printAppendICCData(byte[] ICCData, PrintCanvas canvas, Paint paint) {
        byte[] temp = new byte[256];
        int len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x4F, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("AID: " + ISOUtil.byte2hex(temp, 0, len), paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x50, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("LABLE: " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(temp, 0, len)), paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x9F26, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("TC: " + ISOUtil.byte2hex(temp, 0, len), paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x5F34, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("PanSN: " + ISOUtil.byte2hex(temp, 0, len), paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x95, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("TVR: " + ISOUtil.byte2hex(temp, 0, len), paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x9B, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("TSI: " + ISOUtil.byte2hex(temp, 0, len), paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x9F36, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("ATC: " + ISOUtil.byte2hex(temp, 0, len) + "", paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x9F33, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("TermCap: " + ISOUtil.byte2hex(temp, 0, len) + "", paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x9F09, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("AppVer: " + ISOUtil.byte2hex(temp, 0, len) + "", paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x9F34, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("CVM: " + ISOUtil.byte2hex(temp, 0, len) + "", paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x9F10, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("IAD: " + ISOUtil.byte2hex(temp, 0, len) + "", paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x82, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("AIP: " + ISOUtil.byte2hex(temp, 0, len) + "", paint);
        }
        len = PAYUtils.get_tlv_data(ICCData, ICCData.length, 0x9F1E, temp, false);
        if (!PAYUtils.isNullWithTrim(ISOUtil.trimf(ISOUtil.byte2hex(temp, 0, len)))) {
            canvas.drawText("IFD: " + ISOUtil.byte2hex(temp, 0, len) + "", paint);
        }
    }

    /**
     * Datafast
     */
    private void printHeader(String text1, String text2, String text3, String text4, Paint paint, PrintCanvas canvas) {
        setTextPrint(setCenterText(text1.trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint(setCenterText(text2.trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint(setCenterText(text3.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText("Telefono: " + text4.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void print_CardInfo(String Pan, String typeTrans, String data, Paint paint, PrintCanvas canvas, boolean isCopy) {

        switch (typeTrans) {
            case Trans.Type.ELECTRONIC:
            case Trans.Type.ELECTRONIC_DEFERRED:
            case Trans.Type.PAYBLUE:
            case Trans.Type.PAYCLUB:
                break;
            default:
                data = "V: XX/XX";
                break;
        }

        if (isCopy) {
            switch (typeTrans) {
                case Trans.Type.ELECTRONIC:
                case Trans.Type.ELECTRONIC_DEFERRED:
                case Trans.Type.PAYBLUE:
                case Trans.Type.PAYCLUB:
                    break;
                default:
                    data = "";
                    break;
            }
        }
        setTextPrint(setTextColumn("TARJETA: " + checkNull(Pan), checkNull(data), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void print_Lote_Ref(String Lote, String Ref, Paint paint, PrintCanvas canvas) {
        setTextPrint(setTextColumn("LOTE#: " + Lote, "REF: " + Ref, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void print_Acquirer(String Acquirer, String MID_InterOper,Paint paint, PrintCanvas canvas, boolean isCopy) {
        if (!isCopy) {
            if (MID_InterOper!=null)
                setTextPrint(setTextColumn("ADQUIRENTE:", MID_InterOper+" "+Acquirer, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            else
                setTextPrint(setTextColumn("ADQUIRENTE:", Acquirer, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
    }

    private void print_DateAndTime(TransLogData datatrans, Paint paint, PrintCanvas canvas, boolean isTotalReport) {

        try {
            if (isTotalReport) {
                setTextPrint(setTextColumn(PAYUtils.getDay() + "/" + PAYUtils.getMonth() + "/" + String.valueOf(PAYUtils.getYear()).substring(2),
                        PAYUtils.StringPattern(PAYUtils.getLocalTime().trim(), "HHmmss", "HH:mm"), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            } else {
                if (datatrans.getLocalDate() != null && datatrans.getLocalTime() != null) {
                    setTextPrint(setTextColumn("FECHA: " + formato2Fecha(datatrans.getLocalDate()),
                            "HORA: " + formato2Hora(datatrans.getLocalTime()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                } else {
                    setTextPrint(setTextColumn("FECHA: " + PAYUtils.getDay() + "/" + PAYUtils.getMonth() + "/" + String.valueOf(PAYUtils.getYear()).substring(2),
                            "HORA: " + PAYUtils.StringPattern(PAYUtils.getLocalTime().trim(), "HHmmss", "HH:mm"), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }
            }
        } catch (Exception e) {
        }
    }

    private void printAmountDatafast(long Amount0, long AmountXX, long AmountIVA, long TipAmount, long AmountService, long CashOverAmount, long montoFijo,Paint paint, PrintCanvas canvas) {
        println(paint, canvas);

        String total="";
        String IVA = checkNull(tconf.getPORCENTAJE_MAXIMO_IMPUESTO());

        switch (dataTrans.getEName()) {
            case Trans.Type.PAGOS_VARIOS:

                setTextPrint(setTextColumn("IVA " + IVA + String.format("%21s",":US$"), "$" +
                        formatAmountLess(AmountXX), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

                printPromptsAmount(dataTrans, paint, canvas);

                total = formatAmountLess(Amount0 + AmountXX + AmountIVA + TipAmount + AmountService + CashOverAmount);
                break;
            default:
                setTextPrint(setTextColumn(String.format("%-23s","BASE CONSUMO TARIFA " + IVA ) + ":US$", "$" +
                        formatAmountLess(AmountXX), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                setTextPrint(setTextColumn(String.format("%-23s","BASE CONSUMO TARIFA 0 ") + ":US$", "$" +
                        formatAmountLess(Amount0), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                setTextPrint(setTextColumn(String.format("%-23s","SUBTOTAL CONSUMOS ")+ ":US$", "$" +
                        formatAmountLess(AmountXX + Amount0), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

                if (GetAmount.checkIVA()) {
                    setTextPrint(setTextColumn(String.format("%-23s",checkNull(tconf.getLABEL_IMPUESTO()) ) + ":US$", "$" +
                            formatAmountLess(AmountIVA), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }
                if (GetAmount.checkService()) {
                    setTextPrint(setTextColumn(String.format("%-23s",checkNull(tconf.getLABEL_SERVICIO()) ) + ":US$", "$" +
                            formatAmountLess(AmountService), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }

                if (GetAmount.checkTip()) {
                    setTextPrint(setTextColumn(String.format("%-23s",checkNull(tconf.getLABEL_PROPINA()) ) + ":US$", "$" +
                            formatAmountLess(TipAmount), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }

                if (dataTrans.getTipoMontoFijo() != null) {
                    if (dataTrans.getTipoMontoFijo().equals(AUTOMATICO)) {
                        if (dataTrans.getMontoFijo() != 0) {
                            setTextPrint(setTextColumn(String.format("%-23s","TARIFA ") + ":US$", "$" +
                                    formatAmountLess(dataTrans.getMontoFijo()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                        }
                    }
                }

                if (dataTrans.getEName().equals(Trans.Type.CASH_OVER)) {
                    setTextPrint(setTextColumn(String.format("%-23s","CASH OVER ") + ":US$", "$" +
                            formatAmountLess(CashOverAmount), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }

                printPromptsAmount(dataTrans, paint, canvas);

                if (dataTrans.getTipoMontoFijo() != null) {
                    if (dataTrans.getTipoMontoFijo().equals(AUTOMATICO)) {
                        if (dataTrans.getMontoFijo() != 0) {
                            total = formatAmountLess(Amount0 + AmountXX + AmountIVA + TipAmount + AmountService + CashOverAmount + montoFijo);
                        }
                    }else{
                        total = formatAmountLess(Amount0 + AmountXX + AmountIVA + TipAmount + AmountService + CashOverAmount);
                    }
                }
                else {
                    total = formatAmountLess(Amount0 + AmountXX + AmountIVA + TipAmount + AmountService + CashOverAmount);
                }
                break;
        }

        setTextPrint(setTextColumn(String.format("%-15s","VR. TOTAL " ) + ":US$", "$" + total,
                S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        imprimirValorFinanciacion(dataTrans, paint, canvas);
    }

    private void printAmountVoidDatafast(Paint paint, PrintCanvas canvas) {
        println(paint, canvas);
        String total = formatAmountLess(dataTrans.getAmmount0() + dataTrans.getAmmountXX() + dataTrans.getAmmountIVA() + dataTrans.getTipAmout() + dataTrans.getAmmountService() + dataTrans.getAmmountCashOver()+dataTrans.getMontoFijo());
        setTextPrint(setTextColumn("VR. TOTAL : US$", "-$" + total,
                S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
    }

    private void printHistory(boolean isCopy, boolean duplicate, Paint paint, PrintCanvas canvas, String fld57) {

        println(paint, canvas);
        duplicado(duplicate, paint, canvas);
        if (!isCopy) {
            capturaElectronica(paint, canvas);
        }

        println(paint, canvas);

        if (identificadoresActivos[ID_016].equals("016") && !isCopy) {
            imprimirVoucherRapido("***FAST CLUB***", isCopy, paint, canvas);
            imprimirPublicidad(paint, canvas);
        } else if (identificadoresActivos[ID_025].equals("025") && !isCopy) {
            imprimirVoucherRapido("***PACIFICARD***", isCopy, paint, canvas);
            imprimirPublicidad(paint, canvas);
        } else {

            switch (dataTrans.getEName()){
                case Trans.Type.PAGOS_VARIOS:
                    break;
                default:
                    if (!isCopy) {
                        mensajeTerminal(paint, canvas);
                    }
                    printSignatureDatafast(checkNull(dataTrans.getNameCard()), paint, canvas, isCopy);

                    println(paint, canvas);
                    println(paint, canvas);
                    println(paint, canvas);

                    datosTarjetaChip(isCopy, paint, canvas);
                    break;
            }
            originalCopia(isCopy, paint, canvas);

            imprimirPublicidad(paint, canvas);
        }
    }

    private void printSignatureDatafast(String nameCard, Paint paint, PrintCanvas canvas, boolean isCopy) {
        boolean isHexa;
        if (!isCopy) {
            if (!nameCard.equals("----")) {//Con esto evitamos imprimir la cadena "---" que se agrega cuando el labelCard es null
                if (nameCard.length() > 0) {
                    isHexa = nameCard.matches("^[0-9a-fA-F]+$");                   //validacion de variable labelCard para evitar conversion
                    if (!isHexa) {
                        nameCard = ISOUtil.convertStringToHex(nameCard);
                    }
                    setTextPrint("NOMBRE : " + checkNumCharacters(ISOUtil.hex2AsciiStr(nameCard.trim()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                    println(paint, canvas);
                }
            }else{
                setTextPrint("NOMBRE : ", paint, BOLD_ON, canvas, S_SMALL);
                println(paint, canvas);
            }

            String isSignature = checkNull(tconf.getHABILITAR_FIRMA());
            if (isSignature.equals("1")) {
                String dir = Environment.getExternalStorageDirectory().toString() + "/saved_signature/";
                File f0 = new File(dir, "signature.png");
                boolean d0 = f0.exists();
                if (d0) {
                    long lenFile = f0.length();
                    if (lenFile > 2544) {
                        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/saved_signature/" + "signature.png");
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 250, 100, true);
                        canvas.setX(25);
                        canvas.drawBitmap(scaledBitmap, paint);
                        canvas.setX(0);

                        //Se elimina el archivo con la firma del SD
                        f0.delete();
                    }
                }
            }

            setTextPrint("x______________________________________", paint, BOLD_ON, canvas, S_SMALL);

            setTextPrint("EL ESTABLECIMIENTO VERIFICA QUE LA FIRMA ", paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint("DEL CLIENTE ES AUTENTICA.", paint, BOLD_ON, canvas, S_SMALL);

            println(paint, canvas);
            println(paint, canvas);

            if (isSignature.equals("1")) {
                setTextPrint("C.I.: " + checkNull(dataTrans.getCedula()), paint, BOLD_ON, canvas, S_SMALL);
                println(paint, canvas);
                setTextPrint("TELEFONO: " + checkNull(dataTrans.getTelefono()), paint, BOLD_ON, canvas, S_SMALL);
            } else {
                setTextPrint("C.I.:__________________________________", paint, BOLD_ON, canvas, S_SMALL);
                println(paint, canvas);
                setTextPrint("TELEFONO:______________________________", paint, BOLD_ON, canvas, S_SMALL);
            }
        } else {
            if (!nameCard.equals("----")) {//Con esto evitamos imprimir la cadena "---" que se agrega cuando el labelCard es null
                if (nameCard.length() > 0) {
                    isHexa = nameCard.matches("^[0-9a-fA-F]+$");                   //validacion de variable labelCard para evitar conversion
                    if (!isHexa) {
                        nameCard = ISOUtil.convertStringToHex(nameCard);
                    }
                    setTextPrint("NOMBRE : " + checkNumCharacters(ISOUtil.hex2AsciiStr(nameCard.trim()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                    println(paint, canvas);
                }
            }else{
                setTextPrint("NOMBRE : ", paint, BOLD_ON, canvas, S_SMALL);
                println(paint, canvas);
            }
        }
    }

    private void printDataCARD(Paint paint, PrintCanvas canvas, boolean isVoid) {
        if (isICC){
            setTextPrint(checkNull(dataTrans.getTypeAccount()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint("AID: " + checkNull(dataTrans.getAID()), paint, BOLD_ON, canvas, S_SMALL);
        }else if(isNFC){
            if (dataTrans.getAIDName().equals("") && dataTrans.getAID().trim().substring(0,14).equals("A0000000031010")){
                setTextPrint("VISA CREDIT",paint, BOLD_ON, canvas, S_SMALL);
            }else {
                setTextPrint(dataTrans.getAIDName(),paint, BOLD_ON, canvas, S_SMALL);
            }
            setTextPrint("AID: " + checkNumCharacters(dataTrans.getAID().trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
        println(paint, canvas);
    }

    private String getType(int type) {
        String data = null;
        String rta = ingresoCalculoItem(type);
        switch (rta) {
            case PREGUNTA:
                data = "PREGUNTA";
                break;
            case AGREGADO:
                data = "AGREGADO";
                break;
            case DESAGREGADO:
                data = "DESAGREGADO";
                break;
            case MANUAL:
                data = "MANUAL";
                break;
        }
        return data;
    }

    private String ingresoCalculoItem(int ucAmtType) {
        String tipoEntrada;
        tipoEntrada = checkIfModoItem(ucAmtType);
        return tipoEntrada;
    }

    private String checkIfModoItem(int ucTipoMonto) {

        String tipoEntrada = null;

        switch (ucTipoMonto) {
            case SERVICEAMOUNT:
                tipoEntrada = "A";
                break;

            case TIPAMOUNT:
                tipoEntrada = "P";
                break;

            case IVAAMOUNT:
                tipoEntrada = "D";
                break;
        }

        return tipoEntrada;
    }

    private List<TransLogData> orderList(List<TransLogData> list1, boolean isAux) {

        String getNameAcq = "";
        int cont = 0;
        List<TransLogData> auxList = new ArrayList<>();
        List<TransLogData> list = new ArrayList<>(list1);

        try {
            while (list.size() > 0) {

                int lenAuxlist = auxList.size();
                String acqActual = "";

                if (cont >= list.size()) {
                    auxList.add(list.get(0));
                    if (isAux) {
                        getNameAcq = list.get(0).getIssuerName().trim();
                    } else {
                        getNameAcq = list.get(0).getField44().trim();
                    }
                    list.remove(0);
                    cont = 0;

                } else {
                    if (isAux) {
                        getNameAcq = list.get(cont).getIssuerName().trim();
                    } else {
                        getNameAcq = list.get(cont).getField44().trim();
                    }
                }
                if (list.size() > 0) {
                    if (lenAuxlist > 0) {
                        if (isAux) {
                            acqActual = auxList.get(lenAuxlist - 1).getIssuerName().trim();
                        } else {
                            acqActual = auxList.get(lenAuxlist - 1).getField44().trim();
                        }
                    } else {
                        auxList.add(list.get(0));
                        list.remove(0);
                        continue;
                    }
                    if (getNameAcq.equals(acqActual)) {
                        auxList.add(list.get(cont));
                        list.remove(cont);
                    } else {
                        cont++;
                    }
                }
            }
        }
        catch (Exception e){}
        return auxList;
    }

    private void publicidad(String msg) {
        int pos = msg.indexOf("008");

        if (pos != -1) {
            identificadoresActivos[ID_008] = "008";
            rspField57[PUBLICIDAD_MAY] = msg.substring(pos + 3);
        }

        pos = msg.indexOf("009");

        if (pos != -1) {
            identificadoresActivos[ID_009] = "009";
            rspField57[PUBLICIDAD_MIN] = msg.substring(pos + 3);
        }
    }

    private void verificarVoucherRapido(String msg) {

        int pos = msg.indexOf("016");

        if (pos != -1) {
            identificadoresActivos[ID_016] = "016";
            rspField57[VOUCHER_RAPIDO_FAST_CLUB] = msg.substring(pos);
        }

        pos = msg.indexOf("025");

        if (pos != -1) {
            identificadoresActivos[ID_025] = "025";
            rspField57[VOUCHER_RAPIDO_PACIFICARD] = msg.substring(pos);
        }
    }

    private void valorFinanciacion(String msg) {
        int pos = msg.indexOf("004");
        if (pos != -1) {
            identificadoresActivos[ID_004] = "004";
        }
    }

    private void montoFijo(String msg) {
        int pos = msg.indexOf("014");
        if (pos != -1) {
            identificadoresActivos[ID_014] = "014";
        }
    }

    private void clearP57() {
        for (int i = 0; i < rspField57.length; i++) {
            rspField57[i] = "-";
        }
    }

    private void clearIdentificadores() {
        for (int i = 0; i < identificadoresActivos.length; i++) {
            identificadoresActivos[i] = "-";
        }
    }

    private String printField57(String fld57) {
        String id = "";

        clearP57();
        clearIdentificadores();

        if (fld57 != null) {
            id = fld57.substring(0, 3);
            String msg = fld57.substring(3);

            try {
                switch (id) {
                    case "001":                                                             //Security Code
                        identificadoresActivos[ID_001] = id;
                        rspField57[SECURY_CODE] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "002":                                                             //Numero de Telefono recargador
                        identificadoresActivos[ID_002] = id;
                        rspField57[NUM_TEL_RECARGADOR] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "003":                                                             //Numero de transaccion en el proveedor
                        identificadoresActivos[ID_003] = id;
                        rspField57[NUM_TRAN_PROVE] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "004":                                                             //Valor de la financiación
                        identificadoresActivos[ID_004] = id;
                        rspField57[VALOR_FINANCIACION] = msg.substring(0, 8);                //Valor de la financiación
                        rspField57[VALOR_TRANS] = msg.substring(8,16);                          //Total de transaccion
                        verificarVoucherRapido(msg.substring(16));
                        break;
                    case "005":                                                             //Redencion
                        identificadoresActivos[ID_005] = id;
                        rspField57[REDENCION] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "007":                                                            //Nombre y MID del POS
                        identificadoresActivos[ID_007] = id;
                        rspField57[NOMBRE_COMERCIO] = msg.substring(0, 16);                  //Nombre
                        rspField57[MID] = msg.substring(16, 26);                              //MID
                        verificarVoucherRapido(msg);
                        break;
                    case "008":                                                            //Mensaje de publicidad en Mayusculas
                        msg = msg.toUpperCase();
                        identificadoresActivos[ID_008] = id;
                        rspField57[PUBLICIDAD_MAY] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "009":                                                            //Mensaje de publicidad en Minusculas
                        msg = msg.toLowerCase();
                        identificadoresActivos[ID_009] = id;
                        rspField57[PUBLICIDAD_MIN] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "011":                                                            //Nombre del dueño de la cuenta
                        identificadoresActivos[ID_011] = id;
                        rspField57[NOMBRE_DUENO_CUENTA] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "012":                                                            //Codigo de error
                        identificadoresActivos[ID_012] = id;
                        rspField57[COD_ERROR] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "013":                                                            //Valor a pagar en consultas
                        identificadoresActivos[ID_013] = id;
                        rspField57[VALOR_PAGAR_CONS] = msg;
                        verificarVoucherRapido(msg);
                        break;
                    case "014":                                                            //Valor del monto fijo
                        identificadoresActivos[ID_014] = id;
                        rspField57[VALOR_FINANCIACION] = msg.substring(0, 8);                //Valor de la financiación
                        rspField57[VALOR_TRANS] = msg.substring(8, 16);                          //Total de transaccion
                        verificarVoucherRapido(msg.substring(16));
                        break;
                    case "015":                                                            //PIN y dias de vigencia
                        identificadoresActivos[ID_015] = id;
                        rspField57[PIN] = msg.substring(0, 15);                              //PIN
                        rspField57[VIGENCIA] = msg.substring(15);                            //Vigencia
                        verificarVoucherRapido(msg);
                        break;
                    case "016":                                                            //Codigo voucher rapido
                        identificadoresActivos[ID_016] = id;
                        rspField57[VOUCHER_RAPIDO_FAST_CLUB] = msg;
                        break;
                    case "017":                                                            //Nombre + MID + Valor financiacion
                        identificadoresActivos[ID_017] = id;
                        rspField57[NOMBRE_COMERCIO] = msg.substring(0, 16);                  //Nombre
                        rspField57[MID] = msg.substring(16, 26);                            //MID
                        valorFinanciacion(msg.substring(26, 29));
                        rspField57[VALOR_FINANCIACION] = msg.substring(29, 37);             //Valor financiacion
                        rspField57[VALOR_TRANS] = msg.substring(37, 45);                    //Total transaccion
                        verificarVoucherRapido(msg.substring(45));                                       //Codigo voucher rapido
                        break;
                    case "018":
                        identificadoresActivos[ID_018] = id;
                        rspField57[NOMBRE_COMERCIO] = msg.substring(0, 16);
                        rspField57[MID] = msg.substring(16, 26);
                        valorFinanciacion(msg.substring(26, 29));
                        rspField57[VALOR_FINANCIACION] = msg.substring(29, 37);
                        rspField57[VALOR_TRANS] = msg.substring(37, 45);
                        publicidad(msg.substring(45));
                        verificarVoucherRapido(msg);
                        break;
                    case "021":                                                             //Datos de interés + mensaje de publicidad
                        identificadoresActivos[ID_021] = id;
                        valorFinanciacion(msg.substring(0, 3));
                        rspField57[VALOR_FINANCIACION] = msg.substring(3, 11);
                        rspField57[VALOR_TRANS] = msg.substring(11, 19);
                        publicidad(msg.substring(19));
                        verificarVoucherRapido(msg);
                        break;
                    case "023":
                        identificadoresActivos[ID_023] = id;
                        montoFijo(msg.substring(0, 3));
                        rspField57[VALOR_FINANCIACION] = msg.substring(3, 11);                //Valor de la financiación
                        rspField57[VALOR_TRANS] = msg.substring(11, 19);                          //Total de transaccion
                        publicidad(msg.substring(19));
                        verificarVoucherRapido(msg);
                        break;
                    case "025":                                                            //Pagos Electronicos
                        identificadoresActivos[ID_025] = id;
                        rspField57[VOUCHER_RAPIDO_PACIFICARD] = msg;
                        break;
                }
            } catch (IndexOutOfBoundsException e) {
            }
        }

        return id;
    }

    public static String getIdPreAuto(String dato) {//"00083730393231303735"
        String tmp = dato.substring(8);
        tmp = ISOUtil.hex2AsciiStr(tmp);
        return tmp;
    }

    public void printField59(String fld59, Paint paint, PrintCanvas canvas) {

        //String dataEjemplo="3132683139303331333130333835303030303033323930313430323936313930333133313131313537303030303236323033333638333131393033313331313436353330303030303235303239333130333139303331333131343934393030303032373538323431383737";
        String Data = ISOUtil.hex2AsciiStr(fld59.substring(6));

        String fecha;
        String hora;
        String monto;
        String codAuto;
        int Cont = 0;

        println(paint, canvas);
        setTextPrint(setCenterText("PREAUTORIZACION Y AMPLIACIONES", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        printLine(paint, canvas);

        for (int i = 0; i < Data.length(); ) {

            try {
                fecha = Data.substring(i, i + 6);
                i += 6;
                hora = Data.substring(i, i + 6);
                i += 6;
                monto = Data.substring(i, i + 8);
                i += 8;
                codAuto = Data.substring(i, i + 6);
                i += 6;

                setTextPrint(setCenterText(formatoFecha(fecha) + " " + formatoHoraMin(hora.substring(0, 4)) + " $" + formatAmountLess(Long.parseLong(monto)) + " " + tipoTransPreAuto(Cont) + " " + codAuto, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                Cont++;
            } catch (Exception e) {
                System.out.println(e);
                break;
            }
        }

        printLine(paint, canvas);
        println(paint, canvas);
    }

    private String formatoFecha(String fecha) {
        StringBuilder date = new StringBuilder();

        date.append(fecha.substring(4));
        date.append("/");
        date.append(PAYUtils.getMonth(fecha.substring(2, 4)));
        date.append("/");
        date.append(fecha, 0, 2);

        return date.toString();
    }

    private String formato2Fecha(String fecha) {//20190310
        StringBuilder date = new StringBuilder();

        date.append(fecha.substring(6));
        date.append("/");
        date.append(PAYUtils.getMonth(fecha.substring(4, 6)));
        date.append("/");
        date.append(fecha, 2, 4);

        return date.toString();
    }

    private String formatoHora(String hora) {
        StringBuilder date = new StringBuilder();

        date.append(hora, 0, 2);
        date.append(":");
        date.append(hora, 2, 4);
        date.append(":");
        date.append(hora.substring(4));

        return date.toString();
    }

    private String formatoHoraMin(String hora) {
        StringBuilder date = new StringBuilder();

        date.append(hora, 0, 2);
        date.append(":");
        date.append(hora, 2, 4);
        return date.toString();
    }


    private String formato2Hora(String hora) {
        StringBuilder date = new StringBuilder();

        date.append(hora, 0, 2);
        date.append(":");
        date.append(hora, 2, 4);
        return date.toString();
    }

    private String tipoTransPreAuto(int cont) {
        String tipoTrans = "";
        if (cont == 0){
            tipoTrans = "PRE";
        }else{
            tipoTrans = "AMP";
        }
        return tipoTrans;
    }

    private String tipoTransPreAuto(String codAuto) {
        String tipoTrans = "";
        List<TransLogData> listPreAuto = TransLog.getInstance(idLote + FILE_NAME_PREAUTO).getData();
        if (listPreAuto.size() > 0) {

            for (TransLogData transLogData : listPreAuto) {

                if (transLogData.getAuthCode().equals(codAuto)) {
                    switch (transLogData.getTransEName()) {
                        case Trans.Type.PREAUTO:
                            tipoTrans = "PRE";
                            break;
                        case Trans.Type.AMPLIACION:
                            tipoTrans = "AMP";
                            break;
                        default:
                            tipoTrans = "N/A";
                            break;
                    }
                }
            }
        }
        return tipoTrans;
    }

    private void datosTarjetaChip(boolean isCopy, Paint paint, PrintCanvas canvas) {
        if (isICC || isNFC) {
            if (!isCopy)
                printDataCARD(paint, canvas, false);
        }
    }

    private void originalCopia(boolean isCopy, Paint paint, PrintCanvas canvas) {
        if (isCopy) {
            setTextPrint(setCenterText("- CLIENTE -", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setCenterText("- ORIGINAL -", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
    }

    private void mensajeTerminal(Paint paint, PrintCanvas canvas) {

        boolean debit = false;

        if (isICC) {
            String nameAID = checkNull(dataTrans.getTypeAccount()).trim();
            if (nameAID.contains("debito") || nameAID.contains("debit") ||
                    nameAID.contains("DEBITO") || nameAID.contains("DEBIT")) {
                debit = true;
            }
        }else if (isNFC) {
            String nameAID = checkNull(dataTrans.getAIDName().trim());
            if (nameAID.contains("debito") || nameAID.contains("debit") ||
                    nameAID.contains("DEBITO") || nameAID.contains("DEBIT")) {
                debit = true;
            }
        }

        if (debit) {
            setTextPrint("DECLARO QUE EL PRODUCTO DE LA TRANSACCION", paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint("NO SERA UTILIZADO EN ACTIVIDADES DE LAVA-", paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint("DO DE ACTIVOS, FINANCIAMIENTO DEL TERRO-", paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint("RISMO Y OTROS DELITOS.", paint, BOLD_ON, canvas, S_SMALL);
        } else {

            setTextPrint(checkNull(tconf.getFOOTER_LINEA_1()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_2()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_3()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_4()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_5()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_6()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_7()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_8()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_9()), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNull(tconf.getFOOTER_LINEA_10()), paint, BOLD_ON, canvas, S_SMALL);
        }
        println(paint, canvas);
    }

    private void duplicado(boolean isRePrint, Paint paint, PrintCanvas canvas) {
        if (isRePrint) {
            setTextPrint(setCenterText("***DUPLICADO***", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
    }

    private void capturaElectronica(Paint paint, PrintCanvas canvas) {

        if (ISOUtil.stringToBoolean(tconf.getCAPTURA_ELECTRONICA()))
        {
            setTextPrint(setCenterText("CAP ELECT DATAFAST", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }else{
            setTextPrint(setCenterText(checkNull(tconf.getLINEA_AUX()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
    }

    private void mensajeAprobacion(Paint paint, PrintCanvas canvas, boolean isCopy) {
        if (!isCopy) {
            setTextPrint(setCenterText("APROBACION # : " + checkNull(dataTrans.getAuthCode()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            //println(paint, canvas);
            setTextPrint(setCenterText("DATAFAST", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
    }

    private String typeEntryPoint() {
        String typeEntry = "";

        try {
            if (isFallback) {
                typeEntry = "FALLBACK";
            } else if (dataTrans.getEntryMode().equals(MODE_MAG + CapPinPOS())) {
                typeEntry = "BANDA";
            } else if (dataTrans.getEntryMode().equals(MODE_ICC + CapPinPOS())) {
                typeEntry = "CHIP";
            } else if (dataTrans.getEntryMode().equals(MODE_CTL + CapPinPOS()) && dataTrans.isField55()) {
                typeEntry = "CTL C";
            } else if (dataTrans.getEntryMode().equals(MODE_CTL + CapPinPOS()) && !dataTrans.isField55()) {
                typeEntry = "CTL B";
            } else if (dataTrans.getEntryMode().equals(MODE_HANDLE + CapPinPOS())) {
                typeEntry = "MANUAL";
            } else if (dataTrans.getEntryMode().equals("101") || dataTrans.getEntryMode().equals("102")) {//Pagos Electronicos
                typeEntry = "TOKEN";
            }
        } catch (Exception e) {
        }

        return typeEntry;
    }

    private void printFieldsPreVoucher(Paint paint, PrintCanvas canvas) {
        println(paint, canvas);
        setTextPrint("PROPINA: X________________________________", paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
        setTextPrint("NOMBRE: X_________________________________", paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
        setTextPrint("CI/RUC: X_________________________________", paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
        setTextPrint("DIRECCION: X______________________________", paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
        setTextPrint("TELEFONO: X_______________________________", paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
    }

    private void printPrompts(TransLogData logdata, Paint paint, PrintCanvas canvas) {
        try {
            if (logdata.getPromptsPrinter() != null) {
                String[] prompts = logdata.getPromptsPrinter().split("\\|");

                for (int i = 0; i < prompts.length; i++) {
                    //Solo imprime como maximo 10 prompts (Ajuste realizado por sugerencia de Anita 18/03/19 11:48 am)
                    if (i == 10) {
                        break;
                    }

                    String[] key = prompts[i].split(":");
                    setTextPrint(setTextColumn(key[0] + " :", key[1], S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }
            }
        }catch (Exception e){}
    }

    private void printPromptsAmount(TransLogData logdata, Paint paint, PrintCanvas canvas) {

        try {
            if (logdata.getPromptsAmountPrinter() != null) {
                String[] prompts = logdata.getPromptsAmountPrinter().split("\\|");

                for (int i = 0; i < prompts.length; i++) {
                    //Solo imprime como maximo 10 prompts (Ajuste realizado por sugerencia de Anita 18/03/19 11:48 am)
                    if (i == 10) {
                        break;
                    }
                    String[] key = prompts[i].split(":");
                    setTextPrint(setTextColumn(key[0] + " :", key[1], S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                }
            }
        }catch (Exception e){}
    }

    private String anulada(TransLogData logdata) {
        String signo = "";
        if (logdata.getIsVoided()) {
            signo = "-";
        }
        return signo;
    }

    private void imprimirValorFinanciacion(TransLogData logdata, Paint paint, PrintCanvas canvas) {
        try {
            if (identificadoresActivos[ID_004].equals("004")) {
                setTextPrint(setTextColumn(String.format("%-23s","INTERES ") + ":US$", "$" +
                        formatAmountLess(Long.parseLong(rspField57[VALOR_FINANCIACION])), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

                setTextPrint(setTextColumn(String.format("%-15s","GRAN TOTAL ") + ":US$", "$" +
                        formatAmountLess(Long.parseLong(rspField57[VALOR_TRANS])), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            }

            if (identificadoresActivos[ID_014].equals("014")) {
                setTextPrint(setTextColumn(String.format("%-23s", "TARIFA ") + ":US$", "$" +
                        formatAmountLess(Long.parseLong(rspField57[VALOR_FINANCIACION])), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

                setTextPrint(setTextColumn(String.format("%-15s","GRAN TOTAL " ) + ":US$", "$" +
                        formatAmountLess(Long.parseLong(rspField57[VALOR_TRANS])), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            }
        } catch (NumberFormatException e) {
        }
    }

    private void imprimirVoucherRapido(String msg, boolean isCopy, Paint paint, PrintCanvas canvas) {
        println(paint, canvas);
        setTextPrint(setCenterText(checkNull(msg), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint(setCenterText("*NO REQUIERE FIRMA*", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
        println(paint, canvas);
        originalCopia(isCopy, paint, canvas);
    }

    private void imprimirPublicidad(Paint paint, PrintCanvas canvas) {
        if (identificadoresActivos[ID_008].equals("008")) {
            println(paint, canvas);
            if (rspField57[PUBLICIDAD_MAY].length() > 40) {
                setTextPrint(checkNull(rspField57[PUBLICIDAD_MAY].substring(0, 40)), paint, BOLD_ON, canvas, S_SMALL);
                if (rspField57[PUBLICIDAD_MAY].length() > 80) {
                    setTextPrint(checkNull(rspField57[PUBLICIDAD_MAY].substring(40, 80)), paint, BOLD_ON, canvas, S_SMALL);
                    setTextPrint(checkNull(rspField57[PUBLICIDAD_MAY].substring(80)), paint, BOLD_ON, canvas, S_SMALL);
                } else {
                    setTextPrint(checkNull(rspField57[PUBLICIDAD_MAY].substring(40)), paint, BOLD_ON, canvas, S_SMALL);
                }
            } else {
                setTextPrint(checkNull(rspField57[PUBLICIDAD_MAY]), paint, BOLD_ON, canvas, S_SMALL);
            }
        } else if (identificadoresActivos[ID_009].equals("009")) {
            println(paint, canvas);
            if (rspField57[PUBLICIDAD_MIN].length() > 40) {
                setTextPrint(checkNull(rspField57[PUBLICIDAD_MIN].substring(0, 40)), paint, BOLD_ON, canvas, S_SMALL);
                setTextPrint(checkNull(rspField57[PUBLICIDAD_MIN].substring(40)), paint, BOLD_ON, canvas, S_SMALL);
            } else {
                setTextPrint(checkNull(rspField57[PUBLICIDAD_MIN]), paint, BOLD_ON, canvas, S_SMALL);
            }
        }
    }

    private String chequearNumeroComercio(String logdata) {
        switch (logdata) {
            case "0":
                return "CERO";
            case "1":
                return "UNO";
            case "2":
                return "DOS";
            case "3":
                return "TRES";
            case "4":
                return "CUATRO";
            case "5":
                return "CINCO";
            case "6":
                return "SEIS";
            case "7":
                return "SIETE";
            case "8":
                return "OCHO";
            case "9":
                return "NUEVE";
            default:
                return "";
        }
    }

    private void nombreComercioActual(TransLogData logdata, Paint paint, PrintCanvas canvas) {
        setTextPrint(setCenterText(checkNull(logdata.getIdComercio()) + " - COMERCIO " + chequearNumeroComercio(checkNull(logdata.getIdComercio())), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
    }

    private String imprimirTipoTrans(TransLogData translog) {
        String tipoTrans = "";

        switch (translog.getEName()) {
            case Trans.Type.VENTA:
                if (translog.getIsVoided())
                    tipoTrans = "VCA";
                else
                    tipoTrans = "VC";
                break;
            case Trans.Type.DEFERRED:
                switch (translog.getTypeDeferred()) {
                    case Trans.TipoDiferido.CORRIENTE:
                        if (translog.getIsVoided())
                            tipoTrans = "VD1A";
                        else
                            tipoTrans = "VD1";
                        break;
                    case Trans.TipoDiferido.CON_INTERES:
                        if (translog.getIsVoided())
                            tipoTrans = "VD2A";
                        else
                            tipoTrans = "VD2";
                        break;
                    case Trans.TipoDiferido.SIN_INTERES:
                        if (translog.getIsVoided())
                            tipoTrans = "VD3A";
                        else
                            tipoTrans = "VD3";
                        break;
                    case Trans.TipoDiferido.CON_INT_ESPECIAL:
                        if (translog.getIsVoided())
                            tipoTrans = "VD7A";
                        else
                            tipoTrans = "VD7";
                        break;
                    case Trans.TipoDiferido.SIN_INT_ESPECIAL:
                        if (translog.getIsVoided())
                            tipoTrans = "VD9A";
                        else
                            tipoTrans = "VD9";
                        break;
                    case Trans.TipoDiferido.PREFERENTE:
                        if (translog.getIsVoided())
                            tipoTrans = "VD21A";
                        else
                            tipoTrans = "VD21";
                        break;
                    case Trans.TipoDiferido.PLUS:
                        if (translog.getIsVoided())
                            tipoTrans = "VD22A";
                        else
                            tipoTrans = "VD22";
                        break;
                }
                break;
            case Trans.Type.ELECTRONIC:
                if (translog.getIsVoided())
                    tipoTrans = "ECA";
                else
                    tipoTrans = "EC";
                break;
            case Trans.Type.ELECTRONIC_DEFERRED:
                switch (translog.getTypeDeferred()) {
                    case Trans.TipoDiferido.CORRIENTE:
                        if (translog.getIsVoided())
                            tipoTrans = "ED1A";
                        else
                            tipoTrans = "ED1";
                        break;
                    case Trans.TipoDiferido.CON_INTERES:
                        if (translog.getIsVoided())
                            tipoTrans = "ED2A";
                        else
                            tipoTrans = "ED2";
                        break;
                    case Trans.TipoDiferido.SIN_INTERES:
                        if (translog.getIsVoided())
                            tipoTrans = "ED3A";
                        else
                            tipoTrans = "ED3";
                        break;
                    case Trans.TipoDiferido.CON_INT_ESPECIAL:
                        if (translog.getIsVoided())
                            tipoTrans = "ED7A";
                        else
                            tipoTrans = "ED7";
                        break;
                    case Trans.TipoDiferido.SIN_INT_ESPECIAL:
                        if (translog.getIsVoided())
                            tipoTrans = "ED9A";
                        else
                            tipoTrans = "ED9";
                        break;
                    case Trans.TipoDiferido.PREFERENTE:
                        if (translog.getIsVoided())
                            tipoTrans = "ED21A";
                        else
                            tipoTrans = "ED21";
                        break;
                    case Trans.TipoDiferido.PLUS:
                        if (translog.getIsVoided())
                            tipoTrans = "ED22A";
                        else
                            tipoTrans = "ED22";
                        break;
                }
                break;
            case Trans.Type.PREAUTO:
            case Trans.Type.VOID_PREAUTO:
                if (translog.getIsVoided())
                    tipoTrans = "PAA";
                else
                    tipoTrans = "PA";
                break;
            case Trans.Type.AMPLIACION:
                if (translog.getIsVoided())
                    tipoTrans = "APA";
                else
                    tipoTrans = "AP";
                break;
            case Trans.Type.CONFIRMACION:
                if (translog.getIsVoided())
                    tipoTrans = "CPA";
                else
                    tipoTrans = "CP_Request";
                break;
            case Trans.Type.PAGO_PRE_VOUCHER:
                if (translog.getIsVoided())
                    tipoTrans = "PVA";
                else
                    tipoTrans = "PV";
                break;
            case Trans.Type.CASH_OVER:
                if (translog.getIsVoided())
                    tipoTrans = "COA";
                else
                    tipoTrans = "CO";
                break;
            case Trans.Type.PAGOS_VARIOS:
                if (translog.getIsVoided())
                    tipoTrans = translog.getPagoVarioSeleccionado()+"A";
                else
                    tipoTrans = translog.getPagoVarioSeleccionado();
                break;
            default:
                tipoTrans = "NN";
                break;
        }

        return tipoTrans;
    }

    private String getFechaCierre(String cierre){
        SharedPreferences prefs = mContext.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString(cierre, null);
    }


    /**
     * Maximo 4 lineas de 20 caracteres
     * @param paint
     * @param canvas
     */
    private void printMessageSettle(Paint paint, PrintCanvas canvas){

        //Cada campo maximo 40 caracteres
        String msg1 = tconf.getMENSAJE_CIERRE1();
        String msg2 = tconf.getMENSAJE_CIERRE2();

        boolean header = false;
        boolean footer = false;

        if (msg1 != null || msg2 != null){
            if (!msg1.equals("")){
                header = true;
                footer = true;
            }else if (!msg2.equals("")){
                header = true;
                footer = true;
            }else {
                return;
            }
        }

        if (header){
            setTextPrint("******************************************", paint, BOLD_OFF, canvas, S_SMALL);
            setTextPrint(setCenterText("MENSAJE DE DATAFAST:", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            setTextPrint("******************************************", paint, BOLD_OFF, canvas, S_SMALL);
        }

        if (msg1!=null){
            if (msg1.length()>20) {
                setTextPrint(setCenterText(checkNull(msg1.substring(0, 20)), S_BIG), paint, BOLD_ON, canvas, S_BIG);
                setTextPrint(setCenterText(checkNull(msg1.substring(20)), S_BIG), paint, BOLD_ON, canvas, S_BIG);
            }else {
                if (!msg1.equals("")) {
                    setTextPrint(setCenterText(checkNull(msg1), S_BIG), paint, BOLD_ON, canvas, S_BIG);
                }
            }
        }

        if (msg2!=null){
            if (msg2.length()>20) {
                setTextPrint(setCenterText(checkNull(msg2.substring(0, 20)), S_BIG), paint, BOLD_ON, canvas, S_BIG);
                setTextPrint(setCenterText(checkNull(msg2.substring(20)), S_BIG), paint, BOLD_ON, canvas, S_BIG);
            }else {
                if (!msg2.equals("")) {
                    setTextPrint(setCenterText(checkNull(msg2), S_BIG), paint, BOLD_ON, canvas, S_BIG);
                }
            }
        }

        if (footer) {
            setTextPrint("******************************************", paint, BOLD_OFF, canvas, S_SMALL);
        }
    }
}
