package com.innoxyz.data.runtime.model.notify;

import com.innoxyz.data.runtime.model.attachment.Attachment;
import com.innoxyz.data.runtime.model.common.NamedText;
import com.innoxyz.data.runtime.model.topic.Post;
import com.innoxyz.data.runtime.model.user.User;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class NotifyThing {
    //TASK
    public String id;
    public String name;
    public List<User> assignees;
    public String deadline;
    public NamedText state;
    public NamedText priority;
    public String description;
    public int creatorId;
    public String creatorName;

    //TOPIC
    public String subject;
    public String content;
    public String createTime;
    public String lastPostId;
    public List<Attachment> attachments;
    public String hostType;
    public Post lastPost;

    //FILE
    public int length;
    public String type;
    public String contentType;
    public String dispName;

    //ANNOUNCEMENT
    public String title;

    public String getName() {
        if(subject!=null){
            //topic标题
            return subject;
        }
        else if(title!=null){
            return title;
        }
        else if(dispName!=null){
            return dispName;
        }
        else if(name!=null){
            return name;
        }
        return "";
    }

}
