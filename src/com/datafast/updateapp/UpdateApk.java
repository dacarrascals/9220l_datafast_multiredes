package com.datafast.updateapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

/**
 * Clase para verificar si debe instalar actualizacion
 */
public class UpdateApk {

    public static final String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + File.separator + "download";

    private Context mCtx;

    public UpdateApk(Context mCtx) {
        this.mCtx = mCtx;
    }

    public String[] instalarApp(Context c) {
        String[] listOfFiles = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS).list();

        String[] ret = new String[listOfFiles.length];

        String packageNameDisco = null;
        String versionNameDisco = null;
        String versionNameAppInstalada = null;

        for (int i = 0; i < listOfFiles.length; i++) {

            String apkEnDisco = listOfFiles[i];


            if (apkEnDisco.endsWith(".apk")) {

                final PackageManager pm = c.getPackageManager();
                String fullPath = DEFAULT_DOWNLOAD_PATH + "/" + apkEnDisco;
                PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);

                try {
                    packageNameDisco = info.packageName;
                    versionNameDisco = info.versionName;
                } catch (Exception e) {
                    ret[i] = apkEnDisco;
                    e.printStackTrace();
                    //continue;

                }

                if (packageNameDisco == null){
                    packageNameDisco = "";
                }

                if (!estaInstaladaAplicacion(packageNameDisco.trim(), c)) {

                    File file = new File(DEFAULT_DOWNLOAD_PATH + "/" + apkEnDisco);
                    if (file.exists()) {
                        if (estaInstaladaAplicacion("com.downloadmanager", c)) {
                            checkUpdate();
                        } else {
                            break;
                        }
                    }
                } else {
                    PackageInfo pinfo = null;
                    try {

                        pinfo = c.getPackageManager().getPackageInfo(packageNameDisco, 0);
                        versionNameAppInstalada = pinfo.versionName;

                        if (!versionNameDisco.equals(versionNameAppInstalada)) {

                            File file = new File(DEFAULT_DOWNLOAD_PATH + "/" + apkEnDisco);
                            if (file.exists()) {
                                if (estaInstaladaAplicacion("com.downloadmanager", c)) {
                                    checkUpdate();
                                } else {
                                    break;
                                }
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        return ret;

    }

    private boolean estaInstaladaAplicacion(String nombrePaquete, Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(nombrePaquete, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void checkUpdate(){
        //indica al mdm si tiene apps por instalar
        Intent launchIntent = new Intent("NOTIFYDOWNLOAD");
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mCtx.startActivity(launchIntent);
        }else{
            boolean isNull = (launchIntent==null);
            Toast.makeText(mCtx, "no se pudo abrir intent null="+isNull, Toast.LENGTH_LONG).show();
        }
    }
}
