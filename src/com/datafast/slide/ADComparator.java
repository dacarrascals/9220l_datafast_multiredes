package com.datafast.slide;

import android.util.Log;

import java.util.Comparator;

/**
 * Created by zhouqiang on 2017/11/10.
 */

public class ADComparator implements Comparator<String> {
    @Override
    public int compare(String pathAD1, String pathAD2) {
        return getAdNumber(pathAD1)-getAdNumber(pathAD2);
    }

    private int getAdNumber(String pathAD){
        Log.d("ad" , "pathAD:"+pathAD);
        int beg = pathAD.lastIndexOf("/")+1;
        int end = pathAD.lastIndexOf(".");
        return Integer.parseInt(pathAD.substring(beg, end));
    }
}
