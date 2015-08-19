package com.innoxyz.ui.fragments.project;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.project.Project;
import com.innoxyz.data.runtime.model.project.ProjectProtocol;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.customviews.RefreshListView;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.interfaces.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import com.innoxyz.R;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-1
 * Time: 上午9:51
 * To change this template use File | Settings | File Templates.
 */
public class ProjectList extends BaseFragment implements OnRefreshListener, View.OnClickListener{

    //项目列表
    private RefreshListView projectListView;
    ProjectListViewAdapter projectAdapter;
    //搜索框
    String searchQuery = "";

    //自定义搜索框
    //原始搜索视图
    View mySearchView;
    View originalSearchview;
    TextView original_search_textview;
    //激活搜索视图
    View popupSearchView;
    EditText popup_search_edittext;
    //取消按钮
    TextView cancelTextView;
    //软键盘
    InputMethodManager imm;


    Menu currentMenu;
    MenuItem menuItem;

    //当前显示的项目
    List<Project> visibleProjectList;
    //全部项目
    List<Project> allProjectList;
    //加星项目
    List<Project> starProjectList;

    //网络请求参数
    RequestParams allParams;
    RequestParams starParams;
    //请求第几页，从0开始
    int allPage = 0;
    int starPage = 0;

    //与服务器通信数据对象
    Protocol<Project> Protocol;
    Protocol<Project> protocol;
    ProjectProtocol projectProtocol;//存储通知
    Pager<Project> pager;

    //当前的ActionBar，修改显示文字
    ActionBar actionBar;

    //当前是否为加星项目
    boolean starFlag = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Activity a = getActivity();
        actionBar = a.getActionBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 创建下拉刷新
        projectListView = new RefreshListView(getActivity(), null);
        // 创建adapter
        projectAdapter = new ProjectListViewAdapter();
        // 项目
        visibleProjectList = new ArrayList<>();
        allProjectList = new ArrayList<>();
        starProjectList = new ArrayList<>();
        // 设置网络请求参数
        allParams = new RequestParams();
        starParams = new RequestParams();
        allParams.add("pageSize", "10");
        starParams.add("pageSize", "10");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_projectlist_home, container, false);
        //添加Menu
        setHasOptionsMenu(true);
        projectListView = (RefreshListView) rootView.findViewById(R.id.projectlist_itemlist);
        projectListView.setOnRefreshListener(this);

        //添加搜索视图
        mySearchView = inflater.inflate(R.layout.my_search_view, null);
        //原始搜索框
        originalSearchview = mySearchView.findViewById(R.id.original_searchview);
        original_search_textview = (TextView) mySearchView.findViewById(R.id.original_search_textview);
        original_search_textview.setOnClickListener(this);

        //请求第几页，从0开始
        allPage = 0;
        starPage = 0;

        //激活搜索视图
        popupSearchView = mySearchView.findViewById(R.id.popup_searchview);
        //激活搜索框
        popup_search_edittext = (EditText)popupSearchView.findViewById(R.id.popup_search_edittext);
        popup_search_edittext.setFocusable(true);
        popup_search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchQuery = s.toString();
                //刷新视图
                projectListView.startRefreshWithoutHeader();
                allParams.put("nameLike", s);
                starParams.put("nameLike", s);
                if (!starFlag) {
                    allPage = 0;
                    updateAllProjectList();
                }
                else {
                    starPage = 0;
                    updateStarProjectList();
                }
            }
        });

        //取消按钮
        cancelTextView = (TextView)popupSearchView.findViewById(R.id.popup_window_tv_cancel);
        cancelTextView.setOnClickListener(this);

        //软键盘
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //设置为头布局
        projectListView.addHeaderView(mySearchView);


        //绑定adapter
        projectListView.setAdapter(projectAdapter);
        //设置item点击事件，进入项目详情
        projectListView.setOnItemClickListener(new ClickToProjectListener());

        //默认刷新全部项目列表
        updateAllProjectList();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.original_search_textview:
                //点击原始搜索框，激活新的搜索框
                originalSearchview.setVisibility(View.GONE);
                popupSearchView.setVisibility(View.VISIBLE);
                popup_search_edittext.requestFocus();

                imm.showSoftInput(popup_search_edittext,InputMethodManager.SHOW_FORCED);
                break;
            case R.id.popup_window_tv_cancel:
                //点击取消，恢复
                //清空popup_search_edittext内容
                popup_search_edittext.setText("");
                originalSearchview.setVisibility(View.VISIBLE);
                popupSearchView.setVisibility(View.GONE);
                popup_search_edittext.clearFocus();
                imm.hideSoftInputFromWindow(popup_search_edittext.getWindowToken(), 0); //强制隐藏键盘
                break;

        }
    }

    //点击则进入项目详情页
    private class ClickToProjectListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!projectListView.isHeaderHidden())
                return;
            Project clickedProject = (Project) view.getTag(R.id.item_object);
            Bundle bundle = new Bundle();
            bundle.putInt("projectId", clickedProject.getId());
            bundle.putString("projectName", clickedProject.getName());
            new ActivityCommand(DetailActivity.class, ProjectHome.class, ProjectList.this.getActivity(), bundle, null).Execute();

        }
    }

    //显示全部项目列表
    private void updateAllProjectList() {
        //加载页数
        allParams.put("page", String.valueOf(allPage));
        allParams.put("archive", "false");

        Factory.getHttpClient().get(URL.LIST_JOINED_LPROJECT, allParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                projectProtocol = ProjectProtocol.fromJson(response);
                if (projectProtocol.isOk()) {
                    pager = projectProtocol.getData();
                    //如果是刷新，则清空；加载则不清空
                    if (projectListView.isRefreshing()) {
                        allProjectList.clear();
                    }
                    //搜索结果为空
                    if (pager == null) {
                        visibleProjectList.clear();
                    }else {
                        allProjectList.addAll(pager.getData());
                        //可见列表指向它
                        visibleProjectList = allProjectList;
                    }
                    //刷新视图
                    projectAdapter.notifyDataSetChanged();
                    //告诉listView的pager内容
                    projectListView.setDataPager(pager);
                } else {
                    Toast.makeText(getActivity(), "网络故障，请重试", Toast.LENGTH_SHORT).show();
                }
                //修改视图
                projectListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败。。。", Toast.LENGTH_SHORT).show();
                //可见列表指向它
                visibleProjectList = allProjectList;
                //修改视图
                projectListView.hideHeaderFooter();
            }
        });
    }
    //显示加星项目列表
    private void updateStarProjectList() {
        //加载页数
        starParams.put("page", String.valueOf(starPage));

        Factory.getHttpClient().get(URL.LIST_LIKED_LPROJECT, starParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Protocol = new Protocol<Project>();
                protocol = Protocol.fromJson(response, new TypeToken<Protocol<Project>>() {}.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
                    //如果是刷新，则清空；加载则不清空
                    if (projectListView.isRefreshing()) {
                        starProjectList.clear();
                    }
                    //搜索结果为空
                    if (pager == null) {
                        visibleProjectList.clear();
                    }else {
                        starProjectList.addAll(pager.getData());
                        //可见列表指向它
                        visibleProjectList = starProjectList;
                    }
                    //刷新视图
                    projectAdapter.notifyDataSetChanged();
                    //告诉listView的pager内容
                    projectListView.setDataPager(pager);
                } else {
                    Toast.makeText(getActivity(), "网络故障，请重试", Toast.LENGTH_SHORT).show();
                }
                //修改视图
                projectListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败。。。", Toast.LENGTH_SHORT).show();
                //可见列表指向它
                visibleProjectList = starProjectList;
                //修改视图
                projectListView.hideHeaderFooter();
            }
        });
    }

    //当下拉刷新时触发
    @Override
    public void onDownPullRefresh() {
        //刷新当前状态的第0页
        //全部
        if (!starFlag) {
            allPage = 0;
            updateAllProjectList();
        }else {
            starPage = 0;
            updateStarProjectList();
        }
    }
    //当上拉加载时触发
    @Override
    public void onLoadingMore() {
        //全部
        if (!starFlag) {
            allPage++;
            updateAllProjectList();
        }else {
            starPage++;
            updateStarProjectList();
        }
    }


    //项目列表adapter
    class ProjectListViewAdapter extends BaseAdapter {

        class ViewHolder{
            TextView project_item_name;
            ToggleButton project_item_liked;
        }

        @Override
        public int getCount() {
            return visibleProjectList.size();
        }

        @Override
        public Object getItem(int position) {
            return visibleProjectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup){
            if(view == null){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_project, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.project_item_name = (TextView)view.findViewById(R.id.project_item_name);
                viewHolder.project_item_liked = (ToggleButton)view.findViewById(R.id.project_item_liked);

                view.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) view.getTag();

            //把项目数据绑定给item供点击事件使用
            view.setTag(R.id.item_object, visibleProjectList.get(i));

            holder.project_item_name.setText(visibleProjectList.get(i).getName());

            if(!starFlag){
                //全部项目，根据各自标识
                holder.project_item_liked.setChecked(visibleProjectList.get(i).isLiked());
            }
            else{
                //加星项目
                holder.project_item_liked.setChecked(true);
            }
            //点击对项目加星
            holder.project_item_liked.setOnClickListener(new toggleStarProjectListener());
            holder.project_item_liked.setTag(R.id.item_object, visibleProjectList.get(i));

            return view;
        }
    }

    //对项目加星
    private class toggleStarProjectListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String addStar = ((ToggleButton)v).isChecked() ? "true" : "false";

            Project project = (Project)v.getTag(R.id.item_object);

            RequestParams params1 = new RequestParams();
            params1.put("type2", "LPROJECT");
            String thingId = String.valueOf(project.getId());
            params1.put("thingId2", thingId);
            params1.put("add", addStar);
            Factory.getHttpClient().post(URL.LINK_LIKE, params1, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    Object o = (new Gson()).fromJson(response,Object.class);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }



    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.projectlist_fragment_actions, menu);
        currentMenu = menu;
        menuItem = currentMenu.findItem(R.id.action_projectlist_allorstar);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        //点击加星按钮
        if (id == R.id.action_projectlist_allorstar){
            starFlag = !starFlag;
            if(!starFlag) {
                //全部项目
                actionBar.setTitle(R.string.title_project_list_all);
                item.setIcon(R.drawable.all);

                //刷新第1页
                allPage = 0;
                projectListView.startRefresh();
                updateAllProjectList();
            } else{
                //加星项目
                actionBar.setTitle(R.string.title_project_list_star);
                item.setIcon(R.drawable.star_white);

                //刷新第1页
                starPage = 0;
                projectListView.startRefresh();
                updateStarProjectList();
            }

        }
        return true;
    }

}
