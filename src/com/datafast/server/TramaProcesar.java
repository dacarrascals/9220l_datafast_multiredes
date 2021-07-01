package com.datafast.server;

import com.newpos.libpay.utils.ISOUtil;

import org.jpos.iso.ISOSource;

public class TramaProcesar {

    private ISOSource source;
    private String cmd;
    private byte[] dat;
    private String ipClient;
    private String portClient;
    private byte[] datRsp;
    private String ipRsp;
    private String portRsp;

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

    public byte[] getDatRsp() {
        return datRsp;
    }

    public void setDatRsp(byte[] datRsp) {
        this.datRsp = datRsp;
    }

    public String getIpRsp() {
        return ipRsp;
    }

    public void setIpRsp(String ipRsp) {
        this.ipRsp = ipRsp;
    }

    public String getPortRsp() {
        return portRsp;
    }

    public void setPortRsp(String portRsp) {
        this.portRsp = portRsp;
    }

    public String ToString(){
        StringBuilder sb = new StringBuilder();
        try{
            sb.append("\n\nTrama a Procesar: ");
            sb.append(cmd);
            sb.append("\n");
            sb.append(ipClient);
            sb.append(":");
            sb.append(portClient);
            sb.append("\n");
            sb.append(ISOUtil.hex2AsciiStr(ISOUtil.bcd2str(dat, 0, dat.length)));

            sb.append("\n\nRespuesta trama procesada: ");
            sb.append(cmd);
            sb.append("\n");
            sb.append(ipRsp);
            sb.append(":");
            sb.append(portRsp);
            sb.append("\n");
            sb.append(ISOUtil.hex2AsciiStr(ISOUtil.bcd2str(datRsp, 0, datRsp.length)));
            sb.append("\n");

        }catch (Exception e){}
        return sb.toString();
    }
}
