package com.datafast.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import com.android.newpos.pay.R;
import com.datafast.menus.menus;

import java.util.Iterator;
import java.util.Map;

import cn.desert.newpos.payui.UIUtils;

public class MenuAcquirers {

    private Context mContext;
    private WaitSelectAcquirers callback;
    private boolean showMsgCancel;

    public MenuAcquirers(Context mContext) {
        this.mContext = mContext;
        this.showMsgCancel = false;
    }

    public void setType(boolean show) {
        this.showMsgCancel = show;
    }

    public void menuAcquirers(final Map<String, String> listAquirers, final WaitSelectAcquirers callback) {

        String key = null;
        this.callback = callback;
        // Imprimimos el Map con un Iterador
        final Iterator it = listAquirers.keySet().iterator();
        int i = 0;
        final String[] items = new String[listAquirers.size()];
        while (it.hasNext()) {
            key = (String) it.next();
            //System.out.println("Clave: " + key + " -> Valor: " + listAquirers.get(key));
            items[i++] = listAquirers.get(key);
        }
        if (items.length == 1) {
            callback.getSelectAcquirers(key);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("SELECCIONE COMERCIO");
            builder.setIcon(R.drawable.ic_launcher_1);
            builder.setCancelable(false);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int select) {
                    Iterator it = listAquirers.keySet().iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        if ((select + 1) == Integer.valueOf(key)) {
                            //StartAppDATAFAST.idAcquirer = String.valueOf(key);
                            callback.getSelectAcquirers(String.valueOf(key));
                        }
                    }
                    //System.out.println("Clave: " + select + " -> Valor: " + StartAppDATAFAST.idAcquirer);
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    menus.idAcquirer = String.valueOf(-1);
                    if (showMsgCancel)
                        UIUtils.startResult((Activity) mContext, false, "TRANSACCION CANCELADA");
                }
            });
            AlertDialog a = builder.create();
            a.show();
        }
    }
}
