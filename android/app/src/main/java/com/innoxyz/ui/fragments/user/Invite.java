package com.innoxyz.ui.fragments.user;

import android.os.Bundle;
import android.text.InputType;
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
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.common.ReplyAction;
import com.innoxyz.R;
/**
 * Created with IntelliJ IDEA
 * User: InnoXYZ
 * Date: 2014/4/11
 * Time: 15:08
 */
public class Invite extends BaseFragment implements ReplyAction {

    private EditText inviteName, inviteEmail, inviteDetail;
    private String inviteNameString, inviteEmailString, inviteDetailString;

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_invite);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_invite, container, false);

        inviteName = (EditText)ret.findViewById(R.id.invite_name);
        inviteEmail = (EditText)ret.findViewById(R.id.invite_email);
        inviteDetail = (EditText)ret.findViewById(R.id.invite_detail);

        inviteName.setHint(R.string.invite_name_hint);
        inviteEmail.setHint(R.string.invite_email_hint);
        inviteDetail.setHint(R.string.invite_detail_hint);

        inviteEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        return ret;

    }
    
    public void reply(){

        // Reset errors.
        inviteName.setError(null);
        inviteEmail.setError(null);
        inviteDetail.setError(null);

        // Store values at the time of the post attempt.
        inviteNameString = inviteName.getText().toString();
        inviteEmailString = inviteEmail.getText().toString();
        inviteDetailString = inviteDetail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid detail.
        if (TextUtils.isEmpty(inviteDetailString)) {
            inviteDetail.setError(getString(R.string.error_field_required));
            focusView = inviteDetail;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(inviteEmailString)) {
            inviteEmail.setError(getString(R.string.error_field_required));
            focusView = inviteEmail;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(inviteNameString)) {
            inviteName.setError(getString(R.string.error_field_required));
            focusView = inviteName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else{
            new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.INVITE)
                    .addParameter("username", inviteNameString)
                    .addParameter("email", inviteEmailString)
                    .addParameter("message", inviteDetailString)
                    .setOnResponseListener(new PostResponseHandler(new InviteReplyListener()))
                    .request();
        }
    }

    private class InviteReplyListener implements OnPostListener {

        public void onPostSuccess(){
            //Log.e("AAAAAAAAAAAA", "SUCCESSSSSSSSS");
            Toast.makeText(getActivity(), R.string.invite_success, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        public void onPostFail(){

            //Log.e("AAAAAAAAAAAA", "FAILLLLLLLLLLLLL");
            Toast.makeText(getActivity(), R.string.invite_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
