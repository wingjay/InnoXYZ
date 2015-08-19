package com.innoxyz.data.runtime.model.document;

import android.content.Context;
import android.content.res.Resources;

import com.innoxyz.R;
import com.innoxyz.global.InnoXYZApp;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 * 包含两种类型type:DIR、FILE
 */
@Data
public class Document {
    public String id;
    public int length;
    public int creatorId;
    public String creatorName;
    public String createTime;
    public String type;
    public String contentType;
    //文件/目录名
    public String dispName;

    //根据文件类型，获取完整文件名（添加后缀）
    public String getFileRealName() {
        //把contentType里的字符串 / 和 - 都换成 .
        String modifiedContentType, suffix;
        Resources resources = InnoXYZApp.getApplication().getResources();
        modifiedContentType = this.contentType.replaceAll("/", ".");
        modifiedContentType = modifiedContentType.replaceAll("-", ".");
        int suffixId = resources.getIdentifier(modifiedContentType, "string", InnoXYZApp.getApplication().getPackageName());
        if (suffixId == 0) {
            return dispName ;
        }else {
            suffix = resources.getString(suffixId);
            return dispName + "." + suffix;
        }
    }

}
