package com.innoxyz.data.runtime.beans.user;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created by laborish on 2014-4-12.
 */
public class ChangePassErrors {
    @JsonMap(required = false)
    public String[] passwordOld;
    @JsonMap(required = false)
    public String[] passwordNew;
    @JsonMap(required = false)
    public String[] passwordAgain;
}
