package com.innoxyz.data.runtime.beans.user;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 下午1:09
 * To change this template use File | Settings | File Templates.
 */
public class Education {
    @JsonMap
    public String start;
    @JsonMap
    public String end;
    @JsonMap
    public String major;
    @JsonMap
    public String university;
    @JsonMap
    public String degree;
}
