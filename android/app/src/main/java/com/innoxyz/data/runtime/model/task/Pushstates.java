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
public class Pushstates extends Pager<Pushstate> {
    @SerializedName("data")
    public Pushstate[] pushstates;

}
