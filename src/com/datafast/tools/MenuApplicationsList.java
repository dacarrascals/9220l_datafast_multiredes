package com.datafast.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.newpos.pay.R;

import cn.desert.newpos.payui.UIUtils;

public class MenuApplicationsList {

    private Context mContext;
    private WaitSelectApplicationsList callback;
    private boolean showMsgCancel;

    public MenuApplicationsList(Context mContext) {
        this.mContext = mContext;
        this.showMsgCancel = false;
    }

    public void menuApplicationsList(String[] apps, final WaitSelectApplicationsList callback){
        final String[] listApps = new String[apps.length];
        int i = 0;
        final int[] selapp = new int[1];
        for(String  str : apps)
        {
            listApps[i++] = str.trim();
            System.out.println(str.trim());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_title_listapp);

        //list of items
        builder.setSingleChoiceItems(listApps, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int select) {
                        // item selected logic
                        selapp[0] = select;
                    }
                });


        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        callback.getAppListSelect(selapp[0]);
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.getAppListSelect(-1);
                if (showMsgCancel)
                    UIUtils.startResult((Activity) mContext, false, "TRANSACCION CANCELADA");
            }
        });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
}
