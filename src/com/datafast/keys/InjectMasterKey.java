package com.datafast.keys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.android.newpos.pay.StartAppDATAFAST;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.SDKException;
import com.pos.device.ped.KeySystem;
import com.pos.device.ped.KeyType;
import com.pos.device.ped.Ped;

import java.util.Timer;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.ResultControl;

public class InjectMasterKey extends AppCompatActivity {

    public static final int MASTERKEYIDX = 0;
    private static final int WORKINGKEYIDX = 0;
    static callBackGetMasterKey mk;
    public static String pwMasterKey;
    private Timer timer = new Timer() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inject_master_key);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            pwMasterKey = bundle.getString("pw");

            loadWebGif();
            getMk();
        }
    }

    private void getMk() {

        mk = new callBackGetMasterKey(new callBackGetMasterKey.FileCallback() {
            @Override
            public String RspUnpack(String OK_unpack) {

                String mk = OK_unpack;
                if (OK_unpack.length() > 1) {

                    try {
                        Thread.sleep(500);
                        if(injectMk(OK_unpack) == 0) {
                            processResponse("MASTER KEY INYECTADA EXITOSAMENTE!!",true);
                        }
                        else{
                            processResponse("FALLO INSERTANDO MASTER KEY!!", false);
                        }
                        finish();
                    } catch (InterruptedException e) {
                        Logger.error("Exception" + e.toString());
                        Thread.currentThread().interrupt();
                    }

                } else {

                    if (OK_unpack.equals("1")) {
                        processResponse("CONTRASEÑA INCORRECTA!!", false);
                        finish();
                    }
                    else if (OK_unpack.equals("3")) {
                        processResponse("ERROR VUELVA A INTENTAR!!", false);
                        finish();
                    }
                }

                return "";
            }
        });
        mk.execute();
    }

    /**
     *
     * @param masterKey
     * @return
     */
    public static int injectMk(String masterKey) {
        Log.d("MASTER KEY", masterKey);
        byte[] masterKeyData = ISOUtil.str2bcd(masterKey, false);
        int ret = Ped.getInstance().injectKey(KeySystem.MS_DES, KeyType.KEY_TYPE_MASTK, MASTERKEYIDX, masterKeyData);//the app must be System User can inject success.
        Log.d("MASTER KEY", "inject master key ret=" + ret);
        return ret;
    }

    /**
     *
     * @param workingKey
     * @return
     */
    public static int injectWorkingKey(String workingKey) {
        Log.d("WORKING KEY", workingKey);
        byte[] workingKeyData = ISOUtil.str2bcd(workingKey, false);
        int ret = Ped.getInstance().writeKey(KeySystem.MS_DES, KeyType.KEY_TYPE_PINK, MASTERKEYIDX, WORKINGKEYIDX, Ped.KEY_VERIFY_NONE, workingKeyData);
        Log.d("WORKING KEY", "inject working key ret=" + ret);
        return ret;
    }

    public static boolean threreIsKey(int indexKey, String msg, Activity activity){
        int retTmp = Ped.getInstance().checkKey(KeySystem.MS_DES, KeyType.KEY_TYPE_MASTK, indexKey, 0);
        if(retTmp == 0){
            return true;
        }else {
            UIUtils.toast(activity, R.drawable.ic_launcher, msg, Toast.LENGTH_SHORT);
            return false;
        }
    }

    /**
     *
     * @param msg
     * @param flag
     */
    private void processResponse(String msg, boolean flag){
        Intent intent = new Intent();
        intent.setClass(InjectMasterKey.this, ResultControl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag", flag);
        bundle.putString("info", msg);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     *
     */
    public void loadWebGif() {
        WebView wvInsert;
        wvInsert = (WebView) findViewById(R.id.wb_loading_mk);
        wvInsert.loadDataWithBaseURL(null, "<HTML><body bgcolor='#FFF'><div align=center>" +
                "<img width=\"128\" height=\"128\" src='file:///android_asset/gif/load3.gif'/></div></body></html>", "text/html", "UTF-8", null);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mk != null)
            mk.cancel(true);

    }

    @Override
    public void onBackPressed() {
        if (mk != null)
            mk.cancel(true);

        UIUtils.startView(InjectMasterKey.this, StartAppDATAFAST.class);
    }

    public static void deleteKeys(KeyType keyType, int idxKey){
        try {
            Ped.getInstance().deleteKey(KeySystem.MS_DES, keyType, idxKey);
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }
}
