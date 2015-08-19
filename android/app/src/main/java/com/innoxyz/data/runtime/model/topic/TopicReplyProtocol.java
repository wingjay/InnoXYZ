package com.innoxyz.data.runtime.model.topic;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class TopicReplyProtocol {
    @SerializedName("action_returns")
    public String actionReturns;


    public Pager<TopicReply> pager;

    public Topic topic;

    public String error;
    //协议返回表示是否成功
    public boolean isOk(){
        if ("login".equalsIgnoreCase(this.actionReturns)) {
            Protocol.login();
        }
        return "ok".equalsIgnoreCase(this.actionReturns) || "success".equalsIgnoreCase(this.actionReturns) || "y".equalsIgnoreCase(this.actionReturns);
    }

    //将一个json字符串转换成一个protocol对象
    public static TopicReplyProtocol fromJson(String json){
        Gson gson=new Gson();

        TopicReplyProtocol protocol;
        try {
            protocol=gson.fromJson(json,TopicReplyProtocol.class);
            return protocol;
        }catch (Exception e){
            protocol=new TopicReplyProtocol();
            protocol.actionReturns ="error";
            protocol.error="返回的数据格式错误";
            e.printStackTrace();
            System.out.println("Json转换成对象时出异常！");
        }

        return protocol;
    }


}
