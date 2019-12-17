package com.newpos.libpay.trans.translog;

import java.io.Serializable;

/**
 * 交易日志详情信息类
 *
 * @author zhouqiang
 */
public class TransLogData implements Serializable {

    private String nameTrade;
    private String phoneTrade;
    private String AID;
    private String AIDName;
    private String addressTrade;
    private int numCuotas;
    private String datePrint;
    private String numCell;
    private String MsgID;
    private boolean isTip;

    public boolean isTip() {
        return isTip;
    }

    public void setTip(boolean tip) {
        isTip = tip;
    }

    public String getMsgID() {
        return MsgID;
    }

    public void setMsgID(String msgID) {
        MsgID = msgID;
    }

    public String getNumCell() {
        return numCell;
    }

    public void setNumCell(String numCell) {
        this.numCell = numCell;
    }

    public String getDatePrint() {
        return datePrint;
    }

    public void setDatePrint(String datePrint) {
        this.datePrint = datePrint;
    }

    public int getNumCuotas() {
        return numCuotas;
    }

    public void setNumCuotas(int numCuotas) {
        this.numCuotas = numCuotas;
    }

    public String getAddressTrade() {
        return addressTrade;
    }

    public void setAddressTrade(String addressTrade) {
        this.addressTrade = addressTrade;
    }


    public String getNameTrade() {
        return nameTrade;
    }

    public void setNameTrade(String nameTrade) {
        this.nameTrade = nameTrade;
    }

    public String getPhoneTrade() {
        return phoneTrade;
    }

    public void setPhoneTrade(String phoneTrade) {
        this.phoneTrade = phoneTrade;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getAIDName() {
        return AIDName;
    }

    public void setAIDName(String AIDName) {
        this.AIDName = AIDName;
    }

    private String labelCard;

    public String getLabelCard() {
        return labelCard;
    }

    public void setLabelCard(String labelCard) {
        this.labelCard = labelCard;
    }

    public String getNameCard() {
        return nameCard;
    }

    public void setNameCard(String nameCard) {
        this.nameCard = nameCard;
    }

    private String nameCard;

    /**
     * 应用密文 此次交易是联机还是脱机
     */


    private String TypeCoin;

    public String getTypeCoin() {
        return TypeCoin;
    }

    public void setTypeCoin(String typeCoin) {
        TypeCoin = typeCoin;
    }


    private int AAC;

    public int getAAC() {
        return AAC;
    }

    public void setAAC(int AAC) {
        this.AAC = AAC;
    }

    /**
     * 标记此次用开方式是否是非接方式
     */
    private boolean isNFC;

    public boolean isNFC() {
        return isNFC;
    }

    public void setNFC(boolean isNFC) {
        this.isNFC = isNFC;
    }

    /**
     * 标记此次交易用卡方式是否是插卡
     */
    private boolean isICC;

    public boolean isICC() {
        return isICC;
    }

    public void setICC(boolean isICC) {
        this.isICC = isICC;
    }

    /**
     * 标记此次交易是否是扫码方式
     */
    private boolean isScan;

    public boolean isScan() {
        return isScan;
    }

    public void setScan(boolean scan) {
        isScan = scan;
    }

    private int RecState;

    /**
     * 初始状态为0，已上送成功1，已上送但是失败2
     **/

    public long getTipAmout() {
        return TipAmout;
    }

    public void setTipAmout(long tipAmout) {
        TipAmout = tipAmout;
    }

    /**
     * 小费
     **/
    private long TipAmout = 0;
    /**
     * 原交易流水号
     **/
    private String BatchNo;

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    /**
     * 标记如果是消费类交易，此交易是否已经撤销
     */
    private boolean isVoided;

    public boolean getIsVoided() {
        return isVoided;
    }

    public void setVoided(boolean isVoided) {
        this.isVoided = isVoided;
    }

    private boolean isTarjetaCierre;

    public boolean isTarjetaCierre() {
        return isTarjetaCierre;
    }

    public void setTarjetaCierre(boolean tarjetaCierre) {
        isTarjetaCierre = tarjetaCierre;
    }

    /**
     * 预授权交易是否已经完成，完成的交易不能再次完成
     */
    private boolean isPreComp;

    public boolean isPreComp() {
        return isPreComp;
    }

    public void setPreComp(boolean preComp) {
        isPreComp = preComp;
    }

    public int getRecState() {
        return RecState;
    }

    public void setRecState(int recState) {
        RecState = recState;
    }

    private String TypeTransVoid;

    public String getTypeTransVoid() {
        return TypeTransVoid;
    }

    public void setTypeTransVoid(String typeTransVoid) {
        TypeTransVoid = typeTransVoid;
    }

    /**
     * 交易英文名称
     * 详见 @{@link com.newpos.libpay.trans.Trans.Type}
     */
    private String TransEName;

    public String getEName() {
        return TransEName;
    }

    public void setEName(String eName) {
        TransEName = eName;
    }


    /**
     * 第二域卡号，2 加了*号的字串
     */
    private String Pan;

    public String getPan() {
        return Pan;
    }

    public void setPan(String pan) {
        Pan = pan;
    }


    /**
     * 第三域，2 预处理码
     */
    private String panNormal;

    public String getPanNormal() {
        return panNormal;
    }

    public void setPanNormal(String panNormal) {
        this.panNormal = panNormal;
    }


    /**
     * 第三域，3 预处理码
     */
    private String ProcCode;

    public String getProcCode() {
        return ProcCode;
    }

    public void setProcCode(String procCode) {
        ProcCode = procCode;
    }

    /**
     * 第四域，4 标记此次交易的金额
     */
    private Long Amount;

    public Long getAmount() {
        return Amount;
    }

    public void setAmount(Long amount) {
        Amount = amount;
    }

    /**
     * 第11域 , 交易流水号
     */
    private String TraceNo;

    public String getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(String traceNo) {
        TraceNo = traceNo;
    }

    /**
     * 第12域，交易时间
     */
    private String LocalTime;

    public String getLocalTime() {
        return LocalTime;
    }

    public void setLocalTime(String localTime) {
        LocalTime = localTime;
    }

    /**
     * 第13域，交易日期
     */
    private String LocalDate;

    public String getLocalDate() {
        return LocalDate;
    }

    public void setLocalDate(String localDate) {
        LocalDate = localDate;
    }

    /**
     * 第14域，卡片有效期
     */
    private String ExpDate;

    public String getExpDate() {
        return ExpDate;
    }

    public void setExpDate(String expDate) {
        ExpDate = expDate;
    }

    /**
     * 第15域。交易日期
     */
    private String SettleDate;

    public String getSettleDate() {
        return SettleDate;
    }

    public void setSettleDate(String settleDate) {
        SettleDate = settleDate;
    }

    /**
     * 第22域，输入方式
     */
    private String EntryMode;

    public String getEntryMode() {
        return EntryMode;
    }

    public void setEntryMode(String entryMode) {
        EntryMode = entryMode;
    }

    /**
     * 第23域，卡序号
     */
    private String PanSeqNo;

    public String getPanSeqNo() {
        return PanSeqNo;
    }

    public void setPanSeqNo(String panSeqNo) {
        PanSeqNo = panSeqNo;
    }

    /**
     * 第三域，24 预处理码
     */
    private String Nii;

    public String getNii() {
        return Nii;
    }

    public void setNii(String nii) {
        Nii = nii;
    }

    /**
     * 第25域 ， 服务点条件吗
     */
    private String SvrCode;

    public String getSvrCode() {
        return SvrCode;
    }

    public void setSvrCode(String svrCode) {
        SvrCode = svrCode;
    }

    /**
     * 第32域
     */
    private String AcquirerID;

    public String getAcquirerID() {
        return AcquirerID;
    }

    public void setAcquirerID(String acquirerID) {
        AcquirerID = acquirerID;
    }

    /**
     * 第35域
     */
    private String Track2;

    public String getTrack2() {
        return Track2;
    }

    public void setTrack2(String track2) {
        Track2 = track2;
    }


    /**
     * 第37域
     */
    private String RRN;

    public String getRRN() {
        return RRN;
    }

    public void setRRN(String rRN) {
        RRN = rRN;
    }

    /**
     * 第38域，认证码
     */
    private String AuthCode;

    public String getAuthCode() {
        return AuthCode;
    }

    public void setAuthCode(String authCode) {
        AuthCode = authCode;
    }

    /**
     * 第39域，响应码
     */
    private String RspCode;

    public String getRspCode() {
        return RspCode;
    }

    public void setRspCode(String rspCode) {
        RspCode = rspCode;
    }


    /**
     * 第41域，响应码
     */
    private String TermID;

    public String getTermID() {
        return TermID;
    }

    public void setTermID(String termID) {
        TermID = termID;
    }

    /**
     * 第42域，响应码
     */
    private String MerchID;

    public String getMerchID() {
        return MerchID;
    }

    public void setMerchID(String merchID) {
        MerchID = merchID;
    }

    /**
     * 第44域，发卡行和收单行
     */
    private String Field44;

    public String getField44() {
        return Field44;
    }

    public void setField44(String field44) {
        Field44 = field44;
    }


    /**
     * 第49域，交易货币代码
     */
    private String CurrencyCode;

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        CurrencyCode = currencyCode;
    }

    /**
     * 操作员号码
     */
    private int oprNo;

    public int getOprNo() {
        return oprNo;
    }

    public void setOprNo(int oprNo) {
        this.oprNo = oprNo;
    }

    /**
     * 备注 52 从第三位开始到最后
     */
    private String PIN;

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String pin) {
        PIN = pin;
    }

    /**
     * 如果是卡交易，55 存储此次交易的卡片55域信息
     */
    private byte[] ICCData;

    public byte[] getICCData() {
        return ICCData;
    }

    public void setICCData(byte[] iCCData) {
        ICCData = iCCData;
    }

    /**
     * 交易60域
     */
    private String Field60;

    public String getField60() {
        return Field60;
    }

    public void setField60(String field60) {
        Field60 = field60;
    }

    /**
     * 交易62域
     */
    private String Field62;

    public String getField62() {
        return Field62;
    }

    public void setField62(String field62) {
        Field62 = field62;
    }

    /**
     * 备注 63 从第三位开始到最后
     */
    private String Field63;

    public String getField63() {
        return Field63;
    }

    public void setField63(String field63) {
        Field63 = field63;
    }

    /**
     * 备注 63 从第三位开始到最后
     */
    private String Refence;

    public String getRefence() {
        return Refence;
    }

    public void setRefence(String refence) {
        Refence = refence;
    }

    /**
     * 发卡组织 63域 前三位
     */
    private String IssuerName;

    public String getIssuerName() {
        return IssuerName;
    }

    public void setIssuerName(String issuerName) {
        IssuerName = issuerName;
    }

    public String getTypeAccount() {
        return typeAccount;
    }

    public void setTypeAccount(String typeAccount) {
        this.typeAccount = typeAccount;
    }

    private String typeAccount;

    /**
     *
     */
    private boolean fallback;

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    private String AdquirerName;

    public String getAdquirerName() {
        return AdquirerName;
    }

    public void setAdquirerName(String adquirerName) {
        AdquirerName = adquirerName;
    }


    /**
     * DATAFAST
     */
    private long ammountXX;
    private long ammount0;
    private long ammountIVA;
    private long ammountService;
    private long ammountTip;
    private long ammountCashOver;
    private long montoFijo;
    private String NII;
    private String track1;
    private String CVV;
    private String field54;
    private boolean isField55;
    private String field55;
    private String field57;
    private String field58;
    private String field59;
    private String field59Print;
    private String field61;
    private String ARQC;
    private String TC;
    private String TVR;
    private String TSI;
    private String addRespData;
    private String MsgType;
    private String typeDeferred;
    private String field57Print;
    private String OTT;
    private String token;
    private String idVendedor;
    private String cedula;
    private String telefono;
    private String IdPreAutAmpl;
    private String TypeTransElectronic;
    private String pagoVarioSeleccionado;
    private String pagoVarioSeleccionadoNombre;
    private boolean alreadyPrinted = false;
    private String promptsPrinter;
    private String promptsAmountPrinter;
    private String tipoMontoFijo;
    private boolean multicomercio;
    private String idComercio;
    private String nameMultAcq;
    private String MID_InterOper;
    private String PanPE;
    private String intRev = "0";

    public long getAmmountXX() {
        return ammountXX;
    }

    public void setAmmountXX(long ammountXX) {
        this.ammountXX = ammountXX;
    }

    public long getAmmount0() {
        return ammount0;
    }

    public void setAmmount0(long ammount0) {
        this.ammount0 = ammount0;
    }

    public long getAmmountIVA() {
        return ammountIVA;
    }

    public void setAmmountIVA(long ammountIVA) {
        this.ammountIVA = ammountIVA;
    }

    public long getAmmountService() {
        return ammountService;
    }

    public void setAmmountService(long ammountService) {
        this.ammountService = ammountService;
    }

    public long getAmmountTip() {
        return ammountTip;
    }

    public void setAmmountTip(long ammountTip) {
        this.ammountTip = ammountTip;
    }

    public long getAmmountCashOver() {
        return ammountCashOver;
    }

    public void setAmmountCashOver(long ammountCashOver) {
        this.ammountCashOver = ammountCashOver;
    }

    public long getMontoFijo() {
        return montoFijo;
    }

    public void setMontoFijo(long montoFijo) {
        this.montoFijo = montoFijo;
    }

    public String getNII() {
        return NII;
    }

    public void setNII(String NII) {
        this.NII = NII;
    }

    public String getTrack1() {
        return track1;
    }

    public void setTrack1(String track1) {
        this.track1 = track1;
    }

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }

    public boolean isField55() {
        return isField55;
    }

    public void setIsField55(boolean field55) {
        isField55 = field55;
    }

    public String getField55() {
        return field55;
    }

    public void setField55(String field55) {
        this.field55 = field55;
    }

    public String getField54() {
        return field54;
    }

    public void setField54(String field54) {
        this.field54 = field54;
    }

    public String getField57() {
        return field57;
    }

    public void setField57(String field57) {
        this.field57 = field57;
    }

    public String getField58() {
        return field58;
    }

    public void setField58(String field58) {
        this.field58 = field58;
    }

    public String getField59() {
        return field59;
    }

    public void setField59(String field59) {
        this.field59 = field59;
    }

    public String getField59Print() {
        return field59Print;
    }

    public void setField59Print(String field59Print) {
        this.field59Print = field59Print;
    }

    public String getField61() {
        return field61;
    }

    public void setField61(String field61) {
        this.field61 = field61;
    }

    public String getARQC() {
        return ARQC;
    }

    public void setARQC(String ARQC) {
        this.ARQC = ARQC;
    }

    public String getTC() {
        return TC;
    }

    public void setTC(String TC) {
        this.TC = TC;
    }

    public String getTVR() {
        return TVR;
    }

    public void setTVR(String TVR) {
        this.TVR = TVR;
    }

    public String getTSI() {
        return TSI;
    }

    public void setTSI(String TSI) {
        this.TSI = TSI;
    }

    public String getAddRespData() {
        return addRespData;
    }

    public void setAddRespData(String addRespData) {
        this.addRespData = addRespData;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getTypeDeferred() {
        return typeDeferred;
    }

    public void setTypeDeferred(String typeDeferred) {
        this.typeDeferred = typeDeferred;
    }

    public String getField57Print() {
        return field57Print;
    }

    public void setField57Print(String field57Print) {
        this.field57Print = field57Print;
    }

    public String getOTT() {
        return OTT;
    }

    public void setOTT(String OTT) {
        this.OTT = OTT;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isVoided() {
        return isVoided;
    }

    public String getTransEName() {
        return TransEName;
    }

    public void setTransEName(String transEName) {
        TransEName = transEName;
    }

    public String getIdPreAutAmpl() {
        return IdPreAutAmpl;
    }

    public void setIdPreAutAmpl(String idPreAutAmpl) {
        IdPreAutAmpl = idPreAutAmpl;
    }

    public String getTypeTransElectronic() {
        return TypeTransElectronic;
    }

    public void setTypeTransElectronic(String typeTransElectronic) {
        TypeTransElectronic = typeTransElectronic;
    }

    public String getPagoVarioSeleccionado() {
        return pagoVarioSeleccionado;
    }

    public void setPagoVarioSeleccionado(String pagoVarioSeleccionado) {
        this.pagoVarioSeleccionado = pagoVarioSeleccionado;
    }

    public String getPagoVarioSeleccionadoNombre() {
        return pagoVarioSeleccionadoNombre;
    }

    public void setPagoVarioSeleccionadoNombre(String pagoVarioSeleccionadoNombre) {
        this.pagoVarioSeleccionadoNombre = pagoVarioSeleccionadoNombre;
    }

    public boolean isAlreadyPrinted() {
        return alreadyPrinted;
    }

    public void setAlreadyPrinted(boolean alreadyPrinted) {
        this.alreadyPrinted = alreadyPrinted;
    }

    public String getPromptsPrinter() {
        return promptsPrinter;
    }

    public void setPromptsPrinter(String promptsPrinter) {
        this.promptsPrinter = promptsPrinter;
    }

    public String getPromptsAmountPrinter() {
        return promptsAmountPrinter;
    }

    public void setPromptsAmountPrinter(String promptsAmountPrinter) {
        this.promptsAmountPrinter = promptsAmountPrinter;
    }

    public String getTipoMontoFijo() {
        return tipoMontoFijo;
    }

    public void setTipoMontoFijo(String tipoMontoFijo) {
        this.tipoMontoFijo = tipoMontoFijo;
    }

    public boolean isMulticomercio() {
        return multicomercio;
    }

    public void setMulticomercio(boolean multicomercio) {
        this.multicomercio = multicomercio;
    }

    public String getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(String idComercio) {
        this.idComercio = idComercio;
    }

    public String getMID_InterOper() {
        return MID_InterOper;
    }

    public void setMID_InterOper(String MID_InterOper) {
        this.MID_InterOper = MID_InterOper;
    }

    public String getPanPE() {
        return PanPE;
    }

    public void setPanPE(String panPE) {
        PanPE = panPE;
    }

    public String getNameMultAcq() {
        return nameMultAcq;
    }

    public void setNameMultAcq(String nameMultAcq) {
        this.nameMultAcq = nameMultAcq;
    }

    public void setIntRev(String intRev){
        this.intRev = intRev;
    }

    public String getIntRev(){
        return intRev;
    }

    /********************************************************/
}
