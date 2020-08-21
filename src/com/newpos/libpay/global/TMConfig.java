package com.newpos.libpay.global;

import android.content.Context;

import com.newpos.libpay.PaySdk;
import com.newpos.libpay.PaySdkException;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.config.DevConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by zhouqiang on 2017/4/29.
 *
 * @author zhouqiang
 * 全局参数管理
 */

public class TMConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private static String ConfigPath = "config.dat";
    private static TMConfig mInstance = null;

    /**
     * 本程序的所有文件的保存路径
     */
    private static String ROOT_FILE_PATH;

    public static String getRootFilePath() {
        return ROOT_FILE_PATH;
    }

    public static void setRootFilePath(String rootFilePath) {
        ROOT_FILE_PATH = rootFilePath;
    }

    /**
     * 是否开启sdk调试信息
     */
    private boolean isDebug;

    public boolean isDebug() {
        return isDebug;
    }

    public TMConfig setDebug(boolean debug) {
        isDebug = debug;
        return mInstance;
    }

    /**
     * 是否开启sdk联机功能
     */
    private boolean isOnline = false;

    public boolean isOnline() {
        return isOnline;
    }

    public TMConfig setOnline(boolean online) {
        isOnline = online;
        return mInstance;
    }

    /**
     * 银行logo列表索引
     */
    private int bankid;

    public int getBankid() {
        return bankid;
    }

    public TMConfig setBankid(int bankid) {
        if (bankid < TMConstants.BANKID.ASSETS.length && bankid >= 0) {
            this.bankid = bankid;
        } else {
            this.bankid = 0;
        }
        return mInstance;
    }

    /**
     * 标记当前sdk环境支持的支付规范，不同在于相关标准及交易算法
     * 1 ==== 应用交易规范基本按照银联规范
     * 2 ==== 应用交易规范基本按照中信规范
     */
    private int standard;

    public int getStandard() {
        return standard;
    }

    public TMConfig setStandard(int standard) {
        if (standard == 1 || standard == 2) {
            this.standard = standard;
        } else {
            this.standard = 1;
        }
        return mInstance;
    }

    /**
     * 联机后台台公网端口
     */
    private String ip;

    public String getIp() {
        return ip;
    }

    public TMConfig setIp(String ip) {
        this.ip = ip;
        return mInstance;
    }

    /**
     * 联机后台公网IP
     */
    private String ip2;

    public String getIP2() {
        return ip2;
    }

    public TMConfig setIp2(String s) {
        this.ip2 = s;
        return mInstance;
    }

    /**
     * 非接强制PBOC
     */
    private boolean forcePboc;

    public boolean isForcePboc() {
        return forcePboc;
    }

    public TMConfig setForcePboc(boolean forcePboc) {
        this.forcePboc = forcePboc;
        return mInstance;
    }

    /**
     * 联机后内网IP
     */
    private String port;

    public String getPort() {
        return port;
    }

    public TMConfig setPort(String port) {
        this.port = port;
        return mInstance;
    }

    /**
     * 联机后台内网端口
     */
    private String port2;

    public String getPort2() {
        return port2;
    }

    public TMConfig setPort2(String s) {
        this.port2 = s;
        return mInstance;
    }

    /**
     * 联机超时时间（单位:秒）
     */
    private int timeout;

    public int getTimeout() {
        return timeout;
    }

    public TMConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return mInstance;
    }

    /**
     * 是否开启公网通讯
     */
    private boolean isPubCommun = false;

    public TMConfig setPubCommun(boolean is) {
        this.isPubCommun = is;
        return mInstance;
    }

    public boolean getPubCommun() {
        return isPubCommun;
    }

    /**
     * 等待用户交互超时时间(单位 :秒)
     */
    private int waitUserTime;

    public int getWaitUserTime() {
        return waitUserTime;
    }

    public TMConfig setWaitUserTime(int waitUserTime) {
        this.waitUserTime = waitUserTime;
        return mInstance;
    }

    /**
     * 扫码时开启闪光
     */
    private boolean scanTorchOn;

    public boolean isScanTorchOn() {
        return scanTorchOn;
    }

    public TMConfig setScanTorchOn(boolean scanTorchOn) {
        this.scanTorchOn = scanTorchOn;
        return mInstance;
    }

    /**
     * 扫码时扫到结果打开蜂鸣提示
     */
    private boolean scanBeeper;

    public boolean isScanBeeper() {
        return scanBeeper;
    }

    public TMConfig setScanBeeper(boolean scanBeeper) {
        this.scanBeeper = scanBeeper;
        return mInstance;
    }

    /**
     * 扫码时使用后置
     */
    private boolean scanBack;

    public boolean isScanBack() {
        return scanBack;
    }

    public TMConfig setScanBack(boolean scanFront) {
        this.scanBack = scanFront;
        return mInstance;
    }

    /**
     * 消费撤销密码开关
     */
    private boolean revocationPassWSwitch;

    public boolean getRevocationPassSwitch() {
        return revocationPassWSwitch;
    }

    public TMConfig setRevocationPassWSwitch(boolean is) {
        this.revocationPassWSwitch = is;
        return mInstance;
    }

    /**
     * 消费撤销用卡开关
     */
    private boolean revocationCardSwitch;

    public TMConfig setRevocationCardSwitch(boolean is) {
        this.revocationCardSwitch = is;
        return mInstance;
    }

    public boolean getRevocationCardSwitch() {
        return revocationCardSwitch;
    }

    /**
     * 预授权撤销密码开关
     */
    private boolean preauthVoidPassSwitch;

    public boolean isPreauthVoidPassSwitch() {
        return preauthVoidPassSwitch;
    }

    public TMConfig setPreauthVoidPassSwitch(boolean preauthVoidPassSwitch) {
        this.preauthVoidPassSwitch = preauthVoidPassSwitch;
        return mInstance;
    }

    /**
     * 预授权完成密码开关
     */
    private boolean preauthCompletePassSwitch;

    public boolean isPreauthCompletePassSwitch() {
        return preauthCompletePassSwitch;
    }

    public TMConfig setPreauthCompletePassSwitch(boolean preauthCompletePassSwitch) {
        this.preauthCompletePassSwitch = preauthCompletePassSwitch;
        return mInstance;
    }

    /**
     * 预授权完成撤销用卡开关
     */
    private boolean preauthCompleteVoidCardSwitch;

    public boolean isPreauthCompleteVoidCardSwitch() {
        return preauthCompleteVoidCardSwitch;
    }

    public TMConfig setPreauthCompleteVoidCardSwitch(boolean preauthCompleteVoidCardSwitch) {
        this.preauthCompleteVoidCardSwitch = preauthCompleteVoidCardSwitch;
        return mInstance;
    }

    /**
     * 主管密码
     */
    private String masterPass;

    public String getMasterPass() {
        return masterPass;
    }

    public TMConfig setMasterPass(String pass) {
        this.masterPass = pass;
        return mInstance;
    }

    /**
     * 维护密码
     */
    private String maintainPass;

    public String getMaintainPass() {
        return maintainPass;
    }

    public TMConfig setMaintainPass(String pass) {
        this.maintainPass = pass;
        return mInstance;
    }

    /**
     * 主密钥索引号
     */
    private int masterKeyIndex;

    public int getMasterKeyIndex() {
        return masterKeyIndex;
    }

    public TMConfig setMasterKeyIndex(int masterKeyIndex) {
        this.masterKeyIndex = masterKeyIndex;
        return mInstance;
    }

    /**
     * 是否校验IC卡刷卡
     */
    private boolean isCheckICC;

    public boolean isCheckICC() {
        return isCheckICC;
    }

    public TMConfig setCheckICC(boolean is) {
        this.isCheckICC = is;
        return mInstance;
    }

    /**
     * 报文TPDU
     */
    private String tpdu;

    public String getTpdu() {
        return tpdu;
    }

    public TMConfig setTpdu(String tpdu) {
        this.tpdu = tpdu;
        return mInstance;
    }

    /**
     * 报文头
     */
    private String header;

    public String getHeader() {
        return header;
    }

    public TMConfig setHeader(String header) {
        this.header = header;
        return mInstance;
    }

    /**
     * 终端ID
     */
    private String TermID;

    public String getTermID() {
        return TermID;
    }

    public TMConfig setTermID(String termID) {
        TermID = termID;
        return mInstance;
    }

    /**
     * 商户ID
     */
    private String MerchID;

    public String getMerchID() {
        return MerchID;
    }

    public TMConfig setMerchID(String merchID) {
        MerchID = merchID;
        return mInstance;
    }

    /**
     * 分支号
     */
    private int BatchNo;

    public String getBatchNo() {
        return ISOUtil.padleft(BatchNo + "", 6, '0');
    }

    public TMConfig setBatchNo(int batchNo) {
        BatchNo = batchNo;
        if (this.BatchNo == 999999) {
            this.BatchNo = 0;
        }
        this.BatchNo += 1;
        return mInstance ;
    }

    public TMConfig setBatchNopc(int batchNo) {
        BatchNo = batchNo;
        if (this.BatchNo == 999999) {
            this.BatchNo = 0;
        }
        return mInstance ;
    }

    /**
     * 流水号
     */
    private int TraceNo;

    public String getTraceNo() {
        return ISOUtil.padleft(TraceNo + "", 6, '0');
    }

    public TMConfig setTraceNo(int traceNo) {
        TraceNo = traceNo;
        return mInstance;
    }

    /**
     * 操作员编号
     */
    private int oprNo;

    public int getOprNo() {
        return oprNo;
    }

    public TMConfig setOprNo(int oprNo) {
        this.oprNo = oprNo;
        return mInstance;
    }

    /**
     * 1-3联 1 商户 2.持卡人(没签名) 3.银行
     */
    private int PrinterTickNumber;

    public int getPrinterTickNumber() {
        return PrinterTickNumber;
    }

    public TMConfig setPrinterTickNumber(int n) {
        this.PrinterTickNumber = n;
        return mInstance;
    }

    /**
     * 商户名称
     */
    private String MerchName;

    public String getMerchName() {
        return MerchName;
    }

    public TMConfig setMerchName(String merchName) {
        MerchName = merchName;
        return mInstance;
    }

    /**
     * 1中文 2英文 3中英文
     */
    private int printEn;

    public int getPrintEn() {
        return printEn;
    }

    public TMConfig setPrintEn(int lang) {
        this.printEn = lang;
        return mInstance;
    }

    /**
     * 磁道是否加密
     */
    private boolean isTrackEncrypt;

    public boolean isTrackEncrypt() {
        return isTrackEncrypt;
    }

    public TMConfig setTrackEncrypt(boolean is) {
        this.isTrackEncrypt = is;
        return mInstance;
    }

    /**
     * 是否是单倍长密钥
     */
    private boolean isSingleKey;

    public boolean isSingleKey() {
        return isSingleKey;
    }

    public TMConfig setSingleKey(boolean is) {
        this.isSingleKey = is;
        return mInstance;
    }

    /**
     * 货币代码
     */
    private String CurrencyCode;

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public TMConfig setCurrencyCode(String cur) {
        this.CurrencyCode = cur;
        return mInstance;
    }

    /**
     * 商行代码
     */
    private String firmCode;

    public String getFirmCode() {
        return firmCode;
    }

    public TMConfig setFirmCode(String firmCode) {
        this.firmCode = firmCode;
        return mInstance;
    }

    /**
     * 冲正重发次数
     */
    private int reversalCount;

    public int getReversalCount() {
        return reversalCount;
    }

    public TMConfig setReversalCount(int reversalCount) {
        this.reversalCount = reversalCount;
        return mInstance;
    }

    /*------------------- CONFIG COMERCIO-----------------*/

    /**
     * habSSL
     */
    private boolean isHabSSL;

    public boolean isHabSSL() {
        return isHabSSL;
    }

    public TMConfig setHabSSL(boolean is) {
        this.isHabSSL = is;
        return mInstance;
    }

    /**
     * habTLS1_0
     */
    private boolean isHabTLS1_0;

    public boolean isHabTLS1_0() {
        return isHabTLS1_0;
    }

    public TMConfig setHabTLS1_0(boolean is) {
        this.isHabTLS1_0 = is;
        return mInstance;
    }

    /**
     * habTLS1_1
     */
    private boolean isHabTLS1_1;

    public boolean isHabTLS1_1() {
        return isHabTLS1_1;
    }

    public TMConfig setHabTLS1_1(boolean is) {
        this.isHabTLS1_1 = is;
        return mInstance;
    }

    /**
     * habTLS1_2
     */
    private boolean isHabTLS1_2;

    public boolean isHabTLS1_2() {
        return isHabTLS1_2;
    }

    public TMConfig setHabTLS1_2(boolean is) {
        this.isHabTLS1_2 = is;
        return mInstance;
    }

    /**
     * habEFTSEC
     */
    private boolean isHabEFTSEC;

    public boolean isHabEFTSEC() {
        return isHabEFTSEC;
    }

    public TMConfig setHabEFTSEC(boolean is) {
        this.isHabEFTSEC = is;
        return mInstance;
    }

    /**
     * certSslName
     */
    private String certSslName;

    public String getcertSslName() {
        return certSslName;
    }

    public TMConfig setcertSslName(String certSslName) {
        this.certSslName = certSslName;
        return mInstance;
    }

    /**
     * banderaMessageTel
     */
    private boolean banderaMessageTel;

    public boolean isBanderaMessageTel() {
        return banderaMessageTel;
    }

    public TMConfig setbanderaMessageTel(boolean banderaMessageTel) {
        this.banderaMessageTel = banderaMessageTel;
        return mInstance;
    }

    /**
     * banderaMessageFirma
     */
    private boolean banderaMessageFirma;

    public boolean isBanderaMessageFirma() {
        return banderaMessageFirma;
    }

    public TMConfig setbanderaMessageFirma(boolean banderaMessageFirma) {
        this.banderaMessageFirma = banderaMessageFirma;
        return mInstance;
    }

    /**
     * banderaMessageDoc
     */
    private boolean banderaMessageDoc;

    public boolean isBanderaMessageDoc() {
        return banderaMessageDoc;
    }

    public TMConfig setbanderaMessageDoc(boolean banderaMessageDoc) {
        this.banderaMessageDoc = banderaMessageDoc;
        return mInstance;
    }

    /**
     * banderaMessageEmi
     */
    private boolean banderaMessageEmi;

    public boolean isBanderaMessageEmi() {
        return banderaMessageEmi;
    }

    public TMConfig setbanderaMessageEmi(boolean banderaMessageEmi) {
        this.banderaMessageEmi = banderaMessageEmi;
        return mInstance;
    }

    /**
     * activar_validacion_disp
     */
    private boolean activar_validacion_disp;

    public boolean isActivarValidacionDisp() {
        return activar_validacion_disp;
    }

    public TMConfig setActivarValidacionDisp(boolean activar_validacion_disp) {
        this.activar_validacion_disp = activar_validacion_disp;
        return mInstance;
    }

    /**
     * KIN_Cifrado
     */
    private String KIN_Cifrado;

    public String isKINCifrado() {
        return KIN_Cifrado;
    }

    public TMConfig setKINCifrado(String KIN_Cifrado) {
        this.KIN_Cifrado = KIN_Cifrado;
        return mInstance;
    }

    /**
     * ClaveMenuWifi
     */
    private String ClaveMenuWifi;

    public String getClaveMenuWifi() {
        return ClaveMenuWifi;
    }

    public TMConfig setClaveMenuWifi(String ClaveMenuWifi) {
        this.ClaveMenuWifi = ClaveMenuWifi;
        return mInstance;
    }

    /**
     * Intentos de conexion
     */
    private int IntentosConex;

    public int getIntentosConex() {
        return IntentosConex;
    }

    public TMConfig setIntentosConex(int intentosConex) {
        IntentosConex = intentosConex;
        return mInstance;
    }

    private String Nii;

    public String getNii() {
        return Nii;
    }

    public TMConfig setNii(String nii) {
        Nii = nii;
        return mInstance;
    }

    private String TransMaxGasolinera;

    public String getTransMaxGasolinera() {
        return TransMaxGasolinera;
    }

    public TMConfig setTransMaxGasolinera(String transmaxgasolinera) {
        TransMaxGasolinera = transmaxgasolinera;
        return mInstance;
    }

    private String CID;

    public String getCID(){
        return CID;
    }

    public TMConfig setCID(String cid){
        CID = cid;
        return mInstance;
    }

    /*-----------------------------------------------------------*/
    private TMConfig() {
        try {
            loadFile(PaySdk.getInstance().getContext(),
                    PaySdk.getInstance().getParaFilepath());
        } catch (PaySdkException pse) {
            System.err.println("TMConfig->" + pse.toString());
        }
    }

    public static TMConfig getInstance() {
        String fullPath = getRootFilePath() + ConfigPath;
        if (mInstance == null) {
            try {
                mInstance = (TMConfig) PAYUtils.file2Object(fullPath);
            } catch (FileNotFoundException e) {
                System.err.println("getInstance->" + e.toString());
            } catch (IOException e) {
                System.err.println("getInstance->" + e.toString());
            } catch (ClassNotFoundException e) {
                System.err.println("getInstance->" + e.toString());
            }
            if (mInstance == null) {
                mInstance = new TMConfig();
            }
        }
        return mInstance;
    }

    private void loadFile(Context context, String path) {
        System.out.println("loadFile->path:" + path);
        String T = "1";
        Properties properties = PAYUtils.lodeConfig(context, TMConstants.DEFAULTCONFIG);
        if (properties != null) {
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                System.out.println("loadFile->name:" + name);
                if (!PAYUtils.isNullWithTrim(name)) {
                    int index = Integer.parseInt(name.substring(name.length() - 2, name.length()));
                    String prop = properties.getProperty(name);
                    try {
                        switch (index - 1) {
                            case 0:
                                setIp(prop);
                                break;
                            case 1:
                                setPort(prop);
                                break;
                            case 2:
                                setIp2(prop);
                                break;
                            case 3:
                                setPort2(prop);
                                break;
                            case 4:
                                setTimeout(Integer.parseInt(prop) * 1000);
                                break;
                            case 5:
                                setPubCommun(prop.equals(T) ? true : false);
                                break;
                            case 6:
                                setWaitUserTime(Integer.parseInt(prop));
                                break;
                            case 7:
                                setRevocationPassWSwitch(prop.equals(T) ? true : false);
                                break;
                            case 8:
                                setRevocationCardSwitch(prop.equals(T) ? true : false);
                                break;
                            case 9:
                                setPreauthVoidPassSwitch(prop.equals(T) ? true : false);
                                break;
                            case 10:
                                setPreauthCompletePassSwitch(prop.equals(T) ? true : false);
                                break;
                            case 11:
                                setPreauthCompleteVoidCardSwitch(prop.equals(T) ? true : false);
                                break;
                            case 12:
                                setMasterPass(prop);
                                break;
                            case 13:
                                setMasterKeyIndex(Integer.parseInt(prop));
                                break;
                            case 14:
                                setCheckICC(prop.equals(T) ? true : false);
                                break;
                            case 15:
                                setTpdu(prop);
                                break;
                            case 16:
                                setHeader(prop);
                                break;
                            case 17:
                                String SN = DevConfig.getSN();
                                SN = SN.substring(4, SN.length());
                                setTermID("NG" + SN);
                                //setTermID(prop);
                                break;
                            case 18:
                                setMerchID(prop);
                                break;
                            case 19:
                                setBatchNo(Integer.parseInt(prop));
                                break;
                            case 20:
                                setTraceNo(Integer.parseInt(prop));
                                break;
                            case 21:
                                setOprNo(Integer.parseInt(prop));
                                break;
                            case 22:
                                setPrinterTickNumber(Integer.parseInt(prop));
                                break;
                            case 23:
                                setMerchName(prop);
                                break;
                            case 24:
                                setPrintEn(Integer.parseInt(prop));
                                break;
                            case 25:
                                setTrackEncrypt(prop.equals(T) ? true : false);
                                break;
                            case 26:
                                setSingleKey(prop.equals(T) ? true : false);
                                break;
                            case 27:
                                setCurrencyCode(prop);
                                break;
                            case 28:
                                setFirmCode(prop);
                                break;
                            case 29:
                                setReversalCount(Integer.parseInt(prop));
                                break;
                            case 30:
                                setMaintainPass(prop);
                                break;
                            case 31:
                                setScanTorchOn(prop.equals(T) ? true : false);
                                break;
                            case 32:
                                setScanBeeper(prop.equals(T) ? true : false);
                                break;
                            case 33:
                                setScanBack(prop.equals(T) ? true : false);
                                break;
                            case 34:
                                setDebug(prop.equals(T) ? true : false);
                                break;
                            case 35:
                                setOnline(prop.equals(T) ? true : false);
                                break;
                            case 36:
                                setBankid(Integer.parseInt(prop));
                                break;
                            case 37:
                                setStandard(Integer.parseInt(prop));
                                break;
                            case 38:
                                setForcePboc(prop.equals(T) ? true : false);
                                break;
                            case 39:
                                setHabSSL(prop.equals(T) ? true : false);
                                break;
                            case 40:
                                setHabTLS1_0(prop.equals(T) ? true : false);
                                break;
                            case 41:
                                setHabTLS1_1(prop.equals(T) ? true : false);
                                break;
                            case 42:
                                setHabTLS1_2(prop.equals(T) ? true : false);
                                break;
                            case 43:
                                setHabEFTSEC(prop.equals(T) ? true : false);
                                break;
                            case 44:
                                setcertSslName(prop);
                                break;
                            case 45:
                                setbanderaMessageTel(prop.equals(T) ? true : false);
                                break;
                            case 46:
                                setbanderaMessageFirma(prop.equals(T) ? true : false);
                                break;
                            case 47:
                                setbanderaMessageDoc(prop.equals(T) ? true : false);
                                break;
                            case 48:
                                setbanderaMessageEmi(prop.equals(T) ? true : false);
                                break;
                            case 49:
                                setActivarValidacionDisp(prop.equals(T) ? true : false);
                                break;
                            case 50:
                                setKINCifrado(prop);
                                break;
                            case 51:
                                setClaveMenuWifi(prop);
                                break;
                            case 52:
                                setIntentosConex(Integer.parseInt(prop));
                                break;
                            case 53:
                                setNii(prop);
                                break;
                            case 54:
                                setTransMaxGasolinera(prop);
                                break;
                            case 55:
                                setCID(prop);
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println("loadFile->" + e.toString());
                    }
                }
            }
        }
        save();
    }

    /**
     * 流水号自增设置，用于交易过程中流水号控制
     *
     * @return
     */
    public TMConfig incTraceNo() {
        if (this.TraceNo == 999999) {
            this.TraceNo = 0;
        }
        this.TraceNo += 1;
        this.save();
        return mInstance;
    }

    /**
     * 保存当前配置
     */
    public boolean save() {
        String FullName = getRootFilePath() + ConfigPath;
        try {
            File file = new File(FullName);
            if (file.exists()) {
                file.delete();
            }
            PAYUtils.object2File(mInstance, FullName);
        } catch (FileNotFoundException e) {
            System.err.println("save->" + e.toString());
            return false;
        } catch (IOException e) {
            System.err.println("save->" + e.toString());
            return false;
        }
        return true;
    }

    public void activeDebugMode(boolean active){
        this.setDebug(active);
        this.save();
    }
}