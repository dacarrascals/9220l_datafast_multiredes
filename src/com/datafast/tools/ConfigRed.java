package com.datafast.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.pinpad.cmd.CP.IpWifiConf;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.BaseActivity;

public class ConfigRed extends BaseActivity implements View.OnClickListener{

    TextView tvIp1, tvIp2, tvIp3, tvIp4;
    EditText etIp1, etIp2, etIp3, etIp4;

    TextView tvMask1, tvMask2, tvMask3, tvMask4;
    EditText etMask1, etMask2, etMask3, etMask4;

    TextView tvGateway1, tvGateway2, tvGateway3, tvGateway4;
    EditText etGateway1, etGateway2, etGateway3, etGateway4;

    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_red);
        setNaviTitle("CONFIG RED POS");

        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        mapObjects();

        initData();

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

        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

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

    }

    public void initData() {

        String[] datos = UtilNetwork.getWifi(getApplicationContext());
        String[] mask = datos[0].split("\\.");
        String[] gateway = datos[3].split("\\.");
        String[] ip = UtilNetwork.getIPAddress(true).split("\\.");

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
        etIp4.requestFocus();

    }

    private void save() {
        inputMethodManager.hideSoftInputFromInputMethod(getWindow().getCurrentFocus().getWindowToken(), 0);
        String ip = concatIP();
        String mask = concatMask();
        String gateway = concatGateway();

        if (!ip.equals("") && !mask.equals("") && !gateway.equals("")) {
            try {
                IpWifiConf.setStaticIpConfiguration(getApplicationContext(), ip, mask, gateway);
                UIUtils.toast(this, R.drawable.ic_launcher_1, getString(R.string.save_success), Toast.LENGTH_SHORT);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            UIUtils.toast(ConfigRed.this, R.drawable.ic_launcher_1, "Debe Llenar todos los datos", Toast.LENGTH_SHORT);
        }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //IP
            case R.id.tvIp1:
                etIp1.requestFocus();
                etIp1.setSelection(etIp1.getText().length());
                inputMethodManager.showSoftInput(etIp1, 0);
                break;
            case R.id.tvIp2:
                etIp2.requestFocus();
                etIp2.setSelection(etIp2.getText().length());
                inputMethodManager.showSoftInput(etIp2, 0);
                break;
            case R.id.tvIp3:
                etIp3.requestFocus();
                etIp3.setSelection(etIp3.getText().length());
                inputMethodManager.showSoftInput(etIp3, 0);
                break;
            case R.id.tvIp4:
                etIp4.requestFocus();
                etIp4.setSelection(etIp4.getText().length());
                inputMethodManager.showSoftInput(etIp4, 0);
                break;
            //MASK
            case R.id.tvMask1:
                etMask1.requestFocus();
                etMask1.setSelection(etMask1.getText().length());
                inputMethodManager.showSoftInput(etMask1, 0);
                break;
            case R.id.tvMask2:
                etMask2.requestFocus();
                etMask2.setSelection(etMask2.getText().length());
                inputMethodManager.showSoftInput(etMask2, 0);
                break;
            case R.id.tvMask3:
                etMask3.requestFocus();
                etMask3.setSelection(etMask3.getText().length());
                inputMethodManager.showSoftInput(etMask3, 0);
                break;
            case R.id.tvMask4:
                etMask4.requestFocus();
                etMask4.setSelection(etMask4.getText().length());
                inputMethodManager.showSoftInput(etMask4, 0);
                break;
            //GATEWAY
            case R.id.tvGateway1:
                etGateway1.requestFocus();
                etGateway1.setSelection(etGateway1.getText().length());
                inputMethodManager.showSoftInput(etGateway1, 0);
                break;
            case R.id.tvGateway2:
                etGateway2.requestFocus();
                etGateway2.setSelection(etGateway2.getText().length());
                inputMethodManager.showSoftInput(etGateway2, 0);
                break;
            case R.id.tvGateway3:
                etGateway3.requestFocus();
                etGateway3.setSelection(etGateway3.getText().length());
                inputMethodManager.showSoftInput(etGateway3, 0);
                break;
            case R.id.tvGateway4:
                etGateway4.requestFocus();
                etGateway4.setSelection(etGateway4.getText().length());
                inputMethodManager.showSoftInput(etGateway4, 0);
                break;
        }
    }
}
