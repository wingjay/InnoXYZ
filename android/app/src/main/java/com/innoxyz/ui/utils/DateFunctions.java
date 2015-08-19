package com.innoxyz.ui.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-8
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class DateFunctions {
    private static SimpleDateFormat sdfs[] = new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    };
    //date：2014-11-28T00:00:00.000Z
    public static Date parseDate(String date) throws Exception {
        for(SimpleDateFormat sdf : sdfs) {
            try {
                return sdf.parse(date);
            } catch (Exception e) {}
        }
        throw new Exception();
    }
    //original：2014-11-28T00:00:00.000Z  format：yyyy-M-dd failed：unknown
    public static String RewriteDate(String original, String format, String failed) {
        try {
            return DateFormat.format(format, parseDate(original)).toString();
        } catch (Exception e) {
            return failed;
        }
    }

    public static String RewriteDate(String original) {
        return RewriteDate(original, "yyyy-M-dd", "unknown");
    }
}
