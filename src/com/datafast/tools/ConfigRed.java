package com.datafast.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.android.newpos.pay.StartAppDATAFAST;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.pinpad.cmd.CP.IpEthernetConf;
import com.datafast.pinpad.cmd.CP.IpWifiConf;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.KeyBoardUtil;
import com.pos.device.net.eth.EthernetManager;
import com.pos.device.net.wifi.PosWifiManager;
import com.pos.device.net.wifi.WifiSsidInfo;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.BaseActivity;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class ConfigRed extends BaseActivity implements View.OnClickListener {

    TextView tvIp1, tvIp2, tvIp3, tvIp4;
    EditText etIp1, etIp2, etIp3, etIp4;
    private final static String DHCP = "DHCP";
    private final static String STATIC = "ESTATICO";

    private final static String ACTIVADO = "ACTIVADO";
    private final static String DESACTIVADO = "DESACTIVADO";
    private WifiManager wifiManager;
    EditText etPort;

    TextView tvMask1, tvMask2, tvMask3, tvMask4, tvMask;
    EditText etMask1, etMask2, etMask3, etMask4;

    TextView tvGateway1, tvGateway2, tvGateway3, tvGateway4;
    EditText etGateway1, etGateway2, etGateway3, etGateway4;

    TextView tvDns1, tvDns2, tvDns3, tvDns4;
    EditText etDns1, etDns2, etDns3, etDns4;

    RelativeLayout containerDns;

    InputMethodManager inputMethodManager;

    Switch switchConnectionType,switchConnection;
    private CounterTimer counterTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_red);
        setNaviTitle("CONFIG RED POS");

        KeyBoardUtil.assistActivity(this);

        tvMask = findViewById(R.id.tv_Mask);
        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        switchConnection=findViewById(R.id.sw_conf_red);
        switchConnectionType = findViewById(R.id.sw_conf_ip);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mapObjects();

        etPort.setOnClickListener(ConfigRed.this);

        tvIp1.setOnClickListener(ConfigRed.this);
        tvIp2.setOnClickListener(ConfigRed.this);
        tvIp3.setOnClickListener(ConfigRed.this);
        tvIp4.setOnClickListener(ConfigRed.this);

        tvMask1.setOnClickListener(ConfigRed.this);
        tvMask2.setOnClickListener(ConfigRed.this);
        tvMask3.setOnClickListener(ConfigRed.this);
        tvMask4.setOnClickListener(ConfigRed.this);

        tvGateway1.setOnClickListener(ConfigRed.this);
        tvGateway2.setOnClickListener(ConfigRed.this);
        tvGateway3.setOnClickListener(ConfigRed.this);
        tvGateway4.setOnClickListener(ConfigRed.this);

        tvDns1.setOnClickListener(ConfigRed.this);
        tvDns2.setOnClickListener(ConfigRed.this);
        tvDns3.setOnClickListener(ConfigRed.this);
        tvDns4.setOnClickListener(ConfigRed.this);

        etIp1.setSelection(etIp1.getText().length());
        etIp2.setSelection(etIp2.getText().length());
        etIp3.setSelection(etIp3.getText().length());
        etIp4.setSelection(etIp4.getText().length());

        etMask1.setSelection(etMask1.getText().length());
        etMask2.setSelection(etMask2.getText().length());
        etMask3.setSelection(etMask3.getText().length());
        etMask4.setSelection(etMask4.getText().length());

        etGateway1.setSelection(etGateway1.getText().length());
        etGateway2.setSelection(etGateway2.getText().length());
        etGateway3.setSelection(etGateway3.getText().length());
        etGateway4.setSelection(etGateway4.getText().length());

        etDns1.setSelection(etDns1.getText().length());
        etDns2.setSelection(etDns2.getText().length());
        etDns3.setSelection(etDns3.getText().length());
        etDns4.setSelection(etDns4.getText().length());

        if (EthernetManager.getInstance().isEtherentEnabled()){
            switchConnection.setText(ACTIVADO);
            switchConnection.setChecked(true);
        }


        switchConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterTimer();
                if (switchConnection.isChecked()) {
                    if ( wifiManager.isWifiEnabled() ){
                        wifiManager.setWifiEnabled(false);
                    }
                    EthernetManager.getInstance().setEtherentEnabled(true);
                    switchConnection.setText(ACTIVADO);
                    checkConnection();
                } else {
                    if ( !wifiManager.isWifiEnabled() ){
                        wifiManager.setWifiEnabled(true);
                    }
                    EthernetManager.getInstance().setEtherentEnabled(false);
                    switchConnection.setText(DESACTIVADO);
                    containerDns.setVisibility(View.GONE);
                    checkConnection();
                }
            }
        });

        String stateIp = null;
        if (isWifiConnected()) {
            stateIp = IpWifiConf.getConnectionTypeWifi(getApplicationContext());
        }

        if (EthernetManager.getInstance().isEtherentEnabled() && stateIp == null) {
            stateIp = IpEthernetConf.getConnectionTypeEther();
        }

        if (stateIp.equals(DHCP)) {
            initDataDHCP();
            etPort.setImeOptions(6);
            switchConnectionType.setChecked(false);
            switchConnectionType.setText(DHCP);
            containerDns.setVisibility(View.GONE);
            disableComponents(false, R.color.des_color);
        } else {
            initDataStatic();
            etPort.setImeOptions(5);
            switchConnectionType.setChecked(true);
            switchConnectionType.setText(STATIC);
            containerDns.setVisibility(View.VISIBLE);
            disableComponents(true, R.color.transparent);
        }

        switchConnectionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterTimer();
                if (switchConnectionType.isChecked()) {
                    switchConnectionType.setText(STATIC);
                    containerDns.setVisibility(View.VISIBLE);
                    disableComponents(true, R.color.transparent);
                } else {
                    switchConnectionType.setText(DHCP);
                    containerDns.setVisibility(View.GONE);
                    disableComponents(false, R.color.des_color);
                }
            }
        });

        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        operatingEditTextPORT();
        operatingEditTextIP();
        operatingEditTextMA();
        operatingEditTextGA();
        operatingEditTextDNS();

        etIp4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    etIp4.clearFocus();
                    etMask4.requestFocus();
                }
                return false;
            }
        });

        etMask4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    etMask4.clearFocus();
                    etGateway4.requestFocus();
                }
                return false;
            }
        });

        counterTimer();
    }
    public void checkConnection(){
        final ProgressDialog progressDialog = new ProgressDialog(ConfigRed.this);
            if(switchConnection.getText().equals("ACTIVADO")){
                progressDialog.setTitle("Verificando conexion"); // Setting Title
                progressDialog.setMessage("Conectando a  red LAN.."); // Setting Message
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isConected()){
                            EthernetManager.getInstance().setEtherentEnabled(false);
                            switchConnection.setChecked(false);
                            wifiManager.setWifiEnabled(true);
                            switchConnection.setText(DESACTIVADO);
                        }
                    }
                }, 5000);
            }else {
                progressDialog.setTitle("Verificando conexion"); // Setting Title
                progressDialog.setMessage("Conectando a una red WIFI.."); // Setting Message
            }
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if(!isConected()){
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            }).start();

    }

    public void counterTimer(){
        if (StartAppDATAFAST.isInit){
            if(counterTimer != null){
                counterTimer.countDownTimer.cancel();
                counterTimer.countDownTimer.start();
            }else {
                counterTimer = new CounterTimer(this);
                counterTimer.counterDownTimer();
            }
        }
    }
    public boolean isConected(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    @Override
    protected void back() {
        super.back();
        inputMethodManager.hideSoftInputFromInputMethod(getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inputMethodManager.hideSoftInputFromInputMethod(getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    public void mapObjects() {

        etPort = findViewById(R.id.et_Port);

        containerDns = findViewById(R.id.containerDns);

        tvIp1 = findViewById(R.id.tvIp1);
        tvIp2 = findViewById(R.id.tvIp2);
        tvIp3 = findViewById(R.id.tvIp3);
        tvIp4 = findViewById(R.id.tvIp4);

        etIp1 = findViewById(R.id.etIp1);
        etIp2 = findViewById(R.id.etIp2);
        etIp3 = findViewById(R.id.etIp3);
        etIp4 = findViewById(R.id.etIp4);

        tvMask1 = findViewById(R.id.tvMask1);
        tvMask2 = findViewById(R.id.tvMask2);
        tvMask3 = findViewById(R.id.tvMask3);
        tvMask4 = findViewById(R.id.tvMask4);

        etMask1 = findViewById(R.id.etMask1);
        etMask2 = findViewById(R.id.etMask2);
        etMask3 = findViewById(R.id.etMask3);
        etMask4 = findViewById(R.id.etMask4);

        tvGateway1 = findViewById(R.id.tvGateway1);
        tvGateway2 = findViewById(R.id.tvGateway2);
        tvGateway3 = findViewById(R.id.tvGateway3);
        tvGateway4 = findViewById(R.id.tvGateway4);

        etGateway1 = findViewById(R.id.etGateway1);
        etGateway2 = findViewById(R.id.etGateway2);
        etGateway3 = findViewById(R.id.etGateway3);
        etGateway4 = findViewById(R.id.etGateway4);

        tvDns1 = findViewById(R.id.tvDns1);
        tvDns2 = findViewById(R.id.tvDns2);
        tvDns3 = findViewById(R.id.tvDns3);
        tvDns4 = findViewById(R.id.tvDns4);

        etDns1 = findViewById(R.id.etDns1);
        etDns2 = findViewById(R.id.etDns2);
        etDns3 = findViewById(R.id.etDns3);
        etDns4 = findViewById(R.id.etDns4);

    }

    private void disableComponents(boolean isEnable, int color) {
        tvIp1.setEnabled(isEnable);
        tvIp2.setEnabled(isEnable);
        tvIp3.setEnabled(isEnable);
        tvIp4.setEnabled(isEnable);

        etIp1.setEnabled(isEnable);
        etIp1.setBackgroundColor(getResources().getColor(color));
        etIp2.setEnabled(isEnable);
        etIp2.setBackgroundColor(getResources().getColor(color));
        etIp3.setEnabled(isEnable);
        etIp3.setBackgroundColor(getResources().getColor(color));
        etIp4.setEnabled(isEnable);
        etIp4.setBackgroundColor(getResources().getColor(color));

        tvMask1.setEnabled(isEnable);
        tvMask2.setEnabled(isEnable);
        tvMask3.setEnabled(isEnable);
        tvMask4.setEnabled(isEnable);

        etMask1.setEnabled(isEnable);
        etMask1.setBackgroundColor(getResources().getColor(color));
        etMask2.setEnabled(isEnable);
        etMask2.setBackgroundColor(getResources().getColor(color));
        etMask3.setEnabled(isEnable);
        etMask3.setBackgroundColor(getResources().getColor(color));
        etMask4.setEnabled(isEnable);
        etMask4.setBackgroundColor(getResources().getColor(color));

        tvGateway1.setEnabled(isEnable);
        tvGateway2.setEnabled(isEnable);
        tvGateway3.setEnabled(isEnable);
        tvGateway4.setEnabled(isEnable);

        etGateway1.setEnabled(isEnable);
        etGateway1.setBackgroundColor(getResources().getColor(color));
        etGateway2.setEnabled(isEnable);
        etGateway2.setBackgroundColor(getResources().getColor(color));
        etGateway3.setEnabled(isEnable);
        etGateway3.setBackgroundColor(getResources().getColor(color));
        etGateway4.setEnabled(isEnable);
        etGateway4.setBackgroundColor(getResources().getColor(color));

        tvDns1.setEnabled(isEnable);
        tvDns2.setEnabled(isEnable);
        tvDns3.setEnabled(isEnable);
        tvDns4.setEnabled(isEnable);

        etDns1.setEnabled(isEnable);
        etDns1.setBackgroundColor(getResources().getColor(color));
        etDns2.setEnabled(isEnable);
        etDns2.setBackgroundColor(getResources().getColor(color));
        etDns3.setEnabled(isEnable);
        etDns3.setBackgroundColor(getResources().getColor(color));
        etDns4.setEnabled(isEnable);
        etDns4.setBackgroundColor(getResources().getColor(color));

    }

    private int getListeningPort() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("config_ip", Context.MODE_PRIVATE);
        return Integer.parseInt(preferences.getString("port", "9999"));
    }

    public void initDataDHCP() {
        String[] datos, mask, gateway, ip;

        if (isWifiConnected()) {
            Logger.information("ConfigRed.java -> Ingreso por wifi");
            datos = UtilNetwork.getWifi(getApplicationContext(), false);
            for (int i = 0; i < datos.length ; i++) {
                Logger.information("Datos ConfigRed wifi "+i+" :"+datos[i]);
            }
            mask = datos[0].split("\\.");
            gateway = datos[3].split("\\.");
            ip = UtilNetwork.getIPAddress(true).split("\\.");

        } else {
            Logger.information("ConfigRed.java -> Ingreso por ethernet");
            datos = UtilNetwork.getWifi(getApplicationContext(), true);
            for (int i = 0; i < datos.length ; i++) {
                Logger.information("Datos ConfigRed ethernet "+i+" :"+datos[i]);
            }
            ip = UtilNetwork.getIPAddress(true).split("\\.");
            mask = datos[1].split("\\.");
            gateway = datos[3].split("\\.");
        }

        for(int i = 0; i < datos.length; i++){
            System.out.println("PASO -> "+ datos[i]);
        }

        etPort.setText(String.valueOf(getListeningPort()));

        etIp1.setText(ip[0]);
        etIp2.setText(ip[1]);
        etIp3.setText(ip[2]);
        etIp4.setText(ip[3]);

        etMask1.setText(mask[0]);
        etMask2.setText(mask[1]);
        etMask3.setText(mask[2]);
        etMask4.setText(mask[3]);

        etGateway1.setText(gateway[0]);
        etGateway2.setText(gateway[1]);
        etGateway3.setText(gateway[2]);
        etGateway4.setText(gateway[3]);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        if (switchConnectionType.isChecked()){
            etIp4.requestFocus();
        }else {
            etPort.requestFocus();
        }

    }

    public void initDataStatic() {
        String[] datos, mask, gateway, ip, dns;

        if (isWifiConnected()) {
            Logger.information("ConfigRed.java -> Ingreso por wifi");
            datos = UtilNetwork.getNetInformation(getApplicationContext(), false);
            for (int i = 0; i < datos.length ; i++) {
                Logger.information("Datos ConfigRed wifi "+i+" :"+datos[i]);
            }
            ip = datos[0].split("\\.");
            dns = datos[1].split("\\.");
            gateway = datos[2].split("\\.");
            mask = datos[3].split("\\.");
        } else {
            Logger.information("ConfigRed.java -> Ingreso por ethernet");
            datos = UtilNetwork.getNetInformation(getApplicationContext(), true);
            for (int i = 0; i < datos.length ; i++) {
                Logger.information("Datos ConfigRed ethernet "+i+" :"+datos[i]);
            }
            ip = datos[0].split("\\.");
            dns = datos[1].split("\\.");
            gateway = datos[2].split("\\.");
            mask = datos[3].split("\\.");
        }

        for(int i = 0; i < datos.length; i++){
            System.out.println("PASO -> "+ datos[i]);
        }

        etPort.setText(String.valueOf(getListeningPort()));

        etIp1.setText(ip[0]);
        etIp2.setText(ip[1]);
        etIp3.setText(ip[2]);
        etIp4.setText(ip[3]);

        etMask1.setText(mask[0]);
        etMask2.setText(mask[1]);
        etMask3.setText(mask[2]);
        etMask4.setText(mask[3]);

        etGateway1.setText(gateway[0]);
        etGateway2.setText(gateway[1]);
        etGateway3.setText(gateway[2]);
        etGateway4.setText(gateway[3]);

        etDns1.setText(dns[0]);
        etDns2.setText(dns[1]);
        etDns3.setText(dns[2]);
        etDns4.setText(dns[3]);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        if (switchConnectionType.isChecked()){
            etIp4.requestFocus();
        }else {
            etPort.requestFocus();
        }

    }

    private String[] getDataConnection(){
        String[] dataCon = {concatIP(), concatMask(), concatGateway(), concatDns()};
        for(String data : dataCon){
            if (data.equals("")){
                dataCon = null;
                break;
            }
        }
        return dataCon;
    }

    private boolean setConnection(String[] dataConnection, boolean staticConnection){
        if (isWifiConnected()) {
            try {
                if (staticConnection) {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                        IpWifiConf.setStaticIpConfiguration(getApplicationContext(), dataConnection[0], dataConnection[1], dataConnection[2], dataConnection[3]);
                    }else {
                        PosWifiManager wifiManager = PosWifiManager.getInstance();
                        WifiSsidInfo wifiSsidInfo = new WifiSsidInfo();
                        wifiSsidInfo.setConnectionType(WifiSsidInfo.NetType.STATIC_IP);
                        WifiSsidInfo.StaticIP staticIP = new WifiSsidInfo.StaticIP(dataConnection[0], dataConnection[3], dataConnection[2], "24");
                        wifiSsidInfo.setStaticIpConfigs(staticIP);
                        wifiManager.setCurrentSsidConfigs(wifiSsidInfo);

                    }
                } else {
                    IpWifiConf.wifiDhcp(getApplicationContext());
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (EthernetManager.getInstance().isEtherentEnabled()) {
            try {
                if (staticConnection) {
                    IpEthernetConf.setConnectionStaticIP(dataConnection[0], dataConnection[3], dataConnection[1], dataConnection[2]);
                } else {
                    IpEthernetConf.etherDhcp();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void save() {
        boolean change = false;
        boolean invalidDataConnection = false;
        if (switchConnectionType.isChecked()) {
            inputMethodManager.hideSoftInputFromInputMethod(getWindow().getCurrentFocus().getWindowToken(), 0);
            String[] dataConnection = getDataConnection();
            if (dataConnection != null) {
                change = setConnection(dataConnection, true);
            } else {
                UIUtils.toast(ConfigRed.this, R.drawable.ic_launcher_1, "Debe Llenar todos los datos", Toast.LENGTH_SHORT);
                invalidDataConnection = true;
            }
        }else {
            change = setConnection(null, false);
        }

        if (change) {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("config_ip", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("port", etPort.getText().toString());
            edit.apply();

            UIUtils.startResult(ConfigRed.this, true, "DATOS DE RED ACTUALIZADOS", false);
        } else if (!invalidDataConnection) {
            UIUtils.startResult(ConfigRed.this, false, "ERROR AL ACTUALIZAR DATOS", false);
        }

    }

    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null) && (cm.getActiveNetworkInfo() != null) && (cm.getActiveNetworkInfo().getType() == TYPE_WIFI);
    }

    public String concatIP() {
        String ret = "";
        if (etIp1.getText().toString().equals("") || etIp2.getText().toString().equals("") ||
                etIp3.getText().toString().equals("") || etIp4.getText().toString().equals("")) {
            return "";
        } else {
            ret = etIp1.getText().toString() + "." + etIp2.getText().toString() + "." + etIp3.getText().toString() + "." + etIp4.getText().toString();
            return ret;
        }
    }

    public String concatMask() {
        String ret = "";
        if (etMask1.getText().toString().equals("") || etMask2.getText().toString().equals("") ||
                etMask3.getText().toString().equals("") || etMask4.getText().toString().equals("")) {
            return "";
        } else {
            ret = etMask1.getText().toString() + "." + etMask2.getText().toString() + "." + etMask3.getText().toString() + "." + etMask4.getText().toString();
            return ret;
        }
    }

    public String concatGateway() {
        String ret = "";
        if (etGateway1.getText().toString().equals("") || etGateway2.getText().toString().equals("") ||
                etGateway3.getText().toString().equals("") || etGateway4.getText().toString().equals("")) {
            return "";
        } else {
            ret = etGateway1.getText().toString() + "." + etGateway2.getText().toString() + "." + etGateway3.getText().toString() + "." + etGateway4.getText().toString();
            return ret;
        }
    }

    public String concatDns() {
        String ret = "";
        if (etDns1.getText().toString().equals("") || etDns2.getText().toString().equals("") ||
                etDns3.getText().toString().equals("") || etDns4.getText().toString().equals("")) {
            return "8.8.8.8";
        } else {
            ret = etDns1.getText().toString() + "." + etDns2.getText().toString() + "." + etDns3.getText().toString() + "." + etDns4.getText().toString();
            return ret;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //PORT
            case R.id.et_Port:
                counterTimer();
                etPort.requestFocus();
                etPort.setSelection(etPort.getText().length());
                inputMethodManager.showSoftInput(etPort, 0);
                break;
            //IP
            case R.id.tvIp1:
                counterTimer();
                etIp1.requestFocus();
                etIp1.setSelection(etIp1.getText().length());
                inputMethodManager.showSoftInput(etIp1, 0);
                break;
            case R.id.tvIp2:
                counterTimer();
                etIp2.requestFocus();
                etIp2.setSelection(etIp2.getText().length());
                inputMethodManager.showSoftInput(etIp2, 0);
                break;
            case R.id.tvIp3:
                counterTimer();
                etIp3.requestFocus();
                etIp3.setSelection(etIp3.getText().length());
                inputMethodManager.showSoftInput(etIp3, 0);
                break;
            case R.id.tvIp4:
                counterTimer();
                etIp4.requestFocus();
                etIp4.setSelection(etIp4.getText().length());
                inputMethodManager.showSoftInput(etIp4, 0);
                break;
            //MASK
            case R.id.tvMask1:
                counterTimer();
                etMask1.requestFocus();
                etMask1.setSelection(etMask1.getText().length());
                inputMethodManager.showSoftInput(etMask1, 0);
                break;
            case R.id.tvMask2:
                counterTimer();
                etMask2.requestFocus();
                etMask2.setSelection(etMask2.getText().length());
                inputMethodManager.showSoftInput(etMask2, 0);
                break;
            case R.id.tvMask3:
                counterTimer();
                etMask3.requestFocus();
                etMask3.setSelection(etMask3.getText().length());
                inputMethodManager.showSoftInput(etMask3, 0);
                break;
            case R.id.tvMask4:
                counterTimer();
                etMask4.requestFocus();
                etMask4.setSelection(etMask4.getText().length());
                inputMethodManager.showSoftInput(etMask4, 0);
                break;
            //GATEWAY
            case R.id.tvGateway1:
                counterTimer();
                etGateway1.requestFocus();
                etGateway1.setSelection(etGateway1.getText().length());
                inputMethodManager.showSoftInput(etGateway1, 0);
                break;
            case R.id.tvGateway2:
                counterTimer();
                etGateway2.requestFocus();
                etGateway2.setSelection(etGateway2.getText().length());
                inputMethodManager.showSoftInput(etGateway2, 0);
                break;
            case R.id.tvGateway3:
                counterTimer();
                etGateway3.requestFocus();
                etGateway3.setSelection(etGateway3.getText().length());
                inputMethodManager.showSoftInput(etGateway3, 0);
                break;
            case R.id.tvGateway4:
                counterTimer();
                etGateway4.requestFocus();
                etGateway4.setSelection(etGateway4.getText().length());
                inputMethodManager.showSoftInput(etGateway4, 0);
                break;
            //DNS
            case R.id.tvDns1:
                counterTimer();
                etDns1.requestFocus();
                etDns1.setSelection(etDns1.getText().length());
                inputMethodManager.showSoftInput(etDns1, 0);
                break;
            case R.id.tvDns2:
                counterTimer();
                etDns2.requestFocus();
                etDns2.setSelection(etDns2.getText().length());
                inputMethodManager.showSoftInput(etDns2, 0);
                break;
            case R.id.tvDns3:
                counterTimer();
                etDns3.requestFocus();
                etDns3.setSelection(etDns3.getText().length());
                inputMethodManager.showSoftInput(etDns3, 0);
                break;
            case R.id.tvDns4:
                counterTimer();
                etDns4.requestFocus();
                etDns4.setSelection(etDns4.getText().length());
                inputMethodManager.showSoftInput(etDns4, 0);
                break;
        }
    }

    private void operatingEditTextPORT(){
        etPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    boolean borrado = false;
    int lenTxt = 0;
    private String mTextIP1;
    private String mTextIP2;
    private String mTextIP3;
    private String mTextIP4;
    private SharedPreferences mPreferences;

    private void operatingEditTextIP() {

        mPreferences = getApplicationContext().getSharedPreferences("config_IP", Context.MODE_PRIVATE);

        etIp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextIP1 = s.toString().substring(0, s.length() - 1);
                            etIp1.setText(mTextIP1);
                        } else {
                            mTextIP1 = s.toString().trim();
                        }
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_FIRST", mTextIP1.length());
                        editor.apply();

                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s.length() > 0) {
                    if (Integer.parseInt(String.valueOf(s)) > 255) {
                        etIp1.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2 && Integer.parseInt(String.valueOf(s)) <= 255) {
                    etIp2.setFocusable(true);
                    etIp2.requestFocus();
                }
            }
        });

        etIp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextIP2 = s.toString().substring(0, s.length() - 1);
                            etIp2.setText(mTextIP2);
                        } else {
                            mTextIP2 = s.toString().trim();
                        }
                        if (Integer.parseInt(mTextIP2) > 255) {
                            //TODO  zq
                            return;
                        }
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_SECOND", mTextIP2.length());
                        editor.apply();

                        if (s.length() > 2) {
                            etIp3.setFocusable(true);
                            etIp3.requestFocus();
                        }
                    }
                }

                if(etIp2.getText().toString().length() == 0){
                    etIp1.setFocusable(true);
                    etIp1.requestFocus();
                    etIp1.setSelection(etIp1.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etIp1.getText().toString())
                        && etIp1.length() > 1 && borrado) {
                    borrado = false;
                    etIp1.setFocusable(true);
                    etIp1.requestFocus();
                    etIp1.setSelection(etIp1.getText().length());
                }*/

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxt = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxt) {
                    borrado = true;
                }
                if (etIp2.getText().length() > 0) {
                    if (Integer.parseInt(etIp2.getText().toString()) > 255) {
                        borrado = false;
                        etIp2.setText("");
                    }
                }
            }
        });

        etIp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextIP3 = s.toString().substring(0, s.length() - 1);
                            etIp3.setText(mTextIP3);
                        } else {
                            mTextIP3 = s.toString().trim();
                        }

                        if (Integer.parseInt(mTextIP3) > 255) {
                            //TODO  zq
                            return;
                        }

                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_THIRD", mTextIP3.length());
                        editor.apply();

                        if (s.length() > 2) {
                            etIp4.setFocusable(true);
                            etIp4.requestFocus();
                        }
                    }
                }

                if(etIp3.getText().toString().length() == 0){
                    etIp2.setFocusable(true);
                    etIp2.requestFocus();
                    etIp2.setSelection(etIp2.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etIp2.getText().toString())
                        && etIp2.length() > 1 && borrado) {
                    borrado = false;
                    etIp2.setFocusable(true);
                    etIp2.requestFocus();
                    etIp2.setSelection(etIp2.getText().length());
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxt = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxt) {
                    borrado = true;
                }
                if (etIp3.getText().length() > 0) {
                    if (Integer.parseInt(etIp3.getText().toString()) > 255) {
                        borrado = false;
                        etIp3.setText("");
                    }
                }
            }
        });

        etIp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    mTextIP4 = s.toString().trim();

                    if (Integer.parseInt(mTextIP4) > 255) {
                        //TODO  zq
                        return;
                    }

                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putInt("IP_FOURTH", mTextIP4.length());
                    editor.apply();
                }

                if(etIp4.getText().toString().length() == 0){
                    etIp3.setFocusable(true);
                    etIp3.requestFocus();
                    etIp3.setSelection(etIp3.getText().length());
                }

               /* if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etIp3.getText().toString())
                        && etIp3.length() > 1 && borrado) {
                    borrado = false;
                    etIp3.setFocusable(true);
                    etIp3.requestFocus();
                    etIp3.setSelection(etIp3.getText().length());
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxt = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxt) {
                    borrado = true;
                }
                if (etIp4.getText().length() > 0) {
                    if (Integer.parseInt(etIp4.getText().toString()) > 255) {
                        borrado = false;
                        etIp4.setText("");
                    }
                }
            }
        });

        etIp4.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean action=false;
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    onClick(tvMask4);
                    action = true;
                }
                return action;
            }
        });
    }


    boolean borradoM = false;
    int lenTxtM = 0;
    private String mTextMA1;
    private String mTextMA2;
    private String mTextMA3;
    private String mTextMA4;
    private SharedPreferences mPreferencesMA;

    private void operatingEditTextMA() {

        mPreferencesMA = getApplicationContext().getSharedPreferences("config_MASK", Context.MODE_PRIVATE);

        etMask1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextMA1 = s.toString().substring(0, s.length() - 1);
                            etMask1.setText(mTextMA1);
                        } else {
                            mTextMA1 = s.toString().trim();
                        }
                        SharedPreferences.Editor editor = mPreferencesMA.edit();
                        editor.putInt("IP_FIRST", mTextMA1.length());
                        editor.apply();

                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s.length() > 0) {
                    if (Integer.parseInt(String.valueOf(s)) > 255) {
                        etMask1.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2 && Integer.parseInt(String.valueOf(s)) <= 255) {
                    etMask2.setFocusable(true);
                    etMask2.requestFocus();
                }
            }
        });

        etMask2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextMA2 = s.toString().substring(0, s.length() - 1);
                            etMask2.setText(mTextMA2);
                        } else {
                            mTextMA2 = s.toString().trim();
                        }
                        if (Integer.parseInt(mTextMA2) > 255) {
                            //TODO  zq
                            return;
                        }
                        SharedPreferences.Editor editor = mPreferencesMA.edit();
                        editor.putInt("IP_SECOND", mTextMA2.length());
                        editor.apply();

                        if (s.length() > 2) {
                            etMask3.setFocusable(true);
                            etMask3.requestFocus();
                        }
                    }
                }

                if(etMask2.getText().toString().length() == 0){
                    etMask1.setFocusable(true);
                    etMask1.requestFocus();
                    etMask1.setSelection(etMask1.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etMask1.getText().toString())
                        && etMask1.length() > 1 && borradoM) {
                    borradoM = false;
                    etMask1.setFocusable(true);
                    etMask1.requestFocus();
                    etMask1.setSelection(etMask1.getText().length());
                }*/

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtM = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtM) {
                    borradoM = true;
                }
                if (etMask2.getText().length() > 0) {
                    if (Integer.parseInt(etMask2.getText().toString()) > 255) {
                        borradoM = false;
                        etMask2.setText("");
                    }
                }
            }
        });

        etMask3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextMA3 = s.toString().substring(0, s.length() - 1);
                            etMask3.setText(mTextMA3);
                        } else {
                            mTextMA3 = s.toString().trim();
                        }

                        if (Integer.parseInt(mTextMA3) > 255) {
                            //TODO  zq
                            return;
                        }

                        SharedPreferences.Editor editor = mPreferencesMA.edit();
                        editor.putInt("IP_THIRD", mTextMA3.length());
                        editor.apply();

                        if (s.length() > 2) {
                            etMask4.setFocusable(true);
                            etMask4.requestFocus();
                        }
                    }
                }

                if(etMask3.getText().toString().length() == 0){
                    etMask2.setFocusable(true);
                    etMask2.requestFocus();
                    etMask2.setSelection(etMask2.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etMask2.getText().toString())
                        && etMask2.length() > 1 && borradoM) {
                    borradoM = false;
                    etMask2.setFocusable(true);
                    etMask2.requestFocus();
                    etMask2.setSelection(etMask2.getText().length());
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtM = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtM) {
                    borradoM = true;
                }
                if (etMask3.getText().length() > 0) {
                    if (Integer.parseInt(etMask3.getText().toString()) > 255) {
                        borradoM = false;
                        etMask3.setText("");
                    }
                }
            }
        });

        etMask4.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    mTextMA4 = s.toString().trim();

                    if (Integer.parseInt(mTextMA4) > 255) {
                        //TODO  zq
                        return;
                    }

                    SharedPreferences.Editor editor = mPreferencesMA.edit();
                    editor.putInt("IP_FOURTH", mTextMA4.length());
                    editor.apply();
                }

                if(etMask4.getText().toString().length() == 0){
                    etMask3.setFocusable(true);
                    etMask3.requestFocus();
                    etMask3.setSelection(etMask3.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etMask3.getText().toString())
                        && etMask3.length() > 1 && borradoM) {
                    borradoM = false;
                    etMask3.setFocusable(true);
                    etMask3.requestFocus();
                    etMask3.setSelection(etMask3.getText().length());
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtM = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtM) {
                    borradoM = true;
                }
                if (etMask4.getText().length() > 0) {
                    if (Integer.parseInt(etMask4.getText().toString()) > 255) {
                        borradoM = false;
                        etMask4.setText("");
                    }
                }
            }
        });

        etMask4.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean action=false;
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    onClick(tvGateway4);
                    action = true;
                }
                return action;
            }
        });
    }


    boolean borradoG = false;
    int lenTxtG = 0;
    private String mTextGA1;
    private String mTextGA2;
    private String mTextGA3;
    private String mTextGA4;
    private SharedPreferences mPreferencesGA;

    private void operatingEditTextGA() {

        mPreferencesGA = getApplicationContext().getSharedPreferences("config_MASK", Context.MODE_PRIVATE);

        etGateway1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextGA1 = s.toString().substring(0, s.length() - 1);
                            etGateway1.setText(mTextGA1);
                        } else {
                            mTextGA1 = s.toString().trim();
                        }
                        SharedPreferences.Editor editor = mPreferencesGA.edit();
                        editor.putInt("IP_FIRST", mTextGA1.length());
                        editor.apply();

                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s.length() > 0) {
                    if (Integer.parseInt(String.valueOf(s)) > 255) {
                        etGateway1.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2 && Integer.parseInt(String.valueOf(s)) <= 255) {
                    etGateway2.setFocusable(true);
                    etGateway2.requestFocus();
                }
            }
        });

        etGateway2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextGA2 = s.toString().substring(0, s.length() - 1);
                            etGateway2.setText(mTextGA2);
                        } else {
                            mTextGA2 = s.toString().trim();
                        }
                        if (Integer.parseInt(mTextGA2) > 255) {
                            //TODO  zq
                            return;
                        }
                        SharedPreferences.Editor editor = mPreferencesGA.edit();
                        editor.putInt("IP_SECOND", mTextGA2.length());
                        editor.apply();

                        if (s.length() > 2) {
                            etGateway3.setFocusable(true);
                            etGateway3.requestFocus();
                        }
                    }
                }

                if(etGateway2.getText().toString().length() == 0){
                    etGateway1.setFocusable(true);
                    etGateway1.requestFocus();
                    etGateway1.setSelection(etGateway1.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etGateway1.getText().toString())
                        && etGateway1.length() > 1 && borradoG) {
                    borradoG = false;
                    etGateway1.setFocusable(true);
                    etGateway1.requestFocus();
                    etGateway1.setSelection(etGateway1.getText().length());
                }*/

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtG = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtG) {
                    borradoG = true;
                }
                if (etGateway2.getText().length() > 0) {
                    if (Integer.parseInt(etGateway2.getText().toString()) > 255) {
                        borradoG = false;
                        etGateway2.setText("");
                    }
                }
            }
        });

        etGateway3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextGA3 = s.toString().substring(0, s.length() - 1);
                            etGateway3.setText(mTextGA3);
                        } else {
                            mTextGA3 = s.toString().trim();
                        }

                        if (Integer.parseInt(mTextGA3) > 255) {
                            //TODO  zq
                            return;
                        }

                        SharedPreferences.Editor editor = mPreferencesGA.edit();
                        editor.putInt("IP_THIRD", mTextGA3.length());
                        editor.apply();

                        if (s.length() > 2) {
                            etGateway4.setFocusable(true);
                            etGateway4.requestFocus();
                        }
                    }
                }

                if(etGateway3.getText().toString().length() == 0){
                    etGateway2.setFocusable(true);
                    etGateway2.requestFocus();
                    etGateway2.setSelection(etGateway2.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etGateway2.getText().toString())
                        && etGateway2.length() > 1 && borradoG) {
                    borradoG = false;
                    etGateway2.setFocusable(true);
                    etGateway2.requestFocus();
                    etGateway2.setSelection(etGateway2.getText().length());
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtG = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtG) {
                    borradoG = true;
                }
                if (etGateway3.getText().length() > 0) {
                    if (Integer.parseInt(etGateway3.getText().toString()) > 255) {
                        borradoG = false;
                        etGateway3.setText("");
                    }
                }
            }
        });

        etGateway4.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    mTextGA4 = s.toString().trim();

                    if (Integer.parseInt(mTextGA4) > 255) {
                        //TODO  zq
                        return;
                    }

                    SharedPreferences.Editor editor = mPreferencesGA.edit();
                    editor.putInt("IP_FOURTH", mTextGA4.length());
                    editor.apply();
                }

                if(etGateway4.getText().toString().length() == 0){
                    etGateway3.setFocusable(true);
                    etGateway3.requestFocus();
                    etGateway3.setSelection(etGateway3.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etGateway3.getText().toString())
                        && etGateway3.length() > 1 && borradoG) {
                    borradoG = false;
                    etGateway3.setFocusable(true);
                    etGateway3.requestFocus();
                    etGateway3.setSelection(etGateway3.getText().length());
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtG = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtG) {
                    borradoG = true;
                }
                if (etGateway4.getText().length() > 0) {
                    if (Integer.parseInt(etGateway4.getText().toString()) > 255) {
                        borradoG = false;
                        etGateway4.setText("");
                    }
                }
            }
        });

        etGateway4.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean action=false;
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    onClick(tvDns4);
                    action = true;
                }
                return action;
            }
        });
    }

    boolean borradoD = false;
    int lenTxtD = 0;
    private String mTextDNS1;
    private String mTextDNS2;
    private String mTextDNS3;
    private String mTextDNS4;
    private SharedPreferences mPreferencesDNS;

    private void operatingEditTextDNS() {

        mPreferencesDNS = getApplicationContext().getSharedPreferences("config_DNS", Context.MODE_PRIVATE);

        etDns1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextDNS1 = s.toString().substring(0, s.length() - 1);
                            etDns1.setText(mTextDNS1);
                        } else {
                            mTextDNS1 = s.toString().trim();
                        }
                        SharedPreferences.Editor editor = mPreferencesDNS.edit();
                        editor.putInt("DNS_FIRST", mTextDNS1.length());
                        editor.apply();

                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s.length() > 0) {
                    if (Integer.parseInt(String.valueOf(s)) > 255) {
                        etDns1.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2 && Integer.parseInt(String.valueOf(s)) <= 255) {
                    etDns2.setFocusable(true);
                    etDns2.requestFocus();
                }
            }
        });

        etDns2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextDNS2 = s.toString().substring(0, s.length() - 1);
                            etDns2.setText(mTextDNS2);
                        } else {
                            mTextDNS2 = s.toString().trim();
                        }
                        if (Integer.parseInt(mTextDNS2) > 255) {
                            //TODO  zq
                            return;
                        }
                        SharedPreferences.Editor editor = mPreferencesDNS.edit();
                        editor.putInt("DNS_SECOND", mTextDNS2.length());
                        editor.apply();

                        if (s.length() > 2) {
                            etDns3.setFocusable(true);
                            etDns3.requestFocus();
                        }
                    }
                }

                if(etDns2.getText().toString().length() == 0){
                    etDns1.setFocusable(true);
                    etDns1.requestFocus();
                    etDns1.setSelection(etDns1.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etDns1.getText().toString())
                        && etDns1.length() > 1 && borradoD) {
                    borradoD = false;
                    etDns1.setFocusable(true);
                    etDns1.requestFocus();
                    etDns1.setSelection(etDns1.getText().length());
                }*/

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtD = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtD) {
                    borradoD = true;
                }
                if (etDns2.getText().length() > 0) {
                    if (Integer.parseInt(etDns2.getText().toString()) > 255) {
                        borradoD = false;
                        etDns2.setText("");
                    }
                }
            }
        });

        etDns3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mTextDNS3 = s.toString().substring(0, s.length() - 1);
                            etDns3.setText(mTextDNS3);
                        } else {
                            mTextDNS3 = s.toString().trim();
                        }

                        if (Integer.parseInt(mTextDNS3) > 255) {
                            //TODO  zq
                            return;
                        }

                        SharedPreferences.Editor editor = mPreferencesDNS.edit();
                        editor.putInt("DNS_THIRD", mTextDNS3.length());
                        editor.apply();

                        if (s.length() > 2) {
                            etDns4.setFocusable(true);
                            etDns4.requestFocus();
                        }
                    }
                }

                if(etDns3.getText().toString().length() == 0){
                    etDns2.setFocusable(true);
                    etDns2.requestFocus();
                    etDns2.setSelection(etDns2.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etDns2.getText().toString())
                        && etDns2.length() > 1 && borradoD) {
                    borradoD = false;
                    etDns2.setFocusable(true);
                    etDns2.requestFocus();
                    etDns2.setSelection(etDns2.getText().length());
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtD = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtD) {
                    borradoD = true;
                }
                if (etDns3.getText().length() > 0) {
                    if (Integer.parseInt(etDns3.getText().toString()) > 255) {
                        borradoD = false;
                        etDns3.setText("");
                    }
                }
            }
        });

        etDns4.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counterTimer();
                if (s != null && s.length() > 0) {
                    mTextDNS4 = s.toString().trim();

                    if (Integer.parseInt(mTextDNS4) > 255) {
                        //TODO  zq
                        return;
                    }

                    SharedPreferences.Editor editor = mPreferencesDNS.edit();
                    editor.putInt("DNS_FOURTH", mTextDNS4.length());
                    editor.apply();
                }

                if(etDns4.getText().toString().length() == 0){
                    etDns3.setFocusable(true);
                    etDns3.requestFocus();
                    etDns3.setSelection(etDns3.getText().length());
                }

                /*if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etDns3.getText().toString())
                        && etDns3.length() > 1 && borradoD) {
                    borradoD = false;
                    etDns3.setFocusable(true);
                    etDns3.requestFocus();
                    etDns3.setSelection(etDns3.getText().length());
                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lenTxtD = s.length();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < lenTxtD) {
                    borradoD = true;
                }
                if (etDns4.getText().length() > 0) {
                    if (Integer.parseInt(etDns4.getText().toString()) > 255) {
                        borradoD = false;
                        etDns4.setText("");
                    }
                }
            }
        });
    }

}
