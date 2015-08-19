package com.innoxyz.data.runtime.model.user;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class UserInfo {
    @SerializedName("realname")
    public String name;
    public UserDetail detail;
    public UserProfile profile;
}
