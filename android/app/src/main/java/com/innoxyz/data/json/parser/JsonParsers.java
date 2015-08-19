package com.innoxyz.data.json.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-6
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
public class JsonParsers { //todo：反射机制

    final Method[] methodMap = new Method[JSONType.values().length];
    final Method[] arrayMethodMap = new Method[JSONType.values().length];
    private final HashMap<Class<?>, JsonParser> parserMap = new HashMap<Class<?>, JsonParser>();

    public JsonParsers() throws NoSuchMethodException {
        methodMap[JSONType.ARRAY.value()] = JSONObject.class.getMethod("getJSONArray", String.class);
        methodMap[JSONType.BOOLEAN.value()] = JSONObject.class.getMethod("getBoolean", String.class);
        methodMap[JSONType.DOUBLE.value()] = JSONObject.class.getMethod("getDouble", String.class);
        methodMap[JSONType.INT.value()] = JSONObject.class.getMethod("getInt", String.class);
        methodMap[JSONType.LONG.value()] = JSONObject.class.getMethod("getLong", String.class);
        methodMap[JSONType.OBJECT.value()] = JSONObject.class.getMethod("getJSONObject", String.class);
        methodMap[JSONType.MAP.value()] = JSONObject.class.getMethod("getJSONObject", String.class);
        methodMap[JSONType.STRING.value()] = JSONObject.class.getMethod("getString", String.class);
        for(Method method : methodMap) {
            if ( method!=null ) {
                method.setAccessible(true);
            }
        }

        arrayMethodMap[JSONType.ARRAY.value()] = JSONArray.class.getMethod("getJSONArray", int.class);
        arrayMethodMap[JSONType.BOOLEAN.value()] = JSONArray.class.getMethod("getBoolean", int.class);
        arrayMethodMap[JSONType.DOUBLE.value()] = JSONArray.class.getMethod("getDouble", int.class);
        arrayMethodMap[JSONType.INT.value()] = JSONArray.class.getMethod("getInt", int.class);
        arrayMethodMap[JSONType.LONG.value()] = JSONArray.class.getMethod("getLong", int.class);
        arrayMethodMap[JSONType.OBJECT.value()] = JSONArray.class.getMethod("getJSONObject", int.class);
        arrayMethodMap[JSONType.MAP.value()] = JSONArray.class.getMethod("getJSONObject", int.class);
        arrayMethodMap[JSONType.STRING.value()] = JSONArray.class.getMethod("getString", int.class);
        for(Method method : arrayMethodMap) {
            if ( method!=null ) {
                method.setAccessible(true);
            }
        }
    }

    public <T> JsonParser<T> getParser(Class<T> tClass) {
        JsonParser<T> ret = parserMap.get(tClass);
        if ( ret == null ) {
            ret = new JsonParser<T>(tClass);
            ret.creator = this;
            parserMap.put(tClass, ret);
        }
        return ret;
    }
}
