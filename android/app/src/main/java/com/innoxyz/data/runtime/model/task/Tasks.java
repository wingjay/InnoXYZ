package com.innoxyz.data.runtime.model.task;

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
public class Tasks extends Pager<Task> {
    @SerializedName("data")
    public Task[] tasks;

}
