package cn.desert.newpos.payui.transrecord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.android.newpos.pay.R;
import com.datafast.inicializacion.prompts.Prompt;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.tools.CounterTimer;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.pinpad.OfflineRSA;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.device.scanner.QRCInfo;
import com.newpos.libpay.device.user.OnUserResultListener;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.PayApplication;

import static com.datafast.menus.menus.idAcquirer;

public class HistoryTrans extends Activity implements
        HistorylogAdapter.OnItemReprintClick, TransUI, View.OnClickListener {

    ListView lv_trans;
    View view_nodata;
    View view_reprint;
    EditText search_edit;
    ImageView search;
    LinearLayout z;
    LinearLayout root;
    ImageView close;
    CounterTimer counterTimer;

    private HistorylogAdapter adapter;
    private boolean isSearch = false;
    public static final String EVENTS = "EVENTS";
    public static final String LAST = "LAST";
    public static final String COMMON = "COMMON";
    public static final String ALL = "ALL";
    public static final String ALL_F_ACUM = "ALL_F_ACUM";
    public static final String ALL_F_REDEN = "ALL_F_REDEN";
    private boolean isCommonEvents = false;
    private PrintManager manager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        PayApplication.getInstance().addActivity(this);
        close = findViewById(R.id.iv_close);
        close.setVisibility(View.VISIBLE);
        close.setOnClickListener(this);
        lv_trans = findViewById(R.id.history_lv);
        view_nodata = findViewById(R.id.history_nodata);
        view_reprint = findViewById(R.id.reprint_process);
        search_edit = findViewById(R.id.history_search_edit);
        search = findViewById(R.id.history_search);
        z = findViewById(R.id.history_search_layout);
        root = findViewById(R.id.transaction_details_root);
        adapter = new HistorylogAdapter(this, this);
        lv_trans.setAdapter(adapter);
        view_reprint.setVisibility(View.GONE);
        search.setOnClickListener(new SearchListener());
        manager = PrintManager.getmInstance(this, this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String even = bundle.getString(HistoryTrans.EVENTS);
            switch (Objects.requireNonNull(even)) {
                case LAST:
                    re_print(TransLog.getInstance(idAcquirer).getLastTransLog());
                    break;
                case ALL:
                    printAll(ALL);
                    break;
                case ALL_F_ACUM:
                    printAll(ALL_F_ACUM);
                    break;
                case ALL_F_REDEN:
                    printAll(ALL_F_REDEN);
                    break;
                default:
                    isCommonEvents = true;
                    break;
            }
        }

        counterTimer = new CounterTimer(this);
        counterTimer.counterDownTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<TransLogData> list = TransLog.getInstance(idAcquirer).getData();
        List<TransLogData> temp = new ArrayList<>();
        int num = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            temp.add(num, list.get(i));
            num++;
        }
        if (list.size() > 0) {
            showView(false);
            adapter.setList(temp);
            adapter.notifyDataSetChanged();
            isSearch = true;
            search.setImageResource(android.R.drawable.ic_menu_search);
        } else {
            showView(true);
        }
    }

    private void showView(boolean isShow) {
        if (isShow) {
            lv_trans.setVisibility(View.GONE);
            view_nodata.setVisibility(View.VISIBLE);
        } else {
            lv_trans.setVisibility(View.VISIBLE);
            view_nodata.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnItemClick(String traceNO) {
        re_print(TransLog.getInstance(idAcquirer).searchTransLogByTraceNo(traceNO));
    }

    private void re_print(final TransLogData data) {
        //view_reprint.setVisibility(View.VISIBLE);
        //lv_trans.setVisibility(View.GONE);
        //z.setVisibility(View.GONE);
        final ProgressDialog progressDialog = new ProgressDialog(HistoryTrans.this);
        progressDialog.setMessage(getResources().getString(R.string.msg_re_print));
        progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                manager.print(data, false, true);
                mHandler.sendEmptyMessage(0);
                progressDialog.dismiss();
            }
        }.start();

    }

    private void printAll(final String key) {
        new Thread() {
            @Override
            public void run() {
                manager.selectPrintReport(key);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            view_reprint.setVisibility(View.GONE);
            lv_trans.setVisibility(View.VISIBLE);
            z.setVisibility(View.VISIBLE);
            if (!isCommonEvents) {
                finish();
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view.equals(close)) {
            finish();
        }
    }

    private final class SearchListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (isSearch) {
                String edit = ISOUtil.padleft(search_edit.getText().toString() + "", 6, '0');
                if (!PAYUtils.isNullWithTrim(edit)) {
                    TransLog transLog = TransLog.getInstance(idAcquirer);
                    TransLogData data = transLog.searchTransLogByTraceNo(edit);
                    if (data != null) {
                        InputMethodManager imm = (InputMethodManager) HistoryTrans.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(imm).hideSoftInputFromWindow(search_edit.getWindowToken(), 0);
                        List<TransLogData> list = new ArrayList<>();
                        list.add(0, data);
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                        search.setImageResource(android.R.drawable.ic_menu_revert);
                        isSearch = false;
                    } else {
                        UIUtils.toast(HistoryTrans.this, R.drawable.ic_launcher_1, HistoryTrans.this.getResources().getString(R.string.not_any_record), Toast.LENGTH_SHORT);
                        //Toast.makeText(HistoryTrans.this , HistoryTrans.this.getResources().getString(R.string.not_any_record), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                loadData();
            }
        }
    }

    @Override
    public void handling(int timeout, final int status) {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv_trans.setVisibility(View.GONE);
                z.setVisibility(View.GONE);
                view_reprint.setVisibility(View.VISIBLE);
                ((TextView) view_reprint.findViewById(R.id.handing_msginfo))
                        .setText(getResources().getString(R.string.re_print));
                ((WebView) findViewById(R.id.handling_loading)).
                        loadDataWithBaseURL(null, "<HTML><body bgcolor='#FFF'><div align=center>" +
                                "<img width=\"80\" height=\"80\" src='file:///android_asset/gif/load3.gif'/>" +
                                "</div></body></html>", "text/html", "UTF-8", null);
            }
        });*/
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
    public InputInfo getOutsideInput(int i, InputManager.Mode mode, String typeCoin) {
        return null;
    }

    @Override
    public CardInfo getCardUse(String i, int i1, int i2, String i3) {
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
    public QRCInfo getQRCInfo(int i, InputManager.Style style) {
        return null;
    }

    @Override
    public PinInfo getPinpadOnlinePin(int i, String s, String s1) {
        return null;
    }

    @Override
    public PinInfo getPinpadOfflinePin(int timeout, String amount, String cardNo) {
        return null;
    }

    @Override
    public PinInfo getPinpadOfflinePin(int timeout, int i, OfflineRSA key, int offlinecounts) {
        return null;
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
}
