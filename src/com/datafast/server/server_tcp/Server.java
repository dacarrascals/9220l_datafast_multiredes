package com.datafast.server.server_tcp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
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
    CountDownLatch countDown;
    CountDownTimer cTimer = null;
    DataInputStream dataInputStream;
    boolean validacion = false;

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

                    Logger.information("Server.java -> Se ingrea a escuchar peticiones");

                    Socket socket = serverSocket.accept();
                    socket.setReuseAddress(true);
                    validacion = false;

                    // block the call until connection is created and return Socket object

                    final InputStream input = socket.getInputStream();
                    dataInputStream = new DataInputStream(input);

                    //final OutputStream outputStream = socket.getOutputStream();

                    text = readSocket(dataInputStream);

                    if(text != null) {
                        String total = ISOUtil.byte2hex(text);
                        String newText = total.substring(2);
                        String verificar = newText.replace("0", "");
                        if (verificar.length() > 10) {
                            Logger.information("Server.java -> LLega nueva petición");
                            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, text);
                            socketServerReplyThread.run();
                        }
                    }else{
                        socket.close();
                    }
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

            runOnUiThread(new Runnable() {
                public void run() {
                    startTimer();
                }
            });

            do {
                contentBuf = input.available();

                if (validacion){
                    break;
                }

                if (contentBuf <= 0)
                    continue;

                for (int i = 0; i < 2; i++)
                    lenRx[i] = (byte) input.read();
                break;

            } while (true);

            cancelTimer();

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
                    Thread.sleep(500);
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

            } catch (Exception e) {
                // TODO Auto-generated catch block
                try {
                    hostThreadSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
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
