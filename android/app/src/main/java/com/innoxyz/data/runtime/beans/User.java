package com.innoxyz.data.runtime.beans;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午2:34
 * To change this template use File | Settings | File Templates.
 */
public class User {
    @JsonMap
    public int id;
    @JsonMap(required = false)
    public String realName;
    @JsonMap(required = false)
    public String realname;
    @JsonMap(required = false)
    public String name;
    @JsonMap(required = false)
    public String email;
}
