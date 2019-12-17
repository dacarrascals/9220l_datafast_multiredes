package cn.desert.newpos.payui.setting.ui.simple;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.menus.MenuAction;
import com.datafast.server.activity.ServerTCP;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.BaseActivity;
import cn.desert.newpos.payui.setting.ui.SettingsFrags;
import cn.desert.newpos.payui.setting.view.IPEditText;

import static cn.desert.newpos.payui.master.MasterControl.hideKeyboard;

//import static com.datafast.menus.menus.acquirerRow;

/**
 * Created by zhouqiang on 2017/11/15.
 */
public class CommunSettings extends BaseActivity implements View.OnClickListener {
    EditText commun_timeout;
    IPEditText commun_pub_ip;
    EditText commun_pub_port;
    EditText merchant_tid;
    LinearLayout layout_termID_comm;
    TextView tv_label_termID;
    TextView tv_label_nii;
    EditText et_nii;
    EditText et_intentos;

    private TMConfig config;
    private boolean isOpen;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_commun);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        commun_timeout = findViewById(R.id.setting_com_timeout);
        commun_pub_ip = findViewById(R.id.setting_com_public_ip);
        commun_pub_port = findViewById(R.id.setting_com_public_port);
        merchant_tid = findViewById(R.id.merchant_tid);
        et_intentos = findViewById(R.id.setting_com_intentos_de_conex);
        et_nii = findViewById(R.id.setting_com_nii);
        tv_label_termID = findViewById(R.id.tv_ter_id);
        tv_label_nii = findViewById(R.id.tv_timeout);
        layout_termID_comm = findViewById(R.id.layout_termID_comm);

        if (SettingsFrags.JUMP_KEY == null) {
            setNaviTitle(getIntent().getExtras().getString(MenuAction.JUMP_KEY));
        } else {
            key = getIntent().getExtras().getString(SettingsFrags.JUMP_KEY);
            setNaviTitle(key);
        }
        config = TMConfig.getInstance();
        initData();
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

    private void initData() {

        switch (key) {
            case DefinesDATAFAST.ITEM_TIPO_COMUNICACION:
                tv_label_nii.setText("NII");
                et_nii.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                merchant_tid.setVisibility(View.INVISIBLE);
                tv_label_termID.setVisibility(View.INVISIBLE);
                layout_termID_comm.setVisibility(View.INVISIBLE);
                break;

            default:
                commun_timeout.setText(String.valueOf(config.getTimeout() / 1000));
                isOpen = true;
                commun_pub_ip.setIPText(config.getIp().split("\\."));
                commun_pub_port.setText(config.getPort());
                merchant_tid.setText(config.getTermID());
                et_intentos.setText(String.valueOf(config.getIntentosConex()));
                et_nii.setText(config.getNii());
                break;
        }
    }

    private void save() {
        String ip = commun_pub_ip.getIPText();
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
                .setTermID(tid)
                .setIntentosConex(Integer.parseInt(intentConex))
                .setNii(nii)
                .save();
        UIUtils.toast(this, R.drawable.ic_launcher, getString(R.string.save_success), Toast.LENGTH_SHORT);
        hideKeyboard(CommunSettings.this);
        finish();
    }

    @Override
    public void onClick(View view) {
        InputMethodManager imm = (InputMethodManager) CommunSettings.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
