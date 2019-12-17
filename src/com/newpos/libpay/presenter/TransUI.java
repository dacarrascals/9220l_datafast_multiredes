package com.newpos.libpay.presenter;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.datafast.inicializacion.prompts.Prompt;
import com.datafast.pinpad.cmd.process.ProcessPPFail;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.pinpad.OfflineRSA;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.device.scanner.QRCInfo;
import com.newpos.libpay.trans.translog.TransLogData;

import java.util.ArrayList;

/**
 * Created by zhouqiang on 2017/3/15.
 *
 * @author zhouqiang
 * 交易UI接口类
 */

public interface TransUI {
    /**
     * 获取外界输入UI接口(提示用户输入信息)
     *
     * @return return
     */
    InputInfo getOutsideInput(int timeout, InputManager.Mode type, String title);

    /**
     * 获取外界卡片UI接口(提示用户用卡)
     *
     * @return return
     */
    CardInfo getCardUse(String msg, int timeout, int mode, String title);

    CardInfo getCardUseAmount(String msg, int timeout, int mode, String title, String label, String amount);

    CardInfo getCardUsePagosElect(String msg, int timeout, int mode, String title, String label, String amount, int minLen, int maxLen);

    /**
     *
     * @param msg
     * @param timeout
     * @param mode
     * @param title
     * @return
     */
    CardInfo getCardFallback(String msg, int timeout, int mode, String title);

    /**
     * 获取外界扫码支付方式接口(提示用户扫码操作)
     *
     * @param timeout Timeout
     * @param mode    @{@link InputManager.Style}
     * @return return
     */
    QRCInfo getQRCInfo(int timeout, InputManager.Style mode);

    /**
     * 获取密码键盘输入联机PIN
     *
     * @param timeout Timeout
     * @param amount  Monto
     * @param cardNo  Numero Tarjeta
     */
    PinInfo getPinpadOnlinePin(int timeout, String amount, String cardNo);

    /**
     * @param timeout       Timeout
     * @param i             Listener
     * @param key           Key
     * @param offlinecounts Contador
     * @return return
     */
    //PinInfo getPinpadOfflinePin(int timeout, int i, OfflineRSA key, int offlinecounts);
    /**
     * 脱机PIN
     * @param timeout
     * @param amount
     * @param cardNo
     * @return
     */
    PinInfo getPinpadOfflinePin(int timeout , String amount , String cardNo);

    /**
     * 人机交互显示UI接口(卡号确认)
     *
     * @param cn 卡号
     */
    int showCardConfirm(int timeout, String cn);

    /**
     * @param msg        Mensaje
     * @param btnCancel  Boton cancel
     * @param btnConfirm boton confirmar
     * @return return
     */
    InputInfo showMessageInfo(String title, String msg, String btnCancel, String btnConfirm, int timeout);

    /**
     * @param msg        Mensaje
     * @param btnCancel  Boton cancel
     * @param btnConfirm boton confirmar
     * @return return
     */
    InputInfo showMessageImpresion(String title, String msg, String btnCancel, String btnConfirm, int timeout);

    /**
     * 人机交互显示UI接口(多应用卡片选择)
     *
     * @param timeout Timeout
     * @param list    Lista
     * @return return
     */
    int showCardApplist(int timeout, String[] list);

    /**
     * 人机交互显示UI接口（多语言选择接口）
     *
     * @param timeout Timeout
     * @param langs   Lenguajes
     * @return return
     */
    int showMultiLangs(int timeout, String[] langs);

    /**
     * 人机交互显示UI接口(耗时处理操作)
     *
     * @param timeout Timeout
     * @param status  TransStatus 状态标志以获取详细错误信息
     */
    void handling(int timeout, int status);

    void handlingError(int timeout, int status);

    /**
     * 人机交互显示UI接口(耗时处理操作)
     *
     * @param timeout Timeout
     * @param status  TransStatus 状态标志以获取详细错误信息
     */
    void handling(int timeout, int status, String title);

    void handlingInfo(int timeout, int status, String msg);

    /**
     * 人机交互显示UI接口
     *
     * @param timeout Timeout
     * @param logData 详细交易日志
     */
    int showTransInfo(int timeout, TransLogData logData);

    /**
     * 交易成功处理结果
     *
     * @param code Codigo
     */
    void trannSuccess(int timeout, int code, String... args);

    /**
     * 人机交互显示UI接口(显示交易出错错误信息)
     *
     * @param errcode 实际代码错误返回码
     */
    void showError(int timeout, int errcode);

    void showfinish();

    void showError(int timeout, int errcode, ProcessPPFail processPPFail);

    /**
     * @param timeout Timeout
     * @param title   Titulo
     * @return return
     */
    InputInfo showTypeCoin(int timeout, final String title);

    /**
     * @param timeout Timeout
     * @param title   Titulo
     * @return return
     */
    InputInfo showInputUser(int timeout, final String title, final String label2, int min, int max);

    /**
     * @param errcode Error code
     */
    void toasTrans(int errcode, boolean sound, boolean isErr);

    void toasTransReverse(int errcode, boolean sound, boolean isErr);

    void toasTrans(String errcode, boolean sound, boolean isErr);

    /**
     * @param timeout Timeout
     * @param title   Titulo
     * @param label   Mensaje
     * @return return
     */
    InputInfo showConfirmAmount(int timeout, final String title, final String label, String amnt, boolean isHTML);

    /**
     * @param message Mensaje
     */
    void showMessage(String message, boolean transaccion);

    /**
     * @param img Imagen
     */
    void showCardImg(String img);

    /**
     * @param timeout   Timeout
     * @param title     Titulo
     * @param transType Tipo Transaccion
     * @return return
     */
    InputInfo showSignature(int timeout, String title, String transType);

    /**
     * @param timeout   Timeout
     * @param title     Titulo
     * @param transType Tipo Transaccion
     * @return return
     */
    InputInfo showList(int timeout, String title, String transType, final ArrayList<String> listMenu, int id);


    /**
     *
     * @param timeout
     * @param transType
     * @param nameAcq
     * @param cls
     * @return
     */
    InputInfo showInputPrompt(int timeout, String transType, String nameAcq, Prompt cls);

    /**
     * 提示脱机密码结果
     * @param count
     */
    void showOfflinePinResult(int count);
}
