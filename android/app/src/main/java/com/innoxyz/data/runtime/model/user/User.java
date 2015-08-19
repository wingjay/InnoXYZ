package com.innoxyz.data.runtime.model.user;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class User {
    public int id;
    public String realName;
    public String realname;
    public String name;
    public String email;

    public String getName(){
        if (realName != null)
            return realName;
        else if (realname != null)
            return realname;
        else if (name != null)
            return name;
        else
            return null;
    }
}
