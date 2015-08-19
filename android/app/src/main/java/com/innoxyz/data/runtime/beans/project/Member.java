package com.innoxyz.data.runtime.beans.project;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.User;

/**
 * Created by laborish on 14-3-15.
 */
public class Member {
    //
    @JsonMap(required = false)
    public Role role;
    @JsonMap(name = "createUser")
    public User user;
    @JsonMap(required = false)
    public String confirm;
}
