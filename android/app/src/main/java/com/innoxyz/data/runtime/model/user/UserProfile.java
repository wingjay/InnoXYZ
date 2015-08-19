package com.innoxyz.data.runtime.model.user;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class UserProfile {
    public List<Education> educations;
    public List<Experience> experiences;
    public List<Paper> papers;
}
