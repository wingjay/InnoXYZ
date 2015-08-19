package com.innoxyz.data.runtime.beans.attachment;

import com.innoxyz.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-26
 * Time: 下午7:36
 * To change this template use File | Settings | File Templates.
 */
public class Attachment {
    //附件ID
    @JsonMap
    public String id;
    //附件名称
    @JsonMap
    public String name;
}
