package com.picture.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil extends StringUtils {
    public static String padLeft(String oldStr, int totalWidth) {
        if (StringUtils.isEmpty(oldStr) == true) {
            oldStr = "";
        }
        oldStr = oldStr.trim();
        if (totalWidth <= oldStr.length()) {
            return oldStr;
        }

        StringBuilder sb = new StringBuilder(totalWidth);
        int fill = totalWidth - oldStr.length();

        for (int i = 0; i < fill; i++) {
            sb.append("0");
        }

        sb.append(oldStr);
        return sb.toString();
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isEmpty(CharSequence value) {
        return isNull(value) || value.length() == 0;
    }
}
