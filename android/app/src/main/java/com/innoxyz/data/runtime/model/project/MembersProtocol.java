package com.innoxyz.data.runtime.model.project;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.user.User;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class MembersProtocol {
    //返回的状态，一般而言是ok或者error.
    @SerializedName("action_returns")
    private String actionReturns;

    @SerializedName("links")
    public List<Member> members;

    //协议返回表示是否成功
    public boolean isOk(){
        if ("login".equalsIgnoreCase(this.actionReturns)) {
            Protocol.login();
        }
        return "ok".equalsIgnoreCase(this.actionReturns) || "success".equalsIgnoreCase(this.actionReturns) || "y".equalsIgnoreCase(this.actionReturns);
    }

    //将一个json字符串转换成一个protocol对象
    public static MembersProtocol fromJson(String json){
        Gson gson=new Gson();

        MembersProtocol protocol;
        try {
            protocol=gson.fromJson(json,MembersProtocol.class);
            return protocol;
        }catch (Exception e){
            protocol=new MembersProtocol();
            protocol.actionReturns ="error";
            e.printStackTrace();
            System.out.println("Json转换成对象时出异常！");
        }

        return protocol;
    }

}
