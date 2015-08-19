package com.innoxyz.data.runtime.beans.announcement;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午3:07
 * To change this template use File | Settings | File Templates.
 */
public class Announcement {
    @JsonMap
    public String id;
    @JsonMap
    public String title;
    @JsonMap
    public String content;
    @JsonMap
    public String createTime;
}
