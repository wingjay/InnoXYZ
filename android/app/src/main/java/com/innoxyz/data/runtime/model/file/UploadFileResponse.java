package com.innoxyz.data.runtime.model.file;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Created by yj on 2014/12/13.
 * 上传附件后的响应
 */
@Data
public class UploadFileResponse {

//{"fileName":"4.png","action_returns":"success","creatorUserName":"尹捷",
// "docType":"image/png","uploadedTime":"刚刚",
// "fileUrl":"http://www.innoxyz.com/file/file.action?id\u003d548bf79b0cf20517fd0d4208\u0026type\u003ddocument",
// "fileLength":29672,"fileId":"548bf79b0cf20517fd0d4208"}

    public String fileName;

    @SerializedName("action_returns")
    public String status;
    public String error;

    public String creatorUserName;
    public String docType;
    public String uploadedTime;
    public String fileUrl;
    public int fileLength;
    //文件id，每个文件所处的文件夹以此命名
    public String fileId;

    public boolean isOk(){
        return "ok".equalsIgnoreCase(this.status) || "success".equalsIgnoreCase(this.status) || "y".equalsIgnoreCase(this.status);
    }
    //将响应数据转化为对象
    public static UploadFileResponse fromJson(String json){
        Gson gson = new Gson();
        UploadFileResponse uploadAttachResponse;
        try {
            uploadAttachResponse = gson.fromJson(json, UploadFileResponse.class);
            return uploadAttachResponse;
        } catch (Exception e){
            uploadAttachResponse = new UploadFileResponse();
            uploadAttachResponse.setStatus("ERROR");
            uploadAttachResponse.setError("返回的数据格式错误");

            e.printStackTrace();
        }

        return uploadAttachResponse;
    }
}
