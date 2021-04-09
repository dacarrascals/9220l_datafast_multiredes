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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
            UIUtils.toast(activity, R.drawable.ic_launcher_1, msg, Toast.LENGTH_SHORT);
            return false;
        }
    }

    public static boolean validateMK(int indexKey){
        int retTmp = Ped.getInstance().checkKey(KeySystem.MS_DES, KeyType.KEY_TYPE_MASTK, indexKey, 0);
        Logger.debug("Init.java -> Se valida existencia de MasterKey -> " + retTmp);
        return retTmp == 0;
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

    public static boolean decryptKey(String keyStr, boolean isMk) {

        Logger.debug("Init.java -> Se ingresa a hacer la desencripción de la llave -> " + isMk);

        String confiKey = "490B39AAEEB33AEF0242E1D82D467CFF9AB3E1A745AF69CD";
        SecretKey secretKey = new SecretKeySpec(ISOUtil.str2bcd(confiKey, false), "DESede");
        Cipher decipher;
        try {
            if (keyStr.equals("")){
                return false;
            }
            decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Logger.debug("Init.java -> Error Cath 1 en decryptKey");
            e.printStackTrace();
            return false;
        }
        try {
            byte[] iv = ISOUtil.hex2byte("0000000000000000");
            IvParameterSpec ips = new IvParameterSpec(iv);
            decipher.init(Cipher.DECRYPT_MODE, secretKey, ips);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            Logger.debug("Init.java -> Error Cath 2 en decryptKey");
            e.printStackTrace();
            return false;
        }
        byte[] decipherText;
        try {
            if((decipherText = decipher.doFinal(ISOUtil.str2bcd(keyStr, false))) == null)
                return false;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            Logger.debug("Init.java -> Error Cath 3 en decryptKey");
            e.printStackTrace();
            return false;
        }
        String finalKey = getResultProcessKey(decipherText);
        if (finalKey != null){
            return injectKey(finalKey, isMk) == 0;
        }else {
            return false;
        }

    }

    /**
     * permite determinar si la llave es de 8 o 16 bytes, si es de 8 byte
     * omitira el octeto relleno on 0x08 en la encripcion, y tomara solo los
     * primeros 8 bytes que contienen la llave
     * @param decipherText
     * @return
     */
    private static String getResultProcessKey(byte[] decipherText){
        byte[] octeto = new byte[8];
        int cont = 0;
        boolean isOctetoFill = false;
        String key;
        try {
            for (int i = 0; i < decipherText.length; i++) {
                if (decipherText[i] == 0x08) {
                    octeto[cont] = decipherText[i];
                    cont++;
                }
                if (cont==8) {
                    cont = 0;
                    isOctetoFill = true;
                }
            }
            if (isOctetoFill)
                key = ISOUtil.byte2hex(decipherText).substring(0,16);
            else
                key = ISOUtil.byte2hex(decipherText);

            return key;
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Método para inyección de llave Master y Woking
     * @param key Llave a inyectar
     * @param isMk indica si la llave a inyectar, true para Masterkey - false para Workingkey
     * @return 0 si el proceso fue exitoso
     */
    public static int injectKey(String key, boolean isMk) {
        Logger.debug("Init.java -> Se ingresa a hacer la inyección de la llave injectKey() -> " + isMk);
        byte[] keyData = ISOUtil.str2bcd(key, false);
        int ret;
        if (isMk) {//the app must be System User can inject success.
            ret = Ped.getInstance().injectKey(KeySystem.MS_DES, KeyType.KEY_TYPE_MASTK, MASTERKEYIDX, keyData);
        } else {
            ret = Ped.getInstance().writeKey(KeySystem.MS_DES, KeyType.KEY_TYPE_PINK, MASTERKEYIDX, WORKINGKEYIDX, Ped.KEY_VERIFY_NONE, keyData);
        }
        return ret;
    }

}
