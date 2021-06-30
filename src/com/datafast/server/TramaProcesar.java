package com.datafast.server;

import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOUtil;

public class TramaProcesar {

    private ISOSource source;
    private String cmd;
    private byte[] dat;
    private String ipClient;
    private String portClient;

    public ISOSource getSource() {
        return source;
    }

    public void setSource(ISOSource source) {
        this.source = source;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public byte[] getDat() {
        return dat;
    }

    public void setDat(byte[] dat) {
       this.dat = dat;
    }

    public String getIpClient() {
        return ipClient;
    }

    public void setIpClient(String ipClient) {
        this.ipClient = ipClient;
    }

    public String getPortClient() {
        return portClient;
    }

    public void setPortClient(String portClient) {
        this.portClient = portClient;
    }

    public String ToString(){
        StringBuilder sb = new StringBuilder();
        try{
            sb.append("Trama a Procesar: ");
            sb.append(cmd);
            sb.append(" ");
            sb.append(ipClient);
            sb.append(":");
            sb.append(portClient);
            sb.append(" ");
            sb.append(ISOUtil.hexString(dat));

        }catch (Exception e){}
        return sb.toString();
    }
}
