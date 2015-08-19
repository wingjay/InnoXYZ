package com.innoxyz.data.runtime.model.message;

import com.innoxyz.data.runtime.model.user.User;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class Message {
    public String id;
    public String subject;
    public int senderId;
    public String senderName;
    public String sendTime;
    public String content;
    public List<User> recivers;
}
