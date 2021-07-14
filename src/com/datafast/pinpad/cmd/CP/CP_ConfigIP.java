package com.datafast.pinpad.cmd.CP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class CP_ConfigIP extends AppCompatActivity {

    private static boolean wifiEthernet;

    public static boolean isWifiEthernet() {
        return wifiEthernet;
    }

    public static WifiManager setWifi(Context context, String IP, String gateway, String mask)
    {
        WifiConfiguration wifiConf = null;
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        /*EthDevinfoParcel eth0Info = Ethernet.getInstance().getEthConfig(EthDevinfoParcel.ETH0_NAME);
        EthDevinfoParcel eth1Info = Ethernet.getInstance().getEthConfig(EthDevinfoParcel.ETH1_NAME);

        if (eth0Info != null || eth1Info != null) {
            try {
                if (!saveStaticIPConfig(IP,gateway,mask)){
                    return null;
                }
                wifiEthernet = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (WifiConfiguration conf : configuredNetworks){
                if (conf.networkId == connectionInfo.getNetworkId()){
                    wifiConf = conf;
                    break;
                }
            }

            try{
                setIpAssignment("STATIC", wifiConf); //or "DHCP" for dynamic setting
                setIpAddress(InetAddress.getByName(IP), 24, wifiConf);
                setGateway(InetAddress.getByName(gateway), wifiConf);
                setDNS(InetAddress.getByName("8.8.8.8"), wifiConf);
                wifiManager.updateNetwork(wifiConf); //apply the setting
                wifiManager.saveConfiguration(); //Save it
                wifiEthernet = true;
            }catch(Exception e){
                //System.out.println("ERROR EN LA CONFIGURACION DE LA IP");
                e.printStackTrace();
                return null;
            }
        }*/
        return wifiManager;
    }

    public static void setIpAssignment(String assign , WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);

        ArrayList mLinkAddresses = (ArrayList)getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException{
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
        Object routeInfo = routeInfoConstructor.newInstance(gateway);

        ArrayList mRoutes = (ArrayList)getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;

        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); //or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns);
    }

    public static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    private static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    /*public static boolean saveStaticIPConfig(String ip, String gateway, String mask) {
        EthDevinfoParcel ethDevinfo = new EthDevinfoParcel();
        ethDevinfo.setConnectMode(EthDevinfoParcel.ETHERNET_CONN_MODE_MANUAL);
        ethDevinfo.setIfName("eth0");
        ethDevinfo.setIpAddress(ip);
        ethDevinfo.setNetMask(mask);
        ethDevinfo.setDnsAddr(gateway);
        ethDevinfo.setRouteAddr(gateway);
        boolean saved = Ethernet.getInstance().saveIPConfig(ethDevinfo);
        if (saved) {
            System.out.println("CONFIG DONE");
        } else {
            System.out.println("CONFIG FAILED");
        }

        return saved;
    }*/

    /*public static void saveDynamicIPConfig() {
        EthDevinfoParcel ethDevinfo = new EthDevinfoParcel();
        ethDevinfo.setConnectMode(EthDevinfoParcel.ETHERNET_CONN_MODE_DHCP);
        ethDevinfo.setIfName("eth0");
        ethDevinfo.setIpAddress("");
        ethDevinfo.setNetMask("");
        ethDevinfo.setDnsAddr("");
        ethDevinfo.setRouteAddr("");
        boolean saved = Ethernet.getInstance().saveIPConfig(ethDevinfo);
        if (saved) {
            System.out.println("CONFIG DONE");
        } else {
            System.out.println("CONFIG FAILED");
        }
    }*/
}
