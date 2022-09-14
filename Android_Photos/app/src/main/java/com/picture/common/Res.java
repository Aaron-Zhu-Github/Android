package com.picture.common;

import android.os.Environment;

/**
 * Common resource tools
 */
public class Res {

    /**
     * Picture path
     */
    public static class url {
        /**
         * Root folder name
         */
        public static final String media_collection = "eBook";
        /**
         * Picture storage path
         */
        public static final String media_pic = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + media_collection + "/image";
        /**
         * Phone photo path
         */
        public static final String photo_uri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
        /**
         * Custom photo album
         */
        public static final String picture_other_uri = photo_uri + "/define";
    }

    public static class function {
        /**
         * Edit
         */
        public static final String edit = "Edit";
        /**
         * Delete
         */
        public static final String delete = "Delete";
    }
}
