package com.picture.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.picture.db.DBManager;
import com.picture.entity.Tag;
import com.picture.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TagManager extends DBManager {
    public static final String TABLE_NAME = "tb_tag";
    public static final String FIELD_TAG_NO = "tag_no";
    public static final String FIELD_TAG_NAME = "tag_name";
    public static final String FIELD_FILE_NAME = "file_name";
    public static final String FIELD_FILE_PATH = "file_path";
    public static final String FIELD_LOCATION = "location";

    public TagManager(Context context) {
        super(context);
    }

    private String queryMaxCode() {
        String maxCode = "";
        String sqlCmd = " select tag_no from tb_tag order by tag_no desc limit 1 ";
        Cursor cursor = rawQuery(sqlCmd, null);
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String tagNo = cursor.getString(cursor.getColumnIndex(FIELD_TAG_NO));
                String tmpCode = String.valueOf(Integer.parseInt(tagNo) + 1);
                maxCode = StringUtil.padLeft(tmpCode, 10);
            }
            cursor.close();
        }
        if (TextUtils.isEmpty(maxCode)) {
            maxCode = "0000000001";
        }
        return maxCode;
    }

    public long add(String tagName, String fileName, String filePath, String location) {
        ContentValues cvs = new ContentValues();
        cvs.put(FIELD_TAG_NO, queryMaxCode());
        cvs.put(FIELD_TAG_NAME, tagName);
        cvs.put(FIELD_FILE_NAME, fileName);
        cvs.put(FIELD_FILE_PATH, filePath);
        cvs.put(FIELD_LOCATION, location);
        long ret = insert(TABLE_NAME, cvs);
        return ret;
    }

    public Tag query(String fileName, String filePath) {
        String selection = " file_name = ? and file_path = ? ";
        String[] selArgs = new String[]{fileName, filePath};
        Cursor cursor = query(TABLE_NAME, selection, selArgs);
        Tag tag = null;
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                tag = new Tag();
                String tagNo = cursor.getString(cursor.getColumnIndex(FIELD_TAG_NO));
                String tagName = cursor.getString(cursor.getColumnIndex(FIELD_TAG_NAME));
                String name = cursor.getString(cursor.getColumnIndex(FIELD_FILE_NAME));
                String path = cursor.getString(cursor.getColumnIndex(FIELD_FILE_PATH));
                String location = cursor.getString(cursor.getColumnIndex(FIELD_LOCATION));

                tag.setTagNo(tagNo);
                tag.setTagName(tagName);
                tag.setFileName(name);
                tag.setFilePath(path);
                tag.setLocation(location);
            }
        }
        return tag;
    }

    public List<Tag> queryTags(String tagName, String name) {
        List<Tag> tags = new ArrayList<>();
        String sqlCmd = "";
        if ("person".equals(tagName)) {
            sqlCmd = " select * from tb_tag where tag_name like '%" + tagName + "%' and file_name like '%" + name.toLowerCase() + "%'";
        } else if ("location".equals(tagName)) {
            sqlCmd = " select * from tb_tag where tag_name like '%" + tagName + "%' and location like '%" + name.toLowerCase() + "%'";
        }

        Cursor cursor = rawQuery(sqlCmd, null);
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String tagNo = cursor.getString(cursor.getColumnIndex(FIELD_TAG_NO));
                String tag = cursor.getString(cursor.getColumnIndex(FIELD_TAG_NAME));
                String fileName = cursor.getString(cursor.getColumnIndex(FIELD_FILE_NAME));
                String path = cursor.getString(cursor.getColumnIndex(FIELD_FILE_PATH));

                Tag tagObj = new Tag();
                tagObj.setTagNo(tagNo);
                tagObj.setTagName(tag);
                tagObj.setFileName(fileName);
                tagObj.setFilePath(path);
                tags.add(tagObj);
            }
        }
        return tags;
    }

    public int update(String tagNo, String tagName) {
        ContentValues cvs = new ContentValues();
        cvs.put(FIELD_TAG_NAME, tagName);

        String selection = " tag_no = ? ";
        String[] selArgs = new String[]{tagNo};

        int ret = update(TABLE_NAME, cvs, selection, selArgs);
        return ret;
    }

    public int delete(String tagNo) {
        String whereClause = " tag_no = ? ";
        String[] whereArgs = new String[]{tagNo};
        int ret = delete(TABLE_NAME, whereClause, whereArgs);
        return ret;
    }
}
