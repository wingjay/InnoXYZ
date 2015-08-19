package com.innoxyz.ui.fragments.announcement;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.interfaces.OnPostListener;
import com.innoxyz.data.remote.response.PostResponseHandler;
import com.innoxyz.data.runtime.model.ParcelableUser;
import com.innoxyz.ui.customviews.MultiSelectSpinner;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.common.ReplyAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.innoxyz.R;
/**
 * Created by laborish on 14-3-29.
 */
public class AnnouncementNew extends BaseFragment implements ReplyAction {

    private int projectId;
    //序列化数据
    List<ParcelableUser> parcelableMembers;
    //存储反序列化后的参与者数据
    private Map<String,Integer> memberMap = new HashMap<>();

    ActionBar actionBar;

    private MultiSelectSpinner memberSelectSpinner;
    private CheckBox toRegEmailCheckbox;
    private EditText newAnnouncementTitle, newAnnouncementContent;
    private String announcementTitleString, announcementContentString;

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
        View ret = inflater.inflate(R.layout.fragment_new_announcement, container, false);

        if (actionBar != null) {
            actionBar.setTitle(R.string.announcement_new_announcement);
        }

        memberSelectSpinner = (MultiSelectSpinner)ret.findViewById(R.id.new_announcement_toUsers);
        memberSelectSpinner.setHint(R.string.announcement_new_announcement_toUsers_hint);
        //memberSelectSpinner.setItems(new ArrayList<String>(memberMap.keySet()));
        memberSelectSpinner.setItemsAndIds(parcelableMembers);

        toRegEmailCheckbox = (CheckBox)ret.findViewById(R.id.announcement_new_send_to_regEmail_checkBox);

        newAnnouncementTitle = (EditText)ret.findViewById(R.id.new_announcement_title);
        newAnnouncementContent = (EditText)ret.findViewById(R.id.new_announcement_content);

        newAnnouncementTitle.setHint(R.string.announcement_new_announcement_title_hint);
        newAnnouncementContent.setHint(R.string.announcement_new_announcement_content_hint);

        return ret;
    }

    public void reply(){

        //Log.e("AAAAAAAAAAAA","actionDone pressed");

        // Reset errors.
        newAnnouncementTitle.setError(null);
        newAnnouncementContent.setError(null);

        // Store values at the time of the login attempt.
        announcementTitleString = newAnnouncementTitle.getText().toString();
        announcementContentString = newAnnouncementContent.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(announcementContentString)) {
            newAnnouncementContent.setError(getString(R.string.error_field_required));
            focusView = newAnnouncementContent;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(announcementTitleString)) {
            newAnnouncementTitle.setError(getString(R.string.error_field_required));
            focusView = newAnnouncementTitle;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

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
                StringRequestBuilder stringRequestBuilder = new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.ANNOUNCEMENT_NEW)
                        .addParameter("thingId", "" + projectId)
                        .addParameter("title", announcementTitleString)
                        .addParameter("content", announcementContentString)
                        .addParameter("emailNotify", toRegEmailCheckbox.isChecked()?"true":"false")
                        .addParameter("attachments", "[]")
                        .setOnResponseListener(new PostResponseHandler(new AnnouncementNewReplyListener()));
                if(userIds.compareTo("[]")!=0){
                    stringRequestBuilder.addParameter("userIds", userIds);
                }
                stringRequestBuilder.request();

            } catch (Exception e) {
            }
        }
    }

    private class AnnouncementNewReplyListener implements OnPostListener {

        public void onPostSuccess(){
            //Log.e("AAAAAAAAAAAA", "SUCCESSSSSSSSS");
            Toast.makeText(getActivity(), R.string.announcement_new_success, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        public void onPostFail(){

            //Log.e("AAAAAAAAAAAA", "FAILLLLLLLLLLLLL");
            Toast.makeText(getActivity(), R.string.announcement_new_fail, Toast.LENGTH_SHORT).show();
        }
    }

}
