package com.datafast.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import java.util.TreeSet;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.newpos.pay.R;
import com.datafast.pinpad.cmd.CP.IpEthernetConf;
import com.github.angads25.toggle.LabeledSwitch;
import com.pos.device.net.eth.EthernetManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.desert.newpos.payui.UIUtils;

public class WifiSettings extends AppCompatActivity {
    private SwitchCompat switchWifi;
    private WifiManager wifiManager;
    private WebView webView;
    private ListView listaWifi;
    private TextView txtToolbarText, txtBuscandoRedes;
    private RelativeLayout relativeLayout;
    private int count;
    private boolean switchIsOn;
    Context context = this;
    CountDownTimer timer;
    private Parcelable state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_wifi);
        if (EthernetManager.getInstance().isEtherentEnabled()) {
            if (isConected() && isTypeConected().equalsIgnoreCase("ETHERNET")){
                alertDialog();
            }else {
                showWifiSettings();
            }
        }else{
            showWifiSettings();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(r);
        timer.cancel();
        finish();
    }

    public boolean isConected(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public String isTypeConected(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        String isTypeConected=activeNetwork.getTypeName();
        return isTypeConected;
    }

    private void showWifiSettings(){
        EthernetManager.getInstance().setEtherentEnabled(false);
        switchWifi = (SwitchCompat) findViewById(R.id.swt);
        webView = (WebView) findViewById(R.id.webViewProgress);
        listaWifi = (ListView) findViewById(R.id.listWifi);
        txtToolbarText = (TextView) findViewById(R.id.txtToolbarText);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative2);
        txtBuscandoRedes = (TextView) findViewById(R.id.txtBuscandoRedes);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        inicializarTimer();

        if ( !wifiManager.isWifiEnabled() ){
            wifiManager.setWifiEnabled(true);
        }
        txtToolbarText.setText("Apagar WiFi");
        switchWifi.setChecked(true);
        switchWifi.setEnabled(false);
        setSwitchIsOn(true);
        obtenerLista();

        switchWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchWifi.setEnabled(false);
                estadoWifi(isSwitchIsOn());
            }
        });

    }

    private void alertDialog(){

        final Dialog dialog= new Dialog(this);
        dialog.setContentView(R.layout.alertdialog_red);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Button btCancel= dialog.findViewById(R.id.setting_pass_cancel);
        Button btConfirm= dialog.findViewById(R.id.setting_pass_confirm);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWifiSettings();
                EthernetManager.getInstance().setEtherentEnabled(false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void inicializarTimer(){
        timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }
            public void onFinish() {
                wifiManager.reassociate();
                timer.cancel();
                state = listaWifi.onSaveInstanceState();
                timer.start();
                mostrarLista();

            }

        };
    }

    private void estadoWifi(boolean estado) {
        if (!estado){
            switchWifi.setChecked(true);
            setSwitchIsOn(true);
            if ( !wifiManager.isWifiEnabled() ) {
                wifiManager.setWifiEnabled(true);
            }
            txtToolbarText.setText("Apagar WiFi");
            obtenerLista();

        } else {
            if ( wifiManager.isWifiEnabled() ){
                wifiManager.disconnect();
                wifiManager.setWifiEnabled(false);
                timer.cancel();
            }
            state = null;
            switchWifi.setChecked(false);
            setSwitchIsOn(false);
            webView.setVisibility(View.GONE);
            listaWifi.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            txtToolbarText.setText("Encender WiFi");
            txtBuscandoRedes.setVisibility(View.GONE);
            handler.removeCallbacks(r);
            handler.postDelayed(r2, 1200);
        }
    }

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            txtBuscandoRedes.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            timer.start();
            mostrarLista();
        }
    };
    Runnable r2 = new Runnable() {
        @Override
        public void run() {
            switchWifi.setEnabled(true);
        }
    };

    private void obtenerLista() {
        webView.loadDataWithBaseURL(null, "<HTML><body bgcolor='#FFF'><div align=center>" +
                "<img width=\"80\" height=\"80\" src='file:///android_asset/gif/load3.gif'/></div></body></html>", "text/html", "UTF-8", null);

        webView.setVisibility(View.VISIBLE);
        txtBuscandoRedes.setVisibility(View.VISIBLE);
        txtBuscandoRedes.setText("BUSCANDO REDES WIFI...");

        handler.postDelayed(r,5000);
    }

    private void mostrarLista() {
        final String redConectada = wifiManager.getConnectionInfo().getSSID();

        final List<String> redFormateada = eliminarVacios(wifiManager.getScanResults(), redConectada.replace("\"", ""));

        ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.setting_list_item);
        listaWifi.setAdapter(arrayAdapter);

        if (redFormateada.size() > 0) {
            if ( redFormateada.get(0).equals("NULL") ){
                redesError();
            } else {
                switchWifi.setEnabled(true);
                listaWifi.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);

                WifiAdapter wifiAdapter = new WifiAdapter(context,redFormateada, redConectada );

                listaWifi.setAdapter(wifiAdapter);
                if (state != null)
                    listaWifi.onRestoreInstanceState(state);

                listaWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String redConectadaActual =  wifiManager.getConnectionInfo().getSSID();
                        final String red = redFormateada.get(position);
                        if (redConectadaActual != null) {
                            if (red.equals(redConectadaActual.replace("\"", ""))) {
                                String[] opc = new String[] { "Desconectar Red"};

                                AlertDialog opciones = new AlertDialog.Builder(
                                        WifiSettings.this)
                                        .setTitle(red)
                                        .setItems(opc,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int selected) {
                                                        if (selected == 0) {
                                                            modificarRed(redConectadaActual.replace("\"", ""),red);
                                                        }
                                                    }
                                                }).create();
                                opciones.show();
                            } else {

                                if (isConnected()){
                                    AlertDialog.Builder b=new AlertDialog.Builder(WifiSettings.this);
                                    b.setTitle(red);
                                    b.setMessage("¿Desea conectarse a la red?");
                                    b.setCancelable(false);
                                    b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            modificarRed(redConectadaActual.replace("\"", ""),red);
                                            final String finalRed = red;

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ingresarContraseña(finalRed,false);
                                                }
                                            },3000);
                                        }
                                    });
                                    b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    b.show();
                                } else {
                                    ingresarContraseña(red,false);
                                }
                            }
                        }
                    }
                });

                listaWifi.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                        final String redConectadaActual =  wifiManager.getConnectionInfo().getSSID();
                        final String red = redFormateada.get(position);
                        if (redConectadaActual != null) {
                            if (red.equals(redConectadaActual.replace("\"", ""))) {
                                String[] opc = new String[] { "Desconectar red", "Olvidar red"};

                                AlertDialog opciones = new AlertDialog.Builder(
                                        WifiSettings.this)
                                        .setTitle(red)
                                        .setItems(opc,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int selected) {
                                                        if (selected == 0) {
                                                            modificarRed(redConectadaActual.replace("\"", ""),red);
                                                        } else if (selected == 1) {
                                                            final int typeNet = typeNetwork(red);
                                                            final String preSharedKey = getExistingNetworkKey("\"" + red + "\"");
                                                            WifiConfiguration wc = new WifiConfiguration();
                                                            wc.SSID = "\"" + red + "\"";
                                                            if (typeNet != WifiConfiguration.KeyMgmt.NONE) {
                                                                wc.preSharedKey = "\""+ preSharedKey + "\"";
                                                            }
                                                            olvidarRed(red, preSharedKey, typeNet);
                                                        }
                                                    }
                                                }).create();
                                opciones.show();
                            } else {

                                if (isConnected()){
                                    final String finalRed = red;
                                    ingresarContraseña(finalRed,true);

                                } else {
                                    ingresarContraseña(red,true);

                                }
                            }
                        }
                        return true;
                    }

                });
            }
        } else {
            redesError();
        }
    }

    private void redesError() {
        UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "No hay redes disponibles", Toast.LENGTH_SHORT);
        wifiManager.setWifiEnabled(false);
        handler.removeCallbacks(r);
        timer.cancel();
        estadoWifi(true);
    }


    private List<String> eliminarVacios(List<ScanResult> scanResults, String firstNetwork) {
        ArrayList<String> resultados = new ArrayList<>();

        if (scanResults != null) {
            for (int i = 0; i < scanResults.size(); i++) {
                if (scanResults.get(i).SSID.length() > 0) {
                    if (!scanResults.get(i).SSID.equals(firstNetwork))
                        resultados.add(scanResults.get(i).SSID);
                }
            }
            if (resultados.size() > 0) {
                resultados = eliminarRepetidos(resultados);
                if (!firstNetwork.equals("<unknown ssid>"))
                    resultados.add(0, firstNetwork);
            }
        } else {
            resultados.add("NULL");
        }

        return resultados;
    }

    private ArrayList<String> eliminarRepetidos(ArrayList<String> resultados) {

        Set<String> TreeSet = new TreeSet<String>(resultados);
        resultados.clear();
        resultados.addAll(TreeSet);

        return resultados;
    }

    private void ingresarContraseña(final String titulo, final boolean valida) {
        final int typeNet = typeNetwork(titulo);
        if (typeNet > 0) {

            final String preSharedKey = getExistingNetworkKey("\"" + titulo + "\"");
            if (preSharedKey != null && !preSharedKey.equals("")) {
                if(valida){
                    String[] opc = new String[] { "Conectar red", "Olvidar red"};

                    AlertDialog opciones = new AlertDialog.Builder(
                            WifiSettings.this)
                            .setTitle(titulo)
                            .setItems(opc,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int selected) {
                                            if (selected == 0) {
                                                final String redConectadaActual =  wifiManager.getConnectionInfo().getSSID();
                                                if (!(titulo.equals(redConectadaActual.replace("\"", ""))) && isConnected()){
                                                    modificarRed(redConectadaActual.replace("\"", ""),titulo);
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            validarRed(titulo, preSharedKey, typeNet);
                                                        }
                                                    },3000);
                                                }else{
                                                    validarRed(titulo, preSharedKey, typeNet);
                                                }
                                            } else if (selected == 1) {
                                                olvidarRed(titulo, preSharedKey, typeNet);
                                            }
                                        }
                                    }).create();
                    opciones.show();
                }else{
                    validarRed(titulo, preSharedKey, typeNet);
                }
            } else {
                if(valida){
                    AlertDialog.Builder b=new AlertDialog.Builder(WifiSettings.this);
                    b.setTitle(titulo);
                    b.setMessage("Conectar red");
                    b.setCancelable(false);
                    b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String redConectadaActual =  wifiManager.getConnectionInfo().getSSID();
                            if (isConnected()){
                                modificarRed(redConectadaActual.replace("\"", ""),titulo);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ingresarContraseña(titulo,!valida);
                                    }
                                },3000);
                            }else {
                                ingresarContraseña(titulo, !valida);
                            }

                        }
                    });
                    b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    b.show();

                }else{
                    final Dialog dialog = UIUtils.centerDialog(context, R.layout.setting_home_pass, R.id.setting_pass_layout);
                    final EditText newEdit = dialog.findViewById(R.id.setting_pass_new);
                    final TextView title_pass = dialog.findViewById(R.id.title_pass);
                    ImageView icono=dialog.findViewById(R.id.icono);
                    icono.setImageDrawable(icono.getResources().getDrawable(R.drawable.ic_wifi2));
                    Button confirm = dialog.findViewById(R.id.setting_pass_confirm);
                    final ToggleButton ivShowHidePass= dialog.findViewById(R.id.ivShowHidePass);
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

                    newEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                    newEdit.requestFocus();
                    title_pass.setText(titulo);

                    dialog.findViewById(R.id.setting_pass_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            validarRed( titulo, newEdit.getText().toString(), typeNet);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }
            }

        } else {
            validarRed( titulo, "", typeNet);
        }
    }

    private void olvidarRed(String red, String contraseña, int typeKey){
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + red + "\"";
        if (typeKey != WifiConfiguration.KeyMgmt.NONE) {
            wc.preSharedKey = "\""+ contraseña + "\"";
        }
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(typeKey);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        if (netId == -1) {
            netId = getExistingNetworkId(wc.SSID);
        }
        wifiManager.disableNetwork(netId);
        wifiManager.removeNetwork(netId);
        wifiManager.saveConfiguration();
        wifiManager.reassociate();
        mostrarLista();
    }

    private int typeNetwork(String titulo) {
        List<ScanResult> lista = wifiManager.getScanResults();

        Set<ScanResult> hashSet = new HashSet<ScanResult>(lista);
        lista.clear();
        lista.addAll(hashSet);

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).SSID.equals(titulo)) {
                String capabilities = lista.get(i).capabilities;
                if (!capabilities.contains("-")) {
                    return WifiConfiguration.KeyMgmt.NONE;
                } else {
                    String type = capabilities.substring(capabilities.indexOf("-") + 1, capabilities.indexOf("-") + 4);

                    if (type.contains("IEEE802.1X"))
                        return WifiConfiguration.KeyMgmt.IEEE8021X;
                    else if (type.contains("WPA"))
                        return WifiConfiguration.KeyMgmt.WPA_EAP;
                    else if (type.contains("PSK"))
                        return WifiConfiguration.KeyMgmt.WPA_PSK;

                }
            }
        }
        return -1;
    }

    public void onClickBack(View view) {
        handler.removeCallbacks(r);
        timer.cancel();
        finish();
    }

    public void Actualizar(View view) {
        cambiarColores();
        wifiManager.reassociate();
        mostrarLista();
    }

    private void cambiarColores() {
        final ImageView imageView = (ImageView) findViewById(R.id.imgId);
        count = 0;

        CountDownTimer timerRefresh = new CountDownTimer(3000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                switch (count){
                    case 0:
                    case 3:
                        imageView.setColorFilter(context.getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
                        break;

                    case 1:
                    case 4:
                        imageView.setColorFilter(context.getResources().getColor(R.color.base_blue), PorterDuff.Mode.SRC_IN);
                        break;

                    case 2:
                        imageView.setColorFilter(context.getResources().getColor(R.color.dull_blue), PorterDuff.Mode.SRC_IN);
                        break;
                }
                count ++;
            }

            @Override
            public void onFinish() {
                count = 0;
                imageView.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
            }
        };
        timerRefresh.start();
    }

    private void validarRed(String red, String contraseña, int typeKey){
        boolean conexionExitosa;
        // setup a ic_wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + red + "\"";
        if (typeKey != WifiConfiguration.KeyMgmt.NONE) {
            wc.preSharedKey = "\""+ contraseña + "\"";
        }
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(typeKey);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        if (netId == -1) {
            netId = getExistingNetworkId(wc.SSID);
        }
        conexionExitosa = wifiManager.enableNetwork(netId, false);

        if(conexionExitosa) {
            conectar(red, netId);
        } else {
            UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "Longitud inválida", Toast.LENGTH_SHORT);
        }
    }

    private int getExistingNetworkId(String SSID) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (SSID.equalsIgnoreCase(existingConfig.SSID)) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }

    private String getExistingNetworkKey(String SSID) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (SSID.equalsIgnoreCase(existingConfig.SSID)) {
                    return existingConfig.preSharedKey;
                }
            }
        }
        return null;
    }

    CountDownTimer timer2;
    private void conectar(final String ssid, final int netId) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        timer.cancel();
        progressDialog.setMessage("Conectando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        timer2 = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (isConnected()) {
                    if (wifiManager.getConnectionInfo().getSSID().replace("\"", "").equals(ssid)) {
                        UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "Conexión establecida", Toast.LENGTH_SHORT);
                        progressDialog.cancel();
                        wifiManager.reassociate();
                        mostrarLista();
                        timer.start();
                        timer2.cancel();
                    }
                }
            }

            @Override
            public void onFinish() {
                UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "No es posible establecer conexión, reintente", Toast.LENGTH_SHORT);
                progressDialog.cancel();
                wifiManager.disableNetwork(netId);
                wifiManager.removeNetwork(netId);
                wifiManager.saveConfiguration();
                wifiManager.reassociate();
                mostrarLista();
                timer.start();
                timer2.cancel();
            }
        };
        timer2.start();
    }

    private void modificarRed(String titulo,String red) {
        boolean desconexionExitosa;

        final int typeKey = typeNetwork(titulo);

        // setup a ic_wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + titulo + "\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(typeKey);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        if (netId == -1) {
            netId = wifiManager.getConnectionInfo().getNetworkId();
        }
        String tituloDialog="Desconectando de " + titulo + "...";
        desconexionExitosa = wifiManager.disableNetwork(netId);        // desconectar
        if (!(red.equals(titulo.replace("\"", ""))) && isConnected()){
             tituloDialog="Desconectando de " + titulo + " para conectar a " + red;
        }

        if (desconexionExitosa){
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(tituloDialog);
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    //UIUtils.toast((Activity) context, R.drawable.ic_launcher, "Red desconectada", Toast.LENGTH_SHORT);
                }
            }, 2000);
        } else {
            UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "No fué posible desconectar", Toast.LENGTH_SHORT);
        }

        wifiManager.reassociate();
        mostrarLista();
        timer.start();

    }
    public boolean isConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return  wifi.isConnected();

    }

    public boolean isSwitchIsOn() {
        return switchIsOn;
    }
    public void setSwitchIsOn(boolean switchIsOn) {
        this.switchIsOn = switchIsOn;
    }
}
