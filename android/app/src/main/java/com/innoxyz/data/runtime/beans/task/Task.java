package com.innoxyz.data.runtime.beans.task;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.User;
import com.innoxyz.data.runtime.beans.common.NamedText;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-26
 * Time: 下午8:38
 * To change this template use File | Settings | File Templates.
 */
public class Task {
    @JsonMap
    public String id;
    @JsonMap
    public String name;
    @JsonMap(required = false)
    public User[] assignees = new User[0];
    @JsonMap(required = false)
    public String deadline;
    @JsonMap
    public NamedText state;
    @JsonMap
    public NamedText priority;
    @JsonMap
    public String description;
    @JsonMap
    public int creatorId;
    @JsonMap
    public String creatorName;
}
