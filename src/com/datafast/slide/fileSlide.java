package com.datafast.slide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class fileSlide {

    public static void readCopy(String path, Context context){
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        copyToAssets2(context, "ad", path + "/");
    }

    public static List<String> getAds(String dir) {
        List<String> adList = new ArrayList<>();
        File adRoot = new File(dir);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(new Date());

        File[] adDirs = adRoot.listFiles();
        if (adDirs == null) {
            return adList;
        }
        for (File adDir : adDirs) {
            if (adDir.isDirectory()) {
                String dirName = adDir.getName();
                String[] infos = dirName.split("-");
                if (infos.length != 3) {
                    continue;
                } else if (now.compareTo(infos[2]) > 0) {
                    delete(adDir);
                } else if (now.compareTo(infos[1]) > 0) {
                    for (File ad : adDir.listFiles()) {
                        adList.add(ad.getAbsolutePath());
                    }
                }
            }
        }
        // 如果没有服务器下发的广告，显示默认广告
        if (adList.size() == 0) {
            for (File adFile : adDirs) {
                if (adFile.getName().contains(".json")) {
                    continue;
                }
                adList.add(adFile.getAbsolutePath());
            }
        }
//        Collections.sort(adList, new ADComparator());
        return adList;
    }

    public static void delete(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    public static void copyToAssets2(Context context, String assetDir, String dir) {

        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            // 创建文件夹
            if (!mWorkingPath.mkdirs()) {
                // 文件夹创建不成功时调用
            }
        }

        for (int i = 0; i < files.length; i++) {
            try {

                // 获得每个文件的名字
                String fileName = files[i];
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }

                InputStream in = null;
                try {
                    if (0 != assetDir.length()) {
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    }else {
                        in = context.getAssets().open(fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //it said that this is a directory
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }

                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }
                FileOutputStream out = new FileOutputStream(outFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.flush();
                out.getFD().sync();
                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyToAssets(Context context, String assetDir, String dir) {

        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            // 创建文件夹
            if (!mWorkingPath.mkdirs()) {
                // 文件夹创建不成功时调用
            }
        }

        for (int i = 0; i < files.length; i++) {
            try {

                // 获得每个文件的名字
                String fileName = files[i];
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }

                InputStream in = null;
                try {
                    if (0 != assetDir.length()) {
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    } else {
                        in = context.getAssets().open(fileName);
                    }
                } catch (Exception e) {
                    //it said that this is a directory
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }

                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }

                try (FileOutputStream out = new FileOutputStream(outFile)) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    if (in != null) {
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.flush();
                        out.getFD().sync();
                        out.close();
                        in.close();
                    }
                }
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String pathName,
                                                     int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                            int dstHeight, int inSampleSize) {
        // 如果inSampleSize是2的倍数，也就说这个src已经是我们想要的缩略图了，直接返回即可。
        if (inSampleSize == 1) {
            return src;
        }
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }
}
