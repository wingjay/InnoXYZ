package com.innoxyz.ui.utils;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-28
 * Time: 下午1:07
 * To change this template use File | Settings | File Templates.
 */
public class FilesizeFunctions {
    final static String[] units = new String[]{"B", "KB", "MB", "GB"};
    public static String dispSize(int length) {
        int unit = 0;
        double size = length;
        while(size > 1000) {
            unit ++;
            size /= 1024;
        }
        int m = 1, n = 2;
        if ( size >= 10 ) {
            m++;
            n--;
        }
        if ( size >= 100 ) {
            m++;
            n--;
        }
        if ( unit == 0 ) {
            n = 0;
        }
        return String.format(String.format("%%%d.%df%s", m, n, units[unit]), size);
    }
}
