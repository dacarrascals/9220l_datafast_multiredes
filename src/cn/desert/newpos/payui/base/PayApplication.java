package cn.desert.newpos.payui.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.PaySdkException;
import com.newpos.libpay.PaySdkListener;
import com.newpos.libpay.device.pinpad.MasterKeyinfo;
import com.newpos.libpay.device.pinpad.PinpadKeytem;
import com.newpos.libpay.device.pinpad.PinpadKeytype;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.device.pinpad.WorkKeyinfo;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.printer.Printer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhouqiang on 2017/7/3.
 */

public class PayApplication extends Application {

    private static PayApplication app ;
    private List<Activity> mList = new LinkedList<>();
    public static volatile boolean isInit = false ;
    private static final String APP_RUN = "app_run" ;
    private static final String APP_DEK = "app_des" ;
    private SharedPreferences runPreferences ;
    private SharedPreferences.Editor runEditor ;
    private SharedPreferences dekPreferences ;
    private SharedPreferences.Editor dekEditor ;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this ;
        initPaysdk();
    }

    private void initPaysdk(){
        //ApplicationCrash.getInstance().init(app);
        runPreferences = getSharedPreferences(APP_RUN , MODE_PRIVATE);
        runEditor = runPreferences.edit() ;
        dekPreferences = getSharedPreferences(APP_DEK , MODE_PRIVATE);
        dekEditor = dekPreferences.edit() ;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PaySdk.getInstance().init(app, new PaySdkListener() {
                        @Override
                        public void success() {
                            isInit = true ;
                            //initKeys();
                            Logger.debug("sdk init ok");
                            //int status = Printer.getInstance().getStatus();
                            //Logger.debug("print sta:" + status);
                        }
                    });
                }catch (PaySdkException e){
                    Logger.error("Exception" + e.toString());
                }
            }
        }).start();

    }

    public static PayApplication getInstance(){
        return app ;
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        isInit = false ;
        PaySdk.getInstance().exit();
        //结束栈中
        try {
            for (Activity activity : mList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            Logger.error("Exception" + e.toString());
        } finally {
            System.exit(0);
            System.gc();
        }
    }

    public void setRunned(){
        runEditor.clear().commit();
        runEditor.putBoolean(APP_RUN , true).commit();
    }

    public boolean isRunned(){
        return runPreferences.getBoolean(APP_RUN , false) ;
    }

    /**
     * 经典桌面与简约桌面
     * @return
     */
    public boolean isClassical(){
        return dekPreferences.getBoolean(APP_DEK , false);
    }

    public void setClassical(boolean classical){
        dekEditor.clear().commit();
        dekEditor.putBoolean(APP_DEK , classical).commit();
    }

    public boolean isAppInstalled(Context context, String uri , boolean b) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    private void initKeys() {
        TMConfig cfg = TMConfig.getInstance();

        MasterKeyinfo masterKeyinfo = new MasterKeyinfo();
        masterKeyinfo.setKeySystem(PinpadKeytem.MS_DES);
        masterKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_MASTK);
        masterKeyinfo.setMasterIndex(cfg.getMasterKeyIndex());
        masterKeyinfo.setPlainKeyData(ISOUtil.str2bcd("11111111111111112222222222222222" , false));
        PinpadManager.loadMKey(masterKeyinfo);

        byte[] keyData = ISOUtil.str2bcd("1CF08008FD62A1E217153829C3A6E51C2A7B0CB84A187EE99C9D002BE1010250792913C4325EA56471657F39F8B3D6562CC515E0403BEB676CCCB22E" , false);
        WorkKeyinfo workKeyinfo = new WorkKeyinfo() ;
        workKeyinfo.setMasterKeyIndex(cfg.getMasterKeyIndex());
        workKeyinfo.setWorkKeyIndex(cfg.getMasterKeyIndex());
        workKeyinfo.setMode(16777216);
        workKeyinfo.setKeySystem(PinpadKeytem.MS_DES);

        byte[] temp;
        int keyLen;
        keyLen = 20 ;

        temp = new byte[keyLen];
        System.arraycopy(keyData, 0, temp, 0, keyLen);
        workKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_PINK);
        workKeyinfo.setPrivacyKeyData(temp);
        PinpadManager.loadWKey(workKeyinfo);

        System.arraycopy(keyData, keyLen, temp, 0, keyLen);
        workKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_MACK);
        if(cfg.getStandard() == 1){
            System.arraycopy(temp , 0 , temp , 8 , 8 );
        }
        workKeyinfo.setPrivacyKeyData(temp);
        PinpadManager.loadWKey(workKeyinfo);

        System.arraycopy(keyData, keyLen*2, temp, 0, keyLen);
        workKeyinfo.setKeyType(PinpadKeytype.KEY_TYPE_EAK);
        workKeyinfo.setPrivacyKeyData(temp);
        PinpadManager.loadWKey(workKeyinfo);
    }
}
