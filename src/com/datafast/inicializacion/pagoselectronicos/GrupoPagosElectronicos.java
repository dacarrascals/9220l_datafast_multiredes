package com.datafast.inicializacion.pagoselectronicos;

import android.content.Context;
import android.database.Cursor;

import com.datafast.inicializacion.trans_init.trans.dbHelper;

import java.util.ArrayList;

import static com.datafast.inicializacion.trans_init.Init.NAME_DB;

public class GrupoPagosElectronicos {

    public static ArrayList<PagosElectronicos> GetListaPagosElectronicos(String nomGrupoPeTconf, Context context) {
        boolean PagosElec = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        ArrayList<PagosElectronicos> aLp = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append(" select * ");
        sb.append(" from PAGOS_ELEC ");
        sb.append(" where trim(ID_PAGOS_ELECTRONICOS) ");
        sb.append(" in( ");
        sb.append(" select trim(PAGOS_ELECTRONICOS_ID) ");
        sb.append(" from GrupoXPagosElectronicos ");
        sb.append(" where trim(GRUPO_ID) ");
        sb.append(" in( ");
        sb.append(" select trim(GRUPO_ID) ");
        sb.append(" from GrupoPagosElectronicos ");
        sb.append(" where trim(GRUPO_NOMBRE) = (?) ");
        sb.append(" ) ");
        sb.append(" ); ");

        String sql = sb.toString();
        try {
            Cursor cursor = databaseAccess.rawQuery(sql.toString(), new String[]{nomGrupoPeTconf});
            cursor.moveToFirst();
            int indexColumn;
            PagosElectronicos pagosElectronicos = null;
            while (!cursor.isAfterLast()){
                pagosElectronicos = new PagosElectronicos();
                pagosElectronicos.clearPagosElectronicos();
                indexColumn = 0;
                for (String s : pagosElectronicos.fields) {
                    pagosElectronicos.setPagosElectronicos(s, cursor.getString(indexColumn++).trim());
                }
                PagosElec = true;
                cursor.moveToNext();
                aLp.add(pagosElectronicos);
            }
            cursor.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        databaseAccess.closeDb();

        if (!PagosElec)
            aLp = null;

        return aLp;
    }


}
