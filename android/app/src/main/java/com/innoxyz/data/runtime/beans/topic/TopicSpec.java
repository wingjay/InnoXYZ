package com.innoxyz.data.runtime.beans.topic;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午5:10
 * To change this template use File | Settings | File Templates.
 */
public class TopicSpec {
    @JsonMap
    public Topic topic;
    @JsonMap
    public TopicReplies pager = new TopicReplies();
}
