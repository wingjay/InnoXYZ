package com.innoxyz.data.runtime.beans.user;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 下午2:01
 * To change this template use File | Settings | File Templates.
 */
public class Paper {
    @JsonMap
    public String title;
    @JsonMap
    public String abstracts;
    @JsonMap
    public String date;
    @JsonMap
    public String publisher;
    @JsonMap
    public String keywords;
}
