package com.datafast.server.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
import com.datafast.tools.Wifi;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;
import java.io.IOException;
import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import static com.android.newpos.pay.StartAppDATAFAST.lastCmd;
import static com.android.newpos.pay.StartAppDATAFAST.isInit;
import static com.android.newpos.pay.StartAppDATAFAST.resumePA;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CB;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CP;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.NN;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PC;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;

public class ServerTCP extends AppCompatActivity {

    private Server server;
    private ImageView setting;
    private Dialog mDialog;
    private Wifi wifi;
    private Control control = null;
    private Actualizacion actualizacion = null;
    private ConfiguracionBasica configuracionBasica = null;
    private slide slide;
    private boolean ret;
    private String[] tipoVenta;
    private int seleccion = 0;

    public static waitResponse listenerServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_tcp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Control.echoTest || Actualizacion.echoTest){
            Control.echoTest = false;
            Actualizacion.echoTest = false;
            MenuAction menuAction =  new MenuAction(ServerTCP.this, "ECHO TEST");
            menuAction.SelectAction();
        }

        slide = new slide( ServerTCP.this, true);
        slide.galeria(this, R.id.adcolumn);

        toolbar();
        MasterControl.setMcontext(ServerTCP.this);
        if (isInit) {
            server = new Server(ServerTCP.this);
            wifi = new Wifi(ServerTCP.this);
            control = new Control(ServerTCP.this);
            actualizacion = new Actualizacion(ServerTCP.this);
            configuracionBasica = new ConfiguracionBasica(ServerTCP.this);
        } else {
            settings();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
    }

    private void stopServer(){
        if (server != null) {
            if (server.getServerSocket() != null && !server.getServerSocket().isClosed()) {
                try {
                    server.getServerSocket().close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Enciende la pantalla cuando llega una transacción
     * */
    public static void unlockScreen(Context context){
        PowerManager.WakeLock powerManager = ((PowerManager)context.getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag:");
        powerManager.acquire();
        powerManager.release();
    }

    public void startTrans(final String aCmd, final byte[] aDat, waitResponse l) {

        unlockScreen(this);

        this.listenerServer = l;
        new Thread() {
            @Override
            public void run() {
                if (checkCardPresent(aCmd)){
                    Intent intent = new Intent();
                    seleccion = 0;

                    switch (aCmd) {
                        case CB:
                            boolean cbRet = configuracionBasica.procesoCb(aDat);
                            listenerServer.waitRspHost(configuracionBasica.getCb_response().packData());
                            if (cbRet){
                                UIUtils.startResult(ServerTCP.this,true,"CONFIGURACION BASICA\nENVIADA",false);
                            }else {
                                UIUtils.startResult(ServerTCP.this,false,"ERROR EN TRAMA",false);
                            }
                            break;
                        case PP:
                            PP_Request pp_request = new PP_Request();
                            pp_request.UnPackData(aDat);
                            seleccion = Integer.parseInt(pp_request.getTypeTrans());
                            if (pp_request.getCountValid() > 0){
                                ProcessPPFail processPPFail = new ProcessPPFail(ServerTCP.this);
                                processPPFail.responsePPInvalid(pp_request, "ERROR EN TRAMA", ERROR_PROCESO);
                                UIUtils.startResult(ServerTCP.this,false,"ERROR EN TRAMA",false);
                                break;
                            }
                        case LT:
                        case CT:
                            if (seleccion == 0){
                                seleccion = 1;
                            }
                            tipoVenta = new String[] {"VENTA","DIFERIDO","ANULACION","VENTA","NN","PAGOS CON CODIGO","NN"};
                            MenuAction menuAction =  new MenuAction(ServerTCP.this, tipoVenta[seleccion - 1]);
                            menuAction.SelectAction();
                            break;
                        case CP:
                            ret = wifi.comunicacion(aDat, listenerServer);
                            if (ret){
                                stopServer();
                                UIUtils.startResult(ServerTCP.this,true,"DATOS DE RED ACTUALIZADOS",false);
                            }else {
                                stopServer();
                                UIUtils.startResult(ServerTCP.this,false,"ERROR EN TRAMA",false);
                            }
                            break;
                        case PC:
                            int pcRet = control.actualizacionControl(aDat);
                            listenerServer.waitRspHost(control.getPc_response().packData());

                            if (pcRet == 0){
                                UIUtils.startResult(ServerTCP.this,false,"ERROR EN TRAMA",false);
                            }

                            if (pcRet == 1){
                                UIUtils.startResult(ServerTCP.this,true,"TRANS. BORRADAS\nINICIO DE DIA REALIZADO",false);
                            }

                            break;
                        case NN:
                            break;
                        case PA:
                            actualizacion.procesoActualizacion(aDat);
                            if (actualizacion.tramaValida == -1) {
                                UIUtils.startResult(ServerTCP.this,false,"ERROR EN TRAMA",false);
                            } else {
                                if (actualizacion.intentOK){
                                    resumePA = true;
                                    //UIUtils.startResult(ServerTCP.this,true,"PROCESO DE ACTUALIZACION INICIADO",true);
                                }else {
                                    UIUtils.startResult(ServerTCP.this,false,actualizacion.msgfail,false);
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
        slide.setTimeoutSlide(5000);
        if (resumePA) {
            System.out.println("ENTRA AL TIMER");
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(ServerTCP.this, Init.class);
            intent.putExtra("PARCIAL", false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        slide.stopSlide();
    }

    public void toolbar() {
        setting = (ImageView) findViewById(R.id.iv_close);
        setting.setVisibility(View.VISIBLE);
        setting.setImageResource(R.drawable.ic_configuracion);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maintainPwd("CLAVE TECNICO", tconf.getCLAVE_TECNICO(), DefinesDATAFAST.ITEM_CONFIGURACION, 6);
            }
        });

    }

    private void maintainPwd(String title, final String pwd, final String type_trans, int lenEdit) {
        final Intent intent = new Intent();
        mDialog = UIUtils.centerDialog(ServerTCP.this, R.layout.setting_home_pass, R.id.setting_pass_layout);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText newEdit = mDialog.findViewById(R.id.setting_pass_new);
        final TextView title_pass = mDialog.findViewById(R.id.title_pass);
        Button confirm = mDialog.findViewById(R.id.setting_pass_confirm);
        newEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lenEdit)});
        newEdit.requestFocus();
        title_pass.setText(title);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) ServerTCP.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newEdit.getWindowToken(), 0);

                String np = newEdit.getText().toString();

                if (!np.equals("")){
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
                }else{
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
    }

    private void settings(){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);


        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(ServerTCP.this, menus.class);
        intent.putExtra(DefinesDATAFAST.DATO_MENU, DefinesDATAFAST.ITEM_COMUNICACION);
        startActivity(intent);
    }
    private boolean checkCardPresent(String aCmd) {
        if (lastCmd.equals(LT) && aCmd.equals(PP)){
            return true;
        }
        final IccReader iccReader0;
        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 1000);
        do {
            if (iccReader0.isCardPresent()){
                try{
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2,1000);
                    Thread.sleep(1000);
                }catch (Exception e){
                    break;
                }
            }
        } while (iccReader0.isCardPresent());

        return true;
    }

    @Override
    public void onBackPressed() {}
}
