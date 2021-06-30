package com.datafast.server.server_tcp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.datafast.server.EFTGCPinpad;
import com.datafast.server.TramaProcesar;
import com.datafast.server.activity.ServerTCP;
import com.newpos.libpay.Logger;

import java.net.ServerSocket;
import java.util.Set;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;

import static com.android.newpos.pay.StartAppDATAFAST.mymap;
import static com.datafast.server.activity.ServerTCP.listenerServer;
import static com.newpos.libpay.trans.finace.FinanceTrans.ppResponse;

public class Server extends AppCompatActivity {

    ServerTCP activity;
    ServerSocket serverSocket;
    public static int socketServerPORT;
    CountDownTimer cTimer = null;
    boolean validacion = false;

    public static String cmd = "";
    public static byte[] dat;
    public static byte[] info;
    public static boolean correctLength;

    EFTGCPinpad eftgcPinpad;

    public Server(ServerTCP activity) {
        this.activity = activity;
        socketServerPORT = getListeningPort();
        Logger.information("Puerto escucha: -> " + socketServerPORT);
        eftgcPinpad = new EFTGCPinpad(this.activity);
        readCurrentMap();
        eftgcPinpad.start();
    }

    public static void setCmd(String cmd) {
        Server.cmd = cmd;
    }

    public static void setDat(byte[] dat) {
        Server.dat = dat;
    }

    public int getPort() {
        return socketServerPORT;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void onDestroy() {
        super.onDestroy();
        eftgcPinpad = null;
    }

    void startTimer() {
        cTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                validacion = true;
            }
        };
        cTimer.start();
    }

    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    private int getListeningPort(){
        SharedPreferences preferences = activity.getSharedPreferences("config_ip", Context.MODE_PRIVATE);
        return Integer.parseInt(preferences.getString("port", "9999"));
    }

    /**
     * Convert bcd to int
     *
     * @param buffer
     * @return
     */
    public int bcdToInt(byte[] buffer) {
        int lenInt = ((buffer[1] & 0x0F) + (((buffer[1] & 0xF0) >> 4) * 16) + ((buffer[0] & 0x0F) * 16 * 16) + (((buffer[0] & 0xF0) >> 4) * 16 * 16 * 16));
        return lenInt;
    }


    private void readCurrentMap(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Set<Integer> keys = mymap.keySet();
                        for (Integer key : keys) {
                            final TramaProcesar tramaProcesar = (TramaProcesar) mymap.get(key);

                            mymap.remove(key);


                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity.getBaseContext(), tramaProcesar.getCmd(), Toast.LENGTH_LONG).show();

                                }
                            });*/
                            eftgcPinpad.processCMD(tramaProcesar);

                        }
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
