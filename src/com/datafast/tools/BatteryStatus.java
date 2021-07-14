package com.datafast.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryStatus extends BroadcastReceiver {
    int levelBattery;
    boolean isCharging = false;

    public int getLevelBattery() {
        return levelBattery;
    }

    public boolean isCharging() {
        return isCharging;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        this.levelBattery = 100;
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        this.isCharging = true;
    }
}
