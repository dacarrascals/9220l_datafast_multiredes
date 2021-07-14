package com.newpos.libpay.process;

import android.os.SystemClock;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.newpos.libpay.Logger;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.SDKException;
import com.pos.device.emv.CoreParam;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVCallback;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.emv.TerminalMckConfigure;
//import com.pos.device.ped.RsaPinKey;
import com.pos.device.ped.RsaPinKey;
import com.pos.device.picc.EmvContactlessCard;
import com.pos.device.qpboc.QPbocHandler;
import com.pos.device.qpboc.QPbocParameters;


/**
 * Created by zhouqiang on 2016/11/21.
 * QPBOC流程控制
 * @author zhouqiang
 */

public class QpbocTransaction {
    private EmvContactlessCard emvContactlessCard = null ;
    private IEMVHandler emvHandler = null;
    private QPbocHandler qPbocHandler = null ;

    private TransUI transUI ;
    private TransInputPara para ;

    private long q_amount ;
    private long q_otheramount ;

    private String EC_AMOUNT ;

    private int timeout ;

    public QpbocTransaction(TransInputPara p){
        para = p ;
        transUI = p.getTransUI() ;
        Logger.debug("type = "+para.getTransType());
        Logger.debug("amount = "+para.isNeedAmount());
        Logger.debug("online = "+para.isNeedOnline());
        Logger.debug("pass = "+para.isNeedPass());
        Logger.debug("eccash = "+para.isECTrans());
        Logger.debug("print = "+para.isNeedPrint());
        if(para.isNeedAmount()){
            q_amount = para.getAmount();
            q_otheramount = para.getOtherAmount();
        }
        emvHandler = EMVHandler.getInstance();
        qPbocHandler = QPbocHandler.getInstance();
        try {
            emvContactlessCard = EmvContactlessCard.connect() ;
        } catch (SDKException e) {
            Logger.error("Exception" + e.toString());
        }
    }

    public int start(){
        timeout = 60 * 1000 ;
        initEmvKernel();
        if(para.isECTrans()){
            emvHandler.pbocECenable(true);
            Logger.debug("set pboc EC enable true");
        }else {
            emvHandler.pbocECenable(false);
            Logger.debug("set pboc EC enable false");
        }
        emvHandler = EMVHandler.getInstance();
        emvHandler.setEMVInitCallback(emvInitListener);
        emvHandler.setApduExchangeCallback(apduExchangeListener);
        emvHandler.setDataElement(new byte[] { (byte) 0x9c }, new byte[] { 0x00 });
        byte online ;
        if(para.isNeedOnline()){
            online = (byte) 0x80 ;
        }else {
            online = (byte) 0x00 ;
        }
        byte[] transactionProperty = {(byte) 0x26 , online , 0 , (byte) 0x80} ;//走QPBOC流程
        QPbocParameters parameters = new QPbocParameters() ;
        parameters.setStatusCheckSupported((byte)1);
        parameters.setTransactionProperty(transactionProperty);
        int ret = qPbocHandler.setParameter(parameters);
        Logger.debug("qpboc.setParameter ret = " +ret);
        int transType  = 0 ;
        int amount  = Integer.valueOf(String.valueOf(para.getAmount())) ;
        Logger.debug("qPbocHandler.preTramsaction amount="+amount);
        ret = qPbocHandler.preTramsaction(Integer.valueOf(ISOUtil.padleft(2 + "", 6, '0')) , (byte)transType , amount) ;
        Logger.debug("qpboc.preTransaction ret = " +ret);
        if(para.isECTrans()){
            byte[] apdu = ISOUtil.hex2byte("80CA9F7900");
            byte[] recv = exeAPDU(apdu);
            EC_AMOUNT = fromApdu2Amount(ISOUtil.hexString(recv));
            Logger.debug("取电子现金余额（9F79）="+EC_AMOUNT);
        }
        ret = qPbocHandler.readData(0);
        Logger.debug("qpboc.readData ret = "+ret);
        if(ret!=0){
            return Tcode.T_qpboc_errno ;
        }
        return ret ;
    }

    private IEMVCallback.ApduExchangeListener apduExchangeListener = new IEMVCallback.ApduExchangeListener() {
        @Override
        public int apduExchange(byte[] sendData, int[] recvLen, byte[] recvData) {
            Logger.debug("==apduExchangeListener==");
            Logger.debug("sendData = " + ISOUtil.byte2hex(sendData));
            int[] status = new int[1];
            long start = SystemClock.uptimeMillis();
            while(true){
                if(SystemClock.uptimeMillis()-start>3*1000){
                    break;
                }
                try {
                    if(emvContactlessCard == null){
                        break;
                    }else {
                        status[0] = emvContactlessCard.getStatus() ;
                    }
                } catch (SDKException e) {
                    Logger.error("Exception" + e.toString());
                }
                if(status[0]== EmvContactlessCard.STATUS_EXCHANGE_APDU){
                    break;
                }
                try {
                    Thread.sleep(6);
                }catch (Exception e){
                    Logger.error("Exception" + e.toString());
                }
            }
            int len = 0 ;
            try {
                if(emvContactlessCard == null){
                    return -1 ;
                } else {
                    byte[] rawData = emvContactlessCard.transmit(sendData);
                    if (rawData != null) {
                        Logger.debug("rawData = " + ISOUtil.hexString(rawData));
                        len = rawData.length;
                        if (len < 2 || rawData[len - 2] != (byte) 0x90) {
                            return -1;
                        }
                        System.arraycopy(rawData, 0, recvData, 0, rawData.length);
                    }
                }
            } catch (SDKException e) {
                Logger.error("Exception" + e.toString());
            }
            if (len >= 0) {
                recvLen[0] = len;
                Logger.debug("Data received from card:" + ISOUtil.byte2hex(recvData,0,recvLen[0]));
                return 0;
            }
            return -1;
        }
    };

    /**
     * 返回卡号
     * 非接不能取5A标签
     * 从57标签获取卡号
     * @return
     */
    public String getCardNO(){
        byte[] temp = new byte[256] ;
        int len = PAYUtils.get_tlv_data_kernal(0x57 , temp);
        if(len < 7)	{//磁道信息没读到
            return null;
        }else {
            return ISOUtil.hexString(temp).split("D")[0] ;
        }
    }

    /**
     * 返回电子现金余额
     * @return
     */
    public String getEC_AMOUNT(){
        return EC_AMOUNT ;
    }

    private IEMVCallback.EMVInitListener emvInitListener = new IEMVCallback.EMVInitListener() {
        @Override
        public int candidateAppsSelection() {
            Logger.debug("==candidateAppsSelection==");
            return 0 ;
        }

        @Override
        public void multiLanguageSelection() {
            Logger.debug("==multiLanguageSelection==");
            byte[] tag = new byte[] { 0x5F, 0x2D };
            int ret = emvHandler.checkDataElement(tag);
            // 从内核读aid 0x9f06 设置是否支持联机PIN
            Logger.debug("==multiLanguageSelection==ret:"+ ret);
        }

        @Override
        public int getAmount(int[] transAmount, int[] cashBackAmount) {
            Logger.debug("==getAmount==");
            Logger.debug("====getAmount======"+q_amount);
            if (para.isNeedAmount()) {
                if(q_amount <= 0){
                    // 调用输入金额
                    Logger.debug("EMV>>需要输入金额且金额参数未0>>EMV流程执行获取金额的回调");
                    InputInfo info = transUI.getOutsideInput(timeout , InputManager.Mode.AMOUNT,"");
                    if(info.isResultFlag()){
                        q_amount = (int) (Double.parseDouble(info.getResult()) * 100);
                        Logger.debug("EMV>>用户输入的金额="+q_amount);
                    }else {
                        //TODO
                    }
                    q_otheramount = 0;
                }else {
                    transAmount[0] = Integer.valueOf(q_amount + "");
                    cashBackAmount[0] = Integer.valueOf(q_otheramount + "");
                }
            }
            return 0;
        }

        @Override
        public int getPin(int[] pinLen, byte[] cardPin) {
            Logger.debug("==getPin==");
            return 0;
        }

        @Override
        public int getOfflinePin(int i, RsaPinKey rsaPinKey, byte[] bytes, byte[] bytes1) {
            return 0;
        }

        @Override
        public int pinVerifyResult(int tryCount) {
            Logger.debug("==pinVerifyResult===");
            return 0;
        }

        @Override
        public int checkOnlinePIN() {
            Logger.debug("==checkOnlinePIN==");
            return 0;
        }

        /** 核对身份证证件 **/
        @Override
        public int checkCertificate() {
            Logger.debug("==checkCertificate==");
            return 0;
        }

        @Override
        public int onlineTransactionProcess(byte[] brspCode, byte[] bauthCode,
                                            int[] authCodeLen, byte[] bauthData,
                                            int[] authDataLen, byte[] script,
                                            int[] scriptLen, byte[] bonlineResult) {
            Logger.debug("==onlineTransactionProcess=");
            return 0;
        }

        @Override
        public int issuerReferralProcess() {
            Logger.debug("=issuerReferralProcess==");
            return 0;
        }

        @Override
        public int adviceProcess(int firstFlg) {
            Logger.debug("==adviceProcess==");
            return 0;
        }

        @Override
        public int checkRevocationCertificate(int caPublicKeyID, byte[] RID, byte[] destBuf) {
            Logger.debug("==checkRevocationCertificate===");
            return -1;
        }

        /**
         * 黑名单
         */
        @Override
        public int checkExceptionFile(int panLen, byte[] pan, int panSN) {
            Logger.debug("==checkExceptionFile==");
            return -1;
        }

        /**
         * 判断IC卡脱机的累计金额 超过就强制联机
         */
        @Override
        public int getTransactionLogAmount(int panLen, byte[] pan, int panSN) {
            Logger.debug("==getTransactionLogAmount===");
            return 0;
        }

        /*@Override
        public int getOfflinePin(int i, RsaPinKey rsaPinKey, byte[] bytes, byte[] bytes1) {
            Logger.debug("==getOfflinePin===");
            return 0;
        }*/
    };

    /**
     * 交互apdu
     * @param apdu
     * @return
     */
    private byte[] exeAPDU(byte[] apdu){
        byte[] rawData = null ;
        int recvlen = 0 ;
        int[] status = new int[1];
        long start = SystemClock.uptimeMillis();
        while(true){
            if(SystemClock.uptimeMillis()-start>3*1000){
                break;
            }
            try {
                status[0] = emvContactlessCard.getStatus() ;
            } catch (SDKException e) {
                Logger.error("Exception" + e.toString());
            }
            if(status[0]== EmvContactlessCard.STATUS_EXCHANGE_APDU){
                break;
            }
            try {
                Thread.sleep(6);
            }catch (Exception e){
                Logger.error("Exception" + e.toString());
            }
        }
        try {
            rawData = emvContactlessCard.transmit(apdu) ;
            if(rawData!=null){
                Logger.debug("rawData = "+ ISOUtil.hexString(rawData));
                recvlen = rawData.length ;
            }
        } catch (SDKException e) {
            Logger.error("Exception" + e.toString());
        }
        byte[] recv = new byte[32] ;
        if(rawData!=null){
            System.arraycopy(rawData,0,recv,0,recvlen);
        }else {
            recv = null ;
        }
        return recv ;
    }

    /**
     * 从apdu中格式化金额
     * @param hex
     * @return
     */
    private String fromApdu2Amount(String hex){
        int len = hex.length() ;
        if(len > 2 && hex.contains("9F79") && hex.contains("9000")){
            int offset = 4 ;
            int l = Integer.parseInt(hex.substring(offset , offset+2));
            return hex.substring(offset+2 , offset+2+l*2);
        }
        return null ;
    }

    /**
     * 初始化Kernel
     * @return
     */
    public boolean initEmvKernel() {
        emvHandler.initDataElement();
        emvHandler.setKernelType(EMVHandler.KERNEL_TYPE_PBOC);

        // 配置MCK,支持项默认为支持，不支持的请设置为-1
        TerminalMckConfigure configure = new TerminalMckConfigure();
        configure.setTerminalType(0x22);
        configure.setTerminalCapabilities(new byte[] { (byte) 0xE0,
                (byte) 0xF8, (byte) 0xC8 });
        configure.setAdditionalTerminalCapabilities(new byte[] { 0x60, 0x00,
                (byte) 0xF0, (byte) 0xA0, 0x01 });

        configure.setSupportCardInitiatedVoiceReferrals(false);
        configure.setSupportForcedAcceptanceCapability(false);
        if(para.isNeedOnline()){
            configure.setSupportForcedOnlineCapability(true);
        }else {
            configure.setSupportForcedOnlineCapability(false);
        }
        configure.setPosEntryMode(0x05);

        int ret = emvHandler.setMckConfigure(configure);
        if (ret != 0) {
            Logger.debug("setMckConfigure failed");
            return false;
        }
        CoreParam coreParam = new CoreParam();
        coreParam.setTerminalId("POS00001".getBytes());
        coreParam.setMerchantId("000000000000000".getBytes());
        coreParam.setMerchantCateCode(new byte[] { 0x00, 0x01 });
        coreParam.setMerchantNameLocLen(35);
        coreParam.setMerchantNameLoc("Band Card Test Center,Beijing,China".getBytes());
        coreParam.setTerminalCountryCode(new byte[] { 0x01, 0x56 });
        coreParam.setTransactionCurrencyCode(new byte[] { 0x01, 0x56 });
        coreParam.setReferCurrencyCode(new byte[] { 0x01, 0x56 });
        coreParam.setTransactionCurrencyExponent(0x02);
        coreParam.setReferCurrencyExponent(0x02);
        coreParam.setReferCurrencyCoefficient(1000);
        coreParam.setTransactionType(EMVHandler.EMVTransType.EMV_GOODS);

        ret = emvHandler.setCoreInitParameter(coreParam);
        if (ret != 0) {
            Logger.debug("setCoreInitParameter error");
            return false;
        }
        return true;
    }
}
