package com.datafast.pinpad.cmd.process;

import android.content.Context;

import com.datafast.pinpad.cmd.CT.CT_Request;
import com.datafast.pinpad.cmd.CT.CT_Response;
import com.datafast.pinpad.cmd.LT.LT_Request;
import com.datafast.pinpad.cmd.LT.LT_Response;
import com.datafast.pinpad.cmd.PP.PP_Request;
import com.datafast.pinpad.cmd.PP.PP_Response;
import com.datafast.pinpad.cmd.Tools.encryption;
import com.datafast.server.server_tcp.Server;
import com.newpos.libpay.device.contactless.EmvL2Process;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;
import static com.datafast.server.activity.ServerTCP.listener;
import static com.newpos.libpay.presenter.TransUIImpl.getErrInfo;

public class ProcessPPFail extends FinanceTrans {

    private CT_Response ctResponse;
    private LT_Response ltResponse;
    private PP_Response pp_response;
    private EmvL2Process emvL2Process;
    private ISO8583 iso8583;
    private String keySecurity;
    private int inputModeFail = 0;
    private String PANFail = "";
    private String expDateFail = "";
    private String cardHolderNameFail = "";
    private String ARQCFail = "";

    public ProcessPPFail(Context ctx, ISO8583 iso8583) {
        super(ctx);
        this.ctResponse = new CT_Response();
        this.ltResponse = new LT_Response();
        this.pp_response = new PP_Response();
        this.iso8583 = iso8583;
    }

    public void setInputMode(int entrada){
        this.inputModeFail = entrada;
    }
    public void setPAN(String entrada){
        this.PANFail = entrada;
    }
    public void setExpDate(String entrada){
        this.expDateFail = entrada;
    }
    public void setEmvL2Process(EmvL2Process emvl2){
        this.emvL2Process = emvl2;
    }
    public void setCardHolderNameFail(String entrada){
        this.cardHolderNameFail = entrada;
    }
    public void setARQCFail(String ARQC){
        this.ARQCFail = ARQC;
    }

    public void cmdCancel(String cmd, int codRet){
        //iso8583.clearData();

        switch (Server.cmd) {

            case CT:
                CT_Request ct_request = new CT_Request();
                ct_request.UnPackData(Server.dat);
                keySecurity = ct_request.getHash();

                ctResponse.setTypeMsg(CT);
                ctResponse.setRspCodeMsg(PAYUtils.selectRspCode(codRet,iso8583.getfield(39)));
                ctResponse.setCardNumber(ISOUtil.spacepad("", 64));
                ctResponse.setBinCard(ISOUtil.spacepad("", 6));
                ctResponse.setCardExpDate(ISOUtil.spacepad("", 4));
                ctResponse.setMsgRsp(ISOUtil.padright(PAYUtils.selectRspMsg(codRet) + "", 20, ' '));
                ctResponse.setFiller(ISOUtil.spacepad("", 27));
                ctResponse.setHash(keySecurity);

                listener.waitRspHost(ctResponse.packData());

                break;

            case LT:
                LT_Request lt_request = new LT_Request();
                lt_request.UnPackData(Server.dat);

                keySecurity = lt_request.getHash();
                ltResponse.setTypeMsg(LT);
                ltResponse.setRspCodeMsg(PAYUtils.selectRspCode(codRet,iso8583.getfield(39)));
                ltResponse.setIdCodNetCte("0");
                ltResponse.setIdCodNetDef("0");
                ltResponse.setCardNumber(ISOUtil.spacepad("", 25));
                ltResponse.setCardExpDate(ISOUtil.spacepad("", 4));
                ltResponse.setCardNumEncryp(ISOUtil.spacepad("", 64));
                //ltResponse.setMsgRsp("TRANS CANCELADA     ");
                ltResponse.setMsgRsp(ISOUtil.padright(PAYUtils.selectRspMsg(codRet) + "", 20, ' '));
                ltResponse.setFiller(ISOUtil.spacepad("", 27));
                ltResponse.setHash(keySecurity);
                retVal = 0;

                listener.waitRspHost(ltResponse.packData());
                break;

            case PP:
                PP_Request pp_request = new PP_Request();
                pp_request.UnPackData(Server.dat);
                keySecurity = pp_request.getHash();

                pp_response.setTypeMsg(PP);
                if (codRet == Tcode.T_err_batch_full){
                    pp_response.setRspCodeMsg(PAYUtils.selectRspCode(02,iso8583.getfield(39)));
                }else {
                    pp_response.setRspCodeMsg(PAYUtils.selectRspCode(codRet,iso8583.getfield(39)));
                }
                pp_response.setIdCodNetAcq(ISOUtil.padleft(pp_request.getIdCodNetAcq() + "", 2, '0'));
                pp_response.setRspCode(ISOUtil.spacepadZero(iso8583.getfield(39), 2));
                String mensaje = getErrInfo(String.valueOf(codRet));
                if (mensaje.length() > 20){
                    mensaje = mensaje.substring(0,20);
                }
                pp_response.setMsgRsp(ISOUtil.padright( mensaje, 20, ' '));
                pp_response.setSecuencialTrans(ISOUtil.spacepadRight(iso8583.getfield(11),6));
                pp_response.setHourTrans(ISOUtil.spacepadRight(iso8583.getfield(12), 6));
                if (iso8583.getfield(13) != null){
                    pp_response.setDateTrans(ISOUtil.spacepadRight(PAYUtils.getYear() + iso8583.getfield(13), 8));
                }else {
                    pp_response.setDateTrans(ISOUtil.spacepadRight(iso8583.getfield(13), 8));
                }
                pp_response.setNumberAuth(ISOUtil.spacepadRight(iso8583.getfield(38), 6));
                if (iso8583.getfield(41) != null){
                    pp_response.setTID(ISOUtil.spacepadRight(iso8583.getfield(41), 8));
                }else {
                    pp_response.setTID(ISOUtil.spacepadRight(TermID, 8));
                }
                if (iso8583.getfield(42) != null){
                    pp_response.setMID(ISOUtil.spacepadRight(iso8583.getfield(42), 15));
                }else {
                    pp_response.setMID(ISOUtil.spacepadRight(MerchID, 15));
                }
                
                pp_response.setInterestFinancingValue(ISOUtil.spacepadRight("", 12));
                pp_response.setMsgPrintAwards(ISOUtil.spacepadRight("", 80));

                try {
                    String fld44 = iso8583.getfield(44);
                    if (fld44!=null) {
                        pp_response.setCodBankAcq(ISOUtil.spacepadRight(fld44.substring(0, 2), 3));
                        if (fld44.length() == 5)
                            pp_response.setNameBankAcq(ISOUtil.spacepadRight(CardType[Integer.parseInt(fld44.substring(0, 1)) - 1], 30));
                        else
                            pp_response.setNameBankAcq(ISOUtil.spacepadRight(CardType[Integer.parseInt(fld44.substring(1, 2)) - 1], 30));
                    }else{
                        pp_response.setCodBankAcq(ISOUtil.spacepad("", 3));
                        pp_response.setNameBankAcq(ISOUtil.spacepad("", 30));
                    }
                }catch (IndexOutOfBoundsException e){}

                if (iso8583.getfield(39) != null){
                    pp_response.setNumberBatch(ISOUtil.spacepadRight(BatchNo,6));
                    pp_response.setNameGroupCard(ISOUtil.spacepadRight(rango.getNOMBRE_RANGO(), 25));
                }else {
                    pp_response.setNumberBatch(ISOUtil.spacepadRight("",6));
                    pp_response.setNameGroupCard(ISOUtil.spacepadRight("", 25));
                }

                pp_response.setModeReadCard(PAYUtils.entryModePP(inputModeFail));

                if (montoFijo > 0){
                    pp_response.setFixedAmount(ISOUtil.padleft(montoFijo + "", 12, '0'));
                }else {
                    pp_response.setFixedAmount(ISOUtil.padleft( "", 12, ' '));
                }
                pp_response.setValidatePIN(ISOUtil.spacepad("", 15));

                if (inputModeFail == ENTRY_MODE_NFC){
                    pp_response.setNameCardHolder(ISOUtil.spacepadRight(verifyHolderName(emvL2Process.getHolderName()), 40));
                    pp_response.setARQC(ISOUtil.spacepadRight(emvL2Process.GetARQC(),16));
                    pp_response.setTVR(ISOUtil.spacepadRight(emvL2Process.GetTVR(),10));
                    pp_response.setTSI(ISOUtil.spacepadRight(emvL2Process.GetTSI(),4));
                    pp_response.setAppEMV(ISOUtil.spacepadRight(emvL2Process.GetLable(), 20));
                    pp_response.setAIDEMV(ISOUtil.spacepadRight(emvL2Process.GetAid(), 20));
                    pp_response.setCriptEMV(ISOUtil.spacepad(emvL2Process.GetCID(), 22));
                }else if (inputModeFail == ENTRY_MODE_ICC){
                    pp_response.setNameCardHolder(ISOUtil.spacepadRight(getNameCard(), 40));
                    pp_response.setARQC(ISOUtil.spacepadRight(ARQCFail,16));
                    pp_response.setTVR(ISOUtil.spacepadRight(getTVR(),10));
                    pp_response.setTSI(ISOUtil.spacepadRight(getTSI(),4));
                    pp_response.setAppEMV(ISOUtil.spacepadRight(getLabelCard(), 20));
                    pp_response.setAIDEMV(ISOUtil.spacepadRight(getAID(), 20));
                    pp_response.setCriptEMV(ISOUtil.spacepad("", 22));
                } else {
                    pp_response.setNameCardHolder(ISOUtil.spacepadRight(cardHolderNameFail, 40));
                    pp_response.setARQC(ISOUtil.spacepadRight("",16));
                    pp_response.setTVR(ISOUtil.spacepadRight("",10));
                    pp_response.setTSI(ISOUtil.spacepadRight("",4));
                    pp_response.setAppEMV(ISOUtil.spacepad("", 20));
                    pp_response.setAIDEMV(ISOUtil.spacepad("", 20));
                    pp_response.setCriptEMV(ISOUtil.spacepad("", 22));
                }
                pp_response.setExpDateCard(ISOUtil.spacepadRight(expDateFail,4));

                if (pp_request.getTypeTrans().equals("06")){
                    pp_response.setNumberCardMask(ISOUtil.spacepadRight(PANFail,25));
                    pp_response.setNumberCardEncrypt(ISOUtil.spacepad(encryption.hashSha256(iso8583.getfield(2)),64));
                    pp_response.setFiller(ISOUtil.spacepadRight(packageMaskedCard(iso8583.getfield(2)), 27));
                }else {
                    pp_response.setNumberCardMask(ISOUtil.spacepadRight(packageMaskedCard(PANFail),25));
                    pp_response.setNumberCardEncrypt(ISOUtil.spacepad(encryption.hashSha256(PANFail),64));
                    pp_response.setFiller(ISOUtil.spacepadRight("", 27));
                }
                pp_response.setHash(keySecurity);

                listener.waitRspHost(pp_response.packData());
                break;
        }
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
}
