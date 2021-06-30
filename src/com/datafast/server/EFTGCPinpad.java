/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datafast.server;

import android.content.Context;
import android.content.SharedPreferences;

import com.datafast.server.activity.ServerTCP;
import com.datafast.server.callback.waitResponse;
import com.datafast.server.unpack.dataReceived;
import com.newpos.libpay.Logger;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.event.EventISOServer;
import org.jpos.iso.server.ISOServerGC;

import java.io.IOException;

import static com.android.newpos.pay.StartAppDATAFAST.contador;
import static com.android.newpos.pay.StartAppDATAFAST.mymap;
import static com.datafast.server.server_tcp.Server.dat;
import static com.datafast.server.server_tcp.Server.cmd;
import static com.android.newpos.pay.StartAppDATAFAST.lastCmd;
import static com.datafast.server.server_tcp.Server.correctLength;


/**
 * @author francisco
 */
public class EFTGCPinpad implements EventISOServer {

    private ServerTCP activity;
    byte[] info;
    ISOSource sourceLocal;
    ISOMsg mLocal;
    private int port;

    public EFTGCPinpad(ServerTCP activity) {
        //Not implemented
        this.activity = activity;
    }

    private int getListeningPort() {
        SharedPreferences preferences = activity.getSharedPreferences("config_ip", Context.MODE_PRIVATE);
        return Integer.parseInt(preferences.getString("port", "9999"));
    }

    public void start() {
        try {

            port = getListeningPort();
            ISOServerGC isoServerGC = new ISOServerGC(this);
            isoServerGC.setPort(port);
            isoServerGC.setTCPViolation(true);
            isoServerGC.setMinThreadPool(1);
            isoServerGC.setMaxThreadPool(2);
            isoServerGC.setInactivityTimeout((50000) + 5000); //Le suma 5 seg mas por que el timeout de envio al autorizador es TMConfig.getInstance().getTimeoutData()
            //isoServerGC.getInactivityTimeout();
            isoServerGC.setEnableLogXml(false);
            isoServerGC.start();
        } catch (Exception ex) {
        }

    }

    @Override
    public void receive(ISOSource source, ISOMsg m) {
        try {

            dataReceived dataReceived = new dataReceived();

            sourceLocal = source;
            mLocal = m;
            byte[] requestByte = new byte[((byte[]) m.getValue(0)).length - 2];
            System.arraycopy(m.getValue(0), 2, requestByte, 0, ((byte[]) m.getValue(0)).length - 2);

            dataReceived.identifyCommand(requestByte);
            cmd = dataReceived.getCmd();
            dat = dataReceived.getDataRaw();
            correctLength = dataReceived.isCorrectLength();

            TramaProcesar tramaProcesar = new TramaProcesar();
            tramaProcesar.setSource(sourceLocal);
            tramaProcesar.setCmd(cmd);
            tramaProcesar.setDat(dat);
            tramaProcesar.setIpClient(String.valueOf(((BaseChannel) m.getSource()).getSocket().getInetAddress().getHostAddress()));
            tramaProcesar.setPortClient(String.valueOf(((BaseChannel) m.getSource()).getSocket().getPort()));
            Logger.information(tramaProcesar.ToString());

            if (lastCmd.equals("CP") && cmd.equals("PC")) {
                Thread.sleep(500);
            }

            mymap.put(contador.getCount(), tramaProcesar);

        } catch (ISOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String quitarTildes(String texto) {
        String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";
        String output = texto;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.

            output = output.replace(original.charAt(i), ascii.charAt(i));

        }//for i
        return output;
    }

    public void processCMD(final TramaProcesar tramaProcesar) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.startTrans(tramaProcesar.getCmd(), tramaProcesar.getDat(), new waitResponse() {
                    @Override
                    public void waitRspHost(byte[] infoLocal) {
                        info = infoLocal;
                        try {
                            lastCmd = cmd;
                            String result = new String(info);
                            byte[] resp = quitarTildes(result).getBytes();

                            mLocal.set(0, resp);
                            Logger.information("EFTGCPinpad.java -> " + result);
                            listenNotify();

                        } catch (ISOException e) {
                            Logger.information("EFTGCPinpad.java -> Excepcion" + e.getMessage());
                        }
                    }
                });
            }
        });

        funWait();
        Logger.information("EFTGCPinpad.java -> Envia a respuesta a Caja");
        try {
            tramaProcesar.getSource().send(mLocal);
            System.gc();
        } catch (IOException | ISOException e) {
            e.printStackTrace();
        }
    }

    /**
     * object lock
     */
    private Object o = new byte[0];

    /**
     * Notify
     */
    private void listenNotify() {
        synchronized (o) {
            o.notify();
        }
    }

    /**
     * block
     */
    private void funWait() {
        synchronized (o) {
            try {
                o.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
