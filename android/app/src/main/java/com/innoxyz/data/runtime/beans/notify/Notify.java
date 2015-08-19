package com.innoxyz.data.runtime.beans.notify;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午12:39
 * To change this template use File | Settings | File Templates.
 */
public class Notify {
    //通知id
    @JsonMap(name = "_id")
    public String id;
    //项目id
    @JsonMap(required = false)
    public int hostId;
    //
    @JsonMap(required = false)
    public int hostType;
    //项目名称
    @JsonMap(required = false)
    public String hostName;
    //最新修改时间
    @JsonMap
    public String last;
    //通知类型 如 TOPIC
    @JsonMap(required = false)
    public String thingType = null;
    //子通知
    @JsonMap(name = "notifications")
    public SubNotify[] subNotifies;

    //本条通知的具体内容
    @JsonMap(required = false)
    public NotifyThing thing;
}
