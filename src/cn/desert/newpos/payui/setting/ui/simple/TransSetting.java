package cn.desert.newpos.payui.setting.ui.simple;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.newpos.pay.R;
import com.newpos.libpay.global.TMConfig;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.BaseActivity;
import cn.desert.newpos.payui.setting.ui.SettingsFrags;
import cn.desert.newpos.payui.setting.ui.simple.transson.TransMerchantSetting;

/**
 * Created by zhouqiang on 2017/11/15.
 * @author zhouqiang
 */
public class TransSetting extends BaseActivity implements View.OnClickListener{
    RelativeLayout merchant ;
    RelativeLayout master ;
    RelativeLayout trans ;
    RelativeLayout scan ;
    ImageView inputPass ;
    ImageView useCard ;
    ImageView forcePBOC;

    private TMConfig config ;
    private Dialog mDialog ;

    private boolean isUseCard ;
    private boolean isInputPass ;
    private boolean isPBOC ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_trans);
        setNaviTitle(getIntent().getExtras().getString(SettingsFrags.JUMP_KEY));
        config  = TMConfig.getInstance();
        initData();
        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void initData(){
        merchant = (RelativeLayout) findViewById(R.id.setting_home_trans_merchant);
        //master = (RelativeLayout) findViewById(R.id.setting_home_trans_pass);
        //trans = (RelativeLayout) findViewById(R.id.setting_home_trans_sys);
        //scan = (RelativeLayout) findViewById(R.id.setting_home_trans_scan);
        //inputPass = (ImageView) findViewById(R.id.setting_home_trans_input_pass);
        //useCard = (ImageView) findViewById(R.id.setting_home_trans_use_card);
        //forcePBOC = (ImageView) findViewById(R.id.setting_home_trans_force_pboc);
        merchant.setOnClickListener(this);
        //master.setOnClickListener(this);
        //trans.setOnClickListener(this);
        //scan.setOnClickListener(this);
        //setCardSwitch(config.getRevocationCardSwitch());
        //setPassSwith(config.getRevocationPassSwitch());
        //setPBOCSwitch(config.isForcePboc());
        /*inputPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassSwith(!isInputPass);
            }
        });
        useCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardSwitch(!isUseCard);
            }
        });
        forcePBOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPBOCSwitch(!isPBOC);
            }
        });*/
    }

    public void setCardSwitch(boolean is){
        isUseCard = is ;
        if(is){
            useCard.setImageResource(R.drawable.home2_setting_commun_open);
        }else {
            useCard.setImageResource(R.drawable.home2_setting_commun_close);
        }
    }

    public void setPBOCSwitch(boolean is){
        isPBOC = is ;
        if(is){
            forcePBOC.setImageResource(R.drawable.home2_setting_commun_open);
        }else {
            forcePBOC.setImageResource(R.drawable.home2_setting_commun_close);
        }
    }

    public void setPassSwith(boolean is){
        isInputPass = is ;
        if(is){
            inputPass.setImageResource(R.drawable.home2_setting_commun_open);
        }else {
            inputPass.setImageResource(R.drawable.home2_setting_commun_close);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent() ;
        String text = "" ;
        /*if(view.getId() == R.id.setting_home_trans_pass){
            changeMasterPwd();
        }else {*/
        switch (view.getId()){
            case R.id.setting_home_trans_merchant :
                intent.setClass(this , TransMerchantSetting.class);
                text = getString(R.string.trans_merchant_para);
                break;
            /*case R.id.setting_home_trans_sys :
                intent.setClass(this , TransSysSetting.class);
                text = getString(R.string.trans_sys_para);
                break;
            case R.id.setting_home_trans_scan :
                intent.setClass(this , TransScanSetting.class);
                text = getString(R.string.trans_scan_para);
                break;*/
        }
        intent.putExtra(SettingsFrags.JUMP_KEY , text);
        startActivity(intent);
        //}
    }

    private void changeMasterPwd(){
        mDialog = UIUtils.centerDialog(this , R.layout.setting_home_pass, R.id.setting_pass_layout);
        final EditText newEdit = (EditText) mDialog.findViewById(R.id.setting_pass_new);
        newEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        final EditText oldEdit = (EditText) mDialog.findViewById(R.id.setting_pass_old);
        oldEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        Button confirm = (Button) mDialog.findViewById(R.id.setting_pass_confirm);
        final ToggleButton ivShowHidePass= mDialog.findViewById(R.id.ivShowHidePass);
        ivShowHidePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    newEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowHidePass.setBackground(getResources().getDrawable(R.drawable.ic_visibility));

                }else{
                    newEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowHidePass.setBackground(getResources().getDrawable(R.drawable.ic_invisible));

                }
                newEdit.setSelection(newEdit.getText().length());
            }
        });
        mDialog.findViewById(R.id.setting_pass_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String np = newEdit.getText().toString();
                String op = oldEdit.getText().toString();
                if(op.equals(TMConfig.getInstance().getMasterPass())){
                    if(np.equals("")||np == null){
                        Toast.makeText(TransSetting.this , getString(R.string.data_null) , Toast.LENGTH_SHORT).show();
                    }else {
                        mDialog.dismiss();
                        TMConfig.getInstance().setMasterPass(np).save();
                        Toast.makeText(TransSetting.this , getString(R.string.save_success) , Toast.LENGTH_SHORT).show();
                    }
                }else {
                    newEdit.setText("");
                    oldEdit.setText("");
                    Toast.makeText(TransSetting.this , getString(R.string.original_pass_err) , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void save(){
        config.setRevocationCardSwitch(isUseCard)
                .setRevocationPassWSwitch(isInputPass)
                .setForcePboc(isPBOC)
                .save();
        finish();
    }
}
