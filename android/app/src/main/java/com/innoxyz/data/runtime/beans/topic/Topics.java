package com.innoxyz.data.runtime.beans.topic;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.attachment.Attachment;
import com.innoxyz.data.runtime.beans.common.Pager;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午3:41
 * To change this template use File | Settings | File Templates.
 */
public class Topics {
    @JsonMap(name = "pager")
    public TopicList topicList = new TopicList();
    @JsonMap
    public HashMap<String, Post> postMap;

    public static class TopicList extends Pager<Topic> {
        @JsonMap(name = "data")
        public Topic[] topics;

        @Override
        protected Topic[] getItems() {
            return topics;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void setItems(Topic[] items) {
            topics = items;
        }
    }

    public void Normalize() {
        if ( topicList == null ) {
            topicList = new TopicList();
        }
        if ( topicList.topics != null ) {
            for(Topic topic : topicList.topics) {
                if ( topic.lastPostId != null ) {
                    topic.lastPost = postMap.get(topic.lastPostId);
                }
                if ( topic.attachments == null ) {
                    topic.attachments = new Attachment[0];
                }
            }
        }
    }
}
