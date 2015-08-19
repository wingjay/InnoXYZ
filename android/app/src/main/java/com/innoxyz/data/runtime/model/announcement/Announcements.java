package com.innoxyz.data.runtime.model.announcement;

import com.innoxyz.data.runtime.model.Pager;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Announcements extends Pager<Announcement> {
    public Announcement[] announcements;

    protected Announcement[] getItems() {
        return announcements;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void setItems(Announcement[] items) {
        announcements = items;//To change body of implemented methods use File | Settings | File Templates.
    }
}
