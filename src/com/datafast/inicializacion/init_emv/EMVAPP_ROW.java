package com.datafast.inicializacion.init_emv;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.inicializacion.trans_init.trans.dbHelper;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.emv.TerminalAidInfo;

import org.jpos.stis.TLV_parsing;

import cn.desert.newpos.payui.UIUtils;

import static com.datafast.inicializacion.trans_init.Init.NAME_DB;
import static org.jpos.stis.Util.hex2byte;



/**
 *
 * @author francisco
 */
public class EMVAPP_ROW {

    private String subType;
    private String len;
    private String eType;
    private String eBitField;
    private String eRSBThresh;
    private String eRSTarget;
    private String eRSBMax;
    private String eTACDenial;
    private String eTACOnline;
    private String eTACDefault;
    private String eACFG;
    //private String hash_signed;
    private boolean showDebug = false;

    public static String[] fields = new String[]{
            "subType",
            "len",
            "eType",
            "eBitField",
            "eRSBThresh",
            "eRSTarget",
            "eRSBMax",
            "eTACDenial",
            "eTACOnline",
            "eTACDefault",
            "eACFG"
            //"hash_signed"
    };

    private static EMVAPP_ROW emvappRow;

    public void setEMVAPP_ROW(String column, String value) {
        switch (column) {
            case "subType":
                setSubType(value);
                break;
            case "len":
                setLen(value);
                break;
            case "eType":
                seteType(value);
                break;
            case "eBitField":
                seteBitField(value);
                break;
            case "eRSBThresh":
                seteRSBThresh(value);
                break;
            case "eRSTarget":
                seteRSTarget(value);
                break;
            case "eRSBMax":
                seteRSBMax(value);
                break;
            case "eTACDenial":
                seteTACDenial(value);
                break;
            case "eTACOnline":
                seteTACOnline(value);
                break;
            case "eTACDefault":
                seteTACDefault(value);
                break;
            case "eACFG":
                seteACFG(value);
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
        textToVerify.append(this.eType);
        textToVerify.append(this.eBitField);
        textToVerify.append(this.eRSBThresh);
        textToVerify.append(this.eRSTarget);
        textToVerify.append(this.eRSBMax);
        textToVerify.append(this.eTACDenial);
        textToVerify.append(this.eTACOnline);
        textToVerify.append(this.eTACDefault);
        textToVerify.append(this.eACFG);
        //return RSAPos.VerifySignHash(loadPublicKey("publickey", context), textToVerify.toString(), hex2byte(this.hash_signed));
        return true;
    }

    public void clearEMVAPP_ROW() {
        for (String s : EMVAPP_ROW.fields) {
            setEMVAPP_ROW(s, "");
        }
    }

    public boolean selectEMVAPP_ROW(Context context) {
        boolean ok = false;
        dbHelper databaseAccess = new dbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : EMVAPP_ROW.fields) {
            sql.append(s);
            if (counter++ < EMVAPP_ROW.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from emvapps");
        sql.append(";");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;
            IEMVHandler emvHandler = EMVHandler.getInstance();
            while (!cursor.isAfterLast()){
                clearEMVAPP_ROW();
                indexColumn = 0;
                for (String s : EMVAPP_ROW.fields) {
                    setEMVAPP_ROW(s, cursor.getString(indexColumn++));
                }

                if (showDebug) {
                    Log.d("emvinit", "\n" + this.ToString());
                    Log.d("emvinit", "EMVAPP_ROW checkSigned: " + (this.checkSigned(context) ? "true" : "false"));
                    System.out.println("");
                    System.out.println("");
                    System.out.println("");
                }

                try {

                    TLV_parsing tlvParsing = new TLV_parsing(geteACFG());

                    if (showDebug) {
                        Log.d("emvinit: ", "\n" + tlvParsing.getAllTags());
                    }

                    TerminalAidInfo terminalAidInfo = new TerminalAidInfo();
                    terminalAidInfo.setAIDdLength(tlvParsing.getValueB(0x9f06).length);
                    terminalAidInfo.setAId(tlvParsing.getValueB(0x9f06));


                    byte[] tmp = hex2byte(this.geteBitField());
                    if ((tmp[0] &= 0x01) == 0x01) {
                        terminalAidInfo.setSupportPartialAIDSelect(false);//disable
                    } else {
                        terminalAidInfo.setSupportPartialAIDSelect(true);//enable
                    }

                    terminalAidInfo.setApplicationPriority(0);
                    terminalAidInfo.setTargetPercentage(0);
                    terminalAidInfo.setMaximumTargetPercentage(0);


                    if (!tlvParsing.getValue(0x9f1b).equals("NA")) {
                        terminalAidInfo.setTerminalFloorLimit(Integer.parseInt(tlvParsing.getValue(0x9f1b)));
                    }

                    terminalAidInfo.setThresholdValue(Integer.parseInt(this.geteRSBMax()));
                    terminalAidInfo.setTerminalActionCodeDenial(hex2byte(this.geteTACDenial()));
                    terminalAidInfo.setTerminalActionCodeOnline(hex2byte(this.geteTACOnline()));
                    terminalAidInfo.setTerminalActionCodeDefault(hex2byte(this.geteTACDefault()));

                    if (!tlvParsing.getValue(0x9f01).equals("NA")) {
                        terminalAidInfo.setAcquirerIdentifier(tlvParsing.getValueB(0x9f01));
                    }

                    byte[] ddol = tlvParsing.getValueB(0x9f49);
                    if (ddol != null) {
                        terminalAidInfo.setLenOfDefaultDDOL(ddol.length);
                        terminalAidInfo.setDefaultDDOL(ddol);
                    }


                    byte[] tdol = tlvParsing.getValueB(0x0097);
                    if (tdol != null) {
                        terminalAidInfo.setLenOfDefaultTDOL(tdol.length);
                        terminalAidInfo.setDefaultTDOL(tdol);
                    }


                    byte[] applicationVersion = tlvParsing.getValueB(0x9F09);
                    if (applicationVersion != null) {
                        terminalAidInfo.setApplicationVersion(applicationVersion);
                    }

                    int rta = emvHandler.addAidInfo(terminalAidInfo);

                    if (showDebug)
                        Log.d("emvinit", "load aid, aid: " + tlvParsing.getValue(0x9f06) + " - Result: " + String.valueOf(rta));

                    ok = true;
                    cursor.moveToNext();
                }catch (Exception e) {
                    //permite continuar con la inyeccion de la siguente configuracion de app
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher_1, "Error al cargar AID", Toast.LENGTH_SHORT);
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

    private EMVAPP_ROW() {
    }

    public static EMVAPP_ROW getSingletonInstance() {
        if (emvappRow == null) {
            emvappRow = new EMVAPP_ROW();
        } else {
            System.out.println("No se puede crear otro objeto, ya existe");
        }
        return emvappRow;
    }

    public String ToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EMVAPP_ROW: \n");

        sb.append("\t");
        sb.append("Subtype: ");
        sb.append(this.subType);
        sb.append("\n");

        sb.append("\t");
        sb.append("len: ");
        sb.append(this.len);
        sb.append("\n");

        sb.append("\t");
        sb.append("eType: ");
        sb.append(this.eType);
        sb.append("\n");

        sb.append("\t");
        sb.append("eBitField: ");
        sb.append(this.eBitField);
        sb.append("\n");

        sb.append("\t");
        sb.append("eRSBThresh: ");
        sb.append(this.eRSBThresh);
        sb.append("\n");

        sb.append("\t");
        sb.append("eRSTarget: ");
        sb.append(this.eRSTarget);
        sb.append("\n");

        sb.append("\t");
        sb.append("eRSBMax: ");
        sb.append(this.eRSBMax);
        sb.append("\n");

        sb.append("\t");
        sb.append("eTACDenial: ");
        sb.append(this.eTACDenial);
        sb.append("\n");

        sb.append("\t");
        sb.append("eTACOnline: ");
        sb.append(this.eTACOnline);
        sb.append("\n");

        sb.append("\t");
        sb.append("eTACDefault: ");
        sb.append(this.eTACDefault);
        sb.append("\n");

        sb.append("\t");
        sb.append("eACFG: ");
        sb.append(this.eACFG);
        sb.append("\n");

       /* sb.append("\t");
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

    public String geteType() {
        return eType;
    }

    public void seteType(String eType) {
        this.eType = eType;
    }

    public String geteBitField() {
        return eBitField;
    }

    public void seteBitField(String eBitField) {
        this.eBitField = eBitField;
    }

    public String geteRSBThresh() {
        return eRSBThresh;
    }

    public void seteRSBThresh(String eRSBThresh) {
        this.eRSBThresh = eRSBThresh;
    }

    public String geteRSTarget() {
        return eRSTarget;
    }

    public void seteRSTarget(String eRSTarget) {
        this.eRSTarget = eRSTarget;
    }

    public String geteRSBMax() {
        return eRSBMax;
    }

    public void seteRSBMax(String eRSBMax) {
        this.eRSBMax = eRSBMax;
    }

    public String geteTACDenial() {
        return eTACDenial;
    }

    public void seteTACDenial(String eTACDenial) {
        this.eTACDenial = eTACDenial;
    }

    public String geteTACOnline() {
        return eTACOnline;
    }

    public void seteTACOnline(String eTACOnline) {
        this.eTACOnline = eTACOnline;
    }

    public String geteTACDefault() {
        return eTACDefault;
    }

    public void seteTACDefault(String eTACDefault) {
        this.eTACDefault = eTACDefault;
    }

    public String geteACFG() {
        return eACFG;
    }

    public void seteACFG(String eACFG) {
        this.eACFG = eACFG;
    }

}
