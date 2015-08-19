package com.innoxyz.data.runtime.beans;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.project.Member;

/**
 * Created by laborish on 2014-4-14.
 */
public class MembersOfThing{

    @JsonMap(name = "data")
    public Member[] members;
}

