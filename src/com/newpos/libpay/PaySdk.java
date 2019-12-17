package com.newpos.libpay;

import android.app.Activity;
import android.content.Context;

import com.datafast.transactions.Settle.AutoSettle;
import com.datafast.transactions.Settle.Settle;
import com.datafast.transactions.anulacion.Anulacion;
import com.datafast.transactions.cash_over.CashOver;
import com.datafast.transactions.diferido.Deferred;
import com.datafast.transactions.echotest.EchoTest;
import com.datafast.transactions.pagos_electronicos.TransPagosElectronicos;
import com.datafast.transactions.pagos_varios.TransPagosVarios;
import com.datafast.transactions.pre_voucher.PagoPreVoucher;
import com.datafast.transactions.pre_voucher.PreVoucher;
import com.datafast.transactions.preautorizacion.Ampliacion;
import com.datafast.transactions.preautorizacion.AnulacionPreautorizacion;
import com.datafast.transactions.preautorizacion.Confirmacion;
import com.datafast.transactions.preautorizacion.PreAutorizacion;
import com.datafast.transactions.preautorizacion.ReImpresion;
import com.datafast.transactions.venta.Venta;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.global.TMConstants;
import com.newpos.libpay.paras.EmvAidInfo;
import com.newpos.libpay.paras.EmvCapkInfo;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.presenter.TransUIImpl;
import com.newpos.libpay.presenter.TransView;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.ecquery.ECEnquiryTrans;
import com.newpos.libpay.trans.finace.query.EnquiryTrans;
import com.newpos.libpay.trans.finace.quickpass.QuickPassTrans;
import com.newpos.libpay.trans.finace.refund.RefundTrans;
import com.newpos.libpay.trans.finace.revocation.VoidTrans;
import com.newpos.libpay.trans.finace.sale.SaleTrans;
import com.newpos.libpay.trans.finace.scan.ScanRefund;
import com.newpos.libpay.trans.finace.scan.ScanSale;
import com.newpos.libpay.trans.finace.scan.ScanVoid;
import com.newpos.libpay.trans.finace.transfer.TransferTrans;
import com.newpos.libpay.trans.manager.DparaTrans;
import com.newpos.libpay.trans.manager.LogonTrans;
import com.newpos.libpay.trans.manager.SettleTrans;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.SDKManager;
import com.pos.device.SDKManagerCallback;

/**
 * Created by zhouqiang on 2017/4/25.
 *
 * @author zhouqiang
 * 支付sdk管理者
 */
public class PaySdk {

    /**
     * 单例
     */
    private static PaySdk mInstance = null;

    /**
     * 上下文对象，用于获取相关资源和使用其相应方法
     */
    private Context mContext = null;

    /**
     * 获前端段activity对象，主要用于扫码交易
     */
    private Activity mActivity = null;

    /**
     * MVP交媾P层接口，用于对m和v的交互
     */
    private TransPresenter presenter = null;

    /**
     * 标记sdk环境前端是否进行初始化操作
     */
    private static boolean isInit = false;

    /**
     * 初始化PaySdk环境的回调接口
     */
    private PaySdkListener mListener = null;

    /**
     * PaySdk产生的相关文件的保存路径
     * 如代码不进行设置，默认使用程序data分区
     *
     * @link @{@link String}
     */
    private String cacheFilePath = null;

    /**
     * 终端参数文件路径,用于设置一些交易中的偏好属性
     * 如代码不进行设置，默认使用程序自带配置文件
     *
     * @link @{@link String}
     */
    private String paraFilepath = null;

    public Context getContext() throws PaySdkException {
        if (this.mContext == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }
        return mContext;
    }

    public PaySdk setActivity(Activity activity) {
        this.mActivity = activity;
        return mInstance;
    }

    public PaySdk setParaFilePath(String path) {
        this.paraFilepath = path;
        return mInstance;
    }

    public String getParaFilepath() {
        return this.paraFilepath;
    }

    public PaySdk setCacheFilePath(String path) {
        this.cacheFilePath = path;
        return mInstance;
    }

    public String getCacheFilePath() {
        return this.cacheFilePath;
    }

    private PaySdk() {
    }

    public PaySdk setListener(PaySdkListener listener) {
        this.mListener = listener;
        return mInstance;
    }

    public static PaySdk getInstance() {
        if (mInstance == null) {
            mInstance = new PaySdk();
        }
        return mInstance;
    }

    public void init(Context context) throws PaySdkException {
        this.mContext = context;
        this.init();
    }

    public void init(Context context, PaySdkListener listener) throws PaySdkException {
        this.mContext = context;
        this.mListener = listener;
        this.init();
    }

    public void init() throws PaySdkException {
        System.out.println("init->start.....");
        if (this.mContext == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }

        if (this.paraFilepath == null || !this.paraFilepath.endsWith("properties")) {
            this.paraFilepath = TMConstants.DEFAULTCONFIG;
        }

        if (this.cacheFilePath == null) {
            this.cacheFilePath = mContext.getFilesDir() + "/";
        } else if (!this.cacheFilePath.endsWith("/")) {
            this.cacheFilePath += "/";
        }

        TMConfig.setRootFilePath(this.cacheFilePath);
        System.out.println("init->paras files path:" + this.paraFilepath);
        System.out.println("init->cache files will be saved in:" + this.cacheFilePath);
        System.out.println("init->pay sdk will run based on:" + (TMConfig.getInstance().getBankid() == 1 ? "UNIONPAY" : "CITICPAY"));
        /*if (!TMConfig.getInstance().isOnline()) {
            PAYUtils.copyAssetsToData(this.mContext, EmvAidInfo.FILENAME);
            PAYUtils.copyAssetsToData(this.mContext, EmvCapkInfo.FILENAME);
        }*/
        SDKManager.init(mContext, new SDKManagerCallback() {
            @Override
            public void onFinish() {
                isInit = true;
                System.out.println("init->success");
                if (mListener != null) {
                    mListener.success();
                }
            }
        });
    }

    /**
     * 释放卡片驱动资源
     */
    public void releaseCard() {
        if (isInit) {
            CardManager.getInstance(0).releaseAll();
        }
    }

    /**
     * 释放sdk环境资源
     */
    public void exit() {
        if (isInit) {
            SDKManager.release();
            isInit = false;
        }
    }

    public void startTrans(String transType, TransView tv) throws PaySdkException {
        if (this.mActivity == null) {
            throw new PaySdkException(PaySdkException.PARA_NULL);
        }
        TransInputPara para = new TransInputPara();
        para.setTransUI(new TransUIImpl(mActivity, tv));
        if (transType.equals(Trans.Type.SALE)) {
            para.setTransType(Trans.Type.SALE);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(true);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new SaleTrans(this.mContext, Trans.Type.SALE, para);
        }
        if (transType.equals(Trans.Type.SCANSALE)) {
            para.setTransType(Trans.Type.SCANSALE);
            para.setNeedPrint(true);
            para.setNeedAmount(true);
            presenter = new ScanSale(this.mContext, Trans.Type.SCANSALE, para);
        }
        if (transType.equals(Trans.Type.SCANVOID)) {
            para.setTransType(Trans.Type.SCANVOID);
            para.setNeedPrint(true);
            para.setNeedAmount(true);
            presenter = new ScanVoid(this.mContext, Trans.Type.SCANVOID, para);
        }
        if (transType.equals(Trans.Type.SCANREFUND)) {
            para.setTransType(Trans.Type.SCANREFUND);
            para.setNeedPrint(true);
            para.setNeedAmount(true);
            presenter = new ScanRefund(this.mContext, Trans.Type.SCANREFUND, para);
        }
        if (transType.equals(Trans.Type.DOWNPARA)) {
            para.setTransType(Trans.Type.DOWNPARA);
            presenter = new DparaTrans(this.mContext, Trans.Type.DOWNPARA, para);
        }
        if (transType.equals(Trans.Type.LOGON)) {
            para.setTransType(Trans.Type.LOGON);
            presenter = new LogonTrans(this.mContext, Trans.Type.LOGON, para);
        }
        if (transType.equals(Trans.Type.ENQUIRY)) {
            para.setTransType(Trans.Type.ENQUIRY);
            para.setNeedOnline(true);
            para.setNeedConfirmCard(true);
            para.setNeedPass(true);
            para.setEmvAll(true);
            presenter = new EnquiryTrans(this.mContext, Trans.Type.ENQUIRY, para);
        }
        if (transType.equals(Trans.Type.VOID)) {
            para.setTransType(Trans.Type.VOID);
            para.setNeedConfirmCard(false);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedPass(false);
            para.setEmvAll(false);
            presenter = new VoidTrans(this.mContext, Trans.Type.VOID, para);
        }
        if (transType.equals(Trans.Type.EC_ENQUIRY)) {
            para.setTransType(Trans.Type.EC_ENQUIRY);
            para.setNeedConfirmCard(true);
            para.setECTrans(true);
            para.setEmvAll(false);
            presenter = new ECEnquiryTrans(this.mContext, Trans.Type.EC_ENQUIRY, para);
        }
        if (transType.equals(Trans.Type.QUICKPASS)) {
            para.setTransType(Trans.Type.QUICKPASS);
            para.setNeedAmount(true);
            para.setECTrans(true);
            para.setNeedPrint(true);
            para.setEmvAll(true);
            presenter = new QuickPassTrans(this.mContext, Trans.Type.QUICKPASS, para);
        }
        if (transType.equals(Trans.Type.SETTLE)) {
            para.setTransType(Trans.Type.SETTLE);
            para.setNeedPrint(true);
            presenter = new SettleTrans(this.mContext, Trans.Type.SETTLE, para);
        }
        if (transType.equals(Trans.Type.REFUND)) {
            para.setTransType(Trans.Type.REFUND);
            para.setNeedConfirmCard(true);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedPass(false);
            para.setEmvAll(false);
            presenter = new RefundTrans(this.mContext, Trans.Type.REFUND, para);
        }
        if (transType.equals(Trans.Type.TRANSFER)) {
            para.setTransType(Trans.Type.TRANSFER);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedPass(false);
            para.setEmvAll(false);
            presenter = new TransferTrans(this.mContext, Trans.Type.TRANSFER, para);
        }
        if (transType.equals(Trans.Type.ECHO_TEST)) {
            para.setTransType(Trans.Type.ECHO_TEST);
            para.setNeedOnline(true);
            para.setNeedPrint(false);
            para.setNeedPass(false);
            para.setEmvAll(false);
            presenter = new EchoTest(this.mContext, Trans.Type.ECHO_TEST, para);
        }
        if (transType.equals(Trans.Type.VENTA)) {
            para.setTransType(Trans.Type.VENTA);
            para.setNeedOnline(true);
            para.setNeedPrint(false);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(false);
            para.setEmvAll(true);
            presenter = new Venta(this.mContext, Trans.Type.VENTA, para);
        }
        if (transType.equals(Trans.Type.ANULACION)) {
            para.setTransType(Trans.Type.ANULACION);
            para.setNeedConfirmCard(false);
            para.setNeedOnline(true);
            para.setNeedPrint(false);
            para.setNeedPass(false);
            para.setNeedAmount(false);
            para.setEmvAll(true);
            presenter = new Anulacion(this.mContext, Trans.Type.ANULACION, para);
        }
        if (transType.equals(Trans.Type.SETTLE)) {
            para.setTransType(Trans.Type.SETTLE);
            para.setNeedOnline(false);
            para.setNeedPrint(true);
            para.setNeedPass(false);
            para.setEmvAll(false);
            presenter = new Settle(this.mContext, Trans.Type.SETTLE, para);
        }
        if (transType.equals(Trans.Type.AUTO_SETTLE)) {
            para.setTransType(Trans.Type.AUTO_SETTLE);
            para.setNeedOnline(false);
            para.setNeedPrint(true);
            para.setNeedPass(false);
            para.setEmvAll(false);
            presenter = new AutoSettle(this.mContext, Trans.Type.AUTO_SETTLE, para);
        }
        if (transType.equals(Trans.Type.DEFERRED)) {
            para.setTransType(Trans.Type.DEFERRED);
            para.setNeedOnline(true);
            para.setNeedPrint(false);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new Deferred(this.mContext, Trans.Type.DEFERRED, para);
        }
        if (transType.equals(Trans.Type.ELECTRONIC)) {
            para.setTransType(Trans.Type.ELECTRONIC);
            para.setNeedOnline(true);
            para.setNeedPrint(false);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new TransPagosElectronicos(this.mContext, Trans.Type.ELECTRONIC, para);
        }
        if (transType.equals(Trans.Type.PREAUTO)) {
            para.setTransType(Trans.Type.PREAUTO);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new PreAutorizacion(this.mContext, Trans.Type.PREAUTO, para);
        }
        if (transType.equals(Trans.Type.AMPLIACION)) {
            para.setTransType(Trans.Type.AMPLIACION);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new Ampliacion(this.mContext, Trans.Type.AMPLIACION, para);
        }
        if (transType.equals(Trans.Type.CONFIRMACION)) {
            para.setTransType(Trans.Type.CONFIRMACION);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new Confirmacion(this.mContext, Trans.Type.CONFIRMACION, para);
        }
        if (transType.equals(Trans.Type.VOID_PREAUTO)) {
            para.setTransType(Trans.Type.VOID_PREAUTO);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new AnulacionPreautorizacion(this.mContext, Trans.Type.VOID_PREAUTO, para);
        }
        if (transType.equals(Trans.Type.REIMPRESION)) {
            para.setTransType(Trans.Type.REIMPRESION);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new ReImpresion(this.mContext, Trans.Type.REIMPRESION, para);
        }
        if (transType.equals(Trans.Type.PREVOUCHER)) {
            para.setTransType(Trans.Type.PREVOUCHER);
            para.setNeedOnline(false);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(true);
            para.setEmvAll(true);
            presenter = new PreVoucher(this.mContext, Trans.Type.PREVOUCHER, para);
        }
        if (transType.equals(Trans.Type.PAGO_PRE_VOUCHER)) {
            para.setTransType(Trans.Type.PAGO_PRE_VOUCHER);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(false);
            para.setEmvAll(true);
            presenter = new PagoPreVoucher(this.mContext, Trans.Type.PAGO_PRE_VOUCHER, para);
        }
        if (transType.equals(Trans.Type.CASH_OVER)) {
            para.setTransType(Trans.Type.CASH_OVER);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(false);
            para.setEmvAll(true);
            presenter = new CashOver(this.mContext, Trans.Type.CASH_OVER, para);
        }
        if (transType.equals(Trans.Type.PAGOS_VARIOS)) {
            para.setTransType(Trans.Type.PAGOS_VARIOS);
            para.setNeedOnline(true);
            para.setNeedPrint(true);
            para.setNeedConfirmCard(false);
            para.setNeedPass(true);
            para.setNeedAmount(false);
            para.setEmvAll(true);
            presenter = new TransPagosVarios(this.mContext, Trans.Type.PAGOS_VARIOS, para);
        }
        if (isInit) {
            new Thread() {
                @Override
                public void run() {
                    presenter.start();
                }
            }.start();
        } else {
            throw new PaySdkException(PaySdkException.NOT_INIT);
        }
    }
}
