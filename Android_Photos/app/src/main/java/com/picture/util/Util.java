package com.picture.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {

    private static final int DETECT_YUWEN_COUNT = 12;

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public static String readAssets(Context context, String file) {
        List<String> values = new ArrayList<>();
        try {
            values = IOUtils.readLines(context.getAssets().open(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value = StringUtils.join(values, " ");
        return value;
    }

    public static List<Integer> getRandomList(int totalCount) {
        int minNum = 0;
        int maxNum = totalCount;
        Random rnd = new Random();
        List<Integer> intList = new ArrayList<>();
        for (int i = 0; i < DETECT_YUWEN_COUNT && i < totalCount; i++) {
            int rndNum = rnd.nextInt(maxNum) % (maxNum - minNum + 1) + minNum;
            if (intList.contains(rndNum) == true) {
                i--;
            } else {
                intList.add(rndNum);
            }
        }
        return intList;
    }

    public static List<Integer> getSwitchList(List<Integer> intList) {
        return getRandomList(intList.size());
    }

    public static int getPlayPos(int totalCount) {
        Random rnd = new Random();
        int minNum = 0;
        int maxNum = 0;
        if (totalCount <= DETECT_YUWEN_COUNT) {
            maxNum = totalCount;
        } else {
            maxNum = DETECT_YUWEN_COUNT;
        }
        int rndNum = rnd.nextInt(maxNum) % (maxNum - minNum + 1) + minNum;
        return rndNum;
    }
}
