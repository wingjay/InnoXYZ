package com.innoxyz.data.runtime.model.task;

import com.innoxyz.data.runtime.model.common.NamedText;
import com.innoxyz.data.runtime.model.user.User;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class Task {
    public String id;
    public String name;
    public List<User> assignees;
    public String deadline;
    public NamedText state;
    public NamedText priority;
    public String description;
    public int creatorId;
    public String creatorName;
}
