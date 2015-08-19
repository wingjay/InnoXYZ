package com.innoxyz.data.runtime.model.notify;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class Notify {
    @SerializedName("_id")
    String id;
    String thingType;
    int total;
    String last;
    @SerializedName("notifications")
    List<SubNotify> subNotifies;
    NotifyThing thing;
    int hostId;
    String hostName;
}
