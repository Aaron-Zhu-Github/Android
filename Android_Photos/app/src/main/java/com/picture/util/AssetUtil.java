package com.picture.util;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AssetUtil {

    public static String readAssets(Context context, String file) {
        List<String> values = new ArrayList<>();
        try {
            values = IOUtils.readLines(context.getAssets().open(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value = StringUtil.join(values, " ");
        return value;
    }
}
