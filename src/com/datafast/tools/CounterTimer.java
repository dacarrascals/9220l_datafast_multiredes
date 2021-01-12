package com.datafast.tools;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;

public class CounterTimer {

    CountDownTimer countDownTimer;
    Activity activity;

    public CounterTimer(Activity activity) {
        this.activity = activity;
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

    private void deleteTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
