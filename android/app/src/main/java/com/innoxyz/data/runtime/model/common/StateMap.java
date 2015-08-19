package com.innoxyz.data.runtime.model.common;

import java.util.HashMap;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
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
