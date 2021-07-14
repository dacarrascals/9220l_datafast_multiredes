package com.datafast.inicializacion.trans_init.trans;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.inicializacion.trans_init.Init;
import com.google.common.base.Strings;
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import cn.desert.newpos.payui.UIUtils;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.ENTRY_POINT;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.PROCESSING;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.REVOK;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.TERMINAL;
import static com.datafast.inicializacion.trans_init.Init.InitParcial;
import static com.datafast.inicializacion.trans_init.Init.InitTotal;
import static com.datafast.inicializacion.trans_init.Init.pd;

/**
 * Created by Technology&Solutions on 27/03/2017.
 */

/**
 * Send and wait response from host.
 */
public class SendRcvd extends AsyncTask<Void, Integer, byte[]> {

    public static final String TAG = "SendClass";

    public static final int TIMEOUT = 1;
    public static final int NO_ACCESS_INTERNET = 2;
    public static final int HOST_OFF = 3;

    private int resultTx = 0;
    private String ipHost;
    private int portHost;
    private int timeOut;
    private InputStream in;
    private OutputStream dis;
    //private DataOutputStream sendBuffer;
    Socket clientRequest = null;
    private Context context;
    private TcpCallback callback;

    public static String TCONF = "TCONF";
    public static String ACQS = "ACQS";
    public static String ISSUERS = "ISSUERS";
    public static String CARDS = "CARDS";
    public static String EMVAPPS = "emvapps";
    public static String CAPKS = "capks";
    public static String EXTRAPARAMS = "extraparams";
    public static String HOST_CONFI = "HOST_CONFI";
    public static String IPs = "IP";
    public static String PAGOS_ELEC = "PAGOS_ELEC";
    public static String PAGOS_VAR = "PAGOS_VAR";
    public static String PROMPTS = "PROMPTS";
    public static String GRUPOXPROMPT = "GrupoXPrompt";
    public static String GRUPO_PROMPT = "Grupo_prompt";
    public static String GRUPOXPAGOSVARIOS = "GrupoXPagosVarios";
    public static String GRUPOPAGOSVARIOS = "GrupoPagosVarios";
    public static String GRUPOXPAGOSELECTRONICOS = "GrupoXPagosElectronicos";
    public static String GRUPOPAGOSELECTRONICOS = "GrupoPagosElectronicos";

    private String pathDefault;
    private String nii;
    private String TID;
    private int tramaQueEnvia;
    private String FileName;
    private String Offset;
    private String gHashTotal;
    private File File;
    private byte[] txBuf;
    private byte[] rxBuf;
    private String resultOk;

    /**
     * @param ipHost
     * @param portHost
     * @param timeOut
     * @param ctx
     * @param callback
     */
    public SendRcvd(String ipHost, int portHost, int timeOut, Context ctx, final TcpCallback callback) {
        this.callback = callback;
        this.ipHost = ipHost;
        this.portHost = portHost;
        this.timeOut = timeOut;
        this.context = ctx;
    }

    public SendRcvd(String ipHost, int portHost, int timeOut, Context ctx) {
        this.ipHost = ipHost;
        this.portHost = portHost;
        this.timeOut = timeOut;
        this.context = ctx;
    }

    public void callbackResponse(final TcpCallback callback) {
        this.callback = callback;
    }

    public String getPathDefault() {
        return pathDefault;
    }

    public void setPathDefault(String pathDefault) {
        this.pathDefault = pathDefault;
    }

    public String getNii() {
        return nii;
    }

    public void setNii(String nii) {
        this.nii = nii;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public int getTramaQueEnvia() {
        return tramaQueEnvia;
    }

    public void setTramaQueEnvia(int tramaQueEnvia) {
        this.tramaQueEnvia = tramaQueEnvia;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getOffset() {
        return Offset;
    }

    public void setOffset(String offset) {
        Offset = offset;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context, R.style.Mypd);
        pd.setCancelable(false);
        pd.setIcon(R.drawable.ic_polariscloud);
        pd.setTitle(Html.fromHtml("<h4> Polaris Cloud </h4>"));
        pd.setMessage("Descargando Inicializacion por favor espere...");
        pd.setIndeterminate(false);
        pd.setMax(100);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
    }

    @Override
    protected byte[] doInBackground(Void... voids) {

        int contentBuf = 0;
        long wait_Time;
        byte[] lenIsoRx = new byte[2];
        byte[] dataISO = null;
        ByteArrayOutputStream byteOs ;
        byteOs = new ByteArrayOutputStream();

        if (!isNetworkAvailable()) {
            //Log.e(TAG, "No Internet access...");
            resultTx = NO_ACCESS_INTERNET;
            return null;
        }

        try {

            clientRequest = new Socket();
            clientRequest.setSoTimeout(timeOut);

            clientRequest.connect(new InetSocketAddress(ipHost, portHost), timeOut);

            in = clientRequest.getInputStream();
            dis = clientRequest.getOutputStream();
            if (clientRequest.isConnected()) {

                Offset = "" + calcularOffset(FileName, true);

                while (true) {

                    txBuf = packIsoInit();

                    Log.i(TAG, "Sending...");
                    Log.i(TAG, ISOUtil.hexString(txBuf));
                    dis.write(txBuf);
                    dis.flush();

                    wait_Time = System.currentTimeMillis() + this.timeOut;

                    do {
                        if (System.currentTimeMillis() >= wait_Time) {
                            resultTx = TIMEOUT;
                            break;
                        }


                        int i;
                        int len;
                        long total = 0;
                        int lenpp = 0;
                        byte[] bb;


                        try {
                            byteOs = new ByteArrayOutputStream();
                            if ((i = in.read(lenIsoRx)) != -1) {
                                len = bcdToInt(lenIsoRx);
                                bb = new byte[len + 2];

                                lenpp = len;
                                while (len > 0 && (i = in.read(bb)) != -1) {
                                    total += i;
                                    //Thread.sleep(500);
                                    publishProgress((int) ((total * 100) / lenpp));
                                    byteOs.write(bb, 0, i);
                                    len -= i;
                                }
                                break;
                            }
                        } catch (InterruptedIOException e) {
                            // 读取超时处理
                            Log.w("PAY_SDK", "Recive：读取流数据超时异常");
                            return null;
                        }
                    } while (true);
                    Log.i(TAG, "Connection closing...");

                    if (resultTx == TIMEOUT) {
                        break;
                    }


                    try {
                        rxBuf = byteOs.toByteArray();

                        if (rxBuf == null)
                            break;

                        //Recibe la rx del host
                        Log.i(TAG, "Receiving...");
                        Log.i(TAG, ISOUtil.hexString(rxBuf));

                        if (unpackDescarga(new ISO(rxBuf, ISO.lenghtNotInclude, ISO.TpduInclude)) == true) {
                            ISO rspIso = new ISO(rxBuf, ISO.lenghtNotInclude, ISO.TpduInclude);
                            TMConfig.getInstance().incTraceNo().save();

                            Log.i(TAG, rspIso.GetField(ISO.field_60_RESERVED_PRIVATE));

                            if (rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("930101")) {
                                Offset = "" + calcularOffset(FileName, false);
                                if (isCancelled()) {
                                    File temporal = new File(pathDefault + File.separator+ FileName + "T");
                                    if (temporal.exists()) {
                                        temporal.delete();
                                    }
                                    break;
                                } else {
                                    continue;
                                }
                            } else if (rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("930100")) {
                                resultOk = "OK";
                                break;
                            } else if (rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("930080")) {
                                resultOk = "OK";
                                break;
                            }
                        } else {
                            break;
                        }
                    }catch (Exception e){
                        return null;
                    }
                }
            } else {
                Log.e(TAG, "Client no connected..");
                resultTx = HOST_OFF;
            }

        } catch (IOException e) {

            e.printStackTrace();
            Log.e(TAG, "The port of server is closed...");
            resultTx = HOST_OFF;

            return null;

        } finally {

            try {
                if (resultTx != HOST_OFF) {
                    Log.i(TAG, "Closing...");
                    in.close();
                    dis.close();
                    clientRequest.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteOs.toByteArray();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        pd.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(byte[] iso) {
        super.onPostExecute(iso);
        try {

            //Barra indicadora de progreso
            if (pd != null && pd.isShowing())
                pd.dismiss();
            //Check messages of error
            validatedMessageError(resultTx);
            //call for process the buffer
            callback.RspHost(iso,resultOk);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if device have connectivity to internet
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Check if to port of server is open
     *
     * @param ip
     * @param port
     * @param timeout
     * @return
     */
    public static boolean isPortOpen(final String ip, final int port, final int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } catch (ConnectException ce) {
            ce.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
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

    /**
     * Show messages of error
     *
     * @param msgE
     */
    public void validatedMessageError(int msgE) {
        switch (msgE) {
            case TIMEOUT:
                //Toast.makeText(Tools.getCurrentContext(), "ERROR, TIEMPO DE ESPERA AGOTADO", Toast.LENGTH_LONG).show();
                UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "ERROR, TIEMPO DE ESPERA AGOTADO", Toast.LENGTH_LONG);
                break;
            case NO_ACCESS_INTERNET:
                //Toast.makeText(Tools.getCurrentContext(), "ERROR, NO HAY CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
                UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "ERROR, NO HAY CONEXIÓN A INTERNET", Toast.LENGTH_LONG);
                break;
            case HOST_OFF:
                //Toast.makeText(Tools.getCurrentContext(), "ERROR, NO HAY CONEXIÓN CON EL SERVIDOR", Toast.LENGTH_LONG).show();
                UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "ERROR, INICIALIZACION FALLIDA", Toast.LENGTH_LONG);
                break;
        }
    }

    /**
     * In this interface the definition of the onPostExecute
     * method is performed, which receives the
     * response of the request from the WS method.
     */
    public interface TcpCallback {
        void RspHost(byte[] rxBuf, String resultOk);
    }




    private long calcularOffset(String fileName, boolean deleteFile)
    {
        long len=0;
        File dir= new File(pathDefault);
        File= new File(pathDefault + File.separator+ fileName + "T");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (File.exists()) {
            if (deleteFile)
                File.delete();

            len = File.length();

        }
        else {
            try {
                if (!File.createNewFile()) {
                    Log.d("tag", "create file fail");
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return len;
    }

    private String getHash(String fileNameTable,boolean forcedInit)
    {
        String ret="NA";
        String fileName = fileNameTable;
        FileInputStream fileIn= null;
        if(forcedInit==false) {

            File fileToRead = new File( pathDefault + File.separator + fileName);
            try {
                if (fileToRead.exists()) {
                    fileIn = new FileInputStream(fileToRead);
                    InputStreamReader InputRead = new InputStreamReader(fileIn);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    byte[] inputBuffer = new byte[1024];
                    int charRead;

                    while ((charRead = fileIn.read(inputBuffer)) > 0) {
                        bos.write(inputBuffer, 0, charRead);
                    }
                    InputRead.close();
                    bos.close();

                    ret = Tools.hashSha1(bos.toByteArray());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private byte[] armarTramaDescarga(String aFileName, String aOffset, String hash)
    {
        String outField60 = null;
        String nombreArchivo = null ,offset = null ,solicitudBytes = "9000";
        byte tmp[] = null;

        nombreArchivo = aFileName;
        offset =  aOffset;

        ISO iso = new ISO(ISO.lenghtInclude, ISO.TpduInclude);
        iso.setTPDUId("60");
        iso.setTPDUDestination(nii = ISOUtil.padleft(nii + "", 4, '0'));
        iso.setTPDUSource("0000");

        iso.setMsgType("0800");
        if (offset.equals("0"))
            iso.setField(ISO.field_03_PROCESSING_CODE, "930100");
        else
            iso.setField(ISO.field_03_PROCESSING_CODE, "930101");
        iso.setField(ISO.field_11_SYSTEMS_TRACE_AUDIT_NUMBER, Strings.padStart(String.valueOf(TMConfig.getInstance().getTraceNo()),6,'0'));
        iso.setField(ISO.field_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION, TID);

        outField60 =nombreArchivo+","+offset+","+solicitudBytes;
        iso.setField(ISO.field_60_RESERVED_PRIVATE,outField60);

        if (offset.equals("0"))
            iso.setField(ISO.field_61_RESERVED_PRIVATE,"NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|");

        Log.i(TAG, "Request DE60: "+outField60);
        tmp = iso.getTxnOutput();

        return tmp;
    }

    private byte[] armarTramaDescargaParcial(String aFileName,String aOffset,boolean forcedInit)
    {
        String outField60 = null;
        String outField61 = null;
        String separator = "|";
        String tidAUX=aFileName.replace(".zip","");

        String nombreArchivo = null ,offset = null ,solicitudBytes = "9000";
        byte tmp[] = null;

        nombreArchivo = aFileName;
        offset =  aOffset;

        ISO iso = new ISO(ISO.lenghtInclude, ISO.TpduInclude);
        iso.setTPDUId("60");
        iso.setTPDUDestination(nii = ISOUtil.padleft(nii + "", 4, '0'));
        iso.setTPDUSource("0000");

        iso.setMsgType("0800");
        iso.setField(ISO.field_03_PROCESSING_CODE, "930100");
        iso.setField(ISO.field_11_SYSTEMS_TRACE_AUDIT_NUMBER, Strings.padStart(String.valueOf(TMConfig.getInstance().getTraceNo()),6,'0'));
        iso.setField(ISO.field_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION, TID);

        outField60 =nombreArchivo+","+offset+","+solicitudBytes;
        iso.setField(ISO.field_60_RESERVED_PRIVATE,outField60);
        outField61="";

        outField61 +=   getHash(tidAUX+"_"+TCONF+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+ACQS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+ISSUERS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+CARDS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+EMVAPPS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+CAPKS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+EXTRAPARAMS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+HOST_CONFI+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+IPs+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+PAGOS_ELEC+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+PAGOS_VAR+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+PROMPTS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+GRUPOXPROMPT+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+GRUPO_PROMPT+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+GRUPOXPAGOSVARIOS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+GRUPOPAGOSVARIOS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+GRUPOXPAGOSELECTRONICOS+".txtT",forcedInit) + separator +
                getHash(tidAUX+"_"+GRUPOPAGOSELECTRONICOS+".txtT",forcedInit) + separator +

                //CTL Files
                getHash(tidAUX+"_"+ENTRY_POINT+".bin",forcedInit) + separator +
                getHash(tidAUX+"_"+PROCESSING+".bin",forcedInit) + separator +
                getHash(tidAUX+"_"+REVOK+".bin",forcedInit) + separator +
                getHash(tidAUX+"_"+TERMINAL+".bin",forcedInit) + separator;

        iso.setField(ISO.field_61_RESERVED_PRIVATE,outField61);

        tmp = iso.getTxnOutput();

        return tmp;
    }

    private byte[] packIsoInit(){
        byte[] data = new byte[0];

        switch (tramaQueEnvia){
            case InitTotal:
                data = armarTramaDescarga(FileName, Offset, null);
                break;
            case InitParcial:
                data = armarTramaDescargaParcial(FileName, Offset, false);
                break;
        }

        return data;
    }

    private boolean unpackDescarga(ISO rspTx)
    {

        String rspCode = rspTx.GetField(ISO.field_39_RESPONSE_CODE);
        String procCode = rspTx.GetField(ISO.field_03_PROCESSING_CODE);
        String field64;
        byte f64[];
        String field60;
        String field61;
        String field62;
        String hashSegmento;

        if (procCode.equals("930080")){
            field62 = rspTx.GetField(ISO.field_62_RESERVED_PRIVATE);
            hashSegmento = field62.substring(0, field62.indexOf("|"));
            gHashTotal = field62.substring(field62.indexOf("|") + 1);
            f64 = rspTx.GetFieldB(ISO.field_64_MESSAGE_AUTHENTICATION_CODE);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(File, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                int len = rspTx.getSizeField(64);
                if (Tools.hashSha1(f64).equals(hashSegmento)) {
                    fileOutputStream.write(f64, 0, rspTx.getSizeField(64));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }else {
            int len64 = 0;
            if (rspCode.equals("00")) {
                field61 = rspTx.GetField(ISO.field_61_RESERVED_PRIVATE);
                hashSegmento = field61.substring(0, field61.indexOf("|"));
                gHashTotal = field61.substring(field61.indexOf("|") + 1);
                f64 = rspTx.GetFieldB(ISO.field_64_MESSAGE_AUTHENTICATION_CODE);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(File, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    int len = rspTx.getSizeField(64);
                    if (Tools.hashSha1(f64).equals(hashSegmento)) {
                        fileOutputStream.write(f64, 0, rspTx.getSizeField(64));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else if (rspCode.equals("05")) {
                field60 = "ERROR EN LA DESCARGA \n NO EXISTE TERMINAL";//rspTx.GetField(ISO.field_60_RESERVED_PRIVATE);
                //UIUtils.toast((Activity) context, R.drawable.ic_launcher, field60, Toast.LENGTH_SHORT);
                resultOk = field60;
                return false;
            } else if (rspCode.equals("95")) {
                field60 = rspTx.GetField(ISO.field_60_RESERVED_PRIVATE);
                //UIUtils.toast((Activity) context, R.drawable.ic_launcher, field60, Toast.LENGTH_SHORT);
                resultOk = field60;
                return false;
            } else {
                field60 = "Code: " + rspCode + " ERROR DESCONOCIDO";
                resultOk = field60;
                return false;
            }
        }

        //return false;
    }
}


