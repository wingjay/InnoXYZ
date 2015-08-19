package com.innoxyz.data.runtime.beans.topic;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.attachment.Attachment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-26
 * Time: 下午9:32
 * To change this template use File | Settings | File Templates.
 */
public class TopicReply {
    @JsonMap
    public String id;
    @JsonMap
    public String content;
    @JsonMap
    public int creatorId;
    @JsonMap
    public String creatorName;
    @JsonMap
    public String createTime;
    @JsonMap(required = false)
    public Attachment[] attachments;
}
