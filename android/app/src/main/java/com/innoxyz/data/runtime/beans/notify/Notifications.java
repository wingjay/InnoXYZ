package com.innoxyz.data.runtime.beans.notify;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.common.Pager;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午12:39
 * To change this template use File | Settings | File Templates.
 */
public class Notifications extends Pager<Notify> {
    @JsonMap(name = "data")
    public Notify[] notifies;

    @Override
    protected Notify[] getItems() {
        return notifies;
    }

    @Override
    protected void setItems(Notify[] items) {
        notifies = items;
    }
}
