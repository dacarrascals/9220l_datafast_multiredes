package com.datafast.slide;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import com.android.desert.admanager.ConvenientBanner;
import com.android.desert.admanager.holder.CBViewHolderCreator;
import com.android.newpos.pay.R;
import java.io.File;

public class slide {

    private static ConvenientBanner<String> adColumn;
    private String DEFAULT_PATH_ADS = Environment.getExternalStorageDirectory() + File.separator + "ADS";

    public slide(Context context, boolean loop){
        adColumn = new ConvenientBanner(context, loop);
    }

    public static void galeria(Activity activity, int id ){

        String path = activity.getFilesDir() + "/ad";

        fileSlide.readCopy(path, activity);
        adColumn = (ConvenientBanner<String>) activity.findViewById(id);
        adColumn.setPages(new CBViewHolderCreator<AdHolder>() {

            @Override
            public AdHolder createHolder() {
                return new AdHolder();
            }

        }, fileSlide.getAds(path + "/")).setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focused});

    }

    public void stopSlide(){
        if (adColumn != null){
            this.adColumn.stopTurning();
        }
    }

    public void setTimeoutSlide(int timeout){
        if (timeout >= 0 && adColumn != null){
            this.adColumn.startTurning(timeout);
        }
    }
}
