package com.innoxyz.ui.fragments.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.reflect.TypeToken;
import com.innoxyz.R;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.file.UploadFileResponse;
import com.innoxyz.data.runtime.model.project.Project;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.interfaces.OnGetAttachmentListener;
import com.innoxyz.ui.interfaces.OnUploadFileListener;
import com.innoxyz.ui.thirdparty.filechooser.FilechooserActivity;
import com.innoxyz.ui.thirdparty.filechooser.ItemType;
import com.innoxyz.ui.thirdparty.filechooser.SelectionMode;
import com.innoxyz.util.Factory;
import com.innoxyz.util.Util;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yj.
 * Date: 14-12-6
 * Time: 下午8:58
 * 上传流程：先根据项目id向服务器获取项目信息，并根据当前是任务/话题获取文件夹存储目录id，然后把附件上传至该文件夹中，成功后上传回复文本。
 * 拍照上传：拍摄照片后暂时存入/InnoXYZ/Upload/Cache中，上传至服务器后获取该文件的fileID，在把照片转存入/InnoXYZ/Upload/{fileID}中
 */
//回复Fragment，点击上传附件按钮，可以触发上传附件。
public class ReplyFragment extends BaseFragment implements View.OnClickListener{
    /* 当前显示的Fragment的类名，用于指定dirID */
    private static final String Task_Reply_Fragment = "com.innoxyz.ui.fragments.task.TaskReply";
    private static final String Topic_Reply_Fragment = "com.innoxyz.ui.fragments.topic.TopicReply";

    /* 上传附件请求码 */
    private List<File> selectedFiles;
    /* 上传附件请求码 */
    AlertDialog dialog;
    private static final int FILE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private String[] items = new String[] {"选择本地文件", "拍照"};

    /*上传照片临时目录 和 存储目录*/
    private static final String UPLOAD_FILE_CACHE_DIR = InnoXYZApp.getApplication().getUploadFileCacheDir();
    private static final String UPLOAD_FILE_DIR = InnoXYZApp.getApplication().getUploadFileDir();
    /*拍照上传名称*/
    private static String UPLOAD_PICTURE_PATH;
    //照片文件名
    private String PIC_NAME;
    //若拍照，则不仅要上传，也要在本地保存（命名）
    private File file;
    private Uri uploadPicUri;
    //照片保存到本地时压缩的比例
    private static final int widthScale = 1;
    private static final int heightScale = 1;

    //上传附件时的参数
    private String thingType;
    private int thingId;
    private String returntype = "normal";
    private Boolean temporary = true;
    private String dirId;
    private String attachName;

    //网络连接失败次数
    private int maxUploadFailedNum = 0;

    //从相机/SD卡 获取附件监听器
    private OnGetAttachmentListener getAttachmentListener = null;
    //上传文件 监听器
    private OnUploadFileListener onUploadAttachmentListener = null;

    //上传附件，可以选择文件或者拍照
    @Override
    public void onClick(View view) {
        //判断文件夹是否存在，若不存在，则要创建
        if (Util.hasSdCard()){
            ///storage/sdcard0/InnoXYZ/Upload
            //先新建文件夹
            File filedir = new File(UPLOAD_FILE_CACHE_DIR);
            if(!filedir.exists()){
                try{
                    boolean mkdir = filedir.mkdirs();
                    if (!mkdir){
                        Toast.makeText(getActivity(),"InnoXYZ/Upload目录创建失败,请重新尝试或检查SD卡",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            Toast.makeText(getActivity(),"内存卡不存在",Toast.LENGTH_SHORT).show();
            return;
        }

        //创建对话框来选择方式
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("上传附件")
                .setItems(items,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                //选择本地文件
                                Intent fileIntent = new Intent(getActivity(), FilechooserActivity.class);
                                fileIntent.putExtra(FilechooserActivity.BUNDLE_ITEM_TYPE, ItemType.FILE); //DIRECTORY ALL
                                fileIntent.putExtra(FilechooserActivity.BUNDLE_SELECTION_MODE, SelectionMode.SINGLE_ITEM); //单选 SINGLE_ITEM MULTIPLE_ITEM
                                startActivityForResult(fileIntent, FILE_REQUEST_CODE);
                                break;
                            case 1:
                                //拍照存在本地
                                //创建文件目录
                                PIC_NAME = System.currentTimeMillis() + ".png";
                                UPLOAD_PICTURE_PATH  = UPLOAD_FILE_CACHE_DIR + File.separator + PIC_NAME;
                                file = new File(UPLOAD_PICTURE_PATH);
                                uploadPicUri = Uri.fromFile(file);
                                //开启相机拍照，照片存入 uploadPicUri
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadPicUri);
                                cameraIntent.putExtra("return-data",false);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        //存储选中的文件
        //selectedFiles = new ArrayList<>();
        switch (requestCode){
            case FILE_REQUEST_CODE:
                //从本地获取文件，则直接上传。上面设置为单选，所以此处path长度为1
                ArrayList<String> path = data.getStringArrayListExtra(FilechooserActivity.BUNDLE_SELECTED_PATHS);
                if (path.size() == 1) {
                    readyToUploadFiles(new File(path.get(0)));
                }

//                for (String path : paths) {
//                    selectedFiles.add(new File(path));
//                }
                break;
            case CAMERA_REQUEST_CODE:
                //从相机拍照，会自动存入UPLOAD_PICTURE_PATH，取出图片并上传
                if (Util.hasSdCard()){
                    File picFile = new File(UPLOAD_PICTURE_PATH);
                    if (picFile.exists()){
                        //Toast.makeText(getActivity(),"照片附件已经存入/InnoXYZ/Upload目录！",Toast.LENGTH_SHORT).show();
                        //上传
                        readyToUploadFiles(picFile);
                    }else{
                        Toast.makeText(getActivity(),"拍摄的照片保存失败！",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(),"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //获取上传附件的部分参数
    protected void setParams(int projectedId, String hostType){
        thingId = projectedId;
        thingType = hostType;
    }

    /**
     * 绑定获取附件监听器
     * @param listener
     */
    protected void setOnGetAttachmentListener(OnGetAttachmentListener listener) {
        getAttachmentListener = listener;
    }
    /**
     * 绑定上传文件监听器
     * @param listener
     */
    protected void setOnUploadFileListener(OnUploadFileListener listener) {
        onUploadAttachmentListener = listener;
    }


    // 先根据项目ID来获取该项目的信息，包括topicFolder、taskFolder
    // 接口：http://innoxyz.com/thing/lproject/get-info.action?thingId=815
    private void readyToUploadFiles(File file){
        final File uploadFile = file;
        // 先根据项目ID来获取该项目的信息，包括topicFolder等
        // 接口：http://innoxyz.com/thing/lproject/get-info.action?thingId=815
        RequestParams params = new RequestParams();
        params.put("thingId", thingId);

        Factory.getHttpClient().get(URL.GET_INFO_OF_LPROJECT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                //获取数据
                Protocol<Project> Protocol = new Protocol<Project>();
                Protocol<Project> protocol = Protocol.fromJson(response, new TypeToken<Protocol<Project>>() {
                }.getType());
                Project project = protocol.getData();
                //根据当前显示的Fragment获取附件存储目录ID
                String currentFragmentName = getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT").getClass().getName();
                if (currentFragmentName.equals(Task_Reply_Fragment)) {
                    dirId = project.getTaskFolder();
                } else if (currentFragmentName.equals(Topic_Reply_Fragment)) {
                    dirId = project.getTopicFolder();
                }
                //如果获取到了dirId就去显示该文件名
                if (!dirId.equals("") && !dirId.equals(null)) {
                    //显示
                    getAttachmentListener.showAttachment(uploadFile);
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity(), "项目信息获取失败，请重新尝试！", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 把文件上传至服务器，如果该文件是照片，即存储于 UPLOAD_FILE_CACHE_DIR 临时目录中，则要移动到 UPLOAD_FILE_DIR/{fileID}
     * thingtype = LPROJECT
     * thingId = 804
     * returntype = normal
     * temporary = true
     * dirid = 546d73310cf205a5a1f1b7b4 // thing/lproject/get-info.action 项目id
     * upload = file文件
     * name = 0.png
     */
    protected void UploadAttach(final File uploadFile){
        final boolean isFromCamera;
        isFromCamera = UPLOAD_FILE_CACHE_DIR.equalsIgnoreCase(uploadFile.getParent());

        RequestParams params = new RequestParams();
        params.put("thingtype", thingType);
        params.put("thingId", thingId);
        params.put("returntype", "normal");
        params.put("temporary", "true");
        params.put("dirid", dirId);
        try {
            params.put("upload", uploadFile);
            params.put("name", uploadFile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "附件开始上传，请稍等", Toast.LENGTH_SHORT).show();
        Factory.getPostHttpClient().post(URL.UPLOAD_DOCUMENT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                UploadFileResponse uploadFileResponse = UploadFileResponse.fromJson(response);
                if (uploadFileResponse.isOk()){
                    //Toast.makeText(getActivity(), "附件" + uploadFileResponse.getFileName() + "上传成功", Toast.LENGTH_SHORT).show();
                    onUploadAttachmentListener.successUpload(uploadFileResponse.getFileId(), uploadFile);
                    //如果是照片则移动目录(创建fileID目录)
                    if (isFromCamera) {
                        int moveNum = 1;
                        boolean moveResult = false;
                        while (!moveResult && moveNum<=3) {
                            moveResult = Util.moveFileToAnotherDir(uploadFile, UPLOAD_FILE_DIR + File.separator + uploadFileResponse.getFileId());
                            if (moveResult)
                                moveNum = 1;
                            else moveNum++;
                        }
                        if (moveNum != 1) {
                            Toast.makeText(getActivity(), "附件" + uploadFileResponse.getFileName() + "成功上传！已保存到/InnoXYZ/Upload/Cache中", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), "附件" + uploadFileResponse.getFileName() + "成功上传！已保存到/InnoXYZ/Upload中", Toast.LENGTH_SHORT).show();
                        }
                    }
                    maxUploadFailedNum = 0;
                }else {
                    onUploadAttachmentListener.failUpload(uploadFile);
                }

            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //上传失败
                maxUploadFailedNum ++;
                if ( maxUploadFailedNum >= 3 ){
                    Toast.makeText(getActivity(), "网络连接失败，请重新尝试！", Toast.LENGTH_SHORT).show();
                }else {
                    //继续尝试
                    onUploadAttachmentListener.failUpload(uploadFile);
                }
            }
        });

    }

    /**
     * 上传回复文本 POST
     * TASK_REPLY:
     * state:{"taskId":"532d84b673f2fa1479576e6b","hostId":"545","thingType":"LPROJECT","comment":"456","modifications":{"state":{"newValue":"IN_PROGRESS"}}}
     * attachments:["548bf79b0cf20517fd0d4208"]
     * TOPIC_REPLY:
     * topicId:54753ba70cf20517fd0d09d1
     * content:阿道夫
     * attachments:["5493fc6b0cf24955326b6821"]
     */
    protected void UploadContent(RequestParams params) {
        String uploadUrl = "";
        //根据当前显示的Fragment来获取上传接口
        String currentFragmentName = getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT").getClass().getName();
        if (currentFragmentName.equals(Task_Reply_Fragment)) {
            uploadUrl = URL.TASK_REPLY;
        } else if (currentFragmentName.equals(Topic_Reply_Fragment)) {
            uploadUrl = URL.TOPIC_REPLY;
        }
        //上传
        if (uploadUrl.equals(""))
            return;
        Factory.getPostHttpClient().post(uploadUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Protocol protocol = Protocol.fromJson(response);
                if (protocol.isOk()) {
                    getActivity().finish();
                    Toast.makeText(getActivity(), "回复成功，可刷新查看", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "回复失败请重试~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity(), "与服务器通信失败...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
