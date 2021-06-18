package com.datafast.menus;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.newpos.pay.R;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.inicializacion.tools.PolarisUtil;
import com.datafast.tools.CounterTimer;
import com.datafast.tools.PermissionStatus;
import com.newpos.libpay.utils.ISOUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import cn.desert.newpos.payui.UIUtils;

import static com.android.newpos.pay.StartAppDATAFAST.inyecccionLLaves;
import static com.android.newpos.pay.StartAppDATAFAST.isInit;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.tools.PermissionStatus.firstTry;


/**
 * Copyright images
 * By Alfredo Hernandez
 * <div>Icons made by <a href="https://www.flaticon.com/authors/alfredo-hernandez" title="Alfredo Hernandez">Alfredo Hernandez</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 */

public class menus extends AppCompatActivity {

    public static String idAcquirer;
    public static int contFallback = 0;
    Boolean isClose = true;
    boolean isOne;
    boolean isDisplay;

    CounterTimer counterTimer;

    public static final int FALLBACK = 3;
    public static final int NO_FALLBACK = 0;
    public static final int TOTAL_BATCH = 500;

    private String menu;

    RelativeLayout layoutSaver;
    ImageView imageSaver;
    TextView version;
    PermissionStatus permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);
        RelativeLayout relativeLayoutBack = (RelativeLayout) findViewById(R.id.relativeLayoutBack);
        layoutSaver = (RelativeLayout) findViewById(R.id.layoutSaver);
        imageSaver = (ImageView) findViewById(R.id.imageSaver);
        version = (TextView) findViewById(R.id.textView_vrs);
        permissionStatus = new PermissionStatus(menus.this, this);

        if(isInit) {
            relativeLayoutBack.setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mostrarMenu(Objects.requireNonNull(extras.getString(DefinesDATAFAST.DATO_MENU)));
            menu = Objects.requireNonNull(extras.getString(DefinesDATAFAST.DATO_MENU));
            if (menu.equals(DefinesDATAFAST.ITEM_PRINCIPAL)) {
                relativeLayoutBack.setVisibility(View.INVISIBLE);
                /*version.setText(StartAppDATAFAST.CERT + StartAppDATAFAST.VERSION);*/
                version.setText("V" + getVersion().split("_")[0]+" B"+ getVersion().split("_")[1]);
                version.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.versionName;
    }

    private void mostrarMenu(String tipoMenu) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        if (tipoMenu.equals(DefinesDATAFAST.ITEM_TRANSACCIONES)) {
            String title = " ";
            toolbar.setTitle(Html.fromHtml(title));
        } else {
            String title = " ";
            toolbar.setTitle(Html.fromHtml(title));
        }

        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyItemMenu);

        RecyclerViewAdaptadorMenu recyclerViewAdaptadorMenu;
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerViewAdaptadorMenu = new RecyclerViewAdaptadorMenu(obtenerItems(tipoMenu), this, DefinesDATAFAST.TIPO_LAYOUT_LINEAR);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewAdaptadorMenu = new RecyclerViewAdaptadorMenu(obtenerItems(tipoMenu), this, DefinesDATAFAST.TIPO_LAYOUT_GRID);
        recyclerView.setAdapter(recyclerViewAdaptadorMenu);
        permissionStatus.permissionWriteSettings();

    }

    public void onClickBack(View view) {
        if (!menu.equals(DefinesDATAFAST.ITEM_PRINCIPAL)) {
            if (isInit && inyecccionLLaves)
                finish();
        }
    }

    public List<menuItemsModelo> obtenerItems(String tipoMenu) {
        List<menuItemsModelo> itemMenu = new ArrayList<>();

        switch (tipoMenu) {

            case DefinesDATAFAST.ITEM_PRINCIPAL:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_POLARIS, R.drawable.cloud));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_COMERCIO, R.drawable.ic_comercio));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONEXION, R.drawable.comunication));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_WIFI, R.drawable.ic_wifi));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_APPMANAGER, R.drawable.ic_appmanager));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_RED, R.drawable.ic_configuracion));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_AGENTE_POLARIS, R.drawable.cloud));
                break;

            case DefinesDATAFAST.ITEM_TRANSACCIONES:
                counterDownTimerMenus();
                if (isInit) {
                    if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_VENTA()))
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_VENTA, R.drawable.ic_venta));
                    if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_DIFERIDO()))
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_DIFERIDO, R.drawable.ic_diferido));
                    if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_PRE_AUTO()))
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_PRE_AUTORIZACION, R.drawable.ic_menupreautorizacion));
                    if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_ANULACION()))
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_ANULACION, R.drawable.ic_anulacion));
                    if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_PAGOS_VARIOS()))
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_PAGOS_VARIOS, R.drawable.ic_pagosvarios));
                    if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_PRE_VOUCHER())) {
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_PRE_VOUCHER, R.drawable.ic_prevoucher));
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_PAGO_PREVOUCHER, R.drawable.ic_prevoucher));
                    }
                    if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_CASH_OVER()))
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CASH_OVER, R.drawable.ic_cashover));
                    if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_PAGOS_ELECTRONICOS()))
                        itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_PAGOS_ELECTRONICOS, R.drawable.ic_pagoselectronicos));

                }else{
                    UIUtils.toast(menus.this, R.drawable.ic_launcher_1, "Debe Inicializar POS!", Toast.LENGTH_LONG);
                }
                break;

            case DefinesDATAFAST.ITEM_PRE_AUTORIZACION:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_TRANS_PRE_AUT, R.drawable.ic_preauto));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_AMPLIACION, R.drawable.ic_ampliacionpre));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIRMACION, R.drawable.ic_confirmacion));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_ANULACION_PRE_AUT, R.drawable.ic_anulacionpre));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_REIMPRESION_PRE_AUT, R.drawable.ic_reimpresionpreauto));
                break;

            case DefinesDATAFAST.ITEM_IMPRESION:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_RE_IMPRESION, R.drawable.ic_reimpresionpreauto));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_REPORTE_DETALLADO, R.drawable.ic_reportedetallado));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_TEST, R.drawable.ic_test));
                break;

            case DefinesDATAFAST.ITEM_RE_IMPRESION:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_TRANS_EN_PANTALLA, R.drawable.ic_menuimpresion));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_PREAUTO_PANTALLA, R.drawable.ic_menuimpresion));
                break;

            case DefinesDATAFAST.ITEM_COMERCIO:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_ECHO_TEST, R.drawable.ic_echo));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_BORRAR_REVERSO, R.drawable.ic_borrarreverso));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_TRANS_EN_PANTALLA, R.drawable.ic_menuimpresion));
                //itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_MASTER_KEY, R.drawable.ic_test));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_DATOS_COMERCIO, R.drawable.ic_test));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_COMERCIO, R.drawable.comunication));
                break;

            case DefinesDATAFAST.ITEM_COMUNICACION:
                if (isInit && inyecccionLLaves){
                    counterDownTimerMenus();
                }
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_INICIALIZACION, R.drawable.ic_inicializacion));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_INICIAL, R.drawable.ic_configuracion));
                if (!isInit || !inyecccionLLaves){
                    itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_RED, R.drawable.ic_configuracion));
                    itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_WIFI, R.drawable.ic_wifi));
                }
                break;
        }

        return itemMenu;
    }

    private void counterDownTimerMenus() {
        counterTimer = new CounterTimer(this);
        layoutSaver.setClickable(false);
        counterTimer.counterDownTimer();
    }

    public void onClickCloseDisplay(View view) {
        isDisplay = false;
        layoutSaver.setVisibility(View.GONE);
        setBrightness(140);
    }

    public void setBrightness(int brightness1) {
        int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness1);
    }

    @Override
    public void onBackPressed() {
        if (isInit && inyecccionLLaves)
            finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

     @Override
     protected void onResume() {
         super.onResume();
         contFallback = 0;
         isInit = PolarisUtil.isInitPolaris(menus.this);
         boolean permisosStatus = permissionStatus.validatePermissions();
         if ((!isInit && permisosStatus) || (!inyecccionLLaves && permisosStatus)) {
             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     if (PolarisUtil.isInitMsg){
                         if (UIUtils.dialog != null) {
                             if (!UIUtils.dialog.isShowing()) {
                                 UIUtils.showAlertDialogInit("ATENCIÓN", DefinesDATAFAST.MSG_INIT, menus.this);
                             }
                         } else {
                             UIUtils.showAlertDialogInit("ATENCIÓN", DefinesDATAFAST.MSG_INIT, menus.this);
                         }
                         PolarisUtil.isInitMsg = false;
                     }
                 }
             }, 500);

         }
         if (firstTry) {
             permissionStatus.confirmPermissionMsg();
         } else {
             permissionStatus.reqPermissions();
         }
     }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

