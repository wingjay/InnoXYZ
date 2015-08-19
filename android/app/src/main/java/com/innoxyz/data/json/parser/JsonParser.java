package com.innoxyz.data.json.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-6
 * Time: 下午4:32
 * To change this template use File | Settings | File Templates.
 */
//todo：不理解
class AnnotatedField {
    String name;
    JSONType type;
    Field field;
    boolean required;

    AnnotatedField(JsonMap map, Field field) {
        if ( map.name().length() > 0 ) {
            name = map.name();
        } else {
            name = field.getName();
        }
        if ( map.type() != JSONType.UNASSIGNED ) {
            type = map.type();
        } else {
            type = JSONType.valueOf(field.getType());
        }
        field.setAccessible(true);
        this.field = field;
        this.required = map.required();
    }
}

public class JsonParser<T> {

    private Class<T> classType;
    private AnnotatedField[] storedFields;
    JsonParsers creator;

    JsonParser(Class<T> tClass) {
        classType = tClass;
        List<AnnotatedField> list = new ArrayList<AnnotatedField>();
        for(Field field : classType.getFields()) {
            JsonMap map = field.getAnnotation(JsonMap.class);
            if ( map != null ) {
                list.add(new AnnotatedField(map, field));
            }
        }
        storedFields = list.toArray(new AnnotatedField[0]);
    }

    protected Object ParseArrayElement(JSONArray array, int index, Class<?> tClass) throws Exception {
        JSONType type = JSONType.valueOf(tClass);
        if ( type == JSONType.OBJECT ) {
            return creator.getParser(tClass).Parse(array.getJSONObject(index));
        }
        if ( type == JSONType.ARRAY ) {
            JSONArray arr = array.getJSONArray(index);
            Class<?> compType = tClass.getComponentType();
            Object value = Array.newInstance(compType, arr.length());
            for(int i=0; i<arr.length(); i++) {
                Array.set(value, i, ParseArrayElement(arr, i, compType));
            }
            return value;
        } else {
            return creator.arrayMethodMap[type.value()].invoke(array, index);
        }
    }
    @SuppressWarnings("unchecked")
    public T Parse(JSONObject jsonObject) throws Exception {
        T ret = classType.newInstance();
        for(AnnotatedField af : storedFields) {
            if ( !af.required && !jsonObject.has(af.name) ) {
                continue;
            }
            Object value = null;
            if ( af.type == JSONType.ARRAY ) {
                JSONArray arr = jsonObject.getJSONArray(af.name);
                Class<?> compType = af.field.getType().getComponentType();
                value = Array.newInstance(compType, arr.length());
                for(int i=0; i<arr.length(); i++) {
                    Array.set(value, i, ParseArrayElement(arr, i, compType));
                }
            } else if ( af.type == JSONType.OBJECT ) {
                if ( jsonObject.isNull(af.name) ) {
                    value = null;
                } else {
                    value = creator.getParser(af.field.getType()).Parse(jsonObject.getJSONObject(af.name));
                }
            } else if ( af.type == JSONType.MAP ) {
                Map map = (Map) af.field.getType().newInstance();
                JSONObject json = jsonObject.getJSONObject(af.name);
                Type valueType = ((ParameterizedType) af.field.getGenericType()).getActualTypeArguments()[1];
                Iterator keyIter = json.keys();
                while ( keyIter.hasNext() ) {
                    String key = (String) keyIter.next();
                    map.put(key, creator.getParser((Class) valueType).Parse(json.getJSONObject(key)));
                }
                value = map;
            } else {
                value = creator.methodMap[af.type.value()].invoke(jsonObject, af.name);
            }
            af.field.set(ret, value);
        }
        return ret;
    }
}
