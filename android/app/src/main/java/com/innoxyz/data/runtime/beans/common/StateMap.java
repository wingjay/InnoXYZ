package com.innoxyz.data.runtime.beans.common;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午2:22
 * To change this template use File | Settings | File Templates.
 */
public class StateMap {
    public static HashMap<String, String> map;
    static {
        map = new HashMap<String, String>();
        map.put("NEW", "新建");
        map.put("SUSPEND", "挂起");
        map.put("IN_PROGRESS", "进行中");
        map.put("COMPLETE", "完成");
    }

    public static String getText(String name) {
        if ( map.containsKey(name) ) {
            return map.get(name);
        } else {
            return "其他";
        }
    }
}
