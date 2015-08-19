package com.innoxyz.data.runtime.model.message;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.common.Pager;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
public class Messages extends Pager<Message> {
    @JsonMap(name = "data")
    public Message[] messages;


    @Override
    protected Message[] getItems() {
        return messages;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setItems(Message[] items) {
        messages = items;
    }
}
