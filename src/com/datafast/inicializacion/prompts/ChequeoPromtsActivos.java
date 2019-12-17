package com.datafast.inicializacion.prompts;

import android.content.Context;
import android.database.Cursor;

import com.datafast.inicializacion.trans_init.trans.dbHelper;

import java.util.ArrayList;

import static com.datafast.inicializacion.trans_init.Init.NAME_DB;

public class ChequeoPromtsActivos {

    public static ArrayList<Prompt> GetPrompts(String idTconf, String idOtherTable, Context context) {
        boolean Prompts = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        ArrayList<Prompt> aLp = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        int counter = 1;
        for (String s : Prompt.fields) {
            sb.append(s);
            if (counter++ < Prompt.fields.length) {
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
            Prompt prompt = null;
            while (!cursor.isAfterLast()){
                prompt = new Prompt();
                prompt.clearPrompt();
                indexColumn = 0;
                for (String s : prompt.fields) {
                    prompt.setPrompt(s, cursor.getString(indexColumn++).trim());
                }
                Prompts = true;
                cursor.moveToNext();
                aLp.add(prompt);
            }
            cursor.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        databaseAccess.closeDb();

        if (!Prompts)
            aLp = null;

        return aLp;
    }

    public static ArrayList<Prompt> GetPrompts(String nombreGrupoPrompt, Context context) {
        boolean Prompts = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        ArrayList<Prompt> aLp = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        int counter = 1;
        for (String s : Prompt.fields) {
            sb.append(s);
            if (counter++ < Prompt.fields.length) {
                sb.append(",");
            }
        }
        sb.append(" from PROMPTS ");
        sb.append(" where trim(codigo_prompts) ");
        sb.append(" in( ");
        sb.append(" select trim(prompt_code) ");
        sb.append(" from GrupoXPrompt ");
        sb.append(" where trim(grupo_id) ");
        sb.append(" in( ");
        sb.append(" select trim(grupo_id) ");
        sb.append(" from Grupo_prompt ");
        sb.append(" where trim(grupo_nombre) = (?) ");
        sb.append(" ) ");
        sb.append(" ); ");

        String sql = sb.toString();
        try {
            Cursor cursor = databaseAccess.rawQuery(sql.toString(), new String[]{nombreGrupoPrompt});
            cursor.moveToFirst();
            int indexColumn;
            Prompt prompt = null;
            while (!cursor.isAfterLast()){
                prompt = new Prompt();
                prompt.clearPrompt();
                indexColumn = 0;
                for (String s : prompt.fields) {
                    prompt.setPrompt(s, cursor.getString(indexColumn++).trim());
                }
                Prompts = true;
                cursor.moveToNext();
                aLp.add(prompt);
            }
            cursor.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        databaseAccess.closeDb();

        if (!Prompts)
            aLp = null;

        return aLp;
    }
}
