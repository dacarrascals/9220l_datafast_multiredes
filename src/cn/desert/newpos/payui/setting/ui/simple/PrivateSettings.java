package cn.desert.newpos.payui.setting.ui.simple;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.newpos.pay.R;
import com.newpos.libpay.global.TMConfig;

import cn.desert.newpos.payui.base.BaseActivity;
import cn.desert.newpos.payui.setting.ui.SettingsFrags;

/**
 * Created by zhouqiang on 2017/11/15.
 * @author zhouqiang
 */
public class PrivateSettings extends BaseActivity {
    Button online_offline ;
    ImageView debug_nodebug ;

    private TMConfig config ;
    private boolean isOnline ;
    private boolean isDebug ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_private);
        setNaviTitle(getIntent().getExtras().getString(SettingsFrags.JUMP_KEY));
        config = TMConfig.getInstance();
        initData();
        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void initData(){
        online_offline = (Button) findViewById(R.id.pri_online_offlne);
        debug_nodebug = (ImageView) findViewById(R.id.pri_debug_nodebug);

        setOnlineSwitch(config.isOnline());
        setDebugSwitch(config.isDebug());

        online_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnlineSwitch(!isOnline);
            }
        });
        debug_nodebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDebugSwitch(!isDebug);
            }
        });
    }

    public void setOnlineSwitch(boolean is){
        isOnline = is ;
        if(is){
            online_offline.setText(getString(R.string.online_trans));
        }else {
            online_offline.setText(getString(R.string.local_present));
        }
    }

    public void setDebugSwitch(boolean is){
        isDebug = is ;
        if(is){
            debug_nodebug.setImageResource(R.drawable.home2_setting_commun_open);
        }else {
            debug_nodebug.setImageResource(R.drawable.home2_setting_commun_close);
        }
    }

    private void save(){
        config.setOnline(isOnline)
                .setDebug(isDebug)
                .save();

        finish();
    }
}
