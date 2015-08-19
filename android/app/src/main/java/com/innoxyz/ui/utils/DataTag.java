package com.innoxyz.ui.utils;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午4:34
 * To change this template use File | Settings | File Templates.
 */
public class DataTag {
    public static String makeTag(Class cls, String tag, String name) {
        return String.format("%s[%s].%s", cls.getName(), tag, name);
    }
}
