package cn.desert.newpos.payui.setting.ui.simple;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.desert.newpos.payui.base.BaseActivity;
import cn.desert.newpos.payui.setting.ui.SettingsFrags;

/**
 * Created by zhouqiang on 2017/11/16.
 * @author zhouqiang
 */
public class ErrlogSettings extends BaseActivity {

    private RelativeLayout noData ;
    private LinearLayout hasData ;
    private Spinner list ;
    private TextView details ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_errlog);
        setNaviTitle(getIntent().getExtras().getString(SettingsFrags.JUMP_KEY));
        setRightText(R.string.clear_data);
        setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
        initData();
    }

    private void initData(){
        noData = (RelativeLayout) findViewById(R.id.errlog_no_data_layout);
        hasData = (LinearLayout) findViewById(R.id.errlog_has_data_layout);
        list = (Spinner) findViewById(R.id.errlog_list);
        details = (TextView) findViewById(R.id.errlog_details);
        if(hasData()){
            hasData.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            loadData();
        }else {
            hasData.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    private void clear(){
        File file = new File(TMConfig.getRootFilePath()+"errlog/");
        if(file.exists()){
            file.delete();
        }
        if(hasData()){
            Toast.makeText(this , getString(R.string.op_fail) , Toast.LENGTH_SHORT).show();
        }else {
            hasData.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasData(){
        File file = new File(TMConfig.getRootFilePath()+"errlog/");
        if(file.exists() && file.isDirectory()) {
            String[] list = file.list();
            if (list != null && list.length != 0) {
                return true;
            }
        }
        return false ;
    }

    private void loadData(){
        File file = new File(TMConfig.getRootFilePath()+"errlog/");
        ArrayAdapter adapter = new ArrayAdapter(this , android.R.layout.simple_spinner_dropdown_item , file.list());
        list.setAdapter(adapter);
        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String err = readSingleLog(adapterView.getItemAtPosition(i).toString()) ;
                if(err != null){
                    int usage = err.indexOf("TYPE=user");
                    details.setText(err.substring(usage , err.length())) ;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private String readSingleLog(String filename){
        String result = "";
        String apkPathString = TMConfig.getRootFilePath()+"errlog/";
        File judgeFile = new File(apkPathString + filename);
        if (!judgeFile.exists()) {
            return null ;
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(apkPathString + "/"+filename)){
                byte[] buffer = new byte[fileInputStream.available() + 1 ];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int len ;
                while ((len = fileInputStream.read(buffer)) == -1);
                outputStream.write(buffer, 0, len);
                byte[] byteStream = outputStream.toByteArray();
                result = new String(byteStream);
            } catch (FileNotFoundException fnfe) {
                Logger.error("Exception" + fnfe.toString());
            } catch (IOException ioe) {
                Logger.error("Exception" + ioe.toString());
            }
        }
        return result;
    }
}
