package com.innoxyz.data.json.parser;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-6
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public enum JSONType {
    UNASSIGNED(-1),
    BOOLEAN(0),
    INT(1),
    DOUBLE(2),
    LONG(3),
    STRING(4),
    ARRAY(5),
    MAP(6),
    OBJECT(7);

    private final int mValue;
    private JSONType(int value) {
        this.mValue = value;
    }

    public int value() {
        return mValue;
    }

    public static JSONType valueOf(Class<?> type) {
        if ( type==boolean.class || type==Boolean.class ) { return BOOLEAN; }
        if ( type==int.class || type==Integer.class ) {  return INT; }
        if ( type==double.class || type==Double.class ) { return DOUBLE; }
        if ( type==long.class || type==Long.class ) { return LONG; }
        if ( type==String.class ) { return STRING; }
        if ( type.isArray() ) { return ARRAY; }
        if ( Map.class.isAssignableFrom(type) ) { return MAP; }
        return OBJECT;
    }
}
