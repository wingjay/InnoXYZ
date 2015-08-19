package com.innoxyz.data.runtime.beans.task;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.common.Pager;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午1:02
 * To change this template use File | Settings | File Templates.
 */
public class Pushstates extends Pager<Pushstate> {
    @JsonMap(name = "data")
    public Pushstate[] pushstates;

    @Override
    protected Pushstate[] getItems() {
        return pushstates;
    }

    @Override
    protected void setItems(Pushstate[] items) {
        pushstates = items;
    }
}
