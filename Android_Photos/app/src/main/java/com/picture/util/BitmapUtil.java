package com.picture.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapUtil {

    public static Bitmap bitmapZoomByHeight(Bitmap srcBitmap, float newHeight) {
        float scale = newHeight / (((float) srcBitmap.getHeight()));
        return BitmapUtil.bitmapZoomByScale(srcBitmap, scale, scale);
    }

    public static Bitmap bitmapZoomByHeight(Drawable drawable, float newHeight) {
        Bitmap bitmap = BitmapUtil.drawableToBitmap(drawable);
        float scale = newHeight / (((float) bitmap.getHeight()));
        return BitmapUtil.bitmapZoomByScale(bitmap, scale, scale);
    }

    public static Bitmap bitmapZoomByScale(Bitmap srcBitmap, float scaleWidth, float scaleHeight) {
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, true);
        if (bitmap != null) {
            return bitmap;
        } else {
            return srcBitmap;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap drawableToBitmap2(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bm = bd.getBitmap();
        return bm;
    }

    public static void saveBitmapToSDCard(Bitmap bitmap, String path) {
        File file = new File(path);
//        if (file.exists()) {
//            file.delete();
//        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ((OutputStream) fileOutputStream));
            fileOutputStream.close();
        } catch (Exception v0) {
            v0.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromSDCard(String path) {
        Bitmap bitmap = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            if (fileInputStream != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeStream(((InputStream) fileInputStream), null, options);
            }
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }

    public static void saveBmpToPath(final Bitmap bitmap, final String filePath) {
        File file = new File(filePath);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap toHorizontalMirror(Bitmap bm) {
        Canvas canvas = new Canvas();
        Bitmap newBm = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(newBm);
        Matrix matrix = new Matrix();
        matrix.setScale(-1.0f, 1.0f);
        matrix.postTranslate(bm.getWidth(), 0);
        canvas.drawBitmap(bm, matrix, null);
        return newBm;
    }

    public static int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static void saveBmpToOriginalPath(final Bitmap bitmap, final String filePath) {
        File file = new File(filePath);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
}
