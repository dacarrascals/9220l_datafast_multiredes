package com.datafast.tools;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.util.Log;

import com.datafast.datos_pruebas.DebugInit;

public class CounterTimer {

    CountDownTimer countDownTimer;
    Activity activity;
    Dialog dialog;

    public CounterTimer(Activity activity) {
        this.activity = activity;
    }

    public CounterTimer(Dialog dialog) {
        this.dialog = dialog;
    }

    public void counterDownTimer() {
        deleteTimer();

        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                Log.i("onTick", "finish onTick countDownTimer HomeDataFast");
                deleteTimer();
                activity.finish();
            }
        }.start();
    }

    public void counterDownTimerDialog() {
        deleteTimer();
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                Log.i("onTick", "finish onTick countDownTimer HomeDataFast");
                deleteTimer();
                dialog.dismiss();
            }
        }.start();
    }

    private void deleteTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
