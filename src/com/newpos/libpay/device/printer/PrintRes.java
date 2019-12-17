package com.newpos.libpay.device.printer;

import java.util.Locale;

/**
 * Created by zhouqiang on 2017/4/4.
 * 打印常量类
 *
 * @author zhouqiang
 */

public class PrintRes {
    public interface CH {
        boolean zh = Locale.getDefault().getLanguage().equals("zh");
        String WANNING = zh ? "警告:该固件是测试版本，不能用于商业用途，在此版本上进行交易可能危害到持卡人的用卡安全。" : "Warning:Debug firmware,use for commercial forbidden,it will be hurt the benefit of cardholder through this version.";
        String MERCHANT_COPY = zh ? "商户存根     MERCHANT COPY" : "MERCHANT COPY";
        String CARDHOLDER_COPY = zh ? "持卡人存根     CARDHOLDER COPY" : "CARDHOLDER COPY";
        String BANK_COPY = zh ? "银行存根     BANK COPY" : "BANK COPY";
        String MERCHANT_NAME = zh ? "商户名称(MERCHANT NAME):" : "MERCHANT NAME:";
        String MERCHANT_ID = zh ? "商户编号(MERCHANT NO):" : "MERCHANT NO:";
        String TERNIMAL_ID = zh ? "终端编号(TERMINAL NO):" : "TERMINAL NO:";
        String OPERATOR_NO = zh ? "操作员号(OPERATOR NO):" : "OPERATOR NO";
        String CARD_NO = zh ? "卡号(CARD NO):" : "CARD NO:";
        String SCANCODE = zh ? "付款码(PayCode):" : "PayCode:";
        String ISSUER = zh ? "发卡行(ISSUER):  中信银行" : "ISSUER : China Bank";
        String ISSUER2 = zh ? "发卡行(ISSUER):" : "ISSUER:";
        String ACQUIRER = zh ? "收单行(ACQ):  银联商务" : "ACQ : Unionpay";
        String ACQUIRER2 = zh ? "收单行(ACQ):" : "ACQ:";
        String TRANS_AAC = zh ? "应用密文(AAC):" : "AAC:";
        String TRANS_AAC_ARQC = zh ? "联机交易" : "ARQC";
        String TRANS_AAC_TC = zh ? "脱机交易" : "TC";
        String TRANS_TYPE = zh ? "交易类型(TXN. TYPE):" : "TXN. TYPE :";
        String CARD_EXPDATE = zh ? "卡有效期(EXP. DATE):" : "EXP. DATE:";
        String BATCH_NO = zh ? "批次号(BATCH NO):" : "BATCH NO:";
        String VOUCHER_NO = zh ? "凭证号(VOUCHER NO):" : "VOUCHER NO:";
        String AUTH_NO = zh ? "授权码(AUTH NO):" : "AUTH NO:";
        String DATE_TIME = zh ? "日期/时间(DATE/TIME):" : "DATE/TIME:";
        String REF_NO = zh ? "交易参考号(REF. NO):" : "REF. NO:";
        String AMOUNT = zh ? "金额(AMOUNT):" : "AMOUNT:";
        String EC_AMOUNT = zh ? "电子现金余额(AMOUNT):" : "EC AMOUNT:";
        String CARD_AMOUNT = zh ? "卡余额(AMOUNT):" : "AMOUNT:";
        String RMB = zh ? "RMB:" : "$:";
        String REFERENCE = zh ? "备注/REFERENCE" : "REFERENCE";
        String REPRINT = zh ? "***** 重打印 *****" : "***** REPRINT *****";
        String CARDHOLDER_SIGN = zh ? "持卡人签名" : "CardHolder Signature";
        String AGREE_TRANS = zh ? "本人同意以上交易" : "I agree these transaction above";
        String SETTLE_SUMMARY = zh ? "结算总计单" : "Settle Sum Receipt";
        String SETTLE_LIST = zh ? "类型/TYPE      笔数/SUM      金额/AMOUNT" : "TYPE      SUM      AMOUNT";
        String SETTLE_INNER_CARD = zh ? "内卡：对账平" : "Inner card；Reconciliation";
        String SETTLE_OUTER_CARD = zh ? "外卡：对账平" : "Outer card:Reconciliation";
        String SETTLE_DETAILS = zh ? "结算明细单" : "Settle Detail Receipt";
        String SETTLE_DETAILS_LIST_CH = zh ? "凭证号   类型   授权码   金额   卡号" : "VOUCHER     TYPE     AUTHNO     AMOUNT    CARDNO";
        String SETTLE_DETAILS_LIST_EN = "VOUCHER     TYPE     AUTHNO     AMOUNT    CARDNO";
        String DETAILS = "Transaction Details";
    }

    public static final String[] TRANSCH = {
            "余额查询",
            "消费",
            "消费撤销",
            "电子现金余额查询",
            "快速消费",
            "结算",
            "预授权",
            "预授权完成",
            "预授权完成撤销",
            "预授权撤销",
            "退货",
            "转账",
            "圈存",
            "圈提",
            "签到",
            "签退",
            "参数公钥下载",
            "扫码消费",
            "扫码撤销",
            "扫码退货",
            "迴聲測試",
            "銷售"
    };

    public static final String[] TRANSEN = {
            "ENQUIRY",
            "SALE",
            "VOID",
            "EC_ENQUIRY",
            "QUICKPASS",
            "SETTLE", // 5
            "PREAUTH",
            "PREAUTHCOMPLETE",
            "PREAUTHCOMPLETEVOID",
            "PREAUTHVOID",
            "REFUND",
            "TRANSFER",
            "CREFORLOAD",
            "DEBFORLOAD",
            "LOGON",
            "LOGOUT",
            "DOWNPARA",
            "SCANSALE",
            "SCANVOID",
            "SCANREFUND",
            "ECHO_TEST",//20
            "VENTA", //21
            "ANULACION",//22
            "FALLBACK", //23
            "AUTO_SETTLE",//24
            "SALE_CTL",//25
            "DIFERIDO",//26
            "PAGO_CON_CODIGO",//27
            "PREAUTO",//28
            "AMPLIACION",//29
            "CONFIRMACION",//30
            "ANULACION_PREAUTO",//31
            "REIMPRESION",//32
            "PREVOUCHER", //33
            "PAGO_PREVOUCHER",//34
            "CASH_OVER", //35
            "PAGOS_VARIOS"//36
    };
}
