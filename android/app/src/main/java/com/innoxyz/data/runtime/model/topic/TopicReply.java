package com.innoxyz.data.runtime.model.topic;


import com.innoxyz.data.runtime.model.attachment.Attachment;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class TopicReply {
    public String id;
    public String content;
    public int creatorId;
    public String creatorName;
    public String createTime;
    public List<Attachment> attachments;
}
