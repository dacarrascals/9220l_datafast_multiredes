package com.datafast.pinpad.cmd.CP;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.datafast.server.server_tcp.Server;
import com.newpos.libpay.utils.ISOUtil;

public class CP_Request {

    private int countValid;
    private String ip;
    private String mask;
    private String gateway;
    private String ipPrimary;
    private String portPrimary;
    private String ipSecundary;
    private String portSecundary;
    private String filler1;
    private String filler2;
    private String filler3;
    private String filler4;
    private String portListenPinpad;

    private String hash;

    public int getCountValid() {
        return countValid;
    }

    public void setCountValid(int countValid) {
        this.countValid = countValid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getIpPrimary() {
        return ipPrimary;
    }

    public void setIpPrimary(String ipPrimary) {
        this.ipPrimary = ipPrimary;
    }

    public String getPortPrimary() {
        return portPrimary;
    }

    public void setPortPrimary(String portPrimary) {
        this.portPrimary = portPrimary;
    }

    public String getIpSecundary() {
        return ipSecundary;
    }

    public void setIpSecundary(String ipSecundary) {
        this.ipSecundary = ipSecundary;
    }

    public String getPortSecundary() {
        return portSecundary;
    }

    public void setPortSecundary(String portSecundary) {
        this.portSecundary = portSecundary;
    }

    public String getFiller1() {
        return filler1;
    }

    public void setFiller1(String filler1) {
        this.filler1 = filler1;
    }

    public String getFiller2() {
        return filler2;
    }

    public void setFiller2(String filler2) {
        this.filler2 = filler2;
    }

    public String getFiller3() {
        return filler3;
    }

    public void setFiller3(String filler3) {
        this.filler3 = filler3;
    }

    public String getFiller4() {
        return filler4;
    }

    public void setFiller4(String filler4) {
        this.filler4 = filler4;
    }

    public String getPortListenPinpad() {
        return portListenPinpad;
    }

    public void setPortListenPinpad(String portListenPinpad) {
        this.portListenPinpad = portListenPinpad;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void UnPackData(byte[] aData){

        byte[] tmp = null;
        int offset = 0;

        try {

            //ip
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.ip = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //mask
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.mask = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //gateway
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.gateway = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //ipPrimary
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.ipPrimary = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //portPrimary
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            this.portPrimary = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //ipSecundary
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            this.ipSecundary = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //portSecundary
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            this.portSecundary = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //filler1
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 15){
                countValid ++;
            }
            this.filler1 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //filler2
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 6){
                countValid ++;
            }
            this.filler2 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //filler3
            tmp = new byte[15];
            System.arraycopy(aData, offset, tmp, 0, 15);
            offset += 15;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 15){
                countValid ++;
            }
            this.filler3 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //filler4
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).length() != 6){
                countValid ++;
            }
            this.filler4 = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

            //
            tmp = new byte[6];
            System.arraycopy(aData, offset, tmp, 0, 6);
            offset += 6;
            this.portListenPinpad = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (portListenPinpad != null && !portListenPinpad.equals("") && !portListenPinpad.equals("000000")){
                if (!portListenPinpad.matches(".*[A-Z].*")){
                    if (portListenPinpad.length() != 4){
                        if (Integer.parseInt(portListenPinpad) > 9999){
                            countValid ++;
                        }
                    }
                }else{
                    countValid += 1;
                }
            }
            //hash
            tmp = new byte[32];
            System.arraycopy(aData, offset, tmp, 0, 32);
            offset += 32;
            this.hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            if (hash.length() != 32){
                countValid ++;
                int len = portListenPinpad.length();
                hash = portListenPinpad.substring(len - 2) + hash;
            }

        }
        catch(Exception e) {
            e.getMessage();
        }

        return;

    }

}
