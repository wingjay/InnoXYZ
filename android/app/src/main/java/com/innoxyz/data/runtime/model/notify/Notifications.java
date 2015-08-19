package com.innoxyz.data.runtime.model.notify;

import com.google.gson.annotations.SerializedName;
import com.innoxyz.data.runtime.model.Pager;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Notifications extends Pager<Notify> {
    @SerializedName("data")
    public Notify[] notifies;

}
