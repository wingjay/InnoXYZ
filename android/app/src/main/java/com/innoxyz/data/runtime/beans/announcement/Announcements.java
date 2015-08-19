package com.innoxyz.data.runtime.beans.announcement;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.common.Pager;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午3:07
 * To change this template use File | Settings | File Templates.
 */
public class Announcements extends Pager<Announcement> {
    @JsonMap(name = "data")
    public Announcement[] announcements;

    @Override
    protected Announcement[] getItems() {
        return announcements;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setItems(Announcement[] items) {
        announcements = items;//To change body of implemented methods use File | Settings | File Templates.
    }
}
