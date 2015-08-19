package com.innoxyz.data.runtime.beans.user;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 下午1:55
 * To change this template use File | Settings | File Templates.
 */
public class Experience {
    @JsonMap
    public String title;
    @JsonMap
    public String classes;
    @JsonMap
    public String summary;
    @JsonMap
    public String start;
    @JsonMap
    public String end;
}
