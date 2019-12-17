package com.datafast.startboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.newpos.pay.StartAppDATAFAST;

public class StartBoot extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent Intent = new Intent(context, StartAppDATAFAST.class);
        Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent);
    }
}
