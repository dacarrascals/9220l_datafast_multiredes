package com.datafast.inicializacion.pagosvarios;

import android.content.Context;
import android.database.Cursor;

import com.datafast.inicializacion.trans_init.trans.dbHelper;

import java.util.ArrayList;

import static com.datafast.inicializacion.trans_init.Init.NAME_DB;

public class GrupoPagosVarios {

    public static ArrayList<PagosVarios> GetPagoVarioSeleccionado(String idTconf, String idOtherTable, Context context) {
        boolean PagosVar = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        ArrayList<PagosVarios> aLp = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        int counter = 1;
        for (String s : PagosVarios.fields) {
            sb.append(s);
            if (counter++ < PagosVarios.fields.length) {
                sb.append(",");
            }
        }

        sb.append(" from prompts where trim(id_prompts) in (  ");
        sb.append("select trim(x.prompt_code) from (  ");
        sb.append("(select prompt_code from grupoXprompt where trim(grupo_id) = ?)x  ");
        sb.append("inner join  ");
        sb.append("(select prompt_code from grupoXprompt where trim(grupo_id) = ?)y   ");
        sb.append("on x.prompt_code = y.prompt_code));  ");

        String sql = sb.toString();
        try {
            Cursor cursor = databaseAccess.rawQuery(sql.toString(), new String[]{idTconf, idOtherTable});
            cursor.moveToFirst();
            int indexColumn;
            PagosVarios pagosVarios = null;
            while (!cursor.isAfterLast()){
                pagosVarios = new PagosVarios();
                pagosVarios.clearPagosVarios();
                indexColumn = 0;
                for (String s : pagosVarios.fields) {
                    pagosVarios.setPagosVarios(s, cursor.getString(indexColumn++).trim());
                }
                PagosVar = true;
                cursor.moveToNext();
                aLp.add(pagosVarios);
            }
            cursor.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        databaseAccess.closeDb();

        if (!PagosVar)
            aLp = null;

        return aLp;
    }

    public static ArrayList<PagosVarios> GetListaPagosVarios(String nomGrupoPvTconf, Context context) {
        boolean PagosVar = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        ArrayList<PagosVarios> aLp = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append(" select * ");
        sb.append(" from PAGOS_VAR ");
        sb.append(" where trim(ID_PAGOS_VARIOS) ");
        sb.append(" in( ");
        sb.append(" select trim(PAGOS_VARIOS_ID) ");
        sb.append(" from GrupoXPagosVarios ");
        sb.append(" where trim(GRUPO_ID) ");
        sb.append(" in( ");
        sb.append(" select trim(GRUPO_ID) ");
        sb.append(" from GrupoPagosVarios ");
        sb.append(" where trim(GRUPO_NOMBRE) = (?) ");
        sb.append(" ) ");
        sb.append(" ); ");

        String sql = sb.toString();
        try {
            Cursor cursor = databaseAccess.rawQuery(sql.toString(), new String[]{nomGrupoPvTconf});
            cursor.moveToFirst();
            int indexColumn;
            PagosVarios pagosVarios = null;
            while (!cursor.isAfterLast()){
                pagosVarios = new PagosVarios();
                pagosVarios.clearPagosVarios();
                indexColumn = 0;
                for (String s : pagosVarios.fields) {
                    pagosVarios.setPagosVarios(s, cursor.getString(indexColumn++).trim());
                }
                PagosVar = true;
                cursor.moveToNext();
                aLp.add(pagosVarios);
            }
            cursor.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        databaseAccess.closeDb();

        if (!PagosVar)
            aLp = null;

        return aLp;
    }

    //obtenemos el codigo del grupo vario seleccionado
    public static String GetCodePagosVarios(String nombrePagoVarSeleccionado, Context context) {

        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        String grupo_id = "-1";
        StringBuilder sb = new StringBuilder();

        sb.append(" select grupo_id as gid ");
        sb.append(" select codigo_pagos_varios as gid ");
        sb.append(" from PAGOS_VAR ");
        sb.append(" where trim(texto_pagos_varios)  = (?); ");

        String sql = sb.toString();

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString(), new String[]{nombrePagoVarSeleccionado});
            cursor.moveToFirst();
            int indexColumn;
            while (!cursor.isAfterLast()) {
                indexColumn = 0;
                grupo_id = cursor.getString(indexColumn++).trim();
                //Log.d("sqlite", cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();
        return grupo_id;
    }
}
