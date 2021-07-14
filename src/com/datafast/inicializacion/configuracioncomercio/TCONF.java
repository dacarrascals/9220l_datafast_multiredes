package com.datafast.inicializacion.configuracioncomercio;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.datafast.inicializacion.trans_init.trans.dbHelper;

import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.inicializacion.trans_init.Init.NAME_DB;

public class TCONF {

    private String COMERCIO_ID;
    private String NOMBRE_COMERCIO;
    private String DIRECCION_PRINCIPAL;
    private String DIRECCION_SECUNDARIA;
    private String TELEFONO_COMERCIO;
    private String LINEA_AUX;
    private String CIUDAD;
    private String RUC;
    private String HOST;
    private String CARD_ACCP_TERM;
    private String CARD_ACCP_MERCH;
    private String NUMERO_LOTE;
    private String MONEDA;
    //private String GRUPO_RANGOS;
    private String REVERSO;
    private String HABILITAR_IMPUESTO;
    private String LABEL_IMPUESTO;
    private String TIPO_IMPUESTO;
    private String PORCENTAJE_MAXIMO_IMPUESTO;
    private String CAPTURA_ELECTRONICA;
    private String PIN_BYPASS;
    private String CAJA_REGISTRADORA;
    private String ENTRADA_MANUAL_PREAUTO;
    private String MONTO_MAXIMO_TRANSACCION;
    private String MONTO_MINIMO_TRANSACCION;
    private String HABILITAR_SERVICIO;
    private String LABEL_SERVICIO;
    private String TIPO_SERVICIO;
    private String PORCENTAJE_MAXIMO_SERVICIO;
    private String HABILITAR_ICE;
    private String TIPO_ICE;
    private String PORCENTAJE_MAXIMO_ICE;
    private String TRANSACCION_VENTA;
    private String TRANSACCION_DIFERIDO;
    private String TRANSACCION_ANULACION;
    private String TRANSACCION_PAGOS_VARIOS;
    private String TRANSACCION_CASH_OVER;
    private String TRANSACCION_PRE_VOUCHER;
    private String TRANSACCION_PAGOS_ELECTRONICOS;
    private String TRANSACCION_PRE_AUTO;
    private String GRUPO_PROMPTS;
    private String GRUPO_PAGOS_VARIOS;
    private String GRUPO_PAGOS_ELECTRONICOS;
    private String HABILITAR_PROPINA;
    private String LABEL_PROPINA;
    private String TIPO_PROPINA;
    private String PORCENTAJE_MAXIMO_PROPINA;
    private String HABILITAR_OTROS_IMPUESTOS;
    private String LABEL_OTROS_IMPUESTOS;
    private String TIPO_OTROS_IMPUESTOS;
    private String PORCENTAJE_MAXIMO;
    private String HABILITA_MONTO_FIJO;
    private String VALOR_MONTO_FIJO;
    private String NO_PERMITIR_2_TRANS_MISMO_TARJ;
    private String TARIFA_CERO;
    private String LABEL_ICE;
    private String HABILITA_FIRMA;

    private String CLAVE_COMERCIO;
    private String NUM_SERIAL;
    private String HEADER_COMERCIO;
    private String HEADER_DIRECCION_1;
    private String HEADER_DIRECCION_2;
    private String HEADER_TELEFONO;
    private String HEADER_LINEA_AUX;
    private String FOOTER_LINEA_1;
    private String FOOTER_LINEA_2;
    private String FOOTER_LINEA_3;
    private String FOOTER_LINEA_4;
    private String FOOTER_LINEA_5;
    private String FOOTER_LINEA_6;
    private String FOOTER_LINEA_7;
    private String FOOTER_LINEA_8;
    private String FOOTER_LINEA_9;
    private String FOOTER_LINEA_10;
    private String CLAVE_TECNICO;
    private String SIMBOLO_MONEDA_LOCAL;
    private String SIMBOLO_DOLAR;
    private String SIMBOLO_EURO;
    private String DIAS_CIERRE;
    private String HORAS_ECHO;
    private String HABILITA_CIERRE;
    private String HABILITA_CIERRE_AUTOMATICO_DIA;
    private String HORA_CIERRE;
    private String HABILITA_PLC;
    private String NOTAS;
    private String TIPO_INICIALIZACION;
    private String HABILITA_IMPRIMIR_RECIBO;
    private String HABILITA_IMPRIMIR_CARD_HOLDER;
    private String HABILITA_IMPRIMIR_COD_BARRAS;
    private String TIPO_CODIGO_BARRAS;
    private String COPIA_VOUCHER;
    private String MENSAJE_CIERRE1;
    private String MENSAJE_CIERRE2;

    public static String TCONF_GRUPO_ID;

    public static String[] fields = new String[]{
            "COMERCIO_ID",
            "NOMBRE_COMERCIO",
            "DIRECCION_PRINCIPAL",
            "DIRECCION_SECUNDARIA",
            "TELEFONO_COMERCIO",
            "LINEA_AUX",
            "CIUDAD",
            "RUC",
            "HOST",
            "CARD_ACCP_TERM",
            "CARD_ACCP_MERCH",
            "NUMERO_LOTE",
            "MONEDA",
            //"GRUPO_RANGOS",
            "REVERSO",
            "HABILITAR_IMPUESTO",
            "LABEL_IMPUESTO",
            "TIPO_IMPUESTO",
            "PORCENTAJE_MAXIMO_IMPUESTO",
            "TARIFA_CERO",
            "CAPTURA_ELECTRONICA",
            "PIN_BYPASS",
            "CAJA_REGISTRADORA",
            "ENTRADA_MANUAL_PREAUTO",
            "MONTO_MAXIMO_TRANSACCION",
            "MONTO_MINIMO_TRANSACCION",
            "HABILITAR_SERVICIO",
            "LABEL_SERVICIO",
            "TIPO_SERVICIO",
            "PORCENTAJE_MAXIMO_SERVICIO",
            "HABILITAR_ICE",
            "LABEL_ICE",
            "TIPO_ICE",
            "PORCENTAJE_MAXIMO_ICE",
            "TRANSACCION_VENTA",
            "TRANSACCION_DIFERIDO",
            "TRANSACCION_ANULACION",
            "TRANSACCION_PAGOS_VARIOS",
            "TRANSACCION_CASH_OVER",
            "TRANSACCION_PRE_VOUCHER",
            "TRANSACCION_PAGOS_ELECTRONICOS",
            "TRANSACCION_PRE_AUTO",
            "GRUPO_PROMPTS",
            "GRUPO_PAGOS_VARIOS",
            "GRUPO_PAGOS_ELECTRONICOS",
            "HABILITAR_PROPINA",
            "LABEL_PROPINA",
            "TIPO_PROPINA",
            "PORCENTAJE_MAXIMO_PROPINA",
            "HABILITAR_OTROS_IMPUESTOS",
            "LABEL_OTROS_IMPUESTOS",
            "TIPO_OTROS_IMPUESTOS",
            "PORCENTAJE_MAXIMO",
            "HABILITA_MONTO_FIJO",
            "VALOR_MONTO_FIJO",
            "NO_PERMITIR_2_TRANS_MISMO_TARJ",
            "HABILITA_FIRMA",

            "CLAVE_COMERCIO",
            "NUM_SERIAL",
            "HEADER_COMERCIO",
            "HEADER_DIRECCION_1",
            "HEADER_DIRECCION_2",
            "HEADER_TELEFONO",
            "HEADER_LINEA_AUX",
            "FOOTER_LINEA_1",
            "FOOTER_LINEA_2",
            "FOOTER_LINEA_3",
            "FOOTER_LINEA_4",
            "FOOTER_LINEA_5",
            "FOOTER_LINEA_6",
            "FOOTER_LINEA_7",
            "FOOTER_LINEA_8",
            "FOOTER_LINEA_9",
            "FOOTER_LINEA_10",
            "CLAVE_TECNICO",
            "SIMBOLO_MONEDA_LOCAL",
            "SIMBOLO_DOLAR",
            "SIMBOLO_EURO",
            "DIAS_CIERRE",
            "HORAS_ECHO",
            "HABILITA_CIERRE",
            "HABILITA_CIERRE_AUTOMATICO_DIA",
            "HORA_CIERRE",
            "HABILITA_PLC",
            "NOTAS",
            "TIPO_INICIALIZACION",
            "HABILITA_IMPRIMIR_RECIBO",
            "HABILITA_IMPRIMIR_CARD_HOLDER",
            "HABILITA_IMPRIMIR_COD_BARRAS",
            "TIPO_CODIGO_BARRAS",
            "COPIA_VOUCHER"/*,
            "MENSAJE_CIERRE1",
            "MENSAJE_CIERRE2"*/

    };

    public static TCONF getSingletonInstance(){
        if (tconf == null){
            tconf = new TCONF();
        }else{
            Log.d("TCONF", "No se puede crear otro objeto, ya existe");
        }
        return tconf;
    }

    public void setTCONF(String column, String value){
        switch (column){
            case "COMERCIO_ID":
                setCOMERCIO_ID(value);
                break;
            case "NOMBRE_COMERCIO":
                setNOMBRE_COMERCIO(value);
                break;
            case "DIRECCION_PRINCIPAL":
                setDIRECCION_PRINCIPAL(value);
                break;
            case "DIRECCION_SECUNDARIA":
                setDIRECCION_SECUNDARIA(value);
                break;
            case "TELEFONO_COMERCIO":
                setTELEFONO_COMERCIO(value);
                break;
            case "LINEA_AUX":
                setLINEA_AUX(value);
                break;
            case "CIUDAD":
                setCIUDAD(value);
                break;
            case "RUC":
                setRUC(value);
                break;
            case "HOST":
                setHOST(value);
                break;
            case "CARD_ACCP_TERM":
                setCARD_ACCP_TERM(value);
                break;
            case "CARD_ACCP_MERCH":
                setCARD_ACCP_MERCH(value);
                break;
            case "NUMERO_LOTE":
                setNUMERO_LOTE(value);
                break;
            case "MONEDA":
                setMONEDA(value);
                break;
            /*case "GRUPO_RANGOS":
                setGRUPO_RANGOS(value);
                break;*/
            case "REVERSO":
                setREVERSO(value);
                break;
            case "HABILITAR_IMPUESTO":
                setHABILITAR_IMPUESTO(value);
                break;
            case "LABEL_IMPUESTO":
                setLABEL_IMPUESTO(value);
                break;
            case "TIPO_IMPUESTO":
                setTIPO_IMPUESTO(value);
                break;
            case "PORCENTAJE_MAXIMO_IMPUESTO":
                setPORCENTAJE_MAXIMO_IMPUESTO(value);
                break;
            case "TARIFA_CERO":
                setTARIFA_CERO(value);
                break;
            case "CAPTURA_ELECTRONICA":
                setCAPTURA_ELECTRONICA(value);
                break;
            case "PIN_BYPASS":
                setPIN_BYPASS(value);
                break;
            case "CAJA_REGISTRADORA":
                setCAJA_REGISTRADORA(value);
                break;
            case "ENTRADA_MANUAL_PREAUTO":
                setENTRADA_MANUAL_PREAUTO(value);
                break;
            case "MONTO_MAXIMO_TRANSACCION":
                setMONTO_MAXIMO_TRANSACCION(value);
                break;
            case "MONTO_MINIMO_TRANSACCION":
                setMONTO_MINIMO_TRANSACCION(value);
                break;
            case "HABILITAR_SERVICIO":
                setHABILITAR_SERVICIO(value);
                break;
            case "LABEL_SERVICIO":
                setLABEL_SERVICIO(value);
                break;
            case "TIPO_SERVICIO":
                setTIPO_SERVICIO(value);
                break;
            case "PORCENTAJE_MAXIMO_SERVICIO":
                setPORCENTAJE_MAXIMO_SERVICIO(value);
                break;
            case "HABILITAR_ICE":
                setHABILITAR_ICE(value);
                break;
            case "LABEL_ICE":
                setLABEL_ICE(value);
                break;
            case "TIPO_ICE":
                setTIPO_ICE(value);
                break;
            case "PORCENTAJE_MAXIMO_ICE":
                setPORCENTAJE_MAXIMO_ICE(value);
                break;
            case "TRANSACCION_VENTA":
                setTRANSACCION_VENTA(value);
                break;
            case "TRANSACCION_DIFERIDO":
                setTRANSACCION_DIFERIDO(value);
                break;
            case "TRANSACCION_ANULACION":
                setTRANSACCION_ANULACION(value);
                break;
            case "TRANSACCION_PAGOS_VARIOS":
                setTRANSACCION_PAGOS_VARIOS(value);
                break;
            case "TRANSACCION_CASH_OVER":
                setTRANSACCION_CASH_OVER(value);
                break;
            case "TRANSACCION_PRE_VOUCHER":
                setTRANSACCION_PRE_VOUCHER(value);
                break;
            case "TRANSACCION_PAGOS_ELECTRONICOS":
                setTRANSACCION_PAGOS_ELECTRONICOS(value);
                break;
            case "TRANSACCION_PRE_AUTO":
                setTRANSACCION_PRE_AUTO(value);
                break;
            case "GRUPO_PROMPTS":
                setGRUPO_PROMPTS(value);
                break;
            case "GRUPO_PAGOS_VARIOS":
                setGRUPO_PAGOS_VARIOS(value);
                break;
            case "GRUPO_PAGOS_ELECTRONICOS":
                setGRUPO_PAGOS_ELECTRONICOS(value);
                break;
            case "HABILITAR_PROPINA":
                setHABILITAR_PROPINA(value);
                break;
            case "LABEL_PROPINA":
                setLABEL_PROPINA(value);
                break;
            case "TIPO_PROPINA":
                setTIPO_PROPINA(value);
                break;
            case "PORCENTAJE_MAXIMO_PROPINA":
                setPORCENTAJE_MAXIMO_PROPINA(value);
                break;
            case "HABILITAR_OTROS_IMPUESTOS":
                setHABILITAR_OTROS_IMPUESTOS(value);
                break;
            case "LABEL_OTROS_IMPUESTOS":
                setLABEL_OTROS_IMPUESTOS(value);
                break;
            case "TIPO_OTROS_IMPUESTOS":
                setTIPO_OTROS_IMPUESTOS(value);
                break;
            case "PORCENTAJE_MAXIMO":
                setPORCENTAJE_MAXIMO(value);
                break;
            case "HABILITA_MONTO_FIJO":
                setHABILITA_MONTO_FIJO(value);
                break;
            case "VALOR_MONTO_FIJO":
                setVALOR_MONTO_FIJO(value);
                break;
            case "NO_PERMITIR_2_TRANS_MISMO_TARJ":
                setNO_PERMITIR_2_TRANS_MISMO_TARJ(value);
                break;
            case "HABILITA_FIRMA":
                setHABILITAR_FIRMA(value);
                break;

            case "CLAVE_COMERCIO":
                setCLAVE_COMERCIO(value);
                break;
            case "NUM_SERIAL":
                setNUM_SERIAL(value);
                break;
            case "HEADER_COMERCIO":
                setHEADER_COMERCIO(value);
                break;
            case "HEADER_DIRECCION_1":
                setHEADER_DIRECCION_1(value);
                break;
            case "HEADER_DIRECCION_2":
                setHEADER_DIRECCION_2(value);
                break;
            case "HEADER_TELEFONO":
                setHEADER_TELEFONO(value);
                break;
            case "HEADER_LINEA_AUX":
                setHEADER_LINEA_AUX(value);
                break;
            case "FOOTER_LINEA_1":
                setFOOTER_LINEA_1(value);
                break;
            case "FOOTER_LINEA_2":
                setFOOTER_LINEA_2(value);
                break;
            case "FOOTER_LINEA_3":
                setFOOTER_LINEA_3(value);
                break;
            case "FOOTER_LINEA_4":
                setFOOTER_LINEA_4(value);
                break;
            case "FOOTER_LINEA_5":
                setFOOTER_LINEA_5(value);
                break;
            case "FOOTER_LINEA_6":
                setFOOTER_LINEA_6(value);
                break;
            case "FOOTER_LINEA_7":
                setFOOTER_LINEA_7(value);
                break;
            case "FOOTER_LINEA_8":
                setFOOTER_LINEA_8(value);
                break;
            case "FOOTER_LINEA_9":
                setFOOTER_LINEA_9(value);
                break;
            case "FOOTER_LINEA_10":
                setFOOTER_LINEA_10(value);
                break;
            case "CLAVE_TECNICO":
                setCLAVE_TECNICO(value);
                break;
            case "SIMBOLO_MONEDA_LOCAL":
                setSIMBOLO_MONEDA_LOCAL(value);
                break;
            case "SIMBOLO_DOLAR":
                setSIMBOLO_DOLAR(value);
                break;
            case "SIMBOLO_EURO":
                setSIMBOLO_EURO(value);
                break;
            case "DIAS_CIERRE":
                setDIAS_CIERRE(value);
                break;
            case "HORAS_ECHO":
                setHORAS_ECHO(value);
                break;
            case "HABILITA_CIERRE":
                setHABILITA_CIERRE(value);
                break;
            case "HABILITA_CIERRE_AUTOMATICO_DIA":
                setHABILITA_CIERRE_AUTOMATICO_DIA(value);
                break;
            case "HORA_CIERRE":
                setHORA_CIERRE(value);
                break;
            case "HABILITA_PLC":
                setHABILITA_PLC(value);
                break;
            case "NOTAS":
                setNOTAS(value);
                break;
            case "TIPO_INICIALIZACION":
                setTIPO_INICIALIZACION(value);
                break;
            case "HABILITA_IMPRIMIR_RECIBO":
                setHABILITA_IMPRIMIR_RECIBO(value);
                break;
            case "HABILITA_IMPRIMIR_CARD_HOLDER":
                setHABILITA_IMPRIMIR_CARD_HOLDER(value);
                break;
            case "HABILITA_IMPRIMIR_COD_BARRAS":
                setHABILITA_IMPRIMIR_COD_BARRAS(value);
                break;
            case "TIPO_CODIGO_BARRAS":
                setTIPO_CODIGO_BARRAS(value);
                break;
            case "COPIA_VOUCHER":
                setCOPIA_VOUCHER(value);
                break;
            /*case "MENSAJE_CIERRE1":
                setMENSAJE_CIERRE1(value);
                break;
            case "MENSAJE_CIERRE2":
                setMENSAJE_CIERRE2(value);
                break;*/

            default:
                break;
        }
    }

    public void clearTCONF() {
        for (String s : TCONF.fields) {
            setTCONF(s, "");
        }
    }

    public void selectTconf(Context context){
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        StringBuilder sql = new StringBuilder();

        sql.append("select ");
        int counter = 1;
        for (String s : TCONF.fields) {
            sql.append(s);
            if (counter++ < TCONF.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from TCONF");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;
            while (!cursor.isAfterLast()) {
                clearTCONF();
                indexColumn = 0;
                for (String s : TCONF.fields) {
                    //setTCONF(s, cursor.getString(indexColumn++).trim());
                    setTCONF(s, cursor.getString(indexColumn++).trim());
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

    //Obtenemos el grupo_id de la tabla TCONF
    public String GetGrupoIdTconf(Context context) {

        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        String grupo_id = "-1";
        StringBuilder sb = new StringBuilder();
        sb.append("select trim(grupo_id) as gid ");
        sb.append("from grupo_prompt ");
        sb.append("where trim(grupo_nombre) in ");
        sb.append("(select trim(grupo_prompts) from TCONF);");
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

    public String getCOMERCIO_ID() {
        return COMERCIO_ID;
    }

    public void setCOMERCIO_ID(String COMERCIO_ID) {
        this.COMERCIO_ID = COMERCIO_ID;
    }

    public String getNOMBRE_COMERCIO() {
        return NOMBRE_COMERCIO;
    }

    public void setNOMBRE_COMERCIO(String NOMBRE_COMERCIO) {
        this.NOMBRE_COMERCIO = NOMBRE_COMERCIO;
    }

    public String getDIRECCION_PRINCIPAL() {
        return DIRECCION_PRINCIPAL;
    }

    public void setDIRECCION_PRINCIPAL(String DIRECCION_PRINCIPAL) {
        this.DIRECCION_PRINCIPAL = DIRECCION_PRINCIPAL;
    }

    public String getDIRECCION_SECUNDARIA() {
        return DIRECCION_SECUNDARIA;
    }

    public void setDIRECCION_SECUNDARIA(String DIRECCION_SECUNDARIA) {
        this.DIRECCION_SECUNDARIA = DIRECCION_SECUNDARIA;
    }

    public String getTELEFONO_COMERCIO() {
        return TELEFONO_COMERCIO;
    }

    public void setTELEFONO_COMERCIO(String TELEFONO_COMERCIO) {
        this.TELEFONO_COMERCIO = TELEFONO_COMERCIO;
    }

    public String getLINEA_AUX() {
        return LINEA_AUX;
    }

    public void setLINEA_AUX(String LINEA_AUX) {
        this.LINEA_AUX = LINEA_AUX;
    }

    public String getCIUDAD() {
        return CIUDAD;
    }

    public void setCIUDAD(String CIUDAD) {
        this.CIUDAD = CIUDAD;
    }

    public String getRUC() {
        return RUC;
    }

    public void setRUC(String RUC) {
        this.RUC = RUC;
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public String getCARD_ACCP_TERM() {
        return CARD_ACCP_TERM;
    }

    public void setCARD_ACCP_TERM(String CARD_ACCP_TERM) {
        this.CARD_ACCP_TERM = CARD_ACCP_TERM;
    }

    public String getCARD_ACCP_MERCH() {
        return CARD_ACCP_MERCH;
    }

    public void setCARD_ACCP_MERCH(String CARD_ACCP_MERCH) {
        this.CARD_ACCP_MERCH = CARD_ACCP_MERCH;
    }

    public String getNUMERO_LOTE() {
        return NUMERO_LOTE;
    }

    public void setNUMERO_LOTE(String NUMERO_LOTE) {
        this.NUMERO_LOTE = NUMERO_LOTE;
    }

    public String getMONEDA() {
        return MONEDA;
    }

    public void setMONEDA(String MONEDA) {
        this.MONEDA = MONEDA;
    }

    /*public String getGRUPO_RANGOS() {
        return GRUPO_RANGOS;
    }

    public void setGRUPO_RANGOS(String GRUPO_RANGOS) {
        this.GRUPO_RANGOS = GRUPO_RANGOS;
    }*/

    public String getREVERSO() {
        return REVERSO;
    }

    public void setREVERSO(String REVERSO) {
        this.REVERSO = REVERSO;
    }

    public String getHABILITAR_IMPUESTO() {
        return HABILITAR_IMPUESTO;
    }

    public void setHABILITAR_IMPUESTO(String HABILITAR_IMPUESTO) {
        this.HABILITAR_IMPUESTO = HABILITAR_IMPUESTO;
    }

    public String getLABEL_IMPUESTO() {
        return LABEL_IMPUESTO;
    }

    public void setLABEL_IMPUESTO(String LABEL_IMPUESTO) {
        this.LABEL_IMPUESTO = LABEL_IMPUESTO;
    }

    public String getTIPO_IMPUESTO() {
        return TIPO_IMPUESTO;
    }

    public void setTIPO_IMPUESTO(String TIPO_IMPUESTO) {
        this.TIPO_IMPUESTO = TIPO_IMPUESTO;
    }

    public String getPORCENTAJE_MAXIMO_IMPUESTO() {
        return PORCENTAJE_MAXIMO_IMPUESTO;
    }

    public void setPORCENTAJE_MAXIMO_IMPUESTO(String PORCENTAJE_MAXIMO_IMPUESTO) {
        this.PORCENTAJE_MAXIMO_IMPUESTO = PORCENTAJE_MAXIMO_IMPUESTO;
    }

    public String getTARIFA_CERO() {
        return TARIFA_CERO;
    }

    public void setTARIFA_CERO(String TARIFA_CERO) {
        this.TARIFA_CERO = TARIFA_CERO;
    }

    public String getCAPTURA_ELECTRONICA() {
        return CAPTURA_ELECTRONICA;
    }

    public void setCAPTURA_ELECTRONICA(String CAPTURA_ELECTRONICA) {
        this.CAPTURA_ELECTRONICA = CAPTURA_ELECTRONICA;
    }

    public String getPIN_BYPASS() {
        return PIN_BYPASS;
    }

    public void setPIN_BYPASS(String PIN_BYPASS) {
        this.PIN_BYPASS = PIN_BYPASS;
    }

    public String getCAJA_REGISTRADORA() {
        return CAJA_REGISTRADORA;
    }

    public void setCAJA_REGISTRADORA(String CAJA_REGISTRADORA) {
        this.CAJA_REGISTRADORA = CAJA_REGISTRADORA;
    }

    public String getENTRADA_MANUAL_PREAUTO() {
        return ENTRADA_MANUAL_PREAUTO;
    }

    public void setENTRADA_MANUAL_PREAUTO(String ENTRADA_MANUAL_PREAUTO) {
        this.ENTRADA_MANUAL_PREAUTO = ENTRADA_MANUAL_PREAUTO;
    }

    public String getMONTO_MAXIMO_TRANSACCION() {
        return MONTO_MAXIMO_TRANSACCION;
    }

    public void setMONTO_MAXIMO_TRANSACCION(String MONTO_MAXIMO_TRANSACCION) {
        this.MONTO_MAXIMO_TRANSACCION = MONTO_MAXIMO_TRANSACCION;
    }

    public String getMONTO_MINIMO_TRANSACCION() {
        return MONTO_MINIMO_TRANSACCION;
    }

    public void setMONTO_MINIMO_TRANSACCION(String MONTO_MINIMO_TRANSACCION) {
        this.MONTO_MINIMO_TRANSACCION = MONTO_MINIMO_TRANSACCION;
    }

    public String getHABILITAR_SERVICIO() {
        return HABILITAR_SERVICIO;
    }

    public void setHABILITAR_SERVICIO(String HABILITAR_SERVICIO) {
        this.HABILITAR_SERVICIO = HABILITAR_SERVICIO;
    }

    public String getLABEL_SERVICIO() {
        return LABEL_SERVICIO;
    }

    public void setLABEL_SERVICIO(String LABEL_SERVICIO) {
        this.LABEL_SERVICIO = LABEL_SERVICIO;
    }

    public String getTIPO_SERVICIO() {
        return TIPO_SERVICIO;
    }

    public void setTIPO_SERVICIO(String TIPO_SERVICIO) {
        this.TIPO_SERVICIO = TIPO_SERVICIO;
    }

    public String getPORCENTAJE_MAXIMO_SERVICIO() {
        return PORCENTAJE_MAXIMO_SERVICIO;
    }

    public void setPORCENTAJE_MAXIMO_SERVICIO(String PORCENTAJE_MAXIMO_SERVICIO) {
        this.PORCENTAJE_MAXIMO_SERVICIO = PORCENTAJE_MAXIMO_SERVICIO;
    }

    public String getHABILITAR_ICE() {
        return HABILITAR_ICE;
    }

    public void setHABILITAR_ICE(String HABILITAR_ICE) {
        this.HABILITAR_ICE = HABILITAR_ICE;
    }

    public String getLABEL_ICE() {
        return LABEL_ICE;
    }

    public void setLABEL_ICE(String LABEL_ICE) {
        this.LABEL_ICE = LABEL_ICE;
    }

    public String getTIPO_ICE() {
        return TIPO_ICE;
    }

    public void setTIPO_ICE(String TIPO_ICE) {
        this.TIPO_ICE = TIPO_ICE;
    }

    public String getPORCENTAJE_MAXIMO_ICE() {
        return PORCENTAJE_MAXIMO_ICE;
    }

    public void setPORCENTAJE_MAXIMO_ICE(String PORCENTAJE_MAXIMO_ICE) {
        this.PORCENTAJE_MAXIMO_ICE = PORCENTAJE_MAXIMO_ICE;
    }

    public String getTRANSACCION_VENTA() {
        return TRANSACCION_VENTA;
    }

    public void setTRANSACCION_VENTA(String TRANSACCION_VENTA) {
        this.TRANSACCION_VENTA = TRANSACCION_VENTA;
    }

    public String getTRANSACCION_DIFERIDO() {
        return TRANSACCION_DIFERIDO;
    }

    public void setTRANSACCION_DIFERIDO(String TRANSACCION_DIFERIDO) {
        this.TRANSACCION_DIFERIDO = TRANSACCION_DIFERIDO;
    }

    public String getTRANSACCION_ANULACION() {
        return TRANSACCION_ANULACION;
    }

    public void setTRANSACCION_ANULACION(String TRANSACCION_ANULACION) {
        this.TRANSACCION_ANULACION = TRANSACCION_ANULACION;
    }

    public String getTRANSACCION_PAGOS_VARIOS() {
        return TRANSACCION_PAGOS_VARIOS;
    }

    public void setTRANSACCION_PAGOS_VARIOS(String TRANSACCION_PAGOS_VARIOS) {
        this.TRANSACCION_PAGOS_VARIOS = TRANSACCION_PAGOS_VARIOS;
    }

    public String getTRANSACCION_CASH_OVER() {
        return TRANSACCION_CASH_OVER;
    }

    public void setTRANSACCION_CASH_OVER(String TRANSACCION_CASH_OVER) {
        this.TRANSACCION_CASH_OVER = TRANSACCION_CASH_OVER;
    }

    public String getTRANSACCION_PRE_VOUCHER() {
        return TRANSACCION_PRE_VOUCHER;
    }

    public void setTRANSACCION_PRE_VOUCHER(String TRANSACCION_PRE_VOUCHER) {
        this.TRANSACCION_PRE_VOUCHER = TRANSACCION_PRE_VOUCHER;
    }

    public String getTRANSACCION_PAGOS_ELECTRONICOS() {
        return TRANSACCION_PAGOS_ELECTRONICOS;
    }

    public void setTRANSACCION_PAGOS_ELECTRONICOS(String TRANSACCION_PAGOS_ELECTRONICOS) {
        this.TRANSACCION_PAGOS_ELECTRONICOS = TRANSACCION_PAGOS_ELECTRONICOS;
    }

    public String getTRANSACCION_PRE_AUTO() {
        return TRANSACCION_PRE_AUTO;
    }

    public void setTRANSACCION_PRE_AUTO(String TRANSACCION_PRE_AUTO) {
        this.TRANSACCION_PRE_AUTO = TRANSACCION_PRE_AUTO;
    }

    public String getGRUPO_PROMPTS() {
        return GRUPO_PROMPTS;
    }

    public void setGRUPO_PROMPTS(String GRUPO_PROMPTS) {
        this.GRUPO_PROMPTS = GRUPO_PROMPTS;
    }

    public String getGRUPO_PAGOS_VARIOS() {
        return GRUPO_PAGOS_VARIOS;
    }

    public void setGRUPO_PAGOS_VARIOS(String GRUPO_PAGOS_VARIOS) {
        this.GRUPO_PAGOS_VARIOS = GRUPO_PAGOS_VARIOS;
    }

    public String getGRUPO_PAGOS_ELECTRONICOS() {
        return GRUPO_PAGOS_ELECTRONICOS;
    }

    public void setGRUPO_PAGOS_ELECTRONICOS(String GRUPO_PAGOS_ELECTRONICOS) {
        this.GRUPO_PAGOS_ELECTRONICOS = GRUPO_PAGOS_ELECTRONICOS;
    }

    public String getHABILITAR_PROPINA() {
        return HABILITAR_PROPINA;
    }

    public void setHABILITAR_PROPINA(String HABILITAR_PROPINA) {
        this.HABILITAR_PROPINA = HABILITAR_PROPINA;
    }

    public String getLABEL_PROPINA() {
        return LABEL_PROPINA;
    }

    public void setLABEL_PROPINA(String LABEL_PROPINA) {
        this.LABEL_PROPINA = LABEL_PROPINA;
    }

    public String getTIPO_PROPINA() {
        return TIPO_PROPINA;
    }

    public void setTIPO_PROPINA(String TIPO_PROPINA) {
        this.TIPO_PROPINA = TIPO_PROPINA;
    }

    public String getPORCENTAJE_MAXIMO_PROPINA() {
        return PORCENTAJE_MAXIMO_PROPINA;
    }

    public void setPORCENTAJE_MAXIMO_PROPINA(String PORCENTAJE_MAXIMO_PROPINA) {
        this.PORCENTAJE_MAXIMO_PROPINA = PORCENTAJE_MAXIMO_PROPINA;
    }

    public String getHABILITAR_OTROS_IMPUESTOS() {
        return HABILITAR_OTROS_IMPUESTOS;
    }

    public void setHABILITAR_OTROS_IMPUESTOS(String HABILITAR_OTROS_IMPUESTOS) {
        this.HABILITAR_OTROS_IMPUESTOS = HABILITAR_OTROS_IMPUESTOS;
    }

    public String getLABEL_OTROS_IMPUESTOS() {
        return LABEL_OTROS_IMPUESTOS;
    }

    public void setLABEL_OTROS_IMPUESTOS(String LABEL_OTROS_IMPUESTOS) {
        this.LABEL_OTROS_IMPUESTOS = LABEL_OTROS_IMPUESTOS;
    }

    public String getTIPO_OTROS_IMPUESTOS() {
        return TIPO_OTROS_IMPUESTOS;
    }

    public void setTIPO_OTROS_IMPUESTOS(String TIPO_OTROS_IMPUESTOS) {
        this.TIPO_OTROS_IMPUESTOS = TIPO_OTROS_IMPUESTOS;
    }

    public String getPORCENTAJE_MAXIMO() {
        return PORCENTAJE_MAXIMO;
    }

    public void setPORCENTAJE_MAXIMO(String PORCENTAJE_MAXIMO) {
        this.PORCENTAJE_MAXIMO = PORCENTAJE_MAXIMO;
    }

    public String getHABILITA_MONTO_FIJO() {
        return HABILITA_MONTO_FIJO;
    }

    public void setHABILITA_MONTO_FIJO(String HABILITA_MONTO_FIJO) {
        this.HABILITA_MONTO_FIJO = HABILITA_MONTO_FIJO;
    }

    public String getVALOR_MONTO_FIJO() {
        return VALOR_MONTO_FIJO;
    }

    public void setVALOR_MONTO_FIJO(String VALOR_MONTO_FIJO) {
        this.VALOR_MONTO_FIJO = VALOR_MONTO_FIJO;
    }

    public String getNO_PERMITIR_2_TRANS_MISMO_TARJ() {
        return NO_PERMITIR_2_TRANS_MISMO_TARJ;
    }

    public void setNO_PERMITIR_2_TRANS_MISMO_TARJ(String NO_PERMITIR_2_TRANS_MISMO_TARJ) {
        this.NO_PERMITIR_2_TRANS_MISMO_TARJ = NO_PERMITIR_2_TRANS_MISMO_TARJ;
    }

    public String getHABILITAR_FIRMA() {
        return HABILITA_FIRMA;
    }

    public void setHABILITAR_FIRMA(String HABILITAR_FIRMA) {
        this.HABILITA_FIRMA = HABILITAR_FIRMA;
    }

    public String getCLAVE_COMERCIO() {
        return CLAVE_COMERCIO;
    }

    public void setCLAVE_COMERCIO(String CLAVE_COMERCIO) {
        this.CLAVE_COMERCIO = CLAVE_COMERCIO;
    }

    public String getNUM_SERIAL() {
        return NUM_SERIAL;
    }

    public void setNUM_SERIAL(String NUM_SERIAL) {
        this.NUM_SERIAL = NUM_SERIAL;
    }

    public String getHEADER_COMERCIO() {
        return HEADER_COMERCIO;
    }

    public void setHEADER_COMERCIO(String HEADER_COMERCIO) {
        this.HEADER_COMERCIO = HEADER_COMERCIO;
    }

    public String getHEADER_DIRECCION_1() {
        return HEADER_DIRECCION_1;
    }

    public void setHEADER_DIRECCION_1(String HEADER_DIRECCION_1) {
        this.HEADER_DIRECCION_1 = HEADER_DIRECCION_1;
    }

    public String getHEADER_DIRECCION_2() {
        return HEADER_DIRECCION_2;
    }

    public void setHEADER_DIRECCION_2(String HEADER_DIRECCION_2) {
        this.HEADER_DIRECCION_2 = HEADER_DIRECCION_2;
    }

    public String getHEADER_TELEFONO() {
        return HEADER_TELEFONO;
    }

    public void setHEADER_TELEFONO(String HEADER_TELEFONO) {
        this.HEADER_TELEFONO = HEADER_TELEFONO;
    }

    public String getHEADER_LINEA_AUX() {
        return HEADER_LINEA_AUX;
    }

    public void setHEADER_LINEA_AUX(String HEADER_LINEA_AUX) {
        this.HEADER_LINEA_AUX = HEADER_LINEA_AUX;
    }

    public String getFOOTER_LINEA_1() {
        return FOOTER_LINEA_1;
    }

    public void setFOOTER_LINEA_1(String FOOTER_LINEA_1) {
        this.FOOTER_LINEA_1 = FOOTER_LINEA_1;
    }

    public String getFOOTER_LINEA_2() {
        return FOOTER_LINEA_2;
    }

    public void setFOOTER_LINEA_2(String FOOTER_LINEA_2) {
        this.FOOTER_LINEA_2 = FOOTER_LINEA_2;
    }

    public String getFOOTER_LINEA_3() {
        return FOOTER_LINEA_3;
    }

    public void setFOOTER_LINEA_3(String FOOTER_LINEA_3) {
        this.FOOTER_LINEA_3 = FOOTER_LINEA_3;
    }

    public String getFOOTER_LINEA_4() {
        return FOOTER_LINEA_4;
    }

    public void setFOOTER_LINEA_4(String FOOTER_LINEA_4) {
        this.FOOTER_LINEA_4 = FOOTER_LINEA_4;
    }

    public String getFOOTER_LINEA_5() {
        return FOOTER_LINEA_5;
    }

    public void setFOOTER_LINEA_5(String FOOTER_LINEA_5) {
        this.FOOTER_LINEA_5 = FOOTER_LINEA_5;
    }

    public String getFOOTER_LINEA_6() {
        return FOOTER_LINEA_6;
    }

    public void setFOOTER_LINEA_6(String FOOTER_LINEA_6) {
        this.FOOTER_LINEA_6 = FOOTER_LINEA_6;
    }

    public String getFOOTER_LINEA_7() {
        return FOOTER_LINEA_7;
    }

    public void setFOOTER_LINEA_7(String FOOTER_LINEA_7) {
        this.FOOTER_LINEA_7 = FOOTER_LINEA_7;
    }

    public String getFOOTER_LINEA_8() {
        return FOOTER_LINEA_8;
    }

    public void setFOOTER_LINEA_8(String FOOTER_LINEA_8) {
        this.FOOTER_LINEA_8 = FOOTER_LINEA_8;
    }

    public String getFOOTER_LINEA_9() {
        return FOOTER_LINEA_9;
    }

    public void setFOOTER_LINEA_9(String FOOTER_LINEA_9) {
        this.FOOTER_LINEA_9 = FOOTER_LINEA_9;
    }

    public String getFOOTER_LINEA_10() {
        return FOOTER_LINEA_10;
    }

    public void setFOOTER_LINEA_10(String FOOTER_LINEA_10) {
        this.FOOTER_LINEA_10 = FOOTER_LINEA_10;
    }

    public String getCLAVE_TECNICO() {
        return CLAVE_TECNICO;
    }

    public void setCLAVE_TECNICO(String CLAVE_TECNICO) {
        this.CLAVE_TECNICO = CLAVE_TECNICO;
    }

    public String getSIMBOLO_MONEDA_LOCAL() {
        return SIMBOLO_MONEDA_LOCAL;
    }

    public void setSIMBOLO_MONEDA_LOCAL(String SIMBOLO_MONEDA_LOCAL) {
        this.SIMBOLO_MONEDA_LOCAL = SIMBOLO_MONEDA_LOCAL;
    }

    public String getSIMBOLO_DOLAR() {
        return SIMBOLO_DOLAR;
    }

    public void setSIMBOLO_DOLAR(String SIMBOLO_DOLAR) {
        this.SIMBOLO_DOLAR = SIMBOLO_DOLAR;
    }

    public String getSIMBOLO_EURO() {
        return SIMBOLO_EURO;
    }

    public void setSIMBOLO_EURO(String SIMBOLO_EURO) {
        this.SIMBOLO_EURO = SIMBOLO_EURO;
    }

    public String getDIAS_CIERRE() {
        return DIAS_CIERRE;
    }

    public void setDIAS_CIERRE(String DIAS_CIERRE) {
        this.DIAS_CIERRE = DIAS_CIERRE;
    }

    public String getHORAS_ECHO() {
        return HORAS_ECHO;
    }

    public void setHORAS_ECHO(String HORAS_ECHO) {
        this.HORAS_ECHO = HORAS_ECHO;
    }

    public String getHABILITA_CIERRE() {
        return HABILITA_CIERRE;
    }

    public void setHABILITA_CIERRE(String HABILITA_CIERRE) {
        this.HABILITA_CIERRE = HABILITA_CIERRE;
    }

    public String getHABILITA_CIERRE_AUTOMATICO_DIA() {
        return HABILITA_CIERRE_AUTOMATICO_DIA;
    }

    public void setHABILITA_CIERRE_AUTOMATICO_DIA(String HABILITA_CIERRE_AUTOMATICO_DIA) {
        this.HABILITA_CIERRE_AUTOMATICO_DIA = HABILITA_CIERRE_AUTOMATICO_DIA;
    }

    public String getHORA_CIERRE() {
        return HORA_CIERRE;
    }

    public void setHORA_CIERRE(String HORA_CIERRE) {
        this.HORA_CIERRE = HORA_CIERRE;
    }

    public String getHABILITA_PLC() {
        return HABILITA_PLC;
    }

    public void setHABILITA_PLC(String HABILITA_PLC) {
        this.HABILITA_PLC = HABILITA_PLC;
    }

    public String getNOTAS() {
        return NOTAS;
    }

    public void setNOTAS(String NOTAS) {
        this.NOTAS = NOTAS;
    }

    public String getTIPO_INICIALIZACION() {
        return TIPO_INICIALIZACION;
    }

    public void setTIPO_INICIALIZACION(String TIPO_INICIALIZACION) {
        this.TIPO_INICIALIZACION = TIPO_INICIALIZACION;
    }

    public String getHABILITA_IMPRIMIR_RECIBO() {
        return HABILITA_IMPRIMIR_RECIBO;
    }

    public void setHABILITA_IMPRIMIR_RECIBO(String HABILITA_IMPRIMIR_RECIBO) {
        this.HABILITA_IMPRIMIR_RECIBO = HABILITA_IMPRIMIR_RECIBO;
    }

    public String getHABILITA_IMPRIMIR_CARD_HOLDER() {
        return HABILITA_IMPRIMIR_CARD_HOLDER;
    }

    public void setHABILITA_IMPRIMIR_CARD_HOLDER(String HABILITA_IMPRIMIR_CARD_HOLDER) {
        this.HABILITA_IMPRIMIR_CARD_HOLDER = HABILITA_IMPRIMIR_CARD_HOLDER;
    }

    public String getHABILITA_IMPRIMIR_COD_BARRAS() {
        return HABILITA_IMPRIMIR_COD_BARRAS;
    }

    public void setHABILITA_IMPRIMIR_COD_BARRAS(String HABILITA_IMPRIMIR_COD_BARRAS) {
        this.HABILITA_IMPRIMIR_COD_BARRAS = HABILITA_IMPRIMIR_COD_BARRAS;
    }

    public String getTIPO_CODIGO_BARRAS() {
        return TIPO_CODIGO_BARRAS;
    }

    public void setTIPO_CODIGO_BARRAS(String TIPO_CODIGO_BARRAS) {
        this.TIPO_CODIGO_BARRAS = TIPO_CODIGO_BARRAS;
    }

    public String getCOPIA_VOUCHER() {
        return COPIA_VOUCHER;
    }

    public void setCOPIA_VOUCHER(String COPIA_VOUCHER) {
        this.COPIA_VOUCHER = COPIA_VOUCHER;
    }

    public String getMENSAJE_CIERRE1() {
        return MENSAJE_CIERRE1;
    }

    public void setMENSAJE_CIERRE1(String MENSAJE_CIERRE1) {
        this.MENSAJE_CIERRE1 = MENSAJE_CIERRE1;
    }

    public String getMENSAJE_CIERRE2() {
        return MENSAJE_CIERRE2;
    }

    public void setMENSAJE_CIERRE2(String MENSAJE_CIERRE2) {
        this.MENSAJE_CIERRE2 = MENSAJE_CIERRE2;
    }
}
