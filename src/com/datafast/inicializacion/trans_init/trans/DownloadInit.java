package com.datafast.inicializacion.trans_init.trans;

public class DownloadInit {
 /*   public static String gFileName;
    public static String gTID;
    public static String gOffset;
    public static Context ctx;
     public DownloadInit(Context context, String aFilename, String aTID)
    {
        this.gFileName=aFilename;
        this.gTID=aTID;
        this.gOffset="0";
        this.ctx = context;
    }
    public static File gFile;
    public static final String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStorageDirectory() +
     File.separator + "download";

    private long calcularOffset(String fileName)
    {
        long len=0;
        gFile= new File(DEFAULT_DOWNLOAD_PATH + File.separator+ fileName + "T");
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
    public static byte[] armarTramaDescarga(String aFileName, String aOffset)
    {
        String outField60 = null;
        String nombreArchivo = null ,offset = null ,solicitudBytes = "20000";
        byte tmp[] = null;

        nombreArchivo = aFileName;
        offset =  aOffset;

        ISO iso = new ISO(ISO.lenghtInclude, ISO.TpduInclude);
        iso.setTPDUId("60");
        iso.setTPDUDestination("0001");
        iso.setTPDUSource("0000");

        iso.setMsgType("0800");
        iso.setField(ISO.field_03_PROCESSING_CODE, "920100");
        iso.setField(ISO.field_11_SYSTEMS_TRACE_AUDIT_NUMBER, Strings.padStart(String.valueOf(TMConfig.getInstance().getTraceNo()),6,'0'));
        iso.setField(ISO.field_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION, gTID);

        outField60 =nombreArchivo+","+offset+","+solicitudBytes;
        iso.setField(ISO.field_60_RESERVED_PRIVATE,outField60);

        tmp = iso.getTxnOutput();

        return tmp;
    }
    public int download()
    {
        long offset;
        offset = calcularOffset(gFileName);
        gOffset = ""+offset;
        return OnlineTrans(gFileName,gOffset);
    }

    private int OnlineTrans(String aFileName, String aOffset) {
        byte[] data;
        final int[] ret = {1};
        data = armarTramaDescarga(aFileName,aOffset);

        SendRcvd sendTrans = new SendRcvd("186.154.93.81", Integer.parseInt("7177"), Integer.parseInt("20000"), this.ctx, new SendRcvd.TcpCallback() {
            @Override
            public void RspHost(byte[] rxBuf) {
                if (rxBuf == null) {
                    UIUtils.toast((Activity) ctx, R.drawable.ic_launcher, "RX NULL", Toast.LENGTH_SHORT);
                    return;
                }


                if(unpackDescarga(new ISO(rxBuf,ISO.lenghtNotInclude, ISO.TpduInclude))==true)
                {
                    ISO rspIso = new ISO(rxBuf,ISO.lenghtNotInclude, ISO.TpduInclude);
                    //ISO.incStan();
                    TMConfig.getInstance().incTraceNo().save();

                    if(rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("920101"))
                    {
                        long offset;
                        offset = calcularOffset(gFileName);
                        OnlineTrans(gFileName,""+offset);
                    }
                    else if(rspIso.GetField(ISO.field_03_PROCESSING_CODE).equals("920100"))
                    {
                        UIUtils.toast((Activity) ctx, R.drawable.ic_launcher, (rspIso.GetField(ISO.field_60_RESERVED_PRIVATE)), Toast.LENGTH_SHORT);
                        if(processFile(gFileName))
                            ret[0] = 0;

                    }
                    //  guardarLogin(userLogin,pswLogin);
                }


            }
        });
        sendTrans.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,data);

        return ret[0];
    }

    boolean processFile(String aFileName) {
        int READ_BLOCK_SIZE = 65000*2;
        File file = new File(DEFAULT_DOWNLOAD_PATH + File.separator+ gFileName + "T");
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
                        String readstring= String.copyValueOf(inputBuffer,0,charRead);
                        s +=readstring;
                    }
                    InputRead.close();
                    String[] fieldSplit=s.split(";");
                    this.ctx.deleteDatabase("init");
                    dbHelper db = new dbHelper(this.ctx, "init", null, 1);

                    db.openDb("init");
                    for (String str:fieldSplit) {
                        db.execSql(str);
                    }

                    db.closeDb();
                    //Init ok
                    new File(DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName).delete();
                    return true;

                } catch (Exception e) {
                    //Tools.toast("Inicializacion Fallo");
                    UIUtils.toast((Activity) ctx, R.drawable.ic_launcher, "Inicializacion Fallo", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                    new File(DEFAULT_DOWNLOAD_PATH +  File.separator+ gFileName).delete();
                    return false;
                }
            }
            if (gFileName.endsWith(".zip")) {

            }
        }
        return false;
    }
    static boolean unpackDescarga(ISO rspTx)
    {

        String rspCode = rspTx.GetField(ISO.field_39_RESPONSE_CODE);
        String field64;
        String field60;
        int len64 = 0;
        if (rspCode.equals("00")){
            field60=rspTx.GetField(ISO.field_60_RESERVED_PRIVATE);
            field64=rspTx.GetField(ISO.field_64_MESSAGE_AUTHENTICATION_CODE);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream( gFile ,true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try{
                int len=rspTx.getSizeField(64);
                fileOutputStream.write(field64.getBytes(),0,rspTx.getSizeField(64));

            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }

*/

}
