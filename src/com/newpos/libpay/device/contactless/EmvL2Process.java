package com.newpos.libpay.device.contactless;

import android.content.Context;

import com.datafast.file_management.Files_Management;
import com.newpos.bypay.EmvL2;
import com.newpos.bypay.IEmvInitLibCallBack;
import com.newpos.bypay.IEmvL2CallBack;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import java.io.File;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.ENTRY_POINT;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.PROCESSING;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.TERMINAL;
import static com.newpos.libpay.trans.finace.FinanceTrans.DOLAR;
import static com.newpos.libpay.trans.finace.FinanceTrans.EURO;
import static com.newpos.libpay.trans.finace.FinanceTrans.LOCAL;
import static com.newpos.libpay.utils.ISOUtil.padleft;


public class EmvL2Process {
    public EmvL2Process(Context context, TransInputPara p){
        mContext =context;
        para = p;
        Tparam = p.getTransUI();
    }
    protected TransUI Tparam;
    private String path;
    private String[] modules;

    private Context mContext;
    private int TapCardMode =0;
    private EmvL2 emvL2 =null;
    private CallBackHandle emvl2CallBack;
    private ParamEmvL2 param;
    private CardManager detects;
    public long Cl2amount;
    public long Cl2otherAmount;
    protected boolean ClMode;
    protected byte[] ICCData; // 55*
    protected byte[]AID;
    protected byte[]track1;
    protected String track2;
    protected String PanSeqNo;
    protected String cardno;
    protected byte[]ExpDate;
    protected String expdate;
    protected int CVM_type;
    protected String Lable="";
    protected String TVR;
    protected String TSI;
    protected String ARQC;
    protected String CID;
    protected String Random ;//随机数
    protected String traceNo;
    protected String typeCoin;
    protected String holderName;
    protected String typeTrans;
    public String tkn;

    private TransInputPara para;

    byte[] PaypassTag55 = {(byte) 0x9F, (byte)0x26, // AC (Application Cryptogram)
            (byte) 0x9F, (byte)0x27, // CID
            (byte) 0x9F, (byte)0x10, // IAD (Issuer Application Data)
            (byte) 0x9F, (byte)0x37, // Unpredicatable Number
            (byte) 0x9F, (byte)0x36, // ATC (Application Transaction Counter)
            (byte) 0x95, // TVR
            (byte) 0x9A, // Transaction Date
            (byte) 0x9C, // Transaction Type
            (byte) 0x9F, (byte)0x02, // Amount Authorised
            (byte) 0x5F,(byte)0x2A, // Transaction Currency Code
            (byte) 0x82, // AIP
            (byte) 0x9F,(byte)0x1A, // Terminal Country Code
            (byte) 0x9F,(byte)0x03, // Amount Other
            (byte) 0x9F,(byte)0x33, // Terminal Capabilities
            // opt
            (byte) 0x9F,(byte)0x34, // CVM Result
            (byte) 0x9F,(byte)0x35, // Terminal Type
            (byte) 0x9F,(byte)0x1E, // IFD Serial Number
            (byte) 0x84, // Dedicated File Name
            (byte) 0x9F,(byte)0x09, // Application Version #
            (byte) 0x9F,(byte)0x41, // Transaction Sequence Counter
            0x4F,
            (byte) 0x5F,(byte)0x34, // PAN Sequence Number
            0x50//Application label
    };
    byte[] PaywaveTag55 = {
            (byte) 0x9F, (byte)0x26, // AC (Application Cryptogram)
            (byte) 0x9F, (byte)0x27, // CID
            (byte) 0x9F, (byte)0x10, // IAD (Issuer Application Data)
            (byte) 0x9F, (byte)0x37, // Unpredicatable Number
            (byte) 0x9F, (byte)0x36, // ATC (Application Transaction Counter)
            (byte) 0x95, // TVR
            (byte) 0x9A, // Transaction Date
            (byte) 0x9C, // Transaction Type
            (byte) 0x9F, (byte)0x02, // Amount Authorised
            (byte) 0x5F,(byte)0x2A, // Transaction Currency Code
            (byte) 0x82, // AIP
            (byte) 0x9F,(byte)0x1A, // Terminal Country Code
            (byte) 0x9F,(byte)0x03, // Amount Other
            (byte) 0x9F,(byte)0x33, // Terminal Capabilities
            // opt
            (byte) 0x9F,(byte)0x34, // CVM Result
            (byte) 0x9F,(byte)0x35, // Terminal Type
            (byte) 0x9F,(byte)0x1E, // IFD Serial Number
            (byte) 0x84, // Dedicated File Name
            (byte) 0x9F,(byte)0x09, // Application Version #
            (byte) 0x9F,(byte)0x41, // Transaction Sequence Counter
            0x4F,
            (byte) 0x5F,(byte)0x34, // PAN Sequence Number
            0x50//Application label
    };
    byte[] PayGenericTag55 = {
            (byte) 0x9F, (byte)0x26, // AC (Application Cryptogram)
            (byte) 0x9F, (byte)0x27, // CID
            (byte) 0x9F, (byte)0x10, // IAD (Issuer Application Data)
            (byte) 0x9F, (byte)0x37, // Unpredicatable Number
            (byte) 0x9F, (byte)0x36, // ATC (Application Transaction Counter)
            (byte) 0x95, // TVR
            (byte) 0x9A, // Transaction Date
            (byte) 0x9C, // Transaction Type
            (byte) 0x9F, (byte)0x02, // Amount Authorised
            (byte) 0x5F,(byte)0x2A, // Transaction Currency Code
            (byte) 0x82, // AIP
            (byte) 0x9F,(byte)0x1A, // Terminal Country Code
            (byte) 0x9F,(byte)0x03, // Amount Other
            (byte) 0x9F,(byte)0x33, // Terminal Capabilities
            // opt
            (byte) 0x9F,(byte)0x34, // CVM Result
            (byte) 0x9F,(byte)0x35, // Terminal Type
            (byte) 0x9F,(byte)0x1E, // IFD Serial Number
            (byte) 0x84, // Dedicated File Name
            (byte) 0x9F,(byte)0x09, // Application Version #
            //(byte) 0x9F,(byte)0x41, // Transaction Sequence Counter
            //0x4F,
            (byte) 0x5F,(byte)0x34, // PAN Sequence Number
            //0x50//Application label
            (byte) 0x9F,(byte)0x06,
            (byte) 0x9F,(byte)0x07,
            (byte) 0x9F,(byte)0x53,
            (byte) 0x9F,(byte)0x71
    };
    public int emvl2ParamInit(){
        SetDataEmpty();
        ParamEmvL2 paramEmvL2=new ParamEmvL2();
        Logger.debug("begin to initialize emv l2");
        path=mContext.getFilesDir().getPath()+"/";
        // currently we load kernel in apk path.we'll load these
        //in the system path.

        modules=new String[]{path+"libPayPass.so",path+"libPayWave.so",path+"libXPressPay.so",path+"libDPAS.so"};
        //replace the above string using the following string after loading kernel in the system path.
        // modules=new String []{EmvL2.PayWave,EmvL2.PayPass};
        detects = CardManager.getInstance(0x10);
        param = paramEmvL2;//transaction parameters.
        emvL2 = new EmvL2();
        EmvL2.EMVL2Init(new IEmvInitLibCallBack() {
            @Override
            public void InitCallback(boolean isAllSu) {
                if (isAllSu){
                    Logger.debug("init bypay sdk successfully");
                }else
                    Logger.debug("init bypay fail");
            }
        });
        File tempFile = new File(mContext.getFilesDir().getPath()+"/libPayPass.so");
        if (!tempFile.exists()){
            PAYUtils.copyAssetsToData(mContext,"libPayPass.so");
            PAYUtils.copyAssetsToData(mContext,"libPayWave.so");
            PAYUtils.copyAssetsToData(mContext,"libXPressPay.so");
            PAYUtils.copyAssetsToData(mContext,"libDPAS.so");
        }
        emvl2CallBack = new CallBackHandle(Tparam,param,detects);
        emvl2CallBack.setmCtx(mContext);
        emvl2CallBack.setTypeTrans(typeTrans);
        emvL2.EmvL2CallBackSet(emvl2CallBack);//set callback .just need initialize one time.
        emvL2.LoadKernels(modules);//make sure this method called after callback set.very important.just need initialize one time.
        //emvL2.EmvL2transCounterSet(1);//Read transaction counter from your parameter and set
        emvL2.EmvL2transCounterSet(Long.parseLong(traceNo));//Read transaction counter from your parameter and set  JM
        emvL2.EmvL2OptionsSet(param.l2Options);//set transaction's options list
        emvL2.EmvL2AidClear();//clear aid
        emvL2.EmvL2ClParamClear();//clear cl param.

        //load param
        //byte[] su = PAYUtils.getAssertFileData("TERMINAL");
        byte[] su = Files_Management.readFileBin(TERMINAL, mContext);
        if (su!=null){
            Logger.debug("terminfs: "+ ISOUtil.byte2hex(su));
            emvL2.EmvL2TerminfSet(su, su.length);//load terminal configuration
        }else{
            return 1;
        }

        //byte[] aid = PAYUtils.getAssertFileData("PROCESSING");
        byte[] aid = Files_Management.readFileBin(PROCESSING, mContext);
        if (aid!=null){
            Logger.debug("aids' len: "+aid.length+"\naids: "+ISOUtil.byte2hex(aid));
            int index =0;
            int aidsTalLen = aid.length;
            index +=(20+1+2);//1hash mark + 20hash value
            int count = 1;
            while (index<aidsTalLen){
                if((aid[index]&0xff) == 0x81) {
                    index += 1;//81 for more than 128
                    Logger.error("get 81 tag , increase index ,curent: "+index);
                }
                int aidLen = aid[index]&0xff;
                index +=1;//one byte for aid's len
                byte[] tempAid = new byte[aidLen+1];
                System.arraycopy(aid,index,tempAid,0,aidLen);
                Logger.debug("aid : "+ISOUtil.byte2hex(tempAid)+"\nlength: "+tempAid.length+"actual len : "+aidLen);
                int ret = emvL2.EmvL2AidAdd(tempAid,aidLen,count);//load aids
                if (ret !=0)
                    Logger.debug("load aid "+ISOUtil.byte2hex(tempAid)+"fail");
                index+=aidLen;
                index +=2;// 2 byte for aid mark
                count++;
            }
        }else{
            return 2;
        }

        //byte[] entrypoint = PAYUtils.getAssertFileData("ENTRY_POINT");
        byte[] entrypoint = Files_Management.readFileBin(ENTRY_POINT, mContext);
        if (entrypoint!=null){
            Logger.debug("length: "+entrypoint.length+"\nentrypoint : "+ISOUtil.byte2hex(entrypoint));
            int index = 20+1+2;//1hash mark + 20hash value
            int epTalLen = entrypoint.length;
            while (index<epTalLen){
                if((entrypoint[index]&0xff) == 0x81) {
                    index += 1;//81 for more than 128
                    Logger.debug("get 81 tag , increase index ,curent: "+index);
                }
                int signalLen = entrypoint[index]&0xff;
                Logger.debug("cl signal len: "+signalLen);
                index +=1; // len value
                byte[] tempEP = new byte[signalLen+1];
                System.arraycopy(entrypoint,index,tempEP,0,signalLen);
                Logger.debug("length: "+signalLen+"\nEPsignal : "+ISOUtil.byte2hex(tempEP));
                int ret = emvL2.EmvL2ClParamAdd((byte) param.tranType, tempEP, signalLen);//load entry point parameters
                if (ret != 0){
                    Logger.debug("load cl param "+ISOUtil.byte2hex(tempEP)+" fail");
                }
                index += signalLen;
                index +=2;// 2 byte for aid mark
                Logger.debug("index: "+index);
            }
        }
        else{
            return 3;
        }
        Logger.debug("load param done");
        return 0;
    }
    public void SetAmount(long amount,long otheramount){
        Cl2amount=amount;
        Cl2otherAmount=otheramount;
    }
    public int start(){
        param.amount = Cl2amount;
        if (param.amount<=0) {
            emvL2.EmvL2TransactionClose();
            return -1;
        }
        if(param.tranType == EmvL2.L2_TT_WITH_CASHBACK ){
            param.otherAmount =Cl2otherAmount;
            if (param.otherAmount<=0) {
                emvL2.EmvL2TransactionClose();
                return -1;
            }
        }


        transactionSetData(param);
        int cs;
        int TranResult = -1;
        boolean isFallback=false;

        param.DetectContactLess = param.l2Options.Contactless;
        param.DetectContact = param.l2Options.Contact;
        param.DetectMagStripe = param.l2Options.Magstripe;

        if (!param.DetectContactLess){//Contact
            Logger.debug("use icc card");
            cs = emvl2CallBack.FnCardDetect(param);
            switch (cs){
                case EmvL2.L2_CS_SWIPED:
                    TranResult =IEmvL2CallBack.L2_FAILED;
                    break;
                case EmvL2.L2_CS_INSERTED:
                    TranResult = emvL2.EmvL2TransactionExecute(1);
                    if (TranResult == IEmvL2CallBack.L2_ALTER_OTHER_INTERFACE){
                        param.DetectContact = false;
                        isFallback = true;
                        if (!param.DetectMagStripe)
                            TranResult = IEmvL2CallBack.L2_END_APPLICATION;
                        else {
                            cs = emvl2CallBack.FnCardDetect(param);
                            switch (cs){
                                case EmvL2.L2_CS_SWIPED:
                                    TranResult = IEmvL2CallBack.L2_FAILED;
                                    break;
                                case EmvL2.L2_CS_INSERTED:
                                    TranResult = emvL2.EmvL2TransactionExecute(1);
                                    break;
                                case EmvL2.L2_CS_CANCELED:
                                    TranResult = cs;
                                    break;
                                case EmvL2.L2_CS_TIMEOUT:
                                    TranResult = cs;
                                    break;
                                default:
                                    TranResult = IEmvL2CallBack.L2_TRANSACTION_CANCELLED;
                                    break;
                            }
                        }
                    }
                    break;
                case EmvL2.L2_CS_CANCELED:
                    break;
                case EmvL2.L2_CS_TIMEOUT:
                    break;
                default:
                    TranResult = IEmvL2CallBack.L2_TRANSACTION_CANCELLED;
                    break;
            }
        }else {

            TranResult = emvL2.EmvL2TransactionExecute(0);

            if (getToken()) {
                return 0;
            }

            Logger.debug("emvL2.EmvL2TransactionExecute get return "+TranResult);
            if (TranResult == IEmvL2CallBack.L2_NOT_EMV_CARD_POOLED){
                TranResult = IEmvL2CallBack.L2_FAILED;
                Logger.debug("L2_NOT_EMV_CARD_POOLED ");
            }else if(TranResult == IEmvL2CallBack.L2_ALTER_OTHER_INTERFACE||
                    emvL2.EmvL2NoContactlessSupported()) {
                param.DetectContactLess = false;
                isFallback = true;
                if (!param.DetectContact && !param.DetectMagStripe)
                    TranResult = IEmvL2CallBack.L2_END_APPLICATION;
                else {
                    cs =emvl2CallBack.FnCardDetect(param);
                    Logger.debug("contactless part get tap card return "+cs);
                    switch (cs){
                        case EmvL2.L2_CS_SWIPED:
                            TranResult = IEmvL2CallBack.L2_FAILED;
                            break;
                        case EmvL2.L2_CS_INSERTED:
                            TranResult = IEmvL2CallBack.L2_FAILED;
                            break;
                        case EmvL2.L2_CS_CANCELED:
                            TranResult = cs;
                            break;
                        case EmvL2.L2_CS_TIMEOUT:
                            TranResult = cs;
                            break;
                        default:
                            TranResult = IEmvL2CallBack.L2_TRANSACTION_CANCELLED;
                            break;
                    }
                }
            }
        }
        Logger.debug("transaction close with "+TranResult);
        if(TranResult==IEmvL2CallBack.L2_APPROVED){

            Logger.debug("EmvL2GetMode="+ISOUtil.bcd2int(emvL2.EmvL2GetMode()));
            if(ISOUtil.bcd2int(emvL2.EmvL2GetMode())==91){
                ClMode=true;
            }
            if(SetTransData()!=IEmvL2CallBack.L2_NONE)
                TranResult=IEmvL2CallBack.L2_FAILED;
        }
        emvL2.EmvL2TransactionClose();
        if(TranResult==IEmvL2CallBack.L2_APPROVED)
            return  0;
        else if (TranResult==IEmvL2CallBack.L2_DECLINED || TranResult==IEmvL2CallBack.L2_END_APPLICATION){
            return 7;
        }
        else
            return -1;
    }
    public boolean GetClMode(){
        return ClMode;
    }
    public byte [] GetEmvOnlineData(){
        return ICCData;
    }
    public String GetCardNo(){
        return cardno;
    }
    public String GetPanSeqNo(){
        return PanSeqNo;
    }
    public String GetTrack2data(){
        return track2;
    }
    public String GetAid(){
        if (AID != null)
            return ISOUtil.hexString(AID);
        else
            return "";
    }
    public String GetLable(){return Lable;}
    public String GetTVR(){return TVR;}
    public String GetTSI(){return TSI;}
    public String GetARQC(){return ARQC;}
    public String GetCID(){return CID;}
    public String GetRandom(){return Random;}
    public String getExpdate() {
        return expdate;
    }
    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public void setTypeCoin(String typeCoin) {
        this.typeCoin = typeCoin;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setTypeTrans(String typeTrans) {
        this.typeTrans = typeTrans;
    }

    public String deleteCharString0(String sourceString, char chElemData) {
        String deleteString = "";
        for (int i = 0; i < sourceString.length(); i++) {
            if (sourceString.charAt(i) != chElemData) {
                deleteString += sourceString.charAt(i);
            }
        }
        return deleteString;
    }
    public int GetCVMType(){
        return CVM_type;
    }
    private int SetTransData(){
        byte []tPanSN;
        byte []tTrack2;
        ExpDate = emvL2.EmvL2DataGetByTag(0x5F24);
        if(ExpDate!=null){
            Logger.debug("ExpDate="+ISOUtil.hexString(ExpDate));
            expdate = ISOUtil.hexString(ExpDate).substring(0,4);
        }

        // 1磁道
        track1 = emvL2.EmvL2DataGetByTag(0x9F1F);
        if(track1!=null)
            Logger.debug("track1="+ISOUtil.hexString(track1));
        // 卡序号
        tPanSN = emvL2.EmvL2DataGetByTag(0x5F34);
        if(tPanSN!=null) {
            Logger.debug("tPanSN="+ISOUtil.hexString(tPanSN));
            if (tPanSN.length == 1) {
                PanSeqNo = padleft(ISOUtil.bcd2int(tPanSN, 0, tPanSN.length) + "", 3, '0');
                Logger.debug("PanSeqNo = " + PanSeqNo);
            } else {
                PanSeqNo = null;
            }
        }
        AID=emvL2.EmvL2DataGetByTag(0x4F);
        if(AID==null)
            AID=emvL2.EmvL2DataGetByTag(0x9F06);
        if(AID != null) {
            Logger.debug("AID="+ISOUtil.hexString(AID));
            /*if (ISOUtil.hexString(AID).startsWith("A000000003"))
                ICCData = emvL2.EmvL2DataGetByTagList(PaywaveTag55, PaypassTag55.length);
            else
                ICCData = emvL2.EmvL2DataGetByTagList(PaypassTag55, PaypassTag55.length);*/
            ICCData = emvL2.EmvL2DataGetByTagList(PayGenericTag55, PayGenericTag55.length);
            Logger.debug("ICCData="+ISOUtil.hexString(ICCData));
        }
        tTrack2 = emvL2.EmvL2DataGetByTag(0x57);
        if(tTrack2==null) {
            Logger.debug("first track2=null");
            tTrack2 = emvL2.EmvL2DataGetByTag(0x9F6B);
        }
        if(tTrack2!=null) {
            Logger.debug("tTrack2 ["+tTrack2.length+"]="+ISOUtil.hexString(tTrack2));
            if(ISOUtil.hexString(tTrack2).endsWith("F")||ISOUtil.hexString(tTrack2).endsWith("f"))
            {
                Logger.error("end with F");
                int Sindex=ISOUtil.hexString(tTrack2).length();
                if(ISOUtil.hexString(tTrack2).endsWith("F"))
                    track2=deleteCharString0(ISOUtil.hexString(tTrack2),'F') ;
                else if(ISOUtil.hexString(tTrack2).endsWith("f"))
                    track2=deleteCharString0(ISOUtil.hexString(tTrack2),'f') ;
            }
            else{
                track2=ISOUtil.hexString(tTrack2);
            }
            Logger.debug("track2="+track2);
            if (track2.length() < 7) {//磁道信息没读到
                return IEmvL2CallBack.L2_FAILED;
            } else {
                if(track2.length()>37){
                    track2=track2.substring(0, 37);
                    Logger.debug("more than 37 , after track2="+track2);
                }
                cardno = track2.split("D")[0];
                try{
                    if (expdate == null){
                        expdate = track2.split("D")[1].substring(0,4);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else
            return IEmvL2CallBack.L2_FAILED;
        //CVM_type=emvL2.EmvL2GetCvm().getCvmValue();
        Logger.debug("CVM type="+CVM_type);
//for print
        byte[] temp = new byte[256];
        //随机数
        temp = emvL2.EmvL2DataGetByTag(0x9F37);
        if(temp!=null){
            Random = ISOUtil.byte2hex(temp);
            Logger.debug("get Random = " + Random);
        }
        // 应用标签
        temp = emvL2.EmvL2DataGetByTag(0x50);
        if(temp!=null) {
            Lable = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(temp));
            Logger.debug("get Lable = " + Lable);
        }

        temp = emvL2.EmvL2DataGetByTag(0x9F26);
        if(temp!=null) {
            ARQC = ISOUtil.byte2hex(temp);
            Logger.debug("get ARQC = " + ARQC);
        }

        temp = emvL2.EmvL2DataGetByTag(0x9F27);
        if(temp!=null) {
            CID = ISOUtil.byte2hex(temp);
            Logger.debug("get CID = " + CID);
        }

        temp = emvL2.EmvL2DataGetByTag(0x9B);
        if(temp!=null) {
            TSI = ISOUtil.byte2hex(temp);
            Logger.debug("get TSI = " + TSI);
        }

        temp = emvL2.EmvL2DataGetByTag(0x95);
        if(temp!=null) {
            TVR = ISOUtil.byte2hex(temp);
            Logger.debug("get TVR = " + TVR);
        }

        temp = emvL2.EmvL2DataGetByTag(0x5F20);
        if (temp!=null){
            holderName = ISOUtil.byte2hex(temp);
            Logger.debug("get holderName = " + holderName);
        }else{
            holderName="---";
        }
        return IEmvL2CallBack.L2_NONE;
    }
    private void SetDataEmpty(){
        ClMode=false;   //false means EMV mode
        ICCData=null;
        AID=null;
        PanSeqNo=null;
        cardno=null;
        track1=null;
        track2=null;
        cardno=null;
        ExpDate=null;
        Cl2amount=0;
        Cl2otherAmount=0;
        CVM_type=0;
    }
    private void transactionSetData(ParamEmvL2 param){
        byte trans_types[] = {
                0x00,
                0x01,
                0x09,
                0x20,
                0x21,
                0x31,
                0x50,
                0x60,
                0x70,
                0x78,
                0x79,
                (byte) 0x90
        };
        byte[] trans_data=new byte[32];
        int trans_data_len=0;
        trans_data[trans_data_len++] = (byte) 0x9C;//transaction type
        trans_data[trans_data_len++] = 0x01;
        trans_data[trans_data_len++] = trans_types[param.tranType];

        trans_data[trans_data_len++] = (byte) 0x9F;//transaction amount
        trans_data[trans_data_len++] = 0x02;
        trans_data[trans_data_len++] = 0x06;

        byte[] amount = ISOUtil.str2bcd(String.valueOf(param.amount),true);
        System.arraycopy(amount,0,trans_data,trans_data_len+6-amount.length,amount.length);
        trans_data_len += 6;

        trans_data[trans_data_len++] = (byte) 0x9F;//transaction other amount
        trans_data[trans_data_len++] = 0x03;
        trans_data[trans_data_len++] = 0x06;
        byte[] otheramount = ISOUtil.str2bcd(String.valueOf(param.otherAmount),false);
        System.arraycopy(otheramount,0,trans_data,trans_data_len,otheramount.length);
        trans_data_len += 6;
        Logger.debug("set transaction data: "+ISOUtil.byte2hex(trans_data)+"\n length: "+trans_data_len);

        trans_data[trans_data_len++] = (byte) 0x5F;//transaction currency code
        trans_data[trans_data_len++] = 0x2A;
        trans_data[trans_data_len++] = 0x02;
        byte[] tranCode = new byte[0];
        switch (typeCoin){//JM
            case LOCAL:
            case DOLAR:
                tranCode = new byte[]{0x08, 0x40};
                break;
            case EURO:
                tranCode = new byte[]{0x09, 0x78};
                break;
            default:
                tranCode = new byte[]{0x08, 0x40};
                break;
        }
        System.arraycopy(tranCode,0,trans_data,trans_data_len,2);
        trans_data_len += 2;

        emvL2.EmvL2TransDataSet(trans_data,trans_data_len);

    }

    /**
     * Solo aplica para pagos electronicos, no se
     * continua la trans ctl, ya que solo se requiere
     * obtener el token enviado desde la app del celular
     * @return
     */
    private boolean getToken(){
        boolean ret = false;

        if (typeTrans==null)
            return ret;

        switch (typeTrans){
            case Trans.Type.ELECTRONIC:
            case Trans.Type.ELECTRONIC_DEFERRED:
                tkn = emvl2CallBack.token;
                emvL2.EmvL2TransactionClose();
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }
}

