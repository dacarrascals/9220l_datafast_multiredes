package com.newpos.libpay.trans.finace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ToneGenerator;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.android.desert.keyboard.InputInfo;
import com.android.newpos.libemv.EMVISRCode;
import com.android.newpos.libemv.PBOCCardInfo;
import com.android.newpos.libemv.PBOCOnlineResult;
import com.android.newpos.libemv.PBOCTag9c;
import com.android.newpos.libemv.PBOCTransProperty;
import com.android.newpos.libemv.PBOCUtil;
import com.android.newpos.libemv.PBOCode;
import com.datafast.inicializacion.trans_init.trans.Tools;
import com.datafast.pinpad.cmd.CT.CT_Request;
import com.datafast.pinpad.cmd.CT.CT_Response;
import com.datafast.pinpad.cmd.LT.LT_Request;
import com.datafast.pinpad.cmd.LT.LT_Response;
import com.datafast.pinpad.cmd.PP.PP_Response;
import com.datafast.pinpad.cmd.Tools.encryption;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.pinpad.cmd.rules.RulesPinPad;
import com.datafast.server.server_tcp.Server;
import com.datafast.transactions.common.CommonFunctionalities;
import com.datafast.transactions.common.GetAmount;
import com.newpos.bypay.EmvL2CVM;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.contactless.EmvL2Process;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.process.EmvTransaction;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.manager.RevesalTrans;
import com.newpos.libpay.trans.manager.ScriptTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.trans.translog.TransLogReverse;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.printer.Printer;

import org.jpos.iso.IF_CHAR;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;

import static cn.desert.newpos.payui.master.MasterControl.incardTable;
import static com.android.newpos.pay.StartAppDATAFAST.host_confi;
import static com.android.newpos.pay.StartAppDATAFAST.lastCmd;
import static com.android.newpos.pay.StartAppDATAFAST.lastInputMode;
import static com.android.newpos.pay.StartAppDATAFAST.lastTrack;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_REVERSE;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_FALLBACK;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_ICC_SWIPE;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.GERCARD_MSG_SWIPE_ICC_CTL;
import static com.datafast.inicializacion.configuracioncomercio.Rango.CENTRO;
import static com.datafast.inicializacion.configuracioncomercio.Rango.INICIO_FIN;
import static com.datafast.inicializacion.configuracioncomercio.Rango.SIN_MASCARA;
import static com.datafast.menus.menus.FALLBACK;
import static com.datafast.menus.menus.TOTAL_BATCH;
import static com.datafast.menus.menus.contFallback;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.OK;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;
import static com.datafast.server.activity.ServerTCP.listenerServer;
import static com.datafast.transactions.common.CommonFunctionalities.Fld58PromptsAmountPrinter;
import static com.datafast.transactions.common.CommonFunctionalities.Fld58PromptsPrinter;
import static com.datafast.transactions.common.GetAmount.NO_OPERA;
import static com.datafast.transactions.common.GetAmount.PIDE_CONFIRMACION;
import static com.newpos.libpay.device.printer.PrintManager.getIdPreAuto;
import static com.newpos.libpay.presenter.TransUIImpl.getStatusInfo;
import static com.newpos.libpay.trans.Trans.Type.PAGOS_VARIOS;
import static com.newpos.libpay.trans.Trans.Type.SETTLE;

/**
 * 金融交易类
 *
 * @author zhouqiang
 */
public class FinanceTrans extends Trans {

    /**
     * 外界输入类型
     */
    public static final int INMODE_HAND = 0x01;
    public static final int INMODE_MAG = 0x02;
    public static final int INMODE_QR = 0x04;
    public static final int INMODE_IC = 0x08;
    public static final int INMODE_NFC = 0x10;

    /**
     * 联机交易还是脱机交易
     */
    public static final int AAC_ARQC = 1;
    public static final int AAC_TC = 0;

    public static final String LOCAL = "1";
    public static final String DOLAR = "2";
    public static final String EURO = "3";

    /**
     * var multi-acq
     */
    protected final int NOMBRE_COMERCIO = 0;
    protected final int MID = 1;

    /**
     * 卡片模式
     */
    protected int inputMode = 0x00;// 刷卡模式 1 手输卡号；2刷卡；5 3插IC；7 4非接触卡

    /**
     * 是否有密码
     */
    protected boolean isPinExist = false;

    /**
     * 是否式IC卡
     */
    protected boolean isICC = false;

    /**
     * 标记此次交易是否需要冲正
     */
    protected boolean isReversal;

    /**
     * 标记此次交易是否需要存记录
     */
    protected boolean isSaveLog;

    /**
     * 是否借记卡交易
     */
    protected boolean isDebit;

    /**
     * 标记此交易联机前是否进行冲正上送
     */
    protected boolean isProcPreTrans;

    /**
     * 后置交易
     */
    protected boolean isProcSuffix;

    /**
     * whether need GAC2
     */
    protected boolean isNeedGAC2;


    /*---------ATC-------*/
    protected String typeCoin;

    protected String host_id;

    protected String currency_name;

    protected int numCuotas;

    protected String amex4DBC;

    protected String numCelular;

    protected String pinSpecial;

    public String transEname;

    protected boolean isTip;

    //public boolean NotShowGraphInterface;

    protected CT_Response ctResponse;

    protected LT_Response ltResponse;

    protected PP_Response pp_response;

    protected RulesPinPad rulesPinPad;

    protected ProcessPPFail processPPFail;

    protected long[] montos;

    protected String expDate;

    protected String ARQC;

    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     */
    public FinanceTrans(Context ctx, String transEname) {
        super(ctx, transEname);
        this.transEname = transEname;
        iso8583.setHasMac(false);
        setTraceNoInc(true);

        ctResponse = new CT_Response();
        ltResponse = new LT_Response();
        pp_response = new PP_Response();
        rulesPinPad = new RulesPinPad();
    }

    public FinanceTrans(Context ctx, String transEname, String fileNameLog) {
        super(ctx, transEname, fileNameLog);
        this.transEname = transEname;
        iso8583.setHasMac(false);
        setTraceNoInc(true);

        ctResponse = new CT_Response();
        ltResponse = new LT_Response();
        pp_response = new PP_Response();
        rulesPinPad = new RulesPinPad();
    }

    public FinanceTrans(Context ctx) {
        super(ctx);
    }

    /**
     * 联机前某些特殊值的处理
     *
     * @param inputMode
     */
    protected void setDatas(int inputMode) {

        Logger.debug("==FinanceTrans->setDatas==");
        this.inputMode = inputMode;

        if (isPinExist) {
            CaptureCode = "12";
        }

        EntryMode = ISOUtil.padleft(inputMode + "", 2, '0');

        if (inputMode == ENTRY_MODE_MAG) {
            if (isFallBack) {
                EntryMode = MODE1_FALLBACK + CapPinPOS();
            } else {
                EntryMode = MODE_MAG + CapPinPOS();
            }
        } else if (inputMode == ENTRY_MODE_ICC) {
            EntryMode = MODE_ICC + CapPinPOS();
        } else if (inputMode == ENTRY_MODE_NFC) {
            EntryMode = MODE_CTL + CapPinPOS();
        } else if (inputMode == ENTRY_MODE_HAND) {
            if (transEname.equals(Type.ELECTRONIC) && TypeTransElectronic.equals(Trans.Type.PAYCLUB)) {
                EntryMode = "10" + CapPinPOS();
            } else if (transEname.equals(Type.ELECTRONIC) && TypeTransElectronic.equals(Type.PAYBLUE)) {
                EntryMode = "102";
            } else {
                EntryMode = MODE_HANDLE + CapPinPOS();
            }
        } else if (inputMode == Integer.parseInt("101") || inputMode == Integer.parseInt("102")){
            if (transEname.equals(Type.ANULACION)){
                if (TypeTransElectronic.equals(Trans.Type.PAYCLUB)) {
                    EntryMode = "10" + CapPinPOS();
                } else if (TypeTransElectronic.equals(Type.PAYBLUE)) {
                    EntryMode = "102";
                }
            }else {
                if (transEname.equals(Type.ELECTRONIC) && TypeTransElectronic.equals(Trans.Type.PAYCLUB)) {
                    EntryMode = "10" + CapPinPOS();
                } else if (transEname.equals(Type.ELECTRONIC) && TypeTransElectronic.equals(Type.PAYBLUE)) {
                    EntryMode = "102";
                }
            }
        }else {
            EntryMode = "000";
        }

        if (isPinExist || Track2 != null || Track3 != null) {
            if (isPinExist) {
                SecurityInfo = "2";
            } else {
                SecurityInfo = "0";
            }
            if (cfg.isSingleKey()) {
                SecurityInfo += "0";
            } else {
                SecurityInfo += "6";
            }
            if (cfg.isTrackEncrypt()) {
                SecurityInfo += "10000000000000";
            } else {
                SecurityInfo += "00000000000000";
            }
        }
        appendField60("048");
    }

    public static String CapPinPOS() {
        String capPINPos = "1";
        if (rango.getNOMBRE_EMISOR().equals("UNION PAY")) {
            capPINPos = "2";
        }
        return capPINPos;
    }

    /**
     * 从内核获取
     * 卡号，
     * 有效期，
     * 2磁道，
     * 1磁道，
     * 卡序号
     * 55域数据
     */
    protected void setICCData() {
        Logger.debug("==FinanceTrans->setICCData==");
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

        ARQC = getARQC();
        if (ARQC != null){
            processPPFail.setARQCFail(ARQC);
        }

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

    /**
     * set some IC card data
     */
    protected void setICCDataCTL() {
        Logger.debug("==FinanceTrans->setICCData==");
        PBOCCardInfo info = PBOCUtil.getPBOCCardInfo();
        Pan = info.getCardNO();
        ExpDate = info.getExpDate();
        Track2 = info.getCardTrack2();
        Track1 = info.getCardTrack1();
        Track3 = info.getCardTrack3();
        PanSeqNo = info.getCardSeqNo();
        ICCData = PBOCUtil.getF55Data(PBOCUtil.wOnlineTags);
    }

    /**
     * 设置交易报文8583各域值，设置完后判断冲正等，即可联机
     */
    protected void setFields() {
        Logger.debug("==FinanceTrans->setFields==");
        int[] trackLen = new int[2];
        byte[] encryTrack = new byte[256];
        if (MsgID != null) {
            iso8583.setField(0, MsgID);
        }
        if (Pan != null) {
            iso8583.setField(2, Pan);
        }
        if (ProcCode != null) {
            iso8583.setField(3, ProcCode);
        }
        if (Amount > 0) {
            String AmoutData = "";
            AmoutData = ISOUtil.padleft(Amount + "", 12, '0');
            iso8583.setField(4, AmoutData);
        }
        if (TraceNo != null) {
            iso8583.setField(11, TraceNo);
        }
        if (LocalTime != null) {
            iso8583.setField(12, LocalTime);
        }
        if (LocalDate != null) {
            iso8583.setField(13, LocalDate);
        }
        if (ExpDate != null) {
            iso8583.setField(14, ExpDate);
        }
        if (SettleDate != null) {
            iso8583.setField(15, SettleDate);
        }
        if (EntryMode != null) {
            iso8583.setField(22, EntryMode);
        }
        if (PanSeqNo != null) {
            iso8583.setField(23, PanSeqNo);
        }
        if (Nii != null) {
            iso8583.setField(24, Nii);
        }
        if (SvrCode != null) {
            iso8583.setField(25, SvrCode);
        }
        if (CaptureCode != null) {
            iso8583.setField(26, CaptureCode);
        }
        if (AcquirerID != null) {
            iso8583.setField(32, AcquirerID);
        }
        if (Track2 != null && cfg.isTrackEncrypt()) {
            Track2 = PinpadManager.getInstance().getEac(0, Track2);
        }
        iso8583.setField(35, Track2);
        if (Track3 != null && cfg.isTrackEncrypt()) {
            Track3 = PinpadManager.getInstance().getEac(0, Track3);
        }
        iso8583.setField(36, Track3);
        if (RRN != null) {
            iso8583.setField(37, RRN);
        }
        if (AuthCode != null) {
            iso8583.setField(38, AuthCode);
        }
        if (RspCode != null) {
            iso8583.setField(39, RspCode);
        }
        if (TermID != null) {
            iso8583.setField(41, TermID);
        }
        if (MerchID != null) {
            iso8583.setField(42, MerchID);
        }
        if (Field44 != null) {
            iso8583.setField(44, Field44);
        }
        if (Field48 != null) {
            iso8583.setField(48, Field48);
        }
        if (CurrencyCode != null) {
            iso8583.setField(49, CurrencyCode);
        }
        if (PIN != null) {
            iso8583.setField(52, PIN);
        }
        if (SecurityInfo != null) {
            iso8583.setField(53, SecurityInfo);
        }
        if (ExtAmount != null) {
            iso8583.setField(54, ExtAmount);
        }
        if (ICCData != null) {
            iso8583.setField(55, ISOUtil.byte2hex(ICCData));
        }
        if (Field60 != null) {
            iso8583.setField(60, Field60);
        }
        if (Field61 != null) {
            iso8583.setField(61, Field61);
        }
        if (Field62 != null) {
            iso8583.setField(62, Field62);
        }
        if (Field63 != null) {
            iso8583.setField(63, Field63);
        }
    }


    public void SetFieldTrans() {

        if (transEname.equals(Type.ANULACION)) {
            setFieldAnulacion();
        }

        LocalTime = PAYUtils.getLocalTime();
        LocalDate = PAYUtils.getLocalDate();

        switch (transEname) {
            case Type.VENTA:
            case Type.SALE_CTL:
            case Type.DEFERRED:
            case Type.PREAUTO:
            case Type.AMPLIACION:
            case Type.CONFIRMACION:
            case Type.VOID_PREAUTO:
            case Type.REIMPRESION:
            case Type.ELECTRONIC:
            case Type.PREVOUCHER:
            case Type.PAGO_PRE_VOUCHER:
            case Type.CASH_OVER:
            case Type.PAGOS_VARIOS:
                setFieldVenta();
                break;
            case SETTLE: //SETTLE
            case Type.AUTO_SETTLE: //AUTO_SETTLE
                setFieldsSettle();
                break;
        }
    }

    public void setFieldVenta() {

        iso8583.clearData();
        Logger.debug("==FinanceTrans->setFields==");

        if (MsgID != null) {
            iso8583.setField(0, MsgID);
        }

        setField02();

        if (ProcCode != null) {
            iso8583.setField(3, ProcCode);
        }

        String AmoutData = setField04();
        if (AmoutData != null) {
            iso8583.setField(4, AmoutData);
        }

        if (TraceNo != null) {
            iso8583.setField(11, TraceNo);
        }

        //LocalTime = PAYUtils.getLocalTime();

        if (LocalTime != null) {
            iso8583.setField(12, LocalTime);
        }

        if (LocalDate != null) {
            iso8583.setField(13, LocalDate);
        }

        ExpDate = setField14();
        if (ExpDate != null) {
            iso8583.setField(14, ExpDate);
        }

        if (EntryMode != null) {
            iso8583.setField(22, EntryMode);
        }
        if (PanSeqNo != null) {
            iso8583.setField(23, PanSeqNo);
        }

        if (Nii != null) {
            iso8583.setField(24, Nii);
        }

        if (SvrCode != null) {
            iso8583.setField(25, SvrCode);
        }

        if (Track2 != null) {
            iso8583.setField(35, Track2);
        }

        setField38();

        if (TermID != null) {
            iso8583.setField(41, TermID);
        }
        if (MerchID != null) {
            iso8583.setField(42, MerchID);
        }

        if (Track1 != null) {
            if (!Track1.equals("") && (EntryMode.equals(MODE_MAG + CapPinPOS())||EntryMode.equals(MODE1_FALLBACK+CapPinPOS())))
                iso8583.setField(45, Track1);
        }

        setField48();
        if (Field48 != null) {
            iso8583.setField(48, Field48);
        }

        if (PIN != null) {
            iso8583.setField(52, PIN);
        }

        setFiled54();
        if (ExtAmount != null) {
            iso8583.setField(54, ExtAmount);
        }

        if (ICCData != null) {
            isField55 = true;
            iso8583.setField(55, ISOUtil.byte2hex(ICCData));
        }

        setField57();
        if (Field57 != null)
            iso8583.setField(57, Field57);

        setField58();
        if (Field58 != null) {
            multicomercio = CommonFunctionalities.isMulticomercio();
            if (multicomercio){
                idComercio = CommonFunctionalities.getIdComercio();
            }
            iso8583.setField(58, Field58);
        }

        setField59();
        if (Field59 != null && !para.getTransType().equals(Type.VOID_PREAUTO)) {
            iso8583.setField(59, Field59);
        }

        Field60 = BatchNo;
        if (Field60 != null) {
            iso8583.setField(60, Field60);
        }

        setField61();
        if (Field61 != null) {
            iso8583.setField(61, Field61);
        }
    }

    private void setField02() {

        if ((para.getTransType().equals(Type.ELECTRONIC)) || (inputMode == ENTRY_MODE_HAND)) {
            if (Pan != null) {
                iso8583.setField(2, Pan);
            }
        }
    }

    private String setField04() {
        String AmoutData;
        if (ISOUtil.stringToBoolean(rango.getTARJETA_CIERRE())){
            AmountBase0 = GetAmount.getMontoTarjetaCierre()[0];
            AmountXX = GetAmount.getMontoTarjetaCierre()[1];
            IvaAmount = 0;
            TipAmount = 0;
            ServiceAmount = 0;
            CashOverAmount = 0;

            Amount = AmountBase0 + AmountXX;
        }else {
            if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO()) && tipoMontoFijo != null) {
                Amount = AmountXX + IvaAmount + montoFijo;
            } else {

                if (!para.getTransType().equals(Type.ANULACION)) {
                    if (CommonFunctionalities.isSumarTotales()) {
                        AmountBase0 += CommonFunctionalities.getSumarTotales();
                    }
                }
                /*Amount = AmountBase0 + AmountXX + IvaAmount + TipAmount + ServiceAmount + CashOverAmount;*/
            }
        }
        AmoutData = ISOUtil.padleft(Amount + "", 12, '0');
        return AmoutData;
    }

    private String setField14() {
        String dat = null;

        if (ExpDate != null){
            expDate = ExpDate;
            processPPFail.setExpDate(expDate);//en caso de fallo
        }

        if (para.getTransType().equals(Type.ELECTRONIC) || para.getTransType().equals(Type.ELECTRONIC_DEFERRED) || TypeTransElectronic!=null) {
            dat = "0000";
        }

        if (inputMode == ENTRY_MODE_HAND) {
            dat = ExpDate;
        }
        return dat;
    }

    private void setField38() {
        if (para.getTransType().equals(Type.VOID_PREAUTO)) {
            iso8583.setField(38, "000000");
        }
    }

    /**
     * 设置交易报文8583各域值，设置完后判断冲正等，即可联机
     */
    private void setFiled54() {
        switch (para.getTransType()) {
            case Type.VOID_PREAUTO:
                ExtAmount = null;
                break;
            default:
                int cont = 0;
                StringBuilder ExtAmount = new StringBuilder();
                if (GetAmount.checkIVA()) {
                    cont++;
                    ExtAmount.append("3112");//1=IVA 12=Longitud Monto
                    String ivaAmount = ISOUtil.padleft(IvaAmount + "", 12, '0');
                    ExtAmount.append(ISOUtil.stringToAscii(ivaAmount));
                }
                if (GetAmount.checkService()) {
                    cont++;
                    ExtAmount.append("3212");//2=Servicio 12=Longitud Monto
                    String serviceAmount = ISOUtil.padleft(ServiceAmount + "", 12, '0');
                    ExtAmount.append(ISOUtil.stringToAscii(serviceAmount));
                }
                if (GetAmount.checkTip()) {
                    cont++;
                    ExtAmount.append("3312");//3=Propina 12=Longitud Monto
                    String propinaAmount = ISOUtil.padleft(TipAmount + "", 12, '0');
                    ExtAmount.append(ISOUtil.stringToAscii(propinaAmount));
                }
                if (para.getTransType().equals(Type.DEFERRED) ||
                        para.getTransType().equals(Type.ANULACION) ||
                        para.getTransType().equals(Type.ELECTRONIC_DEFERRED)) {
                    String idDiferido = "";
                    for (int i = 0; i < deferredType.length; i++) {
                        if (TypeDeferred != null) {
                            if (TypeDeferred.equals(deferredType[i][1])) {
                                idDiferido = deferredType[i][0];
                            }
                        }
                    }
                    switch (idDiferido) {
                        case "002":
                        case "007":
                        case "021":
                        case "022":
                            cont++;
                            ExtAmount.append("3412");//4=Interes 12=Longitud Monto
                            String interes = ISOUtil.padleft(0 + "", 12, '0');//El interes siempre va en cero
                            ExtAmount.append(ISOUtil.stringToAscii(interes));

                            break;
                        default:
                            break;
                    }
                }
                if (PAYUtils.stringToBoolean(tconf.getHABILITA_MONTO_FIJO()) && tipoMontoFijo != null) {
                    switch (tipoMontoFijo) {
                        case NO_OPERA:
                        case PIDE_CONFIRMACION:
                            break;
                        default:
                            cont++;
                            ExtAmount.append("3612");//6=Gasolinera 12=Longitud Monto
                            String gasolinera = ISOUtil.padleft(montoFijo + "", 12, '0');
                            ExtAmount.append(ISOUtil.stringToAscii(gasolinera));
                            break;
                    }
                }

                if (para.getTransType().equals(Type.CASH_OVER)){
                    cont++;
                    ExtAmount.append("3712");//7=Cash Over 12=Longitud Monto
                    String casOver = ISOUtil.padleft(CashOverAmount + "", 12, '0');
                    ExtAmount.append(ISOUtil.stringToAscii(casOver));
                }

                this.ExtAmount = "3" + cont + ExtAmount;

                break;
        }
    }

    private void setField57() {
        StringBuilder fld57 = new StringBuilder();
        switch (para.getTransType()) {
            case Type.DEFERRED:
            case Type.ELECTRONIC_DEFERRED:
                for (int i = 0; i < deferredType.length; i++) {
                    if (TypeDeferred.equals(deferredType[i][1])) {
                        fld57.append(deferredType[i][0]);
                        //Field57 = deferredType[i][0];
                    }
                }
                fld57.append("03");
                fld57.append(ISOUtil.zeropad(numCuotasDeferred, 3));
                break;
            case Type.PAGOS_VARIOS:
                try {
                    fld57.append("008");
                    //Field57 = "008";
                    fld57.append("03");
                    fld57.append(ISOUtil.zeropad(pagoVarioSeleccionado.substring(1), 3));
                    //Field57 += "03" + ISOUtil.zeropad(pagoVarioSeleccionado.substring(1), 3);
                } catch (IndexOutOfBoundsException e) {
                }
                break;
            default:
                if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO()) && tipoMontoFijo != null) {
                    switch (tipoMontoFijo) {
                        case NO_OPERA:
                        case PIDE_CONFIRMACION:
                            fld57.append("00003000");
                            //Field57 = "00003000";
                            break;
                        default:
                            fld57.append("00403000");
                            //Field57 = "00403000";//Identificador 004 solo se utiliza si el tipo de comercio es una Gasolinera, el campo datos siempre es 000
                            break;
                    }
                } else {
                    if (pagoVarioSeleccionado != null){
                        fld57.append("008");
                        //Field57 = "008";
                        fld57.append("03");
                        fld57.append(ISOUtil.zeropad(pagoVarioSeleccionado.substring(1), 3));
                        //Field57 += "03" + ISOUtil.zeropad(pagoVarioSeleccionado.substring(1), 3);
                    }else
                        fld57.append("00003000");
                    //Field57 = "00003000";
                }
                break;
        }

        fld57.append("030");
        fld57.append("03");
        fld57.append(ISOUtil.zeropad(pp_request.getMonthsGrace(),3));

        fld57.append("031");
        fld57.append("03");
        fld57.append(ISOUtil.zeropad(pp_request.getIdCodNetAcq(),3));

        Field57 = fld57.toString();
        //Field57 += "031" + "03" + ISOUtil.zeropad(pp_request.getIdCodNetAcq(),3);
    }

    private void setField58() {
        switch (para.getTransType()) {
            case Type.AMPLIACION:
            case Type.CONFIRMACION:
            case Type.VOID_PREAUTO:
            case Type.ANULACION:
                //case Type.REPRINT:
                Field58 = IdPreAutAmpl;
                break;
            case Type.ELECTRONIC:
            case Type.ELECTRONIC_DEFERRED:
                if (TypeTransElectronic.equals(Type.PAYBLUE)){
                    if (Field58 == null) {
                        Field58 = "001036363137393133313034";//TAG 66 (Identificador de Proveedor de Servicio)
                        Field58 += "00053637303036";//TAG 67 (Identificador de Interfaz utilizada)
                    }else{
                        Field58 += "001036363137393133313034";//TAG 66 (Identificador de Proveedor de Servicio)
                        Field58 += "00053637303036";//TAG 67 (Identificador de Interfaz utilizada)
                    }
                }

                break;
            default:
                break;
        }
    }

    private void setField59() {

        switch (para.getTransType()){
            case Type.PAGOS_VARIOS:
                break;
            default:
                String amountBase0 = ISOUtil.padleft(AmountBase0 + "", 12, '0');
                String amountbasexx = ISOUtil.padleft(AmountXX + "", 12, '0');
                Field59 = "3032313012" + ISOUtil.stringToAscii(amountBase0) + "313112" + ISOUtil.stringToAscii(amountbasexx);
                break;
        }
    }

    private String setField48() {

        String datoPrompt = "";
        StringBuilder tmp = null;
        Field48 = "";

        if (ISOUtil.stringToBoolean(rango.getCVV2())) {
            if (CVV != null) {
                tmp = new StringBuilder();
                datoPrompt = ISOUtil.stringToAscii(CVV);
                tmp.append(ISOUtil.convertStringToHex("92"));//92=CVC2/CVV2 (MasterCard y Visa)
                tmp.append(ISOUtil.padleft(CVV.length() + "", 4, '0'));
                tmp.append(datoPrompt);
                Field48 = tmp.toString();
            }
        }

        if (ISOUtil.stringToBoolean(rango.getV_4DBC())) {
            if (CVV != null) {
                tmp = new StringBuilder();
                datoPrompt = ISOUtil.stringToAscii(CVV);
                tmp.append(ISOUtil.convertStringToHex("95"));//95= 4DBC (American Express)
                tmp.append(ISOUtil.padleft(CVV.length() + "", 4, '0'));
                tmp.append(datoPrompt);
                Field48 += tmp.toString();
            }
        }

        switch (para.getTransType()) {
            //0 = Partial Approval Terminal Support Indicator (Preautorización y Ampliación)
            case Type.PREAUTO:
            case Type.AMPLIACION:
                Field48 += "363100053030303030";//61 =Pos Data Extended Condition Codes
                break;
            //1 = Final Authorization Indicator
            case Type.CONFIRMACION:
                Field48 += "363100053030303031";
                break;
        }

        if (Field48.length() == 0) {
            Field48 = null;
        } else if (para.getTransType().equals(Type.VOID_PREAUTO)) {
            switch (inputMode) {
                case ENTRY_MODE_FALLBACK:
                case ENTRY_MODE_HAND:
                case ENTRY_MODE_MAG:
                    Field48 = null;
                    break;
            }
        }

        return Field48;
    }

    private void setField61() {
        switch (para.getTransType()) {
            case Type.PREAUTO:
            case Type.AMPLIACION:
                Field61 = "00000040003002180000000000";
                break;
            case Type.CONFIRMACION:
            case Type.VOID_PREAUTO:
                Field61 = "00000000003002180000000000";
                break;
            case Type.ELECTRONIC:
            case Type.ELECTRONIC_DEFERRED:
                if (TypeTransElectronic.equals(Type.PAYBLUE)){
                    Field61 = "00000000003002180000000000";
                }
                break;

            default:
                if (PAYUtils.stringToBoolean(rango.getINTER_OPER())) {

                    if (EntryMode.equals(MODE_MAG + CapPinPOS())){
                        Field61 = "03M24020005000300000000000011D2600000000003002180000000000U2302000500030000000000001";
                    }else if(EntryMode.equals(MODE_ICC + CapPinPOS())){
                        Field61 = "03M24020005000300000000000012D2600000000003002180000000000U2302000500030000000000001";
                    } else if (EntryMode.equals(MODE_HANDLE + CapPinPOS())){
                        Field61 = "03M24020005000300000000000015D2600000000003002180000000000U2302000500030000000000001";
                    } else if (EntryMode.equals(MODE_CTL + CapPinPOS())){
                        if (ICCData!=null){
                            Field61 = "03M24020005000300000000000012D2600000000003002180000000000U2302000500030000000000001";
                        }else{
                            Field61 = "03M24020005000300000000000012D2600000000004002180000000000U2302000500030000000000001";
                        }
                    } else if (EntryMode.equals(MODE1_FALLBACK + CapPinPOS())){
                        Field61 = "03M24020005000300000000000013D2600000000003002180000000000U2302000500030000000000001";
                    }
                }

                break;
        }
    }

    public void setFieldAnulacion() {

        Logger.debug("==FinanceTrans->setFieldAnulacion==");
        iso8583.clearData();

        if (MsgID != null) {
            iso8583.setField(0, MsgID);
        }

        if (Pan != null && EntryMode != null) {
            switch (inputMode){
                case ENTRY_MODE_HAND:
                case 102:
                case 101:
                    iso8583.setField(2, Pan);
                    break;
            }
        }

        if (ProcCode != null) {
            iso8583.setField(3, ProcCode);
        }

        String AmoutData = setField04();
        if (AmoutData != null) {
            iso8583.setField(4, AmoutData);
        }

        if (TraceNo != null) {
            iso8583.setField(11, TraceNo);
        }

        if (LocalTime != null) {
            iso8583.setField(12, PAYUtils.getLocalTime());
        }

        if (LocalDate != null) {
            iso8583.setField(13, PAYUtils.getLocalDate().substring(0, 4));
        }

        ExpDate = setField14();
        if (ExpDate != null) {
            iso8583.setField(14, ExpDate);
        }

        if (EntryMode != null) {
            iso8583.setField(22, EntryMode);
        }

        if (PanSeqNo != null) {
            iso8583.setField(23, PanSeqNo);
        }

        if (Nii != null) {
            iso8583.setField(24, Nii);
        }

        if (SvrCode != null) {
            iso8583.setField(25, SvrCode);
        }

        if (Track2 != null) {
            iso8583.setField(35, Track2);
        }

        if (AuthCode != null) {
            iso8583.setField(38, AuthCode);
        }

        if (TermID != null) {
            iso8583.setField(41, TermID);
        }
        if (MerchID != null) {
            iso8583.setField(42, MerchID);
        }

        setField48();
        if (Field48 != null) {
            iso8583.setField(48, Field48);
        }

        //setFiled54();
        if (ExtAmount != null) {
            iso8583.setField(54, ExtAmount);
        }

        if (ICCData != null) {
            isField55 = true;
            iso8583.setField(55, ISOUtil.byte2hex(ICCData));
        }

        //setField57();
        if (Field57 != null)
            iso8583.setField(57, Field57);

        //setField58();
        if (Field58 != null) {
            iso8583.setField(58, Field58);
        }

        setField59();
        if (Field59 != null) {
            iso8583.setField(59, Field59);
        }

        Field60 = BatchNo;
        if (Field60 != null) {
            iso8583.setField(60, Field60);
        }

        //setField61();
        if (Field61 != null) {
            iso8583.setField(61, Field61);
        }
    }

    private void setFieldsSettle() {

        iso8583.clearData();
        Logger.debug("==FinanceTrans->setFieldsLogout==");

        if (MsgID != null) {
            iso8583.setField(0, MsgID);
        }

        if (ProcCode != null) {
            iso8583.setField(3, ProcCode);
        }

        if (TraceNo != null) {
            iso8583.setField(11, TraceNo);
        }

        if (Nii != null) {
            iso8583.setField(24, Nii);
        }

        if (TermID != null) {
            iso8583.setField(41, TermID);
        }

        if (MerchID != null) {
            iso8583.setField(42, MerchID);
        }

        if (BatchNo != null) {
            iso8583.setField(60, BatchNo);
        }

        if (Field63 != null)
            iso8583.setField(63, Field63);

    }

    private void setFieldsBatchNo(TransLogData data) {

        iso8583.clearData();
        Logger.debug("==FinanceTrans->setFields==");

        iso8583.setField(0, "0320");

        switch (data.getEntryMode()) {
            case "011"://Manual
            case "012"://Manual unionPay
                if (data.getPan() != null)
                    iso8583.setField(2, data.getPan());

                if (data.getExpDate() != null)
                    iso8583.setField(14, data.getExpDate());
                break;
            case "101"://PE
            case "102"://PE
                if (data.getPanPE() != null)
                    iso8583.setField(2, data.getPanPE());

                if (data.getExpDate() != null)
                    iso8583.setField(14, data.getExpDate());

                break;
        }

        if (data.getProcCode() != null)
            iso8583.setField(3, data.getProcCode());

        String AmoutData;
        AmoutData = ISOUtil.padleft(data.getAmount() + "", 12, '0');
        iso8583.setField(4, AmoutData);

        if (data.getTraceNo() != null) {
            iso8583.setField(11, data.getTraceNo());
        }

        if (data.getLocalTime() != null) {
            iso8583.setField(12, data.getLocalTime());
        }

        if (data.getLocalDate() != null) {
            iso8583.setField(13, data.getLocalDate().substring(4));
        }


        if (data.getEntryMode() != null) {
            iso8583.setField(22, data.getEntryMode());
        }

        if (data.getNii() != null) {
            iso8583.setField(24, data.getNii());
        }

        if (data.getSvrCode() != null) {
            iso8583.setField(25, data.getSvrCode());
        }

        if (data.getTrack2() != null) {
            iso8583.setField(35, data.getTrack2());
        }

        if (data.getRRN() != null) {
            iso8583.setField(37, data.getRRN());
        }

        if (data.getAuthCode() != null) {
            iso8583.setField(38, data.getAuthCode());
        }

        if (data.getTermID() != null) {
            iso8583.setField(41, data.getTermID());
        }
        if (data.getMerchID() != null) {
            iso8583.setField(42, data.getMerchID());
        }

        if (data.getTrack1() != null) {
            iso8583.setField(45, data.getTrack1());
        }

        if (data.getCVV() != null) {
            iso8583.setField(48, data.getCVV());
        }

        if (data.getField54() != null) {
            iso8583.setField(54, data.getField54());
        }

        if (data.getField57() != null) {
            iso8583.setField(57, data.getField57());
        }

        if (data.getField58() != null) {
            if (data.getEName().equals(Type.CONFIRMACION)){
                iso8583.setField(58, data.getField58());
            }
        }

        if (data.getField59() != null) {
            iso8583.setField(59, data.getField59());
        }

        iso8583.setField(60, data.getMsgID() + data.getTraceNo() + data.getBatchNo());


    }

    protected void setNII() {

        switch (transEname) {
            case SETTLE:
                if (host_confi.getNII_CIERRE() != null)
                    Nii = ISOUtil.padleft(host_confi.getNII_CIERRE() + "", 4, '0');
                else
                    Nii = "0000";
                break;

            case PAGOS_VARIOS:
                if (host_confi.getNII_PAGOS_VARIOS() != null)
                    Nii = ISOUtil.padleft(host_confi.getNII_PAGOS_VARIOS() + "", 4, '0');
                else
                    Nii = "0000";
                break;

            default:
                if (host_confi.getNII_TRANSACCIONES() != null)
                    Nii = ISOUtil.padleft(host_confi.getNII_TRANSACCIONES() + "", 4, '0');
                else
                    Nii = "0000";
                break;

        }
    }

    protected int Reverse(){
        retVal = Tcode.T_not_reverse;
        if (isProcPreTrans) {
            List<TransLogData> list = TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).getData();
            TransLogData revesalData = null;

            //REVISO SI ES REVERSO AUTOMATICO (CAJA)
            if (pp_request.getTypeTrans().equals("04")){
                revesalData = TransLog.getReversal(true);
            }

            //VALIDO SI EXISTEN REVERSOS
            if (revesalData != null || list.size() > 0) {

                //SE INSTANCIA CLASE QUE SE ENCARGA
                //DEL ENVIO DE EL O LOS REVERSOS EXISTENTES
                //transUI.handling(timeout + 10000, Tcode.Status.terminal_reversal);
                RevesalTrans revesal = new RevesalTrans(context, "REVERSAL", transUI);

                //CICLO QUE SIRVE PARA INTENTAR ENVIAR UN REVERSO X VECES POR DEFECTO 1
                for (int i = 0; i < 1; i++) {

                    //SE VALIDA LA DATA DE LA ULTIMA TRANSACCION PARA ENVIAR EL REVERSO
                    //EN CASO DE LLEGAR EL COMANDO 04 VALIDADO ANTERIORMENTE
                    if (revesalData != null){
                        //SE ENVIA EL REVERSO
                        retVal = revesal.sendRevesal(revesalData);
                        if (retVal == 0) {
                            TransLog.clearReveral(true);
                            transUI.toasTransReverse(Tcode.Status.rev_receive_ok, true, false);
                            break;
                        } else {
                            if (retVal != Tcode.T_socket_err && retVal != Tcode.T_send_err) {
                                continue;
                            }
                        }
                    }

                    //SE RECORRE EL BATCH DE REVERSOS PARA HACER EL RESPECTIVO ENVIO Y ELIMINACION
                    //CADA REVERSO CUENTA CON 3 INTENTOS PARA ENVIARSE EN CASO CONTRARIO SE ELIMINA
                    for (int x = 0; x < list.size(); x++) {
                        revesalData = list.get(x);

                        //SE VALIDA LA DATA Y SE PROCEDE A ENVIAR EL REVERSO
                        if (revesalData != null){
                            retVal = revesal.sendRevesal(revesalData);
                        }

                        //SE VERIFICA SI EL ENVIO DEL REVERSO FUE EFECTIVO Y SE CONTINUA CON
                        // LOS DEMAS REVERSOS EN CASO CONTRARIO SE FINALIZA LA TRANSACCION
                        if (retVal == 0) {
                            deleteReverse_Save(revesalData,false);
                            transUI.toasTransReverse(Tcode.Status.rev_receive_ok, true, false);
                            continue;
                        } else {
                            //SE ACTUALIZA EL NUMERO DE INTENTOS Y SE TERMINA EL ENVIO DEL REVERSO
                            revesalData.setIntRev(String.valueOf(Integer.parseInt(revesalData.getIntRev()) + 1));
                            if (!PAYUtils.isNullWithTrim(revesal.rspCode)){
                                revesalData.setRspCode(revesal.rspCode);
                            }

                            //SE VERIFICA EL NUMERO DE INTENTOS QUE LLEVA EL REVERSO INTETANDO ENVIARSE
                            //SI ES IGUAL A 3 SE ELIMINA EL REVERSO
                            if (Integer.parseInt(revesalData.getIntRev()) >= 3){
                                if (deleteReverse_Save(revesalData,false)){
                                    break;
                                }
                            }else {
                                deleteReverse_Save(revesalData,true);
                            }
                            break;
                        }
                    }
                }

                //SE VALIDA RETVAL CON EL VALOR RECIBIDO
                if (retVal == Tcode.T_socket_err || retVal == Tcode.T_send_err) {
                    //transUI.toasTrans(Tcode.T_err_send_rev, true, false);
                    return retVal;
                } else {
                    if (retVal != 0) {
                        return retVal;
                    }
                }
            }
        }

        if (pp_request.getTypeTrans().equals("04")){
            transEname = "REVERSAL";
            responsePP();
        }

        return retVal;
    }

    private boolean deleteReverse_Save(TransLogData data, boolean update){
        boolean ret = false;
        int index = TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).getCurrentIndex(data);
        System.out.println("index " + index);
        if (index >= 0){
            if (update){
                TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).updateTransLog(index, data);
                ret = TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).saveLog(idAcquirer + FILE_NAME_REVERSE);
            }else {
                ret = TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).deleteTransLog(index);
                ret = TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).saveLog(idAcquirer + FILE_NAME_REVERSE);
            }
        }
        return ret;
    }

    protected int OnlineTrans(EmvTransaction emvTrans) {

        byte[] tag9f27 = new byte[1];
        byte[] tag9b = new byte[2];

        switch (Server.cmd) {
            case LT:
                responseLT();
                return retVal;

            case CT:
                responseCT();
                return retVal;
        }

        TransLogData Reveral = null;
        int indexRev = -1;

        setNII();
        SetFieldTrans();

        transUI.handling(timeout, Tcode.Status.connecting_center);

        int retries = Integer.parseInt(host_confi.getREINTENTOS());  //Intentos
        int startRetries = 1;
        int rta;

        do { // Intentara N veces el connect
            transUI.handling(timeout, Tcode.Status.connecting_center, "CONECTANDO IP1 (" + startRetries + ")");
            rta = connect();
            if (rta == 0) {
                startRetries = retries;
            }
            startRetries ++;
            transUI.handling(timeout, Tcode.Status.msg_retry);
            transUI.handling(timeout, Tcode.Status.connecting_center);
        }while (retries >= startRetries);

        if (rta == -1){
            retries = Integer.parseInt(host_confi.getREINTENTOS());
            startRetries = 1;
            cfg = TMConfig.getInstance();
            cfg.setPubCommun(false);
            loadConfigIP();
            do {
                transUI.handling(timeout, Tcode.Status.connecting_center, "CONECTANDO IP2 (" + startRetries + ")");
                rta = connect();
                if (rta == 0) {
                    startRetries = retries;
                }
                startRetries ++;
            }while (retries >= startRetries);
        }

        if (rta == -1) {
            return Tcode.T_socket_err;
        }

        if (isReversal) {
            Logger.debug("FinanceTrans->OnlineTrans->save Reversal");

            Reveral = setReveralData();
            TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).saveLog(Reveral, idAcquirer + FILE_NAME_REVERSE);
            indexRev = TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).getCurrentIndex(Reveral);

            TransLog.clearReveral(true);

            TransLog.saveReversal(Reveral, true);
        }

        transUI.handling(timeout, Tcode.Status.send_data_2_server);
        retVal = send();

        if (retVal == -1) {
            return Tcode.T_send_err;
        }

        if (retVal == 0) {
            if (isTraceNoInc) {
                cfg.incTraceNo();
            }
        }

        transUI.handling(timeout, Tcode.Status.send_over_2_recv);
        byte[] respData = recive();
        netWork.close();
        if (respData == null || respData.length <= 0) {
            return Tcode.T_no_answer;
        }

        retVal = iso8583.unPacketISO8583(respData);

        if (retVal != 0) {
            if (retVal == Tcode.T_package_mac_err) {
                if (isReversal) {
                    //Devuelva el mensaje de verificación de error de MAC, actualice la causa correcta A0
                    //TransLogData newR = TransLog.getReversal(false);
                    if (Reveral != null){
                        Reveral.setRspCode("A0");
                        if (indexRev >= 0){
                            TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).deleteTransLog(indexRev);
                            TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).saveLog(Reveral, idAcquirer + FILE_NAME_REVERSE);
                        }
                    }

                }
            }
            return retVal;
        }

        RspCode = iso8583.getfield(39);

        PAYUtils.dateTime(iso8583.getfield(13), iso8583.getfield(12));

        if (verificarCodDiners()!=0)
            return retVal;

        String strICC = iso8583.getfield(55);

        //Esta validacion se incluye por que para las anulaciones (0400 maneja DF),
        //No se recibe P38 entonces se debe tomar el que se envia en el requerimiento
        switch (transEname){
            case Type.ANULACION:
            //case Type.VOID_PREAUTO:
            //case Type.REIMPRESION:
                break;

            default:
                AuthCode = iso8583.getfield(38);
                break;
        }

        if (strICC != null && (!strICC.trim().equals(""))) {
            ICCData = ISOUtil.str2bcd(strICC, false);
        } else {
            ICCData = null;
        }

        if ("95".equals(RspCode) && (para.getTransType().equals(SETTLE) || para.getTransType().equals(Type.AUTO_SETTLE))) {
            transUI.handling(timeout, Tcode.Status.settle_error);
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Logger.error("Exception" + e.toString());
                }
                break;
            }
            transUI.handling(timeout, Tcode.Status.send_data_2_server);
            retVal = sendBatchUpload();
            if (retVal == 0) {
                ProcCode = "960000";
                setFieldsSettle();
                retVal = OnLineTrans();
                if (retVal == 0)
                    RspCode = iso8583.getfield(39);
            }
        }
        if ("95".equals(RspCode)) {
            return 95;
        }

        if (!"00".equals(RspCode) && !para.getTransType().equals(Type.REIMPRESION)) {
            if (indexRev >= 0){
                TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).deleteTransLog(indexRev);
            }
            //Trans reject
            int ret = formatRsp(RspCode);
            return ret;
        }

        if (para.getTransType().equals(Type.REIMPRESION) && !RspCode.equals("05")) {
            if (indexRev >= 0){
                TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).deleteTransLog(indexRev);
            }
            int ret = formatRsp(RspCode);
            return ret;
        }

        if (inputMode == ENTRY_MODE_ICC) {
            boolean need2AC = TransEName.equals(Type.VENTA) || TransEName.equals(Type.QUICKPASS);

            if (emvTrans != null && retVal == 0 && need2AC) {
                retVal = emvTrans.afterOnline(RspCode, AuthCode, ICCData, retVal);
                int lenOf9f27 = PAYUtils.get_tlv_data_kernal(0x9F27, tag9f27);
                if (lenOf9f27 != 1) {
                    // Procesamiento de falla de IC Si el campo 39 es 00, el archivo de actualización es correcto. 39 Campo 06
                    if (isReversal){
                        if (Reveral != null){
                            Reveral.setRspCode("06");
                            if (indexRev >= 0){
                                TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).deleteTransLog(indexRev);
                                TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).saveLog(Reveral, idAcquirer + FILE_NAME_REVERSE);
                            }
                        }
                    }
                }
                if (tag9f27[0] != 0x40) {
                    // Aprobado en segundo plano, rechazado por la tarjeta, para mantener el golpe
                    return Tcode.T_gen_2_ac_fail;
                }
                //Resultado del script del emisor
                int len9b = PAYUtils.get_tlv_data_kernal(0x9b, tag9b);
                if (len9b == 2 && (tag9b[0] & 0x04) != 0) {
                    // Guarde los resultados del script de línea de tarjeta
                    byte[] temp = new byte[256];
                    int len = PAYUtils.pack_tags(PAYUtils.wISR_tags, temp);
                    if (len > 0) {
                        ICCData = new byte[len];
                        System.arraycopy(temp, 0, ICCData, 0, len);
                    } else {
                        ICCData = null;
                    }
                    TransLogData scriptResult = setScriptData();
                    TransLog.saveScriptResult(scriptResult);
                }
            }

        } else if (inputMode == ENTRY_MODE_NFC) {
            if (isNeedGAC2) {
                retVal = genAC2Trans();
                if (retVal != PBOCode.PBOC_TRANS_SUCCESS) {
                    return retVal;
                }
            }
        }

        if (retVal != 0) {
            return retVal;
        }


        //脚本上送
        TransLogData data = TransLog.getScriptResult();
        if (data != null) {
            ScriptTrans script = new ScriptTrans(context, "SENDSCRIPT");
            int ret = script.sendScriptResult(data);
            if (ret == 0) {
                TransLog.clearScriptResult();
            }
        }

        TransLogData logData;

        if (isSaveLog) {
            logData = setLogData();
            switch (transEname){
                case Type.VOID_PREAUTO:
                    int index = TransLog.getInstance(host_id).getCurrentIndex(logData);
                    TransLog.getInstance(host_id).deleteTransLog(index);
                    TransLog.getInstance(host_id).saveLog(logData, host_id);
                    break;
                default:
                    transLog.saveLog(logData, host_id);
                    break;
            }
        } else {
            logData = setLogData();
        }

        if (indexRev >= 0){
            TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).deleteTransLog(indexRev);
        }

        if (para.isNeedPrint()) {
            retVal = printData(logData);
        }

        responsePP();
        return retVal;
    }

    protected int OfflineTrans() {

        SetFieldTrans();
        cfg.incTraceNo();

        TransLogData logData;

        if (isSaveLog) {
            logData = setLogData();
            transLog.saveLog(logData, host_id);
        } else {
            logData = setLogData();
        }

        if (para.isNeedPrint()) {
            retVal = printData(logData);
        }

        return retVal;
    }

    private void check_exists_pre_aut(TransLogData logData) {

        try {
            //Se elimina el registro si el trace coincide con alguno del archivo
            if (TransLog.getInstance(host_id).searchTransLogByTraceNo(logData.getTraceNo()) != null) {
                int index = TransLog.getInstance(host_id).getCurrentIndex(logData);
                TransLog.getInstance(host_id).deleteTransLog(index);
                if (TransLog.getInstance(host_id).getSize() == 0) {
                    TransLog.getInstance(host_id).clearAll(host_id);
                } else
                    TransLog.getInstance(host_id).saveLog(host_id);
            }

        } catch (Exception e) {
            e.getMessage();
        }

    }

    private int sendBatchUpload() {

        List<TransLogData> list = TransLog.getInstance(idAcquirer).getData();
        TransLogData data;

        for (int i = 0; i < list.size(); i++) {

            data = list.get(i);

            if (!data.isVoided() && !data.isTarjetaCierre()) {
                setFieldsBatchNo(data);

                retVal = OnLineTrans();

                if (retVal == 0) {
                    RspCode = iso8583.getfield(39);
                    if (!RspCode.equals("00")) {
                        return Tcode.T_err_batch_trans;
                    }
                } else {
                    Logger.debug("Revesal result :" + retVal);
                }
            } else {
                retVal = 0;
            }
        }
        return retVal;
    }

    public static String getTypeCoin(String TypeCoin) {
        String typeCoinL;
        switch (TypeCoin) {
            case LOCAL:
                typeCoinL = "840";
                break;
            case DOLAR:
                typeCoinL = "840";
                break;
            case EURO:
                typeCoinL = "978";
                break;
            default:
                typeCoinL = "840";
                break;
        }
        return typeCoinL;
    }

    private String getNameCardSwhipe(String trak1) {
        String nameCard = null;
        try {
            String[] parts = trak1.split("\\^");
            nameCard = parts[1];
            return nameCard.trim();
        }catch (ArrayIndexOutOfBoundsException e){}
        return null;
    }

    public String getNameCard() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x5F20, temp);
        String nameCard = new String(temp, 0, len);
        return nameCard.trim();
    }

    @NonNull
    protected String getLabelCard() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x50, temp);
        String aux = null;
        try {
            aux = new String(temp, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return aux.trim().substring(0, len);
    }

    public String getARQC() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x9F26, temp);
        String aux = ISOUtil.bcd2str(temp, 0, len);
        return aux.trim();
    }

    public String getCID() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x9F27, temp);
        String aux = ISOUtil.bcd2str(temp, 0, len);
        return aux.trim();
    }

    public String getAID() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x9F06, temp);
        String aux = ISOUtil.bcd2str(temp, 0, len);
        return aux.trim();
    }

    public String getTC() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x9F26, temp);
        String aux = ISOUtil.bcd2str(temp, 0, len);
        return aux.trim();
    }

    protected String getTVR() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x95, temp);
        String aux = ISOUtil.bcd2str(temp, 0, len);
        return aux.trim();
    }

    public String getTSI() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x9B, temp);
        String aux = ISOUtil.bcd2str(temp, 0, len);
        return aux.trim();
    }

    private String getPreferenceLabelCard() {
        byte[] temp = new byte[128];
        int len = PAYUtils.get_tlv_data_kernal(0x9F11, temp);
        String aux = null;

        try {
            aux = new String(temp, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error("Exception" + e.toString());
            Thread.currentThread().interrupt();
        }

        if (temp[0] == 1) {
            len = PAYUtils.get_tlv_data_kernal(0x9F12, temp);
            aux = null;

            try {
                aux = new String(temp, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Logger.error("Exception" + e.toString());
                Thread.currentThread().interrupt();
            }
        } else {
            len = PAYUtils.get_tlv_data_kernal(0x50, temp);
            aux = null;

            try {
                aux = new String(temp, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Logger.error("Exception" + e.toString());
                Thread.currentThread().interrupt();
            }
        }
        if (aux != null) {
            return aux.trim().substring(0, len);
        }
        return null;
    }

    private TransLogData setLogData() {

        TransLogData LogData = new TransLogData();

        if (MsgID != null) {
            LogData.setMsgID(MsgID);
        }

        if (ProcCode != null) {
            LogData.setProcCode(ProcCode);
        }

        if (typeCoin != null) {
            LogData.setTypeCoin(typeCoin);
        }

        if (iso8583.getfield(2)!=null){
            LogData.setPanNormal(iso8583.getfield(2));
            LogData.setPan(packageMaskedCard(iso8583.getfield(2)));
            LogData.setPanPE(packageMaskedCard(iso8583.getfield(2)));
        }else {
            if (Pan != null) {
                if (TransEName.equals(Type.ANULACION)){
                    if (TypeTransElectronic!=null){
                        switch (TypeTransElectronic) {
                            case Trans.Type.PAYCLUB:
                            case Trans.Type.PAYBLUE:
                                LogData.setPanNormal(Pan);
                                LogData.setPan(packageMaskedCard(Pan));
                                LogData.setPanPE(PanPE);
                                break;
                        }
                    }else{
                        LogData.setPanNormal(Pan);
                        LogData.setPan(packageMaskedCard(Pan));
                    }
                }else{
                    LogData.setPan(Pan);
                    LogData.setPanNormal(Pan);
                    LogData.setPan(packageMaskedCard(Pan));
                }
            }
        }

        if (ExpDate != null) {
            LogData.setExpDate(ExpDate);
        }

        LogData.setOprNo(cfg.getOprNo());

        if (BatchNo != null) {
            LogData.setBatchNo(BatchNo);
        }
        if (TransEName != null) {
            LogData.setEName(TransEName);
            if (TypeTransVoid != null)
                LogData.setTypeTransVoid(TypeTransVoid);
        }

        LogData.setAAC(FinanceTrans.AAC_ARQC);

        if (AmountBase0 != 0) {
            LogData.setAmmount0(AmountBase0);
        }

        if (AmountXX != 0) {
            LogData.setAmmountXX(AmountXX);
        }

        if (IvaAmount != 0) {
            LogData.setAmmountIVA(IvaAmount);
        }

        if (TipAmount != 0) {
            LogData.setTipAmout(TipAmount);
        }

        if (ServiceAmount != 0) {
            LogData.setAmmountService(ServiceAmount);
        }

        if (CashOverAmount != 0) {
            LogData.setAmmountCashOver(CashOverAmount);
        }

        if (Amount != 0) {
            LogData.setAmount(Amount);
        }

        if (ExtAmount != null) {
            LogData.setField54(ExtAmount);
        }

        if (montoFijo != 0) {
            LogData.setMontoFijo(montoFijo);
        }

        if (tipoMontoFijo != null) {
            LogData.setTipoMontoFijo(tipoMontoFijo);
        }

        if (transEname.equals(Type.PREVOUCHER)) {
            if (isTip)
                LogData.setTip(isTip);
        }

        if (iso8583.getfield(11) != null) {
            LogData.setTraceNo(iso8583.getfield(11));
        } else if (TraceNo != null) {
            LogData.setTraceNo(TraceNo);
        }

        if (LocalTime != null) {
            LogData.setLocalTime(LocalTime);
        }

        LogData.setLocalDate(PAYUtils.getYear() + LocalDate);

        LogData.setDatePrint(PAYUtils.getMonth() + " " + PAYUtils.getDay() + "," + PAYUtils.getYear());

        if (numCuotas > 0) {
            LogData.setNumCuotas(numCuotas);
        }
        if (iso8583.getfield(14) != null) {
            LogData.setExpDate(iso8583.getfield(14));
        }
        if (iso8583.getfield(15) != null) {
            LogData.setSettleDate(iso8583.getfield(15));
        }
        if (EntryMode != null) {
            LogData.setEntryMode(EntryMode);
        }
        if (iso8583.getfield(23) != null) {
            LogData.setPanSeqNo(iso8583.getfield(23));
        }
        if (Nii != null) {
            LogData.setNii(Nii);
        }
        if (SvrCode != null) {
            LogData.setSvrCode(SvrCode);
        }
        if (iso8583.getfield(32) != null) {
            LogData.setAcquirerID(iso8583.getfield(32));
        }
        if (Track2 != null) {
            LogData.setTrack2(Track2);
        }
        if (iso8583.getfield(37) != null) {
            LogData.setRRN(iso8583.getfield(37));
        }

        switch (transEname){
            case Type.ANULACION:
            case Type.REIMPRESION:
                if (AuthCode != null) {
                    LogData.setAuthCode(AuthCode);
                }
                break;
            case Type.VOID_PREAUTO:
                LogData.setAuthCode(AuthCode);
                LogData.setVoided(true);
                break;

            default:
                if (iso8583.getfield(38) != null) {
                    LogData.setAuthCode(iso8583.getfield(38));
                }
                break;
        }

        if (iso8583.getfield(39) != null) {
            LogData.setRspCode(iso8583.getfield(39));
        }
        if (TermID != null) {
            LogData.setTermID(TermID);
        }
        if (MerchID != null) {
            LogData.setMerchID(MerchID);
        }

        //Si se recibe el P42 se maneja como interoperabilidad en el Voucher
        if (iso8583.getfield(42) != null){
            LogData.setMID_InterOper(iso8583.getfield(42));
        }

        String id = iso8583.getfield(44);
        if (id != null) {
            String field44="";
            try {
                //id = ISOUtil.toHex(id);
                LogData.setAddRespData(id);
                if (id.length() == 5)
                    field44 = CardType[Integer.parseInt(id.substring(0, 1)) - 1];
                else {
                    field44 = CardType[Integer.parseInt(id.substring(1, 2)) - 1];
                }
            }catch (IndexOutOfBoundsException e){}

            //Adquirente
            LogData.setField44(field44);
        }

        if (iso8583.getfield(49) != null) {
            LogData.setCurrencyCode(iso8583.getfield(49));
        }

        if (Field57 != null) {
            LogData.setField57(Field57);
        }
        if (iso8583.getfield(57) != null) {

            LogData.setField57Print(iso8583.getfield(57));

            if (multicomercio) {
                String[] rspField57 = UnpackFld57MultiAcq(iso8583.getfield(57));

                if (rspField57[NOMBRE_COMERCIO]!=null){
                    nameMultAcq = rspField57[NOMBRE_COMERCIO];
                    LogData.setNameMultAcq(nameMultAcq);
                }
                /*if (rspField57[MID]!=null){
                    LogData.setMerchID(ISOUtil.padright("" + rspField57[MID], 15, ' '));
                }*/
            }
        }
        if (iso8583.getfield(58) != null) {
            LogData.setField58(getIdPreAuto(iso8583.getfield(58)));
        }

        if (Field58 != null) {
            LogData.setField58(Field58);
            LogData.setMulticomercio(multicomercio);
            if (multicomercio)
                LogData.setIdComercio(idComercio);
        }

        if (Fld58PromptsPrinter != null) {
            LogData.setPromptsPrinter(CommonFunctionalities.getFld58PromptsPrinter());
        }

        if (Fld58PromptsAmountPrinter != null){
            LogData.setPromptsAmountPrinter(CommonFunctionalities.getFld58PromptsAmountPrinter());
        }

        if (iso8583.getfield(59) != null) {
            switch (transEname) {
                case Type.CONFIRMACION:
                    LogData.setField59(Field59);
                    LogData.setField59Print(iso8583.getfield(59));
                    break;
                default:
                    LogData.setField59(iso8583.getfield(59));
                    break;
            }
        }else{
            if (Field59 != null){
                LogData.setField59(Field59);
            }
        }

        if (CurrencyCode != null) {
            LogData.setCurrencyCode(CurrencyCode);
        }
        if (PIN != null) {
            LogData.setPIN(PIN);
        }
        if (Field62 != null) {
            LogData.setField62(Field62);
        }
        if (Field63 != null) {
            LogData.setField63(Field63);
        }
        if (Field61 != null) {
            LogData.setField61(Field61);
        }
        if (ICCData != null) {
            LogData.setICCData(ICCData);
            LogData.setField55(ISOUtil.byte2hex(ICCData));
        }
        if (isField55){
            LogData.setIsField55(isField55);
        }
        if (inputMode == ENTRY_MODE_NFC) {
            LogData.setNFC(true);
        }
        if (inputMode == ENTRY_MODE_ICC) {
            LogData.setICC(true);
        }

        if (isFallBack)
            LogData.setFallback(isFallBack);

        if (IdPreAutAmpl != null) {
            LogData.setIdPreAutAmpl(IdPreAutAmpl);
        }

        if (TypeTransElectronic != null) {
            LogData.setTypeTransElectronic(TypeTransElectronic);
        }

        if (pagoVarioSeleccionado != null) {
            LogData.setPagoVarioSeleccionado(pagoVarioSeleccionado);
        }

        if (pagoVarioSeleccionadoNombre != null){
            LogData.setPagoVarioSeleccionadoNombre(pagoVarioSeleccionadoNombre);
        }

        if (TypeDeferred != null) {
            LogData.setTypeDeferred(TypeDeferred);
        }

        if (CodOTT != null) {
            LogData.setOTT(CodOTT);
        }

        if (TokenElectronic != null) {
            LogData.setToken(TokenElectronic);
        }

        if (TransEName.equals(Type.ANULACION)){
            if (issuerName != null) {
                LogData.setIssuerName(issuerName);
            }

            if (labelName != null) {
                LogData.setLabelCard(labelName);
            }

            if (nameMultAcq != null){
                LogData.setNameMultAcq(nameMultAcq);
            }

        }else {
            if (rango.getNOMBRE_EMISOR() != null) {
                LogData.setIssuerName(rango.getNOMBRE_EMISOR());
            }

            if (rango.getNOMBRE_RANGO() != null) {
                LogData.setLabelCard(rango.getNOMBRE_RANGO());
            }
        }

        if (EntryMode != null) {

            if (EntryMode.equals(MODE_ICC + CapPinPOS())) {
                LogData.setNameCard(getNameCard());
            } else if (EntryMode.equals(MODE_MAG + CapPinPOS()) || EntryMode.equals(MODE1_FALLBACK + CapPinPOS()) || EntryMode.equals(MODE2_FALLBACK + CapPinPOS())) {
                if (Track1 != null)
                    LogData.setNameCard(getNameCardSwhipe(Track1));
            } else if (EntryMode.equals(MODE_CTL + CapPinPOS())) {
                if (!MasterControl.HOLDER_NAME.equals("---"))
                    LogData.setNameCard(MasterControl.HOLDER_NAME);
                if (emvl2.GetLable() != null) {
                    LogData.setAIDName(emvl2.GetLable());
                }
                if (emvl2.GetAid() != null){
                    LogData.setAID(emvl2.GetAid());
                }
            }

            if (EntryMode.equals(MODE_ICC + CapPinPOS())) {
                LogData.setAID(getAID());
                LogData.setARQC(getARQC());
                LogData.setTC(getTC());
                LogData.setTVR(getTVR());
                LogData.setTSI(getTSI());
                LogData.setTypeAccount(getLabelCard());
            }
        }

        if (tconf.getNOMBRE_COMERCIO() != null) {
            LogData.setNameTrade(tconf.getNOMBRE_COMERCIO());
        }

        if (tconf.getDIRECCION_PRINCIPAL() != null) {
            LogData.setAddressTrade(tconf.getDIRECCION_PRINCIPAL());
            LogData.setPhoneTrade(tconf.getTELEFONO_COMERCIO());

        }

        //Para tarjeta cierre no se almacena la trans en el batch
        if (ISOUtil.stringToBoolean(rango.getTARJETA_CIERRE())){
            //isSaveLog = false;
            LogData.setTarjetaCierre(true);
        }

        LogData.setAlreadyPrinted(false);

        //Verificar y firma cedula y numero de telefono
        String isSignature = tconf.getHABILITAR_FIRMA();

        if (isSignature.equals("1") && !para.getTransType().equals(Type.ANULACION) && !para.getTransType().equals(SETTLE)) {
            InputInfo inputInfo = transUI.showSignature(timeout, "FIRMA", para.getTransType());
            if (inputInfo.getResult() != null){
                if (!inputInfo.getResult().trim().equals("false")) {
                    String[] parts = inputInfo.getResult().trim().split(";");
                    LogData.setCedula(parts[0]);
                    if (parts.length > 1)
                        LogData.setTelefono(parts[1]);
                }
            }

        }

        return LogData;
    }

    /**
     * 保存扫码交易数据
     *
     * @param code 付款码
     * @return
     */
    protected TransLogData setScanData(String code) {
        TransLogData LogData = new TransLogData();
        LogData.setAmount(Amount);
        LogData.setPan(code);
        LogData.setOprNo(cfg.getOprNo());
        LogData.setBatchNo(BatchNo);
        LogData.setEName(TransEName);
        LogData.setICCData(ICCData);
        if (inputMode == ENTRY_MODE_NFC) {
            LogData.setNFC(true);
        }
        if (inputMode == ENTRY_MODE_ICC) {
            LogData.setICC(true);
        }
        if (inputMode == ENTRY_MODE_QRC) {
            LogData.setScan(true);
        }
        LogData.setLocalDate(PAYUtils.getYMD());
        LogData.setTraceNo(TraceNo);
        LogData.setLocalTime(PAYUtils.getHMS());
        LogData.setSettleDate(PAYUtils.getYMD());
        LogData.setAcquirerID("12345678");
        LogData.setRRN("170907084952");
        LogData.setAuthCode("084952");
        LogData.setRspCode("00");
        LogData.setField44("0425       0461       ");
        LogData.setCurrencyCode("156");
        return LogData;
    }

    /**
     * 脱机打单
     *
     * @param ec_amount
     * @return
     */
    protected int offlineTrans(String ec_amount) {
        if (isSaveLog) {
            TransLogData LogData = new TransLogData();
            if (para.getTransType().equals(Type.EC_ENQUIRY)) {
                LogData.setAmount(Long.parseLong(ec_amount));
            } else {
                LogData.setAmount(Amount);
            }
            LogData.setPan(PAYUtils.getSecurityNum(Pan, 6, 3));
            LogData.setOprNo(cfg.getOprNo());
            LogData.setEName(TransEName);
            LogData.setEntryMode(ISOUtil.padleft(inputMode + "", 2, '0') + "10");
            LogData.setTraceNo(cfg.getTraceNo());
            LogData.setBatchNo(cfg.getBatchNo());
            LogData.setLocalDate(PAYUtils.getYear() + PAYUtils.getLocalDate());
            LogData.setLocalTime(PAYUtils.getLocalTime());
            LogData.setAAC(FinanceTrans.AAC_TC);
            LogData.setICCData(ICCData);
            if (inputMode == ENTRY_MODE_NFC) {
                LogData.setNFC(true);
            }
            if (inputMode == ENTRY_MODE_ICC) {
                LogData.setICC(true);
            }
            transLog.saveLog(LogData);
            if (isTraceNoInc) {
                cfg.incTraceNo();
            }
        }
        if (para.isNeedPrint()) {
            transUI.handling(timeout, Tcode.Status.printing_recept);
            PrintManager print = PrintManager.getmInstance(context, transUI);
            do {
                retVal = print.print(transLog.getLastTransLog(), false, false);
            } while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
            if (retVal == Printer.PRINTER_OK) {
                return 0;
            } else {
                return Tcode.T_printer_exception;
            }
        } else {
            return 0;
        }
    }

    /**
     * 设置发卡行脚本数据
     *
     * @return
     */
    private TransLogData setScriptData() {
        TransLogData LogData = new TransLogData();
        LogData.setPan(PAYUtils.getSecurityNum(Pan, 6, 3));
        LogData.setICCData(ICCData);
        LogData.setBatchNo(BatchNo);
        LogData.setAmount(Long.parseLong(iso8583.getfield(4)));
        LogData.setTraceNo(iso8583.getfield(11));
        LogData.setLocalTime(iso8583.getfield(12));
        LogData.setLocalDate(iso8583.getfield(13));
        LogData.setEntryMode(iso8583.getfield(22));
        LogData.setPanSeqNo(iso8583.getfield(23));
        LogData.setAcquirerID(iso8583.getfield(32));
        LogData.setRRN(iso8583.getfield(37));
        LogData.setAuthCode(iso8583.getfield(38));
        LogData.setCurrencyCode(iso8583.getfield(49));
        return LogData;
    }

    /**
     * 设置冲正数据
     *
     * @return
     */
    private TransLogData setReveralData() {

        TransLogData LogData = new TransLogData();

        if (transEname != null) {
            LogData.setEName(transEname);
        }

        if ((para.getTransType().equals(Type.ELECTRONIC)) || (inputMode == ENTRY_MODE_HAND)) {
            if (Pan != null) {
                LogData.setPan(Pan);
            }
        }

        if (ProcCode != null) {
            LogData.setProcCode(ProcCode);
        }

        LogData.setAmount(Amount);

        LogData.setTipAmout(TipAmount);

        if (TraceNo != null) {
            LogData.setTraceNo(TraceNo);
        }

        if (LocalTime != null) {
            LogData.setLocalTime(LocalTime);
        }

        if (LocalDate != null) {
            LogData.setLocalDate(LocalDate);
        }

        if (EntryMode != null) {
            LogData.setEntryMode(EntryMode);
        }

        setField14();
        if (ExpDate != null) {
            LogData.setExpDate(ExpDate);
        }

        if (PanSeqNo != null) {
            LogData.setPanSeqNo(PanSeqNo);
        }

        if (Nii != null) {
            LogData.setNii(Nii);
        }

        if (SvrCode != null) {
            LogData.setSvrCode(SvrCode);
        }

        if (Track2 != null) {
            LogData.setTrack2(Track2);
        }

        if (TermID != null) {
            LogData.setTermID(TermID);
        }

        if (MerchID != null) {
            LogData.setMerchID(MerchID);
        }

        if (ExtAmount != null) {
            LogData.setField54(ExtAmount);
        }

        if (ICCData != null) {
            LogData.setICCData(ICCData);
            LogData.setField55(ISOUtil.byte2hex(ICCData));
        }

        if (Field57 != null) {
            LogData.setField57(Field57);
        }

        if (Field58 != null) {
            LogData.setField58(Field58);
        }

        if (Field59 != null) {
            LogData.setField59(Field59);
        }

        if (Field60 != null) {
            LogData.setField60(Field60);
        }

        setField61();
        if (Field61 != null) {
            LogData.setField61(Field61);
        }

        if (Field63 != null) {
            LogData.setField63(Field63);
        }

        LogData.setAlreadyPrinted(false);

        return LogData;
    }

    /**
     * 格式化处理响应码
     *
     * @param rsp
     * @return
     */
    public static int formatRsp(String rsp) {
        //String[] stand_rsp = {"5A", "5B", "6A", "A0", "D1", "D2", "D3", "D4", "N6", "N7"};
        String[] stand_rsp = {
                "00","01","02","03","04","05","06","07","08","09",
                "10","11","12","13","14","15","16","17","18","19",
                "20","21","22","23","24","25","26","27","28","29",
                "30","31","32","33","34","35","36","37","38","39",
                "40","41","42","43","44","45","46","47","48","49",
                "50","51","52","53","54","55","56","57","58","59","60",
                "61","62","63","64","65","66","67","68","69","70",
                "71","72","73","74","75","76","77","78","79","80",
                "81","82","83","84","85","86","87","88","89","90",
                "91","92","93","94","95","96","97","98","99","A1",
                "A2","A3","A4","A5","A6","A7","A8","A9","AN","B0",
                "B1","B2","B3","B4","B5","B6","B7","B8","B9","C0",
                "C1","C2","C3","C4","C5","C6","C7","C8","C9","D0",
                "D1","D2","D3","D4","D5","D6","D7","D8","D9","DP",
                "E1","K0","K1","K2","K3","N0","N3","N4","N7","P0",
                "P1","P2","Q1","U4","UB","XA","XD","Z3","YY","ZZ",
                "N8",
        };
        int START = 3000;
        boolean finded = false;
        for (int i = 0; i < stand_rsp.length; i++) {
            if (stand_rsp[i].equals(rsp)) {
                START += i;
                finded = true;
                break;
            }
        }
        if (finded) {
            return START;
        } else {
            //return Integer.parseInt(rsp);
            return 4000;
        }
    }

    /**
     * 不进行报文联机，用于无网演示
     * 将不对数据进行任何校验与处理
     *
     * @return
     */
    private int LocalPresentations() {
        if (isSaveLog) {
            TransLogData LogData = new TransLogData();
            LogData.setAmount(Amount);
            LogData.setPan(PAYUtils.getSecurityNum(Pan, 6, 3));
            LogData.setOprNo(cfg.getOprNo());
            LogData.setEName(TransEName);
            LogData.setEntryMode(ISOUtil.padleft(inputMode + "", 2, '0') + "10");
            LogData.setTraceNo(cfg.getTraceNo());
            LogData.setBatchNo(cfg.getBatchNo());
            LogData.setLocalDate(PAYUtils.getYear() + PAYUtils.getLocalDate());
            LogData.setLocalTime(PAYUtils.getLocalTime());
            LogData.setAuthCode(PAYUtils.getLocalTime());
            LogData.setAAC(FinanceTrans.AAC_TC);
            LogData.setICCData(ICCData);
            if (inputMode == ENTRY_MODE_NFC) {
                LogData.setNFC(true);
            }
            if (inputMode == ENTRY_MODE_ICC) {
                LogData.setICC(true);
            }
            transLog.saveLog(LogData);
            if (isTraceNoInc) {
                cfg.incTraceNo();
            }
        }
        if (para.isNeedPrint()) {
            Logger.debug("FinanceTrans>>NotNeedOnline>>开始打单");
            transUI.handling(timeout, Tcode.Status.printing_recept);
            PrintManager print = PrintManager.getmInstance(context, transUI);
            do {
                retVal = print.print(transLog.getLastTransLog(), false, false);
            } while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
            if (retVal == Printer.PRINTER_OK) {
                return 0;
            } else {
                return Tcode.T_printer_exception;
            }
        } else {
            return 0;
        }
    }

    private int printData(TransLogData logData) {
        transUI.handling(timeout, Tcode.Status.printing_recept);
        PrintManager printManager = PrintManager.getmInstance(context, transUI);
        printManager.setHost_id(host_id);
        do {
            retVal = printManager.print(logData, false, false);
            if (!logData.getEName().equals(SETTLE) && !logData.getEName().equals(Trans.Type.AUTO_SETTLE)) {

                InputInfo inputInfo = transUI.showMessageImpresion("INFORMACION", "¿DESEA IMPRIMIR COPIA?", "NO", "SI", 30*1000);

                if (ISOUtil.stringToBoolean(tconf.getCOPIA_VOUCHER())){
                    if (inputInfo.isResultFlag())
                        printManager.print(logData, true, false);
                }
                else{
                    printManager.print(logData, true, false);
                }
            }
        } while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
        if (retVal == Printer.PRINTER_OK) {
            retVal = 0;
        } else {
            retVal = Tcode.T_printer_exception;
        }
        return retVal;
    }

    private int printDataReject(String value1, String value2, int ret) {
        transUI.handling(timeout, Tcode.Status.printing_recept);
        PrintManager printManager = PrintManager.getmInstance(context, transUI);
        do {
            retVal = printManager.printTransreject(value1, value2, ret);
        } while (retVal == Printer.PRINTER_STATUS_PAPER_LACK);
        if (retVal == Printer.PRINTER_OK) {
            retVal = 0;
        } else {
            retVal = Tcode.T_printer_exception;
        }
        return retVal;
    }

    /**
     * deal with GAC2
     *
     * @return
     */
    private int genAC2Trans() {
        PBOCOnlineResult result = new PBOCOnlineResult();
        result.setField39(RspCode.getBytes());
        result.setFiled38(AuthCode.getBytes());
        result.setField55(ICCData);
        result.setResultCode(PBOCOnlineResult.ONLINECODE.SUCCESS);
        int retVal = pbocManager.afterOnlineProc(result);
        Logger.debug("genAC2Trans->afterOnlineProc:" + retVal);

        //Issue script deal result
        int isResult = pbocManager.getISResult();
        if (isResult != EMVISRCode.NO_ISR) {
            // save issue script result
            byte[] temp = new byte[256];
            int len = PAYUtils.pack_tags(PAYUtils.wISR_tags, temp);
            if (len > 0) {
                ICCData = new byte[len];
                System.arraycopy(temp, 0, ICCData, 0, len);
            } else {
                ICCData = null;
            }
            TransLogData scriptResult = setScriptData();
            TransLog.saveScriptResult(scriptResult);
        }

        if (retVal != PBOCode.PBOC_TRANS_SUCCESS) {
            //IC card transaction failed, if return "00" in field 39,
            //update the field 39 as "06" in reversal data
            TransLogData revesalData = TransLog.getReversal(false);
            if (revesalData != null) {
                revesalData.setRspCode("06");
                TransLog.saveReversal(revesalData, false);
            }
        }

        return retVal;
    }

    private int verificarCodDiners(){

        //Codigo Diners
        if ("88".equals(RspCode)){
            for (int i = 0; i < 3; i++) {

                transUI.handling(timeout, Tcode.Status.msg_cod_diners);
                 // espere 20 seg
                try {
                    Thread.sleep(20*1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Logger.error("Exception" + e.toString());
                }

                transUI.handling(timeout, Tcode.Status.send_data_2_server);
                isCodDinners = true;
                retVal = OnLineTrans();
                transUI.handling(timeout, Tcode.Status.send_over_2_recv);
                if (retVal == 0) {

                    RspCode = iso8583.getfield(39);

                    if (RspCode.equals("00")){
                        return retVal;
                    }
                    if (RspCode.equals("88")){
                        continue;
                    }else {
                        break;
                    }
                }
            }
        }

        return retVal;
    }

    public String packageMaskedCard(String pan){
        String panTemp="";
        if (pan==null)
            return panTemp;
        switch (rango.getTIPO_MASCARA()){
            case CENTRO:
                panTemp = PAYUtils.getSecurityNum(pan, 6, 3);
                break;
            case INICIO_FIN:
                panTemp = PAYUtils.getSecurityNum2(pan);
                break;
            case SIN_MASCARA:
                panTemp = pan;
                break;
            default:
                panTemp = PAYUtils.getSecurityNum(pan, 6, 3);
                break;
        }
        return panTemp;
    }

    private String[] UnpackFld57MultiAcq(String fld57) {
        String id = "";
        String[] rspField57 = new String[2];
        String[] identificadoresActivos = new String[25];

        final int ID_007 = 5;
        final int ID_017 = 14;
        final int ID_018 = 15;

        for (int i = 0; i < rspField57.length; i++) {
            rspField57[i] = "-";
        }

        for (int i = 0; i < identificadoresActivos.length; i++) {
            identificadoresActivos[i] = "-";
        }

        if (fld57 != null) {
            id = fld57.substring(0, 3);
            String msg = fld57.substring(3);

            try {
                switch (id) {

                    //Nombre y MID del POS
                    case "007":
                        identificadoresActivos[ID_007] = id;
                        rspField57[NOMBRE_COMERCIO] = msg.substring(0, 16);
                        rspField57[MID] = msg.substring(16, 26);
                        break;
                    //Nombre + MID + Valor financiacion
                    case "017":
                        identificadoresActivos[ID_017] = id;
                        rspField57[NOMBRE_COMERCIO] = msg.substring(0, 16);
                        rspField57[MID] = msg.substring(16, 26);
                        break;
                    case "018":
                        identificadoresActivos[ID_018] = id;
                        rspField57[NOMBRE_COMERCIO] = msg.substring(0, 16);
                        rspField57[MID] = msg.substring(16, 26);
                        break;
                }
            } catch (IndexOutOfBoundsException e) {
            }
        }

        return rspField57;
    }

    /**
     * Procesar Pago
     */
    public static byte[] ppResponse;
    private void responsePP() {

        pp_response.setTypeMsg(PP);
        pp_response.setRspCodeMsg(OK);
        int prp = retVal;
        pp_response.setIdCodNetAcq(ISOUtil.padleft(pp_request.getIdCodNetAcq() + "", 2, '0'));
        pp_response.setRspCode(ISOUtil.spacepadZero(RspCode, 2));
        String mensaje = getStatusInfo(String.valueOf(codersp(transEname)));
        if (mensaje.length() > 20){
            mensaje = mensaje.substring(0,20);
        }
        pp_response.setMsgRsp(ISOUtil.padright(mensaje + "", 20, ' '));
        if ( pp_request.getTypeTrans().equals("04")){
            pp_response.setSecuencialTrans(ISOUtil.spacepad("",6));
            pp_response.setNumberBatch(ISOUtil.spacepadRight("",6));
        }else {
            pp_response.setSecuencialTrans(ISOUtil.spacepadRight(TraceNo,6));
            pp_response.setNumberBatch(ISOUtil.spacepadRight(BatchNo,6));
        }

        if (iso8583.getfield(12) != null && !iso8583.getfield(12).isEmpty()){
            pp_response.setHourTrans(ISOUtil.spacepadRight(iso8583.getfield(12), 6));
        }else {
            pp_response.setHourTrans(ISOUtil.spacepadRight(PAYUtils.getLocalTime(), 6));
        }

        if (iso8583.getfield(13) != null){
            pp_response.setDateTrans(ISOUtil.spacepadRight(PAYUtils.getYear() + iso8583.getfield(13), 8));
        }else {
            pp_response.setDateTrans(ISOUtil.spacepadRight(PAYUtils.getLocalDate2(), 8));
        }

        if (transEname.equals(Type.ANULACION)){
            AuthCode = "";
        }
        pp_response.setNumberAuth(ISOUtil.spacepadRight(AuthCode, 6));

        if (iso8583.getfield(41) != null){
            pp_response.setTID(ISOUtil.spacepadRight(iso8583.getfield(41), 8));
        }else {
            pp_response.setTID(TermID);
        }

        if (iso8583.getfield(42) != null){
            pp_response.setMID(ISOUtil.spacepadRight(iso8583.getfield(42), 15));
        }else {
            pp_response.setMID(ISOUtil.spacepadRight(MerchID, 15));
        }


        String fld57 = iso8583.getfield(57);
        if (fld57 != null){
            String id = fld57.substring(0,3);
            String msg = fld57.substring(3);
            String interes = financiacion(id,msg);
            if (interes.equals("")){
                pp_response.setInterestFinancingValue(ISOUtil.spacepadRight(interes, 12));
            }else {
                pp_response.setInterestFinancingValue(ISOUtil.padleft(interes, 12, '0'));
            }

            pp_response.setMsgPrintAwards(ISOUtil.spacepadRight(publicVoucher(id, msg), 80));
        }else {
            pp_response.setInterestFinancingValue(ISOUtil.spacepadRight("", 12));
            pp_response.setMsgPrintAwards(ISOUtil.spacepadRight("", 80));
        }

        try {
            String fld44 = iso8583.getfield(44);
            if (fld44!=null) {
                pp_response.setCodBankAcq(ISOUtil.padleft(fld44.substring(0, 2), 3,'0'));
                if (fld44.length() == 5)
                    pp_response.setNameBankAcq(ISOUtil.spacepadRight(CardType[Integer.parseInt(fld44.substring(0, 1)) - 1], 30));
                else
                    pp_response.setNameBankAcq(ISOUtil.spacepadRight(CardType[Integer.parseInt(fld44.substring(1, 2)) - 1], 30));
            }else{
                pp_response.setCodBankAcq(ISOUtil.spacepadRight("", 3));
                pp_response.setNameBankAcq(ISOUtil.spacepadRight("", 30));
            }
        }catch (IndexOutOfBoundsException e){}

        pp_response.setNameGroupCard(ISOUtil.spacepadRight(rango.getNOMBRE_RANGO(),25));
        pp_response.setModeReadCard(PAYUtils.entryModePP(inputMode, isFallBack));

        if (montoFijo > 0){
            pp_response.setFixedAmount(ISOUtil.padleft(montoFijo + "", 12, '0'));
        }else {
            pp_response.setFixedAmount(ISOUtil.padleft( "", 12, ' '));
        }

        if (inputMode == ENTRY_MODE_NFC) {
            pp_response.setNameCardHolder(ISOUtil.spacepadRight(verifyHolderName(emvl2.getHolderName()), 40));
            pp_response.setARQC(ISOUtil.spacepadRight(emvl2.GetARQC(),16));
            pp_response.setTVR(ISOUtil.spacepadRight(emvl2.GetTVR(),10));
            pp_response.setTSI(ISOUtil.spacepadRight(emvl2.GetTSI(),4));
            pp_response.setAppEMV(ISOUtil.spacepadRight(emvl2.GetLable(), 20));
            pp_response.setAIDEMV(ISOUtil.spacepadRight(emvl2.GetAid(), 20));
            pp_response.setCriptEMV(ISOUtil.spacepadRight("", 22));
            pp_response.setValidatePIN(ISOUtil.spacepadRight("", 15));
            pp_response.setExpDateCard(ISOUtil.spacepadRight(emvl2.getExpdate(),4));
        } else if (inputMode == ENTRY_MODE_ICC){
            pp_response.setNameCardHolder(ISOUtil.spacepadRight(getNameCard(), 40));
            pp_response.setARQC(ISOUtil.spacepadRight(ARQC+"",16));
            pp_response.setTVR(ISOUtil.spacepadRight(getTVR(),10));
            pp_response.setTSI(ISOUtil.spacepadRight(getTSI(),4));
            pp_response.setAppEMV(ISOUtil.spacepadRight(getLabelCard(), 20));
            pp_response.setAIDEMV(ISOUtil.spacepadRight(getAID(), 20));
            pp_response.setCriptEMV(ISOUtil.spacepadRight(getTC(), 22));
            pp_response.setValidatePIN(ISOUtil.spacepad("", 15));
            pp_response.setExpDateCard(ISOUtil.spacepadRight(expDate,4));
        }else {
            if (Track1 != null) {
                pp_response.setNameCardHolder(ISOUtil.spacepadRight(getNameCardSwhipe(Track1), 40));
            } else {
                pp_response.setNameCardHolder(ISOUtil.spacepadRight("", 40));
            }
            pp_response.setARQC(ISOUtil.spacepadRight("",16));
            pp_response.setTVR(ISOUtil.spacepadRight("",10));
            pp_response.setTSI(ISOUtil.spacepadRight("",4));
            pp_response.setAppEMV(ISOUtil.spacepadRight("", 20));
            pp_response.setAIDEMV(ISOUtil.spacepadRight("", 20));
            pp_response.setCriptEMV(ISOUtil.spacepad("", 22));
            pp_response.setValidatePIN(ISOUtil.spacepad("", 15));
            pp_response.setExpDateCard(ISOUtil.spacepadRight(expDate,4));
        }

        if (transEname.equals(Trans.Type.ANULACION) || transEname.equals("REVERSAL")){
            pp_response.setExpDateCard(ISOUtil.spacepad("",4));
        }

        String numberCard;
        if (transEname.equals(Trans.Type.ELECTRONIC)){
            pp_response.setNumberCardMask(ISOUtil.spacepadRight(Pan,25));
            pp_response.setFiller(ISOUtil.spacepadRight(packageMaskedCard(iso8583.getfield(2)), 27));
            numberCard = iso8583.getfield(2);
            /*pp_response.setNumberCardEncrypt(ISOUtil.spacepad(encryption.hashSha256(iso8583.getfield(2)),64));*/
        }else {
            pp_response.setNumberCardMask(ISOUtil.spacepadRight(packageMaskedCard(Pan),25));
            numberCard = Pan;
            /*pp_response.setNumberCardEncrypt(ISOUtil.spacepad(encryption.hashSha256(Pan),64));*/
        }

        if (tconf.getSIMBOLO_EURO().equals("0")){
            pp_response.setNumberCardEncrypt(ISOUtil.spacepad(encryption.hashSha1(numberCard),64));
        }else {
            pp_response.setNumberCardEncrypt(ISOUtil.spacepad(encryption.hashSha256(numberCard),64));
        }

        String isSignature = checkNull(tconf.getHABILITAR_FIRMA());
        if (isSignature.equals("1")) {
            pp_response.setFiller(ISOUtil.spacepadRight(decode64(), 27));
        }else {
            if (!transEname.equals(Trans.Type.ELECTRONIC)){
                pp_response.setFiller(ISOUtil.spacepadRight("", 27));
            }
        }
        pp_response.setHash(keySecurity);

        ppResponse = pp_response.packData();

    }

    private String verifyHolderName(String nameCard){
        boolean isHexa;
        String ret = "";
        if (!nameCard.equals("---")) {
            if (nameCard.length() > 0) {
                isHexa = nameCard.matches("^[0-9a-fA-F]+$");                   //validacion de variable labelCard para evitar conversion
                if (!isHexa) {
                    nameCard = ISOUtil.convertStringToHex(nameCard);
                }
                ret = ISOUtil.hex2AsciiStr(nameCard.trim());
            }
        }else{
            return ret;
        }
        return ret;
    }

    private String decode64(){
        String encoded = null;

        String dir = Environment.getExternalStorageDirectory().toString() + "/saved_signature/";
        File f0 = new File(dir, "signature.jpeg");
        boolean d0 = f0.exists();
        if (d0) {
            long lenFile = f0.length();
            if (lenFile > 2544) {
                Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/saved_signature/" + "signature.jpeg");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                //Se elimina el archivo con la firma del SD
                f0.delete();
            }
        }

        return encoded;
    }

    private String checkNull(String strText) {
        if (strText == null) {
            strText = "   ";
        }
        return strText;
    }

    private String financiacion(String id, String msg){
        String ret = "";

        try{
            switch (id){
                case "004":
                case "014":
                    ret = msg.substring(0,8);
                    break;
                case "017":
                    ret = msg.substring(29, 37);
                    break;
                case "021":
                case "023":
                    ret = msg.substring(3, 11);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    private String publicVoucher(String id, String msg){
        String ret = "";

        try{
            switch (id){
                case "008":
                    msg = msg.toUpperCase();
                    ret = msg;
                    break;
                case "009":
                    msg = msg.toLowerCase();
                    ret = msg;
                    break;
                case "021":
                    ret = msg.substring(19);
                    break;
                case "016":
                case "025":
                    if (msg.equals("")){
                        msg = id;
                    }
                    ret = msg;
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    private int codersp(String trans){
        int ret = 0;
        switch (trans){
            case Type.VENTA:
            case Type.DEFERRED:
            case Type.ANULACION:
            case Type.PAYCLUB:
            case Type.PAYBLUE:
            case Type.PAGOS_VARIOS:
            case Type.ELECTRONIC:
            case Type.ELECTRONIC_DEFERRED:
                ret = Tcode.Status.trans_ok;
                //ret = Tcode.Status.sale_succ;
                break;
            /*case Type.DEFERRED:
                ret = Tcode.Status.diferido_exitoso;
                break;
            case Type.ANULACION:
                ret = Tcode.Status.void_succ;
                break;
            case Type.PAYCLUB:
            case Type.PAYBLUE:
                ret = Tcode.Status.pago_electronico_exitoso;
                break;
            case Type.PAGOS_VARIOS:
                ret = Tcode.Status.pago_vario_succ;
                break;*/
            case Type.REVERSAL:
                ret = Tcode.Status.trans_approved;
                break;
        }
        return ret;
    }

    protected int validateReverseCash(){
        int ret = 1995;
        if (Server.cmd.equals(PP)){
            if (retVal == Tcode.T_mid_tid_invalid){
                transUI.showError(timeout,retVal,processPPFail);
            }
            if (pp_request.getTypeTrans().equals("04")){
                keySecurity = pp_request.getHash();
                retVal = Reverse();
                if (retVal == 0){
                    Server.cmd = "PP_REVERSE";
                    transUI.trannSuccess(timeout, Tcode.Status.rev_receive_ok);
                    UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                }else {
                    transUI.showError(timeout, retVal,processPPFail);
                    UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
                    //processPPFail.cmdCancel(Server.cmd, retVal);
                }
                return retVal;
            }
        }
        return ret;
    }

    /**
     * Lectura de tarjeta
     */
    private void responseLT() {
        ltResponse.setTypeMsg(LT);
        ltResponse.setRspCodeMsg(PAYUtils.selectRspCode(retVal,RspCode));
        ltResponse.setIdCodNetCte("0");
        ltResponse.setIdCodNetDef("0");
        ltResponse.setCardNumber(ISOUtil.padright(PAYUtils.getSecurityNum(Pan, 6, 3) + "", 25, ' '));
        if (inputMode == ENTRY_MODE_NFC) {
            ltResponse.setCardExpDate(ISOUtil.spacepadRight(emvl2.getExpdate(),4));
        }else {
            ltResponse.setCardExpDate(ISOUtil.spacepadRight(ExpDate,4));
        }
        rulesPinPad.processCardNumber(Track2, Pan);
        ltResponse.setCardNumEncryp(ISOUtil.padright(rulesPinPad.getCardNumber() + "", 64,' '));
        //ltResponse.setMsgRsp("LECTURA OK          ");
        if (retVal == 0){
            ltResponse.setMsgRsp(ISOUtil.padright("LECTURA OK" + "", 20, ' '));
        }else {
            ltResponse.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(retVal)) + "", 20, ' '));
        }
        ltResponse.setFiller(ISOUtil.spacepad("", 27));
        ltResponse.setHash(keySecurity);
        retVal = 0;

        ppResponse = ltResponse.packData();
        //listenerServer.waitRspHost(ltResponse.packData());
    }

    /**
     * Consulta de tarjeta
     */
    private void responseCT() {
        ctResponse.setTypeMsg(CT);
        ctResponse.setRspCodeMsg(PAYUtils.selectRspCode(retVal,RspCode));
        //ctResponse.setCardNumber(encryption.hashSha256(Pan));
        rulesPinPad.processCardNumber(Track2, Pan);
        ctResponse.setCardNumber(ISOUtil.padright(rulesPinPad.getCardNumber() + "", 64,' '));
        ctResponse.setBinCard(Pan.substring(0,6));
        if (inputMode == ENTRY_MODE_NFC) {
            ctResponse.setCardExpDate(ISOUtil.spacepadRight(emvl2.getExpdate(),4));
        }else {
            ctResponse.setCardExpDate(ISOUtil.spacepadRight(ExpDate,4));
        }
        ctResponse.setCardExpDate(ISOUtil.spacepadRight(ExpDate,4));
        //ltResponse.setMsgRsp("LECTURA OK          ");
        if (retVal == 0){
            ctResponse.setMsgRsp(ISOUtil.padright("CONSULTA OK" + "", 20, ' '));
        }else {
            ctResponse.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(retVal)) + "", 20, ' '));
        }
        ctResponse.setFiller(ISOUtil.spacepad("", 27));
        ctResponse.setHash(keySecurity);
        retVal = 0;

        ppResponse = ctResponse.packData();
        //listenerServer.waitRspHost(ctResponse.packData());
    }

    protected boolean checkBatchAndSettle(boolean checkBatch, boolean checkSettle){
        if (checkBatch) {
            if (transLog.getSize() >= TOTAL_BATCH) {
                transUI.showError(timeout, Tcode.T_err_batch_full,processPPFail);
                //processPPFail.cmdCancel(Server.cmd,Tcode.T_err_batch_full);
                return false;
            }
        }

        if (checkSettle) {
            if (!CommonFunctionalities.checkCierre(context)) {
                transUI.showError(timeout, Tcode.T_err_batch_full,processPPFail);
                UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
                //processPPFail.cmdCancel(Server.cmd,Tcode.T_err_batch_full);
                return false;
            }
        }

        return true;
    }

    /**
     * Amounts for PP
     * @return
     */
    protected boolean setAmountPP() {

        switch (Server.cmd) {

            case CT:
                CT_Request ct_request = new CT_Request();
                ct_request.UnPackData(Server.dat);

                keySecurity = ct_request.getHash();
                if (ct_request.getCountValid() > 0){
                    processPPFail.responseCTInvalid(keySecurity);
                    transUI.showError(timeout, Tcode.T_err_trm);
                    return false;
                }

                AmountBase0 = 0;
                AmountXX = 0;
                IvaAmount = 0;
                ServiceAmount = 0;
                TipAmount = 0;
                ExtAmount = ISOUtil.padleft(TipAmount + "", 12, '0');
                Amount = 1;
                retVal = 0;

                para.setAmountBase0(AmountBase0);
                para.setAmountXX(AmountXX);
                para.setIvaAmount(IvaAmount);
                para.setServiceAmount(ServiceAmount);
                para.setTipAmount(TipAmount);
                para.setAmount(Amount);
                para.setOtherAmount(0);

                para.setCurrency_name(currency_name);
                para.setTypeCoin(typeCoin);

                break;

            case LT:

                LT_Request lt_request = new LT_Request();
                lt_request.UnPackData(Server.dat);

                keySecurity = lt_request.getHash();
                if (lt_request.getCountValid() > 0){
                    processPPFail.responseLTInvalid(keySecurity);
                    transUI.showError(timeout, Tcode.T_err_trm);
                    return false;
                }

                AmountBase0 = 0;
                AmountXX = 0;
                IvaAmount = 0;
                ServiceAmount = 0;
                TipAmount = 0;
                ExtAmount = ISOUtil.padleft(TipAmount + "", 12, '0');
                Amount = 1;
                retVal = 0;

                para.setAmountBase0(AmountBase0);
                para.setAmountXX(AmountXX);
                para.setIvaAmount(IvaAmount);
                para.setServiceAmount(ServiceAmount);
                para.setTipAmount(TipAmount);
                para.setAmount(Amount);
                para.setOtherAmount(0);

                para.setCurrency_name(currency_name);
                para.setTypeCoin(typeCoin);
                break ;

            case PP:

                if (pp_request.getAmountTotal()!=null) {

                    if (pp_request.getAmountNotIVA()!=null && !pp_request.getAmountNotIVA().equals(""))
                        AmountBase0 = Long.parseLong(pp_request.getAmountNotIVA());

                    if (pp_request.getAmountIVA()!=null && !pp_request.getAmountIVA().equals(""))
                        AmountXX = Long.parseLong(pp_request.getAmountIVA());

                    if (pp_request.getIVA()!=null && !pp_request.getIVA().equals(""))
                        IvaAmount = Long.parseLong(pp_request.getIVA());

                    if (pp_request.getService()!=null && !pp_request.getService().equals(""))
                        ServiceAmount = Long.parseLong(pp_request.getService());

                    if (pp_request.getTips()!=null && !pp_request.getTips().equals("")) {
                        TipAmount = Long.parseLong(pp_request.getTips());
                        ExtAmount = ISOUtil.padleft(TipAmount + "", 12, '0');
                    }

                    if (pp_request.getAmountTotal()!=null && !pp_request.getAmountTotal().equals(""))
                        Amount = Long.parseLong(pp_request.getAmountTotal());

                    montos = new long[7];
                    montos[0] = IvaAmount;
                    montos[1] = ServiceAmount;
                    montos[2] = TipAmount;
                    montos[3] = AmountXX;
                    montos[4] = AmountBase0;
                    montos[5] = 0;

                    para.setAmountBase0(AmountBase0);
                    para.setAmountXX(AmountXX);
                    para.setIvaAmount(IvaAmount);
                    para.setServiceAmount(ServiceAmount);
                    para.setTipAmount(TipAmount);
                    para.setAmount(Amount);
                    para.setOtherAmount(0);

                    para.setCurrency_name(currency_name);
                    para.setTypeCoin(typeCoin);

                    keySecurity = pp_request.getHash(); //Por ahora se realiza un echo de la llave recibida desde la caja
                    retVal = 0;

                    return true;
                } else {
                    retVal = Tcode.T_user_cancel_input;
                    transUI.showError(timeout, Tcode.T_user_cancel_input,processPPFail);
                    return false;
                }
        }

        return true;
    }

    protected boolean CardProcess(int mode) {
        if (lastCmd.equals(LT) && Server.cmd.equals(PP) && lastInputMode == ENTRY_MODE_MAG){
            isDebit = false;
            inputMode = ENTRY_MODE_MAG;
            return isMag1(lastTrack);
        }
        String TransEname = "";
        CardInfo cardInfo;
        if (Server.cmd.equals(LT)){
            TransEname = "LECTURA\nDE TARJETA";
        }else if (Server.cmd.equals(CT)){
            TransEname = "CONSULTA\nDE TARJETA";
        }

        if (Server.cmd.equals(LT) || Server.cmd.equals(CT)){
            mode = INMODE_IC | INMODE_MAG | INMODE_HAND;
            cardInfo = transUI.getCardUse(GERCARD_MSG_ICC_SWIPE, timeout, mode, TransEname);
        }else {
            cardInfo = transUI.getCardUseAmount(GERCARD_MSG_SWIPE_ICC_CTL, timeout, mode, transEname,"Monto\nTotal : ",PAYUtils.getStrAmount(Amount));
        }

        if (cardInfo.isResultFalg()) {
            contFallback = 0;
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
                    transUI.showError(timeout, Tcode.T_not_allow,processPPFail);
                    return false;
            }
            para.setInputMode(inputMode);
            processPPFail.setInputMode(inputMode);
            lastInputMode = inputMode;
            if (inputMode == ENTRY_MODE_ICC) {
                if(isICC1()){
                    return true;
                }
            }
            if (inputMode == ENTRY_MODE_MAG) {
                isDebit = false;
                if(isMag1(cardInfo.getTrackNo())){
                    if (Server.cmd.equals(LT)){
                        lastTrack = cardInfo.getTrackNo();
                    }
                    return true;
                }else {
                    lastInputMode = 0x00;
                    lastCmd = "";
                    lastTrack = null;
                }
            }
            if (inputMode == ENTRY_MODE_NFC) {
                if (cfg.isForcePboc()) {
                    if(isICC1()){
                        return true;
                    }
                } else {
                    if(PBOCTrans1()){
                        return true;
                    }
                }
            }
            if (inputMode == ENTRY_MODE_HAND) {
                isDebit = false;
                if(isHandle1()){
                    return true;
                }
            }
        } else {
            retVal = cardInfo.getErrno();
            if (retVal == 0) {
                transUI.showError(timeout, Tcode.T_user_cancel_input,processPPFail);
                return false;
            }
            else if(retVal == 107 || retVal == 109) {
                if (!CommonFunctionalities.validateCard(timeout, transUI)) {
                    transUI.showError(timeout, Tcode.T_err_timeout,processPPFail);
                    return false;
                }
                contFallback++;
            }else{
                transUI.showError(timeout, Tcode.T_wait_timeout,processPPFail);
                return false;
            }
        }
        if (contFallback == FALLBACK){
            contFallback = 0;
            return fallback(cardInfo);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (retVal == 107 || retVal == 109){
            if(CardProcess(mode)){
                return true;
            }else{
                return false;
            }
        }

        return false;
    }

    private boolean fallback(CardInfo cardInfo){
        isFallBack = true;
        processPPFail.setFallBack(true);
        retVal = 0;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cardInfo = transUI.getCardUse(GERCARD_MSG_FALLBACK, timeout,  INMODE_MAG, transEname);
        if (cardInfo.isResultFalg()) {
            int type = cardInfo.getCardType();
            switch (type) {
                case CardManager.TYPE_MAG:
                    inputMode = ENTRY_MODE_MAG;
                    break;
                default:
                    transUI.showError(timeout, Tcode.T_not_allow,processPPFail);
                    return false;
            }
            para.setInputMode(inputMode);
            processPPFail.setInputMode(inputMode);

            if (inputMode == ENTRY_MODE_MAG) {
                isDebit = false;
                if(isMag1(cardInfo.getTrackNo())){
                    if (Server.cmd.equals(LT)){
                        lastInputMode = inputMode;
                        lastTrack = cardInfo.getTrackNo();
                    }
                    return true;
                }
            }
        } else {
            retVal = cardInfo.getErrno();
            if (retVal == 0) {
                transUI.showError(timeout, Tcode.T_user_cancel_input,processPPFail);
                return false;
            }else{
                transUI.showError(timeout, Tcode.T_wait_timeout,processPPFail);
                return false;
            }
        }
        return false;
    }

    public boolean isICC1() {
        String creditCard = "SI";
        para.setAmount(Amount);
        para.setOtherAmount(0);
        transUI.handling(timeout, Tcode.Status.handling);
        emv = new EmvTransaction(para, Type.VENTA);
        emv.setTraceNo(TraceNo);
        retVal = emv.start();
        Pan = emv.getCardNo();
        processPPFail.setPAN(Pan);//en caso de fallo

        if (retVal == 1 || retVal == 0) {
            //Credito
            if (PAYUtils.isNullWithTrim(emv.getPinBlock())) {
                isPinExist = true;
            }//Cancelo usuario
            else if (emv.getPinBlock().equals("CANCEL")) {
                isPinExist = false;
                transUI.showError(timeout, Tcode.T_user_cancel_pin_err,processPPFail);
                return false;
            } else if (emv.getPinBlock().equals("NULL")) {
                isPinExist = false;
                transUI.showError(timeout, Tcode.T_err_pin_null,processPPFail);
                return false;
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
                //prepareOnline();
                return true;
            } else {
                transUI.showError(timeout, retVal,processPPFail);
                return false;
            }
        } else {
            transUI.showError(timeout, retVal,processPPFail);
            return false;
        }
    }

    protected boolean isMag1(String[] tracks) {
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
                ExpDate = data2.substring(data2.indexOf("=") + 1,data2.indexOf("=") + 5);
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
            transUI.showError(timeout, retVal,processPPFail);
            return false;
        } else {
            if (msgLen == 0) {
                //retVal = Tcode.T_search_card_err;
                transUI.showError(timeout, Tcode.T_search_card_err,processPPFail);
                return false;
            } else {

                try {
                    if (!incardTable(data2.substring(0, data2.indexOf('=')), TransEName)) {
                        //retVal = Tcode.T_unsupport_card;
                        transUI.showError(timeout, Tcode.T_unsupport_card,processPPFail);
                        return false;
                    }
                }catch (IndexOutOfBoundsException e) {
                    //retVal = Tcode.T_read_app_data_err;
                    transUI.showError(timeout, Tcode.T_read_app_data_err,processPPFail);
                    return false;
                }

                int splitIndex = data2.indexOf("=");

                if (ISOUtil.stringToBoolean(rango.getPIN_SERVICE_CODE())) {
                    char isDebitChar = data2.charAt(splitIndex + 7);
                    if (isDebitChar == '0' || isDebitChar == '5' || isDebitChar == '6' || isDebitChar == '7') {
                        isDebit = true;
                    }
                }

                if (lastCmd.equals(LT) && Server.cmd.equals(PP) && lastInputMode == ENTRY_MODE_MAG){
                    return afterMAGJudge1(data1, data2, data3);
                }

                if (!ISOUtil.stringToBoolean(rango.getOMITIR_EMV())) {
                    if (data2.length() - splitIndex >= 5) {
                        try{
                            char iccChar = data2.charAt(splitIndex + 5);

                            if ((iccChar == '2' || iccChar == '6') && (!isFallBack)) {
                                //retVal = Tcode.T_ic_not_allow_swipe;
                                transUI.showError(timeout, Tcode.T_ic_not_allow_swipe,processPPFail);
                                return false;
                            } else {
                                if (afterMAGJudge1(data1, data2, data3))
                                    return true;
                            }
                        }catch (Exception e){
                            if (afterMAGJudge1(data1, data2, data3))
                                return true;
                        }
                    } else {
                        transUI.showError(timeout, Tcode.T_search_card_err,processPPFail);
                        return false;
                    }
                } else {
                    if (afterMAGJudge1(data1, data2, data3))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean afterMAGJudge1(String data1, String data2, String data3) {
        String cardNo = data2.substring(0, data2.indexOf('='));
        Pan = cardNo;
        Track1 = data1;
        Track2 = data2;
        Track3 = data3;
        processPPFail.setPAN(Pan);//en caso de fallo
        if (Track1 != null) {
            processPPFail.setCardHolderNameFail(Track1);//en caso de fallo
        }

        if (TransEName.equals(Trans.Type.PREVOUCHER)) {
            if (!ISOUtil.stringToBoolean(rango.getPRE_VOUCHER())) {
                transUI.showError(timeout, Tcode.T_not_allow,processPPFail);
                return false;
            }
        } else if (TransEName.equals(Trans.Type.CASH_OVER)) {
            if (!ISOUtil.stringToBoolean(rango.getCASH_OVER())) {
                transUI.showError(timeout, Tcode.T_not_allow,processPPFail);
                return false;
            }
        }

        if (!CommonFunctionalities.permitirTransGasolinera(Pan)){
            transUI.showError(timeout, Tcode.T_msg_err_gas,processPPFail);
            return false;
        }

        if (Server.cmd.equals(PP)){
            if ((retVal = CommonFunctionalities.last4card(timeout, TransEName, Pan, transUI, ISOUtil.stringToBoolean(rango.getULTIMOS_4()))) != 0) {
                return false;
            }

            if ((retVal = CommonFunctionalities.setCVV2(timeout, TransEName, transUI, ISOUtil.stringToBoolean(rango.getCVV2()))) != 0) {
                return false;
            }
        }

        CVV = CommonFunctionalities.getCvv2();

        return true;
        //prepareOnline();
    }

    protected boolean PBOCTrans1() {

        int code = 0;

        PBOCTransProperty property = new PBOCTransProperty();
        property.setTag9c(PBOCTag9c.sale);
        property.setTraceNO(Integer.parseInt(TraceNo));
        property.setFirstEC(false);
        property.setForceOnline(true);
        property.setAmounts(Amount);
        property.setOtherAmounts(0);
        property.setIcCard(false);

        transUI.handling(timeout * 2, Tcode.Status.process_trans);

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
            return false;
        }

        emvl2.SetAmount(Amount, 0);
        emvl2.setTypeCoin(typeCoin);//JM
        code = emvl2.start();
        processPPFail.setEmvL2Process(emvl2);
        Logger.debug("EmvL2Process return = " + code);
        if (code != 0) {
            if (code==7){
                retVal=Tcode.T_insert_card;
            }else{
                retVal=Tcode.T_err_detect_card_failed;
            }
            transUI.showError(timeout, retVal,processPPFail);
            return false;
        }

        Pan = emvl2.GetCardNo();
        processPPFail.setPAN(Pan);//en caso de fallo
        PanSeqNo = emvl2.GetPanSeqNo();
        Track2 = emvl2.GetTrack2data();
        ICCData = emvl2.GetEmvOnlineData();
        MasterControl.HOLDER_NAME = emvl2.getHolderName();
        Logger.error("PAN =" + Pan);

        if (!CommonFunctionalities.permitirTransGasolinera(Pan)){
            //retVal = Tcode.T_msg_err_gas;
            transUI.showError(timeout, Tcode.T_msg_err_gas,processPPFail);
            return false;
        }

        if (!incardTable(Pan, TransEName)) {
            //retVal = Tcode.T_unsupport_card;
            transUI.showError(timeout, Tcode.T_unsupport_card,processPPFail);
            return false;
        }

        if (TransEName.equals(Trans.Type.PREVOUCHER)) {
            if (!ISOUtil.stringToBoolean(rango.getPRE_VOUCHER())) {
                transUI.showError(timeout, Tcode.T_not_allow,processPPFail);
                return false;
            }
        } else if (TransEName.equals(Trans.Type.CASH_OVER)) {
            if (!ISOUtil.stringToBoolean(rango.getCASH_OVER())) {
                transUI.showError(timeout, Tcode.T_not_allow,processPPFail);
                return false;
            }
        }

        //Aca deben validarse los cvm

        if (emvl2.GetCVMType() == EmvL2CVM.L2_CVONLINE_PIN) {
            if (CommonFunctionalities.ctlPIN(Pan, timeout, Amount, transUI) != 0) {
                //retVal = Tcode.T_user_cancel_input;
                transUI.showError(timeout, Tcode.T_user_cancel_input,processPPFail);
                return false;
            }
            PIN = CommonFunctionalities.getPIN();
        }

        if (emvl2.GetCVMType() == EmvL2CVM.L2_CVOBTAIN_SIGNATURE) {
            MasterControl.CTL_SIGN = true;
        }


        if(!handlePBOCode1(PBOCode.PBOC_REQUEST_ONLINE))
            return false;

        return true;
    }

    /**
     * handle PBOC transaction
     *
     * @param code
     */
    private boolean handlePBOCode1(int code) {
        if (code != PBOCode.PBOC_REQUEST_ONLINE) {
            transUI.showError(timeout, code,processPPFail);
            return false;
        }
        if (inputMode != ENTRY_MODE_NFC)
            setICCDataCTL();

        //prepareOnline();
        return true;
    }

    protected boolean isHandle1() {
        if ((retVal = CommonFunctionalities.setPanManual(timeout, TransEName, transUI)) != 0) {
            return false;
        }

        Pan = CommonFunctionalities.getPan();
        processPPFail.setPAN(Pan);//en caso de fallo

        if (!CommonFunctionalities.permitirTransGasolinera(Pan)){
            //retVal = Tcode.T_msg_err_gas;
            transUI.showError(timeout, Tcode.T_msg_err_gas,processPPFail);
            return false;
        }

        if (!incardTable(Pan, TransEName)) {
            //retVal = Tcode.T_unsupport_card;
            transUI.showError(timeout, Tcode.T_unsupport_card,processPPFail);
            return false;
        }

        if (TransEName.equals(Trans.Type.PREVOUCHER)) {
            if (!ISOUtil.stringToBoolean(rango.getPRE_VOUCHER())) {
                transUI.showError(timeout, Tcode.T_not_allow,processPPFail);
                return false;
            }
        } else if (TransEName.equals(Trans.Type.CASH_OVER)) {
            if (!ISOUtil.stringToBoolean(rango.getCASH_OVER())) {
                transUI.showError(timeout, Tcode.T_not_allow,processPPFail);
                return false;
            }
        }

        if (!ISOUtil.stringToBoolean(rango.getMANUAL())) {
            //retVal = Tcode.T_err_not_allow;
            transUI.showError(timeout, Tcode.T_err_not_allow,processPPFail);
            return false;
        }

        if ((retVal = CommonFunctionalities.setFechaExp(timeout, TransEName, transUI, ISOUtil.stringToBoolean(rango.getFECHA_EXP()))) != 0) {
            return false;
        }

        ExpDate = CommonFunctionalities.getExpDate();

        if ((retVal = CommonFunctionalities.setCVV2(timeout, TransEName, transUI, ISOUtil.stringToBoolean(rango.getCVV2()))) != 0) {
            return false;
        }

        CVV = CommonFunctionalities.getCvv2();

        return true;
        //prepareOnline();
    }

    protected boolean requestPin1() {

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
                        transUI.showError(timeout, info.getErrno(),processPPFail);
                        return false;
                    }
                } else {
                    isPinExist = false;
                    retVal = Tcode.T_user_cancel_input;
                    transUI.showError(timeout, retVal,processPPFail);
                    return false;
                }
            }
        }
        return true;
    }

    protected void msgAprob(int code, boolean checkAuthCode){
        if (typeCoin != null) {
            switch (typeCoin) {
                case DOLAR:
                    if (checkAuthCode) {
                        String authCode = iso8583.getfield(38);
                        if (authCode != null) {
                            transUI.handlingInfo(timeout,code,"\nAPROBADA #" + authCode);
                            //transUI.trannSuccess(timeout, code, "APROBADA #" + authCode);
                        } else
                            transUI.handlingInfo(timeout, code, "\nAPROBADA #000000");
                    }else{
                        transUI.handlingInfo(timeout, code, "");
                    }
                    break;
            }
        } else {
            transUI.handlingInfo(timeout, code, "");
        }
        UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
    }

    protected void fild58(){
        if (!PAYUtils.isNullWithTrim(pp_request.getInvoiceNumber())){
            Field58 = "06" + pp_request.getInvoiceNumber();
            if (!PAYUtils.isNullWithTrim(pp_request.getPushSalesman())){
                Field58 +=  tagvendedor(pp_request.getPushSalesman().substring(0,2)) + pp_request.getPushSalesman().substring(2);
            }
        }else {
            if (!PAYUtils.isNullWithTrim(pp_request.getPushSalesman())){
                Field58 = tagvendedor(pp_request.getPushSalesman().substring(0,2)) + pp_request.getPushSalesman().substring(2);
            }
        }
        if (Field58 != null) {
            Field58 = ISOUtil.convertStringToHex(Field58);
        }
    }

    protected String tagvendedor(String tag){
        String ret = "";
        switch (tag){
            case "01":
                ret = "10";
                break;
            case "02":
                ret = "15";
                break;
            case "03":
                ret = "16";
                break;
            case "04":
                ret = "19";
                break;
            default:
                break;
        }

        return ret;
    }
}
