package cn.desert.newpos.payui.master;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputListener;
import com.android.desert.keyboard.InputManager;
import com.android.newpos.pay.R;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.inicializacion.configuracioncomercio.Rango;
import com.datafast.inicializacion.init_emv.CAPK_ROW;
import com.datafast.inicializacion.init_emv.EMVAPP_ROW;
import com.datafast.inicializacion.prompts.ChequeoPromtsActivos;
import com.datafast.inicializacion.prompts.Prompt;
import com.datafast.server.activity.ServerTCP;
import com.datafast.server.server_tcp.Server;
import com.datafast.tools.InputManager2;
import com.datafast.tools.MenuApplicationsList;
import com.datafast.tools.WaitSelectApplicationsList;
import com.datafast.transactions.callbacks.waitResponseFallback;
import com.datafast.transactions.common.CommonFunctionalities;
import com.datafast.transactions.common.FormatAmount;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.PaySdkException;
import com.newpos.libpay.device.printer.PrintRes;
import com.newpos.libpay.device.user.OnUserResultListener;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.presenter.TransView;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;
import com.pos.device.printer.Printer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.PayApplication;

import static com.android.newpos.pay.StartAppDATAFAST.batteryStatus;
import static com.android.newpos.pay.StartAppDATAFAST.listPrompts;
import static com.android.newpos.pay.StartAppDATAFAST.paperStatus;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREAUTO;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREVOUCHER;
import static com.datafast.menus.menus.FALLBACK;
import static com.datafast.menus.menus.contFallback;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.server.activity.ServerTCP.listenerServer;
import static com.newpos.libpay.presenter.TransUIImpl.getStatusInfo;
import static com.newpos.libpay.trans.Trans.Type.ELECTRONIC;
import static com.newpos.libpay.trans.Trans.Type.ELECTRONIC_DEFERRED;
import static com.newpos.libpay.trans.Trans.idLote;
import static com.newpos.libpay.trans.finace.FinanceTrans.LOCAL;
import static com.newpos.libpay.trans.finace.FinanceTrans.ppResponse;

//import static com.datafast.menus.menus.acquirerRow;
//import static com.datafast.menus.menus.cardRow;
//import static com.datafast.menus.menus.issuerRow;

/**
 * Created by zhouqiang on 2017/7/3.
 */

public class MasterControl extends AppCompatActivity implements TransView, View.OnClickListener {

    Button btnConfirm;
    Button btnCancel;
    EditText editCardNO;
    EditText transInfo;
    EditText etNumToken;

    //Toolbar
    ImageView close;
    ImageView menu;
    TextView et_title;

    //Type Account
    RadioButton rb_mon1, rb_mon2;
    Button btnCancelTypeCoin, btnAcceptTypeCoin;
    String type_Coin = "1";

    //Input user
    TextView btnCancelInputUser, btnAcceptInputUser;
    EditText et_inputUser;
    TextView tv_inputUser;
    int Min_et_inputUser;
    int Max_et_inputUser;

    //Show message info
    Button btnCancelMsg, btnConfirmMsg;
    EditText et_MsgInfo;
    CountDownTimer countDownTimerGeneral;

    OnUserResultListener listener;

    String inputContent = "";

    public static String TRANS_KEY = "TRANS_KEY";
    public static boolean CTL_SIGN;
    public static String HOLDER_NAME;

    public static Context mcontext;
    //public static waitResponseVoid callback;
    public static waitResponseFallback callbackFallback;
    private CountDownTimer countDownTimer;
    private CountDownTimer countDownTimerImg;

    //Firma
    boolean isSignature;
    boolean isOnSignature;
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    CountDownTimer countDownTimerSignature;

    //prompt
    TextView tituloPrompt;
    EditText entradaDatos;
    Button btnCancelarPrompt, btnAceptarPrompt;
    private Prompt promptActual;

    //Tarjeta manual
    FloatingActionButton btnTarjetaManual;

    ProgressBar progressBar;

    public MasterControl() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PayApplication.getInstance().addActivity(this);

        CTL_SIGN = false;
        HOLDER_NAME = "";
        String type = getIntent().getStringExtra(TRANS_KEY);
        //startTrans(ch2en(type));

        //-----Para pruebas-----
        /*DebugInit.llenar_TCONF();
        DebugInit.llenarIp();
        DebugInit.llenarHostConfi();*/

        if (contFallback != FALLBACK) {

            switch (type) {
                case Trans.Type.SETTLE:
                case Trans.Type.ECHO_TEST:
                case Trans.Type.AUTO_SETTLE:
                    break;

                default:
                    //--------PENDIENTE REVISAR INIT EMV----------------

                    EMVAPP_ROW emvappRow = null;
                    emvappRow = EMVAPP_ROW.getSingletonInstance();
                    emvappRow.selectEMVAPP_ROW(MasterControl.this);

                    CAPK_ROW capkRow = null;
                    capkRow = CAPK_ROW.getSingletonInstance();
                    capkRow.selectCAPK_ROW(MasterControl.this);
                    //--------------------------------------------------

                    /*LoadEMV loadEMV = new LoadEMV();
                    loadEMV.loadAIDCAPK2EMVKernel();*/

                    break;
            }

            if (type.equals(Trans.Type.AUTO_SETTLE)) {
                startTrans(type);
            } else {
                switch (type) {
                    case Trans.Type.PREAUTO:
                    case Trans.Type.AMPLIACION:
                    case Trans.Type.VOID_PREAUTO:
                    case Trans.Type.REIMPRESION:
                        idAcquirer = idLote + FILE_NAME_PREAUTO;
                        break;
                    case Trans.Type.PREVOUCHER:
                        idAcquirer = idLote + FILE_NAME_PREVOUCHER;
                        break;
                    default:
                        idAcquirer = idLote;
                        break;
                }
                startTrans(type);
            }
        } else {
            if (idAcquirer != null)
                startTrans(type);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //PaySdk.getInstance().releaseCard();
    }

    @Override
    public void onClick(View view) {

        if (view.equals(close)) {
            if (mSignaturePad != null) {
                mSignaturePad.clear();
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/saved_signature/" + "signature.jpeg");
                if (file.exists()){
                    file.delete();
                }
            }
            contFallback = 0;
            listener.cancel();
        }

        if (view.equals(btnCancel)) {
            listener.cancel();
        }
        if (view.equals(btnConfirm)) {
            listener.confirm(InputManager.Style.COMMONINPUT);
        }

        //Type Account
        if (view.equals(btnCancelTypeCoin)) {
            listener.cancel();
        }
        if (view.equals(btnAcceptTypeCoin)) {
            inputContent = type_Coin;
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
        if (view.equals(rb_mon1)) {
            rb_mon1.setChecked(true);
            rb_mon2.setChecked(false);
            type_Coin = "1";
        }
        if (view.equals(rb_mon2)) {
            rb_mon1.setChecked(false);
            rb_mon2.setChecked(true);
            type_Coin = "2";
        }
        if (view.equals(btnCancelInputUser)) {
            listener.cancel();
        }

        //Show Message
        if (view.equals(btnConfirmMsg)) {
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
        if (view.equals(btnCancelMsg)) {
            listener.cancel();
        }

        //prompt
        if (view.equals(btnAceptarPrompt)) {

            if (promptActual.getTIPO_DATO().equals(Prompt.MONTO)){
                inputContent = FormatAmount.removeCharacter(entradaDatos.getText().toString(), "[$,.]","");
            }else {
                inputContent = entradaDatos.getText().toString();
            }

            if (entradaDatos.length() != 0) {

                if (entradaDatos.length() >= Integer.parseInt(promptActual.getLONGITUD_MINIMA())) {
                    hideKeyBoard(entradaDatos.getWindowToken());
                    listener.confirm(InputManager.Style.COMMONINPUT);
                }else{
                    UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, getString(R.string.longitud_invalida), Toast.LENGTH_SHORT);
                }
            } else {
                UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, getString(R.string.ingrese_dato), Toast.LENGTH_SHORT);
            }
        }
        if (view.equals(btnCancelarPrompt)) {
            listener.cancel();
        }

        //Tarjeta Manual
        if (view.equals(btnTarjetaManual)) {
            Snackbar.make(view, "Tarjeta Manual", Snackbar.LENGTH_LONG).show();
            inputContent = "MANUAL";
            //hideKeyBoard(entradaDatos.getWindowToken());
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
    }

    @Override
    public void showCardView(final String msg, final int timeout, final int mode, final String title, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_handling);
                close = (ImageView) findViewById(R.id.iv_close);
                close.setVisibility(View.VISIBLE);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                EditText edtInvisible = (EditText) findViewById(R.id.edtInvisible);

                btnTarjetaManual = (FloatingActionButton) findViewById(R.id.btn_tarjeta_manual);

                edtInvisible.requestFocus();
                edtInvisible.setInputType(InputType.TYPE_NULL);
                edtInvisible.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            listener.cancel();
                        }
                        return false;
                    }
                });

                LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.LinearTimeout);
                relativeLayout.setVisibility(View.VISIBLE);
                final TextView textViewTitle = (TextView) findViewById(R.id.textView_cont);
                runTimeGeneral(textViewTitle, timeout);

                try {
                    et_title.setText(title.replace("_", " "));
                } catch (Exception e){
                    et_title.setText("");
                }

                close.setOnClickListener(MasterControl.this);

                if ((mode & FinanceTrans.INMODE_HAND) != 0) {
                    btnTarjetaManual.setOnClickListener(MasterControl.this);
                } else {
                    btnTarjetaManual.setVisibility(View.INVISIBLE);
                }

                showHanding(msg);

                deleteTimer();
                //counterDownTimer(timeout);
            }
        });
    }

    @Override
    public void showCardViewAmount(final String msg, final int timeout, final int mode, final String title,final String label, final String amnt, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.confirm_amount_card);
                close = (ImageView) findViewById(R.id.iv_close);
                close.setVisibility(View.VISIBLE);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                btnCancel = (Button) findViewById(R.id.btn_cancel_mon);
                btnConfirm = (Button) findViewById(R.id.btn_conf_mon);
                EditText edtInvisible = (EditText) findViewById(R.id.edtInvisible);

                TextView total = (TextView) findViewById(R.id.monto_display_area);

                LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.LinearTimeout);
                relativeLayout.setVisibility(View.VISIBLE);
                final TextView textViewTitle = (TextView) findViewById(R.id.textView_cont);
                runTimeGeneral(textViewTitle, timeout);


                btnTarjetaManual = (FloatingActionButton) findViewById(R.id.btn_tarjeta_manual);

                edtInvisible.requestFocus();
                edtInvisible.setInputType(InputType.TYPE_NULL);
                edtInvisible.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            contFallback = 0;
                            listener.cancel();
                        }
                        return false;
                    }
                });

                try {
                    et_title.setText(title.replace("_", " "));
                } catch (Exception e){
                    et_title.setText("");
                }

                close.setOnClickListener(MasterControl.this);
                /*btnCancel.setOnClickListener(MasterControl.this);
                btnConfirm.setOnClickListener(MasterControl.this);*/

                if ((mode & FinanceTrans.INMODE_HAND) != 0) {
                    btnTarjetaManual.setOnClickListener(MasterControl.this);
                } else {
                    btnTarjetaManual.setVisibility(View.INVISIBLE);
                }
                total.setText(label + " " + amnt);

                showHanding(msg);

                deleteTimer();
                //counterDownTimer(timeout);
            }
        });
    }

    @Override
    public void showCardPagosElect(final String msg, final int timeout, int mode, final String title, final String label, final String amnt, final int minLen, final int maxLen, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.confirm_amount_card_pagos_elect);
                close = (ImageView) findViewById(R.id.iv_close);
                close.setVisibility(View.VISIBLE);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                btnCancel = (Button) findViewById(R.id.btn_cancel_pagos);
                btnConfirm = (Button) findViewById(R.id.btn_confirm_pagos);
                etNumToken = (EditText) findViewById(R.id.TxtToken);

                etNumToken.setHint("INGRESE " + msg.substring(msg.indexOf("|") + 1));
                etNumToken.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLen)});

                TextView total = (TextView) findViewById(R.id.monto_display_area);

                LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.LinearTimeout);
                relativeLayout.setVisibility(View.VISIBLE);
                final TextView textViewTitle = (TextView) findViewById(R.id.textView_cont);
                runTimeGeneral(textViewTitle, timeout);

                //btnTarjetaManual = (FloatingActionButton) findViewById(R.id.btn_tarjeta_manual);
                try {
                    String temporal = title.replace("_"," ");
                    et_title.setText( temporal.substring(0,temporal.indexOf(" ")) + "\n" + temporal.substring(temporal.indexOf(" ")) );
                } catch (Exception e){
                    et_title.setText("");
                }
                close.setOnClickListener(MasterControl.this);

                etNumToken.requestFocus();

                etNumToken.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            listener.cancel();
                        }
                        return false;
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etNumToken.length() < minLen){
                            UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, getString(R.string.longitud_invalida), Toast.LENGTH_SHORT);
                        } else {
                            inputContent = "MANUAL|" + etNumToken.getText().toString();
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    }
                });

                //btnTarjetaManual.setOnClickListener(MasterControl.this);

                total.setText(label + " " + amnt);

                showHanding(msg.substring(0, msg.indexOf("|")));

                deleteTimer();
            }
        });
    }


    @Override
    public void showQRCView(int timeout, InputManager.Style mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_qrc);
            }
        });
    }

    @Override
    public void showCardNo(final int timeout, final String pan, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_cardno);
                //showConfirmCardNO(PAYUtils.getSecurityNum(pan, 6, 3));
                showConfirmCardNO(pan);

                deleteTimer();
                //counterDownTimer(timeout);
            }
        });
    }

    @Override
    public void showMessageInfo(final String title, final String msg, final String btnCancel, final String btnConfirm, final int timeout, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_cardno);

                close = (ImageView) findViewById(R.id.iv_close);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                et_MsgInfo = (EditText) findViewById(R.id.cardno_display_area);
                btnCancelMsg = (Button) findViewById(R.id.cardno_cancel);
                btnConfirmMsg = (Button) findViewById(R.id.cardno_confirm);

                close.setVisibility(View.VISIBLE);
                et_title.setText(title);
                et_MsgInfo.setText(msg);
                btnCancelMsg.setText(btnCancel);
                btnConfirmMsg.setText(btnConfirm);

                close.setOnClickListener(MasterControl.this);
                btnCancelMsg.setOnClickListener(MasterControl.this);
                btnConfirmMsg.setOnClickListener(MasterControl.this);

                counterDownTimer(timeout, "Tiempo de espera de ingreso de datos agotado",true);
            }
        });
    }

    @Override
    public void showMessageImpresion(final String title, final String msg, final String btnCancel, final String btnConfirm, final int timeout, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_cardno);

                close = (ImageView) findViewById(R.id.iv_close);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                et_MsgInfo = (EditText) findViewById(R.id.cardno_display_area);
                btnCancelMsg = (Button) findViewById(R.id.cardno_cancel);
                btnConfirmMsg = (Button) findViewById(R.id.cardno_confirm);

                close.setVisibility(View.VISIBLE);
                et_title.setText(title);
                et_MsgInfo.setText(msg);
                btnCancelMsg.setText(btnCancel);
                btnConfirmMsg.setText(btnConfirm);

                close.setOnClickListener(MasterControl.this);
                btnCancelMsg.setOnClickListener(MasterControl.this);
                btnConfirmMsg.setOnClickListener(MasterControl.this);

                counterDownTimer(timeout, "",false);
            }
        });
    }


    @Override
    public void showInputView(final int timeout, final InputManager.Mode mode, OnUserResultListener l, final String title) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //InputManager inputManager = new InputManager(MasterControl.this) ;
                InputManager2 inputManager = new InputManager2(MasterControl.this);
                inputManager.setListener(new InputListener() {
                    @Override
                    public void callback(InputInfo inputInfo) {
                        if (inputInfo.isResultFlag()) {
                            inputContent = inputInfo.getResult();
                            listener.confirm(inputInfo.getNextStyle());
                        } else {
                            listener.cancel();
                        }
                    }
                });
                if (Locale.getDefault().getLanguage().equals("zh")) {
                    inputManager.setLang(InputManager.Lang.CH);
                } else {
                    inputManager.setLang(InputManager.Lang.EN);
                }

                if (mode == InputManager.Mode.AMOUNT) {
                    //inputManager.setTitle(getString(R.string.please_input_amount) + typeCoin);
                    inputManager.setTitle(title);
                }
                if (mode == InputManager.Mode.PASSWORD) {
                    //inputManager.setTitle(R.string.please_input_master_pass);
                    inputManager.setTitle(title);
                }
                if (mode == InputManager.Mode.VOUCHER) {
                    //inputManager.setTitle(R.string.please_input_trace_no);
                    inputManager.setTitle(title);
                }
                if (mode == InputManager.Mode.AUTHCODE) {
                    inputManager.setTitle(R.string.please_input_auth_code);
                }
                if (mode == InputManager.Mode.DATETIME) {
                    inputManager.setTitle(R.string.please_input_data_time);
                }
                if (mode == InputManager.Mode.REFERENCE) {
                    //inputManager.setTitle(R.string.please_input_reference);
                    inputManager.setTitle(title);
                }

                if (mode == InputManager.Mode.VOUCHER && title.equals(DefinesDATAFAST.TITULO_ANULACION)) {
                    inputManager.addEdit(mode, 6);
                } else {
                    inputManager.addEdit(mode);
                }

                if (mode == InputManager.Mode.PASSWORD) {
                    inputManager.addKeyboard(true);
                } else {
                    inputManager.addKeyboard(false);
                }

                inputManager.addStyles();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                setContentView(inputManager.getView(true));

                counterDownTimer(timeout, "Tiempo de espera de ingreso de datos agotado",true);
            }
        });
    }

    @Override
    public String getInput(InputManager.Mode type) {
        return inputContent;
    }

    @Override
    public void showTransInfoView(final int timeout, final TransLogData data, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_transinfo);
                TextView tv_title = (TextView) findViewById(R.id.title_find_result);
                showOrignalTransInfo(data);
                if (data.getEName().equals(Trans.Type.PREVOUCHER)) {
                    tv_title.setText("PAGAR PREVOUCHER");
                }
                counterDownTimer(timeout, "Tiempo de espera de confirmacion de datos agotado",true);
            }
        });
    }

    @Override
    public void showCardAppListView(int timeout, final String[] apps, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MenuApplicationsList applicationsList = new MenuApplicationsList(MasterControl.this);
                applicationsList.menuApplicationsList(apps, new WaitSelectApplicationsList() {
                    @Override
                    public void getAppListSelect(int idApp) {
                        listener.confirm(idApp);
                    }
                });
            }
        });
    }

    @Override
    public void showMultiLangView(int timeout, String[] langs, OnUserResultListener l) {
        this.listener = l;
    }

    @Override
    public void showSuccess(int timeout, final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (ppResponse != null && (Server.cmd.equals(CT) || Server.cmd.equals(LT) || info.contains("ANULACION") || Server.cmd.equals("PP_REVERSE"))) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listenerServer.waitRspHost(ppResponse);
                            alreadySend = false;
                        }
                    }, 2000);

                    alreadySend = true;

                }

                //UIUtils.toast(MasterControl.this , info);
                UIUtils.startResult(MasterControl.this, true, info);
                deleteTimer();
            }
        });
    }

    public static boolean alreadySend = false;
    @Override
    public void showError(int timeout, final String err, final boolean pp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (ppResponse != null && pp && !alreadySend) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listenerServer.waitRspHost(ppResponse);
                            alreadySend = false;
                        }
                    }, 2000);

                    alreadySend = true;

                }

                UIUtils.startResult(MasterControl.this, false, err);
                deleteTimer();
            }
        });
    }

    @Override
    public void showMsgInfo(final int timeout, final String status, final boolean transaccion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_handling);
                close = (ImageView) findViewById(R.id.iv_close);
                close.setOnClickListener(MasterControl.this);
                btnTarjetaManual = (FloatingActionButton) findViewById(R.id.btn_tarjeta_manual);
                btnTarjetaManual.setVisibility(View.INVISIBLE);
                progressBar = findViewById(R.id.progress);

                LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.LinearTimeout);
                if (status.equals(getStatusInfo(String.valueOf(Tcode.Status.process_trans)))){
                    relativeLayout.setVisibility(View.VISIBLE);
                    final TextView textViewTitle = (TextView) findViewById(R.id.textView_cont);
                    runTimeGeneral(textViewTitle, timeout);
                }else {
                    relativeLayout.setVisibility(View.GONE);
                }

                if (transaccion){
                    progressBar.setVisibility(View.INVISIBLE);
                }

                if (transaccion && ppResponse != null && !alreadySend){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listenerServer.waitRspHost(ppResponse);
                            alreadySend = false;
                        }
                    }, 2000);
                    alreadySend = true;

                }

                showHanding(status);

                deleteTimer();
                //counterDownTimer(timeout);
            }
        });
    }

    @Override
    public void showMsgInfo(final int timeout, final String status, final String title, final boolean transaccion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_handling);
                close = (ImageView) findViewById(R.id.iv_close);
                close.setOnClickListener(MasterControl.this);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                btnTarjetaManual = (FloatingActionButton) findViewById(R.id.btn_tarjeta_manual);
                btnTarjetaManual.setVisibility(View.INVISIBLE);
                progressBar = findViewById(R.id.progress);

                if (transaccion){
                    progressBar.setVisibility(View.INVISIBLE);
                }

                try {
                    et_title.setText(title.replace("_", " "));
                }catch (Exception e){
                    et_title.setText("");
                }

                showHanding(status);

                deleteTimer();
                //counterDownTimer(timeout);
            }
        });
    }

    @Override
    public void showTypeCoinView(final int timeout, final String title, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_menu_tipo_moneda);
                rb_mon1 = (RadioButton) findViewById(R.id.rb_moneda1);
                rb_mon2 = (RadioButton) findViewById(R.id.rb_moneda2);
                btnCancelTypeCoin = (Button) findViewById(R.id.btn_cancel_mon);
                btnAcceptTypeCoin = (Button) findViewById(R.id.btn_conf_mon);

                rb_mon1.setOnClickListener(MasterControl.this);
                rb_mon2.setOnClickListener(MasterControl.this);
                btnCancelTypeCoin.setOnClickListener(MasterControl.this);
                btnAcceptTypeCoin.setOnClickListener(MasterControl.this);

                rb_mon1.setChecked(true);
                setToolbar(title);
            }
        });
    }

    @Override
    public void showInputUser(final int timeout, final String title, final String label, final int min, final int max, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_input_user);

                close = (ImageView) findViewById(R.id.iv_close);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                close.setVisibility(View.VISIBLE);
                try {
                    et_title.setText(title.replace("_", " "));
                } catch (Exception e){
                    et_title.setText("");
                }

                LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.LinearTimeout);
                relativeLayout.setVisibility(View.VISIBLE);
                final TextView textViewTitle = (TextView) findViewById(R.id.textView_cont);
                runTimeGeneral(textViewTitle, timeout);

                Min_et_inputUser = min;
                Max_et_inputUser = max;

                et_inputUser = (EditText) findViewById(R.id.editText_input);
                et_inputUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                tv_inputUser = (TextView) findViewById(R.id.textView_title);
                btnCancelInputUser = (TextView) findViewById(R.id.last4_cancel);
                btnAcceptInputUser = (TextView) findViewById(R.id.last4_confirm);
                //et_inputUser.setInputType(InputType.TYPE_NULL);
                et_inputUser.requestFocus();

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(et_inputUser, InputMethodManager.SHOW_IMPLICIT);

                et_inputUser.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            listener.cancel();
                        }
                        return false;
                    }
                });

                btnAcceptInputUser.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        InputMethodManager imm = (InputMethodManager) MasterControl.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_inputUser.getWindowToken(), 0);

                        if (!et_inputUser.getText().toString().equals("")){
                            if (et_inputUser.length() < Min_et_inputUser){
                                UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, getString(R.string.longitud_invalida), Toast.LENGTH_SHORT);
                            } else {
                                inputContent = et_inputUser.getText().toString();
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }
                        }
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) MasterControl.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_inputUser.getWindowToken(), 0);
                        listener.cancel();
                    }
                });
                //btnAcceptInputUser.setOnClickListener(MasterControl.this);
                btnCancelInputUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) MasterControl.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_inputUser.getWindowToken(), 0);
                        listener.cancel();
                    }
                });

                tv_inputUser.setText(label);

                counterDownTimer(timeout, "Tiempo de espera de ingreso de datos agotado",true);
            }
        });
    }

    @Override
    public void toasTransView(final String errcode, final boolean sound) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sound) {
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);
                    toneG.stopTone();
                }
                UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, errcode, Toast.LENGTH_SHORT);
            }
        });

    }

    @Override
    public void toasTransViewReverse(final String errcode, final boolean sound) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sound) {
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);
                    toneG.stopTone();
                }
                //UIUtils.toastReverse(MasterControl.this, R.drawable.ic_launcher_1, errcode, Toast.LENGTH_SHORT);
            }
        });

    }

    @Override
    public void showConfirmAmountView(final int timeout, final String title, final String label, final String amnt, final boolean isHTML, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_confirm_amount);

                close = (ImageView) findViewById(R.id.iv_close);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                btnCancel = (Button) findViewById(R.id.btn_cancel_mon);
                btnConfirm = (Button) findViewById(R.id.btn_conf_mon);

                close.setVisibility(View.VISIBLE);
                try {
                    et_title.setText(title.replace("_", " "));
                }catch (Exception e){
                    et_title.setText("");
                }
                close.setOnClickListener(MasterControl.this);
                btnCancel.setOnClickListener(MasterControl.this);
                btnConfirm.setOnClickListener(MasterControl.this);

                EditText total = (EditText) findViewById(R.id.monto_display_area);
                
                if (isHTML)
                    total.setText(Html.fromHtml(label + " " + amnt));
                else
                    total.setText(label + " " + amnt);

                counterDownTimer(timeout, "Tiempo de espera de ingreso de datos agotado",true);
            }
        });
    }

    @Override
    public void showSignatureView(final int timeout, OnUserResultListener l, final String title, final String transType) {
        this.listener = l;
        isSignature = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_signature);
                deleteTimer();
                final TextView textViewTitle = (TextView) findViewById(R.id.textView_cont);
                final EditText editText_cedula = (EditText) findViewById(R.id.editText_cedula);
                final EditText editText_telefono = (EditText) findViewById(R.id.editText_telefono);
                close = (ImageView) findViewById(R.id.iv_close);
                close.setOnClickListener(MasterControl.this);
                editText_cedula.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText_telefono.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText_cedula.requestFocus();

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                runTime(textViewTitle);
                mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
                mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                    @Override
                    public void onStartSigning() {
                        isOnSignature = true;
                    }

                    @Override
                    public void onSigned() {
                        mClearButton.setEnabled(true);
                    }

                    @Override
                    public void onClear() {
                        runTime(textViewTitle);
                        mClearButton.setEnabled(false);
                        isOnSignature = false;
                    }
                });
                mClearButton = (Button) findViewById(R.id.clear_button);
                mSaveButton = (Button) findViewById(R.id.save_button);
                mSaveButton.setEnabled(true);
                mClearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSignaturePad.clear();
                    }
                });

                editText_cedula.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            listener.cancel();
                        }
                        return false;
                    }
                });

                editText_telefono.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK){
                            listener.cancel();
                        }
                        return false;
                    }
                });

                mSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText_cedula.getText().toString().trim().length() > 5 && isOnSignature) {
                            Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                            saveImage(signatureBitmap);
                            File file = new File(Environment.getExternalStorageDirectory().toString() + "/saved_signature/" + "signature.jpeg");
                            System.out.println("FIRMA " + file.length());
                            if (file.length() >= 8500) {
                                UIUtils.showAlertDialog("Informacion","Ingrese una firma más pequeña", MasterControl.this);
                                mSignaturePad.clear();
                            } else {
                                countDownTimerSignature.cancel();
                                inputContent = editText_cedula.getText().toString() + ";" + editText_telefono.getText().toString();
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }

                        } else if (!isOnSignature) {
                            UIUtils.showAlertDialog("Informacion","Debe ingresar firma", MasterControl.this);
                        } else if (editText_cedula.getText().toString().trim().length() <= 5) {
                            UIUtils.showAlertDialog("Informacion","Debe ingresar cédula", MasterControl.this);
                        }
                    }
                });

            }
        });

    }

    private void runTime(final TextView textViewTitle) {
        if (countDownTimerSignature != null) {
            countDownTimerSignature.cancel();
            countDownTimerSignature = null;
        }
        final int[] i = {120};
        countDownTimerSignature = new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                textViewTitle.setText(i[0]-- + "");
            }

            public void onFinish() {
                countDownTimerSignature.cancel();
                inputContent = "false";
                listener.confirm(InputManager.Style.COMMONINPUT);
            }
        }.start();
    }

    final void saveImage(Bitmap signature) {

        String root = Environment.getExternalStorageDirectory().toString();

        // the directory where the signature will be saved
        File myDir = new File(root + "/saved_signature");

        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        // set the file name of your choice
        String fname = "signature.jpeg";

        // in our case, we delete the previous file, you can remove this
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        try {

            // save the signature
            FileOutputStream out = new FileOutputStream(file);
            signature.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();

            //Toast.makeText(this.getContext(), "Firma guardada.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView(final int timeout, OnUserResultListener l, final String title, final String transType, final ArrayList<String> listMenu, final int id) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.frag_show_list);
                close = (ImageView) findViewById(R.id.iv_close);
                et_title = (TextView) findViewById(R.id.textView_titleToolbar);
                menu = (ImageView) findViewById(R.id.iv_menus);

                close.setVisibility(View.VISIBLE);
                menu.setImageResource(id);

                try {
                    et_title.setText(title.replace("_", " "));
                }catch (Exception e){
                    et_title.setText("");
                }

                initList(transType, listMenu);

                close.setOnClickListener(MasterControl.this);

                counterDownTimer(timeout, "Tiempo de espera de ingreso de datos agotado",true);
            }
        });

    }

    @Override
    public void showInputPromptView(final int timeout, final String transType, String nameAcq, final Prompt cls, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_interfaz_prompt);

                et_title = (TextView) findViewById(R.id.textView_titleToolbar);

                try {
                    et_title.setText(transType.replace("_", " "));
                } catch (Exception e){
                    et_title.setText("");
                }

                tituloPrompt = (TextView) findViewById(R.id.tv_nombre_prompt);
                entradaDatos = (EditText) findViewById(R.id.et_input);
                btnCancelarPrompt = (Button) findViewById(R.id.btn_cancelar);
                //btnAceptarPrompt = (Button) findViewById(R.id.btn_aceptar);

                btnCancelarPrompt.setOnClickListener(MasterControl.this);
                //btnAceptarPrompt.setOnClickListener(MasterControl.this);

                promptActual = null;

                if (cls != null) {
                    promptActual = cls;

                    tituloPrompt.setText(promptActual.getNOMBRE_PROMPTS());
                    int lonMax = Integer.parseInt(promptActual.getLONGITUD_MAXIMA());

                    entradaDatos.setInputType(CommonFunctionalities.tipoEntrada(promptActual.getTIPO_DATO()));
                    entradaDatos.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lonMax)});

                    if (promptActual.getTIPO_DATO().equals(Prompt.MONTO)){
                        entradaDatos.setText("$0.00");
                        entradaDatos.addTextChangedListener(new FormatAmount(entradaDatos));
                    }

                    entradaDatos.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            InputMethodManager imm = (InputMethodManager) MasterControl.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(entradaDatos.getWindowToken(), 0);

                            if (!entradaDatos.getText().toString().equals("")){

                                if (promptActual.getTIPO_DATO().equals(Prompt.MONTO)){
                                    inputContent = FormatAmount.removeCharacter(entradaDatos.getText().toString(), "[$,.]","");
                                }else {
                                    inputContent = entradaDatos.getText().toString();
                                }

                                if (entradaDatos.length() != 0) {

                                    if (entradaDatos.length() >= Integer.parseInt(promptActual.getLONGITUD_MINIMA())) {
                                        listener.confirm(InputManager.Style.COMMONINPUT);
                                    }else{
                                        UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, getString(R.string.longitud_invalida), Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, getString(R.string.ingrese_dato), Toast.LENGTH_SHORT);
                                }
                            }
                        }
                    });
                }

                deleteTimer();
                //counterDownTimer(timeout);
            }
        });
    }

    @Override
    public void showOfflinePIN(final int counts) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (counts == 0) {
                    UIUtils.toast(MasterControl.this , true, "Verify Offline PIN Successfully");
                } else if (counts == 1) {
                    UIUtils.toast(MasterControl.this , false , "You've only got last opportunity");
                } else {
                    UIUtils.toast(MasterControl.this , false , "You've only got" +counts+ "opportunities");
                }
            }
        });
    }

    @Override
    public void showfinishview() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkCardPresent();
            }
        }, 2000);
    }

    private void checkCardPresent() {
        final IccReader iccReader0;
        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 1000);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView removeCard ;
                if (iccReader0.isCardPresent()) {
                    setContentView(R.layout.activity_remove_card);
                    removeCard = (ImageView) findViewById(R.id.iv_remove__card);
                    removeCard.setImageResource(R.drawable.remove_card);
                }
            }
        });

        do {
            if (iccReader0.isCardPresent()){
                try{
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2,2000);
                    Thread.sleep(2000);
                }catch (Exception e){
                    Log.e("Error", ""+e);
                    break;
                }
            } else {
                break;
            }
        } while (iccReader0.isCardPresent());

        //finish();
        startActivity( new Intent(MasterControl.this, ServerTCP.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void initList(String transType, final ArrayList<String> listMenu) {
        final ListView listview = (ListView) findViewById(R.id.simpleListView);
        ArrayList<String> list = new ArrayList<>();
        list = listMenu;
        list.add("");

        final StableArrayAdapter adapter = new StableArrayAdapter(MasterControl.this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(500).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                if (!item.equals("")) {
                                    inputContent = item;
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                }

                            }
                        });
            }

        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        StableArrayAdapter(Context context, int textViewResourceId,
                           List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }


    private void startTrans(String type) {
        try {
            if ((batteryStatus.getLevelBattery() <= 8) && (!batteryStatus.isCharging()) && (!type.equals(Trans.Type.ECHO_TEST))) {
                UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, DefinesDATAFAST.MSG_BATTERY, Toast.LENGTH_SHORT);
                finish();
            } else if ( paperStatus.getRet() == Printer.PRINTER_STATUS_PAPER_LACK && (!type.equals(Trans.Type.ECHO_TEST)) ){
                UIUtils.toast(MasterControl.this, R.drawable.ic_launcher_1, DefinesDATAFAST.MSG_PAPER, Toast.LENGTH_SHORT);
                finish();
            }else {
                PaySdk.getInstance().startTrans(type, this);
            }
        } catch (PaySdkException e) {
            Logger.error("Exception" + e.toString());
        }
    }

    public static String ch2en(String ch) {
        String[] chs = PrintRes.TRANSCH;
        int index = 0;
        for (int i = 0; i < chs.length; i++) {
            if (chs[i].equals(ch)) {
                index = i;
            }
        }
        return PrintRes.TRANSEN[index];
    }

    public static String en2ch(String en) {
        String[] chs = PrintRes.TRANSEN;
        int index = 0;
        for (int i = 0; i < chs.length; i++) {
            if (chs[i].equals(en)) {
                index = i;
            }
        }
        return PrintRes.TRANSCH[index];
    }

    private void showConfirmCardNO(String pan) {
        btnConfirm = (Button) findViewById(R.id.cardno_confirm);
        btnCancel = (Button) findViewById(R.id.cardno_cancel);
        editCardNO = (EditText) findViewById(R.id.cardno_display_area);
        ImageView iv = (ImageView) findViewById(R.id.trans_cardno_iv);
        iv.setImageBitmap(PAYUtils.getLogoByBankId(this, TMConfig.getInstance().getBankid()));
        btnCancel.setOnClickListener(MasterControl.this);
        btnConfirm.setOnClickListener(MasterControl.this);
        editCardNO.setText(pan);
    }

    private void showOrignalTransInfo(TransLogData data) {
        btnConfirm = (Button) findViewById(R.id.transinfo_confirm);
        btnCancel = (Button) findViewById(R.id.transinfo_cancel);
        btnCancel.setOnClickListener(MasterControl.this);
        btnConfirm.setOnClickListener(MasterControl.this);
        transInfo = (EditText) findViewById(R.id.transinfo_display_area);

        StringBuilder info = new StringBuilder();

        info.append("<b>" + getString(R.string.void_original_trans) + "</b>" + " ");
        if (data.getEName().equals(ELECTRONIC_DEFERRED))//Ajuste Visual
            info.append(ELECTRONIC.replace("_", " "));
        else
            info.append(data.getEName().replace("_", " "));
        info.append("<br/>");
        info.append("<b>" + getString(R.string.void_card_no) + "</b>");
        info.append(" ");
        info.append(data.getPan() + "<br/>");
        info.append("<b>" + getString(R.string.void_trace_no) + "</b>");
        info.append(" ");
        info.append(data.getTraceNo() + "<br/>");
        if (!PAYUtils.isNullWithTrim(data.getAuthCode())) {
            info.append("<b>" + getString(R.string.void_auth_code) + "</b>");
            info.append(" ");
            info.append(data.getAuthCode() + "<br/>");
        }
        info.append("<b>" + getString(R.string.void_batch_no) + "</b>");
        info.append(" ");
        info.append(data.getBatchNo() + "<br/>");

        if (data.getTypeCoin().equals(LOCAL)) {
            info.append("<b>" + getString(R.string.void_amount) + "</b>");
            info.append(" $. ");
            info.append(PAYUtils.getStrAmount(data.getAmount()) + "<br/>");
        } else {
            info.append("<b>" + getString(R.string.void_amount) + "</b>");
            info.append(" $ ");
            info.append(PAYUtils.getStrAmount(data.getAmount()) + "<br/>");
        }
        info.append("<b>" + getString(R.string.void_time) + "</b>");
        info.append(" ");
        info.append(PAYUtils.printStr(data.getLocalDate(), data.getLocalTime()));

        /*String info = "<b>" + getString(R.string.void_original_trans) + "</b>" + " " + data.getEName().replace("_", " ") + "<br/>";
        info += "<b>" + getString(R.string.void_card_no) + "</b>" + " " + data.getPan() + "<br/>";

        info += "<b>" + getString(R.string.void_trace_no) + "</b>" + " " + data.getTraceNo() + "<br/>";

        if (!PAYUtils.isNullWithTrim(data.getAuthCode())) {
            info += "<b>" + getString(R.string.void_auth_code) + "</b>" + " " + data.getAuthCode() + "<br/>";
        }
        info += "<b>" + getString(R.string.void_batch_no) + "</b>" + " " + data.getBatchNo() + "<br/>";

        if (data.getTypeCoin().equals(LOCAL)) {
            info += "<b>" + getString(R.string.void_amount) + "</b>" + " $. " + PAYUtils.getStrAmount(data.getAmount()) + "<br/>";
            *//*if (ISOUtil.stringToBoolean(tconf.getHABILITAR_PROPINA()))
                info += "<b>" + getString(R.string.void_tip) + "</b>" + " DOLAR. " + PAYUtils.getStrAmount(data.getTipAmout()) + "<br/>";*//*
        } else {
            info += "<b>" + getString(R.string.void_amount) + "</b>" + " $ " + PAYUtils.getStrAmount(data.getAmount()) + "<br/>";
            *//*if (ISOUtil.stringToBoolean(tconf.getHABILITAR_PROPINA()))
                info += "<b>" + getString(R.string.void_tip) + "</b>" + " DOLAR " + PAYUtils.getStrAmount(data.getTipAmout()) + "<br/>";*//*
        }
        info += "<b>" + getString(R.string.void_time) + "</b>" + " " + PAYUtils.printStr(data.getLocalDate(), data.getLocalTime());*/

        transInfo.setText(Html.fromHtml(info.toString()));
    }

    private void showHanding(String msg) {
        TextView tv = (TextView) findViewById(R.id.handing_msginfo);
        tv.setText(msg);
//        WebView wv = (WebView) findViewById(R.id.handling_loading);
//        wv.loadDataWithBaseURL(null, "<HTML><body bgcolor='#FFF'><div align=center>" +
//                "<img width=\"80\" height=\"80\" src='file:///android_asset/gif/load3.gif'/></div></body></html>", "text/html", "UTF-8", null);
    }

    private void setToolbar(String titleToolbar) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        String title = "<h4>" + titleToolbar + "</h4>";
        toolbar.setTitle(Html.fromHtml(title));
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                setSupportActionBar(toolbar);
            }
        }, 0);
    }

    private void hideKeyBoard(IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        //    UIUtils.startResult(MasterControl.this, false, "Operacion cancelada por el usuario");
    }

    /**
     * Check card exist in table.
     *
     * @param cardNum Numero Tarjeta
     * @return return
     */
    public static boolean incardTable(String cardNum, String tipoTrans) {

        if (cardNum == null)
            return false;

        if (cardNum.length()< 10)
            return false;

        if(Server.cmd.equals(CT))
            return true;

        String pan = cardNum.substring(0, 10);

        if (!Rango.inCardTableACQ(tipoTrans, pan, rango, "",mcontext)) {
            System.out.println("No se encontraron parametros");
            return false;
        }

        return true;
    }

    /**
     * Check card exist in table,
     * only for PE Wallet
     *
     * @param cardNum Numero Tarjeta
     * @return return
     */
    public static boolean incardTable(String cardNum, String tipoTrans, String Wallet) {

        if (cardNum == null)
            return false;

        if (cardNum.length()< 10)
            return false;

        String pan = cardNum.substring(0, 10);

        if (!Rango.inCardTableACQ(tipoTrans, pan, rango, Wallet, mcontext)) {
            System.out.println("No se encontraron parametros");
            return false;
        }

        return true;
    }

    public static void llenarPrompts(String idTconf, String idOtherTable){
        listPrompts = new ArrayList<>();
        listPrompts = ChequeoPromtsActivos.GetPrompts(idTconf, idOtherTable, mcontext);
        if (listPrompts == null) {
            listPrompts = new ArrayList<>();
            listPrompts.clear();
        } else  if (listPrompts.isEmpty())
            listPrompts.clear();
    }

    public static void llenarPrompts(String idTconf){
        listPrompts = new ArrayList<>();
        listPrompts = ChequeoPromtsActivos.GetPrompts(idTconf, mcontext);
        if (listPrompts == null) {
            listPrompts = new ArrayList<>();
            listPrompts.clear();
        } else  if (listPrompts.isEmpty())
            listPrompts.clear();
    }

    @Override
    public void showCardViewImg(final String img, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_card_image);
                loadWebGifImg(img);
                countDownTimerImg = new CountDownTimer(1000, 500) {
                    public void onTick(long millisUntilFinished) {
                        Log.e("onTick", "init onTick countDownTimerImg");
                    }

                    public void onFinish() {
                        Log.e("onTick", "finish onTick countDownTimerImg");
                        listener.cancel();
                        countDownTimerImg.cancel();
                    }
                }.start();

            }
        });
    }

    private void loadWebGifImg(String nameCard) {

        ImageView wvInsert = (ImageView) findViewById(R.id.webview_card_img);

        switch (nameCard.trim()) {
            case "0"://visa
                wvInsert.setImageResource(R.drawable.visa);
                break;
            case "1"://Master
                wvInsert.setImageResource(R.drawable.mastercard);
                break;
            case "2"://Amex
                wvInsert.setImageResource(R.drawable.amex);
                break;
            case "3"://Diners
                wvInsert.setImageResource(R.drawable.diners);
                break;
            case "4"://Visa Electron
                wvInsert.setImageResource(R.drawable.electron);
                break;
            case "5"://Maestro
                wvInsert.setImageResource(R.drawable.maestro);
                break;
            case "6"://Datafast
                wvInsert.setImageResource(R.drawable.datfast);
                break;
            case "payclub"://PAYCLUB
                wvInsert.setImageResource(R.drawable.payclub);
                break;
            case "wallet"://PAYBLUE
                wvInsert.setImageResource(R.drawable.payblue);
                break;
            default:
                break;
        }
    }

    /**
     *
     * se agrega booleano para la pantalla de imprimir copia no utilice
     * el Resultcontrol y finalice la actividad
     */
    private void counterDownTimer(int timeout, final String mensaje, final boolean usarStar) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(timeout, 5000) {
            public void onTick(long millisUntilFinished) {
                Log.e("onTick", "init onTick countDownTimer");
            }

            public void onFinish() {
                countDownTimer.cancel();
                if (usarStar){
                    UIUtils.startResult(MasterControl.this, false, mensaje);
                    listener.cancel();
                }else {
                    listener.confirm(0);
                }
            }
        }.start();
    }

    private void runTimeGeneral(final TextView textViewTitle, int timeout) {
        if (countDownTimerGeneral != null) {
            countDownTimerGeneral.cancel();
            countDownTimerGeneral = null;
        }

        System.out.println("TIMEOUT " + timeout);
        //final int[] i = {Integer.parseInt(String.valueOf(timeout).substring(0, String.valueOf(timeout).length() - 3))};
        countDownTimerGeneral = new CountDownTimer(timeout, 1000) {

            public void onTick(long millisUntilFinished) {
                String timeToString = String.valueOf(millisUntilFinished);
                // textViewTitle.setText(i[0]-- + "");
                textViewTitle.setText(timeToString.substring(0, timeToString.length() - 3));
            }

            public void onFinish() {
                contFallback = 0;
                countDownTimerGeneral.cancel();
                listener.cancel();
                /*inputContent = "false";
                listener.confirm(InputManager.Style.COMMONINPUT);*/
            }
        }.start();
    }

    private void deleteTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public static void setMcontext(Context mcontext) {
        MasterControl.mcontext = mcontext;
    }
}
