package com.innoxyz.data.runtime.beans.task;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.attachment.Attachment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午1:03
 * To change this template use File | Settings | File Templates.
 */
//任务回复内容
public class Pushstate {
    //任务回复创建者
    @JsonMap
    public String creatorName;
    @JsonMap
    public int creatorId;
    @JsonMap
    public String createTime;
    //任务回复内容
    @JsonMap
    public String comment;

    //任务附件
    @JsonMap(required = false)
    public Attachment[] attachments = null;
    //任务状态更新
    @JsonMap(required = false)
    public Modification modifications = null;

    public static class Modification {
        //状态更新
        @JsonMap(name = "state", required = false)
        public Change stateChange = null;
        //名称更新
        @JsonMap(name = "name", required = false)
        public Change nameChange = null;
    }
    //更新
    public static class Change {
        //原有值
        @JsonMap
        public String oldValue;
        //更新值
        @JsonMap
        public String newValue;
    }
}

