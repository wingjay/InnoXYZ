package com.innoxyz.data.runtime.beans.user;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 下午2:11
 * To change this template use File | Settings | File Templates.
 */
public class UserInfo {
    @JsonMap
    public UserDetail detail;
    @JsonMap
    public UserProfile profile;
}
