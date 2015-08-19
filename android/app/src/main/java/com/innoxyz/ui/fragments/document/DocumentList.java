package com.innoxyz.ui.fragments.document;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.document.Document;
import com.innoxyz.data.runtime.model.document.DocumentProtocol;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.customviews.FileDownloadImageView;
import com.innoxyz.ui.customviews.RefreshListView;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.interfaces.OnRefreshListener;
import com.innoxyz.ui.utils.DateFunctions;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import com.innoxyz.R;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-28
 * Time: 下午12:26
 * 获取所有文档数据根据目录和文件的不同在adapter中采用不同的布局来显示
 */
public class DocumentList extends BaseFragment implements OnRefreshListener{

    public static final String DIR_DOCUMENT_TYPE = "DIR";
    public static final String FILE_DOCUMENT_TYPE = "FILE";
    //所有文件
    private List<Document> allDocumentList;
    private RefreshListView allDocumentListView;
    private DocumentListViewAdapter documentListViewAdapter;

    //项目ID
    private Integer projectID;
    //文件夹目录ID
    private String entryID = "";

    //分页
    int page;
    boolean hasMore = false;

    //从服务器获取文档数据
    RequestParams getDocumentparams;
    DocumentProtocol protocol;
    Pager<Document> pager;

    //当前状态 刷新或加载
    private static final int NORMAL = 0;
    private static final int REFRESH = 1;
    private static final int LOAD = 2;
    private int CURRENT_STATUS = NORMAL;

    String dirName = "";
    ActionBar actionBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //获取项目ID
        projectID = getArguments().getInt("projectId");
        //获取存储目录ID，如果成功获取则直接根据目录获取文件夹数据，否则要先利用项目ID获取存储目录ID
        entryID = getArguments().getString("entryId");
        dirName = getArguments().getString("dirName");
        //获取顶部菜单
        actionBar = getActivity().getActionBar();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allDocumentListView = new RefreshListView(getActivity(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (actionBar != null){
            if (TextUtils.isEmpty(dirName))
                actionBar.setTitle(R.string.title_document_home);
            else
                actionBar.setTitle(dirName);
        }

        View view = inflater.inflate(R.layout.fragment_document_list, container, false);
        allDocumentListView = (RefreshListView)view.findViewById(R.id.document_listview);

        //初始化网络参数
        page = 0;
        getDocumentparams = new RequestParams();
        allDocumentList = new ArrayList<>();
        //初始化adapter
        documentListViewAdapter = new DocumentListViewAdapter(getActivity(), allDocumentList);
        allDocumentListView.setAdapter(documentListViewAdapter);
        //绑定刷新监听
        allDocumentListView.setOnRefreshListener(this);

        //点击文件夹进入
        allDocumentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (allDocumentListView.isRefreshing())
                    return;
                Document document = (Document)view.getTag(R.id.item_object);
                //如果是文件，不响应
                if (document.getType().equalsIgnoreCase(getResources().getString(R.string.FILE)))
                    return;
                //绑定数据 entryId 再次进入本页面
                Bundle bundle = new Bundle();
                bundle.putString("entryId", document.getId());
                bundle.putString("dirName", document.getDispName());
                bundle.putInt("projectId", projectID);
                new ActivityCommand(DetailActivity.class, DocumentList.class, DocumentList.this.getActivity(), bundle, null).Execute();

            }
        });

        //启动刷新
        allDocumentListView.startRefresh();
        //获取文件夹目录ID并触发获取文档数据
        getDirID();

        return view;
    }

    //获取文件夹目录ID
    private void getDirID() {
        if (entryID == null || entryID.equalsIgnoreCase("")) {
            //向服务器请求获取文档目录entryID
            RequestParams getDirParams = new RequestParams();
            getDirParams.put("thingId", "" + projectID);

            Factory.getHttpClient().get(URL.GET_BASEDIR_OF_PROJECT, getDirParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    Protocol<String> tempProtocol = new Protocol<String>();
                    Protocol<String> protocol = tempProtocol.fromJson(response, new TypeToken<Protocol<String>>(){}.getType());
                    if (protocol.isOk()) {
                        entryID = protocol.getData();
                        //刷新文档数据
                        updateDocumentList();
                    }else {
                        Toast.makeText(getActivity(), "无法正确获取文件夹ID,可尝试刷新该页面", Toast.LENGTH_SHORT).show();
                        allDocumentListView.hideHeaderFooter();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity(), "网络连接失败。。。", Toast.LENGTH_SHORT).show();
                    allDocumentListView.hideHeaderFooter();
                }
            });
        }else {
            //若有entryID则直接刷新
            updateDocumentList();
        }
    }
    //刷新文档数据
    private void updateDocumentList() {
        //加载文档数据
        getDocumentparams.put("entryId", entryID);
        getDocumentparams.put("page", page);

        Factory.getHttpClient().get(URL.LIST_DOCUMENT_IN_PROJECT, getDocumentparams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                protocol = DocumentProtocol.fromJson(response);
                if (protocol.isOk()) {
                    pager = protocol.getData();
                    if (allDocumentListView.isRefreshing()) {
                        allDocumentList.clear();
                    }
                    allDocumentList.addAll(pager.getData());
                    //刷新视图
                    documentListViewAdapter.notifyDataSetChanged();
                    //告诉listView的pager内容
                    allDocumentListView.setDataPager(pager);
                } else {
                    Toast.makeText(getActivity(), "数据获取错误", Toast.LENGTH_SHORT).show();
                }
                allDocumentListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败。。。", Toast.LENGTH_SHORT).show();
                allDocumentListView.hideHeaderFooter();
            }
        });
    }


    //刷新
    @Override
    public void onDownPullRefresh() {
        //设置刷新
        page = 0;
        updateDocumentList();
    }

    //加载
    @Override
    public void onLoadingMore() {
        page ++;
        updateDocumentList();
    }


    private class DocumentListViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<Document> mDocumentList;

        public static final int FILE_LAYOUT = 0;// 2种不同的布局
        public static final int DIR_LAYOUT = 1;

        class FileViewHolder {
            TextView document_name;
            FileDownloadImageView download_document_state;
        }
        class DirViewHolder {
            TextView directory_name, dir_creator_name, dir_creat_time;
        }

        public DocumentListViewAdapter(Context context, List<Document> allDocumentList) {
            this.mDocumentList = allDocumentList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


        /**
         * 返回所有的layout的数量
         *
         * */
        @Override
        public int getViewTypeCount() {
            return 2;
        }
        /**
         * 根据数据源的position返回需要显示的的layout的type
         *
         * type的值必须从0开始
         *
         * */
        @Override
        public int getItemViewType(int position) {
            Document document = mDocumentList.get(position);
            String theType = document.getType();
            return (theType.equalsIgnoreCase(getResources().getString(R.string.FILE))) ? FILE_LAYOUT : DIR_LAYOUT;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            //先判断是文件夹还是文件
            FileViewHolder fileHolder = null;
            DirViewHolder dirHolder = null;
            Document document = mDocumentList.get(i);
            int type = getItemViewType(i);

            if (view == null) {
                switch (type) {
                    case FILE_LAYOUT:
                        fileHolder = new FileViewHolder();
                        view = inflater.inflate(R.layout.listitem_document, null);
                        fileHolder.document_name = (TextView) view.findViewById(R.id.document_name);
                        fileHolder.download_document_state = (FileDownloadImageView) view.findViewById(R.id.download_document_state);
                        //必须在此赋值，才能在首次加载页面时即可显示
                        fileHolder.download_document_state.setInit(getActivity(), document.getId(), document.getFileRealName());
                        fileHolder.document_name.setText(document.getFileRealName());

                        view.setTag(fileHolder);
                        //保存数据
                        view.setTag(R.id.item_object, document);
                        break;
                    case DIR_LAYOUT:
                        dirHolder = new DirViewHolder();
                        view = inflater.inflate(R.layout.listitem_directory, null);
                        dirHolder.directory_name = (TextView) view.findViewById(R.id.directory_name);
                        dirHolder.dir_creator_name = (TextView) view.findViewById(R.id.dir_creator_name);
                        dirHolder.dir_creat_time = (TextView) view.findViewById(R.id.dir_creat_time);
                        //必须在此赋值，才能在首次加载页面时即可显示
                        dirHolder.directory_name.setText(document.getDispName());
                        dirHolder.dir_creator_name.setText(document.getCreatorName());
                        dirHolder.dir_creat_time.setText(DateFunctions.RewriteDate(document.getCreateTime(), "yy-M-d", "unknown"));

                        view.setTag(dirHolder);
                        //保存数据
                        view.setTag(R.id.item_object, document);
                        break;
                    default:
                        break;
                }
            } else {
                switch (type) {
                    case FILE_LAYOUT:
                        fileHolder = (FileViewHolder) view.getTag();
                        fileHolder.download_document_state.setInit(getActivity(), document.getId(), document.getFileRealName());
                        fileHolder.document_name.setText(document.getFileRealName());
                        //保存数据
                        view.setTag(R.id.item_object, document);
                        break;
                    case DIR_LAYOUT:
                        dirHolder = (DirViewHolder) view.getTag();
                        dirHolder.directory_name.setText(document.getDispName());
                        dirHolder.dir_creator_name.setText(document.getCreatorName());
                        dirHolder.dir_creat_time.setText(DateFunctions.RewriteDate(document.getCreateTime(), "yy-M-d", "unknown"));
                        //保存数据
                        view.setTag(R.id.item_object, document);
                        break;
                    default:
                        break;
                }

            }

            return view;

        }

    }

}
