package com.innoxyz.data.runtime.model.announcement;

import com.innoxyz.data.runtime.model.attachment.Attachment;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class Announcement {
    public String id;
    //创建者
    public String creatorName;
    public String title;
    public String content;
    public String createTime;
    //附件
    public List<Attachment> attachments;
}
