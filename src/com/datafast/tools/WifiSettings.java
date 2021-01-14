package com.datafast.tools;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.github.angads25.toggle.LabeledSwitch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.desert.newpos.payui.UIUtils;

public class WifiSettings extends AppCompatActivity {
    private LabeledSwitch switchWifi;
    private WifiManager wifiManager;
    private WebView webView;
    private ListView listaWifi;
    private TextView txtToolbarText, txtBuscandoRedes;
    private RelativeLayout relativeLayout;
    private int count;
    Context context = this;
    CountDownTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_wifi);

        switchWifi = (LabeledSwitch) findViewById(R.id.swt);
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
        switchWifi.setOn(true);
        obtenerLista();

        switchWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoWifi(switchWifi.isOn());
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(r);
        timer.cancel();
        finish();
    }

    private void inicializarTimer(){
        timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }
            public void onFinish() {
                wifiManager.reassociate();
                mostrarLista();
                timer.cancel();
                timer.start();
            }

        };
    }

    private void estadoWifi(boolean estado) {
        if (!estado){
            if ( !wifiManager.isWifiEnabled() ) {
                wifiManager.setWifiEnabled(true);
            }
            txtToolbarText.setText("Apagar WiFi");
            obtenerLista();

        } else {
            if ( wifiManager.isWifiEnabled() ){
                wifiManager.setWifiEnabled(false);
                timer.cancel();
            }
            webView.setVisibility(View.GONE);
            listaWifi.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            txtToolbarText.setText("Encender WiFi");
            txtBuscandoRedes.setVisibility(View.GONE);
            handler.removeCallbacks(r);
        }
    }

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            switchWifi.setEnabled(true);
            txtBuscandoRedes.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            timer.start();
            mostrarLista();
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

        final List<String> redFormateada = eliminarVacios(wifiManager.getScanResults());

        ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.setting_list_item);
        listaWifi.setAdapter(arrayAdapter);

        if (redFormateada.size() > 0) {
            if ( redFormateada.get(0).equals("NULL") ){
                redesError();
            } else {
                listaWifi.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);

                WifiAdapter wifiAdapter = new WifiAdapter(context,redFormateada, redConectada );

                listaWifi.setAdapter(wifiAdapter);

                listaWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String redConectadaActual =  wifiManager.getConnectionInfo().getSSID();
                        String red = redFormateada.get(position);
                        if (redConectadaActual != null) {
                            if (red.equals(redConectadaActual.replace("\"", ""))) {
                                modificarRed(red);
                            } else {
                                ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                                NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                                if (wifi.isConnected()){
                                    modificarRed(redConectadaActual.replace("\"", ""));
                                    final String finalRed = red;

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ingresarContraseña(finalRed);
                                        }
                                    },3000);
                                } else {
                                    ingresarContraseña(red);
                                }
                            }
                        }
                    }
                });
            }
        } else {
            redesError();
        }
    }

    private void redesError() {
        UIUtils.toast((Activity) context, R.drawable.ic_launcher, "No hay redes disponibles", Toast.LENGTH_SHORT);
        switchWifi.setOn(false);
        estadoWifi(true);
        handler.removeCallbacks(r);
        timer.cancel();
    }

    private List<String> eliminarVacios(List<ScanResult> scanResults) {
        ArrayList<String> resultados = new ArrayList<>();

        if (scanResults != null) {
            for (int i = 0; i < scanResults.size(); i++) {
                if (scanResults.get(i).SSID.length() > 0) {
                    resultados.add(scanResults.get(i).SSID);
                }
            }
            if (resultados.size() > 0) {
                resultados = eliminarRepetidos(resultados);
            }
        } else {
            resultados.add("NULL");
        }

        return resultados;
    }

    private ArrayList<String> eliminarRepetidos(ArrayList<String> resultados) {

        Set<String> hashSet = new HashSet<String>(resultados);
        resultados.clear();
        resultados.addAll(hashSet);

        return resultados;
    }

    private void ingresarContraseña(final String titulo) {
        final int typeNet = typeNetwork(titulo);
        if (typeNet > 0) {

            String preSharedKey = getExistingNetworkKey("\"" + titulo + "\"");

            if (preSharedKey != null && !preSharedKey.equals("")) {

                validarRed(titulo, preSharedKey, typeNet);

            } else {

                final Dialog dialog = UIUtils.centerDialog(context, R.layout.setting_home_pass, R.id.setting_pass_layout);
                final EditText newEdit = dialog.findViewById(R.id.setting_pass_new);
                final TextView title_pass = dialog.findViewById(R.id.title_pass);
                Button confirm = dialog.findViewById(R.id.setting_pass_confirm);
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

        } else {
            validarRed( titulo, "", typeNet);
        }
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
            conectar(red);
        } else {
            UIUtils.toast((Activity) context, R.drawable.ic_launcher, "Longitud inválida", Toast.LENGTH_SHORT);
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
    private void conectar(final String ssid ) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        timer.cancel();
        progressDialog.setMessage("Conectando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        timer2 = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (wifi.isConnected()) {
                    if (wifiManager.getConnectionInfo().getSSID().replace("\"", "").equals(ssid)) {
                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, "Conexión establecida", Toast.LENGTH_SHORT);
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
                UIUtils.toast((Activity) context, R.drawable.ic_launcher, "Contraseña incorrecta", Toast.LENGTH_SHORT);
                progressDialog.cancel();
                wifiManager.reassociate();
                mostrarLista();
                timer.start();
                timer2.cancel();
            }
        };
        timer2.start();
    }

    private void modificarRed(String titulo) {
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
        desconexionExitosa = wifiManager.disableNetwork(netId);        // desconectar

        if (desconexionExitosa){
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Desconectando de " + titulo + "...");
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
            UIUtils.toast((Activity) context, R.drawable.ic_launcher, "No fué posible desconectar", Toast.LENGTH_SHORT);
        }

        wifiManager.reassociate();
        mostrarLista();
        timer.start();

    }
}
