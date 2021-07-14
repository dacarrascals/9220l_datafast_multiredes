package com.datafast.inicializacion.prompts;

import java.io.Serializable;


public class Prompt implements Serializable {

    private String ID_PROMPTS ;
    private String CODIGO_PROMPTS ;
    private String NOMBRE_PROMPTS ;
    private String TIPO_DATO ;
    private String LONGITUD_MINIMA ;
    private String LONGITUD_MAXIMA ;
    private String IMPRIMIR_PROMPT ;
    private String SUMAR_TOTALES ;
    private String VALOR_NEGATIVO ;
    private String VENTA ;
    private String VENTA_GASOLINERA ;
    private String DIFERIDO ;
    private String PAGOS_VARIOS ;

    public static final String NUMERICO = "0";
    public static final String ALFA_NUMERICO = "1";
    public static final String FECHA = "2";
    public static final String CLAVE = "3";
    public static final String MONTO = "4";

    public static String[] fields = new String[]{
            "ID_PROMPTS",
            "CODIGO_PROMPTS",
            "NOMBRE_PROMPTS",
            "TIPO_DATO",
            "LONGITUD_MINIMA",
            "LONGITUD_MAXIMA",
            "IMPRIMIR_PROMPT",
            "SUMAR_TOTALES",
            "VALOR_NEGATIVO",
            "VENTA",
            "VENTA_GASOLINERA",
            "DIFERIDO",
            "PAGOS_VARIOS"
    };

    /*public static Prompt getSingletonInstance(){
        if (prompt == null){
            prompt = new Prompt();
        }else{
            Log.d("Prompt", "No se puede crear otro objeto, ya existe");
        }
        return prompt;
    }*/

    public void setPrompt(String column, String value) {
        switch (column) {
            case "ID_PROMPTS":
                setCODIGO_PROMPTS(value);
                break;
            case "CODIGO_PROMPTS":
                setCODIGO_PROMPTS(value);
                break;
            case "NOMBRE_PROMPTS":
                setNOMBRE_PROMPTS(value);
                break;
            case "TIPO_DATO":
                setTIPO_DATO(value);
                break;
            case "LONGITUD_MINIMA":
                setLONGITUD_MINIMA(value);
                break;
            case "LONGITUD_MAXIMA":
                setLONGITUD_MAXIMA(value);
                break;
            case "IMPRIMIR_PROMPT":
                setIMPRIMIR_PROMPT(value);
                break;
            case "SUMAR_TOTALES":
                setSUMAR_TOTALES(value);
                break;
            case "VALOR_NEGATIVO":
                setVALOR_NEGATIVO(value);
                break;
            case "VENTA":
                setVENTA(value);
                break;
            case "VENTA_GASOLINERA":
                setVENTA_GASOLINERA(value);
                break;
            case "DIFERIDO":
                setDIFERIDO(value);
                break;
            case "PAGOS_VARIOS":
                setPAGOS_VARIOS(value);
                break;
            default:
                break;
        }

    }

    public void clearPrompt() {
        for (String s : Prompt.fields) {
            setPrompt(s, "");
        }
    }

    public String getID_PROMPTS() {
        return ID_PROMPTS;
    }

    public void setID_PROMPTS(String ID_PROMPTS) {
        this.ID_PROMPTS = ID_PROMPTS;
    }

    public String getCODIGO_PROMPTS() {
        return CODIGO_PROMPTS;
    }

    public void setCODIGO_PROMPTS(String CODIGO_PROMPTS) {
        this.CODIGO_PROMPTS = CODIGO_PROMPTS;
    }

    public String getNOMBRE_PROMPTS() {
        return NOMBRE_PROMPTS;
    }

    public void setNOMBRE_PROMPTS(String NOMBRE_PROMPTS) {
        this.NOMBRE_PROMPTS = NOMBRE_PROMPTS;
    }

    public String getTIPO_DATO() {
        return TIPO_DATO;
    }

    public void setTIPO_DATO(String TIPO_DATO) {
        this.TIPO_DATO = TIPO_DATO;
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

    public String getIMPRIMIR_PROMPT() {
        return IMPRIMIR_PROMPT;
    }

    public void setIMPRIMIR_PROMPT(String IMPRIMIR_PROMPT) {
        this.IMPRIMIR_PROMPT = IMPRIMIR_PROMPT;
    }

    public String getSUMAR_TOTALES() {
        return SUMAR_TOTALES;
    }

    public void setSUMAR_TOTALES(String SUMAR_TOTALES) {
        this.SUMAR_TOTALES = SUMAR_TOTALES;
    }

    public String getVALOR_NEGATIVO() {
        return VALOR_NEGATIVO;
    }

    public void setVALOR_NEGATIVO(String VALOR_NEGATIVO) {
        this.VALOR_NEGATIVO = VALOR_NEGATIVO;
    }

    public String getVENTA() {
        return VENTA;
    }

    public void setVENTA(String VENTA) {
        this.VENTA = VENTA;
    }

    public String getVENTA_GASOLINERA() {
        return VENTA_GASOLINERA;
    }

    public void setVENTA_GASOLINERA(String VENTA_GASOLINERA) {
        this.VENTA_GASOLINERA = VENTA_GASOLINERA;
    }

    public String getDIFERIDO() {
        return DIFERIDO;
    }

    public void setDIFERIDO(String DIFERIDO) {
        this.DIFERIDO = DIFERIDO;
    }

    public String getPAGOS_VARIOS() {
        return PAGOS_VARIOS;
    }

    public void setPAGOS_VARIOS(String PAGOS_VARIOS) {
        this.PAGOS_VARIOS = PAGOS_VARIOS;
    }
}
