package com.datafast.inicializacion.configuracioncomercio;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.datafast.inicializacion.trans_init.trans.dbHelper;

import static com.android.newpos.pay.StartAppDATAFAST.host_confi;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.inicializacion.trans_init.Init.NAME_DB;


public class Host_Confi {

    private String ID_HOST;
    private String NOMBRE_HOST;
    private String TIPO_COMUNICACION;
    private String FORMATO_MENSAJE;
    private String REINTENTOS;
    private String TIEMPO_ESPERA_CONEXION;
    private String TIEMPO_ESPERA_RESPUESTA;
    private String NII_TRANSACCIONES;
    private String NII_CIERRE;
    private String NII_ECHO_TEST;
    private String NII_PAGOS_VARIOS;
    //private String HABILITAR_RESPALDO;
    private String IP_TRAN1;
    private String IP_TRAN2;
    private String LLAVE_1;
    private String LLAVE_2;
    private String LLAVE_DOBLE;
    private String DUKPT;
    private String NO_TIEMPO_HOST;
    private String EMV;

    public static String[] fields = new String[]{
            "ID_HOST",
            "NOMBRE_HOST",
            "TIPO_COMUNICACION",
            "FORMATO_MENSAJE",
            "REINTENTOS",
            "TIEMPO_ESPERA_CONEXION",
            "TIEMPO_ESPERA_RESPUESTA",
            "NII_TRANSACCIONES",
            "NII_CIERRE",
            "NII_ECHO_TEST",
            "NII_PAGOS_VARIOS",
            //"HABILITAR_RESPALDO",
            "IP_TRAN1",
            "IP_TRAN2",
            "LLAVE_1",
            "LLAVE_2",
            "LLAVE_DOBLE",
            "DUKPT",
            "NO_TIEMPO_HOST",
            "EMV"
    };

    public static Host_Confi getSingletonInstance(){
        if (host_confi == null){
            host_confi = new Host_Confi();
        }else{
            Log.d("Host_Confi", "No se puede crear otro objeto, ya existe");
        }
        return host_confi;
    }

    public void setHostConfi(String column, String value) {
        switch (column) {
            case "ID_HOST":
                setID_HOST(value);
                break;
            case "NOMBRE_HOST":
                setNOMBRE_HOST(value);
                break;
            case "TIPO_COMUNICACION":
                setTIPO_COMUNICACION(value);
                break;
            case "FORMATO_MENSAJE":
                setFORMATO_MENSAJE(value);
                break;
            case "REINTENTOS":
                setREINTENTOS(value);
                break;
            case "TIEMPO_ESPERA_CONEXION":
                setTIEMPO_ESPERA_CONEXION(value);
                break;
            case "TIEMPO_ESPERA_RESPUESTA":
                setTIEMPO_ESPERA_RESPUESTA(value);
                break;
            case "NII_TRANSACCIONES":
                setNII_TRANSACCIONES(value);
                break;
            case "NII_CIERRE":
                setNII_CIERRE(value);
                break;
            case "NII_ECHO_TEST":
                setNII_ECHO_TEST(value);
                break;
            case "NII_PAGOS_VARIOS":
                setNII_PAGOS_VARIOS(value);
                break;
            /*case "HABILITAR_RESPALDO":
                setHABILITAR_RESPALDO(value);
                break;*/
            case "IP_TRAN1":
                setIP_TRAN1(value);
                break;
            case "IP_TRAN2":
                setIP_TRAN2(value);
                break;
            case "LLAVE_1":
                setLLAVE_1(value);
                break;
            case "LLAVE_2":
                setLLAVE_2(value);
                break;
            case "LLAVE_DOBLE":
                setLLAVE_DOBLE(value);
                break;
            case "DUKPT":
                setDUKPT(value);
                break;
            case "NO_TIEMPO_HOST":
                setNO_TIEMPO_HOST(value);
                break;
            case "EMV":
                setEMV(value);
                break;
        }
    }

    public void clearHost_Confi() {
        for (String s : Host_Confi.fields) {
            setHostConfi(s, "");
        }
    }

    public void selectHostConfi(Context context){
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        StringBuilder sql = new StringBuilder();

        sql.append("select ");
        int counter = 1;
        for (String s : Host_Confi.fields) {
            sql.append(s);
            if (counter++ < Host_Confi.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from HOST_CONFI ");
        sql.append(" where trim(NOMBRE_HOST) ");
        sql.append(" in(?) ");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString(), new String[]{tconf.getHOST()});
            cursor.moveToFirst();
            int indexColumn;
            while (!cursor.isAfterLast()) {
                clearHost_Confi();
                indexColumn = 0;
                for (String s : Host_Confi.fields) {
                    setHostConfi(s, cursor.getString(indexColumn++).trim());
                }
                //Log.d("sqlite", cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();
    }

    public String getID_HOST() {
        return ID_HOST;
    }

    public void setID_HOST(String ID_HOST) {
        this.ID_HOST = ID_HOST;
    }

    public String getNOMBRE_HOST() {
        return NOMBRE_HOST;
    }

    public void setNOMBRE_HOST(String NOMBRE_HOST) {
        this.NOMBRE_HOST = NOMBRE_HOST;
    }

    public String getTIPO_COMUNICACION() {
        return TIPO_COMUNICACION;
    }

    public void setTIPO_COMUNICACION(String TIPO_COMUNICACION) {
        this.TIPO_COMUNICACION = TIPO_COMUNICACION;
    }

    public String getFORMATO_MENSAJE() {
        return FORMATO_MENSAJE;
    }

    public void setFORMATO_MENSAJE(String FORMATO_MENSAJE) {
        this.FORMATO_MENSAJE = FORMATO_MENSAJE;
    }

    public String getREINTENTOS() {
        return REINTENTOS;
    }

    public void setREINTENTOS(String REINTENTOS) {
        this.REINTENTOS = REINTENTOS;
    }

    public String getTIEMPO_ESPERA_CONEXION() {
        return TIEMPO_ESPERA_CONEXION;
    }

    public void setTIEMPO_ESPERA_CONEXION(String TIEMPO_ESPERA_CONEXION) {
        this.TIEMPO_ESPERA_CONEXION = TIEMPO_ESPERA_CONEXION;
    }

    public String getTIEMPO_ESPERA_RESPUESTA() {
        return TIEMPO_ESPERA_RESPUESTA;
    }

    public void setTIEMPO_ESPERA_RESPUESTA(String TIEMPO_ESPERA_RESPUESTA) {
        this.TIEMPO_ESPERA_RESPUESTA = TIEMPO_ESPERA_RESPUESTA;
    }

    public String getNII_TRANSACCIONES() {
        return NII_TRANSACCIONES;
    }

    public void setNII_TRANSACCIONES(String NII_TRANSACCIONES) {
        this.NII_TRANSACCIONES = NII_TRANSACCIONES;
    }

    public String getNII_CIERRE() {
        return NII_CIERRE;
    }

    public void setNII_CIERRE(String NII_CIERRE) {
        this.NII_CIERRE = NII_CIERRE;
    }

    public String getNII_ECHO_TEST() {
        return NII_ECHO_TEST;
    }

    public void setNII_ECHO_TEST(String NII_ECHO_TEST) {
        this.NII_ECHO_TEST = NII_ECHO_TEST;
    }

    public String getNII_PAGOS_VARIOS() {
        return NII_PAGOS_VARIOS;
    }

    public void setNII_PAGOS_VARIOS(String NII_PAGOS_VARIOS) {
        this.NII_PAGOS_VARIOS = NII_PAGOS_VARIOS;
    }

    /*public String getHABILITAR_RESPALDO() {
        return HABILITAR_RESPALDO;
    }

    public void setHABILITAR_RESPALDO(String HABILITAR_RESPALDO) {
        this.HABILITAR_RESPALDO = HABILITAR_RESPALDO;
    }*/

    public String getIP_TRAN1() {
        return IP_TRAN1;
    }

    public void setIP_TRAN1(String IP_TRAN1) {
        this.IP_TRAN1 = IP_TRAN1;
    }

    public String getIP_TRAN2() {
        return IP_TRAN2;
    }

    public void setIP_TRAN2(String IP_TRAN2) {
        this.IP_TRAN2 = IP_TRAN2;
    }

    public String getLLAVE_1() {
        return LLAVE_1;
    }

    public void setLLAVE_1(String LLAVE_1) {
        this.LLAVE_1 = LLAVE_1;
    }

    public String getLLAVE_2() {
        return LLAVE_2;
    }

    public void setLLAVE_2(String LLAVE_2) {
        this.LLAVE_2 = LLAVE_2;
    }

    public String getLLAVE_DOBLE() {
        return LLAVE_DOBLE;
    }

    public void setLLAVE_DOBLE(String LLAVE_DOBLE) {
        this.LLAVE_DOBLE = LLAVE_DOBLE;
    }

    public String getDUKPT() {
        return DUKPT;
    }

    public void setDUKPT(String DUKPT) {
        this.DUKPT = DUKPT;
    }

    public String getNO_TIEMPO_HOST() {
        return NO_TIEMPO_HOST;
    }

    public void setNO_TIEMPO_HOST(String NO_TIEMPO_HOST) {
        this.NO_TIEMPO_HOST = NO_TIEMPO_HOST;
    }

    public String getEMV() {
        return EMV;
    }

    public void setEMV(String EMV) {
        this.EMV = EMV;
    }
}
