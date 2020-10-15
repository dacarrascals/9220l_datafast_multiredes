package com.datafast.pinpad.cmd.CP;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.pos.device.net.eth.EthernetInfo;
import com.pos.device.net.eth.EthernetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class IpEthernetConf {

    public static void setConnectionStaticIP(String ip, String mask, String gateway) throws Exception {

        EthernetInfo info = new EthernetInfo();

        String prefix = String.valueOf(countBits(mask));

        info.setConnectionType(EthernetInfo.NetType.STATIC_IP);
        // Instancio la IP
        EthernetInfo.StaticIP infoIP = new EthernetInfo.StaticIP(ip, gateway, gateway, prefix);
        //La seteo en INFO
        info.setStaticIpConfigs(infoIP);
        //La seteo en manager
        EthernetManager manager = EthernetManager.getInstance();
        manager.setEtherentConfigs(info);

    }


    public static String getConnectionTypeEther() {
        String ret = null;
        EthernetInfo info = EthernetManager.getInstance().getEtherentConfigs();
        if (info.getConnectionType() == EthernetInfo.NetType.DHCP) {
            ret = "DHCP";
        } else if (info.getConnectionType() == EthernetInfo.NetType.STATIC_IP) {
            ret = "STATIC";
        }

        return ret;
    }

    public static void etherDhcp() throws Exception {
        EthernetInfo info = new EthernetInfo();
        info.setConnectionType(EthernetInfo.NetType.DHCP);
        EthernetManager manager = EthernetManager.getInstance();
        manager.setEtherentConfigs(info);
    }


    private static int countBits(String mask) throws Exception {

        String[] octetos = null;
        int cantBits = 0;

        if (mask == null || mask.isEmpty()) {
            throw new Exception("La mascara esta vacia");
        }
        if (!mask.contains(".")) {
            throw new Exception("Formato de mascara incorrecto: No esta dividio por puntos.");
        }

        octetos = mask.split("\\.");

        if (octetos.length != 4) {
            throw new Exception("Formato de mascara incorrecto: No hay cuatro octetos");
        }

        for (String octeto : octetos) {
            int octNum = Integer.parseInt(octeto);
            octeto = Integer.toBinaryString(octNum);
            cantBits += octeto.replaceAll("0", "").length();
        }

        return cantBits;

    }

}




