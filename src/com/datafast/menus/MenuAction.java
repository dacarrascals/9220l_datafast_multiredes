package com.datafast.menus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.provider.Settings;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.datafast.definesDATAFAST.DefinesDATAFAST;
import com.datafast.inicializacion.trans_init.Init;
import com.datafast.keys.PwMasterKey;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.datafast.printer.PrintParameter;
import com.datafast.tools.UtilNetwork;
import com.datafast.tools.WifiSettings;
import com.datafast.tools_bacth.ToolsBatch;
import com.datafast.transactions.callbacks.makeInitCallback;
import com.datafast.transactions.callbacks.waitPrintReport;
import com.datafast.transactions.callbacks.waitSeatleReport;
import com.datafast.transactions.common.CommonFunctionalities;
import com.newpos.libpay.device.printer.PrintRes;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.trans.translog.TransLogReverse;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.printer.Printer;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cn.desert.newpos.payui.setting.ui.simple.CommunSettings;
import cn.desert.newpos.payui.transrecord.HistoryTrans;

import static com.android.newpos.pay.StartAppDATAFAST.batteryStatus;
import static com.android.newpos.pay.StartAppDATAFAST.isInit;
import static com.android.newpos.pay.StartAppDATAFAST.paperStatus;
import static com.android.newpos.pay.StartAppDATAFAST.tconf;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREAUTO;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_PREVOUCHER;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_REVERSE;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_PAGO_PREVOUCHER;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_PREAUTO_PANTALLA;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_PRE_VOUCHER;
import static com.datafast.definesDATAFAST.DefinesDATAFAST.ITEM_REPORTE_DETALLADO;
import static com.datafast.menus.menus.idAcquirer;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;
import static com.newpos.libpay.trans.Trans.idLote;

//import static com.datafast.menus.menus.acquirerRow;
//import static com.datafast.menus.menus.issuerRow;

public class MenuAction {

    private Context context;
    private String tipoDeMenu;
    private String Estado;

    private Dialog mDialog;

    //Claves para cuando no esta inicializado el POS
    private final String TERMINAL_PWD = "000000";
    private final String COMERCIO_PWD = "0000";

    public static String JUMP_KEY = "JUMP_KEY";

    public static waitPrintReport callbackPrint;
    public static waitSeatleReport callBackSeatle;
    public static makeInitCallback makeInitCallback;
    protected ProcessPPFail processPPFail;

    public MenuAction(Context context, String tipoDeMenu) {
        this.context = context;
        this.tipoDeMenu = tipoDeMenu;
        MasterControl.setMcontext(context);
    }
    public void SelectAction() {
        Intent intent = new Intent();

        switch (tipoDeMenu) {
            case DefinesDATAFAST.ITEM_VENTA:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.NN:
                processPPFail = new ProcessPPFail(context, null);
                processPPFail.cmdCancel(PP, 0);
                UIUtils.toast((Activity) context, R.drawable.ic_launcher, "TRANSACCION NO DISPONIBLE", Toast.LENGTH_SHORT);
                break;
            case DefinesDATAFAST.ITEM_DIFERIDO:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[26]);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.ITEM_PAGOS_ELECTRONICOS:
                if (ISOUtil.stringToBoolean(tconf.getTRANSACCION_PAGOS_ELECTRONICOS())){
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(context, MasterControl.class);
                    intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[27]);
                    context.startActivity(intent);
                }else{
                    processPPFail = new ProcessPPFail(context, null);
                    processPPFail.cmdCancel(PP, 0);
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, "LOS PAGOS ELECTRONICOS \n NO ESTAN HABILITADOS", Toast.LENGTH_SHORT);
                }
                break;
            case DefinesDATAFAST.ITEM_TRANS_PRE_AUT:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[28]);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.ITEM_AMPLIACION:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[29]);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.ITEM_CONFIRMACION:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[30]);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.ITEM_ANULACION_PRE_AUT:
                if (isInit)
                    maintainPwd("CLAVE DE COMERCIO", tconf.getCLAVE_COMERCIO(), DefinesDATAFAST.ITEM_ANULACION_PRE_AUT, 4);
                else
                    maintainPwd("CLAVE DE COMERCIO", COMERCIO_PWD, DefinesDATAFAST.ITEM_ANULACION_PRE_AUT, 4);

                break;
            case DefinesDATAFAST.ITEM_REIMPRESION_PRE_AUT:
                if (isInit)
                    maintainPwd("CLAVE DE COMERCIO", tconf.getCLAVE_COMERCIO(), DefinesDATAFAST.ITEM_REIMPRESION_PRE_AUT, 4);
                else
                    maintainPwd("CLAVE DE COMERCIO", COMERCIO_PWD, DefinesDATAFAST.ITEM_REIMPRESION_PRE_AUT, 4);
                break;
            case DefinesDATAFAST.ITEM_ECHO_TEST:
                if (isInit) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(context, MasterControl.class);
                    intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[20]);
                    context.startActivity(intent);
                }else{
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, "Debe Inicializar POS!", Toast.LENGTH_LONG);
                }

                break;
            case DefinesDATAFAST.ITEM_ANULACION:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[22]);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.ITEM_DEPOSITO:
                if (isInit)
                    maintainPwd("CLAVE DE COMERCIO", tconf.getCLAVE_COMERCIO(), DefinesDATAFAST.ITEM_DEPOSITO, 4);
                else
                    maintainPwd("CLAVE DE COMERCIO", COMERCIO_PWD, DefinesDATAFAST.ITEM_DEPOSITO, 4);

                break;
            case DefinesDATAFAST.ITEM_POLARIS:
                if (isInit)
                    maintainPwd("CLAVE TECNICO", tconf.getCLAVE_TECNICO(), DefinesDATAFAST.ITEM_POLARIS, 6);
                else
                    maintainPwd("CLAVE TECNICO", TERMINAL_PWD, DefinesDATAFAST.ITEM_POLARIS, 6);

                break;
            case DefinesDATAFAST.ITEM_TEST:
                if ((batteryStatus.getLevelBattery() <= 8) && (!batteryStatus.isCharging())) {
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_BATTERY, Toast.LENGTH_SHORT);
                } else if (paperStatus.getRet() == Printer.PRINTER_STATUS_PAPER_LACK){
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_PAPER, Toast.LENGTH_SHORT);
                } else {
                    intent = new Intent(context, PrintParameter.class);
                    intent.putExtra("typeReport", DefinesDATAFAST.ITEM_TEST);
                    context.startActivity(intent);
                }

                break;
            case DefinesDATAFAST.ITEM_TRANS_EN_PANTALLA:
                if ((batteryStatus.getLevelBattery() <= 8) && (!batteryStatus.isCharging())) {
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_BATTERY, Toast.LENGTH_SHORT);
                } else if (paperStatus.getRet() == Printer.PRINTER_STATUS_PAPER_LACK){
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_PAPER, Toast.LENGTH_SHORT);
                } else {

                    idAcquirer = idLote;
                    if (ToolsBatch.statusTrans(idAcquirer)) {
                        intent = new Intent(context, HistoryTrans.class);
                        intent.putExtra(HistoryTrans.EVENTS, HistoryTrans.COMMON);
                        context.startActivity(intent);
                    } else {
                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.LOTE_VACIO, Toast.LENGTH_LONG);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                    }
                }
                break;
            case ITEM_PREAUTO_PANTALLA:
                if ((batteryStatus.getLevelBattery() <= 8) && (!batteryStatus.isCharging())) {
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_BATTERY, Toast.LENGTH_SHORT);
                } else if (paperStatus.getRet() == Printer.PRINTER_STATUS_PAPER_LACK){
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_PAPER, Toast.LENGTH_SHORT);
                } else {

                    idAcquirer = idLote + FILE_NAME_PREAUTO;
                    if (ToolsBatch.statusTrans(idAcquirer)) {
                        intent = new Intent(context, HistoryTrans.class);
                        intent.putExtra(HistoryTrans.EVENTS, HistoryTrans.COMMON);
                        context.startActivity(intent);
                    } else {
                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.LOTE_VACIO_PREAUTO, Toast.LENGTH_LONG);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                    }
                }
                break;
            case DefinesDATAFAST.ITEM_BORRAR_LOTE:
                if (isInit)
                    maintainPwd("CLAVE TECNICO", tconf.getCLAVE_TECNICO(), DefinesDATAFAST.ITEM_BORRAR_LOTE, 6);
                else
                    maintainPwd("CLAVE TECNICO", TERMINAL_PWD, DefinesDATAFAST.ITEM_BORRAR_LOTE, 6);

                break;
            case DefinesDATAFAST.ITEM_BORRAR_REVERSO:
                if (isInit)
                    maintainPwd("CLAVE TECNICO", tconf.getCLAVE_TECNICO(), DefinesDATAFAST.ITEM_BORRAR_REVERSO, 6);
                else
                    maintainPwd("CLAVE TECNICO", TERMINAL_PWD, DefinesDATAFAST.ITEM_BORRAR_REVERSO, 6);
                break;
            case DefinesDATAFAST.ITEM_INICIALIZACION:
                idAcquirer = idLote;
                if (!ToolsBatch.statusTrans(idAcquirer) && !ToolsBatch.statusTrans(idAcquirer + FILE_NAME_PREAUTO)) {
                    UIUtils.cancelToastInit();
                    //if (!ToolsBatch.statusTrans(context)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(context, Init.class);
                    intent.putExtra("PARCIAL", false);
                    context.startActivity(intent);
                } else {
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_SETTLE, Toast.LENGTH_SHORT);
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                }
                break;
            case DefinesDATAFAST.ITEM_CONFIG_INICIAL:
                UIUtils.cancelToastInit();
                String text = "CONFIG INICIAL";
                intent.putExtra(JUMP_KEY, text);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, CommunSettings.class);
                context.startActivity(intent);
                break;
            case ITEM_REPORTE_DETALLADO:
                if ((batteryStatus.getLevelBattery() <= 8) && (!batteryStatus.isCharging())) {
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_BATTERY, Toast.LENGTH_SHORT);
                } else if (paperStatus.getRet() == Printer.PRINTER_STATUS_PAPER_LACK){
                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_PAPER, Toast.LENGTH_SHORT);
                } else {
                    callbackPrint = null;
                    idAcquirer = idLote;
                    if (ToolsBatch.statusTrans(idAcquirer) || ToolsBatch.statusTrans(idAcquirer + FILE_NAME_PREAUTO)) {
                        PrintParameter.setPrintTotals(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, PrintParameter.class);
                        intent.putExtra("typeReport", DefinesDATAFAST.ITEM_REPORTE_DETALLADO);
                        context.startActivity(intent);
                    } else {
                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.LOTE_VACIO, Toast.LENGTH_SHORT);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                    }
                }
                break;
            case ITEM_PRE_VOUCHER:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[33]);
                context.startActivity(intent);
                break;
            case ITEM_PAGO_PREVOUCHER:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[34]);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.ITEM_CASH_OVER:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[35]);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.ITEM_PAGOS_VARIOS:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[36]);
                context.startActivity(intent);
                break;


            case DefinesDATAFAST.ITEM_MASTER_KEY:
                if (isInit)
                    maintainPwd("CLAVE TECNICO", tconf.getCLAVE_TECNICO(), DefinesDATAFAST.ITEM_MASTER_KEY, 6);
                else
                    maintainPwd("CLAVE TECNICO", TERMINAL_PWD, DefinesDATAFAST.ITEM_MASTER_KEY, 6);
                break;
            case DefinesDATAFAST.ITEM_DATOS_COMERCIO:
                UIUtils.dialogInformativo(context,"INFORMACION DEL COMERCIO",
                        "Comercio: " + TMConfig.getInstance().getMerchID() + "\n" +
                        "Terminal: " + TMConfig.getInstance().getTermID());
                break;
            case DefinesDATAFAST.ITEM_SETTINGS:
                intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
                break;
            case DefinesDATAFAST.ITEM_CONEXION:
                String[] datos = UtilNetwork.getWifi(context);
                UIUtils.dialogInformativo(context,"DATOS DE CONEXION",
                        "IP: " + UtilNetwork.getIPAddress(true) + "\n" +
                        "GATWEY: " + datos[3] + "\n" +
                        "MASK: " + datos[0] + "\n" +
                        "RED: " + datos[4]);
                break;
            case DefinesDATAFAST.ITEM_CONFIG_WIFI:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, WifiSettings.class);
                context.startActivity(intent);
                break;

            default:
                intent.setClass(context, menus.class);
                intent.putExtra(DefinesDATAFAST.DATO_MENU, tipoDeMenu);
                context.startActivity(intent);
                break;
        }
    }

    private void maintainPwd(String title, final String pwd, final String type_trans, int lenEdit) {
        final Intent intent = new Intent();
        mDialog = UIUtils.centerDialog(context, R.layout.setting_home_pass, R.id.setting_pass_layout);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText newEdit = mDialog.findViewById(R.id.setting_pass_new);
        final TextView title_pass = mDialog.findViewById(R.id.title_pass);
        Button confirm = mDialog.findViewById(R.id.setting_pass_confirm);
        newEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lenEdit)});
        newEdit.requestFocus();
        title_pass.setText(title);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newEdit.getWindowToken(), 0);
                String np = newEdit.getText().toString();

                if (!np.equals("")){
                    if (np.equals(pwd)) {
                        switch (type_trans) {
                            case DefinesDATAFAST.ITEM_DEPOSITO:

                                TransLogData revesalData = TransLog.getReversal(false);
                                if (revesalData != null) {
                                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_REV_PEN, Toast.LENGTH_SHORT);
                                }else {

                                    if ((batteryStatus.getLevelBattery() <= 8) && (!batteryStatus.isCharging())) {
                                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_BATTERY, Toast.LENGTH_SHORT);
                                    } else if (paperStatus.getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_PAPER, Toast.LENGTH_SHORT);
                                    } else {
                                        idAcquirer = idLote;
                                        if (ToolsBatch.statusTrans(idAcquirer)) {
                                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                            alertDialog.setIcon(R.drawable.ic_launcher);
                                            alertDialog.setTitle("INFORMACIÓN");
                                            alertDialog.setMessage("¿IMPRIMIR RESUMEN DE VENTAS?");
                                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Si",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(final DialogInterface dialog, int which) {

                                                            PrintParameter.setPrintTotals(true);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.setClass(context, PrintParameter.class);
                                                            intent.putExtra("typeReport", DefinesDATAFAST.ITEM_REPORTE_DETALLADO);
                                                            context.startActivity(intent);

                                                            callbackPrint = null;
                                                            callbackPrint = new waitPrintReport() {
                                                                @Override
                                                                public void getRspPrintReport(int status) {

                                                                    if (status == 0) {
                                                                        PrintParameter.setPrintTotals(false);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        intent.setClass(context, MasterControl.class);
                                                                        intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[5]);
                                                                        context.startActivity(intent);
                                                                    }

                                                                    dialog.dismiss();
                                                                }
                                                            };
                                                        }
                                                    });
                                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            PrintParameter.setPrintTotals(false);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.setClass(context, MasterControl.class);
                                                            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[5]);
                                                            context.startActivity(intent);
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();

                                            makeInitCallback = null;

                                            makeInitCallback = new makeInitCallback() {
                                                @Override
                                                public void getMakeInitCallback(boolean status) {
                                                    //if (Settle.isSeatleOk) {
                                                    if (status) {
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.setClass(context, Init.class);
                                                        intent.putExtra("PARCIAL", true);
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            };

                                        } else {
                                            UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.LOTE_VACIO, Toast.LENGTH_SHORT);
                                            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                                            toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                                        }
                                    }
                                }
                                break;
                            case DefinesDATAFAST.ITEM_BORRAR_REVERSO:

                                TransLogData revesalData2 = TransLog.getReversal(false);
                                if (TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).getSize() > 0 || revesalData2 != null) {
                                    //SE VALIDA SI EXISTE REVERSO PENDIENTE
                                    if (TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).getSize() > 0){
                                        TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).clearAll(idAcquirer + FILE_NAME_REVERSE);
                                    }
                                    //SE VALIDA SI EXISTE REVERSO DE LA CAJA PENDIENTE
                                    if (revesalData2 != null) {
                                        TransLog.clearReveral(true);
                                    }

                                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, "REVERSO BORRADO EXITOSAMENTE", Toast.LENGTH_SHORT);
                                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                                    toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                                } else
                                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, "NO EXISTE REVERSO", Toast.LENGTH_SHORT);

                                break;
                            case DefinesDATAFAST.ITEM_BORRAR_LOTE:

                                TransLogData revesalData1 = TransLog.getReversal(false);
                                if (revesalData1 != null) {
                                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_REV_PEN, Toast.LENGTH_SHORT);
                                }else if (TransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE).getSize() > 0){
                                    UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_REV_PEN, Toast.LENGTH_SHORT);
                                }else {
                                    if (paperStatus.getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, DefinesDATAFAST.MSG_PAPER, Toast.LENGTH_SHORT);
                                    } else {

                                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                        alertDialog.setIcon(R.drawable.ic_launcher);
                                        alertDialog.setTitle("INFORMACIÓN");
                                        alertDialog.setMessage("¿DESEA BORRAR LOTE?");
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Si",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(final DialogInterface dialog, int which) {

                                                        idAcquirer = idLote + FILE_NAME_PREAUTO;
                                                        if (ToolsBatch.statusTrans(idAcquirer)) {
                                                            if (idAcquirer != null) {
                                                                if (ToolsBatch.statusTrans(idAcquirer)) {
                                                                    int val = Integer.parseInt(TMConfig.getInstance().getBatchNo());
                                                                    TMConfig.getInstance().setBatchNo(val).save();
                                                                    TransLog.getInstance(idAcquirer).clearAll(idAcquirer);
                                                                    CommonFunctionalities.limpiarPanTarjGasolinera("");
                                                                }
                                                            }
                                                        }

                                                        idAcquirer = idLote;
                                                        if (ToolsBatch.statusTrans(idAcquirer)) {
                                                            int val = Integer.parseInt(TMConfig.getInstance().getBatchNo());
                                                            TMConfig.getInstance().setBatchNo(val).save();
                                                            TransLog.getInstance(idAcquirer).clearAll(idAcquirer);
                                                            CommonFunctionalities.limpiarPanTarjGasolinera("");
                                                        }

                                                        idAcquirer = idLote + FILE_NAME_PREVOUCHER;
                                                        if (ToolsBatch.statusTrans(idAcquirer)) {
                                                            if (idAcquirer != null) {
                                                                if (ToolsBatch.statusTrans(idAcquirer)) {
                                                                    int val = Integer.parseInt(TMConfig.getInstance().getBatchNo());
                                                                    TMConfig.getInstance().setBatchNo(val).save();
                                                                    TransLog.getInstance(idAcquirer).clearAll(idAcquirer);
                                                                    CommonFunctionalities.limpiarPanTarjGasolinera("");
                                                                }
                                                            }
                                                        }

                                                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, "LOTE BORRADO", Toast.LENGTH_SHORT);
                                                    }
                                                });

                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                        alertDialog.show();
                                    }
                                }
                                break;
                            case DefinesDATAFAST.ITEM_ANULACION:
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(context, MasterControl.class);
                                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[22]);
                                context.startActivity(intent);
                                break;
                            case DefinesDATAFAST.ITEM_ANULACION_PRE_AUT:
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(context, MasterControl.class);
                                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[31]);
                                context.startActivity(intent);
                                break;
                            case DefinesDATAFAST.ITEM_REIMPRESION_PRE_AUT:
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(context, MasterControl.class);
                                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[32]);
                                context.startActivity(intent);
                                break;
                            case DefinesDATAFAST.ITEM_POLARIS:
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(context, menus.class);
                                intent.putExtra(DefinesDATAFAST.DATO_MENU, DefinesDATAFAST.ITEM_COMUNICACION);
                                context.startActivity(intent);
                                break;
                            case DefinesDATAFAST.ITEM_MASTER_KEY:
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(context, PwMasterKey.class);
                                context.startActivity(intent);
                                break;
                        }
                        mDialog.dismiss();

                    } else {
                        newEdit.setText("");
                        UIUtils.toast((Activity) context, R.drawable.ic_launcher, context.getString(R.string.err_msg_pwoperario), Toast.LENGTH_SHORT);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                    }

                }
            }
        });

        mDialog.findViewById(R.id.setting_pass_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }
}
