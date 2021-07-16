package com.datafast.server.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.newpos.pay.R;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.inicializacion.trans_init.Init;
import com.datafast.menus.MenuAction;
import com.datafast.menus.menus;
import com.datafast.pinpad.cmd.CB.ConfiguracionBasica;
import com.datafast.pinpad.cmd.PA.Actualizacion;
import com.datafast.pinpad.cmd.PC.Control;
import com.datafast.pinpad.cmd.PP.PP_Request;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.server.callback.waitResponse;
import com.datafast.server.server_tcp.Server;
import com.datafast.slide.slide;
import com.datafast.tools.ConfigRed;
import com.datafast.tools.CounterTimer;
import com.datafast.tools.Wifi;
import com.datafast.tools_bacth.ToolsBatch;
import com.datafast.updateapp.UpdateApk;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.beeper.Beeper;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;

import java.io.IOException;
import java.util.Objects;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;

import static com.android.newpos.pay.StartAppDATAFAST.inyecccionLLaves;
import static com.android.newpos.pay.StartAppDATAFAST.lastCmd;
import static com.android.newpos.pay.StartAppDATAFAST.isInit;
import static com.android.newpos.pay.StartAppDATAFAST.resumePA;
import static com.android.newpos.pay.StartAppDATAFAST.server;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.android.newpos.pay.StartAppDATAFAST.toneG;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREAUTO;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CB;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CP;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.NN;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PC;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.TO;
import static com.datafast.server.server_tcp.Server.cmd;
import static com.newpos.libpay.trans.Trans.idLote;
import static com.pos.device.sys.SystemManager.reboot;
import static java.lang.Thread.sleep;

public class ServerTCP extends AppCompatActivity {

    private ImageView setting;
    public static Dialog mDialog;
    private Wifi wifi;
    private Control control = null;
    public static Actualizacion actualizacion = null;
    private ConfiguracionBasica configuracionBasica = null;
    private slide slide;
    private boolean ret;
    private String[] tipoVenta;
    private int seleccion = 0;
    CounterTimer counterTimer;

    public static waitResponse listenerServer;

    boolean isInEcho;
    public static boolean installApp = false;
    public static boolean interrupInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_tcp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Logger.information("ServerTCP.java -> onCreate");
        validacionesInciales(true);
    }

    public void validacionesInciales(boolean isOncreate) {

        isInEcho = false;

        if ((Control.echoTest && !Control.failEchoTest && ISOUtil.stringToBoolean(tconf.getHABILITA_PLC())) || Actualizacion.goEchoTest) {
            Control.echoTest = false;
            Actualizacion.echoTest = false;
            Actualizacion.goEchoTest = false;
            isInEcho = true;
            MenuAction menuAction = new MenuAction(ServerTCP.this, "ECHO TEST");
            menuAction.SelectAction();
        }

        if (installApp) {
            installApp = false;
            UpdateApk updateApk = new UpdateApk(ServerTCP.this);
            updateApk.instalarApp(ServerTCP.this);
        }

        slide = new slide(ServerTCP.this, true);
        com.datafast.slide.slide.galeria(this, R.id.adcolumn);

        if (isOncreate)
            toolbar();

        MasterControl.setMcontext(ServerTCP.this);
        if (isInit && inyecccionLLaves) {
            if (!isInEcho) {
                if (isOncreate) {
                    if (server == null)
                        server = new Server(ServerTCP.this);
                    if (toneG == null)
                        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 1000);
                }
                wifi = new Wifi(ServerTCP.this);
                control = new Control(ServerTCP.this);
                actualizacion = new Actualizacion(ServerTCP.this);
                configuracionBasica = new ConfiguracionBasica(ServerTCP.this);
            }
        } else {
            settings();
        }
    }

    private void stopServer() {
        server = null;
    }

    /**
     * Enciende la pantalla cuando llega una transacción
     */
    public void unlockScreen(Context context) {
        PowerManager.WakeLock powerManager = ((PowerManager) Objects.requireNonNull(context.getSystemService(POWER_SERVICE))).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag:");
        powerManager.acquire(10*60*1000L /*10 minutes*/);
        powerManager.release();
    }

    public void startTrans(final String aCmd, final byte[] aDat, waitResponse l) {

        removeOptionsMenu();
        unlockScreen(this);

        this.listenerServer = l;
        new Thread() {
            @Override
            public void run() {
                if (checkCardPresent(aCmd)) {
                    Intent intent = new Intent();
                    seleccion = 0;
                    if (mDialog!=null){
                        mDialog.dismiss();
                    }
                    switch (aCmd) {
                        case CB:
                            boolean cbRet = configuracionBasica.procesoCb(aDat);
                            listenerServer.waitRspHost(configuracionBasica.getCb_response().packData());
                            if (cbRet) {
                                UIUtils.startResult(ServerTCP.this, true, "CONFIGURACION BASICA\nENVIADA", false);
                            } else {
                                UIUtils.startResult(ServerTCP.this, false, "ERROR EN TRAMA", false);
                            }
                            break;
                        case PP:

                            Logger.information("ServerTCP.java -> Inicia proceso para un PP");

                            PP_Request pp_request = new PP_Request();

                            if (!Server.correctLength) {
                                pp_request.UnPackHash(aDat);
                                Logger.information("ServerTCP.java -> Error longitud de trama no corresponde");
                                ProcessPPFail processPPFail = new ProcessPPFail(ServerTCP.this);
                                processPPFail.responsePPInvalid(pp_request, "ERROR EN TRAMA", ERROR_PROCESO, true);
                                UIUtils.startResult(ServerTCP.this, false, "ERROR EN TRAMA", false);
                                break;
                            }

                            pp_request.UnPackData(aDat);
                            if (pp_request.getCountValid() > 0) {
                                Logger.information("ServerTCP.java -> Error, la trama no es correcta");
                                ProcessPPFail processPPFail = new ProcessPPFail(ServerTCP.this);
                                processPPFail.responsePPInvalid(pp_request, "ERROR EN TRAMA", ERROR_PROCESO, true);
                                UIUtils.startResult(ServerTCP.this, false, "ERROR EN TRAMA", false);
                                break;
                            }
                            seleccion = Integer.parseInt(pp_request.getTypeTrans());
                            if (Control.echoTest && Control.failEchoTest) {
                                Control.failEchoTest = false;
                            }
                        case LT:
                        case CT:
                            if (seleccion == 0) {
                                seleccion = 1;
                            }
                            tipoVenta = new String[]{"VENTA", "DIFERIDO", "ANULACION", "VENTA", "NN", "PAGOS CON CODIGO", "NN"};
                            MenuAction menuAction = new MenuAction(ServerTCP.this, tipoVenta[seleccion - 1]);
                            menuAction.SelectAction();
                            break;
                        case CP:
                            Logger.information("ServerTCP.java -> Inicia proceso para un CP");
                            ret = wifi.comunicacion(aDat, listenerServer);
                            if (ret) {
                                UIUtils.startResult(ServerTCP.this, true, "DATOS DE RED ACTUALIZADOS \n REINICIANDO POS", false);
                            } else {
                                //stopServer();
                                UIUtils.startResult(ServerTCP.this, false, "ERROR EN TRAMA", false);
                            }
                            break;
                        case PC:
                            int pcRet = control.actualizacionControl(aDat);
                            listenerServer.waitRspHost(control.getPc_response().packData());

                            if (pcRet == 1) {
                                UIUtils.startResult(ServerTCP.this, true, "TRANS. BORRADAS\nINICIO DE DIA REALIZADO", false);
                            }else{
                                UIUtils.startResult(ServerTCP.this, false, "ERROR EN TRAMA", false);
                            }

                            break;
                        case NN:
                            break;
                        case PA:
                            actualizacion.procesoActualizacion(aDat);
                            if (actualizacion.tramaValida == -1) {
                                UIUtils.startResult(ServerTCP.this, false, "ERROR EN TRAMA", false);
                            } else {
                                if (actualizacion.intentOK) {
                                    resumePA = true;
                                    //UIUtils.startResult(ServerTCP.this,true,"PROCESO DE ACTUALIZACION INICIADO",true);
                                } else {
                                    UIUtils.startResult(ServerTCP.this, false, actualizacion.msgfail, false);
                                }
                            }
                            listenerServer.waitRspHost(actualizacion.getPa_response().packData());
                            break;
                        default:
                            break;
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.information("ServerTCP.java -> onResume");
        if (mDialog!=null){
            mDialog.dismiss();
        }
        slide.setTimeoutSlide(5000);
        if (resumePA) {
            resumePA = false;
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(ServerTCP.this, Init.class);
            intent.putExtra("PARCIAL", true);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.information("ServerTCP.java -> onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.information("ServerTCP.java -> onPause");
        slide.stopSlide();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.information("ServerTCP.java -> onRestart");
        validacionesInciales(false);
    }

    @Override
    public void onBackPressed() {
    }

    public void toolbar() {
        setting = (ImageView) findViewById(R.id.iv_close);
        setting.setVisibility(View.VISIBLE);
        setting.setImageResource(R.drawable.ic_baseline_more_vert_24);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //maintainPwd("CLAVE TECNICO", tconf.getCLAVE_TECNICO(), DefinesDATAFAST.ITEM_CONFIGURACION, 6);
                OptionsMenu();
            }
        });

    }

    private void OptionsMenu(){
        RelativeLayout contenedor = (RelativeLayout) findViewById(R.id.servertcp);
        final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        inflater.inflate(R.layout.menu_options,contenedor,true);

        LinearLayout conf = findViewById(R.id.configuracion);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOptionsMenu();
                maintainPwd("CLAVE TECNICO", tconf.getCLAVE_TECNICO(), DefinesDATAFAST.ITEM_CONFIGURACION, 6);
            }
        });
        LinearLayout datosred = findViewById(R.id.datosdered);
        datosred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOptionsMenu();
                MenuAction menuAction= new MenuAction(ServerTCP.this, DefinesDATAFAST.ITEM_CONEXION);
                menuAction.SelectAction();
                if (mDialog!=null)
                     counterTimer();
            }
        });
        LinearLayout resumentrans = findViewById(R.id.resumentrans);
        resumentrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOptionsMenu();
                MenuAction menuAction = new MenuAction(ServerTCP.this, DefinesDATAFAST.ITEM_RESUMEN_TRANS);
                menuAction.SelectAction();
                counterTimer();
            }
        });
        LinearLayout inicializacion = findViewById(R.id.inicializacion);
        inicializacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOptionsMenu();
                mDialog = alertDialogConfirm(true);
                counterTimer();
            }
        });
        LinearLayout actualizacionremota = findViewById(R.id.actualizacionremota);
        actualizacionremota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOptionsMenu();
                mDialog = alertDialogConfirm(false);
                counterTimer();
            }
        });
        RelativeLayout m = findViewById(R.id.menuOptions);
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOptionsMenu();
            }
        });
    }

    private void removeOptionsMenu(){
        ViewGroup menu = findViewById(R.id.servertcp);
        RelativeLayout options = findViewById(R.id.menuOptions);
        menu.removeView(options);
    }

    private Dialog alertDialogConfirm(boolean isIni){

        final Dialog dialog= new Dialog(this);
        dialog.setContentView(R.layout.alertdialog_red_confirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Button btCancel= dialog.findViewById(R.id.btn_no);
        Button btConfirm= dialog.findViewById(R.id.btn_si);
        TextView textConfirm = dialog.findViewById(R.id.textconfirm);
        ImageView imgconfirm = dialog.findViewById(R.id.imgconfirm);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        if(isIni){
            btConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    MenuAction menuAction = new MenuAction(ServerTCP.this,DefinesDATAFAST.ITEM_INICIALIZACION);
                    menuAction.SelectAction();
                }
            });
        }else{
            imgconfirm.setImageDrawable(getResources().getDrawable(R.drawable.ic_actualizacionremota_confirm));
            textConfirm.setText("¿Desea realizar una \nactualización remota?");
            btConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Actualizacion.actualizacionMenu();
                }
            });
        }

        dialog.show();
        return dialog;
    }

    private void maintainPwd(String title, final String pwd, final String type_trans, int lenEdit) {
        final Intent intent = new Intent();
        mDialog = UIUtils.centerDialog(ServerTCP.this, R.layout.setting_home_pass, R.id.setting_pass_layout);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LinearLayout reiniciar=mDialog.findViewById(R.id.setting_pass_layout);
        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterTimer();
            }
        });
        final EditText newEdit = mDialog.findViewById(R.id.setting_pass_new);
        newEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterTimer();
            }
        });
        final TextView title_pass = mDialog.findViewById(R.id.title_pass);
        Button confirm = mDialog.findViewById(R.id.setting_pass_confirm);
        final ToggleButton ivShowHidePass= mDialog.findViewById(R.id.ivShowHidePass);
        ivShowHidePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    newEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowHidePass.setBackground(getResources().getDrawable(R.drawable.ic_visibility));

                }else{
                    newEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowHidePass.setBackground(getResources().getDrawable(R.drawable.ic_invisible));

                }
                newEdit.setSelection(newEdit.getText().length());
            }
        });
        newEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lenEdit)});
        newEdit.requestFocus();
        title_pass.setText(title);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterTimer();
                InputMethodManager imm = (InputMethodManager) ServerTCP.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newEdit.getWindowToken(), 0);

                String np = newEdit.getText().toString();

                if (!np.equals("")) {
                    if (np.equals(pwd)) {

                        Intent intent = new Intent();
                        intent.setClass(ServerTCP.this, menus.class);
                        intent.putExtra(DefinesDATAFAST.DATO_MENU, DefinesDATAFAST.ITEM_PRINCIPAL);
                        startActivity(intent);

                        mDialog.dismiss();

                    } else {
                        newEdit.setText("");
                        UIUtils.toast(ServerTCP.this, R.drawable.ic_launcher_1, getString(R.string.err_msg_pwoperario), Toast.LENGTH_SHORT);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                    }
                } else {
                    UIUtils.toast(ServerTCP.this, R.drawable.ic_launcher_1, getString(R.string.err_msg_password), Toast.LENGTH_SHORT);
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                }
            }
        });

        mDialog.findViewById(R.id.setting_pass_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        counterTimer();
    }

    private void settings() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);


        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(ServerTCP.this, menus.class);
        intent.putExtra(DefinesDATAFAST.DATO_MENU, DefinesDATAFAST.ITEM_COMUNICACION);
        startActivity(intent);
    }

    private void counterTimer() {
        counterTimer = new CounterTimer(mDialog);
        counterTimer.counterDownTimerDialog();
    }


    private boolean checkCardPresent(String aCmd) {
        final IccReader iccReader0;
        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
        if (!iccReader0.isCardPresent() && !(lastCmd.equals(LT) && aCmd.equals(PP))) {
            try {
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 1000);
                sleep(1000);
            } catch (Exception e) {
            }
        }
        if (lastCmd.equals(LT) && aCmd.equals(PP)) {
            try {
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 1000);
                sleep(1000);
            } catch (Exception e) {
            }
            return true;
        }

        do {
            if (iccReader0.isCardPresent()) {
                try {
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 1000);
                    sleep(1000);
                } catch (Exception e) {
                    break;
                }
            }
        } while (iccReader0.isCardPresent());

        return true;
    }
}
