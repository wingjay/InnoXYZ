package com.innoxyz.data.runtime.beans.notify;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA
 * User: InnoXYZ
 * Date: 13-11-26
 * Time: 下午8:05
 */
public class SubNotify {
    @JsonMap(required = false)
    public String content;
    @JsonMap
    public String creatorName;
    @JsonMap(name = "type")
    public String subNotifyType;
}
