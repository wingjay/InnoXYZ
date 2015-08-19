package com.innoxyz.ui.fragments.announcement;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.innoxyz.data.runtime.model.attachment.Attachment;
import com.innoxyz.data.runtime.model.attachment.ParcelableAttachment;
import com.innoxyz.ui.adapters.AttachmentListAdapter;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.R;
import com.innoxyz.ui.utils.DateFunctions;
import com.innoxyz.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-11-29
 * Time: 下午9:09
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementHome extends BaseFragment {

    //获取传递过来的附件对象数据
    List<ParcelableAttachment> parcelAttachments;

    private String projectName;
    private String creatorName;
    private String title;
    private String time;
    private String content;
    private List<Attachment> attachments;

    ListView attachListView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //获取传递数据
        projectName = getArguments().getString("projectName");
        creatorName = getArguments().getString("creatorName");
        title = getArguments().getString("title");
        time = getArguments().getString("time");
        content = getArguments().getString("content");
        attachments = new ArrayList<>();
        parcelAttachments = new ArrayList<>();
        //获取序列化附件数据
        parcelAttachments = getArguments().getParcelableArrayList("attachments");
        //反序列化，转成普通附件列表数据
        if (parcelAttachments != null) {
            for (ParcelableAttachment seriattach : parcelAttachments) {
                Attachment a = new Attachment();
                a.setId(seriattach.getId());
                a.setName(seriattach.getName());
                attachments.add(a);
            }
        }

        //标题
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_announcement_home);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View ret = inflater.inflate(R.layout.fragment_announcement_home, container, false);
        //修改标题： 所属项目：xxx
        ((TextView)ret.findViewById(R.id.announcement_home_projectname))
                .setText( getResources().getString(R.string.template_topichome_projectname)
                .replace("%name%", projectName) );

        //标题
        TextView titleTextView = (TextView)ret.findViewById(R.id.announcement_title);
        titleTextView.setText(title);
        //发布者
        ((TextView) ret.findViewById(R.id.announcement_release_user)).setText(creatorName);
        //发布时间
        ((TextView) ret.findViewById(R.id.announcement_time)).setText(DateFunctions.RewriteDate(time, "yyyy年M月dd日 HH:mm", "unknown"));

        //内容
        TextView contentTextView = (TextView)ret.findViewById(R.id.announcement_home_content);
        content =  Util.unescape(content);
        contentTextView.setText(Html.fromHtml(content));
        contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        //附件
        attachListView = (ListView)ret.findViewById(R.id.announcement_home_attach_listview);
        if (attachments != null && attachments.size() != 0) {
            AttachmentListAdapter attachmentListAdapter = new AttachmentListAdapter(getActivity(), attachments);
            attachListView.setAdapter(attachmentListAdapter);
        }else{
            attachListView.setAdapter(null);
        }
        return ret;

    }


}
