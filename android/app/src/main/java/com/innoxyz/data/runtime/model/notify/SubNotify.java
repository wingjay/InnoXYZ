package com.innoxyz.data.runtime.model.notify;


import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class SubNotify {
    //子通知内容
    public String content;

    //子通知创建者
    public String creatorName;

    //子通知类型
    @SerializedName("type")
    public String subNotifyType;
}
