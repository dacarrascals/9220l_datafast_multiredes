package cn.desert.newpos.payui.setting.ui.simple.transson;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.base.BaseActivity;

/**
 * Created by zhouqiang on 2017/11/15.
 * @author zhouqiang
 */
public class TransMerchantSetting extends BaseActivity {
    //EditText merchant_mid ;
    EditText merchant_tid ;
    //EditText merchant_name ;

    EditText traceNO ;
    EditText batchNo ;
    EditText tpdu ;

    Spinner waitTime ;

    private TMConfig config ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_trans_merchant);
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
        //merchant_mid = (EditText) findViewById(R.id.merchant_merid);
        merchant_tid = (EditText) findViewById(R.id.merchant_tid);
        //merchant_name = (EditText) findViewById(R.id.merchant_name);
        //merchant_name.setText(config.getMerchName());
        merchant_tid.setText(config.getTermID());
        //merchant_mid.setText(config.getMerchID());

        traceNO = (EditText) findViewById(R.id.sys_trace_no);
        batchNo = (EditText) findViewById(R.id.sys_batch_no);
        tpdu = (EditText) findViewById(R.id.sys_tpdu);
        waitTime = (Spinner) findViewById(R.id.sys_wait_time);
        traceNO.setText(config.getTraceNo());
        batchNo.setText(config.getBatchNo());
        tpdu.setText(config.getTpdu());
        setAdaper(waitTime , R.array.wait_time);
        waitTime.setSelection(config.getWaitUserTime()/30 -1 , true);
    }

    private void setAdaper(Spinner spinner , int arrayID){
        String[] array = getResources().getStringArray(arrayID);
        ArrayAdapter adapter = new ArrayAdapter(this , android.R.layout.simple_spinner_dropdown_item , array);
        spinner.setAdapter(adapter);
    }

    private void save(){
        //String mid = merchant_mid.getText().toString();
        String tid = merchant_tid.getText().toString();
        //String name = merchant_name.getText().toString();

        String tn = traceNO.getText().toString();
        String bn = batchNo.getText().toString();
        String tp = tpdu.getText().toString();

        if(/*PAYUtils.isNullWithTrim(mid)||
                PAYUtils.isNullWithTrim(name)||*/
                PAYUtils.isNullWithTrim(tid)||
                PAYUtils.isNullWithTrim(bn)||
                PAYUtils.isNullWithTrim(tp)){
            Toast.makeText(this , getString(R.string.data_null) , Toast.LENGTH_SHORT).show();
            return;
        }
        if(/*mid.length() != 15 || tid.length() != 8 ||*/ tp.length()!=10){
            Toast.makeText(this , getString(R.string.len_err) , Toast.LENGTH_SHORT).show();
            return;
        }
        /*config.setMerchID(mid)
                .setMerchName(name)*/
          config.setTermID(tid)
                .setTraceNo(Integer.parseInt(tn))
                .setBatchNo(Integer.parseInt(bn))
                .setTpdu(tp)
                .setWaitUserTime((waitTime.getSelectedItemPosition()+1)*30)
                .save();
        finish();
    }
}
