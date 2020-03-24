package com.datafast.printer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.android.newpos.pay.R;
import com.datafast.inicializacion.prompts.Prompt;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.pinpad.OfflineRSA;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.device.scanner.QRCInfo;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.translog.TransLogData;

import java.util.ArrayList;

import cn.desert.newpos.payui.UIUtils;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_ANULACION;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_BORRAR_LOTE;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_REPORTE_DETALLADO;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_REPORTE_EMV;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_REPORTE_STIS;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_REPORTE_TERMINAL;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_TEST;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_ULTIMO_CIERRE;

public class PrintParameter extends AppCompatActivity implements TransUI {

    TextView txt;
    TextView tv_title;
    private PrintManager manager = null;
    private static boolean printTotals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        txt = (TextView) findViewById(R.id.output);
        txt.setText(R.string.printing);
        loading();

        manager = PrintManager.getmInstance(this, this);

        String typeReport;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                typeReport = null;
            } else {
                typeReport = extras.getString("typeReport");
            }
        } else {
            typeReport = (String) savedInstanceState.getSerializable("typeReport");
        }

        tv_title = (TextView) findViewById(R.id.textView_titleToolbar);
        tv_title.setText(Html.fromHtml("<h4>" + typeReport + "</h4>"));
        printAll(typeReport);
    }

    private void printAll(final String typeReport) {
        new Thread() {
            @Override
            public void run() {
                switch (typeReport) {
                    case ITEM_TEST:
                        manager.printParamInit();
                        break;
                    case ITEM_REPORTE_STIS:
                        //manager.printReportStis();
                        break;
                    case ITEM_REPORTE_EMV:
                        manager.printEMVAppCfg();
                        break;
                    case ITEM_REPORTE_TERMINAL:
                        manager.printConfigTerminal();
                        break;
                    case ITEM_ULTIMO_CIERRE:
                        if (manager.printLastSettle() == -1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UIUtils.toast(PrintParameter.this, R.drawable.ic_launcher_1, "NO EXISTEN" + "\n" + "TRANSACCIONES", Toast.LENGTH_SHORT);
                                }
                            });

                        }
                        break;
                    case ITEM_ANULACION:
                        manager.printVoidDatafast(false,false);
                        break;
                    case ITEM_REPORTE_DETALLADO:
                        //manager.printReportDatafast(false, false, isPrintTotals());
                        manager.printReportDatafast(isPrintTotals());
                        break;
                    case ITEM_BORRAR_LOTE:
                        manager.printReportDatafast(false, true, isPrintTotals(), true);
                        break;
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };

    private void loading() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView wv = (WebView) findViewById(R.id.wb_init_stis);
                wv.loadDataWithBaseURL(null, "<HTML><body bgcolor='#FFF'><div align=center>" +
                        "<img width=\"128\" height=\"128\" src='file:///android_asset/gif/load3.gif'/></div></body></html>", "text/html", "UTF-8", null);
            }
        });

    }

    @Override
    public InputInfo getOutsideInput(int timeout, InputManager.Mode type, String title) {
        return null;
    }

    @Override
    public CardInfo getCardUse(String msg, int timeout, int mode, String title) {
        return null;
    }

    @Override
    public CardInfo getCardUseAmount(String msg, int timeout, int mode, String title, String label, String amount) {
        return null;
    }

    @Override
    public CardInfo getCardUsePagosElect(String msg, int timeout, int mode, String title, String label, String amount, int minLen, int maxLen) {
        return null;
    }

    @Override
    public CardInfo getCardFallback(String msg, int timeout, int mode, String title) {
        return null;
    }

    @Override
    public QRCInfo getQRCInfo(int timeout, InputManager.Style mode) {
        return null;
    }

    @Override
    public PinInfo getPinpadOnlinePin(int timeout, String amount, String cardNo) {
        return null;
    }

    @Override
    public PinInfo getPinpadOfflinePin(int timeout, String amount, String cardNo) {
        return null;
    }

    @Override
    public int showCardConfirm(int timeout, String cn) {
        return 0;
    }

    @Override
    public InputInfo showMessageInfo(String title, String msg, String btnCancel, String btnConfirm, int timeout) {
        return null;
    }

    @Override
    public InputInfo showMessageImpresion(String title, String msg, String btnCancel, String btnConfirm, int timeout) {
        return null;
    }

    @Override
    public int showCardApplist(int timeout, String[] list) {
        return 0;
    }

    @Override
    public int showMultiLangs(int timeout, String[] langs) {
        return 0;
    }

    @Override
    public void handling(int timeout, int status) {

    }

    @Override
    public void handlingError(int timeout, int status) {

    }

    @Override
    public void handling(int timeout, int status, String title) {

    }

    @Override
    public void handlingInfo(int timeout, int status, String msg) {

    }

    @Override
    public int showTransInfo(int timeout, TransLogData logData) {
        return 0;
    }

    @Override
    public void trannSuccess(int timeout, int code, String... args) {

    }

    @Override
    public void showError(int timeout, int errcode) {

    }

    @Override
    public void showfinish() {

    }

    @Override
    public void showError(int timeout, int errcode, ProcessPPFail processPPFail) {

    }

    @Override
    public InputInfo showTypeCoin(int timeout, String title) {
        return null;
    }

    @Override
    public InputInfo showInputUser(int timeout, String title, String label, int min, int max) {
        return null;
    }

    @Override
    public void toasTrans(int errcode, boolean sound, boolean isErr) {

    }

    @Override
    public void toasTransReverse(int errcode, boolean sound, boolean isErr) {

    }

    @Override
    public void toasTrans(String errcode, boolean sound, boolean isErr) {
        
    }

    @Override
    public InputInfo showConfirmAmount(int timeout, String title, String label, String amnt, boolean isHTML) {
        return null;
    }

    @Override
    public void showMessage(String message, boolean transaccion) {

    }

    @Override
    public void showCardImg(String img) {
    }

    @Override
    public InputInfo showSignature(int timeout, String title, String transType) {
        return null;
    }

    @Override
    public InputInfo showList(int timeout, String title, String transType, final ArrayList<String> listMenu, int id) {
        return null;
    }

    @Override
    public InputInfo showInputPrompt(int timeout, String transType, String nameAcq, Prompt cls) {
        return null;
    }

    @Override
    public void showOfflinePinResult(int count) {

    }


    public static boolean isPrintTotals() {
        return printTotals;
    }

    public static void setPrintTotals(boolean printTotal) {
        printTotals = printTotal;
    }
}
