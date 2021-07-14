package com.datafast.inicializacion.pagosvarios;

public class PagosVarios {

    private String ID_PAGOS_VARIOS;
    private String CODIGO_PAGOS_VARIOS;
    private String NOMBRE_PAGOS_VARIOS;
    private String TEXTO_PAGOS_VARIOS;
    private String GRUPO_PROMPTS;
    private String BIN;
    private String LONGITUD_MINIMA;
    private String LONGITUD_MAXIMA;
    private String TIEMPO_ESPERA;
    private String ENTRADA_MANUAL_CODIGO;

    public static String PV_GRUPOPROMPT;

    public static String[] fields = new String[]{
            "ID_PAGOS_VARIOS",
            "CODIGO_PAGOS_VARIOS",
            "NOMBRE_PAGOS_VARIOS",
            "TEXTO_PAGOS_VARIOS",
            "GRUPO_PROMPTS",
            "BIN",
            "LONGITUD_MINIMA",
            "LONGITUD_MAXIMA",
            "TIEMPO_ESPERA",
            "ENTRADA_MANUAL_CODIGO"
    };

   /* public static PagosVarios getSingletonInstance(){
        if (prompt == null){
            prompt = new Prompt();
        }else{
            Log.d("Prompt", "No se puede crear otro objeto, ya existe");
        }
        return prompt;
    }*/

    public void setPagosVarios(String column, String value) {
        switch (column) {
            case "ID_PAGOS_VARIOS":
                setID_PAGOS_VARIOS(value);
                break;
            case "CODIGO_PAGOS_VARIOS":
                setCODIGO_PAGOS_VARIOS(value);
                break;
            case "NOMBRE_PAGOS_VARIOS":
                setNOMBRE_PAGOS_VARIOS(value);
                break;
            case "TEXTO_PAGOS_VARIOS":
                setTEXTO_PAGOS_VARIOS(value);
                break;
            case "GRUPO_PROMPTS":
                setGRUPO_PROMPTS(value);
                break;
            case "BIN":
                setBIN(value);
                break;
            case "LONGITUD_MINIMA":
                setLONGITUD_MINIMA(value);
                break;
            case "LONGITUD_MAXIMA":
                setLONGITUD_MAXIMA(value);
                break;
            case "TIEMPO_ESPERA":
                setTIEMPO_ESPERA(value);
                break;
            case "ENTRADA_MANUAL_CODIGO":
                setENTRADA_MANUAL_CODIGO(value);
                break;
            default:
                break;
        }
    }

    public void clearPagosVarios() {
        for (String s : PagosVarios.fields) {
            setPagosVarios(s, "");
        }
    }

    public String getID_PAGOS_VARIOS() {
        return ID_PAGOS_VARIOS;
    }

    public void setID_PAGOS_VARIOS(String ID_PAGOS_VARIOS) {
        this.ID_PAGOS_VARIOS = ID_PAGOS_VARIOS;
    }

    public String getCODIGO_PAGOS_VARIOS() {
        return CODIGO_PAGOS_VARIOS;
    }

    public void setCODIGO_PAGOS_VARIOS(String CODIGO_PAGOS_VARIOS) {
        this.CODIGO_PAGOS_VARIOS = CODIGO_PAGOS_VARIOS;
    }

    public String getNOMBRE_PAGOS_VARIOS() {
        return NOMBRE_PAGOS_VARIOS;
    }

    public void setNOMBRE_PAGOS_VARIOS(String NOMBRE_PAGOS_VARIOS) {
        this.NOMBRE_PAGOS_VARIOS = NOMBRE_PAGOS_VARIOS;
    }

    public String getTEXTO_PAGOS_VARIOS() {
        return TEXTO_PAGOS_VARIOS;
    }

    public void setTEXTO_PAGOS_VARIOS(String TEXTO_PAGOS_VARIOS) {
        this.TEXTO_PAGOS_VARIOS = TEXTO_PAGOS_VARIOS;
    }

    public String getGRUPO_PROMPTS() {
        return GRUPO_PROMPTS;
    }

    public void setGRUPO_PROMPTS(String GRUPO_PROMPTS) {
        this.GRUPO_PROMPTS = GRUPO_PROMPTS;
    }

    public String getBIN() {
        return BIN;
    }

    public void setBIN(String BIN) {
        this.BIN = BIN;
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

    public String getTIEMPO_ESPERA() {
        return TIEMPO_ESPERA;
    }

    public void setTIEMPO_ESPERA(String TIEMPO_ESPERA) {
        this.TIEMPO_ESPERA = TIEMPO_ESPERA;
    }

    public String getENTRADA_MANUAL_CODIGO() {
        return ENTRADA_MANUAL_CODIGO;
    }

    public void setENTRADA_MANUAL_CODIGO(String ENTRADA_MANUAL_CODIGO) {
        this.ENTRADA_MANUAL_CODIGO = ENTRADA_MANUAL_CODIGO;
    }
}
