package com.datafast.inicializacion.pagoselectronicos;

public class PagosElectronicos {
    private String ID_PAGOS_ELECTRONICOS;
    private String NOMBRE_PAGO_ELECTRONICO;
    private String IMAGEN;
    private String NUM_TARJETA;
    private String TIEMPO_ESPERA;
    private String LONGITUD_MINIMA;
    private String LONGITUD_MAXIMA;

    public static String[] fields = new String[]{
            "ID_PAGOS_ELECTRONICOS",
            "NOMBRE_PAGO_ELECTRONICO",
            "IMAGEN",
            "NUM_TARJETA",
            "TIEMPO_ESPERA",
            "LONGITUD_MINIMA",
            "LONGITUD_MAXIMA"
    };

    public void setPagosElectronicos(String column, String value) {
        switch (column) {
            case "ID_PAGOS_ELECTRONICOS":
                setID_PAGOS_ELECTRONICOS(value);
                break;
            case "NOMBRE_PAGO_ELECTRONICO":
                setNOMBRE_PAGO_ELECTRONICO(value);
                break;
            case "IMAGEN":
                setIMAGEN(value);
                break;
            case "NUM_TARJETA":
                setNUM_TARJETA(value);
                break;
            case "TIEMPO_ESPERA":
                setTIEMPO_ESPERA(value);
                break;
            case "LONGITUD_MINIMA":
                setLONGITUD_MINIMA(value);
                break;
            case "LONGITUD_MAXIMA":
                setLONGITUD_MAXIMA(value);
                break;
            default:
                break;
        }

    }


    public void clearPagosElectronicos() {
        for (String s : PagosElectronicos.fields) {
            setPagosElectronicos(s, "");
        }
    }

    public String getID_PAGOS_ELECTRONICOS() {
        return ID_PAGOS_ELECTRONICOS;
    }

    public void setID_PAGOS_ELECTRONICOS(String ID_PAGOS_ELECTRONICOS) {
        this.ID_PAGOS_ELECTRONICOS = ID_PAGOS_ELECTRONICOS;
    }

    public String getNOMBRE_PAGO_ELECTRONICO() {
        return NOMBRE_PAGO_ELECTRONICO;
    }

    public void setNOMBRE_PAGO_ELECTRONICO(String NOMBRE_PAGO_ELECTRONICO) {
        this.NOMBRE_PAGO_ELECTRONICO = NOMBRE_PAGO_ELECTRONICO;
    }

    public String getIMAGEN() {
        return IMAGEN;
    }

    public void setIMAGEN(String IMAGEN) {
        this.IMAGEN = IMAGEN;
    }

    public String getNUM_TARJETA() {
        return NUM_TARJETA;
    }

    public void setNUM_TARJETA(String NUM_TARJETA) {
        this.NUM_TARJETA = NUM_TARJETA;
    }

    public String getTIEMPO_ESPERA() {
        return TIEMPO_ESPERA;
    }

    public void setTIEMPO_ESPERA(String TIEMPO_ESPERA) {
        this.TIEMPO_ESPERA = TIEMPO_ESPERA;
    }

    public String getLONGITUD_MINIMA() {
        return LONGITUD_MINIMA;
    }

    public void setLONGITUD_MINIMA(String LONGITUD_MINIMA) {
        this.LONGITUD_MINIMA = LONGITUD_MINIMA;
    }

    public String getLONGITUD_MAXIMA() {
        return LONGITUD_MAXIMA;
    }

    public void setLONGITUD_MAXIMA(String LONGITUD_MAXIMA) {
        this.LONGITUD_MAXIMA = LONGITUD_MAXIMA;
    }
}
