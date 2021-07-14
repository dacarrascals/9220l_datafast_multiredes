package com.datafast.inicializacion.configuracioncomercio;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.datafast.inicializacion.pagosvarios.PagosVarios;
import com.datafast.inicializacion.trans_init.trans.dbHelper;
import com.newpos.libpay.trans.Trans;

import static cn.desert.newpos.payui.master.MasterControl.llenarPrompts;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.inicializacion.trans_init.Init.NAME_DB;
import static com.newpos.libpay.trans.Trans.Type.PAYBLUE;

public class Rango {
    private String ID_RANGOS;
    private String NOMBRE_RANGO;
    private String IDENTIFICADOR_RANGO ;
    private String ID_AS400 ;
    private String RANGO_MIN ;
    private String RANGO_MAX ;
    private String GRUPO_AS400 ;
    private String TIPO_CUENTA_DEFAULT ;
    private String NII ;
    private String TIPO_MASCARA ;
    private String GRUPO_PROMPTS ;
    private String TIPO_MONTO_FIJO ;
    private String NOMBRE_EMISOR ;
    private String IMAGEN_MOSTRAR ;
    private String MANUAL ;
    private String FECHA_EXP ;
    private String CHECK_DIG ;
    private String CVV2 ;
    private String V4DBC ;
    private String ULTIMOS_4 ;
    private String TARJETA_CIERRE ;
    private String INTER_OPER ;
    private String DEBITO ;
    private String PIN ;
    private String TIPO_DE_CUENTA ;
    private String PRE_VOUCHER ;
    private String CASH_OVER ;
    private String OMITIR_EMV ;
    private String PIN_SERVICE_CODE ;
    private String PERMITIR_TARJ_EXP;

    public static String CARDRANGE_GRUPO_ID;

    public final static String CENTRO = "0";
    public final static String INICIO_FIN = "1";
    public final static String SIN_MASCARA = "2";

    public static String[] fields = new String[]{
            "ID_RANGOS",
            "NOMBRE_RANGO",
            "IDENTIFICADOR_RANGO",
            "ID_AS400",
            "RANGO_MIN",
            "RANGO_MAX",
            "GRUPO_AS400",
            "TIPO_CUENTA_DEFAULT",
            "NII",
            "TIPO_MASCARA",
            "GRUPO_PROMPTS",
            "TIPO_MONTO_FIJO",
            "NOMBRE_EMISOR",
            "IMAGEN_MOSTRAR",
            "MANUAL",
            "FECHA_EXP",
            "CHECK_DIG",
            "CVV2",
            "V4DBC",
            "ULTIMOS_4",
            "TARJETA_CIERRE",
            "INTER_OPER",
            "DEBITO",
            "PIN",
            "TIPO_DE_CUENTA",
            "PRE_VOUCHER",
            "CASH_OVER",
            "OMITIR_EMV",
            "PIN_SERVICE_CODE",
            "PERMITIR_TARJ_EXP"
    };

    public static Rango getSingletonInstance(){
        if (rango == null){
            rango = new Rango();
        }else{
            Log.d("Rango", "No se puede crear otro objeto, ya existe");
        }
        return rango;
    }

    public void setRango(String column, String value){
        switch (column){
            case "ID_RANGOS":
                setID_RANGOS(value);
                break;
            case "NOMBRE_RANGO":
                setNOMBRE_RANGO(value);
                break;
            case "IDENTIFICADOR_RANGO":
                setIDENTIFICADOR_RANGO(value);
                break;
            case "ID_AS400":
                setID_AS400(value);
                break;
            case "RANGO_MIN":
                setRANGO_MIN(value);
                break;
            case "RANGO_MAX":
                setRANGO_MAX(value);
                break;
            case "GRUPO_AS400":
                setGRUPO_AS400(value);
                break;
            case "TIPO_CUENTA_DEFAULT":
                setTIPO_CUENTA_DEFAULT(value);
                break;
            case "NII":
                setNII(value);
                break;
            case "TIPO_MASCARA":
                setTIPO_MASCARA(value);
                break;
            case "GRUPO_PROMPTS":
                setGRUPO_PROMPTS(value);
                break;
            case "TIPO_MONTO_FIJO":
                setTIPO_MONTO_FIJO(value);
                break;
            case "NOMBRE_EMISOR":
                setNOMBRE_EMISOR(value);
                break;
            case "IMAGEN_MOSTRAR":
                setIMAGEN_MOSTRAR(value);
                break;
            case "MANUAL":
                setMANUAL(value);
                break;
            case "FECHA_EXP":
                setFECHA_EXP(value);
                break;
            case "CHECK_DIG":
                setCHECK_DIG(value);
                break;
            case "CVV2":
                setCVV2(value);
                break;
            case "V4DBC":
                setV_4DBC(value);
                break;
            case "ULTIMOS_4":
                setULTIMOS_4(value);
                break;
            case "TARJETA_CIERRE":
                setTARJETA_CIERRE(value);
                break;
            case "INTER_OPER":
                setINTER_OPER(value);
                break;
            case "DEBITO":
                setDEBITO(value);
                break;
            case "PIN":
                setPIN(value);
                break;
            case "TIPO_DE_CUENTA":
                setTIPO_DE_CUENTA(value);
                break;
            case "PRE_VOUCHER":
                setPRE_VOUCHER(value);
                break;
            case "CASH_OVER":
                setCASH_OVER(value);
                break;
            case "OMITIR_EMV":
                setOMITIR_EMV(value);
                break;
            case "PIN_SERVICE_CODE":
                setPIN_SERVICE_CODE(value);
                break;
            case "PERMITIR_TARJ_EXP":
                setPERMITIR_TARJ_EXP(value);
                break;
            default:
                break;
        }
    }

    public void clearRango() {
        for (String s : Rango.fields) {
            setRango(s, "");
        }
    }

    public static boolean inCardTableACQ(String tipoTrans, String PAN,Rango cardRow, String wallet, Context context) {
        boolean ok = false;
        try {

            switch (tipoTrans){
                case Trans.Type.PAGOS_VARIOS:
                    if (cardRow.selectCARD_ROW(PAN, context) == true) {
                        llenarPrompts(PagosVarios.PV_GRUPOPROMPT);
                        ok = true;
                    }
                    break;
                case Trans.Type.ELECTRONIC:
                case Trans.Type.ELECTRONIC_DEFERRED:
                    if (wallet.equals(PAYBLUE)) {
                        if (cardRow.selectCARD_ROW(PAN, context) == true) {
                            rango.CARDRANGE_GRUPO_ID = rango.getGRUPO_PROMPTS();
                            llenarPrompts(rango.CARDRANGE_GRUPO_ID);
                            ok = true;
                        }
                    }else{
                        tconf.TCONF_GRUPO_ID = tconf.GetGrupoIdTconf(context);
                        if (cardRow.selectCARD_ROW(PAN, context) == true) {
                            rango.CARDRANGE_GRUPO_ID = cardRow.GetGrupoIdCardRange(rango.getGRUPO_PROMPTS(), context);
                            llenarPrompts(tconf.TCONF_GRUPO_ID, rango.CARDRANGE_GRUPO_ID);
                            ok = true;
                        }
                    }
                    break;
                default:
                    tconf.TCONF_GRUPO_ID = tconf.GetGrupoIdTconf(context);
                    if (cardRow.selectCARD_ROW(PAN, context) == true) {
                        rango.CARDRANGE_GRUPO_ID = cardRow.GetGrupoIdCardRange(rango.getGRUPO_PROMPTS(), context);
                        llenarPrompts(tconf.TCONF_GRUPO_ID, rango.CARDRANGE_GRUPO_ID);
                        ok = true;
                    }
                    break;
            }


        } catch (Exception ex) {

        }
        return ok;
    }

    public boolean selectCARD_ROW(String PAN, Context context) {
       boolean ok = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : Rango.fields) {
            sql.append(s);
            if (counter++ < Rango.fields.length) {
                sql.append(",");
            }
        }

        sql.append(" from CARDS where ");
        sql.append("(cast(RANGO_MIN as integer) <= cast('");
        sql.append(PAN);
        sql.append("' as integer) and cast(RANGO_MAX as integer) >= cast('");
        sql.append(PAN);
        sql.append("' as integer)) ");
        sql.append("order by cast(RANGO_MAX as integer) - cast(RANGO_MIN as integer) asc limit 1;");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;

            while (!cursor.isAfterLast()){
                clearRango();
                indexColumn = 0;
                for (String s : Rango.fields) {
                    setRango(s, cursor.getString(indexColumn++).trim());
                }
                ok = true;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();
        return ok;
    }

    //obtenemos el grupo id para el card_range
    public String GetGrupoIdCardRange(String nomGrupoPrompt, Context context) {

        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        String grupo_id = "-1";
        StringBuilder sb = new StringBuilder();

        sb.append("select grupo_id as gid ");
        sb.append("from Grupo_prompt ");
        sb.append("where trim(grupo_nombre) = ");
        sb.append("'");
        sb.append(nomGrupoPrompt);
        sb.append("'");
        sb.append(";");
        String sql = sb.toString();

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
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

    public String getID_RANGOS() {
        return ID_RANGOS;
    }

    public void setID_RANGOS(String ID_RANGOS) {
        this.ID_RANGOS = ID_RANGOS;
    }

    public String getNOMBRE_RANGO() {
        return NOMBRE_RANGO;
    }

    public void setNOMBRE_RANGO(String NOMBRE_RANGO) {
        this.NOMBRE_RANGO = NOMBRE_RANGO;
    }

    public String getIDENTIFICADOR_RANGO() {
        return IDENTIFICADOR_RANGO;
    }

    public void setIDENTIFICADOR_RANGO(String IDENTIFICADOR_RANGO) {
        this.IDENTIFICADOR_RANGO = IDENTIFICADOR_RANGO;
    }

    public String getID_AS400() {
        return ID_AS400;
    }

    public void setID_AS400(String ID_AS400) {
        this.ID_AS400 = ID_AS400;
    }

    public String getRANGO_MIN() {
        return RANGO_MIN;
    }

    public void setRANGO_MIN(String RANGO_MIN) {
        this.RANGO_MIN = RANGO_MIN;
    }

    public String getRANGO_MAX() {
        return RANGO_MAX;
    }

    public void setRANGO_MAX(String RANGO_MAX) {
        this.RANGO_MAX = RANGO_MAX;
    }

    public String getGRUPO_AS400() {
        return GRUPO_AS400;
    }

    public void setGRUPO_AS400(String GRUPO_AS400) {
        this.GRUPO_AS400 = GRUPO_AS400;
    }

    public String getTIPO_CUENTA_DEFAULT() {
        return TIPO_CUENTA_DEFAULT;
    }

    public void setTIPO_CUENTA_DEFAULT(String TIPO_CUENTA_DEFAULT) {
        this.TIPO_CUENTA_DEFAULT = TIPO_CUENTA_DEFAULT;
    }

    public String getNII() {
        return NII;
    }

    public void setNII(String NII) {
        this.NII = NII;
    }

    public String getTIPO_MASCARA() {
        return TIPO_MASCARA;
    }

    public void setTIPO_MASCARA(String TIPO_MASCARA) {
        this.TIPO_MASCARA = TIPO_MASCARA;
    }

    public String getGRUPO_PROMPTS() {
        return GRUPO_PROMPTS;
    }

    public void setGRUPO_PROMPTS(String GRUPO_PROMPTS) {
        this.GRUPO_PROMPTS = GRUPO_PROMPTS;
    }

    public String getTIPO_MONTO_FIJO() {
        return TIPO_MONTO_FIJO;
    }

    public void setTIPO_MONTO_FIJO(String TIPO_MONTO_FIJO) {
        this.TIPO_MONTO_FIJO = TIPO_MONTO_FIJO;
    }

    public String getNOMBRE_EMISOR() {
        return NOMBRE_EMISOR;
    }

    public void setNOMBRE_EMISOR(String NOMBRE_EMISOR) {
        this.NOMBRE_EMISOR = NOMBRE_EMISOR;
    }

    public String getIMAGEN_MOSTRAR() {
        return IMAGEN_MOSTRAR;
    }

    public void setIMAGEN_MOSTRAR(String IMAGEN_MOSTRAR) {
        this.IMAGEN_MOSTRAR = IMAGEN_MOSTRAR;
    }

    public String getMANUAL() {
        return MANUAL;
    }

    public void setMANUAL(String MANUAL) {
        this.MANUAL = MANUAL;
    }

    public String getFECHA_EXP() {
        return FECHA_EXP;
    }

    public void setFECHA_EXP(String FECHA_EXP) {
        this.FECHA_EXP = FECHA_EXP;
    }

    public String getCHECK_DIG() {
        return CHECK_DIG;
    }

    public void setCHECK_DIG(String CHECK_DIG) {
        this.CHECK_DIG = CHECK_DIG;
    }

    public String getCVV2() {
        return CVV2;
    }

    public void setCVV2(String CVV2) {
        this.CVV2 = CVV2;
    }

    public String getV_4DBC() {
        return V4DBC;
    }

    public void setV_4DBC(String v_4DBC) {
        V4DBC = v_4DBC;
    }

    public String getULTIMOS_4() {
        return ULTIMOS_4;
    }

    public void setULTIMOS_4(String ULTIMOS_4) {
        this.ULTIMOS_4 = ULTIMOS_4;
    }

    public String getTARJETA_CIERRE() {
        return TARJETA_CIERRE;
    }

    public void setTARJETA_CIERRE(String TARJETA_CIERRE) {
        this.TARJETA_CIERRE = TARJETA_CIERRE;
    }

    public String getINTER_OPER() {
        return INTER_OPER;
    }

    public void setINTER_OPER(String INTER_OPER) {
        this.INTER_OPER = INTER_OPER;
    }

    public String getDEBITO() {
        return DEBITO;
    }

    public void setDEBITO(String DEBITO) {
        this.DEBITO = DEBITO;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public String getTIPO_DE_CUENTA() {
        return TIPO_DE_CUENTA;
    }

    public void setTIPO_DE_CUENTA(String TIPO_DE_CUENTA) {
        this.TIPO_DE_CUENTA = TIPO_DE_CUENTA;
    }

    public String getPRE_VOUCHER() {
        return PRE_VOUCHER;
    }

    public void setPRE_VOUCHER(String PRE_VOUCHER) {
        this.PRE_VOUCHER = PRE_VOUCHER;
    }

    public String getCASH_OVER() {
        return CASH_OVER;
    }

    public void setCASH_OVER(String CASH_OVER) {
        this.CASH_OVER = CASH_OVER;
    }

    public String getOMITIR_EMV() {
        return OMITIR_EMV;
    }

    public void setOMITIR_EMV(String OMITIR_EMV) {
        this.OMITIR_EMV = OMITIR_EMV;
    }

    public String getPIN_SERVICE_CODE() {
        return PIN_SERVICE_CODE;
    }

    public void setPIN_SERVICE_CODE(String PIN_SERVICE_CODE) {
        this.PIN_SERVICE_CODE = PIN_SERVICE_CODE;
    }

    public String getPERMITIR_TARJ_EXP() {
        return PERMITIR_TARJ_EXP;
    }

    public void setPERMITIR_TARJ_EXP(String PERMITIR_TARJ_EXP) {
        this.PERMITIR_TARJ_EXP = PERMITIR_TARJ_EXP;
    }
}
