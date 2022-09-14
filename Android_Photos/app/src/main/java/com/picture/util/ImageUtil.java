package com.picture.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ImageUtil {
    public static byte[] convertByte(ImageView iv) {
        iv.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(iv.getDrawingCache());
        iv.setDrawingCacheEnabled(false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Drawable convertDrawable(byte[] b) {
        ByteArrayInputStream bain = new ByteArrayInputStream(b);
        return Drawable.createFromStream(bain, "img");
    }

    public static void loadImgByUrl(ImageView imageView, String filePath) {
        Glide.with(imageView.getContext()).load(filePath)
                .skipMemoryCache(true).centerCrop().fitCenter().into(imageView);
    }

    public static void loadImgByUrl(ImageView imageView, int drawableId) {
        Glide.with(imageView.getContext()).load(drawableId)
                .skipMemoryCache(true).centerCrop().fitCenter().into(imageView);
    }
    
    public static void loadImgByUrl(ImageView imageView, Bitmap bitmap) {
        Glide.with(imageView.getContext()).load(bitmap)
                .skipMemoryCache(true).centerCrop().fitCenter().into(imageView);
    }

    public static String getStringFromDrawableRes(Context context, int id) {
        Resources resources = context.getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(id) + "/"
                + resources.getResourceTypeName(id) + "/"
                + resources.getResourceEntryName(id);
        return path;
    }

    public static Uri registerDatabase(Context context, String path) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", path);
        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues);
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String path = "";
        if (StringUtil.isNull(uri)) {
            return path;
        }
        if ("content".equals(uri.getScheme())) {
            String[] columns = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, columns, null, null, null);
                if (!cursor.moveToFirst()) {
                    return "";
                }
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } finally {
                if (!StringUtil.isNull(cursor)) {
                    cursor.close();
                }
            }
        } else if ("file".equals(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    public static boolean existDataColumn(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (StringUtil.isNull(cursor)) {
            return false;
        }
        String[] columnNames = cursor.getColumnNames();
        if (!Arrays.asList(columnNames).contains(MediaStore.Images.Media.DATA)) {
            return false;
        }
        return cursor.moveToFirst();
    }

    public static Uri getSavedPersonimageFilePath(Context context) {
        Uri dir = getSavedPersonImageDirPath(context);
        dir = Uri.withAppendedPath(dir, "image");
        return dir;
    }

    public static void deleteSavedPersonimage(Context context) {
        File file = new File(getSavedPersonimageFilePath(context).getPath());
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean existPersonimage(Context context) {
        File file = new File(getSavedPersonimageFilePath(context).getPath());
        return file.exists();
    }

    public static Bitmap getSavedPersonImage(Context context) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bm;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(getSavedPersonimageFilePath(context).getPath()));
        } catch (FileNotFoundException e) {
        }

        bm = BitmapFactory.decodeStream(fis, null, option);
        return bm;
    }

    public static void savePersonImage(Context context, Bitmap saveBitmap) {
        if (StringUtil.isNull(saveBitmap)) {
            return;
        }
        File saveFile = new File(getSavedPersonimageFilePath(context).getPath());
        BufferedOutputStream stream;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(saveFile));
        } catch (FileNotFoundException e) {
            return;
        }

        boolean compressResult = saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        if (!compressResult) {
            return;
        }
        ImageUtil.closeStream(stream);
    }

    private static void closeStream(Closeable stream) {
        if (!StringUtil.isNull(stream)) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    private static Uri getSavedPersonImageDirPath(Context context) {
        Uri dir = Uri.fromFile(context.getExternalFilesDir(null));
        dir = Uri.withAppendedPath(dir, "Personimage");
        new File(dir.getPath()).mkdirs();
        Uri nomediaUri = Uri.withAppendedPath(dir, ".nomedia");
        if (!new File(nomediaUri.getPath()).exists()) {
            saveNoMediaFile(dir);
        }
        return dir;
    }

    private static void saveNoMediaFile(Uri saveDir) {
        Uri uri = Uri.withAppendedPath(saveDir, ".nomedia");
        File file = new File(uri.getPath());
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
