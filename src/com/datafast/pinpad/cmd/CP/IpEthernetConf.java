package com.datafast.pinpad.cmd.CP;

import com.pos.device.net.eth.EthernetInfo;
import com.pos.device.net.eth.EthernetManager;


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




