package com.newpos.libpay.trans;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.newpos.libemv.PBOCManager;
import com.datafast.inicializacion.configuracioncomercio.ChequeoIPs;
import com.datafast.menus.menus;
import com.datafast.pinpad.cmd.PP.PP_Request;
import com.datafast.pinpad.cmd.Tools.UtilPP;
import com.datafast.server.server_tcp.Server;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.contactless.EmvL2Process;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.global.TMConstants;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.helper.ssl.NetworkHelper;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.process.EmvTransaction;
import com.newpos.libpay.process.QpbocTransaction;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogReverse;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static com.android.newpos.pay.StartAppDATAFAST.host_confi;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tablaIp;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_REVERSE;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;


/**
 * 交易抽象类，定义所有交易类的父类
 *
 * @author zhouqiang
 */
public abstract class Trans {

    public static final int BASE12AMOUNT = 0;
    public final int RFDAMOUNT = 1;
    public final int ORGAMOUNT = 2;
    public static final int TIPAMOUNT = 3;
    public static final int IVAAMOUNT = 4;
    public static final int BASE0AMOUNT = 5;
    public static final int SERVICEAMOUNT = 6;
    public static final int AMOUNT = 7;
    public static final int CASHOVERAMOUNT = 8;

    public static final String AGREGADO = "Agregado";
    public static final String DESAGREGADO = "Desagregado";
    public static final String MANUAL = "Manual";
    public static final String PREGUNTA = "Pregunta";

    public static final String idLote = "01";

    /**
     * 上下文对象
     */
    protected Context context;

    /**
     * 8583组包解包
     */
    protected ISO8583 iso8583;

    /**
     * 网络操作对象
     */
    protected NetworkHelper netWork;

    /**
     * 交易记录的集合
     */
    protected TransLog transLog;

    protected TransLogReverse transLogRevese;

    /**
     * 配置文件操作实例
     */
    protected TMConfig cfg;

    /**
     * MODEL与VIEW层接口实例
     */
    protected TransUI transUI;

    /**
     * 等待页面超时时间
     */
    protected int timeout;

    /**
     * 返回值全局定义
     */
    protected int retVal;

    /**
     * EMV流程控制实例
     */
    protected EmvTransaction emv;

    /**
     * QPBOC流程控制实例
     */
    protected QpbocTransaction qpboc;

    /**
     * 交易相关参数集合
     */
    protected TransInputPara para;
    protected boolean isCodDinners = false;

    protected Trans() {
    }

    /**
     * 交易类型定义
     */
    public interface Type {
        String LOGON = "LOGON";
        String DOWNPARA = "DOWNPARA";
        String QUERY_EMV_CAPK = "QUERY_EMV_CAPK";
        String DOWNLOAD_EMV_CAPK = "DOWNLOAD_EMV_CAPK";
        String DOWNLOAD_EMV_CAPK_END = "DOWNLOAD_EMV_CAPK_END";
        String QUERY_EMV_PARAM = "QUERY_EMV_PARAM";
        String DOWNLOAD_EMV_PARAM = "DOWNLOAD_EMV_PARAM";
        String DOWNLOAD_EMV_PARAM_END = "DOWNLOAD_EMV_PARAM_END";
        String LOGOUT = "LOGOUT";
        String SALE = "SALE";
        String ENQUIRY = "ENQUIRY";
        String VOID = "VOID";
        String EC_ENQUIRY = "EC_ENQUIRY";
        String QUICKPASS = "QUICKPASS";
        String REFUND = "REFUND";
        String TRANSFER = "TRANSFER";
        String CREFORLOAD = "CREFORLOAD";
        String DEBFORLOAD = "DEBFORLOAD";
        String SETTLE = "SETTLE";
        String UPSEND = "UPSEND";
        String REVERSAL = "REVERSAL";
        String SENDSCRIPT = "SENDSCRIPT";
        String SCANSALE = "SCANSALE";
        String SCANVOID = "SCANVOID";
        String SCANREFUND = "SCANREFUND";
        String ECHO_TEST = "ECHO_TEST";
        String VENTA = "VENTA";
        String ANULACION = "ANULACION";
        String FALLBACK = "FALLBACK";
        String AUTO_SETTLE = "AUTO_SETTLE";
        String SALE_CTL = "SALE_CTL";
        String ELECTRONIC = "PAGO_CON_CODIGO";
        String DEFERRED = "DIFERIDO";
        String ELECTRONIC_DEFERRED = "PAGO_CON_CODIGO_DIFERIDO";
        String ALL = "ALL";
        String ECHO = "ECHO";
        String DIFERIDOS_PAYCLUB = "DIFERIDOS - PAYCLUB";
        String DIFERIDOS_BDP_WALLET = "DIFERIDOS - BDP WALLET";
        String PAYCLUB = "PAYCLUB";
        String PAYBLUE = "BDP WALLET";
        String PREAUTO = "PREAUTO";
        String AMPLIACION = "AMPLIACION";
        String CONFIRMACION = "CONFIRMACION";
        String VOID_PREAUTO = "ANULACION_PREAUTO";
        String PAGOS_VARIOS = "PAGOS_VARIOS";
        String REIMPRESION = "REIMPRESION";
        String PREVOUCHER = "PREVOUCHER";
        String PAGO_PRE_VOUCHER = "PAGO_PREVOUCHER";
        String CASH_OVER = "CASH_OVER";
    }

    //Archivo CTL.ADQ
    protected String CardType[] = {
            "PACIFICARD",
            "DINERS CLUB",
            "BANCO DEL PICHINCHA",
            "BANCO GUAYAQUIL",
            "DATAFAST",
            "SOLIDARIO",
            "MEDIANET",
            "BCO DEL AUSTRO",
            "29 OCTUBRE",
    };

    public static String deferredType[][] = {
            {"002", "CON INTERES"},
            {"003", "SIN INTERES"},
            {"007", "CON INT.ESPECIAL"},
            {"009", "SIN INT.ESPECIAL"},
            {"001", "CORRIENTE"},
            {"021", "PREFERENTE"},
            {"022", "PLUS"}
    };

    public interface TipoDiferido {
        String CON_INTERES = "CON INTERES";
        String SIN_INTERES = "SIN INTERES";
        String CON_INT_ESPECIAL = "CON INT.ESPECIAL";
        String SIN_INT_ESPECIAL = "SIN INT.ESPECIAL";
        String CORRIENTE = "CORRIENTE";
        String PREFERENTE = "PREFERENTE";
        String PLUS = "PLUS";
    }

    public static String moneda[] = new String[]{
            "Local",//0
            "Dolar",//1
            "Euro"//2
    };

    /** 报文域定义 */

    /**
     * 0 消息类型
     */
    protected String MsgID;

    /**
     * 2* 卡号
     */
    protected String Pan;
    protected String PanPE;

    /**
     * 3  预处理码
     */
    protected String ProcCode;

    /**
     * 4* 金额
     */
    protected long Amount;

    protected long AmountBase0;

    protected long AmountXX;

    protected long ServiceAmount;

    protected long TipAmount;

    protected long IvaAmount;

    protected long CashOverAmount;

    protected long montoFijo;

    protected String tipoMontoFijo;

    protected String CVV;

    protected String TypeDeferred;

    protected String TypeTransElectronic;

    protected String pagoVarioSeleccionado;

    protected String pagoVarioSeleccionadoNombre;

    protected String IdPreAutAmpl;

    protected String numCuotasDeferred;

    protected String CodOTT;

    protected String TokenElectronic;

    protected boolean multicomercio;

    protected String nameMultAcq;

    protected String idComercio;

    protected String issuerName;

    protected String labelName;

    protected boolean isField55;

    /**
     * 11域交易流水号
     */
    protected String TraceNo;

    /**
     * 12 hhmmss*
     */
    protected String LocalTime;

    /**
     * 13 MMDD*
     */
    protected String LocalDate;

    /**
     * 14 YYMM*
     */
    protected String ExpDate;

    /**
     * 15 MMDD*
     */
    protected String SettleDate;

    /**
     * 22*
     */
    protected String EntryMode;

    /**
     * 23*
     */
    protected String PanSeqNo;
    /**
     * 24*
     */
    protected String Nii;

    /**
     * 25
     */
    protected String SvrCode;

    /**
     * 26
     */
    protected String CaptureCode;

    /**
     * 32*
     */
    protected String AcquirerID;

    /**
     * 1磁道数据
     */
    protected String Track1;

    /**
     * 35
     */
    protected String Track2;

    /**
     * 36
     */
    protected String Track3;

    /**
     * 37*
     */
    protected String RRN;

    /**
     * 38*
     */
    protected String AuthCode;

    /**
     * 39
     */
    protected String RspCode;

    /**
     * 41
     */
    protected String TermID;

    /**
     * 42
     */
    protected String MerchID;

    /**
     * 44 *
     */
    protected String Field44;

    /**
     * 48 *
     */
    protected String Field48;

    /**
     * 49*
     */
    protected String CurrencyCode;

    /**
     * 52
     */
    protected String PIN;

    /**
     * 53
     */
    protected String SecurityInfo;

    /**
     * 54
     */
    protected String ExtAmount;

    protected String Field54;

    /**
     * 55*
     */
    protected byte[] ICCData;

    /**
     * 57
     */
    protected String Field57;

    /**
     * 58
     */
    protected String Field58;

    /**
     * 59
     */
    protected String Field59;

    /**
     * 60
     */
    protected String Field60;

    /**
     * 61
     */
    protected String Field61;

    /**
     * 62
     */
    protected String Field62;

    /**
     * 63
     */
    protected String Field63;

    /**
     * 交易中文名
     */
    protected String TransCName;

    protected String TypeTransVoid;
    /**
     * 交易英文名 主键 交易初始化设置
     */
    protected String TransEName;

    /**
     * 批次号 60_2
     */
    protected String BatchNo;

    /**
     * 标记此次交易流水号是否自增
     */
    protected boolean isTraceNoInc = false;

    /**
     * 是否允许IC卡降级为磁卡
     */
    protected boolean isFallBack;

    /**
     * 使用原交易的第3域和 60.1域
     */
    protected boolean isUseOrgVal = false;

    protected String F60_1;
    protected String F60_3;

    protected String keySecurity;

    /**
     * 22域服务点输入方式
     */
    public static final int ENTRY_MODE_HAND = 1;
    public static final int ENTRY_MODE_MAG = 2;
    public static final int ENTRY_MODE_ICC = 5;
    public static final int ENTRY_MODE_NFC = 7;
    public static final int ENTRY_MODE_QRC = 9;
    public static final int ENTRY_MODE_FALLBACK = 3;

    public static final String MODE_MAG = "90";
    public static final String MODE_ICC = "05";
    public static final String MODE1_FALLBACK = "80";
    public static final String MODE2_FALLBACK = "90";
    public static final String MODE_CTL = "07";
    //public static final String MODE_CTL_NO_PIN = "07";
    public static final String MODE_HANDLE = "01";


    protected EmvL2Process emvl2;   //非接交易

    /**
     * PBOC library manager
     */
    protected PBOCManager pbocManager;

    protected PP_Request pp_request;

    /***
     * Trans 构造
     * @param ctx
     * @param transEname
     */
    public Trans(Context ctx, String transEname) {
        try {
            this.context = ctx;
            this.TransEName = transEname;
            //int timeOutAccept = Integer.parseInt(host_confi.getTIEMPO_ESPERA_RESPUESTA()); //Temporal, falta campo en DB
            this.timeout = 30 * 1000;
            this.pp_request = new PP_Request();
            loadConfig();
            transLog = TransLog.getInstance(menus.idAcquirer);
            transLogRevese = TransLogReverse.getInstance(menus.idAcquirer + FILE_NAME_REVERSE);
            this.pbocManager = PBOCManager.getInstance();
            this.pbocManager.setDEBUG(true);
        }catch (NumberFormatException e) {
        }
    }

    public Trans(Context ctx, String transEname, String fileNameLog) {
        try {
            this.context = ctx;
            this.TransEName = transEname;
            //int timeOutAccept = Integer.parseInt(host_confi.getTIEMPO_ESPERA_RESPUESTA()); //Temporal, falta campo en DB
            this.timeout = 30 * 1000;
            loadConfig();
            transLogRevese = TransLogReverse.getInstance(menus.idAcquirer + fileNameLog);
        }catch (NumberFormatException e) {
        }
    }

    public Trans(Context ctx) {
        this.context = ctx;
        loadDateTrans();
    }

    /**
     * 加载初始设置
     */
    private void loadConfig() {

        updateMidTid();

        loadDateTrans();

        cfg.setPubCommun(true);  //Se pone en true para que siempre se intente primero por la IP 1
        loadConfigIP();
        String tpdu = getTpdu(TransEName);//cfg.getTpdu();
        String header = cfg.getHeader();
        setFixedDatas();

        iso8583 = new ISO8583(this.context, tpdu, header);
    }

    protected void updateMidTid(){
        switch (Server.cmd){
            case PP:
                pp_request.UnPackData(Server.dat);
                updateMidTid(pp_request);
                break;
            default:
                break;
        }
    }

    protected void loadDateTrans(){
        cfg = TMConfig.getInstance();

        TermID = ISOUtil.padright( cfg.getTermID() + "", 8, '0');
        MerchID = ISOUtil.padright(cfg.getMerchID() + "", 15, ' ');
        CurrencyCode = cfg.getCurrencyCode();
        BatchNo = ISOUtil.padleft("" + cfg.getBatchNo(), 6, '0');
        TraceNo = ISOUtil.padleft("" + cfg.getTraceNo(), 6, '0');
    }

    protected void loadConfigIP(){
        boolean isPub = cfg.getPubCommun();

        String ipKey, portKey;
        SharedPreferences preferences = context.getSharedPreferences("config_ip", Context.MODE_PRIVATE);
        if (isPub){  //Se valida la variable para seleccionar la ip
            tablaIp = ChequeoIPs.seleccioneIP(0);
            ipKey = "ip_primary";
            portKey = "port_primary";
        } else {
            tablaIp = ChequeoIPs.seleccioneIP(1);
            ipKey = "ip_secundary";
            portKey = "port_secundary";
        }

        String ip = preferences.getString(ipKey, "");
        int port = Integer.parseInt(preferences.getString(portKey, "0"));
        int timeoutRsp = 0;
        int timeoutCon = 0;

        //JM
        try {
            if (ip.equals("")){
                if(tablaIp.getIP_HOST() != null){
                    ip = tablaIp.getIP_HOST();
                }else {
                    ip = cfg.getIp();
                }
            }
            if (port == 0){
                if(tablaIp.getPUERTO() != null){
                    port = Integer.parseInt(tablaIp.getPUERTO());
                }else{
                    port = Integer.parseInt(cfg.getPort());
                }
            }

//            if (tablaIp.getIP_HOST() != null) {
//                ip = tablaIp.getIP_HOST();
//            } else {
//                ip = cfg.getIp();
//            }
//            if (tablaIp.getPUERTO() != null) {
//                port = Integer.parseInt(tablaIp.getPUERTO());
//            } else {
//                port = Integer.parseInt(cfg.getPort());
//            }

            if (host_confi.getTIEMPO_ESPERA_RESPUESTA() != null) {
                timeoutRsp = Integer.parseInt(host_confi.getTIEMPO_ESPERA_RESPUESTA()) * 1000;
            } else {
                timeoutRsp = cfg.getTimeout();
            }

            if (host_confi.getTIEMPO_ESPERA_CONEXION() != null){
                timeoutCon = Integer.parseInt(host_confi.getTIEMPO_ESPERA_CONEXION()) * 1000;
            }else {
                timeoutCon = cfg.getTimeout();
            }
        } catch (NumberFormatException e) {
            ip = cfg.getIp();
            port = Integer.parseInt(cfg.getPort());
            timeout = cfg.getTimeout();
        }
        netWork = new NetworkHelper(ip, port, timeoutRsp,timeoutCon, context);
    }

    /**
     * 设置消息类型及60域3个子域数据
     */
    protected void setFixedDatas() {
        Logger.debug("==Trans->setFixedDatas==");
        if (null == TransEName) {
            return;
        }
        Properties pro = PAYUtils.lodeConfig(context, TMConstants.TRANS);
        if (pro == null) {
            return;
        }
        String prop = pro.getProperty(TransEName);
        String[] propGroup = prop.split(",");
        if (!PAYUtils.isNullWithTrim(propGroup[0])) {
            MsgID = propGroup[0];
        } else {
            MsgID = null;
        }
        if (isUseOrgVal == false) {
            if (!PAYUtils.isNullWithTrim(propGroup[1])) {
                ProcCode = propGroup[1];
            } else {
                ProcCode = null;
            }
        }
        if (!PAYUtils.isNullWithTrim(propGroup[2])) {
            SvrCode = propGroup[2];
        } else {
            SvrCode = null;
        }
        if (isUseOrgVal == false) {
            if (!PAYUtils.isNullWithTrim(propGroup[3])) {
                F60_1 = propGroup[3];
            } else {
                F60_1 = null;
            }
        }
        if (!PAYUtils.isNullWithTrim(propGroup[4])) {
            F60_3 = propGroup[4];
        } else {
            F60_3 = null;
        }
        if (F60_1 != null && F60_3 != null) {
            Field60 = F60_1 + cfg.getBatchNo() + F60_3;
        }
        try {
            TransCName = new String(propGroup[5].getBytes("ISO-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error("Exception" + e.toString());
        }
    }

    /**
     * 获取流水号是否自增
     *
     * @return
     */
    public boolean isTraceNoInc() {
        return isTraceNoInc;
    }

    /**
     * 设置流水号是否自增
     *
     * @param isTraceNoInc
     */
    public void setTraceNoInc(boolean isTraceNoInc) {
        this.isTraceNoInc = isTraceNoInc;
    }

    /**
     * 追加60域内容
     *
     * @param f60
     */
    protected void appendField60(String f60) {
        Field60 = Field60 + f60;
    }

    /**
     * 连接
     *
     * @return
     */
    protected int connect() {
        return netWork.Connect();
    }

    /**
     * 发送
     *
     * @return
     */
    protected int send() {
        byte[] pack = iso8583.packetISO8583();
        if (pack == null) {
            return -1;
        }
        Logger.debug("交易:" + TransEName + "\n发送报文:" + ISOUtil.hexString(pack));
        return netWork.Send(pack);
    }

    /**
     * 接收
     *
     * @return
     */
    protected byte[] recive() {
        byte[] recive = null;
        try {
            recive = netWork.Recive(2048);
        } catch (IOException e) {
            return null;
        }
        if (recive != null) {
            Logger.debug("交易:" + TransEName + "\n接收报文:" + ISOUtil.hexString(recive));
        }
        return recive;
    }

    /**
     * 联机处理
     *
     * @return
     */
    protected int OnLineTrans() {

        int reintentos = Integer.parseInt(host_confi.getREINTENTOS());
        int showReintentos = 1;
        int rta;

        do {
            transUI.handling(timeout, Tcode.Status.connecting_center, "CONECTANDO IP1 (" + showReintentos + ")");
            showReintentos ++;
            rta = connect();
            if (rta == 0) {
                reintentos = 0;
            }
            reintentos --;
        }while (reintentos >0);

        showReintentos = 0;

        if (rta == -1){
            reintentos = Integer.parseInt(host_confi.getREINTENTOS());
            cfg = TMConfig.getInstance();
            cfg.setPubCommun(false);
            loadConfigIP();
            do {
                transUI.handling(timeout, Tcode.Status.connecting_center, "CONECTANDO IP2 (" + showReintentos + ")");
                showReintentos ++;
                rta = connect();
                if (rta == 0) {
                    reintentos = 0;
                }
                reintentos --;
            }while (reintentos >0);
        }

        if (rta == -1) {
            return Tcode.T_socket_err;
        }
        if(!isCodDinners) {
            transUI.handling(timeout + 10000, Tcode.Status.terminal_reversal);
        }else{
            transUI.handling(timeout, Tcode.Status.send_over_2_recv);
        }

        if (send() == -1) {
            return Tcode.T_send_err;
        }

        byte[] respData = recive();
        netWork.close();

        if (respData == null || respData.length <= 0) {
            return Tcode.T_receive_err;
        }

        int ret = iso8583.unPacketISO8583(respData);

        if (ret == 0) {
            if (isTraceNoInc) {
                cfg.incTraceNo().save();
            }
        }

        return ret;
    }

    /**
     * 清除关键信息
     */
    protected void clearPan() {
        Pan = null;
        Track2 = null;
        Track3 = null;
        rango.clearRango();
        System.gc();//显示调用清除内存
    }

    public static String getTpdu(String trans) {
        StringBuilder tmp_TPDU = new StringBuilder();
        String nii;
        String tpdu = "";

        switch (trans) {
            case Trans.Type.ECHO_TEST:
                if (host_confi.getNII_ECHO_TEST() != null)
                    nii = ISOUtil.padleft(host_confi.getNII_ECHO_TEST() + "", 4, '0');
                else
                    nii = "0000";
                break;
            case Trans.Type.SETTLE:
                if (host_confi.getNII_CIERRE() != null)
                    nii = ISOUtil.padleft(host_confi.getNII_CIERRE() + "", 4, '0');
                else
                    nii = "0000";
                break;
            case Trans.Type.PAGOS_VARIOS:
                if (host_confi.getNII_PAGOS_VARIOS() != null)
                    nii = ISOUtil.padleft(host_confi.getNII_PAGOS_VARIOS() + "", 4, '0');
                else
                    nii = "0000";
                break;
            default:
                if (host_confi.getNII_TRANSACCIONES() != null)
                    nii = ISOUtil.padleft(host_confi.getNII_TRANSACCIONES() + "", 4, '0');
                else
                    nii = "0000";
                break;
        }

        //JM
        tmp_TPDU.append("60");
        tmp_TPDU.append(nii);
        tmp_TPDU.append("0000");

        tpdu = tmp_TPDU.toString();
        return tpdu;
    }

    protected int updateMidTid(PP_Request pp_request) {
        if(!UtilPP.checkTidMid(pp_request.getTID(), pp_request.getMID(), context)){
            retVal = Tcode.T_mid_tid_invalid;
            //transUI.showError(timeout, retVal);
            return retVal;
        }
        UtilPP.UpdateTidMidFromCash(pp_request.getTID(), pp_request.getMID(), context);

        return retVal;
    }
}
