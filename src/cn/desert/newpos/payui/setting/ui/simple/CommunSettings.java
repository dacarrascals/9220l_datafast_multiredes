package cn.desert.newpos.payui.setting.ui.simple;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.menus.MenuAction;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.PAYUtils;

import java.util.Timer;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.BaseActivity;
import cn.desert.newpos.payui.setting.ui.SettingsFrags;

/**
 * Created by zhouqiang on 2017/11/15.
 */
public class CommunSettings extends BaseActivity implements View.OnClickListener {
    EditText commun_timeout;

    EditText commun_pub_port;
    EditText merchant_tid;
    RelativeLayout layout_termID_comm;
    TextView tv_label_termID;
    TextView tv_label_nii;
    EditText et_nii;
    EditText et_intentos;

    TextView txtTimeout, txtPort, txtTerminal, txtNii;
    TextView tvIp1, tvIp2, tvIp3, tvIp4;
    EditText etIp1, etIp2, etIp3, etIp4;

    InputMethodManager inputMethodManager;

    private TMConfig config;
    private boolean isOpen;
    private String key;
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_commun);

        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        txtTimeout = findViewById(R.id.txtTimeout);
        txtPort = findViewById(R.id.txtPuerto);
        txtTerminal = findViewById(R.id.txtTerminal);
        txtNii = findViewById(R.id.txtNii);

        tvIp1 = findViewById(R.id.tvIp1);
        tvIp2 = findViewById(R.id.tvIp2);
        tvIp3 = findViewById(R.id.tvIp3);
        tvIp4 = findViewById(R.id.tvIp4);

        etIp1 = findViewById(R.id.etIp1);
        etIp2 = findViewById(R.id.etIp2);
        etIp3 = findViewById(R.id.etIp3);
        etIp4 = findViewById(R.id.etIp4);

        if (SettingsFrags.JUMP_KEY == null) {
            setNaviTitle(getIntent().getExtras().getString(MenuAction.JUMP_KEY));
        } else {
            key = getIntent().getExtras().getString(SettingsFrags.JUMP_KEY);
            setNaviTitle(key);
        }
        config = TMConfig.getInstance();
        initData();

        operatingEditText();

        txtTimeout.setOnClickListener(CommunSettings.this);
        txtPort.setOnClickListener(CommunSettings.this);
        txtTerminal.setOnClickListener(CommunSettings.this);
        txtNii.setOnClickListener(CommunSettings.this);

        tvIp1.setOnClickListener(CommunSettings.this);
        tvIp2.setOnClickListener(CommunSettings.this);
        tvIp3.setOnClickListener(CommunSettings.this);
        tvIp4.setOnClickListener(CommunSettings.this);

        commun_timeout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    commun_timeout.setOnClickListener(null);
                }
                return false;
            }
        });

        commun_pub_port.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    commun_pub_port.clearFocus();
                    merchant_tid.requestFocus();
                }
                return false;
            }
        });

        merchant_tid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    merchant_tid.clearFocus();
                    et_nii.requestFocus();
                }
                return false;
            }
        });

        et_nii.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    inputMethodManager.hideSoftInputFromInputMethod(getWindow().getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });

        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (key.equals(DefinesDATAFAST.ITEM_TIPO_COMUNICACION)) {
                    //finish();
                } else {
                    save();
                }

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
        if(timer!=null){
            timer.cancel();
        }
    }

    private void initData() {

        switch (key) {
            case DefinesDATAFAST.ITEM_TIPO_COMUNICACION:
                layout_termID_comm = findViewById(R.id.layout_termID_comm);

                et_nii = findViewById(R.id.setting_com_timeout);
                commun_pub_port = findViewById(R.id.setting_com_public_port);
                merchant_tid = findViewById(R.id.merchant_tid);
                tv_label_termID = findViewById(R.id.tv_ter_id);
                tv_label_nii = findViewById(R.id.tv_timeout);
                et_intentos = findViewById(R.id.setting_com_intentos_de_conex);

                tv_label_nii.setText("NII");
                et_nii.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                merchant_tid.setVisibility(View.INVISIBLE);
                tv_label_termID.setVisibility(View.INVISIBLE);
                layout_termID_comm.setVisibility(View.INVISIBLE);

                break;

            default:
                layout_termID_comm = findViewById(R.id.layout_termID_comm);
                commun_timeout = findViewById(R.id.setting_com_timeout);
                //commun_pub_ip = findViewById(R.id.setting_com_public_ip);
                commun_pub_port = findViewById(R.id.setting_com_public_port);
                merchant_tid = findViewById(R.id.merchant_tid);
                et_intentos = findViewById(R.id.setting_com_intentos_de_conex);
                et_nii = findViewById(R.id.setting_com_nii);

                commun_timeout.setText(String.valueOf(config.getTimeout() / 1000));
                isOpen = true;
                setIPText(config.getIp().split("\\."));
                commun_pub_port.setText(config.getPort());
                commun_pub_port.setSelection(config.getPort().length());
                merchant_tid.setText(config.getTIDPolaris());
                merchant_tid.setSelection(config.getTIDPolaris().length());
                et_intentos.setText(String.valueOf(config.getIntentosConex()));
                et_nii.setText(config.getNii());
                et_nii.setSelection(config.getNii().length());
                break;
        }
    }


    boolean borrado = false;
    int lenTxt = 0;
    private String mText1;
    private String mText2;
    private String mText3;
    private String mText4;
    private SharedPreferences mPreferences;
    private void operatingEditText() {

        mPreferences = getApplicationContext().getSharedPreferences("config_IP",Context.MODE_PRIVATE);

        etIp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null && s.length() > 0) {
                    if (s.length() > 1 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            mText1 = s.toString().substring(0, s.length() - 1);
                            etIp1.setText(mText1);
                        } else {
                            mText1 = s.toString().trim();
                        }
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_FIRST", mText1.length());
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
                            mText2 = s.toString().substring(0, s.length() - 1);
                            etIp2.setText(mText2);
                        } else {
                            mText2 = s.toString().trim();
                        }
                        if (Integer.parseInt(mText2) > 255) {
                            //TODO  zq
                            return;
                        }
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_SECOND", mText2.length());
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
                            mText3 = s.toString().substring(0, s.length() - 1);
                            etIp3.setText(mText3);
                        } else {
                            mText3 = s.toString().trim();
                        }

                        if (Integer.parseInt(mText3) > 255) {
                            //TODO  zq
                            return;
                        }

                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putInt("IP_THIRD", mText3.length());
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
                    mText4 = s.toString().trim();

                    if (Integer.parseInt(mText4) > 255) {
                        //TODO  zq
                        return;
                    }

                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putInt("IP_FOURTH", mText4.length());
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

    public void setIPText(String[] ip){
        etIp1.setText(ip[0]);
        etIp2.setText(ip[1]);
        etIp3.setText(ip[2]);
        etIp4.setText(ip[3]);
        etIp4.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void save() {
        inputMethodManager.hideSoftInputFromInputMethod(getWindow().getCurrentFocus().getWindowToken(), 0);
        String ip = concatIP();
        String port = commun_pub_port.getText().toString();
        String timeout = commun_timeout.getText().toString();
        String tid = merchant_tid.getText().toString();
        String intentConex = et_intentos.getText().toString();
        String nii = et_nii.getText().toString();
        if (PAYUtils.isNullWithTrim(ip) ||
                PAYUtils.isNullWithTrim(tid) ||
                PAYUtils.isNullWithTrim(port) ||
                PAYUtils.isNullWithTrim(timeout) ||
                PAYUtils.isNullWithTrim(intentConex) ||
                PAYUtils.isNullWithTrim(nii)) {
            Toast.makeText(this, getString(R.string.data_null), Toast.LENGTH_SHORT).show();
            return;
        }
        config.setIp(ip)
                .setPort(port)
                .setTimeout(Integer.parseInt(timeout) * 1000)
                .setPubCommun(isOpen)
                .setTIDPolaris(tid)
                .setIntentosConex(Integer.parseInt(intentConex))
                .setNii(nii)
                .save();
        UIUtils.toast(this, R.drawable.ic_launcher_1, getString(R.string.save_success), Toast.LENGTH_SHORT);
        //hideKeyboard(CommunSettings.this);
        finish();
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.txtTimeout:
                commun_timeout.requestFocus();
                commun_timeout.setSelection(commun_timeout.getText().length());
                inputMethodManager.showSoftInput(commun_timeout, 0);
                break;

            case R.id.txtPuerto:
                commun_pub_port.requestFocus();
                commun_pub_port.setSelection(commun_pub_port.getText().length());
                inputMethodManager.showSoftInput(commun_pub_port, 0);
                break;

            case R.id.txtTerminal:
                merchant_tid.requestFocus();
                merchant_tid.setSelection(merchant_tid.getText().length());
                inputMethodManager.showSoftInput(merchant_tid, 0);
                break;

            case R.id.txtNii:
                et_nii.requestFocus();
                et_nii.setSelection(et_nii.getText().length());
                inputMethodManager.showSoftInput(et_nii, 0);
                break;

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

        }

    }
}