package com.innoxyz.data.runtime.model.project;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
public class MembersOfProj{

    @SerializedName("links")
    public List<Member> members;
}
