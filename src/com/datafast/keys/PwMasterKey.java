package com.datafast.keys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.server.activity.ServerTCP;
import com.datafast.tools.CounterTimer;

import cn.desert.newpos.payui.UIUtils;

import static com.datafast.keys.InjectMasterKey.MASTERKEYIDX;
import static com.datafast.keys.InjectMasterKey.threreIsKey;

public class PwMasterKey extends AppCompatActivity {

    private Button btn_ok, btn_cnl;
    private EditText et_pw;
    private CounterTimer counterTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_master_key);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        et_pw = (EditText)findViewById(R.id.et_pw_mk);
        btn_ok = (Button) findViewById(R.id.btn_conf_mon);
        btn_cnl = (Button) findViewById(R.id.btn_cancel_mon);

        /*et_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) PwMasterKey.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_pw.getWindowToken(), 0);
            }
        });*/

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_pw.getText().length() != 0) {
                    Intent intent = new Intent();
                    intent.setClass(PwMasterKey.this, InjectMasterKey.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString("pw", et_pw.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                else{
                    UIUtils.toast(PwMasterKey.this, R.drawable.ic_launcher_1, getString(R.string.err_msg_pwmk), Toast.LENGTH_SHORT);
                }
            }
        });

        btn_cnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (threreIsKey(MASTERKEYIDX, "Debe cargar Master Key", PwMasterKey.this)){
                    UIUtils.startResult(PwMasterKey.this,false,"INYECCION MASTER KEY CANCELADA" ,false);
                }
            }
        });

        counterTimer = new CounterTimer(this);
        counterTimer.counterDownTimer();
    }

    public void onResume() {
        super.onResume();
        et_pw.setText("");
    }
}
