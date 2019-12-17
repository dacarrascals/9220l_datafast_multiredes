package com.datafast.datos_pruebas;

import com.datafast.inicializacion.prompts.Prompt;

import java.util.ArrayList;

import static com.android.newpos.pay.StartAppDATAFAST.host_confi;
import static com.android.newpos.pay.StartAppDATAFAST.listPrompts;
import static com.android.newpos.pay.StartAppDATAFAST.rango;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;


// OJO clase solo para pruebas
public class DebugInit {

    public static void llenarClaseRango() {

        rango.setIDENTIFICADOR_RANGO("DINERS");
        rango.setID_AS400("17");
        rango.setRANGO_MIN("1000000000");
        rango.setRANGO_MAX("9999999999");
        rango.setGRUPO_AS400("19");
        rango.setTIPO_CUENTA_DEFAULT("54");
        rango.setNII("004");
        rango.setTIPO_MASCARA("CENTRO");
        rango.setGRUPO_PROMPTS("GRUPO02");
        rango.setTIPO_MONTO_FIJO("Recibido");
        rango.setNOMBRE_EMISOR("DINERS");

        rango.setMANUAL("1");
        rango.setFECHA_EXP("1");
        rango.setCHECK_DIG("1");
        rango.setCVV2("1");
        rango.setV_4DBC("0");
        rango.setULTIMOS_4("1");
        rango.setTARJETA_CIERRE("1");
        rango.setINTER_OPER("1");
        rango.setDEBITO("1");
        rango.setPIN("1");
        rango.setTIPO_DE_CUENTA("0");
        rango.setPRE_VOUCHER("1");
        rango.setCASH_OVER("1");
        rango.setOMITIR_EMV("1");
        rango.setPIN_SERVICE_CODE("1");
        rango.setPERMITIR_TARJ_EXP("0");
    }

    public static void llenarPrompt(){

        Prompt prompt1 = new Prompt();
        Prompt prompt2 = new Prompt();

        listPrompts = new ArrayList<>();
        listPrompts.clear();

        /*prompt1.setCODIGO_PROMPTS("17");
        prompt1.setNOMBRE_PROMPTS("NRO.CUPO");
        prompt1.setTIPO_DATO("Numerico");
        prompt1.setLONGITUD_MAXIMA("7");
        prompt1.setLONGITUD_MINIMA("7");
        prompt1.setIMPRIMIR_PROMPT("1");
        prompt1.setSUMAR_TOTALES("0");
        prompt1.setVALOR_NEGATIVO("0");
        prompt1.setVENTA("1");
        prompt1.setDIFERIDO("0");
        prompt1.setVENTA_GASOLINERA("0");
        prompt1.setPAGOS_VARIOS("1");

        listPrompts.add(prompt1);

        prompt2.setCODIGO_PROMPTS("10");
        prompt2.setNOMBRE_PROMPTS("VENDEDOR");
        prompt2.setTIPO_DATO("Numerico");
        prompt2.setLONGITUD_MAXIMA("10");
        prompt2.setLONGITUD_MINIMA("1");
        prompt2.setIMPRIMIR_PROMPT("0");
        prompt2.setSUMAR_TOTALES("0");
        prompt2.setVALOR_NEGATIVO("0");
        prompt2.setVENTA("1");
        prompt2.setDIFERIDO("0");
        prompt2.setVENTA_GASOLINERA("0");
        prompt2.setPAGOS_VARIOS("1");

        listPrompts.add(prompt2);*/

    }

    public static void llenar_TCONF(){
        tconf.setCOMERCIO_ID("1");
        tconf.setNOMBRE_COMERCIO("LAB. PRUEBAS WPOSS");
        tconf.setDIRECCION_PRINCIPAL("CARRERA 40 # 24 A 77");
        tconf.setDIRECCION_SECUNDARIA("");
        tconf.setTELEFONO_COMERCIO("Telefono: 42533752");
        tconf.setLINEA_AUX("* DATAFAST *");
        tconf.setCIUDAD("BOGOTA");
        tconf.setRUC("0999999999991");
        tconf.setHOST("SIMULADOR");
        tconf.setCARD_ACCP_TERM("WPOS0001");//WPOS0001
        tconf.setCARD_ACCP_MERCH("7100020403     ");//"7100020403     "
        tconf.setNUMERO_LOTE("1");
        tconf.setMONEDA("Local");
        //tconf.setGRUPO_RANGOS("RangeGroup");
        tconf.setREVERSO("Antes de la Tran");
        tconf.setHABILITAR_IMPUESTO("1");
        tconf.setLABEL_IMPUESTO("IVA 12%");
        tconf.setTIPO_IMPUESTO("Desagregado");
        tconf.setPORCENTAJE_MAXIMO_IMPUESTO("12");
        tconf.setTARIFA_CERO("1");
        tconf.setCAPTURA_ELECTRONICA("1");
        tconf.setPIN_BYPASS("0");
        tconf.setCAJA_REGISTRADORA("0");
        tconf.setENTRADA_MANUAL_PREAUTO("0");
        tconf.setMONTO_MAXIMO_TRANSACCION("5000");
        tconf.setMONTO_MINIMO_TRANSACCION("100");
        tconf.setHABILITAR_SERVICIO("1");
        tconf.setLABEL_SERVICIO("Servicio");
        tconf.setTIPO_SERVICIO("Agregado");
        tconf.setPORCENTAJE_MAXIMO_SERVICIO("10");
        tconf.setHABILITAR_ICE("0");
        tconf.setLABEL_ICE("ICE");
        tconf.setTIPO_ICE("Manual");
        tconf.setPORCENTAJE_MAXIMO_ICE("00");
        tconf.setTRANSACCION_VENTA("1");
        tconf.setTRANSACCION_DIFERIDO("1");
        tconf.setTRANSACCION_ANULACION("1");
        tconf.setTRANSACCION_PAGOS_VARIOS("1");
        tconf.setTRANSACCION_CASH_OVER("1");
        tconf.setTRANSACCION_PRE_VOUCHER("1");
        tconf.setTRANSACCION_PAGOS_ELECTRONICOS("1");
        tconf.setTRANSACCION_PRE_AUTO("1");
        tconf.setGRUPO_PROMPTS("C-GENERAL");
        tconf.setGRUPO_PAGOS_VARIOS("PRUEBA AR");
        tconf.setGRUPO_PAGOS_ELECTRONICOS("PAGOS ELECTRON");
        tconf.setHABILITAR_PROPINA("1");
        tconf.setLABEL_PROPINA("Propina");
        tconf.setTIPO_PROPINA("Pregunta");
        tconf.setPORCENTAJE_MAXIMO_PROPINA("10");
        tconf.setHABILITAR_OTROS_IMPUESTOS("0");
        tconf.setLABEL_OTROS_IMPUESTOS("Otros Imp.");
        tconf.setTIPO_OTROS_IMPUESTOS("Manual");
        tconf.setPORCENTAJE_MAXIMO("10");
        tconf.setHABILITA_MONTO_FIJO("0");
        tconf.setVALOR_MONTO_FIJO("26");
        tconf.setNO_PERMITIR_2_TRANS_MISMO_TARJ("0");

        tconf.setHABILITAR_FIRMA("0");
        tconf.setCLAVE_COMERCIO("1111");
        tconf.setNUM_SERIAL("12345678");
        tconf.setHEADER_COMERCIO("");
        tconf.setHEADER_DIRECCION_1("");
        tconf.setHEADER_DIRECCION_2("");
        tconf.setTELEFONO_COMERCIO("");
        tconf.setLINEA_AUX("");
        tconf.setFOOTER_LINEA_1("DEBO Y PAGARE AL EMISOR INCONDICIONALMENTE");
        tconf.setFOOTER_LINEA_2("Y SIN PROTESTO EL TOTAL DE ESTE PAGARE MAS");
        tconf.setFOOTER_LINEA_3("LOS INTERESES Y CARGOS POR SERVICIO. EN");
        tconf.setFOOTER_LINEA_4("CASO DE MORA PAGARE LA TASA MAXIMA AUTO-");
        tconf.setFOOTER_LINEA_5("RIZADA PARA EL EMISOR. DECLARO QUE EL PRO-");
        tconf.setFOOTER_LINEA_6("DUCTO DE LA TRANSACCION NO SERA UTILIZADO");
        tconf.setFOOTER_LINEA_7("EN ACTIVIDADES DE LAVADO DE ACTIVOS,FINAN-");
        tconf.setFOOTER_LINEA_8("CIAMIENTO DEL TERRORISMO Y OTROS DELITOS.");
        tconf.setFOOTER_LINEA_9("");
        tconf.setFOOTER_LINEA_10("");
        tconf.setCLAVE_TECNICO("166831");
        tconf.setSIMBOLO_MONEDA_LOCAL("$");
        tconf.setSIMBOLO_DOLAR("$");
        tconf.setSIMBOLO_EURO("$");
        tconf.setDIAS_CIERRE("1");
        tconf.setHORAS_ECHO("12");
        tconf.setHABILITA_CIERRE("1");
        tconf.setHABILITA_CIERRE_AUTOMATICO_DIA("0");
        tconf.setHORA_CIERRE("12:30");
        tconf.setHABILITA_PLC("0");
        tconf.setNOTAS("");
        tconf.setTIPO_INICIALIZACION("full");
        tconf.setHABILITA_IMPRIMIR_RECIBO("1");
        tconf.setHABILITA_IMPRIMIR_CARD_HOLDER("1");
        tconf.setHABILITA_IMPRIMIR_COD_BARRAS("0");
        tconf.setTIPO_CODIGO_BARRAS("QR");
    }

    public static void llenarHostConfi(){

        host_confi.setID_HOST("");
        host_confi.setNOMBRE_HOST("");
        host_confi.setTIPO_COMUNICACION("");
        host_confi.setFORMATO_MENSAJE("");
        host_confi.setREINTENTOS("2");
        host_confi.setTIEMPO_ESPERA_CONEXION("30");
        host_confi.setTIEMPO_ESPERA_RESPUESTA("30");
        host_confi.setNII_TRANSACCIONES("021");
        host_confi.setNII_CIERRE("021");
        host_confi.setNII_ECHO_TEST("016");
        host_confi.setNII_PAGOS_VARIOS("021");
        //host_confi.setHABILITAR_RESPALDO("1");
        host_confi.setIP_TRAN1("Simulador");
        host_confi.setIP_TRAN2("Simulador");
        host_confi.setLLAVE_1("1234567890123456");
        host_confi.setLLAVE_2("");
        host_confi.setLLAVE_DOBLE("0");
        host_confi.setDUKPT("0");
        host_confi.setNO_TIEMPO_HOST("0");
        host_confi.setEMV("1");
    }

    public static void llenarIp(){
        /*tablaIp.setID_IP("1");
        tablaIp.setNOMBRE_IP("Simulador");
        tablaIp.setIP_HOST("192.168.11.41");
        tablaIp.setPUERTO("5555");
        tablaIp.setURL("");
        tablaIp.setCERTIFICADO_SERV("");
        tablaIp.setCERTIFICADO_CLI("");
        tablaIp.setAGREGAR_LARGO("1");
        tablaIp.setAGREGAR_TPDU("1");
        tablaIp.setTLS("0");
        tablaIp.setAUTENTICAR_CLIENTE("");*/
    }


    public static void InitLocal(){
        llenar_TCONF();
        llenarPrompt();
        llenarClaseRango();
        llenarHostConfi();
        llenarIp();
    }


}
