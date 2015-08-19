package com.innoxyz.data.runtime.beans.topic;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-26
 * Time: 下午9:29
 * To change this template use File | Settings | File Templates.
 */
public class Post {
    @JsonMap
    public String id;
    @JsonMap
    public String creatorName;
    @JsonMap
    public String createTime;
}
