package com.innoxyz.ui.fragments.task;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.interfaces.OnPostListener;
import com.innoxyz.data.remote.response.PostResponseHandler;
import com.innoxyz.data.runtime.model.file.UploadFileResponse;
import com.innoxyz.ui.fragments.common.ReplyAction;
import com.innoxyz.ui.fragments.common.ReplyFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.innoxyz.R;
import com.innoxyz.ui.interfaces.OnGetAttachmentListener;
import com.innoxyz.ui.interfaces.OnUploadFileListener;
import com.loopj.android.http.RequestParams;

/**
 * Created by laborish on 14-3-28.
 * 对任务进行回复，能上传附件。
 * 附件 和 回复文本 分两次上传
 * 当点击NewActivity中的完成按钮时，执行reply方法来处理。
 */
public class TaskReply extends ReplyFragment implements ReplyAction, OnGetAttachmentListener, OnUploadFileListener {
    private static final String[] stateArray={"修改任务状态", "新建","进行中","挂起","完成","其它"};

    private static final Map<String,String> stateMap = new HashMap<String, String>();
    static{
        stateMap.put("新建","NEW");
        stateMap.put("进行中","IN_PROGRESS");
        stateMap.put("挂起","SUSPEND");
        stateMap.put("完成","COMPLETE");
        stateMap.put("其它","OTHER");
    }

    private int thingId;
    private String thingType;
    private String taskId;
    private String currentState;

    //引用的用户名和内容
    private boolean isQuoted;
    private String quotedUser;
    private String quotedContent;
    private EditText quotedEditText;

    private EditText  replyTaskDetail;
    //选中的附件列表
    private List<File> selectedAttachmentsList;
    private List<String> selectedAttachmentsName;
    //显示选中的附件列表
    private ListView AttachmentListView;
    private AttachmentListAdapter attachmentListAdapter;

    //成功上传的附件ID
    private List<String> attachmentsList;

    //回复文本内容
    String replyContent;
    //private Spinner stateSpinner;
    //private String taskDetailString;

    //选中的任务状态
    private String stateString;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //获取任务信息：所属项目，任务ID，当前状态
        thingId = getArguments().getInt("projectId"); //804
        thingType = getArguments().getString("hostType"); //LPROJECT
        taskId = getArguments().getString("taskId"); //5487115e0cf20517fd0d3885
        currentState = getArguments().getString("state");//新建

        //获取引用的用户名和内容
        quotedUser = getArguments().getString("quotedUser");
        quotedContent = getArguments().getString("quotedContent");

        //设置上传附件的参数
        setParams(thingId, thingType);
        //绑定获取文件监听器
        setOnGetAttachmentListener(this);
        //绑定上传文件监听器
        setOnUploadFileListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示ActionBar标题
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.task_reply_task);
        }
        //创建选中附件管理列表
        selectedAttachmentsList = new ArrayList<>();
        selectedAttachmentsName = new ArrayList<>();
        //初始化 默认为非引用
        isQuoted = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_task_reply, container, false);

        replyTaskDetail = (EditText)ret.findViewById(R.id.reply_task_detail);
        replyTaskDetail.setHint(R.string.reply_hint);
        quotedEditText = (EditText)ret.findViewById(R.id.quote_content);

        //如果是引用，则显示引用内容
        if (!TextUtils.isEmpty(quotedUser) || !TextUtils.isEmpty(quotedContent)) {
            isQuoted = true;
            quotedEditText.setVisibility(View.VISIBLE);
            quotedEditText.setText("@" + quotedUser + ":" + quotedContent);
        }

        Spinner stateSpinner = (Spinner)ret.findViewById(R.id.reply_task_state);
        //上传附件,绑定点击监听
        Button uploadAttachBtn = (Button)ret.findViewById(R.id.upload_attachment);
        uploadAttachBtn.setOnClickListener(this);

        //显示已选中附件列表
        AttachmentListView = (ListView)ret.findViewById(R.id.attachment_listview);
        updateAttachmentsName();
        attachmentListAdapter = new AttachmentListAdapter();
        AttachmentListView.setAdapter(attachmentListAdapter);

        //任务状态
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, stateArray);
        spinnerAdapter.setDropDownViewResource(R.layout.drop_down_item);
        stateSpinner.setAdapter(spinnerAdapter);
        stateSpinner.setSelection(0);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                //获取选中值
                stateString = stateMap.get(parent.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //stateString = stateMap.get(currentState);
            }
        });

        return ret;
    }

    //更新附件名称
    private void updateAttachmentsName() {
        //先清空selectedAttachmentsName
        selectedAttachmentsName.clear();
        if (!selectedAttachmentsList.isEmpty()) {
            //获取附件名称列表
            for (File attachmentFile : selectedAttachmentsList) {
                selectedAttachmentsName.add(attachmentFile.getName());
            }
        }
    }

    //当从本地获取附件后，刷新附件列表视图
    @Override
    public void showAttachment(File attachmentFile) {
        selectedAttachmentsList.add(attachmentFile);
        updateAttachmentsName();
        attachmentListAdapter.notifyDataSetChanged();
    }

    //显示附件名称列表，允许删除附件
    private class AttachmentListAdapter extends BaseAdapter {

        class ViewHolder {
            TextView attachmentNameTextView;
            ImageView deleteAttachment;
        }

        @Override
        public int getCount() {
            return selectedAttachmentsName.size();
        }

        @Override
        public Object getItem(int position) {
            return selectedAttachmentsName.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.fragment_reply_selected_attachments_list, parent, false);
                //配置ViewHolder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.attachmentNameTextView = (TextView)convertView.findViewById(R.id.attachment_name_textView);
                viewHolder.deleteAttachment = (ImageView)convertView.findViewById(R.id.delete_attachment);

                convertView.setTag(viewHolder);
            }

            //装填数据
            ViewHolder holder = (ViewHolder)convertView.getTag();

            holder.attachmentNameTextView.setText(selectedAttachmentsName.get(position));
            //绑定删除监听器
            holder.deleteAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo：是否要删除源文件
                    //删除附件
                    selectedAttachmentsList.remove(position);
                    //更新附件名称列表
                    updateAttachmentsName();
                    //刷新视图
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }


    /**
     * 当点击完成按钮时，回调执行该函数
     * 1、对回复文本内容是否为空进行验证
     * 2、上传selectedAttachmentsList里的文件，上传成功后获取该文件ID
     * 3、上传文本内容
     */
    public void reply(){

        // 验证回复文本
        if (!verifyReplyContent())
            return;

        //上传附件
        if (selectedAttachmentsList.isEmpty()) {
            uploadReplyContent();
            return;
        }
        //存储成功上传的附件ID
        attachmentsList = new ArrayList<>();
        for (File attachment : selectedAttachmentsList) {
            UploadAttach(attachment);
        }
        //
    }

    //附件上传成功,则从附件列表中移除,并将附件的ID存入attachments
    @Override
    public void successUpload(String attachId, File file) {
        selectedAttachmentsList.remove(file);
        attachmentsList.add(attachId);
        attachmentListAdapter.notifyDataSetChanged();
        //若附件全部上传完，则上传文本
        if ( selectedAttachmentsList.isEmpty() ) {
            uploadReplyContent();
        }
    }

    //附件上传失败,则保留在附件列表
    @Override
    public void failUpload(File file) {
        Toast.makeText(getActivity(), "附件" + file.getName() + "上传失败，请检查网络连接", Toast.LENGTH_SHORT).show();
        UploadAttach(file);
    }

    /**
     * 上传回复文本
     */
    private void uploadReplyContent() {
        //设置修改后的任务状态
        if(stateString == null){
            stateString = stateMap.get(currentState);
        }
        if(stateString == null){
            stateString = "OTHER";
        }
        //上传的数据:state、 attachments
        String state = String.format("{\"taskId\":\"%s\",\"hostId\":\"%d\",\"hostType\":\"LPROJECT\",\"comment\":\"%s\",\"modifications\":{\"state\":{\"newValue\":\"%s\"}}}"
                ,taskId
                ,thingId
                ,replyContent
                ,stateString);
        //拼接字符串 attachments = ["123123", "115736"]
        String attachments = "[";
        //如果没有附件
        if (attachmentsList == null) {
            attachments += "]";
        }else {
            int i = 0;
            int attachNum = attachmentsList.size();
            for (String attachid : attachmentsList) {
                if (i == (attachNum - 1)){
                    attachments += "\"" + attachid + "\"]";
                }else {
                    attachments += "\"" + attachid + "\",";
                }
                i++;
            }
        }
        RequestParams params = new RequestParams();
        params.add("state", state);
        params.add("attachments", attachments);
        UploadContent(params);
    }



    // 验证输入文本是否为空
    private boolean verifyReplyContent() {
        //获取输入的回复内容
        replyContent = replyTaskDetail.getText().toString();
        if (isQuoted) {
            replyContent = "[quote][strong]" + quotedUser + ":[/strong]\n\n" + quotedContent+ "[/quote]" + replyContent;
        }

        replyTaskDetail.setError(null);

        // 回复内容不可为空
        if (TextUtils.isEmpty(replyContent)) {
            replyTaskDetail.setError(getString(R.string.error_field_required));
            replyTaskDetail.requestFocus();
            return false;
        }
        return true;
    }

}
