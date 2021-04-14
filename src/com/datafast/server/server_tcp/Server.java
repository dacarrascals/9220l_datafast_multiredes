package com.datafast.server.server_tcp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import com.datafast.server.activity.ServerTCP;
import com.datafast.server.callback.waitResponse;
import com.datafast.server.unpack.dataReceived;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    CountDownLatch countDown;
    DataInputStream dataInputStream;

    public static String cmd = "";
    public static byte[] dat;
    public static byte[] info;
    public static boolean correctLength;

    public Server(ServerTCP activity) {
        this.activity = activity;
        socketServerPORT = getListeningPort();
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void onDestroy() {
        super.onDestroy();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private int getListeningPort(){
        SharedPreferences preferences = activity.getSharedPreferences("config_ip", Context.MODE_PRIVATE);
        return Integer.parseInt(preferences.getString("port", "9999"));
    }

    private class SocketServerThread extends Thread {

        int len;
        byte[] text;

        @Override
        public void run() {
            try {
                // create ServerSocket using specified port
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(socketServerPORT));

                while(true) {

                    Logger.information("Server.java -> Llega una nueva petición");

                    Socket socket = serverSocket.accept();
                    socket.setReuseAddress(true);

                    // block the call until connection is created and return Socket object

                    final InputStream input = socket.getInputStream();
                    dataInputStream = new DataInputStream(input);

                    //final OutputStream outputStream = socket.getOutputStream();

                    text = readSocket(dataInputStream);

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, text);
                    socketServerReplyThread.run();

                }
            } catch (IOException  e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private byte[] readSocket(DataInputStream input) throws IOException {
            int contentBuf;
            byte[] lenRx = new byte[2];
            byte[] data = null;

            do {
                contentBuf = input.available();

                if (contentBuf <= 0)
                    continue;

                for (int i = 0; i < 2; i++)
                    lenRx[i] = (byte) input.read();
                break;

            } while (true);

            len = bcdToInt(lenRx);

            if (contentBuf > 0) {
                data = new byte[len];
                text = new byte[len];
                if (input.read(data, 0, len) < 0) {
                    data = null;
                }

            }
            return data;
        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        byte[] echoData;


        SocketServerReplyThread(Socket socket, byte[] echo) {
            hostThreadSocket = socket;
            echoData = echo;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            dataReceived dataReceived = new dataReceived();

            try {

                Logger.information("Server.java -> Inicia proceso de respuesta");

                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);

                dataReceived.identifyCommand(echoData);
                dat = dataReceived.getDataRaw();
                correctLength = dataReceived.isCorrectLength();
                cmd = dataReceived.getCmd();

                Logger.information("Server.java -> Contenido de la trama que llega: " + ISOUtil.byte2hex(dat));

                Logger.information("Server.java -> Se identifica el CMD: " + cmd);

                if(lastCmd.equals("CP") && cmd.equals("PC")){
                    Thread.sleep(1000);
                }

                countDown = new CountDownLatch(1);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.startTrans(cmd, dat, new waitResponse() {
                            @Override
                            public void waitRspHost(byte[] Info) {
                                info = Info;
                                lastCmd = cmd;
                                countDown.countDown();
                            }
                        });
                    }
                });

                countDown.await();
                printStream.write(info);

                Logger.information("Server.java -> Se responde a la caja -> " + ISOUtil.byte2hex(info));

                printStream.close();
                ppResponse = null;

            } catch (IOException | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

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
