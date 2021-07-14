package com.datafast.keys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.menus.menus;
import com.newpos.libpay.global.TMConfig;

import cn.desert.newpos.payui.UIUtils;

public class PwOperario extends AppCompatActivity {

    private Button btn_ok, btn_cnl;
    private EditText et_pw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_operario);

        et_pw = (EditText)findViewById(R.id.et_pw_operario);
        btn_ok = (Button) findViewById(R.id.btn_conf_mon);
        btn_cnl = (Button) findViewById(R.id.btn_cancel_mon);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_pw.getText().toString().equals(TMConfig.getInstance().getMasterPass())){
                    //UIUtils.toast(PwOperario.this, R.drawable.ic_launcher, getString(R.string.conf_msg_pwoperario), Toast.LENGTH_SHORT);
                    //TMConfig.getInstance().setPw_Operario(true);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(PwOperario.this, menus.class);
                    intent.putExtra(DefinesDATAFAST.DATO_MENU, DefinesDATAFAST.ITEM_MENU_OPERARIO);
                    startActivity(intent);
                    finish();
                }
                else{
                    et_pw.setText("");
                    UIUtils.toast(PwOperario.this, R.drawable.ic_launcher_1, getString(R.string.err_msg_pwoperario), Toast.LENGTH_SHORT);
                    //TMConfig.getInstance().setPw_Operario(false);

                }
            }
        });

        btn_cnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TMConfig.getInstance().setPw_Operario(false);
                finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        et_pw.setText("");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){

            finish();
            return true ;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}
