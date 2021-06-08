package com.datafast.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.widget.Toast;

import com.android.newpos.pay.StartAppDATAFAST;
import com.datafast.inicializacion.configuracioncomercio.ChequeoIPs;
import com.datafast.pinpad.cmd.CP.CP_Request;
import com.datafast.pinpad.cmd.CP.CP_Response;
import com.datafast.pinpad.cmd.CP.IpEthernetConf;
import com.datafast.pinpad.cmd.CP.IpWifiConf;
import com.datafast.server.callback.waitResponse;
import com.datafast.server.server_tcp.Server;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.net.eth.EthernetManager;
import com.pos.device.net.wifi.PosWifiManager;
import com.pos.device.net.wifi.WifiSsidInfo;

import org.jpos.iso.IF_CHAR;

import static android.net.ConnectivityManager.TYPE_WIFI;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CP;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_EN_TRAMA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_TRAMA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.OK;
import static com.newpos.libpay.presenter.TransUIImpl.getStatusInfo;

public class Wifi {

    Context ctx;
    CP_Response cp_response;
    CP_Request cp_request;

    public Wifi(Context ctx) {
        this.ctx = ctx;
        cp_response = new CP_Response();
        cp_request = new CP_Request();
    }

    SharedPreferences preferences;
    SharedPreferences.Editor edit;

    public CP_Response getCp_response() {
        return cp_response;
    }

    public boolean comunicacion(byte[] aDat, waitResponse listener) {
        String[] data = new String[2];
        boolean ret = false;

        if (!Server.correctLength){
            cp_request.UnPackHash(aDat);
            processInvalid();
            listener.waitRspHost(getCp_response().packData());
            cp_request.setCountValid(0);
            return false;
        }

        cp_request.UnPackData(aDat);

        if (cp_request.getCountValid() > 0){
            processInvalid();
            listener.waitRspHost(getCp_response().packData());
            cp_request.setCountValid(0);
            return false;
        }

        preferences = ctx.getSharedPreferences("config_ip", Context.MODE_PRIVATE);
        edit = preferences.edit();

        if (!cp_request.getPortListenPinpad().equals("000000")){
            edit.putString("port", cp_request.getPortListenPinpad());
        }

        if (!PAYUtils.isNullWithTrim(cp_request.getIpPrimary()) && !PAYUtils.isNullWithTrim(cp_request.getPortPrimary())) {
            data[0] = cp_request.getIpPrimary();
            data[1] = cp_request.getPortPrimary();
            ret = ChequeoIPs.updateSelectIps(ChequeoIPs.fieldsIP, data, 0, this.ctx);
            edit.putString("ip_primary", cp_request.getIpPrimary());
            edit.putString("port_primary", cp_request.getPortPrimary());
        }

        if (!PAYUtils.isNullWithTrim(cp_request.getIpSecundary()) && !PAYUtils.isNullWithTrim(cp_request.getPortSecundary())) {
            data[0] = cp_request.getIpSecundary();
            data[1] = cp_request.getPortSecundary();
            ret = ChequeoIPs.updateSelectIps(ChequeoIPs.fieldsIP, data, 1, this.ctx);
            edit.putString("ip_secundary", cp_request.getIpSecundary());
            edit.putString("port_secundary", cp_request.getPortSecundary());
        }

        edit.apply();

        StartAppDATAFAST.listIPs = ChequeoIPs.selectIP(ctx);

        if (ret) {
            processOk();
        } else {
            processFail();
            ret = false;
        }

        listener.waitRspHost(getCp_response().packData());

        //WifiManager wifiManager = CP_ConfigIP.setWifi(this.ctx, cp_request.getIp(), cp_request.getGateway(), cp_request.getMask());

        if (cp_request.getIp().equals("0.0.0.0")){
            setConnection(false);
        }else {
            setConnection(true);
        }

        /*if (isWifiConnected()) {
            try {
                IpWifiConf.setStaticIpConfiguration(this.ctx, cp_request.getIp(), cp_request.getMask(), cp_request.getGateway());
            } catch (Exception e) {
                Toast.makeText(ctx, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        if (EthernetManager.getInstance().isEtherentEnabled()) {
            try {
                IpEthernetConf.setConnectionStaticIP(cp_request.getIp(), cp_request.getMask(), cp_request.getGateway());
            } catch (Exception e) {
                Toast.makeText(ctx, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }*/

        /*if (CP_ConfigIP.isWifiEthernet()){
            wifiManager.disconnect();
            wifiManager.reconnect();
        }*/

        return ret;

    }

    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null) && (cm.getActiveNetworkInfo() != null) && (cm.getActiveNetworkInfo().getType() == TYPE_WIFI);
    }

    private void processOk() {
        cp_response.setRspCodeMsg(OK);
        cp_response.setRspMessage(ISOUtil.padright(getStatusInfo(String.valueOf(58)) + "", 20, ' '));
        cp_response.setTypeMsg(CP);
        cp_response.setHash(cp_request.getHash());
    }

    private void processInvalid() {
        cp_response.setRspCodeMsg(ERROR_PROCESO);
        cp_response.setRspMessage(ISOUtil.padright(getStatusInfo(String.valueOf(57)) + "", 20, ' '));
        cp_response.setTypeMsg(CP);
        cp_response.setHash(cp_request.getHash());
    }

    private void processFail() {
        cp_response.setRspCodeMsg(ERROR_PROCESO);
        cp_response.setRspMessage(ISOUtil.padright(getStatusInfo(String.valueOf(57)) + "", 20, ' '));
        cp_response.setTypeMsg(CP);
        cp_response.setHash(cp_request.getHash());
    }

    private void setConnection(boolean staticConnection) {
        if (isWifiConnected()) {
            try {
                if (staticConnection){
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                        IpWifiConf.setStaticIpConfiguration(this.ctx, cp_request.getIp(), cp_request.getMask(), cp_request.getGateway(), "8.8.8.8");
                    }else {
                        PosWifiManager wifiManager = PosWifiManager.getInstance();
                        WifiSsidInfo wifiSsidInfo = new WifiSsidInfo();
                        wifiSsidInfo.setConnectionType(WifiSsidInfo.NetType.STATIC_IP);
                        WifiSsidInfo.StaticIP staticIP = new WifiSsidInfo.StaticIP(cp_request.getIp(), "8.8.8.8", cp_request.getGateway(),"24");
                        wifiSsidInfo.setStaticIpConfigs(staticIP);
                        wifiManager.setCurrentSsidConfigs(wifiSsidInfo);

                    }
                }else {
                    IpWifiConf.wifiDhcp(ctx);
                }
            } catch (Exception e) {
                //Toast.makeText(ctx, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        if (EthernetManager.getInstance().isEtherentEnabled()) {
            try {
                if (staticConnection){
                    IpEthernetConf.setConnectionStaticIP(cp_request.getIp(), "8.8.8.8", cp_request.getMask(), cp_request.getGateway());
                }else {
                    IpEthernetConf.etherDhcp();
                }
            } catch (Exception e) {
                //Toast.makeText(ctx, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
