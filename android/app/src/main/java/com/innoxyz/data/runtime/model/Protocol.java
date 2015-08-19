package com.innoxyz.data.runtime.model;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.innoxyz.data.runtime.model.login.LoginProtocol;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.MainActivity;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.lang.reflect.Type;

import lombok.Data;

/**
 * 这个是我们通信的协议，通信的内容都可以渲染成一个Protocol对象
 * Created by lishuang on 2014/9/18.
 */
@Data
public class Protocol<T> {

	//返回的状态，一般而言是ok或者error.
    @SerializedName("action_returns")
	private String actionReturns;

	//错误的原因
	private String error;
    //成功消息
    private String success;

    //返回数据
    private T data;
	private Pager<T> pager;

	//协议返回表示是否成功
	public boolean isOk(){
        if ("login".equalsIgnoreCase(this.actionReturns)) {
            login();
        }
        return "ok".equalsIgnoreCase(this.actionReturns) || "success".equalsIgnoreCase(this.actionReturns) || "y".equalsIgnoreCase(this.actionReturns);
	}
    //是否未登录
    public static void login(){
        //登录
        String userName = InnoXYZApp.getApplication().getCurrentUserName();
        String userKey = InnoXYZApp.getApplication().getCurrentUserKey();
        RequestParams params = new RequestParams();
        params.put("username", userName);
        params.put("password", userKey);
        Factory.getHttpClient().post(URL.LOGIN, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

	//将一个json字符串转换成一个protocol对象
	public static Protocol fromJson(String json){
		Gson gson=new Gson();

		Protocol protocol;
		try {
			protocol=gson.fromJson(json,Protocol.class);
			return protocol;
		}catch (Exception e){
			protocol=new Protocol();
			protocol.actionReturns ="error";
			protocol.error="返回的数据格式错误";
			e.printStackTrace();
			System.out.println("Json转换成对象时出异常！");
		}

		return protocol;
	}


    // 非静态方法！将一个json字符串转换成一个带泛型的protocol对象,type就是泛型的实际类型
    // 使用：Protocol<Comment> protocol = Protocol.fromJson(response, new TypeToken<Protocol<Comment>>(){}.getType());
    public Protocol<T> fromJson(String json,Type type){
        Gson gson=new Gson();

        Protocol<T> protocol;
        try {
            protocol = gson.fromJson(json, type);
            return protocol;
        }catch (Exception e){
            protocol = new Protocol<T>();
            protocol.actionReturns ="error";
            protocol.error="返回的数据格式错误";
            e.printStackTrace();
            System.out.println("Json转换成对象时出异常！");
        }
        return protocol;
    }
    

	//将一个对象转换成一个json字符串
	public String toJson(Object obj){
		try {
			Gson gson=new Gson();

			return gson.toJson(this);

		}catch (Exception e){
			e.printStackTrace();
			System.out.println("对象转成json字符串时出异常！");
		}

		return null;
	}

}
