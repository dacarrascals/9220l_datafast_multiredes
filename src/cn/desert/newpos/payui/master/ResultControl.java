package cn.desert.newpos.payui.master;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.newpos.pay.R;
import com.datafast.server.activity.ServerTCP;
import com.datafast.server.server_tcp.Server;
import com.newpos.libpay.Logger;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;

import java.util.Timer;
import java.util.TimerTask;

import cn.desert.newpos.payui.base.BaseActivity;

import static com.android.newpos.pay.StartAppDATAFAST.lastCmd;
import static com.datafast.menus.MenuAction.callBackSeatle;
import static com.datafast.menus.menus.contFallback;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CP;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PC;
import static com.pos.device.sys.SystemManager.reboot;
import static java.lang.Thread.sleep;

/**
 * Created by zhouqiang on 2016/11/12.
 */
public class ResultControl extends BaseActivity {
    Button confirm;
    TextView details;
    ImageView face;
    ImageView removeCard;
    IccReader iccReader0;
    Thread proceso = null;
    private static long second = 1000;
    private final int TIMEOUT_REMOVE_CARD = 60 * 2000;//2 min
    private Timer timer = null;
    private String info = null;
    private boolean back;
    private boolean buttonActive = false;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_result);
        setReturnVisible(View.INVISIBLE);
        setNaviTitle(R.string.trans_result);
        setRightVisiblity(View.GONE);
        confirm = (Button) findViewById(R.id.result_confirm);
        details = (TextView) findViewById(R.id.result_details);
        face = (ImageView) findViewById(R.id.result_img);

        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);

        timer = new Timer();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            displayDetails(bundle.getBoolean("flag"), bundle.getString("info"));
            if (bundle.getBoolean("boton")) {
                confirm.setVisibility(View.VISIBLE);
                buttonActive = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //over();
                        removeCard();
                    }
                }, 5 * second);
            } else {
                if(Server.cmd.equals(PC)){
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //over();
                            removeCard();
                        }
                    }, (long) (0.5 * second));
                } else if(Server.cmd.equals(CP)){
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //over();
                            reboot();
                        }
                    }, 2 * second);
                }else{
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //over();
                            removeCard();
                        }
                    }, 1 * second);
                }
            }
            over();
        } else {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //over();
                    removeCard();
                }
            }, 3 * second);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            over();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void over() {
        //finish();
        confirm.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    removeCard();
                }
                return false;
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //over();
                removeCard();
            }
        });
    }

    private void displayDetails(boolean flag, String info) {
        this.info = info;
        this.flag = flag;
        details.setText(info);
        if (flag) {
            face.setImageResource(R.drawable.result_success);
            details.setTextColor(Color.parseColor("#0097AC"));
        } else {
            face.setImageResource(R.drawable.result_fail);
            details.setTextColor(Color.parseColor("#0097AC"));
        }
    }

    private void removeCard() {
        if (proceso == null) {
            proceso = new Thread(new Runnable() {
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iccReader0 = IccReader.getInstance(SlotType.USER_CARD);

                            if (iccReader0.isCardPresent() && (!lastCmd.equals(LT))) {
                                setContentView(R.layout.activity_remove_card);
                                removeCard = (ImageView) findViewById(R.id.iv_remove__card);
                                removeCard.setImageResource(R.drawable.remove_card);
                            }
                        }
                    });

                    if (checkCard()) {
                        if(info.equals("INICIALIZACION EXITOSA") || info.equals("INICIALIZACION FALLIDA")
                                || info.equals("ECHO TEST OK") || info.equals("NO HUBO RESPUESTA")){
                            startActivity(new Intent(ResultControl.this, ServerTCP.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }else{
                            finish();
                        }
                        if (callBackSeatle != null)
                            callBackSeatle.getRspSeatleReport(0);
                        finish();
                    }
                }
            });
            proceso.start();
        }
    }

    private boolean checkCard() {
        boolean ret = false;
        if ((Server.cmd.equals(LT)) || (lastCmd == LT)) {
            return true;
        }
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        //iccReader0 = IccReader.getInstance(SlotType.USER_CARD);

        long start = SystemClock.uptimeMillis();

        while (true) {

            try {
                if (iccReader0.isCardPresent()) {
                    back = true;
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        Logger.error("Exception" + e.toString());
                        Thread.currentThread().interrupt();
                    }

                } else {
                    back = false;
                    ret = true;
                    break;
                }
            } catch (Exception e) {
                ret = true;
                break;
            }
        }

        proceso = null;
        return ret;
    }
}
