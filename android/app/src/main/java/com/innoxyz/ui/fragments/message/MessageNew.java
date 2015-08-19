package com.innoxyz.ui.fragments.message;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.interfaces.OnPostListener;
import com.innoxyz.data.remote.response.JsonResponseHandler;
import com.innoxyz.data.remote.response.PostResponseHandler;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.data.runtime.beans.user.AutoCompleteUserList;
import com.innoxyz.data.runtime.interfaces.IDataObserver;
import com.innoxyz.ui.customviews.AutomaticWarpViewGroup;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.common.ReplyAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.innoxyz.R;
/**
 * Created by laborish on 14-3-23.
 */
public class MessageNew extends BaseFragment implements ReplyAction {

    private AutoCompleteTextView receiver;
    private EditText subject, content;
    private AutomaticWarpViewGroup checkedReceivers;

    private String subjectString, contentString;

    private SimpleObservedData<AutoCompleteUserList> users;
    private Map<String, UserInfo> userMap = new HashMap<String, UserInfo>();

    private String originalSenderName = null,originalSubject = null;
    private int originalSenderId = -1;

    //private List<Integer> checkedUserIds = new ArrayList<Integer>();



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        originalSenderName = getArguments().getString("senderName");
        originalSubject = getArguments().getString("subject");
        originalSenderId = getArguments().getInt("senderId");

    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.message_new_message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_new_message, container, false);

        receiver = (AutoCompleteTextView)ret.findViewById(R.id.new_message_receiver);
        subject = (EditText)ret.findViewById(R.id.new_message_subject);
        content = (EditText)ret.findViewById(R.id.new_message_content);
        checkedReceivers = (AutomaticWarpViewGroup)ret.findViewById(R.id.new_message_checked_receivers);

        receiver.setHint(R.string.message_new_message_receiver_hint);
        subject.setHint(R.string.message_new_message_subject_hint);
        content.setHint(R.string.message_new_message_content_hint);

        users = new SimpleObservedData<AutoCompleteUserList>(new AutoCompleteUserList());
        users.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                userMap.clear();
                for(int i=0;i<users.getData().users.length;i++){
                    userMap.put(users.getData().users[i].email+users.getData().users[i].name, new UserInfo(users.getData().users[i].name,users.getData().users[i].id));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(userMap.keySet()));
                receiver.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        receiver.setThreshold(1);
        receiver.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer = new Timer();
                final String term = s.toString();
                if(term.length()>0) {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.USER_AUTOCOMPLETE)
                                    .addParameter("term", term)
                                    .setOnResponseListener(new JsonResponseHandler(users, AutoCompleteUserList.class, null))
                                    .request();
                        }
                    }, 1000);
                }

            }
        });

        receiver.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo userInfo = userMap.get(parent.getItemAtPosition(position));

                View checkedUserView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_receiver, null, false);
                ((TextView) checkedUserView.findViewById(R.id.new_message_checked_receiver_name)).setText(userInfo.getName());

                checkedUserView.setTag(userInfo.getId());
                //checkedUserIds.add(userInfo.getId());

                checkedReceivers.addView(checkedUserView);

                //clear the autoComplectTextView
                receiver.setText("");

                if (checkedReceivers.getChildCount() > 0) {
                    checkedReceivers.setVisibility(View.VISIBLE);
                    receiver.setHint(R.string.message_new_message_more_receiver_hint);
                }

                checkedReceivers.invalidate();

            }

        });

        if(originalSenderName!=null && originalSubject!=null && originalSenderId!=-1){
            //reply mode

            View checkedUserView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_receiver, null, false);
            ((TextView) checkedUserView.findViewById(R.id.new_message_checked_receiver_name)).setText(originalSenderName);
            checkedUserView.setTag(originalSenderId);
            checkedReceivers.addView(checkedUserView);
            checkedReceivers.setVisibility(View.VISIBLE);

            receiver.setVisibility(View.GONE);

            subject.setText(getString(R.string.message_reply_prefix) + originalSubject);
            subject.setFocusable(false);
            subject.setTextColor(getResources().getColor(R.color.text_gray));
        }

        return ret;
    }

    public void reply(){

        // Reset errors.
        subject.setError(null);
        content.setError(null);

        // Store values at the time of the login attempt.
        subjectString = subject.getText().toString();
        contentString = content.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(contentString)) {
            content.setError(getString(R.string.error_field_required));
            focusView = content;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(subjectString)) {
            subject.setError(getString(R.string.error_field_required));
            focusView = subject;
            cancel = true;
        }

        // Check for a valid receivers list.
        if (checkedReceivers.getChildCount()<=0){
            receiver.setError(getString(R.string.message_new_null_receiver));
            focusView = receiver;
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
            for(int i = 0; i<checkedReceivers.getChildCount(); i++){
                Integer id = (Integer)checkedReceivers.getChildAt(i).getTag();
                if(id != null){
                    userIds += "\"";
                    userIds += id;
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
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.MESSAGE_NEW)
                        .addParameter("to", "" + userIds)
                        .addParameter("subject", subjectString)
                        .addParameter("contents", contentString)
                        .addParameter("threadid", "")
                        .addParameter("recivers", "")
                        .addParameter("emailNotify", "false")
                        .setOnResponseListener(new PostResponseHandler(new MessageNewReplyListener()))
                        .request();
            } catch (Exception e) {
            }
        }

    }



    private class MessageNewReplyListener implements OnPostListener {

        public void onPostSuccess(){
            //Log.e("AAAAAAAAAAAA", "SUCCESSSSSSSSS");
            Toast.makeText(getActivity(), R.string.message_new_success, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        public void onPostFail(){

            //Log.e("AAAAAAAAAAAA", "FAILLLLLLLLLLLLL");
            Toast.makeText(getActivity(), R.string.message_new_fail, Toast.LENGTH_SHORT).show();
        }
    }

    class UserInfo{

        String name;
        Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }



        UserInfo(String name, Integer id) {
            this.name = name;
            this.id =  id;
        }


    }
}
