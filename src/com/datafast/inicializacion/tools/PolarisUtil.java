package com.datafast.inicializacion.tools;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.inicializacion.trans_init.trans.dbHelper;

import cn.desert.newpos.payui.UIUtils;

import static com.datafast.inicializacion.trans_init.Init.NAME_DB;

public class PolarisUtil {


    /**
     * isInitPolaris check Stis Table
     *
     * @author Francisco Mahecha
     * @version 1.0
     * @param context - Activity's context
     * @return true or false
     */
    public static boolean isInitPolaris(Context context) {
        int countRow;
        int counterTables = 1;

        /*boolean tconfOk = false;
        boolean acqsOk = false;
        boolean issuersOk = false;
        boolean cardsOk = false;
        boolean emvappsOk = false;
        boolean capksOk = false;*/

        boolean cardsOk = false;
        boolean acqsOk = false;
        boolean issuerOk = false;
        boolean tconfOk = false;
        boolean hostConfOk = false;
        boolean ipOk = false;
        boolean pagosElecOk = false;
        boolean pagosVarOk = false;
        boolean promptsOk = false;
        boolean grupoPromptsOk = false;
        boolean grupoPagVarOk = false;
        boolean grupoPagElecOk = false;
        boolean grupoXPagElecOk = false;
        boolean grupoXPagVarOk = false;
        boolean grupoXPromptOk = false;
        boolean emvappsOk = false;
        boolean capksOk = false;

        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        StringBuilder sql = new StringBuilder();

        sql.append("select count (*) from CARDS ");
        sql.append("union all ");
        sql.append("select  count (*) from ACQS ");
        sql.append("union all ");
        sql.append("select  count (*) from ISSUERS ");
        sql.append("union all ");
        sql.append("select  count (*) from TCONF ");
        sql.append("union all ");
        sql.append("select  count (*) from HOST_CONFI ");
        sql.append("union all ");
        sql.append("select  count (*) from IP ");
        sql.append("union all ");
        sql.append("select  count (*) from PAGOS_ELEC ");
        sql.append("union all ");
        sql.append("select  count (*) from PAGOS_VAR ");
        sql.append("union all ");
        sql.append("select  count (*) from PROMPTS ");
        sql.append("union all ");

        sql.append("select  count (*) from Grupo_prompt ");
        sql.append("union all ");
        sql.append("select  count (*) from GrupoPagosVarios ");
        sql.append("union all ");
        sql.append("select  count (*) from GrupoPagosElectronicos ");
        sql.append("union all ");

        sql.append("select  count (*) from GrupoXPagosElectronicos ");
        sql.append("union all ");
        sql.append("select  count (*) from GrupoXPagosVarios ");
        sql.append("union all ");
        sql.append("select  count (*) from GrupoXPrompt ");
        sql.append("union all ");
        sql.append("select count (*) from emvapps ");
        sql.append("union all ");
        sql.append("select  count (*) from capks ");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                countRow = cursor.getInt(0);

                if (countRow == 0) {
                    break;
                } else {
                    switch (counterTables) {
                        case 1:
                            cardsOk = true;
                            break;
                        case 2:
                            acqsOk = true;
                            break;
                        case 3:
                            issuerOk = true;
                            break;
                        case 4:
                            tconfOk = true;
                            break;
                        case 5:
                            hostConfOk = true;
                            break;
                        case 6:
                            ipOk = true;
                            break;
                        case 7:
                            pagosElecOk = true;
                            break;
                        case 8:
                            pagosVarOk = true;
                            break;
                        case 9:
                            promptsOk = true;
                            break;
                        case 10:
                            grupoPromptsOk = true;
                            break;
                        case 11:
                            grupoPagVarOk = true;
                            break;
                        case 12:
                            grupoPagElecOk = true;
                            break;
                        case 13:
                            grupoXPagElecOk = true;
                            break;
                        case 14:
                            grupoXPagVarOk = true;
                            break;
                        case 15:
                            grupoXPromptOk = true;
                            break;
                        case 16:
                            emvappsOk = true;
                            break;
                        case 17:
                            capksOk = true;
                            break;
                    }
                }

                counterTables = counterTables + 1;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();
        if (counterTables == 18 &&
                cardsOk == true &&
                acqsOk == true &&
                issuerOk == true &&
                tconfOk == true &&
                hostConfOk == true &&
                ipOk == true &&
                pagosElecOk == true &&
                pagosVarOk == true &&
                promptsOk == true &&
                grupoPromptsOk == true &&
                grupoPagVarOk == true &&
                grupoPagElecOk == true &&
                grupoXPagElecOk == true &&
                grupoXPagVarOk == true &&
                grupoXPromptOk == true &&
                emvappsOk == true &&
                capksOk == true) {
            return true;
        }else
        {
            //UIUtils.toast((Activity) context, R.drawable.ic_launcher, "Debe Inicializar POS!", Toast.LENGTH_LONG);
            return false;
        }
    }

}
