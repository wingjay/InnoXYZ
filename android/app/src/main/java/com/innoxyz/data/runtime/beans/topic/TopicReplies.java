package com.innoxyz.data.runtime.beans.topic;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.common.Pager;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午5:40
 * To change this template use File | Settings | File Templates.
 */
public class TopicReplies extends Pager<TopicReply> {
    @JsonMap(name = "data")
    public TopicReply[] replies;

    @Override
    protected TopicReply[] getItems() {
        return replies;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setItems(TopicReply[] items) {
        replies = items;//To change body of implemented methods use File | Settings | File Templates.
    }


}
