package com.datafast.pinpad.cmd.CB;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;

import com.datafast.tools.UtilNetwork;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;

import static android.net.ConnectivityManager.TYPE_WIFI;
import static com.android.newpos.pay.StartAppDATAFAST.VERSION;

public class CB_Request {

    private int countValid;

    private Context context;
    private String LPORT;
    private String ip;
    private String mask;
    private String gateway;
    private String requestCB;
    private String hash;
    private String boxCOMM;
    private String boxBAUD;
    private String sw;
    private String regiPPA;
    private String dateEVOCC;
    private String dateEVOCIP;
    private String dataDF;
    private String filler;

    public int getCountValid() {
        return countValid;
    }

    public void setCountValid(int countValid) {
        this.countValid = countValid;
    }

    public CB_Request() { }

    public CB_Request(Context context) {
        this.context = context;
    }

    public String getLPORT() {
        return LPORT;
    }

    public void setLPORT(String LPORT) {
        this.LPORT = LPORT;
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

    public String getRequestCB() {
        return requestCB;
    }

    public void setRequestCB(String requestCB) {
        this.requestCB = requestCB;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBoxCOMM() {
        return boxCOMM;
    }

    public void setBoxCOMM(String boxCOMM) {
        this.boxCOMM = boxCOMM;
    }

    public String getBoxBAUD() {
        return boxBAUD;
    }

    public void setBoxBAUD(String boxBAUD) {
        this.boxBAUD = boxBAUD;
    }

    public String getSw() {
        return sw;
    }

    public void setSw(String sw) {
        this.sw = sw;
    }

    public String getRegiPPA() {
        return regiPPA;
    }

    public void setRegiPPA(String regiPPA) {
        this.regiPPA = regiPPA;
    }

    public String getDateEVOCC() {
        return dateEVOCC;
    }

    public void setDateEVOCC(String dateEVOCC) {
        this.dateEVOCC = dateEVOCC;
    }

    public String getDateEVOCIP() {
        return dateEVOCIP;
    }

    public void setDateEVOCIP(String dateEVOCIP) {
        this.dateEVOCIP = dateEVOCIP;
    }

    public String getDataDF() {
        return dataDF;
    }

    public void setDataDF(String dataDF) {
        this.dataDF = dataDF;
    }

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    public void UnPackData(byte[] aData) {

        byte[] tmp = null;
        int offset = 0;
        TMConfig config = TMConfig.getInstance();

        try {

            this.LPORT = ";";
            this.boxCOMM =";";
            this.boxBAUD = ";";
            this.sw = ";";
            this.regiPPA = ";";
            this.dateEVOCC = ";";
            this.dateEVOCIP = ";";

            initData();
            String[] dataVersion = getVersion().split("_");

            this.dataDF = config.getTermID() + ";"
                    + ";"
                    + ";"
                    + dataVersion[0] + ";"
                    + ";"
                    + dataVersion[1] + ";";

            //hash
            this.filler = "               ";

            tmp = new byte[32];
            System.arraycopy(aData, offset, tmp, 0, 32);
            offset += 32;
            this.hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();
            setCorrectHash(aData);

        } catch (Exception e) {
            e.getMessage();
            setCorrectHash(aData);
        }

        return;
    }

    private void setCorrectHash(byte[] aData){
        try{
            String correctHash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aData)).trim();
            correctHash = correctHash.substring(correctHash.length() - 32);
            if (hash == null || !correctHash.equals(hash)){
                hash = correctHash;
                countValid ++;
            }
        }catch (Exception e){
            e.printStackTrace();
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aData)).length() != 32){
                hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aData)).trim();
                countValid ++;
            }
        }
    }

    private String getVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.versionName;
    }

    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null) && (cm.getActiveNetworkInfo() != null) && (cm.getActiveNetworkInfo().getType() == TYPE_WIFI);
    }

    public void initData() {
        String[] datos;

        if (isWifiConnected()) {
            datos = UtilNetwork.getWifi(context, false);
            ip = UtilNetwork.getIPAddress(true) + ";";
            mask = datos[0] + ";";
            gateway = datos[3] + ";";
        } else {
            datos = UtilNetwork.getWifi(context, true);
            ip = datos[0] + ";";
            mask = datos[1] + ";";
            gateway = datos[3] + ";";
        }
    }


}
