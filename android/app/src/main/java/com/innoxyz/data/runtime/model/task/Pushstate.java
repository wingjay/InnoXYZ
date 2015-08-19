package com.innoxyz.data.runtime.model.task;

import com.google.gson.annotations.SerializedName;
import com.innoxyz.data.runtime.model.attachment.Attachment;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
//任务回复内容
@Data
public class Pushstate {
    //任务回复创建者
    public String creatorName;
    public int creatorId;
    public String createTime;
    //任务回复内容
    public String comment;

    //任务附件
    public List<Attachment> attachments;
    //任务状态更新
    public Modification modifications;

    @Data
    public static class Modification {
        //状态更新
        @SerializedName("state")
        public Change stateChange;
        //名称更新
        @SerializedName("name")
        public Change nameChange;
    }

    //更新
    @Data
    public static class Change {
        //原有值
        public String oldValue;
        //更新值
        public String newValue;
    }
}

