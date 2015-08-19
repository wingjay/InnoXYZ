package com.innoxyz.data.runtime.model.project;

import com.google.gson.annotations.SerializedName;
import com.innoxyz.data.runtime.model.Pager;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
//接收项目信息
@Data
@EqualsAndHashCode(callSuper = false)
public class Projects extends Pager<Projects.Project> {

    @SerializedName("data")
    public Project[] projects;

    //每一个项目的具体信息
    public static class Project {

        public int id;
        public String name;
        public boolean liked = false;
        public String createTime;
    }
}
