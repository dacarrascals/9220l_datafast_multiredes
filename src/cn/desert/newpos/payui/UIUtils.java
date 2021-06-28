package cn.desert.newpos.payui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.server.activity.ServerTCP;
import com.newpos.libpay.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.desert.newpos.payui.master.ResultControl;

import static com.android.newpos.pay.StartAppDATAFAST.toneG;
import static java.lang.Thread.sleep;

/**
 * @author zhouqiang
 * @email wy1376359644@163.com
 */
public class UIUtils {

    /**
     * 显示交易结果
     *
     * @param activity
     * @param flag
     * @param info
     */
    public static void startResult(Activity activity, boolean flag, String info) {
        Intent intent = new Intent();
        intent.setClass(activity, ResultControl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag", flag);
        bundle.putString("info", info);
        bundle.putBoolean("boton",false);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 显示交易结果
     *
     * @param activity
     * @param flag
     * @param info
     */
    public static void startResult(Activity activity, boolean flag, String info, boolean boton) {
        Intent intent = new Intent();
        intent.setClass(activity, ResultControl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag", flag);
        bundle.putBoolean("boton",boton);
        bundle.putString("info", info);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * @param activity
     * @param cls
     */
    public static void startView(Activity activity, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 自定义提示信息
     *
     * @param activity
     * @param content
     */
    public static void toast(Activity activity, boolean flag, int content) {
        LayoutInflater inflater_3 = activity.getLayoutInflater();
        View view_3 = inflater_3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        ImageView face = (ImageView) view_3.findViewById(R.id.app_t_iv);
        if (flag) {
            face.setBackgroundResource(R.drawable.icon_face_laugh);
        } else {
            face.setBackgroundResource(R.drawable.icon_face_cry);
        }
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view_3);
        ((TextView) view_3.findViewById(R.id.toast_tv)).
                setText(activity.getResources().getString(content));
        toast.show();
    }

    /**
     * 自定义提示信息
     *
     * @param activity
     * @param str
     */
    public static void toast(Activity activity, boolean flag, String str) {
        LayoutInflater inflater_3 = activity.getLayoutInflater();
        View view_3 = inflater_3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        ImageView face = (ImageView) view_3.findViewById(R.id.app_t_iv);
        if (flag) {
            face.setBackgroundResource(R.drawable.icon_face_laugh);
        } else {
            face.setBackgroundResource(R.drawable.icon_face_cry);
        }
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view_3);
        ((TextView) view_3.findViewById(R.id.toast_tv)).setText(str);
        toast.show();
    }

    public static void toast(Activity activity, int ico, String str, int duration) {
        LayoutInflater inflater_3 = activity.getLayoutInflater();
        View view_3 = inflater_3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        ImageView face = (ImageView) view_3.findViewById(R.id.app_t_iv);

        face.setBackgroundResource(ico);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(view_3);
        ((TextView) view_3.findViewById(R.id.toast_tv)).setText(str);
        toast.show();
    }

    public static void toastReverse(Activity activity, int ico, String str, int duration) {
        LayoutInflater inflater_3 = activity.getLayoutInflater();
        Timer timer = new Timer();
        View view_3 = inflater_3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        ImageView face = (ImageView) view_3.findViewById(R.id.app_t_iv);

        face.setBackgroundResource(ico);

        final Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(view_3);
        ((TextView) view_3.findViewById(R.id.toast_tv)).setText(str);
        toast.show();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
            }
        } , 1000);
    }

    public static Dialog centerDialog(Context mContext, int resID, int root) {
        final Dialog pd = new Dialog(mContext, R.style.Translucent_Dialog);
        pd.setContentView(resID);
        LinearLayout layout = (LinearLayout) pd.findViewById(root);
        layout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_down));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(true);
        pd.show();
        return pd;
    }

    public static void dialogInformativo(final Context context, final String titulo ,String contenido) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_autenticacion);

        TextView txt = dialog.findViewById(R.id.tv_content);
        ImageView icono =dialog.findViewById(R.id.icono);
        if(titulo.equals("RESUMEN DE TRX")){

        }else if(titulo.equals("DATOS DE CONEXION")){

        }else if(titulo.equals("INFORMACION DEL COMERCIO")){

        }
        TextView tit = dialog.findViewById(R.id.tvTitulo);
        tit.setText(titulo);
        txt.setText(contenido);
        Button btn_aceptar = dialog.findViewById(R.id.btn_aceptar);

        btn_aceptar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    dialog.dismiss();
                    /*if (titulo.contains("CONEXION")) {
                        Intent intent = new Intent(context, ServerTCP.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }*/
                }
                return false;
            }
        });

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /*if (titulo.contains("CONEXION")) {
                    Intent intent = new Intent(context, ServerTCP.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }*/
            }
        });
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        }, 30000);
    }

    public static String getInputTitle(Context c, int type) {
        String str = "";
        for (int i = 0; i < 5; i++) {
            if (type == i) {
                str = c.getResources().getString(IItem.InputTitle.TITLEs[i]);
                break;
            }
        }
        return str;
    }

    public static void sendKeyCode(final int keyCode) {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    Logger.error("Exception when sendPointerSync");
                }
            }
        }.start();
    }

    /**
     * 拷贝assets文件夹至某一目录
     *
     * @param context
     * @param assetDir
     * @param dir
     */
    public static void copyToAssets(Context context, String assetDir, String dir) {

        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            // 创建文件夹
            if (!mWorkingPath.mkdirs()) {
                // 文件夹创建不成功时调用
            }
        }

        for (int i = 0; i < files.length; i++) {
            try {

                // 获得每个文件的名字
                String fileName = files[i];
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }

                InputStream in = null;
                try {
                    if (0 != assetDir.length()) {
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    } else {
                        in = context.getAssets().open(fileName);
                    }
                } catch (Exception e) {
                    Logger.error("Exception" + e.toString());
                    //it said that this is a directory
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }

                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }

                try (FileOutputStream out = new FileOutputStream(outFile)) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    if (in != null) {
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.flush();
                        out.getFD().sync();
                        out.close();
                        in.close();
                    }
                }
            } catch (IOException e) {
                Logger.error("Exception" + e.toString());
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void copyToAssets2(Context context, String assetDir, String dir) {

        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            // 创建文件夹
            if (!mWorkingPath.mkdirs()) {
                // 文件夹创建不成功时调用
            }
        }

        for (int i = 0; i < files.length; i++) {
            try {

                // 获得每个文件的名字
                String fileName = files[i];
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }

                InputStream in = null;
                try {
                    if (0 != assetDir.length()) {
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    }else {
                        in = context.getAssets().open(fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //it said that this is a directory
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }

                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }
                FileOutputStream out = new FileOutputStream(outFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.flush();
                out.getFD().sync();
                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param pathName
     * @param reqWidth  单位：px
     * @param reqHeight 单位：px
     * @return
     * @description 从SD卡上加载图片
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName,
                                                     int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    /**
     * @param options   参数
     * @param reqWidth  目标的宽度
     * @param reqHeight 目标的高度
     * @return
     * @description 计算图片的压缩比率
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                            int dstHeight, int inSampleSize) {
        // 如果inSampleSize是2的倍数，也就说这个src已经是我们想要的缩略图了，直接返回即可。
        if (inSampleSize == 1) {
            return src;
        }
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * 小屏广告获取
     *
     * @param dir
     * @return
     */
    public static List<String> getAds(String dir) {
        List<String> adList = new ArrayList<>();
        File adRoot = new File(dir);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(new Date());

        File[] adDirs = adRoot.listFiles();
        if (adDirs == null) {
            return adList;
        }
        for (File adDir : adDirs) {
            Log.d("ad", "ad list files = " + adDir.getAbsolutePath());
            if (adDir.isDirectory()) {
                String dirName = adDir.getName();
                String[] infos = dirName.split("-");
                if (infos.length != 3) {
                    continue;
                } else if (now.compareTo(infos[2]) > 0) {
                    delete(adDir);
                } else if (now.compareTo(infos[1]) > 0) {
                    for (File ad : adDir.listFiles()) {
                        adList.add(ad.getAbsolutePath());
                    }
                }
            }
        }
        // 如果没有服务器下发的广告，显示默认广告
        if (adList.size() == 0) {
            for (File adFile : adDirs) {
                if (adFile.getName().contains(".json")) {
                    continue;
                }
                adList.add(adFile.getAbsolutePath());
            }
        }
//        Collections.sort(adList, new ADComparator());
        Log.v("ad", "adList:" + adList);
        return adList;
    }

    /**
     * 删除文件或者文件夹
     *
     * @param file
     */
    public static void delete(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 根据资源ID获取资源字符串
     *
     * @param context 上下文对象
     * @param resid   资源ID
     * @return
     */
    public static String getStringByInt(Context context, int resid) {
        String sAgeFormat1 = context.getResources().getString(resid);
        return sAgeFormat1;
    }

    /**
     * 根据资源ID获取资源字符串
     *
     * @param context
     * @param resid
     * @param parm
     * @return
     */
    public static String getStringByInt(Context context, int resid, String parm) {
        String sAgeFormat1 = context.getResources().getString(resid);
        String sFinal1 = String.format(sAgeFormat1, parm);
        return sFinal1;
    }

    /**
     * 根据资源ID获取资源字符串
     *
     * @param context
     * @param resid
     * @param parm1
     * @param parm2
     * @return
     */
    public static String getStringByInt(Context context, int resid,
                                        String parm1, String parm2) {
        String sAgeFormat1 = context.getResources().getString(resid);
        String sFinal1 = String.format(sAgeFormat1, parm1, parm2);
        return sFinal1;
    }

    public static void beep(int typeTone) {

        int timeOut = 1;


        long start = SystemClock.uptimeMillis();
        while (true) {
            toneG.startTone(typeTone, 2000);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                Logger.error("Exception" + e.toString());
                Thread.currentThread().interrupt();
            }

            if (SystemClock.uptimeMillis() - start > timeOut) {
                toneG.stopTone();
                break;
            }
        }
    }

    public static String labelHTML(final String label, final String value) {
        String labelHtml = "";
        return labelHtml = "<b>" + label + "</b>" + " " + value;
    }

    /**
     *  Crea un dialogo de alerta
     *  @return Nuevo dialogo
     */
    public static void showAlertDialog(String title, String msg, Context context){
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);

        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_launcher_1);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /*alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/

        android.app.AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    static AlertDialog.Builder alertDialog;
    public static AlertDialog dialog;
    public static void showAlertDialogInit(String title, String msg, Context context){
        alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_launcher_1);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogI, int which) {
                dialog.dismiss();
            }
        });


        dialog = alertDialog.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}
