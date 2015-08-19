package com.innoxyz.data.runtime.beans.user;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.User;

/**
 * Created by laborish on 14-3-23.
 */
public class AutoCompleteUserList {

    @JsonMap(name = "data")
    public User[] users;
}
