package com.android.newpos.pay;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.datafast.inicializacion.configuracioncomercio.ChequeoIPs;
import com.datafast.inicializacion.configuracioncomercio.Host_Confi;
import com.datafast.inicializacion.configuracioncomercio.IP;
import com.datafast.inicializacion.configuracioncomercio.Rango;
import com.datafast.inicializacion.configuracioncomercio.TCONF;
import com.datafast.inicializacion.pagosvarios.PagosVarios;
import com.datafast.inicializacion.prompts.Prompt;
import com.datafast.inicializacion.tools.PolarisUtil;
import com.datafast.inicializacion.trans_init.Init;
import com.datafast.keys.PwMasterKey;
import com.datafast.server.Contador;
import com.datafast.server.TramaProcesar;
import com.datafast.server.activity.ServerTCP;
import com.datafast.server.server_tcp.Server;
import com.datafast.tools.BatteryStatus;
import com.datafast.tools.PaperStatus;
import com.datafast.tools_card.GetCard;
import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.device.contactless.SaveCtl;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.PAYUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.PayApplication;



public class StartAppDATAFAST extends AppCompatActivity {

    public static final String VERSION = "6.7";
    public static final String CERT = "CERTIFICACION   ";
    public static BatteryStatus batteryStatus;
    public static PaperStatus paperStatus;

    public static GetCard getCard = null;

    public static TCONF tconf = null;
    public static Host_Confi host_confi = null;
    public static IP tablaIp = null;
    public static Rango rango = null;
    public static ArrayList<IP> listIPs = null;
    public static ArrayList<Prompt> listPrompts = null;
    public static ArrayList<PagosVarios> listPagosVarios = null;
    public static boolean isInit = false;
    public static boolean inyecccionLLaves = false;
    public static boolean MODE_KIOSK = false;
    public static String lastCmd = "";
    public static int lastInputMode = 0x00;
    public static String lastPan ="";
    public static String[] lastTrack = null;
    public static boolean resumePA = false;
    public static Server server;
    public static ToneGenerator toneG=null;
    public static SaveCtl oneTap = null;

    public static ConcurrentHashMap<Integer, TramaProcesar>  mymap = new ConcurrentHashMap<Integer,  TramaProcesar>();
    public static Contador contador = new Contador();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validarZonaHoraria();

        if (idioma()){

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {//check system version ,if it is Android 5, load the so file
                PAYUtils.copyAssetsToData(getApplicationContext(),"libPlatform.so");  // copy the so file from folder assets to data folder
                System.load(getFilesDir().getPath()+"/libPlatform.so");		//load the so file
            }

            initSDK();

            isInit = PolarisUtil.isInitPolaris(StartAppDATAFAST.this);
            SharedPreferences preferences = StartAppDATAFAST.this.getSharedPreferences("inyeccion-llaves", Context.MODE_PRIVATE);
            inyecccionLLaves = preferences.getBoolean("stateKeys", false);

            //TMConfig.getInstance().activeDebugMode(true);
            TMConfig.getInstance().setDebug(true);

            //kioske mode
            kiosk();

            batteryStatus = new BatteryStatus();
            paperStatus = new PaperStatus();
            this.registerReceiver(batteryStatus, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

    }

    private boolean idioma() {
        boolean ret = false;
        String idiomaLocal = Locale.getDefault().toString();
        if (!idiomaLocal.equals("es_US")){
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog));

            builder.setIcon(R.drawable.ic_launcher_1);
            builder.setTitle("Advertencia");
            builder.setMessage("Por favor cambia el idioma del dispositivo.\nPreferencia: Espa??ol - Estados Unidos");
            builder.setCancelable(false);

            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        } else {
            ret = true;
        }
        return ret;
    }

    public void kiosk() {
        //kioske mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (MODE_KIOSK)
                startLockTask();
            /*else
                stopLockTask();*/
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idioma()){
            /*InjectMasterKey.injectMk("D573F8765B4975CB");//Master MediaNet
            InjectMasterKey.deleteKeys(KeyType.KEY_TYPE_MASTK, MASTERKEYIDX);*/
            /*if (threreIsKey(MASTERKEYIDX, "Debe cargar Master Key", StartAppDATAFAST.this)){
                initApp();
            }else{
                inyectarLlaves();
            }*/
            initApp();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * inicializa el sdk
     */
    private void initSDK() {
        PaySdk.getInstance().setActivity(this);
        PayApplication.getInstance().addActivity(this);
        PayApplication.getInstance().setRunned();
    }

    /**
     * Instancia todos los objetos necesarios para el manejo de la
     * inicializacion del PSTIS
     */
    private void initObjetPSTIS(){

        //----------- Init DataFast-----------
        if (host_confi == null){
            host_confi = Host_Confi.getSingletonInstance();
        }

        if (listIPs == null){
            listIPs = new ArrayList<>();
        }

        if (tablaIp == null){
            tablaIp = new IP();
        }

        if (tconf == null){
            tconf = TCONF.getSingletonInstance();
        }

        if (rango == null){
            rango = Rango.getSingletonInstance();
        }

        //--------- limpiar datos----------
        if (tconf != null){
            tconf.clearTCONF();
        }
        if (rango != null){
            rango.clearRango();
        }
        if (host_confi != null){
            host_confi.clearHost_Confi();
        }

        if (listIPs != null){
            listIPs.clear();
        }
    }

    /**
     * Permite realizar la inyeccion de la masterkey
     * Por motivo de pruebas se deja de manera fija,
     * pero esta debe ser inyectada desde una tarjeta chip
     */
    private void inyectarLlaves(){
        //initMasterKey();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(StartAppDATAFAST.this, PwMasterKey.class);
        startActivity(intent);
    }

    public static void leerBaseDatos(Context context){
        if (isInit) {
            tconf.selectTconf(context);
            host_confi.selectHostConfi(context);

            listIPs = ChequeoIPs.selectIP(context);
            if (listIPs == null) {
                isInit = false;
                UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "Error al leer tabla, Por favor Inicialice nuevamente", Toast.LENGTH_LONG);
            }
        }
    }

    /**
     * Inicio de la app
     */
    private void initApp(){
        initObjetPSTIS();

        leerBaseDatos(StartAppDATAFAST.this);

        if (ServerTCP.interrupInit) {
            ServerTCP.interrupInit = false;
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(StartAppDATAFAST.this, Init.class);
            intent.putExtra("PARCIAL", false);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            //intent.setClass(StartAppDATAFAST.this, menus.class);
            //intent.putExtra(DefinesDATAFAST.DATO_MENU, DefinesDATAFAST.ITEM_PRINCIPAL);
            intent.setClass(StartAppDATAFAST.this, ServerTCP.class);
            startActivity(intent);
        }
    }

    private boolean setZonaHoraria(String zonaHoraria){
        try {
            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            if (am != null) {
                am.setTimeZone(zonaHoraria);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
        return false;
    }

    private void validarZonaHoraria(){
        boolean ret = true;
        String zonaHorariaEcuador = "America/Bogota";
        String zonaHoraria = TimeZone.getDefault().getID();

        if (!zonaHoraria.equals(zonaHorariaEcuador)){
            ret = setZonaHoraria(zonaHorariaEcuador);
            if(!ret){
                Logger.error("ZONA HORARIA No configurada");
            }
        }
    }
}
