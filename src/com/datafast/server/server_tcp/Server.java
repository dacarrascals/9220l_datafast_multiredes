package com.datafast.server.server_tcp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.datafast.server.EFTGCPinpad;
import com.datafast.server.activity.ServerTCP;
import com.datafast.server.callback.waitResponse;
import com.datafast.server.unpack.dataReceived;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static com.android.newpos.pay.StartAppDATAFAST.lastCmd;
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
}
