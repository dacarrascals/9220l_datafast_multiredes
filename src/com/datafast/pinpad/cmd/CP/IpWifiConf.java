package com.datafast.pinpad.cmd.CP;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import com.datafast.tools.Wifi;
import com.pos.device.net.eth.EthernetManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class IpWifiConf {

    public static String ip = null;
    public static String mask = null;
    public static String gateway = null;
    public static String dns = null;

    public static void setStaticIpConfiguration(Context context, String ip, String mask, String gateway, String dns) throws Exception {
        IpWifiConf.ip = ip;
        IpWifiConf.mask = mask;
        IpWifiConf.gateway = gateway;
        IpWifiConf.dns = dns;

       setStaticIpConfiguration(context,  IpWifiConf.ip, IpWifiConf.mask, IpWifiConf.gateway, new InetAddress[] { InetAddress.getByName(IpWifiConf.dns), InetAddress.getByName("8.8.4.4")});
    }

    public static void setStaticIpConfiguration(Context context, String ip, String mask, String puertaEnlace, InetAddress[] dns) throws Exception {


        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = null;
        WifiInfo connectionInfo = manager.getConnectionInfo();

        List<WifiConfiguration> configuredNetworks = manager.getConfiguredNetworks();

        for (WifiConfiguration conf : configuredNetworks){

            if (conf.networkId == connectionInfo.getNetworkId()){
                config = conf;
                break;
            }
        }

        InetAddress ipAddress = InetAddress.getByName(ip);  // ip
        int prefixLength = countBits(mask); //convierte el prefijo de acuerdo a la mascara
        InetAddress gateway = InetAddress.getByName(puertaEnlace);  // gateway

        // First set up IpAssignment to STATIC.
        Object ipAssignment = getEnumValue("android.net.IpConfiguration$IpAssignment", "STATIC");
        callMethod(config, "setIpAssignment", new String[] { "android.net.IpConfiguration$IpAssignment" }, new Object[] { ipAssignment });

        // Then set properties in StaticIpConfiguration.
        Object staticIpConfig = newInstance("android.net.StaticIpConfiguration");
        Object linkAddress = newInstance("android.net.LinkAddress", new Class<?>[] { InetAddress.class, int.class }, new Object[] { ipAddress, prefixLength });

        setField(staticIpConfig, "ipAddress", linkAddress);
        setField(staticIpConfig, "gateway", gateway);
        getField(staticIpConfig, "dnsServers", ArrayList.class).clear();
        for (int i = 0; i < dns.length; i++)
            getField(staticIpConfig, "dnsServers", ArrayList.class).add(dns[i]);

        callMethod(config, "setStaticIpConfiguration", new String[] { "android.net.StaticIpConfiguration" }, new Object[] { staticIpConfig });
        manager.updateNetwork(config);

        manager.saveConfiguration();
        manager.disconnect();
        manager.reconnect();

    }

    public static String getConnectionTypeWifi(Context context){
        String ret = null;
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = manager.getConnectionInfo();

        List<WifiConfiguration> configuredNetworks = manager.getConfiguredNetworks();
        for (WifiConfiguration conf : configuredNetworks){
            if (conf.networkId == connectionInfo.getNetworkId()){
                if (conf.toString().toLowerCase().contains("DHCP".toLowerCase())){
                    ret = "DHCP";
                }else if(conf.toString().toLowerCase().contains("STATIC".toLowerCase())){
                    ret = "STATIC";
                }
                break;
            }
        }
        return ret;
    }

    public static void wifiDhcp(Context context) throws Exception {
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        WifiConfiguration config = null;
        WifiInfo connectionInfo = manager.getConnectionInfo();

        List<WifiConfiguration> configuredNetworks = manager.getConfiguredNetworks();

        for (WifiConfiguration conf : configuredNetworks){

            if (conf.networkId == connectionInfo.getNetworkId()){
                config = conf;
                break;
            }
        }

        Object ipAssignment = getEnumValue("android.net.IpConfiguration$IpAssignment", "DHCP");
        callMethod(config, "setIpAssignment", new String[] { "android.net.IpConfiguration$IpAssignment" }, new Object[] { ipAssignment });


        manager.updateNetwork(config);
        manager.saveConfiguration();
        manager.disconnect();
        manager.reconnect();

    }

    private static int countBits(String mask) throws Exception{

        String[] octetos = null;
        int cantBits = 0;

        if(mask == null || mask.isEmpty()){
            throw new Exception("La mascara esta vacia");
        }
        if(!mask.contains(".")){
            throw new Exception("Formato de mascara incorrecto: No esta dividio por puntos.");
        }

        octetos =  mask.split("\\.");

        if(octetos.length != 4){
            throw new Exception("Formato de mascara incorrecto: No hay cuatro octetos");
        }

        for(String octeto : octetos){
            int octNum = Integer.parseInt(octeto);
            octeto = Integer.toBinaryString(octNum);
            cantBits+=octeto.replaceAll("0","").length();
        }

        return cantBits;

    }

    private static Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
        return newInstance(className, new Class<?>[0], new Object[0]);
    }

    private static Object newInstance(String className, Class<?>[] parameterClasses, Object[] parameterValues) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException
    {
        Class<?> clz = Class.forName(className);
        Constructor<?> constructor = clz.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object getEnumValue(String enumClassName, String enumValue) throws ClassNotFoundException
    {
        Class<Enum> enumClz = (Class<Enum>)Class.forName(enumClassName);
        return Enum.valueOf(enumClz, enumValue);
    }

    private static void setField(Object object, String fieldName, Object value) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException
    {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }

    private static <T> T getField(Object object, String fieldName, Class<T> type) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException
    {
        Field field = object.getClass().getDeclaredField(fieldName);
        return type.cast(field.get(object));
    }

    private static void callMethod(Object object, String methodName, String[] parameterTypes, Object[] parameterValues) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
    {
        Class<?>[] parameterClasses = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parameterClasses[i] = Class.forName(parameterTypes[i]);

        Method method = object.getClass().getDeclaredMethod(methodName, parameterClasses);
        method.invoke(object, parameterValues);
    }

    public static void covertToDynamic(Context context)throws Exception{
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = null;
        WifiInfo connectionInfo = manager.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = manager.getConfiguredNetworks();

        for (WifiConfiguration conf : configuredNetworks){
            if (conf.networkId == connectionInfo.getNetworkId()){
                config = conf;
                break;
            }
        }

       // First set up IpAssignment to STATIC.
        Object ipAssignment = getEnumValue("android.net.IpConfiguration$IpAssignment", "DHCP");
        callMethod(config, "setIpAssignment", new String[] { "android.net.IpConfiguration$IpAssignment" }, new Object[] { ipAssignment });

        manager.updateNetwork(config);
        manager.saveConfiguration();

        manager.disconnect();
        manager.reconnect();
    }


}
