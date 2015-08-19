package com.innoxyz.data.runtime.beans.project;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created by laborish on 14-3-15.
 */
public class MembersOfProj{

    @JsonMap(name = "links")
    public Member[] members;
}
