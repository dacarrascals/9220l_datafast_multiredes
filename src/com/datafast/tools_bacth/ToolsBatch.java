package com.datafast.tools_bacth;

import android.content.Context;

import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;

import java.util.List;

import static com.newpos.libpay.trans.Trans.idLote;

//import static com.datafast.menus.menus.acquirerRow;

public class ToolsBatch {

    /**
     * Recorre todos los acquirers y valida si alguno tiene trans.
     * @param ctx
     * @return
     */
    public static boolean statusTrans(Context ctx) {
        /*Map<String, String> listAquirers;
        boolean ret = false;
        acquirerRow.selectAcquirer(ctx);

        listAquirers = acquirerRow.getListAcquirers();
        // Imprimimos el Map con un Iterador
        final Iterator it = listAquirers.keySet().iterator();
        int idx = 0;

        while(it.hasNext()){
            String key = (String) it.next();
            List<TransLogData> list = TransLog.getInstance(key).getData();
            if (list.size() > 0) {
                ret =  true;
                break;
            } else {
                ret =  false;
            }
        }*/
        boolean ret = false;
        String key = idLote;
        List<TransLogData> list = TransLog.getInstance(key).getData();
        if (list.size() > 0) {
            ret =  true;
        } else {
            ret =  false;
        }

        return ret;
    }

    public static boolean statusTrans(String idAcq) {
        boolean ret = false;
        List<TransLogData> list = TransLog.getInstance(idAcq).getData();
        if (list.size() > 0) {
            ret =  true;
        } else {
            ret =  false;
        }
        return ret;
    }

    /**
     *
     * @param value
     * @return
     */
    public static String incBatchNo(String value) {
        int val = Integer.parseInt(value);
        if (val == 999999) {
            val = 0;
        }
        val += 1;
        return ISOUtil.padleft(String.valueOf(val) + "", 6, '0');
    }
}
