package com.datafast.server.server_tcp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import com.datafast.server.activity.ServerTCP;
import com.datafast.server.callback.waitResponse;
import com.datafast.server.unpack.dataReceived;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static com.android.newpos.pay.StartAppDATAFAST.lastCmd;
import static com.newpos.libpay.trans.finace.FinanceTrans.ppResponse;

public class Server extends AppCompatActivity {

    ServerTCP activity;
    ServerSocket serverSocket;
    public static int socketServerPORT;
    public static dataReceived dataReceived;

    public static String cmd = "";
    public static byte[] dat;
    public static boolean correctLength;

    public Server(ServerTCP activity) {
        this.activity = activity;
        socketServerPORT = getListeningPort();
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        dataReceived = new dataReceived();
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

                // block the call until connection is created and return Socket object

                final Socket socket = serverSocket.accept();
                final InputStream input = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(input);

                final DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                //final OutputStream outputStream = socket.getOutputStream();

                text = readSocket(dataInputStream);

                dataReceived.identifyCommand(text);
                dat = dataReceived.getDataRaw();
                correctLength = dataReceived.isCorrectLength();
                cmd = dataReceived.getCmd();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.startTrans(cmd,dat,new waitResponse() {
                            @Override
                            public void waitRspHost(byte[] Info) {

                                try {
                                    lastCmd = cmd;
                                    output.write(Info);
                                    socket.close();

                                    input.close();
                                    output.close();
                                    //outputStream.write(Info);
                                    //outputStream.flush();
                                    //output.close();

                                    ppResponse = null;

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

            } catch (IOException e) {
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
