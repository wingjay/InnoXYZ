package com.innoxyz.data.runtime.beans.user;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 下午1:09
 * To change this template use File | Settings | File Templates.
 */
public class UserProfile {
    @JsonMap
    public Education[] educations;
    @JsonMap
    public Experience[] experiences;
    @JsonMap
    public Paper[] papers;
}
