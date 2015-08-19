package com.innoxyz.data.runtime.model;


import java.util.List;

import lombok.Data;

/**
 * 分页对象
 * Created by yj on 2014/12/19.
 */
@Data
public class Pager<T> {
    //页数从0开始
    public int page;
    public int total = 0;
    public int pageSize;
    public List<T> data;

    public boolean hasMore() {
        if (total == 0)
            return false;
        return page < (total-1) / pageSize;
    }

}
