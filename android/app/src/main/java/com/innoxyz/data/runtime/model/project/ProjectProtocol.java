package com.innoxyz.data.runtime.model.project;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 这个是我们通信的协议，通信的内容都可以渲染成一个Protocol对象
 * Created by lishuang on 2014/9/18.
 */
@Data
public class ProjectProtocol{

	//返回的状态，一般而言是ok或者error.
    @SerializedName("action_returns")
	private String actionReturns;

	//错误的原因
	private String error;
    //成功消息
    private String success;

    //返回数据
	private Pager<Project> data;

	//将一个json字符串转换成一个protocol对象
	public static ProjectProtocol fromJson(String json){
		Gson gson=new Gson();

		ProjectProtocol protocol;
		try {
			protocol=gson.fromJson(json,ProjectProtocol.class);
			return protocol;
		}catch (Exception e){
			protocol=new ProjectProtocol();
			protocol.actionReturns ="error";
			protocol.error="返回的数据格式错误";
			e.printStackTrace();
			System.out.println("Json转换成对象时出异常！");
		}

		return protocol;
	}
    //协议返回表示是否成功
    public boolean isOk(){
        if ("login".equalsIgnoreCase(this.actionReturns)) {
            Protocol.login();
        }
        return "ok".equalsIgnoreCase(this.actionReturns) || "success".equalsIgnoreCase(this.actionReturns) || "y".equalsIgnoreCase(this.actionReturns);
    }

}
