package com.newpos.libpay.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.PaySdkException;
import com.newpos.libpay.global.TMConstants;
import com.pos.device.SDKException;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.rtc.RealTimeClock;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import static com.datafast.pinpad.cmd.defines.CmdDatafast.AUTORIZADO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CTL;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_EN_TRAMA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ERROR_PROCESO;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.FBI;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.HDL;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.ICC;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.INICIO_DIA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.INICIO_DIA_MSG;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.MAG;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.OK;
import static com.newpos.libpay.trans.Tcode.T_gen_2_ac_fail;
import static com.newpos.libpay.trans.Tcode.T_socket_err;
import static com.newpos.libpay.trans.Tcode.T_success;
import static com.newpos.libpay.trans.Tcode.T_user_cancel_input;
import static com.newpos.libpay.trans.Tcode.T_user_cancel_operation;
import static com.newpos.libpay.trans.Tcode.T_wait_timeout;
import static com.newpos.libpay.trans.Trans.ENTRY_MODE_FALLBACK;
import static com.newpos.libpay.trans.Trans.ENTRY_MODE_HAND;
import static com.newpos.libpay.trans.Trans.ENTRY_MODE_ICC;
import static com.newpos.libpay.trans.Trans.ENTRY_MODE_MAG;
import static com.newpos.libpay.trans.Trans.ENTRY_MODE_NFC;

/**
 * Created by zhouqiang on 2017/6/30.
 * @author zhouqiang
 * 支付相关工具类
 */

public class PAYUtils {

    /**
     * 发卡脚本结果上送 55域
     */
    public static final int wISR_tags[] = {0x9F33, // Terminal Capabilities
            0x95, // TVR
            0x9F37, // Unpredicatable Number
            0x9F1E, // IFD Serial Number
            0x9F10, // Issuer Application Data
            0x9F26, // Application Cryptogram
            0x9F36, // Application Tranaction Counter
            0x82, // AIP
            0xDF31, // 发卡行脚本结果
            0x9F1A, // Terminal Country Code
            0x9A, // Transaction Date
            0};

    /**
     * 消费 55域数据
     */
    public static final int wOnlineTags[] = {
            0x9F26, // AC (Application Cryptogram)
            0x9F27, // CID
            0x9F10, // IAD (Issuer Application Data)
            0x9F37, // Unpredicatable Number
            0x9F36, // ATC (Application Transaction Counter)
            0x95, // TVR
            //0x9B,// TSI
            0x9A, // Transaction Date
            0x9C, // Transaction Type
            0x9F02, // Amount Authorised
            0x5F2A, // Transaction Currency Code
            0x82, // AIP
            0x9F1A, // Terminal Country Code
            0x9F03, // Amount Other
            0x9F33, // Terminal Capabilities
            // opt
            0x9F34, // CVM Result
            //0x9F35, // Terminal Type
            0x9F1E, // IFD Serial Number
            0x84, // Dedicated File Name
            0x9F09, // Application Version #
            //0x9F41, // Transaction Sequence Counter
            //0x4F,

            0x5F34, // PAN Sequence Number
            //0x50,//应用标签
            0x9F06,//AppId
            0x9F07,//AppUsageControl
            0x9F53,//TxnCategoryCode
            0};
    // 0X8E, //CVM

    public static final int wOnlineTagsUPI[] = {
            0x9F26, // AC (Application Cryptogram)
            0x9F27, // CID
            0x9F10, // IAD (Issuer Application Data)
            0x9F37, // Unpredicatable Number
            0x9F36, // ATC (Application Transaction Counter)
            0x95, // TVR
            //0x9B,// TSI
            0x9A, // Transaction Date
            0x9C, // Transaction Type
            0x9F02, // Amount Authorised
            0x5F2A, // Transaction Currency Code
            0x82, // AIP
            0x9F1A, // Terminal Country Code
            0x9F03, // Amount Other
            0x9F33, // Terminal Capabilities
            // opt
            0x9F34, // CVM Result
            //0x9F35, // Terminal Type
            0x9F1E, // IFD Serial Number
            0x84, // Dedicated File Name
            0x9F09, // Application Version #
            //0x9F41, // Transaction Sequence Counter
            //0x4F,
            0x5F34, // PAN Sequence Number
            //0x50,//应用标签
            0x9F53,//TxnCategoryCode
            0 };

    /**
     * 冲正
     **/
    public static final int reversal_tag[] = {0x95, // TVR
            0x9F1E, // IFD Serial Number
            0x9F10, // Issuer Application Data
            0x9F36, // Application Transaction Counter
            0xDF31, // 发卡行脚本结果
            0};

    /**
     * 将对象和二进制文件类型相互转换
     * 文件序列化转化为Object
     *
     * @param fileName 文件绝对路径
     * @return Object
     */
    public static Object file2Object(String fileName) throws IOException, ClassNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = new FileInputStream(fileName);
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object object = ois.readObject();
            if (fis != null) {
                fis.close();
            }
            return object;
        }
    }

    /**
     * 将对象和二进制文件类型相互转换
     * 把Object输出到文件
     *
     * @param obj        可序列化的对象
     * @param outputFile 保存的文件
     */
    public static void object2File(Object obj, String outputFile) throws IOException {
        File dir = new File(outputFile);
        if (!dir.exists()) {
            // 在指定的文件夹中创建文件
            dir.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(dir);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            //fos = context.openFileOutput(dir.getName() , Context.MODE_WORLD_READABLE);
            oos.writeObject(obj);
            oos.flush();
            fos.getFD().sync();
        }
    }

    /**
     * 获取Assets配置信息
     *
     * @param context  上下问对象
     * @param fildName 配置文件名
     * @return Properties
     */
    public static Properties lodeConfig(Context context, String fildName) {
        Properties prop = new Properties();
        try {
            prop.load(context.getResources().getAssets().open(fildName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Logger.error("Exception" + e.toString());
            return null;
        }
        return prop;
    }

    /**
     * 根据配置文件名和配置属性获取值
     *
     * @param context  上下文对象
     * @param fildName 配置文件名
     * @param name     配置的属性名
     * @return 对应文件对应属性的String
     */
    public static String lodeConfig(Context context, String fildName, String name) {
        Properties pro = new Properties();
        try {
            pro.load(context.getResources().getAssets().open(fildName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
        return (String) pro.get(name);
    }

    /**
     * 通过code值获取银行名字，获取失败时返回code
     *
     * @param mContext 上下文对象
     * @param code     银行表征码
     * @return 银行名称
     */
    public static String getBankName(Context mContext, String code) {
        Properties pro = lodeConfig(mContext, "bankcodelist.properties");
        if (pro == null) {
            System.out.println("bankcodelist.properties配置文件错误");
            return null;
        }
        String bname;
        try {
            if (!isNullWithTrim(pro.getProperty(code))) {
                bname = new String(pro.getProperty(code).getBytes("ISO-8859-1"), "utf-8");
            } else {
                return code;
            }
        } catch (UnsupportedEncodingException e) {
            Logger.error("Exception" + e.toString());
            return code;
        }
        return bname;
    }

    /**
     * 通过错误code获取具体错误，获取失败时返回code
     *
     * @param mcontext 上下文对象
     * @param code     交易响应码
     * @return 交易响应码含义
     */
    public static String getRspCode(Context mcontext, String code) {
        String tiptitle;
        String tipcontent;
        Properties pro = lodeConfig(mcontext, "props/rspcode.properties");
        if (pro == null) {
            System.out.println("rspcode.properties配置文件错误");
            return null;
        }
        try {
            String prop = pro.getProperty(code);
            String[] propGroup = prop.split(",");
            if (!isNullWithTrim(propGroup[0])) {
                tiptitle = new String(propGroup[0].trim().getBytes("ISO-8859-1"), "utf-8");
            } else {
                tiptitle = code;
            }
            if (!isNullWithTrim(propGroup[1])) {
                tipcontent = new String(propGroup[1].trim().getBytes("ISO-8859-1"), "utf-8");
            } else {
                tipcontent = "";
            }
        } catch (UnsupportedEncodingException e) {
            Logger.error("Exception" + e.toString());
            return code;
        }
        return tiptitle + "\n" + tipcontent;
    }

    /**
     * 将assets下文件拷贝至本应用程序data下
     *
     * @param context  上下文对象
     * @param fileName assets文件名称
     * @return 文件拷贝结果
     */
    public static boolean copyAssetsToData(Context context, String fileName) {
        try {
            AssetManager as = context.getAssets();
            try (InputStream ins = as.open(fileName); OutputStream outs = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE)) {
                String dstFilePath = context.getFilesDir().getAbsolutePath() + "/" + fileName;
                byte[] data = new byte[1 << 20];
                int length = ins.read(data);
                outs.write(data, 0, length);
                outs.flush();
                return true;
            }
        } catch (Exception e) {
            Logger.error("Exception" + e.toString());
            return false;
        }
    }

    /**
     * 获取bundle参数列表
     *
     * @param c       上下文对象
     * @param name    Properties文件名
     * @param proName 属性代号
     * @return 属性集合
     */
    public static String[] getProps(Context c, String name, String proName) {
        Properties pro = new Properties();
        try {
            pro.load(c.getResources().getAssets().open(name));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
        String prop = pro.getProperty(proName);
        if (prop == null) {
            return null;
        }
        String[] results = prop.split(",");
        for (int i = 0; i < results.length; i++) {
            try {
                results[i] = new String(results[i].trim().getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                Logger.error("Exception" + e.toString());
            }
        }
        return results;
    }

    /**
     * 读取Assets文件夹中的图片资源
     *
     * @param context 上下文对象
     * @param path    文件路径
     * @return 图片对象
     */
    public static Bitmap getImageFromAssetsFile(Context context, String path) {
        //获取assets下的资源
        Bitmap image = null;
        try {
            //图片放在img文件夹下
            InputStream is = context.getAssets().open(path);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            Logger.error("Exception" + e.toString());
        }
        return image;
    }

    /**
     * 根据银行ID获取相应图标
     *
     * @param context
     * @param bankId
     * @return
     */
    public static Bitmap getLogoByBankId(Context context, int bankId) {
        return getImageFromAssetsFile(context, TMConstants.BANKID.ASSETS[bankId]);
    }

    /**
     * 获取网络时间
     *
     * @return 网络时间
     * @throws Exception
     */
    public static String getNetworkTime() throws Exception {
        URL url = new URL("http://www.bjtime.cn");//取得资源对象
        URLConnection uc = url.openConnection();//生成连接对象
        uc.connect(); //发出连接
        long ld = uc.getDate(); //取得网站日期时间
        Date date = new Date(ld); //转换为标准时间对象
        //分别取得时间中的小时，分钟和秒，并输出
        return date.getYear() + "-" + date.getMonth() + "-" + date.getDay() + "  " +
                date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
    }

    /**
     * 判断当前网络  
     *
     * @param context 上下文对象
     * @return -1：没有网络 ;
     * 1：WIFI网络 ;
     * 2：wap网络 ;
     * 3：net网络
     */
    public static int getNetype(Context context) {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = 3;
            } else {
                netType = 2;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }

    /**
     * 获取当前系统时间
     *
     * @return 格式化后的时间
     */
    public static String getSysTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 获取当前系统时分秒
     *
     * @return 时分秒
     */
    public static String getHMS() {
        Calendar calendar = Calendar.getInstance();
        return str2int(calendar.get(Calendar.HOUR_OF_DAY)) +
                str2int(calendar.get(Calendar.MINUTE)) +
                str2int(calendar.get(Calendar.SECOND));
    }

    /**
     * 获取当前系统年月日
     *
     * @return 年月日
     */
    public static String getYMD() {
        Calendar calendar = Calendar.getInstance();
        return str2int(calendar.get(Calendar.YEAR)) +
                str2int(calendar.get(Calendar.MONTH)) +
                str2int(calendar.get(Calendar.SECOND));
    }

    /**
     * 时间日期转换为两位格式
     *
     * @param date 时间日期
     * @return 两位日期或时间
     */
    public static String str2int(int date) {
        String temp = String.valueOf(date);
        if (temp.length() == 1) {
            return "0" + temp;
        }
        return temp;
    }

    /**
     * @param str
     * @param format
     * @return
     */
    public static String strToDateFormat(String str, String format) {
        return DateToStr(StrToDate(str), format);
    }

    /**
     * 字符串转换成日期
     *
     * @param str //yyyy-MM-dd HH:mm:ss
     * @return date 日期对象
     */
    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            Logger.error("Exception" + e.toString());
        }
        return date;
    }

    /**
     * 字符串转换成日期
     *
     * @param str          //yyyy-MM-dd HH:mm:ss
     * @param formatString 格式化方式
     * @return
     */
    public static Date StrToDate(String str, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);// "yyyy-MM-dd HH:mm:ss"
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            Logger.error("Exception" + e.toString());
        }
        return date;
    }

    /**
     * 日期转换成字符串
     *
     * @param date 日期对象
     * @return str 格式化方式
     */
    public static String DateToStr(Date date, String formatString) {
        String str = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);// formatString
            str = format.format(date);
        } catch (Exception e) {
            Logger.error("Exception" + e.toString());
        }
        return str;
    }

    /**
     * 获取当前年
     *
     * @return 年份的整型
     */
    public static int getYear() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        return year;
    }

    public static int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static String getMonth() {
        Calendar c = Calendar.getInstance();
        String result;
        int month = c.get(Calendar.MONTH);

        switch (month) {
            case 0: {
                result = "ENE";
                break;
            }
            case 1: {
                result = "FEB";
                break;
            }
            case 2: {
                result = "MAR";
                break;
            }
            case 3: {
                result = "ABR";
                break;
            }
            case 4: {
                result = "MAY";
                break;
            }
            case 5: {
                result = "JUN";
                break;
            }
            case 6: {
                result = "JUL";
                break;
            }
            case 7: {
                result = "AGO";
                break;
            }
            case 8: {
                result = "SEP";
                break;
            }
            case 9: {
                result = "OCT";
                break;
            }
            case 10: {
                result = "NOV";
                break;
            }
            case 11: {
                result = "DIC";
                break;
            }
            default: {
                result = "ERROR";
                break;
            }
        }

        return result;
    }

    public static String getMonth(String Month) {
        String result;
        int month = Integer.parseInt(Month) - 1;

        switch (month) {
            case 0: {
                result = "ENE";
                break;
            }
            case 1: {
                result = "FEB";
                break;
            }
            case 2: {
                result = "MAR";
                break;
            }
            case 3: {
                result = "ABR";
                break;
            }
            case 4: {
                result = "MAY";
                break;
            }
            case 5: {
                result = "JUN";
                break;
            }
            case 6: {
                result = "JUL";
                break;
            }
            case 7: {
                result = "AGO";
                break;
            }
            case 8: {
                result = "SEP";
                break;
            }
            case 9: {
                result = "OCT";
                break;
            }
            case 10: {
                result = "NOV";
                break;
            }
            case 11: {
                result = "DIC";
                break;
            }
            default: {
                result = "ERROR";
                break;
            }
        }

        return result;
    }

    /**
     * 时间格式(交易打单专属打印用)
     *
     * @param date 日期字符串
     * @param time 时间字符串
     * @return yyyy/dd/MM HH:mm:ss
     */
    public static String printStr(String date, String time) {
        String newdate = "";
        if (!isNullWithTrim(date) && !isNullWithTrim(time)) {
            if (time.length() == 5) {
                newdate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8) + "  "
                        + "0" + time.substring(0, 1) + ":" + time.substring(1, 3);
            } else {
                newdate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8) + "  "
                        + time.substring(0, 2) + ":" + time.substring(2, 4);
            }
            return newdate;
        }
        return "    ";

//			if(date.length()==8&&time.length()==6){
//				newdate=date.substring(0,4)+"/"
//						+date.substring(4,6)+"/"
//						+date.substring(6,8)+" "
//						+time.substring(0,2)+":"
//						+time.substring(2,4)+":"
//						+time.substring(4,6);
//			}

    }

    /**
     * 字符串非空判断
     *
     * @param str 字符串
     * @return true则为空，false不为空
     */
    public static boolean isNullWithTrim(String str) {
        return str == null || str.trim().equals("") || str.trim().equals("null");
    }

    /**
     * 给字符串加*(安全卡号专用)
     *
     * @param cardNo 卡号
     * @param prefix 保留 前几位
     * @param suffix 保留 后几位
     * @return 加*后的 String
     */
    public static String getSecurityNum(String cardNo, int prefix, int suffix) {
        StringBuffer cardNoBuffer = new StringBuffer();
        int len = prefix + suffix;
        if (cardNo.length() > len) {
            cardNoBuffer.append(cardNo.substring(0, prefix));
            for (int i = 0; i < cardNo.length() - len; i++) {
                cardNoBuffer.append("X");
            }
            cardNoBuffer.append(cardNo.substring(cardNo.length() - suffix, cardNo.length()));
        }
        return cardNoBuffer.toString();
    }


    public static String getSecurityNum2(String cardNo) {
        StringBuffer cardNoBuffer = new StringBuffer();
        if (cardNo.length() > 0) {
            for (int i = 0; i < 4; i++) {
                cardNoBuffer.append("X");
            }
            cardNoBuffer.append(cardNo.substring(4, cardNo.length() - 2));
            cardNoBuffer.append("XX");
        }
        return cardNoBuffer.toString();
    }

    /**
     * 将double类型数据转换为两位小数并转为字符串
     *
     * @param s double浮点数
     * @return 字符串
     */
    public static String TwoWei(double s) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(s);
    }

    /**
     * 金额存的时候*100  所有取的时候要/100   格式转换
     *
     * @param amount
     * @return 0.00格式
     */
    public static String TwoWei(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        double d = 0;
        if (!isNullWithTrim(amount)) {
            d = Double.parseDouble(amount) / 100;
        }
        return df.format(d);
    }

    /**
     * 时间日期转为标准格式
     *
     * @param date       20160607152954
     * @param oldPattern yyyyMMddHHmmss
     * @param newPattern yyyy-MM-dd HH:mm:ss
     * @return 2016-06-07 15:29:54
     */
    public static String StringPattern(String date, String oldPattern,
                                       String newPattern) {
        if (date == null || oldPattern == null || newPattern == null) {
            return "";
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern); // 实例化模板对象
        SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern); // 实例化模板对象
        Date d = null;
        try {
            d = sdf1.parse(date); // 将给定的字符串中的日期提取出来
        } catch (Exception e) { // 如果提供的字符串格式有错误，则进行异常处理
            Logger.error("Exception" + e.toString()); // 打印异常信息
        }
        return sdf2.format(d);
    }

    /**
     * 字符串对象转换为整型数据
     *
     * @param obj 对象
     * @return 整型数值
     */
    public static int Object2Int(Object obj) {
        return Integer.parseInt((String) obj);
    }

    /**
     * 获取银行详细信息
     *
     * @param bankcode 银行代码
     * @return 银行名称
     */
    public static String getBankInfo(Context c, String bankcode) {
        Properties pro = lodeConfig(c, TMConstants.BANKNAME);
        try {
            if (pro != null) {
                return new String(pro.getProperty(
                        ISOUtil.padright(bankcode, 8, '0')).getBytes("ISO-8859-1"), "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            Logger.error("Exception" + e.toString());
        }
        return null;
    }

    /**
     * 压缩图片
     *
     * @param c   上下文对象
     * @param rid 图片资源ID
     * @return 图片对象
     */
    public static Bitmap compress(Context c, int rid) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.decodeResource(c.getResources(), rid).compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * drawable对象转Bitmap对象
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 将long型数据转成金额字符串
     *
     * @param Amount long 型数据
     * @return 金额字符串
     */
    public static String getStrAmount(long Amount) {
        double f1 = Double.valueOf(Amount + "");
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(f1 / 100);
    }

    /**
     * Converts the dp to px
     */
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }


    /**
     * Converts the sp to px
     */
    public static int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 空判断
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 获取一系列标签集合的内核数据
     *
     * @param src
     * @param totalLen
     * @param tag
     * @param value
     * @param withTL
     * @return
     */
    public static int get_tlv_data(byte[] src, int totalLen, int tag,
                                   byte[] value, boolean withTL) {
        int i, Tag, Len;
        int T;

        if (totalLen == 0) {
            return 0;
        }

        i = 0;
        while (i < totalLen) {
            T = i;

            if ((src[i] & 0x1f) == 0x1f) {
                Tag = ISOUtil.byte2int(src, i, 2);
                i += 2;
            } else {
                Tag = ISOUtil.byte2int(new byte[]{src[i++]});
            }

            Len = ISOUtil.byte2int(new byte[]{src[i++]});
            if ((Len & (byte) 0x80) != 0) {
                int lenL = Len & 3;
                Len = ISOUtil.byte2int(src, i, lenL);
                i += lenL;
            }
            // 找到
            if (tag == Tag) {
                //包含Tag和Len
                if (withTL) {
                    Len = Len + (i - T);
                    System.arraycopy(src, T, value, 0, Len);
                    return Len;
                    //不包含Tag和Len
                } else {
                    System.arraycopy(src, i, value, 0, Len);
                    return Len;
                }
            } else {
                i += Len;
            }
        }
        return 0;
    }

    /**
     * 用标签从内核中获得一个数据
     *
     * @param iTag TLV数据的标签
     * @param data 返回的数据
     * @return 真实长度
     */
    public static int get_tlv_data_kernal(int iTag, byte[] data) {
        IEMVHandler handler = EMVHandler.getInstance();
        int len = 0;
        byte[] Tag;
        if (iTag < 0x100) {
            Tag = new byte[1];
            Tag[0] = (byte) iTag;
        } else {
            Tag = new byte[2];
            Tag[0] = (byte) (iTag >> 8);
            Tag[1] = (byte) iTag;
        }
        Logger.debug("Tag = " + ISOUtil.hexString(Tag));
        if (handler.checkDataElement(Tag) == 0) {
            try {
                byte[] result = handler.getDataElement(Tag);
                Logger.debug("get_tlv_data_kernal result = " + ISOUtil.hexString(result));
                System.arraycopy(result, 0, data, 0, result.length);
                len = result.length;
            } catch (SDKException e) {
                Logger.error("Exception" + e.toString());
            }
            //脚本结果
        } else if (iTag == 0xDF31) {
            byte[] result = handler.getScriptResult();
            if (result != null) {
                System.arraycopy(result, 0, data, 0, result.length);
                len = result.length;
            }
        }
        return len;
    }

    /**
     * 从内核拿一组tag标签的数据，包装成一串tlv
     *
     * @param iTags
     * @param dest
     * @return
     */
    public static int pack_tags(int[] iTags, byte[] dest) {
        int i, iTag_len, len;
        byte[] Tag = new byte[2];
        int offset = 0;
        byte[] ptr = new byte[256];

        i = 0;
        while (iTags[i] != 0) {

            if (iTags[i] < 0x100) {
                iTag_len = 1;
                Tag[0] = (byte) iTags[i];
            } else {
                iTag_len = 2;
                Tag[0] = (byte) (iTags[i] >> 8);
                Tag[1] = (byte) iTags[i];
            }

            len = get_tlv_data_kernal(iTags[i], ptr);
            if (len > 0) {
                System.arraycopy(Tag, 0, dest, offset, iTag_len);// 拷标签
                offset += iTag_len;

                if (len < 128) {
                    dest[offset++] = (byte) len;
                } else if (len < 256) {
                    dest[offset++] = (byte) 0x81;
                    dest[offset++] = (byte) len;
                }

                System.arraycopy(ptr, 0, dest, offset, len);
                offset += len;
            }

            i++;
        }
        return offset;
    }

    /**
     * pack a _tlv data
     *
     * @param result out
     * @param tag
     * @param len
     * @param value  in
     * @return
     */
    public static int pack_tlv_data(byte[] result, int tag, int len,
                                    byte[] value, int valueOffset) {
        byte[] temp = null;
        int offset = 0;

        if (len == 0 || value == null || result == null) {
            return 0;
        }

        temp = result;
        if (tag > 0xff) {
            temp[offset++] = (byte) (tag >> 8);
            temp[offset++] = (byte) tag;
        } else {
            temp[offset++] = (byte) tag;
        }

        if (len < 128) {
            temp[offset++] = (byte) len;
        } else if (len < 256) {
            temp[offset++] = (byte) 0x81;
            temp[offset++] = (byte) len;
        } else {
            temp[offset++] = (byte) 0x82;
            temp[offset++] = (byte) (len >> 8);
            temp[offset++] = (byte) len;
        }
        System.arraycopy(value, valueOffset, temp, offset, len);

        return offset + len;
    }

    /**
     * 根据AID查询内卡还是外卡
     *
     * @param rid
     * @return
     */
    public static String getIssureByRid(String rid) {
        String cardCode = null;
        if (rid.length() < 10) {
            return "CUP";
        }
        if (rid.length() > 10) {
            cardCode = rid.substring(0, 10);
        } else {
            cardCode = rid;
        }

        if (cardCode.equals("A000000003")) {
            return "VIS";
        }
        if (cardCode.equals("A000000004")) {
            return "MCC";
        }
        if (cardCode.equals("A000000065")) {
            return "JCB";
        }
        if (cardCode.equals("A000000025")) {
            return "AEX";
        }
        return "CUP";
    }

    /**
     * 取时分秒
     *
     * @return
     */
    public static String getLocalTime() {
        return DateToStr(new Date(), "HHmmss");
    }

    /**
     * 取月天
     *
     * @return
     */
    public static String getLocalDate() {
        return DateToStr(new Date(), "MMdd");
    }

    /**
     * 取月天
     *
     * @return
     */
    public static String getLocalDate2() {
        return DateToStr(new Date(), "yyyyMMdd");
    }

    /**
     * 取年月
     *
     * @return
     */
    public static String getExpDate() {
        return DateToStr(new Date(), "yyMM");
    }


    public static String getStatus(int id, int language) {
        String[] pro = null;
        try {
            pro = PAYUtils.getProps(PaySdk.getInstance().getContext(), "lan/lan_en.properties", String.valueOf(id));
        } catch (PaySdkException e) {
            Logger.error("Exception" + e.toString());
        }
        if (pro != null) {
            return pro[0];
        }
        return null;
    }

    public static byte[] getAssertFileData(String fileName) {
        Context context = null;
        try {
            context = PaySdk.getInstance().getContext();
        } catch (PaySdkException e) {
            Logger.error("Exception" + e.toString());
        }
        assert context != null;
        try (AssetManager as = context.getAssets(); InputStream ins = as.open(fileName)) {
            byte[] data = new byte[1 << 20];
            int length = ins.read(data);
            byte[] out = new byte[length];
            System.arraycopy(data, 0, out, 0, length);
            return out;
        } catch (Exception e) {
            Logger.error("Exception" + e.toString());
            return null;
        }
    }

    /**
     * @param value
     * @return
     */
    public static boolean stringToBoolean(String value) {
        if (value.equals("1"))
            return true;
        else
            return false;
    }

    /**
     * @param retVal
     * @return
     */
    public static String selectRspCode(int retVal, String code39) {
        String ret;
        switch (retVal) {
            case T_success:
                ret = OK;
                break;
            case T_user_cancel_input:
            case T_user_cancel_operation:
            case T_socket_err:
            case T_wait_timeout:
            case T_gen_2_ac_fail:
                ret = ERROR_PROCESO;
                break;
            case 2:
                ret = INICIO_DIA;
                break;
            default:
                if (code39 == null){
                    ret = ISOUtil.spacepad("20", 2);
                }else {
                    ret = ISOUtil.spacepad("00", 2);
                }
                break;
        }

        return ret;
    }

    public static String selectRspMsg(int retVal) {
        String ret;
        switch (retVal) {
            case T_success:
                ret = AUTORIZADO;
                break;
            case 2:
                ret = INICIO_DIA_MSG;
                break;
            default:
                ret = ERROR_EN_TRAMA;
                break;
        }

        return ret;
    }

    public static String entryModePP(int val, boolean isFallback) {
        String inputMode = "";

        switch (val) {
            case ENTRY_MODE_HAND:
                inputMode = ISOUtil.spacepadRight(HDL, 2);
                break;
            case ENTRY_MODE_MAG:
                if (isFallback) {
                    inputMode = ISOUtil.spacepadRight(FBI, 2);
                } else {
                    inputMode = ISOUtil.spacepadRight(MAG, 2);
                }
                break;
            case ENTRY_MODE_ICC:
                inputMode = ISOUtil.spacepadRight(ICC, 2);
                break;
            case ENTRY_MODE_NFC:
                inputMode = ISOUtil.spacepadRight(CTL, 2);
                break;
            default:
                inputMode = ISOUtil.spacepadRight("", 2);
                break;
        }
        return inputMode;
    }

    public static void dateTime(String date, String time){
        if (time != null && date != null){
            StringBuilder dateTime = new StringBuilder();
            try {
                dateTime.append(PAYUtils.getYear());
                dateTime.append(date);
                dateTime.append(time);

                RealTimeClock.set(dateTime.toString());
            } catch (SDKException e) {
                e.printStackTrace();
            }
        }
    }
}
