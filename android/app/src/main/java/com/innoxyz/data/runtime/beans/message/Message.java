package com.innoxyz.data.runtime.beans.message;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.User;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
public class Message {
    @JsonMap
    public String id;
    @JsonMap
    public String subject;
    @JsonMap
    public int senderId;
    @JsonMap
    public String senderName;
    @JsonMap
    public String sendTime;
    @JsonMap
    public String content;
    @JsonMap(required = false)
    public User[] recivers;
}
