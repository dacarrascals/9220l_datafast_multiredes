package com.datafast.pinpad.cmd.defines;

public class CmdDatafast {

    //Commands pinpad
    public static final String LT = "LT";
    public static final String CT = "CT";
    public static final String PP = "PP";
    public static final String CP = "CP";
    public static final String NN = "NN";
    public static final String PC = "PC";
    public static final String PA = "PA";


    //rsp code pp
    public static final String OK = "00";
    public static final String ERROR_TRAMA = "01";
    public static final String INICIO_DIA = "02";
    public static final String ERROR_PROCESO = "20";
    public static final String ERROR_CONN_PP = "ER";
    public static final String TO = "TO";

    //entryMode PP
    public static final String HDL = "01";
    public static final String MAG = "02";
    public static final String ICC = "03";
    public static final String FMI = "04";
    public static final String FBI = "05";
    public static final String CTL = "06";

    //msg respuesta
    public static final String AUTORIZADO = "AUTORIZADO";
    public static final String ERROR_EN_TRAMA = "TRANS CANCELADA";
    public static final String ERROR_CON_PINPAD = "ERR.CONEXION PINPAD";
    public static final String INICIO_DIA_MSG = "INICIO DE DIA";
}
