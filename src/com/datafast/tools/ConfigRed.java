package com.datafast.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.pinpad.cmd.CP.IpEthernetConf;
import com.datafast.pinpad.cmd.CP.IpWifiConf;
import com.datafast.server.activity.ServerTCP;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.net.eth.EthernetManager;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.BaseActivity;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class ConfigRed extends BaseActivity implements View.OnClickListener{

    TextView tvIp1, tvIp2, tvIp3, tvIp4;
    EditText etIp1, etIp2, etIp3, etIp4;

    TextView tvMask1, tvMask2, tvMask3, tvMask4, tvMask;
    EditText etMask1, etMask2, etMask3, etMask4;

    TextView tvGateway1, tvGateway2, tvGateway3, tvGateway4;
    EditText etGateway1, etGateway2, etGateway3, etGateway4;

    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_red);
        setNaviTitle("CONFIG RED POS");
        tvMask = findViewById(R.id.tv_Mask);

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


        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        operatingEditTextIP();
        operatingEditTextMA();
        operatingEditTextGA();

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
        String[] datos, mask, gateway, ip;

        if (isWifiConnected()) {

            datos = UtilNetwork.getWifi(getApplicationContext(), false);
            mask = datos[0].split("\\.");
            gateway = datos[3].split("\\.");
            ip = UtilNetwork.getIPAddress(true).split("\\.");

        } else {

            datos = UtilNetwork.getWifi(getApplicationContext(), true);
            ip = datos[0].split("\\.");
            mask = datos[1].split("\\.");
            gateway = datos[3].split("\\.");

        }

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
        boolean change = false;
        inputMethodManager.hideSoftInputFromInputMethod(getWindow().getCurrentFocus().getWindowToken(), 0);
        String ip = concatIP();
        String mask = concatMask();
        String gateway = concatGateway();

        if (!ip.equals("") && !mask.equals("") && !gateway.equals("")) {

                if (isWifiConnected()) {
                    try {
                        IpWifiConf.setStaticIpConfiguration(getApplicationContext(), ip, mask, gateway);
                        change = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (EthernetManager.getInstance().isEtherentEnabled()) {
                    try {
                        IpEthernetConf.setConnectionStaticIP(ip, mask, gateway);
                        change = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (change) {
                    UIUtils.startResult(ConfigRed.this,true,"DATOS DE RED ACTUALIZADOS",false);
                } else {
                    UIUtils.startResult(ConfigRed.this,false,"ERROR AL ACTUALIZAR DATOS",false);
                }

        } else {
            UIUtils.toast(ConfigRed.this, R.drawable.ic_launcher_1, "Debe Llenar todos los datos", Toast.LENGTH_SHORT);
        }

    }

    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
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


    boolean borrado = false;
    int lenTxt = 0;
    private String mTextIP1;
    private String mTextIP2;
    private String mTextIP3;
    private String mTextIP4;
    private SharedPreferences mPreferences;
    private void operatingEditTextIP() {

        mPreferences = getApplicationContext().getSharedPreferences("config_IP",Context.MODE_PRIVATE);

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
                if (s.length() > 0){
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

                if (start == 0 && s != null && s.length() == 0
                        && ! PAYUtils.isNullWithTrim(etIp1.getText().toString())
                        && etIp1.length() > 1 && borrado) {
                    borrado = false;
                    etIp1.setFocusable(true);
                    etIp1.requestFocus();
                    etIp1.setSelection(etIp1.getText().length());
                }

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

                if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etIp2.getText().toString())
                        && etIp2.length() > 1 && borrado) {
                    borrado = false;
                    etIp2.setFocusable(true);
                    etIp2.requestFocus();
                    etIp2.setSelection(etIp2.getText().length());
                }
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

                if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etIp3.getText().toString())
                        && etIp3.length() > 1 && borrado) {
                    borrado = false;
                    etIp3.setFocusable(true);
                    etIp3.requestFocus();
                    etIp3.setSelection(etIp3.getText().length());
                }
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
    }



    boolean borradoM = false;
    int lenTxtM = 0;
    private String mTextMA1;
    private String mTextMA2;
    private String mTextMA3;
    private String mTextMA4;
    private SharedPreferences mPreferencesMA;
    private void operatingEditTextMA() {

        mPreferencesMA = getApplicationContext().getSharedPreferences("config_MASK",Context.MODE_PRIVATE);

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
                if (s.length() > 0){
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

                if (start == 0 && s != null && s.length() == 0
                        && ! PAYUtils.isNullWithTrim(etMask1.getText().toString())
                        && etMask1.length() > 1 && borradoM) {
                    borradoM = false;
                    etMask1.setFocusable(true);
                    etMask1.requestFocus();
                    etMask1.setSelection(etMask1.getText().length());
                }

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

                if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etMask2.getText().toString())
                        && etMask2.length() > 1 && borradoM) {
                    borradoM = false;
                    etMask2.setFocusable(true);
                    etMask2.requestFocus();
                    etMask2.setSelection(etMask2.getText().length());
                }
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

                if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etMask3.getText().toString())
                        && etMask3.length() > 1 && borradoM) {
                    borradoM = false;
                    etMask3.setFocusable(true);
                    etMask3.requestFocus();
                    etMask3.setSelection(etMask3.getText().length());
                }
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
    }


    boolean borradoG = false;
    int lenTxtG = 0;
    private String mTextGA1;
    private String mTextGA2;
    private String mTextGA3;
    private String mTextGA4;
    private SharedPreferences mPreferencesGA;
    private void operatingEditTextGA() {

        mPreferencesGA = getApplicationContext().getSharedPreferences("config_MASK",Context.MODE_PRIVATE);

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
                if (s.length() > 0){
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

                if (start == 0 && s != null && s.length() == 0
                        && ! PAYUtils.isNullWithTrim(etGateway1.getText().toString())
                        && etGateway1.length() > 1 && borradoG) {
                    borradoG = false;
                    etGateway1.setFocusable(true);
                    etGateway1.requestFocus();
                    etGateway1.setSelection(etGateway1.getText().length());
                }

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

                if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etGateway2.getText().toString())
                        && etGateway2.length() > 1 && borradoG) {
                    borradoG = false;
                    etGateway2.setFocusable(true);
                    etGateway2.requestFocus();
                    etGateway2.setSelection(etGateway2.getText().length());
                }
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

                if (start == 0 && s != null && s.length() == 0
                        && !PAYUtils.isNullWithTrim(etGateway3.getText().toString())
                        && etGateway3.length() > 1 && borradoG) {
                    borradoG = false;
                    etGateway3.setFocusable(true);
                    etGateway3.requestFocus();
                    etGateway3.setSelection(etGateway3.getText().length());
                }
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
    }

}
