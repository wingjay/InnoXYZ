package com.innoxyz.ui.fragments.topic;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.interfaces.OnPostListener;
import com.innoxyz.data.remote.response.PostResponseHandler;
import com.innoxyz.data.runtime.model.ParcelableUser;
import com.innoxyz.data.runtime.model.attachment.Attachment;
import com.innoxyz.data.runtime.model.attachment.ParcelableAttachment;
import com.innoxyz.ui.customviews.MultiSelectSpinner;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.common.ReplyAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.innoxyz.R;
/**
 * Created by laborish on 14-3-5.
 */
public class TopicNew extends BaseFragment implements ReplyAction {


    private int projectId;
    //序列化数据
    List<ParcelableUser> parcelableMembers;
    //存储反序列化后的参与者数据
    private Map<String,Integer> memberMap = new HashMap<>();

    ActionBar actionBar;


    MultiSelectSpinner memberSelectSpinner;
    private EditText newTopicName, newTopicDetail;
    private String topicNameString, topicDetailString;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        projectId = getArguments().getInt("projectId");
        //获取项目成员序列化信息
        parcelableMembers = new ArrayList<>();
        parcelableMembers = getArguments().getParcelableArrayList("memberList");
        //反序列化，转成普通附件列表数据
        for (ParcelableUser parcelableUser : parcelableMembers) {
            memberMap.put(parcelableUser.getName(), parcelableUser.getId());
        }

        actionBar = getActivity().getActionBar();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_new_topic, container, false);

        if (actionBar != null) {
            actionBar.setTitle(R.string.topic_new_topic);
        }
        memberSelectSpinner = (MultiSelectSpinner)ret.findViewById(R.id.multiSelectSpinner);
        memberSelectSpinner.setHint(R.string.topic_new_topic_members_hint);
        memberSelectSpinner.setItemsAndIds(parcelableMembers);

        newTopicName = (EditText)ret.findViewById(R.id.new_topic_name);
        newTopicDetail = (EditText)ret.findViewById(R.id.new_topic_detail);

        newTopicName.setHint(R.string.topic_new_topic_name_hint);
        newTopicDetail.setHint(R.string.topic_new_topic_detail_hint);

        return ret;
    }

    public void reply(){

        newTopicName.setError(null);
        newTopicDetail.setError(null);

        // Store values at the time of the login attempt.
        topicNameString = newTopicName.getText().toString();
        topicDetailString = newTopicDetail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(topicDetailString)) {
            newTopicDetail.setError(getString(R.string.error_field_required));
            focusView = newTopicDetail;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(topicNameString)) {
            newTopicName.setError(getString(R.string.error_field_required));
            focusView = newTopicName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
//            showProgress(true);

            String userIds = "";
            List<String> selectedName = memberSelectSpinner.getSelectedStrings();
            for(int i = 0; i<selectedName.size(); i++){
                if(memberMap.containsKey(selectedName.get(i))){
                    userIds += "\"";
                    userIds += Integer.toString(memberMap.get(selectedName.get(i)));
                    userIds += "\"";
                    userIds += ",";
                }
            }
            if(userIds.length()!=0 && userIds.charAt(userIds.length()-1) == ','){
                userIds = userIds.substring(0, userIds.length()-1);
            }
            userIds = "[" + userIds;
            userIds += "]";

            try {
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.TOPIC_NEW)
                        .addParameter("thingId", "" + projectId)
                        .addParameter("thingType", "LPROJECT")
                        .addParameter("subject", topicNameString)
                        .addParameter("content", topicDetailString)
                        .addParameter("attachments", "[]")
                        .addParameter("userIds", userIds)
                        .setOnResponseListener(new PostResponseHandler(new TopicNewReplyListener()))
                        .request();
            } catch (Exception e) {
            }
        }
    }

    private class TopicNewReplyListener implements OnPostListener {

        public void onPostSuccess(){
            Toast.makeText(getActivity(), R.string.topic_new_success, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        public void onPostFail(){
            Toast.makeText(getActivity(), R.string.topic_new_fail, Toast.LENGTH_SHORT).show();
        }
    }

}
