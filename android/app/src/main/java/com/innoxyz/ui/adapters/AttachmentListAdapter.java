package com.innoxyz.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innoxyz.R;
import com.innoxyz.data.runtime.model.attachment.Attachment;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.customviews.FileDownloadImageView;
import com.innoxyz.util.Factory;
import com.innoxyz.util.Util;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yj on 2014-11-14.
 */

public class AttachmentListAdapter extends BaseAdapter {

    List<Attachment> attachments;
    Context context;

    class ViewHolder{
        ImageView attachment_icon;
        TextView attachment_name_textView;
        FileDownloadImageView download_attachment_state;
    }

    public AttachmentListAdapter(Context mContext, List<Attachment> mAttachments){
        this.context = mContext;
        this.attachments = mAttachments;
    }

    public void updateAttachments(List<Attachment> mAttachments) {
        this.attachments = mAttachments;
    }

    @Override
    public int getCount(){
        return attachments.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        //附件ID
        final String attachmentID = attachments.get(i).getId();
        //附件名
        final String attachmentName = attachments.get(i).getName();
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.listitem_attachment, null);
            ViewHolder holder = new ViewHolder();
            holder.attachment_icon = (ImageView)view.findViewById(R.id.attachment_icon);
            holder.attachment_name_textView = (TextView)view.findViewById(R.id.attachment_name_textView);
            //初始化
            holder.download_attachment_state = (FileDownloadImageView)view.findViewById(R.id.download_attachment_state);
            holder.download_attachment_state.setInit(context, attachmentID, attachmentName);
            view.setTag(holder);
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.attachment_name_textView.setText(attachmentName);

        return view;
    }


}
