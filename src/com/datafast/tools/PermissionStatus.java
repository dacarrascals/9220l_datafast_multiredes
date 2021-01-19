package com.datafast.tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class PermissionStatus {

    Context context;
    Activity activity;

    String[] permits =  new String[] {
            WRITE_EXTERNAL_STORAGE, ACCESS_COARSE_LOCATION};

    public PermissionStatus(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void permissionWriteSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.System.canWrite(context)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        }
    }

    public void reqPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permits, 100);
        }
    }

    public boolean validatePermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String idPermission : permits){
                if (activity.checkSelfPermission(idPermission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getMsgPermissions() {
        SharedPreferences preferences = context.getSharedPreferences("permissions", Context.MODE_PRIVATE);
        return preferences.getBoolean("msg", false);
    }

    public void setMsgPermissions(boolean msgPermission){
        SharedPreferences preferences = context.getSharedPreferences("permissions", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean("msg", msgPermission);
        edit.apply();
    }

    public void showDialogPermission(String msg, final boolean selectMsg){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Permisos Desactivados")
                .setMessage(msg)
                .setCancelable(false);

        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selectMsg){
                    reqPermissions();
                }else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                }
            }
        }).show();
    }

    public void confirmPermissionMsg(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(Settings.System.canWrite(context)){
                if (!getMsgPermissions()){
                    reqPermissions();
                    setMsgPermissions(true);
                }else if (getMsgPermissions() && !validatePermissions()){
                    boolean nvpPermission = false;
                    for (String id : permits){
                        nvpPermission = activity.shouldShowRequestPermissionRationale(id);
                        if (!nvpPermission){
                            showDialogPermission("Has deshabilitado los mensajes de permisos. Entra en permisos y activalos manualmente.", false);
                            break;
                        }
                    }
                    if (nvpPermission){
                        showDialogPermission("Debe aceptar los permisos para el correcto funcionamiento de la App.", true);
                    }
                }
            }else {
                permissionWriteSettings();
            }
        }
    }
}
