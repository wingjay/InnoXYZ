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
public class Topic {
    public String id;
    public String subject;
    public String content;
    public String createTime;

    public List<Attachment> attachments;
    public String creatorName;
    public int creatorId;
    //最近回复
    public String lastPostId;
    public String lastPostTime;


}
