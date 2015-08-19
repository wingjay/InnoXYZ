package com.innoxyz.data.remote.response;

import android.util.Log;
import com.innoxyz.data.json.parser.JsonParser;
import com.innoxyz.data.remote.exceptions.ActionFailedException;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.global.InnoXYZApp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 * 从服务器获取数据content，转化为json对象，并根据数据的目标类把数据存入 SimpleObservedData 的data中
 */
public class JsonResponseHandler extends TextResponseHandler {

    final static String RESULT = "action_returns";
    final static String ERROR = "action_errors";
    final static String SUCCESS = "success";
    //存储数据
    protected final SimpleObservedData related;
    //数据类型
    protected final Class<?> clazz;
    protected final List<String> subObjects;

    public JsonResponseHandler(SimpleObservedData sod, Class<?> clazz, List<String> subObjects) {
        this.related = sod;
        this.clazz = clazz;
        this.subObjects = subObjects;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean OnResponseContent(int responseCode, String content) throws Exception {
        Log.i("Request", content);
        //将content转化为json对象。content是二进制流，要反序列化为json
        JSONObject json = new JSONObject(content);
        //如果请求失败
        if ( json.getString(RESULT).compareTo(SUCCESS)!=0 ) {
            String err_msg = "";
            if ( json.has(ERROR) ) {
                JSONArray err_arr = json.getJSONArray(ERROR);
                for (int i=0; i< err_arr.length(); i++) {
                    err_msg += err_arr.getString(i) + "\n";
                }
            }
            throw new ActionFailedException(err_msg);
        }
        //todo：利用了反射机制
        JsonParser parser = InnoXYZApp.getApplication().getJsonParsers().getParser(clazz);
        if ( subObjects != null ) {
            for(String name : subObjects) {
                json = json.getJSONObject(name); //把key = name的值value取出来
            }
        }
        //将json对象转化为clazz类，将数据发送给 observer 执行 update方法，对数据进一步操作
        related.setData(parser.Parse(json), true);
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
