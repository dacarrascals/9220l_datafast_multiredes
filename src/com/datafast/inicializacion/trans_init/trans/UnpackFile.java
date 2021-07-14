package com.datafast.inicializacion.trans_init.trans;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.Thread.sleep;

/**
 * Created by Technology&Solutions on 31/05/2017.
 */

public class UnpackFile extends AsyncTask<String, Integer,Boolean> {

    //private ProgressDialog mProgresoDescompresion;
    private String _Ubicacion_ZIP;
    private String _Destino_Descompresion;
    private boolean _Mantener_ZIP;
    private FileCallback callback;
    private boolean ponerLaT;

    /**
     * Descomprime un archivo .ZIP
     * @param ctx Contexto de la Aplicación Android
     * @param Ubicacion Ruta ABSOLUTA de un archivo .zip
     * @param Destino Ruta ABSOLUTA del destino de la descompresión. Finalizar con /
     * @param Mantener Indica si se debe mantener el archivo ZIP despues de descomprimir
     */

    public UnpackFile(Context ctx, String Ubicacion, String Destino, boolean ponerLaT, boolean Mantener, final FileCallback callback)

    {
        this._Ubicacion_ZIP = Ubicacion;
        this._Destino_Descompresion = Destino;
        this._Mantener_ZIP = Mantener;
        this.callback = callback;
        this.ponerLaT = ponerLaT;

    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        //mProgresoDescompresion.show();

    }

    @Override
    protected Boolean doInBackground(String... params) {

        int size;
        byte[] buffer = new byte[2048];

        new File(_Destino_Descompresion).mkdirs(); //Crea la ruta de descompresion si no existe

        try {

            try {

                FileInputStream lector_archivo = new FileInputStream( _Destino_Descompresion+_Ubicacion_ZIP);
                ZipInputStream lector_zip = new ZipInputStream(lector_archivo);
                ZipEntry item_zip = null;

                while ((item_zip = lector_zip.getNextEntry()) != null) {
                    Log.v("UnpackFile", "Descomprimiendo " + item_zip.getName());

                    if (item_zip.isDirectory()) { //Si el elemento es un directorio, crearlo
                        Crea_Carpetas(item_zip.getName(), _Destino_Descompresion);
                    } else {
                        FileOutputStream outStream;
                        if (ponerLaT){
                            if (item_zip.getName().endsWith(".bin") || item_zip.getName().endsWith(".BIN")){
                                outStream = new FileOutputStream(_Destino_Descompresion + item_zip.getName());
                            }else{
                                outStream = new FileOutputStream(_Destino_Descompresion + item_zip.getName()+"T");
                            }
                        }else{
                            outStream = new FileOutputStream(_Destino_Descompresion + item_zip.getName());
                        }
//                        FileOutputStream outStream = ponerLaT ? new FileOutputStream(_Destino_Descompresion + item_zip.getName()+"T")
//                                : new FileOutputStream(_Destino_Descompresion + item_zip.getName());
                        //FileOutputStream outStream = new FileOutputStream(_Destino_Descompresion + item_zip.getName()+"T");
                        BufferedOutputStream bufferOut = new BufferedOutputStream(outStream, buffer.length);

                        while ((size = lector_zip.read(buffer, 0, buffer.length)) != -1) {
                            bufferOut.write(buffer, 0, size);
                        }

                        bufferOut.flush();
                        bufferOut.close();
                    }
                }

                lector_zip.close();
                lector_archivo.close();

                //Conservar archvi .zip
                if(!_Mantener_ZIP)
                    new File(_Ubicacion_ZIP).delete();

                //Espera para poder realizar las validaciones por cada ciclo
                sleep(5000);

                return true;

            } catch (Exception e) {
                Log.e("UnpackFile", "Descomprimir", e);
            }

            //mProgresoDescompresion.dismiss();

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return false;

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        //mProgresoDescompresion.setProgress(progress[0]);

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        //callback
        callback.RspUnpack(aBoolean);
    }

    /**
     * Crea la carpeta donde seran almacenados los archivos del .zip
     * @param dir
     * @param location
     */
    private void Crea_Carpetas(String dir, String location) {
        File f = new File(location + dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    /**
     *
     */
    public interface FileCallback{
        boolean RspUnpack(boolean OK_unpack);
    }

}
