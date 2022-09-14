package com.picture.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapResizer {
    private Context context;

    public BitmapResizer(Context context) {
        this.context = context;
    }

    public Bitmap resize(String filePath, int outputWidth, int outputHeight) {
        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap srcBmp = BitmapFactory.decodeFile(filePath, ops);
        if (Bitmap.Config.RGB_565 != srcBmp.getConfig()) {
            return null;
        }
        return resize(srcBmp, outputWidth, outputHeight);
    }

    public Bitmap resize(Bitmap srcBmp, int outputWidth, int outputHeight) {
        Bitmap destBmp = null;
        try {

            int height = srcBmp.getHeight();
            int width = srcBmp.getWidth();


            float scaleWidth = 1;
            float scaleHeight = 1;
            if (height > width) {
                scaleWidth = outputHeight / (float) width;
                scaleHeight = outputWidth / (float) height;
            } else {
                scaleWidth = outputWidth / (float) width;
                scaleHeight = outputHeight / (float) height;
            }

            float scale = Math.min(scaleWidth, scaleHeight);
            if (scale <= 1) {
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                destBmp = Bitmap.createBitmap(srcBmp, 0, 0, width, height, matrix, true);
            } else {
                destBmp = Bitmap.createBitmap(srcBmp);
            }

            return destBmp;
        } finally {
            if (srcBmp != null && !srcBmp.isRecycled() && !srcBmp.equals(destBmp)) {
                srcBmp.recycle();
                srcBmp = null;
            }
        }
    }
}
