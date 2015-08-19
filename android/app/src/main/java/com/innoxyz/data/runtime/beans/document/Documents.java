package com.innoxyz.data.runtime.beans.document;

import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.data.runtime.beans.common.Pager;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-28
 * Time: 下午12:03
 * To change this template use File | Settings | File Templates.
 */
public class Documents extends Pager<Document> {
    @JsonMap(name = "data")
    public Document[] documents;
    @Override
    protected Document[] getItems() {
        return documents;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setItems(Document[] items) {
        documents = items;
    }
}
