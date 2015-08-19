package com.innoxyz.data.runtime;

import android.content.Context;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午3:19
 * To change this template use File | Settings | File Templates.
 */
//运行时数据管理
public class RuntimeDataManager {
    //数据中心 一个context对象对应一个Hash表
    private HashMap<Context, HashMap<String, Object>> dataCenter = new HashMap<Context, HashMap<String, Object>>();

    public RuntimeDataManager() {}

    //为一个context对象创建一个新的Hash表或者获取这个context对象的Hash表
    //context => HashMap<key,value>
    public HashMap<String, Object> Add(Context context, boolean alwaysCreateNew) {
        if ( alwaysCreateNew || !dataCenter.containsKey(context) ) {
            return dataCenter.put(context, new HashMap<String, Object>());
        } else {
            return dataCenter.get(context);
        }
    }
    //从数据中心中删除一个context对象
    public void Release(Context context) {
        dataCenter.remove(context);
    }
    //根据context获取它的一个Hash表
    public HashMap<String, Object> get(Context context) {
        return dataCenter.get(context);
    }
}
