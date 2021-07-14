package com.datafast.inicializacion.configuracioncomercio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.datafast.inicializacion.trans_init.trans.dbHelper;

import java.util.ArrayList;

import static com.android.newpos.pay.StartAppDATAFAST.listIPs;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.inicializacion.trans_init.Init.IPs;
import static com.datafast.inicializacion.trans_init.Init.NAME_DB;

public class ChequeoIPs {

    public static ArrayList<IP> selectIP(Context context) {
        boolean IPNull = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        ArrayList<IP> aLp = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        int counter = 1;
        for (String s : IP.fields) {
            sql.append(s);
            if (counter++ < IP.fields.length) {
                sql.append(",");
            }
        }

        sql.append(" from ( ");
        sql.append(" select * from IP ");
        sql.append(" where trim(NOMBRE_IP) ");
        sql.append(" in ");
        sql.append(" (select trim(IP_TRAN1) from HOST_CONFI ");
        sql.append(" where trim(NOMBRE_HOST) ");
        sql.append(" in(?)) ");
        sql.append(" union all ");
        sql.append(" select * from IP ");
        sql.append(" where trim(NOMBRE_IP) ");
        sql.append(" in ");
        sql.append(" (select trim(IP_TRAN2) from HOST_CONFI ");
        sql.append(" where trim(NOMBRE_HOST) ");
        sql.append(" in(?)) ");
        sql.append(" ) ");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString(), new String[]{tconf.getHOST(),tconf.getHOST()});
            cursor.moveToFirst();
            int indexColumn;
            IP ips = null;
            while (!cursor.isAfterLast()) {
                ips = new IP();
                ips.clearIP();
                indexColumn = 0;
                for (String s : IP.fields) {
                    ips.setIP(s, cursor.getString(indexColumn++).trim());
                }
                IPNull = true;
                cursor.moveToNext();
                aLp.add(ips);
            }
            cursor.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();

        if (!IPNull)
            aLp = null;

        return aLp;
    }

    public static IP seleccioneIP(int posicion){
        IP ipActual = null;
        try {
            ipActual = listIPs.get(posicion);
        } catch (IndexOutOfBoundsException e){

        }
        return ipActual;
    }

    public static String[] fieldsIP = new String[]{
            "IP_HOST",
            "PUERTO"
    };

    public static boolean updateSelectIps(String[] rowToModificate, String[] args, int ind, Context context) {
        boolean ok = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        String id = ChequeoIPs.seleccioneIP(ind).getNOMBRE_IP();
        StringBuilder sql = new StringBuilder();

        sql.append(" UPDATE ");
        sql.append(IPs);
        sql.append(" set ");
        int idx;
        idx = 0;
        for (String s : IP.fields) {
            if (s.equals(rowToModificate[idx])) {
                sql.append(rowToModificate[idx]);
                sql.append(" = ");
                sql.append("'");
                sql.append(args[idx]);
                sql.append("'");
                if (idx < (rowToModificate.length - 1)) {
                    idx++;
                    sql.append(",");
                }
            }
        }

        sql.append(" where trim(NOMBRE_IP)= ");
        sql.append("'");
        sql.append(id);
        sql.append("'");
        sql.append(";");

        try {
            //System.out.println("SENTECIA SQL*******" + sql.toString());
            databaseAccess.execSql(sql.toString());
            ok = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        databaseAccess.close();
        return ok;
    }
}
