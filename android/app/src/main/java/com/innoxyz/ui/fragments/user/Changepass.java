package com.innoxyz.ui.fragments.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import com.innoxyz.data.json.parser.JsonParser;
import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.response.TextResponseHandler;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.data.runtime.beans.user.ChangePassErrors;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.common.ReplyAction;

import org.json.JSONObject;

import java.util.Arrays;
import com.innoxyz.R;
/**
 * Created by laborish on 2014-4-12.
 */
public class Changepass extends BaseFragment implements ReplyAction {

    private EditText passwordOld, passwordNew, passwordAgain;
    private String passwordOldString, passwordNewString, passwordAgainString;

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_changepass);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_changepass, container, false);

        passwordOld = (EditText)ret.findViewById(R.id.changepass_passwordOld);
        passwordNew = (EditText)ret.findViewById(R.id.changepass_passwordNew);
        passwordAgain = (EditText)ret.findViewById(R.id.changepass_passwordAgain);

        passwordOld.setHint(R.string.changepass_passwordold_hint);
        passwordNew.setHint(R.string.changepass_passwordnew_hint);
        passwordAgain.setHint(R.string.changepass_passwordagain_hint);

        return ret;

    }
    
    public void reply(){
        // Reset errors.
        passwordOld.setError(null);
        passwordNew.setError(null);
        passwordAgain.setError(null);

        // Store values at the time of the post attempt.
        passwordOldString = passwordOld.getText().toString();
        passwordNewString = passwordNew.getText().toString();
        passwordAgainString = passwordAgain.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid detail.
        if (TextUtils.isEmpty(passwordAgainString)) {
            passwordAgain.setError(getString(R.string.error_field_required));
            focusView = passwordAgain;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(passwordNewString)) {
            passwordNew.setError(getString(R.string.error_field_required));
            focusView = passwordNew;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(passwordOldString)) {
            passwordOld.setError(getString(R.string.error_field_required));
            focusView = passwordOld;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else{

            if(!passwordNewString.equals(passwordAgainString)){
                passwordAgain.setError(getString(R.string.changepass_passwordagain_notequal));
                focusView = passwordAgain;
                cancel = true;
            }

            if(passwordAgainString.length()<6){
                passwordAgain.setError(getString(R.string.changepass_passwordagain_notlongenough));
                focusView = passwordAgain;
                cancel = true;
            }

            if(passwordNewString.length()<6){
                passwordNew.setError(getString(R.string.changepass_passwordagain_notlongenough));
                focusView = passwordNew;
                cancel = true;
            }

            if (cancel) {

                focusView.requestFocus();
            }
            else{
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.CHANGE_PASS)
                        .addParameter("passwordOld", passwordOldString)
                        .addParameter("passwordNew", passwordNewString)
                        .addParameter("passwordAgain", passwordAgainString)
                        .setOnResponseListener(new changePassResponseHandler())
                        .request();
            }
        }
    }

    public class changePassResponseHandler extends TextResponseHandler{

        final static String RESULT = "action_returns";
        final static String SUCCESS = "success";
        final static String INPUT = "input";

        public changePassResponseHandler(){}

        @Override
        public final boolean OnResponseContent(int responseCode, String content){
            Log.i("Request", content);
            try{
                JSONObject json = new JSONObject(content);
                if( json.getString(RESULT).compareTo(SUCCESS)!=0){

                    JsonParser parser = InnoXYZApp.getApplication().getJsonParsers().getParser(ChangePassErrors.class);

                    SimpleObservedData<ChangePassErrors> errors = new SimpleObservedData<ChangePassErrors>(new ChangePassErrors());
                    errors.setData((ChangePassErrors)parser.Parse(json.getJSONObject("errors")), false);

                    if(errors.getData().passwordOld!=null){
                        passwordOld.setError(Arrays.toString(errors.getData().passwordOld));
                        passwordOld.requestFocus();
                    }
                    else if(errors.getData().passwordNew!=null){
                        passwordNew.setError(Arrays.toString(errors.getData().passwordNew));
                        passwordNew.requestFocus();
                    }
                    else if(errors.getData().passwordAgain!=null){
                        passwordAgain.setError(Arrays.toString(errors.getData().passwordAgain));
                        passwordAgain.requestFocus();
                    }
                    else{
                        Toast.makeText(getActivity(), R.string.changepass_fail, Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(getActivity(), R.string.changepass_success, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
            catch (Exception e){

            }

            return true;
        }

    }
}
