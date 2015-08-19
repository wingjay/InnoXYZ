package com.innoxyz.data.runtime.beans.document;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-28
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
public class Document {
    @JsonMap
    public String id;
    @JsonMap
    public int length;
    @JsonMap
    public int creatorId;
    @JsonMap(required = false)
    public String creatorName;
    @JsonMap(required = false)
    public String createTime;
    @JsonMap
    public String type;
    @JsonMap(required = false)
    public String contentType;
    @JsonMap(required = false)
    public String dispName = "";
}
