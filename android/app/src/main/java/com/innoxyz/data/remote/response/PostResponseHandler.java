package com.innoxyz.data.remote.response;

import com.innoxyz.data.remote.exceptions.ActionFailedException;
import com.innoxyz.data.remote.interfaces.OnPostListener;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by laborish on 14-3-16.
 */
//post请求的响应结果
public class PostResponseHandler extends TextResponseHandler {

    final static String RESULT = "action_returns";
    final static String ERROR = "action_errors";
    final static String SUCCESS = "success";

    protected final OnPostListener onPostListener;

    public PostResponseHandler(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    public final boolean OnResponseContent(int responseCode, String content) throws Exception{

        //Log.e("Request", content);
        JSONObject json = new JSONObject(content);
        //请求失败
        if ( json.getString(RESULT).compareTo(SUCCESS)!=0 ) {

            onPostListener.onPostFail();

            String err_msg = "";
            if ( json.has(ERROR) ) {
                JSONArray err_arr = json.getJSONArray(ERROR);
                for (int i=0; i< err_arr.length(); i++) {
                    err_msg += err_arr.getString(i) + "\n";
                }
            }
            throw new ActionFailedException(err_msg);
        }
        //请求成功
        onPostListener.onPostSuccess();

        return true;
    }
}
