package com.innoxyz.ui.utils;

import java.lang.reflect.Array;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-25
 * Time: 下午3:56
 * To change this template use File | Settings | File Templates.
 */
public class ArrayUtils {
    public static <T> T[] merge(T[] l1, T[] l2) {
        if ( l1 == null || l1.length == 0) {
            return l2;
        }
        if ( l2 == null || l2.length == 0) {
            return l1;
        }
        Class<?> clazz = l1.getClass().getComponentType();
        int length = l1.length + l2.length;
        T[] ret = (T[]) Array.newInstance(clazz, length);
        for(int i=0; i<l1.length; i++) {
            ret[i] = l1[i];
        }
        for(int i=0; i<l2.length; i++) {
            ret[i+l1.length] = l2[i];
        }
        return ret;
    }
}
