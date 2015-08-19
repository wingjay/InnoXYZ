package com.innoxyz.data.runtime.beans.common;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午12:42
 * To change this template use File | Settings | File Templates.
 */
public class NamedText {
    @JsonMap
    public String text;
    @JsonMap
    public String name;

    @Override
    public String toString() {
        return text;
    }
}
