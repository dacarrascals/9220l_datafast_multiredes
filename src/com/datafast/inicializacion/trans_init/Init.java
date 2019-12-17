package com.datafast.inicializacion.trans_init;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.inicializacion.configuracioncomercio.ChequeoIPs;
import com.datafast.inicializacion.init_emv.CAPK_ROW;
import com.datafast.inicializacion.init_emv.EMVAPP_ROW;
import com.datafast.inicializacion.tools.PolarisUtil;
import com.datafast.inicializacion.trans_init.trans.ISO;
import com.datafast.inicializacion.trans_init.trans.SendRcvd;
import com.datafast.inicializacion.trans_init.trans.Tools;
import com.datafast.inicializacion.trans_init.trans.UnpackFile;
import com.datafast.inicializacion.trans_init.trans.dbHelper;
import com.datafast.keys.InjectMasterKey;
import com.datafast.menus.MenuAction;
import com.datafast.server.activity.ServerTCP;
import com.datafast.transactions.callbacks.waitInitCallback;
import com.google.common.base.Strings;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.presenter.TransView;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.SDKException;
import com.pos.device.beeper.Beeper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.desert.newpos.payui.UIUtils;

import static com.android.newpos.pay.StartAppDATAFAST.host_confi;
import static com.android.newpos.pay.StartAppDATAFAST.isInit;
import static com.android.newpos.pay.StartAppDATAFAST.listIPs;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.CAKEY;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ENTRY_POINT;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.NAME_FOLDER_CTL_FILES;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.PROCESSING;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.REVOK;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.TERMINAL;
import static com.datafast.menus.MenuAction.callBackSeatle;
import static com.datafast.transactions.common.CommonFunctionalities.saveDateSettle;

public class Init extends AppCompatActivity {

    TextView txt;
    TextView tv_title;
    private String IP;
    private String puerto;
    private static String nii;
    private int espera;
    private String TID;
    public static String gHashTotal;
    public boolean isParcial = false;
    public static final int InitTotal = 1;
    public static final int InitParcial = 2;
    public int tipoInit;
    private TransView transView;


    public static waitInitCallback callBackInit;

    public static String gFileName;
    public static String gTID;
    public static String gOffset;
    public static File gFile;
    public static final String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStorageDirectory()+File.separator + "datafast";
    public static String NAME_DB = "init";

    static String TCONF = "TCONF";
    static String ACQS = "ACQS";
    static String ISSUERS = "ISSUERS";
    static String CARDS = "CARDS";
    static String EMVAPPS = "emvapps";
    static String CAPKS = "capks";
    static String EXTRAPARAMS = "extraparams";
    static String HOST_CONFI = "HOST_CONFI";
    public static String IPs = "IP";
    static String PAGOS_ELEC = "PAGOS_ELEC";
    static String PAGOS_VAR = "PAGOS_VAR";
    static String PROMPTS = "PROMPTS";
    static String GRUPOXPROMPT = "GrupoXPrompt";
    static String GRUPO_PROMPT = "Grupo_prompt";
    static String GRUPOXPAGOSVARIOS = "GrupoXPagosVarios";
    static String GRUPOPAGOSVARIOS = "GrupoPagosVarios";
    static String GRUPOXPAGOSELECTRONICOS = "GrupoXPagosElectronicos";
    static String GRUPOPAGOSELECTRONICOS = "GrupoPagosElectronicos";

    private static String getNameFileCTL(int id) {
        String ret="";
        switch (id)
        {
            case 1:
                ret=ENTRY_POINT;
                break;
            case 2:
                ret=PROCESSING;
                break;
            case 3:
                ret=REVOK;
                break;
            case 4:
                ret=TERMINAL;
                break;
            case 5:
                ret=CAKEY;
                break;
        }
        return ret;
    }
    private static String getNameTableById(int id)
    {
        String ret="";
        switch (id)
        {
            case 1:
                ret=TCONF;
                break;
            case 2:
                ret=ACQS;
                break;
            case 3:
                ret=ISSUERS;
                break;
            case 4:
                ret=CARDS;
                break;
            case 5:
                ret=EMVAPPS;
                break;
            case 6:
                ret=CAPKS;
                break;
            case 7:
                ret=EXTRAPARAMS;
                break;
            case 8:
                ret=HOST_CONFI;
                break;
            case 9:
                ret=IPs;
                break;
            case 10:
                ret=PAGOS_ELEC;
                break;
            case 11:
                ret=PAGOS_VAR;
                break;
            case 12:
                ret=PROMPTS;
                break;
            case 13:
                ret=GRUPOXPROMPT;
                break;
            case 14:
                ret=GRUPO_PROMPT;
                break;
            case 15:
                ret=GRUPOXPAGOSVARIOS;
                break;
            case 16:
                ret=GRUPOPAGOSVARIOS;
                break;
            case 17:
                ret=GRUPOXPAGOSELECTRONICOS;
                break;
            case 18:
                ret=GRUPOPAGOSELECTRONICOS;
                break;
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        txt = (TextView)findViewById(R.id.output);

        isParcial = getIntent().getBooleanExtra("PARCIAL", false);
        tipoInit = isParcial ? InitParcial : InitTotal;

        tv_title = (TextView) findViewById(R.id.textView_titleToolbar);
        tv_title.setText(Html.fromHtml("<h4> INICIALIZACION POLARIS </h4>"));
        callBackSeatle = null;
        init();
        download(null, tipoInit);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    private void init(){

        this.TID = ISOUtil.padright(TMConfig.getInstance().getTermID()+"", 8, '0');
        this.gFileName=TID+".zip";
        this.gTID=TID;
        this.gOffset="0";

        this.IP = TMConfig.getInstance().getIp();
        this.puerto = TMConfig.getInstance().getPort();
        this.nii = TMConfig.getInstance().getNii();
        this.espera = TMConfig.getInstance().getTimeout();
    }

    private long calcularOffset(String fileName)
    {
        long len=0;
        File dir= new File(DEFAULT_DOWNLOAD_PATH);
        gFile= new File(DEFAULT_DOWNLOAD_PATH + File.separator+ fileName + "T");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (gFile.exists()) {
            len = gFile.length();

        }
        else {
            try {
                if (!gFile.createNewFile()) {
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

    static String getHash(String fileNameTable,boolean forcedInit)
    {
        String ret="NA";
        String fileName = fileNameTable;
        FileInputStream fileIn= null;
        if(forcedInit==false) {

            File fileToRead = new File(DEFAULT_DOWNLOAD_PATH + File.separator + fileName);
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

    public static byte[] armarTramaDescarga(String aFileName, String aOffset, String hash)
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
        iso.setField(ISO.field_03_PROCESSING_CODE, "930100");
        iso.setField(ISO.field_11_SYSTEMS_TRACE_AUDIT_NUMBER, Strings.padStart(String.valueOf(TMConfig.getInstance().getTraceNo()),6,'0'));
        iso.setField(ISO.field_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION, gTID);

        outField60 =nombreArchivo+","+offset+","+solicitudBytes;
        iso.setField(ISO.field_60_RESERVED_PRIVATE,outField60);
        iso.setField(ISO.field_61_RESERVED_PRIVATE,"NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|");

        tmp = iso.getTxnOutput();

        return tmp;
    }

    private void download(String hash, int tramaQueEnvia) {
        onlineTrans();
    }


    private int onlineTrans() {

        final byte[] dataVacia = new byte[]{};

        SendRcvd sendTrans = new SendRcvd(IP, Integer.parseInt(puerto), espera, Init.this);

        sendTrans.setFileName(gFileName);
        sendTrans.setNii(nii);
        sendTrans.setTID(gTID);
        sendTrans.setOffset(gOffset);
        sendTrans.setPathDefault(DEFAULT_DOWNLOAD_PATH);
        sendTrans.setTramaQueEnvia(tipoInit);

        sendTrans.callbackResponse(new SendRcvd.TcpCallback(){

            @Override
            public void RspHost(byte[] rxBuf, String resultOk) {
                if (rxBuf == null || Arrays.equals(rxBuf, dataVacia)) {
                    //UIUtils.toast(Init.this, R.drawable.ic_launcher, "ERROR, INICIALIZACION FALLIDA", Toast.LENGTH_SHORT);
                    UIUtils.startResult(Init.this,false,"ERROR, INICIALIZACION FALLIDA",true);
                    //finish();
                    return;
                }

                if (!resultOk.equals("OK")){
                    //UIUtils.toast(Init.this, R.drawable.ic_launcher, resultOk, Toast.LENGTH_SHORT);
                    UIUtils.startResult(Init.this,false,resultOk,true);
                    //finish();
                    return;
                }

                ISO rspIso = new ISO(rxBuf,ISO.lenghtNotInclude, ISO.TpduInclude);

                if(rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("930100"))
                {
                    UIUtils.toast(Init.this, R.drawable.ic_launcher, (rspIso.GetField(ISO.field_60_RESERVED_PRIVATE)), Toast.LENGTH_SHORT);

                    callBackInit = null;

                    txt.setText(R.string.label_init_process);

                    if(processFile(gFileName)){

                        callBackInit = new waitInitCallback() {
                            @Override
                            public void getRspInitCallback(int status) {
                                try {
                                    //Inyectar WorkingKey
                                    if(!inyectarWorkingKey()){
                                        isInit = false;
                                        //UIUtils.toast(Init.this, R.drawable.ic_launcher, "INICIALIZACION FALLIDA", Toast.LENGTH_SHORT);
                                        UIUtils.startResult(Init.this,false,"INYECCION DE LLAVE FALLIDA",true);
                                        //finish();
                                    }else {

                                        isInit = PolarisUtil.isInitPolaris(Init.this);
                                        //isInit = true;
                                        if (isInit) {
                                            tconf.selectTconf(Init.this);
                                            host_confi.selectHostConfi(Init.this);
                                            listIPs = ChequeoIPs.selectIP(Init.this);
                                            if (listIPs == null) {
                                                listIPs = new ArrayList<>();
                                                listIPs.clear();
                                                isInit = false;
                                                //UIUtils.toast(Init.this, R.drawable.ic_launcher, "Error al leer tabla, Por favor Inicialice nuevamente", Toast.LENGTH_LONG);
                                                UIUtils.startResult(Init.this,false,"Error al leer tabla, Por favor Inicialice nuevamente",true);
                                                //finish();
                                            } else if (listIPs.isEmpty()) {
                                                listIPs.clear();
                                                isInit = false;
                                                //UIUtils.toast(Init.this, R.drawable.ic_launcher, "Error al leer tabla, Por favor Inicialice nuevamente", Toast.LENGTH_LONG);
                                                UIUtils.startResult(Init.this,false,"Error al leer tabla, Por favor Inicialice nuevamente",true);
                                                //finish();
                                            } else {

                                                int numLote = Integer.parseInt(tconf.getNUMERO_LOTE());
                                                if (numLote != 0)
                                                    TMConfig.getInstance().setBatchNo(numLote - 1).save();
                                                else
                                                    TMConfig.getInstance().setBatchNo(numLote).save();

                                                saveDateSettle(Init.this);
                                                Beeper.getInstance().beep();
                                                //UIUtils.toast(Init.this, R.drawable.ic_launcher, "INICIALIZACION EXITOSA", Toast.LENGTH_SHORT);
                                                UIUtils.startResult(Init.this, true, "INICIALIZACION EXITOSA", true);
                                                //finish();
                                                //startActivity( new Intent(Init.this, ServerTCP.class) );
                                            }
                                        } else {
                                            //UIUtils.toast(Init.this, R.drawable.ic_launcher, "INICIALIZACION FALLIDA", Toast.LENGTH_SHORT);
                                            UIUtils.startResult(Init.this,false,"INICIALIZACION FALLIDA",true);
                                            //finish();
                                        }
                                    }

                                } catch (SDKException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                    }else{
                        //UIUtils.toast(Init.this, R.drawable.ic_launcher, "INICIALIZACION FALLIDA", Toast.LENGTH_SHORT);
                        UIUtils.startResult(Init.this,false,"INICIALIZACION FALLIDA",true);

                        //finish();
                    }
                }
                else if (rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("930080")){
                    if(processFile(gFileName)) {
                        callBackInit = null;
                        callBackInit = new waitInitCallback() {
                            @Override
                            public void getRspInitCallback(int status) {
                                finish();
                            }
                        };
                    }
                }
            }
        });

        sendTrans.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return 0;
    }

    /*private int OnlineTrans(String aFileName, String aOffset, final String hash, final int tramaQueEnvia) {
        byte[] data = new byte[0];

        switch (tramaQueEnvia){
            case InitTotal:
                data = armarTramaDescarga(aFileName, aOffset, hash);
                break;
            case InitParcial:
                data = armarTramaDescargaParcial(aFileName, aOffset, false);
                break;
        }

        //SendRcvd sendTrans = new SendRcvd("186.154.93.81", Integer.parseInt("7177"), Integer.parseInt("20000"), Init.this, new SendRcvd.TcpCallback() {
        final byte[] dataVacia = new byte[]{};
        SendRcvd sendTrans = new SendRcvd(IP, Integer.parseInt(puerto), espera, Init.this, new SendRcvd.TcpCallback() {
            @Override
            public void RspHost(byte[] rxBuf) {
                if (rxBuf == null || Arrays.equals(rxBuf, dataVacia)) {
                    //UIUtils.toast(Init.this, R.drawable.ic_launcher, "ERROR DE CONEXIÓN", Toast.LENGTH_SHORT);
                    finish();
                    return;
                }

                String dta = ISOUtil.bcd2str(rxBuf, 0, rxBuf.length);
                //System.out.println(dta);

                if(unpackDescarga(new ISO(rxBuf,ISO.lenghtNotInclude, ISO.TpduInclude))==true)
                {
                    ISO rspIso = new ISO(rxBuf,ISO.lenghtNotInclude, ISO.TpduInclude);
                    //ISO.incStan();
                    TMConfig.getInstance().incTraceNo().save();

                    if(rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("930101"))
                    {
                        long offset;
                        offset = calcularOffset(gFileName);
                        OnlineTrans(gFileName,""+offset, hash, tramaQueEnvia);
                    }
                    else if(rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("930100"))
                    {
                        UIUtils.toast(Init.this, R.drawable.ic_launcher, (rspIso.GetField(ISO.field_60_RESERVED_PRIVATE)), Toast.LENGTH_SHORT);

                        callBackInit = null;

                        txt.setText(R.string.label_init_process);

                        if(processFile(gFileName)){

                            callBackInit = new waitInitCallback() {
                                @Override
                                public void getRspInitCallback(int status) {
                                    try {
                                        //Inyectar WorkingKey
                                        inyectarWorkingKey();
                                        isInit = PolarisUtil.isInitPolaris(Init.this);
                                        //isInit = true;
                                        if (isInit) {
                                            tconf.selectTconf(Init.this);
                                            host_confi.selectHostConfi(Init.this);
                                            listIPs = ChequeoIPs.selectIP(Init.this);
                                            if (listIPs == null) {
                                                listIPs = new ArrayList<>();
                                                listIPs.clear();
                                                isInit = false;
                                                UIUtils.toast(Init.this, R.drawable.ic_launcher, "Error al leer tabla, Por favor Inicialice nuevamente", Toast.LENGTH_LONG);
                                            }else if (listIPs.isEmpty()) {
                                                listIPs.clear();
                                                isInit = false;
                                                UIUtils.toast(Init.this, R.drawable.ic_launcher, "Error al leer tabla, Por favor Inicialice nuevamente", Toast.LENGTH_LONG);
                                            }
                                            else {

                                                int numLote = Integer.parseInt(tconf.getNUMERO_LOTE());
                                                if (numLote != 0)
                                                    TMConfig.getInstance().setBatchNo(numLote - 1).save();
                                                else
                                                    TMConfig.getInstance().setBatchNo(numLote).save();

                                                saveDateSettle(Init.this);
                                                Beeper.getInstance().beep();
                                                UIUtils.toast(Init.this, R.drawable.ic_launcher, "INICIALIZACION EXITOSA", Toast.LENGTH_SHORT);
                                                finish();
                                            }
                                        }
                                        else{
                                            UIUtils.toast(Init.this, R.drawable.ic_launcher, "INICIALIZACION FALLIDA", Toast.LENGTH_SHORT);
                                            finish();
                                        }

                                    } catch (SDKException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                        }else{
                            UIUtils.toast(Init.this, R.drawable.ic_launcher, "INICIALIZACION FALLIDA", Toast.LENGTH_SHORT);
                            finish();
                        }
                    }

                    else if (rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("930080")){
                        if(processFile(gFileName)) {
                            callBackInit = null;
                            callBackInit = new waitInitCallback() {
                                @Override
                                public void getRspInitCallback(int status) {
                                    finish();
                                }
                            };
                        }
                    }
                    //  guardarLogin(userLogin,pswLogin);
                }else {
                    finish();
                }


            }
        });
        sendTrans.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,data);

        return 0;
    }*/

    public boolean processFile(String aFileName) {
        int READ_BLOCK_SIZE = 65000*2;
        File file = new File(DEFAULT_DOWNLOAD_PATH +File.separator+ gFileName + "T");
        if (!file.exists()) {
            file = new File(DEFAULT_DOWNLOAD_PATH + File.separator + gFileName);
        }
        if (file.exists())
        {
            file.renameTo(new File(DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName));
            if (gFileName.endsWith(".txt")) {
                try {
                    FileInputStream fileIn=new FileInputStream(new File(DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName));
//                            this.ctx.openFileInput(DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName);
                    InputStreamReader InputRead= new InputStreamReader(fileIn);

                    char[] inputBuffer= new char[READ_BLOCK_SIZE];
                    String s="";
                    int charRead;

                    while ((charRead=InputRead.read(inputBuffer))>0) {
                        // char to string conversion
                        String readstring=String.copyValueOf(inputBuffer,0,charRead);
                        s +=readstring;
                    }
                    InputRead.close();
                    String [] fieldSplit=s.split(";");
                    dbHelper db = new dbHelper(getApplicationContext(), "init", null, 1);
                    db.openDb("init");
                    for (String str:fieldSplit) {
                        if(!str.equals("\n")) {
                            if(str.contains("DROP TABLE")) {
                                try {
                                    db.execSql(str);
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                            else
                            {
                                db.execSql(str);
                            }
                        }
                    }

                    db.closeDb();
                    String rename = DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName + "T";
                    new File(DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName).renameTo(new File(rename));

                    //file.delete();//luego de creada la tabla en la base de datos se eliminan los archivos descargados

                } catch (Exception e) {
                    //UIUtils.toast(Init.this, R.drawable.ic_launcher, "INICIALIZACION FALLIDA", Toast.LENGTH_SHORT);
                    UIUtils.startResult(Init.this,false,"INICIALIZACION FALLIDA",true);
                    //Tools.toast("Inicializacion Fallo");
                    e.printStackTrace();
                    new File(DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName).delete();
                    return false;
                }

                //Tools.saveHash(gHashTotal, getApplicationContext()); //guarda hash
            }
            if (gFileName.endsWith(".zip")) {
                unzip( gFileName, DEFAULT_DOWNLOAD_PATH+  File.separator , getApplicationContext() );
            }
        }
        return true;
    }

    /**
     * Lee los archivos de configuracion de CTL y los copia en una ruta interna dentro del
     * package de la aplicacion (acceso solo desde la app), posterior a esto se eliminan del
     * SD
     * @param aFileName
     * @param aFileWithOutExt
     * @return
     */
    public boolean processFilesCTL(String aFileName, String aFileWithOutExt) {
        File fileLocation = new File(DEFAULT_DOWNLOAD_PATH +File.separator+ aFileName);

        ContextWrapper cw = new ContextWrapper(Init.this);
        File directory = cw.getDir(NAME_FOLDER_CTL_FILES, Context.MODE_PRIVATE);
        File file = new File(directory + File.separator + aFileWithOutExt);

        if (fileLocation.exists()) {

            if (gFileName.endsWith(".bin")||gFileName.endsWith(".BIN")) {
                try {
                    FileInputStream InputRead = new FileInputStream(fileLocation);
                    FileOutputStream outWrite = new FileOutputStream(file);

                    byte[] inputBuffer = new byte[1024];
                    int charRead;

                    while ((charRead = InputRead.read(inputBuffer)) > 0) {
                        outWrite.write(inputBuffer,0,charRead);
                        outWrite.flush();
                    }
                    InputRead.close();
                    outWrite.close();

                    //fileLocation.delete();

                }catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public void unzip( final String zipFile, String location, Context context) {

        UnpackFile unpackFile;
        boolean ponerLaT = true;

        unpackFile = new UnpackFile(context, zipFile, location, ponerLaT, true, new UnpackFile.FileCallback() {
            @Override
            public boolean RspUnpack(boolean OK_unpack) {

                if (OK_unpack) {
                    String nameAux;
                    String nameTbl;

                    int i;
                    new File(DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName).delete();

                    nameAux=zipFile.replace(".zip","");
                    i=1;
                    nameTbl=getNameTableById(i);
                    while(!nameTbl.equals(""))
                    {
                        gFileName = nameAux + "_" + getNameTableById(i) + ".txt";
                        processFile(gFileName);
                        i++;
                        nameTbl=getNameTableById(i);
                    }

                    i=1;
                    nameTbl=getNameFileCTL(i);
                    while (!nameTbl.equals("")) {
                        gFileName = nameAux + "_" + getNameFileCTL(i) + ".bin";
                        processFilesCTL(gFileName, getNameFileCTL(i));
                        i++;
                        nameTbl=getNameFileCTL(i);
                    }

                    //Tools.toast("Inicializacion finalizada!!!");
                    if (callBackInit != null)
                        callBackInit.getRspInitCallback(0);
                    return true;
                } else {
                    return false;
                }
            }
        });
        unpackFile.execute();
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
                fileOutputStream = new FileOutputStream(gFile, true);
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
//            field60=rspTx.GetField(ISO.field_60_RESERVED_PRIVATE);
                //          field64=rspTx.GetField(ISO.field_64_MESSAGE_AUTHENTICATION_CODE);
                field61 = rspTx.GetField(ISO.field_61_RESERVED_PRIVATE);
                hashSegmento = field61.substring(0, field61.indexOf("|"));
                gHashTotal = field61.substring(field61.indexOf("|") + 1);
                f64 = rspTx.GetFieldB(ISO.field_64_MESSAGE_AUTHENTICATION_CODE);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(gFile, true);
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
                //UIUtils.toast(Init.this, R.drawable.ic_launcher, field60, Toast.LENGTH_SHORT);
                UIUtils.startResult(Init.this,false,field60,true);
                return false;
            } else if (rspCode.equals("95")) {
                field60 = rspTx.GetField(ISO.field_60_RESERVED_PRIVATE);
                //UIUtils.toast(Init.this, R.drawable.ic_launcher, field60, Toast.LENGTH_SHORT);
                UIUtils.startResult(Init.this,false,field60,true);
                return false;
            }
        }

        return false;
    }

    private boolean inyectarWorkingKey(){
        String workingKey = "";
        tconf.selectTconf(Init.this);
        host_confi.selectHostConfi(Init.this);
        if (host_confi != null) {

            if (PAYUtils.stringToBoolean(host_confi.getLLAVE_DOBLE())) {
                workingKey = host_confi.getLLAVE_1() + host_confi.getLLAVE_2();
            } else {
                workingKey = host_confi.getLLAVE_1();
            }
            if (InjectMasterKey.injectWorkingKey(workingKey)!=0) {
                //UIUtils.toast(Init.this, R.drawable.ic_launcher, "INYECCION DE LLAVE FALLIDA", Toast.LENGTH_SHORT);
                //UIUtils.startResult(Init.this,false,"INYECCION DE LLAVE FALLIDA",true);
                return false;
            }
        }
        return true;
    }
}
