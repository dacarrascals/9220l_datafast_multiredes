package cn.desert.newpos.payui.setting.ui.simple;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.newpos.libpay.device.pinpad.MasterKeyinfo;
import com.newpos.libpay.device.pinpad.PinpadKeytem;
import com.newpos.libpay.device.pinpad.PinpadKeytype;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.base.BaseActivity;
import cn.desert.newpos.payui.setting.ui.SettingsFrags;

/**
 * Created by zhouqiang on 2017/11/15.
 * @author zhouqiang
 */
public class KeysSettings extends BaseActivity {

    EditText index ;
    EditText keys ;
    RadioButton des ;
    RadioButton gm ;

    private TMConfig config ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_keys);
        setNaviTitle(getIntent().getExtras().getString(SettingsFrags.JUMP_KEY));
        config = TMConfig.getInstance() ;
        initData();
        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void initData(){
        index = (EditText) findViewById(R.id.master_key_index);
        keys = (EditText) findViewById(R.id.master_key_data);
        gm = (RadioButton) findViewById(R.id.master_key_des);
        des = (RadioButton) findViewById(R.id.master_key_des);
        index.setText(String.valueOf(config.getMasterKeyIndex()));
    }

    private void save(){
        String idx = index.getText().toString();
        String data = keys.getText().toString();
        if(PAYUtils.isNullWithTrim(idx) || PAYUtils.isNullWithTrim(data)){
            Toast.makeText(this , getString(R.string.data_null) , Toast.LENGTH_SHORT).show();
            return;
        }
        int ks = des.isChecked()? PinpadKeytem.MS_DES: PinpadKeytem.MS_SM4 ;
        int i = Integer.parseInt(idx);
        if(data.length() == 32){
            TMConfig.getInstance().setMasterKeyIndex(i).save();
            MasterKeyinfo masterKeyinfo = new MasterKeyinfo();
            masterKeyinfo.setKeySystem(ks);
            masterKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_MASTK);
            masterKeyinfo.setMasterIndex(i);
            masterKeyinfo.setPlainKeyData(ISOUtil.str2bcd(data , false));
            int ret = PinpadManager.loadMKey(masterKeyinfo);
            if(0 == ret){
                Toast.makeText(this , getString(R.string.save_success) , Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this , getString(R.string.unknown_err)+"["+ret+"]" , Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this ,getString(R.string.len_err),Toast.LENGTH_SHORT).show();
        }
    }
}
