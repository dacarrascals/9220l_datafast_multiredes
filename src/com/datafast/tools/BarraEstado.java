package com.datafast.tools;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.android.newpos.pay.R;

public class BarraEstado extends LinearLayout {

    TextClock textClock;
    TextView tvBateria,tvSignalType;
    //TextView tvOperador;
    ImageView imgSenal, imgCharge,modoKiosco;

    public int tipoRed;

    public static int senal;

    public BarraEstado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.status_bar, this);
        textClock = findViewById(R.id.textClock);
        tvBateria = findViewById(R.id.tvBateria);
        imgCharge = findViewById(R.id.imgCharge);
        imgSenal = findViewById(R.id.imgSenal);
        tvSignalType = findViewById(R.id.tvSignalType);
        modoKiosco = findViewById(R.id.imgModoKiosko);
        CargarBarraDeEstado(context);
        getSignalType(context);
        validarModoKiosko(context);
    }



    private void CargarBarraDeEstado(final Context context) {
        setSignal(context);

        //Bateria
        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctxt, Intent intent) {
                context.unregisterReceiver(this);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                tvBateria.setText(String.valueOf(level) + "%");
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                if (!isCharging) {
                    imgCharge.setVisibility(INVISIBLE);
                } else {
                    imgCharge.setVisibility(VISIBLE);
                }
            }
        };
        tvBateria = findViewById(R.id.tvBateria);
        context.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void setSignal(Context context){

        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        tipoRed = telephonyManager.getNetworkType();

        myPhoneStateListener listener = new myPhoneStateListener();

        telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public class myPhoneStateListener extends PhoneStateListener {

        int signalStrengthValue;

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            String ssignal = signalStrength.toString();

            String[] parts = ssignal.split(" ");

            if (tipoRed == TelephonyManager.NETWORK_TYPE_LTE){
                signalStrengthValue = Integer.parseInt(parts[9]);
            }else {
                if (signalStrength.isGsm()) {
                    if (signalStrength.getGsmSignalStrength() != 99)
                        signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
                    else
                        signalStrengthValue = signalStrength.getGsmSignalStrength();
                }
            }

            setImageSignal(signalStrengthValue);
        }
    }
    
    private void setImageSignal(int dBm){
            if(dBm <= -120){
                imgSenal.setImageResource(R.drawable.signal_null);
            }else if(dBm >= -119 && dBm <= -104){
                imgSenal.setImageResource(R.drawable.signal_low);
            }else if(dBm >= -103 && dBm <= -98){
                imgSenal.setImageResource(R.drawable.signal_low);
            }else if(dBm >= -97 && dBm <= -90){
                imgSenal.setImageResource(R.drawable.signal_med);
            }else if(dBm >= -89 && dBm <= -77){
                imgSenal.setImageResource(R.drawable.signal_med);
            }else if(dBm >= -76 && dBm <= -60){
                imgSenal.setImageResource(R.drawable.signal_full);
            }else if(dBm == 0){
                imgSenal.setImageResource(R.drawable.signal_full);
            }else{
                imgSenal.setImageResource(R.drawable.no_sim);
            }
            senal = dBm;
    }
    private void getSignalType(Context context){

        String tipoRed="";
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        try {
            tipoRed = networkInfo.getTypeName();

        }catch (Exception e){
            tipoRed = "NO DATA";
        }
        //tipoRed = networkInfo.getTypeName();
        switch (tipoRed) {
            case "NO DATA":
                tvSignalType.setText(tipoRed);
                break;
            case "WIFI":
                tvSignalType.setText(tipoRed);
                break;
            case "MOBILE":
                String tipoGSM = networkInfo.getSubtypeName();
                switch (tipoGSM){
                    case "EDGE":
                        tvSignalType.setText("2G");
                        break;
                    case "UMTS":
                    case "HSPA":
                    case "HSDPA":
                        tvSignalType.setText("3G");
                        break;
                    case "HSPA+":
                        tvSignalType.setText("3.5G");
                        break;
                    case "LTE":
                        tvSignalType.setText("4G");
                        break;
                }
                break;
            default:
                tvSignalType.setText(networkInfo.getSubtypeName());
                break;
        }

    }

    public void validarModoKiosko(Context context){
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if(am.isInLockTaskMode()){
                modoKiosco.setImageResource(R.drawable.phone_loc);
            }
        }catch (NullPointerException e){
            Log.e("Error", ""+e);
        }

    }


}
