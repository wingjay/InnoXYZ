package com.innoxyz.data.runtime.beans.common;


import com.innoxyz.data.json.parser.JsonMap;
import com.innoxyz.ui.utils.ArrayUtils;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-26
 * Time: 下午7:48
 * To change this template use File | Settings | File Templates.
 */
abstract public class Pager<T> {
    @JsonMap
    public int page; //页数从0开始
    @JsonMap
    public int total;
    @JsonMap
    public int pageSize;

    protected abstract T[] getItems();
    protected abstract void setItems(T[] items);

    public void append(Pager<T> pager) {
        if ( getItems() == null || getItems().length == 0 ) {
            setItems(pager.getItems());
        } else {
            setItems(ArrayUtils.merge(getItems(), pager.getItems()));
        }
        page = pager.page;
        total = pager.total;
        pageSize = pager.pageSize;
    }

    public boolean hasMore() {
        return page < (total-1) / pageSize;
    }
}
