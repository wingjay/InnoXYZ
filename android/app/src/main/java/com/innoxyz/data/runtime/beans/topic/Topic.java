package com.innoxyz.data.runtime.beans.topic;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.attachment.Attachment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-26
 * Time: 下午9:29
 * To change this template use File | Settings | File Templates.
 */
public class Topic {
    @JsonMap
    public String id;
    @JsonMap
    public String subject;
    @JsonMap
    public String content;
    @JsonMap
    public String createTime;
    @JsonMap(required = false)
    public String lastPostId = null;
    @JsonMap(required = false)
    public Attachment[] attachments;
    @JsonMap
    public String creatorName;
    @JsonMap
    public int creatorId;

    public Post lastPost;
}
