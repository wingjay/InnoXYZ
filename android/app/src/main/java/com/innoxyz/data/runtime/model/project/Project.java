package com.innoxyz.data.runtime.model.project;


import com.google.gson.Gson;

import lombok.Data;

//接收项目信息
@Data
public class Project{
    //项目ID
    private int id;
    //项目名称
    private String name;
    //创建时间
    private String createTime;
    private String state;
    //每一个项目有自己的一套存储附件的目录ID
    //基本目录
    private String baseDirId;
    //项目任务附件存放目录
    private String taskFolder;
    //项目讨论附件存放目录
    private String topicFolder;

    private String hiddenFolder;

    private LprojectType lprojectType;

    private String blockId;

    private String description;

    private boolean liked;

    @Data
    private class LprojectType{
        private String name;
    }


    //将一个json字符串转换成一个protocol对象
    public static Project fromJson(String json){
        Gson gson=new Gson();

        Project project;
        try {
            project=gson.fromJson(json,Project.class);
            return project;
        }catch (Exception e){
            project=new Project();
            e.printStackTrace();
            System.out.println("Json转换成对象时出异常！");
        }

        return project;
    }
}
