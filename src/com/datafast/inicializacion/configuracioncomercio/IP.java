package com.datafast.inicializacion.configuracioncomercio;

public class IP {

    private String ID_IP;
    private String NOMBRE_IP;
    private String IP_HOST;
    private String PUERTO;
    private String URL;
    private String CERTIFICADO_SERV;
    private String CERTIFICADO_CLI;
    private String AGREGAR_LARGO;
    private String AGREGAR_TPDU;
    private String TLS;
    private String AUTENTICAR_CLIENTE;

    public static String[] fields = new String[]{
            "ID_IP",
            "NOMBRE_IP",
            "IP_HOST",
            "PUERTO",
            "URL",
            "CERTIFICADO_SERV",
            "CERTIFICADO_CLI",
            "AGREGAR_LARGO",
            "AGREGAR_TPDU",
            "TLS",
            "AUTENTICAR_CLIENTE"
    };

    /*public static IP getSingletonInstance(){
        if (tablaIp == null){
            tablaIp = new IP();
        }else{
            Log.d("IP", "No se puede crear otro objeto, ya existe");
        }
        return tablaIp;
    }*/

    public void setIP(String column, String value) {
        switch (column) {
            case "ID_IP":
                setID_IP(value);
                break;
            case "NOMBRE_IP":
                setNOMBRE_IP(value);
                break;
            case "IP_HOST":
                setIP_HOST(value);
                break;
            case "PUERTO":
                setPUERTO(value);
                break;
            case "URL":
                setURL(value);
                break;
            case "CERTIFICADO_SERV":
                setCERTIFICADO_SERV(value);
                break;
            case "CERTIFICADO_CLI":
                setCERTIFICADO_CLI(value);
                break;
            case "AGREGAR_LARGO":
                setAGREGAR_LARGO(value);
                break;
            case "AGREGAR_TPDU":
                setAGREGAR_TPDU(value);
                break;
            case "TLS":
                setTLS(value);
                break;
            case "AUTENTICAR_CLIENTE":
                setAUTENTICAR_CLIENTE(value);
                break;
            default:
                break;
        }
    }

    public void clearIP() {
        for (String s : IP.fields) {
            setIP(s, "");
        }
    }

    public String getID_IP() {
        return ID_IP;
    }

    public void setID_IP(String ID_IP) {
        this.ID_IP = ID_IP;
    }

    public String getNOMBRE_IP() {
        return NOMBRE_IP;
    }

    public void setNOMBRE_IP(String NOMBRE_IP) {
        this.NOMBRE_IP = NOMBRE_IP;
    }

    public String getIP_HOST() {
        return IP_HOST;
    }

    public void setIP_HOST(String IP_HOST) {
        this.IP_HOST = IP_HOST;
    }

    public String getPUERTO() {
        return PUERTO;
    }

    public void setPUERTO(String PUERTO) {
        this.PUERTO = PUERTO;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getCERTIFICADO_SERV() {
        return CERTIFICADO_SERV;
    }

    public void setCERTIFICADO_SERV(String CERTIFICADO_SERV) {
        this.CERTIFICADO_SERV = CERTIFICADO_SERV;
    }

    public String getCERTIFICADO_CLI() {
        return CERTIFICADO_CLI;
    }

    public void setCERTIFICADO_CLI(String CERTIFICADO_CLI) {
        this.CERTIFICADO_CLI = CERTIFICADO_CLI;
    }

    public String getAGREGAR_LARGO() {
        return AGREGAR_LARGO;
    }

    public void setAGREGAR_LARGO(String AGREGAR_LARGO) {
        this.AGREGAR_LARGO = AGREGAR_LARGO;
    }

    public String getAGREGAR_TPDU() {
        return AGREGAR_TPDU;
    }

    public void setAGREGAR_TPDU(String AGREGAR_TPDU) {
        this.AGREGAR_TPDU = AGREGAR_TPDU;
    }

    public String getTLS() {
        return TLS;
    }

    public void setTLS(String TLS) {
        this.TLS = TLS;
    }

    public String getAUTENTICAR_CLIENTE() {
        return AUTENTICAR_CLIENTE;
    }

    public void setAUTENTICAR_CLIENTE(String AUTENTICAR_CLIENTE) {
        this.AUTENTICAR_CLIENTE = AUTENTICAR_CLIENTE;
    }
}
