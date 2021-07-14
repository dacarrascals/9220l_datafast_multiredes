package cn.desert.newpos.payui.setting.ui.classical;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.newpos.pay.R;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.setting.view.IPEditText;

/**
 * Created by zhouqiang on 2017/3/10.
 * @author zhouqiang
 */
public class CommunFrags implements View.OnClickListener{

    private Activity mActivity = null ;
    private RelativeLayout rLayout = null ;

    private IPEditText pubIpEdit ;
    private EditText pubPorEdit ;
    private IPEditText inIpEdit ;
    private EditText inPorEdit ;
    private Spinner spinner ;
    private ToggleButton pubSwitch ;

    public CommunFrags(Activity c , RelativeLayout l , String title){
        this.mActivity = c ;
        this.rLayout = l ;
        rLayout.removeAllViews();
        rLayout.inflate(mActivity, R.layout.setting_frag_comun, rLayout);
        ((TextView)rLayout.findViewById(R.id.setting_title_tv)).setText(title);
        rLayout.findViewById(R.id.setting_save).setOnClickListener(this);
        pubIpEdit = (IPEditText) rLayout.findViewById(R.id.setting_public_ip);
        pubPorEdit = (EditText) rLayout.findViewById(R.id.setting_public_port);
        inIpEdit = (IPEditText) rLayout.findViewById(R.id.setting_inner_ip);
        inPorEdit = (EditText) rLayout.findViewById(R.id.setting_inner_port);
        spinner = (Spinner) rLayout.findViewById(R.id.setting_commun_timeout);
        pubSwitch = (ToggleButton) rLayout.findViewById(R.id.setting_public_switch);
        readHistory();
    }

    private void readHistory(){
        pubIpEdit.setIPText(getIPArray(TMConfig.getInstance().getIp()));
        pubPorEdit.setText(TMConfig.getInstance().getPort());
        inIpEdit.setIPText(getIPArray(TMConfig.getInstance().getIP2()));
        inPorEdit.setText(TMConfig.getInstance().getPort2());
        pubSwitch.setChecked(TMConfig.getInstance().getPubCommun());
        String[] array = mActivity.getResources().getStringArray(R.array.commun_timeout);
        ArrayAdapter adapter = new ArrayAdapter(mActivity , android.R.layout.simple_spinner_dropdown_item , array);
        spinner.setAdapter(adapter);
        spinner.setSelection((TMConfig.getInstance().getTimeout()/1000/30)-1);
    }

    private String[] getIPArray(String ip){
        String[] iparray = new String[4] ;
        iparray[0] = ip.substring(0 , ip.indexOf(".") ) ;
        String temp = ip.substring(iparray[0].length()+1 , ip.length()) ;
        iparray[1] = temp.substring(0 , temp.indexOf(".")) ;
        temp = temp.substring(iparray[1].length()+1 , temp.length());
        iparray[2] = temp.substring(0 , temp.indexOf(".")) ;
        iparray[3] = temp.substring(iparray[2].length()+1 , temp.length());
        return iparray ;
    }

    @Override
    public void onClick(View view) {
        if(R.id.setting_save == view.getId()){
            save();
        }
    }

    private void save(){
        String ip = pubIpEdit.getIPText() ;
        String port = pubPorEdit.getText().toString() ;
        String ip2 = inIpEdit.getIPText() ;
        String port2 = inPorEdit.getText().toString() ;
        if(PAYUtils.isNullWithTrim(ip)|| PAYUtils.isNullWithTrim(port)
                ||PAYUtils.isNullWithTrim(ip2)||PAYUtils.isNullWithTrim(port2)){
            UIUtils.toast(mActivity , false ,R.string.data_null);
        }else {
            TMConfig.getInstance()
                    .setIp(ip)
                    .setIp2(ip2)
                    .setPort(port)
                    .setPort2(port2)
                    .setTimeout((spinner.getSelectedItemPosition() + 1) * 30 * 1000)
                    .setPubCommun(pubSwitch.isChecked()?true:false)
                    .save();
            UIUtils.toast(mActivity , true , R.string.save_success);
        }
    }
}
