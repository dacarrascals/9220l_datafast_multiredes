package com.datafast.inicializacion.init_emv;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.inicializacion.trans_init.trans.dbHelper;
import com.pos.device.emv.CAPublicKey;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.desert.newpos.payui.UIUtils;

import static com.datafast.definesDATAFAST.DefinesDATAFAST.CAKEY;
import static com.datafast.inicializacion.trans_init.Init.NAME_DB;
import static org.jpos.stis.Util.hex2byte;
import static org.jpos.stis.Util.hexString;


/**
 *
 * @author francisco
 */
public class CAPK_ROW {

    private String subType;
    private String len;
    private String KeyIdx;
    private String RID;
    private String Exponent;
    private String KeySize;
    private String Key;
    private String ExpiryDate;
    private String EffectDate;
    private String Chksum;
    private String sha1;
    //private String hash_signed;
    private boolean showDebug = false;

    public static String[] fields = new String[]{
            "subType",
            "len",
            "KeyIdx",
            "RID",
            "Exponent",
            "KeySize",
            "Key",
            "ExpiryDate",
            "EffectDate",
            "Chksum",
            "sha1"
            //"hash_signed"
    };

    public static String[] fieldsCAKEY = new String[]{
            "RID",
            "KeyIdx",
            "Exponent",
            "subType",
            "len",
            "KeySize",
            "Key"};

    private static CAPK_ROW capkRow;

    public void setCAPK_ROW(String column, String value) {
        switch (column) {
            case "subType":
                setSubType(value);
                break;
            case "len":
                setLen(value);
                break;
            case "KeyIdx":
                setKeyIdx(value);
                break;
            case "RID":
                setRID(value);
                break;
            case "Exponent":
                setExponent(value);
                break;
            case "KeySize":
                setKeySize(value);
                break;
            case "Key":
                setKey(value);
                break;
            case "ExpiryDate":
                setExpiryDate(value);
                break;
            case "EffectDate":
                setEffectDate(value);
                break;
            case "Chksum":
                setChksum(value);
                break;
            case "sha1":
                setSha1(value);
                break;
            /*case "hash_signed":
                setHash_signed(value);
                break;*/
            default:
                break;
        }

    }

    public boolean checkSigned(Context context) {
        StringBuilder textToVerify = new StringBuilder();
        textToVerify.append(this.subType);
        textToVerify.append(this.len);
        textToVerify.append(this.KeyIdx);
        textToVerify.append(this.RID);
        textToVerify.append(this.Exponent);
        textToVerify.append(this.KeySize);
        textToVerify.append(this.Key);
        textToVerify.append(this.ExpiryDate);
        textToVerify.append(this.EffectDate);
        textToVerify.append(this.Chksum);
        textToVerify.append(this.sha1);
        //return RSAPos.VerifySignHash(loadPublicKey("publickey", context), textToVerify.toString(), hex2byte(this.hash_signed));
        return true;
    }

    public void clearCAPK_ROW() {
        for (String s : CAPK_ROW.fields) {
            setCAPK_ROW(s, "");
        }
    }

    public boolean selectCAPK_ROW(Context context) {
        boolean ok = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : CAPK_ROW.fields) {
            sql.append(s);
            if (counter++ < CAPK_ROW.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from capks");
        sql.append(";");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;
            IEMVHandler emvHandler = EMVHandler.getInstance();
            while (!cursor.isAfterLast()) {
                clearCAPK_ROW();
                indexColumn = 0;
                for (String s : CAPK_ROW.fields) {
                    setCAPK_ROW(s, cursor.getString(indexColumn++));
                }

                if (showDebug) {
                    Log.d("emvinit", "\n" + this.ToString());
                    Log.d("emvinit", "CAPK_ROW checkSigned: " + (this.checkSigned(context) ? "true" : "false"));
                    System.out.println("");
                    System.out.println("");
                    System.out.println("");
                }

                try {

                    CAPublicKey caPublicKey = new CAPublicKey();
                    caPublicKey.setRID(hex2byte(this.getRID()));
                    caPublicKey.setIndex(Integer.parseInt(this.getKeyIdx(), 16));

                    int moduleLength = Integer.parseInt(this.getKeySize(), 16);
                    caPublicKey.setLenOfModulus(moduleLength);
                    byte[] key = hex2byte(this.getKey());
                    byte[] module = new byte[moduleLength];
                    System.arraycopy(key, 0, module, 0, moduleLength);
                    caPublicKey.setModulus(module);


                    byte[] exponent = getExp(hex2byte(this.getExponent()));
                    if (caPublicKey != null && exponent != null) {
                        caPublicKey.setLenOfExponent(exponent.length);
                        caPublicKey.setExponent(exponent);
                    }
                    byte[] expDate = new byte[3];
                    byte[] date = hex2byte(this.getExpiryDate());
                    byte[] lastDayMonth = lastDayOfMonth(date);
                    System.arraycopy(date, 1, expDate, 0, 2);
                    System.arraycopy(lastDayMonth, 0, expDate, 2, 1);
                    caPublicKey.setExpirationDate(expDate);


                    caPublicKey.setChecksum(hex2byte(this.getSha1()));

                    int rta = emvHandler.addCAPublicKey(caPublicKey);

                    if (showDebug)
                        Log.d("emvinit", "load capk index:  " + this.getKeyIdx() + " - Result: " + String.valueOf(rta));

                    ok = true;
                    cursor.moveToNext();
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "Error al cargar CAPK", Toast.LENGTH_SHORT);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();
        return ok;
    }

    /**
     * create file CAKEY used in the callback CTL
     * @param context
     * @return
     */
    public boolean selectDataCAPK_ROW(Context context) {
        boolean ok = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : CAPK_ROW.fieldsCAKEY) {
            sql.append(s);
            if (counter++ < CAPK_ROW.fieldsCAKEY.length) {
                sql.append(",");
            }
        }
        sql.append(" from capks");
        sql.append(";");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;

            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("CTL_Cakps", Context.MODE_PRIVATE);
            File file = new File(directory, CAKEY);
            FileOutputStream out = new FileOutputStream(file);

            while (!cursor.isAfterLast()) {
                clearCAPK_ROW();
                indexColumn = 0;
                for (String s : CAPK_ROW.fieldsCAKEY) {
                    setCAPK_ROW(s, cursor.getString(indexColumn++));
                }

                try {
                    //5bytes RID
                    out.write(hex2byte(this.RID));
                    out.flush();

                    //1byte index
                    out.write(hex2byte(this.KeyIdx));
                    out.flush();

                    // 1 byte exponent
                    int len_of_exponent = getExpCTL(hex2byte(this.getExponent()));

                    if (len_of_exponent == 1){
                        byte[] len = {0x01};
                        out.write(len);
                    }else if (len_of_exponent == 3){
                        byte[] len = {0x03};
                        out.write(len);
                    }
                    out.flush();

                    // 1byte module length
                    //int moduleLength = Integer.parseInt(this.getKeySize(), 16);
                    byte[] auxData = hex2byte(this.getKeySize());
                    out.write(auxData[1]);
                    out.flush();

                    // exponent value
                    if (len_of_exponent == 1){
                        byte[] len = {0x03};
                        out.write(len);
                    }else if (len_of_exponent == 3){
                        byte[] len = {0x01, 0x00, 0x01};
                        out.write(len);
                    }
                    out.flush();

                    // module value
                    auxData = hex2byte(this.getKey());
                    int lenKey = Integer.parseInt(this.getKeySize(), 16);
                    byte[] key = new byte[lenKey];
                    System.arraycopy(auxData, 0, key, 0, lenKey);

                    out.write(key);
                    out.flush();

                }catch (IOException ioe) {
                    Log.e("Error File CAKEY", ioe.getMessage());
                }

                ok = true;
                cursor.moveToNext();
            }
            cursor.close();
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        databaseAccess.closeDb();
        return ok;
    }

    private byte[] lastDayOfMonth(byte[] date) {
        byte[] ndays = new byte[]{(byte) 0x00, (byte) 0x31, (byte) 0x28, (byte) 0x31, (byte) 0x30, (byte) 0x31, (byte) 0x30, (byte) 0x31,
                (byte) 0x31, (byte) 0x30, (byte) 0x31, (byte) 0x30, (byte) 0x31};

        byte[] year = new byte[2];
        byte[] month = new byte[1];
        byte[] ret = new byte[1];
        System.arraycopy(date, 0, year, 0, 2);
        System.arraycopy(date, 2, month, 0, 1);

        int yearI = Integer.parseInt(hexString(year));
        int monthI = Integer.parseInt(hexString(month));

        ret[0] = ndays[monthI];
        if (monthI == 0x02) {
            if ((yearI % 4 == 0) && !(yearI % 100 == 0)) {
                ret[0]++; //leap year
            } else if (yearI % 400 == 0) {
                ret[0]++; //leap year
            }
        }

        return ret;

    }

    //as I know, the exponent only have two types
    //One is len 1, exponent =0x03
    //second id len 3, exponent = 0x01,0x00,0x01
    private byte[] getExp(byte[] source) {
        int lenModule = 4;
        int index = 0;
        if (source[0] != 0x00) {
            return null;
        }

        while (lenModule > 0) {
            if (source[index++] == 0x00) {
                lenModule--;
            } else {
                break;
            }
        }

        byte[] exponent = new byte[lenModule];
        if (lenModule > 0) {
            System.arraycopy(source, 4 - lenModule, exponent, 0, lenModule);
        }

        return exponent;
    }


    //as I know, the exponent only have two types
    //One is len 1, exponent =0x03
    //second id len 3, exponent = 0x01,0x00,0x01
    private int getExpCTL(byte[] source) {
        int lenModule = 4;
        int index = 0;
        if (source[0] != 0x00) {
            return 0;
        }

        while (lenModule > 0) {
            if (source[index++] == 0x00) {
                lenModule--;
            } else {
                break;
            }
        }

        byte[] exponent = new byte[lenModule];
        if (lenModule > 0) {
            System.arraycopy(source, 4 - lenModule, exponent, 0, lenModule);
        }

        return lenModule;
    }

    private CAPK_ROW() {
    }

    public static CAPK_ROW getSingletonInstance() {
        if (capkRow == null) {
            capkRow = new CAPK_ROW();
        } else {
            System.out.println("No se puede crear otro objeto, ya existe");
        }
        return capkRow;
    }

    public String ToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CAPK_ROW: \n");

        sb.append("\t");
        sb.append("Subtype: ");
        sb.append(this.subType);
        sb.append("\n");

        sb.append("\t");
        sb.append("len: ");
        sb.append(this.len);
        sb.append("\n");

        sb.append("\t");
        sb.append("KeyIdx: ");
        sb.append(this.KeyIdx);
        sb.append("\n");

        sb.append("\t");
        sb.append("RID: ");
        sb.append(this.RID);
        sb.append("\n");

        sb.append("\t");
        sb.append("Exponent: ");
        sb.append(this.Exponent);
        sb.append("\n");

        sb.append("\t");
        sb.append("KeySize: ");
        sb.append(this.KeySize);
        sb.append("\n");

        sb.append("\t");
        sb.append("Key: ");
        sb.append(this.Key);
        sb.append("\n");

        sb.append("\t");
        sb.append("ExpiryDate: ");
        sb.append(this.ExpiryDate);
        sb.append("\n");

        sb.append("\t");
        sb.append("EffectDate: ");
        sb.append(this.EffectDate);
        sb.append("\n");

        sb.append("\t");
        sb.append("Chksum: ");
        sb.append(this.Chksum);
        sb.append("\n");

        sb.append("\t");
        sb.append("sha1: ");
        sb.append(this.sha1);
        sb.append("\n");

        /*sb.append("\t");
        sb.append("hash_signed: ");
        sb.append(this.hash_signed);
        sb.append("\n");*/

        return sb.toString();

    }

    /*public String getHash_signed() {
        return hash_signed;
    }

    public void setHash_signed(String hash_signed) {
        this.hash_signed = hash_signed;
    }*/

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }

    public String getKeyIdx() {
        return KeyIdx;
    }

    public void setKeyIdx(String KeyIdx) {
        this.KeyIdx = KeyIdx;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getExponent() {
        return Exponent;
    }

    public void setExponent(String Exponent) {
        this.Exponent = Exponent;
    }

    public String getKeySize() {
        return KeySize;
    }

    public void setKeySize(String KeySize) {
        this.KeySize = KeySize;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String ExpiryDate) {
        this.ExpiryDate = ExpiryDate;
    }

    public String getEffectDate() {
        return EffectDate;
    }

    public void setEffectDate(String EffectDate) {
        this.EffectDate = EffectDate;
    }

    public String getChksum() {
        return Chksum;
    }

    public void setChksum(String Chksum) {
        this.Chksum = Chksum;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

}
