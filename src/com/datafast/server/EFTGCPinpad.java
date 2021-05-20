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
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.event.EventISOServer;
import org.jpos.iso.server.ISOServerGC;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static com.datafast.server.server_tcp.Server.dat;
import static com.datafast.server.server_tcp.Server.cmd;
import static com.datafast.server.server_tcp.Server.setCmd;
import static com.datafast.server.server_tcp.Server.setDat;
import static com.android.newpos.pay.StartAppDATAFAST.lastCmd;
import static com.datafast.server.server_tcp.Server.correctLength;


/**
 *
 * @author francisco
 */
public class EFTGCPinpad implements EventISOServer {

    //static org.apache.log4j.Logger loggerReformateador
    //        = org.apache.log4j.Logger.getLogger(EFTGC.class.getName());
    private ServerTCP activity;
    byte[] info;
    ISOSource sourceLocal;
    ISOMsg mLocal;
    private int port;
    CountDownLatch countDown;

    public EFTGCPinpad(ServerTCP activity) {
        //Not implemented
        this.activity = activity;
    }

    private int getListeningPort(){
        SharedPreferences preferences = activity.getSharedPreferences("config_ip", Context.MODE_PRIVATE);
        return Integer.parseInt(preferences.getString("port", "9999"));
    }

    public void start() {
        try {
            //loggerReformateador.info("EFTGC start at: " + "5020");
            port = getListeningPort();;
            ISOServerGC isoServerGC = new ISOServerGC(this);
            isoServerGC.setPort(port);
            isoServerGC.setTCPViolation(true);
            isoServerGC.setMinThreadPool(10);
            isoServerGC.setMaxThreadPool(50);
            isoServerGC.setInactivityTimeout((45*1000) + 5000); //Le suma 5 seg mas por que el timeout de envio al autorizador es TMConfig.getInstance().getTimeoutData()
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
            byte[] response = null;
            byte[] requestByte = new byte[((byte[]) m.getValue(0)).length -2];
            StringBuilder sb = new StringBuilder();
            String request = new String((byte[]) m.getValue(0));  //Asi se lee como cadena
            System.arraycopy(m.getValue(0),2,requestByte,0,((byte[]) m.getValue(0)).length -2);
            sb.append("\nIncoming: \n");
            sb.append(org.jpos.iso.ISOUtil.hexdump((byte[]) m.getValue(0)));
            sb.append("\n");
            sb.append(org.jpos.iso.ISOUtil.hexdump(requestByte));
            sb.append("\n");
            sb.append(request);
            sb.append("\n");


            System.out.println("Trama---: "+sb.toString());

            Logger.information("Server.java -> Contenido de la trama que llega: " + sb.toString());

            dataReceived.identifyCommand(requestByte);
            //setCmd(dataReceived.getCmd());
            cmd = dataReceived.getCmd();
            Logger.information("Server.java -> Se identifica el CMD: " + cmd);
            //setDat(dataReceived.getDataRaw());
            dat = dataReceived.getDataRaw();
            correctLength = dataReceived.isCorrectLength();

            if(lastCmd.equals("CP") && cmd.equals("PC")){
                Thread.sleep(500);
            }

            countDown = new CountDownLatch(1);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.startTrans(cmd, dat, new waitResponse() {
                        @Override
                        public void waitRspHost(byte[] infoLocal) {
                            info = infoLocal;
                            try {
                                lastCmd = cmd;
                                String result = new String(info);
                                byte[] resp = quitarTildes(result).getBytes();

                                mLocal.set(0, resp);
                                countDown.countDown();

                            } catch (ISOException e) {
                                //System.out.println("Paso por el primer catch");

                                //socket=null;
                            }
                        }


                    });
                }
            });
            countDown.await();
            sourceLocal.send(mLocal);

        } catch (ISOException | InterruptedException | IOException e) {
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

    /*private static byte[] getTxn(String info) {
        try {
            if (info.equals("")) {
                return null;
            }
            byte[] txn = new byte[info.length()];
            System.arraycopy(info.getBytes(), 0, txn, 0, info.length());
            return txn;
        } catch (Exception ex) {
            return null;
        }

    }*/

}
