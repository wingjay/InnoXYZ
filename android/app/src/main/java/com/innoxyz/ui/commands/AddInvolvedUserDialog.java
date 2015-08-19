package com.innoxyz.ui.commands;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.interfaces.OnPostListener;
import com.innoxyz.data.remote.response.JsonResponseHandler;
import com.innoxyz.data.remote.response.PostResponseHandler;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.data.runtime.beans.MembersOfThing;
import com.innoxyz.data.runtime.model.ParcelableUser;
import com.innoxyz.data.runtime.interfaces.IDataObserver;
import com.innoxyz.ui.adapters.NetworkimageTextAdapter;
import com.innoxyz.ui.customviews.CheckableRelativeLayout;

import java.util.Arrays;
import java.util.List;
import com.innoxyz.R;
/**
 * Created by laborish on 2014-4-14.
 */
public class AddInvolvedUserDialog implements AdapterView.OnItemClickListener{

    public final static String TYPE_NAME = "AddInvolvedUserDialog_TYPE";
    public final static String TYPE_TOPIC = "TOPIC";
    public final static String TYPE_TASK = "TASK";

    final private Activity activity;
    //private List<ParcelableUser> projectUsers;
    private String[] names;
    private Integer[] ids;
    private boolean[] selection;
    SimpleObservedData<MembersOfThing> membersOfThing;

    //private List<ParcelableUser> involvedUsers;
    private int projectId;
    private String thingId;
    private String type;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ListView listView;


    public AddInvolvedUserDialog(Activity activity, Bundle bundle){
        this.activity = activity;
        projectId = bundle.getInt("projectId");
        type = bundle.getString(TYPE_NAME);
        if(type.equals(TYPE_TASK)){
            thingId = bundle.getString("taskId");
        }
        else{
            thingId = bundle.getString("topicId");
        }

        membersOfThing = new SimpleObservedData<MembersOfThing>(new MembersOfThing());
        membersOfThing.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {

                builder = new AlertDialog.Builder(AddInvolvedUserDialog.this.activity);
                builder.setPositiveButton("确定",null);
                builder.setAdapter(new NetworkimageTextAdapter(AddInvolvedUserDialog.this.activity, names, ids), null);
                alertDialog = builder.create();
                listView = alertDialog.getListView();
                listView.setItemsCanFocus(false);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setOnItemClickListener(AddInvolvedUserDialog.this);

                for(int i=0;i<membersOfThing.getData().members.length;i++){
                    for(int j=0;j<ids.length;j++){
                        if(ids[j].equals(membersOfThing.getData().members[i].user.id)){
                            selection[j] = true;
                        }
                    }
                }

                alertDialog.show();

                for (int j = 0; j < selection.length; ++j){
                    listView.setItemChecked(j, selection[j]);
                }
            }

        });


    }

    public AddInvolvedUserDialog setItemsAndIds(List<ParcelableUser> projectUsers){
        names = new String[projectUsers.size()];
        ids = new Integer[projectUsers.size()];

        for(int i=0;i<projectUsers.size();i++){
            names[i] = projectUsers.get(i).getName();
            ids[i] = projectUsers.get(i).getId();
        }

        selection = new boolean[names.length];
        Arrays.fill(selection, false);

        return this;
    }

    public void show(){

        new StringRequestBuilder(activity).setRequestInfo(AddressURIs.GET_INVOLVES)
                .addParameter("thingId2", "" + thingId)
                .addParameter("pageSize", "100")
                .addParameter("type2", type)
                .setOnResponseListener(new JsonResponseHandler(membersOfThing, MembersOfThing.class, null))
                .request();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int which, long id){

        CheckableRelativeLayout checkableRelativeLayout = (CheckableRelativeLayout) view;
        if(checkableRelativeLayout.isChecked()){
            new StringRequestBuilder(activity).setRequestInfo(AddressURIs.ADD_INVOLVES)
                    .addParameter("userIds", "[\""+ids[which]+"\"]")
                    .addParameter("thingId2", thingId)
                    .addParameter("type2", type)
                    .setOnResponseListener(new PostResponseHandler(new AddInvolvesReplyListener()))
                    .request();
        }
        else{
            new StringRequestBuilder(activity).setRequestInfo(AddressURIs.REMOVE_INVOLVES)
                    .addParameter("thingIds", "[\""+ids[which]+"\"]")
                    .addParameter("type1", "USER")
                    .addParameter("thingId2", thingId)
                    .addParameter("type2", type)
                    .addParameter("linkType", "INVOLVED")
                    .setOnResponseListener(new PostResponseHandler(new RemoveInvolvesReplyListener()))
                    .request();
        }


    }

    private class AddInvolvesReplyListener implements OnPostListener {

        public void onPostSuccess(){
            Toast.makeText(activity, R.string.add_involve_success, Toast.LENGTH_SHORT).show();
        }

        public void onPostFail(){
            Toast.makeText(activity, R.string.add_involve_fail, Toast.LENGTH_SHORT).show();
        }
    }

    private class RemoveInvolvesReplyListener implements OnPostListener {

        public void onPostSuccess(){
            Toast.makeText(activity, R.string.remove_involve_success, Toast.LENGTH_SHORT).show();
        }

        public void onPostFail(){
            Toast.makeText(activity, R.string.remove_involve_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
