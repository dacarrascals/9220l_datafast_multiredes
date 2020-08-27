package com.datafast.menus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.newpos.pay.R;
import com.android.newpos.pay.StartAppDATAFAST;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.inicializacion.tools.PolarisUtil;
import com.newpos.libpay.utils.ISOUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import cn.desert.newpos.payui.UIUtils;

import static com.android.newpos.pay.StartAppDATAFAST.isInit;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;


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

    CountDownTimer countDownTimerMenus, countDownTimerDisplay;

    public static final int FALLBACK = 3;
    public static final int NO_FALLBACK = 0;
    public static final int TOTAL_BATCH = 500;

    private String menu;

    RelativeLayout layoutSaver;
    ImageView imageSaver;
    TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);
        RelativeLayout relativeLayoutBack = (RelativeLayout) findViewById(R.id.relativeLayoutBack);
        layoutSaver = (RelativeLayout) findViewById(R.id.layoutSaver);
        imageSaver = (ImageView) findViewById(R.id.imageSaver);
        version = (TextView) findViewById(R.id.textView_vrs);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mostrarMenu(Objects.requireNonNull(extras.getString(DefinesDATAFAST.DATO_MENU)));
            menu = Objects.requireNonNull(extras.getString(DefinesDATAFAST.DATO_MENU));
            if (menu.equals(DefinesDATAFAST.ITEM_PRINCIPAL)) {
                relativeLayoutBack.setVisibility(View.INVISIBLE);
                version.setText(StartAppDATAFAST.CERT + StartAppDATAFAST.VERSION);
            }
        }
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

    }

    public void onClickBack(View view) {
        if (!menu.equals(DefinesDATAFAST.ITEM_PRINCIPAL)) {
            if (isInit)
                finish();
        }
    }

    public List<menuItemsModelo> obtenerItems(String tipoMenu) {
        List<menuItemsModelo> itemMenu = new ArrayList<>();

        switch (tipoMenu) {

            case DefinesDATAFAST.ITEM_PRINCIPAL:
                counterDownTimerMenus();
                deleteTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_POLARIS, R.drawable.cloud));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_COMERCIO, R.drawable.ic_comercio));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONEXION, R.drawable.comunication));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_WIFI, R.drawable.ic_wifi));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_APPMANAGER, R.drawable.ic_appmanager));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_RED, R.drawable.ic_configuracion));
                break;

            case DefinesDATAFAST.ITEM_TRANSACCIONES:
                counterDownTimerMenus();
                deleteTimerDisplay();
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
                deleteTimerDisplay();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_TRANS_PRE_AUT, R.drawable.ic_preauto));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_AMPLIACION, R.drawable.ic_ampliacionpre));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIRMACION, R.drawable.ic_confirmacion));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_ANULACION_PRE_AUT, R.drawable.ic_anulacionpre));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_REIMPRESION_PRE_AUT, R.drawable.ic_reimpresionpreauto));
                break;

            case DefinesDATAFAST.ITEM_IMPRESION:
                counterDownTimerMenus();
                deleteTimerDisplay();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_RE_IMPRESION, R.drawable.ic_reimpresionpreauto));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_REPORTE_DETALLADO, R.drawable.ic_reportedetallado));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_TEST, R.drawable.ic_test));
                break;

            case DefinesDATAFAST.ITEM_RE_IMPRESION:
                counterDownTimerMenus();
                deleteTimerDisplay();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_TRANS_EN_PANTALLA, R.drawable.ic_menuimpresion));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_PREAUTO_PANTALLA, R.drawable.ic_menuimpresion));
                break;

            case DefinesDATAFAST.ITEM_COMERCIO:
                counterDownTimerMenus();
                deleteTimerDisplay();
                deleteTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_ECHO_TEST, R.drawable.ic_echo));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_BORRAR_REVERSO, R.drawable.ic_borrarreverso));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_TRANS_EN_PANTALLA, R.drawable.ic_menuimpresion));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_MASTER_KEY, R.drawable.ic_test));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_DATOS_COMERCIO, R.drawable.ic_test));
                break;

            case DefinesDATAFAST.ITEM_COMUNICACION:
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_INICIALIZACION, R.drawable.ic_inicializacion));
                itemMenu.add(new menuItemsModelo(DefinesDATAFAST.ITEM_CONFIG_INICIAL, R.drawable.ic_configuracion));
                break;
        }

        return itemMenu;
    }

    private void counterDownTimerDisplay() {
        if (countDownTimerDisplay != null) {
            countDownTimerDisplay.cancel();
            countDownTimerDisplay = null;
        }
        layoutSaver.setClickable(false);
        countDownTimerDisplay = new CountDownTimer(60000 * 3, 30000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Log.e("onTick", "finish onTick countDownTimer Display");
                deleteTimerDisplay();
                counterDownTimerDisplay();
            }
        }.start();
    }

    private void deleteTimerDisplay() {
        if (countDownTimerDisplay != null) {
            countDownTimerDisplay.cancel();
            countDownTimerDisplay = null;
        }
    }

    private void counterDownTimerMenus() {
        if (countDownTimerMenus != null) {
            countDownTimerMenus.cancel();
            countDownTimerMenus = null;
        }
        layoutSaver.setClickable(false);
        countDownTimerMenus = new CountDownTimer(30000, 5000) {
            public void onTick(long millisUntilFinished) {
                Log.i("onTick", "init onTick countDownTimer HomeDataFast");
            }

            public void onFinish() {
                Log.i("onTick", "finish onTick countDownTimer HomeDataFast");
                deleteTimerMenus();
                deleteTimerDisplay();
                finish();
            }
        }.start();
    }

    private void deleteTimerMenus() {
        if (countDownTimerMenus != null) {
            countDownTimerMenus.cancel();
            countDownTimerMenus = null;
        }
    }

    public void onClickCloseDisplay(View view) {
        deleteTimerDisplay();
        isDisplay = false;
        counterDownTimerDisplay();
        layoutSaver.setVisibility(View.GONE);
        setBrightness(140);
    }

    public void setBrightness(int brightness1) {
        int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0
        );
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness1);
    }

    @Override
    public void onBackPressed() {
        if (isInit)
            finish();
    }

     @Override
     protected void onResume() {
         super.onResume();
         contFallback = 0;
         isInit = PolarisUtil.isInitPolaris(menus.this);
         if (!isInit) {
             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     if (UIUtils.dialog != null) {
                         if (!UIUtils.dialog.isShowing()) {
                             UIUtils.showAlertDialogInit("ATENCIÓN", DefinesDATAFAST.MSG_INIT, menus.this);
                         }
                     } else {
                         UIUtils.showAlertDialogInit("ATENCIÓN", DefinesDATAFAST.MSG_INIT, menus.this);
                     }
                 }
             }, 500);

         }
     }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

