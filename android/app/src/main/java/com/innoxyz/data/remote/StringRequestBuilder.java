package com.innoxyz.data.remote;

import android.app.Activity;
import android.util.Log;

import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.util.Factory;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.cookie.Cookie;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by laborish on 13-12-15.
 */
public class StringRequestBuilder {

    //public StringRequest stringRequest =

    private int method;
    //主机
    private String baseURI = AddressURIs.HOST;
    //请求URI
    private String relURI = "";
    //请求参数
    private Map<String, String> params = new HashMap<String, String>();
    //给当前实例绑定http响应监听器，监听器具体内容要在实例中实现
    private Response.Listener<String> listener = null;
    private boolean reqFromWeb = true;
    private Activity currActivity;

    //创建字符串请求构造器，传入Activity
    public StringRequestBuilder(Activity a){
        currActivity = a;
    }

    public StringRequestBuilder reqFromWeb(boolean b){
        reqFromWeb = b;
        return this;
    }

    //获取请求信息-路径和方法
    public StringRequestBuilder setRequestInfo(RequestInfo ri) {
        setSubURI(ri.relURI);
        setMethod(ri.method);
        return this;
    }

    public StringRequestBuilder setMethod(String method) {

        if ( method.compareToIgnoreCase("get")==0 ) {
            this.method = Method.GET;
        } else {
            this.method = Method.POST;
        }
        return this;
    }

    protected StringRequestBuilder setBaseURI(String uri) {
        this.baseURI = uri;
        return this;
    }

    public StringRequestBuilder setSubURI(String uri) {
        this.relURI = uri;
        return this;
    }
    //添加请求参数
    public StringRequestBuilder addParameter(String key, String val){
        this.params.put(key,val);
        return this;
    }
    //为当前 StringRequestBuilder 绑定一个http响应监听器，具体监听器内容要在实例中实现
    public StringRequestBuilder setOnResponseListener(Response.Listener<String> listener){
        this.listener = listener;
        return this;
    }
    //获取请求，返回 StringRequestWithCache
    public StringRequestWithCache getRequest(){
        String uri = baseURI+relURI;
        String uriWithParams = uri;
        //将请求参数附加到请求Uri中
        uriWithParams += "?";
        for(String key : params.keySet()){
            uriWithParams += key + "=" + params.get(key) + "&";
        }//uriWithParams = baseURI/user/login.action?password=yinjie&username=yinjie@qq.com&
        //截取字符串
        uriWithParams = uriWithParams.substring(0,uriWithParams.length()-1);//uriWithParams = baseURI/user/login.action?password=yinjie&username=yinjie@qq.com

        if(method == Method.GET){
            uri = uriWithParams;
        }
        StringRequestWithCache stringRequest = new StringRequestWithCache(method,uri,listener,null);
        stringRequest.setKeyUrl(uriWithParams);
        Log.i("Request", "stringstringRequest: " + stringRequest.toString());
        return stringRequest;
    }

    //执行网络请求 先从缓存中找，未命中则将当前请求添加到请求队列中
    public void request(){
        final StringRequestWithCache stringRequest = getRequest();
        //缓存未实现！
        if( !reqFromWeb && InnoXYZApp.getApplication().getWebCache().containsKey(stringRequest.getKeyUrl())){
//            Log.i("CACHE","GET: [" + stringRequest.getKeyUrl() + "][" + InnoXYZApp.getApplication().getWebCache().get(stringRequest.getKeyUrl()) + "]");
//
//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    listener.onResponse(InnoXYZApp.getApplication().getWebCache().get(stringRequest.getKeyUrl()));
//                }
//            };
//            currActivity.runOnUiThread(r);
        }
        else{
            //no cache hit
            //将该请求加入到 RequestQueue 队列中
            InnoXYZApp.getApplication().getRequestQueue().add(stringRequest);
        }

    }

    class StringRequestWithCache extends StringRequest{

        public StringRequestWithCache(int method, String url, Response.Listener<String> listener,
                                      Response.ErrorListener errorListener){
            super(method, url, listener, errorListener);

        }

        String keyUrl = "";

        public void setKeyUrl(String s){
            keyUrl = s;
        }

        public String getKeyUrl(){
            return keyUrl;
        }

        @Override
        public Map<String, String> getParams() {
            Log.i("Request","params: "+params.toString());
            return params;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError { //拼接服务器请求的头文件：Cookie、Accept、Referer等，具体要看Volley的http请求
            Map<String, String> headers = super.getHeaders();

            if( headers == null || headers.equals(Collections.emptyMap())){
                headers = new HashMap<>();
            }

            //InnoXYZApp.getApplication().addIcCookie(headers); //把存在 sharedPreferences 中的cookie值存入headers中发送给服务器
            //Cookie IcCookie = InnoXYZApp.getApplication().getIcCookie();
            Cookie IcCookie = Factory.getIcCookie();
            headers.put("Cookie", IcCookie.getName() + "=" + IcCookie.getValue());

            headers.put("Accept", "application/json");
            headers.put("Referer", baseURI);
            headers.put("X-Requested-With", "XMLHttpRequest");

            if(method == Method.POST){
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            }
            return headers;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            // since we don't know which of the two underlying network vehicles
            // will Volley use, we have to handle and store session cookies manually
            //InnoXYZApp.getApplication().checkIcCookie(response.headers);

            //add response to cache
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            InnoXYZApp.getApplication().getWebCache().put(keyUrl, parsed);
            Log.i("CACHE","ADD: [" + keyUrl + "][" + parsed + "]");

            return super.parseNetworkResponse(response);
        }

    }


}
