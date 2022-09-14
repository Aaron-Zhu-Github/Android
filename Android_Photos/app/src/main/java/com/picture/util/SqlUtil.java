package com.picture.util;

import android.text.TextUtils;

public class SqlUtil {
    public static final String EQUAL = " = ";
    public static final String MARK_QUESTION = " ? ";
    public static final String AND = " AND ";
    private StringBuilder sql;

    public SqlUtil() {
        sql = new StringBuilder();
    }

    public String buildSelect(String... appendix) {
        String sql = "";
        String link;
        for (String param : appendix) {
            link = param + EQUAL + MARK_QUESTION;
            if (TextUtils.isEmpty(sql)) {
                sql = link;
            } else {
                sql += AND + link;
            }
        }
        return sql;
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}
