package com.datafast.tools;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.android.newpos.pay.R;
import com.android.newpos.pay.StartAppDATAFAST;
import com.datafast.inicializacion.configuracioncomercio.ChequeoIPs;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.BaseActivity;

import static com.android.newpos.pay.StartAppDATAFAST.tablaIp;

public class ConfigTransactional extends BaseActivity{

    InputMethodManager inputMethodManager;
    Switch switchTls1, switchTls2;
    EditText port1, port2;
    EditText etIp1, etIp2, etIp3, etIp4;
    EditText et2Ip1, et2Ip2, et2Ip3, et2Ip4;
    String estado, nombreIP1, nombreIP2, portNom1, portNom2;;

    private CounterTimer counterTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_transactional);
        setNaviTitle("CONFIG TRANS");

        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        mapObjects();
        cargarInfoIP1();
        cargarInfoIP2();

        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTLS1();
                saveTLS2();
            }
        });

        /**
         * Se verifica si los puertos de las IP'S son los mismos para aplicar la configuración del TLS a ambas
         */
        switchTls1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (portNom1.equals(portNom2)){
                    switchTls2.setChecked(isChecked);
                }
            }
        });

        switchTls2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (portNom2.equals(portNom1)){
                    switchTls1.setChecked(isChecked);
                }
            }
        });

        if (StartAppDATAFAST.isInit){
            counterTimer = new CounterTimer(this);
            counterTimer.counterDownTimer();
        }

    }

    public void mapObjects() {

        switchTls1 = findViewById(R.id.sw_conf_ip);
        switchTls2 = findViewById(R.id.sw_conf_ip_2);

        port1 = findViewById(R.id.et_Port);
        port1.setKeyListener(null);
        port2 = findViewById(R.id.et_Port_2);
        port2.setKeyListener(null);

        etIp1 = findViewById(R.id.etIp1);
        etIp1.setKeyListener(null);
        etIp2 = findViewById(R.id.etIp2);
        etIp2.setKeyListener(null);
        etIp3 = findViewById(R.id.etIp3);
        etIp3.setKeyListener(null);
        etIp4 = findViewById(R.id.etIp4);
        etIp4.setKeyListener(null);

        et2Ip1 = findViewById(R.id.etIp1_2);
        et2Ip1.setKeyListener(null);
        et2Ip2 = findViewById(R.id.etIp2_2);
        et2Ip2.setKeyListener(null);
        et2Ip3 = findViewById(R.id.etIp3_2);
        et2Ip3.setKeyListener(null);
        et2Ip4 = findViewById(R.id.etIp4_2);
        et2Ip4.setKeyListener(null);

    }

    public void cargarInfoIP1(){
        tablaIp = ChequeoIPs.seleccioneIP(0);
        String[] ip = tablaIp.getIP_HOST().split("\\.");
        nombreIP1 = tablaIp.getNOMBRE_IP();
        portNom1 = tablaIp.getPUERTO();
        etIp1.setText(ip[0]);
        etIp2.setText(ip[1]);
        etIp3.setText(ip[2]);
        etIp4.setText(ip[3]);
        port1.setText(tablaIp.getPUERTO());
        if(PAYUtils.stringToBoolean(tablaIp.getTLS())){
            switchTls1.setChecked(true);
        }else{
            switchTls1.setChecked(false);
        }
    }

    public void cargarInfoIP2(){
        tablaIp = ChequeoIPs.seleccioneIP(1);
        port2.setText(tablaIp.getPUERTO());
        String[] ip = tablaIp.getIP_HOST().split("\\.");
        nombreIP2 = tablaIp.getNOMBRE_IP();
        portNom2 = tablaIp.getPUERTO();
        et2Ip1.setText(ip[0]);
        et2Ip2.setText(ip[1]);
        et2Ip3.setText(ip[2]);
        et2Ip4.setText(ip[3]);
        if(PAYUtils.stringToBoolean(tablaIp.getTLS())){
            switchTls2.setChecked(true);
        }else{
            switchTls2.setChecked(false);
        }
    }

    /**
     * Se verifica el estado del TLS de la IP1 y se hace el update a la Base de Datos
     */
    public void saveTLS1(){
        if(switchTls1.isChecked()){
            estado = "1";
            if(ChequeoIPs.updateSelectIps(new String[] {"TLS"}, new String[] {estado}, 0, ConfigTransactional.this)){
                UIUtils.startResult(ConfigTransactional.this, true, "DATOS TRANSACCIONALES ACTUALIZADOS", false);
            }else{
                UIUtils.startResult(ConfigTransactional.this, false, "ERROR AL ACTUALIZAR DATOS", false);
            }
        }else{
            estado = "0";
            if(ChequeoIPs.updateSelectIps(new String[] {"TLS"}, new String[] {estado}, 0, ConfigTransactional.this)){
                UIUtils.startResult(ConfigTransactional.this, true, "DATOS TRANSACCIONALES ACTUALIZADOS", false);
            }else{
                UIUtils.startResult(ConfigTransactional.this, false, "ERROR AL ACTUALIZAR DATOS", false);
            }
        }
        StartAppDATAFAST.listIPs = ChequeoIPs.selectIP(ConfigTransactional.this);
    }

    /**
     * Se verifica el estado del TLS de la IP2 y se hace el update a la Base de Datos
     */
    public void saveTLS2(){
        if(switchTls2.isChecked()){
            estado = "1";
            if(ChequeoIPs.updateSelectIps(new String[] {"TLS"}, new String[] {estado}, 1, ConfigTransactional.this)){
                UIUtils.startResult(ConfigTransactional.this, true, "DATOS TRANSACCIONALES ACTUALIZADOS", false);
            }else{
                UIUtils.startResult(ConfigTransactional.this, false, "ERROR AL ACTUALIZAR DATOS", false);
            }
        }else{
            estado = "0";
            if(ChequeoIPs.updateSelectIps(new String[] {"TLS"}, new String[] {estado}, 1, ConfigTransactional.this)){
                UIUtils.startResult(ConfigTransactional.this, true, "DATOS TRANSACCIONALES ACTUALIZADOS", false);
            }else{
                UIUtils.startResult(ConfigTransactional.this, false, "ERROR AL ACTUALIZAR DATOS", false);
            }
        }
        StartAppDATAFAST.listIPs = ChequeoIPs.selectIP(ConfigTransactional.this);
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
}
