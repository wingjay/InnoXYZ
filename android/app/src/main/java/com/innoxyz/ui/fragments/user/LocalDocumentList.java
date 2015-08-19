package com.innoxyz.ui.fragments.user;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.reflect.TypeToken;
import com.innoxyz.R;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.document.Document;
import com.innoxyz.data.runtime.model.document.DocumentProtocol;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.customviews.FileDownloadImageView;
import com.innoxyz.ui.customviews.RefreshListView;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.interfaces.OnRefreshListener;
import com.innoxyz.ui.utils.DateFunctions;
import com.innoxyz.util.Factory;
import com.innoxyz.util.Util;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-28
 * Time: 下午12:26
 * 显示本地所有文件，能够查看和删除
 */
public class LocalDocumentList extends BaseFragment{

    public static final String DIR_DOCUMENT_TYPE = "DIR";
    public static final String FILE_DOCUMENT_TYPE = "FILE";

    //无文档提示
    View NullDocumentView;

    class LocalDocument {
        String fileId;
        String fileName;

        public LocalDocument(String Id, String Name) {
            this.fileId = Id;
            this.fileName = Name;
        }
    }

    //所有文件
    private List<LocalDocument> allDocumentList;
    private ListView allDocumentListView;
    private DocumentListViewAdapter documentListViewAdapter;

    ActionBar actionBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //获取顶部菜单
        actionBar = getActivity().getActionBar();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (actionBar != null){
            actionBar.setTitle(R.string.title_local_document);
        }

        View view = inflater.inflate(R.layout.fragment_local_document_list, container, false);
        NullDocumentView = view.findViewById(R.id.null_document_tip);
        allDocumentListView = (ListView)view.findViewById(R.id.local_document_listview);

        allDocumentList = new ArrayList<>();

        //获取上传目录的所有文件
        File uploadDirectory = new File(InnoXYZApp.getApplication().getUploadFileDir());
        File[] uploadContents = uploadDirectory.listFiles();
        getFileIdNameMap(uploadContents);
        //获取下载目录的所有文件
        File downloadDirectory = new File(InnoXYZApp.getApplication().getDownloadFileDir());
        File[] downloadContents = downloadDirectory.listFiles();
        getFileIdNameMap(downloadContents);

        //初始化adapter
        documentListViewAdapter = new DocumentListViewAdapter();
        allDocumentListView.setAdapter(documentListViewAdapter);

        //如果没有文件则显示提示
        if (allDocumentList.size() == 0) {
            NullDocumentView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    //将所有ID文件夹填入allDocumentList
    void getFileIdNameMap(File[] fileContents) {
        if (fileContents == null)
            return;
        int fileNum = fileContents.length;
        for (int i=0; i<fileNum; i++) {
            //获取文件ID
            String fileId = fileContents[i].getName();
            //如果是缓存则忽略
            if (fileId.equalsIgnoreCase("Cache"))
                continue;
            String fileName;
            //获取内部文件名称
            File[] theFile = fileContents[i].listFiles();
            if (theFile.length == 1) {
                fileName = theFile[0].getName();
                LocalDocument localDocument = new LocalDocument(fileId, fileName);
                allDocumentList.add(localDocument);
            }else {
                //删除这个ID文件夹
                boolean delWrongFile = fileContents[i].delete();
                if (!delWrongFile) {
                    Toast.makeText(getActivity(), fileId + "文件夹删除失败，请手动删除", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private class DocumentListViewAdapter extends BaseAdapter {

        //子控件
        class ViewHolder{
            TextView document_name;
            ImageView delete_local_document;
        }

        @Override
        public int getCount() {
            return allDocumentList.size();
        }

        @Override
        public Object getItem(int position) {
            return allDocumentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_localdocument, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.document_name = (TextView)view.findViewById(R.id.document_name);
                viewHolder.delete_local_document = (ImageView)view.findViewById(R.id.delete_local_document);
                view.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) view.getTag();


            //获取当前显示的文档数据
            final LocalDocument localDocument = allDocumentList.get(i);

            //显示文件名
            holder.document_name.setText(localDocument.fileName);

            //查看文件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //LocalDocument localDocument = (LocalDocument)v.getTag(R.id.item_object);
                    File displayFile = new File(InnoXYZApp.getApplication().getUploadFileDir() + File.separator + localDocument.fileId + File.separator + localDocument.fileName);
                    if (displayFile.exists()) {
                        Util.displayFile(getActivity(), displayFile);
                    }else {
                        displayFile = new File(InnoXYZApp.getApplication().getDownloadFileDir() + File.separator + localDocument.fileId + File.separator + localDocument.fileName);
                        if (displayFile.exists()) {
                            Util.displayFile(getActivity(), displayFile);
                        }
                    }
                }
            });
            //删除图标 绑定点击函数
            holder.delete_local_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //弹出删除确认框
                    final NiftyDialogBuilder dialogBuilder = Factory.getNiftyDialog(getActivity());
                    dialogBuilder.withMessage("确认删除？\n\n")
                            .withTitle("提示")
                            .withButton2Text("删除")
                            .withButton1Text("取消")
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int deleteResult = Util.deleteFile(localDocument.fileId);
                                    switch (deleteResult) {
                                        case -1:
                                            Toast.makeText(getActivity(), localDocument.fileName + "文件不存在", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 0:
                                            Toast.makeText(getActivity(), localDocument.fileName + "文件删除失败", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 1:
                                            Toast.makeText(getActivity(), localDocument.fileName + "文件删除成功", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                    dialogBuilder.dismiss();
                                    //删除失败则不用刷新本地数据
                                    if (deleteResult == 0)
                                        return;
                                    //刷新本地文件数据
                                    allDocumentList.remove(localDocument);
                                    //如果没有文档，显示提示
                                    if (allDocumentList.size() == 0) {
                                        NullDocumentView.setVisibility(View.VISIBLE);
                                    } else {
                                        notifyDataSetChanged();
                                    }
                                }
                            })
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogBuilder.dismiss();
                                }
                            })
                            .show();


                }
            });

            return view;

        }

    }

}
