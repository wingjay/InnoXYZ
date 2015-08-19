package com.innoxyz.ui.interfaces;

import com.innoxyz.data.runtime.model.file.UploadFileResponse;

import java.io.File;

/**
 * Created by yj on 2014/12/19.
 * 监听从本地获取到要上传的附件，在视图中显示已选中的附件文件名
 */
public interface OnUploadFileListener {
    void successUpload(String attachId, File file);
    void failUpload(File file);
}
