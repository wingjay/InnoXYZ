package com.innoxyz.data.runtime.model.project;

import com.google.gson.annotations.SerializedName;
import com.innoxyz.data.runtime.model.user.User;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class Member {
    public Role role;
    //成员
    @SerializedName("createUser")
    public User user;
    public String confirm;
}
