package com.innoxyz.data.runtime.beans.task;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.common.Pager;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午2:26
 * To change this template use File | Settings | File Templates.
 */
public class Tasks extends Pager<Task> {
    @JsonMap(name = "data")
    public Task[] tasks;

    @Override
    protected Task[] getItems() {
        return tasks;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setItems(Task[] items) {
        tasks = items;
    }
}
