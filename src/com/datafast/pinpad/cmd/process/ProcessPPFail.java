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
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_TRAMA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;
import static com.datafast.server.activity.ServerTCP.listenerServer;
import static com.newpos.libpay.presenter.TransUIImpl.getErrInfo;
import static com.newpos.libpay.presenter.TransUIImpl.getStatusInfo;
import static com.newpos.libpay.trans.Trans.Type.ANULACION;
import static com.newpos.libpay.trans.Trans.Type.ELECTRONIC_DEFERRED;

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
    private boolean isFallBack;
    private String transName;

    public void setFallBack(boolean fallBack) {
        isFallBack = fallBack;
    }

    public ProcessPPFail(Context ctx){
        super(ctx);
        this.pp_response = new PP_Response();
    }

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
    public String getTransName() {
        return transName;
    }
    public void setTransName(String transName) {
        this.transName = transName;
    }

    public void responseLTInvalid(String keySecurity) {
        ltResponse.setTypeMsg(LT);
        ltResponse.setRspCodeMsg(ERROR_PROCESO);
        ltResponse.setIdCodNetCte(" ");
        ltResponse.setIdCodNetDef(" ");
        ltResponse.setCardNumber(ISOUtil.spacepadRight("", 25));
        ltResponse.setCardExpDate(ISOUtil.spacepadRight("", 4));
        if (tconf.getSIMBOLO_EURO().equals("0")){
            ltResponse.setCardNumEncryp(ISOUtil.spacepadRight("", 40));
        }else {
            ltResponse.setCardNumEncryp(ISOUtil.spacepadRight("", 64));
        }
        ltResponse.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(57)) + "", 20, ' '));
        ltResponse.setFiller(ISOUtil.spacepad("", 27));
        ltResponse.setHash(keySecurity);
        ppResponse = ltResponse.packData();
        listenerServer.waitRspHost(ppResponse);
    }

    public void responseCTInvalid(String keySecurity) {
        ctResponse.setTypeMsg(CT);
        ctResponse.setRspCodeMsg(ERROR_PROCESO);
        if (tconf.getSIMBOLO_EURO().equals("0")){
            ctResponse.setCardNumber(ISOUtil.spacepad("", 40));
        }else {
            ctResponse.setCardNumber(ISOUtil.spacepad("", 64));
        }
        ctResponse.setBinCard(ISOUtil.spacepad("", 6));
        ctResponse.setCardExpDate(ISOUtil.spacepad("", 4));
        ctResponse.setMsgRsp(ISOUtil.padright(getStatusInfo(String.valueOf(57)) + "", 20, ' '));
        ctResponse.setFiller(ISOUtil.spacepad("", 27));
        ctResponse.setHash(keySecurity);
        ppResponse = ctResponse.packData();
        listenerServer.waitRspHost(ppResponse);
    }

    public void responsePPInvalid(PP_Request ppRequestData, String mensaje, String code, boolean rspHost){
        pp_response.setTypeMsg(PP);
        pp_response.setRspCodeMsg(code);
        pp_response.setIdCodNetAcq(ISOUtil.padleft(ppRequestData.getIdCodNetAcq() + "", 2, '0'));
        pp_response.setRspCode("00");
        pp_response.setMsgRsp(ISOUtil.padright(mensaje + "", 20, ' '));
        pp_response.setSecuencialTrans(ISOUtil.spacepad("", 6));
        pp_response.setHourTrans(ISOUtil.spacepad("", 6));
        pp_response.setDateTrans(ISOUtil.spacepad("", 8));
        pp_response.setNumberAuth(ISOUtil.spacepad("", 6));
        pp_response.setTID(ISOUtil.spacepad("", 8));
        pp_response.setMID(ISOUtil.spacepad("", 15));
        pp_response.setInterestFinancingValue(ISOUtil.spacepad("", 12));
        pp_response.setMsgPrintAwards(ISOUtil.spacepad("", 80));
        pp_response.setCodBankAcq(ISOUtil.spacepad("", 3));
        pp_response.setNameBankAcq(ISOUtil.spacepad("", 30));
        pp_response.setNumberBatch(ISOUtil.spacepad("", 6));
        pp_response.setNameGroupCard(ISOUtil.spacepad("", 25));
        pp_response.setModeReadCard(ISOUtil.spacepadRight("", 2));
        pp_response.setFixedAmount(ISOUtil.spacepad("", 12));
        pp_response.setValidatePIN(ISOUtil.spacepad("", 15));
        pp_response.setNameCardHolder(ISOUtil.spacepad("", 40));
        pp_response.setARQC(ISOUtil.spacepad("", 16));
        pp_response.setTVR(ISOUtil.spacepad("", 10));
        pp_response.setTSI(ISOUtil.spacepad("", 4));
        pp_response.setAppEMV(ISOUtil.spacepad("", 20));
        pp_response.setAIDEMV(ISOUtil.spacepad("", 20));
        pp_response.setCriptEMV(ISOUtil.spacepad("", 22));
        pp_response.setExpDateCard(ISOUtil.spacepad("", 4));
        pp_response.setNumberCardMask(ISOUtil.spacepad("", 25));

        if (tconf.getSIMBOLO_EURO().equals("0")){
            pp_response.setNumberCardEncrypt(ISOUtil.spacepad("", 40));
        }else {
            pp_response.setNumberCardEncrypt(ISOUtil.spacepad("", 64));
        }
        pp_response.setFiller(ISOUtil.spacepad("", 27));
        pp_response.setHash(ppRequestData.getHash());

        ppResponse = pp_response.packData();
        if (rspHost){
            listenerServer.waitRspHost(ppResponse);
        }
    }

    public static int[] codErrMsg = new int[]{
            Tcode.T_err_deferred,
            Tcode.T_search_card_err,
            Tcode.T_read_app_data_err,
            Tcode.T_msg_err_gas,
            Tcode.T_trans_done,
            Tcode.T_err_amounts,
            Tcode.T_err_detect_card_failed,
            Tcode.T_no_answer,
            Tcode.T_not_reverse,
            Tcode.T_err_void_not_allow,
            Tcode.T_insert_card,
            Tcode.T_err_not_allow,
            Tcode.T_select_app_err,
            Tcode.T_err_cod,
            Tcode.T_ic_not_allow_swipe,
            Tcode.T_trans_reversed
    };

    public boolean validCodErrMsg(int codRet){
        for(int cod : codErrMsg){
            if (cod == codRet){
                return true;
            }
        }
        return false;
    }

    public static int[] codErr = new int[]{
            Tcode.T_not_reverse,
            Tcode.T_search_card_err,
            Tcode.T_read_app_data_err,
            Tcode.T_err_no_trans,
            Tcode.T_wait_timeout,
            Tcode.T_err_deferred,
            Tcode.T_trans_not_exist,
            Tcode.T_user_cancel_input,
            Tcode.T_err_trm,
            Tcode.T_user_cancel_operation,
            Tcode.T_msg_err_gas,
            Tcode.T_err_amounts,
            Tcode.T_trans_done,
            Tcode.T_unsupport_card,
            Tcode.T_err_detect_card_failed,
            Tcode.T_trans_voided,
            Tcode.T_void_card_not_same,
            Tcode.T_no_answer,
            Tcode.T_err_void_not_allow,
            Tcode.T_insert_card,
            Tcode.T_err_not_allow,
            Tcode.T_select_app_err,
            Tcode.T_err_cod,
            Tcode.T_ic_not_allow_swipe,
            Tcode.T_err_batch_full,
            Tcode.T_socket_err,
            Tcode.T_receive_err,
            Tcode.T_ic_not_allow_swipe,
            Tcode.T_trans_reversed,
            Tcode.T_err_incorrect,
            Tcode.T_user_cancel_pin_err,
            Tcode.T_err_pin_null
    };

    public void cmdCancel(String cmd, int codRet){
        //iso8583.clearData();

        switch (Server.cmd) {

            case CT:
                CT_Request ct_request = new CT_Request();
                ct_request.UnPackData(Server.dat);
                keySecurity = ct_request.getHash();

                ctResponse.setTypeMsg(CT);
                ctResponse.setRspCodeMsg(PAYUtils.selectRspCode(codRet, iso8583.getfield(39)));
                if (tconf.getSIMBOLO_EURO().equals("0")){
                    ctResponse.setCardNumber(ISOUtil.spacepad("", 40));
                }else {
                    ctResponse.setCardNumber(ISOUtil.spacepad("", 64));
                }
                ctResponse.setBinCard(ISOUtil.spacepad("", 6));
                ctResponse.setCardExpDate(ISOUtil.spacepad("", 4));
                ctResponse.setMsgRsp(ISOUtil.padright(PAYUtils.selectRspMsg(codRet) + "", 20, ' '));
                ctResponse.setFiller(ISOUtil.spacepad("", 27));
                ctResponse.setHash(keySecurity);

                ppResponse = ctResponse.packData();

                //listenerServer.waitRspHost(ctResponse.packData());

                break;

            case LT:
                LT_Request lt_request = new LT_Request();
                lt_request.UnPackData(Server.dat);

                keySecurity = lt_request.getHash();
                ltResponse.setTypeMsg(LT);
                ltResponse.setRspCodeMsg(PAYUtils.selectRspCode(codRet, iso8583.getfield(39)));
                ltResponse.setIdCodNetCte("0");
                ltResponse.setIdCodNetDef("0");
                ltResponse.setCardNumber(ISOUtil.spacepad("", 25));
                ltResponse.setCardExpDate(ISOUtil.spacepad("", 4));
                if (tconf.getSIMBOLO_EURO().equals("0")){
                    ltResponse.setCardNumEncryp(ISOUtil.spacepad("", 40));
                }else {
                    ltResponse.setCardNumEncryp(ISOUtil.spacepad("", 64));
                }
                //ltResponse.setMsgRsp("TRANS CANCELADA     ");
                ltResponse.setMsgRsp(ISOUtil.padright(PAYUtils.selectRspMsg(codRet) + "", 20, ' '));
                ltResponse.setFiller(ISOUtil.spacepad("", 27));
                ltResponse.setHash(keySecurity);
                retVal = 0;

                ppResponse = ltResponse.packData();
                //listenerServer.waitRspHost(ltResponse.packData());
                break;

            case PP:
                PP_Request pp_request = new PP_Request();
                pp_request.UnPackData(Server.dat);

                String mensaje;
                if (codRet == Tcode.T_err_batch_full) {
                    mensaje = "PROCESO CONTROL";
                } else if (validCodErrMsg(codRet)) {
                    mensaje = getErrInfo(String.valueOf(Tcode.T_user_cancel_input));
                }else {
                    mensaje = getErrInfo(String.valueOf(codRet));
                }
                if (mensaje.length() > 20) {
                    mensaje = mensaje.substring(0, 20);
                }

                String code;
                if (codRet == Tcode.T_err_batch_full) {
                    code = PAYUtils.selectRspCode(20, iso8583.getfield(39));
                } else {
                    code = PAYUtils.selectRspCode(codRet, iso8583.getfield(39));
                }

                boolean finErr = false;
                for (int cod : codErr) {
                    if (codRet == cod) {
                        if(cod == Tcode.T_err_void_not_allow||cod == Tcode.T_not_reverse ) {
                            mensaje = "TRANS. NO ENCONTRADA";
                        }else if (cod == Tcode.T_socket_err || cod == Tcode.T_receive_err || cod == Tcode.T_no_answer) {
                            mensaje = "NO HUBO RESPUESTA";
                        }else if(cod == Tcode.T_err_incorrect){
                            mensaje = "ERROR EN TARJETA";
                        }else if(cod==Tcode.T_user_cancel_pin_err||cod==Tcode.T_err_pin_null){
                            mensaje = "TRANS CANCELADA ";
                        }
                        responsePPInvalid(pp_request, mensaje, code, false);
                        finErr = true;
                        break;
                    }
                }

                if (finErr) {
                    break;
                }

                keySecurity = pp_request.getHash();

                pp_response.setTypeMsg(PP);
                pp_response.setRspCodeMsg(code);
                pp_response.setIdCodNetAcq(ISOUtil.padleft(pp_request.getIdCodNetAcq() + "", 2, '0'));
                if (codRet == Tcode.T_gen_2_ac_fail) {
                    pp_response.setRspCode(ISOUtil.spacepadZero("YY", 2));
                } else {
                    pp_response.setRspCode(ISOUtil.spacepadZero(iso8583.getfield(39), 2));
                }
                pp_response.setMsgRsp(ISOUtil.padright(mensaje, 20, ' '));
                pp_response.setSecuencialTrans(ISOUtil.spacepadRight(iso8583.getfield(11), 6));

                pp_response.setHourTrans(ISOUtil.spacepadRight(pp_request.getHourTrans(), 6));
                pp_response.setDateTrans(ISOUtil.spacepadRight(pp_request.getDateTrans(), 8));

                if ((pp_request.getTypeTrans().equals("01") && codRet == Tcode.T_trans_rejected) ||
                        (pp_request.getTypeTrans().equals("06") || codRet == Tcode.T_trans_rejected) ||
                         codRet == Tcode.T_gen_2_ac_fail){
                    pp_response.setNumberAuth(ISOUtil.spacepadRight("", 6));
                }else {
                    pp_response.setNumberAuth(ISOUtil.spacepadRight(iso8583.getfield(38), 6));
                }

                if (iso8583.getfield(41) != null) {
                    pp_response.setTID(ISOUtil.spacepadRight(iso8583.getfield(41), 8));
                } else {
                    pp_response.setTID(ISOUtil.spacepadRight(TermID, 8));
                }
                if (iso8583.getfield(42) != null) {
                    pp_response.setMID(ISOUtil.spacepadRight(iso8583.getfield(42), 15));
                } else {
                    pp_response.setMID(ISOUtil.spacepadRight(MerchID, 15));
                }

                pp_response.setInterestFinancingValue(ISOUtil.spacepadRight("", 12));
                pp_response.setMsgPrintAwards(ISOUtil.spacepadRight("", 80));

                if ((pp_request.getTypeTrans().equals("01") && codRet == 3005)
                        || (pp_request.getTypeTrans().equals("06") || codRet == 3005)
                        ||  (pp_request.getTypeTrans().equals("03") || codRet == 3002)
                        ||   codRet == Tcode.T_gen_2_ac_fail){
                    pp_response.setCodBankAcq(ISOUtil.spacepad("", 3));
                    pp_response.setNameBankAcq(ISOUtil.spacepad("", 30));
                }else {
                    try {
                        String fld44 = iso8583.getfield(44);
                        if (fld44 != null) {
                            pp_response.setCodBankAcq(ISOUtil.spacepadRight(fld44.substring(0, 2), 3));
                            if (fld44.length() == 5)
                                pp_response.setNameBankAcq(ISOUtil.spacepadRight(CardType[Integer.parseInt(fld44.substring(0, 1)) - 1], 30));
                            else
                                pp_response.setNameBankAcq(ISOUtil.spacepadRight(CardType[Integer.parseInt(fld44.substring(1, 2)) - 1], 30));
                        } else {
                            pp_response.setCodBankAcq(ISOUtil.spacepad("", 3));
                            pp_response.setNameBankAcq(ISOUtil.spacepad("", 30));
                        }
                    } catch (IndexOutOfBoundsException e) {
                    }
                }

                if (iso8583.getfield(39) != null) {
                    pp_response.setNumberBatch(ISOUtil.spacepadRight(BatchNo, 6));
                    pp_response.setNameGroupCard(ISOUtil.spacepadRight(rango.getNOMBRE_RANGO(), 25));
                } else {
                    pp_response.setNumberBatch(ISOUtil.spacepadRight("", 6));
                    pp_response.setNameGroupCard(ISOUtil.spacepadRight("", 25));
                }

                pp_response.setModeReadCard(PAYUtils.entryModePP(inputModeFail, isFallBack, false));

                if (montoFijo > 0) {
                    pp_response.setFixedAmount(ISOUtil.padleft(montoFijo + "", 12, '0'));
                } else {
                    pp_response.setFixedAmount(ISOUtil.padleft("", 12, ' '));
                }
                pp_response.setValidatePIN(ISOUtil.spacepad("", 15));

                String numberCard;
                if (pp_request.getTypeTrans().equals("06")){
                    pp_response.setNumberCardMask(ISOUtil.spacepadRight(PANFail,25));
                    numberCard = iso8583.getfield(2);
                    /*pp_response.setNumberCardEncrypt(ISOUtil.spacepad(encryption.hashSha256(iso8583.getfield(2)),64));*/
                }else {
                    pp_response.setNumberCardMask(ISOUtil.spacepadRight(packageMaskedCard(PANFail),25));
                    numberCard = PANFail;
                    /*pp_response.setNumberCardEncrypt(ISOUtil.spacepad(encryption.hashSha256(PANFail),64));*/
                }
                pp_response.setFiller(ISOUtil.spacepadRight("", 27));

                if (codRet == Tcode.T_gen_2_ac_fail || isElectronic()) {
                    pp_response.setNumberCardMask(ISOUtil.spacepad("", 25));
                    pp_response.setNameCardHolder(ISOUtil.spacepadRight("", 40));
                    pp_response.setARQC(ISOUtil.spacepadRight("", 16));
                    pp_response.setTVR(ISOUtil.spacepadRight("", 10));
                    pp_response.setTSI(ISOUtil.spacepadRight("", 4));
                    pp_response.setAppEMV(ISOUtil.spacepadRight("", 20));
                    pp_response.setAIDEMV(ISOUtil.spacepadRight("", 20));
                    pp_response.setCriptEMV(ISOUtil.spacepad("", 22));
                } else {
                    if (inputModeFail == ENTRY_MODE_NFC) {
                        pp_response.setNameCardHolder(ISOUtil.spacepadRight(verifyHolderName(emvL2Process.getHolderName()), 40));
                        pp_response.setARQC(ISOUtil.spacepadRight(emvL2Process.GetARQC(), 16));
                        pp_response.setTVR(ISOUtil.spacepadRight(emvL2Process.GetTVR(), 10));
                        pp_response.setTSI(ISOUtil.spacepadRight(emvL2Process.GetTSI(), 4));
                        pp_response.setAppEMV(ISOUtil.spacepadRight(emvL2Process.GetLable(), 20));
                        pp_response.setAIDEMV(ISOUtil.spacepadRight(emvL2Process.GetAid(), 20));
                        pp_response.setCriptEMV(ISOUtil.spacepad(emvL2Process.GetCID(), 22));
                    } else if (inputModeFail == ENTRY_MODE_ICC) {
                        pp_response.setNameCardHolder(ISOUtil.spacepadRight(getNameCard(), 40));
                        pp_response.setARQC(ISOUtil.spacepadRight(ARQCFail, 16));
                        pp_response.setTVR(ISOUtil.spacepadRight(getTVR(), 10));
                        pp_response.setTSI(ISOUtil.spacepadRight(getTSI(), 4));
                        pp_response.setAppEMV(ISOUtil.spacepadRight(getLabelCard(), 20));
                        pp_response.setAIDEMV(ISOUtil.spacepadRight(getAID(), 20));
                        pp_response.setCriptEMV(ISOUtil.spacepad("", 22));
                    } else {
                        if ((pp_request.getTypeTrans().equals("01") || pp_request.getTypeTrans().equals("02"))) {
                            if (cardHolderNameFail.contains("^")) {
                                String[] nameCard = cardHolderNameFail.split("\\^");
                                pp_response.setNameCardHolder(ISOUtil.spacepadRight(nameCard[1], 40));
                            } else {
                                pp_response.setNameCardHolder(ISOUtil.spacepadRight(cardHolderNameFail, 40));
                            }
                            pp_response.setCodBankAcq(ISOUtil.spacepad("", 3));
                            pp_response.setNameBankAcq(ISOUtil.spacepad("", 30));
                            pp_response.setNumberCardMask(ISOUtil.spacepad("", 25));
                        } else {
                            pp_response.setNameCardHolder(ISOUtil.spacepadRight(cardHolderNameFail, 40));
                        }
                        /*pp_response.setNameCardHolder(ISOUtil.spacepadRight(cardHolderNameFail, 40));*/
                        pp_response.setARQC(ISOUtil.spacepadRight("", 16));
                        pp_response.setTVR(ISOUtil.spacepadRight("", 10));
                        pp_response.setTSI(ISOUtil.spacepadRight("", 4));
                        pp_response.setAppEMV(ISOUtil.spacepad("", 20));
                        pp_response.setAIDEMV(ISOUtil.spacepad("", 20));
                        pp_response.setCriptEMV(ISOUtil.spacepad("", 22));
                    }
                }

                if (pp_response.getModeReadCard().equals("05") || pp_response.getModeReadCard().equals("03")){
                    pp_response.setExpDateCard(ISOUtil.spacepadRight("",4));
                }else {
                    pp_response.setExpDateCard(ISOUtil.spacepadRight(expDateFail,4));
                }

                if (pp_request.getTypeTrans().equals("04") && (codRet == Tcode.T_trans_rejected || codRet == 3002 || codRet == Tcode.T_receive_err)){
                    if (tconf.getSIMBOLO_EURO().equals("0")){
                        pp_response.setNumberCardEncrypt(ISOUtil.spacepad("",40));
                    }else {
                        pp_response.setNumberCardEncrypt(ISOUtil.spacepad("",64));
                    }
                }else {

                    if (isElectronic() || isAnulacionElectronic()) {
                        if (tconf.getSIMBOLO_EURO().equals("0")){
                            pp_response.setNumberCardEncrypt(ISOUtil.spacepad("",40));
                        }else {
                            pp_response.setNumberCardEncrypt(ISOUtil.spacepad("",64));
                        }
                    } else {
                        if (tconf.getSIMBOLO_EURO().equals("0")){
                            pp_response.setNumberCardEncrypt(ISOUtil.spacepadRight(encryption.hashSha1(numberCard),40));
                        }else {
                            pp_response.setNumberCardEncrypt(ISOUtil.spacepadRight(encryption.hashSha256(numberCard),64));
                        }
                    }

                }



                pp_response.setHash(keySecurity);

                ppResponse = pp_response.packData();

                //listenerServer.waitRspHost(pp_response.packData());
                break;
        }
    }

    protected boolean isAnulacionElectronic() {
        return transName.equals(ANULACION) && (CodOTT != null || TokenElectronic != null);
    }

    protected boolean isElectronic() {
        return transName.equals(Trans.Type.ELECTRONIC) || transName.equals(ELECTRONIC_DEFERRED);
    }

    private String verifyHolderName(String nameCard){
        boolean isHexa;
        String ret = "";
        if (nameCard != null && !nameCard.equals("")) {
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
        }
        return ret;
    }
}
