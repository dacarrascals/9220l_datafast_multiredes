package com.datafast.slide;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import com.android.desert.admanager.holder.Holder;
import com.datafast.slide.fileSlide;

/**
 * Created by zhouqiang on 2017/11/10.
 */

public class AdHolder implements Holder<String> {

    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    public static final int AD_WIDTH = 640;
    public static final int AD_HEIGHT = 240;

    @Override
    public void UpdateUI(Context context, int position, String data) {
        Bitmap bm = fileSlide.decodeSampledBitmapFromFile(data, AD_WIDTH, AD_HEIGHT);
        imageView.setImageBitmap(bm);
    }
}
