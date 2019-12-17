package cn.desert.newpos.payui.setting.ui.classical;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.newpos.pay.R;
import com.newpos.libpay.device.pinpad.MasterKeyinfo;
import com.newpos.libpay.device.pinpad.PinpadKeytem;
import com.newpos.libpay.device.pinpad.PinpadKeytype;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.UIUtils;

/**
 * Created by zhouqiang on 2017/3/10.
 * @author zhouqiang
 */
public class KeysparaFrags implements View.OnClickListener{

    private Activity mActivity = null ;
    private RelativeLayout rLayout = null ;

    private EditText indexEdit ;
    private EditText dataEdit ;
    private RadioButton desButton ;
    private RadioButton smButton ;

    public KeysparaFrags(Activity a , RelativeLayout l , String title){
        this.mActivity = a ;
        this.rLayout = l ;
        rLayout.removeAllViews();
        rLayout.inflate(mActivity , R.layout.setting_frag_keys , rLayout);
        ((TextView)rLayout.findViewById(R.id.setting_title_tv)).setText(title);
        rLayout.findViewById(R.id.setting_save).setOnClickListener(this);
        indexEdit = (EditText) rLayout.findViewById(R.id.setting_mkey_index);
        dataEdit = (EditText) rLayout.findViewById(R.id.setting_mkey_data);
        desButton = (RadioButton) rLayout.findViewById(R.id.setting_mkey_type_des);
        smButton = (RadioButton) rLayout.findViewById(R.id.setting_mkey_type_sm);
        readHistory();
    }

    private void readHistory(){
        indexEdit.setText(String.valueOf(TMConfig.getInstance().getMasterKeyIndex()));
        desButton.setChecked(true);
        smButton.setChecked(false);
    }

    @Override
    public void onClick(View view) {
        if(R.id.setting_save == view.getId()){
            save();
        }
    }

    private void save(){
        String index = indexEdit.getText().toString();
        String data = dataEdit.getText().toString();
        if(PAYUtils.isNullWithTrim(index)|| PAYUtils.isNullWithTrim(data)){
            UIUtils.toast(mActivity , false , R.string.data_null);
        }else {
            int ks = desButton.isChecked()? PinpadKeytem.MS_DES: PinpadKeytem.MS_SM4 ;
            int idx = Integer.parseInt(index);
            if(data.length() == 32){
                TMConfig.getInstance().setMasterKeyIndex(idx).save();
                MasterKeyinfo masterKeyinfo = new MasterKeyinfo();
                masterKeyinfo.setKeySystem(ks);
                masterKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_MASTK);
                masterKeyinfo.setMasterIndex(idx);
                masterKeyinfo.setPlainKeyData(ISOUtil.str2bcd(data , false));
                int ret = PinpadManager.loadMKey(masterKeyinfo);
                if(0 == ret){
                    UIUtils.toast(mActivity , true , R.string.save_success);
                }else {
                    UIUtils.toast(mActivity , false , mActivity.getResources().getString
                            (R.string.unknown_err)+"["+ret+"]");
                }
            }else {
                UIUtils.toast(mActivity ,false, R.string.len_err);
            }
        }
    }
}
